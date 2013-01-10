/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.editor;

import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EditorUtility;
import org.eclipse.edt.ide.ui.internal.editor.IProblemChangedListener;
import org.eclipse.edt.ide.ui.internal.viewsupport.ElementImageProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Changes the icon on the editor tab whenever the error state changes.
 */
public class EvEditorErrorUpdater implements IProblemChangedListener {

	protected EvEditor					_editor			= null;
	protected ElementImageProvider	_imageProvider	= new ElementImageProvider();

	/**
	 * 
	 */
	public EvEditorErrorUpdater( EvEditor editor ) {
		_editor = editor;
		
		EDTUIPlugin.getDefault().getProblemMarkerManager().addListener( this );
	}

	/**
	 * 
	 */
	public void dispose() {
		EDTUIPlugin.getDefault().getProblemMarkerManager().removeListener( this );
	}

	/**
	 * Declared in IProblemChangedListener
	 */
	public void problemsChanged( IResource[] resources, boolean bMarkerChange ) {
		if( bMarkerChange == true )
			return;

		// See if the editor is in the list of changed resources
		//------------------------------------------------------
		IEditorInput input = _editor.getEditorInput();

		if( input instanceof IFileEditorInput == false )
			return;

		for( int i = 0; i < resources.length; i++ ){
			if( resources[ i ].equals( ( (FileEditorInput)input ).getFile() ) ){
				updateEditorImage( input );
				break;
			}
		}
	}

	/**
	 * Updates the editor tab's image based on the markers in the source. 
	 */
	public void updateEditorImage( IEditorInput input ) {
		// Obtain the current editor tab's image
		//--------------------------------------
		Image imageCurrent = _editor.getTitleImage();
		if( imageCurrent == null )
			return;

		// Obtain the flags
		//-----------------
		int fImageFlags = EditorUtility.populateNodeErrorWarningHashMaps( _editor.getPageSource() );

		final Image imageNew = _imageProvider.getImageLabel( PluginImages.DESC_OBJS_EGLFILE, fImageFlags );

		if( imageCurrent != imageNew ){
			Shell shell = _editor.getEditorSite().getShell();
			if( shell != null && !shell.isDisposed() ) {
				shell.getDisplay().syncExec( new Runnable() {
					public void run() {
						_editor.updateTitleImage( imageNew );
					}
				} );
			}
		}
	}
}
