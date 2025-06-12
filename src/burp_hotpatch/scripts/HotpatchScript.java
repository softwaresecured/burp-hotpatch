package burp_hotpatch.scripts;

import burp_hotpatch.enums.ScriptLanguage;
import burp_hotpatch.enums.ScriptTypes;

public class HotpatchScript {
    public String id = null;
    public String name = "";
    public String content = "";
    public ScriptTypes scriptType;
    public ScriptLanguage scriptLanguage;
    public int executionOrder = 1;
    public boolean enabled = true;

    public HotpatchScript() {

    }

    public HotpatchScript(String id, String name, String content, ScriptTypes scriptType, ScriptLanguage scriptLanguage, boolean isEnabled, int executionOrder ) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.scriptType = scriptType;
        this.scriptLanguage = scriptLanguage;
        this.enabled = isEnabled;
        this.executionOrder = executionOrder;
    }

    public HotpatchScript(String name, String content, ScriptTypes scriptType, ScriptLanguage scriptLanguage) {
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

    public int getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(int executionOrder) {
        this.executionOrder = executionOrder;
    }
}
