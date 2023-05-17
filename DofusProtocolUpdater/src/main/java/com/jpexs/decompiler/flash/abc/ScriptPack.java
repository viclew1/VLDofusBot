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
package com.jpexs.decompiler.flash.abc;

import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.abc.types.ConvertData;
import com.jpexs.decompiler.flash.abc.types.Multiname;
import com.jpexs.decompiler.flash.abc.types.Namespace;
import com.jpexs.decompiler.flash.abc.types.ScriptInfo;
import com.jpexs.decompiler.flash.abc.types.traits.Trait;
import com.jpexs.decompiler.flash.abc.types.traits.TraitClass;
import com.jpexs.decompiler.flash.abc.types.traits.Traits;
import com.jpexs.decompiler.flash.configuration.Configuration;
import com.jpexs.decompiler.flash.exporters.modes.ScriptExportMode;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.flash.helpers.NulWriter;
import com.jpexs.decompiler.flash.helpers.StringBuilderTextWriter;
import com.jpexs.decompiler.flash.search.MethodId;
import com.jpexs.decompiler.flash.treeitems.AS3ClassTreeItem;
import com.jpexs.decompiler.graph.ScopeStack;
import com.jpexs.helpers.CancellableWorker;
import com.jpexs.helpers.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author JPEXS
 */
public class ScriptPack extends AS3ClassTreeItem {

    private static final Logger logger = Logger.getLogger(ScriptPack.class.getName());

    public final ABC abc;

    public List<ABC> allABCs;

    public final int scriptIndex;

    public final List<Integer> traitIndices;

    private final ClassPath path;

    public boolean isSimple = false;

    public boolean scriptInitializerIsEmpty = false;

    @Override
    public SWF getSwf() {
        return this.abc.getSwf();
    }

    public ClassPath getClassPath() {
        return this.path;
    }

    public ScriptPack(ClassPath path, ABC abc, List<ABC> allAbcs, int scriptIndex, List<Integer> traitIndices) {
        super(path.className, path.namespaceSuffix, path);
        this.abc = abc;
        this.scriptIndex = scriptIndex;
        this.traitIndices = traitIndices;
        this.path = path;
        this.allABCs = allAbcs;
    }

    public void convert(final NulWriter writer, final List<Trait> traits, final ConvertData convertData, final ScriptExportMode exportMode, final boolean parallel) throws InterruptedException {

        int sinit_index = this.abc.script_info.get(this.scriptIndex).init_index;
        int sinit_bodyIndex = this.abc.findBodyIndex(sinit_index);
        if (sinit_bodyIndex != -1) {
            List<Traits> ts = new ArrayList<>();
            //initialize all classes traits
            for (Trait t : traits) {
                if (t instanceof TraitClass) {
                    ts.add(this.abc.class_info.get(((TraitClass) t).class_info).static_traits);
                }
            }
            ts.add(this.abc.script_info.get(this.scriptIndex).traits);
            writer.mark();
            this.abc.bodies.get(sinit_bodyIndex).convert(convertData, this.path +/*packageName +*/ "/.scriptinitializer", exportMode, true, sinit_index, this.scriptIndex, -1, this.abc, null, new ScopeStack(), GraphTextWriter.TRAIT_SCRIPT_INITIALIZER, writer, new ArrayList<>(), ts, true, new HashSet<>());
            this.scriptInitializerIsEmpty = !writer.getMark();

        }
        for (int t : this.traitIndices) {
            Trait trait = traits.get(t);
            Multiname name = trait.getName(this.abc);
            Namespace ns = name.getNamespace(this.abc.constants);
            if ((ns.kind == Namespace.KIND_PACKAGE) || (ns.kind == Namespace.KIND_PACKAGE_INTERNAL)) {
                trait.convertPackaged(null, convertData, "", this.abc, false, exportMode, this.scriptIndex, -1, writer, new ArrayList<>(), parallel);
            } else {
                trait.convert(null, convertData, "", this.abc, false, exportMode, this.scriptIndex, -1, writer, new ArrayList<>(), parallel);
            }
        }
    }

