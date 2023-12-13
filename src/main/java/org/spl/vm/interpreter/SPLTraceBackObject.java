package org.spl.vm.interpreter;

import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.objects.SPLObject;


public class SPLTraceBackObject extends SPLObject {

  private String errorMessage;
  private SPLTraceBackObject next;
  private final DefaultEval eval;
  private byte[] debugInfo;
  private int cursor;

  public SPLTraceBackObject(DefaultEval eval) {
    super(SPLTraceBackType.getInstance());
    this.eval = eval;
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
    debugInfo = codeObject.getDebugInfo();
    cursor = 0;
    int pos = codeObject.getFirstLineNo();
    int count = 0;
    while (count < ins) {
      count += getValidCount();
      pos +=  getValidCount();
    }
    errorMessage = "\tFile \"" + codeObject.getFilename() + "\", line " + pos + ", in " + codeObject.getName() + "\n\t\t" + eval.getSourceCode().get(pos - 1).strip();
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
