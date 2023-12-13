package org.spl.vm.internal.objs;

import org.spl.vm.internal.typs.SPLFrameType;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLStringObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPLFrameObject extends SPLObject {
  protected final Map<SPLObject, SPLObject> locals;

  protected final SPLCodeObject codeObject;

  protected final Map<SPLObject, SPLObject> globals;

  protected final byte[] code;
  protected final SPLObject[] evalStack;
  protected final SPLStringObject[] varnames;
  protected int pc;
  protected int top;

  protected SPLObject[] constants;

  protected long insNumExecuted;

  public SPLFrameObject(Map<SPLObject, SPLObject> locals, Map<SPLObject, SPLObject> globals, SPLCodeObject codeObj) {
    super(SPLFrameType.getInstance());
    this.locals = locals;
    this.globals = globals;
    this.codeObject = codeObj;
    pc = 0;
    evalStack = new SPLObject[codeObj.getMaxStackSize()];
    code = codeObj.getCode();
    insNumExecuted = 0;
    varnames = codeObj.getVarnames();
    constants = codeObj.getConstants();
  }

  public SPLFrameObject(SPLCodeObject codeObj) {
    super(SPLFrameType.getInstance());
    this.codeObject = codeObj;
    this.locals = new HashMap<>();
    this.globals = this.locals;

    pc = 0;
    evalStack = new SPLObject[codeObj.getMaxStackSize()];
    code = codeObj.getCode();
    insNumExecuted = 0;
    varnames = codeObj.getVarnames();
    constants = codeObj.getConstants();
  }

  public Map<SPLObject, SPLObject> getLocals() {
    return locals;
  }

  public SPLCodeObject getCodeObject() {
    return codeObject;
  }

  @Override
  public Map<SPLObject, SPLObject> getGlobals() {
    return globals;
  }

  public byte[] getCode() {
    return code;
  }

  public int getPC() {
    return pc;
  }

  public SPLObject[] getEvalStack() {
    return evalStack;
  }

  public SPLStringObject[] getVarnames() {
    return varnames;
  }

  public int getTop() {
    return top;
  }

  public SPLObject[] getConstants() {
    return constants;
  }

  public long getInsNumExecuted() {
    return insNumExecuted;
  }

  public String getFilename() {
    return codeObject.getFilename();
  }

  public List<String> getSourceCode() {
    return codeObject.getSourceCode();
  }
}
