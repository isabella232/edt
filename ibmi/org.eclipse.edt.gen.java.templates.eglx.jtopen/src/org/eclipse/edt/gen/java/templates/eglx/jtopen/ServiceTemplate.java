/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.eglx.jtopen;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Service;

public class ServiceTemplate extends org.eclipse.edt.gen.java.templates.ServiceTemplate implements Constants{

	static final String PART_IS_IBMI_SERVICE = "key_PartIsIbmiService";
	public void preGenFunction(EGLClass part, Context ctx, Function arg) {
		if(!ctx.containsKey(PART_IS_IBMI_SERVICE)){
			Annotation annot = arg.getAnnotation(signature_IBMiProgram);
			if(annot != null){
				ctx.put(PART_IS_IBMI_SERVICE, annot);
			}
		}
		ctx.invokeSuper(this, preGenFunction, part, ctx, arg);
	}

	public void genImports(Service service, Context ctx, TabbedWriter out) {
		if(ctx.containsKey(PART_IS_IBMI_SERVICE)){			
			out.println("import eglx.lang.SysLib;");
			out.println("import com.ibm.as400.access.*;");
			out.println("import java.beans.PropertyVetoException;");
			out.println("import java.io.IOException;");
			out.println("import org.eclipse.edt.java.jtopen.*;");
			out.println("import org.eclipse.edt.javart.json.Json;");
		}
		super.genImports(service, ctx, out);
	}

	public void genSuperClass(Service service, Context ctx, TabbedWriter out, Function arg) {
		if(ctx.containsKey(PART_IS_IBMI_SERVICE)){
			out.print("IBMiProgramCall");
		}
		else{
			super.genFunction(service, ctx, out, arg);
		}
	}
	
	public void genSuperClass(Service service, Context ctx, TabbedWriter out) {
		if(ctx.containsKey(PART_IS_IBMI_SERVICE)){
			out.print("IBMiProgramCall");
		}
		else{
			super.genSuperClass(service, ctx, out);
		}
	}
}
