package com.pmeade.websocket.example;

import com.pmeade.websocket.io.WebSocketServerOutputStream;
import com.pmeade.websocket.net.WebSocket;
import com.pmeade.websocket.net.WebSocketServerSocket;
import com.pmeade.websocket.example.WebSocketConsumerThread;
import com.pmeade.websocket.example.StringMessageQueue;
import com.pmeade.websocket.example.ByteAccumulator;
import com.pmeade.websocket.io.LineInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Arrays;

/**
 * web socket producer thread
 * accepts content from web sockets and places them in the queue
 */
public class WebSocketThread extends Thread {
    public WebSocketThread(WebSocket socket, StringMessageQueue messageQueue, ByteAccumulator buffer) {
        this.webSocket = socket;
        this.messageQueue = messageQueue;
        this.buffer = buffer;
    }
    
    @Override
    public void run() {
        try {
            InputStream wsis = webSocket.getInputStream();
            LineInputStream line = new LineInputStream(wsis);
            String lineStr = "";
            while (finished == false) {
              lineStr = line.readLine();
              messageQueue.push(lineStr);
            }
        } catch (IOException e) {
            finished = true;
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace(System.err);
        } catch(InterruptedException e) {
            finished = true;
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace(System.err);
        }
        try {
            webSocket.close();
        } catch (IOException e) {
            finished = true;
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace(System.err);
        }
    }

    public void finish() {
        finished = true;
    }
    
    private boolean finished = false;
    
    private final WebSocket webSocket;
    private StringMessageQueue messageQueue;
    private ByteAccumulator buffer;
}
