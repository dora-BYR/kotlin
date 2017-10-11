package compatible.support;

import compatible.Compatible;
import compatible.AnotherCompatible;

public class CompatibleCompat {
    static public void noArgs(Compatible c) {}
    public void notStaticImCompat(Compatible c) {}
    static public boolean subtype(Compatible v) { return true; }
    static public boolean subtypeOverride(Compatible v) { return true; }
    static public boolean boxing(Compatible v, Integer i) { return true; }
    static public Boolean boxingResult(Compatible v) { return true; }
    static public boolean vararg(Compatible v, int... ii) { return true; }
    static public boolean varargBoxing(Compatible v, Integer... ii) { return true; }
    static public boolean valueVararg(Compatible v, int... ii) { return false; }
    static public <K> boolean generic(Compatible v, K k) { return true; }
    static public boolean samAdapter(Compatible v, Runnable r) { return true; }
    static public boolean inAnotherCompatible(AnotherCompatible v) { return true; }
    static public boolean differentParamType(Compatible v, long i) { return false; }
    static public int differentReturnType(Compatible v) { return 0; }
    static public boolean subtypeParam(Compatible v, Object s) { return false; }
    static public boolean multipleParams(Compatible v, int i, long l, double d, String s, char c) { return true; }
    static public boolean implicitThisInSubtype(Compatible v) { return true; }
}
