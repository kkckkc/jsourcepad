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

            // Verify git
            /*
            class StatusCallback extends ScriptExecutor.CallbackAdapter {
                boolean successful = false;

                @Override
                public void onFailure(ScriptExecutor.Execution execution) {
                    successful = false;
                }

                @Override
                public void onSuccess(ScriptExecutor.Execution execution) {
                    successful = true;
                }

                public boolean isSuccessful() {
                    return successful;
                }
            };

            StatusCallback statusCallback = new StatusCallback();

            ScriptExecutor se = new ScriptExecutor("gitc --version", Application.get().getThreadPool());
            ScriptExecutor.Execution execution = se.execute(statusCallback, new StringReader(""), System.getenv());
            execution.waitForCompletion();

            if (! statusCallback.isSuccessful()) {
                System.out.println("FAILURE");

                System.out.println(execution.getStderr());

                return false;
                // TODO: Notify user
            }
            */

/*
            // Show available bundles
            URL url = new URL("http://github.com/api/v2/xml/repos/show/textmate");
            final URLConnection conn = url.openConnection();
            conn.connect();

            String s = CharStreams.toString(CharStreams.newReaderSupplier(new InputSupplier<InputStream>() {

                @Override
                public InputStream getInput() throws IOException {
                    return conn.getInputStream();
                }
            }, Charsets.UTF_8));

            System.out.println(s);
*/
            // Install bundles


            continuation.run();

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
        
        return true;
    }
}
