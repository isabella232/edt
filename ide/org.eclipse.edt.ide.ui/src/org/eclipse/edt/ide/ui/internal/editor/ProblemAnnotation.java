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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.jface.text.source.ImageUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.AnnotationPreferenceLookup;

public class ProblemAnnotation extends Annotation implements IAnnotationPresentation, IAnnotation {
	
	private List fOverlaids;
	private ReportedProblem fProblem;
	private Image fImage;
	private boolean fImagesInitialized = false;
	private int fLayer= IAnnotationAccessExtension.DEFAULT_LAYER;
	
	private static final int WARNING_LAYER;
	private static final int ERROR_LAYER;

	static {
		AnnotationPreferenceLookup lookup = EditorsUI.getAnnotationPreferenceLookup();
		WARNING_LAYER = computeLayer(EGLMarkerAnnotation.WARNING_ANNOTATION_TYPE, lookup); 
		ERROR_LAYER = computeLayer(EGLMarkerAnnotation.ERROR_ANNOTATION_TYPE, lookup); 
	}

	private static int computeLayer(String annotationType, AnnotationPreferenceLookup lookup) {
		Annotation annotation= new Annotation(annotationType, false, null);
		AnnotationPreference preference= lookup.getAnnotationPreference(annotation);
		if (preference != null)
			return preference.getPresentationLayer() + 1;
		else
			return IAnnotationAccessExtension.DEFAULT_LAYER + 1;
	}
	
	public ProblemAnnotation(ReportedProblem problem) {
		fProblem = problem;
		if (fProblem.getSeverity() == IMarker.SEVERITY_ERROR) {
			setType(EGLMarkerAnnotation.ERROR_ANNOTATION_TYPE);
			fLayer = ERROR_LAYER;
		}
		else if (fProblem.getSeverity() == IMarker.SEVERITY_WARNING) {
			setType(EGLMarkerAnnotation.WARNING_ANNOTATION_TYPE);
			fLayer = WARNING_LAYER;
		}	
	}

	public boolean hasOverlay() {
		return false;
	}

	public IAnnotation getOverlay() {
		return null;
	}

	public void addOverlaid(IAnnotation annotation) {
		if (fOverlaids == null)
			fOverlaids = new ArrayList(1);
		fOverlaids.add(annotation);
	}

	public void removeOverlaid(IAnnotation annotation) {
		if (fOverlaids != null) {
			fOverlaids.remove(annotation);
			if (fOverlaids.size() == 0)
				fOverlaids = null;
		}
	}

	public Iterator getOverlaidIterator() {
		if (fOverlaids != null)
			return fOverlaids.iterator();
		return null;
	}
	
	public String getText() {
		return fProblem.getMessage();
	}

	
	public boolean isProblem() {
		return fProblem.getSeverity() == IMarker.SEVERITY_ERROR || fProblem.getSeverity() == IMarker.SEVERITY_WARNING;
	}
	
	public int getLayer() {
		return fLayer;
	}
	
	public Integer getProblemKind() {
		return Integer.valueOf(fProblem.getProblemKind());
	}
	
	public boolean isEGLMarkerAnnotation() {
		return false;
	}

	public void paint(GC gc, Canvas canvas, Rectangle bounds) {
		initializeImages();
		if (fImage != null)
			ImageUtilities.drawImage(fImage, gc, canvas, bounds, SWT.CENTER, SWT.TOP);
	}
	
	private void initializeImages() {	
		if (!fImagesInitialized) {
			String name = null;
			if (isProblem()) {
				switch (fProblem.getSeverity()) {
					case IMarker.SEVERITY_WARNING:
						name = ISharedImages.IMG_OBJS_WARN_TSK;
						break;
					case IMarker.SEVERITY_ERROR:
						name = ISharedImages.IMG_OBJS_ERROR_TSK;
						break;
				}
				fImagesInitialized = true;
				if (name != null) {
					fImage = PlatformUI.getWorkbench().getSharedImages().getImage(name);
				}
			}
			fImagesInitialized = true;
		}
	}

}
