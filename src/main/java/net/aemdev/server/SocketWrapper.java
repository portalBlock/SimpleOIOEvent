package net.aemdev.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created by hcherndon on 3/9/14.
 */
public class SocketWrapper implements Runnable {
    private ServerChannel handle;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private EventFiring eventFiring;
    private volatile boolean running = false;


    public SocketWrapper(ServerChannel handle, Socket socket, EventFiring eventFiring) {
        this.handle = handle;
        this.socket = socket;
        this.eventFiring = eventFiring;
    }

    public void close(){
        running = false;
        handle.removeSocket(socket);
        try {
            objectInputStream.close();
            objectOutputStream.close();
            socket.close();
        } catch (IOException e) {
            //This is ok.
            return;
        } catch (Exception e){
            eventFiring.fireException(e);
        }
    }

    public void write(Object object) throws IllegalStateException {
        if(!(object instanceof Serializable)){
            throw new IllegalStateException("Serializable Objects only!");
        }
        try {
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            eventFiring.fireInactive(e);
            close();
            return;
        } catch (Exception e){
            eventFiring.fireException(e);
        }
    }

    @Override
    public void run() {
        eventFiring.fireActive();
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            running = true;
        } catch (IOException e) {
            eventFiring.fireInactive(e);
            close();
            return;
        } catch (Exception e){
            eventFiring.fireException(e);
            eventFiring.fireInactive(e);
            close();
            return;
        }
        while (ServerChannel.isRunning() && running){
            try {
                Object read = objectInputStream.readObject();
                eventFiring.fireChannelRead(read);
            } catch (IOException e){
                eventFiring.fireInactive(e);
                close();
                return;
            } catch (Exception e){
                eventFiring.fireException(e);
            }
        }
    }
}
