grammar PrefixNotation;

OPEN_PAREN : '(';
CLOSE_PAREN : ')';
AND: 'and';
OR: 'or';
NOT : 'neg';
FORALL : 'all';
IMPLIES: 'implies';
IFF: 'iff';
EXISTS : 'exists';
FALSE : 'F';
TRUE : 'T';
COMMA : ',';
IDENT : [A-Za-z_][A-Za-z_0-9]*;
WS : [ \t\r\n]+ -> skip ;

formula: and | or | not | forall | implies | iff | exists | false_ | true_ | predicateAtom;

and: '(' 'and' formula formula ')';
or: '(' 'or' formula formula ')';
not: '(' 'not' formula ')';
implies: '(' 'implies' formula formula ')';
iff: '(' 'iff' formula formula ')';
false_: 'F';
true_: 'T';
forall: '(' 'forall' IDENT formula')';
exists: '(' 'exists' IDENT formula ')';
predicateAtom : '(' IDENT '(' IDENT* ')' ')';