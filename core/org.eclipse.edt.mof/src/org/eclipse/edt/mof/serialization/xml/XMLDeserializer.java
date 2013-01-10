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
package org.eclipse.edt.mof.serialization.xml;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.impl.Dynamic;
import org.eclipse.edt.mof.impl.DynamicEClass;
import org.eclipse.edt.mof.impl.DynamicEObject;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Deserializer;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.eclipse.edt.mof.serialization.ProxyEObject;
import org.eclipse.edt.mof.utils.EList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


public class XMLDeserializer extends DefaultHandler implements Deserializer {
	private static final String Attr_eClass = "eClass";
	private static final String Attr_ID = "ID";
	private static final String Attr_IDREF = "IDREF";
	private static final String Attr_href = "href";
	private static final String Element_entry = "entry";

	XMLReader parser;
	MofFactory mof = MofFactory.INSTANCE;
	Object object;
	Stack<Object> stack = new Stack<Object>();
	Map<String, EObject> eobjects = new HashMap<String, EObject>();
	InputSource input = null;
	IEnvironment env;
	EType datatype;
	EType listDatatype;
	boolean newList = true;
	boolean isPopEntry = false;
	StringBuilder charBuf;
	
	public XMLDeserializer(InputStream input, IEnvironment env) throws UnsupportedEncodingException {
		this.input = new InputSource(input);
		this.env = env;
	}
	
	@Override
	public EObject deserialize() throws DeserializationException {
		if (input == null) { throw new DeserializationException("No input to process"); }
			parser = createParser();
			parser.setContentHandler(this);
			parser.setErrorHandler(this);
		try {
			parser.parse(input);
		} catch (Exception e) {
			throw new DeserializationException(e);
		}
		return (EObject)object;
	}
	
