package burp_hotpatch.enums;

public enum ScriptLanguage {
    PYTHON,
    JAVASCRIPT;

    public static String toFriendlyName( ScriptLanguage scriptLanguage ) {
        switch (scriptLanguage) {
            case PYTHON:
                return "Python";
            case JAVASCRIPT:
                return "JavaScript";
        }
        return null;
    }

    public static ScriptLanguage fromFriendlyName( String name ) {
        switch (name) {
            case "Python":
                return PYTHON;
            case "JavaScript":
                return JAVASCRIPT;
        }
        return null;
    }
}
