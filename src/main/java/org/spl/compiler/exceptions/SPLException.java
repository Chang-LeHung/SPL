package org.spl.compiler.exceptions;

public class SPLException extends Exception {

  public SPLException(String message) {
    super(message);
  }

  public static String buildErrorMessage(
      String filename,
      int lineNo,
      int columnNo,
      int len,
      String code,
      String errorMessage) {
    return "File, " +
        filename +
        ", line " +
        lineNo +
        '\n' +
        code + '\n' + " ".repeat(columnNo) +
        "^".repeat(len) + '\n' +
        "SyntaxError: " +
        errorMessage;
  }

}
