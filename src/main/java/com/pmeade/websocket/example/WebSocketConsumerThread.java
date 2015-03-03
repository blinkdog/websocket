package com.pmeade.websocket.example;

import com.pmeade.websocket.io.WebSocketServerOutputStream;
import com.pmeade.websocket.net.WebSocket;
import com.pmeade.websocket.net.WebSocketServerSocket;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Arrays;
/**
 * web socket consumer thread
 * takes messages from message queue and sends them to clients
 */
public class WebSocketConsumerThread extends Thread {
  public WebSocketConsumerThread(StringMessageQueue messageQueue, LinkedList<WebSocket> connections) {
    this.messageQueue = messageQueue;
    this.connections = connections;
  }
  public void run() {
    String message = "";
    WebSocket webSocket = null;
    WebSocketServerOutputStream wsos = null;
    while(!finished) {
      try {
        message = messageQueue.pop();
        ListIterator<WebSocket> listIterator = connections.listIterator();
        while(listIterator.hasNext()) {
          webSocket = listIterator.next();
          wsos = webSocket.getOutputStream();
          wsos.writeString(message);
        }
      } catch(IOException e) {
        finished = true;
        System.err.println(e.getLocalizedMessage());
        e.printStackTrace(System.err);
      }
      catch(InterruptedException e) {
        finished = true;
        System.err.println(e.getLocalizedMessage());
        e.printStackTrace(System.err);
      }
    }
  }
  private StringMessageQueue messageQueue;
  private LinkedList<WebSocket> connections;
  private boolean finished = false;
}
