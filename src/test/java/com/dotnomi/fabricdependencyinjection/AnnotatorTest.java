package com.dotnomi.fabricdependencyinjection;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class AnnotatorTest extends BasePlatformTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/testData";
  }

  public void testAnnotatorShowsErrorForUnmanagedBean() {
    myFixture.addFileToProject(
      "src/com/dotnomi/fabricdependencyinjection/annotation/ModScoped.java",
      "package com.dotnomi.fabricdependencyinjection.annotation; public @interface ModScoped {}"
    );

    myFixture.addFileToProject(
      "src/com/dotnomi/fabricdependencyinjection/annotation/ModInject.java",
      "package com.dotnomi.fabricdependencyinjection.annotation; public @interface ModInject {}"
    );

    myFixture.configureByFile("AnnotatorTest.java");

    myFixture.checkHighlighting(true, false, false);
  }
}
