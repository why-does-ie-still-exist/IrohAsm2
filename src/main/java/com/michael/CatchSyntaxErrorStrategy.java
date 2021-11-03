package com.michael;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

public class CatchSyntaxErrorStrategy extends BailErrorStrategy {
  @Override
  public Token recoverInline(Parser recognizer) throws RecognitionException {
    throw new SyntaxError(recognizer.getContext().start.getLine());
  }
}
