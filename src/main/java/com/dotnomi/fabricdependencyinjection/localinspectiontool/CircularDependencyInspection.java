package com.dotnomi.fabricdependencyinjection.localinspectiontool;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.Key;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.dotnomi.fabricdependencyinjection.PluginConstants.MOD_INJECT_ANNOTATION;

public class CircularDependencyInspection extends AbstractBaseJavaLocalInspectionTool {
  private static final Key<CachedValue<CycleResult>> CYCLE_DETECTION_KEY = Key.create("com.dotnomi.CYCLE_DETECTION_KEY");
  private static final Key<CachedValue<Map<PsiClass, PsiElement>>> DEPENDENCIES_KEY = Key.create("com.dotnomi.DEPENDENCIES_KEY");

  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {
      @Override
      public void visitClass(@NotNull PsiClass targetClass) {
        super.visitClass(targetClass);
        if (targetClass.hasAnnotation(MOD_INJECT_ANNOTATION) || targetClass.getNameIdentifier() == null) {
          return;
        }

        CachedValuesManager.getManager(targetClass.getProject());
        var result = CachedValuesManager.getCachedValue(targetClass, CYCLE_DETECTION_KEY, () -> {
          var computedResult = findCycleFor(targetClass, new LinkedHashSet<>());
          return CachedValueProvider.Result.create(computedResult, PsiModificationTracker.MODIFICATION_COUNT);
        });

        if (result != null && result.hasCycle()) {
          holder.registerProblem(result.elementToHighlight, result.cyclePath, ProblemHighlightType.GENERIC_ERROR);
        }
      }
    };
  }

  private static CycleResult findCycleFor(PsiClass targetClass, Set<PsiClass> visitingPath) {
    if (visitingPath.contains(targetClass)) {
      return CycleResult.NO_CYCLE;
    }

    visitingPath.add(targetClass);
    for (var entry : getCachedDependencies(targetClass).entrySet()) {
      var dependency = entry.getKey();
      var sourceElement = entry.getValue();

      if (visitingPath.contains(dependency)) {
        var path = new StringBuilder();
        for (var psiClass : visitingPath) {
          path.append(psiClass.getName()).append(" -> ");
        }
        path.append(dependency.getName());
        return new CycleResult(sourceElement, "Circular dependency detected: " + path);
      }

      var resultFromRecursion = findCycleFor(dependency, visitingPath);
      if (resultFromRecursion.hasCycle()) {
        return resultFromRecursion;
      }
    }

    visitingPath.remove(targetClass);
    return CycleResult.NO_CYCLE;
  }

  private static Map<PsiClass, PsiElement> getCachedDependencies(PsiClass targetClass) {
    return CachedValuesManager.getCachedValue(targetClass, DEPENDENCIES_KEY, () -> {
      var dependencies = findDependencies(targetClass);
      return CachedValueProvider.Result.create(dependencies, PsiModificationTracker.MODIFICATION_COUNT);
    });
  }

  private static Map<PsiClass, PsiElement> findDependencies(PsiClass targetClass) {
    var dependencies = new HashMap<PsiClass, PsiElement>();

    for (var constructor : targetClass.getConstructors()) {
      if (constructor.hasAnnotation(MOD_INJECT_ANNOTATION)) {
        for (var parameter : constructor.getParameterList().getParameters()) {
          var dependency = resolvePsiClassFromType(parameter.getType());
          if (dependency != null) dependencies.put(dependency, parameter);
        }
      }
    }

    for (var field : targetClass.getFields()) {
      if (field.hasAnnotation(MOD_INJECT_ANNOTATION)) {
        var dependency = resolvePsiClassFromType(field.getType());
        if (dependency != null) dependencies.put(dependency, field);
      }
    }

    return dependencies;
  }

  private static PsiClass resolvePsiClassFromType(PsiType type) {
    if (type instanceof PsiClassType classType) {
      return classType.resolve();
    }
    return null;
  }

  private static class CycleResult {
    public static final CycleResult NO_CYCLE = new CycleResult(null, null);

    final PsiElement elementToHighlight;
    final String cyclePath;

    CycleResult(PsiElement element, String path) {
      this.elementToHighlight = element;
      this.cyclePath = path;
    }

    boolean hasCycle() {
      return cyclePath != null;
    }
  }
}
