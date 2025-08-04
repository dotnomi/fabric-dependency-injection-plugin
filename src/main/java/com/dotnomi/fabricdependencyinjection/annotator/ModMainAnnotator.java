package com.dotnomi.fabricdependencyinjection.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;

import static com.dotnomi.fabricdependencyinjection.PluginConstants.MOD_MAIN_ANNOTATION;

public class ModMainAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    var pattern = PsiJavaPatterns.psiLiteral().inside(
      PsiJavaPatterns.psiNameValuePair().withName("packageName")
        .inside(PsiJavaPatterns.psiAnnotation().qName(MOD_MAIN_ANNOTATION))
    );

    if (!pattern.accepts(element)) {
      return;
    }

    if (!(element instanceof PsiLiteralExpression literalExpression)) {
      return;
    }

    if (!(literalExpression.getValue() instanceof String packageName) || packageName.isBlank()) {
      return;
    }

    var psiPackage = JavaPsiFacade.getInstance(element.getProject()).findPackage(packageName);
    if (psiPackage == null) {
      holder.newAnnotation(HighlightSeverity.ERROR, "Package '" + packageName + "' does not exist.")
        .range(element.getTextRange())
        .create();
    }
  }
}
