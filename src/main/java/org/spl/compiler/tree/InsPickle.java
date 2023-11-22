package org.spl.compiler.tree;

import org.spl.compiler.bytecode.Instruction;

public interface InsPickle {

  void accept(Visitor<Instruction> t);
}
