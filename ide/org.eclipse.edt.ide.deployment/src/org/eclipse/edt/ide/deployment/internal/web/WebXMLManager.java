/*******************************************************************************
 * Copyright © 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.internal.web;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;

public class WebXMLManager 
{
	public static WebXMLManager instance = new WebXMLManager();
	private Map<String, WebXML> webXMLs = new HashMap<String, WebXML>();;
	public WebXML getWebXMLUtil( IProject project )
	{
		WebXML util = null;
		if( project != null && project.getName() != null && project.getName().length() > 0 )
		{
			util = (WebXML)webXMLs.get(project.getName());
			if( util == null )
			{
				util = new WebXML(project);
				webXMLs.put( project.getName(), util );
			}
		}
		return util;
	}
	
	public void updateModel( IProject project )
	{
		WebXML model = getWebXMLUtil( project );
		model.updateModel();
		webXMLs.remove(project.getName());
	}
}
