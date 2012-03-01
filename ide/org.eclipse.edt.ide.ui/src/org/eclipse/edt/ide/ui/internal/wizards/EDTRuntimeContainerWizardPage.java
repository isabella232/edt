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
package org.eclipse.edt.ide.ui.internal.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTRuntimeContainer;
import org.eclipse.edt.ide.core.EDTRuntimeContainerEntry;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Wizard page that lets you pick an EDT runtime to be added to the Java build path. The available runtimes come from the generators.
 */
@SuppressWarnings("restriction")
public class EDTRuntimeContainerWizardPage extends NewElementWizardPage implements IClasspathContainerPage, IClasspathContainerPageExtension {
	
	private IClasspathEntry currentEntry;
	private EDTRuntimeContainer[] availableLibraries;
	private Combo runtimeCombo;
	private Label description;
	private Label path;
	
	public EDTRuntimeContainerWizardPage() {
		super("EDTRuntimeWizardPage"); //$NON-NLS-1$
		setTitle(NewWizardMessages.EDTRuntimeContainerPage_Title);
		setDescription(NewWizardMessages.EDTRuntimeContainerPage_Description);
		setImageDescriptor(JavaPluginImages.DESC_WIZBAN_ADD_LIBRARY);
		
		// Initialize the avaialble libraries.
		IGenerator[] gens = EDTCoreIDEPlugin.getPlugin().getGenerators();
		if (gens != null && gens.length != 0) {
			List<EDTRuntimeContainer> allLibs = new ArrayList<EDTRuntimeContainer>();
			for (int i = 0; i < gens.length; i++) {
				EDTRuntimeContainer[] libs = gens[i].getRuntimeContainers();
				if (libs != null && libs.length != 0) {
					for (int j = 0; j < libs.length; j++) {
						allLibs.add(libs[j]);
					}
				}
			}
			availableLibraries = allLibs.toArray(new EDTRuntimeContainer[allLibs.size()]);
		}
		else {
			availableLibraries = new EDTRuntimeContainer[0];
		}
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout(2, false));
		
		if (availableLibraries.length == 0 ) {
			new Label(composite, SWT.NONE).setText(NewWizardMessages.EDTRuntimeContainerPage_NoRuntimes);
			updateStatus(new StatusInfo(IStatus.ERROR, "")); //$NON-NLS-1$
		}
		else {
			Label label = new Label(composite, SWT.NONE);
			label.setText(NewWizardMessages.EDTRuntimeContainerPage_LibraryLabel);
			label.setFont(composite.getFont());
			
			runtimeCombo = new Combo(composite, SWT.READ_ONLY);
			runtimeCombo.setFont(composite.getFont());
			runtimeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			runtimeCombo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					selectionChanged();
				}
			});
			
			label = new Label(composite, SWT.NONE);
			label.setText(NewWizardMessages.EDTRuntimeContainerPage_DescriptionLabel);
			label.setFont(composite.getFont());
			
			description = new Label(composite, SWT.WRAP);
			description.setFont(composite.getFont());
			description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			label = new Label(composite, SWT.NONE);
			label.setText(NewWizardMessages.EDTRuntimeContainerPage_PathsLabel);
			label.setFont(composite.getFont());
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));
			
			path = new Label(composite, SWT.WRAP);
			path.setFont(composite.getFont());
			path.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			String[] items = new String[availableLibraries.length];
			for (int i = 0; i < items.length; i++) {
				items[i] = availableLibraries[i].getName();
			}
			runtimeCombo.setItems(items);
			int initialIndex = 0;
			if (currentEntry != null) {
				IPath currPath = currentEntry.getPath();
				for (int i = 0; i < availableLibraries.length; i++) {
					if (availableLibraries[i].getPath().equals(currPath)) {
						initialIndex = i;
						break;
					}
				}
			}
			runtimeCombo.select(initialIndex);
			selectionChanged();
		}
		
		setControl(composite);
	}
	
	private void selectionChanged() {
		int idx = runtimeCombo.getSelectionIndex();
		if (idx < 0 || idx >= availableLibraries.length) {
			// On Windows this gets called when setting the combo's items, but there is no selection yet.
			return;
		}
		EDTRuntimeContainer container = availableLibraries[idx];
		description.setText(container.getDescription());
		
		StringBuilder buf = new StringBuilder();
		EDTRuntimeContainerEntry[] entries = container.getEntries();
		if (entries != null && entries.length > 0) {
			for (int i = 0; i < entries.length; i++) {
				if (entries[i].getClasspathEntry() != null) {
					if (buf.length() > 0) {
						buf.append("\n"); //$NON-NLS-1$
					}
					buf.append(entries[i].getClasspathEntry().getPath());
				}
			}
		}
		path.setText(buf.toString());
		
		currentEntry = JavaCore.newContainerEntry(container.getPath());
	}

	@Override
	public void initialize(IJavaProject project, IClasspathEntry[] currentEntries) {
		setPageComplete(runtimeCombo != null && runtimeCombo.getItemCount() > 0);
	}

	@Override
	public boolean finish() {
		return true;
	}

	@Override
	public IClasspathEntry getSelection() {
		return currentEntry;
	}

	@Override
	public void setSelection(IClasspathEntry containerEntry) {
		currentEntry = containerEntry;
	}

}
