/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package resources.edt.binding;

import org.eclipse.edt.javart.resources.egldd.Binding;

public class DedicatedFactory extends BindingFactory {

	@Override
	public Object createResource(Binding binding) throws Exception {
		//TODO this class doesn't yet exist in the Java runtime.
//		return new HttpProxy();
		return null;
	}
	
}
