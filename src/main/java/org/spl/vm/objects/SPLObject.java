package org.spl.vm.objects;

import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLAttributeError;
import org.spl.vm.exceptions.splexceptions.SPLNotImplemented;
import org.spl.vm.exceptions.splexceptions.SPLRuntimeException;
import org.spl.vm.types.SPLCommonType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SPLObject implements SPLInterface {

  private final SPLCommonType type;
  private Map<SPLObject, Method> methods;
  private Map<SPLObject, SPLObject> attrs;

  public SPLObject(SPLCommonType type) {
    this.type = type;
  }

  @Override
  public SPLCommonType getType() {
    return type;
  }

  @Override
  public SPLObject __add__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '+' not implemented"));
  }

  @Override
  public SPLObject __sub__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '-' not implemented"));
  }

  @Override
  public SPLObject __mul__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '*' not implemented"));
  }

  @Override
  public SPLObject __div__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '/' not implemented"));
  }

  @Override
  public SPLObject __trueDiv__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '//' not implemented"));
  }

  @Override
  public SPLObject __mod__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '%' not implemented"));
  }

  @Override
  public SPLObject __pow__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '**' not implemented"));
  }

  @Override
  public SPLObject __lshift__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '<<' not implemented"));
  }

  @Override
  public SPLObject __URshift__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented("operation '>>=' not implemented"));
  }

  @Override
  public SPLObject __rshift__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented("operation '>>' not implemented"));
  }


  @Override
  public SPLObject __and__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '&' not implemented"));
  }

  @Override
  public SPLObject __or__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '|' not implemented"));
  }


  @Override
  public SPLObject __not__() throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '~' not implemented"));
  }

  @Override
  public SPLObject xor(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '^' not implemented"));
  }


  @Override
  public SPLObject __neg__() throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '-' not implemented"));
  }

  @Override
  public SPLObject __eq__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '==' not implemented"));
  }

  @Override
  public SPLObject __ne__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '!=' not implemented"));
  }

  @Override
  public SPLObject __lt__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented("operation '<' not implemented"));
  }

  @Override
  public SPLObject __gt__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '>' not implemented"));
  }

  @Override
  public SPLObject __le__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented("operation '<=' not implemented"));
  }

  @Override
  public SPLObject __ge__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented("operation '>=' not implemented"));
  }

  @Override
  public SPLObject __conditionalAnd__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '&&' not implemented"));
  }

  @Override
  public SPLObject __conditionalOr__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '||' not implemented"));
  }

  @Override
  public SPLObject __invert__() throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '~' not implemented"));
  }

  @Override
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented("operation 'call' not implemented"));
  }

  @Override
  public SPLObject __str__() {
    return new SPLStringObject(this.toString());
  }

  @Override
  public SPLObject __getAttr__(SPLObject name) throws SPLInternalException {
    if (attrs != null && attrs.containsKey(name)) {
      return attrs.get(name);
    }
    return SPLErrorUtils.splErrorFormat(new SPLAttributeError("Not found a attribute named '" + name + "'"));
  }

  @Override
  public SPLObject __setAttr__(SPLObject name, SPLObject value) throws SPLInternalException {
    if (attrs == null) {
      attrs = new HashMap<>();
    }
    attrs.put(name, value);
    return SPLNoneObject.getInstance();
  }

  @Override
  public SPLObject __getMethod__(SPLObject name) throws SPLInternalException {
    if (methods != null && methods.containsKey(name)) {
      Method method = methods.get(name);
      return new SPLCallObject(method, this, false);
    }
    try {
      Method method = getClass().getMethod(name.toString(), SPLObject[].class);
      if (method.isAnnotationPresent(SPLExportMethod.class) && method.getReturnType().isAssignableFrom(SPLObject.class)) {
        if (methods == null) {
          methods = new HashMap<>();
        }
        methods.put(name, method);
        return new SPLCallObject(method, this, false);
      }
    } catch (NoSuchMethodException ignore) {
    }
    return SPLErrorUtils.splErrorFormat(new SPLRuntimeException("Not found a method named '" + name + "'"));
  }

}
