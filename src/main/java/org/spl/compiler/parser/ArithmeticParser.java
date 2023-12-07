package org.spl.compiler.parser;

import org.spl.compiler.exceptions.SPLSyntaxError;

import java.io.IOException;

/**
 * program          : statement* EOF;
 * statement        : expression ';' | assignment ';'
 * expression       : term (( '+' | '-' | && | ||) term)*;
 * term             : '!' term | power (( '*' | '/' | '>' | '>=' | '<' | '<=' | '!=' | '==') power)*;
 * power            : unary ( '**' | '>>' | '<<' | '>>> | '>' unary )*;
 * unary            : '~' unary | factor;
 * factor           : true | false | NUMBER | functionCall | '(' expression ')' |
 *                    IDENTIFIER ｜ method call;
 * assignment       : IDENTIFIER '=' expression;
 * functionCall     : IDENTIFIER '(' argumentList? ')';
 * method call      : IDENTIFIER '.' IDENTIFIER '(' argumentList? ')';
 * argumentList     : expression (',' expression)*;
 * NUMBER           : [0-9]+;
 * IDENTIFIER       : [a-zA-Z]+;
 */
public class ArithmeticParser extends SPLParser {

  public ArithmeticParser(String filename) throws IOException, SPLSyntaxError {
    super(filename);
  }
}
