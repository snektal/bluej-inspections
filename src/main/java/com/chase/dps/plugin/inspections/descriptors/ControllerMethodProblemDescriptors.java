package com.chase.dps.plugin.inspections.descriptors;

import com.chase.digital.bluej.annotations.apidocs.ApiDocs;
import com.chase.digital.bluej.annotations.security.SecurityPolicy;
import com.chase.dps.plugin.inspections.fixes.InsertAnnotationFix;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

public final class ControllerMethodProblemDescriptors extends ProblemDescriptors {


    private final LocalQuickFix[] fixes = {
            new InsertAnnotationFix(RequestMapping.class, insertFirst),
            new InsertAnnotationFix(ResponseStatus.class, insertFirst),
            new InsertAnnotationFix(ResponseBody.class, insertFirst),
            new InsertAnnotationFix(ApiDocs.class, insertFirst),
            new InsertAnnotationFix(SecurityPolicy.class, insertFirst)};

    private PsiMethod method;

    public ControllerMethodProblemDescriptors(@NotNull PsiMethod method, @NotNull InspectionManager manager) {
        super(manager);
        this.method = method;
    }

    public PsiAnnotation[] getRegisteredAnnotations() {
        List<PsiAnnotation> list = new ArrayList<>();
        for (LocalQuickFix fix : fixes) {
            PsiAnnotation annotation = JavaPsiFacade
                    .getInstance(manager.getProject())
                    .getElementFactory()
                    .createAnnotationFromText(fix.getFamilyName(), method);
            list.add(annotation);
        }
//        return (PsiAnnotation[])Arrays.stream (fixes).map(fix-> JavaPsiFacade.getInstance(manager.getProject()).getElementFactory()
//                .createAnnotationFromText(fix.getFamilyName(), method)).collect(Collectors.toList()).toArray();
        return list.toArray(new PsiAnnotation[fixes.length]);
    }

    @NotNull
    public ControllerMethodProblemDescriptors add(@NotNull PsiElement element, @NotNull String message) {
        descriptors.add(manager.createProblemDescriptor(element, message, fixes, HIGHLIGHT_TYPE, false, false));
        return this;
    }

}
