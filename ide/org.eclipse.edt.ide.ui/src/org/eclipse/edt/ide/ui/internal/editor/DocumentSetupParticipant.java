/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;

public class DocumentSetupParticipant implements IDocumentSetupParticipant {

	public DocumentSetupParticipant() {
	}
	
	public void setup(IDocument document) {
		DocumentProvider egldocProvider = EDTUIPlugin.getDefault().getEGLDocumentProvider();
		IDocumentPartitioner partitioner = egldocProvider.createDocumentPartitioner();
		
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension3= (IDocumentExtension3) document;
			extension3.setDocumentPartitioner(IPartitions.EGL_PARTITIONING, partitioner);
			partitioner.connect(document);
		} 		
		else
			document.setDocumentPartitioner(partitioner);
	}		
}
