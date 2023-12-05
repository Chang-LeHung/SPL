package org.spl.compiler.parser;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLException;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.DefaultASTContext;
import org.spl.compiler.lexer.Lexer;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.lexer.TokenFlow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSyntaxParser implements ASTBuilder<Instruction> {

  protected final List<Lexer.Token> tokens;
  protected final String filename;
  protected final List<String> sourceCode;
  protected final TokenFlow<Lexer.Token> tokenFlow;
  protected final DefaultASTContext<Instruction> context;

  public AbstractSyntaxParser(String filename) throws IOException, SPLSyntaxError {
    this.filename = filename;
    Lexer lexer = new Lexer(filename);
    lexer.doParse();
    tokens = lexer.getTokens();
    tokenFlow = new TokenFlow<>(tokens);
    sourceCode = new ArrayList<>();
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line;
    while ((line = reader.readLine()) != null) {
      sourceCode.add(line);
    }
    context = new DefaultASTContext<>(filename);
    reader.close();
  }

  public static void setSourceCodeInfo(IRNode<?> node, Lexer.Token token) {
    node.setLineNo(token.getLineNo());
    node.setColumnNo(token.getColumnNo());
    node.setLen(token.getLength());
  }

  protected void throwSyntaxError(String message, Lexer.Token token) throws SPLSyntaxError {
    throw new SPLSyntaxError(
        SPLException.buildErrorMessage(
            filename,
            token.getLineNo(),
            token.getColumnNo(),
            token.getLength(),
            sourceCode.get(token.getLineNo() - 1),
            message
        ));
  }

  public List<Lexer.Token> getTokens() {
    return tokens;
  }

  public String getFilename() {
    return filename;
  }

  public List<String> getSourceCode() {
    return sourceCode;
  }

  public TokenFlow<Lexer.Token> getTokenFlow() {
    return tokenFlow;
  }

  public DefaultASTContext<Instruction> getContext() {
    return context;
  }
}
