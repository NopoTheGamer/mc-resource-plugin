package com.github.nopothegamer.mcresourceplugin;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CustomGotoDeclarationHandler implements GotoDeclarationHandler {
    private static final Pattern RESOURCE_PATTERN = Pattern.compile("(\\w+)?:?((.*/)?(.*))");
    private static final Pattern FILE_PATTERN = Pattern.compile("(.*/main/).*");
    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        if (sourceElement == null) return new PsiElement[0];
        if (!sourceElement.toString().equals("PsiJavaToken:STRING_LITERAL")) return new PsiElement[0];
        String sourceText = sourceElement.getContext().getText().replace("\"", "");
        Matcher resourceMatcher = RESOURCE_PATTERN.matcher(sourceText);
        if (!resourceMatcher.matches()) return new PsiElement[0];


        List<PsiElement> psiElements = new ArrayList<>();
        String path = sourceElement.getContainingFile().getVirtualFile().getPath();
        Matcher pathMatcher = FILE_PATTERN.matcher(path);
        if (pathMatcher.matches()) {
            path = pathMatcher.group(1) + "resources/assets/" + resourceMatcher.group(1) + "/" + resourceMatcher.group(2);
            VirtualFile a = VirtualFileManager.getInstance().findFileByUrl("file://" + path);
            if (a != null && a.exists()) {
                psiElements.add(PsiManager.getInstance(sourceElement.getProject()).findFile(a));
            } else return new PsiElement[0];
        } else return new PsiElement[0];

        return psiElements.toArray(new PsiElement[psiElements.size()]);
    }
}
