package io.github.mainstringargs.util.concurrency;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * The Class ScheduledThreadPoolExecutorTracer.
 */
class ScheduledThreadPoolExecutorTracer extends ScheduledThreadPoolExecutor {

    /**
     * Instantiates a new scheduled thread pool executor tracer.
     *
     * @param corePoolSize  the core pool size
     * @param threadFactory the thread factory
     */
    public ScheduledThreadPoolExecutorTracer(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    /**
     * Instantiates a new scheduled thread pool executor tracer.
     *
     * @param corePoolSize the core pool size
     */
    public ScheduledThreadPoolExecutorTracer(int corePoolSize) {
        super(corePoolSize);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable,
     * java.lang.Throwable)
     */
    @Override
    protected void afterExecute(final Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                final Future<?> future = (Future<?>) r;
                if (future.isDone() && !future.isCancelled()) {
                    future.get();
                }
            } catch (final CancellationException ce) {
                t = ce;
            } catch (final ExecutionException ee) {
                t = ee.getCause();
            } catch (final InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
            t.printStackTrace();
        }
    }
}
