package org.spl.vm.objects;

import org.spl.vm.annotations.SPLExportField;
import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLAttributeError;
import org.spl.vm.exceptions.splexceptions.SPLNotImplemented;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.internal.objs.SPLMethodWrapper;
import org.spl.vm.interpreter.ThreadState;
import org.spl.vm.types.SPLCommonType;
import org.spl.vm.types.SPLObjectType;

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
    if (type == null) {
      this.type = SPLObjectType.getInstance();
    } else {
      this.type = type;
    }
    attrs = new HashMap<>();
  }

  @Override
  public SPLCommonType getType() {
    return type;
  }

  @Override
  public SPLObject __add__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s operation '+' not implemented"));
  }

  @Override
  public SPLObject __sub__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '-' not implemented"));
  }

  @Override
  public SPLObject __mul__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '*' not implemented"));
  }

  @Override
  public SPLObject __div__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '/' not implemented"));
  }

  @Override
  public SPLObject __trueDiv__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '//' not implemented"));
  }

  @Override
  public SPLObject __mod__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '%' not implemented"));
  }

  @Override
  public SPLObject __pow__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '**' not implemented"));
  }

  @Override
  public SPLObject __lshift__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '<<' not implemented"));
  }

  @Override
  public SPLObject __URshift__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented(type.getName() + "'s '>>=' not implemented"));
  }

  @Override
  public SPLObject __rshift__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented(type.getName() + "'s '>>' not implemented"));
  }


  @Override
  public SPLObject __and__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '&' not implemented"));
  }

  @Override
  public SPLObject __or__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '|' not implemented"));
  }


  @Override
  public SPLObject __not__() throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '~' not implemented"));
  }

  @Override
  public SPLObject __xor__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '^' not implemented"));
  }


  @Override
  public SPLObject __neg__() throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '-' not implemented"));
  }

  @Override
  public SPLObject __eq__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '==' not implemented"));
  }

  @Override
  public SPLObject __ne__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '!=' not implemented"));
  }

  @Override
  public SPLObject __lt__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented(type.getName() + "'s '<' not implemented"));
  }

  @Override
  public SPLObject __gt__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '>' not implemented"));
  }

  @Override
  public SPLObject __le__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented(type.getName() + "'s '<=' not implemented"));
  }

  @Override
  public SPLObject __ge__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented(type.getName() + "'s '>=' not implemented"));
  }

  @Override
  public SPLObject __conditionalAnd__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '&&' not implemented"));
  }

  @Override
  public SPLObject __conditionalOr__(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '||' not implemented"));
  }

  @Override
  public SPLObject __invert__() throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '~' not implemented"));
  }

  @Override
  @SPLExportMethod
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s '()' not implemented"));
  }

  @Override
  public SPLObject __str__() throws SPLInternalException {
    return new SPLStringObject(this.toString());
  }


  protected SPLObject loadAttributeFromClass(Class<?> clazz, SPLObject name) throws SPLInternalException {
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
      Class<?> clazz = getClass();
      while (SPLObject.class.isAssignableFrom(clazz)) {
        SPLObject res = loadAttributeFromClass(clazz, name);
        if (res != null) {
          attrs.put(name, res);
          return res;
        }
        clazz = clazz.getSuperclass();
      }
    }
    try {
      return __getMethod__(name);
    } catch (Exception ignore) {
    } finally {
      ThreadState.clearCurrentCoroutineState();
    }
    return type.__getAttr__(name);
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
  public SPLCommonIterator __getIterator__() throws SPLInternalException {
    return (SPLCommonIterator) SPLErrorUtils.splErrorFormat(new SPLNotImplemented(type.getName() + "'s 'getIterator' not implemented"));
  }

  @Override
  public SPLObject __getMethod__(SPLObject name) throws SPLInternalException {
    try {
      SPLObject method = type.__getMethod__(name);
      if (method instanceof SPLCallObject callable) {
        callable.setStatic(false);
        callable.setSelf(this);
        return callable;
      } else if (method instanceof SPLFuncObject callable) {
        return new SPLMethodWrapper(callable, this);
      } else if (method instanceof SPLStaticMethodWrapper callable) {
        return callable;
      }
    } catch (Exception ignore) {
      ThreadState.clearCurrentCoroutineState();
    }
    // fall back to self
    if (attrs.containsKey(name)) {
      return attrs.get(name);
    }

    try {
      Method method = this.getClass().getMethod(name.toString(), SPLObject[].class);
      if (method.isAnnotationPresent(SPLExportMethod.class) && method.getReturnType().isAssignableFrom(SPLObject.class)) {
        SPLCallObject callable = new SPLCallObject(method, this, false);
        attrs.put(name, callable);
        return callable;
      }
    } catch (NoSuchMethodException ignore) {
    }

    return SPLErrorUtils.splErrorFormat(new SPLAttributeError("can not find an attribute or method '" + name + "'"));
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

  public Map<SPLObject, SPLObject> getAttrs() {
    return attrs;
  }

  public SPLObject getAttrFromAttrs(SPLObject name) {
    return attrs.get(name);
  }
}
