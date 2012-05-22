/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof;

public interface EObject extends Cloneable {
	
	EClass getEClass();
	void setEClass(EClass type);
	
	Object eGet(EField field);
	void eSet(EField field, Object value);
	
	Object eGet(String name);
	void eSet(String name, Object value);
	
	Object eGet(Integer index);
	void eSet(Integer index, Object value);

	boolean isNull(EField field);
	
	void accept(EVisitor visitor);
	void visitChildren(EVisitor visitor);
	
	Object clone();
	
	String toStringHeader();
	
}
