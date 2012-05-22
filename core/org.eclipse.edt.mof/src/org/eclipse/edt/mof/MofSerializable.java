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


/**
 * Marker Interface that determines instances of the marked type to be serialized
 * as references using an arbitrary Signature supplied by the marked type.
 * This key is used to lookup the referenced type from a configured <code>IEnvironment</code>
 * 
 * @see IEnvironment, LookupDelegate
 */
public interface MofSerializable extends EObject {
	String getMofSerializationKey();
}
