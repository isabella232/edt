BEGIN {
	terminals = "";
	nonTerminals = "";
	
	numTerminals = 0;
	numNonTerminals = 0;
}

# Skip existing declarations
($0 ~ /^nonterminal/ || $0 ~ /terminal/) {
	next;
}

# Skip first 5 lines
(NR <= 5) {
	next;
}

/^[a-zA-Z]/ {
	definedNonterminals[ $1 ] = 1;
}

// {
	for( i = 1; i <= NF; i++ ) {
		if( $i == "//" || $i ~ "{:" || $i ~ "^%" ) {
			next;
		}
		
		if( $i == "precedence" || $i == "nonassoc" || $i == "left" ) {
			continue;
		}
		
		sub( ":.*$", "", $i );
		sub( ";", "", $i );
		sub( ",", "", $i );
		
		if( $i ~ "[a-zA-Z]+" ) {
			if( toupper( $i ) == $i ) {
				if( !( $i in terminalsArr ) ) {
					terminalsArr[ $i ] = 1;
					if( terminals != "" ) {
						terminals = terminals ", "
					}
					terminals = terminals $i
				}
			}
			else {
				if( !( $i in nonTerminalsArr ) ) {
					nonTerminalsArr[ $i ] = 1;
										
					if( $i ~ "_plus$" || $i ~ "_star$" ) {
						sequenceNonTerminals[ $i ] = 1;
					}
					else {
						if( nonTerminals != "" ) {
							nonTerminals = nonTerminals ", "
							
							allNonTerminals[ $i ] = 1;
						}
						nonTerminals = nonTerminals $i
					}
				}
			}
		}
	}
}

END {
	print( "terminal " terminals ";" );
	print( "nonterminal " nonTerminals ";" );
	print( "" );
	print( "Sequence Nonterminals:" );
	for( str in sequenceNonTerminals ) {
		print( str );
	}
	print( "" );
	for( item in allNonTerminals ) {
		if( !( item in definedNonterminals ) ) {
			print( item " was not defined!" );
		}
	}
	print( "" );
	for( item in allNonTerminals ) {
		print "nonterminal " firstUpper( item ) " " item ";"
	}
}

function firstUpper( str ) {
	return toupper( substr( str, 1, 1 ) ) substr( str, 2 );
}