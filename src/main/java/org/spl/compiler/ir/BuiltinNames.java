package org.spl.compiler.ir;

import java.util.HashSet;
import java.util.Set;

public class BuiltinNames {

  public static Set<String> builtinNames = new HashSet<>();

  static {
    builtinNames.add("print");
    builtinNames.add("max");
    builtinNames.add("min");
    builtinNames.add("abs");
    builtinNames.add("pow");
    builtinNames.add("sqrt");
    builtinNames.add("sin");
    builtinNames.add("cos");
    builtinNames.add("tan");
    builtinNames.add("asin");
    builtinNames.add("acos");
    builtinNames.add("atan");
    builtinNames.add("log");
    builtinNames.add("log10");
    builtinNames.add("exp");
    builtinNames.add("floor");
    builtinNames.add("ceil");
    builtinNames.add("round");
  }

  public static void add(String name) {
    builtinNames.add(name);
  }

  public static boolean contain(String name) {
    return builtinNames.contains(name);
  }
}
