package org.spl.vm.objects;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLException;
import org.spl.vm.types.SPLCallType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SPLCallObject extends SPLObject {

  private final Method method;
  private boolean isStatic;
  private SPLObject self;

  public SPLCallObject(Method method, SPLObject self, boolean isStatic) {
    super(SPLCallType.getInstance());
    this.self = self;
    this.method = method;
    this.isStatic = isStatic;
  }

  @Override
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    try {
      if (isStatic) {
        return (SPLObject) method.invoke(null, (Object) args);
      } else {
        return (SPLObject) method.invoke(self, (Object) args);
      }
    } catch (InvocationTargetException | IllegalAccessException e) {
      return SPLErrorUtils.splErrorFormat(new SPLException(e.getCause().getMessage()));
    }
  }

  @Override
  public String toString() {
    if (isStatic) {
      return "NativeMethod-Static{" + method.getName() + "}";
    }
    return "NativeMethod{" + method.getName() + "}, Self is \"" + self.__str__() + "\"";
  }

  @Override
  public SPLObject __str__() {
    return new SPLStringObject(toString());
  }

  public SPLObject getSelf() {
    return self;
  }

  public void setSelf(SPLObject self) {
    this.self = self;
  }

  public boolean isStatic() {
    return isStatic;
  }

  public void setStatic(boolean aStatic) {
    isStatic = aStatic;
  }
}
