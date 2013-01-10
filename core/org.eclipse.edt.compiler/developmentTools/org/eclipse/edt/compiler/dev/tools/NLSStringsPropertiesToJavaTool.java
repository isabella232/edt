/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.dev.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.ibm.icu.util.StringTokenizer;

/**
 * 
 * @author jshavor
 *
 * This tool creates static declartions for a Java class using a properties file as input
 *
 */
public class NLSStringsPropertiesToJavaTool {
	//Need to change these 2 lines to find the right input file
	private static final String dir = "d:\\a\\nlsConversion\\"; //$NON-NLS-1$
	private static final String propertyFileName = "wizardMessages.properties"; //$NON-NLS-1$

	private static final String PUBLIC = "public"; //$NON-NLS-1$
	private static final String STATIC = "static"; //$NON-NLS-1$
	private static final String FINAL = "final"; //$NON-NLS-1$
	private static final String STRING = "String"; //$NON-NLS-1$
	private static final String NON_NLS = "//$NON-NLS-1$"; //$NON-NLS-1$
	
	public static void main(String[] args) {
        new NLSStringsPropertiesToJavaTool().createStaticFields();
	}

	private void createStaticFields() {
		try {
        	String line;
        	BufferedReader in = new BufferedReader(new FileReader(dir + propertyFileName));
        	while ((line = in.readLine()) != null) {
        		StringTokenizer tokenizer = new StringTokenizer(line);
        		if (tokenizer.countTokens() > 0) {
            		String value = tokenizer.nextToken();
            		if (value.startsWith("#")) { //$NON-NLS-1$
            			//not interested in this line
            		}
            		else {
            			int offset = value.indexOf('=');
            			if (offset > 0)
            				value = value.substring(0, offset);
            			createStaticDeclaration(createKey(value), value);
            		}
        		}
        		else
                    System.out.println(line);	//not interested in this line, no changes
        	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String createKey(String key) {
		while (true) {
			int offset2 = key.indexOf('.');
			if (offset2 > 0) {
				StringBuffer buffer = new StringBuffer();
				buffer.append(key.subSequence(0, offset2));
				String sss = key.substring(offset2+1, offset2+2).toUpperCase();
				buffer.append(sss);
				buffer.append(key.substring(offset2+2, key.length()));
				key = buffer.toString();
			}
			else
				break;
		}
		return key;
	}

	private void createStaticDeclaration(String key, String value) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.setLength(0);
		stringBuffer.append("	"); //$NON-NLS-1$
		stringBuffer.append(PUBLIC);
		stringBuffer.append(" "); //$NON-NLS-1$
		stringBuffer.append(STATIC);
		stringBuffer.append(" "); //$NON-NLS-1$
		stringBuffer.append(FINAL);
		stringBuffer.append(" "); //$NON-NLS-1$
		stringBuffer.append(STRING);
		stringBuffer.append(" "); //$NON-NLS-1$
		stringBuffer.append(key);
		stringBuffer.append("=\""); //$NON-NLS-1$
		stringBuffer.append(value);
		stringBuffer.append("\""); //$NON-NLS-1$
		stringBuffer.append(";"); //$NON-NLS-1$
		stringBuffer.append("	"); //$NON-NLS-1$
		stringBuffer.append(NON_NLS);
		System.out.println(stringBuffer);
	}

}
