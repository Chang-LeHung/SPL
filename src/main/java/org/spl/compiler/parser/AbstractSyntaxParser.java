package org.spl.compiler.parser;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.lexer.Lexer;
import org.spl.exceptions.SPLSyntaxError;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSyntaxParser implements ASTBuilder<Instruction> {

  protected final List<Lexer.Token> tokens;
  protected final String filename;
  protected final List<String> sourceCode;


  public AbstractSyntaxParser(String filename) throws IOException, SPLSyntaxError {
    this.filename = filename;
    Lexer lexer = new Lexer(filename);
    lexer.doParse();
    tokens = lexer.getTokens();
    sourceCode = new ArrayList<>();
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line;
    while ((line = reader.readLine()) != null) {
      sourceCode.add(line);
    }
    reader.close();
  }

  @Override
  public IRNode<Instruction> buildAST() throws SPLSyntaxError {
    throw new RuntimeException("not implemented");
  }
}
