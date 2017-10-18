package compatible;

import android.annotation.TargetApi;
import compatible.Compatible;

@kotlin.annotations.jvm.internal.Compat("compatible.support.SubCompatibleCompat")
public class SubCompatible extends Compatible {
    @TargetApi(100)
    @Override public boolean subtypeOverride() { return false; }
    @TargetApi(100)
    public boolean superInCompat() { return true; }
    public boolean implicitThisNotReplaced() { return !noArgs(); }
}

