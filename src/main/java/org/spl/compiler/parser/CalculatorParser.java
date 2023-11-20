package org.spl.compiler.parser;

import org.spl.exceptions.SPLSyntaxError;

import java.io.IOException;

public class CalculatorParser extends AbstractSyntaxParser {
  public CalculatorParser(String filename) throws IOException, SPLSyntaxError {
    super(filename);
  }

  @Override
  public void buildAST() throws SPLSyntaxError {

  }
}
