package org.spl.compiler.tree;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLStringObject;

import java.util.*;

public class InsVisitor implements Visitor<Instruction> {

  private final Map<Integer, Object> idx2Var;
  private final Map<Integer, SPLObject> idx2Constant;
  private final List<Instruction> instructions;
  private final List<String> serializedInstructions;
  private final HashSet<OpCode> loadStoreInstructions;
  private int offset;

  private InsVisitor() {
    idx2Var = new HashMap<>();
    idx2Constant = new HashMap<>();
    offset = 0;
    instructions = new ArrayList<>();
    serializedInstructions = new ArrayList<>();
    String header = String.format("\u001B[31m%-6s %-15s %-16s %-15s\u001B[0m",
        "Offset", "OpCode", "OpName", "Constant/OpArg");
    serializedInstructions.add(header);
    loadStoreInstructions = new HashSet<>();
    loadStoreInstructions.add(OpCode.LOAD_GLOBAL);
    loadStoreInstructions.add(OpCode.STORE_GLOBAL);
    loadStoreInstructions.add(OpCode.LOAD_LOCAL);
    loadStoreInstructions.add(OpCode.LOAD);
    loadStoreInstructions.add(OpCode.STORE_LOCAL);
    loadStoreInstructions.add(OpCode.LOAD_ATTR);
    loadStoreInstructions.add(OpCode.STORE_ATTR);
    loadStoreInstructions.add(OpCode.LOAD_METHOD);
    loadStoreInstructions.add(OpCode.LOAD_NAME);
    loadStoreInstructions.add(OpCode.STORE);
    loadStoreInstructions.add(OpCode.STORE_EXC_VAL);
  }

  public InsVisitor(Map<?, Integer> varMap, Map<SPLObject, Integer> constants) {
    this();
    varMap.forEach((x, y) -> {
      idx2Var.put(y, x);
    });
    constants.forEach((x, y) -> {
      idx2Constant.put(y, x);
    });
  }

  public InsVisitor(SPLStringObject[] vars, SPLObject[] constants) {
    this();
    for (int i = 0; i < vars.length; i++) {
      idx2Var.put(i, vars[i]);
    }
    for (int i = 0; i < constants.length; i++) {
      idx2Constant.put(i, constants[i]);
    }
  }

  @Override
  public void visit(Instruction instruction) {
    String serialized;
    OpCode opcode = instruction.getCode();
    if (loadStoreInstructions.contains(opcode)) {
      serialized = String.format(
          "%-6d %s %s", offset, opcode,
          idx2Var.get((instruction.getOpArg())));
    } else if (opcode == OpCode.LOAD_CONST) {
      serialized = String.format("%-6d %s %s", offset, opcode,
          idx2Constant.get((instruction.getOpArg())));
    } else if (opcode == OpCode.CALL ||
        opcode == OpCode.JUMP_FALSE || opcode == OpCode.JUMP_UNCON_FORWARD ||
        opcode == OpCode.JUMP_BACK || opcode == OpCode.JUMP_BACK_TRUE ||
        opcode == OpCode.JUMP_ABSOLUTE ||
        opcode == OpCode.JMP_TRUE_NO_POP || opcode == OpCode.LONG_JUMP ||
        opcode == OpCode.CALL_METHOD || opcode == OpCode.NEXT ||
        opcode == OpCode.MAKE_FUNCTION) {
      serialized = String.format("%-6d %s %d", offset, opcode,
          instruction.getOpArg());
    } else {
      serialized = String.format("%-6d %s", offset, opcode);
    }
    if (opcode == OpCode.JUMP_ABSOLUTE || opcode == OpCode.LONG_JUMP) {
      offset += 4;
    } else {
      if (instruction.getOparg() >= 255)
        offset += 4;
      else
        offset += 2;
    }
    serializedInstructions.add(serialized);
  }

  public List<Instruction> getInstructions() {
    return instructions;
  }


  public List<String> getSerializedInstructions() {
    return serializedInstructions;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    serializedInstructions.forEach(x -> builder.append(x).append("\n"));
    builder.delete(builder.length() - 1, builder.length());
    return builder.toString();
  }
}
