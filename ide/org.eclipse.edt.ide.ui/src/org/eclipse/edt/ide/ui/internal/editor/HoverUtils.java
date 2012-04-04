/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.text.Position;

class HoverUtils {
	public static void sort(List exact) {
		class AnnotationComparator implements Comparator {
			public int compare(Object o1, Object o2) {
				if (o1 instanceof IAnnotation && o2 instanceof IAnnotation) {
					IAnnotation a1 = (IAnnotation) o1;
					IAnnotation a2 = (IAnnotation) o2;

					if (!a1.isEGLMarkerAnnotation() && a2.isEGLMarkerAnnotation()) {
						return - 1;
					}
					else if (a1.isEGLMarkerAnnotation() && !a2.isEGLMarkerAnnotation()) {
						return 1;
					}
				}
				return 0;
			}
		}
		Collections.sort(exact, new AnnotationComparator());
	}
	
	public static boolean isDuplicateAnnotation(Map messagesAtPosition, Position position, IAnnotation eglAnnotation){ //Integer problemKind) {
		String txt = eglAnnotation.getText();
		if (messagesAtPosition.containsKey(position)) {
			List annots = (List)messagesAtPosition.get(position);
			Integer problemKind = eglAnnotation.getProblemKind();
			
			for(Iterator it=annots.iterator(); it.hasNext();)
			{
				IAnnotation annot = (IAnnotation)it.next();
				if(annot.getProblemKind().equals(problemKind)) {
					if (txt == null) {
						if (annot.getText() == null) {
							return true;
						}
					}
					else {
						if (txt.equals(annot.getText())) {
							return true;
						}
					}
				}
			}			
			annots.add(eglAnnotation);
		}
		else{
			List list = new ArrayList();
			list.add(eglAnnotation);
			messagesAtPosition.put(position, list);
		}

		return false;
	}
	
	/*
	 * Formats a message as HTML text.
	 */
	public static String formatSingleMessage(String message) {
		StringBuffer buffer= new StringBuffer();
		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent(message));
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}
	
	/*
	 * Formats several message as HTML text.
	 */
	public static String formatMultipleMessages(List messages) {
		StringBuffer buffer= new StringBuffer();
		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent(UINlsStrings.EGLAnnotationHoverMultipleMarkersAtThisLine));
		
		HTMLPrinter.startBulletList(buffer);
		Iterator e= messages.iterator();
		while (e.hasNext())
			HTMLPrinter.addBullet(buffer, HTMLPrinter.convertToHTMLContent((String) e.next()));
		HTMLPrinter.endBulletList(buffer);	
		
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}	
	
}
