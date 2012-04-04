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
package org.eclipse.edt.debug.core.java;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.debug.core.DebugException;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIReferenceType;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;

/**
 * Utility class for handling SMAP data.
 */
@SuppressWarnings("restriction")
public class SMAPUtil
{
	private static final SMAPVariableInfo[] EMPTY_VARIABLE_INFOS = {};
	
	private SMAPUtil()
	{
		// No instances.
	}
	
	/**
	 * The SMAP will contain variable information after the normal end of the SMAP ("*E"). Any entries that are malformed will be ignored.
	 * 
	 * @param smap The SMAP retrieved from the class.
	 * @param frame The EGL-wrapped stack frame, if we are parsing variables for a stack frame. Pass in null if the SMAP is for a value instead. If we
	 *            find function information in the SMAP corresponding to the frame, the information will be passed to the frame.
	 * @return an array of {@link SMAPVariableInfo}s, never null.
	 */
	public static SMAPVariableInfo[] parseVariables( String smap, IEGLJavaStackFrame frame )
	{
		if ( smap == null || smap.trim().length() == 0 )
		{
			return EMPTY_VARIABLE_INFOS;
		}
		
		List<SMAPVariableInfo> vars = new ArrayList<SMAPVariableInfo>();
		
		// Skip ahead to the variable section.
		int idx = smap.indexOf( "*E" ); //$NON-NLS-1$
		if ( idx != -1 )
		{
			String variableSection = smap.substring( idx + 2 ).trim();
			StringTokenizer tok = new StringTokenizer( variableSection, "\n" ); //$NON-NLS-1$
			
			String javaFrameSignature = null;
			if ( frame != null )
			{
				try
				{
					javaFrameSignature = frame.getJavaStackFrame().getMethodName() + ";" + frame.getJavaStackFrame().getSignature(); //$NON-NLS-1$ //$NON-NLS-2$
				}
				catch ( DebugException de )
				{
				}
			}
			
			String currentFunction = null;
			StringTokenizer semiTok;
			int pound;
			int semicolon;
			int tokenLen;
			int line;
			boolean specialGlobalType;
			while ( tok.hasMoreTokens() )
			{
				// If we encounter anything unexpected, skip the entry. Shouldn't be a problem so long
				// as no one's mucking around with the SMAP.
				String next = tok.nextToken().trim();
				tokenLen = next.length();
				
				if ( tokenLen == 0 )
				{
					continue;
				}
				
				if ( "*X".equals( next ) ) //$NON-NLS-1$
				{
					// We're done processing.
					break;
				}
				
				specialGlobalType = next.charAt( 0 ) == '*';
				
				try
				{
					if ( specialGlobalType )
					{
						line = -1;
						semicolon = next.indexOf( ';' );
					}
					else
					{
						pound = next.indexOf( '#' );
						if ( pound == -1 )
						{
							continue;
						}
						
						line = Integer.parseInt( next.substring( 0, pound ) );
						semicolon = next.indexOf( ';', pound );
					}
					
					if ( semicolon == -1 )
					{
						continue;
					}
					
					if ( specialGlobalType )
					{
						// It's a global type that doesn't have a specific line (library, table, form, program parameter).
						semiTok = new StringTokenizer( next.substring( semicolon + 1 ), ";" ); //$NON-NLS-1$
						int tokenCount = semiTok.countTokens();
						if ( tokenCount > 0 )
						{
							String eglName;
							String javaName;
							String type;
							
							// Required token: egl name
							eglName = semiTok.nextToken();
							
							// Next token: java name (defaults to egl name)
							if ( tokenCount > 1 )
							{
								javaName = semiTok.nextToken();
							}
							else
							{
								javaName = eglName;
							}
							
							// Next token: egl type (defaults to egl name)
							if ( tokenCount > 2 )
							{
								type = semiTok.nextToken();
							}
							else
							{
								type = eglName;
							}
							
							vars.add( new SMAPVariableInfo( eglName, javaName, type, line, next ) );
						}
					}
					else if ( tokenLen > semicolon + 2 && next.charAt( semicolon + 1 ) == 'F' && next.charAt( semicolon + 2 ) == ':' )
					{
						// It's a function line. First portion is the egl name, second is the java signature.
						int semicolon2 = next.indexOf( ';', semicolon + 1 );
						if ( semicolon2 != -1 )
						{
							String eglName = next.substring( semicolon + 3, semicolon2 );
							currentFunction = next.substring( semicolon2 + 1 );
							
							if ( frame != null && currentFunction != null && currentFunction.equals( javaFrameSignature ) )
							{
								frame.setSMAPFunctionInfo( new SMAPFunctionInfo( eglName, currentFunction, line, next ) );
							}
						}
					}
					else
					{
						// It's a variable line.
						semiTok = new StringTokenizer( next.substring( semicolon + 1 ), ";" ); //$NON-NLS-1$
						if ( semiTok.countTokens() == 3 )
						{
							vars.add( new SMAPVariableInfo( semiTok.nextToken(), semiTok.nextToken(), semiTok.nextToken(), line, currentFunction,
									next ) );
						}
					}
				}
				catch ( NumberFormatException nfe )
				{
					// Ignore the entry, but log the error.
					EDTDebugCorePlugin.log( nfe );
				}
			}
		}
		
		return vars.toArray( new SMAPVariableInfo[ vars.size() ] );
	}
	
