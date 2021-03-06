package kkckkc.jsourcepad.dialog.commitdialog;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import kkckkc.jsourcepad.command.global.OpenCommand;
import kkckkc.jsourcepad.dialog.Dialog;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.EnvironmentProvider;
import kkckkc.jsourcepad.util.Cygwin;
import kkckkc.jsourcepad.util.io.ScriptExecutor;
import kkckkc.jsourcepad.util.io.UISupportCallback;
import kkckkc.jsourcepad.util.ui.WindowFocusUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

public class CommitDialog implements Dialog {

    static final Set<String> NO_COMMIT_STATUSES = new HashSet<String>(Arrays.asList("?", "X"));
    int returnValue;

    @Override
    public int execute(final Window window, final Writer out, final String pwd, final String stdin, final String... args) throws IOException {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    final List<Command> commands = Lists.newArrayList();

                    List<String> files = Lists.newArrayList();
                    List<String> statuses = Lists.newArrayList();

                    Iterator<String> it = Arrays.asList(args).iterator();
                    while (it.hasNext()) {
                        String s = it.next();
                        if (s.equals("--diff-cmd")) {
                            commands.add(new Command("Diff", null,
                                    Iterables.toArray(Splitter.on(",").split(it.next()), String.class), false));
                        } else if (s.equals("--status")) {
                            String statusString = it.next();
                            Iterables.addAll(statuses, Splitter.on(":").split(statusString));
                        } else if (s.equals("--action-cmd")) {
                            String[] cmd = Iterables.toArray(Splitter.on(":").split(it.next()), String.class);
                            String[] labelAndCmd = Iterables.toArray(Splitter.on(",").split(cmd[1]), String.class);
                            String[] cmdArr = new String[labelAndCmd.length - 1];
                            System.arraycopy(labelAndCmd, 1, cmdArr, 0, cmdArr.length);
                            commands.add(new Command(labelAndCmd[0],
                                    Iterables.toArray(Splitter.on(",").split(cmd[0]), String.class),
                                    cmdArr, true));
                        } else {
                            files.add(s);
                        }
                    }

                    Table<Integer, Integer, Object> t = HashBasedTable.create();
                    int row = 0;
                    for (String s : files) {
                        String status = statuses.get(row);
                        t.put(row, 0, ! NO_COMMIT_STATUSES.contains(status));
                        t.put(row, 1, status);
                        t.put(row, 2, s);

                        row++;
                    }


                    final JDialog jdialog = new JDialog(window.getContainer(), java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
                    jdialog.setTitle("Commit");

                    final JTable table = new JTable();

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

                    table.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            if (e.isPopupTrigger()) showPopup(e.getPoint(), table, window, pwd, commands);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            if (e.isPopupTrigger()) showPopup(e.getPoint(), table, window, pwd, commands);
                        }
                    });

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

                    jdialog.setLocationRelativeTo(window.getContainer());
                    jdialog.setLocationByPlatform(true);

                    WindowFocusUtils.showAndFocusDialog(jdialog);


                    if (returnValue == 0) {
                        boolean anyChecked = false;
                        for (Integer r : t.rowKeySet()) {
                            anyChecked |= (Boolean) t.get(r, 0);
                        }
                        if (! anyChecked) {
                            returnValue = 128;
                            return;
                        }

                        try {
                            out.write("-m '" + textArea.getText().replaceAll("'", "'\"'\"'") + "'");
                            for (Integer r : t.rowKeySet()) {
                                if ((Boolean) t.get(r, 0)) {
                                    out.write(" '" + t.get(r, 2) + "'");
                                }
                            }
                            out.flush();
                        } catch (IOException ioe) {
                            throw new RuntimeException(ioe);
                        }
                    }
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }


        return returnValue;
    }

    protected void showPopup(Point point, final JTable table, final Window window, final String pwd, List<Command> commands) {
        final int row = table.rowAtPoint(point);
        String status = (String) table.getValueAt(row, 1);

        JPopupMenu popupMenu = new JPopupMenu();
        for (final Command command : commands) {
            if (command.getStatuses() == null || Arrays.binarySearch(command.getStatuses(), status) >= 0) {
                popupMenu.add(new AbstractAction(command.getLabel()) {
                    public void actionPerformed(ActionEvent e) {
                        StringBuilder builder = new StringBuilder();
                        for (String s : command.getCommand()) {
                            builder.append(Cygwin.makePathForDirectUsage(s)).append(" ");
                        }
                        builder.append(Cygwin.makePathForDirectUsage((String) table.getValueAt(row, 2)));

                        ScriptExecutor scriptExecutor = new ScriptExecutor(builder.toString(), Application.get().getThreadPool());
                        scriptExecutor.setDirectory(new File(Cygwin.toFile(pwd)));
                        scriptExecutor.setShowStderr(false);

                        try {
                            scriptExecutor.execute(new UISupportCallback(window) {
                                @Override
                                public void onAfterSuccess(ScriptExecutor.Execution execution) {
                                    if (command.isStatusChangingCommand()) {
                                        String stdout = execution.getStdout();
                                        if (Strings.isNullOrEmpty(stdout.trim())) {
                                            ((CommitDialogTableModel) table.getModel()).removeRow(row);
                                        } else {
                                            String status = stdout.substring(0, 1);
                                            table.setValueAt(status, row, 1);
                                            table.setValueAt(! NO_COMMIT_STATUSES.contains(status), row, 0);
                                        }
                                    } else {
                                        OpenCommand openCommand = new OpenCommand();
                                        openCommand.setOpenInSeparateWindow(true);
                                        openCommand.setContents(execution.getStdout());

                                        Application.get().getCommandExecutor().execute(openCommand);
                                    }
                                }
                            }, new StringReader(""), EnvironmentProvider.getStaticEnvironment());
                        } catch (IOException e1) {
                            throw new RuntimeException(e1);
                        }
                    }
                });
            }
        }

        popupMenu.show(table, (int) point.getX(), (int) point.getY());
    }

    static class Command {
        private String label;
        private String[] statuses;
        private String[] command;
        private boolean statusChangingCommand;

        Command(String label, String[] statuses, String[] command, boolean statusChangingCommand) {
            this.label = label;
            this.statuses = statuses;
            this.command = command;
            this.statusChangingCommand = statusChangingCommand;

            if (this.statuses != null) {
                Arrays.sort(this.statuses);
            }
        }

        public boolean isStatusChangingCommand() {
            return statusChangingCommand;
        }

        public String getLabel() {
            return label;
        }

        public String[] getStatuses() {
            return statuses;
        }

        public String[] getCommand() {
            return command;
        }
    }
}
