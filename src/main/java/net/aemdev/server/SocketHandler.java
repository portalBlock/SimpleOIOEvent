package net.aemdev.server;

/**
 * Created by hcherndon on 3/9/14.
 */
public class SocketHandler<T> {
    private final SocketWrapper socketWrapper;
    public SocketHandler(SocketWrapper socketWrapper) {
        this.socketWrapper = socketWrapper;
    }

    public void socketRead(T readObject){ }

    public void exception(Throwable throwable) { }

    public void activive(){ }

    public void inactive(Throwable cause){ }

    public final void writeObject(Object object){
        socketWrapper.write(object);
    }

    public final void close(){
        socketWrapper.close();
    }

    public final SocketWrapper getContextSocketWrapper(){
        return socketWrapper;
    }

    protected final void callRead(Object data){
        socketRead((T) data);
    }

    protected final void callException(Throwable throwable){
        exception(throwable);
    }

    protected final void callActive(){
        activive();
    }

    protected final void callInactive(Throwable cause){
        inactive(cause);
    }
}
