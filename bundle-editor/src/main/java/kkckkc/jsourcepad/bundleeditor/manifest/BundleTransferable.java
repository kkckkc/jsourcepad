package kkckkc.jsourcepad.bundleeditor.manifest;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

class BundleTransferable implements Transferable {
    private String data;

    public static DataFlavor DATAFLOVOR = new DataFlavor(BundleTransferable.class, "Bundle Item");

    BundleTransferable(String data) {
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DATAFLOVOR };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DATAFLOVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (! isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);
        return data;
    }
}
