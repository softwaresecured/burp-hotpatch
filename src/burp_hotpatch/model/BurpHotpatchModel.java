package burp_hotpatch.model;

import burp_hotpatch.config.AbstractConfig;
import burp_hotpatch.enums.ConfigKey;
import burp_hotpatch.enums.EditorState;
import burp_hotpatch.enums.OutputType;
import burp_hotpatch.enums.ScriptTypes;
import burp_hotpatch.event.model.BurpHotpatchModelEvent;
import burp_hotpatch.mvc.AbstractModel;
import burp_hotpatch.scripts.Script;
import burp_hotpatch.scripts.ScriptExport;
import burp_hotpatch.util.Logger;
import burp_hotpatch.util.UIUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.swing.table.DefaultTableModel;
import java.util.*;

public class BurpHotpatchModel extends AbstractModel<BurpHotpatchModelEvent> {
    private EditorState editorState = EditorState.INITIAL;
    private Script currentScript = null;
    private HashMap<String,String> stdout = new HashMap<String,String>();
    private HashMap<String,String> stderr = new HashMap<String,String>();
    private ArrayList<Script> scripts = new ArrayList<Script>();
    private OutputType selectedOutputType = OutputType.STDOUT;
    private DefaultTableModel scriptSelectionModel;
    private int currentSelectedIdx = -1;
    private String lastSelectedScriptId = null;
    // Updates available
    private String updateAvailableMessage = null;

    public BurpHotpatchModel() {
        super();
        this.scriptSelectionModel = new DefaultTableModel() {
            @Override
            public Class getColumnClass(int columnIndex) {
                return columnIndex == 1 || columnIndex == 2 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 2;
            }
        };
        for (String col : new String[] {
                "ID",
                "ERR",
                "⏻",
                "Category",
                "Name"}
        ) {
            this.scriptSelectionModel.addColumn(col);
        }
    }

    @Override
    public void load(AbstractConfig config) {
        try {
            if ( config.getString(ConfigKey.SCRIPTS) != null && config.getString(ConfigKey.SCRIPTS).length() > 0 ) {
                importScriptsFromJSON(config.getString(ConfigKey.SCRIPTS));
            }
        } catch (JsonProcessingException e) {
            Logger.log("ERROR", String.format("Error while importing variables: %s", e.getMessage()));
        }
        emit(BurpHotpatchModelEvent.CONFIG_LOADED, null, null);
    }

    @Override
    public void save(AbstractConfig config) {
        try {
            config.setString(ConfigKey.SCRIPTS, exportScriptsAsJSON());
        } catch (JsonProcessingException e) {
            Logger.log("ERROR", String.format("Error while exporting variables: %s", e.getMessage()));
        }
    }


    public String exportScriptsAsJSON() throws JsonProcessingException {
        ScriptExport exportDataObject = exportScripts();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(exportDataObject);
    }

    public void importScriptsFromJSON(String jsonStr ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ScriptExport scriptsExport = mapper.readValue(new String(jsonStr), ScriptExport.class);
        if ( scriptsExport.scripts != null ) {
            for (Script script : scriptsExport.scripts ) {
                Logger.log("INFO", String.format("Importing script %s", script.getName()));
                script.setId(null);
                script.setName(getDeDuplicatedScriptName(script.getName()));
                saveScript(script);
            }
        }
    }

    public ScriptExport exportScripts() {
        ScriptExport export = new ScriptExport();
        export.scripts = new Script[scripts.size()];
        export.scripts = (Script[]) scripts.toArray(export.scripts);
        return export;
    }

    public EditorState getEditorState() {
        return editorState;
    }

    public void setEditorState(EditorState editorState) {
        var old = this.editorState;
        this.editorState = editorState;
        emit(BurpHotpatchModelEvent.EDITOR_STATE_SET, old, editorState);
    }

