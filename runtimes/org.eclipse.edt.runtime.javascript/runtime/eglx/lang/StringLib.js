/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass( "eglx.lang", "StringLib", {
	"constructor" : function() {
	}
});


egl.eglx.lang.StringLib["getNextToken"] = function(/*string*/source, /*int*/
		index, /*string*/delimiters, endAssign) {
	var start = index.ezeUnbox ? index.ezeUnbox() : index;
	var searchEnd = source.length;
	// Validate the substring index.
	if (start < 1 || start > searchEnd) {
		throw egl.createInvalidIndexException("CRRUI2021E", [ index ], index);
	}
	// Search the substring for tokens. We don't use a
	// java.util.StringTokenizer because we need to know the index of
	// the match in addition to the token.
	var tokenStart = start - 1;
	// Skip delimiters at the beginning of the token.
	// Check each char to see if it's a delimiter.
	while (tokenStart != searchEnd && delimiters.indexOf(source.charAt(tokenStart)) != -1) {
		tokenStart++;
	}
	// If we're at the end of the substring, the search failed.
	if (tokenStart >= searchEnd) {
		// Store the end of the token in index.
		index.ezeCopy ? index.ezeCopy(tokenEnd + 1) : "";
		return null;
	}
	// Now we know we've found the beginning of a token. Find its end.
	var tokenEnd = tokenStart + 1;
	// Check each char to see if it's the start of a delimiter.
	while (tokenEnd != searchEnd && delimiters.indexOf(source.charAt(tokenEnd)) == -1) {
		tokenEnd++;
	}
	// Store the end of the token in index.
	index.ezeCopy ? index.ezeCopy(tokenEnd + 1) : "";
	if ( endAssign ) {
		endAssign( source.substring(tokenStart), tokenEnd );
	}
	// Return the token.
	return source.substring(tokenStart, tokenEnd);
};

egl.eglx.lang.StringLib["getTokenCount"] = function(/*string*/source, /*string*/
		delimiters) {
	//Returns the number of tokens in a source string by simulating the getTokenCount function.
	var index = 1;
	var tokens;
	var next;
	var endAssign = function(updatedsource, updatedindex) {
		source = updatedsource;
		index = updatedindex;
	};

	try {
		for (tokens = 0; tokens < source.length; tokens++) {
			next = egl.eglx.lang.StringLib.getNextToken(source, index, delimiters, endAssign);
		}
	} catch (exception) {
		//out of tokens
	} finally {
		return tokens;
	}
};

egl.eglx.lang.StringLib["fromCharCode"] = function(/*integer*/i) {
	if ((i >= 0) && (i<= 65535))
		return String.fromCharCode(i); // TODO should we simply alias somehow?
	else 
		throw new egl.eglx.lang.InvalidArgumentException();
};

egl.eglx.lang.StringLib["spaces"] = function(/*integer*/i) {
	//Returns a string of a specified length.
	var s = [];
	for ( var n = 0; n < i; ++n) {
		s.push(' ');
	}
	return (s.join(''));
};


egl.eglx.lang.StringLib.defaultDateFormat = "MM/dd/yyyy";
egl.eglx.lang.StringLib.db2TimestampFormat = "yyyy-MM-dd-HH.mm.ss.ffffff";
egl.eglx.lang.StringLib.jisTimeFormat = "HH:mm:ss";


/* "Dispatcher" that looks at type a and fwds to the correct variant.
 * TODO sbg this is temporary;  it should be removed when we have a
 * proper implementation of function overrrides....
 */
