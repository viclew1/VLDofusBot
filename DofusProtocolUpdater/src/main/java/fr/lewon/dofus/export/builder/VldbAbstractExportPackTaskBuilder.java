package fr.lewon.dofus.export.builder;

import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import fr.lewon.dofus.export.tasks.VldbExportPackTask;

public abstract class VldbAbstractExportPackTaskBuilder {


    public VldbExportPackTask build(ScriptPack pack, ScriptExportSettings exportSettings, EventListener evl) {
        return new VldbExportPackTask(pack, exportSettings, evl, pack.getName(), this::treatFileContent);
    }

    public abstract void treatFileContent(String fileContent, ScriptPack scriptPack);

    public abstract boolean matchesScriptPack(ScriptPack scriptPack);

}
