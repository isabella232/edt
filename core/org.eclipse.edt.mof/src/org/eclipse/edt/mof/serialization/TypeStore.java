/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.serialization;

import org.eclipse.edt.mof.EClassifier;

/**
 * @author twilson
 *
 */
public interface TypeStore extends ObjectStore {
	public static final String KeyScheme = "mof";
		
	public EClassifier getEType(String typeSignature) throws DeserializationException; 
	
	public void putEType(EClassifier type) throws SerializationException;
	
	public void removeEType(String typeSignature);		
}
