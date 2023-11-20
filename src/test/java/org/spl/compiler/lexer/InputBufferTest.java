package org.spl.compiler.lexer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.spl.exceptions.SPLSyntaxError;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class InputBufferTest {

  @Test
  public void testInputBuffer() throws IOException {
    URL resource = Thread.currentThread().getContextClassLoader().getResource("arithmetic/test01.spl");
    assert resource != null;
    InputBuffer inputBuffer = new InputBuffer(resource.getPath());
    char c = inputBuffer.nextChar();
    System.out.println(c);
    c = inputBuffer.lookAhead();
    Assertions.assertEquals('a', c);
    c = inputBuffer.lookAhead(3);
    Assertions.assertEquals('=', c);
    c = inputBuffer.lookAhead(14);
    System.out.println(c);
    inputBuffer.close();
  }

  @Test
  public void testEscape() {
    char c = '\n';
    System.out.println(c);
  }

  public String getResource(String filename) {
    URL resource = Thread.currentThread().
        getContextClassLoader().
        getResource(filename);
    assert resource != null;
    return resource.getPath();
  }

  public void testAssert(List<Lexer.Token> tokens, int idx, Lexer.TOKEN_TYPE type, Object val) {
    Assertions.assertEquals(type, tokens.get(idx).token);
    Assertions.assertEquals(val, tokens.get(idx).value);
  }

  /**
   * a = 1+2;
   * b = 3-4;
   * @throws IOException
   * @throws SPLSyntaxError
   */
  @Test
  public void testLexer() throws IOException, SPLSyntaxError {
    URL resource = Thread.currentThread().getContextClassLoader().getResource("arithmetic/test01.spl");
    assert resource != null;
    Lexer lexer = new Lexer(resource.getPath());
    lexer.doParse();
    System.out.println(lexer.getTokens());
    List<Lexer.Token> tokens = lexer.getTokens();
    testAssert(tokens, 0, Lexer.TOKEN_TYPE.IDENTIFIER, "a");
    testAssert(tokens, 1, Lexer.TOKEN_TYPE.ASSIGN, "=");
    testAssert(tokens, 2, Lexer.TOKEN_TYPE.INT, 1);
    testAssert(tokens, 3, Lexer.TOKEN_TYPE.PLUS, "+");
    testAssert(tokens, 4, Lexer.TOKEN_TYPE.INT, 2);
    testAssert(tokens, 5, Lexer.TOKEN_TYPE.SEMICOLON, ";");
    testAssert(tokens, 6, Lexer.TOKEN_TYPE.IDENTIFIER, "b");
    testAssert(tokens, 7, Lexer.TOKEN_TYPE.ASSIGN, "=");
    testAssert(tokens, 8, Lexer.TOKEN_TYPE.INT, 3);
    testAssert(tokens, 9, Lexer.TOKEN_TYPE.MINUS, "-");
    testAssert(tokens, 10, Lexer.TOKEN_TYPE.INT, 4);
    testAssert(tokens, 11, Lexer.TOKEN_TYPE.SEMICOLON, ";");
  }

  /**
   * a = True
   * b = False
   * c = a && b
   *
   * a = true
   * b = false
   * c = a && b
   * @throws IOException
   * @throws SPLSyntaxError
   */
  @Test
  public void testBool() throws IOException, SPLSyntaxError {
    String resource = getResource("arithmetic/bool.spl");
    Lexer lexer = new Lexer(resource);
    lexer.doParse();
    System.out.println(lexer.getTokens());
    List<Lexer.Token> tokens = lexer.getTokens();
   testAssert(tokens, 0, Lexer.TOKEN_TYPE.IDENTIFIER, "a");
   testAssert(tokens, 1, Lexer.TOKEN_TYPE.ASSIGN, "=");
   testAssert(tokens, 2, Lexer.TOKEN_TYPE.IDENTIFIER, "True");
   testAssert(tokens, 3, Lexer.TOKEN_TYPE.IDENTIFIER, "b");
   testAssert(tokens, 4, Lexer.TOKEN_TYPE.ASSIGN, "=");
   testAssert(tokens, 5, Lexer.TOKEN_TYPE.IDENTIFIER, "False");
   testAssert(tokens, 6, Lexer.TOKEN_TYPE.IDENTIFIER, "c");
   testAssert(tokens, 7, Lexer.TOKEN_TYPE.ASSIGN, "=");
   testAssert(tokens, 8, Lexer.TOKEN_TYPE.IDENTIFIER, "a");
   testAssert(tokens, 9, Lexer.TOKEN_TYPE.CONDITIONAL_AND, "&&");
   testAssert(tokens, 10, Lexer.TOKEN_TYPE.IDENTIFIER, "b");
   testAssert(tokens, 11, Lexer.TOKEN_TYPE.IDENTIFIER, "a");
   testAssert(tokens, 12, Lexer.TOKEN_TYPE.ASSIGN, "=");
   testAssert(tokens, 13, Lexer.TOKEN_TYPE.TRUE, "true");
   testAssert(tokens, 14, Lexer.TOKEN_TYPE.IDENTIFIER, "b");
   testAssert(tokens, 15, Lexer.TOKEN_TYPE.ASSIGN, "=");
   testAssert(tokens, 16, Lexer.TOKEN_TYPE.FALSE, "false");
   testAssert(tokens, 17, Lexer.TOKEN_TYPE.IDENTIFIER, "c");
   testAssert(tokens, 18, Lexer.TOKEN_TYPE.ASSIGN, "=");
   testAssert(tokens, 19, Lexer.TOKEN_TYPE.IDENTIFIER, "a");
   testAssert(tokens, 20, Lexer.TOKEN_TYPE.CONDITIONAL_AND, "&&");
   testAssert(tokens, 21, Lexer.TOKEN_TYPE.IDENTIFIER, "b");
  }

  @Test
  public void testSyntaxError() throws IOException {
    String resource = getResource("arithmetic/errorfloat.spl");
    Lexer lexer = new Lexer(resource);
    try {
      lexer.doParse();
    } catch (SPLSyntaxError error) {
      System.err.println(error.getMessage());
    }
    System.out.println(lexer.getTokens());
  }

  @Test
  public void testAssign() throws IOException {
    String resource = getResource("arithmetic/assign.spl");
    Lexer lexer = new Lexer(resource);
    try {
      lexer.doParse();
    } catch (SPLSyntaxError error) {
      System.err.println(error.getMessage());
    }
    System.out.println(lexer.getTokens());
  }
}
