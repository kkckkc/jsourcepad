package kkckkc.jsourcepad;

import kkckkc.jsourcepad.util.Config;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class BootstrapLogger {
	public BootstrapLogger() throws IOException {
        if (Config.getMode() == Config.MODE_DEVELOPMENT) {
            LogManager.getLogManager().readConfiguration(
                    Bootstrap.class.getResourceAsStream("/logging.development.properties"));
        } else {
            LogManager.getLogManager().readConfiguration(
                    Bootstrap.class.getResourceAsStream("/logging.properties"));

            Config.getLogFolder().mkdirs();

            FileHandler fileHandler = new FileHandler(Config.getLogFolder() + "/log.txt", 500000, 1, true);
            fileHandler.setFormatter(new SimpleFormatter());

            Logger.getLogger("").addHandler(fileHandler);
        }
	}
}
