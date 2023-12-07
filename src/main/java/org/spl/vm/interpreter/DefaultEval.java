package org.spl.vm.interpreter;

import org.spl.vm.builtin.Builtin;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.internal.objs.SPLFrameObject;
import org.spl.vm.objects.SPLBoolObject;
import org.spl.vm.objects.SPLNoneObject;
import org.spl.vm.objects.SPLObject;

import java.util.Map;


public class DefaultEval extends SPLFrameObject implements Evaluation {


  private String name;

  public DefaultEval(SPLCodeObject codeObj) throws SPLInternalException {
    super(codeObj);
    if (codeObj.getArgs() != 0) {
      throw new SPLInternalException("SPLCodeObject's args must be zero");
    }
    Evaluation.init();
  }

  public DefaultEval(String name, Map<SPLObject, SPLObject> locals, Map<SPLObject, SPLObject> globals, SPLCodeObject codeObj) {
    super(locals, globals, codeObj);
    this.name = name;
    Evaluation.init();
  }

  public String getName() {
    return name;
  }

  @Override
  public SPLObject evalFrame() throws SPLInternalException {
    try {
      while (pc < code.length) {
        insNumExecuted++;
        switch (opcode[code[pc++] & 0xff]) {
          case NOP -> { // NOP
            pc++;
          }
          case ADD -> { // ADD
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.add(rhs);
          }
          case SUB -> { // SUB
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.sub(rhs);
          }
          case MUL -> { // MUL
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.mul(rhs);
          }
          case DIV -> { // DIV
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.div(rhs);
          }
          case TRUE_DIV -> {
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.trueDiv(rhs);
          }
          case NEG -> {
            pc++;
            SPLObject o = evalStack[--top].neg();
            evalStack[top++] = o;
          }
          case MOD -> { // MOD
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.mod(rhs);
          }
          case POWER -> { // POWER
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.pow(rhs);
          }
          case XOR -> { // XOR
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.xor(rhs);
          }
          case LSHIFT -> { // LSHIFT
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.lshift(rhs);
          }
          case RSHIFT -> { // RSHIFT
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.rshift(rhs);
          }
          case U_RSHIFT -> { // U_RSHIFT
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.URshift(rhs);
          }
          case LSHIFT_ASSIGN -> { // LSHIFT_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
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
          case RSHIFT_ASSIGN  -> { // RSHIFT_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
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
          case U_RSHIFT_ASSIGN -> { // U_RSHIFT_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
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
          case AND_ASSIGN -> { // AND_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
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
          case OR_ASSIGN -> { // OR_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
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
          case XOR_ASSIGN -> { // XOR_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
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
          case ADD_ASSIGN -> { // ADD_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
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
          case SUB_ASSIGN -> { // SUB_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
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
          case MUL_ASSIGN -> { // MUL_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
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
          case DIV_ASSIGN -> { // DIV_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
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
          case MOD_ASSIGN -> { // MOD_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
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
          case POWER_ASSIGN -> { // POWER_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
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
          case LT -> { // LT
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            if (lhs.lt(rhs) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case GT -> { // GT
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            if (lhs.gt(rhs) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case EQ -> { // EQ
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            if (lhs.eq(rhs) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case NE -> { // NE
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            if (lhs.ne(rhs) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case LE -> { // LE
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            if (lhs.le(rhs) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case GE -> { // GE
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            if (lhs.ge(rhs) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case AND -> { // AND
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.and(rhs);
          }
          case OR -> { // OR
            pc++;
            SPLObject rhs = evalStack[--top];
            SPLObject lhs = evalStack[--top];
            evalStack[top++] = lhs.or(rhs);
          }
          case INVERT -> { // INVERT
            pc++;
            SPLObject o = evalStack[--top].invert();
            evalStack[top++] = o;
          }
          case CONDITIONAL_AND -> { // CONDITIONAL_AND
            pc++;
            if (evalStack[--top].conditionalAnd(evalStack[--top]) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case CONDITIONAL_OR -> { // CONDITIONAL_OR
            pc++;
            if (evalStack[--top].conditionalOr(evalStack[--top]) == SPLBoolObject.getTrue()) {
              evalStack[top++] = SPLBoolObject.getTrue();
            } else {
              evalStack[top++] = SPLBoolObject.getFalse();
            }
          }
          case NOT -> { // NOT
            pc++;
            evalStack[top++] = evalStack[--top].not();
          }
          case STORE_LOCAL -> { // STORE_LOCAL
            int oparg = code[pc++];
            SPLObject o = evalStack[--top];
            SPLObject key = varnames[oparg];
            locals.put(key, o);
          }
          case LOAD_LOCAL -> { // LOAD_LOCAL
            int oparg = code[pc++];
            if (locals.containsKey(varnames[oparg])) {
              evalStack[top++] = locals.get(varnames[oparg]);
              continue;
            }
            throw new SPLInternalException("InternalError: LOAD_LOCAL failed, " + varnames[oparg].str() + " is not defined");
          }
          case STORE_GLOBAL -> { // STORE_GLOBAL
            int oparg = code[pc++];
            SPLObject o = evalStack[--top];
            globals.put(varnames[oparg], o);
          }
          case LOAD_GLOBAL -> { // LOAD_GLOBAL
            int oparg = code[pc++];
            if (globals.containsKey(varnames[oparg])) {
              evalStack[top++] = globals.get(varnames[oparg]);
              continue;
            }
            throw new SPLInternalException("InternalError: not found, " + varnames[oparg].str() + " is not defined");
          }
          case LOAD_NAME -> { // LOAD_NAME
            int oparg = code[pc++];
            if (locals.containsKey(varnames[oparg])) {
              evalStack[top++] = locals.get(varnames[oparg]);
              continue;
            } else if (globals.containsKey(varnames[oparg])) {
              evalStack[top++] = globals.get(varnames[oparg]);
              continue;
            } else {
              SPLObject o = Builtin.get(varnames[oparg]);
              if (o != null) {
                evalStack[top++] = o;
                continue;
              }
            }
            throw new SPLInternalException("InternalError: LOAD_NAME failed, " + varnames[oparg].str() + " is not defined");
          }
          case LOAD_METHOD -> { // LOAD_METHOD
          }
          case CALL_METHOD -> { // CALL_METHOD
          }
          case STORE -> { // STORE
          }
          case LOAD -> { // LOAD
          }
          case CALL -> { // CALL
            int oparg = code[pc++];
            SPLObject callable = evalStack[--top];
            SPLObject[] args = new SPLObject[oparg];
            for (int i = 0; i < oparg; i++) {
              args[i] = evalStack[--top];
            }
            callable.setGlobals(globals);
            evalStack[top++] = callable.call(args);
          }
          case LOAD_CONST -> { // LOAD_CONST
            int oparg = getOparg();
            evalStack[top++] = constants[oparg];
          }
          case POP -> { // POP
            pc++;
            top--;
          }
          case JUMP_FALSE -> { // JUMP_FALSE
            int oparg = getOparg();
            if (evalStack[--top] == SPLBoolObject.getFalse()) {
              pc += oparg;
            }
          }
          case JMP_TRUE_NO_POP -> { // JUMP_TRUE
            int oparg = getOparg();
            if (evalStack[top] == SPLBoolObject.getTrue()) {
              pc += oparg;
            }
          }
          case JUMP_BACK -> { // JUMP_BACK
            int oparg = getOparg();
            pc -= oparg;
          }
          case JUMP_BACK_TRUE -> { // JUMP_BACK_TRUE
            int oparg = getOparg();
            if (evalStack[--top] == SPLBoolObject.getTrue()) {
              pc -= oparg;
            }
          }
          case JUMP_UNCON_FORWARD -> { // unconditional jump
            int oparg = getOparg();
            pc += oparg;
          }
          case JUMP_ABSOLUTE -> {
            int pos = 0;
            pos |= code[pc++];
            pos <<= 8;
            pos |= code[pc++];
            pos <<= 8;
            pos |= code[pc];
            pc = pos;
          }
          case RETURN -> {
            pc++;
            return evalStack[--top];
          }
          case RETURN_NONE -> {
            pc++;
            return SPLNoneObject.getInstance();
          }
          default -> {
            throw new SPLInternalException("InternalError: unknown opcode " + code[--pc]);
          }
        }

      }
    } catch (SPLInternalException e) {
      e.printStackTrace();
      System.err.println("pc = " + pc);
      throw e;
    }
    return SPLNoneObject.getInstance();
  }

  private int getOparg() {
    if (code[pc] == -1) {
      pc++;
      int arg = code[pc++];
      arg <<= 8;
      arg |= (code[pc++] & 0xff);
      return arg;
    } else {
      return code[pc++];
    }
  }

}
