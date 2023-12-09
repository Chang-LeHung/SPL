package org.spl.vm.builtin;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.objects.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Builtin {
  private final static Map<SPLObject, SPLObject> dict;
  static {
    dict = new HashMap<>();
    register("print");
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
  }

  private static void register(String name) {
    try {
      Method method = Builtin.class.getMethod(name, SPLObject[].class);
      SPLCallObject m = new SPLCallObject(method, true);
      dict.put(new SPLStringObject(name), m);
    } catch (NoSuchMethodException ignore) {
    }
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
        return SPLLongObject.create((long)Math.pow(l1.getVal(), l2.getVal()));
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
        return SPLLongObject.create((long)f.getVal());
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
        return SPLLongObject.create((long)Math.ceil(f.getVal()));
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
        return SPLLongObject.create((long)Math.round(f.getVal()));
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
        System.exit((int)l.getVal());
      }
    }
    throw new SPLInternalException("exit() only takes one int argument");
  }
}
