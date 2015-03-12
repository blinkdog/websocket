package com.pmeade.websocket.example;

import com.pmeade.websocket.io.WebSocketServerOutputStream;
import com.pmeade.websocket.net.WebSocket;
import com.pmeade.websocket.net.WebSocketServerSocket;
import com.pmeade.websocket.example.WebSocketConsumerThread;
import com.pmeade.websocket.example.StringMessageQueue;
import com.pmeade.websocket.example.ByteAccumulator;
import com.pmeade.websocket.example.WebSocketThread;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Arrays;

/**
 * @author pmeade
 */
public class EchoServerSync {
    public static final int PORT = 8080;
    
    public static void main(String[] args) {
        EchoServerSync echoServer = new EchoServerSync();
        try {
            echoServer.doIt();
        } catch(Exception e) {
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace(System.err);
        }
    }
    
    public void doIt() throws Exception
    {
        ServerSocket serverSocket = new ServerSocket(PORT);
        WebSocketServerSocket webSocketServerSocket
                = new WebSocketServerSocket(serverSocket);
        StringMessageQueue messageQueue = new StringMessageQueue();
        LinkedList<WebSocket> connections = new LinkedList<WebSocket>();
        ByteAccumulator buffer = new ByteAccumulator();
        new WebSocketConsumerThread(messageQueue, connections).start();
        while(finished == false) {
            WebSocket socket = webSocketServerSocket.accept();
            connections.add(socket);
            new WebSocketThread(socket, messageQueue, buffer).start();
        }
    }
    
    public void finish() {
        finished = true;
    }
    
    private boolean finished = false;
}
