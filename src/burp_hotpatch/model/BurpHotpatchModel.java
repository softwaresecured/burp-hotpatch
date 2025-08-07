package burp_hotpatch.model;

import burp_hotpatch.config.AbstractConfig;
import burp_hotpatch.enums.*;
import burp_hotpatch.event.model.BurpHotpatchModelEvent;
import burp_hotpatch.mvc.AbstractModel;
import burp_hotpatch.scripts.HotpatchScript;
import burp_hotpatch.scripts.ScriptExecutionContainer;
import burp_hotpatch.scripts.ScriptExport;
import burp_hotpatch.threads.ScriptExecutionThread;
import burp_hotpatch.util.Logger;
import burp_hotpatch.util.UIUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.swing.table.DefaultTableModel;
import java.util.*;

public class BurpHotpatchModel extends AbstractModel<BurpHotpatchModelEvent> {
    private EditorState editorState = EditorState.INITIAL;
    private HotpatchScript currentHotpatchScript = null;
    private HashMap<String,StringBuilder> stdout = new HashMap<String,StringBuilder>();
    private HashMap<String,StringBuilder> stderr = new HashMap<String,StringBuilder>();
    private ArrayList<HotpatchScript> hotpatchScripts = new ArrayList<HotpatchScript>();
    private OutputType selectedOutputType = OutputType.STDOUT;
    private DefaultTableModel scriptSelectionModel;
    private DefaultTableModel runningTasksModel;
    private int currentSelectedIdx = -1;
    private String lastSelectedScriptId = null;
    // Updates available
    private String updateAvailableMessage = null;

    // Thread pool for execution containers
    private ArrayList<ScriptExecutionThread> threadPool = new ArrayList<ScriptExecutionThread>();
    private String currentTaskId = null;

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

        this.runningTasksModel = new DefaultTableModel();
        for (String col : new String[] {
                "ID",
                "Name",
                }
        ) {
            this.runningTasksModel.addColumn(col);
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
        if ( scriptsExport.hotpatchScripts != null ) {
            for (HotpatchScript hotpatchScript : scriptsExport.hotpatchScripts) {
                Logger.log("INFO", String.format("Importing script %s", hotpatchScript.getName()));
                hotpatchScript.setId(null);
                hotpatchScript.setName(getDeDuplicatedScriptName(hotpatchScript.getName()));
                saveScript(hotpatchScript);
            }
        }
    }

