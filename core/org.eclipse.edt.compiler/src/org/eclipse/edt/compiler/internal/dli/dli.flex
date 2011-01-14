package org.eclipse.edt.compiler.internal.pgm.dli;
import java_cup.runtime.*;

%%
%class DLILexer
%cupsym DLINodeTypes
%cup
%ignorecase
%unicode
%char

%{
	private StringBuffer stringBuffer;
	private int matchLength;
	private int matchStart;

	private Symbol symbol(int type) {
		return new Symbol(type, yychar, yychar + yylength());
	}

	private Symbol symbol(int type, Object value) {
		return new Symbol(type, yychar, yychar + yylength(), value);
	}

	private Symbol stringSymbol(int type) {
		String string = stringBuffer.toString();
		stringBuffer = null;
		return new Symbol(type, matchStart, matchStart + matchLength, string);
	}
	
	private int hostvarLength;

	private DLIParser parser;
	
	public void setParser(DLIParser parser) {
		this.parser = parser;
	}
%}

InputCharacter = [^\r\n]
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
Identifier     = ([:jletter:]|[$#@])([:jletterdigit:]|[$#@]])*
Integer        = [0-9]+
DecimalLit     = ({DLit1}|{DLit2})
DLit1          = [0-9]+ \. [0-9]*	// Has integer part and the dot
DLit2          = \. [0-9]+			// Has decimal part and the dot
FloatLit       = ({DecimalLit}|{Integer})[eE][+-]?{Integer}

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment}

TraditionalComment   = "/*" ~"*/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?


%state STRINGLIT
%state SINGLE_QUOTED_STRINGLIT
%state HOSTVAR

%%
<YYINITIAL> {
	"="					{ return symbol(DLINodeTypes.RELATIONOP, "="); }
	"!="				{ return symbol(DLINodeTypes.RELATIONOP, "!="); }
	">"					{ return symbol(DLINodeTypes.RELATIONOP, ">"); }
	">="				{ return symbol(DLINodeTypes.RELATIONOP, ">="); }
	"<"					{ return symbol(DLINodeTypes.RELATIONOP, "<"); }
	"<="				{ return symbol(DLINodeTypes.RELATIONOP, "<="); }

	"&"					{ return symbol(DLINodeTypes.BOOLEANOP, "&"); }
	"|"					{ return symbol(DLINodeTypes.BOOLEANOP, "|"); }
	"#"					{ return symbol(DLINodeTypes.BOOLEANOP, "#"); }

	"("					{ return symbol(DLINodeTypes.LPAREN); }
	")"					{ return symbol(DLINodeTypes.RPAREN); }

	"["					{ return symbol(DLINodeTypes.LBRACKET); }
	"]"					{ return symbol(DLINodeTypes.RBRACKET); }

	"*"					{ return symbol(DLINodeTypes.ASTERISK); }

	"CLSE"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "CLSE"); }
	"DEQ"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "DEQ"); }
	"DLET"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "DLET"); }
	"FLD"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "FLD"); }
	"GHN"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "GHN"); }
	"GHNP"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "GHNP"); }
	"GHU"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "GHU"); }
	"GN"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "GN"); }
	"GNP"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "GNP"); }
	"GU"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "GU"); }
	"ISRT"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "ISRT"); }
	"OPEN"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "OPEN"); }
	"POS"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "POS"); }
	"REPL"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "REPL"); }

	"CHKP"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "CHKP"); }
	"GMSG"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "GMSG"); }
	"GSCD"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "GSCD"); }
	"ICMD"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "ICMD"); }
	"INIT"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "INIT"); }
	"INQY"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "INQY"); }
	"LOG"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "LOG"); }
	"RCMD"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "RCMD"); }
	"ROLB"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "ROLB"); }
	"ROLL"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "ROLL"); }
	"ROLS"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "ROLS"); }
	"SETS"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "SETS"); }
	"SETU"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "SETU"); }
	"SNAP"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "SNAP"); }
	"STAT"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "STAT"); }
	"SYNC"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "SYNC"); }
	"TERM"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "TERM"); }
	"XRST"				{ return symbol(DLINodeTypes.FUNCTIONNAME, "XRST"); }
	
	{Identifier}		{ return symbol(DLINodeTypes.ID, yytext()); }
	{Identifier}#		{ return symbol(DLINodeTypes.ID, yytext()); }
	{Integer}			{ return symbol(DLINodeTypes.INTEGERLIT, yytext()); }
	{DecimalLit}		{ return symbol(DLINodeTypes.DECIMALLIT, yytext()); }
	{FloatLit}			{ return symbol(DLINodeTypes.FLOATLIT, yytext()); }
	
	":"					{ 
							stringBuffer = new StringBuffer();
							hostvarLength = parser.getHostVariableLength(yychar + 1);
							matchStart = yychar;
							matchLength = 1;

							if(hostvarLength == 0) {
								return stringSymbol(DLINodeTypes.HOSTVAR);
							}
							else {
								yybegin(HOSTVAR);
							}
						}
	
	\"					{ yybegin(STRINGLIT); matchStart = yychar; matchLength = 1; stringBuffer = new StringBuffer(); }
	\'					{ yybegin(SINGLE_QUOTED_STRINGLIT); matchStart = yychar; matchLength = 1; stringBuffer = new StringBuffer(); }
	
	{WhiteSpace}		{ /* Do nothing */ }
	{Comment}			{ /* Do nothing */ }
}

