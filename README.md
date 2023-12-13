# SPL
A comprehensive compiler and interpreter implementation.(Educational Project)

SPL aims to help the enthusiasts of compilers and programming languages to understand the programs better. Now it supports most common grammar.

## Basics

### Loops

#### While loop

```spla = 1
while (a < 10) {
    if ( a== 3) {
        a += 1
        continue
    }
    print(a)
    a += 1
    if (a == 5) {
        break
    }
}
```

#### For loop

```spl
a = 1

for(;a < 10; a += 1) {
    if (a == 3)
        continue
    print(a)
    if (a > 5)
        break
}
```

```spl
data = [1, 2, 3, 4, 5]

for i in data {

    print(i)
}
```

