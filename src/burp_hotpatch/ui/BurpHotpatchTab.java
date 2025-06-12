package burp_hotpatch.ui;

import burp_hotpatch.view.BurpHotpatchView;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public class BurpHotpatchTab extends JPanel {
    private BurpHotpatchView burpHotpatchView;
    private JTabbedPane pane = new JTabbedPane();
    public BurpHotpatchTab(BurpHotpatchView burpHotpatchView) {
        this.burpHotpatchView = burpHotpatchView;
        initComponents();
        initLayout();
    }


    private void initLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(initMainTab(),gbc);

    }

    /*
        Script table to the side, buttons
     */
    private JPanel initScriptTable() {
        JPanel pnlCrudBar = initCrudToolBar();
        JPanel pnlImpExpToolbar = initImportExportToolbar();
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Scripts"));
        panel.setLayout(new GridBagLayout());
        JScrollPane scroll = new JScrollPane(burpHotpatchView.jtblScriptSelection);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(pnlCrudBar,gbc);


        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2,2,2,2);
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;

        panel.add(scroll,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(initImportExportToolbar(),gbc);


        setPreferredWidth(scroll,pnlCrudBar.getWidth());
        setPreferredWidth(pnlImpExpToolbar,pnlCrudBar.getWidth());


        return panel;
    }

    public JPanel initLeftSideBar() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,initScriptTable(), initTasksTable());
        split.setResizeWeight(1);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2,2,2,2);
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(split,gbc);
        setPreferredWidth(panel,1);
        return panel;
    }

    public JPanel initTasksToolbar() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        int idx = 0;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jbtnTerminateTask,gbc);
        return panel;
    }



    public JPanel initTasksTable() {
        JPanel panel = new JPanel();

        panel.setBorder(BorderFactory.createTitledBorder("Tasks"));
        panel.setLayout(new GridBagLayout());
        JScrollPane scroll = new JScrollPane(burpHotpatchView.jtblRunningTasks);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2,2,2,2);
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(scroll,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,2);
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(initTasksToolbar(),gbc);

        scroll.setPreferredSize(new Dimension(scroll.getWidth(),300));



        return panel;
    }

    /*
        Import export buttons at bottom
     */
    public JPanel initImportExportToolbar() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        int idx = 0;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jbtnImport,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jbtnExport,gbc);
        return panel;
    }

    /*
        Script editor
     */
    private JPanel initScriptEditor() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        RTextScrollPane scrollPaneContent = new RTextScrollPane(burpHotpatchView.jtxtScriptContent);
        scrollPaneContent.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,scrollPaneContent, initScriptOutputViewer());
        split.setResizeWeight(1);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(split,gbc);
        return panel;
    }

    /*
        Script output
     */
    public JPanel initScriptOutputViewer() {
        JScrollPane scrollPaneOutput = new JScrollPane(burpHotpatchView.jtxtOutput);
        scrollPaneOutput.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        int idy = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = idy++;
        gbc.weightx = 1;
        panel.add(initOutputToolbar(),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = idy++;
        gbc.weightx = 1;
        gbc.weighty = 1;
        panel.add(scrollPaneOutput,gbc);
        return panel;
    }

    /*
        Stderr / Stdout selection
     */
    public JPanel initOutputToolbar() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        int idx = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = idx++;
        panel.add(burpHotpatchView.jradioStdout,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = idx++;
        panel.add(burpHotpatchView.jradioStderr,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = idx++;
        panel.add(new JPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = idx;
        panel.add(burpHotpatchView.jbtnClear,gbc);

        return panel;
    }

    /*
        Script select, name, description, run
    */
    private JPanel initCurrentScript() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        int idx = 0;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JLabel("Name"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jtxtScriptName,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JLabel("Script type"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jcmbScriptType,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JLabel("Language"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jcmbScriptLanguage,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JLabel("Execution order"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jspnExecutionOrder,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jchkEnabled,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jbtnRun,gbc);

        return panel;

    }

    /*
        New, Save, Delete, Cancel buttons
     */
    private JPanel initCrudToolBar() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        int idx = 0;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jbtnNew,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jbtnSave,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jbtnDelete,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpHotpatchView.jbtnCancel,gbc);
        return panel;
    }

    public JPanel initEditor() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Script editor"));
        panel.setLayout(new GridBagLayout());
        int idy = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = idy++;
        gbc.weightx = 1;
        panel.add(initCurrentScript(),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = idy++;
        gbc.weightx = 1;
        gbc.weighty = 1;
        panel.add(initScriptEditor(),gbc);
        return panel;
    }

    public JPanel initMainTab() {

        burpHotpatchView.jtxtUpdateAvailableMessage.setVisible(false);
        burpHotpatchView.jtxtUpdateAvailableMessage.setBorder(BorderFactory.createEmptyBorder());
        burpHotpatchView.jtxtUpdateAvailableMessage.setEditable(false);
        burpHotpatchView.jtxtUpdateAvailableMessage.setHighlighter(null);
        burpHotpatchView.jtxtUpdateAvailableMessage.setContentType("text/html");


        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,initLeftSideBar(), initEditor());
        split.setResizeWeight(0);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        panel.add(split,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,0,2,0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(burpHotpatchView.jtxtUpdateAvailableMessage,gbc);
        return panel;
    }

    private void initComponents() {
        burpHotpatchView.jtxtOutput.setRows(10);
        setPreferredWidth(burpHotpatchView.jtxtScriptName, 300);
        setPreferredWidth(burpHotpatchView.jcmbScriptType, 200);
        setPreferredWidth(burpHotpatchView.jspnExecutionOrder, 80);
    }

    private void setPreferredWidth(JComponent field, int width) {
        field.setPreferredSize(new Dimension(width, (int)field.getPreferredSize().getHeight()));
    }
}