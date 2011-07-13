/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.jdt.debug.core.IJavaType;
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
	public static SMAPVariableInfo[] parseVariables( String smap, EGLJavaStackFrame frame )
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
							
							vars.add( new SMAPVariableInfo( eglName, javaName, type, line ) );
						}
					}
					else if ( tokenLen > semicolon + 2 && next.charAt( semicolon + 1 ) == 'F' && next.charAt( semicolon + 2 ) == ':' )
					{
						// It's a function line. First portion is the egl name, second is the java signature.
						int semicolon2 = next.indexOf( ';', semicolon + 1 );
						if (semicolon2 != -1){
							String eglName = next.substring( semicolon + 3, semicolon2 );
							currentFunction = next.substring( semicolon2 + 1 );
							
							if ( frame != null && currentFunction != null && currentFunction.equals( javaFrameSignature ) )
							{
								frame.setSMAPFunctionInfo( new SMAPFunctionInfo( eglName, currentFunction, line ) );
							}
						}
					}
					else
					{
						// It's a variable line.
						semiTok = new StringTokenizer( next.substring( semicolon + 1 ), ";" ); //$NON-NLS-1$
						if ( semiTok.countTokens() == 3 )
						{
							vars.add( new SMAPVariableInfo( semiTok.nextToken(), semiTok.nextToken(), semiTok.nextToken(), line, currentFunction ) );
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
	 * recognize (currently only JDIReferenceType), then this will return blank.
	 * 
	 * @param type The Java type.
	 * @return the SMAP information.
	 */
	public static String getSMAP( IJavaType type )
	{
		String smap = null;
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
			}
		}
		
		return smap == null ? "" : smap.trim(); //$NON-NLS-1$
	}
	
	/**
	 * Given a set of Java variables, and SMAP variable information, this will determine which Java variables should be displayed. Any variables to be
	 * displayed will be wrapped inside an EGLJavaVariable.
	 * 
	 * @param javaVariables The Java variables, not null.
	 * @param existingEGLVars EGL variables that were processed from a previous call, possibly null.
	 * @param infos The variable information from the SMAP, not null.
	 * @param frame The active EGL-wrapped stack frame, possible null.
	 * @param target The EGL-wrapped debug target.
	 * @param skipLocals True if local variables should be omitted.
	 * @return the filtered, EGL-wrapped variables.
	 * @throws DebugException
	 */
	public static List<EGLJavaVariable> filterAndWrapVariables( IVariable[] javaVariables, EGLJavaVariable[] existingEGLVars,
			SMAPVariableInfo[] infos, EGLJavaStackFrame frame, EGLJavaDebugTarget target, boolean skipLocals ) throws DebugException
	{
		List<EGLJavaVariable> newEGLVariables = new ArrayList<EGLJavaVariable>( javaVariables.length );
		
		// The frame will be null when we're getting variables of a value. Field variables are never local,
		// so this information isn't needed. Fields of records are always "visible", global fields of other logic
		// parts like libraries are always "visible".
		String javaFrameSignature = frame == null ? "" : frame.getJavaStackFrame().getMethodName() + ";" + frame.getJavaStackFrame().getSignature(); //$NON-NLS-1$ //$NON-NLS-2$
		int currentLine = frame == null ? -1 : frame.getLineNumber();
		int frameStartLine = frame == null || frame.getSMAPFunctionInfo() == null ? -1 : frame.getSMAPFunctionInfo().lineDeclared;
		
		for ( int i = 0; i < javaVariables.length; i++ )
		{
			IJavaVariable javaVar = (IJavaVariable)javaVariables[ i ];
			String javaName = javaVar.getName();
			
			boolean addNew = true;
			if ( existingEGLVars != null )
			{
				for ( int j = 0; j < existingEGLVars.length; j++ )
				{
					if ( existingEGLVars[ j ].getJavaVariable() == javaVar )
					{
						// Reuse this variable.
						newEGLVariables.add( existingEGLVars[ j ] );
						addNew = false;
						break;
					}
				}
			}
			
			if ( addNew )
			{
				if ( javaVar.isLocal() )
				{
					if ( !skipLocals )
					{
						SMAPVariableInfo matchingInfo = null;
						for ( int j = 0; j < infos.length; j++ )
						{
							SMAPVariableInfo info = infos[ j ];
							if ( info.javaName.equals( javaName ) )
							{
								// Validate the signature and make sure the info is in scope. There could be multiple
								// local variables of different types with the same name, in different scopes within the
								// function.
								if ( info.javaMethodSignature != null && info.javaMethodSignature.equals( javaFrameSignature )
										&& (currentLine >= info.lineDeclared || currentLine == frameStartLine) )
								{
									if ( matchingInfo == null || matchingInfo.lineDeclared < info.lineDeclared )
									{
										matchingInfo = info;
									}
									
									// Don't break - keep looping to see if there's a better match. We want the info
									// with the highest line number that's within scope.
								}
							}
						}
						
						if ( matchingInfo != null )
						{
							newEGLVariables.add( new EGLJavaVariable( target, javaVar, matchingInfo ) );
						}
					}
				}
				else
				{
					if ( "this".equals( javaName ) ) //$NON-NLS-1$
					{
						newEGLVariables.add( new EGLJavaFunctionContainerVariable( target, javaVar ) );
						continue;
					}
					
					SMAPVariableInfo matchingInfo = null;
					for ( int j = 0; j < infos.length; j++ )
					{
						SMAPVariableInfo info = infos[ j ];
						if ( info.javaName.equals( javaName ) )
						{
							// Make sure it has no signature
							if ( info.javaMethodSignature == null )
							{
								matchingInfo = info;
								break;
							}
						}
					}
					
					if ( matchingInfo != null )
					{
						newEGLVariables.add( new EGLJavaVariable( target, javaVar, matchingInfo ) );
					}
				}
			}
		}
		return newEGLVariables;
	}
}
