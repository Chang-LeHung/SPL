package org.spl.vm.internal.shell;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.DefaultASTContext;
import org.spl.compiler.parser.SPLParser;
import org.spl.vm.internal.SPLCodeObjectBuilder;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.interpreter.DefaultEval;
import org.spl.vm.objects.SPLNoneObject;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLStringObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InteractiveShell {
  private final static String filename = "anonymous";
  private static boolean multiLine = false;

  private static int newLineCount(String line) {
    return line.split("\n").length;
  }

  public static SPLCodeObject compile(String content) throws SPLSyntaxError, IOException {
    SPLParser parser = new SPLParser(filename, content);
    IRNode<Instruction> ir = parser.buildAST();
    DefaultASTContext<Instruction> context = parser.getContext();
    context.generateByteCodes(ir);
    return SPLCodeObjectBuilder.build(context);
  }

  public static void main(String[] args) throws IOException {
    System.out.print("\033[31m");
    System.out.print("Welcome to Project SPL which is a comprehensive compiler and interpreter implementation for educational purpose. (Exit shell by `exit()`)");
    System.out.println("\033[30m");
    String prompt = "Written by Chang-LeHung.(https://github.com/Chang-LeHung)";
    System.out.print("\033[33m");
    System.out.println(prompt);
    System.out.print("\033[0m");
    System.out.print("\033[32m");
    prompt = """
         ______     ______   __       \s
        /\\  ___\\   /\\  == \\ /\\ \\      \s
        \\ \\___  \\  \\ \\  _-/ \\ \\ \\____ \s
         \\/\\_____\\  \\ \\_\\    \\ \\_____\\\s
          \\/_____/   \\/_/     \\/_____/\s
                                      \s""";
    System.out.println(prompt + "\033[0m");
    HashMap<SPLObject, SPLObject> locals = new HashMap<>();
    locals.put(new SPLStringObject("PS1"), new SPLStringObject("$ "));
    Terminal terminal = TerminalBuilder.terminal();
    terminal.echo(false);
    LineReader reader = LineReaderBuilder.builder()
        .terminal(terminal)
        .build();
    StringBuilder builder = new StringBuilder();
    reader.setOpt(LineReader.Option.INSERT_TAB);
    while (true) {
      try {
        builder.delete(0, builder.length());
        label:
        while (true) {
          String line;
          prompt = locals.get(new SPLStringObject("PS1")).__str__().toString();
          if (!multiLine)
            line = reader.readLine(prompt);
          else
            line = reader.readLine();
          switch (line) {
            case "begin":
              multiLine = true;
              System.out.println("\033[32mBegin multi-line mode \033[0m\033[31m(Without Prompt)\033[0m");
              break;
            case "end":
              multiLine = false;
              System.out.println("\033[32mEnd multi-line mode\033[0m");
              break label;
            case "exit":
              System.out.println("\033[32mExiting...\033[0m");
              System.exit(0);
              break;
            case "clear":
              System.out.println("\033[H\033[2J");
            case "vars":
              PrettyPrinter printer = new PrettyPrinter();
              printer.setHeader(List.of("Variable", "Type", "Value"));
              for (var key : locals.keySet()) {
                SPLObject val = locals.get(key);
                String name = key.__str__().toString();
                if (val instanceof SPLStringObject s) {
                  printer.addRow(List.of(name, val.getType().getName(), "\"" + s.__str__().toString() + "\""));
                } else {
                  printer.addRow(List.of(name, val.getType().getName(),val.__str__().toString()));
                }
              }
              printer.print();
              break;
            default:
              builder.append(line);
              if (!multiLine)
                break label;
              builder.append("\n");
              break;
          }
        }
        String line = builder.toString();
        if (line.length() == 0)
          continue;
        else if (locals.containsKey(new SPLStringObject(line))) {
          System.out.println(locals.get(new SPLStringObject(line)).__str__());
          continue;
        } else if (newLineCount(line) == 1) {
          try {
            String command = line.trim();
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor(1000, TimeUnit.MILLISECONDS);
            if (process.exitValue() == 0) {
              BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
              while ((line = out.readLine()) != null) {
                System.out.println(line);
              }
              continue;
            }
          } catch (Exception ignore) {
          }
        }
        SPLCodeObject code = compile(line);
        DefaultEval defaultEval = new DefaultEval(filename, locals, locals, code);
        SPLObject res = defaultEval.evalFrame();
        if (res != SPLNoneObject.getInstance())
          System.out.println(res.__str__());
      } catch (Exception e) {
        System.err.println("\033[31m" + e.getMessage() + "\033[0m");
      }
    }
  }
}