    public ScriptExport exportScripts() {
        ScriptExport export = new ScriptExport();
        export.hotpatchScripts = new HotpatchScript[hotpatchScripts.size()];
        export.hotpatchScripts = (HotpatchScript[]) hotpatchScripts.toArray(export.hotpatchScripts);
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

    public void saveScript(HotpatchScript hotpatchScript) {
        // Add if new
        if ( hotpatchScript.getId() == null ) {
            if ( getScriptByName(hotpatchScript.getName()) != null ) {
                setLastError(String.format("Script with name %s already exists", hotpatchScript.getName()));
                return;
            }
            else {
                hotpatchScript.setId(UUID.randomUUID().toString());
                hotpatchScripts.add(hotpatchScript);
            }
        }
        // Update if existing
        else {
            // Check that name is not already in use
            HotpatchScript updateTarget = getScriptById(hotpatchScript.getId());
            HotpatchScript nameCheck = getScriptByName(hotpatchScript.getName());
            if ( nameCheck != null ) {
                if ( nameCheck.getId() != updateTarget.getId()) {
                    setLastError(String.format("Script with name %s already exists", hotpatchScript.getName()));
                    return;
                }
            }
            updateTarget = getScriptById(hotpatchScript.getId());
            updateTarget.setEnabled(hotpatchScript.isEnabled());
            updateTarget.setName(hotpatchScript.getName());
            updateTarget.setContent(hotpatchScript.getContent());
            updateTarget.setExecutionOrder(hotpatchScript.getExecutionOrder());
        }
        Collections.sort(hotpatchScripts, new Comparator<HotpatchScript>() {
            @Override
            public int compare(HotpatchScript s1, HotpatchScript s2) {
                return Integer.compare(s1.getExecutionOrder(), s2.getExecutionOrder());
            }
        });
        updateScriptsTableModel(hotpatchScript);
        clearStderr(hotpatchScript.getId());
        clearStdout(hotpatchScript.getId());
        emit(BurpHotpatchModelEvent.SCRIPT_SAVED, null, hotpatchScript.getId());
    }

    public void deleteScript( String id ) {
        for (int i = 0; i < hotpatchScripts.size(); i++ ) {
            if ( hotpatchScripts.get(i).getId().equals(id)) {
                hotpatchScripts.remove(i);
                clearStderr(id);
                clearStdout(id);
                emit(BurpHotpatchModelEvent.SCRIPT_DELETED, null, id);
                break;
            }
        }
    }

    public void loadScriptByName( String name ) {
        HotpatchScript hotpatchScript = getScriptByName(name);
        if ( hotpatchScript != null ) {
            setCurrentScript(hotpatchScript);
        }
    }

    public void loadScriptById( String id ) {
        if ( id != null ) {
            HotpatchScript hotpatchScript = getScriptById(id);
            if ( hotpatchScript != null ) {
                setCurrentScript(getCopy(hotpatchScript));
            }
        }
    }

    public HotpatchScript getScriptByName(String name ) {
        for ( HotpatchScript hotpatchScript : hotpatchScripts) {
            if ( hotpatchScript.getName().equalsIgnoreCase(name) ) {
                return getCopy(hotpatchScript);
            }
        }
        return null;
    }

    public HotpatchScript getCopy(HotpatchScript hotpatchScript)  {
        HotpatchScript clone = new HotpatchScript(
                hotpatchScript.getId(),
                hotpatchScript.getName(),
                hotpatchScript.getContent(),
                hotpatchScript.getScriptType(),
                hotpatchScript.getScriptLanguage(),
                hotpatchScript.isEnabled(),
                hotpatchScript.getExecutionOrder()
        );
        return clone;
    }

    public HotpatchScript getScriptById(String id ) {
        for ( HotpatchScript hotpatchScript : hotpatchScripts) {
            if ( hotpatchScript.getId().equalsIgnoreCase(id) ) {
                return hotpatchScript;
            }
        }
        return null;
    }

    public HotpatchScript getCurrentScript() {
        return currentHotpatchScript;
    }

    public void setCurrentScript(HotpatchScript currentHotpatchScript) {
        var old = this.currentHotpatchScript;
        this.currentHotpatchScript = currentHotpatchScript;
        emit(BurpHotpatchModelEvent.CURRENT_SCRIPT_SET, old, currentHotpatchScript);
    }

    public String getStdout(String id) {
        if ( stdout.get(id) != null && !stdout.get(id).isEmpty() ) {
            return stdout.get(id).toString();
        }
        return null;
    }

    public void setStdout(String id, String text) {
        if ( text != null && !text.isBlank()) {
            if ( stdout.get(id) == null ) {
                stdout.put(id, new StringBuilder());
            }
            stdout.get(id).append(text);
            emit(BurpHotpatchModelEvent.STDOUT_SET, null, stdout.get(id).toString());
        }
    }

    public String getStderr(String id) {
        if ( stderr.get(id) != null && !stderr.get(id).isEmpty() ) {
            return stderr.get(id).toString();
        }
        return null;
    }

    public void setStderr(String id, String text) {
        if ( text != null && !text.isBlank()) {
            if ( stderr.get(id) == null ) {
                stderr.put(id, new StringBuilder());
            }
            stderr.get(id).append(text);
            updateScriptsTableModelErrorState(id, true );
            emit(BurpHotpatchModelEvent.STDERR_SET, null, stderr.get(id).toString());
        }
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

    public ArrayList<HotpatchScript> getScripts() {
        return hotpatchScripts;
    }

    public void setScripts(ArrayList<HotpatchScript> hotpatchScripts) {
        var old = this.hotpatchScripts;
        this.hotpatchScripts = hotpatchScripts;
        emit(BurpHotpatchModelEvent.SCRIPTS_SET, old, hotpatchScripts);
    }

    public OutputType getSelectedOutputType() {
        return selectedOutputType;
    }

    public void setSelectedOutputType(OutputType selectedOutputType) {
        this.selectedOutputType = selectedOutputType;
        emit(BurpHotpatchModelEvent.OUTPUT_FORMAT_SET, null, selectedOutputType);
    }

    public void addThread( ScriptExecutionThread thread ) {
        threadPool.add(thread);
        emit(BurpHotpatchModelEvent.EXECUTION_THREAD_ADDED, null, thread.getScriptExecutionContainer().getId());
    }

    public void removeThread ( String id ) {
        for ( int i = 0; i < threadPool.size(); i++ ) {
            if ( threadPool.get(i).getScriptExecutionContainer().getId().equals(id)) {
                threadPool.remove(i);
                emit(BurpHotpatchModelEvent.EXECUTION_THREAD_REMOVED, null, id);
                break;
            }
        }
    }

    public ArrayList<ScriptExecutionThread> getThreadPool() {
        return threadPool;
    }

    public ScriptExecutionThread getExecutingThreadById( String id ) {
        for ( int i = 0; i < threadPool.size(); i++ ) {
            if (threadPool.get(i).getScriptExecutionContainer().getId().equals(id)) {
                return threadPool.get(i);
            }
        }
        return null;
    }

    public void setLastError(String error) {
        emit(BurpHotpatchModelEvent.CURRENT_SCRIPT_SAVE_ERROR, null, error);
    }

    public DefaultTableModel getScriptSelectionModel() {
        return scriptSelectionModel;
    }

    public DefaultTableModel getRunningTasksModel() {
        return runningTasksModel;
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
        if ( currentHotpatchScript != null ) {
            return currentHotpatchScript.getId();
        }
        return null;
    }

    public void setLastSelectedScriptId(String lastSelectedScriptId) {
        this.lastSelectedScriptId = lastSelectedScriptId;
    }

    public void updateScriptsTableModel(HotpatchScript hotpatchScript) {
        int idx = UIUtil.getTableRowIndexById(scriptSelectionModel, hotpatchScript.getId());
        // Update
        if ( idx >= 0 ) {
            scriptSelectionModel.setValueAt(hotpatchScript.getId(),idx,0);
            scriptSelectionModel.setValueAt(getStderr(hotpatchScript.getId()) != null,idx,1);
            scriptSelectionModel.setValueAt(hotpatchScript.isEnabled(),idx,2);
            scriptSelectionModel.setValueAt(ScriptTypes.getCategory(hotpatchScript.getScriptType()),idx,3);
            scriptSelectionModel.setValueAt(hotpatchScript.getName(),idx,4);
        }
        // Add
        else {
            scriptSelectionModel.insertRow(scriptSelectionModel.getRowCount(),new Object[] {
                    hotpatchScript.getId(),
                    false,
                    hotpatchScript.isEnabled(),
                    ScriptTypes.getCategory(hotpatchScript.getScriptType()),
                    hotpatchScript.getName()
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

    public void addRunningTask( ScriptExecutionContainer scriptExecutionContainer ) {
        Logger.log("DEBUG", String.format("Adding running task %s", scriptExecutionContainer.getId()));
        runningTasksModel.insertRow(runningTasksModel.getRowCount(),new Object[] {
                scriptExecutionContainer.getId(),
                String.format(
                        "%s/%s (%s)",
                        ScriptTypes.getCategory(scriptExecutionContainer.getScript().getScriptType()),
                        scriptExecutionContainer.getScript().getName(),
                        ScriptLanguage.toFriendlyName(scriptExecutionContainer.getScript().getScriptLanguage())
                )
        });
        emit(BurpHotpatchModelEvent.RUNNING_TASK_ADDED, null, scriptExecutionContainer.getId());
    }

    public void removeRunningTask( String taskId ) {
        Logger.log("DEBUG", String.format("Removing running task %s", taskId));
        for ( int i = 0; i < runningTasksModel.getRowCount(); i++ ) {
            String curId = (String)runningTasksModel.getValueAt(i,0);
            if ( curId.equals(taskId)) {
                runningTasksModel.removeRow(i);
                break;
            }
        }
        emit(BurpHotpatchModelEvent.RUNNING_TASK_REMOVED, null, taskId);
    }

    public void terminateCurrentTask() {
        if ( currentTaskId != null ) {
            terminateRunningTask(currentTaskId);
        }
    }
    public void terminateRunningTask ( String id ) {
        for ( ScriptExecutionThread scriptExecutionThread : threadPool ) {
            if ( scriptExecutionThread.getScriptExecutionContainer().getId().equals(id)) {
                scriptExecutionThread.setTerminationReason("Terminated by user");
                scriptExecutionThread.terminate();
            }
        }
    }

    public String getCurrentTaskId() {
        return currentTaskId;
    }

    public void setCurrentTaskId(String currentTaskId) {
        var old = this.currentTaskId;
        this.currentTaskId = currentTaskId;
        emit(BurpHotpatchModelEvent.CURRENT_TASK_SET, old, currentTaskId);
    }

    public void toggleCurrentScript(String scriptId) {
        if ( currentHotpatchScript != null && currentHotpatchScript.getId() != null ) {
            if ( currentHotpatchScript.getId().equals(scriptId)) {
                currentHotpatchScript.setEnabled(!currentHotpatchScript.isEnabled());
                saveScript(getCurrentScript());
                Logger.log("INFO", String.format("(Model) Toggling script %s", scriptId));
                emit(BurpHotpatchModelEvent.CURRENT_SCRIPT_TOGGLED, null,currentHotpatchScript.isEnabled());
            }
        }
    }
}
