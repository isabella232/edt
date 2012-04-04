/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.internal.sql.util;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class EGLSQLRetrieveResults {
	private static EGLSQLRetrieveResults INSTANCE = new EGLSQLRetrieveResults();
	List<EGLSQLStructureItem> structureItems;
	List<String> messages;
	List<String> keys;
	boolean retrieveFailed;
	boolean hasColumnsDefinedWithDecimals;
	int maxPrimitiveTypeInfoLength = 0;			

	public EGLSQLRetrieveResults() {
		super();
		structureItems = new ArrayList<EGLSQLStructureItem>();
		messages = new ArrayList<String>();
		keys = new ArrayList<String>();
		retrieveFailed = false;
	}

	public static EGLSQLRetrieveResults getInstance() {
		return INSTANCE;
	}
	
	public void clearMessages() {
		messages.clear();
	}
	
	public List<String> getMessages() {
		return messages;
	}

	public List<EGLSQLStructureItem> getStructureItems() {
		return structureItems;
	}

	public boolean isRetrieveFailed() {
		return retrieveFailed;
	}

	public void setRetrieveFailed(boolean retrieveFailed) {
		this.retrieveFailed = retrieveFailed;
	}

	public List<String> getKeys() {
		return keys;
	}

	public int getMaxPrimitiveTypeInfoLength() {
		return maxPrimitiveTypeInfoLength;
	}

	public void setMaxPrimitiveTypeInfoLength(int i) {
		maxPrimitiveTypeInfoLength = i;
	}

	public boolean hasColumnsDefinedWithDecimals() {
		return hasColumnsDefinedWithDecimals;
	}

	public void setHasColumnsDefinedWithDecimals(boolean b) {
		hasColumnsDefinedWithDecimals = b;
	}
}
