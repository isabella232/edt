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
package org.eclipse.edt.compiler.core.dev.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.ibm.icu.util.StringTokenizer;

/**
 * 
 * @author jshavor
 *
 * This tool helps to migrate properties files to the new V7.0 syntax
 *
 */
public class NLSStringsTool {
	//Need to change these 4 lines to find the right input files
	private static final String dir = "d:\\a\\nlsConversion\\";

//  com.ibm.etools.egl
//	private static final String javaFileName = "EGLBaseNlsStrings.java";
//	private static final String propertyFileName = "EGLBaseResources.properties";
//	private static final String propertyFilePath = "org.eclipse.edt.compiler.internal.EGLBaseResources";

//  com.ibm.etools.egl.webtrans
	private static final String javaFileName = "EGLResourceHandler.java";
	private static final String propertyFileName = "webtrans.properties";
	private static final String propertyFilePath = "com.ibm.etools.egl.webtrans.nls.webtrans";

//  com.ibm.etools.egl.tui
//	private static final String javaFileName = "AVContentMessages.java";
//	private static final String propertyFileName = "AVContentMessages.properties";
//	private static final String propertyFilePath = "com.ibm.etools.egl.tui.ui.views.util.AVContentMessages";

//	private static final String javaFileName = "EGLTuiWizardPageMessages.java";
//	private static final String propertyFileName = "EGLTuiWizardPageMessages.properties";
//	private static final String propertyFilePath = "com.ibm.etools.egl.tui.ui.wizards.pages.EGLTuiWizardPageMessages";

//	private static final String javaFileName = "EGLTuiWizardMessages.java";
//	private static final String propertyFileName = "EGLTuiWizardMessages.properties";
//	private static final String propertyFilePath = "com.ibm.etools.egl.tui.ui.wizards.EGLTuiWizardMessages";

//	private static final String javaFileName = "EGLTuiPreferenceMessages.java";
//	private static final String propertyFileName = "EGLTuiPreferenceMessages.properties";
//	private static final String propertyFilePath = "com.ibm.etools.egl.tui.ui.preferences.EGLTuiPreferenceMessages";

//	private static final String javaFileName = "EGLTuiEditorMessages.java";
//	private static final String propertyFileName = "EGLTuiEditorMessages.properties";
//	private static final String propertyFilePath = "com.ibm.etools.egl.tui.ui.editors.EGLTuiEditorMessages";

//	private static final String javaFileName = "EGLTuiDialogMessages.java";
//	private static final String propertyFileName = "EGLTuiDialogMessages.properties";
//	private static final String propertyFilePath = "com.ibm.etools.egl.tui.ui.dialogs.EGLTuiDialogMessages";

//	private static final String javaFileName = "EGLTuiUtilityMessages.java";
//	private static final String propertyFileName = "EGLTuiUtilityMessages.properties";
//	private static final String propertyFilePath = "com.ibm.etools.egl.tui.model.utils.EGLTuiUtilityMessages";

//  com.ibm.etools.egl.core
//	private static final String javaFileName = "EGLCoreNlsStrings.java";
//	private static final String propertyFileName = "EGLCoreResources.properties";
//	private static final String propertyFilePath = "com.ibm.etools.egl.core.EGLCoreResources";

//  com.ibm.etools.egl.core.ide
//	private static final String javaFileName = "EGLCoreIDENlsStrings.java";
//	private static final String propertyFileName = "EGLCoreIDEResources.properties";
//	private static final String propertyFilePath = "com.ibm.etools.egl.core.ide.EGLCoreIDEResources";

//	private static final String javaFileName = "EGLModelResources.java";
//	private static final String propertyFileName = "EGLModelResources.properties";
//	private static final String propertyFilePath = "com.ibm.etools.egl.model.internal.core.EGLModelResources";

//	private static final String javaFileName = "Util.java";
//	private static final String propertyFileName = "EGLModelSearchResources.properties";
//	private static final String propertyFilePath = "com.ibm.etools.egl.model.internal.core.search.EGLModelSearchResources";

//  com.ibm.etools.egl.parteditor
//	private static final String javaFileName = "EGLPartEditorNlsStrings.java";
//	private static final String propertyFileName = "EGLPartEditorResources.properties";
//	private static final String propertyFilePath = "org.eclipse.edt.compiler.internal.parteditor.EGLPartEditorResources";
	
//  com.ibm.etools.egl.sql
//	private static final String javaFileName = "EGLSQLNlsStrings.java";
//	private static final String propertyFileName = "EGLSQLResources.properties";
//	private static final String propertyFilePath = "org.eclipse.edt.compiler.internal.sql.EGLSQLResources";
	
//	private static final String javaFileName = "EGLUINlsStrings.java";
//	private static final String propertyFileName = "EGLUIMessages.properties";
//	private static final String propertyFilePath = "org.eclipse.edt.compiler.internal.ui.EGLUIMessages";

//	private static final String javaFileName = "NewWizardMessages1.java";
//	private static final String propertyFileName = "NewWizardMessages.properties";
//	private static final String propertyFilePath = "org.eclipse.edt.compiler.internal.ui.wizards.NewWizardMessages";

//	private static final String javaFileName = "EGLSearchMessages2.java";
//	private static final String propertyFileName = "EGLSearchMessages.properties";
//	private static final String propertyFilePath = "org.eclipse.edt.compiler.internal.ui.search.EGLSearchMessages";

//	private static final String javaFileName = "EGLSourceAssistantNlsStrings.java";
//	private static final String propertyFileName = "SourceAssistantMessages.properties";
//	private static final String propertyFilePath = "org.eclipse.edt.compiler.internal.editor.source.assistant.SourceAssistantMessages";

