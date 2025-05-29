package burp_hotpatch.view;

import burp_hotpatch.util.ResourceLoader;
import burp_hotpatch.enums.EditorState;
import burp_hotpatch.enums.OutputType;
import burp_hotpatch.enums.ScriptLanguage;
import burp_hotpatch.enums.ScriptTypes;
import burp_hotpatch.event.controller.BurpHotpatchControllerEvent;
import burp_hotpatch.event.model.BurpHotpatchModelEvent;
import burp_hotpatch.model.BurpHotpatchModel;
import burp_hotpatch.mvc.AbstractView;
import burp_hotpatch.scripts.Script;
import burp_hotpatch.util.MontoyaUtil;
import burp_hotpatch.util.UIUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.beans.PropertyChangeEvent;

public class BurpHotpatchView extends AbstractView<BurpHotpatchControllerEvent, BurpHotpatchModel, BurpHotpatchModelEvent> {
    public JTable jtblScriptSelection;
    public JTextField jtxtScriptName = new JTextField();
    public JComboBox<String> jcmbScriptType = new JComboBox<>();
    public JComboBox<String> jcmbScriptLanguage = new JComboBox<>();
    public JCheckBox jchkEnabled = new JCheckBox("Enabled");
    public JButton jbtnRun = new JButton("Run");

    public JButton jbtnNew = new JButton("New");
    public JButton jbtnSave = new JButton("Save");
    public JButton jbtnDelete = new JButton("Delete");
    public JButton jbtnCancel = new JButton("Cancel");
    public JButton jbtnImport = new JButton("Import");
    public JButton jbtnExport = new JButton("Export");
    public JButton jbtnClear = new JButton("Clear");

    public RSyntaxTextArea jtxtScriptContent = null;
    public JTextArea jtxtOutput = new JTextArea();

    public JRadioButton jradioStdout = new JRadioButton("STDOUT");
    public JRadioButton jradioStderr = new JRadioButton("STDERR");

    public BurpHotpatchView(BurpHotpatchModel model) {
        super(model);
        initComponents();
    }

