package net.aemdev.server;

import net.aemdev.factories.SocketHandlerProvider;
import net.aemdev.factories.ThreadFactoryBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hcherndon on 3/9/14.
 */
public class ServerChannel implements Runnable {
    private static volatile boolean running = false;
    private final String bindIp;
    private final int port;
    private final int timeout;
    private SocketHandlerProvider provider;

    public static boolean isRunning() {
        return running;
    }

    private ExecutorService workerThreads = Executors.newCachedThreadPool(ThreadFactoryBuilder.newBuilder().name("AEMDev WorkerThread #%d").build());
    private ExecutorService eventThreads = Executors.newFixedThreadPool(1, ThreadFactoryBuilder.newBuilder().name("AEMDev EventThread #%d").build());

    private ConcurrentHashMap<Socket, SocketWrapper> mapping = new ConcurrentHashMap<Socket, SocketWrapper>();

    public ServerChannel(String bindIp, int port, int timeout, SocketHandlerProvider provider){
        this.bindIp = bindIp;
        this.port = port;
        this.timeout = timeout;
        this.provider = provider;
    }

    private ServerSocket serverSocket;

    public void start(){
        if(running)
            throw new IllegalStateException("Server already running!");
        workerThreads.execute(this);
    }

    public void stop(){
        if(!running)
            throw new IllegalStateException("Server is already stopped/has never been started!");
        running = false;
        for(SocketWrapper wrapper : mapping.values()){
            wrapper.close();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            //It will be ok.
        } catch (Exception e){
            e.printStackTrace(); //This won't be ok. ;_;
        }
    }

    protected void removeSocket(Socket socket){
        mapping.remove(socket);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(bindIp, port));
            serverSocket.setReceiveBufferSize(8192);
            serverSocket.setPerformancePreferences(0, 1 ,2);
            running = true;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (running){
            try {
                Socket incoming = serverSocket.accept();
                incoming.setSoTimeout(timeout);
                SocketWrapper wrapper = new SocketWrapper(this, incoming, new EventFiring(eventThreads));
                wrapper.getEventFiring().setHandler(provider.getNewSocketHandler(wrapper));
                workerThreads.execute(wrapper);
                mapping.put(incoming, wrapper);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
