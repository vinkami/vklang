# vklang
A programming language made by vinkami.  
This project is separated from the Minecraft plugin [VinkamiLang](https://github.com/vinkami/VinkamiLang). Progresses such as new features and bug fixes will be updated here, while the plugin will only be updated when I feel like it.  
~~Actually both of them are hobby projects so I only update them when I feel like it.~~

## Syntax
```vklang
    val a = 1  // Assign an immutable value
    var b = 2  // Assign a mutable variable
    b = 3      // Reassign a new value to a variable
    
    if (a == 1) {  // If statement. Parentheses and braces are required
        print("a is 1")  // Print function
    } elif (a == 2) {  // Else if statement
        print("a is 2")
    } else {  // Else statement
        print("a is not 1 nor 2")
    }
    
    while (b < 5) {  // While loop
        b += 1
    } complete {  // Complete block. This block will be executed after the loop is finished without breaking
        print("b is now 5")
    }
    
    while (b < 10) {
        b += 1
        if (b == 8) {
            print("b is 8 now")
            break  // Break statement
        }
    } incomplete {  // Incomplete block. This block will be executed after the loop is broken
        print("b is not 10")
    }
```
Exact same code, but with symbols instead of keywords:
```vklang
    $!a = 1  // val
    $b = 2  // var
    b = 3
    
    ?? (a == 1) {  // if 
        print("a is 1")  
    } |? (a == 2) {  // elif
        print("a is 2")
    } || {  // else
        print("a is not 1 nor 2")
    }
    
    ?^ (b < 5) {  // while
        b += 1
    } ?+ {  // complete 
        print("b is now 5")
    }
    
    ?^ (b < 10) {  // while
        b += 1
        ?? (b == 8) {  // if
            print("b is 8 now")
            #<  // break
        }
    } ?- {  // incomplete
        print("b is not 10")
    }
```