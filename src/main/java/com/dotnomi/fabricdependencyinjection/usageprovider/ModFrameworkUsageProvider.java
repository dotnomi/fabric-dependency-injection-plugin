package com.dotnomi.fabricdependencyinjection.usageprovider;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import static com.dotnomi.fabricdependencyinjection.PluginConstants.MOD_INJECT_ANNOTATION;
import static com.dotnomi.fabricdependencyinjection.PluginConstants.MOD_MAIN_ANNOTATION;
import static com.dotnomi.fabricdependencyinjection.PluginConstants.MOD_SCOPED_ANNOTATION;
import static com.dotnomi.fabricdependencyinjection.PluginConstants.POST_CONSTRUCT_ANNOTATION;

public class ModFrameworkUsageProvider implements ImplicitUsageProvider {
  @Override
  public boolean isImplicitUsage(@NotNull PsiElement element) {
    if (element instanceof PsiClass clazz) {
      return clazz.hasAnnotation(MOD_MAIN_ANNOTATION) || clazz.hasAnnotation(MOD_SCOPED_ANNOTATION);
    }

    if (element instanceof PsiMethod method) {
      return !method.isConstructor() && method.hasAnnotation(POST_CONSTRUCT_ANNOTATION);
    }

    return false;
  }

  @Override
  public boolean isImplicitRead(@NotNull PsiElement element) {
    if (element instanceof PsiField field) {
      return field.hasAnnotation(MOD_INJECT_ANNOTATION);
    }
    return false;
  }

  @Override
  public boolean isImplicitWrite(@NotNull PsiElement element) {
    return false;
  }

  @Override
  public boolean isImplicitlyNotNullInitialized(@NotNull PsiElement element) {
    if (element instanceof PsiField field) {
      return field.hasAnnotation(MOD_INJECT_ANNOTATION);
    }
    return false;
  }
}
