package com.welambdas.lambda;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.welambdas.lambda.TokenType.*;

class Scanner {
// bdc
// bhargav anurag. hate arunav
//> keyword-map
  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("and",    AND);
    keywords.put("class",  CLASS);
    keywords.put("else",   ELSE);
    keywords.put("false",  FALSE);
    keywords.put("for",    FOR);
    keywords.put("fun",    FUN);
    keywords.put("if",     IF);
    keywords.put("nil",    NIL);
    keywords.put("or",     OR);
    keywords.put("print",  PRINT);
    keywords.put("return", RETURN);
    keywords.put("super",  SUPER);
    keywords.put("this",   THIS);
    keywords.put("true",   TRUE);
    keywords.put("var",    VAR);
    keywords.put("while",  WHILE);
    keywords.put("#>",  INCLUDE);
  }
//< keyword-map
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
//> scan-state
  private int start = 0;
  private int current = 0;
  private int line = 1;
//< scan-state

  Scanner(String source) {
    this.source = source;
  }
//> scan-tokens
  List<Token> scanTokens() {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexeme.
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }
//< scan-tokens
//> scan-token
  private void scanToken() {
    char c = advance();
    switch (c) {
      case '#':
        fileIncluder(match('>') ? INCLUDE : IDENTIFIER);
        break;
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '%': addToken(MODULO); break;
      case ';': addToken(SEMICOLON); break;
      case '*': addToken(STAR); break;
      case '-':
        addToken(match('-') ? MINUS_MINUS : MINUS);
        break;
      case '+':
        addToken(match('+') ? PLUS_PLUS : PLUS);
        break;
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
//> slash
      case '/':
        if (match('/')) {
          // A comment goes until the end of the line.
          while (peek() != '\n' && !isAtEnd()) advance();
        } else {
          addToken(SLASH);
        }
        break;
//< slash
//> whitespace

      case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace.
        break;

      case '\n':
        line++;
        break;
//< whitespace
//> string-start

      case '"': string(); break;
//< string-start
//> char-error

      default:
        // Lambda.error(line, "Unexpected character.");
//> digit-start
        if (isDigit(c)) {
            number();
//> identifier-start
        } else if (isAlpha(c)) {
            identifier();
//< identifier-start
        } else {
            Lambda.error(line, "Unexpected character.");
        }
//< digit-start
        break;
//< char-error
    }
  }
//< scan-token
//> libraryIncluder
private void fileIncluder(TokenType type) {
  if (type == IDENTIFIER) {
    Lambda.error(line, "Hash usecase: to include files only");
    return;
  }
  List<Token> importedTokens = new ArrayList<Token>();
  while (isAlphaNumeric(peek())) advance();
  String text = source.substring(start + 2, current);
  String importLib = "tokens\\" + text + ".sre";
  try {
    importedTokens = readLib(importLib);
  } catch (IOException e) {
    Lambda.error(null, text + ": not a tokenized library");
  }
  for (Token token : importedTokens) {
    // System.out.println(token);
    addToken(token.type, token.literal, token.lexeme);
  }
}
//< libraryIncluder
//> identifier
  private void identifier() {
    while (isAlphaNumeric(peek())) advance();
//> keyword-type
    String text = source.substring(start, current);
    TokenType type = keywords.get(text);
    if (type == null) type = IDENTIFIER;
    addToken(type);
//< keyword-type
  }
//< identifier
//> number
  private void number() {
    while (isDigit(peek())) advance();

    // Look for a fractional part.
    if (peek() == '.' && isDigit(peekNext())) {
      // Consume the "."
      advance();

      while (isDigit(peek())) advance();
    }

    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }
//< number
//> string
  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++;
      advance();
    }

    if (isAtEnd()) {
        Lambda.error(line, "Unterminated string.");
        return;
    }

    // The closing ".
    advance();

    // Trim the surrounding quotes.
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }
//< string
//> match
  private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;

    current++;
    return true;
  }
//< match
//> peek
  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }
//< peek
//> peek-next
  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  } // [peek-next]
//< peek-next
//> is-alpha
  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
            (c >= 'A' && c <= 'Z') ||
            c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }
//< is-alpha
//> is-digit
  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  } // [is-digit]
//< is-digit
//> is-at-end
  private boolean isAtEnd() {
    return current >= source.length();
  }
//< is-at-end
//> advance-and-add-token
  private char advance() {
    return source.charAt(current++);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }
  private void addToken(TokenType type, Object literal, String lexeme) {
    tokens.add(new Token(type, lexeme, literal, line));
  }
//< advance-and-add-token
  private static List<Token> readLib(String fileName) throws IOException {
    List<Token> obtain = new ArrayList<Token>();
    FileInputStream fin = new FileInputStream (fileName);
    ObjectInputStream ois = new ObjectInputStream(fin);
    try {
      obtain = (ArrayList<Token>)ois.readObject();
    } catch (ClassNotFoundException e) {
      Lambda.error(null, "object not the form of token class.");
    }
    int lasttoken_index = obtain.size() - 1; // to remove the EOF
    obtain.remove(lasttoken_index);
    fin.close();
    return obtain;
  }
}
