package com.welambdas.lambda;

import java.util.List;
import java.util.Map;

class LambdaClass implements LambdaCallable {
  final String name;
  final LambdaClass superclass;
  private final Map<String, LambdaFunction> methods;

  LambdaClass(String name, LambdaClass superclass, Map<String, LambdaFunction> methods) {
    this.superclass = superclass;
    this.name = name;
    this.methods = methods;
  }
  LambdaFunction findMethod(String name) {
    if (methods.containsKey(name)) {
      return methods.get(name);
    }
    if (superclass != null) {
      return superclass.findMethod(name);
    }

    return null;
  }

  @Override
  public String toString() {
    return name;
  }
  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    LambdaInstance instance = new LambdaInstance(this);
    LambdaFunction initializer = findMethod("init");
    if (initializer != null) {
      initializer.bind(instance).call(interpreter, arguments);
    }
    return instance;
  }
  @Override
  public int arity() {
    LambdaFunction initializer = findMethod("init");
    if (initializer == null) return 0;
    return initializer.arity();

  }
}
