package com.welambdas.lambda;

import java.util.List;

interface LambdaCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
