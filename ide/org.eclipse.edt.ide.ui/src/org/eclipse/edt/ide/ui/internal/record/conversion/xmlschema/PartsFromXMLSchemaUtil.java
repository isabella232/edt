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
package org.eclipse.edt.ide.ui.internal.record.conversion.xmlschema;
import java.util.LinkedHashMap;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizardMessages;
import org.eclipse.edt.ide.ui.internal.record.conversion.IMessageHandler;
import org.eclipse.edt.ide.ui.internal.record.conversion.PartsUtil;
import org.eclipse.edt.ide.ui.templates.parts.Annotation;
import org.eclipse.edt.ide.ui.templates.parts.ArrayType;
import org.eclipse.edt.ide.ui.templates.parts.DataItem;
import org.eclipse.edt.ide.ui.templates.parts.DataItemType;
import org.eclipse.edt.ide.ui.templates.parts.DecimalType;
import org.eclipse.edt.ide.ui.templates.parts.Embed;
import org.eclipse.edt.ide.ui.templates.parts.Field;
import org.eclipse.edt.ide.ui.templates.parts.Part;
import org.eclipse.edt.ide.ui.templates.parts.Record;
import org.eclipse.edt.ide.ui.templates.parts.RecordType;
import org.eclipse.edt.ide.ui.templates.parts.SimpleType;
import org.eclipse.edt.ide.ui.templates.parts.Type;

public class PartsFromXMLSchemaUtil extends PartsUtil {
	private XSModel xsModel = null;

	public PartsFromXMLSchemaUtil(IMessageHandler msgHandler) {
		super(msgHandler);
	}

	public Part[] process(Object model, Record wrapRec) {
		this.xsModel = (XSModel) model;
		LinkedHashMap<String,Part> recs = new LinkedHashMap<String,Part>();
		LinkedHashMap<XSTypeDefinition,Type> types = new LinkedHashMap<XSTypeDefinition,Type>();

		XSNamedMap map = this.xsModel.getComponents(XSConstants.TYPE_DEFINITION);
		// Do simple types first
		for (int i = 0; i < map.getLength(); i++) {
			XSTypeDefinition type = (XSTypeDefinition) map.item(i);
			if (type.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE && !type.getNamespace().equals("http://www.w3.org/2001/XMLSchema")//$NON-NLS-1$
					&& !WSDLUtil.isStandardArray(type))
				processType(type, null, recs, types);
		}

		// Then complex types
		for (int i = 0; i < map.getLength(); i++) {
			XSTypeDefinition type = (XSTypeDefinition) map.item(i);
			if (type.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE && !type.getNamespace().equals("http://www.w3.org/2001/XMLSchema")//$NON-NLS-1$
					&& !WSDLUtil.isStandardArray(type))
				processType(type, null, recs, types);
		}

		map = this.xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
		for (int i = 0; i < map.getLength(); i++) {
			XSObject xsObject = map.item(i);
			// System.out.println("Element {" + xsObject.getNamespace() + "}" +
			// xsObject.getName());

			processElement(xsObject, null, recs, types);
		}

		return recs.values().toArray(new Part[recs.values().size()]);
	}

