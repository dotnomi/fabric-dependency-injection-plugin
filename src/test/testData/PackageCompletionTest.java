package com.dotnomi.fabricdependencyinjection.test;

import com.dotnomi.fabricdependencyinjection.annotation.ModMain;

/**
 * This is a test file to demonstrate the usage of the @ModMain annotation with packageName attribute.
 * 
 * When editing the packageName attribute, autocompletion should be triggered:
 * - Automatically when typing a dot
 * - Manually when pressing Ctrl+Space
 */
@ModMain(packageName = "com.example")
public class PackageCompletionTest {
    // Class implementation
}