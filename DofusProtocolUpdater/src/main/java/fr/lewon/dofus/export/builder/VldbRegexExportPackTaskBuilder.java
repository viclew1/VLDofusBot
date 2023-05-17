package fr.lewon.dofus.export.builder;

import com.jpexs.decompiler.flash.abc.ScriptPack;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class VldbRegexExportPackTaskBuilder extends VldbAbstractFileExportPackTaskBuilder {

    private final String regex;

    public VldbRegexExportPackTaskBuilder(String fileName, String regex) {
        super(fileName);
        this.regex = regex;
    }

    @Override
    public void treatFileContent(String fileContent, ScriptPack scriptPack) {
        Pattern p = Pattern.compile(regex);
        Arrays.stream(fileContent.split("\n"))
                .map(p::matcher)
                .filter(Matcher::find)
                .findFirst()
                .ifPresent(this::treatMatcher);
    }

    protected abstract void treatMatcher(Matcher matcher);
}
