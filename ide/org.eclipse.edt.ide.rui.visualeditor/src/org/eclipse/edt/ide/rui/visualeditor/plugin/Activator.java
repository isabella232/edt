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
package org.eclipse.edt.ide.rui.visualeditor.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.palette.EvPaletteRoot;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the EGL RUI Visual Editor plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	private static Activator	_instance;
	// IBMBIDI Append
	public static final String	IS_BIDI			= "isBidi";
	public static final String	PLUGIN_ID		= "org.eclipse.edt.ide.rui.visualeditor";
	public static final String	ID				= "org.eclipse.edt.ide.rui.visualeditor";

	public static final byte[] baALIGNMENT_TEST_GIF = new byte[]{ 
			0x47, 0x49, 0x46, 0x38,
			0x39, 0x61, 0x10, 0x00,
			0x01, 0x00, (byte)0x80, 0x00,
			0x00, 0x00, 0x00, 0x00, 
			(byte)0xff, (byte)0xff, (byte)0xff, 0x2c,
			0x00, 0x00, 0x00, 0x00,
			0x10, 0x00, 0x01, 0x00,
			0x00, 0x02, 0x06, 0x44,
			0x60, (byte)0x87, (byte)0xc1, (byte)0x88,
			0x05, 0x00, 0x3B };

	/**
	 * Returns the singleton instance of this class.
	 */
	public static Activator getDefault() {
		return _instance;
	}

	/**
	 * Returns an image from the image registry.  If the image is not in the registry, then the image
	 * is obtained from the file system using the given string.
	 */
	public static Image getImage( String strImage ) {
		// If the image is not in the registry, obtain it from the
		// file system and register an instance of it
		//--------------------------------------------------------
		ImageRegistry imageRegistry = getDefault().getImageRegistry();
		
		if( imageRegistry.get( strImage ) == null )
			getDefault().registerImage( strImage );

		return imageRegistry.get( strImage );
	}
	
	public static ImageDescriptor getImageDescriptor( String strImage ) {
		ImageRegistry imageRegistry = getDefault().getImageRegistry();
		
		if( imageRegistry.getDescriptor( strImage ) == null ){
			URL url = getImageURL(strImage);
			ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
			imageRegistry.put(strImage, imageDescriptor);
		}
			

		return imageRegistry.getDescriptor( strImage );
	}

	/**
	 * Returns a URL of the location of the image file.
	 */
	public static URL getImageURL( String strFileName ){
		return getDefault().getBundle().getEntry( EvConstants.ICON_FOLDER + "/" + strFileName );
	}

	/**
	 * Returns a file URL for the resource.
	 */
	public static String getResourceURL( String strResource ) {
		URL urlEntry = getDefault().getBundle().getEntry( strResource );
		if( urlEntry == null )
			return null;
		
		URL urlFile = null;
		
		try{
			urlFile = FileLocator.toFileURL( urlEntry );
		}
		catch( IOException ex ){
			return null;
		}
		
		return urlFile.toExternalForm();
	}
	
	/**
	 * Returns a file URL for the resource that resides in the plugin's state data location.
	 * file:/C:/eclipse/.metadata/.plugins/com.ibm.etools.egl.rui.visualeditor/alignmenttest.html
	 */
	public static String getStateResourceURL( String strResource ){
		IPath path = getDefault().getStateLocation();
		Path pathStateFolder = (Path)path;
		File fileBaseFolder = pathStateFolder.toFile();
		String strBasePath = fileBaseFolder.getAbsolutePath();
		String strPathResource = strBasePath + IPath.SEPARATOR + strResource ;
		File fileResource = new File( strPathResource );
		
		if( fileResource == null || fileResource.exists() == false )
			return null;
		
		URI uri = fileResource.toURI();

		URL url = null;
		try{
			url = uri.toURL();
		}
		catch( MalformedURLException ex ){
			return null;
		}

		String strURL = url.toExternalForm();
		return strURL;
	}

	/**
	 * The constructor.
	 */
	public Activator() {
		_instance = this;
	}
	
	/**
	 * Dynamically creates browser files.  These files cannot be in the in the plug-in directory since they are packaged in a plug-in jar file.
	 * Therefore the browser cannot find them.  The files are created in the settings (metadata) directory.
	 * C:\eclipse\.metadata\.plugins\com.ibm.etools.egl.rui.visualeditor\alignmenttest.html
	 */
	protected void createFiles(){
		IPath path = getDefault().getStateLocation();

		if( path instanceof Path == false )
			return;
		
		Path pathStateFolder = (Path)path;
		File fileBaseFolder = pathStateFolder.toFile();
		String strBasePath = fileBaseFolder.getAbsolutePath();

		String strPath1 = strBasePath + IPath.SEPARATOR + EvConstants.HTML_ALIGNMENT_TEST ;
		File file1 = new File( strPath1 );
		if( file1.exists() == false ) {
			try {
				FileWriter fileWriter = new FileWriter( strPath1 );
				BufferedWriter bufferedWriter = new BufferedWriter( fileWriter );
				bufferedWriter.write( "<html><body style=\"background-image:url(" );
				bufferedWriter.write( EvConstants.IMAGE_ALIGNMENT_TEST );
				bufferedWriter.write( ");background-repeat:no-repeat;\" /></html>\r\n" );
				bufferedWriter.flush();
				bufferedWriter.close();
			}
			catch( IOException ex ) {
			}
		}
		
		String strPath2 = strBasePath + IPath.SEPARATOR + EvConstants.HTML_EMPTY;
		File file2 = new File( strPath2 );
		if( file2.exists() == false ) {
			try {
				FileWriter fileWriter = new FileWriter( strPath2 );
				BufferedWriter bufferedWriter = new BufferedWriter( fileWriter );
				bufferedWriter.write( "<html />\r\n" );
				bufferedWriter.flush();
				bufferedWriter.close();
			}
			catch( IOException ex ) {
			}
		}

		String strPath3 = strBasePath + IPath.SEPARATOR + EvConstants.IMAGE_ALIGNMENT_TEST;
		File file3 = new File( strPath3 );
		if( file3.exists() == false ) {
			boolean bCreated = false;
			try {
				bCreated = file3.createNewFile();
			}
			catch( IOException ex ) {
			}

			if( bCreated == true ) {
				try {
					FileOutputStream stream = new FileOutputStream( file3 );
					stream.write( baALIGNMENT_TEST_GIF );
					stream.flush();
					stream.close();
				}
				catch( IOException ex ) {
				}
			}
		}
	}

	/**
	 * Returns the widget icon for this widget.  This is called by the content outline provider
	 * and the design overlay when painting the drop location hierarchy.
	 */
	public Image getWidgetImage( WidgetPart widget ) {
		String strTypeID = null;
		if( widget != null )
			strTypeID = widget.getTypeID();

		ImageRegistry imageRegistry = getImageRegistry();

		Image image = null;
		if( strTypeID != null )
			image = imageRegistry.get( strTypeID );

		if( image != null )
			return image;

		ImageDescriptor descriptor = null;
		if( strTypeID != null )
			descriptor = EvPaletteRoot.getImageDescriptorForNodeType( strTypeID );
		
		if( descriptor == null ) {
			image = Activator.getImage( EvConstants.ICON_DEFAULT_WIDGET );
			if( image != null && imageRegistry.get( EvConstants.ICON_DEFAULT_WIDGET ) == null )
				imageRegistry.put( EvConstants.ICON_DEFAULT_WIDGET, image );

			return image;
		}

		image = descriptor.createImage();
		if( image != null )
			imageRegistry.put( strTypeID, image );

		return image;
	}

	/**
	 * Loads an image from he file system and registers the image in the image registry. 
	 */
	private void registerImage( String fileName ) {
		// The file separator in a jar is always "/" so you can't use File.SEPARATOR in here as it will result in a bad path when jars are used
		//-------------------------------------------------------------------------------------------------------------------------------------
		try {
			getImageRegistry().put( fileName, ImageDescriptor.createFromURL( getBundle().getEntry( EvConstants.ICON_FOLDER + "/" + fileName ) ) ); //$NON-NLS-1$
		}
		catch( Exception ex ) {
		}
	}

	/**
	 * Called when the plug-in is started. 
	 */
	public void start( BundleContext context ) throws Exception {
		super.start( context );
		createFiles();
	}

	/**
	 * Called when the plug-in is stopped. 
	 */
	public void stop( BundleContext context ) throws Exception {
		_instance = null;
		super.stop( context );
	}
	
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
	
	public static void log(Throwable t) {
		if (t instanceof CoreException) {
			log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, t.getMessage(), t.getCause() == null ? t : t.getCause()));
		}
		else {
			log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "Internal Error", t)); //$NON-NLS-1$
		}
	}
}
