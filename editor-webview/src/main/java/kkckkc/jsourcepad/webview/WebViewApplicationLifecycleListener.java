package kkckkc.jsourcepad.webview;

import chrriis.dj.nativeswing.NativeSwing;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import kkckkc.jsourcepad.ApplicationLifecycleListener;

public class WebViewApplicationLifecycleListener implements ApplicationLifecycleListener {
    @Override
    public void applicationAboutToStart() {
        NativeInterface.initialize();
        NativeSwing.initialize();
    }

    @Override
    public void applicationStarted() {
        NativeInterface.runEventPump();
    }
}
