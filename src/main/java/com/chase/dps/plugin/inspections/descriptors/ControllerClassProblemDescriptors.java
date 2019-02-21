package com.chase.dps.plugin.inspections.descriptors;

import com.chase.digital.bluej.annotations.apidocs.ApiDocs;
import com.chase.digital.bluej.annotations.stereotype.BlueJController;
import com.chase.dps.plugin.inspections.fixes.InsertAnnotationFix;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ControllerClassProblemDescriptors extends ProblemDescriptors {


    private List<LocalQuickFix> fixes = new ArrayList<>();
    private PsiClass aClass;

    public ControllerClassProblemDescriptors(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean hasAnnotation) {

        super(manager);
        this.aClass = aClass;

        if (!hasAnnotation) {
            fixes.add(new InsertAnnotationFix(BlueJController.class, insertFirst));

        }
        fixes.add(new InsertAnnotationFix(RequestMapping.class, insertFirst));
        fixes.add(new InsertAnnotationFix(ApiDocs.class, insertFirst));
    }

    @NotNull
    public ControllerClassProblemDescriptors add(@NotNull PsiElement element, @NotNull String message) {
        descriptors.add(manager.createProblemDescriptor(element, message, toArray(fixes), HIGHLIGHT_TYPE, false, false));
        return this;
    }

    @Nullable
    public LocalQuickFix[] toArray(List<LocalQuickFix> fixes) {
        return descriptors.isEmpty() ? null : fixes.toArray(new LocalQuickFix[fixes.size()]);
    }

    public PsiAnnotation[] getRegisteredAnnotations() {
        return (PsiAnnotation[]) fixes.stream().map(fix -> JavaPsiFacade.getInstance(manager.getProject()).getElementFactory()
                .createAnnotationFromText(((InsertAnnotationFix) fix).getFamilyName(), aClass)).collect(Collectors.toList()).toArray();
    }
}
