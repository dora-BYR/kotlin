package compatible;

import android.annotation.TargetApi;

@kotlin.annotations.jvm.internal.Compat("compatible.support.CompatibleCompat")
public class Compatible {
    @TargetApi(100)
    public void noArgs() {}
    @TargetApi(100)
    public void notStaticImCompat() {}
    @TargetApi(100)
    public boolean subtype() { return false; }
    @TargetApi(100)
    public boolean subtypeOverride() { return false; }
    @TargetApi(100)
    public boolean boxing(int i) { return false; }
    @TargetApi(100)
    public boolean boxingResult() { return false; }
    @TargetApi(100)
    public boolean vararg(int... ii) { return false; }
    @TargetApi(100)
    public boolean varargBoxing(int... ii) { return false; }
    @TargetApi(100)
    public boolean valueVararg(int i) { return true; }
    @TargetApi(100)
    public <T> boolean generic(T t) { return false; }
    @TargetApi(100)
    public boolean samAdapter(Runnable r) { return false; }
    @TargetApi(100)
    public boolean differentParamType(int i) { return true; }
    @TargetApi(100)
    public boolean differentReturnType() { return true; }
    @TargetApi(100)
    public boolean subtypeParam(String s) { return true; }
    @TargetApi(100)
    public boolean multipleParams(int i, long l, double d, String s, char c) { return false; }
    @TargetApi(100)
    public boolean implicitThisInSubtype() { return false; }
}