egl.eglx.lang.StringLib["format"] = function(x, b) {
	if (x == null || x.eze$$value === null) {
		throw egl.createNullValueException( "CRRUI2005E", [] );
	}

	var kind;
	
	if ( !x.eze$$signature ) {
		return egl.eglx.lang.StringLib.formatTimestamp( x, b );
	}

	var firstChar = x.eze$$signature.charAt(0);
	var firstCharIdx = 0;
	if (firstChar !== '?') {
		kind = firstChar;
	} else {
		kind = x.eze$$signature.charAt(1);
		firstCharIdx = 1;
	}
	x = x.eze$$value;

	switch (kind) {
	case 'I':
	case 'i':
		return egl.eglx.lang.StringLib.fmtNumDouble( x, b, new egl.eglx.lang.stringlib.LocalizedText() );
	case 'B':
	case 'd':
		return egl.eglx.lang.StringLib.fmtNumBigDecimal( x, b, new egl.eglx.lang.stringlib.LocalizedText() );
	case 'F':
	case 'f':
		return egl.eglx.lang.StringLib.fmtNumFloat( x, b, new egl.eglx.lang.stringlib.LocalizedText() );
//	case 'd':
//		return egl.eglx.lang.StringLib.fmtNumDouble( x, b, new egl.eglx.lang.stringlib.LocalizedText() );
	case 'K':
		return egl.eglx.lang.StringLib.formatDate( x, b );
	case 'J':
		return egl.eglx.lang.StringLib.formatTimestamp( x, b );
	case 'L':
		return egl.eglx.lang.StringLib.formatTime( x, b );
	}
	return "";
	
};

egl.eglx.lang.StringLib["formatTimestamp"] = function(/*timestamp*/val, /*optional string*/format) {
	if (!format || format == "" ) {
		format = egl.eglx.lang.StringLib.db2TimestampFormat;
	}
	try
	{
		return egl.timeStampToString( val, format );
	}
	catch ( oops )
	{
		return null;
	}
};

egl.eglx.lang.StringLib["formatTime"] = function(/*time*/val, /*optional string*/format) {
	if (!format || format == "" ) {
		format = egl.eglx.lang.StringLib.jisTimeFormat;
	}
	try
	{
		return egl.timeToString( val, format );
	}
	catch ( oops )
	{
		return null;
	}
};

egl.eglx.lang.StringLib["formatDate"] = function(/*date*/date, /*optional string*/format) {
	if (!format) {
		format = egl.eglx.lang.StringLib.defaultDateFormat;
	}
	
	try
	{
		return egl.dateToString( date, format );
	}
	catch ( oops )
	{
		return null;
	}
};

egl.eglx.lang.StringLib["fmtNumBigDecimal"] = function( value, fmtstring, locText ){
	if ( fmtstring == null || fmtstring.trim().length == 0 )
	{
		return "";
	}
	
	return egl.eglx.lang.StringLib.fmtNumString( value.toString(), value.signum(), fmtstring, locText );
}
	


egl.eglx.lang.StringLib["fmtNumFloat"] = function( value, fmtstring, locText ) {
	if ( fmtstring == null || fmtstring.trim().length == 0 )
	{
		return "";
	}
	
	if (value > 0.0)
		return egl.eglx.lang.StringLib.fmtNumString( value.toString(), 1, fmtstring, locText );
	else if (value < 0.0)
		return egl.eglx.lang.StringLib.fmtNumString( value.toString(), -1, fmtstring, locText );
	return egl.eglx.lang.StringLib.fmtNumString( value.toString(), 0, fmtstring, locText );
}
	
egl.eglx.lang.StringLib["fmtNumDouble"] = function( value, fmtstring, locText ) {
	if ( fmtstring == null || fmtstring.trim().length == 0 )
	{
		return "";
	}
	
	if (value > 0.0)
		return egl.eglx.lang.StringLib.fmtNumString( value.toString(), 1, fmtstring, locText );
	else if (value < 0.0)
		return egl.eglx.lang.StringLib.fmtNumString( value.toString(), -1, fmtstring, locText );
	return egl.eglx.lang.StringLib.fmtNumString( value.toString(), 0, fmtstring, locText );
}
	
