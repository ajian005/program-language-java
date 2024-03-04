package com.jdk.concurrent;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
/**
 * 线程池使用
 */
public class ThreadPoolDemo {

    public static void main(String[] args) {
        ExecutorService exec = getExecutorServiceFactory("");
        IntStream.range(0, 10).mapToObj(NapTask::new).forEach(exec::execute);
        System.out.println("All tasks submitted");
        exec.shutdown();
        while (!exec.isTerminated()) {
            System.out.println(
                    Thread.currentThread().getName() + " awaiting termination"
            );
            new Nap(0.1);
        }
    }

    public static  ExecutorService getExecutorServiceFactory(String type) {
        ExecutorService executorService = null;
        switch (type) {
            case "SingleThreadExecutor":
                executorService = Executors.newSingleThreadExecutor(); break;
            case "SingleThreadScheduledExecutor":
                executorService = Executors.newSingleThreadScheduledExecutor(); break;
            case "ScheduledThreadPool":
                executorService = Executors.newScheduledThreadPool(1); break;
            case "CachedThreadPool":
                executorService = Executors.newCachedThreadPool(); break;
            case "FixedThreadPool":
                executorService = Executors.newFixedThreadPool(1); break;
            case "WorkStealingPool":
                executorService = Executors.newWorkStealingPool(1); break;
            default:
                // "ThreadPoolExecutor":
                executorService = new ThreadPoolExecutor(
                        1,
                        2,
                        0L,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        Executors.defaultThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy()  );
                break;
        }
        return executorService;
    }

    static class DefaultThreadFactory implements ThreadFactory {
        ThreadGroup group = null;
        String namePrefix = "";
        private final AtomicInteger poolNumber = new AtomicInteger(1);
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + poolNumber.getAndIncrement() +"-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,namePrefix + threadNumber.getAndIncrement(),0);
            if (t.isDaemon()) t.setDaemon(false); //是否守护线程
            if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY); //线程优先级
            return t;
        }
    }
}