package com.dotnomi.fabricdependencyinjection.utility;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.HashSet;
import java.util.Set;

public class PackageUtils {
  public static Set<String> getNextPackageComponents(Project project, String prefix) {
    var allPackages = new HashSet<String>();
    collectAllPackageNames(JavaPsiFacade.getInstance(project).findPackage(""),
      GlobalSearchScope.allScope(project),
      allPackages);

    var nextComponents = new HashSet<String>();
    for (var pkg : allPackages) {
      // If prefix is empty, add top-level packages
      if (prefix.isEmpty() && !pkg.contains(".")) {
        nextComponents.add(pkg);
      } 
      // If prefix matches exactly, add it as a suggestion
      else if (!prefix.isEmpty() && pkg.equals(prefix)) {
        nextComponents.add(pkg);
      }
      // If prefix is a parent package, add the next component
      else if (!prefix.isEmpty() && pkg.startsWith(prefix + ".") && pkg.length() > prefix.length() + 1) {
        var remainder = pkg.substring(prefix.length() + 1);
        var dotIndex = remainder.indexOf('.');
        if (dotIndex != -1) {
          nextComponents.add(remainder.substring(0, dotIndex));
        } else {
          nextComponents.add(remainder);
        }
      }
    }
    return nextComponents;
  }

  private static void collectAllPackageNames(PsiPackage currentPackage, GlobalSearchScope scope, Set<String> collector) {
    if (currentPackage == null || currentPackage.getQualifiedName().isEmpty() && currentPackage.getSubPackages(scope).length == 0) {
      return;
    }
    if (!currentPackage.getQualifiedName().isEmpty()) {
      collector.add(currentPackage.getQualifiedName());
    }
    for (var subPackage : currentPackage.getSubPackages(scope)) {
      collectAllPackageNames(subPackage, scope, collector);
    }
  }
}
