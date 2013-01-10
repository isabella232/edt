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
package org.eclipse.edt.ide.ui.internal.quickfix;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.model.IEGLModelMarker;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.editor.EGLMarkerAnnotation;

public final class ContributedProcessorDescriptor {

	private final IConfigurationElement fConfigurationElement;
	private Object fProcessorInstance;
	private final Set fHandledMarkerTypes;

	private static final String ID= "id"; //$NON-NLS-1$
	private static final String CLASS= "class"; //$NON-NLS-1$

	private static final String HANDLED_MARKER_TYPES= "handledMarkerTypes"; //$NON-NLS-1$
	private static final String MARKER_TYPE= "markerType"; //$NON-NLS-1$

	public ContributedProcessorDescriptor(IConfigurationElement element, boolean testMarkerTypes) {
		fConfigurationElement= element;
		fProcessorInstance= null;
		fHandledMarkerTypes= testMarkerTypes ? getHandledMarkerTypes(element) : null;
	}

	private Set getHandledMarkerTypes(IConfigurationElement element) {
		HashSet map= new HashSet(7);
		IConfigurationElement[] children= element.getChildren(HANDLED_MARKER_TYPES);
		for (int i= 0; i < children.length; i++) {
			IConfigurationElement[] types= children[i].getChildren(MARKER_TYPE);
			for (int k= 0; k < types.length; k++) {
				String attribute= types[k].getAttribute(ID);
				if (attribute != null) {
					map.add(attribute);
				}
			}
		}
		if (map.isEmpty()) {
			map.add(IEGLModelMarker.BUILDPATH_PROBLEM_MARKER);
			map.add(IEGLModelMarker.TASK_MARKER);
			map.add(EGLMarkerAnnotation.ERROR_ANNOTATION_TYPE);
		}
		return map;
	}

	public IStatus checkSyntax() {
		IConfigurationElement[] children= fConfigurationElement.getChildren(ExpressionTagNames.ENABLEMENT);
		if (children.length > 1) {
			String id= fConfigurationElement.getAttribute(ID);
			return new StatusInfo(IStatus.ERROR, "Only one < enablement > element allowed. Disabling " + id); //$NON-NLS-1$
		}
		return new StatusInfo(IStatus.OK, "Syntactically correct quick assist/fix processor"); //$NON-NLS-1$
	}

	public Object getProcessor(Class expectedType) {
		if (fProcessorInstance == null) {
			try {
				Object extension= fConfigurationElement.createExecutableExtension(CLASS);
				if (expectedType.isInstance(extension)) {
					fProcessorInstance= extension;
				} else {
					String message= "Invalid extension to " + fConfigurationElement.getName() //$NON-NLS-1$
					+ ". Must extends '" + expectedType.getName() + "'." + fConfigurationElement.getContributor().getName(); //$NON-NLS-1$ //$NON-NLS-2$
					EDTUIPlugin.log(new Status(IStatus.ERROR, EDTUIPlugin.PLUGIN_ID, message));
					return null;
				}
			} catch (CoreException e) {
				EDTUIPlugin.log(e);
				return null;
			}
		}
		return fProcessorInstance;
	}

	public boolean canHandleMarkerType(String markerType) {
		return fHandledMarkerTypes == null || fHandledMarkerTypes.contains(markerType);
	}

}
