package com.dotnomi.fabricdependencyinjection.completion;

import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PackageReferenceSet;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.dotnomi.fabricdependencyinjection.PluginConstants.MOD_MAIN_ANNOTATION;

public class PackageReferenceContributor extends PsiReferenceContributor {
  @Override
  public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
    var pattern = PsiJavaPatterns.psiLiteral().inside(
      PsiJavaPatterns.psiNameValuePair().withName("packageName")
        .inside(PsiJavaPatterns.psiAnnotation().qName(MOD_MAIN_ANNOTATION))
    );

    registrar.registerReferenceProvider(pattern, new PsiReferenceProvider() {
      @Override
      public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if (!(element instanceof PsiLiteralExpression literalExpression)) {
          return PsiReference.EMPTY_ARRAY;
        }
        if (!(literalExpression.getValue() instanceof String packageText)) {
          return PsiReference.EMPTY_ARRAY;
        }
        return new PackageReferenceSet(packageText, element, 1)
          .getReferences().toArray(PsiReference.EMPTY_ARRAY);
      }
    });
  }
}
