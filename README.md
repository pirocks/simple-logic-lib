# simple-logic-lib
A simple logic library. Currently a work in progress. Jars on Maven central should be working/useable.

####Current Features:
- A library for manipulating first order logic expressions. 
- Outputting to mathml/html
- Outputing to prefix notation/easily parseable string form
- Performing fairly simple pattern matching and pattern based rewriting. 
- Equals/hashcode implementation which handles bound variables correctly.
- Natural deduction proof verification.
- Kotlin DSL builder syntax for natural deduction proofs. 
- Infix builder functions for logic


####Maven Dependency
```xml
<dependency>
  <groupId>io.github.pirocks</groupId>
  <artifactId>simple-logic-lib</artifactId>
  <version>0.0.7</version>
</dependency>
```
 
####Feature wishlist/in progress
- Html/mathml output for natural deduction
- Ability to create custom rules for natural deduction
- Better mathml output/compatibility with more browsers
- Improved documentation 
- Usage Examples
- Compatibility/interop with simple-algebra-lib
