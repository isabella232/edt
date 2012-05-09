/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.search;

import java.text.MessageFormat;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.model.ClassFile;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragment;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class EGLSearchResultLabelProvider extends LabelProvider
{
	public static final int SHOW_LABEL= 1;
	public static final int SHOW_LABEL_PATH= 2;
	public static final int SHOW_PATH_LABEL= 3;
	public static final int SHOW_PATH= 4;
	
	private static final String fgSeparatorFormat= "{0} - {1}"; //$NON-NLS-1$
	
	private WorkbenchLabelProvider fLabelProvider;
	//private ILabelDecorator fDecorator;
    private AbstractTextSearchViewPage fPage;
		
	private int fOrder;
	private String[] fArgs= new String[2];

	public EGLSearchResultLabelProvider(AbstractTextSearchViewPage page, int orderFlag) {
		//fDecorator= PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		fLabelProvider= new WorkbenchLabelProvider();
		fOrder= orderFlag;
        fPage= page;
	}

	public void setOrder(int orderFlag) {
		fOrder= orderFlag;
	}
	
	public int getOrder() {
		return fOrder;
	}

	public String getText(Object element) {
		if(element instanceof IFunction) {
			String fullName = ((IFunction) element).getElementName();
			int index = fullName.indexOf("#");
			if(index != -1) {
				return fullName.substring(index + 1); 
			} else {
				return fullName;
			}
		} else if (element instanceof IPart || element instanceof ClassFile) {
			int matchCount= 0;
			AbstractTextSearchResult result= fPage.getInput();
			if (result != null)
				matchCount= result.getMatchCount(element);
			if (matchCount <= 1)
				return ((IEGLElement) element).getElementName();
			String format= EGLSearchMessages.EGLSearchResultLabelProviderCountFormat;
			return MessageFormat.format(format, new Object[] { ((IEGLElement) element).getElementName(), new Integer(matchCount) });
			
		} else if(element instanceof IEGLElement) {
			if(element instanceof EglarPackageFragment){
				//String eglarName = ((IEGLElement) element).getParent().getElementName();
				//String projName = ((IEGLElement) element).getEGLProject().getElementName();
				//fix for RTC64685
				//element is a JarPackageFragment; for those in the default package, "((IEGLElement) element).getElementName()"
				//returns an empty string. Should handle such case.
				String packageName = ((IEGLElement) element).getElementName();
				if(packageName == null || packageName.trim().length() == 0){
					packageName = EGLSearchMessages.EGLSearchResultLabelProviderDefaultPackage;
				}
				return packageName;
			} else if(element instanceof EglarPackageFragmentRoot){
				String eglarName = ((IEGLElement) element).getElementName();
				return eglarName;
			}
		}
		else if (!(element instanceof IResource))
			return null; //$NON-NLS-1$

		IResource resource= (IResource)element;
		String text= null;

		if (!resource.exists())
			text= EGLSearchMessages.EGLSearchResultLabelProviderRemoved_resourceLabel;
		
		else {
			IPath path= resource.getFullPath().removeLastSegments(1);
			if (path.getDevice() == null)
				path= path.makeRelative();
			if (fOrder == SHOW_LABEL || fOrder == SHOW_LABEL_PATH) {
				text= fLabelProvider.getText(resource);
				if (path != null && fOrder == SHOW_LABEL_PATH) {
					fArgs[0]= text;
					fArgs[1]= path.toString();
					text= MessageFormat.format(fgSeparatorFormat, fArgs);
				}
			} else {
				if (path != null)
					text= path.toString();
				else
					text= ""; //$NON-NLS-1$
				if (fOrder == SHOW_PATH_LABEL) {
					fArgs[0]= text;
					fArgs[1]= fLabelProvider.getText(resource);
					text= MessageFormat.format(fgSeparatorFormat, fArgs);
				}
			}
		}
		
/*
		// Do the decoration
		if (fDecorator != null) {
			String decoratedText= fDecorator.decorateText(text, resource);
		if (decoratedText != null)
			return decoratedText;
		}
*/
		int matchCount= 0;
		AbstractTextSearchResult result= fPage.getInput();
		if (result != null)
			matchCount= result.getMatchCount(element);
		if (matchCount <= 1)
			return text;
		String format= EGLSearchMessages.EGLSearchResultLabelProviderCountFormat;
		return MessageFormat.format(format, new Object[] { text, new Integer(matchCount) });
	}

	public Image getImage(Object element) {
		if(element instanceof IFunction) {
			ImageDescriptorRegistry reg = EDTUIPlugin.getImageDescriptorRegistry();
			ImageDescriptor desp = PluginImages.DESC_OBJS_FUNCTION;
			return reg.get(desp);
		} else if (element instanceof IPart || element instanceof ClassFile) {
			ImageDescriptorRegistry reg = EDTUIPlugin.getImageDescriptorRegistry();
			ImageDescriptor desp = PluginImages.DESC_OBJS_CFILE;
			return reg.get(desp);
		} else if (element instanceof EglarPackageFragmentRoot) {
			ImageDescriptorRegistry reg = EDTUIPlugin.getImageDescriptorRegistry();
			ImageDescriptor desp = PluginImages.DESC_OBJS_PACKFRAG_ROOT_EGLAR;
			return reg.get(desp);
		} else if(element instanceof IEGLElement) {
			ImageDescriptorRegistry reg = EDTUIPlugin.getImageDescriptorRegistry();
			ImageDescriptor desp = PluginImages.DESC_OBJS_PACKAGE;
			return reg.get(desp);
		} else if (!(element instanceof IResource))
			return null; //$NON-NLS-1$

		IResource resource= (IResource)element;
		Image image= fLabelProvider.getImage(resource);
		return image;
	}

	public void dispose() {
		super.dispose();
		fLabelProvider.dispose();
	}

	public boolean isLabelProperty(Object element, String property) {
		return fLabelProvider.isLabelProperty(element, property);
	}

	public void removeListener(ILabelProviderListener listener) {
		super.removeListener(listener);
		fLabelProvider.removeListener(listener);
	}

	public void addListener(ILabelProviderListener listener) {
		super.addListener(listener);
		fLabelProvider.addListener(listener);
	}
    
}