    private void appendTo(GraphTextWriter writer, List<Trait> traits, ConvertData convertData, ScriptExportMode exportMode, boolean parallel) throws InterruptedException {
        boolean first = true;
        //script initializer
        int script_init = this.abc.script_info.get(this.scriptIndex).init_index;
        int bodyIndex = this.abc.findBodyIndex(script_init);
        if (bodyIndex != -1 && Configuration.enableScriptInitializerDisplay.get()) {
            //Note: There must be trait/method highlight even if the initializer is empty to TraitList in GUI to work correctly
            //TODO: handle this better in GUI(?)
            writer.startTrait(GraphTextWriter.TRAIT_SCRIPT_INITIALIZER);
            writer.startMethod(script_init);
            if (exportMode != ScriptExportMode.AS_METHOD_STUBS) {
                if (!this.scriptInitializerIsEmpty) {
                    writer.startBlock();
                    this.abc.bodies.get(bodyIndex).toString(this.path +/*packageName +*/ "/.scriptinitializer", exportMode, this.abc, null, writer, new ArrayList<>(), new HashSet<>());
                    writer.endBlock();
                } else {
                    writer.append(" ");
                }
            }
            writer.endMethod();
            writer.endTrait();
            if (!this.scriptInitializerIsEmpty) {
                writer.newLine();
            }
            first = false;
        } else {
            //"/*classInitializer*/";
        }

        for (int t : this.traitIndices) {
            if (!first) {
                writer.newLine();
            }

            Trait trait = traits.get(t);

            if (!(trait instanceof TraitClass)) {
                writer.startTrait(t);
            }
            Multiname name = trait.getName(this.abc);
            Namespace ns = name.getNamespace(this.abc.constants);
            if ((ns.kind == Namespace.KIND_PACKAGE) || (ns.kind == Namespace.KIND_PACKAGE_INTERNAL)) {
                trait.toStringPackaged(null, convertData, "", this.abc, false, exportMode, this.scriptIndex, -1, writer, new ArrayList<>(), parallel);
            } else {
                trait.toString(null, convertData, "", this.abc, false, exportMode, this.scriptIndex, -1, writer, new ArrayList<>(), parallel);
            }
            if (!(trait instanceof TraitClass)) {
                writer.endTrait();
            }
            first = false;
        }
    }

    public void toSource(GraphTextWriter writer, final List<Trait> traits, final ConvertData convertData, final ScriptExportMode exportMode, final boolean parallel) throws InterruptedException {
        writer.suspendMeasure();
        int timeout = Configuration.decompilationTimeoutFile.get();
        try {
            CancellableWorker.call((Callable<Void>) () -> {
                ScriptPack.this.convert(new NulWriter(), traits, convertData, exportMode, parallel);
                return null;
            }, timeout, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            writer.continueMeasure();
            logger.log(Level.SEVERE, "Decompilation timeout", ex);
            Helper.appendTimeoutCommentAs3(writer, timeout, 0);
            return;
        } catch (CancellationException ex) {
            throw new InterruptedException();
        } catch (ExecutionException ex) {
            writer.continueMeasure();
            Exception convertException = ex;
            Throwable cause = ex.getCause();
            if (cause instanceof Exception) {
                convertException = (Exception) cause;
            }

            if (convertException instanceof CancellationException) {
                throw new InterruptedException();
            }
            if (convertException instanceof InterruptedException) {
                throw (InterruptedException) convertException;
            }
            logger.log(Level.SEVERE, "Decompilation error", convertException);
            Helper.appendErrorComment(writer, convertException);
            return;
        }
        writer.continueMeasure();

        this.appendTo(writer, traits, convertData, exportMode, parallel);
    }

    public String export(ScriptExportSettings exportSettings, boolean parallel) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        StringBuilderTextWriter writer = new StringBuilderTextWriter(Configuration.getCodeFormatting(), sb);
        ConvertData convertData = new ConvertData();
        this.toSource(writer, this.abc.script_info.get(this.scriptIndex).traits.traits, convertData, exportSettings.mode, parallel);

        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + System.identityHashCode(this.abc);
        hash = 79 * hash + this.scriptIndex;
        hash = 79 * hash + Objects.hashCode(this.path);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ScriptPack other = (ScriptPack) obj;
        if (this.abc != other.abc) {
            return false;
        }
        if (this.scriptIndex != other.scriptIndex) {
            return false;
        }
        return Objects.equals(this.path, other.path);
    }

    @Override
    public boolean isModified() {
        if (this.scriptIndex >= this.abc.script_info.size()) {
            return false;
        }
        return this.abc.script_info.get(this.scriptIndex).isModified();
    }

    public void getMethodInfos(List<MethodId> methodInfos) {
        int script_init = this.abc.script_info.get(this.scriptIndex).init_index;
        methodInfos.add(new MethodId(GraphTextWriter.TRAIT_SCRIPT_INITIALIZER, -1, script_init));

        List<Trait> traits = this.abc.script_info.get(this.scriptIndex).traits.traits;
        for (int t : this.traitIndices) {
            Trait trait = traits.get(t);
            trait.getMethodInfos(this.abc, GraphTextWriter.TRAIT_UNKNOWN, -1, methodInfos);
        }
    }

    public void delete(ABC abc, boolean d) {
        ScriptInfo si = abc.script_info.get(this.scriptIndex);
        if (this.isSimple) {
            si.delete(abc, d);
        } else {
            for (int t : this.traitIndices) {
                si.traits.traits.get(t).delete(abc, d);
            }
        }
    }
}
