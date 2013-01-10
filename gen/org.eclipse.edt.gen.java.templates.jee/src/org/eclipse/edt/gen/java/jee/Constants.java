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
package org.eclipse.edt.gen.java.jee;

public interface Constants {
	// these are sub key values used as context hashmap keys
	public static final String SubKey_fieldsProcessed4Resource = "fieldsProcessed4Resource";
	public static final String SubKey_keepAnnotationsOnTheSameLine = "keepAnnotationsOnTheSameLine";
	public static final String SubKey_MemberAnnotations = "SubKey_MemberAnnotations";

	// these are sub key values used on annotations 

	// part names
	public static final String AnnotationXmlAttribute = "eglx.xml.binding.annotation.xmlAttribute";
	public static final String AnnotationXMLRootElement = "eglx.xml.binding.annotation.XMLRootElement";
	public static final String AnnotationXmlElement = "eglx.xml.binding.annotation.xmlElement";
	public static final String AnnotationXMLSchemaType = "eglx.xml.binding.annotation.XMLSchemaType";
	public static final String AnnotationJsonName = "eglx.json.JsonName";
	public static final String AnnotationXmlJavaTypeAdapter = "eglx.xml.javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter";

	public static final String PartHttpRest = "eglx.http.HttpRest";
	public static final String PartHttpProxy = "eglx.http.HttpProxy";
	public static final String LibrarySys = "eglx.lang.SysLib";
	public static final String Resources = "eglx.lang.Resources";
	public static final String AnnotationXMLStructureKind = "eglx.xml.binding.annotation.XMLStructureKind";
	public static final String AnnotationXMLValue = "eglx.xml.binding.annotation.XMLValue";

	// Constants that represent all the method names invoked using the dynamic Template.invoke() methods
	// This allows one to find all references to invocations of the methods being invoked dynamically
	public static final String genAnnotation = "genAnnotation";
	public static final String genFieldAnnotations = "genFieldAnnotations";
	public static final String genGetterAnnotations = "genGetterAnnotations";
	public static final String genPartAnnotations = "genPartAnnotations";
	public static final String genFunctionSignatures = "genFunctionSignatures";
	public static final String genFunctionParameterSignature = "genFunctionParameterSignature";
	public static final String genJavaAnnotation = "genJavaAnnotation";
	public static final String genJsonTypeDependentOptions = "genJsonTypeDependentOptions";
	public static final String genResourceAnnotation = "genResourceAnnotation";
	public static final String preGenAddXMLSchemaType = "preGenAddXMLSchemaType";
	
	// these are used by the validation step. preGen is used to preGen individual items within the part being generated.
	// preGenPart is invoked by the generator and should not be overridden or used by extending logic
	public static final String preGenAnnotations = "preGenAnnotations";
	public static final String preGenPartAnnotation = "preGenPartAnnotation";
}
