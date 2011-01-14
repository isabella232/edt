package com.ibm.etools.egl.pgm.internal.parser;
import java.io.IOException;
import java.io.Reader;
import org.eclipse.edt.compiler.internal.pgm.ITerminalNode;
import org.eclipse.edt.compiler.internal.pgm.NodeFactory;
%%
%class EGLLexer
%implements IEGLLexer
%type int
%public
%ignorecase
%unicode
%buffer 128

%eofval{
	if(yystate() == SQL || yystate() == SQLDELIMITEDID || yystate() == SQLSTRING) {
		yybegin(YYINITIAL);
		return EGLNodeTypes.ERRORSQLSTMTLIT;
	}
	else if(yystate() == STRING) {
		yybegin(YYINITIAL);
		return EGLNodeTypes.ERRORSTRING;
	}
	else if(yystate() == BLOCKCOMMENT) {
		yybegin(YYINITIAL);
		return EGLNodeTypes.ERRORBLOCKCOMMENT;
	}
	else if(yystate() == DLI || yystate() == DLI_QUOTED_ID || yystate() == DLI_DBL_QUOTED_ID) {
		yybegin(YYINITIAL);
		return EGLNodeTypes.ERROR_INLINE_DLI;
	}
	else {
		return EGLNodeTypes.EOF;
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
	protected int index;
	protected boolean invalidEscapeSequence;
	
	private NodeFactory factory;
		
	public void reset(Reader reader, NodeFactory factory) {
		try {
			yyclose();
			yyreset(reader);
		} catch (IOException e) {
			// Should never happen
			e.printStackTrace();
		}
		this.factory = factory;
	}
	
	public ITerminalNode nextTerminal() {
		try {
			int type = yylex();

			if (type == EGLNodeTypes.STRING && invalidEscapeSequence) {
				// invalidEscapeSequence is reset everytime we go into the STRING state
				return factory.createStringTerminalNodeWithInvalidEscape(string.toString(), yystate());
			} else if (type == EGLNodeTypes.STRING || type == EGLNodeTypes.ERRORSTRING || type == EGLNodeTypes.ERRORBLOCKCOMMENT || type == EGLNodeTypes.ERRORSQLSTMTLIT) {
				return factory.createTerminalNode(type, string.toString(), yystate());
			} else if (type == EGLNodeTypes.SQLSTMTLIT) {
				return factory.createSQLTerminalNode(string.toString(), yystate(), index);
			} else if (type == EGLNodeTypes.INLINE_DLI || type == EGLNodeTypes.ERROR_INLINE_DLI) {
				return factory.createDLITerminalNode(type, string.toString(), yystate(), index);
			} else {
				return factory.createTerminalNode(type, yytext(), yystate());
			}
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
        "add"                           { return EGLNodeTypes.ADD ; }
        "all"                           { return EGLNodeTypes.ALL ; }
        "as"                            { return EGLNodeTypes.AS ; }
        "bind"                          { return EGLNodeTypes.BIND ; }
        "byname"                        { return EGLNodeTypes.BYNAME ; }
        "call"                          { return EGLNodeTypes.CALL; }
        "case"                          { return EGLNodeTypes.CASE; }
        "close"                         { return EGLNodeTypes.CLOSE; }
        "const"                         { return EGLNodeTypes.CONST; }
        "converse"                      { return EGLNodeTypes.CONVERSE; }
        "current"                       { return EGLNodeTypes.CURRENT ; }
        "dataitem"                      { return EGLNodeTypes.DATAITEM; }
        "datatable"                     { return EGLNodeTypes.DATATABLE ; }
        "delete"                        { return EGLNodeTypes.DELETE ; }
        "display"                       { return EGLNodeTypes.DISPLAY ; }
        "else"                          { return EGLNodeTypes.ELSE; }
        "embed"                         { return EGLNodeTypes.EMBED ; }
        "end"                           { return EGLNodeTypes.END; }
        "escape"                        { return EGLNodeTypes.ESCAPE ; }
        "execute"                       { return EGLNodeTypes.EXECUTE ; }
        "exit"                          { return EGLNodeTypes.EXIT; }
        "extends"                       { return EGLNodeTypes.EXTENDS; }
        "externallydefined"             { return EGLNodeTypes.EXTERNALLYDEFINED ; }
        "field"                         { return EGLNodeTypes.FIELD ; }
        "for"                           { return EGLNodeTypes.FOR ; }
        "form"                          { return EGLNodeTypes.FORM; }
        "formgroup"                     { return EGLNodeTypes.FORMGROUP; }
        "forupdate"                     { return EGLNodeTypes.FORUPDATE; }
        "forward"                       { return EGLNodeTypes.FORWARD ; }
        "from"                          { return EGLNodeTypes.FROM ; }
        "function"                      { return EGLNodeTypes.FUNCTION; }
        "get"                           { return EGLNodeTypes.GET; }
        "goto"                          { return EGLNodeTypes.GOTO; }
        "handler"                       { return EGLNodeTypes.HANDLER ; }
        "hold"                          { return EGLNodeTypes.HOLD ; }
        "if"                            { return EGLNodeTypes.IF; }
        "implements"                    { return EGLNodeTypes.IMPLEMENTS ; }
        "import"                        { return EGLNodeTypes.IMPORT; }
        "in"                            { return EGLNodeTypes.IN; }
        "insert"                        { return EGLNodeTypes.INSERT ; }
        "interface"                     { return EGLNodeTypes.INTERFACE; }
        "into"                          { return EGLNodeTypes.INTO; }
        "inout"                         { return EGLNodeTypes.INOUT; }
        "inparent"                      { return EGLNodeTypes.INPARENT; }
        "is"                            { return EGLNodeTypes.IS; }
        "isa"                           { return EGLNodeTypes.ISA; }
        "label"                         { return EGLNodeTypes.LABEL ; }
        "library"                       { return EGLNodeTypes.LIBRARY; }
        "like"                          { return EGLNodeTypes.LIKE; }
        "matches"                       { return EGLNodeTypes.MATCHES ; }
        "move"                          { return EGLNodeTypes.MOVE ; }
        "next"                          { return EGLNodeTypes.NEXT; }
        "new"                           { return EGLNodeTypes.NEW; }        
        "nil"                           { return EGLNodeTypes.NIL; }
        "norefresh"                     { return EGLNodeTypes.NOREFRESH ; }
        "not"                           { return EGLNodeTypes.NOT; }
        "nullable"                      { return EGLNodeTypes.NULLABLE ; }
        "onevent"                       { return EGLNodeTypes.ONEVENT ; }
        "onexception"                   { return EGLNodeTypes.ONEXCEPTION ; }
        "open"                          { return EGLNodeTypes.OPEN; }
        "openui"                        { return EGLNodeTypes.OPENUI; }
        "otherwise"                     { return EGLNodeTypes.OTHERWISE; }
        "package"                       { return EGLNodeTypes.PACKAGE ; }
        "pagehandler"                   { return EGLNodeTypes.PAGEHANDLER ; }
        "passing"                       { return EGLNodeTypes.PASSING ; }
        "prepare"                       { return EGLNodeTypes.PREPARE ; }
        "previous"                      { return EGLNodeTypes.PREVIOUS ; }
        "print"                         { return EGLNodeTypes.PRINT ; }
        "private"                       { return EGLNodeTypes.PRIVATE; }
        "program"                       { return EGLNodeTypes.PROGRAM; }
        "record"                        { return EGLNodeTypes.RECORD; }
        "ref"                           { return EGLNodeTypes.REF; }
        "replace"                       { return EGLNodeTypes.REPLACE ; }
        "return"                        { return EGLNodeTypes.RETURN; }
        "returns"                       { return EGLNodeTypes.RETURNS; }
        "returning"						{ return EGLNodeTypes.RETURNING; }
        "service"                       { return EGLNodeTypes.SERVICE ; }
        "set"                           { return EGLNodeTypes.SET ; }
        "show"                          { return EGLNodeTypes.SHOW ; }
        "singlerow"                     { return EGLNodeTypes.SINGLEROW ; }
        "stack"                         { return EGLNodeTypes.STACK ; }
        "static"                        { return EGLNodeTypes.STATIC ; }
        "this"                          { return EGLNodeTypes.THIS ; }
        "to"                            { return EGLNodeTypes.TO ; }
        "transaction"                   { return EGLNodeTypes.TRANSACTION ; }
        "transfer"                      { return EGLNodeTypes.TRANSFER ; }
        "try"                           { return EGLNodeTypes.TRY ; }
        "type"                          { return EGLNodeTypes.TYPE ; }
        "update"                        { return EGLNodeTypes.UPDATE ; }
        "url"							{ return EGLNodeTypes.URL ; }
        "use"                           { return EGLNodeTypes.USE ; }
        "using"                         { return EGLNodeTypes.USING; }
        "usingKeys"                     { return EGLNodeTypes.USINGKEYS; }
        "usingPCB"                      { return EGLNodeTypes.USINGPCB; }
        "when"                          { return EGLNodeTypes.WHEN; }
        "while"                         { return EGLNodeTypes.WHILE; }
        "with"                          { return EGLNodeTypes.WITH ; }


        "("                             { return EGLNodeTypes.LPAREN; }
        ")"                             { return EGLNodeTypes.RPAREN; }

        \"                              { yybegin(STRING); string.setLength(0); string.append('\"'); invalidEscapeSequence = false; }

		"#sql"{WhiteSpace}*"{"			{ yybegin(SQL); string.setLength(0); string.append(yytext()); index = yylength(); }
		"#sqlcondition"{WhiteSpace}*"{"	{ yybegin(SQL); string.setLength(0); string.append(yytext()); index = -yylength(); }

		"#dli"{WhiteSpace}*"{"			{ yybegin(DLI); string.setLength(0); string.append(yytext()); index = yylength(); }
		
		"/*"							{ yybegin(BLOCKCOMMENT); string.setLength(0); string.append(yytext()); }

        "!"                             { return EGLNodeTypes.BANG ; }
        ","                             { return EGLNodeTypes.COMMA; }
        ";"                             { return EGLNodeTypes.SEMI; }
        "."                             { return EGLNodeTypes.DOT; }
        ":"                             { return EGLNodeTypes.COLON; }
        "="                             { return EGLNodeTypes.ASSIGN; }
        "=="                            { return EGLNodeTypes.EQ; }
        "&&"                            { return EGLNodeTypes.AND; }
        "||"                            { return EGLNodeTypes.OR; }
        "!="                            { return EGLNodeTypes.NE; }
        "<"                             { return EGLNodeTypes.LT; }
        ">"                             { return EGLNodeTypes.GT; }
        "<="                            { return EGLNodeTypes.LE; }
        ">="                            { return EGLNodeTypes.GE; }
        "+"                             { return EGLNodeTypes.PLUS; }
        "-"                             { return EGLNodeTypes.MINUS; }
        "*"                             { return EGLNodeTypes.TIMES; }
        "**"                            { return EGLNodeTypes.TIMESTIMES; }
        "/"                             { return EGLNodeTypes.DIV; }
        "%"                             { return EGLNodeTypes.MODULO; }
        "["                             { return EGLNodeTypes.LBRACKET; }
        "]"                             { return EGLNodeTypes.RBRACKET; }
        "{"                             { return EGLNodeTypes.LCURLY; }
        "}"                             { return EGLNodeTypes.RCURLY; }
        "@"                             { return EGLNodeTypes.AT; }

        // Primitive Types
        "any"                           { return EGLNodeTypes.PRIMITIVE; }
        "bigint"                        { return EGLNodeTypes.PRIMITIVE; }
        "bin"                           { return EGLNodeTypes.PRIMITIVE; }
        "boolean"                       { return EGLNodeTypes.PRIMITIVE; }
        "char"                          { return EGLNodeTypes.PRIMITIVE; }
        "dbchar"                        { return EGLNodeTypes.PRIMITIVE; }
        "decimal"                       { return EGLNodeTypes.PRIMITIVE; }
        "float"                         { return EGLNodeTypes.PRIMITIVE; }
        "hex"                           { return EGLNodeTypes.PRIMITIVE; }
        "int"                           { return EGLNodeTypes.PRIMITIVE; }
        "mbchar"                        { return EGLNodeTypes.PRIMITIVE; }
        "money"                         { return EGLNodeTypes.PRIMITIVE; }
        "num"                           { return EGLNodeTypes.PRIMITIVE; }
        "number"                        { return EGLNodeTypes.PRIMITIVE; }
        "numc"                          { return EGLNodeTypes.PRIMITIVE; }
        "pacf"                          { return EGLNodeTypes.PRIMITIVE; }
        "smallfloat"                    { return EGLNodeTypes.PRIMITIVE; }
        "smallint"                      { return EGLNodeTypes.PRIMITIVE; }
        "string"                        { return EGLNodeTypes.PRIMITIVE; }
        "unicode"                       { return EGLNodeTypes.PRIMITIVE; }

		// Large Object Type Keywords
        "blob"                          { return EGLNodeTypes.LOB ; }
        "clob"                          { return EGLNodeTypes.LOB ; }

        // Keywords reserved for future
        "absolute"                      { return EGLNodeTypes.ABSOLUTE ; }      //future
        "by"                            { return EGLNodeTypes.BY ; }            //future
        "byposition"                    { return EGLNodeTypes.BYPOSITION ; }    //future
        "continue"                      { return EGLNodeTypes.CONTINUE ; }      //future
        "decrement"                     { return EGLNodeTypes.DECREMENT ; }     //future
        "dlicall"                       { return EGLNodeTypes.DLICALL ; }       //future
        "first"                         { return EGLNodeTypes.FIRST ; }         //future
        "foreach"                       { return EGLNodeTypes.FOREACH ; }       //future
        "freesql"                       { return EGLNodeTypes.FREESQL ; }       //future
        "group"                         { return EGLNodeTypes.GROUP ; }         //future
        "last"                          { return EGLNodeTypes.LAST ; }          //future
        "languagebundle"        		{ return EGLNodeTypes.LANGUAGEBUNDLE ; }//future
        "of"                            { return EGLNodeTypes.OF ; }            //future
        "out"                           { return EGLNodeTypes.OUT ; }           //future
        "relative"                      { return EGLNodeTypes.RELATIVE ; }      //future
        "scroll"                        { return EGLNodeTypes.SCROLL ; }        //future
        "where"                         { return EGLNodeTypes.WHERE ; }         //future
        "wrap"                          { return EGLNodeTypes.WRAP; }           //future
        "date"                          { return EGLNodeTypes.DATE ; }          //future
        "interval"                      { return EGLNodeTypes.INTERVAL ; }      //future
        "time"                          { return EGLNodeTypes.TIME ; }          //future
        "timestamp"                     { return EGLNodeTypes.TIMESTAMP ; }     //future


        // Macros
        {Identifier}                    { return EGLNodeTypes.ID; }
        {Integer}                       { return EGLNodeTypes.INTEGER; }
        {DecimalLit}                    { return EGLNodeTypes.DECIMALLIT; }
        {FloatLit}                      { return EGLNodeTypes.FLOATLIT; }
        {WhiteSpace}*                   { return EGLNodeTypes.WS; }
        {Comment}                       { return EGLNodeTypes.WS; }
}

<BLOCKCOMMENT> {
		"*/"							{ yybegin(YYINITIAL); string.append(yytext()); return EGLNodeTypes.WS; }
		[^*]*							{ string.append(yytext()); }
		.								{ string.append(yytext()); }
}		

<STRING> {
        \"                              { yybegin(YYINITIAL); string.append('\"'); return EGLNodeTypes.STRING; }
        
        [^\\\"\r\n]+					{ string.append(yytext()); }
        \\\"							{ string.append("\\\""); }
        \\\\							{ string.append("\\\\"); }
        \\[b]							{ string.append("\\b"); }
        \\[f]							{ string.append("\\f"); }
        \\[n]							{ string.append("\\n"); }
        \\[r]							{ string.append("\\r"); }
        \\[t]							{ string.append("\\t"); }
        \\								{ string.append("\\"); invalidEscapeSequence = true; }

        {LineTerminator}				{ yybegin(YYINITIAL); string.append(yytext()); return EGLNodeTypes.ERRORSTRING; }
}

<SQL> {
		"}"								{ yybegin(YYINITIAL); string.append(yytext()); return EGLNodeTypes.SQLSTMTLIT; }
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
		"}"								{ yybegin(YYINITIAL); string.append(yytext()); return EGLNodeTypes.INLINE_DLI; }
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
[^]                    					{ return EGLNodeTypes.error; }
