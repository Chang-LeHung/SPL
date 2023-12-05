package org.spl.compiler.parser;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLException;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.BuiltinNames;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Scope;
import org.spl.compiler.ir.binaryop.*;
import org.spl.compiler.ir.block.ProgramBlock;
import org.spl.compiler.ir.controlflow.IfStmt;
import org.spl.compiler.ir.exp.FuncCallExp;
import org.spl.compiler.ir.exp.MethodCall;
import org.spl.compiler.ir.exp.Pop;
import org.spl.compiler.ir.stmt.*;
import org.spl.compiler.ir.unaryop.Invert;
import org.spl.compiler.ir.unaryop.Neg;
import org.spl.compiler.ir.unaryop.Not;
import org.spl.compiler.ir.vals.*;
import org.spl.compiler.lexer.Lexer;
import org.spl.vm.objects.SPLBoolObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * program      : block*
 * block        : '{' statement* '}'
 *              | statement
 * statement    : ifStatement
 *              | expression
 *              | assign
 * ifStatement  : 'if' '(' expression  ') 'block ('else if' '(' expression  ') block)* ('else' block)*
 * assign       : IDENTIFIER '=' expression
 *              | IDENTIFIER '+=' expression
 *              | IDENTIFIER '-=' expression
 *              | IDENTIFIER '*=' expression
 *              | IDENTIFIER '/=' expression
 *              | IDENTIFIER '//=' expression
 *              | IDENTIFIER '**=' expression
 *              | IDENTIFIER '%=' expression
 *              | IDENTIFIER '<<=' expression
 *              | IDENTIFIER '>>=' expression
 *              | IDENTIFIER '>>>=' expression
 *              | IDENTIFIER '&=' expression
 *              | IDENTIFIER '^=' expression
 *              | IDENTIFIER '|=' expression
 * expression   : disjunction 'if' disjunction 'else' expression
 *              | disjunction
 * disjunction  : conjunction ('||' conjunction)+
 *              | conjunction
 * conjunction  : inversion
 *              | inversion ('&&' inversion)+
 * inversion    : '!' inversion
 *              | comparison
 * comparison   : bitwise_or
 *              | bitwise_or ('==' bitwise_or )
 *              | bitwise_or ('!=' bitwise_or)
 *              | bitwise_or ('<' bitwise_or)
 *              | bitwise_or ('>' bitwise_or)
 *              | bitwise_or ('<=' bitwise_or)
 *              | bitwise_or ('>=' bitwise_or)
 * bitwise_or   : bitwise_xor ('|' bitwise_xor)*
 * bitwise_xor  : bitwise_and ('^' bitwise_and)*
 * bitwise_and  : bitwise_shift ('&' bitwise_shift)*
 * shift_expr   : sum
 *              | sum ('<<' sum)+
 *              | sum ('>>' sum)+
 *              | sum ('>>>' sum)+
 * sum          : term
 *              | term ('+' term | '-' term)+
 * term         : factor
 *              | factor ('*' factor | '/' factor)+
 *              | factor ('%' factor)+
 *              | factor ('//' factor)+
 * factor       : '~' factor
 *              | '+' factor
 *              | '-' factor
 *              | power
 * power        : primary
 *              | primary ** factor
 * primary      : atom primary'
 * primary'     : '.' IDENTIFIER primary'*
 *              | '(' args ')' primary'*
 * atom         : IDENTIFIER
 *              | 'true'
 *              | 'false'
 *              | 'none'
 *              | string
 *              | number
 *              | '(' expression ')'
 *
 */
public class SPLParser extends AbstractSyntaxParser {
  private IRNode<Instruction> root;

  public SPLParser(String filename) throws IOException, SPLSyntaxError {
    super(filename);
  }


  @Override
  public IRNode<Instruction> buildAST() throws SPLSyntaxError {
    if (root != null)
      return root;
    root = program();
    return root;
  }

  @Override
  public IRNode<Instruction> getAST() {
    if (root == null) {
      throw new RuntimeException("AST not built");
    }
    return root;
  }

  private void iterateToEffectiveToken() {
    Lexer.Token top = tokenFlow.peek();
    while (top.isNEWLINE() || top.isSEMICOLON()) {
      tokenFlow.next();
      top = tokenFlow.peek();
      if (top.isEOF())
        break;
    }
  }

  private IRNode<Instruction> program() throws SPLSyntaxError {
    ProgramBlock block = new ProgramBlock();
    boolean flag = false;
    tokenFlow.next();
    while (!tokenFlow.peek().isEOF()) {
      iterateToEffectiveToken();
      Lexer.Token top = tokenFlow.peek();
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
      // if (!(tokenFlow.peek().isNEWLINE() || tokenFlow.peek().isSEMICOLON())) {
      //   throwSyntaxError("Illegal statement, expected semicolon or newline", tokenFlow.peek());
      // }
      // tokenFlow.next();
    }
    return block;
  }

  private IRNode<Instruction> statement() throws SPLSyntaxError {
    try {
      if (tokenFlow.peek().isIF()) {
        return ifStatement();
      } else if (tokenFlow.peek().isIDENTIFIER() && tokenFlow.lookAhead().isASSIGN()) {
        return assignment();
      } else {
        // we will perfect this in the future
        int cursor = tokenFlow.getCursor();
        primary();
        if (tokenFlow.peek().isASSIGN()) {
          tokenFlow.setCursor(cursor);
          return assignment();
        } else {
          tokenFlow.setCursor(cursor);
          return expression();
        }
      }
    } catch (IndexOutOfBoundsException e) {
      throwSyntaxError("Illegal statement, expected assignment or expression", tokenFlow.peek());
    }
    return null;
  }

  private IRNode<Instruction> block() throws SPLSyntaxError {
    if (tokenFlow.peek().isLBRACE()) {
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LBRACE, "require { instead of " + tokenFlow.peek().getValueAsString());
      tokenFlow.next();
      iterateToEffectiveToken();
      ProgramBlock codeBlock = new ProgramBlock();
      setSourceCodeInfo(codeBlock, tokenFlow.peek());
      while (!tokenFlow.peek().isRBRACE()) {
        codeBlock.addIRNode(statement());
        iterateToEffectiveToken();
      }
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RBRACE, "require } instead of " + tokenFlow.peek().getValueAsString());
      tokenFlow.next();
      iterateToEffectiveToken();
      return codeBlock;
    } else {
      return statement();
    }
  }

  private IRNode<Instruction> primary() throws SPLSyntaxError {
    return atom();
  }


  private IRNode<Instruction> expression() throws SPLSyntaxError {
    return disjunction();
  }

  private IRNode<Instruction> disjunction() throws SPLSyntaxError {
    IRNode<Instruction> L = conjunction();
    while (tokenFlow.peek().isCONDITIONAL_OR()) {
      Lexer.Token token = tokenFlow.peek();
      tokenFlow.next();
      IRNode<Instruction> R = conjunction();
      L = new ConditionalOr(L, R);
      setSourceCodeInfo(L, token);
    }
    return L;
  }

  private IRNode<Instruction> conjunction() throws SPLSyntaxError {
    IRNode<Instruction> L = inversion();
    while (tokenFlow.peek().isCONDITIONAL_AND()) {
      Lexer.Token token = tokenFlow.peek();
      tokenFlow.next();
      IRNode<Instruction> R = inversion();
      L = new ConditionalAnd(L, R);
      setSourceCodeInfo(L, token);
    }
    return L;
  }

  private IRNode<Instruction> inversion() throws SPLSyntaxError {
    if (tokenFlow.peek().isNOT()) {
      Lexer.Token token = tokenFlow.peek();
      tokenFlow.next();
      Not not = new Not(inversion());
      setSourceCodeInfo(not, token);
      return not;
    }
    return comparison();
  }

  private IRNode<Instruction> comparison() throws SPLSyntaxError {
    IRNode<Instruction> L = bitwiseOr();
    Lexer.Token token = tokenFlow.peek();
    switch (token.token) {
      case EQ -> {
        tokenFlow.next();
        IRNode<Instruction> R = bitwiseOr();
        L = new Equal(L, R);
        setSourceCodeInfo(L, token);
      }
      case NE -> {
        tokenFlow.next();
        IRNode<Instruction> R = bitwiseOr();
        L = new NotEqual(L, R);
        setSourceCodeInfo(L, token);
      }
      case LT -> {
        tokenFlow.next();
        IRNode<Instruction> R = bitwiseOr();
        L = new LessThan(L, R);
        setSourceCodeInfo(L, token);
      }
      case GT -> {
        tokenFlow.next();
        IRNode<Instruction> R = bitwiseOr();
        L = new GreaterThan(L, R);
        setSourceCodeInfo(L, token);
      }
      case LE -> {
        tokenFlow.next();
        IRNode<Instruction> R = bitwiseOr();
        L = new LessOrEqual(L, R);
        setSourceCodeInfo(L, token);
      }
      case GE -> {
        tokenFlow.next();
        IRNode<Instruction> R = bitwiseOr();
        L = new GreaterOrEqual(L, R);
        setSourceCodeInfo(L, token);
      }
    }
    return L;
  }

  private IRNode<Instruction> bitwiseOr() throws SPLSyntaxError {
    IRNode<Instruction> L = bitwiseXor();
    while (tokenFlow.peek().isOR()) {
      Lexer.Token token = tokenFlow.peek();
      tokenFlow.next();
      IRNode<Instruction> R = bitwiseXor();
      L = new Or(L, R);
      setSourceCodeInfo(L, token);
    }
    return L;
  }

  private IRNode<Instruction> bitwiseXor() throws SPLSyntaxError {
    IRNode<Instruction> L = bitwiseAnd();
    while (tokenFlow.peek().isXOR()) {
      Lexer.Token token = tokenFlow.peek();
      tokenFlow.next();
      IRNode<Instruction> R = bitwiseAnd();
      L = new Xor(L, R);
      setSourceCodeInfo(L, token);
    }
    return L;
  }

  private IRNode<Instruction> bitwiseAnd() throws SPLSyntaxError {
    IRNode<Instruction> L = shift();
    while (tokenFlow.peek().isAND()) {
      Lexer.Token token = tokenFlow.peek();
      tokenFlow.next();
      IRNode<Instruction> R = shift();
      L = new And(L, R);
      setSourceCodeInfo(L, token);
    }
    return L;
  }

  private IRNode<Instruction> shift() throws SPLSyntaxError {
    IRNode<Instruction> L = sum();
    while (tokenFlow.peek().isLSHIFT() || tokenFlow.peek().isRSHIFT() || tokenFlow.peek().isU_RSHIFT()) {
      switch (tokenFlow.peek().token) {
        case LSHIFT -> {
          Lexer.Token token = tokenFlow.peek();
          tokenFlow.next();
          L = new LShift(L, sum());
          setSourceCodeInfo(L, token);
        }
        case RSHIFT -> {
          Lexer.Token token = tokenFlow.peek();
          tokenFlow.next();
          L = new RShift(L, sum());
          setSourceCodeInfo(L, token);
        }
        case U_RSHIFT -> {
          Lexer.Token token = tokenFlow.peek();
          tokenFlow.next();
          L = new URShift(L, sum());
          setSourceCodeInfo(L, token);
        }
      }
    }
    return L;
  }

  private IRNode<Instruction> sum() throws SPLSyntaxError {
    IRNode<Instruction> L = term();
    while (tokenFlow.peek().isPLUS() || tokenFlow.peek().isMINUS()) {
      switch (tokenFlow.peek().token) {
        case PLUS -> {
          Lexer.Token token = tokenFlow.peek();
          tokenFlow.next();
          L = new Add(L, term());
          setSourceCodeInfo(L, token);
        }
        case MINUS -> {
          Lexer.Token token = tokenFlow.peek();
          tokenFlow.next();
          L = new Sub(L, term());
          setSourceCodeInfo(L, token);
        }
      }
    }
    return L;
  }

  private IRNode<Instruction> term() throws SPLSyntaxError {
    IRNode<Instruction> L = factor();
    while (tokenFlow.peek().isMUL() || tokenFlow.peek().isDIV() || tokenFlow.peek().isMOD()) {
      switch (tokenFlow.peek().token) {
        case MUL -> {
          Lexer.Token token = tokenFlow.peek();
          tokenFlow.next();
          L = new Mul(L, factor());
          setSourceCodeInfo(L, token);
        }
        case DIV -> {
          Lexer.Token token = tokenFlow.peek();
          tokenFlow.next();
          L = new Div(L, factor());
          setSourceCodeInfo(L, token);
        }
        case MOD -> {
          Lexer.Token token = tokenFlow.peek();
          tokenFlow.next();
          L = new Mod(L, factor());
          setSourceCodeInfo(L, token);
        }
        case TRUE_DIV -> {
          Lexer.Token token = tokenFlow.peek();
          tokenFlow.next();
          L = new TrueDiv(L, factor());
          setSourceCodeInfo(L, token);
        }
      }
    }
    return L;
  }

  private IRNode<Instruction> factor() throws SPLSyntaxError {
    Lexer.Token token = tokenFlow.peek();
    switch (tokenFlow.peek().token) {
      case MINUS -> {
        tokenFlow.next();
        Neg neg = new Neg(factor());
        setSourceCodeInfo(neg, token);
        return neg;
      }
      case INVERT -> {
        tokenFlow.next();
        Invert invert = new Invert(factor());
        setSourceCodeInfo(invert, token);
        return invert;
      }
      case PLUS -> {
        tokenFlow.next();
        IRNode<Instruction> factor = factor();
        setSourceCodeInfo(factor, token);
        return factor;
      }
      default -> {
        IRNode<Instruction> power = power();
        setSourceCodeInfo(power, token);
        return power;
      }
    }
  }

  private IRNode<Instruction> power() throws SPLSyntaxError {
    IRNode<Instruction> res = primary();
    if (tokenFlow.peek().isPower()) {
      Lexer.Token token = tokenFlow.peek();
      res = new Power(res, factor());
      setSourceCodeInfo(res, token);
    }
    return res;
  }

  private IRNode<Instruction> ifStatement() throws SPLSyntaxError {
    IRNode<Instruction> condition;
    IRNode<Instruction> thenBlock;
    IRNode<Instruction> elseBlock;
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IF, "require if instead of " + tokenFlow.peek().getValueAsString());
    tokenFlow.next();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LEFT_PARENTHESES, "require ( instead of " + tokenFlow.peek().getValueAsString());
    tokenFlow.next();
    condition = expression();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RIGHT_PARENTHESES, "require ) instead of " + tokenFlow.peek().getValueAsString());
    tokenFlow.next();
    thenBlock = block();
    if (tokenFlow.peek().isELSE() && tokenFlow.lookAhead().isIF()) {
      tokenFlow.next();
      elseBlock = ifStatement();
    } else if (tokenFlow.peek().isELSE()) {
      tokenFlow.next();
      elseBlock = block();
    } else {
      elseBlock = null;
    }
    return new IfStmt(condition, thenBlock, elseBlock);
  }


  private IRNode<Instruction> atom() throws SPLSyntaxError {
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

}