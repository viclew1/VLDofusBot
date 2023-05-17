/*
 *  Copyright (C) 2010-2021 JPEXS, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.jpexs.decompiler.flash;

import com.jpexs.decompiler.flash.action.Action;
import com.jpexs.decompiler.flash.action.special.ActionEnd;
import com.jpexs.decompiler.flash.action.special.ActionUnknown;
import com.jpexs.decompiler.flash.action.swf3.*;
import com.jpexs.decompiler.flash.action.swf4.*;
import com.jpexs.decompiler.flash.action.swf5.*;
import com.jpexs.decompiler.flash.action.swf6.*;
import com.jpexs.decompiler.flash.action.swf7.*;
import com.jpexs.decompiler.flash.amf.amf3.Amf3InputStream;
import com.jpexs.decompiler.flash.amf.amf3.Amf3Value;
import com.jpexs.decompiler.flash.amf.amf3.NoSerializerExistsException;
import com.jpexs.decompiler.flash.configuration.Configuration;
import com.jpexs.decompiler.flash.dumpview.DumpInfo;
import com.jpexs.decompiler.flash.dumpview.DumpInfoSpecial;
import com.jpexs.decompiler.flash.dumpview.DumpInfoSpecialType;
import com.jpexs.decompiler.flash.tags.*;
import com.jpexs.decompiler.flash.tags.gfx.*;
import com.jpexs.decompiler.flash.timeline.Timelined;
import com.jpexs.decompiler.flash.types.*;
import com.jpexs.decompiler.flash.types.filters.*;
import com.jpexs.decompiler.flash.types.shaperecords.*;
import com.jpexs.helpers.*;
import com.jpexs.helpers.utf8.Utf8Helper;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.InflaterInputStream;

/**
 * Class for reading data from SWF file
 *
 * @author JPEXS
 */
public class SWFInputStream implements AutoCloseable {

    private MemoryInputStream is;

    private long startingPos;

    private static final Logger logger = Logger.getLogger(SWFInputStream.class.getName());

    public static final byte[] BYTE_ARRAY_EMPTY = new byte[0];

    private final List<ProgressListener> listeners = new ArrayList<>();

    private long percentMax;

    private SWF swf;

    public DumpInfo dumpInfo;

    public void addPercentListener(ProgressListener listener) {
        this.listeners.add(listener);
    }

    public void removePercentListener(ProgressListener listener) {
        int index = this.listeners.indexOf(listener);
        if (index > -1) {
            this.listeners.remove(index);
        }
    }

    private void informListeners() {
        if (this.listeners.size() > 0 && this.percentMax > 0) {
            int percent = (int) (this.getPos() * 100 / this.percentMax);
            if (this.lastPercent != percent) {
                for (ProgressListener pl : this.listeners) {
                    pl.progress(percent);
                }
                this.lastPercent = percent;
            }
        }
    }

    public void setPercentMax(long percentMax) {
        this.percentMax = percentMax;
    }

    /**
     * Constructor
     *
     * @param swf         SWF to read
     * @param data        SWF data
     * @param startingPos
     * @param limit
     * @throws java.io.IOException
     */
    public SWFInputStream(SWF swf, byte[] data, long startingPos, int limit) throws IOException {
        this.swf = swf;
        this.startingPos = startingPos;
        this.is = new MemoryInputStream(data, 0, limit);
    }

    /**
     * Constructor
     *
     * @param swf  SWF to read
     * @param data SWF data
     * @throws java.io.IOException
     */
    public SWFInputStream(SWF swf, byte[] data) throws IOException {
        this(swf, data, 0L, data.length);
    }

    public SWF getSwf() {
        return this.swf;
    }

    /**
     * Gets position in bytes in the stream
     *
     * @return Number of bytes
     */
    public long getPos() {
        return this.startingPos + this.is.getPos();
    }

    /**
     * Sets position in bytes in the stream
     *
     * @param pos Number of bytes
     * @throws java.io.IOException
     */
    public void seek(long pos) throws IOException {
        this.is.seek(pos - this.startingPos);
    }

    private DumpInfo newDumpLevel(String name, String type) {
        return this.newDumpLevel(name, type, DumpInfoSpecialType.NONE, null);
    }

    private DumpInfo newDumpLevel(String name, String type, DumpInfoSpecialType specialType, Object specialValue) {
        if (this.dumpInfo != null) {
            long startByte = this.is.getPos();
            if (this.bitPos > 0) {
                startByte--;
            }
            DumpInfo di = specialType == DumpInfoSpecialType.NONE
                    ? new DumpInfo(name, type, null, startByte, this.bitPos, 0, 0)
                    : new DumpInfoSpecial(name, type, null, startByte, this.bitPos, 0, 0, specialType, specialValue);
            di.parent = this.dumpInfo;
            this.dumpInfo.getChildInfos().add(di);
            this.dumpInfo = di;
        }

        return this.dumpInfo;
    }

    private void endDumpLevel() {
        this.endDumpLevel(null);
    }

    private void endDumpLevel(Object value) {
        if (this.dumpInfo != null) {
            if (this.dumpInfo.startBit == 0 && this.bitPos == 0) {
                this.dumpInfo.lengthBytes = this.is.getPos() - this.dumpInfo.startByte;
            } else {
                this.dumpInfo.lengthBits = (int) ((this.is.getPos() - this.dumpInfo.startByte - 1) * 8 - this.dumpInfo.startBit + (this.bitPos == 0 ? 8 : this.bitPos));
            }
            this.dumpInfo.previewValue = value;
            this.dumpInfo = this.dumpInfo.parent;
        }
    }

    private void endDumpLevelUntil(DumpInfo di) {
        if (di != null) {
            while (this.dumpInfo != null && this.dumpInfo != di) {
                this.endDumpLevel();
            }
        }
    }

    /**
     * Reads one byte from the stream
     *
     * @return byte
     * @throws IOException
     */
    private int readEx() throws IOException {
        this.bitPos = 0;
        return this.readNoBitReset();
    }

    private void alignByte() {
        this.bitPos = 0;
    }

    private int lastPercent = -1;

    private int readNoBitReset() throws IOException, EndOfStreamException {
        int r = this.is.read();
        if (r == -1) {
            throw new EndOfStreamException();
        }

        this.informListeners();
        return r;
    }

    /**
     * Reads one UI8 (Unsigned 8bit integer) value from the stream
     *
     * @param name
     * @return UI8 value or -1 on error
     * @throws IOException
     */
    public int readUI8(String name) throws IOException {
        this.newDumpLevel(name, "UI8");
        int ret = this.readEx();
        this.endDumpLevel(ret);
        return ret;
    }

    /**
     * Reads one null terminated string value from the stream
     *
     * @param name
     * @return String value
     * @throws IOException
     */
    public String readString(String name) throws IOException {
        this.newDumpLevel(name, "string");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int r;
        while (true) {
            r = this.readEx();
            if (r == 0) {
                this.endDumpLevel();
                return new String(baos.toByteArray(), Utf8Helper.charset);
            }
            baos.write(r);
        }
    }

