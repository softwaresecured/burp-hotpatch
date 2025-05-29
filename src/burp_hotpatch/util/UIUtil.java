package burp_hotpatch.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UIUtil {
    public static boolean comboBoxHasItem(MutableComboBoxModel model, String item ) {
        for ( int i = 0; i < model.getSize(); i++ ) {
            if ( model.getElementAt(i).equals(item)) {
                return true;
            }
        }
        return false;
    }

    /*
        Gets a jtable row by id
     */
    public static int getTableRowIndexById(DefaultTableModel model, String id) {
        int idx  = -1;
        for ( int i = 0; i < model.getRowCount(); i++ ) {
            String currentRowId = (String) model.getValueAt(i,0);
            if ( currentRowId.equals(id) ) {
                idx = i;
            }
        }
        return idx;
    }

}
