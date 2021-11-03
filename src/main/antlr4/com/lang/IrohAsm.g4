grammar IrohAsm;
main: line* | EOF;

line: (rangedec | instruction | comment | labelmarker)? EOL;

instruction: mnemonic (firstoperand COMMA)? secondoperand;
rangedec : range assignment?;
firstoperand : range | mem | register;
secondoperand : range | mem | imm | register | label;
label : identifier;
range : identifier OPENBRACKETS imm CLOSEDBRACKETS;
assignment : EQUALS OPENCURL imm (COMMA imm)* CLOSECURL;
labelmarker : identifier COLON;
mem : AT imm;
imm : IMM;
register : REGISTER;
comment : '#' ~EOL*;
mnemonic : MNEMONIC;
identifier: IDENTIFIER;

WHITESPACE : (' ' | '\t') -> skip ;

// remember to append \n to input
EOL : '\r'? '\n';

OPENCURL : '{';
CLOSECURL : '}';
OPENBRACKETS : '[';
CLOSEDBRACKETS : ']';
COMMA : ',';
COLON : ':';
EQUALS : '=';
AT : '@';
MNEMONIC : ('jmp' | 'add' | 'sub' | 'jez' | 'mov' | 'wrt' | 'get');
REGISTER: ('ab' | 'bb' | 'cb' | 'db');
IMM: DIGITS RADIX?;
RADIX : ('d' | 'b' | 'h');
DIGITS : [0-9]+;
IDENTIFIER: ([a-z0-9] | '$' | '_' | '\u00C0'..'\uFFFF')+ ;