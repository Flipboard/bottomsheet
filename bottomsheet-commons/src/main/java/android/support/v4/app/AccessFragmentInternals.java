package android.support.v4.app;

public class AccessFragmentInternals {
    private AccessFragmentInternals() {
    }

    public static int getContainerId(Fragment fragment) {
        return fragment.mContainerId;
    }
}
