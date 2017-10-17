// IGNORE_BACKEND: JS, NATIVE

// WITH_RUNTIME
// FILE: View.java
import kotlin.android.Compat;

@Compat(ViewCompat.class)
public class View {
    public boolean noArgs() { return false; }
    public boolean subtype() { return false; }
    public boolean subtypeOverride() { return false; }
    public boolean boxing(int i) { return false; }
    public boolean boxingResult() { return false; }
    public boolean vararg(int... ii) { return false; }
    public boolean varargBoxing(int... ii) { return false; }
    public boolean valueVararg(int i) { return true; }
    public <T> boolean generic(T t) { return false; }
    public boolean samAdapter(Runnable r) { return false; }
    public boolean differentParamType(int i) { return true; }
    public boolean differentReturnType() { return true; }
    public boolean subtypeParam(String s) { return true; }
    public boolean multipleParams(int i, long l, double d, String s, char c) { return false; }
    public boolean implicitThisInSubtype() { return false; }
    public boolean getProperty() { return false; }
}

// FILE: SubView.java
import kotlin.android.Compat;

@Compat(SubViewCompat.class)
public class SubView extends View {
    @Override public boolean subtypeOverride() { return false; } // todo: do we need to compat this?
    public boolean superInCompat() { return true; }
    public boolean implicitThisNotReplaced() { return !noArgs(); }
}

// FILE: AnotherView.java
import kotlin.android.Compat;

@Compat(ViewCompat.class)
public class AnotherView {
    public boolean inAnotherView() { return false; }
}

// FILE: KtSubView.kt
class KtSubView: View() {
    fun useImplicitThis() = implicitThisInSubtype()
}

// FILE: ViewCompat.java
public class ViewCompat {
    static public boolean noArgs(View v) { return true; }
    static public boolean subtype(View v) { return true; }
    static public boolean subtypeOverride(View v) { return true; }
    static public boolean boxing(View v, Integer i) { return true; }
    static public Boolean boxingResult(View v) { return true; }
    static public boolean vararg(View v, int... ii) { return true; }
    static public boolean varargBoxing(View v, Integer... ii) { return true; }
    static public boolean valueVararg(View v, int... ii) { return false; }
    static public <K> boolean generic(View v, K k) { return true; }
    static public boolean samAdapter(View v, Runnable r) { return true; }
    static public boolean inAnotherView(AnotherView v) { return true; }
    static public boolean differentParamType(View v, long i) { return false; }
    static public int differentReturnType(View v) { return 0; }
    static public boolean subtypeParam(View v, Object s) { return false; }
    static public boolean multipleParams(View v, int i, long l, double d, String s, char c) { return true; }
    static public boolean implicitThisInSubtype(View v) { return true; }
    static public boolean getProperty(View v) { return true; }
}

// FILE: SubViewCompat.java
public class SubViewCompat {
    static boolean superInCompat(View v) { return false; }
}

// FILE: Movable.java
import kotlin.android.Compat;

@Compat(MovableCompat.class)
public interface Movable {
    boolean move();
}

// FILE: MovableImpl.java
public class MovableImpl implements Movable {
    public boolean move() { return false; }
}

// FILE: MovableCompat.java
public class MovableCompat {
    public static boolean move(Movable m) { return true; }
}

// FILE: test.kt
fun box(): String {
    if (!View().noArgs()) return "FAIL noArgs"
    if (!SubView().subtype()) return "FAIL subtype"
    if (!SubView().subtypeOverride()) return "FAIL subtypeOverride"
    if (!View().boxing(0)) return "FAIL boxing"
    if (!View().boxingResult()) return "FAIL boxingResult"
    if (!View().vararg(0)) return "FAIL vararg"
//    if (!View().varargBoxing(0)) return "FAIL varargBoxing"
    if (!View().valueVararg(0)) return "FAIL valueVararg"
//    if (!View().generic(0)) return "FAIL generic"
    if (!SubView().superInCompat()) return "FAIL superInCompat"
    if (!View().samAdapter {}) return "FAIL samAdapter"
    if (!MovableImpl().move()) return "FAIL move"
    if (!AnotherView().inAnotherView()) return "FAIL inAnotherView"
    if (!View().differentParamType(0)) return "FAIL differentParamType"
    if (!View().differentReturnType()) return "FAIL differentReturnType"
    if (!View().subtypeParam("")) return "FAIL subtypeParam"
    if (!View().multipleParams(0, 0L, .0, "", ' ')) return "FAIL subtypeParam"
    if (!View().run { noArgs() }) return "FAIL run { noArgs() }"
    if (!KtSubView().useImplicitThis()) return "FAIL useImplicitThis"
    if (!SubView().implicitThisNotReplaced()) return "FAIL implicitThisNotReplaced"
//    if (!View().property) return "FAIL property"
    return "OK"
}
