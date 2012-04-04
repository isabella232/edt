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

import java.util.Iterator;

public interface IAnnotation {

	/**
	 * @see org.eclipse.jface.text.source.Annotation#isMarkedDeleted()
	 */
	boolean isMarkedDeleted();

	/**
	 * Returns whether this annotation is overlaid.
	 *
	 * @return <code>true</code> if overlaid
	 */
	boolean hasOverlay();

	/**
	 * Returns the overlay of this annotation.
	 *
	 * @return the annotation's overlay
	 */
	IAnnotation getOverlay();

	/**
	 * Returns an iterator for iterating over the
 	 * annotation which are overlaid by this annotation.
	 *
	 * @return an iterator over the overlaid annotations
	 */
	Iterator getOverlaidIterator();

	/**
	 * Adds the given annotation to the list of
	 * annotations which are overlaid by this annotations.
	 *
	 * @param annotation the problem annotation
	 */
	void addOverlaid(IAnnotation annotation);

	/**
	 * Removes the given annotation from the list of
	 * annotations which are overlaid by this annotation.
	 *
	 * @param annotation the problem annotation
	 */
	void removeOverlaid(IAnnotation annotation);
	
	/**
	 * Tells whether this annotation is a problem
	 * annotation.
	 *
	 * @return <code>true</code> if it is a problem annotation
	 */
	boolean isProblem();
	
	/**
	 * @see org.eclipse.jface.text.source.Annotation#getType()
	 */
	String getType();
	
	/**
	 * @see org.eclipse.jface.text.source.Annotation#getText()
	 */
	String getText();
	
	Integer getProblemKind();
	
	boolean isEGLMarkerAnnotation();
	
}
