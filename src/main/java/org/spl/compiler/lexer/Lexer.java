package org.spl.compiler.lexer;

import org.spl.compiler.exceptions.SPLException;
import org.spl.compiler.exceptions.SPLSyntaxError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {


  private final List<Token> tokens;
  private final InputBuffer stream;
  private int lineNo = 1;
  private int columnNo = 1;

  public Lexer(String filename) throws IOException {
    tokens = new ArrayList<>();
    stream = new InputBuffer(filename);
  }

  private void updateLineAndColumn() {
    lineNo = stream.getLineNo();
    columnNo = stream.getColumnNo();
  }

  private void injectTokensAndClearBuilder(Token token, StringBuilder builder) {
    token.setColumnNo(columnNo);
    token.setLineNo(lineNo);
    builder.delete(0, builder.length());
  }

  private char nextChar(StringBuilder builder) {
    char c = stream.nextChar();
    builder.append(c);
    return c;
  }

  public void doParse() throws SPLSyntaxError {
    CHAR_TYPE state = CHAR_TYPE.INIT;
    StringBuilder builder = new StringBuilder();
    char c = nextChar(builder);
    builder.delete(0, builder.length());
    // below code represents a state machine
    while (c != 0) {
      switch (state) {
        // starting branch
        case INIT -> {
          if (Character.isAlphabetic(c)) {
            state = CHAR_TYPE.IDENTIFIER; // means identifier branch
          } else if (Character.isDigit(c)) {
            state = CHAR_TYPE.NUMBER; // means number(int/float) branch
          } else if (c == '"') {
            state = CHAR_TYPE.QUOTATION;
          } else if (c == '+') {
            state = CHAR_TYPE.PLUS;
          } else if (c == '-') {
            state = CHAR_TYPE.MINUS;
          } else if (c == '*') {
            state = CHAR_TYPE.MUL;
          } else if (c == '/') {
            state = CHAR_TYPE.DIV;
          } else if (c == '%') {
            state = CHAR_TYPE.MOD;
          } else if (c == '=') {
            state = CHAR_TYPE.ASSIGN;
          } else if (c == '>') {
            state = CHAR_TYPE.GT;
          } else if (c == '<') {
            state = CHAR_TYPE.LT;
          } else if (c == '&') {
            state = CHAR_TYPE.AND;
          } else if (c == '|') {
            state = CHAR_TYPE.OR;
          } else if (c == '!') {
            state = CHAR_TYPE.NOT;
          } else if (c == '~') {
            state = CHAR_TYPE.INVERT;
          } else if (c == '(') {
            state = CHAR_TYPE.LPAREN;
          } else if (c == ')') {
            state = CHAR_TYPE.RPAREN;
          } else if (c == '^') {
            state = CHAR_TYPE.POWER;
          } else if (c == '{') {
            state = CHAR_TYPE.LBRACE;
          } else if (c == '}') {
            state = CHAR_TYPE.RBRACE;
          } else if (c == ';') {
            state = CHAR_TYPE.SEMICOLON;
          } else if (c == '.') {
            state = CHAR_TYPE.DOT;
          } else if (c == '\n') {
            state = CHAR_TYPE.NEWLINE;
          } else if (c == ',') {
            state = CHAR_TYPE.COMMA;
          } else {
            // white space characters
            if (c == ' ' || c == '\t' || c == '\r') {
              updateLineAndColumn();
              c = nextChar(builder);
              builder.delete(0, builder.length());
            } else {
              throw new SPLSyntaxError("Illegal  character '" + c + "'");
            }
          }
        }
        case COMMA -> {
          Token token = new Token(TOKEN_TYPE.COMMA, ",");
          tokens.add(token);
          injectTokensAndClearBuilder(token, builder);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
          builder.delete(0, builder.length());
        }
        case NEWLINE -> {
          Token token = new Token(TOKEN_TYPE.NEWLINE, "\n");
          tokens.add(token);
          injectTokensAndClearBuilder(token, builder);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
          builder.delete(0, builder.length());
        }
        case DOT -> {
          c = nextChar(builder);
          Token token = new Token(TOKEN_TYPE.DOT, ".");
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
          builder.delete(0, builder.length());
        }
        case IDENTIFIER -> {
          // identifier branch
          builder.append(c);
          while (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_') {
            c = nextChar(builder);
          }
          builder.delete(builder.length() - 1, builder.length());
          Token token = new Token(TOKEN_TYPE.IDENTIFIER, builder.toString());
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case NUMBER -> {
          // number branch
          builder.append(c);
          while (Character.isDigit(c)) {
            c = nextChar(builder);
          }
          if (c == '.') {
            c = nextChar(builder);
            while (Character.isDigit(c)) {
              c = nextChar(builder);
            }
            builder.delete(builder.length() - 1, builder.length());
            if (c == '.') {
              String buffer = stream.getBuffer();
              String msg = SPLException.buildErrorMessage(stream.getFileName(), lineNo, columnNo, stream.getOff() - columnNo, buffer.substring(0, buffer.length() - 1), "Illegal float literal, two or more dots are not allowed in float literals");
              throw new SPLSyntaxError(msg);
            }
            Token token = new Token(TOKEN_TYPE.FLOAT, Float.parseFloat(builder.toString()));
            injectTokensAndClearBuilder(token, builder);
            updateLineAndColumn();
            tokens.add(token);
          } else {
            builder.delete(builder.length() - 1, builder.length());
            Token token = new Token(TOKEN_TYPE.INT, Integer.parseInt(builder.toString()));
            injectTokensAndClearBuilder(token, builder);
            updateLineAndColumn();
            tokens.add(token);
          }
          state = CHAR_TYPE.INIT;
        }
        case QUOTATION -> {
          // string branch
          c = nextChar(builder);
          while (c != '"') {
            if (c == 0) break;
            c = nextChar(builder);
          }
          Token token;
          builder.delete(builder.length() - 1, builder.length());
          if (c == '"') {
            token = new Token(TOKEN_TYPE.STRING, builder.toString());
          } else {
            String msg = SPLException.buildErrorMessage(stream.getFileName(), lineNo, columnNo, stream.getOff() - columnNo, stream.getBuffer(), "Illegal string literal, string literal must be enclosed in double quotes");
            throw new SPLSyntaxError(msg);
          }
          injectTokensAndClearBuilder(token, builder);
          updateLineAndColumn();
          tokens.add(token);
          c = nextChar(builder);
          builder.delete(0, builder.length());
          state = CHAR_TYPE.INIT;
        }
        case PLUS -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_ADD, "+=");
            c = nextChar(builder);
          } else {
            token = new Token(TOKEN_TYPE.PLUS, "+");
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          builder.delete(0, builder.length());
          state = CHAR_TYPE.INIT;
        }
        case SEMICOLON -> {
          c = nextChar(builder);
          Token token = new Token(TOKEN_TYPE.SEMICOLON, ";");
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          builder.delete(0, builder.length());
          state = CHAR_TYPE.INIT;
        }
        case MINUS -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_SUB, "-=");
            c = nextChar(builder);
          } else {
            token = new Token(TOKEN_TYPE.MINUS, "-");
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case MUL -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            c = nextChar(builder);
            token = new Token(TOKEN_TYPE.ASSIGN_MUL, "*=");
          } else {
            if (c == '*') {
              c = nextChar(builder);
              if (c == '=') {
                c = nextChar(builder);
                token = new Token(TOKEN_TYPE.ASSIGN_POWER, "**=");
              } else {
                token = new Token(TOKEN_TYPE.POWER, "**");
              }
            } else {
              token = new Token(TOKEN_TYPE.MUL, "*");
            }
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case DIV -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_DIV, "/=");
            c = nextChar(builder);
          } else {
            token = new Token(TOKEN_TYPE.DIV, "/");
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case MOD -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_MOD, "%=");
            c = nextChar(builder);
          } else {
            token = new Token(TOKEN_TYPE.MOD, "%");
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case ASSIGN -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.EQ, "==");
            c = nextChar(builder);
          } else {
            token = new Token(TOKEN_TYPE.ASSIGN, "=");
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case GT -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.GE, ">=");
            c = nextChar(builder);
          } else if (c == '>') {
            c = nextChar(builder);
            if (c == '=') {
              token = new Token(TOKEN_TYPE.ASSIGN_RSHIFT, ">>=");
              c = nextChar(builder);
            } else {
              if (c == '>') {
                c = nextChar(builder);
                if (c == '=') {
                  token = new Token(TOKEN_TYPE.ASSIGN_U_RSHIFT, ">>>=");
                  c = nextChar(builder);
                } else {
                  token = new Token(TOKEN_TYPE.RSHIFT, ">>>");
                }
              } else {
                token = new Token(TOKEN_TYPE.RSHIFT, ">>");
              }
            }
          } else {
            token = new Token(TOKEN_TYPE.GT, ">");
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case LT -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            // <=
            token = new Token(TOKEN_TYPE.LE, "<=");
            c = nextChar(builder);
          } else if (c == '<') {
            // <<
            c = nextChar(builder);
            if (c == '=') {
              // <<=
              token = new Token(TOKEN_TYPE.ASSIGN_LSHIFT, "<<=");
              c = nextChar(builder);
            } else {
              token = new Token(TOKEN_TYPE.LSHIFT, "<<");
            }
          } else {
            token = new Token(TOKEN_TYPE.LT, "<");
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case AND -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            // &=
            token = new Token(TOKEN_TYPE.ASSIGN_AND, "&=");
            c = nextChar(builder);
          } else if (c == '&') {
            // &&
            token = new Token(TOKEN_TYPE.CONDITIONAL_AND, "&&");
            c = nextChar(builder);
          } else {
            // &
            token = new Token(TOKEN_TYPE.AND, "&");
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case OR -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            // |=
            token = new Token(TOKEN_TYPE.ASSIGN_OR, "|=");
            c = nextChar(builder);
          } else if (c == '|') {
            // ||
            token = new Token(TOKEN_TYPE.CONDITIONAL_OR, "||");
            c = nextChar(builder);
          } else {
            // |
            token = new Token(TOKEN_TYPE.OR, "|");
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case NOT -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.NE, "!=");
            c = nextChar(builder);
          } else {
            token = new Token(TOKEN_TYPE.CONDITIONAL_NOT, "!");
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case INVERT -> {
          Token token;
          c = nextChar(builder);
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_INVERT, "~=");
            c = nextChar(builder);
          } else {
            token = new Token(TOKEN_TYPE.INVERT, "~");
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case LPAREN -> {
          Token token = new Token(TOKEN_TYPE.LEFT_PARENTHESES, "(");
          c = nextChar(builder);
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case RPAREN -> {
          Token token = new Token(TOKEN_TYPE.RIGHT_PARENTHESES, ")");
          c = nextChar(builder);
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case POWER -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_XOR, "^=");
            c = nextChar(builder);
          } else {
            token = new Token(TOKEN_TYPE.XOR, "^");
          }
          injectTokensAndClearBuilder(token, builder);
          tokens.add(token);
          updateLineAndColumn();
          state = CHAR_TYPE.INIT;
        }
        case LBRACE -> {
          Token token = new Token(TOKEN_TYPE.LBRACE, "{");
          injectTokensAndClearBuilder(token, builder);
          updateLineAndColumn();
          tokens.add(token);
          c = nextChar(builder);
          state = CHAR_TYPE.INIT;
        }
        case RBRACE -> {
          Token token = new Token(TOKEN_TYPE.RBRACE, "}");
          injectTokensAndClearBuilder(token, builder);
          updateLineAndColumn();
          tokens.add(token);
          c = nextChar(builder);
          state = CHAR_TYPE.INIT;
        }
      }
    }
    Token token = new Token(TOKEN_TYPE.EOF, "EOF");
    token.setLineNo(1);
    tokens.add(token);
  }

  public List<Token> getTokens() {
    return tokens;
  }

  private enum CHAR_TYPE {
    INIT, DOT, NEWLINE, COMMA, IDENTIFIER, NUMBER, QUOTATION, PLUS, MINUS, MUL, DIV, MOD, ASSIGN, LT, GT, NE, AND, OR, XOR, NOT, POWER, INVERT, LPAREN, RPAREN, LBRACKET, RBRACKET, LBRACE, RBRACE, SEMICOLON
  }

  public enum TOKEN_TYPE {
    EOF, NEWLINE, STARTER, // used only in the function doParse()
    COMMA, IDENTIFIER, TRUE, FALSE, IMPORT, INT, FLOAT, STRING, SEMICOLON, LEFT_PARENTHESES, RIGHT_PARENTHESES, PLUS, MINUS, MUL, DIV, MOD, LSHIFT, RSHIFT, U_RSHIFT, // unconditional left shift
    ASSIGN, EQ, LT, GT, GE, LE, NE, AND, CONDITIONAL_AND, OR, CONDITIONAL_OR, POWER, XOR, NOT, INVERT, CONDITIONAL_NOT, ASSIGN_ADD, ASSIGN_SUB, ASSIGN_MUL, ASSIGN_DIV, ASSIGN_POWER, ASSIGN_MOD, ASSIGN_INVERT, ASSIGN_LSHIFT, ASSIGN_RSHIFT, ASSIGN_U_RSHIFT, ASSIGN_AND, ASSIGN_OR, ASSIGN_XOR, IF, ELSE, DO, WHILE, FOR, BREAK, CONTINUE, RETURN, DOT, LBRACE, RBRACE, IN, CLASS, DEF, GLOBAL
  }

  public static class Token {
    public TOKEN_TYPE token;
    public Object value;
    private int lineNo;
    private int columnNo;

    private int length;

    public Token(TOKEN_TYPE token, Object value) {
      this.token = token;
      this.value = value;
      this.length = String.valueOf(value).length();
      // Correcting token type
      if (token == TOKEN_TYPE.IDENTIFIER) {
        assert value != null;
        String val = (String) value;
        this.length = val.length();
        switch (val) {
          case "for" -> {
            this.token = TOKEN_TYPE.FOR;
          }
          case "while" -> {
            this.token = TOKEN_TYPE.WHILE;
          }
          case "continue" -> {
            this.token = TOKEN_TYPE.CONTINUE;
          }
          case "do" -> {
            this.token = TOKEN_TYPE.DO;
          }
          case "in" -> {
            this.token = TOKEN_TYPE.IN;
          }
          case "class" -> {
            this.token = TOKEN_TYPE.CLASS;
          }
          case "def" -> {
            this.token = TOKEN_TYPE.DEF;
          }
          case "global" -> {
            this.token = TOKEN_TYPE.GLOBAL;
          }
          case "true" -> {
            this.token = TOKEN_TYPE.TRUE;
          }
          case "false" -> {
            this.token = TOKEN_TYPE.FALSE;
          }
          case "import" -> {
            this.token = TOKEN_TYPE.IMPORT;
          }
          case "if" -> {
            this.token = TOKEN_TYPE.IF;
          }
        }
      }
    }

    public int getLineNo() {
      return lineNo;
    }

    public void setLineNo(int lineNo) {
      this.lineNo = lineNo;
    }

    public int getColumnNo() {
      return columnNo;
    }

    public void setColumnNo(int columnNo) {
      this.columnNo = columnNo;
    }

    public int getLength() {
      return length;
    }

    public void setLength(int length) {
      this.length = length;
    }

    public int getInt() {
      return (Integer) (value);
    }

    public String getIdentifier() {
      return (String) (value);
    }

    public String getValueAsString() {
      return String.valueOf(value);
    }

    public double getDouble() {
      return (Double) (value);
    }

    public boolean isDOT() {
      return token == TOKEN_TYPE.DOT;
    }

    public boolean isCONDITIONAL_AND() {
      return token == TOKEN_TYPE.CONDITIONAL_AND;
    }

    public boolean isCONDITIONAL_OR() {
      return token == TOKEN_TYPE.CONDITIONAL_OR;
    }

    public TOKEN_TYPE getToken() {
      return token;
    }

    public boolean isLT() {
      return token == TOKEN_TYPE.LT;
    }

    public boolean isLE() {
      return token == TOKEN_TYPE.LE;
    }

    public boolean isGE() {
      return token == TOKEN_TYPE.GE;
    }

    public boolean isGT() {
      return token == TOKEN_TYPE.GT;
    }

    public boolean isEQ() {
      return token == TOKEN_TYPE.EQ;
    }

    public boolean isNE() {
      return token == TOKEN_TYPE.NE;
    }

    public boolean isPLUS() {
      return token == TOKEN_TYPE.PLUS;
    }

    public boolean isMINUS() {
      return token == TOKEN_TYPE.MINUS;
    }

    public boolean isMUL() {
      return token == TOKEN_TYPE.MUL;
    }

    public boolean isDIV() {
      return token == TOKEN_TYPE.DIV;
    }

    public boolean isMOD() {
      return token == TOKEN_TYPE.MOD;
    }

    public boolean isLSHIFT() {
      return token == TOKEN_TYPE.LSHIFT;
    }

    public boolean isRSHIFT() {
      return token == TOKEN_TYPE.RSHIFT;
    }

    public boolean isINVERT() {
      return token == TOKEN_TYPE.INVERT;
    }

    public boolean isU_RSHIFT() {
      return token == TOKEN_TYPE.U_RSHIFT;
    }

    public boolean isASSIGN() {
      return token == TOKEN_TYPE.ASSIGN ||
          token == TOKEN_TYPE.ASSIGN_ADD ||
          token == TOKEN_TYPE.ASSIGN_SUB ||
          token == TOKEN_TYPE.ASSIGN_MUL ||
          token == TOKEN_TYPE.ASSIGN_DIV ||
          token == TOKEN_TYPE.ASSIGN_MOD ||
          token == TOKEN_TYPE.ASSIGN_LSHIFT ||
          token == TOKEN_TYPE.ASSIGN_RSHIFT ||
          token == TOKEN_TYPE.ASSIGN_U_RSHIFT ||
          token == TOKEN_TYPE.ASSIGN_AND ||
          token == TOKEN_TYPE.ASSIGN_OR ||
          token == TOKEN_TYPE.ASSIGN_XOR ||
          token == TOKEN_TYPE.ASSIGN_POWER ||
          token == TOKEN_TYPE.ASSIGN_INVERT;
    }

    public boolean isPower() {
      return token == TOKEN_TYPE.POWER;
    }

    public boolean isSEMICOLON() {
      return token == TOKEN_TYPE.SEMICOLON;
    }

    public boolean isLEFT_PARENTHESES() {
      return token == TOKEN_TYPE.LEFT_PARENTHESES;
    }

    public boolean isLPAREN() {
      return token == TOKEN_TYPE.LEFT_PARENTHESES;
    }

    public boolean isRPAREN() {
      return token == TOKEN_TYPE.RIGHT_PARENTHESES;
    }

    public boolean isRIGHT_PARENTHESES() {
      return token == TOKEN_TYPE.RIGHT_PARENTHESES;
    }

    public boolean isINT() {
      return token == TOKEN_TYPE.INT;
    }

    public boolean isFLOAT() {
      return token == TOKEN_TYPE.FLOAT;
    }

    public float getFloat() {
      return (Float) (value);
    }

    public boolean isSTRING() {
      return token == TOKEN_TYPE.STRING;
    }

    public boolean isIDENTIFIER() {
      return token == TOKEN_TYPE.IDENTIFIER;
    }

    public boolean isASSIGN_ADD() {
      return token == TOKEN_TYPE.ASSIGN_ADD;
    }

    public boolean isASSIGN_SUB() {
      return token == TOKEN_TYPE.ASSIGN_SUB;
    }

    public boolean isASSIGN_MUL() {
      return token == TOKEN_TYPE.ASSIGN_MUL;
    }

    public boolean isASSIGN_DIV() {
      return token == TOKEN_TYPE.ASSIGN_DIV;
    }

    public boolean isASSIGN_MOD() {
      return token == TOKEN_TYPE.ASSIGN_MOD;
    }

    public boolean isASSIGN_LSHIFT() {
      return token == TOKEN_TYPE.ASSIGN_LSHIFT;
    }

    public boolean isASSIGN_RSHIFT() {
      return token == TOKEN_TYPE.ASSIGN_RSHIFT;
    }

    public boolean isASSIGN_U_RSHIFT() {
      return token == TOKEN_TYPE.ASSIGN_U_RSHIFT;
    }

    public boolean isASSIGN_AND() {
      return token == TOKEN_TYPE.ASSIGN_AND;
    }

    public boolean isASSIGN_OR() {
      return token == TOKEN_TYPE.ASSIGN_OR;
    }

    public boolean isASSIGN_XOR() {
      return token == TOKEN_TYPE.ASSIGN_XOR;
    }

    public boolean isIF() {
      return token == TOKEN_TYPE.IF;
    }

    public boolean isELSE() {
      return token == TOKEN_TYPE.ELSE;
    }

    public boolean isDO() {
      return token == TOKEN_TYPE.DO;
    }

    public boolean isWHILE() {
      return token == TOKEN_TYPE.WHILE;
    }

    public boolean isNOT() {
      return token == TOKEN_TYPE.NOT;
    }

    public boolean isAND() {
      return token == TOKEN_TYPE.AND;
    }

    public boolean isOR() {
      return token == TOKEN_TYPE.OR;
    }

    public boolean isXOR() {
      return token == TOKEN_TYPE.XOR;
    }

    public boolean isEQUAL() {
      return token == TOKEN_TYPE.EQ;
    }

    public boolean isNOT_EQUAL() {
      return token == TOKEN_TYPE.NE;
    }

    public boolean isNEWLINE() {
      return token == TOKEN_TYPE.NEWLINE;
    }

    public boolean isEOF() {
      return token == TOKEN_TYPE.EOF;
    }

    public boolean isTRUE() {
      return token == TOKEN_TYPE.TRUE;
    }

    public boolean isFALSE() {
      return token == TOKEN_TYPE.FALSE;
    }

    public String toString() {
      return "Token{" + "token:" + token + ";" + "value:" + value + ";" + "Line:" + lineNo + ";" + "Column:" + columnNo + '}';
    }
  }
}
