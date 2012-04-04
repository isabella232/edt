/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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


import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.runtime.IPath;
/**
 * This abstract class provides features often used when resolving imports
 * in the workspace.
 *
 * Creation date: (9/4/2001 2:00:57 PM)
 * @author: Brian Svihovec
 */
public abstract class AbstractImportResolver {

	/***
	 * resolvedImports - the resolved imports, stored in the correct order
	 */
	private ArrayList resolvedImports = new ArrayList();

	/**
	 * unresolvedImports
	 */
	private ArrayList unresolvedImports = new ArrayList();

	/***
	 * hashedImports - the resolved imports, stored in a hash table
	 */
	private HashSet hashedImports = new HashSet();

	/***
	 * importContainers - the imports to use to resolve the file handles
	 */
	private ArrayList importContainers = new ArrayList();

	/***
	 * ALL_CHAR - the character to use for wildcards
	 */
	public static final char ALL_CHAR = '*';

	/***
	 * ANY_CHAR - the character to use for wildcards
	 */
	public static final char ANY_CHAR = '#';

	/**
	 * Constructor for AbstractImportResolver.
	 */
	public AbstractImportResolver(String[] imports) {
		super();
		
		if(imports != null)
		{
			buildImportList(imports);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/6/2001 9:55:10 AM)
	 * @param imports java.lang.String[]
	 */
	private void buildImportList(String[] imports) {

		AbstractImportContainer temp = null;
		
		// put all of the import statements in an array list
		for (int i = 0; i < imports.length; i++) {
			temp = getNewImportContainer(imports[i]);
			getImportContainers().add(temp);
		}

	}
	
	protected abstract AbstractImportContainer getNewImportContainer(String importString);
	
	/**
	 * Insert the method's description here.
	 * Creation date: (9/5/2001 5:07:14 PM)
	 * @return boolean
	 * @param fileName java.lang.String
	 */
	protected boolean importHasWildcard(AbstractImportContainer importContainer) {

		boolean hasWildcard = false;

		if(importContainer != null)
		{
			String fileName = importContainer.getFile();
			
			// remove the import statement only if it doesn't have a * or # in it.
			if (fileName.indexOf(ANY_CHAR) != -1 || fileName.indexOf(ALL_CHAR) != -1) 
			{
				hasWildcard = true;
			}
		}

		return hasWildcard;
	}
	
	/**
	 * Returns the hashedImports.
	 * @return HashSet
	 */
	public HashSet getHashedImports() {
		return hashedImports;
	}

	/**
	 * Returns the importContainers.
	 * @return ArrayList
	 */
	public ArrayList getImportContainers() {
		return importContainers;
	}

	/**
	 * Returns the resolvedImports.
	 * @return ArrayList
	 */
	public ArrayList getResolvedImports() {
		return resolvedImports;
	}

	/**
	 * Returns the unresolvedImports.
	 * @return ArrayList
	 */
	public ArrayList getUnresolvedImports() {
		return unresolvedImports;
	}

	public void reset()
	{
		getResolvedImports().clear();
		getHashedImports().clear();
	}
	
	/**
	 * Sets the unresolvedImports.
	 * @param unresolvedImports The unresolvedImports to set
	 */
	public void setUnresolvedImports(ArrayList unresolvedImports) {
		this.unresolvedImports = unresolvedImports;
	}
	
	/**
	 * Replace all occurrences of '\' with '/'.
	 * FolderHandles are stored using '/', but our imports allow
	 * Creation date: (11/15/2001 2:41:32 PM)
	 * @return java.lang.String
	 * @param folder java.lang.String
	 */
	protected String fixFolderSeparators(String folder) {
	
		String newFolder = folder;
		
		newFolder = newFolder.replace('\\', IPath.SEPARATOR);
	
		return newFolder;
	}
}
