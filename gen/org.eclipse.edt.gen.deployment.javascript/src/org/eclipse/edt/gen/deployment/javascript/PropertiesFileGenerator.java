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
package org.eclipse.edt.gen.deployment.javascript;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.edt.javart.json.NameValuePairNode;
import org.eclipse.edt.javart.json.ObjectNode;
import org.eclipse.edt.javart.json.StringNode;

public abstract class PropertiesFileGenerator {

	private String propertiesVariable;
	
	protected PropertiesFileGenerator( String propertiesVariable )
	{
		this.propertiesVariable = propertiesVariable;
	}
	
	public byte[] generatePropertiesFile( InputStream is, String bundleName )
	{
		byte[] result = null;
		byte[] propertiesFileBytes = loadPropertiesFile( is );
		
		if ( propertiesFileBytes != null )
		{
			StringBuffer content = new StringBuffer();
			content.append( "try{" );
			content.append( propertiesVariable + "['" + bundleName + "'] = \n" );
			content.append(convertPropertiesToJSON( propertiesFileBytes ) );
			content.append( ";}catch(e){ }" );  //TODO Throw EGL Exception
			try {
				result = content.toString().getBytes("UTF-8");
			}
			catch (UnsupportedEncodingException uee) {
				// Won't happen.  Every JVM must support UTF-8.
				result = content.toString().getBytes();
			}
		}
		
		return result;
	}
	
	protected String convertPropertiesToJSON(byte[] bytes) {
		String result;
		Properties properties = new Properties();
		try{
			properties.load(new InputStreamReader(new ByteArrayInputStream(bytes), "UTF-8"));
			ObjectNode objectNode = new ObjectNode();
			for (Iterator iter = properties.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				StringNode keyNode = new StringNode(key, false);
				StringNode valueNode = new StringNode(((String)properties.get(key)).trim(), false);
				NameValuePairNode pairNode = new NameValuePairNode(keyNode, valueNode);
				
				objectNode.addPair(pairNode);
			}
			result = objectNode.toJson();
		}catch(IOException e){
			result = "";
		}
		return result;
	}

	protected byte[] loadPropertiesFile(InputStream is) {
		byte[] result = null;
		if(is!=null){
			try{
				DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
				try{
					result = new byte[dis.available()];
					dis.readFully(result);
					dis.close();
				}catch(IOException e){
					dis.close();
				}
			}catch(Exception e){
				result = null;
			}
		}
		return result;
	}
}
