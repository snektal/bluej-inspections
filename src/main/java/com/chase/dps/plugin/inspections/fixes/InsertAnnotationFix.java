package com.chase.dps.plugin.inspections.fixes;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import org.jetbrains.annotations.NotNull;

public final class InsertAnnotationFix implements LocalQuickFix {
  @NotNull
  private final String annotationText;

  @NotNull
  private final String annotationBody;

  @NotNull
  private final String annotationClassName;

  @NotNull
  private final InsertionPlace place;

  public InsertAnnotationFix(@NotNull Class<?> annotationClass, @NotNull String annotationBody, boolean insertFirst) {
    this.annotationClassName = annotationClass.getSimpleName();
    this.annotationText = '@' + annotationClassName + annotationBody;
    this.annotationBody = annotationBody;
    this.place = insertFirst ? InsertionPlace.FIRST : InsertionPlace.BEFORE_LAST;
  }

  @Override
  @NotNull
  public String getName() {
    return "Insert " + annotationText + " annotation";
  }

  @Override
  @NotNull
  public String getFamilyName() {
    return annotationText;
  }

  @NotNull
  public String getAnnotationClassName() {
    return annotationClassName;
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    final PsiElement element = descriptor.getPsiElement();
    if (!(element instanceof PsiModifierListOwner)) {
      return;
    }
    final PsiModifierList modifiers = ((PsiModifierListOwner) element).getModifierList();
    if (modifiers == null) {
      return;
    }
    final PsiAnnotation annotation = JavaPsiFacade.getElementFactory(project).createAnnotationFromText(annotationText, element);
    place.insert(modifiers, annotation);
  }

  private enum InsertionPlace {
    FIRST {
      @Override
      public void insert(@NotNull PsiModifierList modifiers, @NotNull PsiAnnotation annotation) {
        modifiers.addBefore(annotation, modifiers.getFirstChild());
      }
    },

    BEFORE_LAST {
      @Override
      public void insert(@NotNull PsiModifierList modifiers, @NotNull PsiAnnotation annotation) {
        modifiers.addBefore(annotation, modifiers.getLastChild());
      }
    };

    protected abstract void insert(@NotNull PsiModifierList modifiers, @NotNull PsiAnnotation annotation);
  }
}
