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
package com.jpexs.decompiler.flash.abc.types.traits;

import com.jpexs.decompiler.flash.abc.ABC;
import com.jpexs.decompiler.flash.abc.avm2.parser.script.AbcIndexing;
import com.jpexs.decompiler.flash.abc.types.*;
import com.jpexs.decompiler.flash.exporters.modes.ScriptExportMode;
import com.jpexs.decompiler.flash.exporters.script.Dependency;
import com.jpexs.decompiler.flash.exporters.script.DependencyParser;
import com.jpexs.decompiler.flash.exporters.script.DependencyType;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.flash.helpers.NulWriter;
import com.jpexs.decompiler.flash.helpers.hilight.HighlightSpecialType;
import com.jpexs.decompiler.flash.search.MethodId;
import com.jpexs.decompiler.graph.DottedChain;
import com.jpexs.decompiler.graph.ScopeStack;
import com.jpexs.decompiler.graph.TypeItem;
import com.jpexs.helpers.Helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author JPEXS
 */
public class TraitClass extends Trait implements TraitWithSlot {

    private static final long serialVersionUID = -6173905137187390895L;
    public int slot_id;

    public int class_info;

    private boolean classInitializerIsEmpty;

    private List<Integer> frameTraitNames = new ArrayList<>();

    @Override
    public void delete(ABC abc, boolean d) {
        abc.deleteClass(this.class_info, d);
        abc.constants.getMultiname(this.name_index).deleted = d;
    }

    @Override
    public int getSlotIndex() {
        return this.slot_id;
    }

    @Override
    public String toString(ABC abc, List<DottedChain> fullyQualifiedNames) {
        return "Class " + abc.constants.getMultiname(this.name_index).toString(abc.constants, fullyQualifiedNames) + " slot=" + this.slot_id + " class_info=" + this.class_info + " metadata=" + Helper.intArrToString(this.metadata);
    }

    @Override
    public void getDependencies(int scriptIndex, int classIndex, boolean isStatic, String customNs, ABC abc, List<Dependency> dependencies, List<String> uses, DottedChain ignorePackage, List<DottedChain> fullyQualifiedNames) throws InterruptedException {
        super.getDependencies(scriptIndex, -1, false, customNs, abc, dependencies, uses, ignorePackage == null ? this.getPackage(abc) : ignorePackage, fullyQualifiedNames);
        ClassInfo classInfo = abc.class_info.get(this.class_info);
        InstanceInfo instanceInfo = abc.instance_info.get(this.class_info);
        DottedChain packageName = instanceInfo.getName(abc.constants).getNamespace(abc.constants).getName(abc.constants); //assume not null name

        //DependencyParser.parseDependenciesFromMultiname(customNs, abc, dependencies, uses, abc.constants.getMultiname(instanceInfo.name_index), packageName, fullyQualifiedNames);
        if (instanceInfo.super_index > 0) {
            DependencyParser.parseDependenciesFromMultiname(customNs, abc, dependencies, uses, abc.constants.getMultiname(instanceInfo.super_index), packageName, fullyQualifiedNames, DependencyType.INHERITANCE);
        }
        for (int i : instanceInfo.interfaces) {
            DependencyParser.parseDependenciesFromMultiname(customNs, abc, dependencies, uses, abc.constants.getMultiname(i), packageName, fullyQualifiedNames, DependencyType.INHERITANCE);
        }

        //static
        classInfo.static_traits.getDependencies(scriptIndex, this.class_info, true, customNs, abc, dependencies, uses, packageName, fullyQualifiedNames);

        //static initializer
        DependencyParser.parseDependenciesFromMethodInfo(null, scriptIndex, this.class_info, true, customNs, abc, classInfo.cinit_index, dependencies, uses, packageName, fullyQualifiedNames, new ArrayList<>());

        //instance
        instanceInfo.instance_traits.getDependencies(scriptIndex, this.class_info, false, customNs, abc, dependencies, uses, packageName, fullyQualifiedNames);

        //instance initializer
        DependencyParser.parseDependenciesFromMethodInfo(null, scriptIndex, this.class_info, false, customNs, abc, instanceInfo.iinit_index, dependencies, uses, packageName, fullyQualifiedNames, new ArrayList<>());
    }

