/*
 * Licensed Materials - Property of IBM
 *
 * Copyright IBM Corporation 2008, 2010. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA DP Schedule Contract with IBM Corp.
 */

/* TODO sbg This entire file is a temporary part of the transition to the
 * "new" runtime....delete prior to 0.7.0
 */
egl.convertIntToDecimal = function( x, limit, creatx )
{
	return egl.convertDecimalToDecimal( new egl.javascript.BigDecimal( String( x ) ), 0, limit, creatx );
};


/* TODO sbg Delete -- no longer needed
egl.egl.core.StrLib.$inst.characterLen = function (s)
{
	return egl.egl.core.StrLib.$inst.textLen(s);
}; */
