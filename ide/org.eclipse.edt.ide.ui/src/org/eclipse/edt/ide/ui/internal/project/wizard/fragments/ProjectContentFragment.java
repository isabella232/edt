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
package org.eclipse.edt.ide.ui.internal.project.wizard.fragments;

import org.eclipse.edt.ide.ui.internal.project.wizard.pages.ProjectWizardMainPage;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class ProjectContentFragment extends WizardFragment {	
	
	protected Group group;
	protected Label description;
	public	  Button specifyProjectDirectory;
	protected Label directoryLabel;
	protected Text directory;
	protected Button browseDirectory;
	
	public ProjectContentFragment(Composite renderOn, ProjectWizardMainPage parentPage) {
		super(renderOn, parentPage);
	}

	public Composite renderSection() {
		this.group = new Group(this.renderOn, SWT.NULL);
		this.group.setText(NewWizardMessages.ProjectContentFragment_0);
		this.group.setLayout(new FormLayout());
		
		this.description = new Label(this.group, SWT.WRAP);
		this.description.setText(NewWizardMessages.ProjectContentFragment_1);

		this.specifyProjectDirectory = new Button(this.group, SWT.CHECK);
		this.specifyProjectDirectory.setText(NewWizardMessages.ProjectContentFragment_2);
		
		this.directoryLabel = new Label(this.group, SWT.NULL);
		this.directoryLabel.setText(NewWizardMessages.ProjectContentFragment_3);
		
		this.directory = new Text(this.group, SWT.BORDER);
		
		this.browseDirectory = new Button(this.group, SWT.PUSH);
		this.browseDirectory.setText(NewWizardMessages.ProjectContentFragment_4);
		
		directory.setEnabled(false);
		browseDirectory.setEnabled(false);
		
		hookListeners();
		layout();
		setInitialValues();
		
		return group;
	}
	
	protected void setInitialValues() {
	}
	
	private void layout() {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 10);
		data.top = new FormAttachment(0, 10);
		data.right = new FormAttachment(100, -10);
		this.description.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0, 10);
		data.top = new FormAttachment(this.description, 10);
		this.specifyProjectDirectory.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0, 10);
		data.top = new FormAttachment(this.specifyProjectDirectory, 10);
		this.directoryLabel.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(this.directoryLabel, 10);
		data.top = new FormAttachment(this.directoryLabel, 0 , SWT.CENTER);
		data.right = new FormAttachment(this.browseDirectory, -5, SWT.LEFT);
		this.directory.setLayoutData(data);
		
		data = new FormData();
		data.right = new FormAttachment(100, -10);
		data.top = new FormAttachment(this.directoryLabel, 0 , SWT.CENTER);
		this.browseDirectory.setLayoutData(data);
	}

	protected abstract void hookListeners();
	
	/**
	 * Open an appropriate directory browser
	 */
	protected abstract void handleLocationBrowseButtonPressed();
	
}
