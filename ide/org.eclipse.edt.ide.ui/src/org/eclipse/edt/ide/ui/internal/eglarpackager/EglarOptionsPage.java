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
package org.eclipse.edt.ide.ui.internal.eglarpackager;

import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 *	Page 2 of the JAR Package wizard
 */
public class EglarOptionsPage extends WizardPage implements IEglarPackageWizardPage {
	// Untyped listener
	private class UntypedListener implements Listener {
		/*
		 * Implements method from Listener
		 */
		public void handleEvent(Event e) {
			if (getControl() == null)
				return;
			update();
		}
	}

	private EglarPackageData fEglarPackage;

	// widgets
	private Composite	fTheGroup;
	private Label		fVendorLabel;
	private Text		fVendorText;
	private Label		fVersionLabel;
	private Text		fVersionText;

	private final static String PAGE_NAME= "EglarOptionsWizardPage"; //$NON-NLS-1$

	private final static String STORE_EXPORT_ARE_EXPORT_EGL_SRC = PAGE_NAME + ".STORE_EXPORT_ARE_EXPORT_EGL_SRC";
	private final static String STORE_EXPORT_IS_EGLAR_GENERATABLE= PAGE_NAME + ".EXPORT_IS_EGLAR_GENERATABLE"; //$NON-NLS-1$
	private final static String STORE_EXPORT_VENDOR= PAGE_NAME + ".EXPORT_VENDOR"; //$NON-NLS-1$
	private final static String STORE_EXPORT_VERSION= PAGE_NAME + ".EXPORT_VERSION"; //$NON-NLS-1$
	/**
	 *	Create an instance of this class
	 * @param jarPackage
	 */
	public EglarOptionsPage(EglarPackageData eglarPackage) {
		super(PAGE_NAME);
		setTitle(EglarPackagerMessages.EglarOptionsPage_title);
		setDescription(EglarPackagerMessages.EglarOptionsPage_description);
		fEglarPackage= eglarPackage;
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(
			new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));

		createOptionsGroup(composite);

		restoreWidgetValues();
		setControl(composite);
		update();

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IUIHelpConstants.EGLAR_OPTIONS_WIZARD_PAGE);
	}

	/**
	 *	Create the export options specification widgets.
	 *
	 *	@param parent org.eclipse.swt.widgets.Composite
	 */
	protected void createOptionsGroup(Composite parent) {
		
		initializeDialogUnits(parent);

		Composite optionsGroup= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginHeight= 0;
		optionsGroup.setLayout(layout);

		createSpacer(optionsGroup);
		createDescriptionFileGroup(parent);
	}

	/**
	 * Persists resource specification control setting that are to be restored
	 * in the next instance of this page. Subclasses wishing to persist
	 * settings for their controls should extend the hook method
	 * <code>internalSaveWidgetValues</code>.
	 */
	public final void saveWidgetValues() {
		// update directory names history
		IDialogSettings settings= getDialogSettings();
		if (settings != null) {
			settings.put(STORE_EXPORT_ARE_EXPORT_EGL_SRC, fEglarPackage.areEGLSrcFilesExported());
			settings.put(STORE_EXPORT_IS_EGLAR_GENERATABLE, fEglarPackage.isEglarGeneratable());
			settings.put(STORE_EXPORT_VENDOR, fEglarPackage.getVendorName());
			settings.put(STORE_EXPORT_VERSION, fEglarPackage.getVersionName());
		}
	}

	/**
	 *	Hook method for restoring widget values to the values that they held
	 *	last time this wizard was used to completion.
	 */
	protected void restoreWidgetValues() {
		initializeJarPackage();

		fVendorText.setText(fEglarPackage.getVendorName());
		fVersionText.setText(fEglarPackage.getVersionName());
	}

	/**
	 *	Initializes the JAR package from last used wizard page values.
	 */
	protected void initializeJarPackage() {
		IDialogSettings settings= getDialogSettings();
		if (settings != null) {
			fEglarPackage.setExportEGLSrcFiles(settings.getBoolean(STORE_EXPORT_ARE_EXPORT_EGL_SRC));
			fEglarPackage.setEglarGeneratable(settings.getBoolean(STORE_EXPORT_IS_EGLAR_GENERATABLE));
			fEglarPackage.setVendorName(settings.get(STORE_EXPORT_VENDOR));
			fEglarPackage.setVersionName(settings.get(STORE_EXPORT_VERSION));
		}
	}

	private void update() {
		updateModel();
	}

	/**
	 *	Stores the widget values in the JAR package.
	 */
	protected void updateModel() {
		if (getControl() == null) {
			return;
		}

		fEglarPackage.setExportEGLSrcFiles(false);
		fEglarPackage.setEglarGeneratable(true);
		fEglarPackage.setVendorName(this.fVendorText.getText());
		fEglarPackage.setVersionName(fVersionText.getText());
	}

	/*
	 * Implements method from IJarPackageWizardPage
	 */
	public boolean isPageComplete() {
		return true;
	}

	public boolean canFlipToNextPage() {
		return fEglarPackage.areGeneratedFilesExported() && super.canFlipToNextPage();
	}

	/*
	 * Overrides method from WizardDataTransferPage
	 */
	protected void createDescriptionFileGroup(Composite parent) {
		fTheGroup= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		fTheGroup.setLayout(layout);
		fTheGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		fVendorLabel= new Label(fTheGroup, SWT.NONE);
		fVendorLabel.setText(EglarPackagerMessages.EglarOptionsPage_vendor_label);

		fVendorText= new Text(fTheGroup, SWT.SINGLE | SWT.BORDER);
		fVendorText.addListener(SWT.Modify, new UntypedListener());
		GridData data= new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.widthHint= convertWidthInCharsToPixels(40);
		fVendorText.setLayoutData(data);

		
		fVersionLabel= new Label(fTheGroup, SWT.NONE);
		fVersionLabel.setText(EglarPackagerMessages.EglarOptionsPage_version_label);

		fVersionText= new Text(fTheGroup, SWT.SINGLE | SWT.BORDER);
		fVersionText.addListener(SWT.Modify, new UntypedListener());
		data= new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.widthHint= convertWidthInCharsToPixels(20);
		fVersionText.setText("1.0");
		fVersionText.setLayoutData(data);
	}

	/*
	 * Implements method from IJarPackageWizardPage.
	 */
	public void finish() {
		saveWidgetValues();
	}

	/**
	 * Creates a horizontal spacer line that fills the width of its container.
	 *
	 * @param parent the parent control
	 */
	protected void createSpacer(Composite parent) {
		Label spacer= new Label(parent, SWT.NONE);
		GridData data= new GridData();
		data.horizontalAlignment= GridData.FILL;
		data.verticalAlignment= GridData.BEGINNING;
		spacer.setLayoutData(data);
	}
}
