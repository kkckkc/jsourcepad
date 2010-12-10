package kkckkc.jsourcepad.dialog.commitdialog;

import com.google.common.collect.Table;

import javax.swing.table.AbstractTableModel;

public class CommitDialogTableModel extends AbstractTableModel {
    private Table data;

    public CommitDialogTableModel(Table data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.rowKeySet().size();
    }

    @Override
    public int getColumnCount() {
        return data.columnKeySet().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex, columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "";
            case 1: return "";
            case 2: return "File";
        }
        return "";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? Boolean.class : String.class;
    }

    public void setValueAt(Object value, int row, int col) {
        if (col == 0)
            data.put(row, col, !(Boolean) data.get(row, col));
        if (col == 1)
            data.put(row, col, value);

        fireTableCellUpdated(row, col);
    }

    public void removeRow(int row) {
        data.remove(row, 0);
        data.remove(row, 1);
        data.remove(row, 2);
        fireTableRowsDeleted(row, row);
    }
}
