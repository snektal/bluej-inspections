package com.chase.dps.plugin.inspections.fixes;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

final class ControllerAnnotationFix implements LocalQuickFix {

    @Override
    @NotNull
    public String getFamilyName() {
        return "Controller interface";
    }

    @Nls
    @NotNull
    @Override
    public String getName() {
        return "Add @BlueJController class annotation";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        final PsiElement element = descriptor.getPsiElement();
        if (!(element instanceof PsiClass)) {
            return;
        }
        final PsiClass psiClass = (PsiClass) element;
        if (!psiClass.isInterface()) {
            return;
        }

        PsiAnnotation annotation = psiClass.getAnnotation("@BlueJController");
        if (annotation != null) {
            return;
        }
    }
}