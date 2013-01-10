/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
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
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tool to find unused messages and message constants for validation.
 */
public class FindUnusedMessagesAndConstants {
	public static void main(String[] args) throws Exception {
		BufferedReader br = null;
		Map<String, String> messages = new HashMap<String, String>();
		Map<String, String> prConstants = new HashMap<String, String>();
		Map<String, String> emConstants = new HashMap<String, String>();
		List<String> syntaxErrors = new ArrayList<String>();
		
		try {
			// Gather the messages in EGLValidationResources
			File file = new File("src/org/eclipse/edt/compiler/internal/core/builder/EGLValidationResources.properties");
			br = new BufferedReader(new FileReader(file));
			
			// For each message, check if a constant has been defined for it in IProblemRequestor.
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.startsWith("#") && line.contains("=")) {
					int start = 0;;
					int end = line.indexOf("=");
					String num = line.substring(start, end).trim();
					messages.put(num, line);
				}
			}
		}
		finally {
			if (br != null) {
				br.close();
				br = null;
			}
		}
		
		try {
			// Gather the constants defined in SyntaxError
			File file = new File("src/org/eclipse/edt/compiler/core/ast/SyntaxError.java");
			br = new BufferedReader(new FileReader(file));
			
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("public static final int ")) {
					int start = line.indexOf("=") + 1;
					int end = line.indexOf(";", start);
					String num = line.substring(start, end).trim();
					syntaxErrors.add(num);
				}
			}
		}
		finally {
			if (br != null) {
				br.close();
				br = null;
			}
		}
		
		try {
			// Gather the constants defined in IProblemRequestor
			File file = new File("src/org/eclipse/edt/compiler/internal/core/builder/IProblemRequestor.java");
			br = new BufferedReader(new FileReader(file));
			
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("public static final int ")) {
					int start = line.indexOf("=") + 1;
					int end = line.indexOf(";", start);
					String num = line.substring(start, end).trim();
					prConstants.put(num, line);
				}
			}
		}
		finally {
			if (br != null) {
				br.close();
				br = null;
			}
		}
		
		try {
			// Gather the constants defined in EGLMessage
			File file = new File("src/org/eclipse/edt/compiler/internal/util/EGLMessage.java");
			br = new BufferedReader(new FileReader(file));
			
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("public static final String ") && !line.startsWith( "public static final String EGLMESSAGE_GROUP_" )) {
					int start = line.indexOf("\"") + 1;
					int end = line.indexOf("\"", start);
					String num = line.substring(start, end).trim();
					emConstants.put(num, line);
				}
			}
		}
		finally {
			if (br != null) {
				br.close();
				br = null;
			}
		}
		
		boolean errorsReported = false;
		
		// For each message, check if a constant has been defined for it in IProblemRequestor
		List<Map.Entry<String, String>> msgsWithoutConstants = new ArrayList<Map.Entry<String,String>>();
		for (Map.Entry<String, String> entry : messages.entrySet()) {
			if (!prConstants.containsKey(entry.getKey()) && !emConstants.containsKey(entry.getKey()) && !syntaxErrors.contains(entry.getKey())) {
				msgsWithoutConstants.add(entry);
			}
		}
		if (msgsWithoutConstants.size() > 0) {
			Collections.sort(msgsWithoutConstants, new IDComparator());
			if (errorsReported) {
				System.out.println();
				System.out.println();
				System.out.println();
			}
			System.out.println("*** EGLValidationResources messages that have no corresponding constant in IProblemRequestor, EGLMessage, or SyntaxError (total=" + msgsWithoutConstants.size() + "): ***");
			for (Map.Entry<String, String> entry : msgsWithoutConstants) {
				System.out.println(entry.getValue());
			}
			errorsReported = true;
		}
		
		// Finally, report any constants that have no corresponding message in EGLValidationResources
		List<Map.Entry<String, String>> prConstantsWithoutMsgs = new ArrayList<Map.Entry<String,String>>();
		for (Map.Entry<String, String> entry : prConstants.entrySet()) {
			if (!messages.containsKey(entry.getKey())) {
				prConstantsWithoutMsgs.add(entry);
			}
		}
		if (prConstantsWithoutMsgs.size() > 0) {
			Collections.sort(prConstantsWithoutMsgs, new IDComparator());
			if (errorsReported) {
				System.out.println();
				System.out.println();
				System.out.println();
			}
			System.out.println("*** IProblemRequestor constants that have no corresponding message in EGLValidationResources (total=" + prConstantsWithoutMsgs.size() + "): ***");
			for (Map.Entry<String, String> entry : prConstantsWithoutMsgs) {
				System.out.println(entry.getValue());
			}
			errorsReported = true;
		}
		
		List<Map.Entry<String, String>> emConstantsWithoutMsgs = new ArrayList<Map.Entry<String,String>>();
		for (Map.Entry<String, String> entry : emConstants.entrySet()) {
			if (!messages.containsKey(entry.getKey())) {
				emConstantsWithoutMsgs.add(entry);
			}
		}
		if (emConstantsWithoutMsgs.size() > 0) {
			Collections.sort(emConstantsWithoutMsgs, new IDComparator());
			if (errorsReported) {
				System.out.println();
				System.out.println();
				System.out.println();
			}
			System.out.println("*** EGLMessage constants that have no corresponding message in EGLValidationResources (total=" + emConstantsWithoutMsgs.size() + "): ***");
			for (Map.Entry<String, String> entry : emConstantsWithoutMsgs) {
				System.out.println(entry.getValue());
			}
			errorsReported = true;
		}
	}
	
	private static class IDComparator implements Comparator {
		@Override
		public int compare(Object object1, Object object2) {
			if (object1 instanceof Map.Entry && object2 instanceof Map.Entry) {
				return compare(((Map.Entry)object1).getKey(), ((Map.Entry)object2).getKey());
			}
			if (object1 instanceof String && object2 instanceof String) {
				return ((String)object1).compareTo((String)object2);
			}
			return 0;
		}
	}
}
