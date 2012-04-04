/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

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
