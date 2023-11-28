package org.spl.compiler.tree;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;

import java.util.*;

public class InsVisitor implements Visitor<Instruction> {

  private final Map<Integer, Object> idx2constant;
  private int offset;
  private final List<Instruction> instructions;
  private final List<String> serializedInstructions;
  private final HashSet<OpCode> loadStoreInstructions;

  public InsVisitor(Map<?, Integer> constantTable) {
    idx2constant = new HashMap<>();
    constantTable.forEach((x, y) -> {
      idx2constant.put(y, x);
    });
    offset = 0;
    instructions = new ArrayList<>();
    serializedInstructions = new ArrayList<>();
    String header = String.format("\u001B[31m%-6s %s %15s %s\u001B[0m", "Offset", "OpCode", "OpName", "OpArg");
    serializedInstructions.add(header);
    loadStoreInstructions = new HashSet<>();
    loadStoreInstructions.add(OpCode.LOAD_GLOBAL);
    loadStoreInstructions.add(OpCode.STORE_GLOBAL);
    loadStoreInstructions.add(OpCode.LOAD_LOCAL);
    loadStoreInstructions.add(OpCode.STORE_LOCAL);
    loadStoreInstructions.add(OpCode.LOAD_METHOD);
    loadStoreInstructions.add(OpCode.LOAD_CONST);
    loadStoreInstructions.add(OpCode.LOAD_NAME);
    loadStoreInstructions.add(OpCode.STORE);
  }

  @Override
  public void visit(Instruction instruction) {
    String serialized;
    if (loadStoreInstructions.contains(instruction.getCode())) {
      serialized = String.format(
          "%-6d %s %s", offset, instruction.getCode(),
          idx2constant.get((instruction.getOpArg())));
    } else if (instruction.getCode() == OpCode.CALL ||
        instruction.getCode() == OpCode.JUMP_FALSE ||
        instruction.getCode() == OpCode.CALL_METHOD) {
      serialized = String.format("%-6d %s %d", offset, instruction.getCode(),
          instruction.getOpArg());
    } else {
      serialized = String.format("%-6d %s", offset, instruction.getCode());
    }
    if (instruction.getOparg() >= 255)
      offset += 4;
    else
      offset += 2;
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
