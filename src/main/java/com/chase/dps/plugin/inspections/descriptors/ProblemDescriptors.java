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
    public abstract ProblemDescriptors add(@NotNull PsiElement element, @NotNull PsiAnnotation annotation,  @NotNull String message) ;

    @NotNull
    public abstract PsiAnnotation[] getMissingAnnotations() ;

    @NotNull
    PsiAnnotation[] getMissingAnnotations(@NotNull PsiModifierListOwner element, InsertAnnotationFix[] fixes, String[] requiredAnnotations)  {
        List<String> existing = getExistingAnnotationNames(element);

        List<PsiAnnotation> list = new ArrayList<>();
        for (String name : requiredAnnotations) {
            if (!existing.contains(name)) {
                createPsiAnnotations(element, fixes, list, name);
            }
        }
        return list.toArray(new PsiAnnotation[fixes.length]);
    }

    @NotNull
    public abstract PsiAnnotation[] getExistingAnnotations() ;

    @NotNull
    PsiAnnotation[] getExistingAnnotations(@NotNull PsiModifierListOwner element, InsertAnnotationFix[] fixes, String[] requiredAnnotations)  {
        List<String> existing = getExistingAnnotationNames(element);

        List<PsiAnnotation> list = new ArrayList<>();
        for (String name : requiredAnnotations) {
            if (existing.contains(name)) {
                createPsiAnnotations(element, fixes, list, name);
            }
        }
        return list.toArray(new PsiAnnotation[fixes.length]);
    }

    @NotNull
    private List<String> getExistingAnnotationNames(@NotNull PsiModifierListOwner element) {
        PsiAnnotation[] annotations = element.getAnnotations();
        // get names of annotations that missing
        return Arrays.stream(annotations)
                .map(this::getAnnotationName)
                .collect(Collectors.toList());
    }

    @NotNull
    private String getAnnotationName(PsiAnnotation a) {
        return a.getQualifiedName().substring(Objects.requireNonNull(a.getQualifiedName()).lastIndexOf('.') + 1);
    }

    private void createPsiAnnotations(@NotNull PsiModifierListOwner element, InsertAnnotationFix[] fixes, List<PsiAnnotation> list, String name) {
        for (InsertAnnotationFix fix : fixes) {
            if (fix.getAnnotationClassName().equals(name)) {
                PsiAnnotation annotation = JavaPsiFacade
                        .getInstance(manager.getProject())
                        .getElementFactory()
                        .createAnnotationFromText(fix.getFamilyName(), element);
                list.add(annotation);
            }
        }
    }

    protected InsertAnnotationFix[] getFilteredAnnotationFixes(InsertAnnotationFix[] fixes, @NotNull PsiAnnotation annotation) {
        String annotationName = getAnnotationName(annotation);
        List<InsertAnnotationFix> filteredAnnotationFixes = new ArrayList<>();
        for (InsertAnnotationFix fix : fixes) {
            if (fix.getAnnotationClassName().equals(annotationName)) {

                filteredAnnotationFixes.add(fix);
            }
        }
        return filteredAnnotationFixes.toArray(new InsertAnnotationFix[filteredAnnotationFixes.size()]);
    }

    @Nullable
    public ProblemDescriptor[] toArray() {
        return descriptors.isEmpty() ? null : descriptors.toArray(new ProblemDescriptor[descriptors.size()]);
    }
}
