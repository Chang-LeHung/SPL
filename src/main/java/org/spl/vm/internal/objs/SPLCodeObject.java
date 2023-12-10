package org.spl.vm.internal.objs;

import org.spl.vm.internal.typs.SPLCodeType;
import org.spl.vm.objects.SPLFloatObject;
import org.spl.vm.objects.SPLLongObject;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLStringObject;

import java.util.Arrays;
import java.util.Map;

public class SPLCodeObject extends SPLObject {
  private final String filename;
  private final int firstLineNo;
  private final byte[] code;
  private final byte[] lenColumn;
  private final byte[] debugInfo;
  private final SPLStringObject[] varnames;
  private final SPLObject[] constants;
  private final int maxStackSize;
  private int args;

  public SPLCodeObject(int args,
                       int maxStackSize,
                       String filename,
                       int firstLineNo,
                       byte[] code,
                       byte[] lenColumn,
                       byte[] debugInfo,
                       Map<Object, Integer> varnames, SPLObject[] constants) {
    super(SPLCodeType.getInstance());
    this.args = args;
    this.maxStackSize = maxStackSize;
    this.filename = filename;
    this.firstLineNo = firstLineNo;
    this.code = code;
    this.lenColumn = lenColumn;
    this.debugInfo = debugInfo;
    this.varnames = new SPLStringObject[varnames.size()];
    varnames.forEach((k, v) -> {
      assert k instanceof String;
      this.varnames[v] = new SPLStringObject((String) k);
    });
    this.constants = constants;
  }

  public static SPLLongObject getSPL(int val) {
    return SPLLongObject.create(val);
  }

  public static SPLLongObject getSPL(long val) {
    return SPLLongObject.create(val);
  }

  public static SPLFloatObject getSPL(double val) {
    return new SPLFloatObject(val);
  }

  public static SPLStringObject getSPL(String val) {
    return new SPLStringObject(val);
  }

  public int getArgs() {
    return args;
  }

  public void setArgs(int args) {
    this.args = args;
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
        ",\nvarnames=" + Arrays.toString(varnames) +
        ",\nmaxStackSize=" + maxStackSize +
        '\n' + '}';
  }

  public SPLStringObject[] getVarnames() {
    return varnames;
  }

  public int getMaxStackSize() {
    return maxStackSize;
  }

  public SPLObject[] getConstants() {
    return constants;
  }
}
