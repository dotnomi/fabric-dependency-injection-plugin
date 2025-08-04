package com.dotnomi.fabricdependencyinjection.linemarker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.dotnomi.fabricdependencyinjection.PluginConstants.MOD_SCOPED_ANNOTATION;

public class ModScopedLineMarkerProvider implements LineMarkerProvider {
  @Override
  public @Nullable LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
    if (!(element instanceof PsiIdentifier && element.getParent() instanceof PsiClass clazz)) {
      return null;
    }

    if (clazz.hasAnnotation(MOD_SCOPED_ANNOTATION)) {
      return new LineMarkerInfo<>(
        element,
        element.getTextRange(),
        AllIcons.Nodes.Class,
        (psiElement) -> "This class is a managed bean.",
        null,
        GutterIconRenderer.Alignment.CENTER,
        () -> "Managed Bean"
      );
    }

    return null;
  }
}
