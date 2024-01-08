package org.spl.compiler.parser;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLException;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.Op;
import org.spl.compiler.ir.Scope;
import org.spl.compiler.ir.binaryop.*;
import org.spl.compiler.ir.block.Program;
import org.spl.compiler.ir.block.ProgramBlock;
import org.spl.compiler.ir.context.DefaultASTContext;
import org.spl.compiler.ir.exp.*;
import org.spl.compiler.ir.stmt.ClassDefinition;
import org.spl.compiler.ir.stmt.Decorator;
import org.spl.compiler.ir.stmt.ImportStmt;
import org.spl.compiler.ir.stmt.YieldStmt;
import org.spl.compiler.ir.stmt.assignstmt.*;
import org.spl.compiler.ir.stmt.controlflow.*;
import org.spl.compiler.ir.stmt.func.FuncDef;
import org.spl.compiler.ir.stmt.returnstmt.Return;
import org.spl.compiler.ir.stmt.returnstmt.ReturnNone;
import org.spl.compiler.ir.unaryop.*;
import org.spl.compiler.ir.vals.*;
import org.spl.compiler.lexer.Lexer;
import org.spl.vm.internal.SPLCodeObjectBuilder;
import org.spl.vm.internal.objs.SPLClassDefinition;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.objects.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 *              | tryStmt
 *              | decoratorStmt
 *              | classDef
 *              | yield
 *              | importStmt
 * importStmt   : 'import' IDENTIFIER
 * yield        : 'yield'
 * classDef     : 'class' IDENTIFIER ('(' IDENTIFIER ')') ?'{' classBody '}
 * decorator    : '@' expression funcDef
 * tryStmt      : 'try' block ('catch' '(' IDENTIFIER IDENTIFIER ')' block) * ('finally' block)*
 * returnStmt   : 'return' expression?
 * globalStmt   : 'global' IDENTIFIER (',' IDENTIFIER)*
 * funcDef      : 'def' funcName '(' paramList? ')' block
 * forStmt      : 'for' '(' expression? ';' expression? ';' expression? ')' block
 *              | 'for' IDENTIFIER 'in' expression block
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
 * atom         : IDENTIFIER (('.' NAME) | ('(' paramList? ')') | '[' expression ']')*
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
 *              | '[' (expression ',')* (expression)? ']'
 *
 */
public class SPLParser extends AbstractSyntaxParser {
  private IRNode<Instruction> root;

  public SPLParser(String filename) throws IOException, SPLSyntaxError {
    super(filename);
  }

