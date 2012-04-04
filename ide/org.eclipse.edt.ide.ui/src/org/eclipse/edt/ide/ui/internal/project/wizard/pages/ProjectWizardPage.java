/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.project.wizard.pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.ide.ui.internal.project.wizard.fragments.IisCompleteListener;
import org.eclipse.edt.ide.ui.internal.project.wizard.fragments.WizardFragment;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public abstract class ProjectWizardPage extends WizardPage 
	implements IisCompleteListener {
	
	protected List fragments = new ArrayList();
	Composite parent;

	/**
	 * @param pageName
	 */
	public ProjectWizardPage(String pageName) {
		super(pageName);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		this.parent = new Composite(parent, SWT.NONE);
		createContents(this.parent);
		setDefaults();
		setControl(this.parent);
	}
	
	/**
	 * Setup the default values for this page. Subclasses should override to provide appropriate
	 * defaults.
	 */
	protected void setDefaults() {
		restoreDefaultSettings();
	}

	/**
	 * Subclasses should implement this method if they have default settings that have been stored
	 * and need to be restored.
	 * 
	 * @see storeDefaultSettings()
	 */
	protected void restoreDefaultSettings() {
	}

	public abstract void createContents(Composite parent);
	
	protected void registerFragment(WizardFragment fragment) {
		this.fragments.add(fragment);
	}
	
	public void dispose() {
		for (Iterator iter = this.fragments.iterator(); iter.hasNext();) {
			WizardFragment fragment = (WizardFragment) iter.next();
			fragment.dispose();
		}
		this.fragments.clear();
		super.dispose();
	}
	
	/**
	 * This should be called by the Wizard just prior to running the performFinish operation.
	 * Subclasses should override to store their default settings.
	 */
	public void storeDefaultSettings() {
	};
	
	/**
	 * Subclasses need to set the project name in their underlying model
	 * @param projectName
	 */
	public abstract void setProjectName(String projectName);
	
	public void handle(boolean isComplete) {
		setPageComplete(isComplete);
	}		

}
