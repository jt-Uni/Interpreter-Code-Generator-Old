grammar SimpleLang;

prog: dec+ EOF ;
dec:	Type Idfr '(' vardec ')' body ;
vardec:	(Type Idfr (',' Type Idfr)*)? ;
body:	'{' (Type Idfr ':=' expr ';')* ene '}';
block:	'{' ene '}';
ene:	expr (';' expr)*;

expr
:   BoolLit                             # BoolExpr
|	Idfr                                # IdExpr
|	IntLit                              # IntExpr
|	Idfr ':=' expr                      # AssignExpr
|	'(' expr op=BinOP expr ')'          # BinOpExpr
|	Idfr '(' args ')'                   # CallFunExpr
|	block                               # BlockExpr
|	'if' expr 'then' block 'else' block # IfExpr
|	'while' expr 'do' block             # WhileExpr
|	'repeat' block 'until' expr         # ForExpr
|	'print' expr                        # PrintExpr
|	'space'                             # SpaceExpr
|	'newline'                           # NewlineExpr
|	'skip'                              # SkipExpr
;

args:	(expr (',' expr)*)?;
BinOP:	'=='  | '<' | '>' | '<='  | '>='
|	 '+' | '-' | '*' | '/' | '&' | '|' | '^';
Type:	'int' | 'bool' | 'unit';
BoolLit:	'true' | 'false';
Idfr:	[a-z][a-zA-Z0-9_]*;
IntLit:	'0' | ('-'? [1-9][0-9]*);
WS     : [ \n\r\t]+ -> skip ;