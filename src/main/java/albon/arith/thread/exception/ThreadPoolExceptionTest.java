package albon.arith.thread.exception;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * what happened when exception happened in a thread
 * Created by albon on 16/10/29.
 */
public class ThreadPoolExceptionTest {
    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1), r -> new Thread(r, "love"),
                (r, t) -> System.out.println("reject " + counter.incrementAndGet())) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                printException(r, t);
            }
        };

        for (int i=0; i<100; ++i) {
            ExceptionRunnable task = new ExceptionRunnable();
            Future<?> future = executorService.submit(task);
            sleep(10l);
        }

        System.out.println("end ......");
    }

    private static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone())
                    future.get();
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
        if (t != null)
            t.printStackTrace();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ExceptionRunnable implements Runnable {

        public void run() {
            sleep(10l);
            throw new RuntimeException("there is something wrong");
        }
    }

}
