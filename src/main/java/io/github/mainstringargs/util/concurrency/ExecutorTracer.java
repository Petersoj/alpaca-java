package io.github.mainstringargs.util.concurrency;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The Class ExecutorTracer.
 */
public class ExecutorTracer {

    /**
     * Instantiates a new executor tracer.
     */
    private ExecutorTracer() {
    }

    /**
     * New fixed thread pool.
     *
     * @param nThreads the n threads
     * @return the executor service
     */
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutorTracer(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    /**
     * New fixed thread pool.
     *
     * @param nThreads      the n threads
     * @param threadFactory the thread factory
     * @return the executor service
     */
    public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        return new ThreadPoolExecutorTracer(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), threadFactory);
    }

    /**
     * New cached thread pool.
     *
     * @param threadFactory the thread factory
     * @return the executor service
     */
    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return new ThreadPoolExecutorTracer(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), threadFactory);
    }

    /**
     * New cached thread pool.
     *
     * @return the executor service
     */
    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutorTracer(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    /**
     * New single thread executor.
     *
     * @param threadFactory the thread factory
     * @return the executor service
     */
    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
        return new FinalizableDelegatedExecutorService(new ThreadPoolExecutorTracer(1, 1, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory));
    }

    /**
     * New single thread executor.
     *
     * @return the executor service
     */
    public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService(new ThreadPoolExecutorTracer(1, 1, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()));
    }

    /**
     * New single thread scheduled executor.
     *
     * @return the scheduled executor service
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
        return new DelegatedScheduledExecutorService(new ScheduledThreadPoolExecutorTracer(1));
    }

    /**
     * New scheduled thread pool.
     *
     * @param corePoolSize the core pool size
     * @return the scheduled executor service
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        return new ScheduledThreadPoolExecutorTracer(corePoolSize);
    }

    /**
     * New scheduled thread pool.
     *
     * @param corePoolSize  the core pool size
     * @param threadFactory the thread factory
     * @return the scheduled executor service
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize,
                                                                  ThreadFactory threadFactory) {
        return new ScheduledThreadPoolExecutorTracer(corePoolSize, threadFactory);
    }

    /**
     * The Class DelegatedExecutorService.
     */
    public static class DelegatedExecutorService extends AbstractExecutorService {

        /** The e. */
        private final ExecutorService e;

        /**
         * Instantiates a new delegated executor service.
         *
         * @param executor the executor
         */
        DelegatedExecutorService(ExecutorService executor) {
            e = executor;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
         */
        public void execute(Runnable command) {
            e.execute(command);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.ExecutorService#shutdown()
         */
        public void shutdown() {
            e.shutdown();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.ExecutorService#shutdownNow()
         */
        public List<Runnable> shutdownNow() {
            return e.shutdownNow();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.ExecutorService#isShutdown()
         */
        public boolean isShutdown() {
            return e.isShutdown();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.ExecutorService#isTerminated()
         */
        public boolean isTerminated() {
            return e.isTerminated();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.ExecutorService#awaitTermination(long,
         * java.util.concurrent.TimeUnit)
         */
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return e.awaitTermination(timeout, unit);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.AbstractExecutorService#submit(java.lang.Runnable)
         */
        public Future<?> submit(Runnable task) {
            return e.submit(task);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.AbstractExecutorService#submit(java.util.concurrent.Callable)
         */
        public <T> Future<T> submit(Callable<T> task) {
            return e.submit(task);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.AbstractExecutorService#submit(java.lang.Runnable,
         * java.lang.Object)
         */
        public <T> Future<T> submit(Runnable task, T result) {
            return e.submit(task, result);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.AbstractExecutorService#invokeAll(java.util.Collection)
         */
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
                throws InterruptedException {
            return e.invokeAll(tasks);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.AbstractExecutorService#invokeAll(java.util.Collection, long,
         * java.util.concurrent.TimeUnit)
         */
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout,
                                             TimeUnit unit) throws InterruptedException {
            return e.invokeAll(tasks, timeout, unit);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.AbstractExecutorService#invokeAny(java.util.Collection)
         */
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
                throws InterruptedException, ExecutionException {
            return e.invokeAny(tasks);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.AbstractExecutorService#invokeAny(java.util.Collection, long,
         * java.util.concurrent.TimeUnit)
         */
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return e.invokeAny(tasks, timeout, unit);
        }
    }

    /**
     * The Class FinalizableDelegatedExecutorService.
     */
    public static class FinalizableDelegatedExecutorService extends DelegatedExecutorService {

        /**
         * Instantiates a new finalizable delegated executor service.
         *
         * @param executor the executor
         */
        FinalizableDelegatedExecutorService(ExecutorService executor) {
            super(executor);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#finalize()
         */
        protected void finalize() {
            super.shutdown();
        }
    }

    /**
     * The Class DelegatedScheduledExecutorService.
     */
    static class DelegatedScheduledExecutorService extends DelegatedExecutorService
            implements ScheduledExecutorService {

        /** The e. */
        private final ScheduledExecutorService e;

        /**
         * Instantiates a new delegated scheduled executor service.
         *
         * @param executor the executor
         */
        DelegatedScheduledExecutorService(ScheduledExecutorService executor) {
            super(executor);
            e = executor;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.ScheduledExecutorService#schedule(java.lang.Runnable, long,
         * java.util.concurrent.TimeUnit)
         */
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return e.schedule(command, delay, unit);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.ScheduledExecutorService#schedule(java.util.concurrent.Callable,
         * long, java.util.concurrent.TimeUnit)
         */
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return e.schedule(callable, delay, unit);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable,
         * long, long, java.util.concurrent.TimeUnit)
         */
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period,
                                                      TimeUnit unit) {
            return e.scheduleAtFixedRate(command, initialDelay, period, unit);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable,
         * long, long, java.util.concurrent.TimeUnit)
         */
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,
                                                         long delay, TimeUnit unit) {
            return e.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    }
}
