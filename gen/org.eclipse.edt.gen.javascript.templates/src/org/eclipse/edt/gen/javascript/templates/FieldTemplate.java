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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public class FieldTemplate extends JavaScriptTemplate {

	public void validate(Field field, Context ctx) {
		ctx.invoke(validate, field.getType(), ctx);
	}

	public void genDeclaration(Field field, Context ctx, TabbedWriter out) {
		// process the field
		ctx.invokeSuper(this, genDeclaration, field, ctx, out);
		ctx.invoke(genRuntimeTypeName, field, ctx, out, TypeNameKind.JavascriptPrimitive);
		out.print(" ");
		ctx.invoke(genName, field, ctx, out);
		out.println(";");
	}

	public void genQualifier(Field field, Context ctx, TabbedWriter out) {
		if (field.getContainer() != null && field.getContainer() instanceof Type)
			ctx.invoke(genQualifier, field.getContainer(), ctx, out, field);
	}

	public void genInstantiation(Field field, Context ctx, TabbedWriter out) {
		ctx.invoke(genInstantiation, field.getType(), ctx, out, field);
	}

	public void genInitialization(Field field, Context ctx, TabbedWriter out) {
		// is this an inout or out temporary variable to a function. if so, then we need to default or instantiate for
		// our parms, and set to null for inout
		if (ctx.getAttribute(field, org.eclipse.edt.gen.Constants.Annotation_functionArgumentTemporaryVariable) != null
			&& ctx.getAttribute(field, org.eclipse.edt.gen.Constants.Annotation_functionArgumentTemporaryVariable) != ParameterKind.PARM_IN) {
			// if the value associated with the temporary variable is 2, then it is to be instantiated (OUT parm)
			// otherwise it is to be defaulted to null (INOUT parm), as there is an assignment already created
			if (ctx.getAttribute(field, org.eclipse.edt.gen.Constants.Annotation_functionArgumentTemporaryVariable) == ParameterKind.PARM_OUT) {
				out.print(Constants.JSRT_EGL_NAMESPACE+ctx.getNativeMapping("egl.lang.AnyObject")+".ezeWrap(");
				if (ctx.mapsToNativeType(field.getType()) || ctx.mapsToPrimitiveType(field.getType()))
					ctx.invoke(genDefaultValue, field.getType(), ctx, out, field);
				else
					ctx.invoke(genInstantiation, field.getType(), ctx, out, field);
				out.print(")");
			} else
				out.print("null");
		} else {
			if (field.isNullable() || TypeUtils.isReferenceType(field.getType()))
				ctx.invoke(genDefaultValue, field.getType(), ctx, out, field);
			else if (ctx.mapsToNativeType(field.getType()) || ctx.mapsToPrimitiveType(field.getType()))
				ctx.invoke(genDefaultValue, field.getType(), ctx, out, field);
			else
				ctx.invoke(genInstantiation, field.getType(), ctx, out, field);
		}
	}

	public void genGetter(Field field, Context ctx, TabbedWriter out) {
		out.print(quoted(genGetterSetterFunctionName("get", field)));
		out.println(": function() {");
		out.print("return ");
		ctx.invoke(genName, field, ctx, out);
		out.println(";");
		out.println("}");
	}

	static String genGetterSetterFunctionName(String prefix, Field field){
		StringBuilder name = new StringBuilder(prefix);
		name.append(field.getName().substring(0, 1).toUpperCase());
		if (field.getName().length() > 1)
			name.append(field.getName().substring(1));
		return name.toString();
	}
	
	public void genSetter(Field field, Context ctx, TabbedWriter out) {
		out.print(quoted(genGetterSetterFunctionName("set", field)));
		out.println(": function(ezeValue) {");
		out.print("this.");
		ctx.invoke(genName, field, ctx, out);
		out.println(" = ezeValue;");
		out.println("}");
	}
	
	public void genXmlField(Field field, Context ctx, TabbedWriter out, Integer arg){
		//create the XMLAnnotationMap
		out.println("xmlAnnotations = {};");
		//add attribute or element
		String xmlNamespace = getNamespace(field);
		String xmlName = getXMLName(field);
		out.print("xmlAnnotations[\"XMLStyle\"] = ");
		if(xmlName == null || xmlName.length() == 0){
			xmlName = field.getId();
		}
		boolean isAttribute = isAttribute(field);
		if(isAttribute){
			out.print("new egl.egl.core.xml.XMLAttribute(");
		}
		else {
			out.print("new egl.egl.core.xml.XMLElement(");
		}
		out.print("\"" + xmlName + "\", ");
		out.print(xmlNamespace == null ? "null" : "\"" + xmlNamespace + "\"");
		if(!isAttribute){
			out.print(", " + Boolean.toString(isXMLNillable(field)));
		}
		out.println(");");
		
		//add xmlschema type
		String xmlSchemaType = getXmlSchemaType(field);
		if(xmlSchemaType != null){
			out.println("xmlAnnotations[\"XMLSchemaType\"] = new egl.egl.core.xml.XMLSchemaType(\"" + xmlSchemaType + "\");");
		}


		Annotation xmlArray = field.getAnnotation("egl.core.XMLArray");
		if(xmlArray != null){
			out.print("xmlAnnotations[\"XMLArray\"] = new egl.egl.core.xml.XMLArray(");
			out.print(xmlArray.getValue("wrapped") == null ? "true, " : (((Boolean)xmlArray.getValue("wrapped")).toString() + ", "));
			String[] elementNames = (String[])xmlArray.getValue("names");
			if(elementNames != null && elementNames.length > 0){
				out.print("[");
				boolean addComma = false;
				for(String elementName : elementNames){
					if(addComma){
						out.print(", ");
					}
					out.print(quoted(elementName));
				}
				out.print("]");
			}
			else{
				out.print("null");
			}
			out.println(");");
		}
		out.print("fields[" + arg.toString() + "] =");
		out.print("new egl.egl.core.xml.XMLFieldInfo(");
		Annotation annot = field.getAnnotation("egl.idl.java.JavaProperty");
		if(annot != null){
			out.print(FieldTemplate.genGetterSetterFunctionName("get", field));
			out.print(", ");
			out.print(FieldTemplate.genGetterSetterFunctionName("set", field));
			out.print(", \"");
		}
		else{
			out.print("\"");
			ctx.invoke(genName, field, ctx, out);
			out.print("\", ");
			out.print("\"");
			ctx.invoke(genName, field, ctx, out);
			out.print("\", \"");
		}
		ctx.invoke(genSignature, field.getType(), ctx, out);
		out.print("\", ");
		ctx.invoke(genRuntimeTypeName, field.getType(), ctx, out, TypeNameKind.JavascriptImplementation);
		out.println(", xmlAnnotations);");
	}
	public static String getXMLName(Field field)
	{
		String name = field.getId();
		Annotation annot;
		if (isAttribute(field)) {
			annot = field.getAnnotation("egl.core.xmlAttribute");
			if (annot != null) {
				String xmlName = (String) annot.getValue("name");
				if (xmlName != null && ((String)xmlName).length() > 0)
					name = xmlName;
			}
		}
		else
		{
			annot = field.getAnnotation("egl.core.xmlElement");
			if (annot != null) {
				String xmlName = (String) annot.getValue("name");
				if (xmlName != null && ((String)xmlName).length() > 0)
					name = xmlName;
			}
		}
		
		return name;
	}
	static boolean isAttribute(Field field) {
		Annotation annot = field.getAnnotation("egl.core.xmlAttribute");
		if (annot != null)
			return true;
		
		return false;
	}
	
	static boolean isElement(Field field) {
		if (isAttribute(field))
			return false;
		
		Annotation annot = field.getAnnotation("egl.core.xmlElement");
		if (annot != null)
			return true;
		
		//Fields in a record default to xmlElement unless the xmlStructure of the record 
		// is simpleContent.  Then the field is either xmlAttribute (must have annotation)
		// or it is a value node.
		annot = field.getContainer().getAnnotation("egl.core.xmlStructure");
		if (annot != null) 
		{
			FieldAccess structure = (FieldAccess) annot.getValue();
			if (structure != null && structure.getID().equalsIgnoreCase("simpleContent")) 
			{
				return false;
			}
			else 
			{
				return true;
			}
		}
		else 
		{
			return true;
		}
	}
	
	private boolean isXMLNillable(Field field)
	{
		Annotation annot = field.getAnnotation("egl.core.xmlElement");
		if (annot != null) {
			Boolean isNillable = (Boolean) annot.getValue("nillable");
			return (isNillable != null && isNillable.booleanValue());
		}
		
		return false;
	}
	public static String getXmlSchemaType(Field field)
	{
		String xmlSchemaType = null;
		Annotation annot = field.getAnnotation("egl.core.XMLSchemaType");
		if (annot != null) 
		{
			if (annot.getValue("name") != null && 
					((String)annot.getValue("name")).length() > 0)

			{
				xmlSchemaType = (String) annot.getValue("name");
			}
		} 
		return xmlSchemaType;
	}
	public static String getNamespace(Field field)
	{
		String namespace = null;
		Annotation annot = field.getAnnotation("egl.core.xmlAttribute");
		if (annot != null) 
		{
			if (annot.getValue("namespace") != null && 
					((String)annot.getValue("namespace")).length() > 0)

			{
				namespace = (String) annot.getValue("namespace");
			}
		} 
		else
		{
			annot = field.getAnnotation("egl.core.xmlElement");
			if (annot != null && 
					annot.getValue("namespace") != null && 
					((String)annot.getValue("namespace")).length() > 0)
			{
				namespace = (String) annot.getValue("namespace");
			}
		}
		return namespace;
	}

}
