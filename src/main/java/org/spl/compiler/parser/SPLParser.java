package org.spl.compiler.parser;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLException;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.Scope;
import org.spl.compiler.ir.binaryop.*;
import org.spl.compiler.ir.block.ProgramBlock;
import org.spl.compiler.ir.context.DefaultASTContext;
import org.spl.compiler.ir.exp.*;
import org.spl.compiler.ir.stmt.assignstmt.*;
import org.spl.compiler.ir.stmt.controlflow.*;
import org.spl.compiler.ir.stmt.func.FuncDef;
import org.spl.compiler.ir.stmt.returnstmt.Return;
import org.spl.compiler.ir.stmt.returnstmt.ReturnNone;
import org.spl.compiler.ir.unaryop.*;
import org.spl.compiler.ir.vals.*;
import org.spl.compiler.lexer.Lexer;
import org.spl.compiler.tree.InsVisitor;
import org.spl.vm.internal.SPLCodeObjectBuilder;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.objects.*;

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
 *              | whileStmt
 *              | doWhile
 *              | forStmt
 *              | globalStmt
 *              | returnStmt
 * returnStmt   : 'return' expression?
 * globalStmt   : 'global' IDENTIFIER (',' IDENTIFIER)*
 * funcDef      : 'def' funcName '(' paramList? ')' block
 * forStmt      : 'for' '(' expression? ';' expression? ';' expression? ')' block
 * doWhile      : 'do' block 'while' '(' expression ')'
 * ifStatement  : 'if' '(' expression  ') 'block ('else if' '(' expression  ') block)* ('else' block)*
 * whileStmt    : 'while' '(' expression ')' block
 * assign       : atom '=' expression
 *              | atom '+=' expression
 *              | atom '-=' expression
 *              | atom '*=' expression
 *              | atom '/=' expression
 *              | atom '//=' expression
 *              | atom '**=' expression
 *              | atom '%=' expression
 *              | atom '<<=' expression
 *              | atom '>>=' expression
 *              | atom '>>>=' expression
 *              | atom '&=' expression
 *              | atom '^=' expression
 *              | atom '|=' expression
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
 * primary      : atom
 * atom         : IDENTIFIER (('.' NAME) | ('(' paramList? ')'))*
 *              | 'true'
 *              | 'false'
 *              | 'none'
 *              | string
 *              | number
 *              | 'break'
 *              | 'continue'
 *              | '(' expression ')'
 *              | 'def' '(' paramList? ')' block'
 *              | 'def' '(' paramList? ')' '->' expression
 *              | '{' ((IDENTIFIER | STRING ) ':' expression ',')* (((IDENTIFIER | STRING ) ':' expression))?'}'
 *              | '{' (expression ',')+ expression'}'
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
      if (node instanceof GlobalNop)
        continue;
      block.addIRNode(node);
      if (node instanceof AbstractIR<Instruction> abIR && !abIR.isStatement()) {
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
      Lexer.Token token = tokenFlow.peek();
      if (tokenFlow.peek().isIF()) {
        return ifStatement();
      } else if (tokenFlow.peek().isReturn()) {
        return returnStatement();
      } else if (tokenFlow.peek().isGlobal()) {
        return globalStatement();
      } else if (tokenFlow.peek().isDef()) {
        return functionDefinition();
      } else if (tokenFlow.peek().isWHILE()) {
        return whileStatement();
      } else if (tokenFlow.peek().isDO()) {
        return doWhileStatement();
      } else if (tokenFlow.peek().isFor()) {
        return forStatement();
      } else if (token.isBreak()) {
        tokenFlow.next();
        Break brk = new Break();
        setSourceCodeInfo(brk, token);
        return brk;
      } else if (token.isSemiColon()) {
        // tokenFlow.next() is prohibited here causing for-statement will consume this token
        NOP nop = new NOP();
        setSourceCodeInfo(nop, token);
        return nop;
      } else if (token.isContinue()) {
        tokenFlow.next();
        Continue cont = new Continue();
        setSourceCodeInfo(cont, token);
        return cont;
      } else if (tokenFlow.peek().isIDENTIFIER()) {
        int cursor = tokenFlow.getCursor();
        atom();
        if (tokenFlow.peek().isASSIGN()) {
          tokenFlow.setCursor(cursor);
          return assignStatement();
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

  private IRNode<Instruction> assignStatement() throws SPLSyntaxError {
    IRNode<Instruction> exp = atom();
    if (exp instanceof Variable lhs) {
      if (lhs.scope() == Scope.OTHERS) {
        lhs.setScope(Scope.LOCAL);
      }
      Lexer.Token sign = tokenFlow.peek();
      tokenFlow.next();
      switch (sign.token) {
        case ASSIGN -> {
          context.addSymbol(lhs.getName());
          context.addVarName(lhs.getName());
          IRNode<Instruction> rhs = expression();
          AssignStmt assignStmt = new AssignStmt(lhs, rhs);
          setSourceCodeInfo(assignStmt, sign);
          return assignStmt;
        }
        case ASSIGN_ADD -> {
          IRNode<Instruction> rhs = expression();
          AddAssignStmt addAssignStmt = new AddAssignStmt(lhs, rhs);
          setSourceCodeInfo(addAssignStmt, sign);
          return addAssignStmt;
        }
        case ASSIGN_TRUE_DIV -> {
          IRNode<Instruction> rhs = expression();
          TrueDivAssignStmt trueDivAssignStmt = new TrueDivAssignStmt(lhs, rhs);
          setSourceCodeInfo(trueDivAssignStmt, sign);
          return trueDivAssignStmt;
        }
        case ASSIGN_SUB -> {
          IRNode<Instruction> rhs = expression();
          SubAssignStmt subAssignStmt = new SubAssignStmt(lhs, rhs);
          setSourceCodeInfo(subAssignStmt, sign);
          return subAssignStmt;
        }
        case ASSIGN_MUL -> {
          IRNode<Instruction> rhs = expression();
          MulAssignStmt mulAssignStmt = new MulAssignStmt(lhs, rhs);
          setSourceCodeInfo(mulAssignStmt, sign);
          return mulAssignStmt;
        }
        case ASSIGN_DIV -> {
          IRNode<Instruction> rhs = expression();
          DivAssignStmt divAssignStmt = new DivAssignStmt(lhs, rhs);
          setSourceCodeInfo(divAssignStmt, sign);
          return divAssignStmt;
        }
        case ASSIGN_POWER -> {
          IRNode<Instruction> rhs = expression();
          PowerAssignStmt powerAssignStmt = new PowerAssignStmt(lhs, rhs);
          setSourceCodeInfo(powerAssignStmt, sign);
          return powerAssignStmt;
        }
        case ASSIGN_MOD -> {
          IRNode<Instruction> rhs = expression();
          ModAssignStmt modAssignStmt = new ModAssignStmt(lhs, rhs);
          setSourceCodeInfo(modAssignStmt, sign);
          return modAssignStmt;
        }
        case ASSIGN_OR -> {
          IRNode<Instruction> rhs = expression();
          OrAssignStmt orAssignStmt = new OrAssignStmt(lhs, rhs);
          setSourceCodeInfo(orAssignStmt, sign);
          return orAssignStmt;
        }
        case ASSIGN_AND -> {
          IRNode<Instruction> rhs = expression();
          AndAssignStmt andAssignStmt = new AndAssignStmt(lhs, rhs);
          setSourceCodeInfo(andAssignStmt, sign);
          return andAssignStmt;
        }
        case ASSIGN_XOR -> {
          IRNode<Instruction> rhs = expression();
          XorAssignStmt xorAssignStmt = new XorAssignStmt(lhs, rhs);
          setSourceCodeInfo(xorAssignStmt, sign);
          return xorAssignStmt;
        }
        case ASSIGN_LSHIFT -> {
          IRNode<Instruction> rhs = expression();
          LshiftAssignStmt lshiftAssignStmt = new LshiftAssignStmt(lhs, rhs);
          setSourceCodeInfo(lshiftAssignStmt, sign);
          return lshiftAssignStmt;
        }
        case ASSIGN_RSHIFT -> {
          IRNode<Instruction> rhs = expression();
          RshiftAssignStmt rshiftAssignStmt = new RshiftAssignStmt(lhs, rhs);
          setSourceCodeInfo(rshiftAssignStmt, sign);
          return rshiftAssignStmt;
        }
        case ASSIGN_U_RSHIFT -> {
          IRNode<Instruction> rhs = expression();
          ULshiftAssignStmt uLshiftAssignStmt = new ULshiftAssignStmt(lhs, rhs);
          setSourceCodeInfo(uLshiftAssignStmt, sign);
          return uLshiftAssignStmt;
        }
      }
    } else if (exp instanceof LoadAttr lhs) {
      Lexer.Token sign = tokenFlow.peek();
      tokenFlow.next();
      switch (sign.token) {
        case ASSIGN -> {
          IRNode<Instruction> rhs = expression();
          // codeGen order: rhs lhs
          StoreAttr storeAttr = new StoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName());
          setSourceCodeInfo(storeAttr, sign);
          return storeAttr;
        }
        case ASSIGN_ADD -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_ADD);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_SUB -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_SUB);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_MUL -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_MUL);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_DIV -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_DIV);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_TRUE_DIV -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_TRUE_DIV);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_POWER -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_POWER);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_MOD -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_MOD);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_OR -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_OR);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_AND -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_AND);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_XOR -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_XOR);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_LSHIFT -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_LSHIFT);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_RSHIFT -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_RSHIFT);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_U_RSHIFT -> {
          IRNode<Instruction> rhs = expression();
          IRNode<Instruction> o = lhs.getLhs();
          Dup newLhs = new Dup(o);
          lhs.setLhs(newLhs);
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_U_RSHIFT);
          setSourceCodeInfo(store, sign);
          return store;
        }
      }
    } else {
      throwSyntaxError("Illegal statement, expected assignment or expression", tokenFlow.peek());
    }
    return null;
  }

  private IRNode<Instruction> returnStatement() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RETURN, "require 'return' instead of \"" + tokenFlow.peek().getValueAsString());
    Lexer.Token token = tokenFlow.peek();
    tokenFlow.next();
    IRNode<Instruction> expression = expression();
    Return ret = new Return(expression);
    setSourceCodeInfo(ret, token);
    return ret;
  }


  public IRNode<Instruction> globalStatement() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.GLOBAL, "require 'global' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    requireIdentifier();
    context.addGlobal(tokenFlow.peek().getIdentifier());
    context.addSymbol(tokenFlow.peek().getIdentifier());
    context.addVarName(tokenFlow.peek().getIdentifier());
    tokenFlow.next();
    while (tokenFlow.peek().isComma()) {
      tokenFlow.next();
      requireIdentifier();
      context.addGlobal(tokenFlow.peek().getIdentifier());
      context.addSymbol(tokenFlow.peek().getIdentifier());
      context.addVarName(tokenFlow.peek().getIdentifier());
      tokenFlow.next();
    }
    return new GlobalNop();
  }

  private void requireIdentifier() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER, "require identifier instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
  }

  private IRNode<Instruction> functionDefinition() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.DEF, "require 'def' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    Lexer.Token token = tokenFlow.peek();
    tokenFlow.next();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER, "require identifier instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    String funcName = tokenFlow.peek().getValueAsString();
    int idxInVar = context.addVarName(funcName);
    context.addSymbol(funcName);
    tokenFlow.next();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LEFT_PARENTHESES, "require '(' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    var funcContext = new DefaultASTContext<>(filename);
    funcContext.setFirstLineNo(token.getLineNo());
    List<String> parameters = new ArrayList<>();
    List<IRNode<Instruction>> defaultParams = new ArrayList<>();
    while (tokenFlow.peek().isIDENTIFIER()) {
      parameters.add(tokenFlow.peek().getValueAsString());
      context.addSymbol(tokenFlow.peek().getValueAsString());
      context.addVarName(tokenFlow.peek().getValueAsString());
      tokenFlow.next();
      if (tokenFlow.peek().isPureAssign()) {
        tokenFlow.back();
        while (tokenFlow.peek().isIDENTIFIER()) {
          tokenFlow.next();
          tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.ASSIGN, "require '=' instead of \"" + tokenFlow.peek().getValueAsString());
          tokenFlow.next();
          defaultParams.add(expression());
          if (tokenFlow.peek().isComma()) {
            tokenFlow.next();
          }
        }
        break;
      }
      if (tokenFlow.peek().isComma()) {
        tokenFlow.next();
        tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER, "require identifier instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
      } else {
        break;
      }
    }
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RIGHT_PARENTHESES, "require ')' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LBRACE, "require '{' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    DefaultASTContext<Instruction> oldState = context;
    parameters.forEach(funcContext::addSymbol);
    parameters.forEach(funcContext::addVarName);
    context = funcContext;
