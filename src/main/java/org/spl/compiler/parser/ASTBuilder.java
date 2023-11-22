package org.spl.compiler.parser;

import org.spl.compiler.ir.IRNode;
import org.spl.exceptions.SPLSyntaxError;

public interface ASTBuilder<E> {
  IRNode<E> buildAST() throws SPLSyntaxError;
}