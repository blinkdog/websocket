package com.pmeade.websocket.example;

import java.util.LinkedList;
/**
 * byte accumulator
 * accumulates bytes received from clients
 */
public class ByteAccumulator {
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
