package org.eclipse.edt.ide.core.internal.errors;
import java.io.IOException;

%%
%class ErrorLexer
%implements IErrorLexer
%type int
%public
%ignorecase
%line
%column
%unicode

%eofval{
	if(yystate() == SQL || yystate() == SQLDELIMITEDID || yystate() == SQLSTRING) {
		yybegin(YYINITIAL);
		return ErrorNodeTypes.ERRORSQLSTMTLIT;
	}
	else if(yystate() == STRING) {
		yybegin(YYINITIAL);
		return ErrorNodeTypes.ERRORSTRING;
	}
	else if(yystate() == BLOCKCOMMENT) {
		yybegin(YYINITIAL);
		return ErrorNodeTypes.ERRORBLOCKCOMMENT;
	}
	else {
		return ErrorNodeTypes.EOF;
	}
%eofval}

InputCharacter = [^\r\n]
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
Identifier     = ([:jletter:]|_)[:jletterdigit:]*
Integer        = [0-9]+
BigintLit = {Integer}[I]
SmallintLit = {Integer}[i]
DecimalLit     = ({DLit1}|{DLit2})
DLit1          = [0-9]+ \. [0-9]*	// Has integer part and the dot
DLit2          = \. [0-9]+			// Has decimal part and the dot
FLit1	  = ({DecimalLit}|{Integer})[eE][+-]?{Integer}
FLit2	  = ({DecimalLit}|{Integer})[F]
FloatLit       = ({FLit1}|{FLit2})
SmallfloatLit  = ({DecimalLit}|{Integer})[f]
BytesLit       = 0[xX][012334567890aAbBcCdDeEfF]+
SQLIdentifier  = [:jletter:][:jletterdigit:]*


/* comments */
Comment = {TraditionalComment} | {EndOfLineComment}

TraditionalComment   = "/*" ~"*/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?


/* SQL comments */
SQLComment		= "--" {InputCharacter}* {LineTerminator}?

%{
	protected StringBuffer string = new StringBuffer();
	protected int curOffset = 0;
	protected boolean isSQLCondition;
        
	public TerminalNode next() {
		try {
			int type = yylex();
			TerminalNode result;
			if(type == ErrorNodeTypes.STRING || type == ErrorNodeTypes.SQLSTMTLIT) {
				result = new TerminalNode(type, string.toString(), yyline, yycolumn, curOffset); 
				curOffset += string.length();
			}
			else {
				result =  new TerminalNode(type, yytext(), yyline, yycolumn, curOffset);
				curOffset += yylength();
			}
			return result;
		} catch (IOException e) {
			// Should never happen
			e.printStackTrace();
			return null;
		}
	}
%}

%state STRING
%state SQL
%state SQLDELIMITEDID
%state SQLSTRING
%state BLOCKCOMMENT

/*
 * If you change the keywords below, you will need to also change
 * com.ibm.etools.egl.EGLKeywordHandler
 */

