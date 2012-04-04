/*******************************************************************************
 * Copyright © 2006, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.util;


public class StringUtil{
	
	public static String clip( String characterItem ){
		int length = characterItem.length();
		if ( length > 0 )
		{
			int startingIdx = length - 1;
			boolean charsDeleted = false;
			while ( startingIdx >= 0 )
			{
				char c = characterItem.charAt( startingIdx );
				if ( c <= ' ' || c == '\u3000' )
				{
					charsDeleted = true;
					startingIdx--;
				}
				else
				{
					break;
				}
			}
			if ( charsDeleted )
			{
				return characterItem.substring( 0, startingIdx + 1 );
			}
		}
		return characterItem;
	}
	

}
