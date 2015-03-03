package com.pmeade.websocket.example;

import com.pmeade.websocket.io.WebSocketServerOutputStream;
import com.pmeade.websocket.net.WebSocket;
import com.pmeade.websocket.net.WebSocketServerSocket;
import com.pmeade.websocket.example.WebSocketConsumerThread;
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

/**
 * web socket producer thread
 * accepts content from web sockets and places them in the queue
 */
class WebSocketThread extends Thread {
    public WebSocketThread(WebSocket socket, StringMessageQueue messageQueue, ByteAccumulator buffer) {
        this.webSocket = socket;
        this.messageQueue = messageQueue;
        this.buffer = buffer;
    }
    
    @Override
    public void run() {
        try {
            InputStream wsis = webSocket.getInputStream();
            byte[] bufferContent;
            int data = wsis.read();
            while (finished == false && data != -1) {
                if(data == 10) {
                  buffer.add((byte)0);
                  bufferContent = new byte[buffer.size()];
                  buffer.toNativeArray(bufferContent);
                  messageQueue.push(new String(bufferContent));
                  buffer.clear();
                  bufferContent = null;
                } else {
                  buffer.add((byte)data);
                }
                data = wsis.read();
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

/**
 * message queue
 * keeps received messages
 */
class StringMessageQueue {
  private Queue<String> q = new LinkedList<String>();
  synchronized String pop() throws InterruptedException {
    while(q.isEmpty()) {
      wait();
    }
    String value = q.remove();
    notifyAll();
    return value;
  }
  synchronized void push(String message) throws InterruptedException {
    q.add(message);
    notifyAll();
  }
}

/**
 * byte accumulator
 * accumulates bytes received from clients
 */
class ByteAccumulator {
  LinkedList<Byte> buffer = new LinkedList<Byte>();
  public void add(byte thing) {
    buffer.add(thing);
  }
  public void toNativeArray(byte[] buff) {
    for(int i = 0; i < buffer.size(); i++) {
      buff[i] = buffer.get(i);
    }
  }
  public int size() {
    return buffer.size();
  }
  public void clear() {
    buffer.clear();
  }
}