%%
<YYINITIAL> {
	"absolute"		    { return ErrorNodeTypes.ABSOLUTE; }
	"add"				{ return ErrorNodeTypes.ADD; }
	"all"				{ return ErrorNodeTypes.ALL; }
	"as"				{ return ErrorNodeTypes.AS; }
	"by"				{ return ErrorNodeTypes.BY; }
	"byname"			{ return ErrorNodeTypes.BYNAME; }
	"byposition"		{ return ErrorNodeTypes.BYPOSITION; }
	"call"				{ return ErrorNodeTypes.CALL; }
	"case"				{ return ErrorNodeTypes.CASE; }
	"class"				{ return ErrorNodeTypes.CLASS; }
	"close"			 	{ return ErrorNodeTypes.CLOSE; }
	"const"			 	{ return ErrorNodeTypes.CONST; }
	"constructor"		{ return ErrorNodeTypes.CONSTRUCTOR; }
	"continue"			{ return ErrorNodeTypes.CONTINUE; }
	"current"			{ return ErrorNodeTypes.CURRENT; }
	"decrement"			{ return ErrorNodeTypes.DECREMENT; }
	"delegate"			{ return ErrorNodeTypes.DELEGATE; }
	"delete"			{ return ErrorNodeTypes.DELETE; }
	"else"				{ return ErrorNodeTypes.ELSE; }
	"end"			 	{ return ErrorNodeTypes.END; }
    "enumeration"       { return ErrorNodeTypes.ENUMERATION; }
	"execute"			{ return ErrorNodeTypes.EXECUTE; }
	"exit"				{ return ErrorNodeTypes.EXIT; }
	"extends"			{ return ErrorNodeTypes.EXTENDS; }
	"externalType"		{ return ErrorNodeTypes.EXTERNALTYPE; }
	"first"				{ return ErrorNodeTypes.FIRST; }
	"for"			 	{ return ErrorNodeTypes.FOR; }
	"foreach"			{ return ErrorNodeTypes.FOREACH; }
	"forupdate"			{ return ErrorNodeTypes.FORUPDATE; }
	"from"				{ return ErrorNodeTypes.FROM; }
	"function"		    { return ErrorNodeTypes.FUNCTION; }
	"get"			 	{ return ErrorNodeTypes.GET; }
	"goto"				{ return ErrorNodeTypes.GOTO; }
	"handler"			{ return ErrorNodeTypes.HANDLER; }
	"hold"				{ return ErrorNodeTypes.HOLD; }
	"if"				{ return ErrorNodeTypes.IF; }
	"implements"		{ return ErrorNodeTypes.IMPLEMENTS; }
	"import"			{ return ErrorNodeTypes.IMPORT; }
	"in"				{ return ErrorNodeTypes.IN; }
	"insert"			{ return ErrorNodeTypes.INSERT; }
	"interface"			{ return ErrorNodeTypes.INTERFACE; }
	"into"				{ return ErrorNodeTypes.INTO; }
	"inout"			 	{ return ErrorNodeTypes.INOUT; }
	"inparent"		    { return ErrorNodeTypes.INPARENT; }
	"is"				{ return ErrorNodeTypes.IS; }
	"isa"				{ return ErrorNodeTypes.ISA; }
	"label"			 	{ return ErrorNodeTypes.LABEL; }
	"last"				{ return ErrorNodeTypes.LAST; }
	"library"			{ return ErrorNodeTypes.LIBRARY; }
	"move"				{ return ErrorNodeTypes.MOVE; }
	"next"				{ return ErrorNodeTypes.NEXT; }
	"new"			 	{ return ErrorNodeTypes.NEW; }	
	"null"			 	{ return ErrorNodeTypes.NULL; }
	"nocursor"			{ return ErrorNodeTypes.NOCURSOR; }
	"not"				{ return ErrorNodeTypes.NOT; }
	"onexception"		{ return ErrorNodeTypes.ONEXCEPTION; }
	"open"				{ return ErrorNodeTypes.OPEN; }
	"otherwise"			{ return ErrorNodeTypes.OTHERWISE; }
	"out"			 	{ return ErrorNodeTypes.OUT; }
	"package"			{ return ErrorNodeTypes.PACKAGE; }
	"prepare"			{ return ErrorNodeTypes.PREPARE; }
	"previous"		    { return ErrorNodeTypes.PREVIOUS; }
	"private"			{ return ErrorNodeTypes.PRIVATE; }
	"program"			{ return ErrorNodeTypes.PROGRAM; }
	"record"			{ return ErrorNodeTypes.RECORD; }
	"relative"		    { return ErrorNodeTypes.RELATIVE; }
	"replace"			{ return ErrorNodeTypes.REPLACE; }
	"return"			{ return ErrorNodeTypes.RETURN; }
	"returns"			{ return ErrorNodeTypes.RETURNS; }
	"returning"			{ return ErrorNodeTypes.RETURNING; }
	"rununit"		 	{ return ErrorNodeTypes.RUNUNIT; }
	"scroll"			{ return ErrorNodeTypes.SCROLL; }
	"service"			{ return ErrorNodeTypes.SERVICE; }
	"set"				{ return ErrorNodeTypes.SET; }
	"singlerow"			{ return ErrorNodeTypes.SINGLEROW; }
	"stack"			 	{ return ErrorNodeTypes.STACK; }
	"static"			{ return ErrorNodeTypes.STATIC; }
	"super"				{ return ErrorNodeTypes.SUPER; }
	"this"				{ return ErrorNodeTypes.THIS; }
	"throw"				{ return ErrorNodeTypes.THROW; }
	"to"				{ return ErrorNodeTypes.TO; }
	"try"			 	{ return ErrorNodeTypes.TRY; }
	"type"				{ return ErrorNodeTypes.TYPE; }
	"update"			{ return ErrorNodeTypes.UPDATE; }
	"url"				{ return ErrorNodeTypes.URL; }
	"use"				{ return ErrorNodeTypes.USE; }
	"using"				{ return ErrorNodeTypes.USING; }
	"usingKeys"			{ return ErrorNodeTypes.USINGKEYS; }
	"when"				{ return ErrorNodeTypes.WHEN; }
	"while"			 	{ return ErrorNodeTypes.WHILE; }
	"with"				{ return ErrorNodeTypes.WITH; }
	"withv60compat"		{ return ErrorNodeTypes.WITHV60COMPAT; }


        \"                              { yybegin(STRING); string.setLength(0); string.append('\"'); }

		"#sql"{WhiteSpace}*"{"			{ yybegin(SQL); isSQLCondition = false; string.setLength(0); string.append(yytext()); }
		"#sqlcondition"{WhiteSpace}*"{"	{ yybegin(SQL); isSQLCondition = true; string.setLength(0); string.append(yytext()); }

		"/*"							{ yybegin(BLOCKCOMMENT); string.setLength(0); string.append(yytext()); }

	"("					{ return ErrorNodeTypes.LPAREN; }
	")"					{ return ErrorNodeTypes.RPAREN; }
	"!"					{ return ErrorNodeTypes.BANG; }
	","					{ return ErrorNodeTypes.COMMA; }
	";"					{ return ErrorNodeTypes.SEMI; }
	"."					{ return ErrorNodeTypes.DOT; }
	":"					{ return ErrorNodeTypes.COLON; }
	"="					{ return ErrorNodeTypes.ASSIGN; }
	"=="				{ return ErrorNodeTypes.EQ; }
	"&&"				{ return ErrorNodeTypes.AND; }
	"and"				{ return ErrorNodeTypes.AND; }
	"&"					{ return ErrorNodeTypes.BITAND; }
	"&="				{ return ErrorNodeTypes.BITANDEQ; }
	"||"				{ return ErrorNodeTypes.OR; }
	"or"				{ return ErrorNodeTypes.OR; }
	"|"					{ return ErrorNodeTypes.BITOR; }
	"|="				{ return ErrorNodeTypes.BITOREQ; }
	"xor"				{ return ErrorNodeTypes.XOR;	}
	"xor="				{ return ErrorNodeTypes.XOREQ; }
	"!="				{ return ErrorNodeTypes.NE; }
	"<"					{ return ErrorNodeTypes.LT; }
	"<<"				{ return ErrorNodeTypes.LEFTSHIFT; }
	">"					{ return ErrorNodeTypes.GT; }
	">>"				{ return ErrorNodeTypes.RIGHTSHIFTARITHMETIC; }
	">>>"				{ return ErrorNodeTypes.RIGHTSHIFTLOGICAL; }
	"<="				{ return ErrorNodeTypes.LE; }
	"<<="				{ return ErrorNodeTypes.LEFTSHIFTEQ; }
	">="				{ return ErrorNodeTypes.GE; }
	">>="				{ return ErrorNodeTypes.RIGHTSHIFTARITHMETICEQ; }
	">>>="				{ return ErrorNodeTypes.RIGHTSHIFTLOGICALEQ; }
	"+"					{ return ErrorNodeTypes.PLUS; }
	"+="				{ return ErrorNodeTypes.PLUSEQ; }
	"-"					{ return ErrorNodeTypes.MINUS; }
	"-="				{ return ErrorNodeTypes.MINUSEQ; }
	"*"					{ return ErrorNodeTypes.TIMES; }
	"*="				{ return ErrorNodeTypes.TIMESEQ;	}
	"**"				{ return ErrorNodeTypes.TIMESTIMES; }
	"**="				{ return ErrorNodeTypes.TIMESTIMESEQ; }
	"::"				{ return ErrorNodeTypes.CONCAT; }
	"::="				{ return ErrorNodeTypes.CONCATEQ; }
	"?:"				{ return ErrorNodeTypes.NULLCONCAT; }
	"?:="				{ return ErrorNodeTypes.NULLCONCATEQ; }
	"/"					{ return ErrorNodeTypes.DIV; }
	"/="				{ return ErrorNodeTypes.DIVEQ; }
	"%"					{ return ErrorNodeTypes.MODULO; }
	"%="				{ return ErrorNodeTypes.MODULOEQ; }
	"["					{ return ErrorNodeTypes.LBRACKET; }
	"]"					{ return ErrorNodeTypes.RBRACKET; }
	"{"					{ return ErrorNodeTypes.LCURLY; }
	"}"					{ return ErrorNodeTypes.RCURLY; }
	"@"					{ return ErrorNodeTypes.AT; }
	"?"					{ return ErrorNodeTypes.QUESTION; }
	"??"				{ return ErrorNodeTypes.QUESTIONQUESTION; }
	"~"					{ return ErrorNodeTypes.NEGATE; }

        // Macros
        {Identifier}                    { return ErrorNodeTypes.ID; }
        {Integer}                       { return ErrorNodeTypes.INTEGER; }
        {BigintLit}						{ return ErrorNodeTypes.BIGINTLIT; }
		{SmallintLit}					{ return ErrorNodeTypes.SMALLINTLIT; }
        {DecimalLit}                    { return ErrorNodeTypes.DECIMALLIT; }
        {FloatLit}                      { return ErrorNodeTypes.FLOATLIT; }
        {SmallfloatLit}                 { return ErrorNodeTypes.SMALLFLOATLIT; }
        {BytesLit}						{ return ErrorNodeTypes.BYTESLIT; }
        {WhiteSpace}*                   { return ErrorNodeTypes.WS; }
        {Comment}                       { return ErrorNodeTypes.WS; }
}

