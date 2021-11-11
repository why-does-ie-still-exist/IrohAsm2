package com.michael;

import java.util.Locale;

public class Operand {
  private final char type;
  private final char value;

  public Operand(char type, char value) {
    if (type == 'r' || type == 'm' || type == 'i') {
      this.type = type;
      this.value = value;
    } else {
      throw new IllegalArgumentException("Operand must be a reg, mem, or imm");
    }
  }

  public Operand(String s) {
    switch (s.toLowerCase(Locale.ROOT)) {
      case "ab":
        type = 'r';
        value = (char) 0b00;
        return;
      case "bb":
        type = 'r';
        value = (char) 0b01;
        return;
      case "cb":
        type = 'r';
        value = (char) 0b10;
        return;
      case "db":
        type = 'r';
        value = (char) 0b11;
        return;
    }
    if (s.charAt(0) == '@') {
      this.type = 'm';
      this.value = (char) Integer.parseInt(s.substring(1));
    } else {
      int radix = 0;
      boolean hasRadix = true;
      switch (s.charAt(s.length() - 1)) {
        case 'h':
          radix = 16;
          break;
        case 'b':
          radix = 2;
          break;
        case 'd':
          radix = 10;
          break;
        default:
          radix = 10;
          hasRadix = false;
          break;
      }
      if (s.charAt(0) == '+' || s.charAt(0) == '-') {
        try {
          String bitString;
          if (hasRadix) {
            bitString =
                Integer.toBinaryString(Integer.parseInt(s.substring(0, s.length() - 1), radix));
          } else {
            bitString = Integer.toBinaryString(Integer.parseInt(s, radix));
          }
          bitString =
              s.charAt(0) == '+'
                  ? "00000000".substring(bitString.length()) + bitString
                  : bitString.substring(0, 8);
          char value = (char) Integer.parseInt(bitString, 2);
          this.type = 'i';
          this.value = value;
          return;
        } catch (NumberFormatException n) {
          throw new IllegalArgumentException("Operand could not be parsed");
        }
      } else {
        try {
          this.type = 'i';
          if (hasRadix) {
            this.value = (char) Integer.parseInt(s.substring(0, s.length() - 1), radix);
          } else {
            this.value = (char) Integer.parseInt(s, radix);
          }
        } catch (NumberFormatException n) {
          throw new IllegalArgumentException("Operand could not be parsed");
        }
      }
    }
  }

  public char getType() {
    return type;
  }

  public char getValue() {
    return value;
  }
}
