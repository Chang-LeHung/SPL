package org.spl.compiler.parser;

import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;

public interface ASTBuilder<E> {
  IRNode<E> buildAST() throws SPLSyntaxError;

  IRNode<E> getAST();
}