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
	else if(yystate() == DLI || yystate() == DLI_QUOTED_ID || yystate() == DLI_DBL_QUOTED_ID) {
		yybegin(YYINITIAL);
		return ErrorNodeTypes.ERROR_INLINE_DLI;
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
DecimalLit     = ({DLit1}|{DLit2})
DLit1          = [0-9]+ \. [0-9]*	// Has integer part and the dot
DLit2          = \. [0-9]+			// Has decimal part and the dot
FloatLit       = ({DecimalLit}|{Integer})[eE][+-]?{Integer}
SQLIdentifier  = [:jletter:][:jletterdigit:]*
DLIIdentifier  = [:jletter:][:jletterdigit:]*


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
			if(type == ErrorNodeTypes.STRING || type == ErrorNodeTypes.SQLSTMTLIT || type == ErrorNodeTypes.INLINE_DLI) {
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

%state DLI
%state DLI_QUOTED_ID
%state DLI_DBL_QUOTED_ID

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
	"bind"				{ return ErrorNodeTypes.BIND; }
	"by"				{ return ErrorNodeTypes.BY; }
	"byname"			{ return ErrorNodeTypes.BYNAME; }
	"byposition"		{ return ErrorNodeTypes.BYPOSITION; }
	"call"				{ return ErrorNodeTypes.CALL; }
	"case"				{ return ErrorNodeTypes.CASE; }
	"close"			 	{ return ErrorNodeTypes.CLOSE; }
	"const"			 	{ return ErrorNodeTypes.CONST; }
	"constructor"		{ return ErrorNodeTypes.CONSTRUCTOR; }
	"continue"			{ return ErrorNodeTypes.CONTINUE; }
	"converse"		    { return ErrorNodeTypes.CONVERSE; }
	"current"			{ return ErrorNodeTypes.CURRENT; }
	"dataitem"			{ return ErrorNodeTypes.DATAITEM; }
	"datatable"			{ return ErrorNodeTypes.DATATABLE; }
	"decrement"			{ return ErrorNodeTypes.DECREMENT; }
	"delegate"			{ return ErrorNodeTypes.DELEGATE; }
	"delete"			{ return ErrorNodeTypes.DELETE; }
	"display"			{ return ErrorNodeTypes.DISPLAY; }
	"else"				{ return ErrorNodeTypes.ELSE; }
	"embed"			 	{ return ErrorNodeTypes.EMBED; }
	"end"			 	{ return ErrorNodeTypes.END; }
    "enumeration"       { return ErrorNodeTypes.ENUMERATION; }
	"escape"			{ return ErrorNodeTypes.ESCAPE; }
	"execute"			{ return ErrorNodeTypes.EXECUTE; }
	"exit"				{ return ErrorNodeTypes.EXIT; }
	"extends"			{ return ErrorNodeTypes.EXTENDS; }
	"externalType"		{ return ErrorNodeTypes.EXTERNALTYPE; }
	"field"			 	{ return ErrorNodeTypes.FIELD; }
	"first"				{ return ErrorNodeTypes.FIRST; }
	"for"			 	{ return ErrorNodeTypes.FOR; }
	"foreach"			{ return ErrorNodeTypes.FOREACH; }
	"form"				{ return ErrorNodeTypes.FORM; }
	"formgroup"			{ return ErrorNodeTypes.FORMGROUP; }
	"forupdate"			{ return ErrorNodeTypes.FORUPDATE; }
	"forward"			{ return ErrorNodeTypes.FORWARD; }
	"freesql"			{ return ErrorNodeTypes.FREESQL; }
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
//	"like"				{ return ErrorNodeTypes.LIKE; }
//	"matches"			{ return ErrorNodeTypes.MATCHES; }
	"move"				{ return ErrorNodeTypes.MOVE; }
	"next"				{ return ErrorNodeTypes.NEXT; }
	"new"			 	{ return ErrorNodeTypes.NEW; }	
	"null"			 	{ return ErrorNodeTypes.NULL; }
	"nocursor"			{ return ErrorNodeTypes.NOCURSOR; }
	"not"				{ return ErrorNodeTypes.NOT; }
	"onevent"			{ return ErrorNodeTypes.ONEVENT; }
	"onexception"		{ return ErrorNodeTypes.ONEXCEPTION; }
	"open"				{ return ErrorNodeTypes.OPEN; }
	"openui"			{ return ErrorNodeTypes.OPENUI; }
	"otherwise"			{ return ErrorNodeTypes.OTHERWISE; }
	"out"			 	{ return ErrorNodeTypes.OUT; }
	"package"			{ return ErrorNodeTypes.PACKAGE; }
	"passing"			{ return ErrorNodeTypes.PASSING; }
	"prepare"			{ return ErrorNodeTypes.PREPARE; }
	"previous"		    { return ErrorNodeTypes.PREVIOUS; }
	"print"			 	{ return ErrorNodeTypes.PRINT; }
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
	"show"				{ return ErrorNodeTypes.SHOW; }
	"singlerow"			{ return ErrorNodeTypes.SINGLEROW; }
	"sqlnullable"		{ return ErrorNodeTypes.SQLNULLABLE; }
	"stack"			 	{ return ErrorNodeTypes.STACK; }
	"static"			{ return ErrorNodeTypes.STATIC; }
	"this"				{ return ErrorNodeTypes.THIS; }
	"throw"				{ return ErrorNodeTypes.THROW; }
	"to"				{ return ErrorNodeTypes.TO; }
	"transaction"		{ return ErrorNodeTypes.TRANSACTION; }
	"transfer"		    { return ErrorNodeTypes.TRANSFER; }
	"try"			 	{ return ErrorNodeTypes.TRY; }
	"type"				{ return ErrorNodeTypes.TYPE; }
	"update"			{ return ErrorNodeTypes.UPDATE; }
	"url"				{ return ErrorNodeTypes.URL; }
	"use"				{ return ErrorNodeTypes.USE; }
	"using"				{ return ErrorNodeTypes.USING; }
	"usingKeys"			{ return ErrorNodeTypes.USINGKEYS; }
	"usingPCB"		    { return ErrorNodeTypes.USINGPCB; }
	"when"				{ return ErrorNodeTypes.WHEN; }
	"while"			 	{ return ErrorNodeTypes.WHILE; }
	"with"				{ return ErrorNodeTypes.WITH; }
	"withv60compat"		{ return ErrorNodeTypes.WITHV60COMPAT; }


        \"                              { yybegin(STRING); string.setLength(0); string.append('\"'); }
        [xX]\"                          { yybegin(STRING); string.setLength(0); string.append('\"'); }
        [cC]\"                          { yybegin(STRING); string.setLength(0); string.append('\"'); }
        [dD]\"                          { yybegin(STRING); string.setLength(0); string.append('\"'); }
        [mM]\"                          { yybegin(STRING); string.setLength(0); string.append('\"'); }
        [cC][xX]\"                      { yybegin(STRING); string.setLength(0); string.append('\"'); }
        [dD][xX]\"                      { yybegin(STRING); string.setLength(0); string.append('\"'); }
        [mM][xX]\"                      { yybegin(STRING); string.setLength(0); string.append('\"'); }
        [uU][xX]\"                      { yybegin(STRING); string.setLength(0); string.append('\"'); }

		"#sql"{WhiteSpace}*"{"			{ yybegin(SQL); isSQLCondition = false; string.setLength(0); string.append(yytext()); }
		"#sqlcondition"{WhiteSpace}*"{"	{ yybegin(SQL); isSQLCondition = true; string.setLength(0); string.append(yytext()); }

		"#dli"{WhiteSpace}*"{"			{ yybegin(DLI); string.setLength(0); string.append(yytext()); }
		
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
	">"					{ return ErrorNodeTypes.GT; }
	"<="				{ return ErrorNodeTypes.LE; }
	">="				{ return ErrorNodeTypes.GE; }
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
	"?["				{ return ErrorNodeTypes.QUESTIONBRACKET; }

        // Primitive Types
		"any"			 	{ return ErrorNodeTypes.PRIMITIVE; }
		"bigint"			{ return ErrorNodeTypes.PRIMITIVE; }
//		"bin"			 	{ return ErrorNodeTypes.NUMERICPRIMITIVE; }
		"boolean"			{ return ErrorNodeTypes.PRIMITIVE; }
		"bytes"				{ return ErrorNodeTypes.CHARPRIMITIVE; }
//		"char"				{ return ErrorNodeTypes.CHARPRIMITIVE; }
//		"dbchar"			{ return ErrorNodeTypes.CHARPRIMITIVE; }
		"decimal"			{ return ErrorNodeTypes.NUMERICPRIMITIVE; }
		"float"			 	{ return ErrorNodeTypes.PRIMITIVE; }
//		"hex"			 	{ return ErrorNodeTypes.CHARPRIMITIVE; }
		"int"			 	{ return ErrorNodeTypes.PRIMITIVE; }
//		"mbchar"			{ return ErrorNodeTypes.CHARPRIMITIVE; }
//		"money"			 	{ return ErrorNodeTypes.NUMERICPRIMITIVE; }
//		"num"			 	{ return ErrorNodeTypes.NUMERICPRIMITIVE; }
		"number"			{ return ErrorNodeTypes.PRIMITIVE; }
//		"numc"				{ return ErrorNodeTypes.NUMERICPRIMITIVE; }
//		"pacf"				{ return ErrorNodeTypes.NUMERICPRIMITIVE; }
		"smallfloat"		{ return ErrorNodeTypes.PRIMITIVE; }
		"smallint"		    { return ErrorNodeTypes.PRIMITIVE; }
		"string"			{ return ErrorNodeTypes.CHARPRIMITIVE; }
//		"unicode"			{ return ErrorNodeTypes.CHARPRIMITIVE; }

		// Large Object Type Keywords
//	    "blob"                          { return ErrorNodeTypes.PRIMITIVE ; }
//      "clob"                          { return ErrorNodeTypes.PRIMITIVE ; }

        // Macros
        {Identifier}                    { return ErrorNodeTypes.ID; }
        {Integer}                       { return ErrorNodeTypes.INTEGER; }
        {DecimalLit}                    { return ErrorNodeTypes.DECIMALLIT; }
        {FloatLit}                      { return ErrorNodeTypes.FLOATLIT; }
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

<DLI> {
		"}"								{ yybegin(YYINITIAL); string.append(yytext()); return ErrorNodeTypes.INLINE_DLI; }
		{DLIIdentifier}					{ string.append(yytext()); }
		{WhiteSpace}*					{ string.append(yytext()); }

		\"								{ yybegin(DLI_DBL_QUOTED_ID); string.append('\"'); }
		\'								{ yybegin(DLI_QUOTED_ID); string.append('\''); }
		.								{ string.append(yytext()); }
}

<DLI_DBL_QUOTED_ID> {
		\"								{ yybegin(DLI); string.append('\"'); }
		\"\"							{ string.append("\"\""); }
		[^\"\r\n]+						{ string.append(yytext()); }
		
		{LineTerminator}				{ yybegin(DLI); string.append(yytext()); }
}

<DLI_QUOTED_ID> {
		\'								{ yybegin(DLI); string.append('\''); }
		\'\'							{ string.append("\'\'"); }
		[^\'\r\n]+						{ string.append(yytext()); }
		
		{LineTerminator}				{ yybegin(DLI); string.append(yytext()); }
}

/* No token was found for the input so throw an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^]                    					{ return ErrorNodeTypes.error; }
