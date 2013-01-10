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

import org.eclipse.edt.ide.rui.server.AbstractPreviewContext;
import org.eclipse.edt.ide.rui.server.DesignContext;
import org.eclipse.edt.ide.rui.server.DesignPaneContentProvider;
import org.eclipse.edt.ide.rui.server.EvEditorProvider;
import org.eclipse.edt.ide.rui.server.IServerListener;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetManager;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.swt.browser.Browser;


public class EvDesignBrowserManager extends EvPreviewBrowserManager implements IServerListener {

	protected boolean		_bAlignmentTestMode = false;
	protected EvDesignPage	_pageDesign	= null;
	protected String		_strNonAlignmentTestURL = null;

	/**
	 * 
	 */
	public EvDesignBrowserManager( Browser browser, String strURL, EvDesignPage pageDesign, EvEditorProvider editorProvider ) {
		super( browser, strURL, editorProvider );
		_pageDesign = pageDesign;
	}

	/**
	 * Declared in IServerListener.
	 * Sample: "{\"ele\":{\"type\":\"VBox\",\"height\":613,\"width\":630,\"offset\":4025,\"len\":594,\"x\":29,\"y\":29},\"children\":{\"0\":{\"ele\":{\"type\":\"VBox\",\"height\":525,\"width\":542,\"offset\":4091,\"len\":519,\"x\":73,\"y\":73},\"children\":{\"0\":{\"ele\":{\"type\":\"HTML\",\"height\":108,\"width\":500,\"offset\":4174,\"len\":379,\"x\":96,\"y\":103}},\"1\":{\"ele\":{\"type\":\"DojoGrid\",\"height\":244,\"width\":440,\"offset\":0,\"len\":0,\"x\":96,\"y\":223}},\"2\":{\"ele\":{\"type\":\"HTML\",\"height\":112,\"width\":500,\"offset\":4591,\"len\":7,\"x\":96,\"y\":484}}}}}}";
	 */
	public void acceptWidgetPositions( String strJson ) {
		// Give the JSON string to the widget manager to parse
		//----------------------------------------------------
		WidgetManager widgetManager = _pageDesign.getWidgetManager();
		widgetManager.setWidgetsFromJsonString( strJson );

		// Notify the design page of the change
		//-------------------------------------
		_pageDesign.widgetsChanged();
	}

	/**
	 * Called by the super class' refreshBrowser method if the context is currently null. 
	 */
	protected AbstractPreviewContext createContext( EvEditorProvider editorProvider ) {
		return new DesignContext( _strURL, _intKey, new DesignPaneContentProvider( editorProvider ), this );
	}
	
	/**
	 * Overrides the super class to handle alignment test
	 */
	public synchronized void refreshBrowser(boolean fullRefresh) {
		if( _bAlignmentTestMode == false ){
			super.refreshBrowser(fullRefresh);
			return;
		}
		
		// Alignment test mode
		// Set the URL of the test page
		//-----------------------------
		if( _browser == null )
			return;
		
		String strURL = Activator.getStateResourceURL( EvConstants.HTML_ALIGNMENT_TEST );
		if( strURL != null )
			_browser.setUrl( strURL );
	}

	/**
	 * Called when a person selects a link to a source line on a browser page with error messages. 
	 */
	public void selectTextInEditor( int offset, int length ) {
		if( _pageDesign == null )
			return;
		
		_pageDesign.getEditor().getPageSource().selectAndReveal( offset, length );
		_pageDesign.getEditor().showPage( 1 );
	}
	
	/**
	 * Tells us whether we are about to do an alignment test or not
	 */
	public void setAlignmentTestMode( boolean bAlignmentTestMode ){
		_bAlignmentTestMode = bAlignmentTestMode;
		
		// Remember the real URL
		//----------------------
		if( bAlignmentTestMode == true ){
			_strNonAlignmentTestURL = super._strURL;
		}
	}
}
