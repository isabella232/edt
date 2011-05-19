/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.edt.ide.core.Logger;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.FolderSelectionDialog;
import org.eclipse.edt.ide.ui.internal.wizards.IStatusChangeListener;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.osgi.service.prefs.BackingStoreException;

public class GenerationSettingsComposite extends Composite {
	
	protected final IResource resource;
	protected final IPreferenceStore prefStore;
	protected final IEclipsePreferences projectPrefs;
	protected final String propertyID;
	protected final String preferenceID;
	protected final IStatusChangeListener statusListener;
	
	protected Button radioOutside;
	protected Button radioInside;
	protected Button browseInside;
	protected Button browseOutside;
	protected Text genInsideDirectory;
	protected Text genOutsideDirectory;
	
	private StatusInfo latestStatus = new StatusInfo();
	
	public GenerationSettingsComposite(Composite parent, int style, IResource resource, IPreferenceStore prefStore,
			IEclipsePreferences projectPrefs, String propertyID, String preferenceID, IStatusChangeListener statusListener) {
		super(parent, style);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);
		
		this.resource = resource;
		this.prefStore = prefStore;
		this.projectPrefs = projectPrefs;
		this.propertyID = propertyID;
		this.preferenceID = preferenceID;
		this.statusListener = statusListener;
		
