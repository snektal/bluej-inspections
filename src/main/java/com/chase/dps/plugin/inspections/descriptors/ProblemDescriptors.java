package com.chase.dps.plugin.inspections.descriptors;

import com.chase.dps.plugin.inspections.fixes.InsertAnnotationFix;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class ProblemDescriptors {

    @NotNull
    static final ProblemHighlightType HIGHLIGHT_TYPE = ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

    @SuppressWarnings("PublicField")
    boolean insertFirst = true;
    boolean insertLast = false;

    @NotNull
    final InspectionManager manager;

    @NotNull
    public Collection<ProblemDescriptor> getDescriptors() {
        return descriptors;
    }

    @NotNull
    final Collection<ProblemDescriptor> descriptors = new ArrayList<>();

    ProblemDescriptors(@NotNull InspectionManager manager) {
        this.manager = manager;
    }

    @NotNull
    public abstract ProblemDescriptors add(@NotNull PsiElement element, @NotNull String message) ;

    @NotNull
    public abstract PsiAnnotation[] getMissingAnnotations() ;

    @NotNull
    PsiAnnotation[] getMissingAnnotations(@NotNull PsiModifierListOwner element, InsertAnnotationFix[] fixes, String[] requiredAnnotations)  {
        PsiAnnotation[] annotations = element.getAnnotations();
        // get names of annotations that missing
        List<String> existing = Arrays.stream(annotations)
                .map(a-> a.getQualifiedName().substring(Objects.requireNonNull(a.getQualifiedName()).lastIndexOf('.') + 1))
                .collect(Collectors.toList());

        List<PsiAnnotation> list = new ArrayList<>();
        Arrays.stream(requiredAnnotations).filter(name-> !existing.contains(name)).forEach(name-> {
            for (InsertAnnotationFix fix : fixes) {
                if (fix.getAnnotationClassName().equals(name)) {
                    PsiAnnotation annotation = JavaPsiFacade
                            .getInstance(manager.getProject())
                            .getElementFactory()
                            .createAnnotationFromText(fix.getFamilyName(), element);
                    list.add(annotation);
                }
            }
        });
        return list.toArray(new PsiAnnotation[fixes.length]);
    }

    @Nullable
    public ProblemDescriptor[] toArray() {
        return descriptors.isEmpty() ? null : descriptors.toArray(new ProblemDescriptor[descriptors.size()]);
    }
}
