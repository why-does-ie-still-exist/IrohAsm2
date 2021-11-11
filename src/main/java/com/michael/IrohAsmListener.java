package com.michael;

import com.lang.IrohAsmBaseListener;
import com.lang.IrohAsmParser;
import com.lang.IrohAsmParser.AssignmentContext;
import com.lang.IrohAsmParser.FirstoperandContext;
import com.lang.IrohAsmParser.ImmContext;
import com.lang.IrohAsmParser.LabelContext;
import com.lang.IrohAsmParser.RangeContext;
import com.lang.IrohAsmParser.SecondoperandContext;
import java.util.ArrayList;
import java.util.HashMap;
import org.antlr.v4.misc.OrderedHashMap;

public class IrohAsmListener extends IrohAsmBaseListener {
  private final OrderedHashMap<String, DataRange> dataranges;
  private final ArrayList<Instruction> instructions;
  private final HashMap<String, Operand> labels;
  private int startingAddress;

  public IrohAsmListener(int dataOffset, HashMap<String, Operand> labels) {
    this.startingAddress = dataOffset;
    this.labels = labels;
    this.instructions = new ArrayList<Instruction>();
    this.dataranges = new OrderedHashMap<String, DataRange>();
  }

  @Override
  public void enterInstruction(IrohAsmParser.InstructionContext ctx) {
    String mnemonic = ctx.mnemonic().getText();
    Operand first = processFirstOperand(ctx.firstoperand());
    Operand second = processSecondOperand(ctx.secondoperand());
    if (first == null) {
      instructions.add(new Instruction(mnemonic, second));
    } else {
      instructions.add(new Instruction(mnemonic, first, second));
    }
  }

  private Operand processFirstOperand(FirstoperandContext ctx) {
    if (ctx != null) {
      if (ctx.range() != null) {
        return dereferenceRange(ctx.range());
      }
      if (ctx.register() != null) {
        return new Operand(ctx.register().getText());
      }
      if (ctx.mem() != null) {
        return new Operand(ctx.mem().getText());
      }
    }
    return null;
  }

  private Operand processSecondOperand(SecondoperandContext ctx) {
    if (ctx != null) {
      if (ctx.range() != null) {
        return dereferenceRange(ctx.range());
      }
      if (ctx.label() != null) {
        return dereferenceLabel(ctx.label());
      }
      if (ctx.register() != null) {
        return new Operand(ctx.register().getText());
      }
      if (ctx.mem() != null) {
        return new Operand(ctx.mem().getText());
      }
      if (ctx.imm() != null) {
        return new Operand(ctx.imm().getText());
      }
    }
    return null;
  }

  private Operand dereferenceRange(RangeContext ctx) {
    DataRange d = dataranges.get(ctx.identifier().getText());
    int index = Integer.parseInt(ctx.imm().getText());
    return d.getNthOffset(index);
  }

  private Operand dereferenceLabel(LabelContext ctx) {
    return labels.get(ctx.identifier().getText());
  }

  @Override
  public void enterRangedec(IrohAsmParser.RangedecContext ctx) {
    RangeContext range = ctx.range();
    var rangeInQuestion = new DataRange(startingAddress, Integer.parseInt(range.imm().getText()));
    startingAddress = rangeInQuestion.getNextAddress();
    AssignmentContext assignmentContext = ctx.assignment();
    if (assignmentContext != null) {
      for (Object i : assignmentContext.children) {
        if (i instanceof ImmContext) {
          var immediateText = ((ImmContext) i).getText();
          var maybeRadix = immediateText.charAt(immediateText.length() - 1);
          if (maybeRadix != 'h' && maybeRadix != 'd' && maybeRadix != 'b') {
            rangeInQuestion.appendData(new Operand(immediateText + "d"));
          } else {
            rangeInQuestion.appendData(new Operand(immediateText));
          }
        }
      }
    }
    dataranges.put(range.identifier().getText(), rangeInQuestion);
  }

  public String getBinary() {
    var stringBuilder = new StringBuilder();
    for (Instruction i : instructions) {
      stringBuilder.append(i);
      stringBuilder.append("\n");
    }
    for (DataRange d : dataranges.values()) {
      stringBuilder.append(d.getBinData());
    }
    return stringBuilder.toString();
  }
}
