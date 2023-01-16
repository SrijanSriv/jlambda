# Lambda

The origin of the Lambda Programming Language. Build using Java for experimentation and ease of understanding.

## The Language

You can download the language from the releases section! Just head to the Releases section and download the `.jar`. You can run a file in the `.orz` extension in the same directory where the `.jar` is saved by the command:

``` java
java -jar .\lambda.jar main.orz
```

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

#### Classes

``` jlambda
class Greeter {
  init(name, time) {
    this.name = name;
    this.time = time;
  }
  greet() {
    print "Good " + this.time + " " + this.name + "!";
  }
}
```
