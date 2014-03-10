package net.aemdev.factories;

import net.aemdev.server.SocketHandler;
import net.aemdev.server.SocketWrapper;

/**
 * Created by hcherndon on 3/9/14.
 */
public abstract class SocketHandlerProvider {
    public abstract SocketHandler getNewSocketHandler(SocketWrapper socketWrapper);
}
