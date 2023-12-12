package org.spl.vm.builtin;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.block.ProgramBlock;
import org.spl.compiler.ir.context.DefaultASTContext;
import org.spl.compiler.ir.exp.LoadAttr;
import org.spl.compiler.ir.exp.Pop;
import org.spl.compiler.ir.stmt.returnstmt.Return;
import org.spl.compiler.lexer.Lexer;
import org.spl.compiler.parser.SPLParser;
import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLRuntimeException;
import org.spl.vm.internal.SPLCodeObjectBuilder;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.internal.utils.Dissembler;
import org.spl.vm.internal.utils.SPLRangeObject;
import org.spl.vm.interpreter.DefaultEval;
import org.spl.vm.objects.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Builtin {
  private final static Map<SPLObject, SPLObject> dict;

  static {
    dict = new HashMap<>();
    register("print");
    register("parse");
    register("range");
    register("eval");
    register("max");
    register("min");
    register("abs");
    register("ceil");
    register("floor");
    register("pow");
    register("sqrt");
    register("sin");
    register("cos");
    register("tan");
    register("asin");
    register("acos");
    register("atan");
    register("log");
    register("log10");
    register("exp");
    register("exit");
    register("dis");
  }


  public static Map<SPLObject, SPLObject> getDict() {
    return dict;
  }

  private static void register(String name) {
    try {
      Method method = Builtin.class.getMethod(name, SPLObject[].class);
      SPLCallObject m = new SPLCallObject(method, null, true);
      dict.put(new SPLStringObject(name), m);
    } catch (NoSuchMethodException ignore) {
    }
  }

  public static void addObject(SPLStringObject name, SPLObject o) {
    dict.put(name, o);
  }

  public static void addObject(String name, SPLObject o) {
    addObject(new SPLStringObject(name), o);
  }

  public static SPLObject get(SPLObject key) {
    return dict.get(key);
  }

  public static SPLObject put(SPLObject key, SPLObject value) {
    return dict.put(key, value);
  }

  public static SPLObject print(SPLObject... args) {
    for (SPLObject arg : args) {
      System.out.print(arg.__str__());
    }
    System.out.println();
    return SPLNoneObject.getInstance();
  }

  public static SPLObject max(SPLObject... args) throws SPLInternalException {
    SPLObject max = args[0];
    for (int i = 1; i < args.length; i++) {
      if (args[i].__gt__(max) == SPLBoolObject.getTrue()) {
        max = args[i];
      }
    }
    return max;
  }

  public static SPLObject min(SPLObject... args) throws SPLInternalException {
    SPLObject min = args[0];
    for (int i = 1; i < args.length; i++) {
      if (args[i].__lt__(min) == SPLBoolObject.getTrue()) {
        min = args[i];
      }
    }
    return min;
  }

  public static SPLObject sin(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return new SPLFloatObject(Math.sin(l.getVal()));
      } else if (arg[0] instanceof SPLFloatObject f) {
        return new SPLFloatObject(Math.sin(f.getVal()));
      }
    }
    throw new SPLInternalException("sin() only takes one int/float argument");
  }

  public static SPLObject cos(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return new SPLFloatObject(Math.cos(l.getVal()));
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return new SPLFloatObject(Math.cos(f.getVal()));
      }
    }
    throw new SPLInternalException("cos() only takes one int/float argument");
  }

  public static SPLObject tan(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return new SPLFloatObject(Math.tan(l.getVal()));
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return new SPLFloatObject(Math.tan(f.getVal()));
      }
    }
    throw new SPLInternalException("tan() only takes one int/float argument");
  }

  public static SPLObject asin(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return new SPLFloatObject(Math.asin(l.getVal()));
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return new SPLFloatObject(Math.asin(f.getVal()));
      }
    }
    throw new SPLInternalException("asin() only takes one int/float argument");
  }

  public static SPLObject acos(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return new SPLFloatObject(Math.acos(l.getVal()));
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return new SPLFloatObject(Math.acos(f.getVal()));
      }
    }
    throw new SPLInternalException("acos() only takes one int/float argument");
  }

  public static SPLObject atan(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return new SPLFloatObject(Math.atan(l.getVal()));
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return new SPLFloatObject(Math.atan(f.getVal()));
      }
    }
    throw new SPLInternalException("atan() only takes one int/float argument");
  }

  public static SPLObject sqrt(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return new SPLFloatObject(Math.sqrt(l.getVal()));
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return new SPLFloatObject(Math.sqrt(f.getVal()));
      }
    }
    throw new SPLInternalException("sqrt() only takes one int/float argument");
  }

  public static SPLObject abs(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return new SPLFloatObject(Math.abs(l.getVal()));
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return new SPLFloatObject(Math.abs(f.getVal()));
      }
    }
    throw new SPLInternalException("abs() only takes one int/float argument");
  }

  public static SPLObject pow(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 2) {
      if (arg[0] instanceof SPLLongObject l1 && arg[1] instanceof SPLLongObject l2) {
        return SPLLongObject.create((long) Math.pow(l1.getVal(), l2.getVal()));
      }
      if (arg[0] instanceof SPLFloatObject f1 && arg[1] instanceof SPLFloatObject f2) {
        return new SPLFloatObject(Math.pow(f1.getVal(), f2.getVal()));
      }
      if (arg[0] instanceof SPLLongObject l1 && arg[1] instanceof SPLFloatObject f2) {
        return new SPLFloatObject(Math.pow(l1.getVal(), f2.getVal()));
      }
      if (arg[0] instanceof SPLFloatObject f1 && arg[1] instanceof SPLLongObject l2) {
        return new SPLFloatObject(Math.pow(f1.getVal(), l2.getVal()));
      }
    }
    throw new SPLInternalException("pow() only takes two int/float arguments");
  }

  public static SPLObject floor(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return l;
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return SPLLongObject.create((long) f.getVal());
      }
    }
    throw new SPLInternalException("floor() only takes one int/float argument");
  }

  public static SPLObject ceil(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return l;
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return SPLLongObject.create((long) Math.ceil(f.getVal()));
      }
    }
    throw new SPLInternalException("ceil() only takes one int/float argument");
  }

  public static SPLObject round(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return l;
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return SPLLongObject.create(Math.round(f.getVal()));
      }
    }
    throw new SPLInternalException("round() only takes one int/float argument");
  }

  public static SPLObject log(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return new SPLFloatObject(Math.log(l.getVal()));
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return new SPLFloatObject(Math.log(f.getVal()));
      }
    }
    throw new SPLInternalException("log() only takes one int/float argument");
  }

  public static SPLObject log10(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return new SPLFloatObject(Math.log10(l.getVal()));
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return new SPLFloatObject(Math.log10(f.getVal()));
      }
    }
    throw new SPLInternalException("log10() only takes one int/float argument");
  }

  public static SPLObject exp(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        return new SPLFloatObject(Math.exp(l.getVal()));
      }
      if (arg[0] instanceof SPLFloatObject f) {
        return new SPLFloatObject(Math.exp(f.getVal()));
      }
    }
    throw new SPLInternalException("exp() only takes one int/float argument");
  }


  public static SPLObject exit(SPLObject... arg) throws SPLInternalException {
    if (arg.length == 1) {
      if (arg[0] instanceof SPLLongObject l) {
        System.exit((int) l.getVal());
      }
    } else if (arg.length == 0) {
      System.exit(0);
    }
    throw new SPLInternalException("exit() only takes 0 or 1 int argument");
  }

  public static SPLObject dis(SPLObject... args) throws SPLInternalException, SPLSyntaxError, IOException {
    if (args.length == 1 && args[0] instanceof SPLFuncObject f) {
      Dissembler dissembler = new Dissembler(f.getCodeObject());
      dissembler.prettyPrint();
      return dissembler;
    } else if (args.length == 1 && args[0] instanceof SPLStringObject s) {
      SPLParser parser = new SPLParser("dis-content", s.getVal());
      IRNode<Instruction> ir = parser.buildAST();
      parser.getContext().generateByteCodes(ir);
      SPLCodeObject code = SPLCodeObjectBuilder.build(parser.getContext());
      Dissembler dissembler = new Dissembler(code);
      dissembler.prettyPrint();
      return dissembler;
    }
    return SPLErrorUtils.splErrorFormat(new SPLRuntimeException("dis() only takes one function argument"));
  }

  public static SPLObject parse(SPLObject... args) throws SPLSyntaxError, IOException, SPLInternalException {
    if (args.length == 1 && args[0] instanceof SPLStringObject s) {
      SPLParser parser = new SPLParser("anonymous", s.getVal());
      List<Lexer.Token> tokens = parser.getTokens();
      tokens.forEach(System.out::println);
      return SPLNoneObject.getInstance();
    }
    return SPLErrorUtils.splErrorFormat(new SPLRuntimeException("parse() only takes one string argument"));
  }


  @SPLExportMethod
  public static SPLObject eval(SPLObject... args) throws SPLSyntaxError, IOException, SPLInternalException {
    if (args.length == 1 && args[0] instanceof SPLStringObject s) {
      String line = s.getVal();
      SPLCodeObject code = compile(line);
      DefaultEval defaultEval = new DefaultEval(code);
      return defaultEval.evalFrame();
    }
    return SPLErrorUtils.splErrorFormat(new SPLRuntimeException("eval() only takes one string argument"));
  }

  public static SPLCodeObject compile(String content) throws SPLSyntaxError, IOException {
    SPLParser parser = new SPLParser("Anonymous", content);
    IRNode<Instruction> ir = parser.buildAST();
    if (ir instanceof ProgramBlock pb) {
      List<IRNode<Instruction>> children = pb.getChildren();
      if (pb.getLast() instanceof Pop) {
        children.remove(children.size() - 1);
        children.add(new Return());
      } else if (pb.getLast() instanceof LoadAttr) {
        children.add(new Return());
      }
    }
    DefaultASTContext<Instruction> context = parser.getContext();
    context.generateByteCodes(ir);
    return SPLCodeObjectBuilder.build(context);
  }

  @SPLExportMethod
  public static SPLObject range(SPLObject... args) throws SPLInternalException {
    if (args.length == 1 && args[0] instanceof SPLLongObject o) {
      return new SPLRangeObject(0, (int) o.getVal(), 1);
    } else if (args.length == 2 && args[0] instanceof SPLLongObject o1
        && args[1] instanceof SPLLongObject o2) {
      return new SPLRangeObject((int) o1.getVal(), (int) o2.getVal(), 1);
    } else if (args.length == 3 && args[0] instanceof SPLLongObject o1
        && args[1] instanceof SPLLongObject o2
        && args[2] instanceof SPLLongObject o3) {
      return new SPLRangeObject((int) o1.getVal(), (int) o2.getVal(), (int) o3.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLRuntimeException("range() only takes 1, 2 or 3 int arguments"));
  }

}
