package com.chase.dps.plugin.inspections;

import com.chase.digital.bluej.annotations.apidocs.ApiDocs;
import com.chase.digital.bluej.annotations.security.SecurityPolicy;
import com.chase.digital.bluej.annotations.stereotype.BlueJController;
import com.chase.dps.plugin.inspections.descriptors.ControllerClassProblemDescriptors;
import com.chase.dps.plugin.inspections.descriptors.ControllerMethodProblemDescriptors;
import com.chase.dps.plugin.inspections.descriptors.ProblemDescriptors;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
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
import java.util.Optional;
import java.util.function.BiPredicate;

public class ControllerInspection extends BaseJavaLocalInspectionTool {

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
            return NO_DESCRIPTORS ;
        }

        if (aClass.hasAnnotation("BlueJController")) {
            final ProblemDescriptors descriptors = new ControllerClassProblemDescriptors(aClass, manager, true);
            for (PsiAnnotation annotation : aClass.getAnnotations()) {
                Problem.checkFor(aClass, annotation)
                        .ifPresent(problem -> descriptors.add(aClass, problem.message));

            }

            return descriptors.toArray();
        }
        return NO_DESCRIPTORS;
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        PsiClass containingClass = method.getContainingClass();
        if (containingClass == null)
            return NO_DESCRIPTORS;

        if (!containingClass.isInterface())
            return NO_DESCRIPTORS;


        if (containingClass.getName() != null &&
                !containingClass.getName().contains("Controller")) {
            return NO_DESCRIPTORS;
        }

        final ProblemDescriptors descriptors = new ControllerMethodProblemDescriptors(method, manager);
        PsiAnnotation[] annotations = method.getAnnotations();
        if (annotations.length == 0) {
            annotations = descriptors.getRegisteredAnnotations();

        }
        for (PsiAnnotation annotation : annotations) {
            Problem.checkFor(method, annotation)
                    .ifPresent(problem -> descriptors.add(method, problem.message));
        }

        return descriptors.toArray();
    }

    private enum Problem implements BiPredicate<PsiModifierListOwner, PsiAnnotation> {

        CLASS_IS_MISSING_BLUEJCONTROLLER_ANNOTATION("Class is missing @BlueJController annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return !owner.has(BlueJController.class);
            }
        },


        CLASS_IS_MISSING_REQUESTMAPPING_ANNOTATION("Class is missing @RequestMapping annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return !owner.has(RequestMapping.class);
            }
        },

        CLASS_IS_MISSING_APIDOCS_ANNOTATION("Class is missing @ApiDocs annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return !owner.has(ApiDocs.class);
            }
        },

        METHOD_IS_MISSING_REQUESTMAPPING_ANNOTATION("Method is missing @RequestMapping annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return !owner.has(RequestMapping.class);
            }
        },

        METHOD_IS_MISSING_RESPONSESTATUS_ANNOTATION("Method is missing @ResponseStatus annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return !owner.has(ResponseStatus.class);
            }
        },
        METHOD_IS_MISSING_RESPONSEBODY_ANNOTATION("Method is missing @ResponseBody annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return !owner.has(ResponseBody.class);
            }
        },
        METHOD_IS_MISSING_APIDOCS_ANNOTATION("Method is missing @ApiDocs annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return !owner.has(ApiDocs.class);
            }
        },
        METHOD_IS_MISSING_SECURITYPOLICY_ANNOTATION("Method is missing @SecurityPolicy annotation") {
            @Override
            public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {

                final AnnotationsOwner owner = AnnotationsOwner.of(member);
                return !owner.has(SecurityPolicy.class);
            }
        };

        @NotNull
        private final String message;

        @NotNull
        public static Optional<Problem> checkFor(@NotNull PsiModifierListOwner member, @NotNull PsiAnnotation annotation) {
            return Arrays.stream(Problem.values()).filter(problem -> problem.test(member, annotation)).findFirst();
        }

        Problem(@NotNull String message) {
            this.message = message;
        }
    }

}