egl.eglx.lang.StringLib["fmtNumString"] = function( valueString, signum, fmtstring, locText ) {
	// We don't want the first digit to be a negative sign or a zero.
	if ( valueString.charAt( 0 ) == '-' )
	{
		valueString = valueString.substring( 1 ); 
	}
	if ( valueString.charAt( 0 ) == '0' )
	{
		valueString = valueString.substring( 1 ); 
	}

	// The default FMTSIZE is assumed to be 80 		
	var FMTSIZE = 80;
	if(valueString.length > FMTSIZE)
	{
		// The reason for this fix is in some cases a number
		// could be represented with an exponent 3e89
		// The to string representation in EGL returns a string
		// that could be more than the default size, and the format
		// specifier could include decimal point #.##. 
		// To accomodate for everything the fmtsize needs to be
		// size of string representation + extra digits after decimal.
		FMTSIZE = valueString.length;
		var decpos = fmtstring.indexOf('.');
		var stlen = fmtstring.length;
		var ndecchars = stlen - decpos;
		FMTSIZE += ndecchars;
	}
	
	
	var AREASIZE = FMTSIZE + 2;

	/*
	 * set the first and last space in formatter to 0, copy the fmtstring
	 * starting at the second space
	 */
	var formatter = new Array( AREASIZE );
	egl.eglx.lang.StringLib.initArray( formatter );
	egl.eglx.lang.StringLib.getChars( fmtstring, 0, Math.min( FMTSIZE, fmtstring.length ), 
			formatter, 1 );

	/*
	 * Set p_frmt to point to one char beyond dec point. This decimal point
	 * is from the format string, thus is always represented by a period
	 * rather than locale dependent.
	 */
	var p_frmt = formatter;
	var p_frmtIndex = 1;
	var firstDecCharLocation = -1;
	for (; egl.eglx.lang.StringLib.toCharCode(p_frmt[p_frmtIndex]) != 0; p_frmtIndex++) {
		if (p_frmt[p_frmtIndex] == '.') 
		{
			p_frmtIndex++;
			firstDecCharLocation = p_frmtIndex;
			break;
		}
	}

	/*
	 * p_frmt should now point to one character beyond the decimal point in
	 * the format string Loop forward from p_frmt and set deccnt accordingly
	 */

	/*
	 * Loop forward from p_frmt and set deccnt accordingly
	 */
	var deccnt = 0;
	for (; egl.eglx.lang.StringLib.toCharCode(p_frmt[p_frmtIndex]) != 0; p_frmtIndex++) {
		switch (p_frmt[p_frmtIndex]) {
		/*
		 * Since p_frmt starts right after the decimal point and there can
		 * only be one decimal point in the format string, we should not
		 * encounter any more '.'.
		 */
		case '$':
		case '#':
		case '&':
		case '*':
		case '(':
		case '+':
		case '-':
		case '<':
			deccnt++;
		}
	}

	/*
	 * get string of text digits (without decimal point) from value
	 */ 
	var dgtstr = new Array( AREASIZE );
	egl.eglx.lang.StringLib.initArray( dgtstr );
	var decpt_pos = egl.eglx.lang.StringLib.deccvt(valueString, signum, deccnt, dgtstr);

	var eedecpoint = new egl.eglx.lang.stringlib.CCharArray(AREASIZE);
	eedecpoint.ccharArrayIndex = 1 + decpt_pos;
	for ( var i = 0; egl.eglx.lang.StringLib.toCharCode(dgtstr[ i ]) != 0; i++ )
	{
		eedecpoint.ccharArray[ i + 1 ] = dgtstr[ i ];
	}

	var useMonetary = new egl.eglx.lang.stringlib.CBoolean( false );
	var rightmost = egl.eglx.lang.StringLib.fmtanalysis( formatter, useMonetary );

	/*
	 * leaderprinted is FALSE iff a leading currency symbol needs to be
	 * printed, but hasn't yet been printed
	 */
	var leaderprinted = new egl.eglx.lang.stringlib.CBoolean(true);

	/*
	 * trailerprinted is FALSE iff a trailing currency symbol needs to be
	 * printed, but hasn't yet been printed
	 */
	var trailerprinted = new egl.eglx.lang.stringlib.CBoolean(true);
	var parprinted = new egl.eglx.lang.stringlib.CBoolean(true), minprinted = new egl.eglx.lang.stringlib.CBoolean(
			true), plusprinted = new egl.eglx.lang.stringlib.CBoolean(true);

	var sym_index = new egl.eglx.lang.stringlib.CInteger(0);

	var signa = new Array( 8 );
	egl.eglx.lang.StringLib.initArray( signa );

	egl.eglx.lang.StringLib.chksigns(formatter, sym_index, parprinted, minprinted, leaderprinted,
			trailerprinted, plusprinted, signa);

	/* load leading signs into signa array */
	var p_source = new egl.eglx.lang.stringlib.CCharArray();
	p_frmtIndex = egl.eglx.lang.StringLib.align(firstDecCharLocation - 1, p_source, eedecpoint, p_frmt, p_frmtIndex, rightmost);

	/* go right to left in format */
	var prstack = new Array(AREASIZE);
	egl.eglx.lang.StringLib.initArray( prstack );

	var lastchar = 0;

	var stack_index = new egl.eglx.lang.stringlib.CInteger(0);
	var currentChar = p_frmt[p_frmtIndex];
	var isNegative = signum == -1;
	for (; egl.eglx.lang.StringLib.toCharCode(currentChar) != 0; currentChar = p_frmt[--p_frmtIndex]) 
	{			
		if ( currentChar == ',' && egl.eglx.lang.StringLib.toCharCode(p_source.ccharArray[p_source.ccharArrayIndex]) == 0 )
		{
			p_frmt[p_frmtIndex] = lastchar; /* just fill leading commas */
			currentChar = lastchar;
		}

		switch ( currentChar ) 
		{
		case ',':
			prstack[stack_index.value++] = locText.getSeparatorSymbol();
			break;
		case '.':
			var decimalSeparator = 
				useMonetary.value ? locText.getMonetaryDecimalSeparator() : locText.getDecimalSymbol();
			prstack[stack_index.value++] = decimalSeparator;
			break;
		case '<':
			egl.eglx.lang.StringLib.leadingLT(p_source, prstack, stack_index);
			break;
		case '*':
			egl.eglx.lang.StringLib.leadingNotLT(p_source, prstack, stack_index, '*');
			break;
		case '&':
			egl.eglx.lang.StringLib.leadingNotLT(p_source, prstack, stack_index, '0');
			break;
		case '#':
			egl.eglx.lang.StringLib.leadingNotLT(p_source, prstack, stack_index, ' ');
			break;
		case '-':
			egl.eglx.lang.StringLib.prsigns(minprinted.value, p_source, prstack, sym_index,
					stack_index, signa, minprinted, plusprinted,
					parprinted, leaderprinted, trailerprinted, isNegative,
					locText);
			break;
		case '+':
			egl.eglx.lang.StringLib.prsigns(plusprinted.value, p_source, prstack, sym_index,
					stack_index, signa, minprinted, plusprinted,
					parprinted, leaderprinted, trailerprinted, isNegative,
					locText);
			break;
		case '(':
			egl.eglx.lang.StringLib.prsigns(parprinted.value, p_source, prstack, sym_index,
					stack_index, signa, minprinted, plusprinted,
					parprinted, leaderprinted, trailerprinted, isNegative,
					locText);
			break;
		case '$':
			egl.eglx.lang.StringLib.prsigns(leaderprinted.value, p_source, prstack, sym_index,
					stack_index, signa, minprinted, plusprinted,
					parprinted, leaderprinted, trailerprinted, isNegative,
					locText);
			break;
		case '@':
			egl.eglx.lang.StringLib.prsigns(trailerprinted.value, p_source, prstack,
					sym_index, stack_index, signa, minprinted, plusprinted,
					parprinted, leaderprinted, trailerprinted, isNegative,
					locText);
			break;
		case ')':
			prstack[stack_index.value++] = isNegative ? ')' : ' ';
			break;
		default:
			prstack[stack_index.value++] = currentChar;
			break;
		} /* end of switch */
		lastchar = currentChar;
	}

	var result = new Array( AREASIZE );
	egl.eglx.lang.StringLib.initArray( result );
	var len = 
		egl.eglx.lang.StringLib.rpfs(result, prstack, stack_index.value - 1, trailerprinted, leaderprinted,
			minprinted, plusprinted, parprinted, p_source);

	return result.slice(0, len ).join("");
}
	
