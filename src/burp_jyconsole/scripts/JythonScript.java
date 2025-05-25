package burp_jyconsole.scripts;

import burp_jyconsole.enums.ScriptTypes;



public class JythonScript {
    public String id = null;
    public String name = "";
    public String content = "";
    public ScriptTypes scriptType;
    public boolean enabled = true;

    public JythonScript() {

    }

    public JythonScript(String id, String name, String content, ScriptTypes scriptType, boolean isEnabled) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.scriptType = scriptType;
        this.enabled = isEnabled;
    }

    public JythonScript(String name, String content, ScriptTypes scriptType) {
        this.name = name;
        this.content = content;
        this.scriptType = scriptType;
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



    @Override
    public String toString() {
        return "JythonScript{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", scriptType=" + scriptType +
                ", enabled=" + enabled +
                '}';
    }
}
