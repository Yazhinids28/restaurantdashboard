package util;

import javax.swing.table.DefaultTableModel;

public class TableUtils {
    public static DefaultTableModel uneditableModel(Object[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
