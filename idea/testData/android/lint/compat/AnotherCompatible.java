package compatible;

import android.annotation.TargetApi;
import compatible.support.CompatibleCompat;
import kotlin.android.Compat;

@Compat(CompatibleCompat.class)
public class AnotherCompatible {
    @TargetApi(100)
    public boolean inAnotherCompatible() { return false; }
}