	/**
	 * Returns the SMAP information for the Java type. It will never be null. If there is no SMAP information, or the Java type was not a type that we
	 * recognize (currently only JDIReferenceType), then this will return blank. When the class file doesn't contain SMAP information we'll try to
	 * read in *.eglsmap files from the disk.
	 * 
	 * @param type The Java type.
	 * @param smapFileCache A cache of class name to SMAP data from *.eglsmap files read off disk, or null if we should not check for such files.
	 * @return the SMAP information.
	 */
	public static String getSMAP( IJavaType type, Map<String, String> smapFileCache )
	{
		String smap = null;
		if ( type instanceof IJavaReferenceType )
		{
			if ( type instanceof JDIReferenceType )
			{
				Type underlyingType = ((JDIReferenceType)type).getUnderlyingType();
				if ( underlyingType instanceof ReferenceType )
				{
					try
					{
						smap = ((ReferenceType)underlyingType).sourceDebugExtension();
					}
					catch ( AbsentInformationException e )
					{
					}
					catch ( UnsupportedOperationException e )
					{
					}
				}
			}
			
			if ( smap == null && smapFileCache != null )
			{
				// No SMAP in the class file; check if the *.eglsmap file can be read in directly.
				// This only works for *.eglsmap files in the classpath of the debug IDE code (i.e. we don't have
				// the classpath of the running application). This allows us to support SMAPs in the Java runtime,
				// since currently their .class files do not contain SMAP data.
				try
				{
					String className = ((IJavaReferenceType)type).getName();
					
					if ( smapFileCache.containsKey( className ) )
					{
						smap = smapFileCache.get( className );
					}
					else
					{
						try
						{
							Class clazz = Class.forName( className );
							String eglsmap = "/" + className.replace( '.', '/' ) + "." + IEGLDebugCoreConstants.SMAP_EXTENSION; //$NON-NLS-1$ //$NON-NLS-2$
							InputStream is = clazz.getResourceAsStream( eglsmap );
							if ( is != null )
							{
								BufferedInputStream bis = new BufferedInputStream( is );
								byte[] b = new byte[ bis.available() ];
								bis.read( b, 0, b.length );
								smap = new String( b, "UTF-8" ); //$NON-NLS-1$
								smapFileCache.put( className, smap );
							}
							else
							{
								smapFileCache.put( className, null );
							}
						}
						catch ( Throwable t )
						{
							smapFileCache.put( className, null );
						}
					}
				}
				catch ( Throwable t )
				{
					// Ignore.
				}
			}
		}
		
		return smap == null
				? "" : smap.trim(); //$NON-NLS-1$
	}
	
	/**
	 * @return true if the given value's default stratum equals {@link IEGLDebugCoreConstants#EGL_STRATUM}
	 */
	public static boolean isEGLStratum( IJavaValue value )
	{
		try
		{
			IJavaType type = value.getJavaType();
			return type instanceof IJavaReferenceType && IEGLDebugCoreConstants.EGL_STRATUM.equals( ((IJavaReferenceType)type).getDefaultStratum() );
		}
		catch ( DebugException e )
		{
		}
		
		return false;
	}
	
	/**
	 * @return true if the given variable's default stratum equals {@link IEGLDebugCoreConstants#EGL_STRATUM}
	 */
	public static boolean isEGLStratum( IJavaVariable variable )
	{
		try
		{
			IJavaType type = variable.getJavaType();
			return type instanceof IJavaReferenceType && IEGLDebugCoreConstants.EGL_STRATUM.equals( ((IJavaReferenceType)type).getDefaultStratum() );
		}
		catch ( DebugException e )
		{
		}
		
		return false;
	}
	
	/**
	 * @return true if the given frame's default stratum equals {@link IEGLDebugCoreConstants#EGL_STRATUM}
	 */
	public static boolean isEGLStratum( IJavaStackFrame frame )
	{
		try
		{
			IJavaReferenceType refType = frame.getReferenceType();
			return refType != null && IEGLDebugCoreConstants.EGL_STRATUM.equals( refType.getDefaultStratum() );
		}
		catch ( DebugException e )
		{
		}
		
		return false;
	}
	
	/**
	 * @return true if the smap value has a default stratum of {@link IEGLDebugCoreConstants#EGL_STRATUM}
	 */
	public static boolean isEGLStratum( String smap )
	{
		if ( smap.length() > 0 )
		{
			// Third line has the default stratum.
			int index = smap.indexOf( '\n' );
			if ( index != -1 )
			{
				index = smap.indexOf( '\n', index + 1 );
				if ( index != -1 )
				{
					int end = smap.indexOf( '\n', index + 1 );
					if ( end != -1 )
					{
						return IEGLDebugCoreConstants.EGL_STRATUM.equals( smap.substring( index + 1, end ) );
					}
				}
			}
		}
		return false;
	}
}
