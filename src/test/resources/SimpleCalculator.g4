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
       | NUMBER
       | VARIABLE
       ;

assignExpr : VARIABLE '=' expr;

// Lexer rules
NUMBER : [0-9]+;
VARIABLE : [a-zA-Z]+;
PLUS : '+';
MINUS : '-';
MULTIPLY : '*';
POWER: '**';
DIVIDE : '/';
LPAREN : '(';
RPAREN : ')';
SEMICOLON : ';';
WS : [ \t\r\n]+ -> skip;