    @Override
    public GraphTextWriter toStringHeader(Trait parent, ConvertData convertData, String path, ABC abc, boolean isStatic, ScriptExportMode exportMode, int scriptIndex, int classIndex, GraphTextWriter writer, List<DottedChain> fullyQualifiedNames, boolean parallel) {
        abc.instance_info.get(this.class_info).getClassHeaderStr(writer, abc, fullyQualifiedNames, false);
        return writer;
    }

    @Override
    public void convertHeader(Trait parent, ConvertData convertData, String path, ABC abc, boolean isStatic, ScriptExportMode exportMode, int scriptIndex, int classIndex, NulWriter writer, List<DottedChain> fullyQualifiedNames, boolean parallel) {
    }

    @Override
    public GraphTextWriter toString(Trait parent, ConvertData convertData, String path, ABC abc, boolean isStatic, ScriptExportMode exportMode, int scriptIndex, int classIndex, GraphTextWriter writer, List<DottedChain> fullyQualifiedNames, boolean parallel) throws InterruptedException {

        InstanceInfo instanceInfo = abc.instance_info.get(this.class_info);
        Multiname instanceInfoMultiname = instanceInfo.getName(abc.constants);
        DottedChain packageName = instanceInfoMultiname.getNamespace(abc.constants).getName(abc.constants); //assume not null name

        fullyQualifiedNames = new ArrayList<>();
        this.writeImportsUsages(scriptIndex, classIndex, false, abc, writer, packageName, fullyQualifiedNames);

        String instanceInfoName = instanceInfoMultiname.getName(abc.constants, fullyQualifiedNames, false, true);

        writer.startClass(this.class_info);

        this.getMetaData(parent, convertData, abc, writer);
        //class header
        instanceInfo.getClassHeaderStr(writer, abc, fullyQualifiedNames, false);
        writer.startBlock();

        //static variables & constants
        ClassInfo classInfo = abc.class_info.get(this.class_info);
        classInfo.static_traits.toString(new Class[]{TraitSlotConst.class}, this, convertData, path +/*packageName +*/ "/" + instanceInfoName, abc, true, exportMode, false, scriptIndex, this.class_info, writer, fullyQualifiedNames, parallel, new ArrayList<>());

        //static initializer
        int bodyIndex = abc.findBodyIndex(classInfo.cinit_index);
        if (bodyIndex != -1) {
            writer.startTrait(GraphTextWriter.TRAIT_CLASS_INITIALIZER);
            writer.startMethod(classInfo.cinit_index);
            if (exportMode != ScriptExportMode.AS_METHOD_STUBS) {
                if (!this.classInitializerIsEmpty) {
                    writer.startBlock();
                    abc.bodies.get(bodyIndex).toString(path +/*packageName +*/ "/" + instanceInfoName + ".staticinitializer", exportMode, abc, this, writer, fullyQualifiedNames, new HashSet<>());
                    writer.endBlock();
                } else {
                    //Note: There must be trait/method highlight even if the initializer is empty to TraitList in GUI to work correctly
                    //TODO: handle this better in GUI(?)
                    writer.append(" ").newLine();
                }
            }
            writer.endMethod();
            writer.endTrait();
            if (!this.classInitializerIsEmpty) {
                writer.newLine();
            }
        } else {
            //"/*classInitializer*/";
        }

        //instance variables
        instanceInfo.instance_traits.toString(new Class[]{TraitSlotConst.class}, this, convertData, path +/*packageName +*/ "/" + instanceInfoName, abc, false, exportMode, false, scriptIndex, this.class_info, writer, fullyQualifiedNames, parallel, new ArrayList<>());

        //instance initializer - constructor
        if (!instanceInfo.isInterface()) {
            String modifier = "";
            Multiname m = abc.constants.getMultiname(instanceInfo.name_index);
            Namespace ns = m.getNamespace(abc.constants);
            if (ns != null) {
                modifier = ns.getPrefix(abc) + " ";
                if (modifier.equals(" ")) {
                    modifier = "";
                }
                if (modifier.startsWith("private")) { //cannot have private constuctor
                    modifier = "";
                }
            }

            writer.newLine();
            writer.startTrait(GraphTextWriter.TRAIT_INSTANCE_INITIALIZER);
            writer.startMethod(instanceInfo.iinit_index);
            writer.appendNoHilight(modifier);
            writer.appendNoHilight("function ");
            writer.appendNoHilight(m.getName(abc.constants, null/*do not want full names here*/, false, true));
            writer.appendNoHilight("(");
            bodyIndex = abc.findBodyIndex(instanceInfo.iinit_index);
            MethodBody body = bodyIndex == -1 ? null : abc.bodies.get(bodyIndex);
            abc.method_info.get(instanceInfo.iinit_index).getParamStr(writer, abc.constants, body, abc, fullyQualifiedNames);
            writer.appendNoHilight(")").startBlock();
            if (exportMode != ScriptExportMode.AS_METHOD_STUBS) {
                if (body != null) {
                    body.toString(path +/*packageName +*/ "/" + instanceInfoName + ".initializer", exportMode, abc, this, writer, fullyQualifiedNames, new HashSet<>());
                }
            }

            writer.endBlock().newLine();
            writer.endMethod();
            writer.endTrait();
        }

        //static methods
        classInfo.static_traits.toString(new Class[]{TraitClass.class, TraitFunction.class, TraitMethodGetterSetter.class}, this, convertData, path +/*packageName +*/ "/" + instanceInfoName, abc, true, exportMode, false, scriptIndex, this.class_info, writer, fullyQualifiedNames, parallel, new ArrayList<>());

        //instance methods
        instanceInfo.instance_traits.toString(new Class[]{TraitClass.class, TraitFunction.class, TraitMethodGetterSetter.class}, this, convertData, path +/*packageName +*/ "/" + instanceInfoName, abc, false, exportMode, false, scriptIndex, this.class_info, writer, fullyQualifiedNames, parallel, new ArrayList<>());

        writer.endBlock(); // class
        writer.endClass();
        writer.newLine();
        return writer;
    }

