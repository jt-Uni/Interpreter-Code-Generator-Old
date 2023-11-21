grammar SimpleLang;

prog: dec+ EOF ;
dec:	Type Idfr '(' vardec ')' body ;
vardec:	(Type Idfr (',' Type Idfr)*)? ;
body:   '{' (Type Idfr ':=' exp ';')* ene '}';
block:	'{' ene '}';
ene:    exp (';' exp)*;

exp
:   BoolLit                             # BoolExpr
|	Idfr                                # IdExpr
|	IntLit                              # IntExpr
|	Idfr ':=' exp                       # AssignExpr
|	'(' exp op=BinOP exp ')'            # BinOpExpr
|	Idfr '(' args ')'                   # CallFunExpr
|	block                               # BlockExpr
|	'if' exp 'then' block 'else' block  # IfExpr
|	'while' exp 'do' block              # WhileExpr
|	'repeat' block 'until' exp          # ForExpr
|	'print' exp                         # PrintExpr
|	'space'                             # SpaceExpr
|	'newline'                           # NewlineExpr
|	'skip'                              # SkipExpr
;

args:	(exp (',' exp)*)?;
BinOP
:   Eq
|   Less
|   Greater
|   LessEq
|   GreaterEq
|	Plus
|   Minus
|   Times
|   Divide
|   And
|   Or
|   Caret
;

BoolLit:	'true' | 'false';
Type:	'int' | 'bool' | 'unit';
Idfr:	[a-z][a-zA-Z0-9_]*;
IntLit:	'0' | ('-'? [1-9][0-9]*);

Space: 'space';
NewLine: 'newline';
Eq: '==';
Less: '<';
Greater: '>';
LessEq: '<=';
GreaterEq: '>=';
Plus: '+';
Minus: '-';
Times: '*';
Divide: '/';
And: '&';
Or: '|';
Caret: '^';

WS     : [ \n\r\t]+ -> skip ;