/*
 * Function: deccvt
 * 
 * Parameters:
 * np - pointer to a decimal structure containing the DECIMAL value
 * to be converted.
 * ndigit - number of digits to the right of decimal point
 * decpt - pointer to an integer that is the position of the decimal point
 * relative to the beginning of the string.
 * decstr - returned decimal string buffer.
 * decstrlen - length of decimal string buffer.
 */
egl.eglx.lang.StringLib["deccvt"] = function ( number,  signum,  ndigit, decstr) {
	var decpt = 0;
	var seq;

	var index = number.indexOf('.');
	if (index == -1) {
		var buf = new egl.eglx.lang.stringlib.StringBuilder();
		buf.append( number );
		buf.append( '.' );
		
		while ( ndigit >= 50 )
		{
			buf.append( egl.eglx.lang.stringlib.Constants.STRING_50_ZEROS );
			ndigit -= 50;
		}

		if ( ndigit > 0 )
		{
			buf.append( egl.eglx.lang.stringlib.Constants.STRING_50_ZEROS.substring( 0, ndigit ) );
		}
		seq = buf;
	} else {
		if (ndigit == 0)				
			seq = number.substring( 0, index );
		else {
			// See if we have too few or too many decimal digits in the string.
			var decDigitsInNumber = number.length - index - 1;
			if ( decDigitsInNumber == ndigit )
			{
				seq = number;
			}
			else if ( decDigitsInNumber < ndigit )
			{
				var zerosNeeded = ndigit - decDigitsInNumber;
				var buf = new egl.eglx.lang.stringlib.StringBuilder(  );
				buf.append( number );

				while ( zerosNeeded >= 50 )
				{
					buf.append( egl.eglx.lang.stringlib.Constants.STRING_50_ZEROS );
					zerosNeeded -= 50;
				}

				if ( zerosNeeded > 0 )
				{
					buf.append( egl.eglx.lang.stringlib.Constants.STRING_50_ZEROS.substring( 0, zerosNeeded ) );
				}
				seq = buf;
			}
			else
			{
				seq = number.substring( 0, number.length - (decDigitsInNumber - ndigit) );
			}
		}
	}
	
	/*
	 * calculate the correct position of the decimal point. handle the value
	 * 0 differently (since the routine returns empty buffer for this
	 * value).
	 */
	if (signum != 0)
	{
		var length;
		if ( typeof( seq ) == "string" ) {
			length =  seq.length;
		} else {
			length = seq.length();
		}
		decpt = length;
		for (var j = 0, i = 0; i < length; i++) {
			var c = seq.charAt(i);
			if (c == '.')
				decpt = i;
			else
				decstr[j++] = c;
		}
	}
	return decpt;
}

