package kkckkc.jsourcepad.util.ui;

import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class FileTransferHandlerHelper {
    private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";

    private DataFlavor fileFlavor, stringFlavor;
    private DataFlavor uriListFlavor;

    public FileTransferHandlerHelper() {

        fileFlavor = DataFlavor.javaFileListFlavor;
        stringFlavor = DataFlavor.stringFlavor;

        try {
            uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean containsFiles(@NotNull DataFlavor[] flavors) {
        return hasFileFlavor(flavors) || hasURIListFlavor(flavors);
    }

    @NotNull
    public List<File> getFiles(@NotNull Transferable t) {
        try {
            // Windows
            if (hasFileFlavor(t.getTransferDataFlavors())) {
                List<File> files = (List<File>) t.getTransferData(fileFlavor);
                if (files == null) files = Collections.emptyList();
                return files;

            // Linux
            } else if (hasURIListFlavor(t.getTransferDataFlavors())) {
                return textURIListToFileList((String) t.getTransferData(uriListFlavor));
            }
        } catch (UnsupportedFlavorException ufe) {
            throw new RuntimeException(ufe);
        } catch (IOException ieo) {
            throw new RuntimeException(ieo);
        }

        return Collections.emptyList();
    }

    @NotNull
    private static List<File> textURIListToFileList(String data) {
        List<File> list = new ArrayList<File>(1);
        for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens(); ) {
            String s = st.nextToken();
            if (s.startsWith("#")) {
                // the line is a comment (as per the RFC 2483)
                continue;
            }
            try {
                URI uri = new URI(s);
                File file = new File(uri);
                list.add(file);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    private boolean hasFileFlavor(DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (fileFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasURIListFlavor(DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (uriListFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }
}
