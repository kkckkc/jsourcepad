package kkckkc.jsourcepad;

import java.util.concurrent.CountDownLatch;

/**
 * Created by IntelliJ IDEA.
 * User: magnus
 * Date: 8/11/11
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
class StartupBenchmark {
    private CountDownLatch cdl = new CountDownLatch(2);

    public void execute() {
        if ("true".equals(System.getProperty("immediateExitForBenchmark"))) {
            try {
                cdl.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        }
    }

    public void applicationInitComplete() {
        cdl.countDown();
    }

    public void applicationStartupComplete() {
        cdl.countDown();
    }
}
