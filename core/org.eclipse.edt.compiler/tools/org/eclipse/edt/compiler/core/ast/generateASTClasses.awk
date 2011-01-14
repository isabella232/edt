BEGIN {
	classNamesToIgnore[ "ArrayList" ] = 1;
	classNamesToIgnore[ "String[]" ] = 1;	
	
	typesThatArentNodes[ "String" ] = 1;
	typesThatArentNodes[ "Primitive" ] = 1;
	typesThatArentNodes[ "int" ] = 1;
	typesThatArentNodes[ "String[]" ] = 1;
	typesThatArentNodes[ "Boolean" ] = 1;
	
	terminalsToTypes["INTEGER"] = "int";
	terminalsToTypes["STRING"] = "String";
	terminalsToTypes["CHANGEME"] = "CHANGEME";
}

($0 ~ /^nonterminal/ || $0 ~ /terminal/) && NF > 2 {
	for( i = 3; i <= NF; i++ ) {
		terminal = $i;
		sub( "[;,]", "", terminal );
		sub( "/.*/", "", $2 );
		terminalsToTypes[terminal] = $2;
	}
}

/^[A-Za-z]/ {
	firstWordOnLine = $1;
}

/{:.* = new.*\(.*\)/ {
	split( $0, arr, "= new " );
	afterNew = arr[2];
	
	sub( "new .*),", "CHANGEME,", afterNew );
	
	split( afterNew, arr, "(" );
	className = arr[1];
	
	if( !(className in classesProcessed) && !(className in classNamesToIgnore)) {
		classesProcessed[className] = 1;
		
		numTokens = split( lastLine, tokens, " " );
		for( i = 1; i <= numTokens; i++ ) {
			numSubtokens = split( tokens[i], subtokens, ":" );
			if( numSubtokens == 2 ) {
				varsToTerminals[ subtokens[2] ] = subtokens[1];
			}
		}
		varsToTerminals[ "CHANGEME" ] = "CHANGEME";

		numParms = split( arr[2], parms, "," );		
	
#		print className;
		for( i = 1; i <= numParms; i++ ) {
			parms[i] = removeSymbolsAndWhitespace( parms[i] );
#			print parms[i] ", " varsToTerminals[ parms[i] ] ", " terminalsToTypes[ varsToTerminals[ parms[i] ] ];
		}

#		print "";
		
		if( system( "test -e " className ".java" ) ) {
			if( split( className, arr, "." ) == 1 ) {
				genClass();
			}
		}
	}
}

// {
	lastLine = $0
}

END {

}

