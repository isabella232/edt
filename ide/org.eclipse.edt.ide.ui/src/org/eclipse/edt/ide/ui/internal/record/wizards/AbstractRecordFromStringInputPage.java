/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.record.wizards;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusUtil;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizardMessages;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractRecordFromStringInputPage extends WizardPage implements SelectionListener, ModifyListener {
	protected Button createFromUrlButton;
	protected Composite urlConfigComposite;
	protected Combo urlText;
	protected String[] recentUrls;

	protected Button createFromFileButton;
	protected Composite fileConfigComposite;
	protected Combo fileText;
	protected Button browseButton;
	protected String[] recentFilePaths;

	protected Button createFromStringButton;
	protected Composite stringConfigComposite;
	protected Text stringText;

	protected ISelection selection;

	protected boolean inputNeedsProcessing = true;

	public AbstractRecordFromStringInputPage(ISelection selection) {
		super(NewRecordWizardMessages.AbstractRecordFromStringInputPage_pageName);
		this.selection = selection;
		
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		createFromUrlButton = createFromUrlButton(container);

		urlConfigComposite = createUrlComposite(container);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalIndent = 18;
		urlConfigComposite.setLayoutData(data);

		createFromFileButton = createFromFileButton(container);

		fileConfigComposite = createFileComposite(container);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalIndent = 18;
		fileConfigComposite.setLayoutData(data);

		createFromStringButton = createFromStringButton(container);

		stringConfigComposite = createStringComposite(container);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalIndent = 18;
		stringConfigComposite.setLayoutData(data);

		createFromStringButton.setSelection(true);

		enableComposite(urlConfigComposite, createFromUrlButton.getSelection());
		enableComposite(fileConfigComposite, createFromFileButton.getSelection());
		enableComposite(stringConfigComposite, createFromStringButton.getSelection());

		setControl(container);

		ok(false);
	}

	protected Button createFromUrlButton(Composite container) {
		return createOptionButton(container, NewRecordWizardMessages.AbstractRecordFromStringInputPage_createFromURL);
	}

	protected Button createFromFileButton(Composite container) {
		return createOptionButton(container, NewRecordWizardMessages.AbstractRecordFromStringInputPage_createFromFile);
	}

	protected Button createFromStringButton(Composite container) {
		return createOptionButton(container, NewRecordWizardMessages.AbstractRecordFromStringInputPage_createFromString);
	}

	protected Button createOptionButton(Composite container, String text) {
		Button button = new Button(container, SWT.RADIO);
		button.setText(text);
		GridData data = new GridData();
		button.setLayoutData(data);
		button.addSelectionListener(this);

		return button;
	}

	protected Composite createFileComposite(Composite parent) {
		Composite container = new Composite(parent, 0);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);

		Label label = new Label(container, 0);
		label.setText(NewRecordWizardMessages.AbstractRecordFromStringInputPage_fileLabel);

		Composite c = new Composite(container, 0);
		layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		c.setLayout(layout);
		c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileText = new Combo(c, SWT.BORDER | SWT.SINGLE);
		fileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (recentFilePaths != null) {
			fileText.setItems(recentFilePaths);
		}
		fileText.addModifyListener(this);

		browseButton = new Button(c, SWT.PUSH);
		browseButton.setText(NewRecordWizardMessages.AbstractRecordFromStringInputPage_browseLabel);
		browseButton.addSelectionListener(this);

		return container;
	}

	protected Composite createUrlComposite(Composite parent) {
		Composite container = new Composite(parent, 0);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);

		Label label = new Label(container, 0);
		label.setText(NewRecordWizardMessages.AbstractRecordFromStringInputPage_urlLabel);

		Composite c = new Composite(container, 0);
		layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		c.setLayout(layout);
		c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		urlText = new Combo(c, SWT.BORDER | SWT.SINGLE);
		urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (recentUrls != null) {
			urlText.setItems(recentUrls);
		}
		urlText.addModifyListener(this);

		return container;
	}

	protected Composite createStringComposite(Composite parent) {
		Composite container = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);

		Label label = new Label(container, 0);
		label.setText(NewRecordWizardMessages.AbstractRecordFromStringInputPage_stringEntryLabel);

		stringText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		stringText.setFont(JFaceResources.getTextFont());
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		stringText.setLayoutData(data);
		stringText.addModifyListener(this);

		return container;
	}

	public void modifyText(ModifyEvent event) {
		inputNeedsProcessing = true;

		validatePage();
	}

	public Object getInput() 
	{
		String input = null;
		if (createFromStringButton.getSelection()) 
		{
			input = stringText.getText();
		}
		else if (createFromUrlButton.getSelection() ||
				(createFromFileButton.getSelection() && fileText.getText().trim().length() != 0))
		{
			try 
			{
				ReadProgressMonitorDialog dialog;
			
				if (createFromUrlButton.getSelection())
				{
					URL url = new URL(urlText.getText());
					dialog= new ReadProgressMonitorDialog(getShell(), url);
				}
				else
				{
					dialog= new ReadProgressMonitorDialog(getShell(), new File(fileText.getText()));
				}
				dialog.run();
				if (dialog.isStatusSuccess())
				{
					input = dialog.getInput();
				}
				else if (!dialog.isStatusCanceled())
				{
					error(dialog.getError());
				}
			}
			catch (Exception ex)
			{
				error(ex.getMessage());
			}
		}

		return input;
	}

	public void widgetSelected(SelectionEvent event) {
		if (event.getSource() == createFromFileButton) {
			enableComposite(fileConfigComposite, createFromFileButton.getSelection());
		} else if (event.getSource() == createFromUrlButton) {
			enableComposite(urlConfigComposite, createFromUrlButton.getSelection());
		} else if (event.getSource() == createFromStringButton) {
			enableComposite(stringConfigComposite, createFromStringButton.getSelection());
		} else if (event.getSource() == browseButton) {
			FileDialog dialog = new FileDialog(getShell());
			dialog.setFilterExtensions(getValidInputFileExtensions());
			if (dialog.open() != null) {
				fileText.setText(dialog.getFilterPath() + File.separator + dialog.getFileName());
			}
		}

		inputNeedsProcessing = true;

		validatePage();
	}

	protected void validatePage() {
		String errorMessage = null;

		if (createFromUrlButton.getSelection()) {
			if (urlText.getText().trim().length() > 0) {
				try {
					new URL(urlText.getText());
				} catch (MalformedURLException ex) {
					errorMessage = ex.getMessage();
				}
			} else {
				errorMessage = NewRecordWizardMessages.AbstractRecordFromStringInputPage_enterURLMessage;
			}
		} else if (createFromFileButton.getSelection()) {
			if (fileText.getText().trim().length() == 0) {
				errorMessage = NewRecordWizardMessages.AbstractRecordFromStringInputPage_enterFilenameMessage;
			}
		} else if (createFromStringButton.getSelection()) {
			if (stringText.getText().trim().length() == 0) {
				errorMessage = NewRecordWizardMessages.AbstractRecordFromStringInputPage_enterStringMessage;
			}
		}

		if (errorMessage == null) {
			ok(true);
		} else {
			error(errorMessage);
		}
	}

	protected void ok(boolean bool) {
		StatusInfo status = new StatusInfo();
		status.setOK();
		setPageComplete(bool);
		StatusUtil.applyToStatusLine(this, status);
	}

	protected void error(String str) {
		StatusInfo status = new StatusInfo();
		status.setError(str);
		setPageComplete(false);
		StatusUtil.applyToStatusLine(this, status);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// Not implemented
	}

	protected abstract String[] getValidInputFileExtensions();

	private void enableComposite(Composite c, boolean enabled) {
		Control[] controls = c.getChildren();
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] instanceof Composite) {
				enableComposite((Composite) controls[i], enabled);
			}
			controls[i].setEnabled(enabled);
		}
	}

	public boolean isInputNeedsProcessing() {
		return inputNeedsProcessing;
	}
	
	public void setInputNeedsProcessing(boolean b) {
		this.inputNeedsProcessing = b;
	}
	
	public void setRecentUrls(String[] urls) {
		this.recentUrls = urls;
	}
	
	public void setRecentFilePaths(String[] filePaths) {
		this.recentFilePaths = filePaths;
	}
	
	public boolean isUrlSource() {
		return createFromUrlButton.getSelection();
	}
	
	public boolean isFileSource() {
		return createFromFileButton.getSelection();
	}
	
	public boolean isStringSource() {
		return createFromStringButton.getSelection();
	}
	
	public String getUrl() {
		return urlText.getText();
	}
	
	public String getFilePath() {
		return fileText.getText();
	}
}
