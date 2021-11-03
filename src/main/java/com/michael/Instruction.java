package com.michael;

import java.util.Locale;

public class Instruction {
  private final String instructionID;
  private final String orderString;
  private String regNum;
  private String value;

  public Instruction(String mnemonic, Operand dest) {
    this.instructionID = getInstructionId(mnemonic);
    if (dest.getType() == 'r') {
      orderString = "00";
      regNum = Integer.toBinaryString(dest.getValue());
      regNum = "00".substring(regNum.length()) + regNum;
      value = "00000000";
    } else if (dest.getType() == 'i' || dest.getType() == 'm') {
      value = Integer.toBinaryString(dest.getValue());
      value = "00000000".substring(value.length()) + value;
      regNum = "00";
      orderString = dest.getType() == 'm' ? "01" : "11";
    } else {
      throw new IllegalArgumentException("Operands Malformed");
    }
  }

  public Instruction(String mnemonic, Operand dest, Operand src) {
    this.instructionID = getInstructionId(mnemonic);
    if (dest.getType() == 'm' && src.getType() == 'r') {
      orderString = "10";
      value = Integer.toBinaryString(dest.getValue());
      value = "00000000".substring(value.length()) + value;
      regNum = Integer.toBinaryString(src.getValue());
      regNum = "00".substring(regNum.length()) + regNum;
    } else {
      value = Integer.toBinaryString(src.getValue());
      value = "00000000".substring(value.length()) + value;
      regNum = Integer.toBinaryString(dest.getValue());
      regNum = "00".substring(regNum.length()) + regNum;
      if (dest.getType() == 'r' && src.getType() == 'i') {
        orderString = "11";
      } else if (dest.getType() == 'r' && src.getType() == 'm') {
        orderString = "01";
      } else if (dest.getType() == 'r' && src.getType() == 'r') {
        orderString = "00";
      } else {
        throw new IllegalArgumentException("Instruction Malformed");
      }
    }
  }

  @Override
  public String toString() {
    return orderString + regNum + instructionID + value;
  }

  public static String getInstructionId(String mnemonic) {
    switch (mnemonic.toLowerCase(Locale.ROOT).trim()){
      case "add" : return "1000";
      case "sub" : return "1001";
      case "mov" : return "0000";
      case "jmp" : return "0001";
      case "jez" : return "0010";
      case "get" : return "0011";
      case "wrt" : return "0100";
    }
    throw new IllegalArgumentException("mnemonic not a valid mnemonic.");
  }
}