    @Override
    public void convert(Trait parent, ConvertData convertData, String path, ABC abc, boolean isStatic, ScriptExportMode exportMode, int scriptIndex, int classIndex, NulWriter writer, List<DottedChain> fullyQualifiedNames, boolean parallel) throws InterruptedException {

        fullyQualifiedNames = new ArrayList<>();

        InstanceInfo instanceInfo = abc.instance_info.get(this.class_info);
        String instanceInfoName = instanceInfo.getName(abc.constants).getName(abc.constants, fullyQualifiedNames, false, true);
        ClassInfo classInfo = abc.class_info.get(this.class_info);

        AbcIndexing index = new AbcIndexing(abc.getSwf());
        //for simplification of String(this)
        int sIndex = abc.constants.getStringId("", false);
        if (sIndex > -1) {
            int nsIndex = abc.constants.getNamespaceId(Namespace.KIND_PACKAGE, DottedChain.TOPLEVEL, sIndex, false);
            if (nsIndex > -1) {
                convertData.thisHasDefaultToPrimitive = null == index.findProperty(new AbcIndexing.PropertyDef("toString", new TypeItem(instanceInfo.getName(abc.constants).getNameWithNamespace(abc.constants, true)), abc, nsIndex), false, true);
            } else {
                convertData.thisHasDefaultToPrimitive = true;
            }
        } else {
            convertData.thisHasDefaultToPrimitive = true;
        }

        //class initializer
        int bodyIndex = abc.findBodyIndex(classInfo.cinit_index);
        if (bodyIndex != -1) {
            writer.mark();
            List<Traits> ts = new ArrayList<>();
            ts.add(classInfo.static_traits);
            abc.bodies.get(bodyIndex).convert(convertData, path +/*packageName +*/ "/" + instanceInfoName + ".staticinitializer", exportMode, true, classInfo.cinit_index, scriptIndex, this.class_info, abc, this, new ScopeStack(), GraphTextWriter.TRAIT_CLASS_INITIALIZER, writer, fullyQualifiedNames, ts, true, new HashSet<>());
            this.classInitializerIsEmpty = !writer.getMark();
        }

        //constructor - instance initializer
        if (!instanceInfo.isInterface()) {
            bodyIndex = abc.findBodyIndex(instanceInfo.iinit_index);
            if (bodyIndex != -1) {
                List<Traits> ts = new ArrayList<>();
                ts.add(instanceInfo.instance_traits);
                MethodBody constructorBody = abc.bodies.get(bodyIndex);
                constructorBody.convert(convertData, path +/*packageName +*/ "/" + instanceInfoName + ".initializer", exportMode, false, instanceInfo.iinit_index, scriptIndex, this.class_info, abc, this, new ScopeStack(), GraphTextWriter.TRAIT_INSTANCE_INITIALIZER, writer, fullyQualifiedNames, ts, true, new HashSet<>());
            }
        }

        //static variables,constants & methods
        classInfo.static_traits.convert(this, convertData, path +/*packageName +*/ "/" + instanceInfoName, abc, true, exportMode, false, scriptIndex, this.class_info, writer, fullyQualifiedNames, parallel);

        instanceInfo.instance_traits.convert(this, convertData, path +/*packageName +*/ "/" + instanceInfoName, abc, false, exportMode, false, scriptIndex, this.class_info, writer, fullyQualifiedNames, parallel);
    }

