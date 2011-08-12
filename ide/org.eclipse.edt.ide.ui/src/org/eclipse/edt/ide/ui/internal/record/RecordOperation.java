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
package org.eclipse.edt.ide.ui.internal.record;

import org.eclipse.edt.ide.ui.wizards.PartOperation;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;

public class RecordOperation extends PartOperation {
	protected String codeTemplateId;
	protected Object contentObj;

	public RecordOperation(RecordConfiguration configuration, String templateID, Object contentsObj) {
		super(configuration);
		this.codeTemplateId = templateID;
		this.contentObj = contentsObj;
	}

	public String getFileContents() throws PartTemplateException {

		if (contentObj != null) {
			return contentObj.toString();
		}

		RecordConfiguration configuration = (RecordConfiguration) super.configuration;
		if (codeTemplateId == null) {
			if (configuration.getRecordType() == RecordConfiguration.BASIC_RECORD) {
				codeTemplateId = "org.eclipse.edt.ide.ui.templates.flexible_basic_record"; //$NON-NLS-1$
			} else if (configuration.getRecordType() == RecordConfiguration.SQL_RECORD) {
				codeTemplateId = "org.eclipse.edt.ide.ui.templates.flexible_SQL_record_with_table_names"; //$NON-NLS-1$
			} else {
				codeTemplateId = "org.eclipse.edt.ide.ui.templates.flexible_basic_record"; //$NON-NLS-1$		
			}
		}

		String partName = configuration.getRecordName();

		return getFileContents("record", //$NON-NLS-1$
				codeTemplateId, new String[] { "${recordName}" //$NON-NLS-1$
				}, new String[] { partName });
	}

}
