package net.aemdev.server;

import java.util.concurrent.ExecutorService;

/**
 * Created by hcherndon on 3/9/14.
 */
public class EventFiring {
    private ExecutorService internalService;
    private SocketHandler handler;

    public EventFiring(ExecutorService internalService, SocketHandler handler) {
        this.internalService = internalService;
        this.handler = handler;
    }

    protected void fireChannelRead(final Object object){
        internalService.execute(new Runnable() {
            @Override
            public void run() {
                handler.callRead(object);
            }
        });
    }

    protected void fireException(final Throwable throwable){
        internalService.execute(new Runnable() {
            @Override
            public void run() {
                handler.callException(throwable);
            }
        });
    }

    protected void fireActive(){
        internalService.execute(new Runnable() {
            @Override
            public void run() {
                handler.callActive();
            }
        });
    }

    protected void fireInactive(final Throwable cause){
        internalService.execute(new Runnable() {
            @Override
            public void run() {
                handler.callInactive(cause);
            }
        });
    }
}
