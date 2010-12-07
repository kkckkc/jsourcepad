package kkckkc.jsourcepad.dialog.tmdialog.dialogs.subversion;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import kkckkc.jsourcepad.dialog.tmdialog.BaseTmDialogDelegate;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.*;

public class RevisionSelector extends BaseTmDialogDelegate {
    private JTable table;
    private DefaultTableModel tableModel;
    private Set<Integer> revisions = Sets.newHashSet();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    protected void buildDialog(JPanel panel) {
        panel.setLayout(new MigLayout("insets dialog", "[grow]", "[grow]u[]"));

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Revision");
        tableModel.addColumn("Date");
        tableModel.addColumn("Author");
        tableModel.addColumn("Message");

        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(400);


        panel.add(new JScrollPane(table), "grow, wrap");

        panel.add(new JButton(OK_ACTION), "tag ok, wrap");
    }

    @Override
    public void load(boolean isFirstTime, Map object) {
        List entries = (List) object.get("entries");
        String title = (String) object.get("title");
        Boolean hideProgressIndicator = (Boolean) object.get("hideProgressIndicator");

        if (title != null) jdialog.setTitle(title);
        if (entries != null) {
            for (int i = 0; i < entries.size(); i++) {
                Map map = (Map) entries.get(i);
                Entry e = Entry.fromMap(map);
                if (revisions.contains(e.getRev())) continue;

                tableModel.addRow(new Object[]{e.getRev(), dateFormat.format(e.getDate()), e.getAuthor(), e.getMsg()});
                revisions.add(e.getRev());
            }
        }
    }

    @Override
    protected Object getReturnData() {
        Map m = Maps.newHashMap();

        m.put("returnArgument", Arrays.asList(table.getValueAt(table.getSelectedRow(), 0)));
        m.put("returnButton", "ok");

        return m;
    }

    static class Entry {
        String author;
        Date date;
        String msg;
        Integer rev;

        public static Entry fromMap(Map m) {
            Entry e = new Entry();
            e.author = (String) m.get("author");
            e.date = (Date) m.get("date");
            e.msg = (String) m.get("msg");
            e.rev = (Integer) m.get("rev");
            return e;
        }

        public String getAuthor() {
            return author;
        }

        public Date getDate() {
            return date;
        }

        public String getMsg() {
            return msg;
        }

        public Integer getRev() {
            return rev;
        }
    }
}
