# Fabric Dependency Injection Plugin

![Build](https://github.com/dotnomi/fabric-dependency-injection-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

This IntelliJ IDEA plugin provides editor support for the Fabric Dependency Injection framework.

<!-- Plugin description -->
The Fabric Dependency Injection Plugin enhances your development experience with the Fabric DI framework by providing:

- Package name autocompletion for @ModMain annotation's packageName attribute
- Circular dependency detection
- Line markers for ModInject and PostConstruct annotations
- Annotators for ModInject and PostConstruct annotations
- Implicit usage detection for framework-related code
<!-- Plugin description end -->

## Features

### Package Name Autocompletion

The plugin provides autocompletion for the `packageName` attribute in the `@ModMain` annotation, similar to how IntelliJ provides autocompletion for `implementation` or `implementationClass` attributes in plugin.xml files.

#### How to Use

1. Open a Java file with the `@ModMain` annotation
2. Edit the `packageName` attribute value
3. Type a dot (.) to trigger autocompletion automatically
4. Or press Ctrl+Space to trigger autocompletion manually

Example:
```java
@ModMain(packageName = "com.example")
public class MyClass {
    // Class implementation
}
```

When editing the `packageName` attribute value, you should see autocompletion suggestions for available packages.

#### Troubleshooting

If autocompletion doesn't work as expected:

1. Make sure the plugin is enabled in IntelliJ IDEA
2. Verify that the `@ModMain` annotation is imported correctly
3. Try restarting IntelliJ IDEA
4. Check the IDE logs for any errors related to the plugin
5. Ensure you're typing a dot (.) character to trigger autocompletion

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "fabric-dependency-injection-plugin"</kbd> >
  <kbd>Install</kbd>
  
- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/dotnomi/fabric-dependency-injection-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
