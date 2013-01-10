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

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EMemberContainer;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.impl.DynamicEClass;
import org.eclipse.edt.mof.serialization.AbstractObjectStore;
import org.eclipse.edt.mof.serialization.SerializationException;
import org.eclipse.edt.mof.serialization.Serializer;
import org.eclipse.edt.mof.utils.TabbedWriter;


@SuppressWarnings("unchecked")
public class XMLSerializer implements Serializer {
	private static final String XMLHeader = "<?xml version=\"1.1\" encoding=\"" + AbstractObjectStore.DEFAULT_ENCODING + "\"?>";
	
	MofFactory mof = MofFactory.INSTANCE;
	Map<EObject, Integer> written = new HashMap<EObject, Integer>();
	TabbedWriter writer = new TabbedWriter(new StringWriter());
	String tabs = "";
	int currentId = 1;
	
	@Override
	public void serialize(EObject object) throws SerializationException {
		writer.println(XMLHeader);
		writeObject(object);
	}
	
	public String getContents() {
		writer.flush();
		return writer.getWriter().toString();
	}
		
	private boolean isAttributeField(EField field, Object value) {
		return !(value instanceof EObject || value instanceof List) || (value instanceof EEnumLiteral) || isTypeReferenceField(field);
	}
	
	private boolean isList(EField field) {
		return field.getEType().getEClassifier() == MofFactory.INSTANCE.getEListEDataType();
	}
		
	private boolean isTypeReferenceField(EField field) {
		EType type = field.getEType();
		MofFactory mof = MofFactory.INSTANCE;
		return type == mof.getMofReferenceTypeClass() 
				|| (type instanceof EClass && ((EClass)type).isSubClassOf(mof.getMofReferenceTypeClass()));
	}
	
	private String xmlEncode(String in) {
		StringBuffer buffer = new StringBuffer();
		for (Character c : in.toCharArray()) {
			switch (c) {
			case '<': buffer.append("&lt;"); break;
			case '>': buffer.append("&gt;"); break;
			case '&': buffer.append("&amp;"); break;
			case ',': buffer.append("&#44;"); break;
			case '"': buffer.append("&quot;"); break;
			case '\b': buffer.append("&#08;"); break;
			case '\t': buffer.append("&#09;"); break;
			case '\n': buffer.append("&#10;"); break;
			case '\f': buffer.append("&#12;"); break;
			case '\r': buffer.append("&#13;"); break;
			default: buffer.append(c);
			}
		}
		return buffer.toString();
	}

	private void writeObject(Object object) {
		if (object instanceof EEnumLiteral) {
			writer.print(((EEnumLiteral)object).getCaseSensitiveName());
			return;
		}
		
		if (object instanceof EObject) {
			if (written.containsKey(object)) {
				writeObjectReference(null, (EObject)object);
			}
			else {
				writeEObjectValue(null, (EObject)object);
			}
			return;
		}
		if ((object instanceof String)
				|| object instanceof Integer
				|| object instanceof Boolean
				|| object instanceof Float
				|| object instanceof BigDecimal
				) {
			writer.print(xmlEncode(object.toString()));
			return;
		}

		if (object instanceof Object[]) {
			writeObjectArray((Object[]) object);
			return;
		}
		
		if (object instanceof Enum) {
			writer.print(((Enum)object).name());
			return;
		}
		
	}
	
	void writeEObjectValue(EField target, EObject object) {
		if (target != null && target.isTransient()) return;
		if (target != null && !target.getContainment()) {
				writeObjectReference(target, object);
				return;
		}
		String elementName;
		if (target == null) {
			elementName = object.getEClass().getCaseSensitiveName();
		} else {
			elementName = target.getCaseSensitiveName();
		}
			
		writeElementStart(elementName, object);
		List<EField> fields = ((EClass)object.getEClass()).getAllEFields();
		for (EField field : fields) {
			Object value = object.eGet(field);
			if (value != null && isAttributeField(field, value) && !field.isTransient()) {
				if (isTypeReferenceField(field)) { 
					value = ((MofSerializable)value).getMofSerializationKey(); 
				}
				writeAttributeValue(field, value);
			} 
		}
		List<EField> elements = new ArrayList();
		for (EField f : fields) {
			Object value = object.eGet(f);
			if (!(f.isTransient() || isAttributeField(f, value) || (value instanceof List && ((List)value).isEmpty()))) elements.add(f);
		}
		if (elements.isEmpty()) {
			writer.println("/>");
		}
		else  {
			writer.println('>');
			writer.pushIndent();
			for (EField field : elements) {
				Object value = ((EObject)object).eGet(field);
				if (value != null) {
					if (isList(field)) {
						writeListValue(field, (List)value);
					} else if (value instanceof EObject) {
						if (field.getContainment()) {
							writeEObjectValue(field, (EObject)value);
						} else {
							writeObjectReference(field, (EObject)value);
						}
					} else {
						writeObject(value);
					}
				}
			}
			writer.popIndent();
			writeElementEnd(elementName);
		}
		return;

	}

