package compatible;

import android.annotation.TargetApi;
import compatible.support.CompatibleCompat;
import kotlin.android.Compat;

@Compat(CompatibleCompat.class)
public class Compatible {
    @TargetApi(100)
    public void noArgs() {}
    @TargetApi(100)
    public void shouldFail() {}
}
