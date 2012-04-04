/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.util.PixelConverter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class EditorPreferencePage extends AbstractPreferencePage {
	protected Button annotateErrorsButton = null;				// annotate errors in text
	protected Button annotateErrorsInOverviewButton = null;		// annotate errors in overview ruler
	protected Button annotateErrorsAsYouTypeButton = null;

	public EditorPreferencePage(){
//		setDescription(UINlsStrings.EditorPreferencePageDescription);
		setPreferenceStore(EDTUIPlugin.getDefault().getPreferenceStore());	
	}
	
	protected Control createContents(Composite parent) {
		Composite composite = (Composite) super.createContents(parent);
		createHeader(composite);
		
		Composite internalComposite = createComposite(composite, 1);
		annotateErrorsButton =  createCheckBox(internalComposite, UINlsStrings.AnnotateErrorsLabel);
		annotateErrorsInOverviewButton = createCheckBox(internalComposite, UINlsStrings.AnnotateErrorsInOverviewLabel);
		annotateErrorsAsYouTypeButton = createCheckBox(internalComposite, UINlsStrings.AnnotateErrorsAsYouTypeLabel);
		
		setSize(composite);
		loadPreferences();

		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IUIHelpConstants.EDITOR_PREFERENCE_CONTEXT);
		Dialog.applyDialogFont(parent);
		return composite;
	}
	
	private void createHeader(Composite contents) {
		final Shell shell= contents.getShell();
		String text = UINlsStrings.TextEditorLink;
		Link link= new Link(contents, SWT.NONE);
		link.setText(text);
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(shell, "org.eclipse.ui.preferencePages.GeneralTextEditor", null, null); //$NON-NLS-1$
			}
		});
		link.setToolTipText( UINlsStrings.TextEditorTooltip );

		GridData gridData= new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.widthHint= 150; // only expand further if anyone else requires it
		link.setLayoutData(gridData);

		addFiller(contents);
	}
	
	private void addFiller(Composite composite) {
		PixelConverter pixelConverter= new PixelConverter(composite);

		Label filler= new Label(composite, SWT.LEFT );
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= 2;
		gd.heightHint= pixelConverter.convertHeightInCharsToPixels(1) / 2;
		filler.setLayoutData(gd);
	}
	
	protected void performDefaults() {
		annotateErrorsButton.setSelection(getPreferenceStore().getDefaultBoolean(EDTUIPreferenceConstants.EDITOR_ERROR_INDICATION));
		annotateErrorsInOverviewButton.setSelection(getPreferenceStore().getDefaultBoolean(EDTUIPreferenceConstants.EDITOR_ERROR_INDICATION_IN_OVERVIEW_RULER));
		annotateErrorsAsYouTypeButton.setSelection(getPreferenceStore().getDefaultBoolean(EDTUIPreferenceConstants.EDITOR_HANDLE_DYNAMIC_PROBLEMS));

		super.performDefaults();
	}

	protected void initializeValues() {
		annotateErrorsButton.setSelection(getPreferenceStore().getBoolean(EDTUIPreferenceConstants.EDITOR_ERROR_INDICATION));
		annotateErrorsInOverviewButton.setSelection(getPreferenceStore().getBoolean(EDTUIPreferenceConstants.EDITOR_ERROR_INDICATION_IN_OVERVIEW_RULER));
		annotateErrorsAsYouTypeButton.setSelection(getPreferenceStore().getBoolean(EDTUIPreferenceConstants.EDITOR_HANDLE_DYNAMIC_PROBLEMS));
	}

	protected void storeValues() {
		getPreferenceStore().setValue(EDTUIPreferenceConstants.EDITOR_ERROR_INDICATION, annotateErrorsButton.getSelection());
		getPreferenceStore().setValue(EDTUIPreferenceConstants.EDITOR_ERROR_INDICATION_IN_OVERVIEW_RULER, annotateErrorsInOverviewButton.getSelection());
		getPreferenceStore().setValue(EDTUIPreferenceConstants.EDITOR_HANDLE_DYNAMIC_PROBLEMS, annotateErrorsAsYouTypeButton.getSelection());
	}

	public boolean performOk() {
		boolean result = super.performOk();
		EDTUIPlugin.getDefault().saveUIPluginPreferences();		
		return result;
	}
}
