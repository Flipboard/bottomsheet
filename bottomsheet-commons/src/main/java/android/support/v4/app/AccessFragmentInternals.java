package android.support.v4.app;

public final class AccessFragmentInternals {
    private AccessFragmentInternals() {
        throw new AssertionError("No instances.");
    }

    public static int getContainerId(Fragment fragment) {
        return fragment.mContainerId;
    }
}
