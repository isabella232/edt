/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.deployment.util;

import java.util.ArrayList;
import java.util.List;

import com.ibm.icu.util.StringTokenizer;

public class PropertiesFileUtil {

	private static final String BUNDLE_SEPARATOR = "-";
	
	private String propertiesFileName;
	private String userMessageLocale;
	
	public PropertiesFileUtil(String propertiesFileName, String userMessageLocale){
		this.propertiesFileName = propertiesFileName;
		this.userMessageLocale = userMessageLocale;		
	}
	
	public PropertiesFileUtil(String includeStatement){
		String temp = includeStatement;
		
		// Remove file extension.
		int lastDot = temp.lastIndexOf('.');
		if (lastDot != -1) {
			temp = temp.substring(0, lastDot);
		}
		
		int sepIndex = temp.indexOf(BUNDLE_SEPARATOR);
		if ( sepIndex != -1 )
		{
			this.propertiesFileName = temp.substring(temp.lastIndexOf("/") + 1, sepIndex);
			this.userMessageLocale = temp.substring(sepIndex + 1, temp.length());
		}
		else
		{
			this.propertiesFileName = temp.substring(temp.lastIndexOf("/") + 1);
			this.userMessageLocale = "";
		}
	}
	
	public String getBundleName(){
		return propertiesFileName;
	}
	
	public String getUserMessageLocale(){
		return userMessageLocale;
	}
	
	public String generateIncludeStatement(){
		return propertiesFileName + BUNDLE_SEPARATOR + userMessageLocale + ".js";
	}
	
	/**
	 * Returns the names of files where we might find the properties.  They are ordered
	 * from most specific to least specific.  For example, if the properties file name
	 * is "foo" and the locale is "en_GB_variant1", this method will return an array
	 * with the following elements:
	 * <OL>
	 * <LI>foo-en_GB_variant1.properties</LI>
	 * <LI>foo-en_GB.properties</LI>
	 * <LI>foo-en.properties</LI>
	 * <LI>foo.properties</LI>
	 * </OL>
	 * 
	 * @return the names of files where we might find the properties.
	 */
	public String[] generatePropertiesFileNames(){
		StringTokenizer st = new StringTokenizer( userMessageLocale, "_" );
		int count = st.countTokens();
		String[] names = new String[ count + 1 ];
		if ( count > 0 )
		{
			String prefix = propertiesFileName + BUNDLE_SEPARATOR + st.nextToken();
			names[ count - 1 ] = prefix + ".properties";
			for ( int i = count - 2; i >= 0; i-- )
			{
				prefix += '_' + st.nextToken();
				names[ i ] = prefix + ".properties";
			}
		}
		names[ count ] = propertiesFileName + ".properties";
		return names;
	}
	
	public static String convertToProperitesFile(String includeStatement){
		// Remove file extension and append '.properties'
		int lastDot = includeStatement.lastIndexOf('.');
		if (lastDot != -1) {
			includeStatement = includeStatement.substring(0, lastDot);
		}
		
		return includeStatement + ".properties";
	}
	
	/**
	 * Returns the names of files where we might find the properties from the js file path.  
	 * They are ordered from most specific to least specific.  For example, if the properties 
	 * file name is "foo" and the locale is "en_GB_variant1", this method will return an array
	 * with the following elements:
	 * <OL>
	 * <LI>foo-en_GB_variant1.properties</LI>
	 * <LI>foo-en_GB.properties</LI>
	 * <LI>foo-en.properties</LI>
	 * <LI>foo.properties</LI>
	 * </OL>
	 * @param jsFileName
	 *            the full js file name of a property.
	 * @return the names of files where we might find the properties.
	 */
	public static String[] convertToProperitesFiles(String jsFileName){
		List<String> names = new ArrayList<String>();
		int bundleIndex = jsFileName.lastIndexOf(BUNDLE_SEPARATOR);
		int lastIndex = jsFileName.lastIndexOf('.');
		jsFileName = jsFileName.substring(0, lastIndex);
		names.add(jsFileName + ".properties");
		if(bundleIndex != -1){
			while(lastIndex > bundleIndex) {			
				jsFileName = jsFileName.substring(0, lastIndex);
				names.add(jsFileName + ".properties");
				lastIndex = jsFileName.lastIndexOf('_');
			}
			names.add(jsFileName.substring(0, bundleIndex) + ".properties");
		}
		
		return names.toArray(new String[names.size()]);
	}
}