//    funcContext.addVarName(funcName);
//    funcContext.addSymbol(funcName);
    IRNode<Instruction> block = block();
    assert block instanceof ProgramBlock;
    var pb = (ProgramBlock) block;
    IRNode<Instruction> last = pb.getLast();
    if (!(last instanceof Return || last instanceof ReturnNone)) {
      ReturnNone returnNone = new ReturnNone();
      setSourceCodeInfo(returnNone, token);
      pb.addIRNode(returnNone);
    }
    // restore old context
    context = oldState;
    funcContext.generateByteCodes(block);
    SPLCodeObject code = SPLCodeObjectBuilder.build(funcContext);
    SPLFuncObject func = new SPLFuncObject(parameters, funcName, code);
    context.addConstantObject(func);
//    InsVisitor insVisitor = new InsVisitor(funcContext.getVarnames(), funcContext.getConstantMap());
//    funcContext.getInstructions().forEach(insVisitor::visit);
//    System.out.println(insVisitor);
    int idxInConstants = context.getConstantObjectIndex(func);
    FuncDef funcDef = new FuncDef(funcName, idxInConstants, idxInVar, defaultParams);
    setSourceCodeInfo(funcDef, token);
    return funcDef;
  }

  private IRNode<Instruction> forStatement() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.FOR, "require 'for' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    Lexer.Token token = tokenFlow.peek();
    tokenFlow.next();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LEFT_PARENTHESES, "require '(' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    IRNode<Instruction> initializer = statement();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.SEMICOLON, "require ';' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    IRNode<Instruction> condition = statement();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.SEMICOLON, "require ';' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    IRNode<Instruction> increment;
    if (tokenFlow.peek().isRIGHT_PARENTHESES()) {
      token = tokenFlow.peek();
      tokenFlow.next();
      increment = new NOP();
      setSourceCodeInfo(increment, token);
    } else {
      increment = statement();
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RIGHT_PARENTHESES, "require ')' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
      tokenFlow.next();
    }
    IRNode<Instruction> block = block();
    ForStmt forStmt = new ForStmt(initializer, condition, increment, block);
    setSourceCodeInfo(forStmt, token);
    return forStmt;
  }

  private IRNode<Instruction> doWhileStatement() throws SPLSyntaxError {
    Lexer.Token token = tokenFlow.peek();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.DO, "require 'do' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    IRNode<Instruction> block = block();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.WHILE, "require 'while' instead of "
        + tokenFlow.peek().getValueAsString());
    tokenFlow.next();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LEFT_PARENTHESES, "require '(' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    IRNode<Instruction> condition = expression();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RIGHT_PARENTHESES, "require ')' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    DoWhile doWhile = new DoWhile(condition, block);
    setSourceCodeInfo(doWhile, token);
    return doWhile;
  }

  private IRNode<Instruction> whileStatement() throws SPLSyntaxError {
    Lexer.Token token = tokenFlow.peek();
    tokenFlow.next();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LEFT_PARENTHESES, "require ( instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    IRNode<Instruction> condition = expression();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RIGHT_PARENTHESES, "require ) instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    IRNode<Instruction> block = block();
    WhileStmt whileStmt = new WhileStmt(condition, block);
    setSourceCodeInfo(whileStmt, token);
    return whileStmt;
  }

  private IRNode<Instruction> block() throws SPLSyntaxError {
    if (tokenFlow.peek().isLBRACE()) {
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LBRACE, "require { instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
      tokenFlow.next();
      iterateToEffectiveToken();
      ProgramBlock codeBlock = new ProgramBlock();
      setSourceCodeInfo(codeBlock, tokenFlow.peek());
      while (!tokenFlow.peek().isRBRACE()) {
        IRNode<Instruction> stmt = statement();
        if (stmt instanceof GlobalNop) {
          iterateToEffectiveToken();
          continue;
        }
        codeBlock.addIRNode(stmt);
        if (stmt instanceof AbstractIR<Instruction> abIR && !abIR.isStatement()) {
          Lexer.Token token = tokenFlow.peek();
          Pop pop = new Pop();
          setSourceCodeInfo(pop, token);
          codeBlock.addIRNode(pop);
        }
        iterateToEffectiveToken();
      }
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RBRACE, "require } instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
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
      tokenFlow.next();
      res = new Power(res, factor());
      setSourceCodeInfo(res, token);
    }
    return res;
  }

  private IRNode<Instruction> ifStatement() throws SPLSyntaxError {
    IRNode<Instruction> condition;
    IRNode<Instruction> thenBlock;
    IRNode<Instruction> elseBlock;
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IF, "require if instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LEFT_PARENTHESES, "require ( instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    condition = expression();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RIGHT_PARENTHESES, "require ) instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    iterateToEffectiveToken();
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
    if (token.isNone()) {
      tokenFlow.next();
      context.addConstantObject(SPLNoneObject.getInstance());
      Literal literal = new Literal(context.getConstantsSize());
      setSourceCodeInfo(literal, token);
      return literal;
    } else if (token.isBreak()) {
      tokenFlow.next();
      Break brk = new Break();
      setSourceCodeInfo(brk, token);
      return brk;
    } else if (token.isSemiColon()) {
      // tokenFlow.next() is prohibited here causing for-statement will consume this token
      NOP nop = new NOP();
      setSourceCodeInfo(nop, token);
      return nop;
    } else if (token.isContinue()) {
      tokenFlow.next();
      Continue cont = new Continue();
      setSourceCodeInfo(cont, token);
      return cont;
    } else if (token.isINT()) {
      tokenFlow.next();
      SPLLongObject o = SPLCodeObject.getSPL(token.getInt());
      context.addConstantObject(o);
      int idx = context.getConstantObjectIndex(o);
      IntLiteral intLiteral = new IntLiteral(token.getInt(), idx);
      setSourceCodeInfo(intLiteral, token);
      return intLiteral;
    } else if (token.isFLOAT()) {
      tokenFlow.next();
      SPLFloatObject o = SPLCodeObject.getSPL(token.getFloat());
      context.addConstantObject(o);
      int idx = context.getConstantObjectIndex(o);
      FloatLiteral floatLiteral = new FloatLiteral(token.getFloat(), idx);
      setSourceCodeInfo(floatLiteral, token);
      return floatLiteral;
    } else if (token.isSTRING()) {
      tokenFlow.next();
      SPLStringObject o = SPLCodeObject.getSPL(token.getValueAsString());
      context.addConstantObject(o);
      int idx = context.getConstantObjectIndex(o);
      StringLiteral stringLiteral = new StringLiteral(token.getValueAsString(), idx);
      setSourceCodeInfo(stringLiteral, token);
      return stringLiteral;
    } else if (token.isFALSE()) {
      tokenFlow.next();
      SPLBoolObject o = SPLBoolObject.getFalse();
      context.addConstantObject(o);
      int idx = context.getConstantObjectIndex(o);
      BoolLiteral boolLiteral = new BoolLiteral(idx);
      setSourceCodeInfo(boolLiteral, token);
      return boolLiteral;
    } else if (token.isDef()) {
      return anonymousFunction();
    } else if (token.isTRUE()) {
      tokenFlow.next();
      SPLBoolObject o = SPLBoolObject.getTrue();
      context.addConstantObject(o);
      int idx = context.getConstantObjectIndex(o);
      BoolLiteral boolLiteral = new BoolLiteral(idx);
      setSourceCodeInfo(boolLiteral, token);
      return boolLiteral;
    } else if (token.isIDENTIFIER()) {
      tokenFlow.next();
      context.addVarName(token.getIdentifier());
      IRNode<Instruction> ret;
      Scope scope;
      if (context.isGlobal(token.getIdentifier())) {
        scope = Scope.GLOBAL;
      } else if (context.containSymbol(token.getIdentifier())) {
        scope = Scope.LOCAL;
      } else {
        scope = Scope.OTHERS;
      }
      ret = new Variable(scope, token.getIdentifier());
      setSourceCodeInfo(ret, token);
      while (tokenFlow.peek().isDOT() || tokenFlow.peek().isLEFT_PARENTHESES()) {
        if (tokenFlow.peek().isDOT()) {
          tokenFlow.next();
          tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER,
              "Expected identifier after '.'");
          String name = tokenFlow.peek().getIdentifier();
          int idx = context.addVarName(name);
          context.addSymbol(name);
          if (tokenFlow.lookAhead().isLEFT_PARENTHESES()) {
            ret = new LoadMethod(ret, idx, name);
          } else {
            ret = new LoadAttr(ret, idx, name);
          }
          tokenFlow.next();
        } else {
          List<IRNode<Instruction>> args = new ArrayList<>();
          tokenFlow.next();
          extractArgs(args);
          FuncCallExp funcCallExp = new FuncCallExp(ret, args);
          setSourceCodeInfo(funcCallExp, token);
          ret = funcCallExp;
        }
      }
      return ret;
    } else if (token.isLPAREN()) {
      tokenFlow.next();
      IRNode<Instruction> expression = expression();
      token = tokenFlow.peek();
      tokenFlow.next();
      if (token.isRPAREN()) {
        return expression;
      } else {
        throwSyntaxError("Expected ')' instead of \"" + token.getValueAsString() + "\"", token);
      }
    }
    throwSyntaxError("Expected an expression instead of \"" + token.getValueAsString() + "\"", token);
    return null;
  }

  private IRNode<Instruction> anonymousFunction() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.DEF,
        "Expected 'def' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LEFT_PARENTHESES, "Expected '(' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    var params = new ArrayList<String>();
    DefaultASTContext<Instruction> auxContex = new DefaultASTContext<>(filename);
    var oldContex = context;
    context = auxContex;
    while (!tokenFlow.peek().isRIGHT_PARENTHESES()) {
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER, "Expected an identifier instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
      params.add(tokenFlow.peek().getIdentifier());
      context.addVarName(tokenFlow.peek().getIdentifier());
      context.addSymbol(tokenFlow.peek().getIdentifier());
      tokenFlow.next();
      if (tokenFlow.peek().isComma()) {
        tokenFlow.next();
      } else {
        tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RIGHT_PARENTHESES, "Expected ')' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
      }
    }
    ProgramBlock block;
    tokenFlow.next();
    if (tokenFlow.peek().isArrow()) {
      tokenFlow.next();
      block = new ProgramBlock();
      Return ret = new Return(expression());
      block.addIRNode(ret);
    } else {
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LBRACE, "Expected '{' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
      block = (ProgramBlock) block();
    }
    IRNode<Instruction> last = block.getLast();
    if (!(last instanceof Return || last instanceof ReturnNone)) {
      ReturnNone returnNone = new ReturnNone();
      setSourceCodeInfo(returnNone, tokenFlow.lookBack());
      block.addIRNode(returnNone);
    }
    context = oldContex;
    auxContex.generateByteCodes(block);
    SPLCodeObject codeObject = SPLCodeObjectBuilder.build(auxContex);
    SPLFuncObject func = new SPLFuncObject(params, codeObject);
    context.addConstantObject(func);
    int idx = context.getConstantObjectIndex(func);
    FuncDef funcDef = new FuncDef(idx, func.getName());
    setSourceCodeInfo(funcDef, tokenFlow.lookBack());
    InsVisitor insVisitor = new InsVisitor(auxContex.getVarnames(), auxContex.getConstantMap());
    auxContex.getInstructions().forEach(insVisitor::visit);
    System.out.println(insVisitor);
    System.out.println(codeObject);
    return funcDef;
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
              "Expected an assign operator instead of \"" + token.getValueAsString() + "\""
          ));
    }
    tokenFlow.next();
    switch (sign.token) {
      case ASSIGN -> {
        context.addSymbol(token.getIdentifier());
        context.addVarName(token.getIdentifier());
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

  private IRNode<Instruction> methodCall() throws SPLSyntaxError {
    Lexer.Token token = tokenFlow.peek();
    tokenFlow.next();
    if (token.isIDENTIFIER()) {
      throwSyntaxError("Expected an method name instead of \"" + token.getValueAsString() + "\"", token);
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
    tokenAssertion(token, Lexer.TOKEN_TYPE.LEFT_PARENTHESES, "Expected '(' instead of \"" + token.getValueAsString() + "\"");
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
                "Expected ',' or ')' instead of \"" + tokenFlow.peek().getValueAsString() + "\"")
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
