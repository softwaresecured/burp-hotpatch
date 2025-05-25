package burp_jyconsole.enums;

public enum ConfigKey {
    SCRIPTS;

    public static final String KEY_PREFIX = "BurpJyconsole";

    public String resolve() {
        return KEY_PREFIX + name();
    }
}