/*
 *
 */
egl.eglx.lang.StringLib["fmtanalysis"] = function ( formatter,  useMonetary ) {
	var i = 1;
	while ( true )
	{
		switch ( formatter[ i ] )
		{
			case 0:
				return i;
				
			case '$':
			case '@':
				useMonetary.value = true;
				break;
		}
		i++;
	}
}

/*
 * chksigns function: Set value of the following global variables according
 * to the format parprinted minprinted leaderprinted trailerprinted
 * plusprinted The idea here is that if the format tells us we are to print
 * a symbol then we try to make sure that we have done so by setting the
 * appropriate flag to FALSE. During calls to prsi, the appropriate flags
 * are set to TRUE. Finally, during the call to rpfs, we check each of these
 * four flags are checked and if any are FALSE, then the outgoing string is
 * filled with *'s (indicating error.)
 */
egl.eglx.lang.StringLib["chksigns"] = function ( formatter,  sym_index,
			 parprinted,  minprinted,  leaderprinted,
			 trailerprinted,  plusprinted,  signa) {
		
	var c = formatter[ 1 ];
	for ( var i = 2; egl.eglx.lang.StringLib.toCharCode(c) != 0; i++ )
	{
		switch ( c ) {
		case '(':
			egl.eglx.lang.StringLib.ldsigna('(', sym_index, signa);
			parprinted.value = false;
			break;
		case '-':
			egl.eglx.lang.StringLib.ldsigna('-', sym_index, signa);
			minprinted.value = false;
			break;
		case '$':
			egl.eglx.lang.StringLib.ldsigna('$', sym_index, signa);
			leaderprinted.value = false;
			break;
		case '@':
			egl.eglx.lang.StringLib.ldsigna('@', sym_index, signa);
			trailerprinted.value = false;
			break;
		case '+':
			egl.eglx.lang.StringLib.ldsigna('+', sym_index, signa);
			plusprinted.value = false;
			break;
		}
		c = formatter[ i ];
	}
}
	
