package org.spl.compiler.ir;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.ir.binaryop.*;
import org.spl.compiler.ir.block.Program;
import org.spl.compiler.ir.block.ProgramBlock;
import org.spl.compiler.ir.exp.*;
import org.spl.compiler.ir.stmt.ClassDefinition;
import org.spl.compiler.ir.stmt.Decorator;
import org.spl.compiler.ir.stmt.assignstmt.*;
import org.spl.compiler.ir.stmt.controlflow.*;
import org.spl.compiler.ir.stmt.func.FuncDef;
import org.spl.compiler.ir.stmt.returnstmt.Return;
import org.spl.compiler.ir.stmt.returnstmt.ReturnNone;
import org.spl.compiler.ir.unaryop.Invert;
import org.spl.compiler.ir.unaryop.Not;
import org.spl.compiler.ir.vals.*;
import org.spl.compiler.lexer.Lexer;

import java.util.List;

public class ASTVisualizer {

  private final IRNode<?> root;
  private final StringBuilder builder;
  private boolean done;
  private String name;

  public ASTVisualizer(IRNode<?> root) {
    this.root = root;
    builder = new StringBuilder();
    done = false;
    name = "Anonymous";
  }

  public ASTVisualizer(IRNode<?> root, String name) {
    this(root);
    this.name = name;
  }

  public String getDotFileContent() {
    if (!done) {
      visualize();
      done = true;
    }
    return builder.toString();
  }

  private void visualize() {
    builder.append("digraph ");
    builder.append("AST").append(" {\n");
    traversal();
    builder.append(String.format("\t%d [label=\"%s\"]\n", root.hashCode(), name));
    builder.append("}");
  }

  private void traversal() {
    visit(root);
  }

  private void visit(IRNode<?> node) {
    if (node instanceof Pop)
      return;
    builder.append(String.format("\t%d [label=\"%s\"]\n", node.hashCode(), getLabel(node)));
    if (isExceptBlock(node)) return;
    if (isTryStmtAndDo(node)) return;
    if (isForStmtAndDo(node)) return;
    if (isLoadAttrAndDo(node)) return;
    if (isLoadMethodAndDo(node)) return;
    if (isStoreAttrAndDo(node)) return;
    if (isInplaceStoreAttrAndDo(node)) return;
    if (isFunDefAndDo(node)) return;
    if (isConciseForAndDo(node)) return;
    if (isDecoratorAndDo(node)) return;
    if (isFunCallAndDo(node)) return;
    if (isClassDefAndDo(node)) return;
    if (isSubscribeAndDo(node)) return;
    if (isSubscribeStoreAndDo(node)) return;
    if (isReturnAndDo(node)) return;
    if (isReturnNoneAndDo(node)) return;
    if (isIfStmtAndDo(node)) return;
    if (isDoWhileAndDo(node)) return;
    if (isWhileAndDo(node)) return;
    List<? extends IRNode<?>> children = node.getVisualizedChildren();
    for (IRNode<?> child : children) {
      if (child instanceof Pop)
        continue;
      builder.append(String.format("\t%d -> %d\n", node.hashCode(), child.hashCode()));
      visit(child);
    }
  }

  private boolean isExceptBlock(IRNode<?> node) {
    if (node instanceof ExceptBlock exceptBlock) {
      builder.append(String.format("\t%d [label=\"CatchBlock\"]\n", exceptBlock.hashCode()));
      String exceptName = exceptBlock.getExceptName();
      String storeName = exceptBlock.getStoreName();
      Object o = new Object();
      builder.append(String.format("\t%d [label=\"%s\"]\n", o.hashCode(), exceptName));
      builder.append(String.format("\t%d -> %d[label=\"Exception\"]\n", exceptBlock.hashCode(), o.hashCode()));
      o = new Object();
      builder.append(String.format("\t%d [label=\"%s\"]\n", o.hashCode(), storeName));
      builder.append(String.format("\t%d -> %d\n", exceptBlock.hashCode(), o.hashCode()));
      if (exceptBlock.getBlock()!= null) {
        builder.append(String.format("\t%d -> %d\n", exceptBlock.hashCode(), exceptBlock.getBlock().hashCode()));
        visit(exceptBlock.getBlock());
      }
      return true;
    }
    return false;
  }

