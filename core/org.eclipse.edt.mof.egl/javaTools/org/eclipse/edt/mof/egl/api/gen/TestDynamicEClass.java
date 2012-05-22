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
package org.eclipse.edt.mof.egl.api.gen;

import java.io.File;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.lookup.PartEnvironment;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.FileSystemObjectStore;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.eclipse.edt.mof.serialization.ObjectStore;


public class TestDynamicEClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File root;
		if (args.length > 0) {
			root = new File(args[0]);
		}
		else {
			root = new File("d:/workspaces/EGL_CE/org.eclipse.edt.mof.egl/EGL_MOF_OUTPUT");
		}
		ObjectStore typeStore = new FileSystemObjectStore(root, PartEnvironment.getCurrentEnv(), "XML");
		PartEnvironment.getCurrentEnv().registerObjectStore(IEnvironment.DefaultScheme, typeStore);
		typeStore = new FileSystemObjectStore(root, PartEnvironment.getCurrentEnv(), "XML");
		PartEnvironment.getCurrentEnv().registerObjectStore(Type.EGL_KeyScheme, typeStore);
		
		Annotation dyn = IrFactory.INSTANCE.createAnnotation("Location");
		dyn.setValue("abc");
		dyn.setValue("xyz", 55);
		dyn.setValue("list", new Object[]{"abc", 1});
		
		try {
			EObject eClass = Environment.INSTANCE.find("egl:egl.lang.eglstring()");
			System.out.println(eClass.getEClass().getName());
		} catch (MofObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeserializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
