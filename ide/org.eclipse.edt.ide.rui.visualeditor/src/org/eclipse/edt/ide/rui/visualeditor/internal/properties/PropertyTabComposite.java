/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class PropertyTabComposite extends Composite {

	public static final int NUM_COLUMNS = 6;
	private int numElements = 0;
	private Composite nonBooleanElements = null;
	private Composite booleanElements = null;
	
	public PropertyTabComposite(Composite parent, int style) {
		super(parent, style);
		
		nonBooleanElements = new Composite(this, SWT.NONE );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		gridLayout.marginWidth = 5;
		gridLayout.makeColumnsEqualWidth = false;
		gridLayout.horizontalSpacing = 10;
		nonBooleanElements.setLayout( gridLayout );
		
		booleanElements = new Composite(this, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 5;
		gridLayout.makeColumnsEqualWidth = false;
		gridLayout.horizontalSpacing = 10;
		booleanElements.setLayout( gridLayout );
	}
	
	public Composite getNonBooleanElementComposite(){
		return nonBooleanElements;
	}
	
	public Composite getBooleanElementsComposite(){
		return booleanElements;
	}
}
