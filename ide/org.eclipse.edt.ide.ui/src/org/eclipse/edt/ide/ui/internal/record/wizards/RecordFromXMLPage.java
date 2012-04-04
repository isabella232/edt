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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizardMessages;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RecordFromXMLPage extends AbstractRecordFromStringInputPage implements ModifyListener {
	public RecordFromXMLPage(ISelection selection) {
		super(selection);
		setTitle(NewRecordWizardMessages.RecordFromXMLPage_title);
		setDescription(NewRecordWizardMessages.RecordFromXMLPage_description);
	}

	protected String[] getValidInputFileExtensions() {
		return new String[] { "*.xml", "*" };//$NON-NLS-1$
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IUIHelpConstants.EGL_NEW_RECORD_FROM_XML_PAGE);
	}

	protected void validatePage() {
		super.validatePage();
		
		if (isPageComplete()) {
			if (createFromStringButton.getSelection() && stringText.getText().trim().length() > 0) {
				try {			
					DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					java.io.StringReader reader = new java.io.StringReader(stringText.getText().trim());
					Document dom = builder.parse(new org.xml.sax.InputSource(reader));
					Element doc = dom.getDocumentElement();					
				} catch (Throwable ex) {
					error(ex.getMessage());
				}		
			}
		}
	}
}
