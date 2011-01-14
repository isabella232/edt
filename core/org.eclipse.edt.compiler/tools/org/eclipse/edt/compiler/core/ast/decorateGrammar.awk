#
# Reads through a CUP file and prints out its contents, modifying the lines
# that begin with a tab followed by "::=" or "|". The terminals and
# nonterminals on this line that do not already have an identifier label
# (:identifier) printed with a label appended.
#

NF == 1 {
	print;
	next;
}
	
/^\t/ {
	for( item in namesToOccurences ) {
		delete namesToOccurences[item];
	}
	
	if( $1 == "::=" ) {
		line = "\t::=\t";
	}
	else if( $1 == "|" ) {
		line = "\t|\t";
	}
	else {
		print;
		next;
	}
	
	doNotChange = 0;
	separator = "";
	for( i = 2; i <= NF; i++ ) {
		if( $i == "//" || $i ~ "^%" ) {
			doNotChange = 1;
		}
	
		colonPieces = split( $i, arr, ":" );
		if( colonPieces == 2 || doNotChange ) {
			line = line separator $i;
		}
		else {
			if( $i in namesToOccurences ) {
		 		namesToOccurences[$i] = namesToOccurences[$i] + 1;
			}
			else {
		 		namesToOccurences[$i] = 1;
			}
			line = line separator $i ":" labelName($i);
		}
		
		separator = " ";
	}
	
	print line;
	next;
}
	
{
	print;
}
	
function labelName(term, result) {
	result = term;
	if( toupper( result ) == result ) {
		result = tolower( result );
	}
	if( result  ~ "[oO][pP][tT]$" ) {
		sub( "[oO][pP][tT]$", "", result  );
	}
	if( result  ~ "_star$" || result ~ "_plus$" ) {
		sub( "_.*$", "s", result  );
	}
	result = result namesToOccurences[term];
	return result;
}