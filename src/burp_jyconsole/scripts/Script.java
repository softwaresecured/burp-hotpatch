package burp_jyconsole.scripts;

import burp_jyconsole.enums.ScriptLanguage;
import burp_jyconsole.enums.ScriptTypes;

public class Script {
    public String id = null;
    public String name = "";
    public String content = "";
    public ScriptTypes scriptType;
    public ScriptLanguage scriptLanguage;
    public boolean enabled = true;

    public Script() {

    }

    public Script(String id, String name, String content, ScriptTypes scriptType, ScriptLanguage scriptLanguage, boolean isEnabled) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.scriptType = scriptType;
        this.scriptLanguage = scriptLanguage;
        this.enabled = isEnabled;
    }

    public Script(String name, String content, ScriptTypes scriptType, ScriptLanguage scriptLanguage) {
        this.name = name;
        this.content = content;
        this.scriptType = scriptType;
        this.scriptLanguage = scriptLanguage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ScriptTypes getScriptType() {
        return scriptType;
    }

    public void setScriptType(ScriptTypes scriptType) {
        this.scriptType = scriptType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ScriptLanguage getScriptLanguage() {
        return scriptLanguage;
    }

    public void setScriptLanguage(ScriptLanguage scriptLanguage) {
        this.scriptLanguage = scriptLanguage;
    }

    @Override
    public String toString() {
        return "Script{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", scriptType=" + scriptType +
                ", scriptLanguage=" + scriptLanguage +
                ", enabled=" + enabled +
                '}';
    }
}
