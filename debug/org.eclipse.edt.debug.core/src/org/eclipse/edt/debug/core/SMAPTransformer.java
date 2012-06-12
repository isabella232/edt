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
package org.eclipse.edt.debug.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URISyntaxException;
import java.security.ProtectionDomain;

import org.eclipse.edt.gen.java.Constants;
import org.eclipse.osgi.util.NLS;

/**
 * Looks for SMAP files and, when found, instruments the class file with the debug information.
 */
public class SMAPTransformer implements ClassFileTransformer
{
	private File defaultLocation;
	
	public SMAPTransformer( String location )
	{
		super();
		if ( location != null && location.length() != 0 )
		{
			this.defaultLocation = new File( location );
		}
	}
	
	@Override
	public byte[] transform( ClassLoader loader, String className, @SuppressWarnings("rawtypes") Class redefiningClass, ProtectionDomain domain,
			byte[] classBytes ) throws IllegalClassFormatException
	{
		// This is being called by multiple threads so we must remain stateless. Run the transform in a stateful manner.
		
		// if the debug info exists, load it
		FileInputStream inputStream = null;
		try
		{
			File location;
			try
			{
				// URI will handle spaces, among other things.
				location = new File( domain.getCodeSource().getLocation().toURI() );
			}
			catch ( URISyntaxException e )
			{
				// Failsafe - just handles spaces.
				location = new File( domain.getCodeSource().getLocation().getFile().replaceAll( "%20", " " ) ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			File inSmapFile;
			try
			{
				inSmapFile = new File( location, className + '.' + IEGLDebugCoreConstants.SMAP_EXTENSION );
				inputStream = new FileInputStream( inSmapFile );
			}
			catch ( Exception e )
			{
				inSmapFile = new File( defaultLocation, className + '.' + IEGLDebugCoreConstants.SMAP_EXTENSION );
				inputStream = new FileInputStream( inSmapFile );
			}
			
			int length = (int)inSmapFile.length();
			byte[] smapBytes = new byte[ length ];
			inputStream.read( smapBytes, 0, length );
			
			return new TransformerWorker().transform( className, classBytes, smapBytes );
		}
		catch ( IOException e )
		{
			return null;
		}
		finally
		{
			if ( inputStream != null )
			{
				try
				{
					inputStream.close();
				}
				catch ( IOException e )
				{
					// Ignore.
				}
			}
		}
	}
	
	public static class TransformerWorker
	{
		private String className;
		private int sourceDebugExtensionIndex;
		private byte[] debugInfoBytes;
		private byte[] inClassBytes;
		private int inClassOffset;
		private byte[] outClassBytes;
		private int outClassIndex;
		
		public byte[] transform( String name, byte[] classBytes, byte[] smapBytes ) throws IllegalClassFormatException
		{
			className = name;
			inClassBytes = classBytes;
			debugInfoBytes = smapBytes;
			
			// update the class file
			outClassBytes = new byte[ classBytes.length + debugInfoBytes.length + 100 ];
			if ( updateClass() )
			{
				// Trim it down to size. IBM JREs don't seem to care, but Sun JREs consider the class invalid if there's
				// too many bytes.
				if ( outClassIndex < outClassBytes.length )
				{
					byte[] temp = new byte[ outClassIndex ];
					System.arraycopy( outClassBytes, 0, temp, 0, outClassIndex );
					return temp;
				}
				return outClassBytes;
			}
			return null;
		}
		
		private boolean updateClass()
		{
			// copy in the class header, major and minor version numbers
			copyBytes( 8 );
			int tagPosition = outClassIndex;
			int tagCount = readUnicode2();
			writeU2( tagCount );
			if ( !copyTags( tagCount ) )
			{
				return false;
			}
			if ( sourceDebugExtensionIndex < 0 )
			{
				// if "SourceDebugExtension" symbol not there add it
				writeUtf8ForSourceDebugExtension();
				// increment the tagCount
				sourceDebugExtensionIndex = tagCount;
				randomAccessWriteU2( tagPosition, ++tagCount );
			}
			// copy the access id, this class id and the superclass id
			copyBytes( 6 );
			int interfaceCount = readUnicode2();
			writeU2( interfaceCount );
			copyBytes( interfaceCount * 2 );
			// copy all of the fields
			copyMembers();
			// copy all of the methods
			copyMembers();
			int attrPosition = outClassIndex;
			int attrCount = readUnicode2();
			writeU2( attrCount );
			// copy the class attributes, return true if the SourceDebugExtension attribute is found
			if ( !copyAttrs( attrCount ) )
			{
				// we will be adding "SourceDebugExtension" and it isn't already counted
				randomAccessWriteU2( attrPosition, ++attrCount );
			}
			writeAttrForSourceDebugExtension( debugInfoBytes );
			return true;
		}
		
		private void copyMembers()
		{
			int count = readUnicode2();
			writeU2( count );
			for ( int i = 0; i < count; i++ )
			{
				// copy the access id, name and descriptor ids
				copyBytes( 6 );
				int attrCount = readUnicode2();
				writeU2( attrCount );
				copyAttrs( attrCount );
			}
		}
		
		private boolean copyAttrs( int attrCount )
		{
			boolean sourceDebugExtensionFound = false;
			for ( int i = 0; i < attrCount; i++ )
			{
				int nameIndex = readUnicode2();
				// if SourceDebugExtension was found, don't replace it
				if ( nameIndex == sourceDebugExtensionIndex )
				{
					sourceDebugExtensionFound = true;
				}
				else
				{
					// copy in the attribute name
					writeU2( nameIndex );
					int length = readUnicode4();
					writeU4( length );
					copyBytes( length );
				}
			}
			return sourceDebugExtensionFound;
		}
		
		private void writeAttrForSourceDebugExtension( byte[] debugInfo )
		{
			writeU2( sourceDebugExtensionIndex );
			writeU4( debugInfo.length );
			for ( int i = 0; i < debugInfo.length; i++ )
			{
				writeU1( debugInfo[ i ] );
			}
		}
		
		private void randomAccessWriteU2( int pos, int val )
		{
			int savePos = outClassIndex;
			outClassIndex = pos;
			writeU2( val );
			outClassIndex = savePos;
		}
		
		private int readUnicode1()
		{
			return ((int)inClassBytes[ inClassOffset++ ]) & 0xFF;
		}
		
		private int readUnicode2()
		{
			return (readUnicode1() << 8) + readUnicode1();
		}
		
		private int readUnicode4()
		{
			return (readUnicode2() << 16) + readUnicode2();
		}
		
		private void writeU1( int val )
		{
			outClassBytes[ outClassIndex++ ] = (byte)val;
		}
		
		private void writeU2( int val )
		{
			writeU1( val >> 8 );
			writeU1( val & 0xFF );
		}
		
		private void writeU4( int val )
		{
			writeU2( val >> 16 );
			writeU2( val & 0xFFFF );
		}
		
		private void copyBytes( int count )
		{
			for ( int i = 0; i < count; i++ )
			{
				outClassBytes[ outClassIndex++ ] = inClassBytes[ inClassOffset++ ];
			}
		}
		
		private byte[] readBytes( int count )
		{
			byte[] bytes = new byte[ count ];
			for ( int i = 0; i < count; i++ )
			{
				bytes[ i ] = inClassBytes[ inClassOffset++ ];
			}
			return bytes;
		}
		
		private void writeBytes( byte[] bytes )
		{
			for ( int i = 0; i < bytes.length; i++ )
			{
				outClassBytes[ outClassIndex++ ] = bytes[ i ];
			}
		}
		
		private boolean copyTags( int tagCount )
		{
			sourceDebugExtensionIndex = -1;
			for ( int i = 1; i < tagCount; i++ )
			{
				int tag = readUnicode1();
				writeU1( tag );
				switch ( tag )
				{
					case 1:
						// tag = utf8
						int length = readUnicode2();
						writeU2( length );
						byte[] utf8 = readBytes( length );
						String str = null;
						try
						{
							str = new String( utf8, Constants.smap_encoding );
						}
						catch ( UnsupportedEncodingException e )
						{
							System.out.println( NLS.bind( EDTDebugCoreMessages.TransformerUnsupportedEncoding, className ) );
							return false;
						}
						if ( str.equals( Constants.smap_attribute ) )
						{
							sourceDebugExtensionIndex = i;
						}
						writeBytes( utf8 );
						break;
					case 3:
						// tag = integer
						copyBytes( 4 );
						break;
					case 4:
						// tag = float
						copyBytes( 4 );
						break;
					case 5:
						// tag = long
						copyBytes( 8 );
						i++;
						break;
					case 6:
						// tag = double
						copyBytes( 8 );
						i++;
						break;
					case 7:
						// tag = class
						copyBytes( 2 );
						break;
					case 8:
						// tag = string
						copyBytes( 2 );
						break;
					case 9:
						// tag = field
						copyBytes( 4 );
						break;
					case 10:
						// tag = method
						copyBytes( 4 );
						break;
					case 11:
						// tag = interfacemethod
						copyBytes( 4 );
						break;
					case 12:
						// tag = name and type
						copyBytes( 4 );
						break;
					default:
						System.out.println( NLS.bind( EDTDebugCoreMessages.TransformerUnknownTag, className ) );
						return false;
				}
			}
			return true;
		}
		
		private void writeUtf8ForSourceDebugExtension()
		{
			// write out the utf8 tag
			writeU1( 1 );
			writeU2( Constants.smap_attribute.length() );
			for ( int i = 0; i < Constants.smap_attribute.length(); i++ )
			{
				writeU1( Constants.smap_attribute.charAt( i ) );
			}
		}
	}
	
	/**
	 * Given a set of directories, it looks for all *.eglsmap files and puts the contents into the *.class files.
	 * 
	 * @param args One or more directories. The path must be valid for passing into java.io.File's constructor.
	 */
	public static void main( String[] args )
	{
		for ( String path : args )
		{
			File dir = new File( path );
			if ( dir.exists() && dir.isDirectory() )
			{
				processDirectory( dir );
			}
		}
	}
	
	static void processDirectory( File dir )
	{
		for ( File file : dir.listFiles() )
		{
			if ( file.isDirectory() )
			{
				processDirectory( file );
			}
			else if ( file.getName().endsWith( IEGLDebugCoreConstants.SMAP_EXTENSION ) )
			{
				File classFile = new File( dir, file.getName()
						.substring( 0, file.getName().length() - IEGLDebugCoreConstants.SMAP_EXTENSION.length() ) + "class" ); //$NON-NLS-1$
				if ( classFile.exists() )
				{
					byte[] smapBytes = getBytes( file );
					if ( smapBytes != null )
					{
						byte[] classBytes = getBytes( classFile );
						if ( classBytes != null )
						{
							try
							{
								byte[] newBytes = new SMAPTransformer.TransformerWorker().transform( file.getAbsolutePath(), classBytes, smapBytes );
								if ( newBytes != null )
								{
									writeBytes( classFile, newBytes );
								}
							}
							catch ( IllegalClassFormatException e )
							{
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
	static byte[] getBytes( File file )
	{
		BufferedInputStream is = null;
		try
		{
			is = new BufferedInputStream( new FileInputStream( file ) );
			byte[] b = new byte[ is.available() ];
			is.read( b );
			return b;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( is != null )
			{
				try
				{
					is.close();
				}
				catch ( Exception e )
				{
				}
			}
		}
		return null;
	}
	
	static void writeBytes( File file, byte[] contents )
	{
		OutputStream os = null;
		try
		{
			os = new BufferedOutputStream( new FileOutputStream( file ) );
			os.write( contents );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( os != null )
			{
				try
				{
					os.close();
				}
				catch ( Exception e )
				{
				}
			}
		}
	}
}
