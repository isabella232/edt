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

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.internal.builder.AbstractMarkerProblemRequestor;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.MarkerUtilities;

//import org.eclipse.edt.ide.core.internal.builder.AbstractMarkerProblemRequestor;

public class EGLMarkerAnnotation extends MarkerAnnotation implements IAnnotation {
	
	public static final String ERROR_ANNOTATION_TYPE = "org.eclipse.ui.workbench.texteditor.error"; 	//$NON-NLS-1$
	public static final String WARNING_ANNOTATION_TYPE = "org.eclipse.ui.workbench.texteditor.warning"; //$NON-NLS-1$
	
	private IAnnotation fOverlay;
	
	/**
	 * Constructor for SampleMarkerAnnotation
	 */
	public EGLMarkerAnnotation(IMarker marker) {
		super(marker);
	}

	public Integer getProblemKind()	{
		IMarker marker = getMarker();
		try {
			if (marker.getAttribute(AbstractMarkerProblemRequestor.EGL_PROBLEM) != null) {
				return ((Integer) marker.getAttribute(AbstractMarkerProblemRequestor.EGL_PROBLEM));
			}
		} catch (CoreException e) {}
		return Integer.valueOf(0);
	}
	
	/**
	 * Overlays this annotation with the given eglAnnotation.
	 *
	 * @param eglAnnotation annotation that is overlaid by this annotation
	 */
	public void setOverlay(IAnnotation eglAnnotation) {
		if (fOverlay != null)
			fOverlay.removeOverlaid(this);

		fOverlay = eglAnnotation;
		if (!isMarkedDeleted())
			markDeleted(fOverlay != null);

		if (fOverlay != null)
			fOverlay.addOverlaid(this);
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.editor.IAnnotation#addOverlaid(org.eclipse.edt.ide.ui.internal.editor.IAnnotation)
	 */
	public void addOverlaid(IAnnotation annotation) {
		// not supported
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.editor.IAnnotation#getOverlaidIterator()
	 */
	public Iterator getOverlaidIterator() {
		// not supported
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.editor.IAnnotation#getOverlay()
	 */
	public IAnnotation getOverlay() {
		return fOverlay;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.editor.IAnnotation#hasOverlay()
	 */
	public boolean hasOverlay() {
		return fOverlay != null;	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.editor.IAnnotation#removeOverlaid(org.eclipse.edt.ide.ui.internal.editor.IAnnotation)
	 */
	public void removeOverlaid(IAnnotation annotation) {
		// not supported
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.editor.IAnnotation#isProblem()
	 */
	public boolean isProblem() {
		IMarker marker = getMarker();
		if (MarkerUtilities.isMarkerType(marker, IMarker.PROBLEM)) {
			switch (MarkerUtilities.getSeverity(marker)) {
				case IMarker.SEVERITY_WARNING:
				case IMarker.SEVERITY_ERROR:
					return true;
				default:
					return false;	
			}	
		}
		return false;
	}

	public boolean isEGLMarkerAnnotation() {
		return true;
	}
	
}
