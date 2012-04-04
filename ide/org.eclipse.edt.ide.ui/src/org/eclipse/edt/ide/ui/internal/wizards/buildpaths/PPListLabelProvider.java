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
package org.eclipse.edt.ide.ui.internal.wizards.buildpaths;

import java.net.URL;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.PPListElement;
import org.eclipse.edt.ide.core.model.PPListElementAttribute;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLElementImageDescriptor;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.viewsupport.ElementImageProvider;
import org.eclipse.edt.ide.ui.internal.viewsupport.ImageDescriptorRegistry;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;

public class PPListLabelProvider extends LabelProvider {
		
	private String fNewLabel, fCreateLabel;
	private ImageDescriptor fFolderImage, fProjectImage, sourceAttachment;
	
	private ImageDescriptorRegistry fRegistry;
	
	public PPListLabelProvider() {
		fNewLabel= NewWizardMessages.CPListLabelProviderNew;
		fCreateLabel= NewWizardMessages.CPListLabelProviderWillbecreated;
		fRegistry= EDTUIPlugin.getImageDescriptorRegistry();
		
		fFolderImage= PluginImages.DESC_OBJS_PACKFRAG_ROOT;

		IWorkbench workbench= EDTUIPlugin.getDefault().getWorkbench();
		fProjectImage= workbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_PROJECT);
		sourceAttachment = PluginImages.DESC_OBJS_SOURCE_ATTACHMENT;
	}
	
	public String getText(Object element) {
		if (element instanceof PPListElement) {
			return getCPListElementText((PPListElement) element);
		} else if (element instanceof PPListElementAttribute) {
			return getCPListElementAttributeText((PPListElementAttribute) element);
		}
		return super.getText(element);
	}
	
	public String getCPListElementAttributeText(PPListElementAttribute attrib) {
		String notAvailable= NewWizardMessages.CPListLabelProviderNone;
		StringBuffer buf= new StringBuffer();
		String key= attrib.getKey();
		if (key.equals(PPListElement.SOURCEATTACHMENT)) {
			buf.append(NewWizardMessages.CPListLabelProviderSource_attachmentLabel);
			IPath path= (IPath) attrib.getValue();
			if (path != null && !path.isEmpty()) {
				if (attrib.getParent().getEntryKind() == IEGLPathEntry.CPE_VARIABLE) {
					buf.append(getVariableString(path));
				} else {
					buf.append(getPathString(path, path.getDevice() != null));
				}
			} else {
				buf.append(notAvailable);
			}
		} else if (key.equals(PPListElement.SOURCEATTACHMENTROOT)) {
			buf.append(NewWizardMessages.CPListLabelProviderSource_attachment_rootLabel);
			IPath path= (IPath) attrib.getValue();
			if (path != null && !path.isEmpty()) {
				buf.append(path.toString());
			} else {
				buf.append(notAvailable);
			}
		} else if (key.equals(PPListElement.JAVADOC)) {
			buf.append(NewWizardMessages.CPListLabelProviderjavadoc_locationlabel);
			URL path= (URL) attrib.getValue();
			if (path != null) {
				buf.append(path.toExternalForm());
			} else {
				buf.append(notAvailable);
			}
		} else if (key.equals(PPListElement.OUTPUT)) {
			buf.append(NewWizardMessages.CPListLabelProviderOutput_folderLabel);
			IPath path= (IPath) attrib.getValue();
			if (path != null) {
				buf.append(path.makeRelative().toString());
			} else {
				buf.append(NewWizardMessages.CPListLabelProviderDefault_output_folderLabel);
			}
		} else if (key.equals(PPListElement.EXCLUSION)) {
			buf.append(NewWizardMessages.CPListLabelProviderExclusion_filterLabel);
			IPath[] patterns= (IPath[]) attrib.getValue();
			if (patterns != null && patterns.length > 0) {
				for (int i= 0; i < patterns.length; i++) {
					if (i > 0) {
						buf.append(NewWizardMessages.CPListLabelProviderExclusion_filter_separator);
					}
					buf.append(patterns[i].toString());
				}
			} else {
				buf.append(notAvailable);
			}
		}
		return buf.toString();
	}
	
	public String getCPListElementText(PPListElement cpentry) {
		IPath path= cpentry.getPath();
		switch (cpentry.getEntryKind()) {
			
			case IEGLPathEntry.CPE_LIBRARY: {
				IResource resource= cpentry.getResource();
				if ("eglar".equalsIgnoreCase(path.getFileExtension())) {
					return getPathString(path, resource == null);
				}
				// should not come here
				return path.makeRelative().toString();
			}
			
			case IEGLPathEntry.CPE_VARIABLE: {
				return getVariableString(path);
			}
			case IEGLPathEntry.CPE_PROJECT:
				return path.lastSegment();
			/*
			case IEGLPathEntry.CPE_CONTAINER:
				try {
					IEGLPathContainer container= EGLCore.getEGLPathContainer(cpentry.getPath(), cpentry.getJavaProject());
					if (container != null) {
						return container.getDescription();
					}
				} catch (EGLModelException e) {
	
				}
				return path.toString();
			*/
			case IEGLPathEntry.CPE_SOURCE: {
				StringBuffer buf= new StringBuffer(path.makeRelative().toString());
				IResource resource= cpentry.getResource();
				if (resource != null && !resource.exists()) {
					buf.append(' ');
					if (cpentry.isMissing()) {
						buf.append(fCreateLabel);
					} else {
						buf.append(fNewLabel);
					}
				}
				return buf.toString();
			}
			default:
				// pass
		}
		return NewWizardMessages.CPListLabelProviderUnknown_elementLabel;
	}
	
	private String getPathString(IPath path, boolean isExternal) {
		return isExternal ? path.toOSString() : path.makeRelative().toString();
	}
	
	private String getVariableString(IPath path) {
		String name= path.makeRelative().toString();
		IPath entryPath= EGLCore.getEGLPathVariable(path.segment(0));
		if (entryPath != null) {
			String appended= entryPath.append(path.removeFirstSegments(1)).toOSString();
			return NewWizardMessages.bind(NewWizardMessages.CPListLabelProviderTwopart, new String[] { name, appended }); //$NON-NLS-1$
		} else {
			return name;
		}
	}
	
	private ImageDescriptor getCPListElementBaseImage(PPListElement cpentry) {
		switch (cpentry.getEntryKind()) {
			case IEGLPathEntry.CPE_SOURCE:
				if (cpentry.getPath().segmentCount() == 1) {
					return fProjectImage;
				} else {
					return fFolderImage;
				}
			case IEGLPathEntry.CPE_LIBRARY:
				return PluginImages.DESC_OBJS_PACKFRAG_ROOT_EGLAR;
			case IEGLPathEntry.CPE_PROJECT:
				if(cpentry.getEGLPathEntry().isBinaryProject()) {
					return PluginImages.DESC_OBJS_EGL_BINARY_PROJECT_OPEN;
				}
				return fProjectImage;
			default:
				return null;
		}
	}			
		
	public Image getImage(Object element) {
		if (element instanceof PPListElement) {
			PPListElement cpentry= (PPListElement) element;
			ImageDescriptor imageDescriptor= getCPListElementBaseImage(cpentry);
			if (imageDescriptor != null) {
				if (cpentry.isMissing()) {
					imageDescriptor= new EGLElementImageDescriptor(imageDescriptor, EGLElementImageDescriptor.WARNING, ElementImageProvider.SMALL_SIZE);
				}
				return fRegistry.get(imageDescriptor);
			}
		} else if(element instanceof PPListElementAttribute) {
			String key= ((PPListElementAttribute)element).getKey();
			if (key.equals(PPListElement.SOURCEATTACHMENT)) {
				sourceAttachment = new EGLElementImageDescriptor(sourceAttachment, 0, ElementImageProvider.SMALL_SIZE);
				return fRegistry.get(sourceAttachment);
			}
		}
		return null;
	}


}	
