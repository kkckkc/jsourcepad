package kkckkc.jsourcepad.theme.osx;

import com.apple.eawt.Application;

import javax.annotation.PostConstruct;

public class OsxApplication {

    @PostConstruct
    public void init() {
        System.out.println("OsxApplication.init");
        Application.getApplication().addPreferencesMenuItem();
        Application.getApplication().addApplicationListener(new OsxApplicationListener());
        Application.getApplication().setEnabledPreferencesMenu(true);
    }

}