    public void saveScript(Script script) {
        // Add if new
        if ( script.getId() == null ) {
            if ( getScriptByName(script.getName()) != null ) {
                setLastError(String.format("Script with name %s already exists", script.getName()));
                return;
            }
            else {
                script.setId(UUID.randomUUID().toString());
                scripts.add(script);
            }
        }
        // Update if existing
        else {
            // Check that name is not already in use
            Script updateTarget = getScriptById(script.getId());
            Script nameCheck = getScriptByName(script.getName());
            if ( nameCheck != null ) {
                if ( nameCheck.getId() != updateTarget.getId()) {
                    setLastError(String.format("Script with name %s already exists", script.getName()));
                    return;
                }
            }
            updateTarget = getScriptById(script.getId());
            updateTarget.setEnabled(script.isEnabled());
            updateTarget.setName(script.getName());
            updateTarget.setContent(script.getContent());
            updateTarget.setExecutionOrder(script.getExecutionOrder());
        }
        Collections.sort(scripts, new Comparator<Script>() {
            @Override
            public int compare(Script s1, Script s2) {
                return Integer.compare(s1.getExecutionOrder(), s2.getExecutionOrder());
            }
        });
        updateScriptsTableModel(script);
        emit(BurpHotpatchModelEvent.SCRIPT_SAVED, null, script.getId());
    }

    public void deleteScript( String id ) {
        for ( int i = 0; i < scripts.size(); i++ ) {
            if ( scripts.get(i).getId().equals(id)) {
                scripts.remove(i);
                clearStderr(id);
                clearStdout(id);
                emit(BurpHotpatchModelEvent.SCRIPT_DELETED, null, id);
                break;
            }
        }
    }

    public void loadScriptByName( String name ) {
        Script script = getScriptByName(name);
        if ( script != null ) {
            setCurrentScript(script);
        }
    }

    public void loadScriptById( String id ) {
        if ( id != null ) {
            Script script = getScriptById(id);
            if ( script != null ) {
                setCurrentScript(getCopy(script));
            }
        }
    }

    public Script getScriptByName(String name ) {
        for ( Script script : scripts ) {
            if ( script.getName().equalsIgnoreCase(name) ) {
                return getCopy(script);
            }
        }
        return null;
    }

    public Script getCopy(Script script)  {
        Script clone = new Script(
                script.getId(),
                script.getName(),
                script.getContent(),
                script.getScriptType(),
                script.getScriptLanguage(),
                script.isEnabled(),
                script.getExecutionOrder()
        );
        return clone;
    }

    public Script getScriptById(String id ) {
        for ( Script script : scripts ) {
            if ( script.getId().equalsIgnoreCase(id) ) {
                return script;
            }
        }
        return null;
    }

    public Script getCurrentScript() {
        return currentScript;
    }

    public void setCurrentScript(Script currentScript) {
        var old = this.currentScript;
        this.currentScript = currentScript;
        emit(BurpHotpatchModelEvent.CURRENT_SCRIPT_SET, old, currentScript);
    }

    public String getStdout(String id) {
        return stdout.get(id);
    }

    public void setStdout(String id, String stdout) {
        var old = this.stdout;
        StringBuilder sb = new StringBuilder();
        sb.append(getStdout(id) != null ? getStdout(id) : "");
        sb.append(stdout);
        this.stdout.put(id,sb.toString());
        emit(BurpHotpatchModelEvent.STDOUT_SET, old, this.stdout.get(id));
    }

    public String getStderr(String id) {
        return stderr.get(id);
    }

    public void setStderr(String id, String stderr) {
        var old = this.stderr;
        StringBuilder sb = new StringBuilder();
        sb.append(getStderr(id) != null ? getStderr(id) : "");
        sb.append(stderr);
        this.stderr.put(id,sb.toString());
        updateScriptsTableModelErrorState(id, !sb.toString().isBlank());
        emit(BurpHotpatchModelEvent.STDERR_SET, old, this.stderr.get(id));
    }

    public void clearStdout( String id ) {
        this.stdout.remove(id);
        emit(BurpHotpatchModelEvent.STDOUT_SET, null, null);
    }

