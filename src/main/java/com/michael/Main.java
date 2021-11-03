package com.michael;

import com.lang.IrohAsmBaseListener;
import com.lang.IrohAsmLexer;
import com.lang.IrohAsmParser;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Main {
  public static void main(String[] args) throws Exception {

    // make sure to change to make case-insensitive(ie force all letters to lowercase)
    String fileContents = Files.readString(Paths.get("src/test/assembly/cool.asm"));

    OffsetCalcListener offsetListener = runOffsetPass(fileToTokenStream(fileContents));

    String binary = runMainPass(fileToTokenStream(fileContents), offsetListener.getOffset(),offsetListener.getLabels());

    System.out.println(binary);
  }

  public static CommonTokenStream fileToTokenStream(String fileContents) {
    var fileStream = CharStreams.fromString(fileContents.toLowerCase(Locale.ROOT));
    var lexer = new IrohAsmLexer(fileStream);
    return new CommonTokenStream(lexer);
  }

  public static OffsetCalcListener runOffsetPass(CommonTokenStream tokenStream) {
    var offsetPassParser = new IrohAsmParser(tokenStream);
    var offsetCalcListener = new OffsetCalcListener(offsetPassParser);
    return ((OffsetCalcListener) (runPass(offsetPassParser, offsetCalcListener)));
  }

  public static String runMainPass(CommonTokenStream tokenStream, int dataOffset, HashMap<String, Operand> labels) {
    var mainPassParser = new IrohAsmParser(tokenStream);
    var languageListener = new IrohAsmListener(dataOffset, labels);
    runPass(mainPassParser, languageListener);
    return languageListener.getBinary();
  }

  public static IrohAsmBaseListener runPass(IrohAsmParser parser, IrohAsmBaseListener listener) {
    parser.setErrorHandler(new CatchSyntaxErrorStrategy());
    ParseTree parseTree = parser.main();
    var parseTreeWalker = new ParseTreeWalker();
    parseTreeWalker.walk(listener, parseTree);
    return listener;
  }
}
