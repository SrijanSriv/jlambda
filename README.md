# Lambda

The origin of the Lambda Programming Language. Build using Java for experimentation and ease of understanding.

## The Language

### Examples

#### Hello world

```jlambda
// The famous hello world
print "Hello, lambdas!";
```

#### Variable declaration

```jlambda
var a = 300;
var b = 6;

var c = a + b;
print c; // prints 306
```

#### Loops

```jlambda
for (var i = 0; i < 5; i = i+1) {
  print i;
}

// prints 
// 0
// 1
// 2
// 3
// 4
```

#### Conditionals

```jlambda
var a = 5;
if (a >= 4) {
  print "a is greater than or equal to 4";
} else {
  print "a is less than 4";
}
```

#### Functions

``` jlambda
fun printSum(a, b) {
  print a + b;
}
```
