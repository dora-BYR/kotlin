package compatible;

import android.annotation.TargetApi;

@kotlin.annotations.jvm.internal.Compat("compatible.support.CompatibleCompat")
public class AnotherCompatible {
    @TargetApi(100)
    public boolean inAnotherCompatible() { return false; }
}