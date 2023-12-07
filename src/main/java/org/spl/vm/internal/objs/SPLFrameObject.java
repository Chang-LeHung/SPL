package org.spl.vm.internal.objs;

import org.spl.vm.internal.typs.SPLFrameType;
import org.spl.vm.objects.SPLObject;

import java.util.HashMap;
import java.util.Map;

public class SPLFrameObject extends SPLObject {
  protected final Map<SPLObject, SPLObject> locals;

  protected final Map<SPLObject, SPLObject> globals;

  protected final byte[] code;

  protected int pc;

  protected final SPLObject[] evalStack;

  protected final SPLObject[] constants;

  protected int top;

  protected long insNumExecuted;

  public SPLFrameObject(Map<SPLObject, SPLObject> locals, Map<SPLObject, SPLObject> globals, SPLCodeObject codeObj) {
    super(SPLFrameType.getInstance());
    this.locals = locals;
    this.globals = globals;
    pc = 0;
    evalStack = new SPLObject[codeObj.getMaxStackSize()];
    code = codeObj.getCode();
    insNumExecuted = 0;
    constants = codeObj.getConstants();
  }

  public SPLFrameObject(SPLCodeObject codeObj) {
    super(SPLFrameType.getInstance());
    this.locals = new HashMap<>();
    this.globals = new HashMap<>();

    pc = 0;
    evalStack = new SPLObject[codeObj.getMaxStackSize()];
    code = codeObj.getCode();
    insNumExecuted = 0;
    constants = codeObj.getConstants();
  }
}
