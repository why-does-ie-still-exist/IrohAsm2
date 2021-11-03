package com.michael;

import java.util.ArrayList;

public class DataRange {
  private final int startAddress;
  private final int length;
  private ArrayList<Operand> data;

  /**
   * Constructor for a data range
   * @param startAddress The first address of this region of memory
   * @param length The number of bytes in the region of memory.
   */
  public DataRange(int startAddress, int length) {
    this.data = new ArrayList<Operand>();
    if (startAddress + length > 255) {
      throw new IllegalArgumentException("Memory range exceeds available memory");
    }
    this.length = length;
    this.startAddress = startAddress;
  }

  /**
   * Gets the next available address after this region of memory.
   * @return the next available address
   */
  public int getNextAddress() {
    return startAddress + length;
  }

  /**
   * Returns a memory operand that corresponds to the region in memory at the zero-indexed nth byte in this region of memory
   * @param n
   * @return
   */
  public Operand getNthOffset(int n) {
    if (n >= length) {
      throw new IllegalArgumentException("Argument exceeds defined length");
    }
    return new Operand("@" + (startAddress + n));
  }

  private String getNthValue(int n) {
    if (n >= data.size()) {
      throw new IllegalArgumentException("Argument exceeds defined length");
    }
    Operand someData = data.get(n);
    String value = Integer.toBinaryString(someData.getValue());
    value = "0000000000000000".substring(value.length()) + value;
    return value;
  }

  public String getBinData() {
    var dataString = new StringBuilder();
    for (int i = 0; i < data.size(); i++) {
      dataString.append(this.getNthValue(i));
      dataString.append("\n");
    }
    for (int i = 0; i < length - data.size(); i++) {
      dataString.append("0000000000000000\n");
    }
    return dataString.toString();
  }

  public void appendData(Operand toBeAdded){
    data.add(toBeAdded);
  }
}
