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
package org.eclipse.edt.ide.ui.internal.record.wizards;

import org.eclipse.edt.ide.ui.internal.record.NewRecordWizard;
import org.eclipse.edt.ide.ui.internal.record.RecordConfiguration;
import org.eclipse.edt.ide.ui.internal.record.conversion.json.PartsFromJsonUtil;
import org.eclipse.edt.ide.ui.templates.parts.Part;
import org.eclipse.edt.ide.ui.templates.parts.Record;
import org.eclipse.edt.javart.json.JsonParser;
import org.eclipse.edt.javart.json.ValueNode;
import org.eclipse.ui.IWorkbenchWizard;

public class RecordFromJsonWizard extends AbstractRecordFromInputWizard implements IWorkbenchWizard {
	public RecordFromJsonWizard() {
		super();
	}

	protected AbstractRecordFromStringInputPage createInputPage() {
		return new RecordFromJsonPage(selection);
	}

	protected boolean processInput(Object input) {
		setParts(null);
		setMessages(null);

		try {
			ValueNode node = JsonParser.parseValue(input.toString());
			setParts(createParts(node));
			((RecordConfiguration)((NewRecordWizard)getParentWizard()).getConfiguration()).setImports("import eglx.json.JSONName;");
		} catch (Throwable ex) {
			addMessage(ex.getMessage());
		}

		return (parts != null && parts.length > 0);
	}

	private Part[] createParts(ValueNode node) {
		Record rec = new Record();
		rec.setName(getFileName());
		return new PartsFromJsonUtil(this).process(node, rec);
	}

	private String getFileName() {
		RecordFromJsonWizard wiz = this;
		NewRecordWizard parentWiz = (NewRecordWizard) wiz.getParentWizard();
		return ((RecordConfiguration) parentWiz.getConfiguration()).getRecordName();
	}

}
