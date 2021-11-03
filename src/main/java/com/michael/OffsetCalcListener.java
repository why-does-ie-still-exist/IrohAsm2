package com.michael;

import com.lang.IrohAsmBaseListener;
import com.lang.IrohAsmParser;
import java.util.HashMap;

public class OffsetCalcListener extends IrohAsmBaseListener {
  IrohAsmParser parser;
  private int numInstructions;
  private HashMap<String, Operand> labels;

  public OffsetCalcListener(IrohAsmParser parser) {
    this.numInstructions = 0;
    this.parser = parser;
    this.labels = new HashMap<String,Operand>();
  }

  public int getNumInstructions() {
    return numInstructions;
  }

  public HashMap<String, Operand> getLabels() {
    return labels;
  }

  @Override
  public void enterInstruction(IrohAsmParser.InstructionContext ctx) {
    numInstructions += 1;
  }

  public int getOffset() {
    return numInstructions;
  }

  @Override
  public void enterLabelmarker(IrohAsmParser.LabelmarkerContext ctx) {
    labels.put(ctx.identifier().getText(), new Operand('i', (char) numInstructions));
  }
}
