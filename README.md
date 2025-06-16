The program should in theorey be able to parse an expression such as
```
3x=1
```
and give a result of 0.3333...

This mostly works however for some reason negatives are a bit buggy, meaning that something like
```
10-\frac{5}{2}x^2=0
```
will give the wrong answer but
```
10-(\frac{5}{2}x^2=0
```
will give the correct answer.

Most bugs can be avoided by being strict with brackets. i.e. always use {}, even when not needed, and generally use () on functions.

The algorithms used are ones I've come up with so are probably rubbish, my best attempt to explain them will be below.


If we consider a mathematical expression such as the following

$$3x^2+2x^4 - x = 2$$

We can see that it is simply a string of expressions that are added together. We could create a table to represent this one as it is quite simple, only requiring 3 headers: Coefficient, expression and power. For the earlier example it would look like so:

  

| Coefficient | Expression | power |
| ----------- | ---------- | ----- |
| 3           | x          | 2     |
| 2           | x          | 4     |
| -1          | x          | 1     |
| =           | =          | =     |
| 2           | 1          | 1     |

While this system works fine for simple expressions like the one above, it does become slightly more complicated once brackets and other functions are involved. For instance the equation

$$\frac{1}{2}(3x^2-1)^2=2$$

Cannot simply be evaluated like the table above, instead we need to create a where each node uses the same structure as one record above. From the equation we create the following tree:

From this tree each record needs to be evaluated. It is important to note that a coefficient can contain its own record, allowing for the chaining together of more complex expressions.
