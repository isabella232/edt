/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.preferences;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;

public class EditorPreferencePage extends AbstractPreferencePage {
	protected Button showLineNumbersButton = null;
	protected Button annotateErrorsButton = null;				// annotate errors in text
	protected Button annotateErrorsInOverviewButton = null;		// annotate errors in overview ruler
	protected Button annotateErrorsAsYouTypeButton = null;
	protected Button autoActivationButton = null;

	public EditorPreferencePage(){
//		setDescription(UINlsStrings.EditorPreferencePageDescription);
		setPreferenceStore(doGetPreferenceStore());		
	}
	
	protected IPreferenceStore doGetPreferenceStore() {
		return EDTUIPlugin.getDefault().getPreferenceStore();
	}
	protected void doSavePreferenceStore() {
		EDTUIPlugin.getDefault().savePluginPreferences();		
	}
	protected Control createContents(Composite parent) {
		Composite composite = (Composite) super.createContents(parent);
		
		Composite internalComposite = createComposite(composite, 1);
		showLineNumbersButton = createCheckBox(internalComposite, UINlsStrings.ShowLineNumbersLabel);
		annotateErrorsButton =  createCheckBox(internalComposite, UINlsStrings.AnnotateErrorsLabel);
		annotateErrorsInOverviewButton = createCheckBox(internalComposite, UINlsStrings.AnnotateErrorsInOverviewLabel);
		annotateErrorsAsYouTypeButton = createCheckBox(internalComposite, UINlsStrings.AnnotateErrorsAsYouTypeLabel);
		
		Group contentAssistGroup = createGroup(composite, 1);
		contentAssistGroup.setText(UINlsStrings.ContentAssistLabel);
		Composite contentAssistComposite = createComposite(contentAssistGroup, 1);
		autoActivationButton= createCheckBox(contentAssistComposite, UINlsStrings.AutoActivationLabel);

		setSize(composite);
		loadPreferences();

		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IUIHelpConstants.EDITOR_PREFERENCE_CONTEXT);
		Dialog.applyDialogFont(parent);
		return composite;
	}
	
	private IPreferenceStore getEditorUIPreferenceStore(){
		return EditorsUI.getPreferenceStore();
	}

	protected void performDefaults() {
		showLineNumbersButton.setSelection(getEditorUIPreferenceStore().getDefaultBoolean(EGLPreferenceConstants.EDITOR_LINE_NUMBER_RULER));
		annotateErrorsButton.setSelection(getEditorUIPreferenceStore().getDefaultBoolean(EGLPreferenceConstants.EDITOR_ERROR_INDICATION));
		annotateErrorsInOverviewButton.setSelection(getEditorUIPreferenceStore().getDefaultBoolean(EGLPreferenceConstants.EDITOR_ERROR_INDICATION_IN_OVERVIEW_RULER));
		annotateErrorsAsYouTypeButton.setSelection(getPreferenceStore().getDefaultBoolean(EGLPreferenceConstants.EDITOR_HANDLE_DYNAMIC_PROBLEMS));
		autoActivationButton.setSelection(getPreferenceStore().getDefaultBoolean(EGLPreferenceConstants.CODEASSIST_AUTOACTIVATION));

		super.performDefaults();
	}

	protected void initializeValues() {
		showLineNumbersButton.setSelection(getEditorUIPreferenceStore().getBoolean(EGLPreferenceConstants.EDITOR_LINE_NUMBER_RULER)); //$NON-NLS-1$
		annotateErrorsButton.setSelection(getEditorUIPreferenceStore().getBoolean(EGLPreferenceConstants.EDITOR_ERROR_INDICATION));
		annotateErrorsInOverviewButton.setSelection(getEditorUIPreferenceStore().getBoolean(EGLPreferenceConstants.EDITOR_ERROR_INDICATION_IN_OVERVIEW_RULER));
		annotateErrorsAsYouTypeButton.setSelection(getPreferenceStore().getBoolean(EGLPreferenceConstants.EDITOR_HANDLE_DYNAMIC_PROBLEMS));
		autoActivationButton.setSelection(getPreferenceStore().getBoolean(EGLPreferenceConstants.CODEASSIST_AUTOACTIVATION));
	}

	protected void storeValues() {
		getEditorUIPreferenceStore().setValue(EGLPreferenceConstants.EDITOR_LINE_NUMBER_RULER, showLineNumbersButton.getSelection());
		getEditorUIPreferenceStore().setValue(EGLPreferenceConstants.EDITOR_ERROR_INDICATION, annotateErrorsButton.getSelection());
		getEditorUIPreferenceStore().setValue(EGLPreferenceConstants.EDITOR_ERROR_INDICATION_IN_OVERVIEW_RULER, annotateErrorsInOverviewButton.getSelection());
		getPreferenceStore().setValue(EGLPreferenceConstants.EDITOR_HANDLE_DYNAMIC_PROBLEMS, annotateErrorsAsYouTypeButton.getSelection());
		getPreferenceStore().setValue(EGLPreferenceConstants.CODEASSIST_AUTOACTIVATION, autoActivationButton.getSelection());
	}

	public boolean performOk() {
		boolean result = super.performOk();

		doSavePreferenceStore();

		return result;
	}
}