	private XMLReader createParser() {
		try {
			return XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public void startElement(String nsURI, String localName, String qName, Attributes attrs) throws SAXException {
		checkCharacters();
		EObject result = null;
		
		if (localName == null || localName.length() == 0) {
			localName = qName;
		}
		
		// One of the following must be set
		String typeSignature = attrs.getValue("", Attr_eClass);
		String idref = attrs.getValue("", Attr_IDREF);
		String href = attrs.getValue("", Attr_href);
		
		if (href != null) {
			// We have a stand-alone EClassifier as value reference
			result = resolveTypeReference(href, true);
		} 
		else if (idref != null) {
			// We have a reference to another element in the document
			result = eobjects.get(idref);
			if (result == null) {
				// We are dealing with a forward reference
				result = new ProxyEObject();
				eobjects.put(idref, result);
			}
		} 
		else if (localName.equals(Element_entry) && listDatatype != null) {

			if (newList) {
				// List object has not been created yet
				stack.push(new EList());
				newList = false;
			}

			// Handle nested List of List values			
			EType type = ((EGenericType)listDatatype).getETypeArguments().get(0);
			if (type instanceof EGenericType) {
				listDatatype = type;
			}
			else {
				datatype = type;
				if (datatype instanceof EDataType) {
					stack.push(""); //push a dummy entry in case the value is ""
				}

				
				isPopEntry = !(datatype instanceof EClass);
			}
			
		}
		else if (typeSignature == null) {
			// We are dealing with an element that represents an EDataType value
			// within a List; otherwise the value would be handled as an attribute
			EObject obj = (EObject)stack.peek();
			if (obj == null)
				throw new SAXException("No type specified for element: " + localName);
			else {
				EClass eClass = obj.getEClass();
				if (eClass instanceof DynamicEClass) {
					stack.push(""); //push a dummy entry in case the value is ""
					datatype = eClass;
				} 
				else {
					EField field = eClass.getEField(localName);
					if (field == null) {
						stack.push(null);
					}
					if (field != null && field.getEType().getEClassifier().equals(mof.getEListEDataType())) {
						datatype = ((EGenericType)field.getEType()).getETypeArguments().get(0);
						if (datatype.getEClassifier().equals(mof.getEListEDataType())) {
							// We have a List of Lists
							listDatatype = datatype;
							datatype = null;
						}
						else {
							if (datatype instanceof EDataType) {
								stack.push(""); //push a dummy entry in case the value is ""
							}
						}
					}
					else {
						throw new SAXException("No type specified for element: " + localName);
					}
				}
			}
		} else {
			EClass eClass = (EClass)resolveTypeReference(typeSignature, false);
			result = eClass.newInstance(true, false);
			for (int i=0; i<attrs.getLength(); i++) {
				String attr = attrs.getLocalName(i);
				if (attr == null || attr.length() == 0) {
					attr = attrs.getQName(i);
				}
				if (attr.equals("ID")) {
					Object proxy = eobjects.get(attrs.getValue(i));
					if (proxy instanceof ProxyEObject) {
						((ProxyEObject)proxy).updateReferences(result);
					}
					eobjects.put(attrs.getValue(i), result);
					continue;
				}
				if (attr.equals(Attr_eClass) || attr.equals(Attr_ID) || attr.equals(Attr_IDREF)) continue;
				
				
				if (eClass instanceof Dynamic) {
					result.eSet(attr, attrs.getValue(i));
				}
				else {
					EField field = eClass.getEField(attr);
					if (field != null) {
						Object value = valueFromString(field.getEType(), attrs.getValue(i));
						result.eSet(field, value);
					}
				}
			}
		}
		if (result != null) 
			stack.push(result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void endElement(String nsURI, String localName, String qName) throws SAXException {
		checkCharacters();
		
		if (localName == null || localName.length() == 0) {
			localName = qName;
		}

		datatype = null;
		
		if (localName.equals(Element_entry)) {
			if (isPopEntry)
				isPopEntry = false;
			else
				return;
		}
		Object source = stack.pop();
		if (source == null) return;
		
		if (stack.isEmpty()) {
			object = source;
		} else {
			Object target = stack.peek();
			if (target instanceof List) {
				((List)target).add(source);
				if (localName.equals(Element_entry)) {
					if (source instanceof List)
						newList = true;  // only occurs if dealing with List of List values
				}
			}
			else if (target instanceof DynamicEObject) {
				// Only happens if source is part of a List
				List list = (List)((DynamicEObject)target).eGet(localName);
				if (list == null) {
					list = new EList();
					((DynamicEObject)target).eSet(localName, list);
				}
				list.add(source);
			}
			else {
				EObject etarget = (EObject)target;
				EField field = ((EClass)etarget.getEClass()).getEField(localName);
				if (field.getEType().getEClassifier() == mof.getEListEDataType()) {
					if ((List)etarget.eGet(field) == null && isNullableListType(field)) {
						etarget.eSet(field, new EList());
					}
					((List)etarget.eGet(field)).add(source);
					if (source instanceof List)
						newList = true;  // only occurs if dealing with List of List values
				}
				else {
					etarget.eSet(field, source);
				}
			}
		}
	}
	
	private boolean isNullableListType(EField field) {
		if (!field.isNullable()) {
			return false;
		}
		if (field.getEType() != null && field.getEType().getEClassifier() != null) {
			return field.getEType().getEClassifier().equals(MofFactory.INSTANCE.getEListEDataType());
		}
		return false;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (charBuf == null) {
			charBuf = new StringBuilder(length);
		}
		charBuf.append(ch, start, length);
	}
	
	private void checkCharacters() throws SAXException {
		if (charBuf != null) {
			if (datatype != null && (datatype instanceof EDataType || datatype instanceof DynamicEClass) ) {
				String string = charBuf.toString();
				Object value;
				stack.pop();   //pop off the dummy entry that was added, because an value of "" will not come here
				if (datatype instanceof DynamicEClass) {
					value = ((DynamicEClass)datatype).getEncodedValue(string);
				}
				else {
					value = valueFromString(datatype, string);
				}
				stack.push(value);
				datatype = null;
			}
			charBuf = null;
		}
	}

	@SuppressWarnings("unchecked")
	private Object valueFromString(EType type, String input) throws SAXException {
		if (type == mof.getEIntEDataType()) {
			return Integer.parseInt(input);
		} else if (type == mof.getEBooleanEDataType()) {
			return Boolean.parseBoolean(input);
		} else if (type instanceof EEnum) {
			Class<Enum> enumClass;
			try {
				enumClass = (Class<Enum>)Class.forName(type.getETypeSignature());
				return Enum.valueOf(enumClass, input);
			} catch (ClassNotFoundException e) {
				return ((EEnum)type).getEEnumLiteral(input);
			}
			
		} else if (type instanceof EClass && (type == mof.getMofReferenceTypeClass() || ((EClass)type).isSubClassOf(mof.getMofReferenceTypeClass()))) {
			try {
				return resolveTypeReference(input, true);
			} catch (Exception e) {
				throw new SAXException(e);
			}
		} else {
			return input;
		}
	}
	
	private EObject resolveTypeReference(String href, boolean useProxies) throws SAXException {
		try {
			EObject obj = env.find(href, useProxies);
			if (obj == null) throw new SAXException("Object not found: " + href);
			return obj;
		} catch (DeserializationException e) {
			throw new SAXException(e);
		} catch (MofObjectNotFoundException e1) {
			throw new SAXException(e1);
		}
	}

}
