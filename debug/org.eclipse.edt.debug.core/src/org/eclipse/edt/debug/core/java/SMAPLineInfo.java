/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.core.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the parsed SMAP line mappings, for both directions of mappings. This is only used when the target VM does not support JSR-45
 * (SourceDebugExtension). When JSR-45 support is available, JDT does all this work for us.
 */
public class SMAPLineInfo
{
	/**
	 * EGL lines map to one or more Java lines.
	 */
	private final Map<Integer, List<Integer>> eglToJavaLines;
	
	/**
	 * Java lines map to one EGL line.
	 */
	private final Map<Integer, Integer> javaToEGLLines;
	
	public SMAPLineInfo()
	{
		this.eglToJavaLines = new HashMap<Integer, List<Integer>>();
		this.javaToEGLLines = new HashMap<Integer, Integer>();
	}
	
	/**
	 * @param eglLine The EGL line to look up.
	 * @return the list of Java lines that maps to the EGL line, or null if there is no mapping.
	 */
	public List<Integer> getJavaLines( int eglLine )
	{
		return this.eglToJavaLines.get( eglLine );
	}
	
	/**
	 * @param eglLine The EGL line to look up.
	 * @return the first Java line that maps to the EGL line, or -1 if there is no mapping.
	 */
	public int getFirstJavaLine( int eglLine )
	{
		List<Integer> javaLines = this.eglToJavaLines.get( eglLine );
		if ( javaLines == null || javaLines.size() == 0 )
		{
			return -1;
		}
		return javaLines.get( 0 );
	}
	
	/**
	 * @param javaLine The Java line to look up.
	 * @return the corresponding EGL line, or -1 if there is no mapping.
	 */
	public int getEGLLine( int javaLine )
	{
		if ( javaToEGLLines.containsKey( javaLine ) )
		{
			return javaToEGLLines.get( javaLine );
		}
		return -1;
	}
	
	/**
	 * Given the SMAP line entry attributes, this calculates the line mappings.
	 * 
	 * @param repeatCount The repeat count of the source lines.
	 * @param eglStartLine The start of the source lines.
	 * @param javaStartLine The start of the output lines.
	 * @param javaLineIncrement The increment of the output lines (number of output lines per source line).
	 */
	public void addMappings( int repeatCount, int eglStartLine, int javaStartLine, int javaLineIncrement )
	{
		for ( int i = 0; i < repeatCount; i++ )
		{
			boolean fastAdd = false;
			List<Integer> javaLines = eglToJavaLines.get( eglStartLine + i );
			if ( javaLines == null )
			{
				javaLines = new ArrayList<Integer>();
				eglToJavaLines.put( eglStartLine + i, javaLines );
				fastAdd = true;
			}
			
			int nextJavaLineStart = javaStartLine + (i * javaLineIncrement);
			for ( int j = 0; j < javaLineIncrement || (j == 0 && javaLineIncrement == 0); j++ ) // increment of 0 is valid and means '1 line'
			{
				if ( fastAdd || !javaLines.contains( nextJavaLineStart + j ) )
				{
					javaLines.add( nextJavaLineStart + j );
				}
				if ( !javaToEGLLines.containsKey( nextJavaLineStart + j ) )
				{
					javaToEGLLines.put( nextJavaLineStart + j, eglStartLine + i );
				}
			}
		}
	}
}
