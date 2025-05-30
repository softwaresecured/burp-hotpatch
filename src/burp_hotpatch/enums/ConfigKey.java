package burp_hotpatch.enums;

public enum ConfigKey {
    SCRIPTS;

    public static final String KEY_PREFIX = "BurpHotpatch";

    public String resolve() {
        return KEY_PREFIX + name();
    }
}
