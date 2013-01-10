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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.edt.ide.rui.actions.ActionLaunchExternalBrowser;
import org.eclipse.edt.ide.rui.visualeditor.internal.actions.EvActionPreferences;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Tooltips;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BrowserManager;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Contributions can be made to this toolbar via the org.eclipse.ui.editorActions extension point.
 * <br/>
 * <pre>&lt;extension
 *        point="org.eclipse.ui.editorActions"&gt;
 *     &lt;editorContribution
 *           targetID="org.eclipse.edt.ide.rui.visualeditor.EvEditor"
 *           id="my.contribution.id"&gt;
 *        &lt;action
 *              class="my.Action"
 *              icon="path/to/icon.gif"
 *              id="my.action.id"
 *              label="My Label"
 *              style="push"
 *              toolbarPath="main"
 *              tooltip="My Label"/&gt;
 *     &lt;/editorContribution&gt;
 *  &lt;/extension&gt;</pre>
 */
public class EvPreviewToolbar extends Composite implements SelectionListener {

	protected ToolItem		_itemLaunchExternalBrowser	= null;
	protected ToolItem		_itemPreferences			= null;
	protected ToolItem		_itemRefreshWebPage			= null;
	protected ToolItem		_itemBrowserInfo			= null;
	protected EvPreviewPage	_pagePreview				= null;
	protected ToolBar		_toolbar					= null;
	
	/**
	 * 
	 */
	public EvPreviewToolbar( Composite compositeParent, int style, EvPreviewPage pagePreview ) {
		super( compositeParent, style );

		_pagePreview = pagePreview;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 16;
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 2;
		setLayout( gridLayout );

		Label label = new Label( this, SWT.NULL );
		GridData gridData = new GridData( GridData.GRAB_HORIZONTAL );
		label.setLayoutData( gridData );

		_toolbar = new ToolBar( this, SWT.HORIZONTAL | SWT.FLAT );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_END );
		_toolbar.setLayoutData( gridData );
		
		_itemBrowserInfo = createBrowserInfo();
		_itemLaunchExternalBrowser = createExternalBrowser();
		_itemPreferences = createPreferences();
		_itemRefreshWebPage = createRefreshWebPage();
		
		// Help
		//-----
		EvHelp.setHelp( _toolbar, EvHelp.PREVIEW_TOOLBAR );
		for( int i = 0; i < _toolbar.getItemCount(); ++i )
			EvHelp.setHelp( _toolbar.getItem( i ).getControl(), EvHelp.PREVIEW_TOOLBAR );
	}
	
	protected ToolItem createBrowserInfo() {
		ToolItem item = new ToolItem( _toolbar, SWT.PUSH | SWT.FLAT );
		item.setImage( Activator.getImage( EvConstants.ICON_USER_AGENT ) );
		item.addSelectionListener( this );
		item.setToolTipText( Tooltips.NL_User_agent );
		return item;
	}
	
	/**
	 * 
	 */
	protected ToolItem createExternalBrowser() {
		ToolItem item = new ToolItem( _toolbar, SWT.PUSH | SWT.FLAT );
		item.setImage( Activator.getImage( EvConstants.ICON_LAUNCH_EXTERNAL_BROWSER ) );
		item.addSelectionListener( this );
		item.setToolTipText( Tooltips.NL_Show_the_web_page_in_an_external_web_browser );
		
		// Disable if no RUI handler
		//--------------------------
		item.setEnabled( _pagePreview.getEditor().isRuiHandler() );
		return item;
	}

	/**
	 * 
	 */
	protected ToolItem createPreferences() {
		ToolItem item = new ToolItem( _toolbar, SWT.PUSH | SWT.FLAT );
		item.setImage( Activator.getImage( EvConstants.ICON_PREFERENCES ) );
		item.addSelectionListener( this );
		item.setToolTipText( Tooltips.NL_Configure_preferences );
		return item;
	}

	/**
	 * 
	 */
	protected ToolItem createRefreshWebPage() {
		ToolItem item = new ToolItem( _toolbar, SWT.PUSH | SWT.FLAT );
		item.setImage( Activator.getImage( EvConstants.ICON_REFRESH_WEB_PAGE ) );
		item.addSelectionListener( this );
		item.setToolTipText( Tooltips.NL_Refresh_web_page );

		// Disable if no RUI handler
		//--------------------------
		item.setEnabled( _pagePreview.getEditor().isRuiHandler() );
		return item;
	}

	/**
	 * Defers an 'enter' key press to the widgetSelected method.
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 * Handles selection of the preview page's tool bar items.
	 */
	public void widgetSelected( SelectionEvent event ) {
		// Preferences
		//------------
		if( event.widget == _itemPreferences ) {
			EvActionPreferences action = new EvActionPreferences();
			action.run();
			
			// Set the focus to allow an F1 press
			// to present the tool bar help
			//-----------------------------------
			_itemPreferences.getParent().setFocus();
		}
		
		// Refresh browser
		// Tells the editor that the model has changed
		// This will generate a web page and
		// refresh the design and preview pages.
		//--------------------------------------------
		else if( event.widget == _itemRefreshWebPage ) {
			_pagePreview._bFullRefresh = true;
			_pagePreview.getEditor().modelChanged();
		}

		// Launch external browser
		//------------------------
		else if( event.widget == _itemLaunchExternalBrowser ) {
			// Launch the external browser with the same URL as the Preview View, which is equal to the name of the HTML File + Context Key
			String url = _pagePreview.getBrowserManager().getURL();
			/*
			int questionMark = url.indexOf('?');
			if (questionMark != -1)
				url = url.substring(0, questionMark);
			*/
			try {
				String ipAddress = InetAddress.getLocalHost().getHostAddress();
				url = "http://" + ipAddress + url.substring(url.indexOf("localhost:") + 9);
			} catch (UnknownHostException e) {
				// leave localhost
			}
			ActionLaunchExternalBrowser action = new ActionLaunchExternalBrowser( url, false );
			action.run();
			
			// Set the focus to allow an F1 press
			// to present the tool bar help
			//-----------------------------------
			_itemLaunchExternalBrowser.getParent().setFocus();
		}
		
		// Display browser information
		//------------------------
		else if( event.widget == _itemBrowserInfo ) {
			BrowserManager.getInstance().displayBrowserInfo(_pagePreview.getBrowser());
		}
	}
}
