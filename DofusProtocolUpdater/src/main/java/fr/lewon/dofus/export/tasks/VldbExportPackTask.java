package fr.lewon.dofus.export.tasks;

import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.RunnableIOExResult;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import com.jpexs.helpers.Helper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

public class VldbExportPackTask implements Callable<File> {

    private final ScriptPack pack;
    private final ScriptExportSettings exportSettings;
    private final EventListener eventListener;
    private final String fileName;
    private final BiConsumer<String, ScriptPack> fileContentTreatment;

    private long startTime;

    public VldbExportPackTask(ScriptPack pack, ScriptExportSettings exportSettings, EventListener evl, String fileName, BiConsumer<String, ScriptPack> fileContentTreatment) {
        this.pack = pack;
        this.exportSettings = exportSettings;
        eventListener = evl;
        this.fileName = fileName;
        this.fileContentTreatment = fileContentTreatment;
    }

    @Override
    public File call() throws IOException, InterruptedException {
        RunnableIOExResult<String> rio = new RunnableIOExResult<>() {
            @Override
            public void run() throws IOException, InterruptedException {
                startTime = System.currentTimeMillis();
                result = pack.export(exportSettings, true);
            }
        };

        rio.run();

        handleExport(rio);

        return null;
    }

    private void handleExport(RunnableIOExResult<String> rio) {
        if (eventListener != null) {
            fileContentTreatment.accept(rio.result, pack);
            long time = System.currentTimeMillis() - startTime;
            eventListener.handleExportedEvent(fileName, Helper.formatTimeSec(time));
        }
    }
}
