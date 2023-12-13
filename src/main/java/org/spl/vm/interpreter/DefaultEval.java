package org.spl.vm.interpreter;

import org.spl.compiler.ir.context.ASTContext;
import org.spl.vm.builtin.Builtin;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLRuntimeException;
import org.spl.vm.interfaces.SPLIterator;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.internal.objs.SPLFrameObject;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.objects.*;
import org.spl.vm.types.SPLCommonType;

import java.util.*;


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
    MainLoop:
    for (; ; ) {
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
              evalStack[top++] = lhs.__add__(rhs);
            }
            case SUB -> { // SUB
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__sub__(rhs);
            }
            case MUL -> { // MUL
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__mul__(rhs);
            }
            case DIV -> { // DIV
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__div__(rhs);
            }
            case TRUE_DIV -> {
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__trueDiv__(rhs);
            }
            case NEG -> {
              pc++;
              SPLObject o = evalStack[--top].__neg__();
              evalStack[top++] = o;
            }
            case MOD -> { // MOD
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__mod__(rhs);
            }
            case POWER -> { // POWER
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__pow__(rhs);
            }
            case XOR -> { // XOR
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__xor__(rhs);
            }
            case LSHIFT -> { // LSHIFT
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__lshift__(rhs);
            }
            case RSHIFT -> { // RSHIFT
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__rshift__(rhs);
            }
            case U_RSHIFT -> { // U_RSHIFT
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__URshift__(rhs);
            }
            case INPLACE_LSHIFT -> { // LSHIFT_ASSIGN
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplaceLshift__(rhs);
            }
            case INPLACE_RSHIFT -> { // RSHIFT_ASSIGN
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplaceRshift__(rhs);
            }
            case INPLACE_TRUE_DIV -> {
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplaceTrueDiv__(rhs);
            }
            case NEXT -> {
              int arg = getOparg();
              SPLObject o = evalStack[this.top - 1];
              if (o instanceof SPLIterator iterator) {
                SPLObject next = iterator.next();
                if (next != SPLStopIteration.getInstance()) {
                  evalStack[top++] = next;
                } else {
                  top--;
                  pc += arg;
                }
                continue;
              }
              throw new SPLInternalException("NEXT can only be used with iterators");
            }
            case GET_ITERATOR -> {
              pc++;
              SPLObject o = evalStack[--top];
              SPLObject iterator = o.__getIterator__();
              if (!(iterator instanceof SPLIterator)) {
                SPLErrorUtils.splErrorFormat(new SPLRuntimeException(o.__str__() + " is not an iterator"));
              } else {
                evalStack[top++] = iterator;
              }
            }
            case INPLACE_U_RSHIFT -> { // U_RSHIFT_ASSIGN
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplaceURshift__(rhs);
            }
            case INPLACE_AND -> { // AND_ASSIGN
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplaceAnd__(rhs);
            }
            case INPLACE_OR -> { // OR_ASSIGN
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplaceOr__(rhs);
            }
            case INPLACE_XOR -> { // XOR_ASSIGN
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplaceXor__(rhs);
            }
            case INPLACE_ADD -> { // ADD_ASSIGN
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplaceAdd__(rhs);
            }
            case INPLACE_SUB -> { // SUB_ASSIGN
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplaceSub__(rhs);
            }
            case INPLACE_MUL -> { // MUL_ASSIGN
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplaceMul__(rhs);
            }
            case INPLACE_DIV -> { // DIV_ASSIGN
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplaceDiv__(rhs);
            }
            case INPLACE_MOD -> { // MOD_ASSIGN
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplaceMod__(rhs);
            }
            case INPLACE_POWER -> { // POWER_ASSIGN
              getOparg();
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__inplacePow__(rhs);
            }
            case LT -> { // LT
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              if (lhs.__lt__(rhs) == SPLBoolObject.getTrue()) {
                evalStack[top++] = SPLBoolObject.getTrue();
              } else {
                evalStack[top++] = SPLBoolObject.getFalse();
              }
            }
            case GT -> { // GT
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              if (lhs.__gt__(rhs) == SPLBoolObject.getTrue()) {
                evalStack[top++] = SPLBoolObject.getTrue();
              } else {
                evalStack[top++] = SPLBoolObject.getFalse();
              }
            }
            case EQ -> { // EQ
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              if (lhs.__eq__(rhs) == SPLBoolObject.getTrue()) {
                evalStack[top++] = SPLBoolObject.getTrue();
              } else {
                evalStack[top++] = SPLBoolObject.getFalse();
              }
            }
            case NE -> { // NE
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              if (lhs.__ne__(rhs) == SPLBoolObject.getTrue()) {
                evalStack[top++] = SPLBoolObject.getTrue();
              } else {
                evalStack[top++] = SPLBoolObject.getFalse();
              }
            }
            case LE -> { // LE
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              if (lhs.__le__(rhs) == SPLBoolObject.getTrue()) {
                evalStack[top++] = SPLBoolObject.getTrue();
              } else {
                evalStack[top++] = SPLBoolObject.getFalse();
              }
            }
            case GE -> { // GE
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              if (lhs.__ge__(rhs) == SPLBoolObject.getTrue()) {
                evalStack[top++] = SPLBoolObject.getTrue();
              } else {
                evalStack[top++] = SPLBoolObject.getFalse();
              }
            }
            case AND -> { // AND
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__and__(rhs);
            }
            case OR -> { // OR
              pc++;
              SPLObject rhs = evalStack[--top];
              SPLObject lhs = evalStack[--top];
              evalStack[top++] = lhs.__or__(rhs);
            }
            case INVERT -> { // INVERT
              pc++;
              SPLObject o = evalStack[--top].__invert__();
              evalStack[top++] = o;
            }
            case CONDITIONAL_AND -> { // CONDITIONAL_AND
              pc++;
              if (evalStack[--top].__conditionalAnd__(evalStack[--top]) == SPLBoolObject.getTrue()) {
                evalStack[top++] = SPLBoolObject.getTrue();
              } else {
                evalStack[top++] = SPLBoolObject.getFalse();
              }
            }
            case CONDITIONAL_OR -> { // CONDITIONAL_OR
              pc++;
              if (evalStack[--top].__conditionalOr__(evalStack[--top]) == SPLBoolObject.getTrue()) {
                evalStack[top++] = SPLBoolObject.getTrue();
              } else {
                evalStack[top++] = SPLBoolObject.getFalse();
              }
            }
            case NOT -> { // NOT
              pc++;
              evalStack[top++] = evalStack[--top].__not__();
            }
            case STORE_LOCAL -> { // STORE_LOCAL
              int oparg = getOparg();
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
              SPLErrorUtils.splErrorFormat(new SPLRuntimeException("Not find a variable named \"" + varnames[oparg].__str__() + "\""));
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
              } else if (Builtin.getDict().containsKey(varnames[oparg])) {
                evalStack[top++] = Builtin.getDict().get(varnames[oparg]);
                continue;
              }
              SPLErrorUtils.splErrorFormat(new SPLRuntimeException("Not find a variable named \"" + varnames[oparg].__str__() + "\""));
            }
            case LOAD_NAME, LOAD -> { // LOAD_NAME
              int oparg = getOparg();
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
              SPLErrorUtils.splErrorFormat(new SPLRuntimeException("Not find a variable named \"" + varnames[oparg].__str__() + "\""));
            }
            case LOAD_METHOD -> { // LOAD_METHOD
              int arg = getOparg();
              SPLObject o = evalStack[--top];
              SPLObject callable = o.__getMethod__(varnames[arg]);
              evalStack[top++] = callable;
            }
            case CALL_METHOD -> { // CALL_METHOD
              // We do not use this instruction now
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
              ((SPLFuncObject) func).setDefaults(defaults);
              evalStack[top++] = func;
            }
            case STORE -> { // STORE
            }
            case STORE_ATTR -> {
              int arg = getOparg();
              SPLObject o = evalStack[--top];
              o.__setAttr__(varnames[arg], evalStack[--top]);
            }
            case LOAD_ATTR -> { // LOAD_ATTR
              int arg = getOparg();
              evalStack[top - 1] = evalStack[top - 1].__getAttr__(varnames[arg]);
            }
            case CALL -> { // CALL
              int oparg = code[pc++];
              SPLObject callable = evalStack[--top];
              SPLObject[] args = new SPLObject[oparg];
              for (int i = 0; i < oparg; i++) {
                args[i] = evalStack[--top];
              }
              evalStack[top++] = callable.__call__(args);
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
            case DUP -> {
              pc++;
              SPLObject t = evalStack[top - 1];
              evalStack[top++] = t;
            }
            case BUILD_LIST -> {
              int arg = getOparg();
              top -= arg;
              ArrayList<SPLObject> params = new ArrayList<>(Arrays.asList(evalStack).subList(top, arg + top));
              evalStack[top++] = new SPLListObject(params);
            }
            case BUILD_MAP -> {
              int arg = getOparg();
              HashMap<SPLObject, SPLObject> params = new HashMap<>();
              top -= arg;
              for (int i = 0; i < arg; i += 2) {
                params.put(evalStack[top + i], evalStack[top + i + 1]);
              }
              evalStack[top++] = new SPLDictObject(params);
            }
            case BUILD_SET -> {
              int arg = getOparg();
              top -= arg;
              HashSet<SPLObject> params = new HashSet<>(Arrays.asList(evalStack).subList(top, arg + top));
              evalStack[top++] = new SPLSetObject(params);
            }
            case SUBSCRIBE -> {
              pc++;
              SPLObject param = evalStack[--top];
              SPLObject o = evalStack[--top];
              evalStack[top++] = o.__subscribe__(param);
            }
            case LONG_JUMP -> {
              int size = 0;
              size |= code[pc++];
              size <<= 8;
              size |= code[pc++];
              size <<= 8;
              size |= code[pc++];
              if ((size & 1) == 1) {
                size = size >> 1;
              } else {
                size = -(size >> 1);
              }
              pc += size;
            }
            case EXEC_MATCH -> {
              pc++;
              SPLObject o = evalStack[--top];
              ThreadState ts = ThreadState.get();
              if (SPLCommonType.isExecMatch(ts.getExecVal(), (SPLCommonType) o)) {
                evalStack[top++] = SPLBoolObject.getTrue();
              } else {
                evalStack[top++] = SPLBoolObject.getFalse();
              }
            }
            case STORE_EXC_VAL -> {
              int arg = getOparg();
              ThreadState ts = ThreadState.get();
              var val = ts.getExecVal();
              assert val != null;
              locals.put(varnames[arg], val);
            }
            default -> {
              throw new SPLInternalException("unknown opcode " + code[--pc]);
            }
          }
        }
        break;
      } catch (SPLInternalException e) {
        ThreadState ts = ThreadState.get();
        if (ts.getExecVal() != null) {
          for (ASTContext.JumpTableEntry entry : this.codeObject.getJumpTable()) {
            if (entry.isInRange(pc)) {
              pc = entry.targetPc();
              continue MainLoop;
            }
          }
        }
        traceThis();
        throw e;
      }
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


  public void traceThis() {
    ThreadState ts = ThreadState.get();
    SPLTraceBackObject trace = ts.getTrace();
    SPLTraceBackObject newTrace = new SPLTraceBackObject(this);
    if (trace == null) {
      ts.setTrace(newTrace);
    } else {
      trace.setNext(newTrace);
    }
  }
}
