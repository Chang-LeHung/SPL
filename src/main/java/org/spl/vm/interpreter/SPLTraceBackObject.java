package org.spl.vm.interpreter;

import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.objects.SPLObject;


public class SPLTraceBackObject extends SPLObject {

  private String errorMessage;
  private SPLTraceBackObject next;
  private final DefaultEval eval;
  private final byte[] debugInfo;
  private final byte[] position;
  private int pos;
  private int cursor;

  public SPLTraceBackObject(DefaultEval eval) {
    super(SPLTraceBackType.getInstance());
    this.eval = eval;
    SPLCodeObject codeObject = eval.getCodeObject();
    debugInfo = codeObject.getDebugInfo();
    position = codeObject.getLenColumn();
    pos = 0;
    doTrace();
  }

  private void doTrace() {
    int endPC = eval.getPC();
    int ins = 0;
    int pc = 0;
    byte[] code = eval.getCode();
    while (pc < endPC) {
      switch (Evaluation.opcode[code[pc++] & 0xff]) {
        case LONG_JUMP, JUMP_ABSOLUTE -> {
          pc += 3;
        }
        default -> {
          if (code[pc] == -1) {
            pc += 3;
          } else {
            pc++;
          }
        }
      }
      ins++;
    }
    SPLCodeObject codeObject = eval.getCodeObject();
    cursor = 0;
    int line = codeObject.getFirstLineNo();
    int count = 0;
    while (count < ins) {
      count += getValidCount();
      line += getValidCount();
    }
    StringBuilder builder = new StringBuilder();
    String codeText = eval.getSourceCode().get(line - 1);
    StringBuilder tip = new StringBuilder(" ".repeat(codeText.length()));
    builder.append("File \"")
        .append(codeObject.getFilename())
        .append("\", line ")
        .append(line)
        .append(", in ")
        .append(codeObject.getName())
        .append("\n\t")
        .append(codeText.strip())
        .append("\n");
    int leftBoundary = 0;
    while (codeText.charAt(leftBoundary) == ' ')
      leftBoundary++;
    iterateLenColumnTo(ins - 1);
    int len = getArg();
    int column = getArg();
    for (int i = 0; i < len; i++) {
      tip.setCharAt(column + i, '^');
    }
    tip.delete(0, leftBoundary);
    errorMessage = builder.append("\t").append(tip).toString();
  }

  private void iterateLenColumnTo(int n) {
    for (int i = 0; i < n; i++) {
      getArg(); // len
      getArg(); // column
    }
  }

  private int getArg() {
    if (position[pos] == -1) {
      pos++;
      int arg = position[pos++];
      arg <<= 8;
      arg |= (position[pos++] & 0xff);
      return arg;
    } else {
      return position[pos++];
    }
  }

  private int getValidCount() {
    if (debugInfo[cursor] == -1) {
      cursor++;
      int arg = debugInfo[cursor++];
      arg <<= 8;
      arg |= (debugInfo[cursor++] & 0xff);
      return arg;
    } else {
      return debugInfo[cursor++];
    }
  }

  public DefaultEval getEval() {
    return eval;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public SPLTraceBackObject getNext() {
    return next;
  }

  public void setNext(SPLTraceBackObject next) {
    this.next = next;
  }
}
