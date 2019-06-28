grammar Prover9Parser;

OPEN_PAREN : '(';
CLOSE_PAREN : ')';
AND: '&';
OR: 'or';
NOT : 'neg';
FORALL : 'all';
IMPLIES: 'implies';
IFF: 'iff';
EXISTS : 'exists';
FALSE : 'F';
TRUE : 'T';
COMMA : ',';
DOT : '.';
IDENT : [A-Za-z_][A-Za-z_0-9]*;
WS : [ \t\r\n]+ -> skip ;

root : formula '.''

formula: and | or | not | forall | implies | iff | exists | false_ | true_ | predicateAtom;

and: '(' formula AND formula ')';
or: '(' 'or' formula formula ')';
not: '(' 'not' formula formula ')';
implies: '(' 'implies' formula formula ')';
iff: '(' 'iff' formula formula ')';
false_: 'F';
true_: 'T';
forall: 'forall';
exists: 'exists';
predicateAtom : IDENT '(' (IDENT (',' IDENT)*)? ')';

/**



top_level_formula ::= formula "."

formula ::= formula binary_operation formula | "-" formula |
            quantifier variable formula | "(" formula ")" |
            predicate | term equality_related_ops term |
            "$T" | "$F" |
            formula "#" attribute

equality_related_ops ::= "=" | "!="

binary_operation ::= "|" | "&" | "->" | "<-" | "<->"

quantifier ::= "all" | "exists"

predicate ::= predicate_name "(" term { "," term }* ")"



term ::= function_name { "(" term { "," term }* ")" } |
         term binary_term_operation term |
         prefix_term_operation term |
         term postfix_term_operation | list

binary_term_operation ::= "==" | "<" | "<=" | ">" | ">=" |
                          "+" | "*" | "@" | "/" | "\" | "^" | "v"
% Note: Lacks infix "-".

prefix_term_operation ::= "-"
% Note that prefix "-" can form either a formula or a term.


postfix_term_operation ::= "'"

list ::= "[]" |
         "[" term { "," term}* [":" term] "]"
% list notation is shorthand for $cons(...).

attribute ::= "label" "(" string ")" |
              "answer" "(" term ")" |
              "action" "(" term ")"
              "bsub_hint_wt" "(" integer ")"

*/