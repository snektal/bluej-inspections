package com.chase.dps.plugin.inspections;

import com.chase.digital.bluej.annotations.apidocs.ApiDocs;
import com.chase.digital.bluej.annotations.security.SecurityPolicy;
import com.chase.digital.bluej.annotations.stereotype.BlueJController;
import com.chase.dps.plugin.inspections.descriptors.ControllerClassProblemDescriptors;
import com.chase.dps.plugin.inspections.descriptors.ControllerMethodProblemDescriptors;
import com.chase.dps.plugin.inspections.descriptors.ProblemDescriptors;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ui.SingleCheckboxOptionsPanel;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

public class ControllerInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final ProblemDescriptor[] NO_DESCRIPTORS = {};

    @Override
    @NotNull
    public JComponent createOptionsPanel() {
        return new SingleCheckboxOptionsPanel("Place annotation as first modifier", this, "insertFirst");
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @Override
    @Nls
    @NotNull
    public String getDisplayName() {
        return "Controller Inspection";
    }

    @Override
    @NonNls
    @NotNull
    public String getShortName() {
        return "ControllerInspection";
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!aClass.isInterface()) {
            return NO_DESCRIPTORS;
        }
        if (aClass.getName() != null &&
                !aClass.getName().contains("Controller")) {
            return NO_DESCRIPTORS;
        }

        final ProblemDescriptors descriptors = new ControllerClassProblemDescriptors(aClass, manager);

        checkClassMissingAnnotations(aClass, descriptors);
        return descriptors.toArray();

    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        PsiClass containingClass = method.getContainingClass();
        if (containingClass == null)
            return NO_DESCRIPTORS;

        if (!containingClass.isInterface())
            return NO_DESCRIPTORS;

        final ProblemDescriptors descriptors = new ControllerMethodProblemDescriptors(method, manager);
        Boolean hasControllerAnnotation = containingClass.hasAnnotation("com.chase.digital.bluej.annotations.stereotype.BlueJController");
        if (!hasControllerAnnotation) {
            checkInvalidAnnotations(method, descriptors, hasControllerAnnotation);
        }
        else {
            checkMissingAnnotations(method, descriptors);
        }

        return descriptors.toArray();
    }

    private void checkClassMissingAnnotations(@NotNull PsiClass aClass, ProblemDescriptors descriptors) {
        for (PsiAnnotation annotation : descriptors.getMissingAnnotations()) {
            if (annotation != null)
                ClassProblem.checkFor(aClass, annotation)
                        .ifPresent(problem -> descriptors.add(aClass, annotation, problem.message));

        }
    }

    private void checkMissingAnnotations(@NotNull PsiMethod method, ProblemDescriptors descriptors) {
        for (PsiAnnotation annotation : descriptors.getMissingAnnotations()) {
            if (annotation != null)
                MethodProblem.checkFor(method, annotation)
                        .ifPresent(problem -> descriptors.add(method, annotation, problem.message));
        }
    }

    private void checkInvalidAnnotations(@NotNull PsiMethod method, ProblemDescriptors descriptors, Boolean hasControllerAnnotation) {
        for (PsiAnnotation annotation : descriptors.getExistingAnnotations()) {
            if (annotation != null)
                InvalidAnnotationProblem.checkFor(method, annotation, hasControllerAnnotation)
                        .ifPresent(problem -> descriptors.add(method, annotation, problem.message));
        }
    }

    private enum MethodProblem implements BiPredicate<PsiModifierListOwner, PsiAnnotation> {

        METHOD_IS_MISSING_REQUESTMAPPING_ANNOTATION("Method is missing @RequestMapping annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return owner.missing(RequestMapping.class) && Objects.requireNonNull(annotation.getQualifiedName()).contains("RequestMapping");
            }
        },

        METHOD_IS_MISSING_RESPONSESTATUS_ANNOTATION("Method is missing @ResponseStatus annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return owner.missing(ResponseStatus.class) && Objects.requireNonNull(annotation.getQualifiedName()).contains("ResponseStatus");
            }
        },
        METHOD_IS_MISSING_RESPONSEBODY_ANNOTATION("Method is missing @ResponseBody annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return owner.missing(ResponseBody.class) && Objects.requireNonNull(annotation.getQualifiedName()).contains("ResponseBody");
            }
        },
        METHOD_IS_MISSING_APIDOCS_ANNOTATION("Method is missing @ApiDocs annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return owner.missing(ApiDocs.class) && Objects.requireNonNull(annotation.getQualifiedName()).contains("ApiDocs");
            }
        },
        METHOD_IS_MISSING_SECURITYPOLICY_ANNOTATION("Method is missing @SecurityPolicy annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return owner.missing(SecurityPolicy.class) && Objects.requireNonNull(annotation.getQualifiedName()).contains("SecurityPolicy");
            }
        };

        @NotNull
        private final String message;

        @NotNull
        static Optional<MethodProblem> checkFor(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {
            return Arrays.stream(MethodProblem.values()).filter(problem -> problem.test(member, annotation)).findFirst();
        }

        MethodProblem(@NotNull String message) {
            this.message = message;
        }
    }

    private enum InvalidAnnotationProblem implements TriPredicate< PsiModifierListOwner, PsiAnnotation, Boolean > {

        METHOD_HAS_INVALID_ANNOTATION() {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation, Boolean hasControllerAnnotation ) {
                if (!hasControllerAnnotation.booleanValue() )
                {
                    this.setMessage("Method has invalid annotation - " + annotation.getQualifiedName().substring(annotation.getQualifiedName().lastIndexOf('.') + 1));
                    return true;
                }
                return false;
            }
        };

        private String message;


        void setMessage(@NotNull String message) {
            this.message = message;
        }

        @NotNull
        static Optional<InvalidAnnotationProblem> checkFor(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation, boolean hasControllerAnnotation) {
            return Arrays.stream(InvalidAnnotationProblem.values()).filter(problem -> problem.test( member, annotation, hasControllerAnnotation)).findFirst();
        }

        InvalidAnnotationProblem() {
        }
    }

    private enum ClassProblem implements BiPredicate<PsiModifierListOwner, PsiAnnotation> {

        CLASS_IS_MISSING_BLUEJCONTROLLER_ANNOTATION("Class is missing @BlueJController annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return owner.missing(BlueJController.class) && Objects.requireNonNull(annotation.getQualifiedName()).contains("BlueJController");
            }
        },


        CLASS_IS_MISSING_REQUESTMAPPING_ANNOTATION("Class is missing @RequestMapping annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {
                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                if (!owner.missing(BlueJController.class))
                    return owner.missing(RequestMapping.class) && Objects.requireNonNull(annotation.getQualifiedName()).contains("RequestMapping");
                return false;
            }
        },

        CLASS_IS_MISSING_APIDOCS_ANNOTATION("Class is missing @ApiDocs annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                if (!owner.missing(BlueJController.class))
                    return owner.missing(ApiDocs.class) && Objects.requireNonNull(annotation.getQualifiedName()).contains("ApiDocs");
                return false;
            }
        };

        @NotNull
        private final String message;

        @NotNull
        static Optional<ClassProblem> checkFor(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {
            return Arrays.stream(ClassProblem.values()).filter(problem -> problem.test(member, annotation)).findFirst();
        }

        ClassProblem(@NotNull String message) {
            this.message = message;
        }
    }

}