package com.dotnomi.fabricdependencyinjection.linemarker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.dotnomi.fabricdependencyinjection.PluginConstants.POST_CONSTRUCT_ANNOTATION;

public class PostConstructLineMarkerProvider implements LineMarkerProvider {
  @Override
  public @Nullable LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
    if (!(element instanceof PsiIdentifier && element.getParent() instanceof PsiMethod method)) {
      return null;
    }

    if (method.hasAnnotation(POST_CONSTRUCT_ANNOTATION)) {
      return new LineMarkerInfo<>(
        element,
        element.getTextRange(),
        AllIcons.Gutter.ReadAccess,
        (item) -> "This method is executed after dependency injection",
        null,
        GutterIconRenderer.Alignment.CENTER,
        () -> "PostConstruct Method"
      );
    }
    return null;
  }
}
