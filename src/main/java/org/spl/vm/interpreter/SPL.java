package org.spl.vm.interpreter;

import org.spl.compiler.SPLCompiler;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.vm.config.SPLConfigBuilder;
import org.spl.vm.config.SPLConfiguration;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.internal.objs.SPLFrameObject;
import org.spl.vm.internal.utils.Dissembler;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.splroutine.SPLRoutineObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class SPL {

  static {
    Evaluation.init();
  }

  private final String filename;
  private final SPLCodeObject code;
  private DefaultEval frame;

  public SPL(String filename) throws SPLSyntaxError, IOException {
    this.filename = filename;
    SPLCompiler compiler = new SPLCompiler(getResource(filename));
    SPLCodeObject code = compiler.compile();
    this.code = code;
    try {
      this.frame = new DefaultEval(code);
    } catch (SPLInternalException ignore) {
    }
  }

  public SPL(String filename, HashMap<SPLObject, SPLObject> locals,
             HashMap<SPLObject, SPLObject> globals) throws SPLSyntaxError, IOException {
    this.filename = filename;
    SPLCompiler compiler = new SPLCompiler(getResource(filename));
    SPLCodeObject code = compiler.compile();
    this.code = code;
    this.frame = new DefaultEval(filename, locals, globals, code);
  }

  public SPL(String filename, HashMap<SPLObject, SPLObject> locals,
             HashMap<SPLObject, SPLObject> globals, SPLCodeObject code) throws SPLSyntaxError, IOException {
    this.filename = filename;
    this.code = code;
    this.frame = new DefaultEval(filename, locals, globals, code);
  }

  public static String getResource(String filename) {
    URL resource = Thread.currentThread().
        getContextClassLoader().
        getResource(filename);
    if (resource == null)
      return filename;
    return resource.getPath();
  }

  public static void printStackTrace() {
    // make content below printed in red
    System.err.print("\033[31m");
    SPLRoutineObject routine = ThreadState.get().getCurrentRoutine();
    System.err.printf("Traceback (most recent call last, %s):%n", routine.getName());
    ThreadState ts = ThreadState.get();
    SPLTraceBackObject trace = ts.getTrace();
    assert trace != null;
    while (trace != null) {
      System.err.println(trace.getErrorMessage());
      trace = trace.getNext();
    }
    System.err.println(ts.getExecVal().getType().getName() + ":" + ts.getExecVal().getMsg());
    System.err.print("\033[0m");
  }

  public SPLObject run() {
    SPLConfiguration build = SPLConfigBuilder.build();
    SPLInternalWorld world = new SPLInternalWorld(build);
    SPLInternalWorld.splWorld = world;
    world.boot(frame);
    return null;
  }

  public SPLCodeObject getCode() {
    return code;
  }


  public SPLFrameObject getFrame() {
    return frame;
  }


  public void dis() {
    Dissembler dissembler = new Dissembler(code);
    dissembler.prettyPrint();
  }

  public String getFilename() {
    return filename;
  }
}