  private boolean isTryStmtAndDo(IRNode<?> node) {
    if (node instanceof TryStmt tryStmt) {
      builder.append(String.format("\t%d [label=\"%s\"]\n", tryStmt.hashCode(), "Try"));
      IRNode<Instruction> tryBlock = tryStmt.getTryBlock();
      if (tryBlock != null) {
        builder.append(String.format("\t%d -> %d[label=\"TryBlock\"]\n", tryStmt.hashCode(), tryBlock.hashCode()));
        visit(tryBlock);
      }
      List<IRNode<Instruction>> ctbs = tryStmt.getCatchBlock();
      if (ctbs != null)
        for (IRNode<Instruction> catchBlock : ctbs) {
          builder.append(String.format("\t%d -> %d[label=\"%s\"]\n", tryStmt.hashCode(), catchBlock.hashCode(), "CatchBlock"));
          visit(catchBlock);
        }
      IRNode<Instruction> finallyBlock = tryStmt.getFinallyBlock();
      if (finallyBlock!= null) {
        builder.append(String.format("\t%d -> %d[label=\"%s\"]\n", tryStmt.hashCode(), finallyBlock.hashCode(), "Finally"));
        visit(finallyBlock);
      }
      return true;
    }
    return false;
  }

  private boolean isWhileAndDo(IRNode<?> node) {
    if (node instanceof WhileStmt whileStmt) {
      builder.append(String.format("\t%d -> %d [label=\"Condition\"]\n", node.hashCode(), whileStmt.getCondition().hashCode()));
      visit(whileStmt.getCondition());
      IRNode<Instruction> block = whileStmt.getBlock();
      if (block != null) {
        builder.append(String.format("\t%d -> %d [label=\"Body\"]\n", node.hashCode(), block.hashCode()));
        visit(block);
      }
      return true;
    }
    return false;
  }

  private boolean isDoWhileAndDo(IRNode<?> node) {
    if (node instanceof DoWhile doWhile) {
      builder.append(String.format("\t%d -> %d [label=\"Condition\"]\n", node.hashCode(), doWhile.getCondition().hashCode()));
      visit(doWhile.getCondition());
      IRNode<Instruction> block = doWhile
          .getBlock();
      if (block != null) {
        builder.append(String.format("\t%d -> %d [label=\"Body\"]\n", node.hashCode(), block.hashCode()));
        visit(block);
      }
      return true;
    }
    return false;
  }

  private boolean isIfStmtAndDo(IRNode<?> node) {
    if (node instanceof IfStmt ifStmt) {
      IRNode<Instruction> condition = ifStmt.getCondition();
      builder.append(String.format("\t%d -> %d [label=\"Condition\"]\n", node.hashCode(), condition.hashCode()));
      visit(condition);
      IRNode<Instruction> thenBlock = ifStmt.getThenBlock();
      if (thenBlock != null) {
        builder.append(String.format("\t%d -> %d [label=\"ThenBlock\"]\n", node.hashCode(), thenBlock.hashCode()));
        visit(thenBlock);
      }
      IRNode<Instruction> elseBlock = ifStmt.getElseBlock();
      if (elseBlock != null) {
        builder.append(String.format("\t%d -> %d [label=\"ElseBlock\"]\n", node.hashCode(), elseBlock.hashCode()));
        visit(elseBlock);
      }
      return true;
    }
    return false;
  }

  private boolean isReturnNoneAndDo(IRNode<?> node) {
    if (node instanceof ReturnNone) {
      builder.append(String.format("\t%d [label=\"%s\", fillcolor=\"#e4706f\", style=\"filled\"]\n", node.hashCode(), "ReturnNone"));
      return true;
    }
    return false;
  }

  private boolean isReturnAndDo(IRNode<?> node) {
    if (node instanceof Return ret) {
      builder.append(String.format("\t%d [label=\"%s\", fillcolor=\"#e4706f\", style=\"filled\"]\n", node.hashCode(), "Return"));
      for (IRNode<Instruction> child : ret.getVisualizedChildren()) {
        builder.append(String.format("\t%d -> %d\n", node.hashCode(), child.hashCode()));
        visit(child);
      }
      return true;
    }
    return false;
  }