	/**
	 * This method processes Elements and determines their EGL types. It returns
	 * a Type object that represents the EGL type. This can be a record type,
	 * array type, or a primitive type.
	 * 
	 * @param xsObject
	 * @param wrapRec
	 * @param recs
	 * @param types
	 * @return
	 */
	private Type processElement(XSObject xsObject, Record wrapRec, LinkedHashMap<String,Part> recs, LinkedHashMap<XSTypeDefinition,Type> types) {
		Type type = null;
		if (xsObject instanceof XSElementDeclaration) {
			XSElementDeclaration xsElement = (XSElementDeclaration) xsObject;
			if (WSDLUtil.isStandardArray(xsElement.getTypeDefinition())) {
				type = processStandardArray((XSComplexTypeDefinition) xsElement.getTypeDefinition(), wrapRec, recs, types);
			} else if (xsElement.getTypeDefinition().getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
				XSComplexTypeDefinition xsComplexType = (XSComplexTypeDefinition) xsElement.getTypeDefinition();
				type = (Type) types.get(xsComplexType);
				if (type == null) { // complex type hasn't been defined
					String typeName = (wrapRec == null || xsComplexType.getName() == null) ? xsElement.getName() : xsComplexType.getName();
					String rootName = typeName;
					// if this is an anonymous type and the name corresponds to
					// an existing type, make name unique
					if (isAnonymousCollision(xsComplexType, xsElement, wrapRec, typeName)) {
						typeName = "_" + wrapRec.getName() + "_" + typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
					}
					if (xsComplexType.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_SIMPLE)
						type = processSimpleRecordType(xsComplexType, recs, types, typeName);
					else
						type = processRecordType(xsComplexType, recs, types, typeName, rootName);
				} else {
					type = (Type) type.clone();
				}
			} else {
				XSSimpleTypeDefinition xsSimpleType = (XSSimpleTypeDefinition) xsElement.getTypeDefinition();
				type = (Type) types.get(xsSimpleType);
				if (type == null) { // simple type hasn't been defined as a
									// dataitem
					type = getPrimitiveType(xsObject, xsSimpleType, xsElement.getName());
					if(WSDLUtil.isTimeStamp(xsSimpleType)) 
						  type.setNullable(true);
				} else {
					type = (Type) type.clone();
				}
			}
		} else if (xsObject instanceof XSAttributeDeclaration) {
			XSAttributeDeclaration xsAttr = (XSAttributeDeclaration) xsObject;
			type = getPrimitiveType(xsObject, (XSSimpleTypeDefinition) xsAttr.getTypeDefinition(), xsAttr.getName());
			if(WSDLUtil.isTimeStamp(xsAttr.getTypeDefinition()))
				   type.setNullable(true);
		} else {
			System.out.println("Don't know what type this is" + xsObject.toString());//$NON-NLS-1$
		}
		return type;
	}

