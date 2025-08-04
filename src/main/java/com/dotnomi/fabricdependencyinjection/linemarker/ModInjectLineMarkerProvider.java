package com.dotnomi.fabricdependencyinjection.linemarker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static com.dotnomi.fabricdependencyinjection.PluginConstants.MOD_INJECT_ANNOTATION;
import static com.dotnomi.fabricdependencyinjection.PluginConstants.MOD_SCOPED_ANNOTATION;

public class ModInjectLineMarkerProvider implements LineMarkerProvider {
  @Override
  public @Nullable LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
    if (!(element instanceof PsiIdentifier && element.getParent() instanceof PsiField field)) {
      return null;
    }

    var isConstructorInjected = false;
    if (!field.hasAnnotation(MOD_INJECT_ANNOTATION)) {
      if (isFieldPotentiallyConstructorInjected(field)) {
        isConstructorInjected = true;
      } else {
        return null;
      }
    }

    var fieldType = field.getType();
    if (!(fieldType instanceof PsiClassType classType)) {
      return null;
    }

    var targetClass = classType.resolve();
    if (targetClass == null || !targetClass.hasAnnotation(MOD_SCOPED_ANNOTATION)) {
      return null;
    }

    String tooltip = isConstructorInjected ? "Navigate to (Constructor Injected) bean" : "Navigate to bean definition";
    return NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod)
      .setTarget(targetClass)
      .setTooltipText(tooltip)
      .createLineMarkerInfo(element);
  }

  private boolean isFieldPotentiallyConstructorInjected(PsiField field) {
    var isFinalAndUninitialized = field.hasModifierProperty(PsiModifier.FINAL) && field.getInitializer() == null;
    if (!isFinalAndUninitialized) {
      return false;
    }

    var containingClass = field.getContainingClass();
    if (containingClass != null) {
      return Arrays.stream(containingClass.getConstructors())
        .anyMatch(constructor -> constructor.hasAnnotation(MOD_INJECT_ANNOTATION));
    }

    return false;
  }
}
