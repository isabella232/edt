/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.services.servlet.rest.rpc;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.javart.Constants;

public class PreviewServiceServlet extends ServiceServlet {

	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	private List<String> addedUris = new ArrayList<String>();
	
	@Override
	protected RestServiceProjectInfo restServiceProjectInfo() {
		if (restServiceProjectInfo == null) {
			restServiceProjectInfo = new RestServiceProjectInfo("/" + contextRoot + "/restservices", new URL[0]);
		}
		return restServiceProjectInfo;
	}
	
	public void addServiceMapping(String uri, String className, boolean stateful) {
		addedUris.add(uri);
		restServiceProjectInfo().addURI(uri, "POST", className, stateful, false, null, null, null);
	}
	
	public void removeServiceMapping(String uri) {
		addedUris.remove(uri);
		restServiceProjectInfo().removeURI(uri, "POST");
	}
	
	public void clearServiceMappings() {
		for (String uri : addedUris) {
			restServiceProjectInfo().removeURI(uri, "POST");
		}
		addedUris.clear();
	}
}
