package org.spl.compiler.parser;

import org.spl.compiler.lexer.Lexer;
import org.spl.exceptions.SPLSyntaxError;

import java.io.IOException;
import java.util.List;

public abstract class AbstractSyntaxParser {

  protected final List<Lexer.Token> tokens;
  protected final String filename;


  public AbstractSyntaxParser(String filename) throws IOException, SPLSyntaxError {
    this.filename = filename;
    Lexer lexer = new Lexer(filename);
    lexer.doParse();
    tokens = lexer.getTokens();
  }

  public abstract void buildAST() throws SPLSyntaxError;
}
