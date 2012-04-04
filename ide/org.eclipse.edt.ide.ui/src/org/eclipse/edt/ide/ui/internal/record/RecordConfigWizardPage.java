/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.record;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class RecordConfigWizardPage extends WizardPage {

	private TableViewer fieldsViewer;

	private ISelection selection;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public RecordConfigWizardPage(ISelection selection) {
		super(NewRecordWizardMessages.RecordConfigWizardPage_pageName);
		setTitle(NewRecordWizardMessages.RecordConfigWizardPage_title);
		setDescription(NewRecordWizardMessages.RecordConfigWizardPage_description);
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		fieldsViewer = new TableViewer(container, SWT.FULL_SELECTION | SWT.BORDER);
		Table table = fieldsViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 300;
		table.setLayoutData(data);

		TableColumn nameColumn = new TableColumn(table, 0);
		nameColumn.setWidth(200);
		nameColumn.setText(NewRecordWizardMessages.RecordConfigWizardPage_nameLabel);

		TableColumn typeColumn = new TableColumn(table, 0);
		typeColumn.setWidth(200);
		typeColumn.setText(NewRecordWizardMessages.RecordConfigWizardPage_typeLabel);
		
		Composite buttonContainer = new Composite(container, 0);
		layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		buttonContainer.setLayout(layout);
		buttonContainer.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		Button addButton = new Button(buttonContainer, SWT.PUSH);
		addButton.setText(NewRecordWizardMessages.RecordConfigWizardPage_addLabel);
		addButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		Button removeButton = new Button(buttonContainer, SWT.PUSH);
		removeButton.setText(NewRecordWizardMessages.RecordConfigWizardPage_removeLabel);
		removeButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		new Label(buttonContainer, 0);

		Button upButton = new Button(buttonContainer, SWT.PUSH);
		upButton.setText(NewRecordWizardMessages.RecordConfigWizardPage_upLabel);
		upButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		Button downButton = new Button(buttonContainer, SWT.PUSH);
		downButton.setText(NewRecordWizardMessages.RecordConfigWizardPage_downLabel);
		downButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		setControl(container);
	}
}
