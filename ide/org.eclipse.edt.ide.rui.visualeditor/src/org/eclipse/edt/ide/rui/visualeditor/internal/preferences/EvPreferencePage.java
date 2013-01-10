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
package org.eclipse.edt.ide.rui.visualeditor.internal.preferences;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvHelp;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.Mnemonics;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * Preference page for the EGL RichUI Visual Editor
 */
public class EvPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, SelectionListener {

	protected EvPreferencePageBrowserSize	_compositeBrowserSize	= null;
	protected EvPreferencePageGeneral		_compositeGeneral		= null;
	protected EvPreferencePageLanguage		_compositeLanguage		= null;
	protected TabFolder						_tabFolder				= null;

	/**
	 *
	 */
	public EvPreferencePage() {
	}

	/**
	 * Creates the user interface for the preference page
	 */
	protected Control createContents( Composite compositeParent ) {
		_tabFolder = new TabFolder( compositeParent, SWT.NULL );

		// Transparency page
		//------------------
		TabItem tabTransparency = new TabItem( _tabFolder, SWT.NULL );
		tabTransparency.setText( Messages.NL_General );
		_compositeGeneral = new EvPreferencePageGeneral( _tabFolder, SWT.NULL );
		tabTransparency.setControl( _compositeGeneral );

		// Browser size page
		//------------------
		TabItem tabBrowserSize = new TabItem( _tabFolder, SWT.NULL );
		tabBrowserSize.setText( Messages.NL_Browser_size );
		_compositeBrowserSize = new EvPreferencePageBrowserSize( _tabFolder, SWT.NULL );
		tabBrowserSize.setControl( _compositeBrowserSize );

		// Language page
		//--------------
		TabItem tabLanguage = new TabItem( _tabFolder, SWT.NULL );
		tabLanguage.setText( Messages.NL_Languages );
		_compositeLanguage = new EvPreferencePageLanguage( _tabFolder, SWT.NULL );
		tabLanguage.setControl( _compositeLanguage );

		// Turn to the last tab folder page used
		//--------------------------------------
		_tabFolder.setSelection( EvPreferences.getInt( EvConstants.PREFERENCE_PAGE_TAB ) );

		// Add the selection listener after the selection has been set
		//------------------------------------------------------------
		_tabFolder.addSelectionListener( this );
		
		new Mnemonics().setMnemonics( _tabFolder );

		return _tabFolder;
	}

	/**
	 * Returns the currently active IEvPreferencePage
	 */
	protected IEvPreferencePage getSelectedIEvPreferencePage() {
		int iIndex = _tabFolder.getSelectionIndex();
		return (IEvPreferencePage)_tabFolder.getItem( iIndex ).getControl();
	}

	/**
	 * 
	 */
	public void init( IWorkbench workbench ) {
	}

	/**
	 * Declared in PreferencePage and overridden here.  Called when a person presses the Restore Defaults button.
	 * The defaults are restored only for the tab folder page that is currently active/visible.
	 */
	public void performDefaults() {
		getSelectedIEvPreferencePage().performDefaults();
	}

	/**
	 * Called when either the Apply or Ok buttons are pressed.
	 */
	public boolean performOk() {

		_compositeBrowserSize.performOk();
		_compositeGeneral.performOk();
		_compositeLanguage.performOK();

		return true;
	}

	/**
	 * Declared in SelectionListener.  Does nothing.
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
	}

	/**
	 * Declared in SelectionListener.  The tab folder is notified then the tab folder changes pages.
	 * The corresponding help context is associated with the tab folder.
	 * The page is remembered in the preferences, so that the same page will appear the next time
	 * the preference dialog is opened.
	 */
	public void widgetSelected( SelectionEvent event ) {
		IEvPreferencePage page = getSelectedIEvPreferencePage();
		EvHelp.setHelp( _tabFolder, page.getHelpID() );

		EvPreferences.setInt( EvConstants.PREFERENCE_PAGE_TAB, _tabFolder.getSelectionIndex() );
	}
}
