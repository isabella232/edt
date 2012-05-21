/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.core.java;

import org.eclipse.edt.debug.core.java.SMAPLineInfo;

/**
 * Lightweight parser to get EGL-Java line mappings. This is not a full, general purpose SMAP parser. It expects one EGL file per SMAP and no nested
 * SMAPS. In the future more support may be added as necessary.
 */
public class SMAPLineParser
{
	public static SMAPLineInfo parse( String smap )
	{
		if ( smap.length() == 0 )
		{
			return null;
		}
		
		// Skip ahead to '*L' which indicates the beginning of line mappings. Once a line starting with '*' is found, the mappings are done.
		int index = smap.indexOf( "*L" ); //$NON-NLS-1$
		if ( index == -1 )
		{
			return null;
		}
		
		// Move to the start of the next line.
		index = smap.indexOf( '\n', index );
		if ( index == -1 )
		{
			return null;
		}
		index++;
		
		String line;
		int lineBreak;
		
		// At this point, index will represent the beginning of a line, and lineBreak the end.
		SMAPLineInfo lineInfo = new SMAPLineInfo();
		while ( true )
		{
			lineBreak = smap.indexOf( '\n', index );
			if ( lineBreak != -1 )
			{
				line = smap.substring( index, lineBreak );
				
				if ( line.length() > 0 && line.charAt( 0 ) == '*' )
				{
					break;
				}
				
				parseLine( line, lineInfo );
				
				index = lineBreak + 1;
			}
			else
			{
				break;
			}
		}
		
		return lineInfo;
	}
	
	private static void parseLine( String line, SMAPLineInfo lineInfo )
	{
		// Format: 'InputStartLine # LineFileID , RepeatCount : OutputStartLine , OutputLineIncrement'
		// where all but 'InputStartLine : OutputStartLine' are optional.
		
		// Since this parser assumes it's one EGL file per SMAP we do not need to look at '# LineFileID'.
		
		int colon = line.indexOf( ':' );
		if ( colon == -1 )
		{
			return;
		}
		
		int eglStartLine;
		int repeatCount = 1; // defaults to 1
		int javaStartLine;
		int javaLineIncrement = 1; // defaults to 1
		
		try
		{
			// 1. Gather the information.
			int pound = line.indexOf( '#' );
			int comma = line.indexOf( ',' );
			
			if ( pound == -1 )
			{
				if ( comma != -1 && comma < colon )
				{
					// 12,3:
					eglStartLine = Integer.parseInt( line.substring( 0, comma ) );
					repeatCount = Integer.parseInt( line.substring( comma + 1, colon ) );
				}
				else
				{
					// 12:
					eglStartLine = Integer.parseInt( line.substring( 0, colon ) );
				}
			}
			else
			{
				eglStartLine = Integer.parseInt( line.substring( 0, pound ) );
				if ( comma != -1 && comma < colon )
				{
					// 12#1,3:
					repeatCount = Integer.parseInt( line.substring( comma + 1, colon ) );
				}
			}
			
			if ( comma != -1 && comma < colon )
			{
				comma = line.indexOf( ',', comma + 1 );
			}
			
			if ( comma == -1 )
			{
				javaStartLine = Integer.parseInt( line.substring( colon + 1 ) );
			}
			else
			{
				javaStartLine = Integer.parseInt( line.substring( colon + 1, comma ) );
				javaLineIncrement = Integer.parseInt( line.substring( comma + 1 ) );
			}
			
			lineInfo.addMappings( repeatCount, eglStartLine, javaStartLine, javaLineIncrement );
		}
		catch ( NumberFormatException nfe )
		{
		}
	}
}
