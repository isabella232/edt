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
package org.eclipse.edt.ide.rui.document.utils;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;

import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.server.PropertyValue;

public class DocumentCache {
	
	private static final String WIDGET_PROCESSED_FLAG = "ProcessedFlag";
	private static final String LAYOUT_PROCESSED_FLAG = "Layout-ProcessedFlag";

	private HashMap			CACHE 						= new HashMap();
	private IEGLDocument	_currentDocument			= null;
	private IFile			_currentFile				= null;
	
	public DocumentCache( IEGLDocument currentDocument,  IFile currentFile ) {
		this._currentDocument = currentDocument;
		this._currentFile = currentFile;
	}
	
	public void clear() {
		CACHE.clear();
	}

	public PropertyValue getPropertyValue( int iStatementOffset, int iStatementLength, String strPropertyName, String strPropertyType ) {
		String key = iStatementOffset + "-" + iStatementLength + WIDGET_PROCESSED_FLAG;
		String propertyKey = iStatementOffset + "-" + iStatementLength + "-" + strPropertyName.toLowerCase();
		if ( CACHE.containsKey(key)) {
			Object propertyObject = CACHE.get(propertyKey);
			if ( propertyObject instanceof PropertyValue ) {
				return (PropertyValue)propertyObject;
			} else if ( propertyObject instanceof Expression ) {
				PropertyValueVisitor visitor = new PropertyValueVisitor(strPropertyType);
				visitor.processExpression((Expression)propertyObject);
				PropertyValue propertyValue = new PropertyValue(visitor.getResult(), visitor.propertyTypeMatch);
				CACHE.put( propertyKey, propertyValue );
				return propertyValue;
			}
			return null;
		}
		GetPropertiesValuesOperation operation = new GetPropertiesValuesOperation( _currentDocument, _currentFile );
		operation.visitProperties( iStatementOffset, iStatementLength );
		List assignments = operation.getAllProperties();
		if ( assignments == null ) {
			return null;
		}

		for ( int i = 0; i < assignments.size(); i ++ ) {
			CACHE.put(iStatementOffset + "-" + iStatementLength + "-" + getIdentifier(((Assignment)assignments.get(i)).getLeftHandSide()), ((Assignment)assignments.get(i)).getRightHandSide());
		}
		CACHE.put( key, WIDGET_PROCESSED_FLAG );
		return getPropertyValue(iStatementOffset, iStatementLength, strPropertyName, strPropertyType);
	}
	
	public PropertyValue getLayoutPropertyValue( int iStatementOffset, int iStatementLength, String strPropertyName, String strPropertyType ) {
		String key = iStatementOffset + "-" + iStatementLength + LAYOUT_PROCESSED_FLAG;
		String propertyKey = iStatementOffset + "-" + iStatementLength + LAYOUT_PROCESSED_FLAG + strPropertyName.toLowerCase();
		if ( CACHE.containsKey(key)) {
			Object propertyObject = CACHE.get(propertyKey);
			if ( propertyObject instanceof PropertyValue ) {
				return (PropertyValue)propertyObject;
			} else if ( propertyObject instanceof Expression ) {
				PropertyValueVisitor visitor = new PropertyValueVisitor(strPropertyType);
				visitor.processExpression((Expression)propertyObject);
				PropertyValue propertyValue = new PropertyValue(visitor.getResult(), visitor.propertyTypeMatch);
				CACHE.put( propertyKey, propertyValue );
				return propertyValue;
			}
			return null;
		}
		GetLayoutPropertiesValuesOperation operation = new GetLayoutPropertiesValuesOperation( _currentDocument, _currentFile );
		operation.visitProperties( iStatementOffset, iStatementLength );
		List assignments = operation.getAllProperties();
		if ( assignments == null ) {
			return null;
		}
			
		for ( int i = 0; i < assignments.size(); i ++ ) {
			CACHE.put(iStatementOffset + "-" + iStatementLength + LAYOUT_PROCESSED_FLAG + getIdentifier(((Assignment)assignments.get(i)).getLeftHandSide()), ((Assignment)assignments.get(i)).getRightHandSide());
		}
		CACHE.put( key, LAYOUT_PROCESSED_FLAG );
		return getLayoutPropertyValue(iStatementOffset, iStatementLength, strPropertyName, strPropertyType);
	}
	
	private String getIdentifier( Expression expression ) {
		if ( expression instanceof SimpleName ) {
			return ((SimpleName)expression).getIdentifier().toLowerCase();
	    }else if ( expression instanceof QualifiedName ) {
	    	return ((QualifiedName)expression).getIdentifier().toLowerCase();
		}
		return "";
	}
}
