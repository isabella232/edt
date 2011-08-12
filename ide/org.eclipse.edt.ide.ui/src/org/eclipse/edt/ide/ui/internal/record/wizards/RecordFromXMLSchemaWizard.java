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
package org.eclipse.edt.ide.ui.internal.record.wizards;

import org.eclipse.edt.ide.ui.internal.record.conversion.RecordSource;
import org.eclipse.edt.ide.ui.internal.record.conversion.xmlschema.XMLSchemaConversion;
import org.eclipse.ui.IWorkbenchWizard;

public class RecordFromXMLSchemaWizard extends AbstractRecordFromInputWizard implements IWorkbenchWizard {
	
	public RecordFromXMLSchemaWizard() {
		super();
	}

	protected AbstractRecordFromStringInputPage createInputPage() {
		return new RecordFromXMLSchemaPage(selection);
	}

	protected boolean processInput(Object input) {
		setParts(null);
		setMessages(null);
		
		boolean ret = false;
		
		if (input != null && (input instanceof RecordSource)) {
			XMLSchemaConversion conv = new XMLSchemaConversion();
			if (conv.convert((RecordSource)input)) {
				setParts(conv.getResultParts());
			}
			if (!conv.isOK())
				setMessages(conv.getErrors());
			else
				setMessages(conv.getMessages());	
			
			ret = conv.isOK();
		}

		return ret;
	}
}
