package com.chase.dps.plugin.inspections.descriptors;

import com.chase.digital.bluej.annotations.apidocs.ApiDocs;
import com.chase.digital.bluej.annotations.security.SecurityPolicy;
import com.chase.dps.plugin.inspections.fixes.InsertAnnotationFix;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public final class ControllerMethodProblemDescriptors extends ProblemDescriptors {

    private static final String [] requiredAnnotations = {
            "RequestMapping",
            "ResponseStatus",
            "ResponseBody",
            "ApiDocs",
            "SecurityPolicy"};

    private final InsertAnnotationFix[] fixes = {
            new InsertAnnotationFix(RequestMapping.class,
                    "( value = \"/\", " +
                            "method = {RequestMethod.POST}, " +
                            "consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE} , " +
                            "produces = {MediaType.APPLICATION_JSON_VALUE} )",
                    insertFirst),
            new InsertAnnotationFix(ResponseStatus.class, "( value = HttpStatus.OK)", insertLast),
            new InsertAnnotationFix(ResponseBody.class, "", insertLast),
            new InsertAnnotationFix(ApiDocs.class, "( value = {PROVIDE DOCUMENTATION DETAILS}, tags = {PROVIDE VALUE HERE})", insertLast),
            new InsertAnnotationFix(SecurityPolicy.class, "(PROFILE_POLICY_VALID_AND_AUTHORIZED)", insertLast)};

    private PsiMethod method;

    public ControllerMethodProblemDescriptors(@NotNull PsiMethod method, @NotNull InspectionManager manager) {
        super(manager);
        this.method = method;
    }

    @NotNull
    @Override
    public PsiAnnotation[] getMissingAnnotations() {
        return getMissingAnnotations(this.method, fixes, requiredAnnotations);
    }

    @NotNull
    public ControllerMethodProblemDescriptors add(@NotNull PsiElement element, @NotNull String message) {
        descriptors.add(manager.createProblemDescriptor(element, message, fixes, HIGHLIGHT_TYPE, false, false));
        return this;
    }

}
