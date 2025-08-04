package com.dotnomi.fabricdependencyinjection;

import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Assert;

public class LineMarkerTest extends BasePlatformTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/testData";
  }

  public void testLineMarkerIsPresentForValidInjection() {
    myFixture.addFileToProject(
      "src/com/dotnomi/fabricdependencyinjection/annotation/ModScoped.java",
      "package com.dotnomi.fabricdependencyinjection.annotation; public @interface ModScoped {}"
    );

    myFixture.addFileToProject(
      "src/com/dotnomi/fabricdependencyinjection/annotation/ModInject.java",
      "package com.dotnomi.fabricdependencyinjection.annotation; public @interface ModInject {}"
    );

    myFixture.configureByFile("LineMarkerTest.java");

    var gutters = myFixture.findAllGutters();

    Assert.assertFalse("Gutter marks should be present", gutters.isEmpty());
    Assert.assertEquals("Exactly one gutter mark should be found", 1, gutters.size());

    GutterMark gutter = gutters.getFirst();
    Assert.assertEquals("Navigate to @ModScoped bean definition", gutter.getTooltipText());
  }
}
