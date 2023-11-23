package org.spl.vm.internal.objs;

import org.spl.vm.internal.typs.SPLCodeType;
import org.spl.vm.objects.*;

import java.util.Arrays;
import java.util.Map;

public class SPLCodeObject extends SPLObject {
  private final int args;
  private final String filename;
  private final int firstLineNo;
  private final byte[] code;
  private final byte[] lenColumn;
  private final byte[] debugInfo;
  private final SPLObject[] constants;
  private final int maxStackSize;

  public SPLCodeObject(int args,
                       int maxStackSize,
                       String filename,
                       int firstLineNo,
                       byte[] code,
                       byte[] lenColumn,
                       byte[] debugInfo,
                       Map<Object, Integer> constantTable) {
    super(SPLCodeType.getInstance());
    this.args = args;
    this.maxStackSize = maxStackSize;
    this.filename = filename;
    this.firstLineNo = firstLineNo;
    this.code = code;
    this.lenColumn = lenColumn;
    this.debugInfo = debugInfo;
    constants = new SPLObject[constantTable.size()];
    constantTable.forEach((k, v) -> {
      if (k instanceof Integer) {
        constants[v] = getSPL((int) k);
      } else if (k instanceof Long) {
        constants[v] = getSPL((long) k);
      } else if (k instanceof Double) {
        constants[v] = getSPL((double) k);
      } else if (k instanceof String) {
        constants[v] = getSPL((String) k);
      } else if (k instanceof SPLBoolObject b) {
        constants[v] = b;
      }
    });
  }

  public int getArgs() {
    return args;
  }

  public String getFilename() {
    return filename;
  }

  public int getFirstLineNo() {
    return firstLineNo;
  }

  public byte[] getCode() {
    return code;
  }

  public byte[] getLenColumn() {
    return lenColumn;
  }

  public byte[] getDebugInfo() {
    return debugInfo;
  }

  @Override
  public String toString() {
    return "SPLCodeObject{\n" +
        "args=" + args +
        ",\nfilename='" + filename + '\'' +
        ",\nfirstLineNo=" + firstLineNo +
        ",\ncode=" + Arrays.toString(code) +
        ",\nlenColumn=" + Arrays.toString(lenColumn) +
        ",\ndebugInfo=" + Arrays.toString(debugInfo) +
        ",\nconstants=" + Arrays.toString(constants) +
        '\n' + '}';
  }

  private SPLLongObject getSPL(int val) {
    return SPLLongObject.create(val);
  }

  private SPLLongObject getSPL(long val) {
    return SPLLongObject.create(val);
  }

  private SPLFloatObject getSPL(double val) {
    return new SPLFloatObject(val);
  }

  private SPLStringObject getSPL(String val) {
    return new SPLStringObject(val);
  }

  public SPLObject[] getConstants() {
    return constants;
  }

  public int getMaxStackSize() {
    return maxStackSize;
  }
}
