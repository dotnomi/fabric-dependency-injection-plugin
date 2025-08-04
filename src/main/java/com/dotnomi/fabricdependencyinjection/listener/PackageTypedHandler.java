package com.dotnomi.fabricdependencyinjection.listener;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.popup.list.ListPopupImpl;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.dotnomi.fabricdependencyinjection.PluginConstants.MOD_MAIN_ANNOTATION;

public class PackageTypedHandler extends TypedHandlerDelegate {
  @Override
  public @NotNull Result charTyped(char charTyped, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
    showCustomPopup(charTyped, project, editor, file, true);
    return Result.CONTINUE;
  }

  @Override
  public @NotNull Result checkAutoPopup(char charTyped, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
    showCustomPopup(charTyped, project, editor, file, false);
    return Result.CONTINUE;
  }

  private void showCustomPopup(char charTyped, Project project, Editor editor, PsiFile file, boolean isTyping) {
    var offset = editor.getCaretModel().getOffset();
    var elementAtCaret = file.findElementAt(offset - 1);

    if (elementAtCaret == null) return;

    var pattern = PsiJavaPatterns.psiLiteral().inside(
      PsiJavaPatterns.psiNameValuePair().withName("packageName").inside(
        PsiJavaPatterns.psiAnnotation().qName(MOD_MAIN_ANNOTATION)
      )
    );

    var elementAtCaretParent = elementAtCaret.getParent();
    if (elementAtCaretParent instanceof PsiLiteralExpression literalExpression) {
      if (pattern.accepts(literalExpression)) {
        var currentText = (String) literalExpression.getValue();
        currentText += charTyped;

        if (isTyping && currentText.length() != 1) return;

        var suggestions = getPackageSuggestions(project, currentText);
        if (suggestions.isEmpty()) {
          System.out.println("No suggestions found.");
          return;
        }

        for (var suggestion : suggestions) {
          System.out.println("Suggestion: " + suggestion);
        }

        String finalCurrentText = currentText;
        Consumer<String> onSelectAction = selectedValue ->
          WriteCommandAction.runWriteCommandAction(project, () -> {
            System.out.println("CurrentText: " + finalCurrentText);
            int lastDotIndex = finalCurrentText.lastIndexOf('.');
            String prefix = (lastDotIndex == -1) ? "" : finalCurrentText;
            String newText = prefix + selectedValue;

            int start = literalExpression.getTextRange().getStartOffset() + 1;
            int end = literalExpression.getTextRange().getEndOffset() - 1;

            editor.getDocument().replaceString(start, end, newText);
            editor.getCaretModel().moveToOffset(start + newText.length());
          }
        );

        ListPopup popup = createPopup(project, suggestions, onSelectAction);
        popup.showInBestPositionFor(editor);
      }
    }
  }

  private List<String> getPackageSuggestions(Project project, String currentText) {
    var packageSearch = "";
    if (currentText.contains(".")) {
      String basePackageName = currentText.endsWith(".") ? currentText.substring(0, currentText.length() - 1) : currentText;
      int lastDot = basePackageName.lastIndexOf('.');
      if(lastDot > -1 && !currentText.endsWith(".")) {
        packageSearch = basePackageName.substring(0, lastDot);
      } else {
        packageSearch = basePackageName;
      }
    }

    PsiPackage basePackage = JavaPsiFacade.getInstance(project).findPackage(packageSearch);
    if (basePackage == null) return Collections.emptyList();

    List<String> suggestions = new ArrayList<>();
    GlobalSearchScope scope = GlobalSearchScope.allScope(project);
    for (PsiPackage subPackage : basePackage.getSubPackages(scope)) {
      var subPackageName = subPackage.getName();
      if (subPackageName == null) continue;
      suggestions.add(subPackage.getName());
    }

    return suggestions;
  }

  private ListPopup createPopup(Project project, List<String> actions, Consumer<String> invokeAction) {
    BaseListPopupStep<String> step = new BaseListPopupStep<>(null, actions) {
      @NotNull
      @Override
      public String getTextFor(String value) {
        return value;
      }

      @Override
      public Icon getIconFor(String value) {
        return AllIcons.Nodes.Package;
      }

      @Nullable
      @Override
      public PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {
        invokeAction.accept(selectedValue);
        return PopupStep.FINAL_CHOICE;
      }
    };
    return new ListPopupImpl(project, step);
  }
}
