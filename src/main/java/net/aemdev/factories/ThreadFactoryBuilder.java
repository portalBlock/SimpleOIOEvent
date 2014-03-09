package net.aemdev.factories;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hcherndon on 3/9/14.
 */
public class ThreadFactoryBuilder {
    private AtomicInteger index = new AtomicInteger(0);
    private String name = "thread-%d";
    private ThreadGroup group = new ThreadGroup("default");
    private boolean daemon = false;
    private int priority = Thread.NORM_PRIORITY;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            Logger.getGlobal().log(Level.SEVERE, String.format("Uncaught Exception in Thread %s", thread.getName()), throwable);
        }
    };

    public ThreadFactoryBuilder() {}
    public static ThreadFactoryBuilder newBuilder(){
        return new ThreadFactoryBuilder();
    }

    public ThreadFactoryBuilder name(String name){
        this.name = name;
        return this;
    }

    public ThreadFactoryBuilder group(ThreadGroup group){
        this.group = group;
        return this;
    }

    public ThreadFactoryBuilder daemon(boolean daemon){
        this.daemon = daemon;
        return this;
    }

    public ThreadFactoryBuilder priority(int priority){
        this.priority = priority;
        return this;
    }

    public ThreadFactoryBuilder uncaughtException(Thread.UncaughtExceptionHandler handler){
        this.uncaughtExceptionHandler = handler;
        return this;
    }


    public ThreadFactory build(){
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(group, runnable);
                thread.setName(String.format(name, index.incrementAndGet()));
                thread.setDaemon(daemon);
                thread.setPriority(priority);
                thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
                return thread;
            }
        };
    }
}