<BLOCKCOMMENT> {
		"*/"							{ yybegin(YYINITIAL); string.append(yytext()); return ErrorNodeTypes.WS; }
		[^*]*							{ string.append(yytext()); }
		.								{ string.append(yytext()); }
}		

<STRING> {
        \"                              { yybegin(YYINITIAL); string.append('\"'); return ErrorNodeTypes.STRING; }
        
        [^\\\"\r\n]+					{ string.append(yytext()); }
        \\\"							{ string.append("\\\""); }
        \\\'							{ string.append("\\\'"); }
        \\\\							{ string.append("\\\\"); }
        \\[b]							{ string.append("\\b"); }
        \\[f]							{ string.append("\\f"); }
        \\[n]							{ string.append("\\n"); }
        \\[r]							{ string.append("\\r"); }
        \\[t]							{ string.append("\\t"); }
        \\								{ string.append("\\"); }

        {LineTerminator}				{ yybegin(YYINITIAL); string.append(yytext()); return ErrorNodeTypes.ERRORSTRING; }
}

<SQL> {
		"}"								{ yybegin(YYINITIAL); string.append(yytext()); return isSQLCondition ? ErrorNodeTypes.SQLCONDITION : ErrorNodeTypes.SQLSTMTLIT; }
		{SQLIdentifier}					{ string.append(yytext()); }
		{WhiteSpace}*					{ string.append(yytext()); }
		{SQLComment}					{ string.append(yytext()); }
		
		\"								{ yybegin(SQLDELIMITEDID); string.append('\"'); }
		\'								{ yybegin(SQLSTRING); string.append('\''); }
		.								{ string.append(yytext()); }
}

<SQLDELIMITEDID> {
		\"								{ yybegin(SQL); string.append('\"'); }
		\"\"							{ string.append("\"\""); }
		[^\"\r\n]+						{ string.append(yytext()); }
		
		{LineTerminator}				{ yybegin(SQL); string.append(yytext()); }
}

<SQLSTRING> {
		\'								{ yybegin(SQL); string.append('\''); }
		\'\'							{ string.append("\'\'"); }
		[^\'\r\n]+						{ string.append(yytext()); }
		
		{LineTerminator}				{ yybegin(SQL); string.append(yytext()); }
}

/* No token was found for the input so throw an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^]                    					{ return ErrorNodeTypes.error; }
