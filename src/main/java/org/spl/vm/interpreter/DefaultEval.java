package org.spl.vm.interpreter;

import org.spl.vm.builtin.Builtin;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.internal.objs.SPLFrameObject;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.objects.SPLBoolObject;
import org.spl.vm.objects.SPLNoneObject;
import org.spl.vm.objects.SPLObject;

import java.util.ArrayList;
import java.util.Map;


public class DefaultEval extends SPLFrameObject implements Evaluation {


  private final String name;

  public DefaultEval(SPLCodeObject codeObj) throws SPLInternalException {
    super(codeObj);
    if (codeObj.getArgs() != 0) {
      throw new SPLInternalException("SPLCodeObject's args must be zero");
    }
    name = "anonymous";
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
          case INPLACE_LSHIFT -> { // LSHIFT_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.inplaceLshift(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).inplaceLshift(rhs));
              continue;
            }
            throw new SPLInternalException("not found " + name.str());
          }
          case INPLACE_RSHIFT -> { // RSHIFT_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.inplaceRshift(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).inplaceRshift(rhs));
              continue;
            }
            throw new SPLInternalException("not found " + name.str());
          }
          case INPLACE_U_RSHIFT -> { // U_RSHIFT_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.inplaceURshift(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).inplaceURshift(rhs));
              continue;
            }
            throw new SPLInternalException("not found " + name.str());
          }
          case INPLACE_AND -> { // AND_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.inplaceAnd(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).inplaceAnd(rhs));
              continue;
            }
            throw new SPLInternalException("not found " + name.str());
          }
          case INPLACE_OR -> { // OR_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.inplaceOr(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).inplaceOr(rhs));
              continue;
            }
            throw new SPLInternalException("not found " + name.str());
          }
          case INPLACE_XOR -> { // XOR_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.inplaceXor(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).inplaceXor(rhs));
              continue;
            }
            throw new SPLInternalException("not found " + name.str());
          }
          case INPLACE_ADD -> { // ADD_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.inplaceAdd(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).inplaceAdd(rhs));
              continue;
            }
            throw new SPLInternalException("not found " + name.str());
          }
          case INPLACE_SUB -> { // SUB_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs != null) {
              locals.put(name, lhs.inplaceSub(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).inplaceSub(rhs));
              continue;
            }
            throw new SPLInternalException("not found " + name.str());
          }
          case INPLACE_MUL -> { // MUL_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs!= null) {
              locals.put(name, lhs.inplaceMul(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).inplaceMul(rhs));
              continue;
            }
            throw new SPLInternalException("not found " + name.str());
          }
          case INPLACE_DIV -> { // DIV_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs!= null) {
              locals.put(name, lhs.inplaceDiv(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).inplaceDiv(rhs));
              continue;
            }
            throw new SPLInternalException("not found " + name.str());
          }
          case INPLACE_MOD -> { // MOD_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs!= null) {
              locals.put(name, lhs.inplaceMod(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).inplaceMod(rhs));
              continue;
            }
            throw new SPLInternalException("not found " + name.str());
          }
          case INPLACE_POWER -> { // POWER_ASSIGN
            int oparg = getOparg();
            SPLObject rhs = evalStack[--top];
            SPLObject name = varnames[oparg];
            SPLObject lhs = locals.get(name);
            if (lhs!= null) {
              locals.put(name, lhs.inplacePow(rhs));
              continue;
            } else if (globals.containsKey(name)) {
              globals.put(name, globals.get(name).inplacePow(rhs));
              continue;
            }
            throw new SPLInternalException("not found " + name.str());
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
            throw new SPLInternalException("LOAD_LOCAL failed, " + varnames[oparg].str() + " is not defined");
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
            throw new SPLInternalException("not found, " + varnames[oparg].str() + " is not defined");
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
            throw new SPLInternalException("LOAD_NAME failed, " + varnames[oparg].str() + " is not defined");
          }
          case LOAD_METHOD -> { // LOAD_METHOD
          }
          case CALL_METHOD -> { // CALL_METHOD
          }
          case MAKE_FUNCTION -> {
            int arg = getOparg();
            var defaults = new ArrayList<SPLObject>();
            SPLObject func = evalStack[--top];
            assert func instanceof SPLFuncObject;
            for (int i = 0; i < arg; i++) {
              defaults.add(evalStack[--top]);
            }
            func.setGlobals(globals);
            ((SPLFuncObject)func).setDefaults(defaults);
            evalStack[top++] = func;
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
            throw new SPLInternalException("unknown opcode " + code[--pc]);
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