  private boolean isSubscribeStoreAndDo(IRNode<?> node) {
    if (node instanceof ArrayStyleStore array) {
      builder.append(String.format("\t%d [label=\"%s\"]\n", node.hashCode(), getOperator(array.getOpCode())));
      Object o = new Object();
      builder.append(String.format("\t%d [label=\"%s\"]\n", o.hashCode(), "[]"));
      builder.append(String.format("\t%d -> %d\n", node.hashCode(), o.hashCode()));
      IRNode<Instruction> obj = array.getObj();
      if (obj != null) {
        builder.append(String.format("\t%d -> %d\n", o.hashCode(), obj.hashCode()));
        visit(obj);
      }
      IRNode<Instruction> sub = array.getSub();
      if (sub != null) {
        builder.append(String.format("\t%d -> %d\n", o.hashCode(), sub.hashCode()));
        visit(sub);
      }
      IRNode<Instruction> value = array.getValue();
      if (value != null) {
        builder.append(String.format("\t%d -> %d\n", node.hashCode(), value.hashCode()));
        visit(value);
      }
      return true;
    }
    return false;
  }

  private boolean isSubscribeAndDo(IRNode<?> node) {
    if (node instanceof ArrayStyle arrayStyle) {
      builder.append(String.format("\t%d [label=\"[]\"]\n", node.hashCode()));
      IRNode<Instruction> lhs = arrayStyle.getLhs();
      builder.append(String.format("\t%d -> %d [label=\"lhs\"]\n", node.hashCode(), lhs.hashCode()));
      visit(lhs);
      IRNode<Instruction> sub = arrayStyle.getSub();
      if (sub != null) {
        builder.append(String.format("\t%d -> %d [label=\"sub\"]\n", node.hashCode(), sub.hashCode()));
        visit(sub);
      }
      return true;
    }
    return false;
  }

  private boolean isClassDefAndDo(IRNode<?> node) {
    if (node instanceof ClassDefinition classDef) {
      builder.append(String.format("\t%d [label=\"class\", fillcolor=\"#5383ec\", style=\"filled\"]\n", node.hashCode()));
      Object o = new Object();
      builder.append(String.format("\t%d [label=\"%s\"]\n", o.hashCode(), classDef.getClassName()));
      builder.append(String.format("\t%d -> %d [label=\"name\"]\n", node.hashCode(), o.hashCode()));
      o = new Object();
      builder.append(String.format("\t%d [label=\"%s\"]\n", o.hashCode(), classDef.getSuperClassName()));
      builder.append(String.format("\t%d -> %d [label=\"super\"]\n", node.hashCode(), o.hashCode()));
      IRNode<Instruction> block = classDef.getBlock();
      builder.append(String.format("\t%d -> %d [label=\"Body\"]\n", node.hashCode(), block.hashCode()));
      visit(block);
      return true;
    }
    return false;
  }

  private boolean isFunCallAndDo(IRNode<?> node) {
    if (node instanceof FuncCallExp call) {
      builder.append(String.format("\t%d [label=\"%s\", fillcolor=\"#f1cd77\", style=\"filled\"]\n", node.hashCode(), "Call"));
      IRNode<Instruction> lhs = call.getLhs();
      builder.append(String.format("\t%d -> %d [label=\"Callee\"]\n", node.hashCode(), lhs.hashCode()));
      visit(lhs);
      builder.append(String.format("\t%d [fillcolor=\"#8acef7\", style=\"filled\"]\n", lhs.hashCode()));

      Object o = new Object();
      builder.append(String.format("\t%d [label=\"(\"]\n", o.hashCode()));
      builder.append(String.format("\t%d -> %d\n", lhs.hashCode(), o.hashCode()));

      for (IRNode<Instruction> arg : call.getArgs()) {
        builder.append(String.format("\t%d -> %d\n", lhs.hashCode(), arg.hashCode()));
        visit(arg);
      }
      o = new Object();
      builder.append(String.format("\t%d [label=\")\"]\n", o.hashCode()));
      builder.append(String.format("\t%d -> %d\n", lhs.hashCode(), o.hashCode()));
      return true;
    }
    return false;
  }

