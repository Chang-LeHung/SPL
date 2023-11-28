package org.spl.vm.interpreter;

import org.spl.vm.builtin.Builtin;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.objects.SPLBoolObject;
import org.spl.vm.objects.SPLNoneObject;
import org.spl.vm.objects.SPLObject;

import java.util.HashMap;
import java.util.Map;

public class DefaultEval implements Evaluation {

  private final Map<SPLObject, SPLObject> locals;
  private final Map<SPLObject, SPLObject> globals;
  private final byte[] code;
  private int pc;
  private final SPLObject[] evalStack;
  private final SPLObject[] constants;
  private int top;

  private long insNumExecuted;

  public DefaultEval(SPLCodeObject codeObj) throws SPLInternalException {
    if (codeObj.getArgs() != 0) {
      throw new SPLInternalException("SPLCodeObject's args must be zero");
    }
    pc = 0;
    evalStack = new SPLObject[codeObj.getMaxStackSize()];
    code = codeObj.getCode();
    locals = new HashMap<>();
    globals = locals;
    insNumExecuted = 0;
    constants = codeObj.getConstants();
  }

  @Override
  public SPLObject evalFrame() {
    try {
      while (pc < code.length) {
        insNumExecuted++;
        switch (code[pc++] & 0xff) {
          case 0 -> { // NOP
            pc++;
          }
          case 1 -> { // ADD
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.add(rhs);
          }
          case 2 -> { // SUB
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.sub(rhs);
          }
          case 3 -> { // MUL
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.mul(rhs);
          }
          case 4 -> { // DIV
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.div(rhs);
          }
          case 5 -> { // MOD
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.mod(rhs);
          }
          case 6 -> { // POWER
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.pow(rhs);
          }
          case 7 -> { // XOR
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.xor(rhs);
          }
          case 8 -> { // LSHIFT
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.lshift(rhs);
          }
          case 9 -> { // RSHIFT
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.rshift(rhs);
          }
          case 10 -> { // U_RSHIFT
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.URshift(rhs);
          }
          case 11 -> { // LSHIFT_ASSIGN
            byte oparg = code[pc++];
            SPLObject rhs = evalStack[--top];
            SPLObject name = constants[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.lshift(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).lshift(rhs));
              continue;
            }
            throw new SPLInternalException("InternalError: not found " + name.str());
          }
          case 12 -> { // RSHIFT_ASSIGN
            byte oparg = code[pc++];
            SPLObject rhs = evalStack[--top];
            SPLObject name = constants[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.rshift(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).rshift(rhs));
              continue;
            }
            throw new SPLInternalException("InternalError: not found " + name.str());
          }
          case 13 -> { // U_RSHIFT_ASSIGN
            byte oparg = code[pc++];
            SPLObject rhs = evalStack[--top];
            SPLObject name = constants[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.URshift(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).URshift(rhs));
              continue;
            }
            throw new SPLInternalException("InternalError: not found " + name.str());
          }
          case 14 -> { // AND_ASSIGN
            byte oparg = code[pc++];
            SPLObject rhs = evalStack[--top];
            SPLObject name = constants[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.and(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).and(rhs));
              continue;
            }
            throw new SPLInternalException("InternalError: not found " + name.str());
          }
          case 15 -> { // OR_ASSIGN
            byte oparg = code[pc++];
            SPLObject rhs = evalStack[--top];
            SPLObject name = constants[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.or(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).or(rhs));
              continue;
            }
            throw new SPLInternalException("InternalError: not found " + name.str());
          }
          case 16 -> { // XOR_ASSIGN
            byte oparg = code[pc++];
            SPLObject rhs = evalStack[--top];
            SPLObject name = constants[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.xor(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).xor(rhs));
              continue;
            }
            throw new SPLInternalException("InternalError: not found " + name.str());
          }
          case 17 -> { // ADD_ASSIGN
            byte oparg = code[pc++];
            SPLObject rhs = evalStack[--top];
            SPLObject name = constants[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.add(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).add(rhs));
              continue;
            }
            throw new SPLInternalException("InternalError: not found " + name.str());
          }
          case 18 -> { // SUB_ASSIGN
            byte oparg = code[pc++];
            SPLObject rhs = evalStack[--top];
            SPLObject name = constants[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.sub(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).sub(rhs));
              continue;
            }
            throw new SPLInternalException("InternalError: not found " + name.str());
          }
          case 19 -> { // MUL_ASSIGN
            byte oparg = code[pc++];
            SPLObject rhs = evalStack[--top];
            SPLObject name = constants[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs!= null) {
              locals.put(name, lhs.mul(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).mul(rhs));
              continue;
            }
            throw new SPLInternalException("InternalError: not found " + name.str());
          }
          case 20 -> { // DIV_ASSIGN
            byte oparg = code[pc++];
            SPLObject rhs = evalStack[--top];
            SPLObject name = constants[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs!= null) {
              locals.put(name, lhs.div(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).div(rhs));
              continue;
            }
            throw new SPLInternalException("InternalError: not found " + name.str());
          }
          case 21 -> { // MOD_ASSIGN
            byte oparg = code[pc++];
            SPLObject rhs = evalStack[--top];
            SPLObject name = constants[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs!= null) {
              locals.put(name, lhs.mod(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).mod(rhs));
              continue;
            }
            throw new SPLInternalException("InternalError: not found " + name.str());
          }
          case 22 -> { // POWER_ASSIGN
            byte oparg = code[pc++];
            SPLObject rhs = evalStack[--top];
            SPLObject name = constants[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs!= null) {
              locals.put(name, lhs.pow(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).pow(rhs));
              continue;
            }
            throw new SPLInternalException("InternalError: not found " + name.str());
          }
          case 23 -> { // LT
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            if (lhs.lt(rhs) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case 24 -> { // GT
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            if (lhs.gt(rhs) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case 25 -> { // EQ
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            if (lhs.eq(rhs) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case 26 -> { // NE
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            if (lhs.ne(rhs) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case 27 -> { // LE
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            if (lhs.le(rhs) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case 28 -> { // GE
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            if (lhs.ge(rhs) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case 29 -> { // AND
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.and(rhs);
          }
          case 30 -> { // OR
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.or(rhs);
          }
          case 31 -> { // INVERT
            pc++;
            evalStack[top++] = evalStack[--top].invert();
          }
          case 32 -> { // CONDITIONAL_AND
            pc++;
            if (evalStack[--top].conditionalAnd(evalStack[--top]) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case 33 -> { // CONDITIONAL_OR
            pc++;
            if (evalStack[--top].conditionalOr(evalStack[--top]) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case 34 -> { // NOT
            pc++;
            evalStack[top++] = evalStack[--top].not();
          }
          case 35 -> { // STORE_LOCAL
            int oparg = code[pc++];
            SPLObject o = evalStack[--top];
            SPLObject key = constants[oparg];
            locals.put(key, o);
          }
          case 36 -> { // LOAD_LOCAL
            int oparg = code[pc++];
            if (locals.containsKey(constants[oparg])) {
              evalStack[top++] = locals.get(constants[oparg]);
              continue;
            }
            throw new SPLInternalException("InternalError: LOAD_LOCAL failed, " + constants[oparg].str() + " is not defined");
          }
          case 37 -> { // STORE_GLOBAL
            int oparg = code[pc++];
            SPLObject o = evalStack[--top];
            globals.put(constants[oparg], o);
          }
          case 38 -> { // LOAD_GLOBAL
            int oparg = code[pc++];
            if (globals.containsKey(constants[oparg])) {
              evalStack[top++] = globals.get(constants[oparg]);
              continue;
            }
            throw new SPLInternalException("InternalError: not found, " + constants[oparg].str() + " is not defined");
          }
          case 39 -> { // LOAD_NAME
            int oparg = code[pc++];
            if (locals.containsKey(constants[oparg])) {
              evalStack[top++] = locals.get(constants[oparg]);
              continue;
            } else if (globals.containsKey(constants[oparg])) {
              evalStack[top++] = globals.get(constants[oparg]);
              continue;
            } else {
              SPLObject o = Builtin.get(constants[oparg]);
              if (o != null) {
                evalStack[top++] = o;
                continue;
              }
            }
            throw new SPLInternalException("InternalError: LOAD_NAME failed, " + constants[oparg].str() + " is not defined");
          }
          case 40 -> { // LOAD_METHOD
          }
          case 41 -> { // CALL_METHOD
          }
          case 42 -> { // STORE
          }
          case 43 -> { // LOAD
          }
          case 44 -> { // CALL
            int oparg = code[pc++];
            SPLObject callable = evalStack[--top];
            SPLObject[] args = new SPLObject[oparg];
            for (int i = 0; i < oparg; i++) {
              args[i] = evalStack[--top];
            }
            evalStack[top++] = callable.call(args);
          }
          case 45 -> { // LOAD_CONST
            int oparg = code[pc++];
            evalStack[top++] = constants[oparg];
          }
          case 46 -> { // POP
            pc++;
            top--;
          }
          case 49 -> { // JUMP_FALSE
            int oparg = code[pc++];
            if (evalStack[--top] == SPLBoolObject.getFalse()) {
              pc += oparg;
            }
          }
          default -> {
            throw new SPLInternalException("InternalError: unknown opcode " + code[--pc]);
          }
        }

      }
    } catch (SPLInternalException e) {
      e.printStackTrace();
      System.err.println("pc = " + pc);
    }
    return SPLNoneObject.getInstance();
  }

  public int getPc() {
    return pc;
  }

  public long getInsNumExecuted() {
    return insNumExecuted;
  }
}