	// Use only for primitive type Object values
	void writeObjectValue(EField target, Object object) {
		if (!target.isTransient()) {
			String elementName = target.getName();
			writer.print('<');
			writer.print(elementName);
			writer.print('>');
			if (object instanceof List) {
				writer.println();
				writeList((List)object);
			}
			else {
				EMemberContainer eClass = target.getDeclarer();
				if (eClass instanceof DynamicEClass) {
					object = ((DynamicEClass)eClass).xmlEncodeValue(object);
				}
				writeObject(object);
			} 
			writer.print("</");
			writer.print(elementName);
			writer.println('>');
		}
	}

	void writeAttributeValue(EField field, Object value) {
		if (value == null || ("".equals(value) && !field.isNullable()) || field.isTransient()) return;
		writer.print(field.getCaseSensitiveName());
		writer.print('=');
		writer.print('"');
		EMemberContainer eClass = field.getDeclarer();
		if (eClass instanceof DynamicEClass) {
			value = ((DynamicEClass)eClass).xmlEncodeValue(value);
		}
		writeObject(value);
		writer.print("\" ");
	}
	
	void writeListValue(EField field, List list) {
		for (Object o : list) {
			if (o instanceof MofSerializable && !field.getContainment()) {
				writeTypeReference(field, (MofSerializable)o);
			} 
			else if(o instanceof EEnumLiteral && isFieldArrayOfEEnum(field)) {
				writer.print('<');
				writer.print(field.getCaseSensitiveName());
				writer.print('>');
				writer.print(((EEnumLiteral)o).getCaseSensitiveName());
				writer.print("</");
				writer.print(field.getCaseSensitiveName());
				writer.print('>');
			}
			else if (o instanceof EObject) {
				writeEObjectValue(field, (EObject)o);
			} else {
				writeObjectValue(field, o);
			}
		}		
	}
	
	boolean isFieldArrayOfEEnum(EField field) {
		return field != null && field.getEType() instanceof EGenericType && ((EGenericType)field.getEType()).getETypeArguments().size() > 0 &&  ((EGenericType)field.getEType()).getETypeArguments().get(0) instanceof EEnum;	
	}
		
	void writeElementStart(String name, EObject obj) {
		writer.print('<');
		writer.print(name);
		writer.print(" ID=\"");
		Integer id = written.get(obj);
		if (id == null) {
			writer.print(currentId);
			written.put(obj, currentId);
			currentId++;
		} else {
			writer.print(id);	
		}
		writer.print("\" ");
	}
	
	void writeElementEnd(String name) {
		writer.print("</");
		writer.print(name);
		writer.println('>');
	}
	
	void writeObjectReference(EField target, EObject obj) {
		String elementName;
		if (target == null) {
			elementName = ((EObject)obj).getEClass().getCaseSensitiveName();
		} else {
			elementName = target.getCaseSensitiveName();
		}
		writer.print('<');
		writer.print(elementName);
		writer.print(" IDREF=\"");
		int id;
		if (written.containsKey(obj)) {
			id = written.get(obj);
		} else {
			written.put(obj, currentId);
			id = currentId++;
		}
		writer.print(id);
		writer.println("\"/>");	
	}

	void writeTypeReference(EField target, MofSerializable obj) {
		writer.print('<');
		writer.print(target.getCaseSensitiveName());
		writer.print(" href=\"");
		writer.print(xmlEncode(obj.getMofSerializationKey()));
		writer.println("\"/>");
	}
	
	// Used only when list is part of a multidimensional list
	// such that there is no field name to represent each list value
	// within the outer list value
	void writeObjectArray(Object[] arr) {
		writer.pushIndent();
		for(Object obj : arr) {
			writer.print("<entry>");
			writeObject(obj);
			writer.println("</entry>");
		}
		writer.popIndent();
	}
	
	// Used only when list is part of a multidimensional list
	// such that there is no field name to represent each list value
	// within the outer list value
	void writeList(List arr) {
		writer.pushIndent();
		for(Object obj : arr) {
			writer.print("<entry>");
			writeObject(obj);
			writer.println("</entry>");
		}
		writer.popIndent();
	}

}
