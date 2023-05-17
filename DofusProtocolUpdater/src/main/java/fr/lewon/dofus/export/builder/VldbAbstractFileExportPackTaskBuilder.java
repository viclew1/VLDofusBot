package fr.lewon.dofus.export.builder;

import com.jpexs.decompiler.flash.abc.ScriptPack;

public abstract class VldbAbstractFileExportPackTaskBuilder extends VldbAbstractExportPackTaskBuilder {

    private final String fileName;

    protected VldbAbstractFileExportPackTaskBuilder(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean matchesScriptPack(ScriptPack scriptPack) {
        return scriptPack.getName().equals(getFileName());
    }
}
