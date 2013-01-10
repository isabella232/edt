/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IConstants {

	public static final String RUI_PROXY_SERVLET_NAME = "EglProxyServlet";
	public static final String RUI_PROXY_MAPPING = "/___proxy";
	public static final String RUI_PROXY_SERVLET = "org.eclipse.edt.javart.services.servlet.proxy.AjaxProxyServlet";
	
	public static final String RUI_COMPRESS_FILTER_NAME = "CompressionFilter";
	public static final String RUI_COMPRESS_FILTER_CLASSNAME = "org.eclipse.edt.javart.util.gzip.CompressionFilter";
	public static final Map<String, String> initParameterList = new HashMap<String, String>();
	public static final List<String> urlMappingList = new ArrayList<String>();

	static {
		initParameterList.put("enable_compression", "true");
		initParameterList.put("compression_threshold", "2048");
		initParameterList.put("trace_level", "0");
		initParameterList.put("trace_dev", "0");
	
		urlMappingList.add("*.html");
		urlMappingList.add("*.css");
		urlMappingList.add("*.js");
		urlMappingList.add("/___proxy");
	}
}
