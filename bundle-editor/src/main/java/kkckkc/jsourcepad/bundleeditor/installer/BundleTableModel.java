package kkckkc.jsourcepad.bundleeditor.installer;

import javax.swing.table.AbstractTableModel;
import java.util.List;

class BundleTableModel extends AbstractTableModel {
    private List<Entry> data;

    public BundleTableModel(List<Entry> data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) return data.get(rowIndex);
        return data.get(rowIndex).getName();
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? "" : "Name";
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
        data.get(row).setSelected(! data.get(row).isSelected());
        fireTableCellUpdated(row, col);
    }

    static class Entry implements Comparable {
        boolean selected;
        boolean disabled;
        String name;
        private String url;

        Entry(boolean selected, boolean disabled, String name, String url) {
            this.selected = selected;
            this.disabled = disabled;
            this.name = name;
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public boolean isSelected() {
            return selected;
        }

        public boolean isEnabled() {
            return !disabled;
        }

        public String getName() {
            return name;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        @Override
        public int compareTo(Object o) {
            return getName().compareTo(((Entry) o).getName());
        }
    }
}
