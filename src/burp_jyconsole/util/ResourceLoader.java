package burp_jyconsole.util;

import burp_jyconsole.enums.ScriptLanguage;
import burp_jyconsole.enums.ScriptTypes;

import java.io.IOException;
import java.io.InputStream;

public final class ResourceLoader {

    private static ResourceLoader INSTANCE;
    public ResourceLoader() {
    }

    public static ResourceLoader getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ResourceLoader();
        }

        return INSTANCE;
    }

    private String loadContent(ScriptLanguage scriptLanguage, String resourceType, String resourceName) {
        String extension = scriptLanguage.equals(ScriptLanguage.JYTHON) ? "py" : "js";
        String templateName = String.format("templates/%s/%s/%s.%s", scriptLanguage.name().toLowerCase(),resourceType,resourceName, extension);
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream((templateName));
            return new String(in.readAllBytes());
        } catch (IOException e) {
            Logger.log("ERROR", String.format("Exception loading template [%s]: %s", templateName, e.getMessage()));
        }
        return null;
    }

    public String getEditorTemplate(ScriptTypes scriptType, ScriptLanguage scriptLanguage) {
        return loadContent(scriptLanguage,"stub",scriptType.name().toLowerCase());
    }

    public String getExecutionScript(ScriptTypes scriptType, ScriptLanguage scriptLanguage, String scriptContent) {
        return loadContent(scriptLanguage,"launcher",scriptType.name().toLowerCase()).replace("__SCRIPT__",scriptContent);
    }
}
