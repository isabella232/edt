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
package org.eclipse.edt.ide.ui.internal.quickfix;

import org.eclipse.edt.ide.core.model.IEGLModelMarker;
import org.eclipse.edt.ide.core.model.IProblem;
import org.eclipse.edt.ide.ui.editor.IProblemLocation;
import org.eclipse.edt.ide.ui.internal.editor.EGLMarkerAnnotation;
import org.eclipse.edt.ide.ui.internal.editor.IAnnotation;


public class ProblemLocation implements IProblemLocation {

	private final int fId;
	private final String[] fArguments;
	private final int fOffset;
	private final int fLength;
	private final boolean fIsError;
	private final String fMarkerType;

	public ProblemLocation(int offset, int length, IAnnotation annotation) {
		fId= annotation.getProblemKind();
		String[] arguments= null;
		fArguments= arguments != null ? arguments : new String[0];
		fOffset= offset;
		fLength= length;
		fIsError= EGLMarkerAnnotation.ERROR_ANNOTATION_TYPE.equals(annotation.getType());
		String markerType= annotation.getType();
		fMarkerType= markerType != null ? markerType : IEGLModelMarker.TRANSIENT_PROBLEM;
	}

	public ProblemLocation(int offset, int length, int id, String[] arguments, boolean isError, String markerType) {
		fId= id;
		fArguments= arguments;
		fOffset= offset;
		fLength= length;
		fIsError= isError;
		fMarkerType= markerType;
	}

	public ProblemLocation(IProblem problem) {
		fId= problem.getID();
		fArguments= problem.getArguments();
		fOffset= problem.getSourceStart();
		fLength= problem.getSourceEnd() - fOffset + 1;
		fIsError= problem.isError();
		fMarkerType = "";
	}


	public int getProblemId() {
		return fId;
	}

	public String[] getProblemArguments() {
		return fArguments;
	}

	public int getLength() {
		return fLength;
	}

	public int getOffset() {
		return fOffset;
	}

	public boolean isError() {
		return fIsError;
	}

	@Override
	public String getMarkerType() {
		return fMarkerType;
	}

}
