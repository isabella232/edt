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
package org.eclipse.edt.ide.core.internal.model;

import org.eclipse.edt.ide.core.model.Signature;

/**
 * Element info for IField elements.
 */

public class SourceFieldElementInfo extends MemberElementInfo {

/**
 * The type name of this field.
 */
protected char[] typeName;

protected boolean hasOccurs;

/**
 * in which package the field type is declared.
 * null if the type is primitive
 */
protected char[] typeDeclaredPackage;
	
/**
 * Returns the type name of the field.
 */
public char[] getTypeName() {
	return this.typeName;
}
/**
 * Returns the type signature of the field.
 *
 * @see Signature
 */
protected String getTypeSignature() {
	return Signature.createTypeSignature(this.typeName, false);
}

/**
 * Sets the type name of the field.
 */
protected void setTypeName(char[] typeName) {
	this.typeName = typeName;
}
	
/**
 * @return
 */
public boolean hasOccurs() {
	return hasOccurs;
}

/**
 * @param b
 */
public void setHasOccurs(boolean b) {
	hasOccurs = b;
}
public char[] getTypeDeclaredPackage() {
	return typeDeclaredPackage;
}
public void setTypeDeclaredPackage(char[] typeDeclaredPackage) {
	this.typeDeclaredPackage = typeDeclaredPackage;
}


}