<HOSTVAR> {
	.					{
							stringBuffer.append(yytext());
							matchLength++;
							if(--hostvarLength == 0) {
								yybegin(YYINITIAL);
								return stringSymbol(DLINodeTypes.HOSTVAR);
							}
						}
}

<STRINGLIT> {
	\"					{ yybegin(YYINITIAL); matchLength++; return stringSymbol(DLINodeTypes.STRINGLIT); }
    
	[^\\\"\r\n]+		{ stringBuffer.append(yytext()); matchLength += yylength(); }
	\\\"				{ stringBuffer.append("\""); matchLength += yylength(); }
	\\\\				{ stringBuffer.append("\\"); matchLength += yylength(); }
	\\[b]				{ stringBuffer.append("\b"); matchLength += yylength(); }
	\\[f]				{ stringBuffer.append("\f"); matchLength += yylength(); }
	\\[n]				{ stringBuffer.append("\n"); matchLength += yylength(); }
	\\[r]				{ stringBuffer.append("\r"); matchLength += yylength(); }
	\\[t]				{ stringBuffer.append("\t"); matchLength += yylength(); }
	\\					{ stringBuffer.append("\\"); matchLength += yylength(); }

	{LineTerminator}	{ yybegin(YYINITIAL); matchLength += yylength(); return stringSymbol(DLINodeTypes.STRINGLIT); }
}

<SINGLE_QUOTED_STRINGLIT> {
	\'					{ yybegin(YYINITIAL); matchLength++; return stringSymbol(DLINodeTypes.SINGLE_QUOTED_STRINGLIT); }
    
	[^\\\'\r\n]+		{ stringBuffer.append(yytext()); matchLength += yylength(); }
	\\\'				{ stringBuffer.append("'"); matchLength += yylength(); }
	\\\\				{ stringBuffer.append("\\"); matchLength += yylength(); }
	\\[b]				{ stringBuffer.append("\b"); matchLength += yylength(); }
	\\[f]				{ stringBuffer.append("\f"); matchLength += yylength(); }
	\\[n]				{ stringBuffer.append("\n"); matchLength += yylength(); }
	\\[r]				{ stringBuffer.append("\r"); matchLength += yylength(); }
	\\[t]				{ stringBuffer.append("\t"); matchLength += yylength(); }
	\\					{ stringBuffer.append("\\"); matchLength += yylength(); }

	{LineTerminator}	{ yybegin(YYINITIAL); matchLength++; return stringSymbol(DLINodeTypes.SINGLE_QUOTED_STRINGLIT); }
}

[^]						{ return symbol(DLINodeTypes.ERROR); }
