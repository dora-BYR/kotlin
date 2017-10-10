
// INSPECTION_CLASS: org.jetbrains.android.inspections.klint.AndroidLintInspectionToolProvider$AndroidKLintNewApiInspection
// INSPECTION_CLASS2: org.jetbrains.android.inspections.klint.AndroidLintInspectionToolProvider$AndroidKLintInlinedApiInspection
// INSPECTION_CLASS3: org.jetbrains.android.inspections.klint.AndroidLintInspectionToolProvider$AndroidKLintOverrideInspection
// DEPENDENCY: Compat.java -> kotlin/android/Compat.java
// DEPENDENCY: Compatible.java -> compatible/Compatible.java
// DEPENDENCY: CompatibleCompat.java -> compatible/support/CompatibleCompat.java

import compatible.Compatible
import compatible.support.CompatibleCompat

fun test() {
    // OK
    Compatible().noArgs()
}