  private boolean isDecoratorAndDo(IRNode<?> node) {
    if (node instanceof Decorator decorator) {
      IRNode<Instruction> expr = decorator.getExpr();
      FuncDef funcDef = decorator.getFuncDef();
      builder.append(String.format("\t%d [label=\"%s\"]\n", node.hashCode(), "@"));
      if (expr != null) {
        builder.append(String.format("\t%d -> %d [label=\"Decorator\"]\n", node.hashCode(), expr.hashCode()));
        visit(expr);
      }
      if (funcDef != null) {
        builder.append(String.format("\t%d -> %d [label=\"FuncDef\"]\n", node.hashCode(), funcDef.hashCode()));
        visit(funcDef);
      }
      return true;
    }
    return false;
  }

  private boolean isConciseForAndDo(IRNode<?> node) {
    if (node instanceof ConciseForStmt forStmt) {
      builder.append(String.format("\t%d [label=\"%s\"]\n", node.hashCode(), "for"));
      Object o = new Object();
      builder.append(String.format("\t%d [label=\"%s\"]\n", o.hashCode(), forStmt.getName()));
      builder.append(String.format("\t%d -> %d [label=\"iterable\"]\n", node.hashCode(), o.hashCode()));
      IRNode<Instruction> expression = forStmt.getExpression();
      if (expression != null) {
        builder.append(String.format("\t%d -> %d [label=\"in\"]\n", o.hashCode(), expression.hashCode()));
        visit(expression);
      }
      IRNode<Instruction> block = forStmt.getBlock();
      if (block != null) {
        builder.append(String.format("\t%d -> %d [label=\"Body\"]\n", node.hashCode(), block.hashCode()));
        visit(block);
      }
      return true;
    }
    return false;
  }

  private boolean isFunDefAndDo(IRNode<?> node) {
    if (node instanceof FuncDef funcDef) {
      StringBuilder tmp = new StringBuilder();
      for (String arg : funcDef.getArgs()) {
        tmp.append(arg).append(", ");
      }
      if (tmp.length() > 2) {
        tmp.delete(tmp.length() - 2, tmp.length());
      }
      builder.append(String.format("\t%d [label=\"def %s (%s)\", fillcolor=\"#65db79\", style=\"filled\"]\n", node.hashCode(), funcDef.getFuncName(), tmp.toString()));
      for (IRNode<Instruction> closure : funcDef.getClosures()) {
        builder.append(String.format("\t%d -> %d [label=\"Closure\"]\n", node.hashCode(), closure.hashCode()));
        visit(closure);
      }
      for (IRNode<Instruction> defaultNode : funcDef.getDefaults()) {
        builder.append(String.format("\t%d -> %d [label=\"Default\"]\n", node.hashCode(), defaultNode.hashCode()));
        visit(defaultNode);
      }
      builder.append(String.format("\t%d -> %d [label=\"Body\"]\n", node.hashCode(), funcDef.getBody().hashCode()));
      visit(funcDef.getBody());
      return true;
    }
    return false;
  }

  private boolean isInplaceStoreAttrAndDo(IRNode<?> node) {
    if (node instanceof InplaceStoreAttr inplaceStoreAttr) {
      builder.append(String.format("\t%d [label=\"%s\"]\n", node.hashCode(), inplaceStoreAttr.getOp()));
      Object o = new Object();
      builder.append(String.format("\t%d -> %d \n", node.hashCode(), o.hashCode()));
      builder.append(String.format("\t%d [label=\".\"]\n", o.hashCode()));
      builder.append(String.format("\t%d -> %d\n", o.hashCode(), inplaceStoreAttr.getLhs().hashCode()));
      Object t = new Object();
      builder.append(String.format("\t%d -> %d\n", o.hashCode(), t.hashCode()));
      builder.append(String.format("\t%d [label=\"%s\"]\n", t.hashCode(), inplaceStoreAttr.getName()));
      visit(inplaceStoreAttr.getLhs());
      builder.append(String.format("\t%d -> %d\n", node.hashCode(), inplaceStoreAttr.getRhs().hashCode()));
      visit(inplaceStoreAttr.getRhs());
      return true;
    }
    return false;
  }

