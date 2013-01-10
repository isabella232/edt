/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * Example implementation for an <code>ITextHover</code> which hovers over Java code.
 */
public class EGLTextHover extends AbstractEGLTextHover {
	
	protected List getEGLAnnotationsForPosition(ISourceViewer viewer, int pos) {
		IAnnotationModel model = viewer.getAnnotationModel();
		
		if (model == null)
			return null;
			
		List exact = new ArrayList();
		List eglAnnotations = new ArrayList();
		Iterator e = model.getAnnotationIterator();
		while (e.hasNext()) {
			eglAnnotations.add(e.next());
		}
		HoverUtils.sort(eglAnnotations);
		HashMap messagesAtPosition = new HashMap();
		for (Iterator iterator = eglAnnotations.iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			if (o instanceof IAnnotation ) {
				IAnnotation a = (IAnnotation) o;
				Position position = model.getPosition((Annotation) a);
				if (position == null || a.getText() == null) {
					continue;
				}
				
				if (HoverUtils.isDuplicateAnnotation(messagesAtPosition, position, a)) {
					continue;
				}
				
				if(pos >= position.offset && pos <= position.offset + position.length) {
					exact.add(a);
				}
			}
		}
		return exact;
	}

	/* (non-Javadoc)
	 * Method declared on ITextHover
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if (hoverRegion != null) {
			try {
				if (hoverRegion.getLength() > -1) {
					List annotationsForPosition = getEGLAnnotationsForPosition((ISourceViewer) textViewer, hoverRegion.getOffset());
					if(annotationsForPosition != null && !annotationsForPosition.isEmpty()) {
						if(annotationsForPosition.size() == 1){
							return HoverUtils.formatSingleMessage(((IAnnotation) annotationsForPosition.get(0)).getText());
						}
						else{
							List messages = new ArrayList();
							
							Iterator e = annotationsForPosition.iterator();
							while (e.hasNext()) {
								IAnnotation eglAnnotation = (IAnnotation) e.next();
								String message = eglAnnotation.getText();
								if (message != null && message.trim().length() > 0)
									messages.add(message.trim());
							}
							return HoverUtils.formatMultipleMessages(messages);
						}
						
					}
					
					IDocument document = textViewer.getDocument();
					if(document != null) {
						return document.get(hoverRegion.getOffset(), hoverRegion.getLength());
					}
				}
			} catch (BadLocationException x) {
			}
		}
		return "empty selection"; //$NON-NLS-1$
	}
}
