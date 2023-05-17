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

import com.jpexs.helpers.Path;

import java.io.*;

/**
 * @author JPEXS
 */
public class SWFSourceInfo {

    private final InputStream inputStream;

    private String file;

    private final String fileTitle;

    private final boolean detectBundle;

    public SWFSourceInfo(InputStream inputStream, String file, String fileTitle) {
        this(inputStream, file, fileTitle, true);
    }

    public SWFSourceInfo(InputStream inputStream, String file, String fileTitle, boolean detectBundle) {
        this.inputStream = inputStream;
        this.file = file;
        this.fileTitle = fileTitle;
        this.detectBundle = detectBundle;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFileTitle() {
        return this.fileTitle;
    }

    /**
     * Get title of the file
     *
     * @return file title
     */
    public String getFileTitleOrName() {
        if (this.fileTitle != null) {
            return this.fileTitle;
        }
        return this.file;
    }

    public boolean isBundle() {
        if (this.inputStream == null) {
            File fileObj = new File(this.file);
            String fileName = fileObj.getName();
            if (fileName.startsWith("asdec_") && fileName.endsWith(".tmp")) {
                return false;
            }
            String extension = Path.getExtension(fileObj);
            return (this.detectBundle) && (extension == null || !(extension.equals(".swf") || extension.equals(".gfx")));
        }
        return false;
    }

    public SWFBundle getBundle(boolean noCheck, SearchMode searchMode) throws IOException {
        if (!this.isBundle()) {
            return null;
        }

        String extension = Path.getExtension(new File(this.file));
        if (extension != null) {
            switch (extension) {
                case ".swc":
                    return new SWC(new File(this.file));
                case ".zip":
                    return new ZippedSWFBundle(new File(this.file));
            }
        }

        return new BinarySWFBundle(new BufferedInputStream(new FileInputStream(this.file)), noCheck, searchMode);
    }
}
