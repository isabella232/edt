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
package org.eclipse.edt.ide.ui.internal.handlers.wizards;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.editor.EGLCodeFormatterUtil;
import org.eclipse.edt.ide.ui.internal.record.RecordConfiguration;
import org.eclipse.edt.ide.ui.wizards.PartOperation;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

public class HandlerOperation extends PartOperation {	
	protected String codeTemplateId;
	protected Object contentObj;

	public HandlerOperation(HandlerConfiguration configuration, String templateID, Object contentsObj) {
		super(configuration);
		this.codeTemplateId = templateID;
		this.contentObj = contentsObj;
	}
	
	public HandlerOperation(HandlerConfiguration configuration, String templateID, Object contentsObj, ISchedulingRule rule) {
		super(configuration, rule);
		this.codeTemplateId = templateID;
		this.contentObj = contentsObj;
	}
	
	public String getFileContents() throws PartTemplateException {
		String contents = null;		
		

		if (contentObj != null) {
			contents = contentObj.toString();
		} else {
			HandlerConfiguration configuration = (HandlerConfiguration) super.configuration;
			if (codeTemplateId == null) {
				if (configuration.getHandlerType() == RecordConfiguration.BASIC_RECORD) {
					codeTemplateId = "org.eclipse.edt.ide.ui.templates.basic_handler_part"; //$NON-NLS-1$
				}
			}
	
			String partName = configuration.getHandlerName();
			
			contents = getFileContents("handler", //$NON-NLS-1$
					codeTemplateId, new String[] { "${handlerName}" //$NON-NLS-1$
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

}
