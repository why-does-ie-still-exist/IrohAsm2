package com.michael;

public class SyntaxError extends RuntimeException {

  public SyntaxError(int lineNo) {
    super("There was an error on line no " + lineNo);
  }
}
