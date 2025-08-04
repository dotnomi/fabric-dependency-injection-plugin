package com.dotnomi.fabricdependencyinjection;

import com.intellij.codeInspection.deadCode.UnusedDeclarationInspection;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Assert;

public class ImplicitUsageTest extends BasePlatformTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/testData";
  }

  public void testModScopedClassIsNotMarkedAsUnused() {
    myFixture.enableInspections(new UnusedDeclarationInspection(true));

    myFixture.addFileToProject(
      "src/com/dotnomi/fabricdependencyinjection/annotation/ModScoped.java",
      "package com.dotnomi.fabricdependencyinjection.annotation; public @interface ModScoped {}"
    );

    myFixture.configureByFile("ImplicitUsageTest.java");

    long unusedWarningCount = myFixture.doHighlighting().stream()
      .filter(info -> info.getDescription() != null && info.getDescription().contains("is never used"))
      .count();

    Assert.assertEquals(1, unusedWarningCount);
  }
}
