package org.spl.compiler.lexer;

import org.spl.exceptions.SPLException;
import org.spl.exceptions.SPLSyntaxError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {


	private final List<Token> tokens;
	private final InputBuffer stream;
	private int lineNo = 1;
	private int columnNo = 0;

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
		token.setLength(builder.length());
		builder.delete(0, builder.length());
	}

	public void doParse() throws SPLSyntaxError {
		CHAR_TYPE state = CHAR_TYPE.INIT;
		StringBuilder builder = new StringBuilder();
		char c = stream.nextChar();
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
					} else {
						// white space characters
						columnNo = stream.getColumnNo();
						c = stream.nextChar();
					}
				}
				// identifier branch
				case IDENTIFIER -> {
					while (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_') {
						builder.append(c);
						c = stream.nextChar();
					}
					Token token = new Token(TOKEN_TYPE.IDENTIFIER, builder.toString());
					injectTokensAndClearBuilder(token, builder);
					tokens.add(token);
					updateLineAndColumn();
					state = CHAR_TYPE.INIT;
				}
				case NUMBER -> {
					// number branch
					while (Character.isDigit(c)) {
						builder.append(c);
						c = stream.nextChar();
					}
					if (c == '.') {
						builder.append(c);
						c = stream.nextChar();
						while (Character.isDigit(c)) {
							builder.append(c);
							c = stream.nextChar();
						}
						if (c == '.') {
							String msg = SPLException.buildErrorMessage(
									stream.getFileName(),
									lineNo,
									columnNo,
									stream.getOff() - columnNo,
									stream.getBuffer(),
									"Illegal float literal, two or more dots are not allowed in float literal"
							);
							throw new SPLSyntaxError(msg);
						}
						Token token = new Token(TOKEN_TYPE.FLOAT, Float.parseFloat(builder.toString()));
						injectTokensAndClearBuilder(token, builder);
						updateLineAndColumn();
						tokens.add(token);
					} else {
						Token token = new Token(TOKEN_TYPE.INT, Integer.parseInt(builder.toString()));
						injectTokensAndClearBuilder(token, builder);
						updateLineAndColumn();
						tokens.add(token);
					}
					state = CHAR_TYPE.INIT;
				}
				case QUOTATION -> {
					// string branch
					c = stream.nextChar();
					while (c != '"') {
						if (c == 0 || Character.isWhitespace(c)) break;
						builder.append(c);
						c = stream.nextChar();
					}
					if (c == '"') {
						Token token = new Token(TOKEN_TYPE.STRING, builder.toString());
						injectTokensAndClearBuilder(token, builder);
						updateLineAndColumn();
						tokens.add(token);
					} else {
						String msg = SPLException.buildErrorMessage(
								stream.getFileName(),
								lineNo,
								columnNo,
								stream.getOff() - columnNo,
								stream.getBuffer(),
								"Illegal string literal, string literal must be enclosed in double quotes"
						);
						throw new SPLSyntaxError(msg);
					}
					builder.delete(0, builder.length());
					state = CHAR_TYPE.INIT;
				}
				case PLUS -> {
					c = stream.nextChar();
					Token token;
					if (c == '=') {
						token = new Token(TOKEN_TYPE.ASSIGN_ADD, "+=");
						c = stream.nextChar();
					} else {
						token = new Token(TOKEN_TYPE.PLUS, "+");
					}
					injectTokensAndClearBuilder(token, builder);
					tokens.add(token);
					updateLineAndColumn();
					builder.delete(0, builder.length());
					state = CHAR_TYPE.INIT;
				}
				case MINUS -> {
					c = stream.nextChar();
					Token token;
					if (c == '=') {
						token = new Token(TOKEN_TYPE.ASSIGN_SUB, "-=");
						c = stream.nextChar();
					} else {
						token = new Token(TOKEN_TYPE.MINUS, "-");
					}
					injectTokensAndClearBuilder(token, builder);
					tokens.add(token);
					updateLineAndColumn();
					state = CHAR_TYPE.INIT;
				}
				case MUL -> {
					c = stream.nextChar();
					Token token;
					if (c == '=') {
						c = stream.nextChar();
						token = new Token(TOKEN_TYPE.ASSIGN_MUL, "*=");
					} else {
						token = new Token(TOKEN_TYPE.MUL, "*");
					}
					injectTokensAndClearBuilder(token, builder);
					tokens.add(token);
					updateLineAndColumn();
					state = CHAR_TYPE.INIT;
				}
				case DIV -> {
					c = stream.nextChar();
					Token token;
					if (c == '=') {
						token = new Token(TOKEN_TYPE.ASSIGN_DIV, "/=");
						c = stream.nextChar();
					} else {
						token = new Token(TOKEN_TYPE.DIV, "/");
					}
					injectTokensAndClearBuilder(token, builder);
					tokens.add(token);
					updateLineAndColumn();
					state = CHAR_TYPE.INIT;
				}
				case MOD -> {
					c = stream.nextChar();
					Token token;
					if (c == '=') {
						token = new Token(TOKEN_TYPE.ASSIGN_MOD, "%=");
						c = stream.nextChar();
					} else {
						token = new Token(TOKEN_TYPE.MOD, "%");
					}
					injectTokensAndClearBuilder(token, builder);
					tokens.add(token);
					updateLineAndColumn();
					state = CHAR_TYPE.INIT;
				}
				case ASSIGN -> {
					c = stream.nextChar();
					Token token;
					if (c == '=') {
						token = new Token(TOKEN_TYPE.EQ, "==");
						c = stream.nextChar();
					} else {
						token = new Token(TOKEN_TYPE.ASSIGN, "=");
					}
					injectTokensAndClearBuilder(token, builder);
					tokens.add(token);
					updateLineAndColumn();
					state = CHAR_TYPE.INIT;
				}
				case GT -> {
					c = stream.nextChar();
					Token token;
					if (c == '=') {
						token = new Token(TOKEN_TYPE.GE, ">=");
						c = stream.nextChar();
					} else if (c == '>') {
						if (stream.lookAhead() == '=') {
							token = new Token(TOKEN_TYPE.ASSIGN_RSHIFT, ">>=");
						} else {
							token = new Token(TOKEN_TYPE.RSHIFT, ">>");
						}
						c = stream.nextChar();
					} else {
						token = new Token(TOKEN_TYPE.GT, ">");
					}
					injectTokensAndClearBuilder(token, builder);
					tokens.add(token);
					updateLineAndColumn();
					state = CHAR_TYPE.INIT;
				}
				case LT -> {
					c = stream.nextChar();
					Token token;
					if (c == '=') {
						// <=
						token = new Token(TOKEN_TYPE.LE, "<=");
						c = stream.nextChar();
					} else if (c == '<') {
						// <<
						c = stream.nextChar();
						if (c == '=') {
							// <<=
							token = new Token(TOKEN_TYPE.ASSIGN_LSHIFT, "<<=");
							c = stream.nextChar();
						} else {
							// <<?
							if (c == '<') {
								c = stream.nextChar();
								if (c == '=') {
									// <<<=
									token = new Token(TOKEN_TYPE.ASSIGN_U_LSHIFT, "<<<=");
									c = stream.nextChar();
								} else {
									// <<<
									token = new Token(TOKEN_TYPE.U_LSHIFT, "<<<");
								}
							} else {
								// <<
								token = new Token(TOKEN_TYPE.LSHIFT, "<<");
							}
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
					c = stream.nextChar();
					Token token;
					if (c == '=') {
						// &=
						token = new Token(TOKEN_TYPE.ASSIGN_AND, "&=");
						c = stream.nextChar();
					} else if (c == '&') {
						// &&
						token = new Token(TOKEN_TYPE.CONDITIONAL_AND, "&&");
						c = stream.nextChar();
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
					c = stream.nextChar();
					Token token;
					if (c == '=') {
						// |=
						token = new Token(TOKEN_TYPE.ASSIGN_OR, "|=");
						c = stream.nextChar();
					} else if (c == '|') {
						// ||
						token = new Token(TOKEN_TYPE.CONDITIONAL_OR, "||");
						c = stream.nextChar();
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
					c = stream.nextChar();
					Token token;
					if (c == '=') {
						token = new Token(TOKEN_TYPE.NE, "!=");
						c = stream.nextChar();
					} else {
						token = new Token(TOKEN_TYPE.CONDITIONAL_NOT, "!");
					}
					injectTokensAndClearBuilder(token, builder);
					tokens.add(token);
					updateLineAndColumn();
					state = CHAR_TYPE.INIT;
				}
				case INVERT -> {
					Token token = new Token(TOKEN_TYPE.NOT, "!");
					c = stream.nextChar();
					injectTokensAndClearBuilder(token, builder);
					tokens.add(token);
					updateLineAndColumn();
					state = CHAR_TYPE.INIT;
				}
				case LPAREN -> {
					Token token = new Token(TOKEN_TYPE.LEFT_PARENTHESES, "(");
					c = stream.nextChar();
					injectTokensAndClearBuilder(token, builder);
					tokens.add(token);
					updateLineAndColumn();
					state = CHAR_TYPE.INIT;
				}
				case RPAREN -> {
					Token token = new Token(TOKEN_TYPE.RIGHT_PARENTHESES, ")");
					c = stream.nextChar();
					injectTokensAndClearBuilder(token, builder);
					tokens.add(token);
					updateLineAndColumn();
					state = CHAR_TYPE.INIT;
				}
				case POWER -> {
					c = stream.nextChar();
					Token token;
					if (c == '=') {
						token = new Token(TOKEN_TYPE.ASSIGN_XOR);
						c = stream.nextChar();
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
					tokens.add(token);
					c = stream.nextChar();
				}
				case RBRACE -> {
					Token token = new Token(TOKEN_TYPE.RBRACE, "}");
					tokens.add(token);
					c = stream.nextChar();
				}
			}
		}

	}

	public List<Token> getTokens() {
		return tokens;
	}

	private enum CHAR_TYPE {
		INIT,
		IDENTIFIER,
		NUMBER,
		QUOTATION,
		PLUS,
		MINUS,
		MUL,
		DIV,
		MOD,
		ASSIGN,
		LT,
		GT,
		NE,
		AND,
		OR,
		XOR,
		NOT,
		POWER,
		INVERT,
		LPAREN,
		RPAREN,
		LBRACKET,
		RBRACKET,
		LBRACE,
		RBRACE
	}

	public enum TOKEN_TYPE {
		STARTER, // used only in the function doParse()
		IDENTIFIER,
		INT,
		FLOAT,
		STRING,
		SEMICOLON,
		LEFT_PARENTHESES,
		RIGHT_PARENTHESES,
		PLUS,
		MINUS,
		MUL,
		DIV,
		MOD,
		LSHIFT,
		RSHIFT,
		U_LSHIFT, // unconditional left shift
		ASSIGN,
		EQ,
		LT,
		GT,
		GE,
		LE,
		NE,
		AND,
		CONDITIONAL_AND,
		OR,
		CONDITIONAL_OR,
		XOR,
		NOT,
		CONDITIONAL_NOT,
		ASSIGN_ADD,
		ASSIGN_SUB,
		ASSIGN_MUL,
		ASSIGN_DIV,
		ASSIGN_MOD,
		ASSIGN_LSHIFT,
		ASSIGN_RSHIFT,
		ASSIGN_U_LSHIFT,
		ASSIGN_AND,
		ASSIGN_OR,
		ASSIGN_XOR,
		IF,
		ELSE,
		DO,
		WHILE,
		FOR,
		BREAK,
		CONTINUE,
		RETURN,
		DOT,
		LBRACE,
		RBRACE,
		IN,
		CLASS,
		DEF,
		GLOBAL
	}

	public static class Token {
		public TOKEN_TYPE token;
		public Object value;
		private int lineNo;
		private int columnNo;

		private int length;

		public Token(TOKEN_TYPE token) {
			this.token = token;
		}

		public Token(TOKEN_TYPE token, Object value) {
			this.token = token;
			this.value = value;
			// fix token type
			if (token == TOKEN_TYPE.IDENTIFIER) {
				assert value != null;
				String val = (String) value;
				switch (val) {
					case "for" ->{
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

		public double getDouble() {
			return (Double) (value);
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

		public boolean isU_LSHIFT() {
			return token == TOKEN_TYPE.U_LSHIFT;
		}

		public boolean isASSIGN() {
			return token == TOKEN_TYPE.ASSIGN;
		}

		public boolean isSEMICOLON() {
			return token == TOKEN_TYPE.SEMICOLON;
		}

		public boolean isLEFT_PARENTHESES() {
			return token == TOKEN_TYPE.LEFT_PARENTHESES;
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

		public boolean isASSIGN_U_LSHIFT() {
			return token == TOKEN_TYPE.ASSIGN_U_LSHIFT;
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

		public String toString() {
			return "Token{" +
					"token:" + token + ";" +
					"value:" + value +
					'}';
		}
	}
}
