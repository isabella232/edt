/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;

/** 
 * The AnnotationHover provides the hover support for egl editors.
 */
 
public class AnnotationHover implements IAnnotationHover {

	/**
	 * Returns the distance to the ruler line. 
	 */
	protected int compareRulerLine(Position position, IDocument document, int line) {
		
		if (position.getOffset() > -1 && position.getLength() > -1) {
			try {
				int javaAnnotationLine= document.getLineOfOffset(position.getOffset());
				if (line == javaAnnotationLine)
					return 1;
				if (javaAnnotationLine <= line && line <= document.getLineOfOffset(position.getOffset() + position.getLength()))
					return 2;
			} catch (BadLocationException x) {
			}
		}
		
		return 0;
	}
	
	/**
	 * Selects a set of markers from the two lists. By default, it just returns
	 * the set of exact matches.
	 */
	protected List select(List exactMatch, List including) {
		return exactMatch;
	}
	
	
	/**
	 * Returns one marker which includes the ruler's line of activity.
	 */
	protected List getEGLAnnotationsForLine(ISourceViewer viewer, int line) {
		
		IDocument document = viewer.getDocument();
		IAnnotationModel model = viewer.getAnnotationModel();
		
		if (model == null)
			return null;
			
		List exact = new ArrayList();
		List including = new ArrayList();
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
				if (position == null)
					continue;

				if (HoverUtils.isDuplicateAnnotation(messagesAtPosition, position, a))
					continue;

				switch (compareRulerLine(position, document, line)) {
					case 1:
						exact.add(a);
						break;
					case 2:
						including.add(a);
						break;
				}
			}
		}
		return select(exact, including);
	}

	/*
	 * @see IVerticalRulerHover#getHoverInfo(ISourceViewer, int)
	 */
	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
		List eglAnnotations = getEGLAnnotationsForLine(sourceViewer, lineNumber);
		if (eglAnnotations != null) {
			
			if (eglAnnotations.size() == 1) {
				
				// optimization
				IAnnotation eglAnnotation= (IAnnotation) eglAnnotations.get(0);
				String message= eglAnnotation.getText();
				if (message != null && message.trim().length() > 0)
					return HoverUtils.formatSingleMessage(message);
					
			} else {
					
				List messages = new ArrayList();
				
				Iterator e = eglAnnotations.iterator();
				while (e.hasNext()) {
					IAnnotation eglAnnotation = (IAnnotation) e.next();
					String message = eglAnnotation.getText();
					if (message != null && message.trim().length() > 0)
						messages.add(message.trim());
				}
				
				if (messages.size() == 1)
					return HoverUtils.formatSingleMessage((String) messages.get(0));
					
				if (messages.size() > 1)
					return HoverUtils.formatMultipleMessages(messages);
			}
		}		
		return null;
	}
	
}
