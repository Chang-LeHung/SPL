package org.spl.compiler.ir;

import java.util.HashSet;
import java.util.Set;

public class BuiltinNames {

  public static Set<String> builtinNames = new HashSet<String>();

  static {
    builtinNames.add("print");
  }

  public static void add(String name) {
    builtinNames.add(name);
  }

  public static boolean contain(String name) {
    return builtinNames.contains(name);
  }
}
