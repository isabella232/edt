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
package org.eclipse.edt.ide.ui.internal.editor.sql;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.ide.ui.internal.results.views.AbstractResultsListViewer;
import org.eclipse.edt.ide.ui.internal.results.views.AbstractResultsViewPart;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.widgets.Composite;

public class SQLResultsListViewer extends AbstractResultsListViewer {
	
	public class EGLSQLResultsContentProvider implements IStructuredContentProvider {
		public EGLSQLResultsContentProvider() {
			super();
		}
		public void dispose() {
		}
		public java.lang.Object[] getElements(java.lang.Object inputElement) {
			if (inputElement instanceof ArrayList)
				return ((ArrayList) inputElement).toArray();
			return null;
		}
		public void inputChanged(org.eclipse.jface.viewers.Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	public class EGLSQLResultsLabelProvider implements org.eclipse.jface.viewers.ILabelProvider {
		public EGLSQLResultsLabelProvider() {
			super();
		}
		
		public void addListener(org.eclipse.jface.viewers.ILabelProviderListener listener) {
		}
		
		public void dispose() {
		}
		
		public org.eclipse.swt.graphics.Image getImage(Object element) {
			return null;
		}
		
		public String getText(Object element) {
			if (element instanceof String)
				return (String) element;
			else if (element instanceof IMarker)
				return ((IMarker) element).getAttribute(IMarker.MESSAGE, ""); //$NON-NLS-1$
			else if (element instanceof EGLMessage)
				return ((EGLMessage) element).getBuiltMessage();
			else if (element instanceof Problem) {
				return getBuiltMessage((Problem) element);
			}
			else
				return ""; //$NON-NLS-1$
		}
		
		public String getBuiltMessage(Problem problem) {
			return "IWN.VAL." + problem.getProblemKind() + ".e " + DefaultProblemRequestor.getMessageFromBundle(problem.getProblemKind(), problem.getInserts(), problem.getResourceBundle());	//$NON-NLS-1$ //$NON-NLS-2$		
		}
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}
		public void removeListener(org.eclipse.jface.viewers.ILabelProviderListener listener) {
		}
	}
	/**
	 * EGLSyntaxCheckResultViewer constructor comment.
	 * @param parent org.eclipse.swt.widgets.Composite
	 */
	public SQLResultsListViewer(Composite parent, AbstractResultsViewPart viewPart) {

		super(parent, viewPart);

		setContentProvider(new EGLSQLResultsContentProvider());
		setLabelProvider(new EGLSQLResultsLabelProvider());
	}
}