	private boolean isAnonymousCollision(XSTypeDefinition type, XSElementDeclaration element, Record wrapRec, String typeName) {
		if (type.getAnonymous() && wrapRec != null) {
			StringList list = this.xsModel.getNamespaces();
			for (int i = 0; i < list.getLength(); i++) {
				String namespace = list.item(i);
				if (this.xsModel.getTypeDefinition(typeName, namespace) != null)
					return true;
			}

			if (element.getScope() == XSConstants.SCOPE_LOCAL
					&& (element.getEnclosingCTDefinition() != null && !WSDLUtil.isStandardArray(element.getEnclosingCTDefinition()))
					&& WSDLUtil.findLocalAnonymousElements(this.xsModel, typeName).length > 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * The method offers special handling for complex types that have a single
	 * element that is an array. If an element type is of this complex type, the
	 * complex type gets replaced with the single element. This allows for
	 * defining multi-occurring arrays. ex. <complexType
	 * name="ArrayOf_xsd_string"> <sequence> <element maxOccurs="unbounded"
	 * minOccurs="0" name="string" nillable="true" type="xsd:string"/>
	 * </sequence> </complexType> <complexType name="Record02"> <sequence>
	 * <element name="item1" nillable="true" type="impl:ArrayOf_xsd_string"/>
	 * <element name="item2" nillable="true" type="impl:Record03"/> <element
	 * name="item3" nillable="true" type="impl:ArrayOfRecord03"/> </sequence>
	 * </complexType> Becomes: Record ArrayOf_xsd_string
	 * {@XMLRootElement{namespace =
	 * "http://services.egl.non"} string_ string[] {@XMLElement{name
	 *  = "string", namespace = "http://services.egl.non",
	 * nillable = true} ; end Record Record02 {@XMLRootElement{namespace
	 *  = "http://services.egl.non"} item1 string[]
	 * {@XMLElement{namespace =
	 * "http://services.egl.non", nillable = true} ; item2 Record03
	 * {@XMLElement{namespace =
	 * "http://services.egl.non", nillable = true} ; item3 Record03[]
	 * {@XMLElement{namespace =
	 * "http://services.egl.non", nillable = true} ; end Record02.item1 is a
	 * string[] instead of ArrayOf_xsd_string.
	 * 
	 * @param complexType
	 * @param wrapRec
	 * @param recs
	 * @param types
	 * @return
	 */
	private Type processStandardArray(XSComplexTypeDefinition complexType, Record wrapRec, LinkedHashMap<String,Part> recs, 
			LinkedHashMap<XSTypeDefinition,Type> types) {
		ArrayType type = new ArrayType();

		XSParticle particle = complexType.getParticle();
		XSObjectList list = ((XSModelGroup) particle.getTerm()).getParticles();
		XSParticle innerParticle = (XSParticle) list.item(0);
		XSElementDeclaration element = (XSElementDeclaration) innerParticle.getTerm();
		Type subType = processElement(element, wrapRec, recs, types);
		subType.setNullable((element.getNillable() || particle.getMinOccurs() == 0) && !WSDLUtil.isSpecialArrayType(element.getTypeDefinition()));
		type.setElementType(subType);
		return type;
	}

	/**
	 * This method will create a DataItem for a schema element that is defined
	 * as a simple type.
	 * 
	 * @param simpleType
	 * @param recs
	 * @param types
	 * @param fieldName
	 * @return
	 */
	private Type processSimpleType(XSSimpleTypeDefinition simpleType, LinkedHashMap<String,Part> recs, 
			LinkedHashMap<XSTypeDefinition,Type> types, String fieldName) {

		String typeName = getTypeName(fieldName);
		DataItemType type = new DataItemType();
		type.setName(typeName);

		String fn = typeName.toUpperCase().toLowerCase();
		if (recs.get(fn) == null) {
			DataItem dataItem = new DataItem();
			dataItem.setName(typeName);

			dataItem.setType(getPrimitiveType(null, simpleType, fieldName));

			recs.put(fn, dataItem);
		}
		return type;
	}

	/**
	 * This method processes a type that is defined in the schema. This will
	 * typically produce a record.
	 * 
	 * @param typeDefinition
	 * @param wrapRec
	 * @param recs
	 * @param types
	 * @return
	 */
	private Type processType(XSTypeDefinition typeDefinition, Record wrapRec, LinkedHashMap<String,Part> recs, LinkedHashMap<XSTypeDefinition,Type> types) {
		Type type = null;
		if (typeDefinition.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
			XSComplexTypeDefinition xsComplexType = (XSComplexTypeDefinition) typeDefinition;
			String typeName = xsComplexType.getName();
			if (xsComplexType.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_SIMPLE)
				type = processSimpleRecordType(xsComplexType, recs, types, typeName);
			else
				type = processRecordType(xsComplexType, recs, types, typeName, typeName);
			types.put(xsComplexType, type);
		} else if (typeDefinition.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
			XSSimpleTypeDefinition xsSimpleType = (XSSimpleTypeDefinition) typeDefinition;
			String typeName = xsSimpleType.getName();
			type = processSimpleType(xsSimpleType, recs, types, typeName);
			types.put(xsSimpleType, type);
		}

		return type;
	}

	private java.util.HashMap<String,String> getFieldNames( XSComplexTypeDefinition xsComplexType, boolean isSimple )
	{
		java.util.HashMap<String,String> fieldNames = new java.util.HashMap<String,String>();
		
		XSObjectList xsObjectList = xsComplexType.getAttributeUses();
		for (int i = 0; i < xsObjectList.getLength(); i++) {
			XSAttributeUse xsAttrUse = (XSAttributeUse) xsObjectList.item(i);
			XSAttributeDeclaration xsAttrDecl = xsAttrUse.getAttrDeclaration();

			// If the complex type is not enclosing the attribute,
			// it is part of an extension. So we need to ignore it. It will be
			// included as
			// part of an embed
			if (xsAttrDecl.getEnclosingCTDefinition() != null && xsAttrDecl.getEnclosingCTDefinition() != xsComplexType)
				continue;

			String originalName = xsAttrDecl.getName();
			fieldNames.put(originalName.toLowerCase(), originalName);
		}
		
		if (!isSimple)
		{
			if (xsComplexType.getParticle() != null)  // has children? 
			{
				XSModelGroup xsModelGroup = (XSModelGroup) xsComplexType.getParticle().getTerm();
				XSModelGroup extGroup = WSDLUtil.getExtensionGroup(xsComplexType);
				if (xsModelGroup != extGroup) // they are equal if extension doesn't
											  // define additional fields
				{
					getFieldNames(xsModelGroup, extGroup, fieldNames);
				}
			}
		}
		return fieldNames;
	}

	private void getFieldNames(XSModelGroup xsModelGroup, XSModelGroup extGroup, java.util.HashMap<String,String> fieldNames)
	{
		XSObjectList xsObjectList = xsModelGroup.getParticles();
		for (int i = 0; i < xsObjectList.getLength(); i++) 
		{
			XSParticle xsParticle = (XSParticle) xsObjectList.item(i);
			if (xsParticle.getTerm() instanceof XSModelGroup) 
			{
				XSModelGroup group = (XSModelGroup) xsParticle.getTerm();
				// if this record is an extension of this group, ignore...an
				// embed was already created.
				if (group != extGroup) 
				{
					getFieldNames(group, null, fieldNames );
				}
				continue;
			}
			if (!(xsParticle.getTerm() instanceof XSElementDeclaration)) 
			{
				// TODO handle this case?
				continue;
			}
			XSElementDeclaration xsElement = (XSElementDeclaration) xsParticle.getTerm();
			String originalName = xsElement.getName();
			fieldNames.put(originalName.toLowerCase(), originalName);
		}
	}

	/**
	 * Process child attributes for a complex type. Each child attribute becomes
	 * an item with the XMLAttribute annotation.
	 * 
	 * @param xsComplexType
	 * @param wrapRec
	 * @param recs
	 * @param types
	 */
	private void processChildAttributes(XSComplexTypeDefinition xsComplexType, Record wrapRec, 
			LinkedHashMap<String,Part> recs, LinkedHashMap<XSTypeDefinition,Type> types, java.util.HashMap<String,String> fieldNames) {
		XSObjectList xsObjectList = xsComplexType.getAttributeUses();
		for (int i = 0; i < xsObjectList.getLength(); i++) {
			XSAttributeUse xsAttrUse = (XSAttributeUse) xsObjectList.item(i);
			XSAttributeDeclaration xsAttrDecl = xsAttrUse.getAttrDeclaration();

			// If the complex type is not enclosing the attribute,
			// it is part of an extension. So we need to ignore it. It will be
			// included as
			// part of an embed
			if (xsAttrDecl.getEnclosingCTDefinition() != null && xsAttrDecl.getEnclosingCTDefinition() != xsComplexType)
				continue;

			String originalName = xsAttrDecl.getName();
			String fieldName = getFieldName(originalName, wrapRec.getName(), fieldNames);
			String xmlName = (originalName.equals(fieldName)) ? null : originalName;

			Field field = new Field();
			field.setName(fieldName);
			Type type = processElement(xsAttrDecl, wrapRec, recs, types);
			if(!type.isNullable())
			      type.setNullable(!xsAttrUse.getRequired());
			field.setType(type);

			Annotation annotation = new Annotation();
			annotation.setName("XMLAttribute");//$NON-NLS-1$
			if (xmlName != null)
				annotation.addField("name", "\"" + xmlName + "\"");//$NON-NLS-1$
			if (xsAttrDecl.getNamespace() != null && xsAttrDecl.getNamespace().length() > 0)
				annotation.addField("namespace", "\"" + xsAttrDecl.getNamespace() + "\"");//$NON-NLS-1$
			field.addAnnotation(annotation);

			wrapRec.addField(field);
		}
	}

	/**
	 * Process the child elements for the record.
	 * 
	 * @param xsComplexType
	 * @param wrapRec
	 * @param recs
	 * @param types
	 */
	private void processChildElements(XSComplexTypeDefinition xsComplexType, Record wrapRec, LinkedHashMap<String,Part> recs, 
			LinkedHashMap<XSTypeDefinition,Type> types, java.util.HashMap<String,String> fieldNames) {
		if (xsComplexType.getParticle() == null) { // no children?
			return;
		}
		XSModelGroup xsModelGroup = (XSModelGroup) xsComplexType.getParticle().getTerm();
		XSModelGroup extGroup = WSDLUtil.getExtensionGroup(xsComplexType);
		if (xsModelGroup != extGroup) // they are equal if extension doesn't
										// define additional fields
			processChildGroups(xsModelGroup, extGroup, wrapRec, recs, types, (xsModelGroup.getCompositor() == XSModelGroup.COMPOSITOR_CHOICE), fieldNames );
	}

	/**
	 * Processes the child groups for the record. If the record is an extension
	 * of another record, there may be multiple groups.
	 * 
	 * @param xsModelGroup
	 * @param extGroup
	 * @param wrapRec
	 * @param recs
	 * @param types
	 */
	private void processChildGroups(XSModelGroup xsModelGroup, XSModelGroup extGroup, Record wrapRec, LinkedHashMap<String,Part> recs, 
			LinkedHashMap<XSTypeDefinition,Type> types, boolean isChoice, java.util.HashMap<String,String> fieldNames) 
	{
		XSObjectList xsObjectList = xsModelGroup.getParticles();
		for (int i = 0; i < xsObjectList.getLength(); i++) {
			XSParticle xsParticle = (XSParticle) xsObjectList.item(i);
			if (xsParticle.getTerm() instanceof XSModelGroup) {
				XSModelGroup group = (XSModelGroup) xsParticle.getTerm();
				// if this record is an extension of this group, ignore...an
				// embed was already created.
				if (group != extGroup) {
					processChildGroups(group, null, wrapRec, recs, types, (group.getCompositor() == XSModelGroup.COMPOSITOR_CHOICE), fieldNames );
				}
				continue;
			}
			if (!(xsParticle.getTerm() instanceof XSElementDeclaration)) {
				continue;
			}
			XSElementDeclaration xsElement = (XSElementDeclaration) xsParticle.getTerm();
			String originalName = xsElement.getName();
			String fieldName = getFieldName(originalName, wrapRec.getName(), fieldNames);
			String xmlName = (originalName.equals(fieldName)) ? null : originalName;

			Field field = new Field();
			field.setName(fieldName);
			Type type = processElement(xsElement, wrapRec, recs, types);
			if (xsParticle.getMaxOccurs() > 1 || xsParticle.getMaxOccursUnbounded() || xsParticle.getMinOccurs() > 1) {
				ArrayType aType = new ArrayType();
				aType.setElementType(type);
				type = aType;
			} else if(!type.isNullable()) {
				type.setNullable((xsParticle.getMinOccurs() == 0 || xsElement.getNillable() || isChoice )
						&& !WSDLUtil.isSpecialArrayType(xsElement.getTypeDefinition()));
			}
			field.setType(type);

			if (xmlName != null || (xsElement.getNamespace() != null && xsElement.getNamespace().length() > 0) || xsElement.getNillable()) {
				Annotation annotation = new Annotation();
				annotation.setName("XMLElement");//$NON-NLS-1$
				if (xmlName != null)
					annotation.addField("name", "\"" + xmlName + "\"");//$NON-NLS-1$
				if (xsElement.getNamespace() != null && xsElement.getNamespace().length() > 0)
					annotation.addField("namespace", "\"" + xsElement.getNamespace() + "\"");//$NON-NLS-1$
				if (xsElement.getNillable())
					annotation.addField("nillable", "true");//$NON-NLS-1$
				field.addAnnotation(annotation);
			}

			wrapRec.addField(field);
		}
	}

	/**
	 * Creates an embed for the derived extension type.
	 * 
	 * @param xsComplexType
	 * @param wrapRec
	 * @param recs
	 * @param types
	 */
	private void processDerivedType(XSComplexTypeDefinition xsComplexType, Record wrapRec, 
			LinkedHashMap<String,Part> recs, LinkedHashMap<XSTypeDefinition,Type> types) {
		XSTypeDefinition derivedType = xsComplexType.getBaseType();
		if (WSDLUtil.isExtensionType(xsComplexType)) {
			if (derivedType instanceof XSComplexTypeDefinition) {
				Embed embed = new Embed();
				Type type = processRecordType((XSComplexTypeDefinition) derivedType, recs, types, derivedType.getName(), derivedType.getName());
				embed.setType(type);
				wrapRec.addField(embed);
			} else {
				Type type = (Type) types.get(derivedType);
				if (type == null) { // simple type hasn't been defined as a
									// dataitem
					type = getPrimitiveType(null, (XSSimpleTypeDefinition) derivedType, derivedType.getName());
				} else {
					type = (Type) type.clone();
				}

				// add value field
				Field field = new Field();
				field.setName("egl_value");//$NON-NLS-1$
				// SimpleType t = new SimpleType();
				// t.setName("string");
				type.setNullable(true);
				field.setType(type);
				wrapRec.addField(field);
			}
		}
	}

	/**
	 * Determines the proper EGL primitive type for the schema simple type
	 * definition.
	 * 
	 * @param object
	 * @param simpleType
	 * @param fieldName
	 * @return
	 */
	private Type getPrimitiveType(XSObject object, XSSimpleTypeDefinition simpleType, final String fieldName) {

		Type type = new SimpleType();

		// turn an xsd string with a fixed length into a unicode field
		if (WSDLUtil.isString(simpleType)) {
			type.setName("string");//$NON-NLS-1$
		} else if (WSDLUtil.isLimitedString(simpleType)) {
			type.setName("string(" + String.valueOf(WSDLUtil.getItemLength(simpleType)) + ")");//$NON-NLS-1$
		}
		// turn an xsd integer (not an int) into a NUM
		else if (XSConstants.INTEGER_DT == simpleType.getBuiltInKind()) {
			String size = "(31)";//$NON-NLS-1$

			if (WSDLUtil.getTotalDigits(simpleType) != -1)
				size = "(" + WSDLUtil.getTotalDigits(simpleType) + ")";

			type.setName("num" + size);//$NON-NLS-1$
		}
		// turn an xsd decimal into an EGL decimal
		else if (XSConstants.DECIMAL_DT == simpleType.getBuiltInKind()) {
			type = new DecimalType();
			int[] decimal = WSDLUtil.getDecimalDefinition(simpleType);
			if (decimal != null) {
				((DecimalType) type).setLength(decimal[0]);
				((DecimalType) type).setDecimals(decimal[1]);
			} else {
				((DecimalType) type).setLength(31);
				((DecimalType) type).setDecimals(0);
			}
		} else if (XSConstants.DURATION_DT == simpleType.getBuiltInKind()) {
			String eglPattern;// = WSDLUtil.getEglPatternFromDoc( simpleType );
			// if( eglPattern.length() > 0 )
			{
				eglPattern = WSDLUtil.eglPattern(simpleType);
			}
			if (eglPattern.length() > 0) {
				eglPattern = "(\"" + eglPattern + "\")";
			} else {
				this._msgHandler.addMessage(NewRecordWizardMessages.bind(NewRecordWizardMessages.PartsFromXMLSchemaUtil_missingDurationPatternMessage,new String[]{(object == null) ? simpleType.getName() : object.getName()}));
			}
			type.setName("interval" + eglPattern);//$NON-NLS-1$
		} else if (XSConstants.DATETIME_DT == simpleType.getBuiltInKind()) {
			String eglPattern = WSDLUtil.eglPattern(simpleType);
			// String eglPattern = WSDLUtil.getEglPatternFromDoc( simpleType );
			if (eglPattern.length() > 0) {
				eglPattern = "(\"" + eglPattern + "\")";
			}
			type.setName("timestamp" + eglPattern);//$NON-NLS-1$
		} else if (XSConstants.BASE64BINARY_DT == simpleType.getBuiltInKind() && WSDLUtil.getItemLength(simpleType) > 0) {
			type.setName("hex(" + (WSDLUtil.getItemLength(simpleType) * 2) + ")");//$NON-NLS-1$
		} else {
			type.setName(WSDLUtil.getEGLPrimitiveBase(simpleType));

			if (XSConstants.LIST_DT == simpleType.getBuiltInKind() || // NMTOKENS
					XSConstants.HEXBINARY_DT == simpleType.getBuiltInKind()) {

				ArrayType atype = new ArrayType();
				atype.setElementType(type);
				type = atype;
			}

		}

		return type;
	}

	private Type processRecordType(XSComplexTypeDefinition xsNode, LinkedHashMap<String,Part> recs, 
			LinkedHashMap<XSTypeDefinition,Type> types, String recordName, String rootName) {
		String typeName = getTypeName(recordName);
		RecordType type = new RecordType();
		type.setName(typeName);

		String fn = typeName.toUpperCase().toLowerCase();
		Record rec = (Record) recs.get(fn);
		if (rec == null) {
			rec = new Record();
			rec.setName(typeName);
			recs.put(fn, rec);

			new PartsFromXMLSchemaUtil(_msgHandler).processRecord(this.xsModel, xsNode, rec, recs, types);

			if (!typeName.equals(recordName) || (xsNode.getNamespace() != null && xsNode.getNamespace().length() > 0)) {
				Annotation annotation = new Annotation();
				annotation.setName("XMLRootElement");//$NON-NLS-1$
				if (!typeName.equals(rootName))
					annotation.addField("name", "\"" + rootName + "\"");//$NON-NLS-1$
				if (xsNode.getNamespace() != null && xsNode.getNamespace().length() > 0)
					annotation.addField("namespace", "\"" + xsNode.getNamespace() + "\"");//$NON-NLS-1$
				rec.addAnnotation(annotation);
			}

		}
		return type;
	}

	private void processRecord(XSModel model, XSComplexTypeDefinition xsTypeDef, Record wrapRec, 
			LinkedHashMap<String,Part> recs, LinkedHashMap<XSTypeDefinition,Type> types) {
		this.xsModel = model;

		// TODO: check for circular definitions
		
		java.util.HashMap<String,String> fieldNames = getFieldNames(xsTypeDef, false);

		processChildAttributes(xsTypeDef, wrapRec, recs, types, fieldNames);
		processDerivedType(xsTypeDef, wrapRec, recs, types);
		processChildElements(xsTypeDef, wrapRec, recs, types, fieldNames);
	}

	private void processSimpleRecord(XSComplexTypeDefinition xsTypeDef, Record wrapRec, 
			LinkedHashMap<String,Part> recs, LinkedHashMap<XSTypeDefinition,Type> types) {
		java.util.HashMap<String,String> fieldNames = getFieldNames(xsTypeDef, true);
		processChildAttributes(xsTypeDef, wrapRec, recs, types, fieldNames);
		if (WSDLUtil.isExtensionType(xsTypeDef))
			processDerivedType(xsTypeDef, wrapRec, recs, types);
		else {
			// add value field
			Field field = new Field();
			field.setName("egl_value");//$NON-NLS-1$
			SimpleType t = new SimpleType();
			t.setName("string");//$NON-NLS-1$
			t.setNullable(true);
			field.setType(t);
			wrapRec.addField(field);
		}
	}

	private Type processSimpleRecordType(XSComplexTypeDefinition xsNode, LinkedHashMap<String,Part> recs, 
			  LinkedHashMap<XSTypeDefinition,Type> types, String fieldName) {
		String typeName = getTypeName(fieldName);
		RecordType type = new RecordType();
		type.setName(typeName);

		String fn = typeName.toUpperCase().toLowerCase();
		Record rec = (Record) recs.get(fn);
		if (rec == null) {
			rec = new Record();
			rec.setName(typeName);
			recs.put(fn, rec);

			// add simpleContent annotation
			Annotation annotation = new Annotation();
			annotation.setName("XMLValue");//$NON-NLS-1$
			annotation.addField("kind", "XMLStructureKind.simpleContent");//$NON-NLS-1$
			rec.addAnnotation(annotation);

			new PartsFromXMLSchemaUtil(_msgHandler).processSimpleRecord(xsNode, rec, recs, types);

		}

		return type;
	}
}
