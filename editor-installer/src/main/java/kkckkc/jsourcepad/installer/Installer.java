package kkckkc.jsourcepad.installer;

import com.google.common.base.Function;
import com.google.common.io.Files;
import kkckkc.jsourcepad.installer.bundle.BundleInstallerDialog;
import kkckkc.jsourcepad.util.Config;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Installer implements Function<Runnable, Boolean> {
    private BundleInstallerDialog bundleInstallerDialog;

    @Autowired
    public void setBundleInstallerDialog(BundleInstallerDialog bundleInstallerDialog) {
        this.bundleInstallerDialog = bundleInstallerDialog;
    }

    @Override
    public Boolean apply(Runnable continuation) {
        try {

            // Copy themes
            try {
                Config.getThemesFolder().mkdirs();
                for (File theme : new File(Config.getApplicationFolder(), "Shared/Themes").listFiles()) {
                    Files.copy(theme, new File(Config.getThemesFolder(), theme.getName()));
                }
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }

            Config.getBundlesFolder().mkdirs();

            // Do you want to install more bundles
            int option = JOptionPane.showOptionDialog(null,
                    "You have no bundles installed. Do you want to run the bundle installer now?\n" +
                    "You can run it later at any time, using the menu Bundles > Install Bundles",
                    "Run Bundle Installer Now?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[] { "Yes, Run Bundle Installer Now", "No, I'll Run it Later" },
                    "Yes, Run Bundle Installer Now");
            if (option == 0) {
                bundleInstallerDialog.show();
            }

            continuation.run();

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
        
        return true;
    }
}
