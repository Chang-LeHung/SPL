package org.spl.compiler.lexer;

import org.spl.compiler.exceptions.SPLException;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLStringObject;
import org.spl.vm.types.SPLCommonType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

  private final String filename;

  private final List<Token> tokens;
  private final StringBuilder code;
  private final List<String> sourceCode;
  private int offset;
  private int lineNo = 1;
  private int columnNo = 0;

  public Lexer(String filename) throws IOException {
    tokens = new ArrayList<>();
    code = new StringBuilder();
    offset = 0;
    sourceCode = new ArrayList<>();
    this.filename = filename;
    try (BufferedReader fileReader = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = fileReader.readLine()) != null) {
        code.append(line).append("\n");
        sourceCode.add(line);
      }
    }
  }

  public Lexer(String filename, String content) {
    tokens = new ArrayList<>();
    code = new StringBuilder(content);
    offset = 0;
    this.filename = filename;
    sourceCode = new ArrayList<>(List.of(content.split("\n")));
  }

  private static void clear(StringBuilder builder) {
    builder.delete(0, builder.length());
  }

  private void injectTokensAndClearBuilder(Token token) {
    token.setColumnNo(columnNo - token.getValueAsString().length() + 1);
    token.setLineNo(lineNo);
  }

  public String getFilename() {
    return filename;
  }

  private char nextChar(StringBuilder builder) {
    if (offset >= code.length())
      return 0;
    char last = 0;
    if (offset > 0) {
      last = code.charAt(offset - 1);
    }
    if (last == '\n') {
      lineNo++;
      columnNo = 0;
    }
    char c = code.charAt(offset++);
    columnNo++;
    builder.append(c);
    return c;
  }

  private void stepBack() {
    assert offset > 0;
    offset--;
    if (code.charAt(offset - 1) == '\n') {
      lineNo--;
      columnNo = sourceCode.get(lineNo - 1).length() + 1;
    } else {
      columnNo--;
    }
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
          switch (c) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> state = CHAR_TYPE.NUMBER;
            case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '_' -> state = CHAR_TYPE.IDENTIFIER;
            case '"', '\'' -> state = CHAR_TYPE.QUOTATION;
            case '+' -> state = CHAR_TYPE.PLUS;
            case '-' -> state = CHAR_TYPE.MINUS;
            case '*' -> state = CHAR_TYPE.MUL;
            case '#' -> state = CHAR_TYPE.HASH;
            case '/' -> state = CHAR_TYPE.DIV;
            case '%' -> state = CHAR_TYPE.MOD;
            case '=' -> state = CHAR_TYPE.ASSIGN;
            case '>' -> state = CHAR_TYPE.GT;
            case '<' -> state = CHAR_TYPE.LT;
            case '&' -> state = CHAR_TYPE.AND;
            case '|' -> state = CHAR_TYPE.OR;
            case '!' -> state = CHAR_TYPE.NOT;
            case '~' -> state = CHAR_TYPE.INVERT;
            case '(' -> state = CHAR_TYPE.LPAREN;
            case ')' -> state = CHAR_TYPE.RPAREN;
            case '^' -> state = CHAR_TYPE.POWER;
            case '{' -> state = CHAR_TYPE.LBRACE;
            case '}' -> state = CHAR_TYPE.RBRACE;
            case ';' -> state = CHAR_TYPE.SEMICOLON;
            case '.' -> state = CHAR_TYPE.DOT;
            case '\n' -> state = CHAR_TYPE.NEWLINE;
            case ',' -> state = CHAR_TYPE.COMMA;
            case ':' -> state = CHAR_TYPE.COLON;
            case '[' -> state = CHAR_TYPE.LBRACKET;
            case ']' -> state = CHAR_TYPE.RBRACKET;
            default -> {
              if (Character.isWhitespace(c)) {
                c = nextChar(builder);
              } else {
                throw new SPLSyntaxError("Illegal character '" + c + "'");
              }
            }
          }
        }
        case HASH -> {
          while (c != '\n') {
            c = nextChar(builder);
          }
          state = CHAR_TYPE.INIT;
        }
        case LBRACKET -> {
          Token token = new Token(TOKEN_TYPE.LBRACKET, "[");
          tokens.add(token);
          injectTokensAndClearBuilder(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case RBRACKET -> {
          Token token = new Token(TOKEN_TYPE.RBRACKET, "]");
          tokens.add(token);
          injectTokensAndClearBuilder(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case COMMA -> {
          Token token = new Token(TOKEN_TYPE.COMMA, ",");
          tokens.add(token);
          injectTokensAndClearBuilder(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case COLON -> {
          Token token = new Token(TOKEN_TYPE.COLON, ":");
          tokens.add(token);
          injectTokensAndClearBuilder(token);
          c = nextChar(builder);
          state = CHAR_TYPE.INIT;
        }
        case NEWLINE -> {
          Token token = new Token(TOKEN_TYPE.NEWLINE, "\\n");
          tokens.add(token);
          injectTokensAndClearBuilder(token);
          token.columnNo += 1;
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case DOT -> {
          Token token = new Token(TOKEN_TYPE.DOT, ".");
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case IDENTIFIER -> {
          clear(builder);
          // identifier branch
          builder.append(c);
          while (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_') {
            c = nextChar(builder);
          }
          stepBack(builder);
          Token token = new Token(TOKEN_TYPE.IDENTIFIER, builder.toString());
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case NUMBER -> {
          clear(builder);
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
            if (c == '.') {
              String msg = SPLException.buildErrorMessage(filename, lineNo, columnNo - builder.length(), builder.length(), sourceCode.get(lineNo - 1), "Illegal float literal, two or more dots are not allowed in float literals");
              throw new SPLSyntaxError(msg);
            }
            stepBack(builder);
            Token token = new Token(TOKEN_TYPE.FLOAT, Float.parseFloat(builder.toString()));
            injectTokensAndClearBuilder(token);
            tokens.add(token);
          } else {
            stepBack(builder);
            Token token = new Token(TOKEN_TYPE.INT, Integer.parseInt(builder.toString()));
            injectTokensAndClearBuilder(token);
            tokens.add(token);
          }
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case QUOTATION -> {
          clear(builder);
          // string branch
          char t = c;
          c = nextChar(builder);
          while (c != t) {
            if (c == 0) break;
            c = nextChar(builder);
          }
          Token token;
          builder.delete(builder.length() - 1, builder.length());
          if (c == '"' || c == '\'') {
            token = new Token(TOKEN_TYPE.STRING, builder.toString());
          } else {
            String msg = SPLException.buildErrorMessage(filename, lineNo, columnNo - builder.length(), builder.length(), sourceCode.get(lineNo - 1), "Illegal string literal, string literal must be enclosed in double quotes");
            throw new SPLSyntaxError(msg);
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case PLUS -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_ADD, "+=");
          } else {
            stepBack();
            token = new Token(TOKEN_TYPE.PLUS, "+");
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case SEMICOLON -> {
          Token token = new Token(TOKEN_TYPE.SEMICOLON, ";");
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case MINUS -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_SUB, "-=");
          } else if (c == '>') {
            token = new Token(TOKEN_TYPE.ARROW, "->");
          } else {
            stepBack();
            token = new Token(TOKEN_TYPE.MINUS, "-");
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case MUL -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_MUL, "*=");
          } else {
            if (c == '*') {
              c = nextChar(builder);
              if (c == '=') {
                token = new Token(TOKEN_TYPE.ASSIGN_POWER, "**=");
              } else {
                stepBack();
                token = new Token(TOKEN_TYPE.POWER, "**");
              }
            } else {
              stepBack();
              token = new Token(TOKEN_TYPE.MUL, "*");
            }
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          c = nextChar(builder);
          state = CHAR_TYPE.INIT;
        }
        case DIV -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_DIV, "/=");
          } else {
            if (c == '/') {
              c = nextChar(builder);
              if (c == '=') {
                token = new Token(TOKEN_TYPE.ASSIGN_TRUE_DIV, "//=");
              } else {
                stepBack();
                token = new Token(TOKEN_TYPE.TRUE_DIV, "//");
              }
            } else {
              stepBack();
              token = new Token(TOKEN_TYPE.DIV, "/");
            }
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          c = nextChar(builder);
          state = CHAR_TYPE.INIT;
        }
        case MOD -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_MOD, "%=");
          } else {
            stepBack();
            token = new Token(TOKEN_TYPE.MOD, "%");
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          c = nextChar(builder);
          state = CHAR_TYPE.INIT;
        }
        case ASSIGN -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.EQ, "==");
          } else {
            stepBack();
            token = new Token(TOKEN_TYPE.ASSIGN, "=");
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          c = nextChar(builder);
          state = CHAR_TYPE.INIT;
        }
        case GT -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.GE, ">=");
          } else if (c == '>') {
            c = nextChar(builder);
            if (c == '=') {
              token = new Token(TOKEN_TYPE.ASSIGN_RSHIFT, ">>=");
            } else {
              if (c == '>') {
                c = nextChar(builder);
                if (c == '=') {
                  token = new Token(TOKEN_TYPE.ASSIGN_U_RSHIFT, ">>>=");
                } else {
                  token = new Token(TOKEN_TYPE.U_RSHIFT, ">>>");
                }
              } else {
                token = new Token(TOKEN_TYPE.RSHIFT, ">>");
              }
            }
          } else {
            token = new Token(TOKEN_TYPE.GT, ">");
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          c = nextChar(builder);
          state = CHAR_TYPE.INIT;
        }
        case LT -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            // <=
            token = new Token(TOKEN_TYPE.LE, "<=");
          } else if (c == '<') {
            // <<
            c = nextChar(builder);
            if (c == '=') {
              // <<=
              token = new Token(TOKEN_TYPE.ASSIGN_LSHIFT, "<<=");
            } else {
              stepBack();
              token = new Token(TOKEN_TYPE.LSHIFT, "<<");
            }
          } else {
            stepBack();
            token = new Token(TOKEN_TYPE.LT, "<");
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          c = nextChar(builder);
          columnNo--;
          state = CHAR_TYPE.INIT;
        }
        case AND -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            // &=
            token = new Token(TOKEN_TYPE.ASSIGN_AND, "&=");
          } else if (c == '&') {
            // &&
            token = new Token(TOKEN_TYPE.CONDITIONAL_AND, "&&");
          } else {
            // &
            stepBack();
            token = new Token(TOKEN_TYPE.AND, "&");
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          c = nextChar(builder);
          state = CHAR_TYPE.INIT;
        }
        case OR -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            // |=
            token = new Token(TOKEN_TYPE.ASSIGN_OR, "|=");
          } else if (c == '|') {
            // ||
            token = new Token(TOKEN_TYPE.CONDITIONAL_OR, "||");
          } else {
            // |
            stepBack();
            token = new Token(TOKEN_TYPE.OR, "|");
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          c = nextChar(builder);
          state = CHAR_TYPE.INIT;
        }
        case NOT -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.NE, "!=");
          } else {
            stepBack();
            token = new Token(TOKEN_TYPE.CONDITIONAL_NOT, "!");
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case INVERT -> {
          Token token;
          c = nextChar(builder);
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_INVERT, "~=");
          } else {
            stepBack();
            token = new Token(TOKEN_TYPE.INVERT, "~");
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case LPAREN -> {
          Token token = new Token(TOKEN_TYPE.LEFT_PARENTHESES, "(");
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case RPAREN -> {
          Token token = new Token(TOKEN_TYPE.RIGHT_PARENTHESES, ")");
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case POWER -> {
          c = nextChar(builder);
          Token token;
          if (c == '=') {
            token = new Token(TOKEN_TYPE.ASSIGN_XOR, "^=");
          } else {
            stepBack();
            token = new Token(TOKEN_TYPE.XOR, "^");
          }
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          c = nextChar(builder);
          state = CHAR_TYPE.INIT;
        }
        case LBRACE -> {
          Token token = new Token(TOKEN_TYPE.LBRACE, "{");
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
        case RBRACE -> {
          Token token = new Token(TOKEN_TYPE.RBRACE, "}");
          injectTokensAndClearBuilder(token);
          tokens.add(token);
          state = CHAR_TYPE.INIT;
          c = nextChar(builder);
        }
      }
    }
    Token token = new Token(TOKEN_TYPE.EOF, "EOF");
    token.setLineNo(lineNo);
    token.setColumnNo(columnNo);
    tokens.add(token);
  }

  private void stepBack(StringBuilder builder) {
    if (offset != code.length()) {
      stepBack();
      builder.delete(builder.length() - 1, builder.length());
    }
  }

  public List<Token> getTokens() {
    return tokens;
  }

  private enum CHAR_TYPE {
    INIT, DOT, NEWLINE, COLON, BRACKET, COMMA, IDENTIFIER, NUMBER, QUOTATION, PLUS, MINUS, MUL, DIV, MOD, ASSIGN, HASH, LT, GT, NE, AND, OR, XOR, NOT, POWER, INVERT, LPAREN, RPAREN, LBRACKET, RBRACKET, LBRACE, RBRACE, SEMICOLON
  }

  public enum TOKEN_TYPE {
    EOF, NEWLINE, STARTER, // used only in the function doParse()
    COMMA, IDENTIFIER, LBRACKET, RBRACKET, TRUE, FALSE, COLON, IMPORT, INT, FLOAT, STRING, SEMICOLON, LEFT_PARENTHESES, RIGHT_PARENTHESES, PLUS, MINUS, MUL, DIV, TRUE_DIV, MOD, LSHIFT, RSHIFT, U_RSHIFT, // unconditional left shift
    ASSIGN, ASSIGN_TRUE_DIV, EQ, LT, GT, GE, LE, NE, AND, CONDITIONAL_AND, OR, CONDITIONAL_OR, POWER, XOR, NOT, INVERT, CONDITIONAL_NOT, ASSIGN_ADD, ARROW, ASSIGN_SUB, ASSIGN_MUL, ASSIGN_DIV, ASSIGN_POWER, ASSIGN_MOD, ASSIGN_INVERT, ASSIGN_LSHIFT, ASSIGN_RSHIFT, ASSIGN_U_RSHIFT, ASSIGN_AND, ASSIGN_OR, ASSIGN_XOR, IF, ELSE, DO, WHILE, FOR, BREAK, CONTINUE, RETURN, DOT, LBRACE, RBRACE, IN, CLASS, DEF, TRY, CATCH, FINALLY, GLOBAL, NONE,
  }

  public static class TokenType extends SPLCommonType {

    private TokenType() {
      super(null, "token", Token.class);
    }

    public static TokenType getInstance() {
      return SelfHolder.INSTANCE;
    }

    private static class SelfHolder {
      public static final TokenType INSTANCE = new TokenType();

    }
  }

  public static class Token extends SPLObject {
    public TOKEN_TYPE token;
    public Object value;
    private int lineNo;
    private int columnNo;

    private int length;

    public Token(TOKEN_TYPE token, Object value) {
      super(TokenType.getInstance());
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
          case "break" -> {
            this.token = TOKEN_TYPE.BREAK;
          }
          case "finally" -> {
            this.token = TOKEN_TYPE.FINALLY;
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
          case "try" -> {
            this.token = TOKEN_TYPE.TRY;
          }
          case "catch" -> {
            this.token = TOKEN_TYPE.CATCH;
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
          case "else" -> {
            this.token = TOKEN_TYPE.ELSE;
          }
          case "none" -> {
            this.token = TOKEN_TYPE.NONE;
          }
          case "return" -> {
            this.token = TOKEN_TYPE.RETURN;
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


    public boolean isComma() {
      return token == TOKEN_TYPE.COMMA;
    }

    public boolean isColon() {
      return token == TOKEN_TYPE.COLON;
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

    public boolean isTrueDiv() {
      return token == TOKEN_TYPE.TRUE_DIV;
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

    public boolean isGlobal() {
      return token == TOKEN_TYPE.GLOBAL;
    }

    public boolean isArrow() {
      return token == TOKEN_TYPE.ARROW;
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

    public boolean isPureAssign() {
      return token == TOKEN_TYPE.ASSIGN;
    }

    public boolean isConstant() {
      return token == TOKEN_TYPE.INT ||
          token == TOKEN_TYPE.FLOAT ||
          token == TOKEN_TYPE.STRING;
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

    public boolean isRBRACE() {
      return token == TOKEN_TYPE.RBRACE;
    }

    public boolean isLBRACE() {
      return token == TOKEN_TYPE.LBRACE;
    }

    public boolean isRBRACKET() {
      return token == TOKEN_TYPE.RBRACKET;
    }

    public boolean isLBRACKET() {
      return token == TOKEN_TYPE.LBRACKET;
    }

    public boolean isRPAREN() {
      return token == TOKEN_TYPE.RIGHT_PARENTHESES;
    }

    public boolean isReturn() {
      return token == TOKEN_TYPE.RETURN;
    }

    public boolean isRIGHT_PARENTHESES() {
      return token == TOKEN_TYPE.RIGHT_PARENTHESES;
    }

    public boolean isINT() {
      return token == TOKEN_TYPE.INT;
    }

    public boolean isBreak() {
      return token == TOKEN_TYPE.BREAK;
    }

    public boolean isContinue() {
      return token == TOKEN_TYPE.CONTINUE;
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

    public boolean isTry() {
      return token == TOKEN_TYPE.TRY;
    }

    public boolean isCatch() {
      return token == TOKEN_TYPE.CATCH;
    }

    public boolean isFinally() {
      return token == TOKEN_TYPE.FINALLY;
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

    public boolean isFor() {
      return token == TOKEN_TYPE.FOR;
    }

    public boolean isWHILE() {
      return token == TOKEN_TYPE.WHILE;
    }

    public boolean isDef() {
      return token == TOKEN_TYPE.DEF;
    }

    public boolean isNOT() {
      return token == TOKEN_TYPE.NOT;
    }

    public boolean isNone() {
      return token == TOKEN_TYPE.NONE;
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

    public boolean isSemiColon() {
      return token == TOKEN_TYPE.SEMICOLON;
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

    @Override
    public SPLObject __str__() {
      return new SPLStringObject(toString());
    }
  }
}
