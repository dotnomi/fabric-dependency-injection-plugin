package com.dotnomi.fabricdependencyinjection.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.dotnomi.fabricdependencyinjection.PluginConstants.MOD_INJECT_ANNOTATION;
import static com.dotnomi.fabricdependencyinjection.PluginConstants.MOD_SCOPED_ANNOTATION;

public class ModInjectAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element instanceof PsiField) {
      annotateField((PsiField) element, holder);
    }

    if (element instanceof PsiMethod method && method.isConstructor()) {
      annotateConstructor(method, holder);
    }
  }

  private void annotateField(PsiField field, @NotNull AnnotationHolder holder) {
    if (!field.hasAnnotation(MOD_INJECT_ANNOTATION)) {
      return;
    }

    var fieldType = field.getType();
    if (fieldType instanceof PsiClassType classType) {
      var targetClass = classType.resolve();
      if (targetClass != null && !targetClass.hasAnnotation(MOD_SCOPED_ANNOTATION)) {
        var fieldTypeElement = field.getTypeElement();
        if (fieldTypeElement != null) {
          var codeReferenceElement = fieldTypeElement.getInnermostComponentReferenceElement();
          if (codeReferenceElement != null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot inject an unmanaged bean. The target class must be annotated with @ModScoped.")
              .range(codeReferenceElement)
              .create();
          }
        }
      }
    }
  }

  private void annotateConstructor(PsiMethod constructor, @NotNull AnnotationHolder holder) {
    if (!constructor.hasAnnotation(MOD_INJECT_ANNOTATION)) {
      return;
    }

    var containingClass = constructor.getContainingClass();
    if (containingClass != null) {
      var injectableConstructorCount = Arrays.stream(containingClass.getConstructors())
        .filter(c -> c.hasAnnotation(MOD_INJECT_ANNOTATION))
        .count();

      if (injectableConstructorCount > 1) {
        var constructorNameIdentifier = constructor.getNameIdentifier();
        if (constructorNameIdentifier != null) {
          holder.newAnnotation(HighlightSeverity.ERROR, "A class can only have one constructor annotated with @ModInject.")
            .range(constructorNameIdentifier)
            .create();
        }
      }
    }

    for (var parameter : constructor.getParameterList().getParameters()) {
      var parameterType = parameter.getType();
      if (parameterType instanceof PsiClassType classType) {
        var parameterClass = classType.resolve();
        if (parameterClass != null && !parameterClass.hasAnnotation(MOD_SCOPED_ANNOTATION)) {
          var parameterTypeElement = parameter.getTypeElement();
          if (parameterTypeElement != null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot inject an unmanaged bean. The parameter's class must be annotated with @ModScoped.")
              .range(parameterTypeElement)
              .create();
          }
        }
      }
    }
  }
}
