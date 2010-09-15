
package kkckkc.jsourcepad.model;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ClipboardManager {
    public static final int HISTORY_SIZE = 100;

    LinkedList<Transferable> history = new LinkedList<Transferable>();

    public void register() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (! history.isEmpty() && history.getLast() == transferable) return;

        history.addLast(transferable);

        while (history.size() > HISTORY_SIZE) {
            history.removeFirst();
        }
    }

    public Transferable getLast() {
        return history.getLast();
    }

    public Transferable getSecondLast() {
        if (history.size() < 2) return null;
        return history.get(history.size() - 2);
    }

    public List<Transferable> getHistory() {
        return history;
    }

    public static String getText(Transferable transferable) {
        try {
            DataFlavor df = DataFlavor.selectBestTextFlavor(transferable.getTransferDataFlavors());

            BufferedReader r = new BufferedReader(df.getReaderForText(transferable));

            StringBuilder b = new StringBuilder();

            String line = null;
            while ((line = r.readLine()) != null) {
                b.append(line).append("\n");
            }

            return b.toString();
        } catch (UnsupportedFlavorException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
