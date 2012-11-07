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

import org.eclipse.edt.ide.ui.internal.record.NewRecordWizard;
import org.eclipse.edt.ide.ui.internal.record.RecordConfiguration;
import org.eclipse.edt.ide.ui.internal.record.conversion.xml.PartsFromXMLUtil;
import org.eclipse.edt.ide.ui.templates.parts.Part;
import org.eclipse.edt.ide.ui.templates.parts.Record;
import org.eclipse.ui.IWorkbenchWizard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RecordFromXMLWizard extends AbstractRecordFromInputWizard implements IWorkbenchWizard {
	public RecordFromXMLWizard() {
		super();
	}
	
	protected AbstractRecordFromStringInputPage createInputPage() {
		return new RecordFromXMLPage(selection);
	}

	protected boolean processInput(Object input) {
		setParts(null);
		setMessages(null);

		try {
			//Make DocumentBuilder.parse ignore DTD references since we do not need them
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			
			java.io.StringReader reader = new java.io.StringReader(input.toString().trim());
			Document dom = builder.parse(new org.xml.sax.InputSource(reader));
			Element doc = dom.getDocumentElement();

			setParts(createParts(doc));
			((RecordConfiguration)((NewRecordWizard)getParentWizard()).getConfiguration()).setImports("import eglx.xml.binding.annotation.*;");
		} catch (Throwable ex) {
			addMessage(ex.getMessage());
		}

		return (parts != null && parts.length > 0);
	}

	private Part[] createParts(Node node) {
		Record rec = new Record();
		rec.setName(getFileName());
		return new PartsFromXMLUtil(this).process(node, rec);
	}

	private String getFileName() {
		AbstractRecordFromInputWizard wiz = this;
		NewRecordWizard parentWiz = (NewRecordWizard) wiz.getParentWizard();
		return ((RecordConfiguration) parentWiz.getConfiguration()).getRecordName();
	}

}
