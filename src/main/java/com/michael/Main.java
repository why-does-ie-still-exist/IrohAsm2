package com.michael;

import com.lang.IrohAsmBaseListener;
import com.lang.IrohAsmLexer;
import com.lang.IrohAsmParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

    if (args.length == 0 || args.length > 2) { // Check number of arguments
      System.out.println("You need one or two files as an argument.");
      System.exit(2); // ERROR_FILE_NOT_FOUND Code
    }

    if (args.length == 1
        && args[0].toLowerCase(Locale.ROOT).equals("-h")) { // check if using help option
      printHelp();
      System.exit(0);
    }

    File outFile;
    if (args.length == 2) { // create ouput file with possible default filename
      outFile = new File(args[1]);
    } else {
      outFile = new File("program.dat");
    }

    PrintWriter outWriter = null; // create output PrintWriter
    try {
      outWriter = new PrintWriter(outFile);
    } catch (FileNotFoundException e) {
      System.out.println("Output File has a problem.");
      e.printStackTrace();
      System.exit(2); // ERROR_FILE_NOT_FOUND Code
    }

    // make sure to change to make case-insensitive(ie force all letters to lowercase)
    String fileContents = Files.readString(Paths.get(args[0]));

    OffsetCalcListener offsetListener = runOffsetPass(fileToTokenStream(fileContents));

    String binary =
        runMainPass(
            fileToTokenStream(fileContents),
            offsetListener.getOffset(),
            offsetListener.getLabels());

    outWriter.print(binary);
    outWriter.close();
  }

  public static void printHelp() {
    System.out.println(
        "This assembler takes one or two arguments: first an assembly input,"
            + "and possibly a second argument for an output file. If not provided,"
            + "the program will write out to program.dat in the current working directory.");
    System.out.println("Example usage:");
    System.out.println("java -jar IrohAsm.jar input.asm output.dat");
  }

  public static CommonTokenStream fileToTokenStream(String fileContents) {
    var fileStream = CharStreams.fromString(fileContents.toLowerCase(Locale.ROOT) + "\n");
    var lexer = new IrohAsmLexer(fileStream);
    return new CommonTokenStream(lexer);
  }

  public static OffsetCalcListener runOffsetPass(CommonTokenStream tokenStream) {
    var offsetPassParser = new IrohAsmParser(tokenStream);
    var offsetCalcListener = new OffsetCalcListener(offsetPassParser);
    return ((OffsetCalcListener) (runPass(offsetPassParser, offsetCalcListener)));
  }

  public static String runMainPass(
      CommonTokenStream tokenStream, int dataOffset, HashMap<String, Operand> labels) {
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
