package burp_jyconsole.ui;

import burp_jyconsole.view.BurpJyConsoleView;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public class BurpJyconsoleTab extends JPanel {
    private BurpJyConsoleView burpJyconsoleView;
    private JTabbedPane pane = new JTabbedPane();
    public BurpJyconsoleTab(BurpJyConsoleView burpJyconsoleView) {
        this.burpJyconsoleView = burpJyconsoleView;
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
        panel.setLayout(new GridBagLayout());
        JScrollPane scroll = new JScrollPane(burpJyconsoleView.jtblScriptSelection);
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
        panel.add(burpJyconsoleView.jbtnImport,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpJyconsoleView.jbtnExport,gbc);
        return panel;
    }

    /*
        Script editor
     */
    private JPanel initScriptEditor() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        RTextScrollPane scrollPaneContent = new RTextScrollPane(burpJyconsoleView.jtxtScriptContent);
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
        JScrollPane scrollPaneOutput = new JScrollPane(burpJyconsoleView.jtxtOutput);
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
        panel.add(burpJyconsoleView.jradioStdout,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = idx++;
        panel.add(burpJyconsoleView.jradioStderr,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = idx++;
        panel.add(new JPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = idx;
        panel.add(burpJyconsoleView.jbtnClear,gbc);

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
        panel.add(burpJyconsoleView.jtxtScriptName,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JLabel("Script type"),gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpJyconsoleView.jcmbScriptType,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpJyconsoleView.jchkEnabled,gbc);

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
        panel.add(burpJyconsoleView.jbtnRun,gbc);

        return panel;

    }

    /*
        New, Save, Delete, Cancel buttopns
     */
    private JPanel initCrudToolBar() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        int idx = 0;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpJyconsoleView.jbtnNew,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpJyconsoleView.jbtnSave,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpJyconsoleView.jbtnDelete,gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpJyconsoleView.jbtnCancel,gbc);
        return panel;
    }

    public JPanel initEditor() {
        JPanel panel = new JPanel();
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

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,initScriptTable(), initEditor());
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
        return panel;
    }

    private void initComponents() {
        burpJyconsoleView.jtxtOutput.setRows(10);
        setPreferredWidth(burpJyconsoleView.jtxtScriptName, 300);
        setPreferredWidth(burpJyconsoleView.jcmbScriptType, 200);
    }

    private void setPreferredWidth(JComponent field, int width) {
        field.setPreferredSize(new Dimension(width, (int)field.getPreferredSize().getHeight()));
    }
}