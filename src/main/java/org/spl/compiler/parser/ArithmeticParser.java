package org.spl.compiler.parser;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.ir.ASTContext;
import org.spl.compiler.ir.BuiltinNames;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Scope;
import org.spl.compiler.ir.binaryop.*;
import org.spl.compiler.ir.controlflow.ProgramBlock;
import org.spl.compiler.ir.exp.FuncCallExp;
import org.spl.compiler.ir.exp.MethodCall;
import org.spl.compiler.ir.stmt.*;
import org.spl.compiler.ir.unaryop.Invert;
import org.spl.compiler.ir.unaryop.Not;
import org.spl.compiler.ir.vals.*;
import org.spl.compiler.lexer.Lexer;
import org.spl.compiler.lexer.TokenFlow;
import org.spl.exceptions.SPLException;
import org.spl.exceptions.SPLSyntaxError;
import org.spl.interpreter.objects.SPLBoolObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class ArithmeticParser extends AbstractSyntaxParser {
  private final TokenFlow<Lexer.Token> tokenFlow;
  private final ASTContext<Instruction> context;
  private IRNode<Instruction> root;

  public ArithmeticParser(String filename) throws IOException, SPLSyntaxError {
    super(filename);
    tokenFlow = new TokenFlow<>(tokens);
    context = new ASTContext<>();
  }

  @Override
  public IRNode<Instruction> buildAST() throws SPLSyntaxError {
    root = program();
    return root;
  }

  public IRNode<Instruction> getAST() {
    if (root == null) {
      throw new RuntimeException("AST not built");
    }
    return root;
  }

  private IRNode<Instruction> program() throws SPLSyntaxError {
    ProgramBlock block = new ProgramBlock();
    tokenFlow.next();
    while (!tokenFlow.peek().isEOF()) {
      Lexer.Token top = tokenFlow.peek();
      while (top.isNEWLINE() || top.isSEMICOLON()) {
        tokenFlow.next();
        top = tokenFlow.peek();
      }
      if (top.isEOF())
        break;
      IRNode<Instruction> node = statement();
      block.addIRNode(node);
    }
    return block;
  }

  private IRNode<Instruction> statement() throws SPLSyntaxError {
    try {
      if (tokenFlow.lookAhead().isASSIGN()) {
        return assignment();
      } else {
        return expression();
      }
    } catch (IndexOutOfBoundsException e) {
      throwSyntaxError("Illegal statement, expected assignment or expression", tokenFlow.peek());
    }
    return null;
  }


  private IRNode<Instruction> expression() throws SPLSyntaxError {
    IRNode<Instruction> term = term();
    Lexer.Token token;
    while (tokenFlow.peek().isPLUS() ||
        tokenFlow.peek().isMINUS() ||
        tokenFlow.peek().isCONDITIONAL_AND() ||
        tokenFlow.peek().isCONDITIONAL_OR()) {
      token = tokenFlow.peek();
      switch (token.token) {
        case PLUS -> {
          tokenFlow.next();
          term = new Add(term, term());
        }
        case MINUS -> {
          tokenFlow.next();
          term = new Sub(term, term());
        }
        case CONDITIONAL_AND -> {
          tokenFlow.next();
          term = new ConditionalAnd(term, term());
        }
        case CONDITIONAL_OR -> {
          tokenFlow.next();
          term = new ConditionalOr(term, term());
        }
      }
    }
    return term;
  }

  private IRNode<Instruction> term() throws SPLSyntaxError {
    if (tokenFlow.peek().isNOT()) {
      tokenFlow.next();
      return new Not(term());
    }
    IRNode<Instruction> lhs = power();
    Lexer.Token token = tokenFlow.peek();
    while (token.isMUL() || token.isDIV() ||
        token.isEQ() || token.isNE() ||
        token.isLT() || token.isGT() ||
        token.isLE() || token.isGE()) {
      switch (token.token) {
        case MUL -> {
          tokenFlow.next();
          lhs = new Mul(lhs, power());
        }
        case DIV -> {
          tokenFlow.next();
          lhs = new Div(lhs, power());
        }
        case EQ -> {
          tokenFlow.next();
          lhs = new Equal(lhs, expression());
        }
        case NE -> {
          tokenFlow.next();
          lhs = new NotEqual(lhs, expression());
        }
        case LE -> {
          tokenFlow.next();
          lhs = new LessOrEqual(lhs, expression());
        }
        case LT -> {
          tokenFlow.next();
          lhs = new LessThan(lhs, expression());
        }
        case GE -> {
          tokenFlow.next();
          lhs = new GreaterOrEqual(lhs, expression());
        }
        case GT -> {
          tokenFlow.next();
          lhs = new GreaterThan(lhs, expression());
        }
        default -> {
          throwSyntaxError("Unexpected token " + token.getValueAsString(), token);
        }
      }
    }
    return lhs;
  }

  private IRNode<Instruction> power() throws SPLSyntaxError {
    IRNode<Instruction> lhs = unary();
    while (tokenFlow.peek().isPower() ||
        tokenFlow.peek().isLSHIFT() ||
        tokenFlow.peek().isRSHIFT() ||
        tokenFlow.peek().isU_LSHIFT()) {
      switch (tokenFlow.peek().token) {
        case POWER -> {
          tokenFlow.next();
          lhs = new Power(lhs, unary());
        }
        case LSHIFT -> {
          tokenFlow.next();
          lhs = new LShift(lhs, unary());
        }
        case RSHIFT -> {
          tokenFlow.next();
          lhs = new RShift(lhs, unary());
        }
        case U_LSHIFT -> {
          tokenFlow.next();
          lhs = new ULShift(lhs, unary());
        }
      }
    }
    return lhs;
  }

  private IRNode<Instruction> unary() throws SPLSyntaxError {
    Lexer.Token token = tokenFlow.peek();
    if (token.isINVERT()) {
      return new Invert(unary());
    } else {
      return factor();
    }
  }

  private IRNode<Instruction> factor() throws SPLSyntaxError {
    Lexer.Token token = tokenFlow.peek();
    if (token.isINT()) {
      tokenFlow.next();
      int idx = context.addConstant(token.getInt());
      return new IntLiteral(token.getInt(), (byte) idx);
    } else if (token.isFLOAT()) {
      tokenFlow.next();
      int idx = context.addConstant(token.getFloat());
      return new FloatLiteral(token.getFloat(), (byte) idx);
    } else if (token.isSTRING()) {
      tokenFlow.next();
      int idx = context.addConstant(token.getValueAsString());
      return new StringLiteral(token.getValueAsString(), (byte) idx);
    } else if (token.isFALSE()) {
      tokenFlow.next();
      SPLBoolObject o = SPLBoolObject.getFalse();
      int idx = context.addConstant(o);
      return new BoolLiteral((byte) idx);
    } else if (token.isTRUE()) {
      tokenFlow.next();
      SPLBoolObject o = SPLBoolObject.getTrue();
      int idx = context.addConstant(o);
      return new BoolLiteral((byte) idx);
    } else if (token.isIDENTIFIER() &&
        tokenFlow.lookAhead().token == Lexer.TOKEN_TYPE.LEFT_PARENTHESES) {
      if (context.containSymbol(token.getIdentifier()) ||
          BuiltinNames.contain(token.getIdentifier())) {
        context.addConstant(token.getIdentifier());
        return functionCall();
      }
      throwSyntaxError("Unknown(Undefined) variable " + token.getIdentifier(), token);
    } else if (token.isIDENTIFIER() &&
        tokenFlow.lookAhead(1).token == Lexer.TOKEN_TYPE.DOT &&
        tokenFlow.lookAhead(2).token == Lexer.TOKEN_TYPE.IDENTIFIER &&
        tokenFlow.lookAhead(3).token == Lexer.TOKEN_TYPE.LEFT_PARENTHESES) {
      if (context.containSymbol(token.getIdentifier()) ||
          BuiltinNames.contain(token.getIdentifier())) {
        context.addConstant(token.getIdentifier());
        return methodCall();
      }
    } else if (token.isIDENTIFIER()) {
      if (context.containSymbol(token.getIdentifier()) ||
          BuiltinNames.contain(token.getIdentifier())) {
        tokenFlow.next();
        context.addConstant(token.getIdentifier());
        return new Variable(Scope.LOCAL, token.getIdentifier());
      }
      throwSyntaxError("Unknown(Undefined) variable " + token.getIdentifier(), token);
    } else if (token.isLPAREN()) {
      tokenFlow.next();
      IRNode<Instruction> expression = expression();
      token = tokenFlow.peek();
      tokenFlow.next();
      if (token.isRPAREN()) {
        return expression;
      } else {
        throwSyntaxError("Expected ')' instead of " + token.getValueAsString(), token);
      }
    }
    throwSyntaxError("Expected an expression instead of " + token.getValueAsString(), token);
    return null;
  }


  private IRNode<Instruction> assignment() throws SPLSyntaxError {
    Lexer.Token token = tokenFlow.peek();
    if (!token.isIDENTIFIER()) {
      throwSyntaxError("Left hand side of assignment must be an variable instead of " + token.getValueAsString(), token);
    }
    tokenFlow.next(); // eat identifier
    Lexer.Token sign = tokenFlow.peek();
    if (!sign.isASSIGN()) {
      throw new SPLSyntaxError(
          SPLSyntaxError.buildErrorMessage(
              filename,
              token.getLineNo(),
              token.getColumnNo(),
              token.getLength(),
              sourceCode.get(token.getLineNo()),
              "Expected an assign operator instead of " + token.getValueAsString()
          ));
    }
    tokenFlow.next();
    switch (sign.token) {
      case ASSIGN -> {
        context.addSymbol(token.getIdentifier());
        context.addConstant(token.getIdentifier());
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new AssignStmt(lhs, rhs);
      }
      case ASSIGN_ADD -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new AndAssignStmt(lhs, rhs);
      }
      case ASSIGN_SUB -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new SubAssignStmt(lhs, rhs);
      }
      case ASSIGN_MUL -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new MulAssignStmt(lhs, rhs);
      }
      case ASSIGN_DIV -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new DivAssignStmt(lhs, rhs);
      }
      case ASSIGN_POWER -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new PowerAssignStmt(lhs, rhs);
      }
      case ASSIGN_MOD -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new ModAssignStmt(lhs, rhs);
      }
      case ASSIGN_OR -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new OrAssignStmt(lhs, rhs);
      }
      case ASSIGN_AND -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new AndAssignStmt(lhs, rhs);
      }
      case ASSIGN_XOR -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new XorAssignStmt(lhs, rhs);
      }
      case ASSIGN_LSHIFT -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new LshiftAssignStmt(lhs, rhs);
      }
      case ASSIGN_RSHIFT -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new RshiftAssignStmt(lhs, rhs);
      }
      case ASSIGN_U_LSHIFT -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        return new ULshiftAssignStmt(lhs, rhs);
      }
    }
    return null;
  }

  private IRNode<Instruction> functionCall() throws SPLSyntaxError {
    Lexer.Token token = tokenFlow.peek();
    tokenFlow.next();
    if (token.isIDENTIFIER()) {
      String name = token.getIdentifier();
      List<IRNode<Instruction>> args = new ArrayList<>();
      tokenFlow.next();
      extractArgs(args);
      return new FuncCallExp(name, args);
    }
    throwSyntaxError("Expected an function name instead of " + token.getValueAsString(), token);
    return null;
  }

  private IRNode<Instruction> methodCall() throws SPLSyntaxError {
    Lexer.Token token = tokenFlow.peek();
    tokenFlow.next();
    if (token.isIDENTIFIER()) {
      throwSyntaxError("Expected an method name instead of " + token.getValueAsString(), token);
    }
    if (tokenFlow.lookAhead().isDOT()) {
      throwSyntaxError("Expected dot instead of " + tokenFlow.lookAhead().getValueAsString(), token);
    }
    String objName = token.getIdentifier();
    tokenFlow.next();
    token = tokenFlow.peek();
    tokenFlow.next();
    String methodName = token.getIdentifier();
    token = tokenFlow.peek();
    tokenFlow.next();
    tokenAssertion(token, Lexer.TOKEN_TYPE.LEFT_PARENTHESES, "Expected '(' instead of " + token.getValueAsString());
    List<IRNode<Instruction>> args = new ArrayList<>();
    extractArgs(args);
    return new MethodCall(objName, methodName, args, Scope.LOCAL);
  }

  private void extractArgs(List<IRNode<Instruction>> args) throws SPLSyntaxError {
    while (tokenFlow.peek().token != Lexer.TOKEN_TYPE.RIGHT_PARENTHESES) {
      args.add(expression());
      if (tokenFlow.peek().token == Lexer.TOKEN_TYPE.COMMA) {
        tokenFlow.next();
      }
    }
    tokenAssertion(
        tokenFlow.peek(),
        Lexer.TOKEN_TYPE.RIGHT_PARENTHESES,
        "Expected ')' instead of  " + tokenFlow.peek().getValueAsString()
    );
    tokenFlow.next(); // eat ')'
  }

  private void tokenAssertion(Lexer.Token token, Lexer.TOKEN_TYPE expected, String message) throws SPLSyntaxError {
    if (token.token != expected) {
      throw new SPLSyntaxError(
          SPLException.buildErrorMessage(
              filename,
              token.getLineNo(),
              token.getColumnNo(),
              token.getLength(),
              sourceCode.get(token.getLineNo()),
              message
          ));
    }
  }

  private void throwSyntaxError(String message, Lexer.Token token) throws SPLSyntaxError {
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

  public ASTContext<Instruction> getContext() {
    return context;
  }

  public TokenFlow<Lexer.Token> getTokenFlow() {
    return tokenFlow;
  }
}