function genClass() {
	filename = className ".java";
	cmd = "echo \"/*\" > " filename;
	system( cmd );
	
	if( firstWordOnLine in terminalsToTypes ) {
		superClass = terminalsToTypes[ firstWordOnLine ];
	}
	else {
		superClass = "Node";
	}
	if( superClass == className ) {
		superClass = "Node";
	}
	
	hasList = 0;
	for( i = 1; i <= numParms; i++ ) {
		if( parms[i] in varsToTerminals ) {
			terminal = varsToTerminals[ parms[i] ];
			
			if( terminal in terminalsToTypes ) {
				type = terminalsToTypes[ terminal ];
				varName = getTerminalDisplayName( terminal );
				if( varName in varNamesToOccurences ) {
					varName = varName varNamesToOccurences[ varName ]+1
					varNamesToOccurences[ varName ] += 1;
				}
				else {
					varNamesToOccurences[ varName ] = 1;
				}
				type = terminalsToTypes[ terminal ];
		
				if( !hasList && type == "List" ) {
					hasList = 1;
				}
			}
		}
	}
	
	concatToFile( filename, " * Created on Apr 23, 2005" );
	concatToFile( filename, " */" );
	concatToFile( filename, "package org.eclipse.edt.compiler.core.ast;" );
	concatToFile( filename, "" );
	if( hasList ) {
		concatToFile( filename, "import java.util.List;" );
		concatToFile( filename, "" );
	}
	concatToFile( filename, "/**" );
	concatToFile( filename, " * " className " AST node type." );
	concatToFile( filename, " *" );	
	concatToFile( filename, " * @author Albert Ho" );
	concatToFile( filename, " * @author David Murray" );
	concatToFile( filename, " */" );
	concatToFile( filename, "public class " className " extends " superClass " {" );
	concatToFile( filename, "" );
	
	ctorSig = "\tpublic " className "(";
	
	for( elem in varNamesToOccurences ) { delete varNamesToOccurences[ elem ] }	
	for( i = 1; i <= numParms; i++ ) {
		if( parms[i] in varsToTerminals ) {
			terminal = varsToTerminals[ parms[i] ];
			
			if( terminal in terminalsToTypes ) {
				type = terminalsToTypes[ terminal ];
				varName = getTerminalDisplayName( terminal );				
				if( varName in varNamesToOccurences ) {
					varName = varName varNamesToOccurences[ varName ]+1
					varNamesToOccurences[ varName ] += 1;
				}
				else {
					varNamesToOccurences[ varName ] = 1;
				}
				
				parmsWithTypes[ parms[i] ] = 1;
				
				comment = "";
				if( type == "List" ) {
					baseTypeName = terminal;
					sub( "_.*$", "", baseTypeName );
					baseType = terminalsToTypes[ baseTypeName ];
					if( baseType == "" ) {
						baseType = "Node";
					}					
					comment = "\t// List of " baseType "s"
				}
				
				concatToFile( filename, "\tprivate " type " " varName ";" comment );
				ctorSig = ctorSig type " " varName ", ";
			}
		}
	}
	
	ctorSig = ctorSig "int startOffset, int endOffset) {";
	
	concatToFile( filename, "" );	
	concatToFile( filename, ctorSig );
	concatToFile( filename, "\t\tsuper(startOffset, endOffset);" );
	concatToFile( filename, "\t\t" );
	
	hasNodeChildren = 0;
	
	for( elem in varNamesToOccurences ) { delete varNamesToOccurences[ elem ] }	
	for( i = 1; i <= numParms; i++ ) {
		if( parms[i] in varsToTerminals ) {
			terminal = varsToTerminals[ parms[i] ];
			
			if( terminal in terminalsToTypes ) {
				type = terminalsToTypes[ terminal ];
				varName = getTerminalDisplayName( terminal );
				if( varName in varNamesToOccurences ) {
					varName = varName varNamesToOccurences[ varName ]+1
					varNamesToOccurences[ varName ] += 1;
				}
				else {
					varNamesToOccurences[ varName ] = 1;
				}
				type = terminalsToTypes[ terminal ];
		
				if( type != "List" ) {
					if( type in typesThatArentNodes ) {
						concatToFile( filename, "\t\tthis." varName " = " varName ";" );
					}					
					else if( terminal ~ "[oO][pP][tT]$" ) {
						concatToFile( filename, "\t\tif(" varName " != null) {" );
						concatToFile( filename, "\t\t\tthis." varName " = " varName ";" );
						concatToFile( filename, "\t\t\tsetParent( " varName " );" );
						concatToFile( filename, "\t\t}" );
						hasNodeChildren = 1;
					}
					else {
						concatToFile( filename, "\t\tthis." varName " = " varName ";" );
						concatToFile( filename, "\t\tsetParent( " varName " );" );
						hasNodeChildren = 1;
					}					
				}
				else {
					baseTypeName = terminal;
					sub( "_.*$", "", baseTypeName );
					baseType = terminalsToTypes[ baseTypeName ];
					
					if( baseType in typesThatArentNodes ) {
						concatToFile( filename, "\t\tthis." varName " = " varName ";" );
					} else {
						concatToFile( filename, "\t\tthis." varName " = setParent(" varName ");" );		
						hasNodeChildren = 1;
					}
				}
			}
		}
	}
	
	concatToFile( filename, "\t}" );
	
	concatToFile( filename, "\t" );
	
	for( elem in varNamesToOccurences ) { delete varNamesToOccurences[ elem ] }	
	for( i = 1; i <= numParms; i++ ) {
		if( parms[i] in varsToTerminals ) {
			terminal = varsToTerminals[ parms[i] ];
			
			if( terminal in terminalsToTypes ) {
				type = terminalsToTypes[ terminal ];
				varName = getTerminalDisplayName( terminal );
				if( varName in varNamesToOccurences ) {
					varName = varName varNamesToOccurences[ varName ]+1
					varNamesToOccurences[ varName ] += 1;
				}
				else {
					varNamesToOccurences[ varName ] = 1;
				}
				type = terminalsToTypes[ terminal ];
		
				concatToFile( filename, "\tpublic " type " get" firstUpper( varName ) "() {" );
				concatToFile( filename, "\t\treturn " varName ";" );
				concatToFile( filename, "\t}" );
				concatToFile( filename, "\t" );
			}
		}
	}
	
	concatToFile( filename, "\tpublic void accept(IASTVisitor visitor) {" );
	if( hasNodeChildren != 0 ) {
		concatToFile( filename, "\t\tboolean visitChildren = visitor.visit(this);" );
		concatToFile( filename, "\t\tif(visitChildren) {" );
		for( elem in varNamesToOccurences ) { delete varNamesToOccurences[ elem ] }	
		for( i = 1; i <= numParms; i++ ) {
			if( parms[i] in varsToTerminals ) {
				terminal = varsToTerminals[ parms[i] ];
				
				if( terminal in terminalsToTypes ) {
					type = terminalsToTypes[ terminal ];
					varName = getTerminalDisplayName( terminal );
					if( varName in varNamesToOccurences ) {
						varName = varName varNamesToOccurences[ varName ]+1
						varNamesToOccurences[ varName ] += 1;
					}
					else {
						varNamesToOccurences[ varName ] = 1;
					}
					type = terminalsToTypes[ terminal ];
					
					if( type != "List" ) {
						if( type in typesThatArentNodes ) {						
						}
						else if( terminal ~ "[oO][pP][tT]$" ) {
							concatToFile( filename, "\t\t\tif(" varName " != null) " varName ".accept(visitor);" );
						}
						else {
							concatToFile( filename, "\t\t\t" varName ".accept(visitor);" );
						}
					}
					else {
						baseTypeName = terminal;
						sub( "_.*$", "", baseTypeName );
						baseType = terminalsToTypes[ baseTypeName ];
						
						if( baseType in typesThatArentNodes ) {
						}
						else {
							concatToFile( filename, "\t\t\tacceptChildren(visitor, " varName ");" );
						}
					}		
				}
			}
		}
		concatToFile( filename, "\t\t}" );
	}
	else {
		concatToFile( filename, "\t\tvisitor.visit(this);" );
	}
	concatToFile( filename, "\t\tvisitor.endVisit(this);" );
	concatToFile( filename, "\t}" );
	
	concatToFile( filename, "}" );
	
	cmd = "unix2dos " filename;
	system( cmd );
}

function concatToFile( filename, str,	i ) {
	cmd = "echo \"" str "\" >> " filename;
	system( cmd );
}

function getTerminalDisplayName( terName ) {
	sub( "_opt$", "Opt", terName );
	sub( "_star$", "s", terName );
	sub( "Decl", "Declaration", terName );
	sub( "INTEGER", "integer", terName );
	sub( "PRIMITIVE", "prim", terName );
	return terName;
}

function removeSymbolsAndWhitespace( str,	i, chars, numChars, ch, charsToTrim, result ) {
	numChars = split( " );:}", charsToTrim, "" );
	for( i = 1; i <= numChars; i++ ) {
		charsToTrim[ charsToTrim[i] ] = 1;
		delete charsToTrim[i];
	}
	
	result = "";
	numChars = split( str, chars, "" );
	for( i = 1; i <= numChars; i++ ) {
		ch = chars[ i ];
		if( !(ch in charsToTrim) ) {
			result = result "" ch;
		}
	}
	return result;
}

function firstUpper( str ) {
	return toupper( substr( str, 1, 1 ) ) substr( str, 2 );
}