		createContents();
		init();
	}
	
	private void createContents() {
		Group group = new Group(this, SWT.BORDER);
		group.setText(UINlsStrings.genSettingsGroupLabel);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		group.setLayout(layout);
		group.setFont(getFont());

		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalIndent = 0;
		group.setLayoutData(data);
		
		if (resource == null) {
			Composite c = new Composite(group, SWT.NULL);
			layout = new GridLayout();
			layout.numColumns = 2;
			c.setLayout(layout);
			c.setFont(group.getFont());
			data = new GridData(GridData.FILL_BOTH);
			data.horizontalIndent = 0;
			c.setLayoutData(data);
			
			new Label(c, SWT.LEFT).setText(UINlsStrings.genDefaultGenDir);
			
			genInsideDirectory = new Text(c, SWT.BORDER);
			data = new GridData(GridData.FILL_HORIZONTAL);
			genInsideDirectory.setLayoutData(data);
			
			if (statusListener != null) {
				genInsideDirectory.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						String text = genInsideDirectory.getText().trim();
						if (text.length() == 0) {
							latestStatus.setError(UINlsStrings.genSettingsValidationDefaultBlank);
							statusListener.statusChanged(latestStatus);
						}
						else if (!new Path(text).isValidPath(text)) {
							latestStatus.setError(UINlsStrings.genSettingsValidationDefaultInvalid);
							statusListener.statusChanged(latestStatus);
						}
						else if (text.charAt(0) == '/') {
							latestStatus.setError(UINlsStrings.genSettingsValidationDefaultRetlative);
							statusListener.statusChanged(latestStatus);
						}
						else if (latestStatus != null && !latestStatus.isOK()){
							latestStatus.setOK();
							statusListener.statusChanged(latestStatus);
						}
					}
				});
			}
		}
		else {
			radioInside = new Button(group, SWT.RADIO);
			radioInside.setText(UINlsStrings.genInsideWorkbench);
			data = new GridData(GridData.FILL_HORIZONTAL);
			radioInside.setLayoutData(data);
			
			Composite cIn = new Composite(group, SWT.NULL);
			layout = new GridLayout();
			layout.numColumns = 2;
			cIn.setLayout(layout);
			cIn.setFont(group.getFont());
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalIndent = 15;
			cIn.setLayoutData(data);
			
			genInsideDirectory = new Text(cIn, SWT.BORDER);
			data = new GridData(GridData.FILL_HORIZONTAL);
			genInsideDirectory.setLayoutData(data);
			
			browseInside = new Button(cIn, SWT.PUSH);
			browseInside.setText("..."); //$NON-NLS-1$
			
			radioOutside = new Button(group, SWT.RADIO);
			radioOutside.setText(UINlsStrings.genOutsideWorkbench);
			data = new GridData(GridData.FILL_HORIZONTAL);
			radioOutside.setLayoutData(data);
			
			Composite cOut = new Composite(group, SWT.NULL);
			layout = new GridLayout();
			layout.numColumns = 2;
			cOut.setLayout(layout);
			cOut.setFont(group.getFont());
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalIndent = 15;
			cOut.setLayoutData(data);
			
			genOutsideDirectory = new Text(cOut, SWT.BORDER);
			data = new GridData(GridData.FILL_HORIZONTAL);
			genOutsideDirectory.setLayoutData(data);
			
			browseOutside = new Button(cOut, SWT.PUSH);
			browseOutside.setText("..."); //$NON-NLS-1$
			
			browseInside.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					FolderSelectionDialog dialog = new FolderSelectionDialog(browseInside.getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
					dialog.setAllowMultiple(false);
					
					String currentValue = genInsideDirectory.getText();
					if (currentValue.length() > 0) {
						IResource initialSelection;
						if (currentValue.charAt(0) == '/') {
							initialSelection = ResourcesPlugin.getWorkspace().getRoot().findMember(currentValue);
						}
						else {
							initialSelection = resource.getProject().findMember(currentValue);
						}
						if (initialSelection != null) {
							dialog.setInitialSelection(initialSelection);
						}
					}
					
					dialog.setTitle(UINlsStrings.genDirSelectionTitle);
					dialog.setMessage(UINlsStrings.genDirSelectionMsg);
					dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
					if (dialog.open() == Window.OK) {
						Object result = dialog.getResult()[0];
						if (result instanceof IContainer) {
							IContainer container = (IContainer)result;
							if (!(container instanceof IProject) && container.getProject().equals(resource.getProject())) {
								genInsideDirectory.setText(container.getProjectRelativePath().toString());
							}
							else {
								genInsideDirectory.setText(container.getFullPath().toString());
							}
						}
					}
				}
			});
			
			browseOutside.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(browseInside.getShell());
					dialog.setText(UINlsStrings.genDirSelectionTitle);
					dialog.setMessage(UINlsStrings.genDirSelectionMsg);
					dialog.setFilterPath(genOutsideDirectory.getText());
					String result = dialog.open();
					if (result != null) {
						genOutsideDirectory.setText(result);
					}
				}
			});
			
			radioInside.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					enableDisableControls();
				}
			});
			
			radioOutside.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					enableDisableControls();
				}
			});
			
			if (statusListener != null) {
				genInsideDirectory.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						if (radioInside.getSelection()) {
							String text = genInsideDirectory.getText().trim();
							if (text.length() == 0) {
								latestStatus.setError(UINlsStrings.genSettingsValidationBlank);
								statusListener.statusChanged(latestStatus);
							}
							else {
								IPath path = new Path(text);
								if (!path.isValidPath(text)) {
									latestStatus.setError(UINlsStrings.genSettingsValidationInvalid);
									statusListener.statusChanged(latestStatus);
								}
								else if (text.charAt(0) == '/' && (path.segmentCount() < 1 || !ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0)).exists())) {
									latestStatus.setError(UINlsStrings.genSettingsValidationProject);
									statusListener.statusChanged(latestStatus);
								}
								else if (latestStatus != null && !latestStatus.isOK()){
									latestStatus.setOK();
									statusListener.statusChanged(latestStatus);
								}
							}
						}
					}
				});
				
				genOutsideDirectory.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						if (radioOutside.getSelection()) {
							if (genOutsideDirectory.getText().trim().length() == 0) {
								latestStatus.setError(UINlsStrings.genSettingsValidationBlank);
								statusListener.statusChanged(latestStatus);
							}
							else if (latestStatus != null && !latestStatus.isOK()){
								latestStatus.setOK();
								statusListener.statusChanged(latestStatus);
							}
						}
					}
				});
			}
		}
	}
	
	private void init() {
		if (resource == null) {
			genInsideDirectory.setText(prefStore.getString(preferenceID));
		}
		else {
			String genDir = ProjectSettingsUtility.getGenerationDirectory(resource, prefStore, projectPrefs, propertyID, preferenceID);
			if (new Path(genDir).isAbsolute()) {
				radioOutside.setSelection(true);
				radioInside.setSelection(false);
				genInsideDirectory.setText(""); //$NON-NLS-1$
				genOutsideDirectory.setText(genDir);
			}
			else {
				radioInside.setSelection(true);
				radioOutside.setSelection(false);
				
				genDir = EclipseUtilities.convertFromInternalPath(genDir);
				if (genDir.length() == 0) {
					genDir = resource.getProject().getFullPath().toString();
				}
				genOutsideDirectory.setText(""); //$NON-NLS-1$
				genInsideDirectory.setText(genDir);
			}
			enableDisableControls();
		}
	}
	
	private void enableDisableControls() {
		if (radioInside.getSelection()) {
			genOutsideDirectory.setEnabled(false);
			browseOutside.setEnabled(false);
			genInsideDirectory.setEnabled(true);
			browseInside.setEnabled(true);
		}
		else {
			genInsideDirectory.setEnabled(false);
			browseInside.setEnabled(false);
			genOutsideDirectory.setEnabled(true);
			browseOutside.setEnabled(true);
		}
	}

	public void performDefaults() {
		latestStatus.setOK();
		statusListener.statusChanged(latestStatus);
		init();
	}
	
	public boolean performOK() {
		if (resource == null) {
			prefStore.setValue(preferenceID, genInsideDirectory.getText());
		}
		else {
			try {
				String value;
				
				if (radioOutside.getSelection()) {
					value = genOutsideDirectory.getText();
				}
				else {
					// Save using internal format.
					value = genInsideDirectory.getText();
					if (value.equals(resource.getProject().getFullPath().toString())) {
						// just use an indicator for 'this project'
						value = ""; //$NON-NLS-1$
					}
					value = EclipseUtilities.convertToInternalPath(value);
				}
				
				ProjectSettingsUtility.setGenerationDirectory(resource, value, projectPrefs, propertyID);
			}
			catch (BackingStoreException e) {
				Logger.log("GenerationSettingsComposite.performOk", NLS.bind(UINlsStrings.genSettingsSaveError, resource.getFullPath().toString()), e); //$NON-NLS-1$
				return false;
			}
		}
		
		return true;
	}

}