    private void initComponents() {
        jtblScriptSelection = new JTable(getModel().getScriptSelectionModel());
        Color defaultTableBackgroundColour = jtblScriptSelection.getBackground();
        Color defaultSelectedBackgroundColour = jtblScriptSelection.getSelectionBackground();
        Color defaultSelectedForgroundColour = jtblScriptSelection.getSelectionForeground();
        Color defaultForegroundColour = jtblScriptSelection.getForeground();
        jtblScriptSelection.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                boolean hasErrors = (boolean) jtblScriptSelection.getValueAt(row, 1);
                setForeground(defaultForegroundColour);
                if ( isSelected && !hasErrors) {
                    setBackground(defaultSelectedBackgroundColour);
                    setForeground(defaultSelectedForgroundColour);
                }
                else {
                    if ((boolean) jtblScriptSelection.getValueAt(row, 1)) {
                        setBackground(new Color(  244, 100, 70  ));
                        setForeground(Color.BLACK);
                    }
                    else {
                        setBackground(defaultTableBackgroundColour);
                    }
                }

                return this;
            }
        });

        int[] colWidths = { 0, 0, 20, 80 };
        for ( int i = 0; i < colWidths.length; i++ ) {
            jtblScriptSelection.getColumnModel().getColumn(i).setMinWidth(colWidths[i]);
            jtblScriptSelection.getColumnModel().getColumn(i).setMaxWidth(colWidths[i]);
            jtblScriptSelection.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
        }
        jtblScriptSelection.setRowSelectionAllowed(true);
        jtblScriptSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(jradioStderr);
        buttonGroup.add(jradioStdout);

        jcmbScriptType.addItem(ScriptTypes.toFriendlyName(ScriptTypes.UTILITY));
        jcmbScriptType.addItem(ScriptTypes.toFriendlyName(ScriptTypes.HTTP_HANDLER_REQUEST_TO_BE_SENT));
        jcmbScriptType.addItem(ScriptTypes.toFriendlyName(ScriptTypes.HTTP_HANDLER_RESPONSE_RECEIVED));
        jcmbScriptType.addItem(ScriptTypes.toFriendlyName(ScriptTypes.PROXY_HANDLER_REQUEST_RECEIVED));
        jcmbScriptType.addItem(ScriptTypes.toFriendlyName(ScriptTypes.PROXY_HANDLER_REQUEST_TO_BE_SENT));
        jcmbScriptType.addItem(ScriptTypes.toFriendlyName(ScriptTypes.SESSION_HANDLING_ACTION));
        jcmbScriptType.addItem(ScriptTypes.toFriendlyName(ScriptTypes.PAYLOAD_PROCESSOR));

        jcmbScriptLanguage.addItem(ScriptLanguage.toFriendlyName(ScriptLanguage.JYTHON));
        jcmbScriptLanguage.addItem(ScriptLanguage.toFriendlyName(ScriptLanguage.JAVASCRIPT));


        jtxtOutput.setEditable(false);


        /*
            https://github.com/bobbylight/RSyntaxTextArea/issues/269
        */
        JTextComponent.removeKeymap("RTextAreaKeymap");
        UIManager.put("RSyntaxTextAreaUI.actionMap", null);
        UIManager.put("RSyntaxTextAreaUI.inputMap", null);
        UIManager.put("RTextAreaUI.actionMap", null);
        UIManager.put("RTextAreaUI.inputMap", null);

        jtxtScriptContent = new RSyntaxTextArea();
        jtxtScriptContent.setEditable(true);
        jtxtScriptContent.setBackground(jtxtOutput.getBackground());
        jtxtScriptContent.setForeground(jtxtOutput.getForeground());
        jtxtScriptContent.setHighlightCurrentLine(false);
        jtxtScriptContent.setAntiAliasingEnabled(true);
        jtxtScriptContent.setFont(jtxtOutput.getFont());
        jtxtScriptContent.setAutoIndentEnabled(true);
        jtxtScriptContent.setCloseCurlyBraces(true);


        updateEditorState(EditorState.INITIAL);
    }

    @Override
    public void attachListeners() {
        attach(jbtnNew, BurpHotpatchControllerEvent.NEW);
        attach(jbtnSave, BurpHotpatchControllerEvent.SAVE);
        attach(jbtnDelete, BurpHotpatchControllerEvent.DELETE);
        attach(jbtnCancel, BurpHotpatchControllerEvent.CANCEL);
        attach(jbtnRun, BurpHotpatchControllerEvent.TOGGLE_SCRIPT_EXECUTION);
        attach(jbtnImport, BurpHotpatchControllerEvent.IMPORT);
        attach(jbtnExport, BurpHotpatchControllerEvent.EXPORT);
        attach(jbtnClear, BurpHotpatchControllerEvent.CLEAR_OUTPUT);
        attach(jcmbScriptType, BurpHotpatchControllerEvent.SCRIPT_TYPE_UPDATED);
        attach(jcmbScriptLanguage, BurpHotpatchControllerEvent.SCRIPT_LANGUAGE_UPDATED);
        attach(jtxtScriptName, BurpHotpatchControllerEvent.NAME_UPDATED);
        attach(jchkEnabled, BurpHotpatchControllerEvent.CURRENT_SCRIPT_ENABLE_TOGGLE);
        attach(jtxtScriptContent, BurpHotpatchControllerEvent.CURRENT_SCRIPT_CONTENT_UPDATED);
        attach(jradioStdout, BurpHotpatchControllerEvent.OUTPUT_TYPE_STDOUT_SELECTED);
        attach(jradioStderr, BurpHotpatchControllerEvent.OUTPUT_TYPE_STDERR_SELECTED);
        attachSelection(jtblScriptSelection, BurpHotpatchControllerEvent.SCRIPT_SELECTION_UPDATED);
        attachTableModelChangeListener(getModel().getScriptSelectionModel(), BurpHotpatchControllerEvent.TABLE_VALUE_UPDATED);
    }

    @Override
    protected void handleEvent(BurpHotpatchModelEvent event, Object previous, Object next) {
        switch ( event ) {
            case CURRENT_SCRIPT_SET:
                setScript((Script) next);
                if ( next != null ) {
                    getModel().setCurrentSelectedIdx(UIUtil.getTableRowIndexById(getModel().getScriptSelectionModel(), ((Script) next).getId()));
                    if ( ((Script) next).getId() != null ) {
                        getModel().setLastSelectedScriptId(((Script) next).getId());
                    }
                    getModel().setEditorState(EditorState.EDIT);
                    getModel().setScriptTemplateModified();
                    getModel().setSelectedOutputType(OutputType.STDOUT);
                    jcmbScriptType.setSelectedItem(ScriptTypes.toFriendlyName(((Script) next).getScriptType()));
                    jcmbScriptLanguage.setSelectedItem(ScriptLanguage.toFriendlyName(((Script) next).getScriptLanguage()));
                    updateEditorFormat();
                }
                break;
            case SCRIPT_OUTPUT_UPDATED:
                jtxtOutput.setText((String) next);
                break;
            case SCRIPT_SAVED:
                selectScriptById((String)next);
                jcmbScriptType.setEnabled(false);
                break;
            case SCRIPT_DELETED:
                getModel().setEditorState(EditorState.INITIAL);
                getModel().setCurrentScript(null);
                getModel().removeFromScriptsTableModel((String)next);
                int currentIndex = getModel().getCurrentSelectedIdx();
                if ( getModel().getCurrentSelectedIdx() >= jtblScriptSelection.getRowCount()) {
                    currentIndex -= 1;
                }
                if ( currentIndex >= 0 && jtblScriptSelection.getRowCount() > 0 ) {
                    jtblScriptSelection.getSelectionModel().setSelectionInterval(currentIndex,currentIndex);
                }
                if ( getModel().getScripts().isEmpty() ) {
                    getModel().setCurrentSelectedIdx(-1);
                }
                break;
            case OUTPUT_FORMAT_SET:
                updateOutputFormatSelectorLineCounts();
                if (getModel().getSelectedOutputType().equals(OutputType.STDOUT)) {
                    jtxtOutput.setText(getModel().getStdout(getModel().getCurrentScriptId()));
                }
                else {
                    jtxtOutput.setText(getModel().getStderr(getModel().getCurrentScriptId()));
                }
                break;
            case STDERR_SET:
                updateOutputFormatSelectorLineCounts();
                if( getModel().getSelectedOutputType().equals(OutputType.STDERR)) {
                    jtxtOutput.setText(getModel().getStderr(getModel().getCurrentScriptId()));
                }
                if ( getModel().getStderr(getModel().getCurrentScriptId()) != null ) {
                    jradioStderr.setSelected(true);
                    getModel().setSelectedOutputType(OutputType.STDERR);
                }
                break;

            case STDOUT_SET:
                updateOutputFormatSelectorLineCounts();
                jradioStdout.setSelected(true);
                getModel().setSelectedOutputType(OutputType.STDOUT);
                if( getModel().getSelectedOutputType().equals(OutputType.STDOUT)) {
                    jtxtOutput.setText(getModel().getStdout(getModel().getCurrentScriptId()));
                }
                break;
            case SCRIPT_TEMPLATE_MODIFIED:
                if ( getModel().getCurrentScript() != null && getModel().getCurrentScript().getId() == null ) {
                    jtxtScriptContent.setText(
                            ResourceLoader.getInstance().getEditorTemplate(
                                    getModel().getCurrentScript().getScriptType(),
                                    getModel().getCurrentScript().getScriptLanguage()
                            )
                    );
                    updateEditorFormat();
                    jbtnRun.setEnabled(getModel().getCurrentScript().getScriptType().equals(ScriptTypes.UTILITY));
                }
                break;

            case EDITOR_STATE_SET:
                updateEditorState((EditorState) next);
                break;
            case CURRENT_SCRIPT_SAVE_ERROR:
                JOptionPane.showMessageDialog(MontoyaUtil.getInstance().getApi().userInterface().swingUtils().suiteFrame(), (String)next,"Error saving script",JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    private void updateEditorFormat() {
        switch ( getModel().getCurrentScript().getScriptLanguage() ) {
            case JYTHON:
                jtxtScriptContent.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
                break;
            case JAVASCRIPT:
                jtxtScriptContent.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
                break;
        }
    }

    private void updateOutputFormatSelectorLineCounts() {
        jradioStderr.setText(String.format("STDERR ( %d lines )", getModel().getStderr(getModel().getCurrentScriptId()) != null ? getModel().getStderr(getModel().getCurrentScriptId()).split("\n").length : 0));
        jradioStdout.setText(String.format("STDOUT ( %d lines )", getModel().getStdout(getModel().getCurrentScriptId()) != null ? getModel().getStdout(getModel().getCurrentScriptId()).split("\n").length : 0));

    }

    private void setScript( Script script ) {
        jtxtScriptName.setText( script != null && script.getName() != null ? script.getName() : "");
        jchkEnabled.setSelected(script != null && script.isEnabled());
        jtxtScriptContent.setText( script != null ? script.getContent() : "");
        if ( script != null && script.getScriptType() != null ) {
            jcmbScriptType.setSelectedItem( script.getScriptType());
        }
        else {
            jcmbScriptType.setSelectedIndex(-1);
        }
        if ( script != null && script.getScriptLanguage() != null ) {
            jcmbScriptLanguage.setSelectedItem( script.getScriptLanguage());
        }
        else {
            jcmbScriptLanguage.setSelectedIndex(-1);
        }
        jradioStdout.setSelected(true);
        if( script != null ) {
            jcmbScriptType.setEnabled(script != null && getModel().getCurrentScript().getId() == null);
            jcmbScriptLanguage.setEnabled(script != null && getModel().getCurrentScript().getId() == null);
        }
        else {
            jcmbScriptType.setEnabled(false);
            jcmbScriptLanguage.setEnabled(false);
        }
        jbtnRun.setEnabled(script != null && script.scriptType.equals(ScriptTypes.UTILITY));
    }

    private void updateEditorState( EditorState editorState ) {
        jtxtScriptName.setEnabled(!editorState.equals(EditorState.INITIAL));
        jcmbScriptType.setEnabled(!editorState.equals(EditorState.INITIAL));
        jcmbScriptLanguage.setEnabled(!editorState.equals(EditorState.INITIAL));
        jchkEnabled.setEnabled(!editorState.equals(EditorState.INITIAL));
        jbtnRun.setEnabled(!editorState.equals(EditorState.INITIAL));
        jtxtScriptContent.setEnabled(!editorState.equals(EditorState.INITIAL));
        jtxtOutput.setEnabled(!editorState.equals(EditorState.INITIAL));
        jradioStderr.setEnabled(!editorState.equals(EditorState.INITIAL));
        jradioStdout.setEnabled(!editorState.equals(EditorState.INITIAL));
        updateEditorButtonsState(editorState);
    }

    private void updateEditorButtonsState(EditorState editorState) {
        switch ( editorState ) {
            case EDIT:
                jbtnNew.setEnabled(true);
                jbtnSave.setEnabled(true);
                jbtnCancel.setEnabled(false);
                jbtnDelete.setEnabled(true);
                break;
            case CREATE:
                jbtnNew.setEnabled(false);
                jbtnSave.setEnabled(true);
                jbtnCancel.setEnabled(true);
                jbtnDelete.setEnabled(false);
                break;
            case INITIAL:
                jbtnNew.setEnabled(true);
                jbtnSave.setEnabled(false);
                jbtnDelete.setEnabled(false);
                jbtnCancel.setEnabled(false);
                break;
        }
    }

    private void selectScriptById(String id) {
        int idx = UIUtil.getTableRowIndexById(getModel().getScriptSelectionModel(),id);
        if ( idx >= 0 ) {
            jtblScriptSelection.setRowSelectionInterval(idx,idx);
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        handleEvent(BurpHotpatchModelEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
    }
}
