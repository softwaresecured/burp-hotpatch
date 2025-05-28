package burp_jyconsole.enums;

public enum ScriptLanguage {
    JYTHON,
    JAVASCRIPT;

    public static String toFriendlyName( ScriptLanguage scriptLanguage ) {
        switch (scriptLanguage) {
            case JYTHON:
                return "Jython";
            case JAVASCRIPT:
                return "JavaScript";
        }
        return null;
    }

    public static ScriptLanguage fromFriendlyName( String name ) {
        switch (name) {
            case "Jython":
                return JYTHON;
            case "JavaScript":
                return JAVASCRIPT;
        }
        return null;
    }
}
