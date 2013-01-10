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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.rui.internal.nls.ILocalesListViewer;
import org.eclipse.edt.ide.rui.internal.nls.Locale;
import org.eclipse.edt.ide.rui.internal.nls.LocaleUtility;
import org.eclipse.edt.ide.rui.internal.nls.LocalesList;
import org.eclipse.edt.ide.rui.preferences.IRUIPreferenceConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvHelp;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * Preference page fragment containing the RUI Visual Editor NLS Settings
 * 
 * @author gslade
 * @since 7.5.1
 *
 */
public class EvPreferencePageLanguage extends Composite implements IEvPreferencePage, ILocalesListViewer {

	protected Combo			_handlerLocaleCombo			= null;
	protected LocalesList	_localesList				= LocalesList.getLocalesList();
	protected Combo			_runtimeMessageLocaleCombo	= null;
	
	IPreferenceStore 		_baseEGLStore 				= EDTCoreIDEPlugin.getPlugin().getPreferenceStore();
	HashMap					_handlerLocaleDisplayToCode = new HashMap();

	/**
	 * @param parent
	 * @param style
	 */
	public EvPreferencePageLanguage( Composite parent, int style ) {
		super( parent, style );
		
		LocalesList.getLocalesList().addChangeListener(this);

		GridLayout gridLayout = new GridLayout();
		setLayout( gridLayout );

		createRuntimeMessagesNLSGroup( parent );
		createHandlerNLSGoup( parent );

		Dialog.applyDialogFont( parent );
		EvHelp.setHelp( this, EvHelp.PREFERENCES_LANGUAGE );
	}

	/**
	 * 
	 */
	protected void createHandlerNLSGoup( Composite parent ) {
		Group runtimeGroup = new Group( this, SWT.NULL );
		runtimeGroup.setText( Messages.NL_Rich_UI_handler_locale );
		GridData layoutData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		runtimeGroup.setLayoutData( layoutData );
		GridLayout layout = new GridLayout();
		runtimeGroup.setLayout( layout );

		_handlerLocaleCombo = new Combo( runtimeGroup, SWT.READ_ONLY );
		layoutData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		_handlerLocaleCombo.setLayoutData( layoutData );

		populateCombo();

		/**
		 * set the value
		 */
		String localeCode = _baseEGLStore.getString( IRUIPreferenceConstants.PREFERENCE_HANDLER_LOCALE );
		if( localeCode == null || localeCode.length() == 0 ) {
			localeCode = LocaleUtility.getDefaultHandlerLocale().getCode();
		}
		String displayString = ""; //$NON-NLS-1$
		for( Iterator iterator = _localesList.getLocales().iterator(); iterator.hasNext() && displayString.equals( "" ); ) { //$NON-NLS-1$
			Locale locale = (Locale)iterator.next();
			if( locale.getCode().equals( localeCode ) ) {
				displayString = buildHandlerLocaleDisplayString(locale);
			}
		}

		_handlerLocaleCombo.setText( displayString );

	}

	/**
	 * 
	 */
	private void populateCombo() {
		/**
		 * populate the combo
		 */
		List descriptions = new ArrayList();
		
		for( Iterator iterator = _localesList.getLocales().iterator(); iterator.hasNext(); ) {
			Locale locale = (Locale)iterator.next();
			String displayString = buildHandlerLocaleDisplayString(locale);
			descriptions.add( displayString );
			_handlerLocaleDisplayToCode.put(displayString, locale.getCode());
		}
		
		String[] listArray = (String[])descriptions.toArray( new String[descriptions.size()] );
		Arrays.sort( listArray );
		_handlerLocaleCombo.setItems( listArray );
	}
	
	private String buildHandlerLocaleDisplayString(Locale locale) {
		return locale.getDescription() + " (" + locale.getCode() + ")";
	}

	/**
	 * 
	 */
	protected void createRuntimeMessagesNLSGroup( Composite parent ) {
		Group runtimeGroup = new Group( this, SWT.NULL );
		runtimeGroup.setText( Messages.NL_Runtime_messages_locale );
		GridData layoutData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		runtimeGroup.setLayoutData( layoutData );

		GridLayout layout = new GridLayout();
		runtimeGroup.setLayout( layout );

		_runtimeMessageLocaleCombo = new Combo( runtimeGroup, SWT.READ_ONLY );
		layoutData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		_runtimeMessageLocaleCombo.setLayoutData( layoutData );

		/**
		 * populate the combo
		 */
		_runtimeMessageLocaleCombo.setItems( LocaleUtility.getRuntimeDescriptionsArray() );
		/**
		 * set the value
		 */
		String runtimeLocale = _baseEGLStore.getString( IRUIPreferenceConstants.PREFERENCE_RUNTIME_MESSAGES_LOCALE );
		if( runtimeLocale == null || runtimeLocale.length() == 0 ) {
			runtimeLocale = LocaleUtility.getDefaultRuntimeLocale().getCode();
		}
		_runtimeMessageLocaleCombo.setText( LocaleUtility.getRuntimeDescriptionForCode( runtimeLocale ) );
	}

	/**
	 * Returns the help ID for this page.
	 */
	public String getHelpID(){
		return EvHelp.PREFERENCES_LANGUAGE;
	}

	/**
	 * 
	 */
	public void performDefaults() {
		String defaultRuntimeLocaleCode = LocaleUtility.getDefaultRuntimeLocale().getCode();;
		String defaultRuntimeLocaleDescription = LocaleUtility.getRuntimeDescriptionForCode(defaultRuntimeLocaleCode);
		_runtimeMessageLocaleCombo.setText( defaultRuntimeLocaleDescription );
		
		
		String defaultHandlerLocaleCode = LocaleUtility.getDefaultHandlerLocale().getCode();;
		String defaultHandlerLocaleDescription = LocaleUtility.getHandlerDescriptionForCode(defaultHandlerLocaleCode);
		Locale dummy = new Locale(defaultHandlerLocaleCode, defaultHandlerLocaleDescription, "");
		_handlerLocaleCombo.setText( buildHandlerLocaleDisplayString(dummy) );
	}

	/**
	 * 
	 */
	public void performOK() {
		String runtimeLocaleCode = LocaleUtility.getRuntimeCodeForDescription( _runtimeMessageLocaleCombo.getText() );
		if( runtimeLocaleCode != null )
			_baseEGLStore.setValue( IRUIPreferenceConstants.PREFERENCE_RUNTIME_MESSAGES_LOCALE, runtimeLocaleCode );

		String displayString = _handlerLocaleCombo.getText();
		String code = (String)_handlerLocaleDisplayToCode.get(displayString);
		if ( code == null ) {
			code = "";
		}
		_baseEGLStore.setValue( IRUIPreferenceConstants.PREFERENCE_HANDLER_LOCALE, code );
	}
	
	public void dispose() {
		LocalesList.getLocalesList().removeChangeListener(this);
		super.dispose();
	}

	public void addLocale(Locale locale) {
		refreshHandlerLocaleCombo();
	}

	private void refreshHandlerLocaleCombo() {
		if (! _handlerLocaleCombo.isDisposed()) {
			String currentSelection = _handlerLocaleCombo.getText();
			populateCombo();
			_handlerLocaleCombo.setText(currentSelection);
		}
	}

	public void removeLocale(Locale locale) {
		refreshHandlerLocaleCombo();
	}

	public void updateLocale(Locale locale) {
		refreshHandlerLocaleCombo();
	}
	
	public void clear() {		
	}
}
