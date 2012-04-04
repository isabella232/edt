/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.wizards.newvariable;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfoRequestor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.swt.graphics.Image;

public class NewEGLVariableWizardUtil {
	public static enum DataType {
		Record, 
		DataItem
	}

	public static List<PartDeclarationInfo> getAvailableParts(IEGLProject project, DataType dataType) {
		int partType = 0;
		switch (dataType) {
		case Record:
			partType = IEGLSearchConstants.RECORD;
			break;
		case DataItem:
			partType = IEGLSearchConstants.ITEM;
			break;
		}
		List<PartDeclarationInfo> partInfos = new ArrayList<PartDeclarationInfo>();
		try {
			IEGLProject[] projects = new IEGLProject[] { project };
			IEGLSearchScope searchScope = SearchEngine.createEGLSearchScope(projects, true);
			PartInfoRequestor searchResult = new PartInfoRequestor(partInfos);
			new SearchEngine().searchAllPartNames(
					ResourcesPlugin.getWorkspace(), null, null,
					IIndexConstants.PATTERN_MATCH,
					IEGLSearchConstants.CASE_INSENSITIVE, partType,
					searchScope, searchResult,
					IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
		} catch (EGLModelException e) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error processing to find parts ", e));
		}
		return getFilterPartInfos(partInfos);
	}
	
	private static List<PartDeclarationInfo> getFilterPartInfos(List<PartDeclarationInfo> partInfos){
		List<PartDeclarationInfo> results = new ArrayList<PartDeclarationInfo>();
		for(PartDeclarationInfo partInfo : partInfos){
			if(!"org.eclipse.edt.rui.widgets".equals(partInfo.getPackageName()) 
				&& !"org.eclipse.edt.rui.mvc".equals(partInfo.getPackageName())
				&& !"org.eclipse.edt.rui.infobus".equals(partInfo.getPackageName())
				&& !"org.eclipse.edt.rui.history".equals(partInfo.getPackageName())
				&& !"dojo.widgets".equals(partInfo.getPackageName())){
				results.add(partInfo);
			}
		}
		return results;
	}


	public static Image getImage(DataType dataType) {
		switch (dataType) {
		case Record:
			return PluginImages.get(PluginImages.IMG_OBJS_RECORD);
		case DataItem:
			return PluginImages.get(PluginImages.IMG_OBJS_DATAITEM);
		default:
			return null;
		}
	}
	
}
