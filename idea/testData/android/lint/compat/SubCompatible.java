package compatible;

import android.annotation.TargetApi;
import compatible.support.SubCompatibleCompat;
import compatible.Compatible;
import kotlin.android.Compat;

@Compat(SubCompatibleCompat.class)
public class SubCompatible extends Compatible {
    @TargetApi(100)
    @Override public boolean subtypeOverride() { return false; }
    @TargetApi(100)
    public boolean superInCompat() { return true; }
    public boolean implicitThisNotReplaced() { return !noArgs(); }
}

