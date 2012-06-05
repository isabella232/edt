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
package org.eclipse.edt.ide.ui.internal.util;

import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class PartInfoLabelProvider extends LabelProvider {
	
	public static final int SHOW_FULLYQUALIFIED=	0x01;
	public static final int SHOW_PACKAGE_POSTFIX=	0x02;
	public static final int SHOW_PACKAGE_ONLY=		0x04;
	public static final int SHOW_ROOT_POSTFIX=		0x08;
	public static final int SHOW_TYPE_ONLY=			0x10;
	public static final int SHOW_TYPE_CONTAINER_ONLY=	0x20;
	
	private static final Image EGLFILE_ICON= PluginImages.get(PluginImages.IMG_OBJS_EGLFILE);
	private static final Image PGM_ICON= PluginImages.get(PluginImages.IMG_OBJS_PGM);
	private static final Image PAGE_ICON= PluginImages.get(PluginImages.IMG_OBJS_PAGE);
	private static final Image FUNCTION_ICON= PluginImages.get(PluginImages.IMG_OBJS_FUNCTION);
	private static final Image RECORD_ICON= PluginImages.get(PluginImages.IMG_OBJS_RECORD);
	private static final Image TABLE_ICON= PluginImages.get(PluginImages.IMG_OBJS_TABLE);
	private static final Image LIBRARY_ICON= PluginImages.get(PluginImages.IMG_OBJS_LIBRARY);
	private static final Image ITEM_ICON= PluginImages.get(PluginImages.IMG_OBJS_DATAITEM);
	private static final Image FORM_ICON= PluginImages.get(PluginImages.IMG_OBJS_FORM);
	private static final Image FORMGRP_ICON= PluginImages.get(PluginImages.IMG_OBJS_FORMGRP);
	private static final Image PKG_ICON= PluginImages.get(PluginImages.IMG_OBJS_PACKAGE);
	private static final Image SERVICE_ICON = PluginImages.get(PluginImages.IMG_OBJS_SERVICE);
	private static final Image INTERFACE_ICON = PluginImages.get(PluginImages.IMG_OBJS_INTERFACE);
	private static final Image HANDLER_ICON = PluginImages.get(PluginImages.IMG_OBJS_HANDLER);
	private static final Image ENUM_ICON = PluginImages.get(PluginImages.IMG_OBJS_ENUMERATION);
	private static final Image EXTERNAL_TYPE_ICON = PluginImages.get(PluginImages.IMG_OBJS_EXTERNALTYPE);
	private static final Image DELEGATE_ICON = PluginImages.get(PluginImages.IMG_OBJS_DELEGATE);
	
	private int fFlags;
	
	public PartInfoLabelProvider(int flags) {
		fFlags= flags;
	}	
	
	private boolean isSet(int flag) {
		return (fFlags & flag) != 0;
	}

	private String getPackageName(String packName) {
		if (packName.length() == 0)
			return UINlsStrings.OpenPartDialog_DefaultPackage;
		else
			return packName;
	}

	/* non java-doc
	 * @see ILabelProvider#getText
	 */
	public String getText(Object element) {
		if (! (element instanceof PartInfo)) 
			return super.getText(element);
		
		PartInfo typeRef= (PartInfo) element;
		StringBuffer buf= new StringBuffer();
		if (isSet(SHOW_TYPE_ONLY)) {
			buf.append(typeRef.getPartName());
		} else if (isSet(SHOW_TYPE_CONTAINER_ONLY)) {
			String containerName= typeRef.getPartContainerName();
			buf.append(getPackageName(containerName));
			if (typeRef instanceof PartDeclarationInfo)
				buf.append(getFileName((PartDeclarationInfo) typeRef));
		} else if (isSet(SHOW_PACKAGE_ONLY)) {
			String packName= typeRef.getPackageName();
			buf.append(getPackageName(packName));
		} else {
			if (isSet(SHOW_FULLYQUALIFIED))
				buf.append(typeRef.getFullyQualifiedName());
			else
				buf.append(typeRef.getPartQualifiedName());

			if (isSet(SHOW_PACKAGE_POSTFIX)) {
				buf.append("--"); //$NON-NLS-1$
				String packName= typeRef.getPackageName();
				buf.append(getPackageName(packName));
			}
		}
		if (isSet(SHOW_ROOT_POSTFIX)) {
			buf.append("--");//$NON-NLS-1$
			if(typeRef instanceof PartDeclarationInfo){
				buf.append(((PartDeclarationInfo)typeRef).getContainerPath());
			} else{
				buf.append(typeRef.getPackageFragmentRootPath().toString());
			}
		}
		return buf.toString();				
	}

	private String getFileName(PartDeclarationInfo typeRef) {
		StringBuffer buf = new StringBuffer();
		buf.append("."); //$NON-NLS-1$
		buf.append(typeRef.getFileName());
		buf.append("." + typeRef.getExtension());
		return buf.toString();
	}
	
	/* non java-doc
	 * @see ILabelProvider#getImage
	 */	
	public Image getImage(Object element) {
		if (! (element instanceof PartInfo)) 
			return super.getImage(element);	

		if (isSet(SHOW_TYPE_CONTAINER_ONLY)) {
			PartInfo typeRef= (PartInfo) element;
			if (typeRef.getPackageName().equals(typeRef.getPartContainerName()))
				return PKG_ICON;

			// XXX cannot check outer type for interface efficiently (5887)
			return EGLFILE_ICON;

		} else if (isSet(SHOW_PACKAGE_ONLY)) {
			return PKG_ICON;
		} else {
			if (element instanceof PartDeclarationInfo) {
				switch (((PartDeclarationInfo)element).getPartType()) {
					case IEGLSearchConstants.PROGRAM: return PGM_ICON;
					case IEGLSearchConstants.LIBRARY: return LIBRARY_ICON;
					case IEGLSearchConstants.RECORD: return RECORD_ICON;
					case IEGLSearchConstants.TABLE: return TABLE_ICON;
					case IEGLSearchConstants.ITEM: return ITEM_ICON;
					case IEGLSearchConstants.FORM: return FORM_ICON;
					case IEGLSearchConstants.FORMGROUP: return FORMGRP_ICON;
					case IEGLSearchConstants.FUNCTION: return FUNCTION_ICON	;
					case IEGLSearchConstants.SERVICE: return SERVICE_ICON;
					case IEGLSearchConstants.INTERFACE: return INTERFACE_ICON;
					case IEGLSearchConstants.EXTERNALTYPE: return EXTERNAL_TYPE_ICON;
					case IEGLSearchConstants.HANDLER: return HANDLER_ICON;
					case IEGLSearchConstants.ENUMERATION: return ENUM_ICON;
					case IEGLSearchConstants.DELEGATE: return DELEGATE_ICON;
					default: return null;
				}
			}
			return EGLFILE_ICON;
		}
	}	
}
