package net.aemdev.server;

import java.util.concurrent.ExecutorService;

/**
 * Created by hcherndon on 3/9/14.
 */
public class EventFiring {
    private ExecutorService internalService;
    private SocketHandler handler = null;

    public EventFiring(ExecutorService internalService) {
        this.internalService = internalService;
    }

    protected void fireChannelRead(final Object object){
        if(handler == null)
            return;
        internalService.execute(new Runnable() {
            @Override
            public void run() {
                handler.callRead(object);
            }
        });
    }

    protected void fireException(final Throwable throwable){
        if(handler == null)
            return;
        internalService.execute(new Runnable() {
            @Override
            public void run() {
                handler.callException(throwable);
            }
        });
    }

    protected void fireActive(){
        if(handler == null)
            return;
        internalService.execute(new Runnable() {
            @Override
            public void run() {
                handler.callActive();
            }
        });
    }

    protected void fireInactive(final Throwable cause){
        if(handler == null)
            return;
        internalService.execute(new Runnable() {
            @Override
            public void run() {
                handler.callInactive(cause);
            }
        });
    }

    public void setHandler(SocketHandler handler) {
        this.handler = handler;
    }

}
