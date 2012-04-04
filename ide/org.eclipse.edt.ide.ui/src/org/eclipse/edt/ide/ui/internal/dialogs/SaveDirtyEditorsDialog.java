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
package org.eclipse.edt.ide.ui.internal.dialogs;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.SelectionDialog;


public class SaveDirtyEditorsDialog extends SelectionDialog {

	private IStructuredContentProvider fContentProvider;
	private ILabelProvider fLabelProvider;
	private ViewerSorter fSorter;
	private Object fInput;
	private TableViewer fTableViewer;
	private Table table;

	public SaveDirtyEditorsDialog(Shell parent) {
		super(parent);
	}

	public void setInput(Object input) {
		fInput = input;
	}

	public void setContentProvider(IStructuredContentProvider sp) {
		fContentProvider = sp;
	}

	public void setLabelProvider(ILabelProvider lp) {
		fLabelProvider = lp;
	}
	
	public void setSorter(ViewerSorter sorter) {
		fSorter = sorter;
	}

	public TableViewer getTableViewer() {
		return fTableViewer;
	}
	
	public void create() {
		setShellStyle(getShellStyle() | SWT.RESIZE);
		super.create();
	}

	protected Label createMessageArea(Composite composite) {
		Label label = new Label(composite, SWT.WRAP);
		label.setText(getMessage());
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(55);
		label.setLayoutData(gd);
		applyDialogFont(label);
		return label;
	}

	protected Control createDialogArea(Composite container) {
		Composite parent = (Composite) super.createDialogArea(container);
		createMessageArea(parent);
		fTableViewer = new TableViewer(parent, getTableStyle());
		fTableViewer.setContentProvider(fContentProvider);
		table = fTableViewer.getTable();
		fTableViewer.setLabelProvider(fLabelProvider);
		if (fSorter != null) {
			fTableViewer.setSorter(fSorter);
		}
		fTableViewer.setInput(fInput);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = convertWidthInCharsToPixels(55);
		gd.heightHint = convertHeightInCharsToPixels(15);
		table.setLayoutData(gd);
		table.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				handleSelectionChanged();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		applyDialogFont(parent);
		return parent;
	}

	protected int getTableStyle() {
		return SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
	}
    
    private void handleSelectionChanged() {
        table.deselectAll();
    }

}
