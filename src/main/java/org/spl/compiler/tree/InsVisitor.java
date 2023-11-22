package org.spl.compiler.tree;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;

import java.util.*;

public class InsVisitor implements Visitor<Instruction> {

  private final Map<Integer, Object> idx2constant;

  private final List<Instruction> instructions;
  private final List<String> serializedInstructions;
  private final HashSet<OpCode> loadStoreInstructions;

  public InsVisitor(Map<?, Integer> constantTable) {
    idx2constant = new HashMap<>();
    constantTable.forEach((x, y) -> {
      idx2constant.put(y, x);
    });
    instructions = new ArrayList<>();
    serializedInstructions = new ArrayList<>();
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
          "%-11s %s", instruction.getCode(),
          idx2constant.get((int) instruction.getOpArg()));
    } else if (instruction.getCode() == OpCode.CALL ||
        instruction.getCode() == OpCode.CALL_METHOD) {
      serialized = String.format("%-11s %d", instruction.getCode(),
          instruction.getOpArg());
    } else {
      serialized = String.format("%-11s", instruction.getCode());
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
    return builder.toString();
  }
}
