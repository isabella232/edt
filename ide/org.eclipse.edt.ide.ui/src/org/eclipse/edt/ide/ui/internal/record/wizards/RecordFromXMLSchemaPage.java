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

import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizardMessages;
import org.eclipse.edt.ide.ui.internal.record.conversion.IMessageHandler;
import org.eclipse.edt.ide.ui.internal.record.conversion.RecordSource;
import org.eclipse.edt.ide.ui.internal.record.conversion.xmlschema.XMLSchemaConversion.GenericLSInput;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

public class RecordFromXMLSchemaPage extends AbstractRecordFromStringInputPage implements DOMErrorHandler {
	public RecordFromXMLSchemaPage(ISelection selection) {
		super(selection);
		setTitle(NewRecordWizardMessages.RecordFromXMLSchemaPage_title);
		setDescription(NewRecordWizardMessages.RecordFromXMLSchemaPage_description);	
	}

	protected String[] getValidInputFileExtensions() {
		return new String[] { "*.xsd", "*" };//$NON-NLS-1$
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IUIHelpConstants.EGL_NEW_RECORD_FROM_SCHEMA_PAGE);
	}

	protected void validatePage() {
		super.validatePage();
		
		if (isPageComplete()) {
			if (createFromStringButton.getSelection() && stringText.getText().trim().length() > 0) {
				try {			
					DOMImplementationRegistry domRegistry = DOMImplementationRegistry.newInstance();
					XSImplementation xsImpl = (XSImplementation) domRegistry.getDOMImplementation("XS-Loader");
					XSLoader xsLoader = xsImpl.createXSLoader(null);
					xsLoader.getConfig().setParameter("error-handler", this);//$NON-NLS-1$
					new java.io.StringReader(stringText.getText().trim());
					xsLoader.load(new GenericLSInput(stringText.getText().trim()));
				} catch (Throwable ex) {
					error(ex.getMessage());
				}		
			}
		}
	}

	public boolean handleError(DOMError error) {
		StringBuffer buffer = new StringBuffer();
		if (error.getLocation().getUri() != null)
			buffer.append("[" + error.getLocation().getUri() + "] ");
		if (error.getLocation().getLineNumber() != -1)
			buffer.append("[" + error.getLocation().getLineNumber() + "," + error.getLocation().getColumnNumber() + "] ");
		if (buffer.length() > 0) {
			buffer.append(error.getMessage());
			if (error.getSeverity() == DOMError.SEVERITY_FATAL_ERROR)
				this.error(buffer.toString());
			else
				((IMessageHandler)this.getWizard()).addMessage(buffer.toString());
		}
		return error.getSeverity() != DOMError.SEVERITY_FATAL_ERROR;
	}

	@Override
	public Object getInput() {
		String input = null;
		int source = 1;
		if (createFromStringButton.getSelection()) {
			input = stringText.getText();
			source = 1;
		} else if (createFromUrlButton.getSelection() ||
				(createFromFileButton.getSelection() && fileText.getText().trim().length() != 0)) {
			
			if (createFromUrlButton.getSelection()) {
				input = urlText.getText();
				source = 3;
			} else {
				input = fileText.getText();
				source = 2;
			}
		}
        RecordSource recordSource = new RecordSource(input,source);
		return recordSource;
	}
}
