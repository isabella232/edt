/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.ide.ui.editor.EGLContentAssistInvocationContext;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;


public final class EGLCompletionProposalCategory {

	private static final String ICON = "icon"; //$NON-NLS-1$
	private final String fId;
	private final String fName;
	private final IConfigurationElement fElement;
	private final ImageDescriptor fImage;

	private boolean fIsSeparateCommand = true;
	private boolean fIsEnabled = true;
	private boolean fIsIncluded = true;
	private final EGLCompletionProposalComputerRegistry fRegistry;

	private int fSortOrder = 0xffff - 1;
	private String fLastError = null;

	EGLCompletionProposalCategory(IConfigurationElement element, EGLCompletionProposalComputerRegistry registry) throws CoreException {
		fElement = element;
		fRegistry = registry;
		IExtension parent = (IExtension) element.getParent();
		fId = parent.getUniqueIdentifier();
		checkNotNull(fId, "id"); //$NON-NLS-1$
		String name = parent.getLabel();
		if (name == null){
			fName = fId;
		}
		else{
			fName = name;
		}

		String icon = element.getAttribute(ICON);
		ImageDescriptor img = null;
		if (icon != null) {
			Bundle bundle = getBundle();
			if (bundle != null) {
				Path path = new Path(icon);
				URL url = FileLocator.find(bundle, path, null);
				img = ImageDescriptor.createFromURL(url);
			}
		}
		fImage = img;

	}

	EGLCompletionProposalCategory(String id, String name, EGLCompletionProposalComputerRegistry registry) {
		fRegistry = registry;
		fId = id;
		fName = name;
		fElement = null;
		fImage = null;
	}

	private Bundle getBundle() {
		String namespace = fElement.getDeclaringExtension().getContributor().getName();
		Bundle bundle = Platform.getBundle(namespace);
		return bundle;
	}

	private void checkNotNull(Object value, String attribute) throws CoreException {
		if (value == null) {
			return;
		}
	}

	public String getId() {
		return fId;
	}

	public String getName() {
		return fName;
	}

	public String getDisplayName() {
		return LegacyActionTools.removeMnemonics(fName);
	}

	public ImageDescriptor getImageDescriptor() {
		return fImage;
	}

	public void setSeparateCommand(boolean enabled) {
		fIsSeparateCommand = enabled;
	}

	public boolean isSeparateCommand() {
		return fIsSeparateCommand;
	}

	public void setIncluded(boolean included) {
		fIsIncluded = included;
	}

	public boolean isIncluded() {
		return fIsIncluded;
	}

	public boolean isEnabled() {
		return fIsEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		fIsEnabled = isEnabled;
	}

	public boolean hasComputers() {
		List descriptors = fRegistry.getProposalComputerDescriptors();
		for (Iterator it = descriptors.iterator(); it.hasNext();) {
			EGLCompletionProposalComputerDescriptor desc = (EGLCompletionProposalComputerDescriptor) it.next();
			if (desc.getCategory() == this){
				return true;
			}
		}
		
		return false;
	}

	public boolean hasComputers(String partition) {
		List descriptors = fRegistry.getProposalComputerDescriptors(partition);
		for (Iterator it = descriptors.iterator(); it.hasNext();) {
			EGLCompletionProposalComputerDescriptor desc = (EGLCompletionProposalComputerDescriptor) it.next();
			if (desc.getCategory() == this)
				return true;
		}

		return false;
	}

	public int getSortOrder() {
		return fSortOrder;
	}

	public void setSortOrder(int sortOrder) {
		fSortOrder = sortOrder;
	}

	public List computeCompletionProposals(EGLContentAssistInvocationContext context, String partition, SubProgressMonitor monitor) {
		fLastError = null;
		List result = new ArrayList();
		List descriptors = new ArrayList(fRegistry.getProposalComputerDescriptors(partition));
		for (Iterator it = descriptors.iterator(); it.hasNext();) {
			EGLCompletionProposalComputerDescriptor desc = (EGLCompletionProposalComputerDescriptor) it.next();
			if (desc.getCategory() == this){
				result.addAll(desc.computeCompletionProposals(context, monitor));
			}
			if (fLastError == null && desc.getErrorMessage() != null){
				fLastError = desc.getErrorMessage();
			}
		}
		return result;
	}

	public List computeContextInformation(EGLContentAssistInvocationContext context, String partition, SubProgressMonitor monitor) {
		fLastError = null;
		List result = new ArrayList();
		List descriptors = new ArrayList(fRegistry.getProposalComputerDescriptors(partition));
		for (Iterator it = descriptors.iterator(); it.hasNext();) {
			EGLCompletionProposalComputerDescriptor desc = (EGLCompletionProposalComputerDescriptor) it.next();
			if (desc.getCategory() == this && (isIncluded() || isSeparateCommand())){
				result.addAll(desc.computeContextInformation(context, monitor));
			}
			if (fLastError == null){
				fLastError = desc.getErrorMessage();
			}
		}
		return result;
	}

	public String getErrorMessage() {
		return fLastError;
	}

	public void sessionStarted() {
		List descriptors = new ArrayList(fRegistry.getProposalComputerDescriptors());
		for (Iterator it = descriptors.iterator(); it.hasNext();) {
			EGLCompletionProposalComputerDescriptor desc = (EGLCompletionProposalComputerDescriptor) it.next();
			if (desc.getCategory() == this)
				desc.sessionStarted();
			if (fLastError == null)
				fLastError = desc.getErrorMessage();
		}
	}

	public void sessionEnded() {
		List descriptors = new ArrayList(fRegistry.getProposalComputerDescriptors());
		for (Iterator it = descriptors.iterator(); it.hasNext();) {
			EGLCompletionProposalComputerDescriptor desc = (EGLCompletionProposalComputerDescriptor) it.next();
			if (desc.getCategory() == this){
				desc.sessionEnded();
			}
			if (fLastError == null){
				fLastError = desc.getErrorMessage();
			}
		}
	}

}
