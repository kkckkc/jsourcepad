package kkckkc.jsourcepad.bundleeditor.manifest;

import java.util.Comparator;

class TreeEntry {
    private String key;
    private String value;
    private boolean folder;

    public static class TreeEntryComparator implements Comparator<TreeEntry> {
        @Override
        public int compare(TreeEntry o1, TreeEntry o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    }


    TreeEntry(String key, String value, boolean folder) {
        this.key = key;
        this.value = value;
        this.folder = folder;
    }

    public boolean isFolder() {
        return folder;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return value;
    }
}
