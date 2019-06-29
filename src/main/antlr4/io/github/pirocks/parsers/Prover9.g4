grammar Prover9;

OPEN_PAREN : '(';
CLOSE_PAREN : ')';
AND: '&';
OR: '|';
NOT : '-';
FORALL : 'all';
RIGHT_IMPLIES: '->';
LEFT_IMPLIES: '<-';
IFF: '<->';
EQ: '=';
NOT_EQ: '!=';
EXISTS : 'exists';
FALSE : '$F';
TRUE : '$T';
COMMA : ',';
DOT : '.';
INTEGER : [0-9]+;
IDENT : [A-Za-z_][A-Za-z_0-9]*;
WS : [ \t\r\n]+ -> skip ;

top_level_formula : formula '.';

integer: INTEGER;
variable : IDENT;
predicate_name : IDENT;
function_name : IDENT;
string: IDENT;

formula : formula binary_operation formula |
            '-' formula |
            quantifier variable formula |
             '(' formula ')' |
            predicate |
             term equality_related_ops term |
            '$T' |
            '$F' |
            formula '#' attribute;

equality_related_ops : '=' |
                        '!=';

binary_operation : '|' |
                   '&' |
                   '->' |
                   '<-' |
                   '<->';

quantifier :  'all' |
              'exists';

predicate : predicate_name '(' term ( ',' term )* ')';

term : function_name ( '(' term ( ',' term )* ')' ) |
         term BINARY_TERM_OPERATION term |
         '-' term |
         term '\'' | list;

BINARY_TERM_OPERATION : '==' |
                        '<' |
                        '<=' |
                        '>' |
                        '>=' |
                        '+' |
                        '*' |
                        '@' |
                        '/' |
                        '\\' |
                        '^' |
                        'v' ;

list : '[]' |
       '[' term ( ',' term)* (':' term)? ']';

attribute : 'label' '(' string ')' |
            'answer' '(' term ')' |
            'action' '(' term ')' |
            'bsub_hint_wt' '(' integer ')';


