/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.egl2mof;

import java.io.File;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.compiler.Context;
import org.eclipse.edt.mof.egl.compiler.EGL2IREnvironment;
import org.eclipse.edt.mof.serialization.TypeStore;


public class Egl2Mof extends Egl2MofExpression {
		
 	public Egl2Mof(EGL2IREnvironment env) {
		super(env);
	}

 	public Egl2Mof(EGL2IREnvironment env, TypeStore store) {
		super(env);
		env.setDefaultSerializeStore(Type.EGL_KeyScheme, store);
	}
	
	public EObject convert(org.eclipse.edt.compiler.core.ast.Part part, Context context) {
		this.context = context;
		part.accept(this);
		EObject mofPart = stack.pop();
		// If the EClassifier has name different than the original Part
		// due to conflicts with EGL keywords for instance, save
		// the result at the original typeSignature as well
		if (mofPart instanceof EClassifier) {
			EClassifier ePart = (EClassifier)mofPart;
			IPartBinding binding = (IPartBinding)part.getName().resolveBinding();
			if (!binding.getName().equalsIgnoreCase(ePart.getName())) {
				env.save(mofSerializationKeyFor(binding), ePart, false);
			}
		}
		return mofPart;
	}
		
}
