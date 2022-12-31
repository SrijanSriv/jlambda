package com.welambdas.lambda;

import java.util.List;

class LambdaFunction implements LambdaCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    LambdaFunction(Stmt.Function declaration, Environment closure, boolean Initializer) {
      this.declaration = declaration;
      this.closure = closure;
      this.isInitializer = Initializer;
    }
    LambdaFunction bind(LambdaInstance instance) {
      Environment environment = new Environment(closure);
      environment.define("this", instance);
      return new LambdaFunction(declaration, environment, isInitializer);
    }

    @Override
    public String toString() {
      return "<fn " + declaration.name.lexeme + ">";
    }

    @Override
    public int arity() {
      return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
      Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
        environment.define(declaration.params.get(i).lexeme,
            arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
          } catch (Return returnValue) {
            if (isInitializer) return closure.getAt(0, "this");
            return returnValue.value;
          }
          if (isInitializer) return closure.getAt(0, "this");
        return null;
    }
}
