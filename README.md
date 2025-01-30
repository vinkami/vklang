# vklang
A programming language made by vinkami.  
This project is separated from the Minecraft plugin [VinkamiLang](https://github.com/vinkami/VinkamiLang). Progresses such as new features and bug fixes will be updated here, while the plugin will only be updated when I feel like it.  
~~Actually both of them are hobby projects so I only update them when I feel like it.~~

## Syntax
```vklang
    val a = 1  # Assign an immutable value
    var b = 2  # Assign a mutable variable
    b = 3      # Reassign a new value to a variable
    
    if (a == 1) {  # If statement. Parentheses and braces are required
        print("a is 1")  # Print function
    } elif (a == 2) {  # Else if statement
        print("a is 2")
    } else {  # Else statement
        print("a is not 1 nor 2")
    } else {
        print("a is not 1")
    }
    ? (b == 2) {  # If statement but with symbols. Both are equivalent and interchangeable
        print("b is 2")
    } |? (b == 3) {
        print("b is 3")
    } || {
        print("b is neither 2 nor 3")
    }
    
    while (a < 5) {  # While loop
        print(a)
        a += 1
    } complete {  # Complete block. This block will be executed after the loop is finished without breaking
        print("a is now 5")
    }
    
    while (b < 5) {
        print(b)
        b += 1
        if (b == 3) {
            break  # Break statement
        }
    } incomplete {  # Incomplete block. This block will be executed after the loop is broken
        print("b is not 5")
    }
```