egl.eglx.lang.StringLib["align"] = function ( decpoint_location,  p_source,
			 eedecpoint,  p_frmt,  p_frmtIndex,  rightmost) {
	/*
	 * If we found a decimal point in the format...
	 */
	if (decpoint_location >= 0) {
		/* align using decimal points */
		p_source.ccharArray = eedecpoint.ccharArray;
		p_source.ccharArrayIndex = eedecpoint.ccharArrayIndex;
		for (p_frmtIndex = decpoint_location + 1; 
			egl.eglx.lang.StringLib.toCharCode(p_frmt[p_frmtIndex]) != 0; p_frmtIndex++) 
		{
			if (egl.eglx.lang.StringLib.toCharCode(p_source.ccharArray[p_source.ccharArrayIndex]) == 0) {
				p_source.ccharArray[p_source.ccharArrayIndex] = '0';
			}
			switch (p_frmt[p_frmtIndex]) {
			case '*':
			case '$':
			case '#':
			case '&':
			case '(':
			case '+':
			case '-':
			case '<':
				p_source.ccharArrayIndex++;
			}
		}
	} else {
		/*
		 * align using first character to the left of the decimal
		 */
		p_frmtIndex = rightmost;
		p_source.ccharArray = eedecpoint.ccharArray;
		p_source.ccharArrayIndex = eedecpoint.ccharArrayIndex;
	}
	p_source.ccharArrayIndex--;
	return p_frmtIndex - 1;
}

/*
 * leading function: Call pr to fill in a symbol. If p_source points to the
 * end of a string, then we call pr on the fillchar symbol (unless the
 * symbol is a ' <', which means "left justify"). If p_source does not point
 * to the end of a string, we simply yank another character off of p_source
 * and call pr on that.
 */
egl.eglx.lang.StringLib["leadingNotLT"] = function ( p_source, 
			 prstack,  stack_index,  fchar) {
	if (egl.eglx.lang.StringLib.toCharCode(p_source.ccharArray[p_source.ccharArrayIndex]) == 0) {
		prstack[stack_index.value++] = fchar;
	} else {
		prstack[stack_index.value++] = p_source.ccharArray[p_source.ccharArrayIndex--];
	}
}
	
egl.eglx.lang.StringLib["leadingLT"] = function ( p_source, 
			 prstack,  stack_index) {
	if (egl.eglx.lang.StringLib.toCharCode(p_source.ccharArray[p_source.ccharArrayIndex]) != 0) 
	{
		prstack[stack_index.value++] = p_source.ccharArray[p_source.ccharArrayIndex--];
	}
}

/*
 * prsigns function: Make calls to prsi or pr, depending upon the value
 * pointed to by p_source. If p_source does not point to the end of string,
 * then we simply call pr(*p_source--). Otherwise, we (conditionally) call
 * prsi on a character from the signa array.
 */
