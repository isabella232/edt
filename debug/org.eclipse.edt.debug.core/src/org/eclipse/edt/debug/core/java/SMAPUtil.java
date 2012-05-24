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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;
import org.eclipse.debug.core.sourcelookup.containers.ZipEntryStorage;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.edt.debug.internal.core.java.SMAPLineParser;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
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
	 * @param target The EGL debug target.
	 * @param type The Java type.
	 * @return the SMAP information.
	 */
	public static String getSMAP( IEGLJavaDebugTarget target, IJavaType type )
	{
		String smap = null;
		if ( type instanceof IJavaReferenceType )
		{
			if ( target.supportsSourceDebugExtension() && type instanceof JDIReferenceType )
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
			
			if ( smap == null )
			{
				// No SMAP in the class file; check if the *.eglsmap file can be read in directly.
				// This only works for *.eglsmap files in the classpath of the debug IDE code (i.e. we don't have
				// the classpath of the running application). This allows us to support SMAPs in the Java runtime,
				// since currently their .class files do not contain SMAP data.
				try
				{
					smap = getSMAP( target, ((IJavaReferenceType)type).getName() );
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
	 * Returns the SMAP information for the class name by reading the file off the disk. If there is no SMAP information this will return blank.
	 * 
	 * @param target The EGL debug target.
	 * @param className The qualified name of the class.
	 * @return the SMAP information.
	 */
	public static String getSMAP( IEGLJavaDebugTarget target, String className )
	{
		SMAPFileCache smapFileCache = target.getSMAPFileCache();
		if ( smapFileCache == null )
		{
			return ""; //$NON-NLS-1$
		}
		
		String smap = null;
		if ( smapFileCache.containsSMAP( className ) )
		{
			smap = smapFileCache.getSMAP( className );
		}
		else
		{
			// Remove inner classes since we're looking for OuterClass.eglsmap.
			int inner = className.indexOf( '$' );
			if ( inner != -1 )
			{
				className = className.substring( 0, inner );
			}
			
			// Try finding it from the user's workspace, including classpath entries like jars, runtime containers, etc.
			String workspacePath = null;
			ISourceLocator locator = target.getLaunch().getSourceLocator();
			if ( locator instanceof ISourceLookupDirector )
			{
				InputStream is = null;
				Object src = ((ISourceLookupDirector)locator).getSourceElement( className.replace( '.', '/' ) + ".java" ); //$NON-NLS-1$
				if ( src instanceof IJavaElement )
				{
					IJavaElement parent = ((IJavaElement)src).getParent();
					if ( parent instanceof IPackageFragment )
					{
						String simpleSMAPName;
						int lastDot = className.lastIndexOf( '.' );
						if ( lastDot == -1 )
						{
							simpleSMAPName = className + "." + IEGLDebugCoreConstants.SMAP_EXTENSION; //$NON-NLS-1$
						}
						else
						{
							simpleSMAPName = className.substring( lastDot + 1 ) + "." + IEGLDebugCoreConstants.SMAP_EXTENSION; //$NON-NLS-1$
						}
						
						try
						{
							Object[] kids = ((IPackageFragment)parent).getNonJavaResources();
							for ( Object kid : kids )
							{
								if ( kid instanceof IStorage && ((IStorage)kid).getName().equals( simpleSMAPName ) )
								{
									is = ((IStorage)kid).getContents();
									workspacePath = ((IStorage)kid).getFullPath().toString();
									break;
								}
							}
						}
						catch ( CoreException ce )
						{
							EDTDebugCorePlugin.log( ce );
						}
					}
				}
				else if ( src instanceof ZipEntryStorage )
				{
					ZipEntryStorage storage = (ZipEntryStorage)src;
					ZipFile zipFile = storage.getArchive();
					ZipEntry zipEntry = storage.getZipEntry();
					String name = zipEntry.getName();
					if ( name.endsWith( ".java" ) ) //$NON-NLS-1$
					{
						zipEntry = zipFile.getEntry( name.substring( 0, name.length() - 4 ) + IEGLDebugCoreConstants.SMAP_EXTENSION );
						if ( zipEntry != null )
						{
							try
							{
								is = zipFile.getInputStream( zipEntry );
							}
							catch ( IOException ioe )
							{
								EDTDebugCorePlugin.log( ioe );
							}
						}
					}
				}
				else if ( src instanceof LocalFileStorage )
				{
					// Filesystem-absolute path.
					IPath smapPath = ((IStorage)src).getFullPath().removeFileExtension().addFileExtension( IEGLDebugCoreConstants.SMAP_EXTENSION );
					File smapFile = smapPath.toFile();
					if ( smapFile.exists() )
					{
						try
						{
							is = new FileInputStream( smapFile );
							IPath workspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation();
							if ( workspaceRoot.isPrefixOf( smapPath ) )
							{
								workspacePath = "/" + smapPath.removeFirstSegments( workspaceRoot.segmentCount() ).toString();
							}
							
						}
						catch ( FileNotFoundException fnfe )
						{
							EDTDebugCorePlugin.log( fnfe );
						}
					}
				}
				else if ( src instanceof IStorage )
				{
					// This will only work if it's inside the workspace.
					IPath smapPath = ((IStorage)src).getFullPath().removeFileExtension().addFileExtension( IEGLDebugCoreConstants.SMAP_EXTENSION );
					IFile smapFile = ResourcesPlugin.getWorkspace().getRoot().getFile( smapPath );
					if ( smapFile.exists() )
					{
						try
						{
							is = smapFile.getContents( true );
							workspacePath = smapPath.toString();
						}
						catch ( CoreException ce )
						{
							EDTDebugCorePlugin.log( ce );
						}
					}
				}
				
				if ( is != null )
				{
					try
					{
						BufferedInputStream bis = new BufferedInputStream( is );
						byte[] b = new byte[ bis.available() ];
						bis.read( b, 0, b.length );
						smap = new String( b, "UTF-8" ); //$NON-NLS-1$
					}
					catch ( Exception e )
					{
						EDTDebugCorePlugin.log( e );
					}
					finally
					{
						try
						{
							is.close();
						}
						catch ( IOException ioe )
						{
						}
					}
				}
			}
			smapFileCache.addEntry( className, smap, workspacePath );
		}
		return smap == null
				? "" //$NON-NLS-1$
				: smap;
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
	public static boolean isEGLStratum( IJavaStackFrame frame, IEGLJavaDebugTarget target )
	{
		if ( target.supportsSourceDebugExtension() )
		{
			try
			{
				IJavaReferenceType refType = frame.getReferenceType();
				return refType != null && IEGLDebugCoreConstants.EGL_STRATUM.equals( refType.getDefaultStratum() );
			}
			catch ( DebugException e )
			{
			}
		}
		else
		{
			try
			{
				String smap = SMAPUtil.getSMAP( target, frame.getReferenceType().getName() );
				return SMAPUtil.isEGLStratum( smap );
			}
			catch ( DebugException e )
			{
			}
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
	
	/**
	 * Given SMAP data, this returns the full path of the source file, or if that's not available it returns the file name. If the SMAP data is not
	 * valid then this returns null.
	 * 
	 * @param smap The SMAP data.
	 * @return the path to the source file, possibly null.
	 */
	public static String getFilePath( String smap )
	{
		if ( smap.length() > 0 )
		{
			int index = smap.indexOf( "*F" ); //$NON-NLS-1$
			if ( index != -1 )
			{
				// Skip this line and the next, to get the full path.
				index = smap.indexOf( '\n', index );
				
				if ( index != -1 )
				{
					// If the next line starts with '+' then the following line has the full path.
					// Otherwise no full path was given and all we know is the file name.
					if ( smap.charAt( index + 1 ) == '+' )
					{
						index = smap.indexOf( '\n', index + 1 );
						if ( index != -1 )
						{
							int end = smap.indexOf( '\n', index + 1 );
							if ( end != -1 )
							{
								return smap.substring( index + 1, end );
							}
						}
					}
					else
					{
						index = smap.indexOf( ' ', index + 1 );
						if ( index != -1 )
						{
							int end = smap.indexOf( '\n', index + 1 );
							if ( end != -1 )
							{
								return smap.substring( index + 1, end ).trim(); // trim() because it could have been multiple whitespace chars
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Given SMAP data, this returns the name of the source file.
	 * 
	 * @param smap The SMAP data.
	 * @return the SMAP's source file name, possibly null.
	 */
	public static String getFileName( String smap )
	{
		String path = getFilePath( smap );
		if ( path != null )
		{
			int index = path.lastIndexOf( '/' );
			if ( index == -1 )
			{
				return path;
			}
			return path.substring( index + 1 );
		}
		return null;
	}
	
	/**
	 * Given SMAP data, this returns the parsed line information which maps Java and EGL lines. This should only be used when the target VM does not
	 * support JSR-45. If the line mappings were not already parsed, that will be done at this time.
	 * 
	 * @param smap The SMAP data.
	 * @param lineMappingCache The line mapping cache, whose key is the SMAP data.
	 * @return the parsed line mappings, possibly null.
	 */
	public static SMAPLineInfo getSMAPLineInfo( String smap, Map<String, SMAPLineInfo> lineMappingCache )
	{
		if ( lineMappingCache != null && smap.length() > 0 )
		{
			SMAPLineInfo info;
			if ( !lineMappingCache.containsKey( smap ) )
			{
				info = SMAPLineParser.parse( smap );
				lineMappingCache.put( smap, info );
			}
			else
			{
				info = lineMappingCache.get( smap );
			}
			return info;
		}
		return null;
	}
}
