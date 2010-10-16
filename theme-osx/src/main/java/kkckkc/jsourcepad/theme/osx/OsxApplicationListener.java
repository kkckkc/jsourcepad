package kkckkc.jsourcepad.theme.osx;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.ui.dialog.settings.SettingsDialog;

public class OsxApplicationListener extends ApplicationAdapter {


    @Override
    public void handlePreferences(ApplicationEvent applicationEvent) {
        SettingsDialog sd = Application.get().getBeanFactory().getBean(SettingsDialog.class);
        sd.show();        
    }

    @Override
    public void handleQuit(ApplicationEvent applicationEvent) {
        applicationEvent.setHandled(true);
        System.exit(0);
    }
}
