/*******************************************************************************
 * Copyright © 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.services.generators;

import org.eclipse.edt.ide.deployment.core.model.Constants;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Part;

public class ServiceUtilities {
	
	public static final String REST_SERVICE_ROOT_ID_ELEM = "restservices";

	public static String getUriMappingFileName( String contextRoot )
	{
		return contextRoot + RestServiceUtilities.URI_MAPPING_FILE_SUFFIX;
	}
	
	static boolean isStateful( Part part )
	{
		boolean isStateful = false;
		Annotation annot = part.getAnnotation( Constants.SERVICE_STATEFUL_ANNOTATION );
		if( annot != null )
		{
			Object value = annot.getValue();
			isStateful = value instanceof Boolean ? ((Boolean)value).booleanValue() : false;
		}
		return isStateful;
	}
	
	static String getUri( LogicAndDataPart part )
	{
		String uri = "";
		Annotation uriAnnotation = part.getAnnotation(Constants.SERVICE_URI_ANNOTATION);
		if( uriAnnotation != null )
		{
			uri = (String)uriAnnotation.getValue();
		}
		if( uri == null || uri.length() == 0 )
		{
			StringBuffer defaultValue = new StringBuffer();
			String path = part.getPackageName();
			if ( path != null ) {
				defaultValue.append( path.replaceAll( "\\\\.", "_" ) );
			}
			defaultValue.append( part.getId() );
			uri = defaultValue.toString();
		}
		if( uri.charAt(0) != '/' )
		{
			uri = '/' + uri;
		}
		return uri;
	}
	
	public static String getRestServiceRoot()
	{
		return REST_SERVICE_ROOT_ID_ELEM;
	}
}