    /**
     * Reads one netstring (length + string) value from the stream
     *
     * @param name
     * @return String value
     * @throws IOException
     */
    public String readNetString(String name) throws IOException {
        this.newDumpLevel(name, "string");
        int length = this.readEx();
        String ret = new String(this.readBytesInternalEx(length));
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one netstring (length + string) value from the stream
     *
     * @param name
     * @param charset
     * @return String value
     * @throws IOException
     */
    public String readNetString(String name, Charset charset) throws IOException {
        this.newDumpLevel(name, "string");
        int length = this.readEx();
        String ret = new String(this.readBytesInternalEx(length), charset);
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one UI32 (Unsigned 32bit integer) value from the stream
     *
     * @param name
     * @return UI32 value
     * @throws IOException
     */
    public long readUI32(String name) throws IOException {
        this.newDumpLevel(name, "UI32");
        long ret = this.readUI32Internal();
        this.endDumpLevel(ret);
        return ret;
    }

    /**
     * Reads one UI32 (Unsigned 32bit integer) value from the stream
     *
     * @return UI32 value
     * @throws IOException
     */
    private long readUI32Internal() throws IOException {
        return (this.readEx() + (this.readEx() << 8) + (this.readEx() << 16) + (this.readEx() << 24)) & 0xffffffff;
    }

    /**
     * Reads one UI16 (Unsigned 16bit integer) value from the stream
     *
     * @param name
     * @return UI16 value
     * @throws IOException
     */
    public int readUI16(String name) throws IOException {
        this.newDumpLevel(name, "UI16");
        int ret = this.readUI16Internal();
        this.endDumpLevel(ret);
        return ret;
    }

    /**
     * Reads one UI16 (Unsigned 16bit integer) value from the stream
     *
     * @return UI16 value
     * @throws IOException
     */
    private int readUI16Internal() throws IOException {
        return this.readEx() + (this.readEx() << 8);
    }

    public int readUI24(String name) throws IOException {
        this.newDumpLevel(name, "UI24");
        int ret = this.readEx() + (this.readEx() << 8) + (this.readEx() << 16);
        this.endDumpLevel(ret);
        return ret;
    }

    /**
     * Reads one SI32 (Signed 32bit integer) value from the stream
     *
     * @param name
     * @return SI32 value
     * @throws IOException
     */
    public long readSI32(String name) throws IOException {
        this.newDumpLevel(name, "SI32");
        long uval = this.readEx() + (this.readEx() << 8) + (this.readEx() << 16) + (this.readEx() << 24);
        if (uval >= 0x80000000) {
            uval = -(((~uval) & 0xffffffff) + 1);
        }
        this.endDumpLevel(uval);
        return uval;
    }

    /**
     * Reads one SI16 (Signed 16bit integer) value from the stream
     *
     * @param name
     * @return SI16 value
     * @throws IOException
     */
    public int readSI16(String name) throws IOException {
        this.newDumpLevel(name, "SI16");
        int uval = this.readEx() + (this.readEx() << 8);
        if (uval >= 0x8000) {
            uval = -(((~uval) & 0xffff) + 1);
        }
        this.endDumpLevel(uval);
        return uval;
    }

    /**
     * Reads one SI8 (Signed 8bit integer) value from the stream
     *
     * @param name
     * @return SI8 value
     * @throws IOException
     */
    public int readSI8(String name) throws IOException {
        this.newDumpLevel(name, "SI8");
        int ret = this.readSI8Internal();
        this.endDumpLevel(ret);
        return ret;
    }

    /**
     * Reads one SI8 (Signed 8bit integer) value from the stream
     *
     * @return SI8 value
     * @throws IOException
     */
    public int readSI8Internal() throws IOException {
        int uval = this.readEx();
        if (uval >= 0x80) {
            uval = -(((~uval) & 0xff) + 1);
        }
        return uval;
    }

    /**
     * Reads one FIXED (Fixed point 16.16) value from the stream
     *
     * @param name
     * @return FIXED value
     * @throws IOException
     */
    public double readFIXED(String name) throws IOException {
        this.newDumpLevel(name, "FIXED");
        int afterPoint = this.readUI16Internal();
        int beforePoint = this.readUI16Internal();
        double ret = beforePoint + ((double) (afterPoint)) / 65536;
        this.endDumpLevel(ret);
        return ret;
    }

    /**
     * Reads one FIXED8 (Fixed point 8.8) signed value from the stream
     *
     * @param name
     * @return FIXED8 value
     * @throws IOException
     */
    public float readFIXED8(String name) throws IOException {
        this.newDumpLevel(name, "FIXED8");
        int afterPoint = this.readEx();
        int beforePoint = this.readSI8Internal();
        float ret = beforePoint + ((float) afterPoint) / 256;
        this.endDumpLevel(ret);
        return ret;
    }

    private long readLong() throws IOException {
        byte[] readBuffer = this.readBytesInternalEx(8);
        return (((long) readBuffer[3] << 56)
                + ((long) (readBuffer[2] & 255) << 48)
                + ((long) (readBuffer[1] & 255) << 40)
                + ((long) (readBuffer[0] & 255) << 32)
                + ((long) (readBuffer[7] & 255) << 24)
                + ((readBuffer[6] & 255) << 16)
                + ((readBuffer[5] & 255) << 8)
                + ((readBuffer[4] & 255)));
    }

    /**
     * Reads one DOUBLE (double precision floating point value) value from the
     * stream
     *
     * @param name
     * @return DOUBLE value
     * @throws IOException
     */
    public double readDOUBLE(String name) throws IOException {
        this.newDumpLevel(name, "DOUBLE");
        long el = this.readLong();
        double ret = Double.longBitsToDouble(el);
        this.endDumpLevel(ret);
        return ret;
    }

    /**
     * Reads one FLOAT (single precision floating point value) value from the
     * stream
     *
     * @param name
     * @return FLOAT value
     * @throws IOException
     */
    public float readFLOAT(String name) throws IOException {
        this.newDumpLevel(name, "FLOAT");
        int val = (int) this.readUI32Internal();
        float ret = Float.intBitsToFloat(val);
        this.endDumpLevel(ret);
        /*int sign = val >> 31;
         int mantisa = val & 0x3FFFFF;
         int exp = (val >> 22) & 0xFF;
         float ret =(sign == 1 ? -1 : 1) * (float) Math.pow(2, exp)*  (1+((mantisa)/ (float)(1<<23)));*/
        return ret;
    }

    /**
     * Reads one FLOAT16 (16bit floating point value) value from the stream
     *
     * @param name
     * @return FLOAT16 value
     * @throws IOException
     */
    public float readFLOAT16(String name) throws IOException {
        this.newDumpLevel(name, "FLOAT16");
        int val = this.readUI16Internal();
        int sign = val >> 15;
        int mantisa = val & 0x3FF;
        int exp = (val >> 10) & 0x1F;
        float ret = (sign == 1 ? -1 : 1) * (float) Math.pow(2, exp) * (1 + ((mantisa) / (float) (1 << 10)));
        this.endDumpLevel(ret);
        return ret;
    }

    /**
     * Reads bytes from the stream
     *
     * @param count Number of bytes to read
     * @param name
     * @return Array of read bytes
     * @throws IOException
     */
    public byte[] readBytesEx(long count, String name) throws IOException {
        if (count <= 0) {
            return BYTE_ARRAY_EMPTY;
        }

        this.newDumpLevel(name, "bytes");
        byte[] ret = this.readBytesInternalEx(count);
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads AMF3 encoded value from the stream
     *
     * @param name
     * @return
     * @throws IOException
     */
    public Amf3Value readAmf3Object(String name) throws IOException, NoSerializerExistsException {
        Amf3InputStream ai = new Amf3InputStream(this.is);
        ai.dumpInfo = this.dumpInfo;

        return new Amf3Value(ai.readValue("amfData"));
    }

    /**
     * Reads byte range from the stream
     *
     * @param count Number of bytes to read
     * @param name
     * @return ByteArrayRange object
     * @throws IOException
     */
    public ByteArrayRange readByteRangeEx(long count, String name) throws IOException {
        return this.readByteRangeEx(count, name, DumpInfoSpecialType.NONE, null);
    }

    /**
     * Reads byte range from the stream
     *
     * @param count        Number of bytes to read
     * @param name
     * @param specialType
     * @param specialValue
     * @return ByteArrayRange object
     * @throws IOException
     */
    public ByteArrayRange readByteRangeEx(long count, String name, DumpInfoSpecialType specialType, Object specialValue) throws IOException {
        if (count <= 0) {
            return ByteArrayRange.EMPTY;
        }

        this.newDumpLevel(name, "bytes", specialType, specialValue);

        int startPos = (int) this.getPos();
        this.skipBytesEx(count);
        this.endDumpLevel();
        return new ByteArrayRange(this.swf.uncompressedData, startPos, (int) count);
    }

    /**
     * Reads bytes from the stream
     *
     * @param count Number of bytes to read
     * @return Array of read bytes
     * @throws IOException
     */
    private byte[] readBytesInternalEx(long count) throws IOException {
        if (count <= 0) {
            return BYTE_ARRAY_EMPTY;
        }

        this.bitPos = 0;
        byte[] ret = new byte[(int) count];
        if (this.is.read(ret) != count) {
            throw new EndOfStreamException();
        }

        this.informListeners();
        return ret;
    }

    /**
     * Skip bytes from the stream
     *
     * @param count Number of bytes to skip
     * @throws IOException
     */
    public void skipBytesEx(long count) throws IOException {
        if (count <= 0) {
            return;
        }

        this.bitPos = 0;
        this.is.seek(this.is.getPos() + count);
        if (this.is.available() < 0) {
            throw new EndOfStreamException();
        }

        this.informListeners();
    }

    /**
     * Skip bytes from the stream
     *
     * @param count Number of bytes to skip
     * @param name
     * @throws IOException
     */
    public void skipBytesEx(long count, String name) throws IOException {
        if (count <= 0) {
            return;
        }

        this.newDumpLevel(name, "bytes");
        this.skipBytesEx(count);
        this.endDumpLevel();
    }

    /**
     * Skip bytes from the stream
     *
     * @param count Number of bytes to skip
     * @throws IOException
     */
    public void skipBytes(long count) throws IOException {
        try {
            this.skipBytesEx(count);
        } catch (EOFException | EndOfStreamException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reads bytes from the stream
     *
     * @param count Number of bytes to read
     * @param name
     * @return Array of read bytes
     * @throws IOException
     */
    public byte[] readBytes(int count, String name) throws IOException {
        if (count <= 0) {
            return BYTE_ARRAY_EMPTY;
        }
        this.newDumpLevel(name, "bytes");
        byte[] ret = new byte[count];
        int i = 0;
        try {
            for (i = 0; i < count; i++) {
                ret[i] = (byte) this.readEx();
            }
        } catch (EOFException | EndOfStreamException ex) {
            ret = Arrays.copyOf(ret, i); // truncate array
            logger.log(Level.SEVERE, null, ex);
        }
        this.endDumpLevel();
        return ret;
    }

    public byte[] readBytesZlib(long count, String name) throws IOException {
        if (count == 0) {
            return BYTE_ARRAY_EMPTY;
        }

        this.newDumpLevel(name, "bytesZlib");
        byte[] data = this.readBytesInternalEx(count);
        this.endDumpLevel();
        return uncompressByteArray(data);
    }

    public static byte[] uncompressByteArray(byte[] data) throws IOException {
        return uncompressByteArray(data, 0, data.length);
    }

    public static byte[] uncompressByteArray(byte[] data, int offset, int length) throws IOException {
        InflaterInputStream dis = new InflaterInputStream(new ByteArrayInputStream(data, offset, length));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int c;
        while ((c = dis.read(buf)) > 0) {
            baos.write(buf, 0, c);
        }
        return baos.toByteArray();
    }

    /**
     * Reads one EncodedU32 (Encoded unsigned 32bit value) value from the stream
     *
     * @param name
     * @return U32 value
     * @throws IOException
     */
    public long readEncodedU32(String name) throws IOException {
        this.newDumpLevel(name, "encodedU32");
        int result = this.readEx();
        if ((result & 0x00000080) == 0) {
            this.endDumpLevel(result);
            return result;
        }
        result = (result & 0x0000007f) | (this.readEx()) << 7;
        if ((result & 0x00004000) == 0) {
            this.endDumpLevel(result);
            return result;
        }
        result = (result & 0x00003fff) | (this.readEx()) << 14;
        if ((result & 0x00200000) == 0) {
            this.endDumpLevel(result);
            return result;
        }
        result = (result & 0x001fffff) | (this.readEx()) << 21;
        if ((result & 0x10000000) == 0) {
            this.endDumpLevel(result);
            return result;
        }
        result = (result & 0x0fffffff) | (this.readEx()) << 28;
        this.endDumpLevel(result);
        return result;
    }

    private int bitPos = 0;

    private int tempByte = 0;

    /**
     * Reads UB[nBits] (Unsigned-bit value) value from the stream
     *
     * @param nBits Number of bits which represent value
     * @param name
     * @return Unsigned value
     * @throws IOException
     */
    public long readUB(int nBits, String name) throws IOException {
        if (nBits == 0) {
            return 0;
        }
        this.newDumpLevel(name, "UB");
        long ret = this.readUBInternal(nBits);
        this.endDumpLevel(ret);
        return ret;
    }

    /**
     * Reads UB[nBits] (Unsigned-bit value) value from the stream
     *
     * @param nBits Number of bits which represent value
     * @return Unsigned value
     * @throws IOException
     */
    private long readUBInternal(int nBits) throws IOException {
        if (nBits == 0) {
            return 0;
        }
        long ret = 0;
        if (this.bitPos == 0) {
            this.tempByte = this.readNoBitReset();
        }
        for (int bit = 0; bit < nBits; bit++) {
            int nb = (this.tempByte >> (7 - this.bitPos)) & 1;
            ret += (nb << (nBits - 1 - bit));
            this.bitPos++;
            if (this.bitPos == 8) {
                this.bitPos = 0;
                if (bit != nBits - 1) {
                    this.tempByte = this.readNoBitReset();
                }
            }
        }
        return ret;
    }

    /**
     * Reads SB[nBits] (Signed-bit value) value from the stream
     *
     * @param nBits Number of bits which represent value
     * @param name
     * @return Signed value
     * @throws IOException
     */
    public long readSB(int nBits, String name) throws IOException {
        if (nBits == 0) {
            return 0;
        }
        this.newDumpLevel(name, "SB");
        long ret = this.readSBInternal(nBits);
        this.endDumpLevel(ret);
        return ret;
    }

    /**
     * Reads SB[nBits] (Signed-bit value) value from the stream
     *
     * @param nBits Number of bits which represent value
     * @return Signed value
     * @throws IOException
     */
    private long readSBInternal(int nBits) throws IOException {
        int uval = (int) this.readUBInternal(nBits);

        int shift = 32 - nBits;
        // sign extension
        uval = (uval << shift) >> shift;
        return uval;
    }

    /**
     * Reads FB[nBits] (Signed fixed-point bit value) value from the stream
     *
     * @param nBits Number of bits which represent value
     * @param name
     * @return Fixed-point value
     * @throws IOException
     */
    public float readFB(int nBits, String name) throws IOException {
        if (nBits == 0) {
            return 0;
        }
        this.newDumpLevel(name, "FB");
        float val = this.readSBInternal(nBits);
        float ret = val / 0x10000;
        this.endDumpLevel(ret);
        return ret;
    }

    /**
     * Reads one RECT value from the stream
     *
     * @param name
     * @return RECT value
     * @throws IOException
     */
    public RECT readRECT(String name) throws IOException {
        RECT ret = new RECT();
        this.newDumpLevel(name, "RECT");
        int NBits = (int) this.readUB(5, "NBits");
        ret.Xmin = (int) this.readSB(NBits, "Xmin");
        ret.Xmax = (int) this.readSB(NBits, "Xmax");
        ret.Ymin = (int) this.readSB(NBits, "Ymin");
        ret.Ymax = (int) this.readSB(NBits, "Ymax");
        ret.nbits = NBits;
        this.alignByte();
        this.endDumpLevel();
        return ret;
    }

    private static void dumpTag(PrintStream out, Tag tag, int index, int level) {
        StringBuilder sb = new StringBuilder();
        sb.append(Helper.formatHex((int) tag.getPos(), 8));
        sb.append(": ");
        sb.append(Helper.indent(level, "", "  "));
        sb.append(Helper.formatInt(index, 4));
        sb.append(". ");
        sb.append(Helper.format(tag.toString(), 25 - 2 * level));
        sb.append(" tagId=");
        sb.append(Helper.formatInt(tag.getId(), 3));
        sb.append(" len=");
        sb.append(Helper.formatInt(tag.getOriginalDataLength(), 8));
        sb.append("  ");
        sb.append(Helper.bytesToHexString(64, tag.getOriginalData(), 0));
        out.println(sb.toString());
        // out.println(Utils.formatHex((int)tag.getPos(), 8) + ": " + Utils.indent(level, "") + Utils.format(tag.toString(), 25 - 2*level) + " tagId="+tag.getId()+" len="+tag.getOrigDataLength()+": "+Utils.bytesToHexString(64, tag.getData(version), 0));
        if (tag instanceof DefineSpriteTag) {
            int i = 0;
            for (Tag subTag : ((DefineSpriteTag) tag).getTags()) {
                dumpTag(out, subTag, i++, level + 1);
            }
        }
    }

    @Override
    public void close() {
    }

    private class TagResolutionTask implements Callable<Tag> {

        private final TagStub tag;

        private final DumpInfo dumpInfo;

        private final int level;

        private final boolean parallel;

        private final boolean skipUnusualTags;

        private final boolean lazy;

        public TagResolutionTask(TagStub tag, DumpInfo dumpInfo, int level, boolean parallel, boolean skipUnusualTags, boolean lazy) {
            this.tag = tag;
            this.dumpInfo = dumpInfo;
            this.level = level;
            this.parallel = parallel;
            this.skipUnusualTags = skipUnusualTags;
            this.lazy = lazy;
        }

        @Override
        public Tag call() throws Exception {
            DumpInfo di = this.dumpInfo;
            try {
                Tag t = resolveTag(this.tag, this.level, this.parallel, this.skipUnusualTags, this.lazy);
                if (this.dumpInfo != null && t != null) {
                    this.dumpInfo.name = t.getName();
                }
                return t;
            } catch (Exception ex) {
                this.tag.getDataStream().endDumpLevelUntil(di);
                logger.log(Level.SEVERE, null, ex);
                return this.tag;
            }
        }
    }

    /**
     * Reads list of tags from the stream. Reading ends with End tag(=0) or end
     * of the stream. Optionally can skip AS1/2 tags when file is AS3
     *
     * @param timelined
     * @param level
     * @param parallel
     * @param skipUnusualTags
     * @param parseTags
     * @param lazy
     * @return List of tags
     * @throws IOException
     * @throws java.lang.InterruptedException
     */
    public List<Tag> readTagList(Timelined timelined, int level, boolean parallel, boolean skipUnusualTags, boolean parseTags, boolean lazy) throws IOException, InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }

        boolean parallel1 = level == 0 && parallel;
        ExecutorService executor = null;
        List<Future<Tag>> futureResults = new ArrayList<>();
        if (parallel1) {
            executor = Executors.newFixedThreadPool(Configuration.getParallelThreadCount());
            futureResults = new ArrayList<>();
        }
        List<Tag> tags = new ArrayList<>();
        Tag tag;
        boolean isAS3 = false;
        while (this.available() > 0) {
            long pos = this.getPos();
            this.newDumpLevel(null, "TAG", DumpInfoSpecialType.TAG, this.getPos());
            try {
                tag = this.readTag(timelined, level, pos, false, parallel1, skipUnusualTags, lazy);
            } catch (EOFException | EndOfStreamException ex) {
                tag = null;
            }

            boolean doParse = true;
            if (!skipUnusualTags) {
                doParse = true;
            } else if (tag != null) {
                switch (tag.getId()) {
                    case FileAttributesTag.ID: // FileAttributes
                        if (tag instanceof TagStub) {
                            tag = resolveTag((TagStub) tag, level, parallel1, skipUnusualTags, lazy);
                        }
                        FileAttributesTag fileAttributes = (FileAttributesTag) tag;
                        if (fileAttributes.actionScript3) {
                            isAS3 = true;
                        }
                        doParse = true;
                        break;
                    case DoActionTag.ID:
                    case DoInitActionTag.ID:
                        doParse = !isAS3;
                        break;
                    case ShowFrameTag.ID:
                    case PlaceObjectTag.ID:
                    case PlaceObject2Tag.ID:
                    case RemoveObjectTag.ID:
                    case RemoveObject2Tag.ID:
                    case PlaceObject3Tag.ID: // ?
                    case StartSoundTag.ID:
                    case FrameLabelTag.ID:
                    case SoundStreamBlockTag.ID:
                    case VideoFrameTag.ID:
                    case EndTag.ID:
                        doParse = true;
                        break;
                    default:
                        if (level > 0) { //No such tags in DefineSprite allowed
                            logger.log(Level.FINE, "Tag({0}) found in DefineSprite => Ignored", tag.getId());
                            doParse = false;
                        } else {
                            doParse = true;
                        }

                }
            }

            if (parseTags && !parallel1 && doParse && (tag instanceof TagStub)) {
                tag = resolveTag((TagStub) tag, level, parallel, skipUnusualTags, lazy);
            }
            DumpInfo di = this.dumpInfo;
            if (di != null && tag != null) {
                di.name = tag.getName();
            }
            this.endDumpLevel(tag == null ? null : tag.getId());
            if (tag == null) {
                break;
            }

            tag.setTimelined(timelined);
            if (!parallel1) {
                tags.add(tag);
            }
            if (Configuration.dumpTags.get() && level == 0) {
                dumpTag(System.out, tag, tags.size() - 1, level);
            }

            if (parseTags && doParse && parallel1 && tag instanceof TagStub && executor != null) {
                Future<Tag> future = executor.submit(new TagResolutionTask((TagStub) tag, di, level, parallel1, skipUnusualTags, lazy));
                futureResults.add(future);
            } else {
                Future<Tag> future = new ImmediateFuture<>(tag);
                futureResults.add(future);
                if (!(tag instanceof TagStub)) {
                    if (di != null) {
                        di.name = tag.getName();
                    }
                }
            }

            if (tag.getId() == EndTag.ID) {
                break;
            }
        }

        if (parallel1) {
            for (Future<Tag> future : futureResults) {
                try {
                    tags.add(future.get());
                } catch (InterruptedException ex) {
                    future.cancel(true);
                } catch (ExecutionException e) {
                    logger.log(Level.SEVERE, "Error during tag reading", e);
                }
            }

            if (executor != null) {
                executor.shutdown();
            }
        }
        return tags;
    }

    public static Tag resolveTag(TagStub tag, int level, boolean parallel, boolean skipUnusualTags, boolean lazy) throws InterruptedException {
        Tag ret;

        ByteArrayRange data = tag.getOriginalRange();
        SWF swf = tag.getSwf();
        SWFInputStream sis = tag.getDataStream();

        try {
            switch (tag.getId()) {
                case 0:
                    ret = new EndTag(sis, data);
                    break;
                case 1:
                    ret = new ShowFrameTag(sis, data);
                    break;
                case 2:
                    ret = new DefineShapeTag(sis, data, lazy);
                    break;
                case 3:
                    ret = new FreeCharacterTag(sis, data);
                    break;
                case 4:
                    ret = new PlaceObjectTag(sis, data);
                    break;
                case 5:
                    ret = new RemoveObjectTag(sis, data);
                    break;
                case 6:
                    ret = new DefineBitsTag(sis, data);
                    break;
                case 7:
                    ret = new DefineButtonTag(sis, data);
                    break;
                case 8:
                    ret = new JPEGTablesTag(sis, data);
                    break;
                case 9:
                    ret = new SetBackgroundColorTag(sis, data);
                    break;
                case 10:
                    ret = new DefineFontTag(sis, data);
                    break;
                case 12:
                    ret = new DoActionTag(sis, data);
                    break;
                case 13:
                    ret = new DefineFontInfoTag(sis, data);
                    break;
                case 15:
                    ret = new StartSoundTag(sis, data);
                    break;
                //case 16: StopSound
                case 17:
                    ret = new DefineButtonSoundTag(sis, data);
                    break;
                case 19:
                    ret = new SoundStreamBlockTag(sis, data);
                    break;
                case 20:
                    ret = new DefineBitsLosslessTag(sis, data);
                    break;
                case 21:
                    ret = new DefineBitsJPEG2Tag(sis, data);
                    break;
                case 22:
                    ret = new DefineShape2Tag(sis, data, lazy);
                    break;
                case 23:
                    ret = new DefineButtonCxformTag(sis, data);
                    break;
                case 24:
                    ret = new ProtectTag(sis, data);
                    break;
                case 25:
                    ret = new PathsArePostScriptTag(sis, data);
                    break;
                case 26:
                    ret = new PlaceObject2Tag(sis, data);
                    break;
                //case 27:
                case 28:
                    ret = new RemoveObject2Tag(sis, data);
                    break;
                case 29:
                    ret = new SyncFrameTag(sis, data);
                    break;
                //case 30:
                case 31:
                    ret = new FreeAllTag(sis, data);
                    break;
                case 32:
                    ret = new DefineShape3Tag(sis, data, lazy);
                    break;
                case 34:
                    ret = new DefineButton2Tag(sis, data);
                    break;
                case 35:
                    ret = new DefineBitsJPEG3Tag(sis, data);
                    break;
                case 36:
                    ret = new DefineBitsLossless2Tag(sis, data);
                    break;
                //case 38: DefineMouseTarget
                case 39:
                    ret = new DefineSpriteTag(sis, level, data, parallel, skipUnusualTags);
                    break;
                case 40:
                    ret = new NameCharacterTag(sis, data);
                    break;
                case 41: //or NameObject
                    ret = new ProductInfoTag(sis, data);
                    break;
                //case 42: DefineTextFormat
                case 43:
                    ret = new FrameLabelTag(sis, data);
                    break;
                //case 44: DefineBehavior
                case 46:
                    ret = new DefineMorphShapeTag(sis, data);
                    break;
                //case 47: FrameTag
                case 48:
                    ret = new DefineFont2Tag(sis, data);
                    break;
                //case 49: GenCommand
                //case 50: DefineCommandObj
                //case 51: CharacterSet
                //case 52: FontRef
                //case 53: DefineFunction
                //case 54: PlaceFunction
                //case 55: GenTagObject
                case 56:
                    ret = new ExportAssetsTag(sis, data);
                    break;
                case 57:
                    ret = new ImportAssetsTag(sis, data);
                    break;
                case 58:
                    ret = new EnableDebuggerTag(sis, data);
                    break;
                case 59:
                    ret = new DoInitActionTag(sis, data);
                    break;
                case 60:
                    ret = new DefineVideoStreamTag(sis, data);
                    break;
                case 61:
                    ret = new VideoFrameTag(sis, data);
                    break;
                case 62:
                    ret = new DefineFontInfo2Tag(sis, data);
                    break;
                case 63:
                    ret = new DebugIDTag(sis, data);
                    break;
                case 64:
                    ret = new EnableDebugger2Tag(sis, data);
                    break;
                case 65:
                    ret = new ScriptLimitsTag(sis, data);
                    break;
                case 66:
                    ret = new SetTabIndexTag(sis, data);
                    break;
                //case 67: DefineShape4 ???
                //case 68: DefineMorphShape2 ???
                case 69:
                    ret = new FileAttributesTag(sis, data);
                    break;
                case 70:
                    ret = new PlaceObject3Tag(sis, data);
                    break;
                case 71:
                    ret = new ImportAssets2Tag(sis, data);
                    break;
                case 72:
                    ret = new DoABCTag(sis, data);
                    break;
                case 73:
                    ret = new DefineFontAlignZonesTag(sis, data);
                    break;
                case 74:
                    ret = new CSMTextSettingsTag(sis, data);
                    break;
                case 75:
                    ret = new DefineFont3Tag(sis, data);
                    break;
                case 76:
                    ret = new SymbolClassTag(sis, data);
                    break;
                case 77:
                    ret = new MetadataTag(sis, data);
                    break;
                case 78:
                    ret = new DefineScalingGridTag(sis, data);
                    break;
                //case 79: DefineDeviceVideo
                //case 80-81:
                case 82:
                    ret = new DoABC2Tag(sis, data);
                    break;
                case 83:
                    ret = new DefineShape4Tag(sis, data, lazy);
                    break;
                case 84:
                    ret = new DefineMorphShape2Tag(sis, data);
                    break;
                //case 85: PlaceImagePrivate
                case 86:
                    ret = new DefineSceneAndFrameLabelDataTag(sis, data);
                    break;
                case 87:
                    ret = new DefineBinaryDataTag(sis, data);
                    break;
                case 88:
                    ret = new DefineFontNameTag(sis, data);
                    break;
                case 89:
                    ret = new StartSound2Tag(sis, data);
                    break;
                case 90:
                    ret = new DefineBitsJPEG4Tag(sis, data);
                    break;
                case 91:
                    ret = new DefineFont4Tag(sis, data);
                    break;
                //case 92: certificate
                case 93:
                    ret = new EnableTelemetryTag(sis, data);
                    break;
                case 94:
                    ret = new PlaceObject4Tag(sis, data);
                    break;
                default:
                    if (swf.gfx) { // GFX tags only in GFX files. There may be incorrect GFX tags in non GFX files
                        switch (tag.getId()) {
                            case 1000:
                                ret = new ExporterInfo(sis, data);
                                break;
                            case 1001:
                                ret = new DefineExternalImage(sis, data);
                                break;
                            case 1002:
                                ret = new FontTextureInfo(sis, data);
                                break;
                            case 1003:
                                ret = new DefineExternalGradient(sis, data);
                                break;
                            case 1004:
                                ret = new DefineGradientMap(sis, data);
                                break;
                            case 1005:
                                ret = new DefineCompactedFont(sis, data);
                                break;
                            case 1006:
                                ret = new DefineExternalSound(sis, data);
                                break;
                            case 1007:
                                ret = new DefineExternalStreamSound(sis, data);
                                break;
                            case 1008:
                                ret = new DefineSubImage(sis, data);
                                break;
                            case 1009:
                                ret = new DefineExternalImage2(sis, data);
                                break;
                            default:
                                ret = new UnknownTag(sis, tag.getId(), data);
                        }
                    } else {
                        ret = new UnknownTag(sis, tag.getId(), data);
                    }
            }

            if (sis.available() > 0) {
                ret.remainingData = sis.readByteRangeEx(sis.available(), "remaining");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error during tag reading. SWF: " + swf.getShortFileName() + " ID: " + tag.getId() + " name: " + tag.getName() + " pos: " + data.getPos(), ex);
            ret = new TagStub(swf, tag.getId(), "ErrorTag", data, null);
        }
        ret.forceWriteAsLong = tag.forceWriteAsLong;
        ret.setTimelined(tag.getTimelined());
        return ret;
    }

    /**
     * Reads one Tag from the stream with optional resolving (= reading tag
     * content)
     *
     * @param timelined
     * @param level
     * @param pos
     * @param resolve
     * @param parallel
     * @param skipUnusualTags
     * @param lazy
     * @return Tag or null when End tag
     * @throws IOException
     * @throws java.lang.InterruptedException
     */
    public Tag readTag(Timelined timelined, int level, long pos, boolean resolve, boolean parallel, boolean skipUnusualTags, boolean lazy) throws IOException, InterruptedException {
        int tagIDTagLength = this.readUI16("tagIDTagLength");
        int tagID = (tagIDTagLength) >> 6;

        logger.log(Level.FINE, "Reading tag. ID={0}, position: {1}", new Object[]{tagID, pos});

        long tagLength = (tagIDTagLength & 0x003F);
        boolean readLong = false;
        if (tagLength == 0x3f) {
            tagLength = this.readSI32("tagLength");
            readLong = true;
        }
        int headerLength = readLong ? 6 : 2;
        SWFInputStream tagDataStream = this.getLimitedStream((int) tagLength);
        int available = this.available();
        if (tagLength > available) {
            tagLength = available;
        }

        ByteArrayRange dataRange = new ByteArrayRange(this.swf.uncompressedData, (int) pos, (int) (tagLength + headerLength));
        this.skipBytes(tagLength);

        TagStub tagStub = new TagStub(this.swf, tagID, "Unresolved", dataRange, tagDataStream);
        tagStub.forceWriteAsLong = readLong;
        Tag ret = tagStub;

        if (tagDataStream.dumpInfo == null && this.dumpInfo != null) {
            this.dumpInfo.tagToResolve = tagStub;
        }

        if (resolve) {
            DumpInfo di = this.dumpInfo;
            try {
                ret = resolveTag(tagStub, level, parallel, skipUnusualTags, lazy);
            } catch (Exception ex) {
                tagDataStream.endDumpLevelUntil(di);
                logger.log(Level.SEVERE, "Problem in " + timelined.toString(), ex);
            }

            if (Configuration._debugMode.get()) {
                byte[] data = ret.getOriginalData();
                byte[] dataNew = ret.getData();
                int ignoreFirst = 0;
                for (int i = 0; i < data.length; i++) {
                    if (i >= dataNew.length) {
                        break;
                    }
                    if (dataNew[i] != data[i]) {
                        if (ignoreFirst > 0) {
                            ignoreFirst--;
                            continue;
                        }
                        String e = "TAG " + ret.toString() + " WRONG, ";
                        for (int j = i - 10; j <= i + 5; j++) {
                            while (j < 0) {
                                j++;
                            }
                            if (j >= data.length) {
                                break;
                            }
                            if (j >= dataNew.length) {
                                break;
                            }
                            if (j >= i) {
                                e += (Long.toHexString(data[j] & 0xff) + " ( is " + Long.toHexString(dataNew[j] & 0xff) + ") ");
                            } else {
                                e += (Long.toHexString(data[j] & 0xff) + " ");
                            }
                        }
                        logger.fine(e);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Reads one Action from the stream
     *
     * @return Action or null when ActionEndFlag or end of the stream
     * @throws IOException
     */
    public Action readAction() throws IOException {
        int actionCode;

        try {
            actionCode = this.readUI8("actionCode");
            if (actionCode == 0) {
                return new ActionEnd();
            }
            if (actionCode == -1) {
                return null;
            }
            int actionLength = 0;
            if (actionCode >= 0x80) {
                actionLength = this.readUI16("actionLength");
            }
            switch (actionCode) {
                // SWF3 Actions
                case 0x81:
                    return new ActionGotoFrame(actionLength, this);
                case 0x83:
                    return new ActionGetURL(actionLength, this, this.swf.version);
                case 0x04:
                    return new ActionNextFrame();
                case 0x05:
                    return new ActionPrevFrame();
                case 0x06:
                    return new ActionPlay();
                case 0x07:
                    return new ActionStop();
                case 0x08:
                    return new ActionToggleQuality();
                case 0x09:
                    return new ActionStopSounds();
                case 0x8A:
                    return new ActionWaitForFrame(actionLength, this);
                case 0x8B:
                    return new ActionSetTarget(actionLength, this, this.swf.version);
                case 0x8C:
                    return new ActionGoToLabel(actionLength, this, this.swf.version);
                // SWF4 Actions
                case 0x96:
                    return new ActionPush(actionLength, this, this.swf.version);
                case 0x17:
                    return new ActionPop();
                case 0x0A:
                    return new ActionAdd();
                case 0x0B:
                    return new ActionSubtract();
                case 0x0C:
                    return new ActionMultiply();
                case 0x0D:
                    return new ActionDivide();
                case 0x0E:
                    return new ActionEquals();
                case 0x0F:
                    return new ActionLess();
                case 0x10:
                    return new ActionAnd();
                case 0x11:
                    return new ActionOr();
                case 0x12:
                    return new ActionNot();
                case 0x13:
                    return new ActionStringEquals();
                case 0x14:
                    return new ActionStringLength();
                case 0x21:
                    return new ActionStringAdd();
                case 0x15:
                    return new ActionStringExtract();
                case 0x29:
                    return new ActionStringLess();
                case 0x31:
                    return new ActionMBStringLength();
                case 0x35:
                    return new ActionMBStringExtract();
                case 0x18:
                    return new ActionToInteger();
                case 0x32:
                    return new ActionCharToAscii();
                case 0x33:
                    return new ActionAsciiToChar();
                case 0x36:
                    return new ActionMBCharToAscii();
                case 0x37:
                    return new ActionMBAsciiToChar();
                case 0x99:
                    return new ActionJump(actionLength, this);
                case 0x9D:
                    return new ActionIf(actionLength, this);
                case 0x9E:
                    return new ActionCall(actionLength);
                case 0x1C:
                    return new ActionGetVariable();
                case 0x1D:
                    return new ActionSetVariable();
                case 0x9A:
                    return new ActionGetURL2(actionLength, this);
                case 0x9F:
                    return new ActionGotoFrame2(actionLength, this);
                case 0x20:
                    return new ActionSetTarget2();
                case 0x22:
                    return new ActionGetProperty();
                case 0x23:
                    return new ActionSetProperty();
                case 0x24:
                    return new ActionCloneSprite();
                case 0x25:
                    return new ActionRemoveSprite();
                case 0x27:
                    return new ActionStartDrag();
                case 0x28:
                    return new ActionEndDrag();
                case 0x8D:
                    return new ActionWaitForFrame2(actionLength, this);
                case 0x26:
                    return new ActionTrace();
                case 0x34:
                    return new ActionGetTime();
                case 0x30:
                    return new ActionRandomNumber();
                // SWF5 Actions
                case 0x3D:
                    return new ActionCallFunction();
                case 0x52:
                    return new ActionCallMethod();
                case 0x88:
                    return new ActionConstantPool(actionLength, this, this.swf.version);
                case 0x9B:
                    return new ActionDefineFunction(actionLength, this, this.swf.version);
                case 0x3C:
                    return new ActionDefineLocal();
                case 0x41:
                    return new ActionDefineLocal2();
                case 0x3A:
                    return new ActionDelete();
                case 0x3B:
                    return new ActionDelete2();
                case 0x46:
                    return new ActionEnumerate();
                case 0x49:
                    return new ActionEquals2();
                case 0x4E:
                    return new ActionGetMember();
                case 0x42:
                    return new ActionInitArray();
                case 0x43:
                    return new ActionInitObject();
                case 0x53:
                    return new ActionNewMethod();
                case 0x40:
                    return new ActionNewObject();
                case 0x4F:
                    return new ActionSetMember();
                case 0x45:
                    return new ActionTargetPath();
                case 0x94:
                    return new ActionWith(actionLength, this, this.swf.version);
                case 0x4A:
                    return new ActionToNumber();
                case 0x4B:
                    return new ActionToString();
                case 0x44:
                    return new ActionTypeOf();
                case 0x47:
                    return new ActionAdd2();
                case 0x48:
                    return new ActionLess2();
                case 0x3F:
                    return new ActionModulo();
                case 0x60:
                    return new ActionBitAnd();
                case 0x63:
                    return new ActionBitLShift();
                case 0x61:
                    return new ActionBitOr();
                case 0x64:
                    return new ActionBitRShift();
                case 0x65:
                    return new ActionBitURShift();
                case 0x62:
                    return new ActionBitXor();
                case 0x51:
                    return new ActionDecrement();
                case 0x50:
                    return new ActionIncrement();
                case 0x4C:
                    return new ActionPushDuplicate();
                case 0x3E:
                    return new ActionReturn();
                case 0x4D:
                    return new ActionStackSwap();
                case 0x87:
                    return new ActionStoreRegister(actionLength, this);
                // SWF6 Actions
                case 0x54:
                    return new ActionInstanceOf();
                case 0x55:
                    return new ActionEnumerate2();
                case 0x66:
                    return new ActionStrictEquals();
                case 0x67:
                    return new ActionGreater();
                case 0x68:
                    return new ActionStringGreater();
                // SWF7 Actions
                case 0x8E:
                    return new ActionDefineFunction2(actionLength, this, this.swf.version);
                case 0x69:
                    return new ActionExtends();
                case 0x2B:
                    return new ActionCastOp();
                case 0x2C:
                    return new ActionImplementsOp();
                case 0x8F:
                    return new ActionTry(actionLength, this, this.swf.version);
                case 0x2A:
                    return new ActionThrow();
                default:
                    /*if (actionLength > 0) {
                     //skip(actionLength);
                     }*/
                    //throw new UnknownActionException(actionCode);
                    Action r = new ActionUnknown(actionCode, actionLength);
                    if (Configuration.useDetailedLogging.get()) {
                        logger.log(Level.SEVERE, "Unknown action code: {0}", actionCode);
                    }
                    return r;
            }
        } catch (EndOfStreamException | ArrayIndexOutOfBoundsException eos) {
            return null;
        }
    }

    /**
     * Reads one MATRIX value from the stream
     *
     * @param name
     * @return MATRIX value
     * @throws IOException
     */
    public MATRIX readMatrix(String name) throws IOException {
        MATRIX ret = new MATRIX();
        this.newDumpLevel(name, "MATRIX");
        ret.hasScale = this.readUB(1, "hasScale") == 1;
        if (ret.hasScale) {
            int NScaleBits = (int) this.readUB(5, "NScaleBits");
            ret.scaleX = (int) this.readSB(NScaleBits, "scaleX");
            ret.scaleY = (int) this.readSB(NScaleBits, "scaleY");
            ret.nScaleBits = NScaleBits;
        }
        ret.hasRotate = this.readUB(1, "hasRotate") == 1;
        if (ret.hasRotate) {
            int NRotateBits = (int) this.readUB(5, "NRotateBits");
            ret.rotateSkew0 = (int) this.readSB(NRotateBits, "rotateSkew0");
            ret.rotateSkew1 = (int) this.readSB(NRotateBits, "rotateSkew1");
            ret.nRotateBits = NRotateBits;
        }
        int NTranslateBits = (int) this.readUB(5, "NTranslateBits");
        ret.translateX = (int) this.readSB(NTranslateBits, "translateX");
        ret.translateY = (int) this.readSB(NTranslateBits, "translateY");
        ret.nTranslateBits = NTranslateBits;
        this.alignByte();
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one CXFORMWITHALPHA value from the stream
     *
     * @param name
     * @return CXFORMWITHALPHA value
     * @throws IOException
     */
    public CXFORMWITHALPHA readCXFORMWITHALPHA(String name) throws IOException {
        CXFORMWITHALPHA ret = new CXFORMWITHALPHA();
        this.newDumpLevel(name, "CXFORMWITHALPHA");
        ret.hasAddTerms = this.readUB(1, "hasAddTerms") == 1;
        ret.hasMultTerms = this.readUB(1, "hasMultTerms") == 1;
        int Nbits = (int) this.readUB(4, "Nbits");
        ret.nbits = Nbits;
        if (ret.hasMultTerms) {
            ret.redMultTerm = (int) this.readSB(Nbits, "redMultTerm");
            ret.greenMultTerm = (int) this.readSB(Nbits, "greenMultTerm");
            ret.blueMultTerm = (int) this.readSB(Nbits, "blueMultTerm");
            ret.alphaMultTerm = (int) this.readSB(Nbits, "alphaMultTerm");
        }
        if (ret.hasAddTerms) {
            ret.redAddTerm = (int) this.readSB(Nbits, "redAddTerm");
            ret.greenAddTerm = (int) this.readSB(Nbits, "greenAddTerm");
            ret.blueAddTerm = (int) this.readSB(Nbits, "blueAddTerm");
            ret.alphaAddTerm = (int) this.readSB(Nbits, "alphaAddTerm");
        }
        this.alignByte();
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one CXFORM value from the stream
     *
     * @param name
     * @return CXFORM value
     * @throws IOException
     */
    public CXFORM readCXFORM(String name) throws IOException {
        CXFORM ret = new CXFORM();
        this.newDumpLevel(name, "CXFORM");
        ret.hasAddTerms = this.readUB(1, "hasAddTerms") == 1;
        ret.hasMultTerms = this.readUB(1, "hasMultTerms") == 1;
        int Nbits = (int) this.readUB(4, "Nbits");
        ret.nbits = Nbits;
        if (ret.hasMultTerms) {
            ret.redMultTerm = (int) this.readSB(Nbits, "redMultTerm");
            ret.greenMultTerm = (int) this.readSB(Nbits, "greenMultTerm");
            ret.blueMultTerm = (int) this.readSB(Nbits, "blueMultTerm");
        }
        if (ret.hasAddTerms) {
            ret.redAddTerm = (int) this.readSB(Nbits, "redAddTerm");
            ret.greenAddTerm = (int) this.readSB(Nbits, "greenAddTerm");
            ret.blueAddTerm = (int) this.readSB(Nbits, "blueAddTerm");
        }
        this.alignByte();
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one CLIPEVENTFLAGS value from the stream
     *
     * @param name
     * @return CLIPEVENTFLAGS value
     * @throws IOException
     */
    public CLIPEVENTFLAGS readCLIPEVENTFLAGS(String name) throws IOException {
        CLIPEVENTFLAGS ret = new CLIPEVENTFLAGS();
        this.newDumpLevel(name, "CLIPEVENTFLAGS");
        ret.clipEventKeyUp = this.readUB(1, "clipEventKeyUp") == 1;
        ret.clipEventKeyDown = this.readUB(1, "clipEventKeyDown") == 1;
        ret.clipEventMouseUp = this.readUB(1, "clipEventMouseUp") == 1;
        ret.clipEventMouseDown = this.readUB(1, "clipEventMouseDown") == 1;
        ret.clipEventMouseMove = this.readUB(1, "clipEventMouseMove") == 1;
        ret.clipEventUnload = this.readUB(1, "clipEventUnload") == 1;
        ret.clipEventEnterFrame = this.readUB(1, "clipEventEnterFrame") == 1;
        ret.clipEventLoad = this.readUB(1, "clipEventLoad") == 1;
        ret.clipEventDragOver = this.readUB(1, "clipEventDragOver") == 1;
        ret.clipEventRollOut = this.readUB(1, "clipEventRollOut") == 1;
        ret.clipEventRollOver = this.readUB(1, "clipEventRollOver") == 1;
        ret.clipEventReleaseOutside = this.readUB(1, "clipEventReleaseOutside") == 1;
        ret.clipEventRelease = this.readUB(1, "clipEventRelease") == 1;
        ret.clipEventPress = this.readUB(1, "clipEventPress") == 1;
        ret.clipEventInitialize = this.readUB(1, "clipEventInitialize") == 1;
        ret.clipEventData = this.readUB(1, "clipEventData") == 1;
        if (this.swf.version >= 6) {
            ret.reserved = (int) this.readUB(5, "reserved");
            ret.clipEventConstruct = this.readUB(1, "clipEventConstruct") == 1;
            ret.clipEventKeyPress = this.readUB(1, "clipEventKeyPress") == 1;
            ret.clipEventDragOut = this.readUB(1, "clipEventDragOut") == 1;
            ret.reserved2 = (int) this.readUB(8, "reserved2");
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one CLIPACTIONRECORD value from the stream
     *
     * @param swf
     * @param tag
     * @param name
     * @param parentClipActions
     * @return CLIPACTIONRECORD value
     * @throws IOException
     */
    public CLIPACTIONRECORD readCLIPACTIONRECORD(SWF swf, Tag tag, String name, CLIPACTIONS parentClipActions) throws IOException {
        this.newDumpLevel(name, "CLIPACTIONRECORD");
        CLIPACTIONRECORD ret = new CLIPACTIONRECORD(swf, this, tag, parentClipActions);
        this.endDumpLevel();
        if (ret.eventFlags.isClear()) {
            return null;
        }
        return ret;
    }

    /**
     * Reads one CLIPACTIONS value from the stream
     *
     * @param swf
     * @param tag
     * @param name
     * @return CLIPACTIONS value
     * @throws IOException
     */
    public CLIPACTIONS readCLIPACTIONS(SWF swf, Tag tag, String name) throws IOException {
        CLIPACTIONS ret = new CLIPACTIONS();
        this.newDumpLevel(name, "CLIPACTIONS");
        ret.reserved = this.readUI16("reserved");
        ret.allEventFlags = this.readCLIPEVENTFLAGS("allEventFlags");
        CLIPACTIONRECORD cr;
        ret.clipActionRecords = new ArrayList<>();
        while ((cr = this.readCLIPACTIONRECORD(swf, tag, "record", ret)) != null) {
            ret.clipActionRecords.add(cr);
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one COLORMATRIXFILTER value from the stream
     *
     * @param name
     * @return COLORMATRIXFILTER value
     * @throws IOException
     */
    public COLORMATRIXFILTER readCOLORMATRIXFILTER(String name) throws IOException {
        COLORMATRIXFILTER ret = new COLORMATRIXFILTER();
        this.newDumpLevel(name, "COLORMATRIXFILTER");
        ret.matrix = new float[20];
        for (int i = 0; i < 20; i++) {
            ret.matrix[i] = this.readFLOAT("cell");
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one RGBA value from the stream
     *
     * @param name
     * @return RGBA value
     * @throws IOException
     */
    public RGBA readRGBA(String name) throws IOException {
        RGBA ret = new RGBA();
        this.newDumpLevel(name, "RGBA");
        ret.red = this.readUI8("red");
        ret.green = this.readUI8("green");
        ret.blue = this.readUI8("blue");
        ret.alpha = this.readUI8("alpha");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one RGBA value from the stream
     *
     * @param name
     * @return RGBA value
     * @throws IOException
     */
    public int readRGBAInt(String name) throws IOException {
        this.newDumpLevel(name, "RGBA");
        int ret = (this.readUI8("red") << 16)
                | (this.readUI8("green") << 8)
                | this.readUI8("blue")
                | (this.readUI8("alpha") << 24);
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one ARGB value from the stream
     *
     * @param name
     * @return ARGB value
     * @throws IOException
     */
    public ARGB readARGB(String name) throws IOException {
        ARGB ret = new ARGB();
        this.newDumpLevel(name, "ARGB");
        ret.alpha = this.readUI8("alpha");
        ret.red = this.readUI8("red");
        ret.green = this.readUI8("green");
        ret.blue = this.readUI8("blue");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one ARGB value from the stream
     *
     * @param name
     * @return ARGB value
     * @throws IOException
     */
    public int readARGBInt(String name) throws IOException {
        this.newDumpLevel(name, "ARGB");
        int ret = (this.readUI8("alpha") << 24)
                | (this.readUI8("red") << 16)
                | (this.readUI8("green") << 8)
                | this.readUI8("blue");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one RGB value from the stream
     *
     * @param name
     * @return RGB value
     * @throws IOException
     */
    public RGB readRGB(String name) throws IOException {
        RGB ret = new RGB();
        this.newDumpLevel(name, "RGB");
        ret.red = this.readUI8("red");
        ret.green = this.readUI8("green");
        ret.blue = this.readUI8("blue");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one RGB value from the stream
     *
     * @param name
     * @return RGB value
     * @throws IOException
     */
    public int readRGBInt(String name) throws IOException {
        this.newDumpLevel(name, "RGB");
        int ret = (0xff << 24)
                | (this.readUI8("red") << 16)
                | (this.readUI8("green") << 8)
                | this.readUI8("blue");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one CONVOLUTIONFILTER value from the stream
     *
     * @param name
     * @return CONVOLUTIONFILTER value
     * @throws IOException
     */
    public CONVOLUTIONFILTER readCONVOLUTIONFILTER(String name) throws IOException {
        CONVOLUTIONFILTER ret = new CONVOLUTIONFILTER();
        this.newDumpLevel(name, "CONVOLUTIONFILTER");
        ret.matrixX = this.readUI8("matrixX");
        ret.matrixY = this.readUI8("matrixY");
        ret.divisor = this.readFLOAT("divisor");
        ret.bias = this.readFLOAT("bias");
        ret.matrix = new float[ret.matrixX][ret.matrixY];
        for (int x = 0; x < ret.matrixX; x++) {
            for (int y = 0; y < ret.matrixY; y++) {
                ret.matrix[x][y] = this.readFLOAT("cell");
            }
        }
        ret.defaultColor = this.readRGBA("defaultColor");
        ret.reserved = (int) this.readUB(6, "reserved");
        ret.clamp = this.readUB(1, "clamp") == 1;
        ret.preserveAlpha = this.readUB(1, "preserveAlpha") == 1;
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one BLURFILTER value from the stream
     *
     * @param name
     * @return BLURFILTER value
     * @throws IOException
     */
    public BLURFILTER readBLURFILTER(String name) throws IOException {
        BLURFILTER ret = new BLURFILTER();
        this.newDumpLevel(name, "BLURFILTER");
        ret.blurX = this.readFIXED("blurX");
        ret.blurY = this.readFIXED("blurY");
        ret.passes = (int) this.readUB(5, "passes");
        ret.reserved = (int) this.readUB(3, "reserved");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one DROPSHADOWFILTER value from the stream
     *
     * @param name
     * @return DROPSHADOWFILTER value
     * @throws IOException
     */
    public DROPSHADOWFILTER readDROPSHADOWFILTER(String name) throws IOException {
        DROPSHADOWFILTER ret = new DROPSHADOWFILTER();
        this.newDumpLevel(name, "DROPSHADOWFILTER");
        ret.dropShadowColor = this.readRGBA("dropShadowColor");
        ret.blurX = this.readFIXED("blurX");
        ret.blurY = this.readFIXED("blurY");
        ret.angle = this.readFIXED("angle");
        ret.distance = this.readFIXED("distance");
        ret.strength = this.readFIXED8("strength");
        ret.innerShadow = this.readUB(1, "innerShadow") == 1;
        ret.knockout = this.readUB(1, "knockout") == 1;
        ret.compositeSource = this.readUB(1, "compositeSource") == 1;
        ret.passes = (int) this.readUB(5, "passes");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one GLOWFILTER value from the stream
     *
     * @param name
     * @return GLOWFILTER value
     * @throws IOException
     */
    public GLOWFILTER readGLOWFILTER(String name) throws IOException {
        GLOWFILTER ret = new GLOWFILTER();
        this.newDumpLevel(name, "GLOWFILTER");
        ret.glowColor = this.readRGBA("glowColor");
        ret.blurX = this.readFIXED("blurX");
        ret.blurY = this.readFIXED("blurY");
        ret.strength = this.readFIXED8("strength");
        ret.innerGlow = this.readUB(1, "innerGlow") == 1;
        ret.knockout = this.readUB(1, "knockout") == 1;
        ret.compositeSource = this.readUB(1, "compositeSource") == 1;
        ret.passes = (int) this.readUB(5, "passes");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one BEVELFILTER value from the stream
     *
     * @param name
     * @return BEVELFILTER value
     * @throws IOException
     */
    public BEVELFILTER readBEVELFILTER(String name) throws IOException {
        BEVELFILTER ret = new BEVELFILTER();
        this.newDumpLevel(name, "BEVELFILTER");
        ret.highlightColor = this.readRGBA("highlightColor"); // Highlight color first. It it opposite of the documentation
        ret.shadowColor = this.readRGBA("shadowColor");
        ret.blurX = this.readFIXED("blurX");
        ret.blurY = this.readFIXED("blurY");
        ret.angle = this.readFIXED("angle");
        ret.distance = this.readFIXED("distance");
        ret.strength = this.readFIXED8("strength");
        ret.innerShadow = this.readUB(1, "innerShadow") == 1;
        ret.knockout = this.readUB(1, "knockout") == 1;
        ret.compositeSource = this.readUB(1, "compositeSource") == 1;
        ret.onTop = this.readUB(1, "onTop") == 1;
        ret.passes = (int) this.readUB(4, "passes");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one GRADIENTGLOWFILTER value from the stream
     *
     * @param name
     * @return GRADIENTGLOWFILTER value
     * @throws IOException
     */
    public GRADIENTGLOWFILTER readGRADIENTGLOWFILTER(String name) throws IOException {
        GRADIENTGLOWFILTER ret = new GRADIENTGLOWFILTER();
        this.newDumpLevel(name, "GRADIENTGLOWFILTER");
        int numColors = this.readUI8("numColors");
        ret.gradientColors = new RGBA[numColors];
        ret.gradientRatio = new int[numColors];
        for (int i = 0; i < numColors; i++) {
            ret.gradientColors[i] = this.readRGBA("gradientColor");
        }
        for (int i = 0; i < numColors; i++) {
            ret.gradientRatio[i] = this.readUI8("gradientRatio");
        }
        ret.blurX = this.readFIXED("blurX");
        ret.blurY = this.readFIXED("blurY");
        ret.angle = this.readFIXED("angle");
        ret.distance = this.readFIXED("distance");
        ret.strength = this.readFIXED8("strength");
        ret.innerShadow = this.readUB(1, "innerShadow") == 1;
        ret.knockout = this.readUB(1, "knockout") == 1;
        ret.compositeSource = this.readUB(1, "compositeSource") == 1;
        ret.onTop = this.readUB(1, "onTop") == 1;
        ret.passes = (int) this.readUB(4, "passes");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one GRADIENTBEVELFILTER value from the stream
     *
     * @param name
     * @return GRADIENTBEVELFILTER value
     * @throws IOException
     */
    public GRADIENTBEVELFILTER readGRADIENTBEVELFILTER(String name) throws IOException {
        GRADIENTBEVELFILTER ret = new GRADIENTBEVELFILTER();
        this.newDumpLevel(name, "GRADIENTBEVELFILTER");
        int numColors = this.readUI8("numColors");
        ret.gradientColors = new RGBA[numColors];
        ret.gradientRatio = new int[numColors];
        for (int i = 0; i < numColors; i++) {
            ret.gradientColors[i] = this.readRGBA("gradientColor");
        }
        for (int i = 0; i < numColors; i++) {
            ret.gradientRatio[i] = this.readUI8("gradientRatio");
        }
        ret.blurX = this.readFIXED("blurX");
        ret.blurY = this.readFIXED("blurY");
        ret.angle = this.readFIXED("angle");
        ret.distance = this.readFIXED("distance");
        ret.strength = this.readFIXED8("strength");
        ret.innerShadow = this.readUB(1, "innerShadow") == 1;
        ret.knockout = this.readUB(1, "knockout") == 1;
        ret.compositeSource = this.readUB(1, "compositeSource") == 1;
        ret.onTop = this.readUB(1, "onTop") == 1;
        ret.passes = (int) this.readUB(4, "passes");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads list of FILTER values from the stream
     *
     * @param name
     * @return List of FILTER values
     * @throws IOException
     */
    public List<FILTER> readFILTERLIST(String name) throws IOException {
        this.newDumpLevel(name, "FILTERLIST");
        int numberOfFilters = this.readUI8("numberOfFilters");
        List<FILTER> ret = new ArrayList<>(numberOfFilters);
        for (int i = 0; i < numberOfFilters; i++) {
            ret.add(this.readFILTER("filter"));
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one FILTER value from the stream
     *
     * @param name
     * @return FILTER value
     * @throws IOException
     */
    public FILTER readFILTER(String name) throws IOException {
        this.newDumpLevel(name, "FILTER");
        int filterId = this.readUI8("filterId");
        FILTER ret = null;
        switch (filterId) {
            case 0:
                ret = this.readDROPSHADOWFILTER("filter");
                break;
            case 1:
                ret = this.readBLURFILTER("filter");
                break;
            case 2:
                ret = this.readGLOWFILTER("filter");
                break;
            case 3:
                ret = this.readBEVELFILTER("filter");
                break;
            case 4:
                ret = this.readGRADIENTGLOWFILTER("filter");
                break;
            case 5:
                ret = this.readCONVOLUTIONFILTER("filter");
                break;
            case 6:
                ret = this.readCOLORMATRIXFILTER("filter");
                break;
            case 7:
                ret = this.readGRADIENTBEVELFILTER("filter");
                break;
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads list of BUTTONRECORD values from the stream
     *
     * @param inDefineButton2 Whether read from inside of DefineButton2Tag or
     *                        not
     * @param name
     * @return List of BUTTONRECORD values
     * @throws IOException
     */
    public List<BUTTONRECORD> readBUTTONRECORDList(boolean inDefineButton2, String name) throws IOException {
        List<BUTTONRECORD> ret = new ArrayList<>();
        this.newDumpLevel(name, "BUTTONRECORDList");
        BUTTONRECORD br;
        while ((br = this.readBUTTONRECORD(inDefineButton2, "record")) != null) {
            ret.add(br);
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one BUTTONRECORD value from the stream
     *
     * @param inDefineButton2 True when in DefineButton2
     * @param name
     * @return BUTTONRECORD value
     * @throws IOException
     */
    public BUTTONRECORD readBUTTONRECORD(boolean inDefineButton2, String name) throws IOException {
        BUTTONRECORD ret = new BUTTONRECORD();
        this.newDumpLevel(name, "BUTTONRECORD");
        ret.reserved = (int) this.readUB(2, "reserved");
        ret.buttonHasBlendMode = this.readUB(1, "buttonHasBlendMode") == 1;
        ret.buttonHasFilterList = this.readUB(1, "buttonHasFilterList") == 1;
        ret.buttonStateHitTest = this.readUB(1, "buttonStateHitTest") == 1;
        ret.buttonStateDown = this.readUB(1, "buttonStateDown") == 1;
        ret.buttonStateOver = this.readUB(1, "buttonStateOver") == 1;
        ret.buttonStateUp = this.readUB(1, "buttonStateUp") == 1;

        if (!ret.buttonHasBlendMode && !ret.buttonHasFilterList
                && !ret.buttonStateHitTest && !ret.buttonStateDown
                && !ret.buttonStateOver && !ret.buttonStateUp && ret.reserved == 0) {
            this.endDumpLevel();
            return null;
        }

        ret.characterId = this.readUI16("characterId");
        ret.placeDepth = this.readUI16("placeDepth");
        ret.placeMatrix = this.readMatrix("placeMatrix");
        if (inDefineButton2) {
            ret.colorTransform = this.readCXFORMWITHALPHA("colorTransform");
            if (ret.buttonHasFilterList) {
                ret.filterList = this.readFILTERLIST("filterList");
            }
            if (ret.buttonHasBlendMode) {
                ret.blendMode = this.readUI8("blendMode");
            }
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads list of BUTTONCONDACTION values from the stream
     *
     * @param swf
     * @param tag
     * @param name
     * @return List of BUTTONCONDACTION values
     * @throws IOException
     */
    public List<BUTTONCONDACTION> readBUTTONCONDACTIONList(SWF swf, Tag tag, String name) throws IOException {
        List<BUTTONCONDACTION> ret = new ArrayList<>();
        this.newDumpLevel(name, "BUTTONCONDACTIONList");
        BUTTONCONDACTION bc;
        while (!(bc = this.readBUTTONCONDACTION(swf, tag, "action")).isLast) {
            ret.add(bc);
        }
        ret.add(bc);
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one BUTTONCONDACTION value from the stream
     *
     * @param swf
     * @param tag
     * @param name
     * @return BUTTONCONDACTION value
     * @throws IOException
     */
    public BUTTONCONDACTION readBUTTONCONDACTION(SWF swf, Tag tag, String name) throws IOException {
        this.newDumpLevel(name, "BUTTONCONDACTION");
        BUTTONCONDACTION ret = new BUTTONCONDACTION(swf, this, tag);
        //ret.actions = readActionList();
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one GRADRECORD value from the stream
     *
     * @param shapeNum 1 in DefineShape, 2 in DefineShape2...
     * @param name
     * @return GRADRECORD value
     * @throws IOException
     */
    public GRADRECORD readGRADRECORD(int shapeNum, String name) throws IOException {
        GRADRECORD ret = new GRADRECORD();
        this.newDumpLevel(name, "GRADRECORD");
        ret.ratio = this.readUI8("ratio");
        if (shapeNum >= 3) {
            ret.color = this.readRGBA("color");
        } else {
            ret.color = this.readRGB("color");
        }
        ret.inShape3 = shapeNum >= 3;
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one GRADIENT value from the stream
     *
     * @param shapeNum 1 in DefineShape, 2 in DefineShape2...
     * @param name
     * @return GRADIENT value
     * @throws IOException
     */
    public GRADIENT readGRADIENT(int shapeNum, String name) throws IOException {
        GRADIENT ret = new GRADIENT();
        this.newDumpLevel(name, "GRADIENT");
        ret.spreadMode = (int) this.readUB(2, "spreadMode");
        ret.interpolationMode = (int) this.readUB(2, "interpolationMode");
        int numGradients = (int) this.readUB(4, "numGradients");
        ret.gradientRecords = new GRADRECORD[numGradients];
        for (int i = 0; i < numGradients; i++) {
            ret.gradientRecords[i] = this.readGRADRECORD(shapeNum, "gradientRecord");

        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one FOCALGRADIENT value from the stream
     *
     * @param shapeNum 1 in DefineShape, 2 in DefineShape2...
     * @param name
     * @return FOCALGRADIENT value
     * @throws IOException
     */
    public FOCALGRADIENT readFOCALGRADIENT(int shapeNum, String name) throws IOException {
        FOCALGRADIENT ret = new FOCALGRADIENT();
        this.newDumpLevel(name, "FOCALGRADIENT");
        ret.spreadMode = (int) this.readUB(2, "spreadMode");
        ret.interpolationMode = (int) this.readUB(2, "interpolationMode");
        int numGradients = (int) this.readUB(4, "numGradients");
        ret.gradientRecords = new GRADRECORD[numGradients];
        for (int i = 0; i < numGradients; i++) {
            ret.gradientRecords[i] = this.readGRADRECORD(shapeNum, "gradientRecord");
        }
        ret.focalPoint = this.readFIXED8("focalPoint");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one FILLSTYLE value from the stream
     *
     * @param shapeNum 1 in DefineShape, 2 in DefineShape2...
     * @param name
     * @return FILLSTYLE value
     * @throws IOException
     */
    public FILLSTYLE readFILLSTYLE(int shapeNum, String name) throws IOException {
        FILLSTYLE ret = new FILLSTYLE();
        this.newDumpLevel(name, "FILLSTYLE");
        ret.fillStyleType = this.readUI8("fillStyleType");
        if (ret.fillStyleType == FILLSTYLE.SOLID) {
            if (shapeNum >= 3) {
                ret.color = this.readRGBA("color");
            } else {
                ret.color = this.readRGB("color");
            }
        }
        ret.inShape3 = shapeNum >= 3;
        if ((ret.fillStyleType == FILLSTYLE.LINEAR_GRADIENT)
                || (ret.fillStyleType == FILLSTYLE.RADIAL_GRADIENT)
                || (ret.fillStyleType == FILLSTYLE.FOCAL_RADIAL_GRADIENT)) {
            ret.gradientMatrix = this.readMatrix("gradientMatrix");
        }
        if ((ret.fillStyleType == FILLSTYLE.LINEAR_GRADIENT)
                || (ret.fillStyleType == FILLSTYLE.RADIAL_GRADIENT)) {
            ret.gradient = this.readGRADIENT(shapeNum, "gradient");
        }
        if (ret.fillStyleType == FILLSTYLE.FOCAL_RADIAL_GRADIENT) {
            ret.gradient = this.readFOCALGRADIENT(shapeNum, "gradient");
        }

        if ((ret.fillStyleType == FILLSTYLE.REPEATING_BITMAP)
                || (ret.fillStyleType == FILLSTYLE.CLIPPED_BITMAP)
                || (ret.fillStyleType == FILLSTYLE.NON_SMOOTHED_REPEATING_BITMAP)
                || (ret.fillStyleType == FILLSTYLE.NON_SMOOTHED_CLIPPED_BITMAP)) {
            ret.bitmapId = this.readUI16("bitmapId");
            ret.bitmapMatrix = this.readMatrix("bitmapMatrix");
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one FILLSTYLEARRAY value from the stream
     *
     * @param shapeNum 1 in DefineShape, 2 in DefineShape2...
     * @param name
     * @return FILLSTYLEARRAY value
     * @throws IOException
     */
    public FILLSTYLEARRAY readFILLSTYLEARRAY(int shapeNum, String name) throws IOException {

        FILLSTYLEARRAY ret = new FILLSTYLEARRAY();
        this.newDumpLevel(name, "FILLSTYLEARRAY");
        int fillStyleCount = this.readUI8("fillStyleCount");
        if (shapeNum > 1 && fillStyleCount == 0xff) {
            fillStyleCount = this.readUI16("fillStyleCount");
        }
        ret.fillStyles = new FILLSTYLE[fillStyleCount];
        for (int i = 0; i < fillStyleCount; i++) {
            ret.fillStyles[i] = this.readFILLSTYLE(shapeNum, "fillStyle");
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one LINESTYLE value from the stream
     *
     * @param shapeNum 1 in DefineShape, 2 in DefineShape2...
     * @param name
     * @return LINESTYLE value
     * @throws IOException
     */
    public LINESTYLE readLINESTYLE(int shapeNum, String name) throws IOException {
        LINESTYLE ret = new LINESTYLE();
        this.newDumpLevel(name, "LINESTYLE");
        ret.width = this.readUI16("width");
        if (shapeNum == 1 || shapeNum == 2) {
            ret.color = this.readRGB("color");
        } else if (shapeNum == 3) {
            ret.color = this.readRGBA("color");
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one LINESTYLE2 value from the stream
     *
     * @param shapeNum 1 in DefineShape, 2 in DefineShape2...
     * @param name
     * @return LINESTYLE2 value
     * @throws IOException
     */
    public LINESTYLE2 readLINESTYLE2(int shapeNum, String name) throws IOException {
        LINESTYLE2 ret = new LINESTYLE2();
        this.newDumpLevel(name, "LINESTYLE2");
        ret.width = this.readUI16("width");
        ret.startCapStyle = (int) this.readUB(2, "startCapStyle");
        ret.joinStyle = (int) this.readUB(2, "joinStyle");
        ret.hasFillFlag = (int) this.readUB(1, "hasFillFlag") == 1;
        ret.noHScaleFlag = (int) this.readUB(1, "noHScaleFlag") == 1;
        ret.noVScaleFlag = (int) this.readUB(1, "noVScaleFlag") == 1;
        ret.pixelHintingFlag = (int) this.readUB(1, "pixelHintingFlag") == 1;
        ret.reserved = (int) this.readUB(5, "reserved");
        ret.noClose = (int) this.readUB(1, "noClose") == 1;
        ret.endCapStyle = (int) this.readUB(2, "endCapStyle");
        if (ret.joinStyle == LINESTYLE2.MITER_JOIN) {
            ret.miterLimitFactor = this.readFIXED8("miterLimitFactor");
        }
        if (!ret.hasFillFlag) {
            ret.color = this.readRGBA("color");
        } else {
            ret.fillType = this.readFILLSTYLE(shapeNum, "fillType");
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one LINESTYLEARRAY value from the stream
     *
     * @param shapeNum 1 in DefineShape, 2 in DefineShape2...
     * @param name
     * @return LINESTYLEARRAY value
     * @throws IOException
     */
    public LINESTYLEARRAY readLINESTYLEARRAY(int shapeNum, String name) throws IOException {
        LINESTYLEARRAY ret = new LINESTYLEARRAY();
        this.newDumpLevel(name, "LINESTYLEARRAY");
        int lineStyleCount = this.readUI8("lineStyleCount");
        if (lineStyleCount == 0xff) {
            lineStyleCount = this.readUI16("lineStyleCount");
        }
        if (shapeNum <= 3) {
            ret.lineStyles = new LINESTYLE[lineStyleCount];
            for (int i = 0; i < lineStyleCount; i++) {
                ret.lineStyles[i] = this.readLINESTYLE(shapeNum, "lineStyle");
            }
        } else {
            ret.lineStyles = new LINESTYLE2[lineStyleCount];
            for (int i = 0; i < lineStyleCount; i++) {
                ret.lineStyles[i] = this.readLINESTYLE2(shapeNum, "lineStyle");
            }
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one SHAPERECORD value from the stream
     *
     * @param fillBits
     * @param lineBits
     * @param shapeNum 1 in DefineShape, 2 in DefineShape2...
     * @return SHAPERECORD value
     * @throws IOException
     */
    private SHAPERECORD readSHAPERECORD(int fillBits, int lineBits, int shapeNum, boolean morphShape, String name) throws IOException {
        SHAPERECORD ret;
        this.newDumpLevel(name, "SHAPERECORD");
        int typeFlag = (int) this.readUB(1, "typeFlag");
        if (typeFlag == 0) {
            boolean stateNewStyles = this.readUB(1, "stateNewStyles") == 1;
            boolean stateLineStyle = this.readUB(1, "stateLineStyle") == 1;
            boolean stateFillStyle1 = this.readUB(1, "stateFillStyle1") == 1;
            boolean stateFillStyle0 = this.readUB(1, "stateFillStyle0") == 1;
            boolean stateMoveTo = this.readUB(1, "stateMoveTo") == 1;
            if ((!stateNewStyles) && (!stateLineStyle) && (!stateFillStyle1) && (!stateFillStyle0) && (!stateMoveTo)) {
                ret = new EndShapeRecord();
            } else {
                StyleChangeRecord scr = new StyleChangeRecord();
                scr.stateNewStyles = stateNewStyles;
                scr.stateLineStyle = stateLineStyle;
                scr.stateFillStyle0 = stateFillStyle0;
                scr.stateFillStyle1 = stateFillStyle1;
                scr.stateMoveTo = stateMoveTo;
                if (stateMoveTo) {
                    scr.moveBits = (int) this.readUB(5, "moveBits");
                    scr.moveDeltaX = (int) this.readSB(scr.moveBits, "moveDeltaX");
                    scr.moveDeltaY = (int) this.readSB(scr.moveBits, "moveDeltaY");
                }
                if (stateFillStyle0) {
                    scr.fillStyle0 = (int) this.readUB(fillBits, "fillStyle0");
                }
                if (stateFillStyle1) {
                    scr.fillStyle1 = (int) this.readUB(fillBits, "fillStyle1");
                }
                if (stateLineStyle) {
                    scr.lineStyle = (int) this.readUB(lineBits, "lineStyle");
                }
                if (stateNewStyles) {
                    if (morphShape) {
                        // This should never happen in a valid SWF
                        throw new IOException("MorphShape should not have new styles.");
                    } else {
                        scr.fillStyles = this.readFILLSTYLEARRAY(shapeNum, "fillStyles");
                        scr.lineStyles = this.readLINESTYLEARRAY(shapeNum, "lineStyles");
                    }
                    scr.numFillBits = (int) this.readUB(4, "numFillBits");
                    scr.numLineBits = (int) this.readUB(4, "numLineBits");
                }
                ret = scr;
            }
        } else { // typeFlag==1
            int straightFlag = (int) this.readUB(1, "straightFlag");
            if (straightFlag == 1) {
                StraightEdgeRecord ser = new StraightEdgeRecord();
                ser.numBits = (int) this.readUB(4, "numBits");
                ser.generalLineFlag = this.readUB(1, "generalLineFlag") == 1;
                if (!ser.generalLineFlag) {
                    ser.vertLineFlag = this.readUB(1, "vertLineFlag") == 1;
                }
                if (ser.generalLineFlag || (!ser.vertLineFlag)) {
                    ser.deltaX = (int) this.readSB(ser.numBits + 2, "deltaX");
                }
                if (ser.generalLineFlag || (ser.vertLineFlag)) {
                    ser.deltaY = (int) this.readSB(ser.numBits + 2, "deltaY");
                }
                ret = ser;
            } else {
                CurvedEdgeRecord cer = new CurvedEdgeRecord();
                cer.numBits = (int) this.readUB(4, "numBits");
                cer.controlDeltaX = (int) this.readSB(cer.numBits + 2, "controlDeltaX");
                cer.controlDeltaY = (int) this.readSB(cer.numBits + 2, "controlDeltaY");
                cer.anchorDeltaX = (int) this.readSB(cer.numBits + 2, "anchorDeltaX");
                cer.anchorDeltaY = (int) this.readSB(cer.numBits + 2, "anchorDeltaY");
                ret = cer;
            }
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one SHAPE value from the stream
     *
     * @param shapeNum   1 in DefineShape, 2 in DefineShape2...
     * @param morphShape
     * @param name
     * @return SHAPE value
     * @throws IOException
     */
    public SHAPE readSHAPE(int shapeNum, boolean morphShape, String name) throws IOException {
        SHAPE ret = new SHAPE();
        this.newDumpLevel(name, "SHAPE");
        ret.numFillBits = (int) this.readUB(4, "numFillBits");
        ret.numLineBits = (int) this.readUB(4, "numLineBits");
        ret.shapeRecords = this.readSHAPERECORDS(shapeNum, ret.numFillBits, ret.numLineBits, morphShape, "shapeRecords");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one SHAPEWITHSTYLE value from the stream
     *
     * @param shapeNum   1 in DefineShape, 2 in DefineShape2...
     * @param morphShape
     * @param name
     * @return SHAPEWITHSTYLE value
     * @throws IOException
     */
    public SHAPEWITHSTYLE readSHAPEWITHSTYLE(int shapeNum, boolean morphShape, String name) throws IOException {
        SHAPEWITHSTYLE ret = new SHAPEWITHSTYLE();
        this.newDumpLevel(name, "SHAPEWITHSTYLE");
        ret.fillStyles = this.readFILLSTYLEARRAY(shapeNum, "fillStyles");
        ret.lineStyles = this.readLINESTYLEARRAY(shapeNum, "lineStyles");
        ret.numFillBits = (int) this.readUB(4, "numFillBits");
        ret.numLineBits = (int) this.readUB(4, "numLineBits");
        ret.shapeRecords = this.readSHAPERECORDS(shapeNum, ret.numFillBits, ret.numLineBits, morphShape, "shapeRecords");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads list of SHAPERECORDs from the stream
     *
     * @param shapeNum 1 in DefineShape, 2 in DefineShape2...
     * @param fillBits
     * @param lineBits
     * @return SHAPERECORDs array
     * @throws IOException
     */
    private List<SHAPERECORD> readSHAPERECORDS(int shapeNum, int fillBits, int lineBits, boolean morphShape, String name) throws IOException {
        List<SHAPERECORD> ret = new ArrayList<>();
        this.newDumpLevel(name, "SHAPERECORDS");
        SHAPERECORD rec;
        do {
            rec = this.readSHAPERECORD(fillBits, lineBits, shapeNum, morphShape, "record");
            if (rec instanceof StyleChangeRecord) {
                StyleChangeRecord scRec = (StyleChangeRecord) rec;
                if (scRec.stateNewStyles) {
                    fillBits = scRec.numFillBits;
                    lineBits = scRec.numLineBits;
                }
            }
            ret.add(rec);
        } while (!(rec instanceof EndShapeRecord));
        this.alignByte();
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one SOUNDINFO value from the stream
     *
     * @param name
     * @return SOUNDINFO value
     * @throws IOException
     */
    public SOUNDINFO readSOUNDINFO(String name) throws IOException {
        SOUNDINFO ret = new SOUNDINFO();
        this.newDumpLevel(name, "SOUNDINFO");
        ret.reserved = (int) this.readUB(2, "reserved");
        ret.syncStop = this.readUB(1, "syncStop") == 1;
        ret.syncNoMultiple = this.readUB(1, "syncNoMultiple") == 1;
        ret.hasEnvelope = this.readUB(1, "hasEnvelope") == 1;
        ret.hasLoops = this.readUB(1, "hasLoops") == 1;
        ret.hasOutPoint = this.readUB(1, "hasOutPoint") == 1;
        ret.hasInPoint = this.readUB(1, "hasInPoint") == 1;
        if (ret.hasInPoint) {
            ret.inPoint = this.readUI32("inPoint");
        }
        if (ret.hasOutPoint) {
            ret.outPoint = this.readUI32("outPoint");
        }
        if (ret.hasLoops) {
            ret.loopCount = this.readUI16("loopCount");
        }
        if (ret.hasEnvelope) {
            int envPoints = this.readUI8("envPoints");
            ret.envelopeRecords = new SOUNDENVELOPE[envPoints];
            for (int i = 0; i < envPoints; i++) {
                ret.envelopeRecords[i] = this.readSOUNDENVELOPE("envelopeRecord");
            }
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one SOUNDENVELOPE value from the stream
     *
     * @param name
     * @return SOUNDENVELOPE value
     * @throws IOException
     */
    public SOUNDENVELOPE readSOUNDENVELOPE(String name) throws IOException {
        SOUNDENVELOPE ret = new SOUNDENVELOPE();
        this.newDumpLevel(name, "SOUNDENVELOPE");
        ret.pos44 = this.readUI32("pos44");
        ret.leftLevel = this.readUI16("leftLevel");
        ret.rightLevel = this.readUI16("rightLevel");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one GLYPHENTRY value from the stream
     *
     * @param glyphBits
     * @param advanceBits
     * @param name
     * @return GLYPHENTRY value
     * @throws IOException
     */
    public GLYPHENTRY readGLYPHENTRY(int glyphBits, int advanceBits, String name) throws IOException {
        GLYPHENTRY ret = new GLYPHENTRY();
        this.newDumpLevel(name, "GLYPHENTRY");
        ret.glyphIndex = (int) this.readUB(glyphBits, "glyphIndex");
        ret.glyphAdvance = (int) this.readSB(advanceBits, "glyphAdvance");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one TEXTRECORD value from the stream
     *
     * @param defineTextNum
     * @param glyphBits
     * @param advanceBits
     * @param name
     * @return TEXTRECORD value
     * @throws IOException
     */
    public TEXTRECORD readTEXTRECORD(int defineTextNum, int glyphBits, int advanceBits, String name) throws IOException {
        TEXTRECORD ret = new TEXTRECORD();
        this.newDumpLevel(name, "TEXTRECORD");
        int first = (int) this.readUB(1, "first"); // always 1
        this.readUB(3, "styleFlagsHasReserved"); // always 0
        ret.styleFlagsHasFont = this.readUB(1, "styleFlagsHasFont") == 1;
        ret.styleFlagsHasColor = this.readUB(1, "styleFlagsHasColor") == 1;
        ret.styleFlagsHasYOffset = this.readUB(1, "styleFlagsHasYOffset") == 1;
        ret.styleFlagsHasXOffset = this.readUB(1, "styleFlagsHasXOffset") == 1;
        if ((!ret.styleFlagsHasFont) && (!ret.styleFlagsHasColor) && (!ret.styleFlagsHasYOffset) && (!ret.styleFlagsHasXOffset) && (first == 0)) { // final text record
            this.endDumpLevel();
            return null;
        }
        if (ret.styleFlagsHasFont) {
            ret.fontId = this.readUI16("fontId");
        }
        if (ret.styleFlagsHasColor) {
            if (defineTextNum == 2) {
                ret.textColorA = this.readRGBA("textColorA");
            } else {
                ret.textColor = this.readRGB("textColor");
            }
        }
        if (ret.styleFlagsHasXOffset) {
            ret.xOffset = this.readSI16("xOffset");
        }
        if (ret.styleFlagsHasYOffset) {
            ret.yOffset = this.readSI16("yOffset");
        }
        if (ret.styleFlagsHasFont) {
            ret.textHeight = this.readUI16("textHeight");
        }
        int glyphCount = this.readUI8("glyphCount");
        ret.glyphEntries = new ArrayList<>(glyphCount);
        for (int i = 0; i < glyphCount; i++) {
            ret.glyphEntries.add(this.readGLYPHENTRY(glyphBits, advanceBits, "glyphEntry"));
        }
        this.alignByte();
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one MORPHGRADRECORD value from the stream
     *
     * @param name
     * @return MORPHGRADRECORD value
     * @throws IOException
     */
    public MORPHGRADRECORD readMORPHGRADRECORD(String name) throws IOException {
        MORPHGRADRECORD ret = new MORPHGRADRECORD();
        this.newDumpLevel(name, "MORPHGRADRECORD");
        ret.startRatio = this.readUI8("startRatio");
        ret.startColor = this.readRGBA("startColor");
        ret.endRatio = this.readUI8("endRatio");
        ret.endColor = this.readRGBA("endColor");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one MORPHGRADIENT value from the stream
     *
     * @param name
     * @return MORPHGRADIENT value
     * @throws IOException
     */
    public MORPHGRADIENT readMORPHGRADIENT(String name) throws IOException {
        MORPHGRADIENT ret = new MORPHGRADIENT();
        this.newDumpLevel(name, "MORPHGRADIENT");
        // Despite of documentation (UI8 1-8), there are two fields
        // spreadMode and interPolationMode which are same as in GRADIENT
        ret.spreadMode = (int) this.readUB(2, "spreadMode");
        ret.interPolationMode = (int) this.readUB(2, "interPolationMode");
        int numGradients = (int) this.readUB(4, "numGradients");
        ret.gradientRecords = new MORPHGRADRECORD[numGradients];
        for (int i = 0; i < numGradients; i++) {
            ret.gradientRecords[i] = this.readMORPHGRADRECORD("gradientRecord");
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one MORPHFOCALGRADIENT value from the stream
     * <p>
     * This is undocumented feature
     *
     * @param name
     * @return MORPHGRADIENT value
     * @throws IOException
     */
    public MORPHFOCALGRADIENT readMORPHFOCALGRADIENT(String name) throws IOException {
        MORPHFOCALGRADIENT ret = new MORPHFOCALGRADIENT();
        this.newDumpLevel(name, "MORPHFOCALGRADIENT");
        ret.spreadMode = (int) this.readUB(2, "spreadMode");
        ret.interPolationMode = (int) this.readUB(2, "interPolationMode");
        int numGradients = (int) this.readUB(4, "numGradients");
        ret.gradientRecords = new MORPHGRADRECORD[numGradients];
        for (int i = 0; i < numGradients; i++) {
            ret.gradientRecords[i] = this.readMORPHGRADRECORD("gradientRecord");
        }
        ret.startFocalPoint = this.readFIXED8("startFocalPoint");
        ret.endFocalPoint = this.readFIXED8("endFocalPoint");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one MORPHFILLSTYLE value from the stream
     *
     * @param name
     * @return MORPHFILLSTYLE value
     * @throws IOException
     */
    public MORPHFILLSTYLE readMORPHFILLSTYLE(String name) throws IOException {
        MORPHFILLSTYLE ret = new MORPHFILLSTYLE();
        this.newDumpLevel(name, "MORPHFILLSTYLE");
        ret.fillStyleType = this.readUI8("fillStyleType");
        if (ret.fillStyleType == MORPHFILLSTYLE.SOLID) {
            ret.startColor = this.readRGBA("startColor");
            ret.endColor = this.readRGBA("endColor");
        }
        if ((ret.fillStyleType == MORPHFILLSTYLE.LINEAR_GRADIENT)
                || (ret.fillStyleType == MORPHFILLSTYLE.RADIAL_GRADIENT)
                || (ret.fillStyleType == MORPHFILLSTYLE.FOCAL_RADIAL_GRADIENT)) {
            ret.startGradientMatrix = this.readMatrix("startGradientMatrix");
            ret.endGradientMatrix = this.readMatrix("endGradientMatrix");
        }
        if ((ret.fillStyleType == MORPHFILLSTYLE.LINEAR_GRADIENT)
                || (ret.fillStyleType == MORPHFILLSTYLE.RADIAL_GRADIENT)) {
            ret.gradient = this.readMORPHGRADIENT("gradient");
        }
        if (ret.fillStyleType == MORPHFILLSTYLE.FOCAL_RADIAL_GRADIENT) {
            ret.gradient = this.readMORPHFOCALGRADIENT("gradient");
        }

        if ((ret.fillStyleType == MORPHFILLSTYLE.REPEATING_BITMAP)
                || (ret.fillStyleType == MORPHFILLSTYLE.CLIPPED_BITMAP)
                || (ret.fillStyleType == MORPHFILLSTYLE.NON_SMOOTHED_REPEATING_BITMAP)
                || (ret.fillStyleType == MORPHFILLSTYLE.NON_SMOOTHED_CLIPPED_BITMAP)) {
            ret.bitmapId = this.readUI16("bitmapId");
            ret.startBitmapMatrix = this.readMatrix("startBitmapMatrix");
            ret.endBitmapMatrix = this.readMatrix("endBitmapMatrix");
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one MORPHFILLSTYLEARRAY value from the stream
     *
     * @param name
     * @return MORPHFILLSTYLEARRAY value
     * @throws IOException
     */
    public MORPHFILLSTYLEARRAY readMORPHFILLSTYLEARRAY(String name) throws IOException {

        MORPHFILLSTYLEARRAY ret = new MORPHFILLSTYLEARRAY();
        this.newDumpLevel(name, "MORPHFILLSTYLEARRAY");
        int fillStyleCount = this.readUI8("fillStyleCount");
        if (fillStyleCount == 0xff) {
            fillStyleCount = this.readUI16("fillStyleCount");
        }
        ret.fillStyles = new MORPHFILLSTYLE[fillStyleCount];
        for (int i = 0; i < fillStyleCount; i++) {
            ret.fillStyles[i] = this.readMORPHFILLSTYLE("fillStyle");
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one MORPHLINESTYLE value from the stream
     *
     * @param name
     * @return MORPHLINESTYLE value
     * @throws IOException
     */
    public MORPHLINESTYLE readMORPHLINESTYLE(String name) throws IOException {
        MORPHLINESTYLE ret = new MORPHLINESTYLE();
        this.newDumpLevel(name, "MORPHLINESTYLE");
        ret.startWidth = this.readUI16("startWidth");
        ret.endWidth = this.readUI16("endWidth");
        ret.startColor = this.readRGBA("startColor");
        ret.endColor = this.readRGBA("endColor");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one MORPHLINESTYLE2 value from the stream
     *
     * @param name
     * @return MORPHLINESTYLE2 value
     * @throws IOException
     */
    public MORPHLINESTYLE2 readMORPHLINESTYLE2(String name) throws IOException {
        MORPHLINESTYLE2 ret = new MORPHLINESTYLE2();
        this.newDumpLevel(name, "MORPHLINESTYLE2");
        ret.startWidth = this.readUI16("startWidth");
        ret.endWidth = this.readUI16("endWidth");
        ret.startCapStyle = (int) this.readUB(2, "startCapStyle");
        ret.joinStyle = (int) this.readUB(2, "joinStyle");
        ret.hasFillFlag = (int) this.readUB(1, "hasFillFlag") == 1;
        ret.noHScaleFlag = (int) this.readUB(1, "noHScaleFlag") == 1;
        ret.noVScaleFlag = (int) this.readUB(1, "noVScaleFlag") == 1;
        ret.pixelHintingFlag = (int) this.readUB(1, "pixelHintingFlag") == 1;
        ret.reserved = (int) this.readUB(5, "reserved");
        ret.noClose = (int) this.readUB(1, "noClose") == 1;
        ret.endCapStyle = (int) this.readUB(2, "endCapStyle");
        if (ret.joinStyle == LINESTYLE2.MITER_JOIN) {
            ret.miterLimitFactor = this.readUI16("miterLimitFactor");
        }
        if (!ret.hasFillFlag) {
            ret.startColor = this.readRGBA("startColor");
            ret.endColor = this.readRGBA("endColor");
        } else {
            ret.fillType = this.readMORPHFILLSTYLE("fillType");
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one MORPHLINESTYLEARRAY value from the stream
     *
     * @param morphShapeNum 1 on DefineMorphShape, 2 on DefineMorphShape2
     * @param name
     * @return MORPHLINESTYLEARRAY value
     * @throws IOException
     */
    public MORPHLINESTYLEARRAY readMORPHLINESTYLEARRAY(int morphShapeNum, String name) throws IOException {
        MORPHLINESTYLEARRAY ret = new MORPHLINESTYLEARRAY();
        this.newDumpLevel(name, "MORPHLINESTYLEARRAY");
        int lineStyleCount = this.readUI8("lineStyleCount");
        if (lineStyleCount == 0xff) {
            lineStyleCount = this.readUI16("lineStyleCount");
        }
        if (morphShapeNum == 1) {
            ret.lineStyles = new MORPHLINESTYLE[lineStyleCount];
            for (int i = 0; i < lineStyleCount; i++) {
                ret.lineStyles[i] = this.readMORPHLINESTYLE("lineStyle");
            }
        } else if (morphShapeNum == 2) {
            ret.lineStyles2 = new MORPHLINESTYLE2[lineStyleCount];
            for (int i = 0; i < lineStyleCount; i++) {
                ret.lineStyles2[i] = this.readMORPHLINESTYLE2("lineStyle2");
            }
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one KERNINGRECORD value from the stream
     *
     * @param fontFlagsWideCodes
     * @param name
     * @return KERNINGRECORD value
     * @throws IOException
     */
    public KERNINGRECORD readKERNINGRECORD(boolean fontFlagsWideCodes, String name) throws IOException {
        KERNINGRECORD ret = new KERNINGRECORD();
        this.newDumpLevel(name, "KERNINGRECORD");
        if (fontFlagsWideCodes) {
            ret.fontKerningCode1 = this.readUI16("fontKerningCode1");
            ret.fontKerningCode2 = this.readUI16("fontKerningCode2");
        } else {
            ret.fontKerningCode1 = this.readUI8("fontKerningCode1");
            ret.fontKerningCode2 = this.readUI8("fontKerningCode2");
        }
        ret.fontKerningAdjustment = this.readSI16("fontKerningAdjustment");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one LANGCODE value from the stream
     *
     * @param name
     * @return LANGCODE value
     * @throws IOException
     */
    public LANGCODE readLANGCODE(String name) throws IOException {
        LANGCODE ret = new LANGCODE();
        this.newDumpLevel(name, "LANGCODE");
        ret.languageCode = this.readUI8("languageCode");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one ZONERECORD value from the stream
     *
     * @param name
     * @return ZONERECORD value
     * @throws IOException
     */
    public ZONERECORD readZONERECORD(String name) throws IOException {
        ZONERECORD ret = new ZONERECORD();
        this.newDumpLevel(name, "ZONERECORD");
        int numZoneData = this.readUI8("numZoneData");
        ret.zonedata = new ZONEDATA[numZoneData];
        for (int i = 0; i < numZoneData; i++) {
            ret.zonedata[i] = this.readZONEDATA("zonedata");
        }
        this.readUB(6, "reserved");
        ret.zoneMaskY = this.readUB(1, "zoneMaskY") == 1;
        ret.zoneMaskX = this.readUB(1, "zoneMaskX") == 1;
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one ZONEDATA value from the stream
     *
     * @param name
     * @return ZONEDATA value
     * @throws IOException
     */
    public ZONEDATA readZONEDATA(String name) throws IOException {
        ZONEDATA ret = new ZONEDATA();
        this.newDumpLevel(name, "ZONEDATA");
        ret.alignmentCoordinate = this.readUI16("alignmentCoordinate");
        ret.range = this.readUI16("range");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one PIX15 value from the stream
     *
     * @param name
     * @return PIX15 value
     * @throws IOException
     */
    public PIX15 readPIX15(String name) throws IOException {
        PIX15 ret = new PIX15();
        this.newDumpLevel(name, "PIX15");
        ret.reserved = (int) this.readUB(1, "reserved");
        ret.red = (int) this.readUB(5, "red");
        ret.green = (int) this.readUB(5, "green");
        ret.blue = (int) this.readUB(5, "blue");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one PIX15 value from the stream
     *
     * @param name
     * @return PIX15 value
     * @throws IOException
     */
    public int readPIX15Int(String name) throws IOException {
        this.newDumpLevel(name, "PIX15");
        int ret = ((int) this.readUB(1, "reserved") << 24)
                | ((int) this.readUB(5, "red") << 19)
                | ((int) this.readUB(5, "green") << 11)
                | ((int) this.readUB(5, "blue") << 3);
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one PIX24 value from the stream
     *
     * @param name
     * @return PIX24 value
     * @throws IOException
     */
    public PIX24 readPIX24(String name) throws IOException {
        PIX24 ret = new PIX24();
        this.newDumpLevel(name, "PIX24");
        ret.reserved = this.readUI8("reserved");
        ret.red = this.readUI8("red");
        ret.green = this.readUI8("green");
        ret.blue = this.readUI8("blue");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one PIX24 value from the stream
     *
     * @param name
     * @return PIX24 value
     * @throws IOException
     */
    public int readPIX24Int(String name) throws IOException {
        this.newDumpLevel(name, "PIX24");
        int ret = (this.readUI8("reserved") << 24)
                | (this.readUI8("red") << 16)
                | (this.readUI8("green") << 8)
                | this.readUI8("blue");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one COLORMAPDATA value from the stream
     *
     * @param colorTableSize
     * @param bitmapWidth
     * @param bitmapHeight
     * @param name
     * @return COLORMAPDATA value
     * @throws IOException
     */
    public COLORMAPDATA readCOLORMAPDATA(int colorTableSize, int bitmapWidth, int bitmapHeight, String name) throws IOException {
        COLORMAPDATA ret = new COLORMAPDATA();
        this.newDumpLevel(name, "COLORMAPDATA");
        ret.colorTableRGB = new int[colorTableSize + 1];
        for (int i = 0; i < colorTableSize + 1; i++) {
            ret.colorTableRGB[i] = this.readRGBInt("colorTableRGB");
        }

        int dataLen = 0;
        for (int y = 0; y < bitmapHeight; y++) {
            int x = 0;
            for (; x < bitmapWidth; x++) {
                dataLen++;
            }
            while ((x % 4) != 0) {
                dataLen++;
                x++;
            }
        }

        ret.colorMapPixelData = this.readBytesEx(dataLen, "colorMapPixelData");
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one BITMAPDATA value from the stream
     *
     * @param bitmapFormat
     * @param bitmapWidth
     * @param bitmapHeight
     * @param name
     * @return COLORMAPDATA value
     * @throws IOException
     */
    public BITMAPDATA readBITMAPDATA(int bitmapFormat, int bitmapWidth, int bitmapHeight, String name) throws IOException {
        BITMAPDATA ret = new BITMAPDATA();
        this.newDumpLevel(name, "BITMAPDATA");
        int pixelCount = bitmapWidth * bitmapHeight;
        int[] pix15 = bitmapFormat == DefineBitsLosslessTag.FORMAT_15BIT_RGB ? new int[pixelCount] : null;
        int[] pix24 = bitmapFormat == DefineBitsLosslessTag.FORMAT_24BIT_RGB ? new int[pixelCount] : null;
        int dataLen = 0;
        int pos = 0;
        for (int y = 0; y < bitmapHeight; y++) {
            for (int x = 0; x < bitmapWidth; x++) {
                if (bitmapFormat == DefineBitsLosslessTag.FORMAT_15BIT_RGB) {
                    dataLen += 2;
                    pix15[pos++] = this.readPIX15Int("pix15");
                } else if (bitmapFormat == DefineBitsLosslessTag.FORMAT_24BIT_RGB) {
                    dataLen += 4;
                    pix24[pos++] = this.readPIX24Int("pix24");
                }
            }
            while ((dataLen % 4) != 0) {
                dataLen++;
                this.readUI8("padding");
            }
        }

        if (bitmapFormat == DefineBitsLosslessTag.FORMAT_15BIT_RGB) {
            ret.bitmapPixelDataPix15 = pix15;
        } else if (bitmapFormat == DefineBitsLosslessTag.FORMAT_24BIT_RGB) {
            ret.bitmapPixelDataPix24 = pix24;
        }

        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one BITMAPDATA value from the stream
     *
     * @param bitmapFormat
     * @param bitmapWidth
     * @param bitmapHeight
     * @param name
     * @return COLORMAPDATA value
     * @throws IOException
     */
    public ALPHABITMAPDATA readALPHABITMAPDATA(int bitmapFormat, int bitmapWidth, int bitmapHeight, String name) throws IOException {
        ALPHABITMAPDATA ret = new ALPHABITMAPDATA();
        this.newDumpLevel(name, "ALPHABITMAPDATA");
        ret.bitmapPixelData = new int[bitmapWidth * bitmapHeight];
        for (int y = 0; y < bitmapHeight; y++) {
            for (int x = 0; x < bitmapWidth; x++) {
                ret.bitmapPixelData[y * bitmapWidth + x] = this.readARGBInt("bitmapPixelData");
            }
        }
        this.endDumpLevel();
        return ret;
    }

    /**
     * Reads one ALPHACOLORMAPDATA value from the stream
     *
     * @param colorTableSize
     * @param bitmapWidth
     * @param bitmapHeight
     * @param name
     * @return ALPHACOLORMAPDATA value
     * @throws IOException
     */
    public ALPHACOLORMAPDATA readALPHACOLORMAPDATA(int colorTableSize, int bitmapWidth, int bitmapHeight, String name) throws IOException {
        ALPHACOLORMAPDATA ret = new ALPHACOLORMAPDATA();
        this.newDumpLevel(name, "ALPHACOLORMAPDATA");
        ret.colorTableRGB = new int[colorTableSize + 1];
        for (int i = 0; i < colorTableSize + 1; i++) {
            ret.colorTableRGB[i] = this.readRGBAInt("colorTableRGB");
        }

        int dataLen = 0;
        for (int y = 0; y < bitmapHeight; y++) {
            int x = 0;
            for (; x < bitmapWidth; x++) {
                dataLen++;
            }
            while ((x % 4) != 0) {
                dataLen++;
                x++;
            }
        }

        ret.colorMapPixelData = this.readBytesEx(dataLen, "colorMapPixelData");
        this.endDumpLevel();
        return ret;
    }

    public int available() throws IOException {
        return this.is.available();
    }

    public long availableBits() throws IOException {
        if (this.bitPos > 0) {
            return this.available() * 8 + (8 - this.bitPos);
        }
        return this.available() * 8;
    }

    public MemoryInputStream getBaseStream() throws IOException {
        int pos = (int) this.is.getPos();
        MemoryInputStream mis = new MemoryInputStream(this.is.getAllRead(), 0, pos + this.is.available());
        mis.seek(pos);
        return mis;
    }

    public SWFInputStream getLimitedStream(int limit) throws IOException {
        SWFInputStream sis = new SWFInputStream(this.swf, this.is.getAllRead(), this.startingPos, (int) (this.is.getPos() + limit));

        // uncomment the following line to turn off lazy dump info collecting
        //sis.dumpInfo = dumpInfo;
        sis.seek(this.is.getPos() + this.startingPos);
        return sis;
    }
}
