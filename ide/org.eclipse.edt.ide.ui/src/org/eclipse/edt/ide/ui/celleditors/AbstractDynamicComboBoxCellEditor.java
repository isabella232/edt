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


package org.eclipse.edt.ide.ui.celleditors;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * A cell editor that presents a list of items in a combo box.
 * The cell editor's value is the zero-based index of the selected
 * item.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public abstract class AbstractDynamicComboBoxCellEditor extends CellEditor {


	/**
	 * The custom combo box control.
	 */
	private CCombo comboBox;
	
	private int swtStyle = SWT.NONE;
	
/**
 * Creates a new cell editor with a combo containing the given 
 * list of choices and parented under the given control. The cell
 * editor value is the zero-based index of the selected item.
 * Initially, the cell editor has no cell validator and
 * the first item in the list is selected. 
 *
 * @param parent the parent control
 * @param items the list of strings for the combo box
 */
public AbstractDynamicComboBoxCellEditor(Composite parent, int swtStyle ) {
	super(parent);
	
	setSwtStyle(swtStyle);
}

public AbstractDynamicComboBoxCellEditor(Composite parent)
{
	super(parent);
}

protected void addKeyListener(Control control)
{
	control.addKeyListener(new KeyAdapter() {
		public void keyReleased(KeyEvent e) {
			keyReleaseOccured(e);
		}
	});
}

protected void addSelectionListener(CCombo combo)
{
	comboBox.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			editOccured();
		}
	});
}

protected void addTraverseListener(Control control)
{
	control.addTraverseListener(new TraverseListener() {
		public void keyTraversed(TraverseEvent e) {
			if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
				e.doit = false;
			}
		}
	});
}

/* (non-Javadoc)
 * Method declared on CellEditor.
 */
protected Control createControl(Composite parent) {
	
	comboBox = new CCombo(parent, getSwtStyle());
	comboBox.setVisibleItemCount(20);
	comboBox.setFont(parent.getFont());
	
	addModifyListener(comboBox);	

	addKeyListener(comboBox);

	addSelectionListener(comboBox);
	
	addTraverseListener(comboBox);

	return comboBox;
}

/**
 * Method addModifyListener.
 * @param comboBox
 */
protected void addModifyListener(CCombo comboBox) {
	
	comboBox.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			editOccured();
		}
	});
}

/* (non-Javadoc)
 * Method declared on CellEditor.
 */
protected void doSetFocus() {
	comboBox.setFocus();
}
/**
 *
 */
public CCombo getComboBox() {
	return comboBox;
}
/**
 * Set the text limit of the actual widget
 *
 * @param limit an integer (type <code>int</code>)
 */
public void setTextLimit(int limit) {
	getComboBox().setTextLimit(limit);
}	
	/**
	 * Gets the swtStyle.
	 * @return Returns a int
	 */
	public int getSwtStyle() {
		return swtStyle;
	}

	/**
	 * Sets the swtStyle.
	 * @param swtStyle The swtStyle to set
	 */
	public void setSwtStyle(int swtStyle) {
		this.swtStyle = swtStyle;
	}
	
	protected void editOccured() {
	String value = comboBox.getText();
	if (value == null)
		value = "";//$NON-NLS-1$
	Object typedValue = value;
	boolean oldValidState = isValueValid();
	boolean newValidState = isCorrect(typedValue);
	if (typedValue == null && newValidState)
		Assert.isTrue(false, "Validator isn't limiting the cell editor's type range");//$NON-NLS-1$
	if (!newValidState) {
		// try to insert the current value into the error message.
		setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] {value}));
	}
	valueChanged(oldValidState, newValidState);
}

}