    @Override
    public int removeTraps(int scriptIndex, int classIndex, boolean isStatic, ABC abc, String path) throws InterruptedException {
        ClassInfo classInfo = abc.class_info.get(this.class_info);
        InstanceInfo instanceInfo = abc.instance_info.get(this.class_info);
        int iInitializer = abc.findBodyIndex(instanceInfo.iinit_index);
        int ret = 0;
        if (iInitializer != -1) {
            ret += abc.bodies.get(iInitializer).removeTraps(abc, this, scriptIndex, this.class_info, false, path);
        }
        int sInitializer = abc.findBodyIndex(classInfo.cinit_index);
        if (sInitializer != -1) {
            ret += abc.bodies.get(sInitializer).removeTraps(abc, this, scriptIndex, this.class_info, true, path);
        }
        ret += instanceInfo.instance_traits.removeTraps(scriptIndex, this.class_info, false, abc, path);
        ret += classInfo.static_traits.removeTraps(scriptIndex, this.class_info, true, abc, path);
        return ret;
    }

    @Override
    public TraitClass clone() {
        TraitClass ret = (TraitClass) super.clone();
        return ret;
    }

    @Override
    public GraphTextWriter convertTraitHeader(ABC abc, GraphTextWriter writer) {
        this.convertCommonHeaderFlags("class", abc, writer);
        writer.appendNoHilight(" slotid ");
        writer.hilightSpecial(Integer.toString(this.slot_id), HighlightSpecialType.SLOT_ID);
        writer.newLine();
        /*writer.appendNoHilight("class_info "); //not in RAbcDasm
        writer.appendNoHilight("" + class_info);
        writer.newLine();*/
        return writer;
    }

    @Override
    public void getMethodInfos(ABC abc, int traitId, int classIndex, List<MethodId> methodInfos) {
        InstanceInfo instanceInfo = abc.instance_info.get(this.class_info);
        ClassInfo classInfo = abc.class_info.get(this.class_info);

        //class initializer
        methodInfos.add(new MethodId(GraphTextWriter.TRAIT_CLASS_INITIALIZER, this.class_info, classInfo.cinit_index));

        //constructor - instance initializer
        methodInfos.add(new MethodId(GraphTextWriter.TRAIT_INSTANCE_INITIALIZER, this.class_info, instanceInfo.iinit_index));

        //static variables,constants & methods
        classInfo.static_traits.getMethodInfos(abc, true, this.class_info, methodInfos);

        instanceInfo.instance_traits.getMethodInfos(abc, false, this.class_info, methodInfos);
    }
}
