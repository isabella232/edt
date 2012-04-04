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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

public class EGLMarkerAnnotationModel extends ResourceMarkerAnnotationModel {

	/** Preference key for dynamic problems */
	private final static String HANDLE_DYNAMIC_PROBLEMS = EDTUIPreferenceConstants.EDITOR_HANDLE_DYNAMIC_PROBLEMS;
	
	private ReverseMap fReverseMap = new ReverseMap();
	private List fGeneratedAnnotations;
	private List fPreviouslyOverlaid = null;
	private List fCurrentlyOverlaid = new ArrayList();
	private boolean fIsHandlingDynamicProblems;
	

	public EGLMarkerAnnotationModel(IResource resource) {
		super(resource);
	}

	/**
	 * @see AbstractMarkerAnnotationModel#createMarkerAnnotation(IMarker)
	 */
	protected MarkerAnnotation createMarkerAnnotation(IMarker marker) {
		return new EGLMarkerAnnotation(marker);
	}
	
	// provide a public way to get to the underlying resource
	public IResource getMarkerResource() {
		return getResource();
	}	
	
	/**
	 * Returns the preference whether handling temporary problems is enabled.
	 */
	protected boolean shouldEnableDynamicProblems() {
		IPreferenceStore store = EDTUIPlugin.getDefault().getPreferenceStore();
		return store.getBoolean(HANDLE_DYNAMIC_PROBLEMS);
	}
	
	public void setIsHandlingDynamicProblems(boolean enable) {
		if (fIsHandlingDynamicProblems != enable) {
			fIsHandlingDynamicProblems = enable;
			if (fIsHandlingDynamicProblems)
				startCollectingProblems();
			else
				stopCollectingProblems();
		}
	}
	
	/**
	 * Tells this annotation model to collect temporary problems from now on.
	 */
	private void startCollectingProblems() {
		fGeneratedAnnotations = new ArrayList();
	}

	/**
	 * Tells this annotation model to no longer collect temporary problems.
	 */
	private void stopCollectingProblems() {
		if (fGeneratedAnnotations != null)
			removeAnnotations(fGeneratedAnnotations, true, true);
		fGeneratedAnnotations = null;
	}
	
	public void reportProblems(List reportedProblems) {
		boolean temporaryProblemsChanged = false;
		synchronized (getLockObject()) {
			fPreviouslyOverlaid = fCurrentlyOverlaid;
			fCurrentlyOverlaid = new ArrayList();

			if (fGeneratedAnnotations.size() > 0) {
				temporaryProblemsChanged = true;
				removeAnnotations(fGeneratedAnnotations, false, true);
				fGeneratedAnnotations.clear();
			}

			if (reportedProblems != null && reportedProblems.size() > 0) {
				Iterator e = reportedProblems.iterator();
				while (e.hasNext()) {
					ReportedProblem problem = (ReportedProblem) e.next();
					Position position = createPositionFromProblem(problem);
					if (position != null) {
						try {
							ProblemAnnotation annotation = new ProblemAnnotation(problem);
							overlayMarkers(position, annotation);
							addAnnotation(annotation, position, false);
							fGeneratedAnnotations.add(annotation);

							temporaryProblemsChanged = true;
						} catch (BadLocationException x) {
							// ignore invalid position
						}
					}
				}
			}
			removeMarkerOverlays(); 
			fPreviouslyOverlaid = null;
		}
		
		if (temporaryProblemsChanged)
			fireModelChanged();
	}
	
	private Position createPositionFromProblem(ReportedProblem problem) {
		int start = problem.getStartOffset();
		int end = problem.getEndOffset();

		if (start > end) {
			end = start + end;
			start = end - start;
			end = end - start;
		}

		if (start == -1 && end == -1) {
			int line = problem.getLineNumber();
			if (line > 0 && fDocument != null) {
				try {
					start = fDocument.getLineOffset(line - 1);
					end = start;
				} catch (BadLocationException x) {}
			}
		}

		if (start > -1 && end > -1)
			return new Position(start, end - start);

		return null;
	}

	private void removeMarkerOverlays() { //(boolean isCanceled) {
		if (fPreviouslyOverlaid != null) {
			Iterator e = fPreviouslyOverlaid.iterator();
			while (e.hasNext()) {
				EGLMarkerAnnotation annotation = (EGLMarkerAnnotation) e.next();
				annotation.setOverlay(null);
			}
		}
	}

