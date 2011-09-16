/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
egl.defineClass( "eglx.lang", "StringLib", {
	"constructor" : function() {
	}
});

	// TODO sbg need the format functions....
	

egl.eglx.lang.StringLib["getNextToken"] = function(/*string*/source, /*int*/
		index, /*string*/delimiters) {
	// Adjust for EGL's 1-based indexing
	var startIndex = (index - 1) / 2;

	//Skip leading tokens
	while (delimiters.indexOf(source.charAt(startIndex)) >= 0
			&& startIndex < source.length)
		startIndex++;

	// If we're at the end of the substring, the search failed
	if (startIndex >= source.length) {
		throw egl.createInvalidIndexException("CRRUI2021E", [ index ],
				index);
	}

	// Now we know we've found the beginning of a token. Find its end.
	var tokenEnd = startIndex + 1;

	// Check each byte to see if it's the start of a one-byte delimiter.
	while (delimiters.indexOf(source.charAt(tokenEnd)) < 0
			&& tokenEnd < source.length)
		tokenEnd++;

	// Store the end of the token in index and adjust for doubling.
	index = (tokenEnd * 2) + 1;

	if (outAssignFn)
		outAssignFn(source, index);

	return source.slice(startIndex, tokenEnd);
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
			next = this.getNextToken(source, index, delimiters, endAssign);
		}
	} catch (exception) {
		//out of tokens
	} finally {
		return tokens;
	}
};

egl.eglx.lang.StringLib["fromCharCode"] = function(/*integer*/i) {
	return String.fromCharCode(i); // TODO should we simply alias somehow?
};

egl.eglx.lang.StringLib["spaces"] = function(/*integer*/i) {
	//Returns a string of a specified length.
	var s = [];
	for ( var n = 0; n < i; ++n) {
		s.push(' ');
	}
	return (s.join(''));
};

