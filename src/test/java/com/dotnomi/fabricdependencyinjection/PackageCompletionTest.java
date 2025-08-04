package com.dotnomi.fabricdependencyinjection;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;

/**
 * Tests for package name autocompletion in @ModMain annotation's packageName attribute.
 * 
 * This test verifies that:
 * 1. Autocompletion is triggered when typing a dot in the packageName attribute
 * 2. Correct package suggestions are provided
 * 3. The PackagePartInsertHandler correctly adds a dot and triggers the next popup
 */

public class PackageCompletionTest extends BasePlatformTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Add necessary annotation files to the test project
        myFixture.addFileToProject(
            "src/com/dotnomi/fabricdependencyinjection/annotation/ModMain.java",
            "package com.dotnomi.fabricdependencyinjection.annotation; public @interface ModMain { String packageName(); }"
        );
        
        // The completion contributor and typed handler are registered in plugin.xml
        // No need to register them explicitly for testing
    }

    /**
     * Test that package name autocompletion is triggered when typing a dot
     * and provides correct package suggestions
     */
    public void testPackageNameAutocompletion() {
        // Configure the test fixture with our test data file
        myFixture.configureByFile("PackageCompletionTest.java");
        
        // Position the caret inside the packageName attribute value
        myFixture.getEditor().getCaretModel().moveToOffset(
            myFixture.getFile().getText().indexOf("com.example") + "com".length()
        );
        
        // Trigger autocompletion
        myFixture.type(".");
        myFixture.completeBasic();
        
        // Get the lookup elements (autocompletion suggestions)
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        
        // Verify that we have autocompletion suggestions
        assertNotNull("No autocompletion suggestions found", lookupElementStrings);
        assertFalse("No autocompletion suggestions found", lookupElementStrings.isEmpty());
        
        // Verify that "example" is among the suggestions
        assertTrue("Expected 'example' to be among the suggestions", 
                  lookupElementStrings.contains("example"));
    }
    
    /**
     * Test that PackagePartInsertHandler correctly adds a dot and triggers the next popup
     */
    public void testPackagePartInsertHandler() {
        // Configure the test fixture with our test data file
        myFixture.configureByFile("PackageCompletionTest.java");
        
        // Position the caret inside the packageName attribute value
        myFixture.getEditor().getCaretModel().moveToOffset(
            myFixture.getFile().getText().indexOf("com.example") + "com".length()
        );
        
        // Trigger autocompletion
        myFixture.type(".");
        myFixture.completeBasic();
        
        // Select the first suggestion
        LookupElement[] lookupElements = myFixture.getLookupElements();
        assertNotNull("No lookup elements found", lookupElements);
        assertTrue("No lookup elements found", lookupElements.length > 0);
        
        // Select the first suggestion (which should be "example")
        myFixture.getLookup().setCurrentItem(lookupElements[0]);
        myFixture.type('\n');
        
        // Verify that a dot was added and the caret is positioned after it
        String text = myFixture.getEditor().getDocument().getText();
        int caretOffset = myFixture.getEditor().getCaretModel().getOffset();
        
        // The text should now contain "com.example." and the caret should be after the last dot
        assertTrue("Text should contain 'com.example.'", text.contains("com.example."));
        assertEquals("Caret should be positioned after the last dot", 
                    text.indexOf("com.example.") + "com.example.".length(), caretOffset);
    }
}