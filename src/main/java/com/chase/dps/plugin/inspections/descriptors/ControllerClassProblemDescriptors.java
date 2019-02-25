package com.chase.dps.plugin.inspections.descriptors;

import com.chase.digital.bluej.annotations.apidocs.ApiDocs;
import com.chase.digital.bluej.annotations.stereotype.BlueJController;
import com.chase.dps.plugin.inspections.fixes.InsertAnnotationFix;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;

public final class ControllerClassProblemDescriptors extends ProblemDescriptors {

    private static final String[] requiredAnnotations = {
            "BlueJController",
            "RequestMapping",
            "ApiDocs"};

    private final InsertAnnotationFix[] fixes = {
            new InsertAnnotationFix(BlueJController.class, "", insertFirst),
            new InsertAnnotationFix(RequestMapping.class, "( value = \"/\")", insertLast),
            new InsertAnnotationFix(ApiDocs.class, "( value = {PROVIDE DOCUMENTATION DETAILS}, tags = {PROVIDE VALUE HERE})", insertLast)};

    private PsiClass aClass;

    public ControllerClassProblemDescriptors(@NotNull PsiClass aClass, @NotNull InspectionManager manager) {

        super(manager);
        this.aClass = aClass;

    }

    @NotNull
    @Override
    public PsiAnnotation[] getMissingAnnotations() {
        return getMissingAnnotations(this.aClass, fixes, requiredAnnotations);
    }

    @NotNull
    @Override
    public PsiAnnotation[] getExistingAnnotations() {
        return getExistingAnnotations(this.aClass, fixes, requiredAnnotations);
    }


    @NotNull
    public ControllerClassProblemDescriptors add(@NotNull PsiElement element, @NotNull PsiAnnotation annotation, @NotNull String message, boolean filterFixes) {
        InsertAnnotationFix[] filteredAnnotationFixes = fixes;
        if(filterFixes) {
            filteredAnnotationFixes = getFilteredAnnotationFixes(fixes, annotation);
        }
        descriptors.add(manager.createProblemDescriptor(element, message, filteredAnnotationFixes, HIGHLIGHT_TYPE, false, false));
        return this;
    }


}
