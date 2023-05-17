package fr.lewon.dofus.export;

import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.configuration.Configuration;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import com.jpexs.helpers.Helper;
import fr.lewon.dofus.export.builder.VldbAbstractExportPackTaskBuilder;
import fr.lewon.dofus.export.tasks.VldbExportPackTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class VldbAS3ScriptExporter {

    private static final Logger logger = Logger.getLogger(VldbAS3ScriptExporter.class.getName());

    public List<File> exportDofusScript(SWF swf, List<ScriptPack> as3scripts, ScriptExportSettings exportSettings, EventListener evl, List<VldbAbstractExportPackTaskBuilder> taskBuilders) {
        final List<File> ret = new ArrayList<>();
        List<ScriptPack> packs = as3scripts != null ? as3scripts : swf.getAS3Packs();

        Map<ScriptPack, List<VldbAbstractExportPackTaskBuilder>> buildersByPack = packs.stream().collect(Collectors.toMap(
                p -> p,
                p -> taskBuilders.stream().filter(tb -> tb.matchesScriptPack(p)).collect(Collectors.toList())
        ));

        List<String> ignoredClasses = new ArrayList<>();
        List<String> ignoredNss = new ArrayList<>();

        String flexClass = swf.getFlexMainClass(ignoredClasses, ignoredNss);

        List<VldbExportPackTask> tasks = new ArrayList<>();
        for (Map.Entry<ScriptPack, List<VldbAbstractExportPackTaskBuilder>> entry : buildersByPack.entrySet()) {
            ScriptPack scriptPack = entry.getKey();
            if (!scriptPack.isSimple && Configuration.ignoreCLikePackages.get()
                    || ignoredClasses.contains(scriptPack.getClassPath().toRawString())
                    || flexClass != null && scriptPack.getClassPath().toRawString().equals(flexClass)) {
                continue;
            }
            entry.getValue().forEach(tb -> tasks.add(tb.build(scriptPack, exportSettings, evl)));
        }

        ExecutorService executor = Executors.newFixedThreadPool(Configuration.getParallelThreadCount());
        List<Future<File>> futureResults = new ArrayList<>();
        for (VldbExportPackTask task : tasks) {
            Future<File> future = executor.submit(task);
            futureResults.add(future);
        }

        try {
            executor.shutdown();
            if (!executor.awaitTermination(Configuration.exportTimeout.get(), TimeUnit.SECONDS)) {
                logger.log(Level.SEVERE, "{0} ActionScript export limit reached", Helper.formatTimeToText(Configuration.exportTimeout.get()));
            }
        } catch (InterruptedException ignored) {
        } finally {
            executor.shutdownNow();
        }

        for (Future<File> futureResult : futureResults) {
            try {
                if (futureResult.isDone()) {
                    ret.add(futureResult.get());
                }
            } catch (InterruptedException ignored) {
            } catch (ExecutionException ex) {
                logger.log(Level.SEVERE, "Error during ABC export", ex);
            }
        }

        return ret;
    }
}