	/**
	 * Overlays value with problem annotation.
	 * @param problemAnnotation
	 */
	private void setOverlay(Object value, ProblemAnnotation problemAnnotation) {
		if (value instanceof EGLMarkerAnnotation) {
			EGLMarkerAnnotation annotation = (EGLMarkerAnnotation) value;
			if (annotation.isProblem()) {
				annotation.setOverlay(problemAnnotation);
				fPreviouslyOverlaid.remove(annotation);
				fCurrentlyOverlaid.add(annotation);
			}
		} 
	}

	private void  overlayMarkers(Position position, ProblemAnnotation problemAnnotation) {
		Object value = getAnnotations(position);
		if (value instanceof List) {
			List list = (List) value;
			for (Iterator e = list.iterator(); e.hasNext();)
				setOverlay(e.next(), problemAnnotation);
		} else {
			setOverlay(value, problemAnnotation);
		}
	}
	
	private Object getAnnotations(Position position) {
		synchronized (getLockObject()) {
			return fReverseMap.get(position);
		}
	}

	/*
	 * @see AnnotationModel#addAnnotation(Annotation, Position, boolean)
	 */
	protected void addAnnotation(Annotation annotation, Position position, boolean fireModelChanged) throws BadLocationException {
		super.addAnnotation(annotation, position, fireModelChanged);

		synchronized (getLockObject()) {
			Object cached = fReverseMap.get(position);
			if (cached == null)
				fReverseMap.put(position, annotation);
			else if (cached instanceof List) {
				List list = (List) cached;
				list.add(annotation);
			} else if (cached instanceof Annotation) {
				List list = new ArrayList(2);
				list.add(cached);
				list.add(annotation);
				fReverseMap.put(position, list);
			}
		}
	}

	/*
	 * @see AnnotationModel#removeAllAnnotations(boolean)
	 */
	protected void removeAllAnnotations(boolean fireModelChanged) {
		super.removeAllAnnotations(fireModelChanged);
		synchronized (getLockObject()) {
			fReverseMap.clear();
		}
	}

	/*
	 * @see AnnotationModel#removeAnnotation(Annotation, boolean)
	 */
	protected void removeAnnotation(Annotation annotation, boolean fireModelChanged) {
		Position position = getPosition(annotation);
		synchronized (getLockObject()) {
			Object cached = fReverseMap.get(position);
			if (cached instanceof List) {
				List list = (List) cached;
				list.remove(annotation);
				if (list.size() == 1) {
					fReverseMap.put(position, list.get(0));
					list.clear();
				}
			} else if (cached instanceof Annotation) {
				fReverseMap.remove(position);
			}
		}
		super.removeAnnotation(annotation, fireModelChanged);
	}
		
	/**
	 * Internal structure for mapping positions to some value.
	 * The reason for this specific structure is that positions can
	 * change over time. Thus a lookup is based on value and not
	 * on hash value.
	 */
	protected static class ReverseMap {

		static class Entry {
			Position fPosition;
			Object fValue;
		}

		private List fList = new ArrayList(2);
		private int fAnchor = 0;

		public Object get(Position position) {
			Entry entry;

			// behind anchor
			int length = fList.size();
			for (int i = fAnchor; i < length; i++) {
				entry = (Entry) fList.get(i);
				if (entry.fPosition.equals(position)) {
					fAnchor = i;
					return entry.fValue;
				}
			}

			// before anchor
			for (int i= 0; i < fAnchor; i++) {
				entry = (Entry) fList.get(i);
				if (entry.fPosition.equals(position)) {
					fAnchor = i;
					return entry.fValue;
				}
			}
			return null;
		}

		private int getIndex(Position position) {
			Entry entry;
			int length = fList.size();
			for (int i= 0; i < length; i++) {
				entry = (Entry) fList.get(i);
				if (entry.fPosition.equals(position))
					return i;
			}
			return -1;
		}

		public void put(Position position,  Object value) {
			int index = getIndex(position);
			if (index == -1) {
				Entry entry = new Entry();
				entry.fPosition = position;
				entry.fValue = value;
				fList.add(entry);
			} else {
				Entry entry = (Entry) fList.get(index);
				entry.fValue = value;
			}
		}

		public void remove(Position position) {
			int index = getIndex(position);
			if (index > -1)
				fList.remove(index);
		}

		public void clear() {
			fList.clear();
		}
	}

	public boolean isHandlingDynamicProblems() {
		return fIsHandlingDynamicProblems;
	}
}

