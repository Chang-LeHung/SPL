grammar SimpleCalculator;


// Parser rules
prog : (expr SEMICOLON)+ EOF;

expr : expr '+' term
     | expr '-' term
     | term
     | assignExpr
     ;

term : term '*' factor
     | term '/' factor
     | factor
     ;

factor : '(' expr ')'
       | factor POWER factor
       | VARIABLE '(' expr (',' expr)* ')'
       | NUMBER
       | VARIABLE
       ;


assignExpr : VARIABLE '=' expr;

// Lexer rules
NUMBER : [0-9]+;
VARIABLE : [a-zA-Z][a-zA-Z_]*;
PLUS : '+';
MINUS : '-';
MULTIPLY : '*';
POWER: '**';
DIVIDE : '/';
LPAREN : '(';
RPAREN : ')';
SEMICOLON : ';';
WS : [ \t\r\n]+ -> skip;
