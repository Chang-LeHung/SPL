package org.spl.vm.interpreter;

import org.spl.compiler.SPLCompiler;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.ASTVisualizer;
import org.spl.vm.internal.shell.InteractiveShell;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

public class SPLMain {

  private final String[] args;

  public SPLMain(String[] args) {
    this.args = args;
  }

  public static void main(String[] args) {
    SPLMain spl = new SPLMain(args);
    spl.start();
  }

  private String matchAndFetchNext(String pattern) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].matches(pattern) && i + 1 < args.length) {
        return args[i + 1];
      }
    }
    return null;
  }

  private String findFileWithEnding(String pattern) {
    for (String arg : args) {
      if (arg.endsWith(pattern)) {
        return arg;
      }
    }
    return null;
  }

  public void saveStringToFile(String content, String filename) throws IOException {
    File file = new File(filename);
    if (!file.exists()) {
      File parentFile = file.getParentFile();
      if (parentFile != null && !parentFile.exists()) {
        parentFile.mkdirs();
      }
      file.createNewFile();
    }
    try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
      writer.write(content);
    }
  }

  private File createFile(String filename) throws IOException {
    File file = new File(filename);
    if (!file.exists()) {
      if (!file.getParentFile().exists()) {
        file.getParentFile().createNewFile();
      }
      file.createNewFile();
    }
    return file;
  }

  public void start() {
    if (args.length == 1) {
      try {
        SPL spl = new SPL(args[0]);
        spl.run();
        if (SPLInternalWorld.splWorld.hasError) {
          Runtime.getRuntime().exit(-1);
        }
      } catch (SPLSyntaxError | IOException e) {
        // output red font words
        System.err.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        Runtime.getRuntime().exit(-1);
      } finally {
        Runtime.getRuntime().exit(0);
      }
    } else if (args.length == 0) {
      try {
        InteractiveShell.main(null);
      } catch (IOException e) {
        System.err.println(e.getMessage());
        System.exit(-1);
      }
    }
    HashSet<String> comArgs = new HashSet<>(List.of(args));
    if (comArgs.contains("-v")) {
      if (comArgs.contains("-o")) {
        String filename = matchAndFetchNext("-o");
        if (filename == null) {
          System.err.println("No output file specified");
          Runtime.getRuntime().exit(-1);
        }
        String file = findFileWithEnding(".spl");
        if (file != null) {
          try {
            SPLCompiler compiler = new SPLCompiler(file);
            compiler.compile();
            ASTVisualizer vis = new ASTVisualizer(compiler.getIr(), file);
            String content = vis.getDotFileContent();
            saveStringToFile(content, filename);
          } catch (IOException | SPLSyntaxError e) {
            System.err.println(e.getMessage());
            Runtime.getRuntime().exit(-1);
          }
        } else {
          System.err.println("No arguments with .spl extension found, please input a spl file");
        }
      }
    }
  }
}
