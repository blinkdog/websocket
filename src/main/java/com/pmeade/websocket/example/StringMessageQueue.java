package com.pmeade.websocket.example;

import java.util.LinkedList;
import java.util.Queue;

/**
 * message queue
 * keeps received messages
 */
public class StringMessageQueue {
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
