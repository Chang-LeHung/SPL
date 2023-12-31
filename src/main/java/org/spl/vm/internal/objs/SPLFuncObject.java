package org.spl.vm.internal.objs;

import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLRuntimeException;
import org.spl.vm.interfaces.SPLContinuable;
import org.spl.vm.internal.typs.SPLFuncType;
import org.spl.vm.internal.utils.Dissembler;
import org.spl.vm.interpreter.DefaultEval;
import org.spl.vm.interpreter.ThreadState;
import org.spl.vm.objects.SPLNoneObject;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLStringObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPLFuncObject extends SPLObject implements SPLContinuable {
  public static int anonymousCount = 0;
  private final List<String> parameters;
  private final String name;
  private final SPLCodeObject codeObject;
  private Map<SPLObject, SPLObject> globals;
  /**
   * defaults will be set in runtime (in instruction MAKE_FUNCTION)
   */
  private List<SPLObject> defaults;
  private DefaultEval evaluation;

  public SPLFuncObject(List<String> parameters, String name, SPLCodeObject codeObject) {
    super(SPLFuncType.getInstance());
    this.parameters = parameters;
    this.name = name;
    this.codeObject = codeObject;
    this.codeObject.setName(new SPLStringObject(name));
  }

  public SPLFuncObject(SPLFuncObject f) {
    super(SPLFuncType.getInstance());
    this.parameters = f.parameters;
    this.name = f.name;
    this.codeObject = f.codeObject;
    this.globals = f.globals;
    this.defaults = f.defaults;
  }

  public SPLFuncObject(List<String> parameters, SPLCodeObject codeObject) {
    super(SPLFuncType.getInstance());
    this.parameters = parameters;
    this.name = "Anonymous-" + ++anonymousCount;
    this.codeObject = codeObject;
    this.codeObject.setName(new SPLStringObject(this.name));
  }

  public List<String> getParameters() {
    return parameters;
  }

  public String getName() {
    return name;
  }

  public List<SPLObject> getDefaults() {
    return defaults;
  }

  public void setDefaults(List<SPLObject> defaults) {
    this.defaults = defaults;
  }

  public SPLCodeObject getCodeObject() {
    return codeObject;
  }

  @Override
  public String toString() {
    return String.format("def %s (...) {...}", name);
  }

  @Override
  public Map<SPLObject, SPLObject> getGlobals() {
    return globals;
  }

  @Override
  public void setGlobals(Map<SPLObject, SPLObject> globals) {
    this.globals = globals;
  }


  public void buildEval(SPLObject... args) throws SPLInternalException {
    if (args.length + defaults.size() < parameters.size()) {
      SPLErrorUtils.splErrorFormat(new SPLRuntimeException(
          String.format("Invalid number of arguments, request %d parameters but only found %d arguments",
              parameters.size() - defaults.size(), args.length)));
    } else if (args.length > parameters.size()) {
      SPLErrorUtils.splErrorFormat(new SPLRuntimeException(
          String.format("Invalid number of arguments, request %d parameters but found %d arguments",
              parameters.size(), args.length)));
    }
    // pass function's parameters as locals
    Map<SPLObject, SPLObject> locals = new HashMap<>();
    for (int i = args.length; i < parameters.size(); i++) {
      locals.put(new SPLStringObject(parameters.get(i)), defaults.get(i - args.length));
    }
    for (int i = 0; i < args.length; i++) {
      locals.put(new SPLStringObject(parameters.get(i)), args[i]);
    }
    assert globals != null;
    evaluation = new DefaultEval(name, locals, globals, codeObject);
  }

  @Override
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    buildEval(args);
    ThreadState ts = ThreadState.get();
    SPLFrameObject currentFrame = ts.getCurrentFrame();
    ts.setCurrentFrame(evaluation);
    SPLObject res = evaluation.evalFrame();
    ts.setCurrentFrame(currentFrame);
    return res;
  }

  @SPLExportMethod
  public SPLObject dis(SPLObject... args) {
    Dissembler dissembler = new Dissembler(codeObject);
    dissembler.prettyPrint();
    return SPLNoneObject.getInstance();
  }

  @Override
  public SPLObject resume() throws SPLInternalException {
    ThreadState ts = ThreadState.get();
    SPLFrameObject currentFrame = ts.getCurrentFrame();
    ts.setCurrentFrame(evaluation);
    SPLObject res = evaluation.resume();
    ts.setCurrentFrame(currentFrame);
    return res;
  }

}
