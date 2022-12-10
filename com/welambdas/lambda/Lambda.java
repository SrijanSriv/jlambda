package com.welambdas.lambda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lambda {
  private static final Interpreter interpreter = new Interpreter();
  static boolean hadError = false;
  static boolean hadRuntimeError = false;
  public static void main(String[] args) throws IOException {
    String fileExt = "orz";
    if (args.length > 1) {
      System.out.println("Usage: lambda [script]");
      System.exit(64);

    } else if (args.length == 1) {
      String fileName = args[0];
      int i = fileName.lastIndexOf('.');
      String ext = fileName.substring(i + 1);

      if (i > 0 && ext.compareTo(fileExt) == 0) {
          runFile(fileName);
        } else {
          System.out.println("Error: incorrect file extension: '.orz' not found");
          System.exit(64);
        }

    } else {
      runPrompt();
    }
  }
  private static void runFile(String path) throws IOException {
    if (hadError) System.exit(65);
    if (hadRuntimeError) System.exit(70);
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));
  }
  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) { 
      System.out.print("/\\~> ");
      String line = reader.readLine();
      if (line == null) break;
      run(line);
      hadError = false;
    }
  }
  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();

    if (hadError) return;

    interpreter.interpret(statements);
  }

  static void error(int line, String message) {
    report(line, "", message);
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
    hadRuntimeError = true;
  }

  private static void report(int line, String where, String message) {
    System.err.println("[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }

  static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }
}
