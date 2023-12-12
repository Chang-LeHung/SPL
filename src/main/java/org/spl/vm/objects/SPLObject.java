package org.spl.vm.objects;

import org.spl.vm.annotations.SPLExportField;
import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLNotImplemented;
import org.spl.vm.exceptions.splexceptions.SPLRuntimeException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.internal.objs.SPLMethodWrapper;
import org.spl.vm.types.SPLCommonType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SPLObject implements SPLInterface {

  @SPLExportField
  protected final SPLCommonType type;
  protected Map<SPLObject, Object> methods;
  protected Map<SPLObject, SPLObject> attrs;

  public SPLObject(SPLCommonType type) {
    this.type = type;
    attrs = new HashMap<>();
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
  public SPLObject __xor__(SPLObject rhs) throws SPLInternalException {
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


  private SPLObject loadAttributeFromClass(Class<?> clazz, SPLObject name) {
    var sn = name.__str__().toString();
    try {
      Field filed = clazz.getDeclaredField(sn);
      filed.setAccessible(true);
      if (filed.isAnnotationPresent(SPLExportField.class) && SPLObject.class.isAssignableFrom(filed.getType())) {
        return (SPLObject) filed.get(this);
      }
    } catch (NoSuchFieldException | IllegalAccessException ignored) {
    }
    return null;
  }
  @Override
  public SPLObject __getAttr__(SPLObject name) throws SPLInternalException {
    if (attrs.containsKey(name)) {
      return attrs.get(name);
    } else {
      // load from this class
      SPLObject res = loadAttributeFromClass(getClass(), name);
      if (res != null) {
        attrs.put(name, res);
        return res;
      }
      // load from super class
      res = loadAttributeFromClass(getClass().getSuperclass(), name);
      if (res != null) {
        attrs.put(name, res);
        return res;
      }
    }
    return __getMethod__(name);
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
  public SPLObject __subscribe__(SPLObject args) throws SPLInternalException {
    return __getAttr__(args);
  }

  @Override
  public SPLObject __getIterator__() throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation 'getIterator' not implemented"));
  }

  @Override
  public SPLObject __getMethod__(SPLObject name) throws SPLInternalException {
    if (attrs != null && attrs.containsKey(name)) {
      SPLObject res = attrs.get(name);
      if (res instanceof SPLFuncObject) {
        return res;
      }
    }
    return type.__getMethod__(name);
  }

  @SPLExportMethod
  public SPLObject bind(SPLObject... args) throws SPLInternalException {
    if (args.length == 2 && args[0] instanceof SPLStringObject name && args[1] instanceof SPLFuncObject func) {
      if (methods == null) {
        methods = new HashMap<>();
      }
      methods.put(name, func);
      return SPLNoneObject.getInstance();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Invalid arguments for bind"));
  }

  @Override
  public String toString() {
    return String.format("<object %s @0x%s>", getType().getName(), Integer.toHexString(hashCode()));
  }
}
