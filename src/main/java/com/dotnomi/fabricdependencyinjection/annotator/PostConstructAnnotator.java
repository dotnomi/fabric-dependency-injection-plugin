package com.dotnomi.fabricdependencyinjection.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.dotnomi.fabricdependencyinjection.PluginConstants.POST_CONSTRUCT_ANNOTATION;

public class PostConstructAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (!(element instanceof PsiMethod method)) {
      return;
    }

    if (!method.hasAnnotation(POST_CONSTRUCT_ANNOTATION)) {
      return;
    }

    var parameterList = method.getParameterList();
    if (parameterList.getParametersCount() > 0) {
      holder.newAnnotation(HighlightSeverity.ERROR, "@PostConstruct method must not have any parameters.")
        .range(parameterList)
        .create();
    }

    var containingClass = method.getContainingClass();
    if (containingClass != null) {
      var postConstructMethods = Arrays.stream(containingClass.getMethods())
        .filter(m -> m.hasAnnotation(POST_CONSTRUCT_ANNOTATION))
        .toList();

      if (postConstructMethods.size() > 1) {
        var methodNameIdentifier = method.getNameIdentifier();
        if (methodNameIdentifier != null) {
          holder.newAnnotation(HighlightSeverity.ERROR, "Only one method can be annotated with @PostConstruct per class.")
            .range(methodNameIdentifier)
            .create();
        }
      }
    }
  }
}
