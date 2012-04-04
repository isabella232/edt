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

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.util.SWTUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardExportResourcesPage;

public abstract class AbstractEglarDestinationWizardPage extends WizardExportResourcesPage implements IEglarPackageWizardPage {
	
	private final String fStoreDestinationNamesId;

	private Combo fDestinationNamesCombo;
	private Button fDestinationBrowseButton;
	private final EglarPackageData fEglarPackage;

	public AbstractEglarDestinationWizardPage(String pageName, IStructuredSelection selection, EglarPackageData eglarPackage) {
		super(pageName, selection);
		fStoreDestinationNamesId= pageName + ".DESTINATION_NAMES_ID"; //$NON-NLS-1$
		fEglarPackage= eglarPackage;
	}
	
	/**
	 * Overrides method from WizardExportResourcesPage
	 */
	@Override
	protected void createDestinationGroup(Composite parent) {

		initializeDialogUnits(parent);

		// destination specification group
		Composite destinationSelectionGroup= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 3;
		destinationSelectionGroup.setLayout(layout);
		destinationSelectionGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));

		String label= getDestinationLabel();
		if (label != null) {
			new Label(destinationSelectionGroup, SWT.NONE).setText(label);
		} else {
			layout.marginWidth= 0;
			layout.marginHeight= 0;
		}

		// destination name entry field
		fDestinationNamesCombo= new Combo(destinationSelectionGroup, SWT.SINGLE | SWT.BORDER);
		SWTUtil.setDefaultVisibleItemCount(fDestinationNamesCombo);
		fDestinationNamesCombo.addListener(SWT.Modify, this);
		fDestinationNamesCombo.addListener(SWT.Selection, this);
		GridData data= new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.widthHint= SIZING_TEXT_FIELD_WIDTH;
		data.horizontalSpan= label == null ? 2 : 1;
		fDestinationNamesCombo.setLayoutData(data);
		if (label == null) {
			SWTUtil.setAccessibilityText(fDestinationNamesCombo, EglarPackagerMessages.AbstractEglarDestinationWizardPage_destinationCombo_AccessibilityText);
		}

		// destination browse button
		fDestinationBrowseButton= new Button(destinationSelectionGroup, SWT.PUSH);
		fDestinationBrowseButton.setText(EglarPackagerMessages.EglarPackageWizardPage_browseButton_text);
		fDestinationBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		SWTUtil.setButtonDimensionHint(fDestinationBrowseButton);
		fDestinationBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleDestinationBrowseButtonPressed();
			}
		});
	}
	
	/**
	 *	Open an appropriate destination browser so that the user can specify a source
	 *	to import from
	 */
	protected void handleDestinationBrowseButtonPressed() {
		
		DirectoryDialog dialog = new DirectoryDialog(getContainer().getShell(), SWT.SHEET);
		dialog.setMessage(getBrowseDialogTitle());
		String dirName = getDestinationValue();

		if (dirName.length() == 0) {
			dialog.setFilterPath(EDTUIPlugin.getWorkspace().getRoot().getLocation().toOSString());
		} else {
			File path = new File(dirName);
			if (path.exists()) {
				dialog.setFilterPath(new Path(dirName).toOSString());
			}
		}

		String selectedDirectory = dialog.open();
		fDestinationNamesCombo.setText(selectedDirectory);
	}
	
	
	protected abstract String getBrowseDialogTitle();
	
	/**
	 * Answer the contents of the destination specification widget. If this
	 * value does not have the required suffix then add it first.
	 *
	 * @return java.lang.String
	 */
	protected String getDestinationValue() {
		String destinationText= fDestinationNamesCombo.getText().trim();
		return destinationText;
	}
	
	/**
	 * 
	 * @param valueToSet
	 */
	protected void setDestinationValue(String valueToSet) {
		fDestinationNamesCombo.setText(valueToSet);
	}
	
	/**
	 *	Answer the string to display in self as the destination type
	 *
	 *	@return java.lang.String
	 */
	protected String getDestinationLabel() {
		return EglarPackagerMessages.EglarPackageWizardPage_destination_label;
	}
	
	/**
	 *	Answer the suffix that files exported from this wizard must have.
	 *	If this suffix is a file extension (which is typically the case)
	 *	then it must include the leading period character.
	 *
	 *	@return java.lang.String
	 */
	protected String getOutputSuffix() {
		return "." + EglarPackagerUtil.EGLAR_EXTENSION; //$NON-NLS-1$
	}
	
	protected void restoreLocation() {
		// destination
		if (fEglarPackage.getEglarLocation().isEmpty()) {
			fDestinationNamesCombo.setText(""); //$NON-NLS-1$
		} else {
			fDestinationNamesCombo.setText(fEglarPackage.getEglarLocation().toOSString());
		}
		
		IDialogSettings settings= getDialogSettings();
		if (settings != null) {
			String[] directoryNames= settings.getArray(fStoreDestinationNamesId);
			if (directoryNames == null)
				return; // ie.- no settings stored
			if (!fDestinationNamesCombo.getText().equals(directoryNames[0]))
				fDestinationNamesCombo.add(fDestinationNamesCombo.getText());
			for (int i= 0; i < directoryNames.length; i++)
				fDestinationNamesCombo.add(directoryNames[i]);
		}
	}
	
	protected void updateModel() {
		String comboText= fDestinationNamesCombo.getText();
		IPath path= Path.fromOSString(comboText);

		fEglarPackage.setEglarLocation(path);
	}
	
	/**
	 * Returns a boolean indicating whether the passed File handle is
	 * is valid and available for use.
	 *
	 * @param targetFile the target
	 * @return boolean
	 */
	protected boolean ensureTargetFileIsValid(File targetFile) {
		if (targetFile.exists()) {
			if (!targetFile.canWrite()) {
				setErrorMessage(EglarPackagerMessages.EglarPackageWizardPage_error_EglarFileExistsAndNotWritable);
				fDestinationNamesCombo.setFocus();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Overrides method from WizardDataTransferPage
	 */
	@Override
	protected boolean validateDestinationGroup() {
		if (fDestinationNamesCombo.getText().length() == 0) {
			// Clear error
			if (getErrorMessage() != null)
				setErrorMessage(null);
			if (getMessage() != null)
				setMessage(null);
			return false;
		}

		// Check if the eglar is put into the workspace and conflicts with the containers
		// exported. If the workspace isn't on the local files system we are fine since
		// the eglar is always created in the local file system
		IPath workspaceLocation= ResourcesPlugin.getWorkspace().getRoot().getLocation();
		if (workspaceLocation != null && workspaceLocation.isPrefixOf(fEglarPackage.getAbsoluteEglarLocation())) {
			int segments= workspaceLocation.matchingFirstSegments(fEglarPackage.getAbsoluteEglarLocation());
			IPath path= fEglarPackage.getAbsoluteEglarLocation().removeFirstSegments(segments);
			IResource resource= ResourcesPlugin.getWorkspace().getRoot().findMember(path);
			if (resource != null && resource.getType() == IResource.FILE) {
				// test if included
				if (EglarPackagerUtil.contains(EglarPackagerUtil.asResources(fEglarPackage.getElements()), (IFile) resource)) {
					setErrorMessage(EglarPackagerMessages.EglarPackageWizardPage_error_cantExportEglarIntoItself);
					return false;
				}
			}
		}
		// Inform user about relative directory
		String currentMessage= getMessage();
		if (!(new File(fDestinationNamesCombo.getText()).isAbsolute())) {
			if (currentMessage == null)
				setMessage(EglarPackagerMessages.EglarPackageWizardPage_info_relativeExportDestination, IMessageProvider.INFORMATION);
		} else {
			if (currentMessage != null)
				setMessage(null);
		}
		return ensureTargetFileIsValid(fEglarPackage.getAbsoluteEglarLocation().toFile());
	}
	
	/**
	 * Set the current input focus to self's destination entry field
	 */
	protected void giveFocusToDestination() {
		fDestinationNamesCombo.setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	protected void saveWidgetValues() {
		IDialogSettings settings= getDialogSettings();
		if (settings != null) {
			String[] directoryNames= settings.getArray(fStoreDestinationNamesId);
			if (directoryNames == null)
				directoryNames= new String[0];
			directoryNames= addToHistory(directoryNames, getDestinationValue());
			settings.put(fStoreDestinationNamesId, directoryNames);
		}
	}

	/**
	 *	Initializes the eglar package from last used wizard page values.
	 */
	protected void initializeEglarPackage() {
		IDialogSettings settings= getDialogSettings();
		if (settings != null) {
			// destination
			String[] directoryNames= settings.getArray(fStoreDestinationNamesId);
			if (directoryNames == null)
				return; // ie.- no settings stored
			fEglarPackage.setEglarLocation(Path.fromOSString(directoryNames[0]));
		}
	}

	/**
	 * Implements method from IEglarPackageWizardPage.
	 */
	public void finish() {
		saveWidgetValues();
	}

	/**
	 * Implements method from Listener
	 */
	public void handleEvent(Event e) {
		if (getControl() == null)
			return;
		update();
	}

	protected void update() {
		updateModel();
		updateWidgetEnablements();
		updatePageCompletion();
	}

	protected void updatePageCompletion() {
		boolean pageComplete= isPageComplete();
		setPageComplete(pageComplete);
		if (pageComplete)
			setErrorMessage(null);
	}
}
