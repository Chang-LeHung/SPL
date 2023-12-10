package org.spl.vm.objects;

import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLOutOfBoundException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.interfaces.SPLIterator;
import org.spl.vm.types.SPLCommonType;
import org.spl.vm.types.SPLListType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SPLListObject extends SPLObject {

  private final List<SPLObject> container;

  public SPLListObject() {
    super(SPLListType.getInstance());
    container = new ArrayList<>();
  }

  public SPLListObject(List<SPLObject> container) {
    super(SPLListType.getInstance());
    this.container = container;
  }

  @Override
  public SPLObject __inplaceAdd__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLListObject other) {
      container.addAll(other.container);
      return this;
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot add list to non-list"));
  }

  @Override
  public SPLObject __add__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLListObject other) {
      ArrayList<SPLObject> res = new ArrayList<>(container);
      res.addAll(other.container);
      return new SPLListObject(res);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot add list to non-list"));
  }

  @Override
  public SPLObject __inplaceMul__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject o) {
      SPLObject[] array = container.toArray(new SPLObject[0]);
      for (int i = 0; i < o.getVal(); i++) {
        container.addAll(Arrays.asList(array));
      }
      return this;
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot mul list to " + rhs.getType()));
  }

  @Override
  public SPLObject __mul__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject o) {
      var res = new ArrayList<SPLObject>();
      for (int i = 0; i < o.getVal(); i++) {
        res.addAll(container);
      }
      return new SPLListObject(res);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot mul list to " + rhs.getType()));
  }

  @Override
  public SPLObject __str__() {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    container.forEach(x -> builder.append(x.__str__()).append(", "));
    if (builder.length() > 2)
      builder.delete(builder.length() - 2, builder.length());
    builder.append("]");
    return new SPLStringObject(builder.toString());
  }

  @SPLExportMethod
  public SPLObject append(SPLObject... args) throws SPLInternalException {
    if (args.length == 1) {
      container.add(args[0]);
      return SPLNoneObject.getInstance();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("append() takes exactly one argument"));
  }

  @Override
  public SPLObject __subscribe__(SPLObject args) throws SPLInternalException {
    if (args instanceof SPLLongObject o) {
      int idx = (int) o.getVal();
      if (idx < container.size())
        return container.get(idx);
      return SPLErrorUtils.splErrorFormat(new SPLOutOfBoundException(
          String.format("Index %d out of bound %d", idx, container.size())));
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Index must be integer"));
  }

  @Override
  public SPLObject __getIterator__() throws SPLInternalException {
    return new SPLListIterator(new ArrayList<>(container));
  }

  public static class SPLListIteratorType extends SPLCommonType {

    private SPLListIteratorType() {
      super(null, "list_iterator", SPLListIterator.class);
    }

    private static class SelfHolder {
      public static final SPLListIteratorType INSTANCE = new SPLListIteratorType();
    }

    public static SPLListIteratorType getInstance() {
      return SelfHolder.INSTANCE;
    }
  }

  public static class SPLListIterator extends SPLObject implements SPLIterator {

    private final List<SPLObject> container;
    int off;
    public SPLListIterator(List<SPLObject> container) {
      super(SPLListIteratorType.getInstance());
      this.container = container;
      off = 0;
    }

    @Override
    public SPLObject next() throws SPLInternalException {
      if (off < container.size()) {
        return container.get(off++);
      }
      return SPLStopIteration.getInstance();
    }
  }
}
