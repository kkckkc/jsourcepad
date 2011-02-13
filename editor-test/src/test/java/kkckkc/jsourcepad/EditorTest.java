package kkckkc.jsourcepad;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import org.junit.After;
import org.junit.Before;

import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EditorTest {

    public static boolean initialized = false;
    private Window w;

    public static void init() throws InterruptedException {
        if (initialized) return;

        try {
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    Application.init();
                }
            });
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
/*
        Bootstrap b = new Bootstrap();

        Thread mainThread = new Thread(b);
        mainThread.start();

*/
        waitForEvents();

        initialized = true;
    }

    private static void waitForEvents() throws InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(2);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                cdl.countDown();
            }
        });

        DispatchStrategy.__EXECUTOR.execute(new Runnable() {
            public void run() {
                cdl.countDown();
            }
        });

        cdl.await(10, TimeUnit.SECONDS);
    }

    public static void pause() {
        try {
            Thread.sleep(5 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  
        }
    }

    @Before
    public void initialize() throws InterruptedException {
        init();

        Application app = Application.get();
        WindowManager wm = app.getWindowManager();
        w = wm.newWindow(new File("."));
        waitForEvents();
    }
    
    @After
    public void tearDown() {
        Application app = Application.get();
        WindowManager wm = app.getWindowManager();
        wm.closeWindow(w);
    }

}
