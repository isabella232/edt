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
package org.eclipse.edt.mof.utils;

import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.TypeNotFoundException;



public class Utils {
	
	public static final MofFactory azure = MofFactory.INSTANCE;
	/**
	 * Returns the package name from a type signature
	 * @param typeSignature
	 * @return
	 */
	public static String getPackageName(String typeSignature) {
		int i = typeSignature.lastIndexOf('.');
		if (i != 0) {
			return typeSignature.substring(0, i);
		} else {
			return "";
		}
	}
	
	public static EObject getETypeFromSignature(String typeSignature, Environment env) throws TypeNotFoundException, DeserializationException {
		int i, j = 0;
		String baseType;
		String[] typeArgs = null;
		i = typeSignature.lastIndexOf('<');	
		j = typeSignature.lastIndexOf('(');
		if (i == -1) {
			if (j == -1) {
				return env.findType(typeSignature);
			}
			else {
				baseType = typeSignature.substring(0, j);
			}
		}
		else {
			baseType = typeSignature.substring(0, i);
			if (j == -1) j = typeSignature.length();
			typeArgs = typeSignature.substring(i+1, j-1).split("[<,>]");
		}
		// If we get here we know we have a generic type signature
		EClassifier eBaseType = (EClassifier)env.findType(baseType);
		EGenericType generic;
		generic = azure.createEGenericType(true);
		generic.setEClassifier(eBaseType);
		if (typeArgs != null) {
			for (String arg : typeArgs) {
				EType eType = (EType)getETypeFromSignature(arg, env);
				generic.addETypeArgument(eType);
			}
		}
		return generic;
	}
	
}
