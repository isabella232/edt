/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Shell;

public class RecordPartSelectionDialog extends OpenPartSelectionDialog {
	List recordList;

	public RecordPartSelectionDialog(Shell parent, IRunnableContext context, int elementKinds, IEGLSearchScope scope, List records) {
		super(parent, context, elementKinds, scope);
		recordList = records;
	}

	protected String getNoPartsTitle() {
		return UINlsStrings.SQLRecordPartDialogErrorTitle;
	}

	protected String getNoPartsMessage() {
		return UINlsStrings.SQLRecordPartDialogNoPartsMessage;
	}	
	
	protected int addParts(ArrayList typeList, final IEGLSearchScope scope, final int elementKinds, final String subType) {
		typeList.addAll(recordList);
		return OK;
	}
}
