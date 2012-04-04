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
package org.eclipse.edt.ide.core.model;

/**
  */
public class PPListElementAttribute {
	
	public static final String K_SOURCEATTACHMENT= "sourcepath"; //$NON-NLS-1$
	public static final String K_SOURCEATTACHMENTROOT= "rootpath"; //$NON-NLS-1$
	public static final String K_JAVADOC= "javadoc"; //$NON-NLS-1$
	public static final String K_OUTPUT= "output"; //$NON-NLS-1$
	public static final String K_EXCLUSION= "exclusion"; //$NON-NLS-1$
	
	private PPListElement fParent;
	private String fKey;
	private Object fValue;
	
	public PPListElementAttribute(PPListElement parent, String key, Object value) {
		fKey= key;
		fValue= value;
		fParent= parent;
	}
	
	public PPListElement getParent() {
		return fParent;
	}

	/**
	 * Returns the key.
	 * @return String
	 */
	public String getKey() {
		return fKey;
	}

	/**
	 * Returns the value.
	 * @return Object
	 */
	public Object getValue() {
		return fValue;
	}
	
	/**
	 * Returns the value.
	 * @return Object
	 */
	public void setValue(Object value) {
		fValue= value;
	}	

}
