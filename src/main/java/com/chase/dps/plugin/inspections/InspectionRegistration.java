package com.chase.dps.plugin.inspections;


import com.intellij.codeInspection.InspectionToolProvider;
import org.jetbrains.annotations.NotNull;

public final class InspectionRegistration implements InspectionToolProvider {

    @Override
    @NotNull
    public Class<?>[] getInspectionClasses() {
        return new Class<?>[]{
                ControllerInspection.class
        };
    }
}
