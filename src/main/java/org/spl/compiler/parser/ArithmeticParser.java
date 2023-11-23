package org.spl.compiler.parser;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLException;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.BuiltinNames;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Scope;
import org.spl.compiler.ir.binaryop.*;
import org.spl.compiler.ir.context.DefaultASTContext;
import org.spl.compiler.ir.controlflow.ProgramBlock;
import org.spl.compiler.ir.exp.FuncCallExp;
import org.spl.compiler.ir.exp.MethodCall;
import org.spl.compiler.ir.exp.Pop;
import org.spl.compiler.ir.stmt.*;
import org.spl.compiler.ir.unaryop.Invert;
import org.spl.compiler.ir.unaryop.Not;
import org.spl.compiler.ir.vals.*;
import org.spl.compiler.lexer.Lexer;
import org.spl.compiler.lexer.TokenFlow;
import org.spl.vm.objects.SPLBoolObject;

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
  private final DefaultASTContext<Instruction> context;
  private IRNode<Instruction> root;

  public ArithmeticParser(String filename) throws IOException, SPLSyntaxError {
    super(filename);
    tokenFlow = new TokenFlow<>(tokens);
    context = new DefaultASTContext<>(filename);
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
    boolean flag = false;
    tokenFlow.next();
    while (!tokenFlow.peek().isEOF()) {
      Lexer.Token top = tokenFlow.peek();
      while (top.isNEWLINE() || top.isSEMICOLON()) {
        tokenFlow.next();
        top = tokenFlow.peek();
      }
      if (top.isEOF())
        break;
      if (!flag) {
        flag = true;
        setSourceCodeInfo(block, top);
      }
      IRNode<Instruction> node = statement();
      block.addIRNode(node);
      if (node instanceof FuncCallExp || node instanceof MethodCall) {
        Lexer.Token token = tokenFlow.peek();
        Pop pop = new Pop();
        setSourceCodeInfo(pop, token);
        block.addIRNode(pop);
      }
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
          setSourceCodeInfo(term, token);
        }
        case MINUS -> {
          tokenFlow.next();
          term = new Sub(term, term());
          setSourceCodeInfo(term, token);
        }
        case CONDITIONAL_AND -> {
          tokenFlow.next();
          term = new ConditionalAnd(term, term());
          setSourceCodeInfo(term, token);
        }
        case CONDITIONAL_OR -> {
          tokenFlow.next();
          term = new ConditionalOr(term, term());
          setSourceCodeInfo(term, token);
        }
      }
    }
    return term;
  }

  private IRNode<Instruction> term() throws SPLSyntaxError {
    if (tokenFlow.peek().isNOT()) {
      Lexer.Token token = tokenFlow.peek();
      tokenFlow.next();
      Not not = new Not(term());
      setSourceCodeInfo(not, token);
      return not;
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
          setSourceCodeInfo(lhs, token);
        }
        case DIV -> {
          tokenFlow.next();
          lhs = new Div(lhs, power());
          setSourceCodeInfo(lhs, token);
        }
        case EQ -> {
          tokenFlow.next();
          lhs = new Equal(lhs, expression());
          setSourceCodeInfo(lhs, token);
        }
        case NE -> {
          tokenFlow.next();
          lhs = new NotEqual(lhs, expression());
          setSourceCodeInfo(lhs, token);
        }
        case LE -> {
          tokenFlow.next();
          lhs = new LessOrEqual(lhs, expression());
          setSourceCodeInfo(lhs, token);
        }
        case LT -> {
          tokenFlow.next();
          lhs = new LessThan(lhs, expression());
          setSourceCodeInfo(lhs, token);
        }
        case GE -> {
          tokenFlow.next();
          lhs = new GreaterOrEqual(lhs, expression());
          setSourceCodeInfo(lhs, token);
        }
        case GT -> {
          tokenFlow.next();
          lhs = new GreaterThan(lhs, expression());
          setSourceCodeInfo(lhs, token);
        }
        default -> {
          throwSyntaxError("Unexpected token " + token.getValueAsString(), token);
        }
      }
      token = tokenFlow.peek();
    }
    return lhs;
  }

  private IRNode<Instruction> power() throws SPLSyntaxError {
    IRNode<Instruction> lhs = unary();
    Lexer.Token token = tokenFlow.peek();
    while (token.isPower() ||
        token.isLSHIFT() ||
        token.isRSHIFT() ||
        token.isU_RSHIFT() ||
        token.isXOR() || token.isAND() ||
        token.isOR()) {
      token = tokenFlow.peek();
      switch (token.token) {
        case POWER -> {
          tokenFlow.next();
          lhs = new Power(lhs, unary());
          setSourceCodeInfo(lhs, token);
        }
        case LSHIFT -> {
          tokenFlow.next();
          lhs = new LShift(lhs, unary());
          setSourceCodeInfo(lhs, token);
        }
        case RSHIFT -> {
          tokenFlow.next();
          lhs = new RShift(lhs, unary());
          setSourceCodeInfo(lhs, token);
        }
        case U_RSHIFT -> {
          tokenFlow.next();
          lhs = new URShift(lhs, unary());
          setSourceCodeInfo(lhs, token);
        }
        case XOR -> {
          tokenFlow.next();
          lhs = new Xor(lhs, unary());
          setSourceCodeInfo(lhs, token);
        }
        case AND -> {
          tokenFlow.next();
          lhs = new And(lhs, unary());
          setSourceCodeInfo(lhs, token);
        }
        case OR -> {
          tokenFlow.next();
          lhs = new Or(lhs, unary());
          setSourceCodeInfo(lhs, token);
        }
      }
    }
    return lhs;
  }

  private IRNode<Instruction> unary() throws SPLSyntaxError {
    Lexer.Token token = tokenFlow.peek();
    if (token.isINVERT()) {
      Invert invert = new Invert(unary());
      setSourceCodeInfo(invert, token);
      return invert;
    } else {
      return factor();
    }
  }

  private IRNode<Instruction> factor() throws SPLSyntaxError {
    Lexer.Token token = tokenFlow.peek();
    if (token.isINT()) {
      tokenFlow.next();
      int idx = context.addConstant(token.getInt());
      IntLiteral intLiteral = new IntLiteral(token.getInt(), (byte) idx);
      setSourceCodeInfo(intLiteral, token);
      return intLiteral;
    } else if (token.isFLOAT()) {
      tokenFlow.next();
      int idx = context.addConstant(token.getFloat());
      FloatLiteral floatLiteral = new FloatLiteral(token.getFloat(), (byte) idx);
      setSourceCodeInfo(floatLiteral, token);
      return floatLiteral;
    } else if (token.isSTRING()) {
      tokenFlow.next();
      int idx = context.addConstant(token.getValueAsString());
      StringLiteral stringLiteral = new StringLiteral(token.getValueAsString(), (byte) idx);
      setSourceCodeInfo(stringLiteral, token);
      return stringLiteral;
    } else if (token.isFALSE()) {
      tokenFlow.next();
      SPLBoolObject o = SPLBoolObject.getFalse();
      int idx = context.addConstant(o);
      BoolLiteral boolLiteral = new BoolLiteral((byte) idx);
      setSourceCodeInfo(boolLiteral, token);
      return boolLiteral;
    } else if (token.isTRUE()) {
      tokenFlow.next();
      SPLBoolObject o = SPLBoolObject.getTrue();
      int idx = context.addConstant(o);
      BoolLiteral boolLiteral = new BoolLiteral((byte) idx);
      setSourceCodeInfo(boolLiteral, token);
      return boolLiteral;
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
        Variable variable = new Variable(Scope.LOCAL, token.getIdentifier());
        setSourceCodeInfo(variable, token);
        return variable;
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
        AssignStmt assignStmt = new AssignStmt(lhs, rhs);
        setSourceCodeInfo(assignStmt, sign);
        return assignStmt;
      }
      case ASSIGN_ADD -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        AddAssignStmt addAssignStmt = new AddAssignStmt(lhs, rhs);
        setSourceCodeInfo(addAssignStmt, sign);
        return addAssignStmt;
      }
      case ASSIGN_SUB -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        SubAssignStmt subAssignStmt = new SubAssignStmt(lhs, rhs);
        setSourceCodeInfo(subAssignStmt, sign);
        return subAssignStmt;
      }
      case ASSIGN_MUL -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        MulAssignStmt mulAssignStmt = new MulAssignStmt(lhs, rhs);
        setSourceCodeInfo(mulAssignStmt, sign);
        return mulAssignStmt;
      }
      case ASSIGN_DIV -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        DivAssignStmt divAssignStmt = new DivAssignStmt(lhs, rhs);
        setSourceCodeInfo(divAssignStmt, sign);
        return divAssignStmt;
      }
      case ASSIGN_POWER -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        PowerAssignStmt powerAssignStmt = new PowerAssignStmt(lhs, rhs);
        setSourceCodeInfo(powerAssignStmt, sign);
        return powerAssignStmt;
      }
      case ASSIGN_MOD -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        ModAssignStmt modAssignStmt = new ModAssignStmt(lhs, rhs);
        setSourceCodeInfo(modAssignStmt, sign);
        return modAssignStmt;
      }
      case ASSIGN_OR -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        OrAssignStmt orAssignStmt = new OrAssignStmt(lhs, rhs);
        setSourceCodeInfo(orAssignStmt, sign);
        return orAssignStmt;
      }
      case ASSIGN_AND -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        AndAssignStmt andAssignStmt = new AndAssignStmt(lhs, rhs);
        setSourceCodeInfo(andAssignStmt, sign);
        return andAssignStmt;
      }
      case ASSIGN_XOR -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        XorAssignStmt xorAssignStmt = new XorAssignStmt(lhs, rhs);
        setSourceCodeInfo(xorAssignStmt, sign);
        return xorAssignStmt;
      }
      case ASSIGN_LSHIFT -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        LshiftAssignStmt lshiftAssignStmt = new LshiftAssignStmt(lhs, rhs);
        setSourceCodeInfo(lshiftAssignStmt, sign);
        return lshiftAssignStmt;
      }
      case ASSIGN_RSHIFT -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        RshiftAssignStmt rshiftAssignStmt = new RshiftAssignStmt(lhs, rhs);
        setSourceCodeInfo(rshiftAssignStmt, sign);
        return rshiftAssignStmt;
      }
      case ASSIGN_U_RSHIFT -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier());
        ULshiftAssignStmt uLshiftAssignStmt = new ULshiftAssignStmt(lhs, rhs);
        setSourceCodeInfo(uLshiftAssignStmt, sign);
        return uLshiftAssignStmt;
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
      FuncCallExp funcCallExp = new FuncCallExp(name, args);
      setSourceCodeInfo(funcCallExp, token);
      return funcCallExp;
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
    Lexer.Token source = new Lexer.Token(null, null);
    source.setLineNo(token.getLineNo());
    source.setColumnNo(token.getColumnNo());
    source.setLength(token.getLength());
    tokenFlow.next();
    token = tokenFlow.peek();
    tokenFlow.next();
    String methodName = token.getIdentifier();
    if (token.getLineNo() == source.getLineNo()) {
      source.setLength(token.getColumnNo() - source.getColumnNo() + token.getLength());
    }
    token = tokenFlow.peek();
    tokenFlow.next();
    tokenAssertion(token, Lexer.TOKEN_TYPE.LEFT_PARENTHESES, "Expected '(' instead of " + token.getValueAsString());
    List<IRNode<Instruction>> args = new ArrayList<>();
    extractArgs(args);
    MethodCall methodCall = new MethodCall(objName, methodName, args, Scope.LOCAL);
    setSourceCodeInfo(methodCall, source);
    return methodCall;
  }

  private void extractArgs(List<IRNode<Instruction>> args) throws SPLSyntaxError {
    if (tokenFlow.peek().token != Lexer.TOKEN_TYPE.RIGHT_PARENTHESES) {
      args.add(expression());
    }
    while (tokenFlow.peek().token != Lexer.TOKEN_TYPE.RIGHT_PARENTHESES) {
      if (tokenFlow.peek().token == Lexer.TOKEN_TYPE.COMMA) {
        tokenFlow.next();
        args.add(expression());
      } else {
        throw new SPLSyntaxError(
            SPLException.buildErrorMessage(
                filename,
                tokenFlow.peek().getLineNo(),
                tokenFlow.peek().getColumnNo(),
                tokenFlow.peek().getLength(),
                sourceCode.get(tokenFlow.peek().getLineNo()),
                "Expected ',' or ')' instead of " + tokenFlow.peek().getValueAsString())
        );
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

  public DefaultASTContext<Instruction> getContext() {
    return context;
  }

  public TokenFlow<Lexer.Token> getTokenFlow() {
    return tokenFlow;
  }

  public void setSourceCodeInfo(IRNode<?> node, Lexer.Token token) {
    node.setLineNo(token.getLineNo());
    node.setColumnNo(token.getColumnNo());
    node.setLen(token.getLength());
  }
}
