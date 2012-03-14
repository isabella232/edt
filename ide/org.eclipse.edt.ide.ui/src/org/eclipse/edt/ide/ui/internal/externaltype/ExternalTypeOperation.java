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
package org.eclipse.edt.ide.ui.internal.externaltype;

import org.eclipse.edt.ide.ui.editor.EGLCodeFormatterUtil;
import org.eclipse.edt.ide.ui.wizards.PartOperation;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

public class ExternalTypeOperation extends PartOperation {
	protected String codeTemplateId;
	protected Object contentObj;

	public ExternalTypeOperation(ExternalTypeConfiguration configuration, String templateID, Object contentsObj) {
		super(configuration);
		this.codeTemplateId = templateID;
		this.contentObj = contentsObj;
	}
	
	public String getFileContents() throws PartTemplateException {
		String contents = null;

		if (contentObj != null) {
			contents = contentObj.toString();
		} else {
	
			ExternalTypeConfiguration configuration = (ExternalTypeConfiguration) super.configuration;
			if (codeTemplateId == null) {
				if (configuration.getExternalTypeType() == ExternalTypeConfiguration.BASIC_EXTERNALTYPE) {
					codeTemplateId = "org.eclipse.edt.ide.ui.templates.flexible_basic_externaltype"; //$NON-NLS-1$
				}
			}
	
			String partName = configuration.getExternalTypeName();
			
			contents = getFileContents("externaltype", //$NON-NLS-1$
					codeTemplateId, new String[] { "${externalTypeName}", "${externalTypeName}","${packageName}"//$NON-NLS-1$
					}, new String[] { partName,partName,configuration.getFPackage() });
		}

		try {
			Document doc = new Document();
			doc.set(contents);
			TextEdit edit = EGLCodeFormatterUtil.format(doc, null);
			edit.apply(doc);
			contents = doc.get();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return contents;
	}
}
