package com.chase.dps.plugin.inspections;

import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Optional;

public interface AnnotationsOwner {

    @SuppressWarnings("StaticMethodNamingConvention")
    @NotNull
    static AnnotationsOwner of(@NotNull PsiModifierListOwner element) {
        return Optional.ofNullable(element.getModifierList()).map(modifiers -> (AnnotationsOwner) new Wrapper(modifiers)).orElse(annotation -> false);
    }

    boolean missing(@NotNull Class<? extends Annotation> annotation);

    final class Wrapper implements AnnotationsOwner {

        @NotNull
        private final PsiModifierList modifiers;

        private Wrapper(@NotNull PsiModifierList modifiers) {
            this.modifiers = modifiers;
        }

        @Override
        public boolean missing(@NotNull Class<? extends Annotation> annotation) {
            return modifiers.findAnnotation(annotation.getCanonicalName()) == null;
        }
    }
}