  public SPLParser(String filename, String content) throws IOException, SPLSyntaxError {
    super(filename, content);
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
    Program block = new Program();
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
        context.setFirstLineNo(top.getLineNo());
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
      } else if (tokenFlow.peek().isAt()) {
        return atStatement();
      } else if (tokenFlow.peek().isClass()) {
        return classDefStmt();
      } else if (tokenFlow.peek().isImport()) {
        return importStmt();
      } else if (token.isYield()) {
        return yieldStatement();
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
      } else if (tokenFlow.peek().isTry()) {
        return tryStatement();
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
      } else {
        return expression();
      }
    } catch (IndexOutOfBoundsException e) {
      throwSyntaxError("Illegal statement, expected assignment or expression", tokenFlow.peek());
    }
    throwSyntaxError("Illegal statement, expected assignment or expression", tokenFlow.peek());
    return null;
  }

  private IRNode<Instruction> importStmt() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IMPORT, "Expected 'import' instead of " + tokenFlow.peek().getValueAsString());
    Lexer.Token token = tokenFlow.peek();
    tokenFlow.next();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER, "Expected package name instead of " + tokenFlow.peek().getValueAsString());
    String packageName = tokenFlow.peek().getValueAsString();
    tokenFlow.next();
    context.addVarName(packageName);
    context.addSymbol(packageName);
    ImportStmt importStmt = new ImportStmt(context.getVarNameIndex(packageName), packageName);
    setSourceCodeInfo(importStmt, token);
    return importStmt;
  }

  private IRNode<Instruction> yieldStatement() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.YIELD, "Expected 'yield' instead of " + tokenFlow.peek().getValueAsString());
    Lexer.Token token = tokenFlow.peek();
    tokenFlow.next();
    YieldStmt yieldStmt = new YieldStmt();
    setSourceCodeInfo(yieldStmt, token);
    return yieldStmt;
  }

  private IRNode<Instruction> classDefStmt() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.CLASS, "Expected 'class' instead of " + tokenFlow.peek().getValueAsString());
    Lexer.Token token = tokenFlow.peek();
    tokenFlow.next();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER, "Expected class name instead of " + tokenFlow.peek().getValueAsString());
    String className = tokenFlow.peek().getIdentifier();
    String superClassName = null;
    tokenFlow.next();
    if (tokenFlow.peek().isLEFT_PARENTHESES()) {
      tokenFlow.next();
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER, "Expected super class name instead of " + tokenFlow.peek().getValueAsString());
      superClassName = tokenFlow.peek().getIdentifier();
      tokenFlow.next();
      if (tokenFlow.peek().isComma()) {
        throwSyntaxError("Only single inheritance is allowed in SPL", tokenFlow.peek());
      }
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RIGHT_PARENTHESES, "Expected ')' instead of " + tokenFlow.peek().getValueAsString());
      tokenFlow.next();
    }
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LBRACE, "Expected '{' instead of " + tokenFlow.peek().getValueAsString());
    var classContext = new DefaultASTContext<>(filename, getSourceCode());
    classContext.setFirstLineNo(tokenFlow.peek().getLineNo());
    DefaultASTContext<Instruction> old = context;
    context = classContext;
    IRNode<Instruction> block = block();
    classContext.generateByteCodes(block);
    SPLCodeObject classBody = SPLCodeObjectBuilder.build(classContext);
    SPLClassDefinition classDef = new SPLClassDefinition(classBody, className);
    context = old;
    context.addConstantObject(classDef);
    int cd = context.getConstantObjectIndex(classDef);
    context.addVarName(className);
    context.addSymbol(className);
    int vd = context.getVarNameIndex(className);
    int sd = -1;
    if (superClassName != null) {
      context.addVarName(superClassName);
      context.addSymbol(superClassName);
      sd = context.getVarNameIndex(superClassName);
    }
    ClassDefinition classDefinition = new ClassDefinition(cd, vd, sd, block, className, superClassName);
    setSourceCodeInfo(classDefinition, token);
    return classDefinition;
  }

  private IRNode<Instruction> atStatement() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.AT, "Expected  '@' instead of " + tokenFlow.peek().getValueAsString());
    Lexer.Token token = tokenFlow.peek();
    tokenFlow.next();
    IRNode<Instruction> expr = expression();
    iterateToEffectiveToken();
    IRNode<Instruction> func = functionDefinition();
    Decorator decorator = new Decorator((FuncDef) func, expr);
    setSourceCodeInfo(decorator, token);
    return decorator;
  }

  private IRNode<Instruction> tryStatement() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.TRY, "Expected  'try' instead of " + tokenFlow.peek().getValueAsString());
    Lexer.Token token = tokenFlow.peek();
    Lexer.Token tryToken = token;
    tokenFlow.next();
    IRNode<Instruction> tryBlock = block();
    List<IRNode<Instruction>> exceptBlocks = new ArrayList<>();
    while (tokenFlow.peek().isCatch()) {
      Lexer.Token exceptToken = tokenFlow.peek();
      tokenFlow.next();
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LEFT_PARENTHESES, "Expected '(' instead of " + tokenFlow.peek().getValueAsString());
      tokenFlow.next();
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER, "Expected an identifier instead of " + tokenFlow.peek().getValueAsString());
      token = tokenFlow.peek();
      tokenFlow.next();
      String exceptName = token.getIdentifier();
      context.addSymbol(exceptName);
      int exceptIdx = context.addVarName(exceptName);
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER, "Expected an identifier instead of " + tokenFlow.peek().getValueAsString());
      token = tokenFlow.peek();
      tokenFlow.next();
      String storeName = token.getIdentifier();
      context.addSymbol(exceptName);
      int storeIdx = context.addVarName(storeName);
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RIGHT_PARENTHESES, "Expected ')' instead of " + tokenFlow.peek().getValueAsString());
      tokenFlow.next();
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LBRACE, "Expected '{' instead of " + tokenFlow.peek().getValueAsString());
      IRNode<Instruction> block = block();
      ExceptBlock exceptBlock = new ExceptBlock(exceptName, exceptIdx, storeName, storeIdx, block);
      setSourceCodeInfo(exceptBlock, exceptToken);
      exceptBlocks.add(exceptBlock);
    }
    IRNode<Instruction> finallyBlock = null;
    if (tokenFlow.peek().isFinally()) {
      tokenFlow.next();
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LBRACE, "Expected '{' instead of " + tokenFlow.peek().getValueAsString());
      finallyBlock = block();
    }
    TryStmt tryStmt = new TryStmt(tryBlock, exceptBlocks, finallyBlock);
    setSourceCodeInfo(tryStmt, tryToken);
    return tryStmt;
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
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_ADD);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_SUB -> {
          IRNode<Instruction> rhs = expression();
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_SUB);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_MUL -> {
          IRNode<Instruction> rhs = expression();
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_MUL);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_DIV -> {
          IRNode<Instruction> rhs = expression();
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_DIV);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_TRUE_DIV -> {
          IRNode<Instruction> rhs = expression();
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_TRUE_DIV);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_POWER -> {
          IRNode<Instruction> rhs = expression();
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_POWER);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_MOD -> {
          IRNode<Instruction> rhs = expression();
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_MOD);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_OR -> {
          IRNode<Instruction> rhs = expression();
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_OR);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_AND -> {
          IRNode<Instruction> rhs = expression();
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_AND);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_XOR -> {
          IRNode<Instruction> rhs = expression();
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_XOR);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_LSHIFT -> {
          IRNode<Instruction> rhs = expression();
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_LSHIFT);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_RSHIFT -> {
          IRNode<Instruction> rhs = expression();
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_RSHIFT);
          setSourceCodeInfo(store, sign);
          return store;
        }
        case ASSIGN_U_RSHIFT -> {
          IRNode<Instruction> rhs = expression();
          InplaceStoreAttr store = new InplaceStoreAttr(lhs.getLhs(), rhs, lhs.getAttrIndex(), lhs.getName(), Op.ASSIGN_U_RSHIFT);
          setSourceCodeInfo(store, sign);
          return store;
        }
      }
    } else if (exp instanceof ArrayStyle lhs) {
      Lexer.Token sign = tokenFlow.peek();
      tokenFlow.next();
      IRNode<Instruction> rhs = expression();
      ArrayStyleStore storeAttr = new ArrayStyleStore(lhs.getLhs(), lhs.getSub(), rhs, sign.token);
      setSourceCodeInfo(storeAttr, sign);
      return storeAttr;
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
    var funcContext = new DefaultASTContext<>(filename, getSourceCode());
    funcContext.setInFunc(true);
    funcContext.setPrev(context);
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
    int idxInConstants = context.getConstantObjectIndex(func);
    Map<String, Integer> closureMap = funcContext.getClosureMap();
    String[] vars = new String[closureMap.size()];
    ArrayList<IRNode<Instruction>> closures = new ArrayList<>();
    closureMap.forEach((k, v) -> vars[v] = k);
    code.setClosures(new SPLObject[vars.length]);
    Variable variable;
    for (String var : vars) {
      if (context.isGlobal(var)) {
        variable = new Variable(Scope.GLOBAL, var, context.getSymbolIndex(var));
      } else if (context.containSymbol(var)) {
        variable = new Variable(Scope.LOCAL, var, context.getSymbolIndex(var));
      } else if (context.loadClosureVar(var) != -1) {
        variable = new Variable(Scope.CLOSURE, var, context.loadClosureVar(var));
      } else {
        variable = new Variable(Scope.OTHERS, var, context.getVarNameIndex(var));
      }
      setSourceCodeInfo(variable, token);
      closures.add(variable);
    }
    FuncDef funcDef = new FuncDef(closures, block, funcName, parameters, idxInConstants, idxInVar, defaultParams);
    setSourceCodeInfo(funcDef, token);
    return funcDef;
  }

  private IRNode<Instruction> forStatement() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.FOR, "require 'for' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    Lexer.Token token = tokenFlow.peek();
    tokenFlow.next();
    if (tokenFlow.peek().isLEFT_PARENTHESES()) {
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
    } else {
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER, "require identifier instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
      String i = tokenFlow.peek().getValueAsString();
      int idx = context.addVarName(i);
      context.addSymbol(i);
      tokenFlow.next();
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IN, "require 'in' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
      tokenFlow.next();
      IRNode<Instruction> expr = expression();
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LBRACE, "require '{' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
      IRNode<Instruction> block = block();
      ConciseForStmt forStmt = new ConciseForStmt(expr, i, idx, block);
      setSourceCodeInfo(forStmt, token);
      return forStmt;
    }
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
    while (tokenFlow.peek().isMUL() ||
        tokenFlow.peek().isDIV() ||
        tokenFlow.peek().isMOD() ||
        tokenFlow.peek().isTrueDiv()) {
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
    } else if (token.isLBRACE()) {
      return dictOrSet();
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
    } else if (token.isLBRACKET()) {
      return list();
    } else if (token.isIDENTIFIER()) {
      int idx;
      tokenFlow.next();
      String identifier = token.getIdentifier();
      context.addVarName(identifier);
      IRNode<Instruction> ret;
      Scope scope;
      idx = context.getVarNameIndex(identifier);
      if (context.isGlobal(identifier)) {
        scope = Scope.GLOBAL;
      } else if (context.containSymbol(identifier)) {
        scope = Scope.LOCAL;
      } else if (context.loadClosureVar(identifier) != -1) {
        scope = Scope.CLOSURE;
        idx = context.loadClosureVar(identifier);
      } else {
        scope = Scope.OTHERS;
      }
      ret = new Variable(scope, identifier, idx);
      setSourceCodeInfo(ret, token);
      while (tokenFlow.peek().isDOT() || tokenFlow.peek().isLEFT_PARENTHESES() || tokenFlow.peek().isLBRACKET()) {
        if (tokenFlow.peek().isDOT()) {
          Lexer.Token t = tokenFlow.peek();
          tokenFlow.next();
          tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER,
              "Expected identifier after '.'");
          String name = tokenFlow.peek().getIdentifier();
          idx = context.addVarName(name);
          context.addSymbol(name);
          if (tokenFlow.lookAhead().isLEFT_PARENTHESES()) {
            ret = new LoadMethod(ret, idx, name);
          } else {
            ret = new LoadAttr(ret, idx, name);
          }
          setSourceCodeInfo(ret, t);
          tokenFlow.next();
        } else if (tokenFlow.peek().isLBRACKET()) {
          Lexer.Token t = tokenFlow.peek();
          tokenFlow.next();
          IRNode<Instruction> sub = expression();
          tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RBRACKET, "Expected ']' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
          tokenFlow.next();
          ret = new ArrayStyle(ret, sub);
          setSourceCodeInfo(ret, t);
        } else {
          Lexer.Token t = tokenFlow.peek();
          List<IRNode<Instruction>> args = new ArrayList<>();
          tokenFlow.next();
          extractArgs(args);
          FuncCallExp funcCallExp = new FuncCallExp(ret, args);
          setSourceCodeInfo(funcCallExp, token);
          ret = funcCallExp;
          setSourceCodeInfo(ret, t);
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

  private IRNode<Instruction> list() throws SPLSyntaxError {
    ArrayList<IRNode<Instruction>> data = new ArrayList<>();
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LBRACKET, "Expected '[' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    while (!tokenFlow.peek().isRBRACKET()) {
      iterateToEffectiveToken();
      data.add(expression());
      iterateToEffectiveToken();
      if (tokenFlow.peek().isComma()) {
        tokenFlow.next();
      } else if (tokenFlow.peek().isRBRACKET()) {
        break;
      } else {
        throwSyntaxError("Expected ',' or ']' instead of \"" + tokenFlow.peek().getValueAsString() + "\"", tokenFlow.peek());
      }
    }
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.RBRACKET, "Expected ']' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    BuildList list = new BuildList();
    list.setChildren(data);
    return list;
  }

  private IRNode<Instruction> dictOrSet() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.LBRACE, "Expected '{' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    iterateToEffectiveToken();
    if (tokenFlow.peek().isRBRACE()) {
      BuildSet set = new BuildSet();
      setSourceCodeInfo(set, tokenFlow.peek());
      tokenFlow.next();
      return set;
    }
    ArrayList<IRNode<Instruction>> data = new ArrayList<>();
    iterateToEffectiveToken();
    IRNode<Instruction> key = expression();
    iterateToEffectiveToken();
    data.add(key);
    if (tokenFlow.peek().isColon()) {
      tokenFlow.next();
      iterateToEffectiveToken();
      data.add(expression());
      iterateToEffectiveToken();
      while (true) {
        if (tokenFlow.peek().isRBRACE()) {
          tokenFlow.next();
          BuildMap map = new BuildMap();
          map.setChildren(data);
          return map;
        } else if (tokenFlow.peek().isComma()) {
          tokenFlow.next();
          iterateToEffectiveToken();
          data.add(expression());
          iterateToEffectiveToken();
          tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.COLON, "Expected ',' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
          tokenFlow.next();
          iterateToEffectiveToken();
          data.add(expression());
          iterateToEffectiveToken();
        }
      }
    } else if (tokenFlow.peek().isComma()) {
      tokenFlow.next();
      while (true) {
        iterateToEffectiveToken();
        data.add(expression());
        iterateToEffectiveToken();
        if (tokenFlow.peek().isRBRACE()) {
          tokenFlow.next();
          BuildSet set = new BuildSet();
          set.setChildren(data);
          return set;
        } else if (tokenFlow.peek().isComma()) {
          tokenFlow.next();
        } else {
          throwSyntaxError("Expected  ',' or '}", tokenFlow.peek());
        }
      }
    } else if (tokenFlow.peek().isRBRACE()) {
      BuildSet set = new BuildSet();
      set.setChildren(data);
      return set;
    }
    throwSyntaxError("Expected ':' or ',' or '}' instead of \"" + tokenFlow.peek().getValueAsString() + "\"", tokenFlow.peek());
    return null;
  }

  private IRNode<Instruction> anonymousFunction() throws SPLSyntaxError {
    tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.DEF,
        "Expected 'def' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    tokenFlow.next();
    var params = new ArrayList<String>();
    DefaultASTContext<Instruction> auxContex = new DefaultASTContext<>(filename, getSourceCode());
    var oldContex = context;
    context = auxContex;
    if (tokenFlow.peek().isLEFT_PARENTHESES()) {
      tokenFlow.next();
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
      tokenFlow.next();
    } else {
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.IDENTIFIER, "Expected 'IDENTIFIER' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
      params.add(tokenFlow.peek().getIdentifier());
      context.addVarName(tokenFlow.peek().getIdentifier());
      context.addSymbol(tokenFlow.peek().getIdentifier());
      tokenFlow.next();
      tokenAssertion(tokenFlow.peek(), Lexer.TOKEN_TYPE.ARROW, "Expected '->' instead of \"" + tokenFlow.peek().getValueAsString() + "\"");
    }
    ProgramBlock block;
    if (tokenFlow.peek().isArrow()) {
      tokenFlow.next();
      if (tokenFlow.peek().isLBRACE()) {
        block = (ProgramBlock) block();
      } else {
        block = new ProgramBlock();
        Return ret = new Return(expression());
        block.addIRNode(ret);
      }
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
    codeObject.setArgs(params.size());
    SPLFuncObject func = new SPLFuncObject(params, codeObject);
    context.addConstantObject(func);
    int idx = context.getConstantObjectIndex(func);
    FuncDef funcDef = new FuncDef(idx, params, func.getName(), block);
    setSourceCodeInfo(funcDef, tokenFlow.lookBack());
//    InsVisitor insVisitor = new InsVisitor(auxContex.getVarnames(), auxContex.getConstantMap());
//    auxContex.getInstructions().forEach(insVisitor::visit);
//    System.out.println(insVisitor);
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
    String name = token.getIdentifier();
    int idx = context.getVarNameIndex(token.getIdentifier());
    Scope scope;
    if (context.isGlobal(name)) {
      scope = Scope.GLOBAL;
    } else if (context.loadClosureVar(name) != -1) {
      scope = Scope.LOCAL;
      idx = context.loadClosureVar(name);
    } else if (context.getSymbolIndex(name) != -1) {
      scope = Scope.LOCAL;
    } else {
      scope = Scope.OTHERS;
    }
    switch (sign.token) {
      case ASSIGN -> {
        context.addSymbol(token.getIdentifier());
        context.addVarName(token.getIdentifier());
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(Scope.LOCAL, token.getIdentifier(), idx);
        AssignStmt assignStmt = new AssignStmt(lhs, rhs);
        setSourceCodeInfo(assignStmt, sign);
        return assignStmt;
      }
      case ASSIGN_ADD -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(scope, token.getIdentifier(), idx);
        AddAssignStmt addAssignStmt = new AddAssignStmt(lhs, rhs);
        setSourceCodeInfo(addAssignStmt, sign);
        return addAssignStmt;
      }
      case ASSIGN_SUB -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(scope, token.getIdentifier(), idx);
        SubAssignStmt subAssignStmt = new SubAssignStmt(lhs, rhs);
        setSourceCodeInfo(subAssignStmt, sign);
        return subAssignStmt;
      }
      case ASSIGN_MUL -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(scope, token.getIdentifier(), idx);
        MulAssignStmt mulAssignStmt = new MulAssignStmt(lhs, rhs);
        setSourceCodeInfo(mulAssignStmt, sign);
        return mulAssignStmt;
      }
      case ASSIGN_DIV -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(scope, token.getIdentifier(), idx);
        DivAssignStmt divAssignStmt = new DivAssignStmt(lhs, rhs);
        setSourceCodeInfo(divAssignStmt, sign);
        return divAssignStmt;
      }
      case ASSIGN_POWER -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(scope, token.getIdentifier(), idx);
        PowerAssignStmt powerAssignStmt = new PowerAssignStmt(lhs, rhs);
        setSourceCodeInfo(powerAssignStmt, sign);
        return powerAssignStmt;
      }
      case ASSIGN_MOD -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(scope, token.getIdentifier(), idx);
        ModAssignStmt modAssignStmt = new ModAssignStmt(lhs, rhs);
        setSourceCodeInfo(modAssignStmt, sign);
        return modAssignStmt;
      }
      case ASSIGN_OR -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(scope, token.getIdentifier(), idx);
        OrAssignStmt orAssignStmt = new OrAssignStmt(lhs, rhs);
        setSourceCodeInfo(orAssignStmt, sign);
        return orAssignStmt;
      }
      case ASSIGN_AND -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(scope, token.getIdentifier(), idx);
        AndAssignStmt andAssignStmt = new AndAssignStmt(lhs, rhs);
        setSourceCodeInfo(andAssignStmt, sign);
        return andAssignStmt;
      }
      case ASSIGN_XOR -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(scope, token.getIdentifier(), idx);
        XorAssignStmt xorAssignStmt = new XorAssignStmt(lhs, rhs);
        setSourceCodeInfo(xorAssignStmt, sign);
        return xorAssignStmt;
      }
      case ASSIGN_LSHIFT -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(scope, token.getIdentifier(), idx);
        LshiftAssignStmt lshiftAssignStmt = new LshiftAssignStmt(lhs, rhs);
        setSourceCodeInfo(lshiftAssignStmt, sign);
        return lshiftAssignStmt;
      }
      case ASSIGN_RSHIFT -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(scope, token.getIdentifier(), idx);
        RshiftAssignStmt rshiftAssignStmt = new RshiftAssignStmt(lhs, rhs);
        setSourceCodeInfo(rshiftAssignStmt, sign);
        return rshiftAssignStmt;
      }
      case ASSIGN_U_RSHIFT -> {
        IRNode<Instruction> rhs = expression();
        Variable lhs = new Variable(scope, token.getIdentifier(), idx);
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
