package org.spl.vm.internal.objs;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLLevelObject;
import org.spl.vm.exceptions.types.SPLLevelType;
import org.spl.vm.internal.typs.SPLClassDefinitionType;
import org.spl.vm.interpreter.DefaultEval;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.types.SPLCommonType;

import java.util.HashMap;

public class SPLClassDefinition extends SPLObject {
  private final SPLCodeObject codeObject;
  private final String className;
  private SPLCommonType superType;

  private int hashCode = -1;

  public SPLClassDefinition(SPLCodeObject codeObject, String name) {
    super(SPLClassDefinitionType.getInstance());
    this.codeObject = codeObject;
    this.className = name;
  }

  public SPLCodeObject getCodeObject() {
    return codeObject;
  }

  public String getClassName() {
    return className;
  }

  public SPLCommonType getSuperType() {
    return superType;
  }

  public void setSuperType(SPLCommonType superType) {
    this.superType = superType;
  }

  @Override
  public int hashCode() {
    if (hashCode == -1) {
      hashCode = className.hashCode() + codeObject.hashCode();
    }
    return hashCode;
  }

  public SPLCommonType buildType(SPLCommonType base) throws SPLInternalException {
    HashMap<SPLObject, SPLObject> locals = new HashMap<>();
    DefaultEval eval = new DefaultEval(className, locals, locals, codeObject);
    eval.evalFrame();
    return new SPLLevelType(null, className, SPLLevelObject.class, base, locals);
  }
}
