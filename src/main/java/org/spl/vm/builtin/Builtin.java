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
      System.out.print(arg.str());
    }
    System.out.println();
    return SPLNoneObject.getInstance();
  }

  public static SPLObject max(SPLObject... args) throws SPLInternalException {
    SPLObject max = args[0];
    for (int i = 1; i < args.length; i++) {
      if (args[i].gt(max) == SPLBoolObject.getTrue()) {
        max = args[i];
      }
    }
    return max;
  }
}
