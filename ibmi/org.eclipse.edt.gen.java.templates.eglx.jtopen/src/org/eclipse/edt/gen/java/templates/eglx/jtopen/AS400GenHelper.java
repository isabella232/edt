/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.eglx.jtopen;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Type;

public class AS400GenHelper {
	static AS400GenHelper INSTANCE = new AS400GenHelper();

	static void genHelperClassName(LogicAndDataPart part, TabbedWriter out){
		out.print(Constants.HELPER_PREFIX);
		out.print(org.eclipse.edt.gen.java.CommonUtilities.classAlias(part));
		out.print(Constants.HELPER_SUFFIX);
	}

	public void genHelperClass(Type type, Context ctx, TabbedWriter out){
		if (type instanceof Record){
			genHelperClass((LogicAndDataPart)type, ctx, out);
		}
		else if (type instanceof Handler){
			genHelperClass((LogicAndDataPart)type, ctx, out);
		}
		else if (type instanceof ArrayType){
			genHelperClass(((ArrayType)type).getElementType(), ctx, out);
		}
	}
	public void genHelperClass(LogicAndDataPart part, Context ctx, TabbedWriter out){
		if(CommonUtilities.getGeneratedHelpers(ctx).add(part.getTypeSignature())){
			out.println();
			out.print("protected class ");
			genHelperClassName(part, out);
			out.println("{");
			
			//getAS400Structure
			out.print("protected com.ibm.as400.access.AS400Structure getAS400Structure(eglx.jtopen.IBMiConnection ");
			out.print(Constants.as400ConnectionName);
			out.println("){");
			out.println("com.ibm.as400.access.AS400Structure ezeStructure = new com.ibm.as400.access.AS400Structure();");
			out.println("java.util.List<com.ibm.as400.access.AS400DataType> fields = new java.util.ArrayList<com.ibm.as400.access.AS400DataType>();");
			//assign fields
		    //CUSTNO string{@AS400Text {length = 7}} = "a1";//char(7);
			//fields.add(new AS400Text(7, ezeConn));
			for (Field field : part.getFields()) {
				out.print("fields.add(");
				AS400GenType.INSTANCE.genAS400Type(field,  field.getType(), ctx, out);
				out.println(");");
			}
			out.println("ezeStructure.setMembers(fields.toArray(new com.ibm.as400.access.AS400DataType[fields.size()]));");
			out.println("return ezeStructure;");
			out.println("}");
	
			out.println("}");
			
			//generate helpers for the part's fields
			for(Field field : part.getFields()){
				genHelperClass(field.getType(), ctx, out);
			}
		}
	}
}
