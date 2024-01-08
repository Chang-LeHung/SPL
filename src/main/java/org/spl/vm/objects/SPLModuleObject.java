package org.spl.vm.objects;

import org.spl.vm.types.SPLModuleType;

import java.util.Map;

public class SPLModuleObject extends SPLObject {

  private final SPLStringObject name;

  public SPLModuleObject(String name, Map<SPLObject, SPLObject> attrs) {
    this(new SPLStringObject(name), attrs);
  }

  public SPLModuleObject(SPLStringObject name, Map<SPLObject, SPLObject> attrs) {
    super(SPLModuleType.getInstance());
    this.name = name;
    this.attrs = attrs;
  }

  public SPLModuleObject(SPLStringObject name) {
    super(SPLModuleType.getInstance());
    this.name = name;
  }

  public SPLModuleObject(String name) {
    this(new SPLStringObject(name));
  }


  public SPLStringObject getName() {
    return name;
  }

}
