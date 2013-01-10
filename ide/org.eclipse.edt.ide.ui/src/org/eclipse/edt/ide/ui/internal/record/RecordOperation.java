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
package org.eclipse.edt.ide.ui.internal.record;

import org.eclipse.edt.ide.ui.editor.EGLCodeFormatterUtil;
import org.eclipse.edt.ide.ui.wizards.PartOperation;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

public class RecordOperation extends PartOperation {
	protected String codeTemplateId;
	protected Object contentObj;

	public RecordOperation(RecordConfiguration configuration, String templateID, Object contentsObj) {
		super(configuration);
		this.codeTemplateId = templateID;
		this.contentObj = contentsObj;
	}

	public String getFileContents() throws PartTemplateException {
		String contents = null;

		if (contentObj != null) {
			contents = contentObj.toString();
		} else {
	
			RecordConfiguration configuration = (RecordConfiguration) super.configuration;
			if (codeTemplateId == null) {
				if (configuration.getRecordType() == RecordConfiguration.BASIC_RECORD) {
					codeTemplateId = "org.eclipse.edt.ide.ui.templates.flexible_basic_record"; //$NON-NLS-1$
				}
			}
	
			String partName = configuration.getRecordName();
			
			contents = getFileContents("record", //$NON-NLS-1$
					codeTemplateId, new String[] { "${recordName}" //$NON-NLS-1$
					}, new String[] { partName });
		}

		try {
			Document doc = new Document();
			doc.set(contents);
			TextEdit edit = EGLCodeFormatterUtil.format(doc, null);
			edit.apply(doc);
			contents = doc.get();
		} catch (Exception ex) {
			
		}
		
		return contents;
	}
	
	@Override
	protected String getFileHeader(String packName) {
		String header = super.getFileHeader(packName);
		String imports = ((RecordConfiguration)configuration).getImports();
		if (imports == null || imports.length() == 0) {
			return header;
		}
		return header + imports + "\n\n";
	}

}
