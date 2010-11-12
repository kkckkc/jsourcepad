package kkckkc.jsourcepad.commitdialog;

import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CommitDialog implements Dialog {

    int returnValue;

    @Override
    public int execute(JFrame parent, Writer out, String... args) throws IOException {
        List<String> files = Lists.newArrayList();
        List<String> statuses = Lists.newArrayList();

        Iterator<String> it = Arrays.asList(args).iterator();
        while (it.hasNext()) {
            String s = it.next();
            if (s.equals("--diff-cmd")) {
                it.next();
            } else if (s.equals("--status")) {
                String statusString = it.next();
                Iterables.addAll(statuses, Splitter.on(":").split(statusString));
            } else if (s.equals("--action-cmd")) {
                it.next();
            } else {
                files.add(s);
            }
        }

        Table<Integer, Integer, Object> t = HashBasedTable.create();
        int row = 0;
        for (String s : files) {
            t.put(row, 0, Boolean.TRUE);
            t.put(row, 1, statuses.get(row));
            t.put(row, 2, s);

            row++;
        }


        final JDialog jdialog = new JDialog(parent, true);
        jdialog.setTitle("Commit");

        JTable table = new JTable();

        JPanel pane = new JPanel();
        pane.setLayout(new MigLayout("insets dialog", "[grow]", "[]r[grow]u[]r[grow]u[]"));

        jdialog.setContentPane(pane);

        JTextArea textArea = new JTextArea(10, 50);

        pane.add(new JLabel("Summary of changes:"), "wrap");
        pane.add(new JScrollPane(textArea), "grow, wrap");

        pane.add(new JLabel("Choose files to commit:"), "wrap");
        pane.add(new JScrollPane(table), "grow, wrap");

        JButton cancel = new JButton("Cancel");
        JButton commit = new JButton("Commit");

        pane.add(cancel, "split,tag cancel");
        pane.add(commit, "split,tag ok");

        table.setModel(new CommitDialogTableModel(t));

        table.getColumnModel().getColumn(0).setMaxWidth(20);
        table.getColumnModel().getColumn(1).setMaxWidth(20);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnValue = 128;
                jdialog.setVisible(false);
            }
        });
        commit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnValue = 0;
                jdialog.setVisible(false);
            }
        });

        jdialog.pack();

        jdialog.setVisible(true);

        if (returnValue == 0) {
            out.write("-m '" + textArea.getText().replaceAll("'", "'\"'\"'") + "'");
            for (Integer r : t.rowKeySet()) {
                out.write(" '" + t.get(r, 2) + "'");
            }
            out.flush();
        }

        return returnValue;
    }

    public static void main(String... args) throws IOException {
        String[] a = new String[] {
                "--diff-cmd", "arg1=svn,diff,--diff-cmd,diff",
                "--status", "M:A:A",
                "--action-cmd", "!:Remove,svn,rm",
                "--action-cmd", "?:Add,svn,add",
                "--action-cmd", "A:Mark Executable,/Users/magnus/Library/Application Support/JSourcePad/Bundles/subversion.tmbundle/Support/commit_status_helper.rb,propset,svn:executable,true",
                "--action-cmd", "A,M,D,C:Revert,/Users/magnus/Library/Application Support/JSourcePad/Bundles/subversion.tmbundle/Support/commit_status_helper.rb,revert",
                "--action-cmd", "C:Resolved,/Users/magnus/Library/Application Support/JSourcePad/Bundles/subversion.tmbundle/Support/commit_status_helper.rb,resolved",
                "b.txt", "c.txt", "k.txt"
        };

        CommitDialog commitDialog = new CommitDialog();
        System.out.println("\n\nReturn: " + commitDialog.execute(null, new OutputStreamWriter(System.out), a));

        System.exit(0);
    }
}