  private boolean isStoreAttrAndDo(IRNode<?> node) {
    if (node instanceof StoreAttr storeAttr) {
      builder.append(String.format("\t%d [label=\"=\"]\n", node.hashCode()));
      Object o = new Object();
      builder.append(String.format("\t%d -> %d \n", node.hashCode(), o.hashCode()));
      builder.append(String.format("\t%d [label=\".\"]\n", o.hashCode()));
      builder.append(String.format("\t%d -> %d\n", o.hashCode(), storeAttr.getLhs().hashCode()));
      Object t = new Object();
      builder.append(String.format("\t%d -> %d\n", o.hashCode(), t.hashCode()));
      builder.append(String.format("\t%d [label=\"%s\"]\n", t.hashCode(), storeAttr.getName()));
      visit(storeAttr.getLhs());
      builder.append(String.format("\t%d -> %d\n", node.hashCode(), storeAttr.getRhs().hashCode()));
      visit(((StoreAttr) node).getRhs());
      return true;
    }
    return false;
  }

  private boolean isLoadMethodAndDo(IRNode<?> node) {
    if (node instanceof LoadMethod loadMethod) {
      builder.append(String.format("\t%d [label=\".\"]\n", node.hashCode()));
      builder.append(String.format("\t%d -> %d [label=\"LHS\"]\n", node.hashCode(), loadMethod.getLhs().hashCode()));
      visit(loadMethod.getLhs());
      Object o = new Object();
      builder.append(String.format("\t%d [label=\"%s\"]\n", o.hashCode(), loadMethod.getName()));
      builder.append(String.format("\t%d -> %d\n", node.hashCode(), o.hashCode()));
      return true;
    }
    return false;
  }

  private boolean isLoadAttrAndDo(IRNode<?> node) {
    if (node instanceof LoadAttr loadAttr) {
      builder.append(String.format("\t%d [label=\".\"]\n", node.hashCode()));
      builder.append(String.format("\t%d -> %d\n", node.hashCode(), loadAttr.getLhs().hashCode()));
      visit(loadAttr.getLhs());
      Object o = new Object();
      builder.append(String.format("\t%d [label=\"%s\"]\n", o.hashCode(), loadAttr.getName()));
      builder.append(String.format("\t%d -> %d\n", node.hashCode(), o.hashCode()));
      return true;
    }
    return false;
  }

  private boolean isForStmtAndDo(IRNode<?> node) {
    if (node instanceof ForStmt forStmt) {
      if (forStmt.getInitializer() != null) {
        builder.append(String.format("\t%d -> %d [label=\"Initializer\"]\n", node.hashCode(), forStmt.getInitializer().hashCode()));
        visit(forStmt.getInitializer());
      }
      if (forStmt.getCondition() != null) {
        builder.append(String.format("\t%d -> %d [label=\"Condition\"]\n", node.hashCode(), forStmt.getCondition().hashCode()));
        visit(forStmt.getCondition());
      }
      if (forStmt.getIncrement() != null) {
        builder.append(String.format("\t%d -> %d [label=\"Increment\"]\n", node.hashCode(), forStmt.getIncrement().hashCode()));
        visit(forStmt.getIncrement());
      }
      if (forStmt.getBody() != null) {
        builder.append(String.format("\t%d -> %d [label=\"Body\"]\n", node.hashCode(), forStmt.getBody().hashCode()));
        visit(forStmt.getBody());
      }
      return true;
    }
    return false;
  }

