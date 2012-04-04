/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.json;

import java.util.List;

import org.eclipse.edt.javart.messages.Message;

import eglx.lang.DynamicAccessException;


public class JsonUtilities 
{
	public static ValueNode getValueNode( ObjectNode object, String key )
	{
		List<NameValuePairNode> pairs = object.pairs;
		NameValuePairNode pair;
		for( int idx = 0; idx < pairs.size(); idx++ )
		{
			pair = pairs.get(idx);
			if( pair.getName().getJavaValue().equalsIgnoreCase(key) )
			{
				return pair.getValue();
			}
		}
		DynamicAccessException dax = new DynamicAccessException();
		throw dax.fillInMessage( Message.DYNAMIC_ACCESS_FAILED, key, object );
	}

	public static ValueNode getValueNode( ObjectNode object, String key, ValueNode defaultValue )
	{
		List pairs = object.pairs;
		NameValuePairNode pair;
		for( int idx = 0; idx < pairs.size(); idx++ )
		{
			pair = (NameValuePairNode)pairs.get(idx);
			if( pair.getName().getJavaValue().equalsIgnoreCase(key) )
			{
				return pair.getValue();
			}
		}
		return defaultValue;
	}

}