egl.eglx.lang.StringLib["prsigns"] = function ( printed,  p_source,
			 prstack,  sym_index,  stack_index,
			signa,  minprinted,  plusprinted,
			 parprinted,  leaderprinted,
			 trailerprinted,  isNegative,  locText) {
	if (egl.eglx.lang.StringLib.toCharCode(p_source.ccharArray[p_source.ccharArrayIndex]) == 0) {
		if (!printed) /* character has not yet been used */
		{
			/*
			 * print according to the character on top of signa
			 */
			sym_index.value--;
			switch (signa[sym_index.value]) {
			case '-':
				egl.eglx.lang.StringLib.prsi(prstack, stack_index, minprinted, '-', ' ', isNegative,
						' ', locText );
				break;
			case '+':
				egl.eglx.lang.StringLib.prsi(prstack, stack_index, plusprinted, '-', '+', isNegative,
						' ', locText);
				break;
			case '(':
				egl.eglx.lang.StringLib.prsi(prstack, stack_index, parprinted, '(', ' ', isNegative,
						' ', locText);
				break;
			case '$':
				egl.eglx.lang.StringLib.prsi(prstack, stack_index, leaderprinted, '$', '$',
						isNegative, ' ', locText);
				break;
			case '@':
				egl.eglx.lang.StringLib.prsi(prstack, stack_index, trailerprinted, '@', '@',
						isNegative, ' ', locText);
				break;
			}
		} else {
			// character has already been used
			prstack[stack_index.value++] = ' ';
		}
	} else /* ...there are symbols yet to be pushed onto the stack */{
		/*
		 * If the symbol in question has yet to be printed
		 */
		if (!printed)
			if (signa[sym_index.value - 1] == '@') /*
																    * ..and
																    * if a
																    * trailing
																    * cur.
																    * sym.
																    */
			{
				/*
				 * Call prsi on it
				 */
				sym_index.value--;
				egl.eglx.lang.StringLib.prsi(prstack, stack_index, trailerprinted, '@', '@',
						isNegative, ' ', locText);
				/* use it */
			} else
				/* Otherwise, just push the symbol */
				prstack[stack_index.value++] = p_source.ccharArray[p_source.ccharArrayIndex--];
	}
}

/*
 * rpfs function: Pop each character off of prstack, and place it in cp. If
 * any of the flags leaderprinted, minprinted, plusprinted, or parprinted
 * are FALSE, this indicates that they should have been printed, but were
 * not (probably because there wasn't enough room for them in the format).
 */
egl.eglx.lang.StringLib["rpfs"] = function ( cp,  prstack,  prIndex,
			 trailerprinted,  leaderprinted,
			 minprinted,  plusprinted,  parprinted,
			 p_source) {
	var cpIndex = 0;
	if (trailerprinted.value && leaderprinted.value
			&& minprinted.value && plusprinted.value
			&& parprinted.value && egl.eglx.lang.StringLib.toCharCode(p_source.ccharArray[p_source.ccharArrayIndex]) == 0) 
	{
		for ( ; prIndex >= 0; cpIndex++, prIndex-- )
		{
			if ( egl.eglx.lang.StringLib.toCharCode(prstack[ prIndex ]) == 0 )
			{
				return cpIndex;
			}
			cp[ cpIndex ] = prstack[ prIndex ];
		}
	} 
	else 
	{
		for ( ; prIndex >= 0; prIndex-- )
		{
			cp[ cpIndex++ ] = '*';
		}
	}
	return cpIndex;
}

/*
 * prsi function: Check to see if the symbol has been printed. If not, make
 * a call to pr, and set the "printed" flag to TRUE. The call made to pr is
 * of the form: pr(pr_sign ? neg: pos); ...unless pr_sign is a '$', in which
 * case we use monfront (see documentation for chksigns)
 */
egl.eglx.lang.StringLib["prsi"] = function ( prstack,  stack_index,
			 printed,  neg,  pos,  isNegative,
			 fillchar, locText ) {
	if (!printed.value) {
		if (neg == '$') /*
						 * push money prefix onto prstack
						 */
		{
			var currencySymbol = locText.getCurrencySymbol();
			var len = currencySymbol.length;
			/*
			 * If there is no leading currency symbol, print a space
			 */
			if ( len == 1 )
			{
				prstack[stack_index.value++] = currencySymbol.charAt( 0 );
			}
			else if ( len == 0 )
			{
				prstack[stack_index.value++] = ' ';
			}
			else {
				while (len > 0)
					prstack[stack_index.value++] = currencySymbol.charAt(--len);
			}
		} else if (neg == '@') {
			var currencySymbol = locText.getCurrencySymbol();
			var len = currencySymbol.length;
			/*
			 * If there is no trailing currency symbol, print a space
			 */
			if ( len == 1 )
			{
				egl.eglx.lang.StringLib.pl(prstack, stack_index, currencySymbol.charAt( 0 ));
			}
			else if ( len == 0 )
			{
				egl.eglx.lang.StringLib.pl(prstack, stack_index, ' ');
			}
			else 
			{
				for (var i = 0; i < len; i++)
					egl.eglx.lang.StringLib.pl(prstack, stack_index, currencySymbol.charAt(i));
			}
		} else
			prstack[stack_index.value++] = isNegative ? neg : pos;
		printed.value = true;
	} else {
		if (fillchar != '<')
			prstack[stack_index.value++] = fillchar;
	}
}

