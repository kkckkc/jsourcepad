package kkckkc.jsourcepad;

import java.io.IOException;
import java.util.logging.LogManager;

public class BootstrapLogger {
	public BootstrapLogger() throws IOException {
        LogManager.getLogManager().readConfiguration(
        		Bootstrap.class.getResourceAsStream("/logging.properties"));
	}
}
