/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart;

import java.io.Serializable;

/**
 * For each setError() call, an ErrorEntry object is created 
 * and stored in a vector, _errorEntries, in the RunUnit.
 * 
 * The ErrorEntry is converted to an error message associated
 * with either a specific or global page field when the
 * next page is displayed.  
 * 
 */
public class ErrorEntry implements Serializable
{
	private static final long serialVersionUID = 70L;
	
	/**
	 * The _itemFmtName field could now be an item formatted 
	 * name or a component id depending on the setting of
	 * _nameIsCompId
	 */
	String _itemFmtName;
	String _msgRscId;
	String _msgKey;
	String[] _inserts;
	String _msgText;
	boolean _nameIsCompId;
	int _type;
	
	public ErrorEntry( String cid, String msg, boolean isCompId )
	{ 
		_itemFmtName = cid;
		_msgText = msg;
		_type = 1;
		_nameIsCompId = isCompId;
	}
	public ErrorEntry( String cid, String rscID, String key, String[] ins, boolean isCompId )
	{
		_itemFmtName = cid;
		_msgRscId = rscID;
		_msgKey = key;
		_inserts = ins;
		_type = 2;
		_nameIsCompId = isCompId;
	}
	
	
	public String getName()
	{
		return _itemFmtName;
	}
	
	public String getMsgRscId()
	{
		return _msgRscId;
	}
	
	public String getMsgKey()
	{
		return _msgKey;
	}
	
	public String[] getMsgInserts()
	{
		return _inserts;
	}
	
	public String getMsgText()
	{
		return _msgText;
	}
	
	public boolean isGlobalError()
	{
		return ( _type == 1 );
	}
	
	public boolean nameIsComponentId()
	{
		return _nameIsCompId;
	}
}
