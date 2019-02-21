package com.chase.dps.plugin.inspections.descriptors;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ProblemDescriptors {

    @NotNull
    static final ProblemHighlightType HIGHLIGHT_TYPE = ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

    @SuppressWarnings("PublicField")
    boolean insertFirst = true;

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
    public abstract PsiAnnotation[] getRegisteredAnnotations() ;

    @Nullable
    public ProblemDescriptor[] toArray() {
        return descriptors.isEmpty() ? null : descriptors.toArray(new ProblemDescriptor[descriptors.size()]);
    }
}