  public static String getLabel(IRNode<?> node) {
    if (node instanceof Add) {
      return "+";
    } else if (node instanceof Sub) {
      return "-";
    } else if (node instanceof Mul) {
      return "*";
    } else if (node instanceof Div) {
      return "/";
    } else if (node instanceof And) {
      return "&";
    } else if (node instanceof ConditionalAnd) {
      return "&&";
    } else if (node instanceof Or) {
      return "|";
    } else if (node instanceof ConditionalOr) {
      return "||";
    } else if (node instanceof Equal) {
      return "==";
    } else if (node instanceof NotEqual) {
      return "!=";
    } else if (node instanceof GreaterThan) {
      return ">";
    } else if (node instanceof GreaterOrEqual) {
      return ">=";
    } else if (node instanceof LessThan) {
      return "<";
    } else if (node instanceof LessOrEqual) {
      return "<=";
    } else if (node instanceof LShift) {
      return "<<";
    } else if (node instanceof RShift) {
      return ">>";
    } else if (node instanceof Mod) {
      return "%";
    } else if (node instanceof Power) {
      return "**";
    } else if (node instanceof TrueDiv) {
      return "//";
    } else if (node instanceof URShift) {
      return ">>>";
    } else if (node instanceof Xor) {
      return "^";
    } else if (node instanceof AndAssignStmt) {
      return "&=";
    } else if (node instanceof Variable var) {
      return var.getName();
    } else if (node instanceof BoolLiteral) {
      return "bool";
    } else if (node instanceof FloatLiteral f) {
      return Float.toString(f.getVal());
    } else if (node instanceof IntLiteral integer) {
      return Integer.toString(integer.getVal());
    } else if (node instanceof Program) {
      return "Program";
    } else if (node instanceof ProgramBlock) {
      return "ProgramBlock";
    } else if (node instanceof Invert) {
      return "~";
    } else if (node instanceof Not) {
      return "!";
    } else if (node instanceof AddAssignStmt) {
      return "+=";
    } else if (node instanceof AssignStmt) {
      return "=";
    } else if (node instanceof DivAssignStmt) {
      return "/=";
    } else if (node instanceof LshiftAssignStmt) {
      return "<<=";
    } else if (node instanceof MulAssignStmt) {
      return "*=";
    } else if (node instanceof RshiftAssignStmt) {
      return ">>=";
    } else if (node instanceof ModAssignStmt) {
      return "%=";
    } else if (node instanceof OrAssignStmt) {
      return "|=";
    } else if (node instanceof SubAssignStmt) {
      return "-=";
    } else if (node instanceof PowerAssignStmt) {
      return "**=";
    } else if (node instanceof TrueDivAssignStmt) {
      return "//=";
    } else if (node instanceof ULshiftAssignStmt) {
      return ">>>=";
    } else if (node instanceof XorAssignStmt) {
      return "^=";
    } else if (node instanceof Break) {
      return "break";
    } else if (node instanceof Continue) {
      return "continue";
    } else if (node instanceof DoWhile) {
      return "Do-While";
    } else if (node instanceof WhileStmt) {
      return "While";
    } else if (node instanceof ExceptBlock) {
      return "ExceptBlock";
    } else if (node instanceof ForStmt) {
      return "For";
    } else if (node instanceof IfStmt) {
      return "If";
    } else if (node instanceof TryStmt) {
      return "Try";
    } else if (node instanceof FuncDef f) {
      return "def " + f.getFuncName();
    } else if (node instanceof Return) {
      return "Return";
    } else if (node instanceof ReturnNone) {
      return "Return None";
    } else if (node instanceof Decorator) {
      return "@";
    } else if (node instanceof FuncCallExp) {
      return "()";
    } else if (node instanceof StringLiteral s) {
      return "\\\"" + s.getVal() + "\\\"";
    } else if (node instanceof ClassDefinition) {
      return "class";
    } else if (node instanceof BuildSet) {
      return "{}(Set)";
    } else if (node instanceof BuildList) {
      return "[]List";
    } else if (node instanceof BuildMap) {
      return "{}(Dict)";
    } else if (node instanceof LoadMethod || node instanceof LoadAttr) {
      return "";
    }
    return "Empty";
  }

  public static String getOperator(Lexer.TOKEN_TYPE opCode) {
    switch (opCode) {
      case ASSIGN_ADD -> {
        return "+=";
      }
      case ASSIGN_SUB -> {
        return "-=";
      }
      case ASSIGN_MUL -> {
        return "*=";
      }
      case ASSIGN_DIV -> {
        return "/=";
      }
      case ASSIGN_MOD -> {
        return "%=";
      }
      case ASSIGN_LSHIFT -> {
        return "<<=";
      }
      case ASSIGN_RSHIFT -> {
        return ">>=";
      }
      case ASSIGN_AND -> {
        return "&=";
      }
      case ASSIGN_XOR -> {
        return "^=";
      }
      case ASSIGN_OR -> {
        return "|=";
      }
      case ASSIGN_POWER -> {
        return "**=";
      }
      case ASSIGN -> {
        return "=";
      }
      case ASSIGN_U_RSHIFT -> {
        return ">>>=";
      }
      case ASSIGN_TRUE_DIV -> {
        return "//=";
      }
      case ASSIGN_INVERT -> {
        return "~=";
      }
      case MUL -> {
        return "*";
      }
      case DIV -> {
        return "/";
      }
      case MOD -> {
        return "%";
      }
      case LSHIFT -> {
        return "<<";
      }
      case RSHIFT -> {
        return ">>";
      }
      case AND -> {
        return "&";
      }
    }
    return "Unknow";
  }
}
