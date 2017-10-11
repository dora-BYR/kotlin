
// INSPECTION_CLASS: org.jetbrains.android.inspections.klint.AndroidLintInspectionToolProvider$AndroidKLintNewApiInspection
// INSPECTION_CLASS2: org.jetbrains.android.inspections.klint.AndroidLintInspectionToolProvider$AndroidKLintInlinedApiInspection
// INSPECTION_CLASS3: org.jetbrains.android.inspections.klint.AndroidLintInspectionToolProvider$AndroidKLintOverrideInspection
// DEPENDENCY: Compat.java -> kotlin/android/Compat.java
// DEPENDENCY: Compatible.java -> compatible/Compatible.java
// DEPENDENCY: CompatibleCompat.java -> compatible/support/CompatibleCompat.java

import compatible.Compatible
import compatible.support.CompatibleCompat

class SubCompatible: Compatible() {
    fun implicitThis() {
        noArgs()
    }
}

fun test() {
    // OK
    Compatible().noArgs()
    Compatible().<error descr="Call requires API level 100 (current min is 1): compatible.Compatible#shouldFail">shouldFail</error>()
}
