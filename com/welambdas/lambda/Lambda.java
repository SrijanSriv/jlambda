package com.welambdas.lambda;

/* imports to use saveLib and readLib
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
*/

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
  private static void run(String source) throws IOException {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    // for (Token token : tokens) {
    //   System.out.println(token);
    // }

    // saveLib("tokens\\test.sre", tokens);
   
    // to check the scanned tokens after getting it from a library
    // List<Token> libknowledge = readLib("tokens\\test.sre");
    // for (Token token : libknowledge) {
    //   System.out.println(token);
    // }

    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();

    if (hadError) return;

    Resolver resolver = new Resolver(interpreter);
    resolver.resolve(statements);
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

  /* funtions to write tokens in a .sre file and read from .sre file
  private static void saveLib(String fileName, List<Token> libtokens) throws IOException {
    List<Token> send = new ArrayList<Token>(libtokens);
    FileOutputStream fout= new FileOutputStream (fileName);
    ObjectOutputStream oos = new ObjectOutputStream(fout);
    oos.writeObject(send);
    fout.close();
  }

  private static List<Token> readLib(String fileName) throws IOException {
    List<Token> obtain = new ArrayList<Token>();
    FileInputStream fin = new FileInputStream (fileName);
    ObjectInputStream ois = new ObjectInputStream(fin);
    try {
      obtain = (ArrayList<Token>)ois.readObject();
    } catch (ClassNotFoundException e) {
      Lambda.error(null, "object not the form of token class.");
    }
    fin.close();
    int lasttoken_index = obtain.size() - 1; // to remove the EOF
    obtain.remove(lasttoken_index);
    return obtain;
  }
  */
}
