package burp;

public final class VERSION {
    public static final int VERSION_MAJOR = 0;
    public static final int VERSION_MINOR = 0;
    public static final int VERSION_PATCH = 1;
    public static final String RELEASE_TYPE = "alpha";

    public static String getVersionStr() {
        return String.format("%d.%d.%d-%s", VERSION_MAJOR, VERSION_MINOR, VERSION_PATCH, RELEASE_TYPE);
    }
}