    public void clearStderr( String id ) {
        this.stderr.remove(id);
        updateScriptsTableModelErrorState(id, false );
        emit(BurpHotpatchModelEvent.STDERR_SET, null, null);
    }

    public ArrayList<Script> getScripts() {
        return scripts;
    }

    public void setScripts(ArrayList<Script> scripts) {
        var old = this.scripts;
        this.scripts = scripts;
        emit(BurpHotpatchModelEvent.SCRIPTS_SET, old, scripts);
    }

    public OutputType getSelectedOutputType() {
        return selectedOutputType;
    }

    public void setSelectedOutputType(OutputType selectedOutputType) {
        var old = this.selectedOutputType;
        this.selectedOutputType = selectedOutputType;
        emit(BurpHotpatchModelEvent.OUTPUT_FORMAT_SET, null, selectedOutputType);
    }

    public void setLastError(String error) {
        emit(BurpHotpatchModelEvent.CURRENT_SCRIPT_SAVE_ERROR, null, error);
    }

    public DefaultTableModel getScriptSelectionModel() {
        return scriptSelectionModel;
    }

    public void setScriptTemplateModified() {
        emit(BurpHotpatchModelEvent.SCRIPT_TEMPLATE_MODIFIED, null, null);
    }

    public int getCurrentSelectedIdx() {
        return currentSelectedIdx;
    }

    public void setCurrentSelectedIdx(int currentSelectedIdx) {
        this.currentSelectedIdx = currentSelectedIdx;
    }

    public String getLastSelectedScriptId() {
        return lastSelectedScriptId;
    }

    public String getCurrentScriptId() {
        if ( currentScript != null ) {
            return currentScript.getId();
        }
        return null;
    }

    public void setLastSelectedScriptId(String lastSelectedScriptId) {
        this.lastSelectedScriptId = lastSelectedScriptId;
    }

    public void updateScriptsTableModel(Script script ) {
        int idx = UIUtil.getTableRowIndexById(scriptSelectionModel,script.getId());
        // Update
        if ( idx >= 0 ) {
            scriptSelectionModel.setValueAt(script.getId(),idx,0);
            scriptSelectionModel.setValueAt(getStderr(script.getId()) != null,idx,1);
            scriptSelectionModel.setValueAt(script.isEnabled(),idx,2);
            scriptSelectionModel.setValueAt(ScriptTypes.getCategory(script.getScriptType()),idx,3);
            scriptSelectionModel.setValueAt(script.getName(),idx,4);
        }
        // Add
        else {
            scriptSelectionModel.insertRow(scriptSelectionModel.getRowCount(),new Object[] {
                    script.getId(),
                    false,
                    script.isEnabled(),
                    ScriptTypes.getCategory(script.getScriptType()),
                    script.getName()
            });
        }
    }

    public void updateScriptsTableModelErrorState( String id, boolean hasErrors ) {
        int idx = UIUtil.getTableRowIndexById(scriptSelectionModel, id);
        if ( idx >= 0 ) {
            scriptSelectionModel.setValueAt(hasErrors,idx,1);
            scriptSelectionModel.fireTableDataChanged();
        }
    }

    public void removeFromScriptsTableModel( String id ) {
        int idx = UIUtil.getTableRowIndexById(scriptSelectionModel,id);
        if ( idx >= 0 ) {
            scriptSelectionModel.removeRow(idx);
            scriptSelectionModel.fireTableDataChanged();
        }
    }

    public String getDeDuplicatedScriptName(String baseName) {
        // TODO: Feels.... dangerous.
        int i = 1;
        while ( getScriptByName(baseName) != null ) {
            i++;
            baseName = String.format("Untitled_%d", i);
        }
        return baseName;
    }

    public void setUpdateAvailableMessage(String updateAvailableMessage) {
        var old = this.updateAvailableMessage;
        this.updateAvailableMessage = updateAvailableMessage;
        emit(BurpHotpatchModelEvent.UPDATE_AVAILABLE_MESSAGE_UPDATED, old, updateAvailableMessage);
    }

    public String getUpdateAvailableMessage() {
        return updateAvailableMessage;
    }
}
