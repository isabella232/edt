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

import org.eclipse.edt.compiler.internal.util.EGLMessage;

public class EGLSQLStructureItem {
	
	String name;
	String primitiveType;
	String length;
	String decimals;	
	String description;
	String columnName;
	String sqlDataCode;
	boolean isReadOnly;
	boolean isNullable;	
    boolean isSQLVar;
    List<EGLMessage> messages;

	public String getColumnName() {
		return columnName;
	}

	public String getDecimals() {
		return decimals;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isNullable() {
		return isNullable;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}
    
    public boolean isSQLVar() {
        return isSQLVar;
    }

	public String getLength() {
		return length;
	}

	public String getName() {
		return name;
	}

	public void setColumnName(String string) {
		columnName = string;
	}

	public void setDecimals(String string) {
		decimals = string;
	}

	public void setDescription(String string) {
		description = string;
	}

	public void setNullable(boolean b) {
		isNullable = b;
	}

	public void setReadOnly(boolean b) {
		isReadOnly = b;
	}
    
    public void setSQLVar(boolean b) {
        isSQLVar = b;
    }

	public void setLength(String string) {
		length = string;
	}

	public void setName(String string) {
		name = string;
	}

	public String getPrimitiveType() {
		return primitiveType;
	}

	public void setPrimitiveType(String string) {
		primitiveType = string;
	}

	public String getSqlDataCode() {
		return sqlDataCode;
	}

	public void setSqlDataCode(String string) {
		sqlDataCode = string;
	}

	/**
	 * @return Returns the messages.
	 */
	public List<EGLMessage> getMessages() {
		if (messages == null) {
			messages = new ArrayList<EGLMessage>();
		}
		return messages;
	}
	/**
	 * @param messages The messages to set.
	 */
	public void setMessages(List<EGLMessage> messages) {
		this.messages = messages;
	}
}
