/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.builder;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;


/**
 * @author Dave Murray
 */
public abstract class DefaultProblemRequestor implements IProblemRequestor {
	
	public static final String EGL_VALIDATION_RESOURCE_BUNDLE_NAME = "org.eclipse.edt.compiler.internal.core.builder.EGLValidationResources"; //$NON-NLS-1$
	public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(EGL_VALIDATION_RESOURCE_BUNDLE_NAME, Locale.getDefault());
	boolean hasError;
	
	@Override
	public abstract void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle);
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) {
		acceptProblem(startOffset, endOffset, severity, problemKind, inserts, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Node astNode, int problemKind) {
		acceptProblem(astNode.getOffset(), astNode.getOffset() + astNode.getLength(), IMarker.SEVERITY_ERROR, problemKind, new String[0], RESOURCE_BUNDLE);
	}
	
	@Override
	public boolean shouldReportProblem(int problemKind) {
		return true;
	}
	
	@Override
	public void acceptProblem(Node astNode, int problemKind, int severity) {
		acceptProblem(astNode.getOffset(), astNode.getOffset() + astNode.getLength(), severity, problemKind, new String[0], RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Node astNode, int problemKind, String[] inserts) {
		acceptProblem(astNode.getOffset(), astNode.getOffset() + astNode.getLength(), IMarker.SEVERITY_ERROR, problemKind, inserts, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Node astNode, int problemKind, int severity, String[] inserts) {
		acceptProblem(astNode.getOffset(), astNode.getOffset() + astNode.getLength(), severity, problemKind, inserts, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Node astNode, int problemKind, int severity, String[] inserts, ResourceBundle bundle) {
		acceptProblem(astNode.getOffset(), astNode.getOffset() + astNode.getLength(), severity, problemKind, inserts, bundle);
	}
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int problemKind, boolean isError, String[] inserts) {
		acceptProblem(startOffset, endOffset, problemKind, isError, inserts, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int problemKind, boolean isError, String[] inserts, ResourceBundle bundle) {
		if (isError) {
			acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR, problemKind, inserts, bundle);
		}
		else {
			acceptProblem(startOffset, endOffset, IMarker.SEVERITY_WARNING, problemKind, inserts, bundle);
		}
	}
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int problemKind, String[] inserts) {
		acceptProblem(startOffset, endOffset, problemKind, true, inserts);		
	}
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind) {
		acceptProblem(startOffset, endOffset, severity, problemKind, new String[0], RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Element element, int problemKind) {
		acceptProblem(element, problemKind, IMarker.SEVERITY_ERROR, null, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Element element, int problemKind, int severity) {
		acceptProblem(element, problemKind, severity, null, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Element element, int problemKind, int severity, String[] inserts) {
		acceptProblem(element, problemKind, severity, inserts, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Element element, int problemKind, int severity, String[] inserts, ResourceBundle bundle) {
		int startOffset = 0;
		int endOffset = 0;
		Annotation annot = element.getAnnotation(IEGLConstants.EGL_LOCATION);
		if (annot != null) {
			Object val = annot.getValue(IEGLConstants.EGL_PARTOFFSET);
			if (val instanceof Integer) {
				startOffset = ((Integer)val).intValue();
				endOffset = startOffset;
				
				val = annot.getValue(IEGLConstants.EGL_PARTLENGTH);
				if (val instanceof Integer) {
					endOffset += ((Integer)val).intValue();
				}
			}
		}
		acceptProblem(startOffset, endOffset, severity, problemKind, inserts, bundle);
	}
	
	public static String getMessageFromBundle(int problemKind, String[] inserts) {
		return getMessageFromBundle(problemKind, inserts, RESOURCE_BUNDLE);
	}
	
	public static String getMessageFromBundle(int problemKind, String[] inserts, ResourceBundle bundle) {
		String message = bundle.getString(Integer.toString(problemKind));
		if (message == null || inserts == null || inserts.length == 0) {
			return message;
		}
		MessageFormat formatter = new MessageFormat(message);
		formatter.applyPattern(message);
		return formatter.format(insertsWithoutNulls(inserts));
	}
	
	private static Object[] insertsWithoutNulls(Object[] originalInserts) {
		int numberInserts = originalInserts.length;
		Object[] newInserts = new Object[numberInserts];
		for (int i = 0; i < numberInserts; i++) {
			if (originalInserts[i] != null)
				newInserts[i] = originalInserts[i];
			else
				newInserts[i] = ""; //$NON-NLS-1$
		}
		return newInserts;
	}
	
	/**
	 * No longer does anything.
	 * @deprecated
	 */
	public static String[] shiftInsertsIfNeccesary(int problemKind, String[] inserts) {
		return inserts;
	}
	
	public boolean hasError() {
		return hasError;
	}
	
	public void setHasError(boolean err) {
		hasError = err;
	}
}
