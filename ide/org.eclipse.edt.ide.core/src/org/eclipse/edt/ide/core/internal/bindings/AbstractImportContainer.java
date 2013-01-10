/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.bindings;


/**
 * An import container is used to hold an import statement and split it
 * up into its file and folder if necessary.
 * Creation date: (9/5/2001 8:45:42 AM)
 * @author: Brian Svihovec
 */
public abstract class AbstractImportContainer {
	
		/***
		 * fullImport - the original import statement
		 */
		private String fullImport = ""; //$NON-NLS-1$

		/***
		 * file - the file name and extension from the import
		 */
		private String file = null;

		/***
		 * folder - the folder from the import'
		 */
		private String folder = null;

		/***
		 * tokens - the tokens to use when splitting up the file and folder
		 */
		private static final char[] tokens = { '\\', '/' };
		/**
		 * ImportContainer constructor comment.
		 */
		public AbstractImportContainer(String importStatement) {
			super();

			fullImport = importStatement;
		}
		/**
		 * Insert the method's description here.
		 * Creation date: (9/5/2001 9:10:06 AM)
		 * @return java.lang.String
		 */
		public java.lang.String getFullImportPath() {

			return fullImport;
		}
		/**
		 * Insert the method's description here.
		 * Creation date: (9/5/2001 9:10:06 AM)
		 * @return java.lang.String
		 */
		public java.lang.String getFile() {

			if (file == null) {
				if (fullImport != null) {
					tokenize(fullImport);
				} else {
					file = ""; //$NON-NLS-1$
				}
			}

			return file;
		}
		/**
		 * Insert the method's description here.
		 * Creation date: (9/5/2001 9:10:06 AM)
		 * @return java.lang.String
		 */
		public java.lang.String getFolder() {

			if (folder == null) {
				if (fullImport != null) {
					// tokenize the full import to get the file name
					tokenize(fullImport);
				} else {
					folder = ""; //$NON-NLS-1$
				}
			}

			return folder;
		}
		/**
		 * Given an import statment
		 * Creation date: (9/5/2001 9:48:30 AM)
		 * @param importStatement java.lang.String
		 */
		private void tokenize(String importStatement) {

			// tokenize the full import to get the file name
			int delimiter = -1;

			for (int i = 0; i < tokens.length; i++) {
				delimiter = importStatement.lastIndexOf(tokens[i]);

				if (delimiter != -1) {
					break;
				}
			}

			if (delimiter != -1) {
				folder = importStatement.substring(0, delimiter);
				file = importStatement.substring(delimiter + 1, fullImport.length());
			} else {
				folder = getDefaultFolderName();
				file = new String(fullImport);
			}
		}
		
		/**
		 * Given the specific implementation of this import container,
		 * the string used to represent a default folder might be different.
		 * This method allows the implementer to define this string.
		 */
		public abstract String getDefaultFolderName();
}
