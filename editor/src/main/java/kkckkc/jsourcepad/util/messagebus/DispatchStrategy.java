package kkckkc.jsourcepad.util.messagebus;

import java.awt.*;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public interface DispatchStrategy { 
	public static final DispatchStrategy SYNC = new DispatchStrategy() {
    	public void execute(Runnable runnable) {
    		runnable.run();
    	}
    };
	public static final DispatchStrategy ASYNC_EVENT = new DispatchStrategy() {
    	public void execute(Runnable runnable) {
    		EventQueue.invokeLater(runnable);
    	}
    };
    public static final DispatchStrategy EVENT = new DispatchStrategy() {
        public void execute(Runnable runnable) {
            if (EventQueue.isDispatchThread()) {
                runnable.run();
            } else {
                EventQueue.invokeLater(runnable);
            }
        }
    };
	public static final DispatchStrategy ASYNC = new DispatchStrategy() {
    	public void execute(Runnable runnable) {
    		__EXECUTOR.execute(runnable);
    	}
    };

    public static final Executor __EXECUTOR = new ThreadPoolExecutor(2, 4,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());


    public void execute(Runnable runnable);
}