	private static final String PACKAGE = "package";
	private static final String IMPORT = "import";
	private static final String PUBLIC = "public";
	private static final String PRIVATE = "private";
	private static final String CLASS = "class";
	private static final String STATIC = "static";
	private static final String FINAL = "final";
	private static final String STRING = "String";
	private static final String BUNDLE_NAME = "BUNDLE_NAME";
	private static final String INTERFACE = "interface";
	private static final String EXTENDS_NLS = "extends NLS";
	private static final String NON_NLS = "//$NON-NLS-1$";
	
	private static HashMap mapping;

	public static void main(String[] args) {
        new NLSStringsTool().run();
	}

	private void run() {
		mapping = createMapping();
		createClassFile();
		System.out.println();
		System.out.println("=======================================================");
		System.out.println();
		createPropertiesFile();
	}

	private void createPropertiesFile() {
		try {
        	String line;
        	String key;
        	String newKey;
        	String restOfLine;
        	int offset;
        	BufferedReader in = new BufferedReader(new FileReader(dir + propertyFileName));
        	while ((line = in.readLine()) != null) {
        		if (line.length() == 0 || line.startsWith("#")) 
        			System.out.println(line);	//not interested in this line, no changes
        		else {
        			offset = line.indexOf('=');
            		key = line.substring(0, offset).trim();
            		newKey = (String) mapping.get(key);
            		if (newKey == null) {
            			System.out.println(line);
            		}
            		else {
                		restOfLine = line.substring(offset, line.length());
                		System.out.println(newKey + restOfLine);
            		}
        		}
        	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Create the relationship of Java key to properties key 
	 */
	private HashMap createMapping() {
		mapping = new HashMap();
		try {
        	String line;
        	BufferedReader in = new BufferedReader(new FileReader(dir + javaFileName));
        	while ((line = in.readLine()) != null) {
        		StringTokenizer tokenizer = new StringTokenizer(line);
        		int numberOfTokens = tokenizer.countTokens();
        		if (numberOfTokens > 5) {
            		String firstToken = tokenizer.nextToken();
            		String secondToken = tokenizer.nextToken();
            		tokenizer.nextToken();
            		tokenizer.nextToken();
            		String key = tokenizer.nextToken();
            		String value = tokenizer.nextToken();
               		if (firstToken.equals(PUBLIC) && secondToken.equals(STATIC)) {
               			if (numberOfTokens == 6) {
               				//handle "=" with key and value both in one token because there was no space before the equal sign
               				int offset = key.indexOf('=');
               				String key1 = key.substring(0, offset);
               				String key2 = key.substring(offset+2, key.length()-2);
                   			mapping.put(key2, key1);
               			}
               			else if (numberOfTokens == 7){
               				//handle " =" and "= " with key and value both in different tokens
               				if (value.charAt(0) == '=') {
                   				value = value.substring(2, value.length()-2);
               				}
               				else {
                   				key = key.substring(0, key.length()-1);
               				}
                   			mapping.put(value, key);
               			}
               			else if (numberOfTokens == 8){
               				//handle " = " with key and value both in different tokens
               				value = tokenizer.nextToken();
               				value = value.substring(1, value.length()-2);
                   			mapping.put(value, key);
               			}
               			else {
                			System.out.println(key + " - I do not understand this line!!!");
               			}
            		}
        		}
        	}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapping;
	}

	private void createClassFile() {
		try {
        	String line;
        	BufferedReader in = new BufferedReader(new FileReader(dir + javaFileName));
        	while ((line = in.readLine()) != null) {
        		line = removeEverythingAfterEquals(line);	//This handles the case where there is no space before the equal sign
        		StringTokenizer tokenizer = new StringTokenizer(line);
        		if (tokenizer.countTokens() > 1) {
            		String firstToken = tokenizer.nextToken();
            		String secondToken = tokenizer.nextToken();
            		if (firstToken.equals(PACKAGE)) {
            			System.out.println(line);
            			System.out.println();
            			createNLSImportStatement();
            			System.out.println();
            		}
            		else if (firstToken.equals(PUBLIC) && secondToken.equals(INTERFACE)) {
                		String className = tokenizer.nextToken();
            			System.out.println();
            			createClassSignature(className);
            			System.out.println();
            			createBundleNameDeclaration();
            			System.out.println();
            			createClassConstructor(className);
            			System.out.println();
            			createStaticInitializer(className);
            		}
            		else {
                		if (firstToken.equals(PUBLIC) && secondToken.equals(STATIC))
                    		createStaticDeclaration(tokenizer);
                		else
                			System.out.println(line);	//not interested in this line, no changes
            		}
        		}
        		else
                    System.out.println(line);	//not interested in this line, no changes
        	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createNLSImportStatement() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.setLength(0);
		stringBuffer.append(IMPORT);
		stringBuffer.append(" org.eclipse.osgi.util.NLS;");
		System.out.println(stringBuffer);
	}

	private void createStaticInitializer(String className) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.setLength(0);
		stringBuffer.append("	");
		stringBuffer.append(STATIC);
		stringBuffer.append(" {");
		System.out.println(stringBuffer);
		stringBuffer.setLength(0);
		stringBuffer.append("		NLS.initializeMessages(BUNDLE_NAME, ");
		stringBuffer.append(className);
		stringBuffer.append(".class);");
		System.out.println(stringBuffer);
		stringBuffer.setLength(0);
		stringBuffer.append("	}");
		System.out.println(stringBuffer);
	}

	private void createClassConstructor(String className) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.setLength(0);
		stringBuffer.append("	");
		stringBuffer.append(PRIVATE);
		stringBuffer.append(" ");
		stringBuffer.append(className);
		stringBuffer.append("() {");
		System.out.println(stringBuffer);
		stringBuffer.setLength(0);
		stringBuffer.append("		// Do not instantiate");
		System.out.println(stringBuffer);
		stringBuffer.setLength(0);
		stringBuffer.append("	}");
		System.out.println(stringBuffer);
	}

	private void createBundleNameDeclaration() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.setLength(0);
		stringBuffer.append("	");
		stringBuffer.append(PRIVATE);
		stringBuffer.append(" ");
		stringBuffer.append(STATIC);
		stringBuffer.append(" ");
		stringBuffer.append(FINAL);
		stringBuffer.append(" ");
		stringBuffer.append(STRING);
		stringBuffer.append(" ");
		stringBuffer.append(BUNDLE_NAME);
		stringBuffer.append(" = \"");
		stringBuffer.append(propertyFilePath);
		stringBuffer.append("\"; ");
		stringBuffer.append(NON_NLS);
		System.out.println(stringBuffer);
	}

	private String removeEverythingAfterEquals(String line) {
		int offset = line.indexOf('=');
		if (offset > 0)
			return line.substring(0, offset);
		else
			return line;
	}

	private void createStaticDeclaration(StringTokenizer tokenizer) {
		StringBuffer stringBuffer = new StringBuffer();
		tokenizer.nextToken();
		tokenizer.nextToken();
		String variableName = tokenizer.nextToken();
		stringBuffer.setLength(0);
		stringBuffer.append("	");
		stringBuffer.append(PUBLIC);
		stringBuffer.append(" ");
		stringBuffer.append(STATIC);
		stringBuffer.append(" ");
		stringBuffer.append(STRING);
		stringBuffer.append(" ");
		stringBuffer.append(variableName);
		stringBuffer.append(";");
		System.out.println(stringBuffer);
	}

	private void createClassSignature(String className) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.setLength(0);
		stringBuffer.append(PUBLIC);
		stringBuffer.append(" ");
		stringBuffer.append(CLASS);
		stringBuffer.append(" ");
		stringBuffer.append(className);
		stringBuffer.append(" ");
		stringBuffer.append(EXTENDS_NLS);
		stringBuffer.append(" {");
		System.out.println(stringBuffer);
	}

}