/*
 * pl function: Push a character onto the prstack. Eventually these will get
 * popped off by a call to rpfs.
 */
egl.eglx.lang.StringLib["pl"] = function (prstack,  stack_index,  c) {
	var index = stack_index.value;
	while (index > 0)
		prstack[index] = prstack[--index];
	prstack[index] = c;
	stack_index.value++;
}

/*
 * ldsigna function: Load c into the signa array (if it has not already been
 * loaded)
 */
egl.eglx.lang.StringLib["ldsigna"] = function (c, sym_index,signa) {

	if ( sym_index.value == 0 || c != signa[ sym_index.value - 1 ] )
	{
		signa[ sym_index.value++ ] = c;
	}
}

egl.eglx.lang.StringLib["getChars"] = function (source, sbegin, send, dest, dbegin) {
	var result = source.substr( sbegin, send ).split("");
	for( var i = 0; i < result.length; i ++, dbegin++ ) {
		dest[dbegin] = result[i];
	}
}

egl.eglx.lang.StringLib["initArray"] = function (source) {
	for( var i = 0; i < source.length; i ++ ) {
		source[i] = 0;
	}
}

egl.eglx.lang.StringLib["toCharCode"] = function (source) {
	if ( source.charCodeAt ) {
		return source.charCodeAt(0);
	}
	return 0;
}

egl.defineClass( "eglx.lang.stringlib", "CBoolean", {
	"constructor" : function(value) {
		this.value = value;
	}
});

egl.defineClass( "eglx.lang.stringlib", "CCharArray", {
	"constructor" : function(ccharArraySize) {
		this.ccharArray = new Array(ccharArraySize);
		egl.eglx.lang.StringLib.initArray( this.ccharArray );
		this.ccharArrayIndex = 0;
	}
});

egl.defineClass( "eglx.lang.stringlib", "CInteger", {
	"constructor" : function(value) {
		this.value = value;
	}
});

egl.defineClass( "eglx.lang.stringlib", "StringBuilder", {
	"constructor" : function(value) {
		this.stringsArray = new Array(""); 
		this.append(value); 
	}
});

egl.eglx.lang.stringlib.StringBuilder.prototype.append = function (value) { 
	if (value) { 
		this.stringsArray.push(value); 
	} 
};

egl.eglx.lang.stringlib.StringBuilder.prototype.length = function () { 
	return this.stringsArray.join("").length; 
};

egl.eglx.lang.stringlib.StringBuilder.prototype.charAt = function (i) { 
	return this.stringsArray.join("").charAt(i); 
};

egl.defineClass( "eglx.lang.stringlib", "LocalizedText", {
	"constructor" : function() {

	}
});

egl.eglx.lang.stringlib.LocalizedText.prototype.getSeparatorSymbol = function () { 
	return ","; 
};

egl.eglx.lang.stringlib.LocalizedText.prototype.getMonetaryDecimalSeparator = function () { 
	return ","; 
};

egl.eglx.lang.stringlib.LocalizedText.prototype.getDecimalSymbol = function () { 
	return "."; 
};

egl.eglx.lang.stringlib.LocalizedText.prototype.getCurrencySymbol = function () { 
	return "$"; 
};


egl.defineClass( "eglx.lang.stringlib", "Constants", {
	"constructor" : function() {

	}
});

egl.eglx.lang.stringlib.Constants.STRING_50_ZEROS = "00000000000000000000000000000000000000000000000000";
