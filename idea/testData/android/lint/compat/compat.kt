
// INSPECTION_CLASS: org.jetbrains.android.inspections.klint.AndroidLintInspectionToolProvider$AndroidKLintNewApiInspection
// INSPECTION_CLASS2: org.jetbrains.android.inspections.klint.AndroidLintInspectionToolProvider$AndroidKLintInlinedApiInspection
// INSPECTION_CLASS3: org.jetbrains.android.inspections.klint.AndroidLintInspectionToolProvider$AndroidKLintOverrideInspection
// DEPENDENCY: Compat.java -> kotlin/android/Compat.java
// DEPENDENCY: Compatible.java -> compatible/Compatible.java
// DEPENDENCY: SubCompatible.java -> compatible/SubCompatible.java
// DEPENDENCY: AnotherCompatible.java -> compatible/AnotherCompatible.java
// DEPENDENCY: CompatibleCompat.java -> compatible/support/CompatibleCompat.java
// DEPENDENCY: SubCompatibleCompat.java -> compatible/support/SubCompatibleCompat.java

import compatible.Compatible
import compatible.SubCompatible
import compatible.AnotherCompatible

class SubCompatible2: Compatible() {
    fun implicitThis() {
        noArgs()
    }
}

fun test() {
    val c = Compatible()
    val sc = SubCompatible()
    val ac = AnotherCompatible()
    c.noArgs()
    c.<error descr="Call requires API level 100 (current min is 1): compatible.Compatible#notStaticImCompat">notStaticImCompat</error>()
    sc.subtype()
    sc.subtypeOverride()
    c.boxing(0)
    c.boxingResult()
    c.vararg(0)
    // This fails for now
    // c.varargBoxing(0)
    c.<error descr="Call requires API level 100 (current min is 1): compatible.Compatible#valueVararg">valueVararg</error>(0)
    sc.<error descr="Call requires API level 100 (current min is 1): compatible.SubCompatible#superInCompat">superInCompat</error>()
    c.samAdapter {}
    ac.inAnotherCompatible()
    c.<error descr="Call requires API level 100 (current min is 1): compatible.Compatible#differentParamType">differentParamType</error>(0)
    c.<error descr="Call requires API level 100 (current min is 1): compatible.Compatible#differentReturnType">differentReturnType</error>()
    c.<error descr="Call requires API level 100 (current min is 1): compatible.Compatible#subtypeParam">subtypeParam</error>("")
    c.multipleParams(0, 0L, .0, "", ' ')
    c.run { noArgs() }
    sc.implicitThisNotReplaced()
}
