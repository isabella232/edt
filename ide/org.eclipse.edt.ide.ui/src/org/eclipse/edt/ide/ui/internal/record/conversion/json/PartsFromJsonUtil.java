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
package org.eclipse.edt.ide.ui.internal.record.conversion.json;

import java.util.LinkedHashMap;

import org.eclipse.edt.ide.ui.internal.record.conversion.IMessageHandler;
import org.eclipse.edt.ide.ui.internal.record.conversion.PartsUtil;
import org.eclipse.edt.ide.ui.templates.parts.Annotation;
import org.eclipse.edt.ide.ui.templates.parts.ArrayType;
import org.eclipse.edt.ide.ui.templates.parts.DecimalType;
import org.eclipse.edt.ide.ui.templates.parts.Field;
import org.eclipse.edt.ide.ui.templates.parts.Part;
import org.eclipse.edt.ide.ui.templates.parts.Record;
import org.eclipse.edt.ide.ui.templates.parts.RecordType;
import org.eclipse.edt.ide.ui.templates.parts.SimpleType;
import org.eclipse.edt.ide.ui.templates.parts.Type;
import org.eclipse.edt.javart.json.ArrayNode;
import org.eclipse.edt.javart.json.BooleanNode;
import org.eclipse.edt.javart.json.DecimalNode;
import org.eclipse.edt.javart.json.FloatingPointNode;
import org.eclipse.edt.javart.json.IntegerNode;
import org.eclipse.edt.javart.json.JsonVisitor;
import org.eclipse.edt.javart.json.NameValuePairNode;
import org.eclipse.edt.javart.json.NullNode;
import org.eclipse.edt.javart.json.ObjectNode;
import org.eclipse.edt.javart.json.StringNode;
import org.eclipse.edt.javart.json.ValueNode;

import eglx.lang.AnyException;

public class PartsFromJsonUtil extends PartsUtil {
	public PartsFromJsonUtil(IMessageHandler msgHandler) {
		super(msgHandler);
	}

	public Part[] process(Object node, Record wrapRec) {
		LinkedHashMap<String,Record> map = new LinkedHashMap<String,Record>();
		map.put(wrapRec.getName().toUpperCase().toLowerCase(), wrapRec);
		return process( (ValueNode)node, wrapRec, map);
	}

	public Part[] process(ValueNode node, Record wrapRec, LinkedHashMap<String,Record> recs) {
		if (node instanceof ObjectNode)
			return process( (ObjectNode)node, wrapRec, recs);
		else if (node instanceof ArrayNode)
			return process( (ArrayNode)node, wrapRec, recs);
		return (Part[]) recs.values().toArray(new Part[recs.values().size()]);
	}
	
	public Part[] process( ArrayNode node, Record wrapRec, LinkedHashMap<String,Record> recs) {
		java.util.HashMap<String,String> fieldNames = new java.util.HashMap<String,String>();
		
		fieldNames.put("array", "array");
		
		String originalName = "array";
		String fieldName = getFieldName(originalName, wrapRec.getName(), fieldNames);
		String jsonName = (originalName.equals(fieldName)) ? null : originalName;

		Field field = new Field();
		field.setName(fieldName);
		field.setType(getType(node, recs, fieldName));

		if (jsonName != null) {
			Annotation annotation = new Annotation();
			annotation.setName("JSONName"); //$NON-NLS-1$
			annotation.setValue("\"" + jsonName + "\"");//$NON-NLS-1$
			field.addAnnotation(annotation);
		}

		wrapRec.addField(field);
		
		return (Part[]) recs.values().toArray(new Part[recs.values().size()]);
	}
	
	public Part[] process(ObjectNode node, Record wrapRec, LinkedHashMap<String,Record> recs) {
		java.util.HashMap<String,String> fieldNames = new java.util.HashMap<String,String>(); 
		for (int i = 0; i<node.getPairs().size(); i++) {
			NameValuePairNode nvNode = (NameValuePairNode)node.getPairs().get(i);
			String originalName = nvNode.getName().getJavaValue();
			fieldNames.put(originalName.toLowerCase(), originalName);
		}
		
		for (int i = 0; i<node.getPairs().size(); i++) {
			NameValuePairNode nvNode = (NameValuePairNode)node.getPairs().get(i);
			String originalName = nvNode.getName().getJavaValue();
			String fieldName = getFieldName(originalName, wrapRec.getName(), fieldNames);
			String jsonName = (originalName.equals(fieldName)) ? null : originalName;

			Field field = new Field();
			field.setName(fieldName);
			field.setType(getType(nvNode.getValue(), recs, fieldName));

			if (jsonName != null) {
				Annotation annotation = new Annotation();
				annotation.setName("JSONName"); //$NON-NLS-1$
				annotation.setValue("\"" + jsonName + "\"");//$NON-NLS-1$
				field.addAnnotation(annotation);
			}

			wrapRec.addField(field);
		}
		return (Part[]) recs.values().toArray(new Part[recs.values().size()]);
	}

	private Type getType(ValueNode valNode, final LinkedHashMap<String,Record> recs, final String fieldName) {

		final Type[] type = new Type[1];

		JsonVisitor visitor = new JsonVisitor() {

			public void endVisit(ArrayNode arg0) {

				ArrayType t = new ArrayType();
				t.setElementType(type[0]);
				type[0] = t;
			}

			public void endVisit(NameValuePairNode arg0) {
			}

			public void endVisit(BooleanNode arg0) {
			}

			public void endVisit(DecimalNode arg0) {
			}

			public void endVisit(FloatingPointNode arg0) {
			}

			public void endVisit(IntegerNode arg0) {
			}

			public void endVisit(NullNode arg0) {
			}

			public void endVisit(ObjectNode arg0) {
			}

			public void endVisit(StringNode arg0) {
			}

			public boolean visit(ArrayNode arg0) throws AnyException {
				return true;
			}

			public boolean visit(NameValuePairNode arg0) throws AnyException {
				return false;
			}

			public boolean visit(BooleanNode arg0) throws AnyException {

				SimpleType t = new SimpleType();
				t.setName("boolean");//$NON-NLS-1$
				type[0] = t;
				return false;
			}

			public boolean visit(DecimalNode arg0) throws AnyException {
				String val = arg0.getStringValue();
				int len = val.length();

				int dec = 0;
				int i = val.indexOf(".");//$NON-NLS-1$
				if (i >= 0) {
					len = len - 1;
					dec = len - i;
				}

				DecimalType t = new DecimalType();
				t.setDecimals(dec);
				t.setLength(len);
				type[0] = t;
				return false;
			}

			public boolean visit(FloatingPointNode arg0) throws AnyException {
				SimpleType t = new SimpleType();
				t.setName("float");//$NON-NLS-1$
				type[0] = t;
				return false;
			}

			public boolean visit(IntegerNode arg0) throws AnyException {
				SimpleType t = new SimpleType();
				t.setName("int");//$NON-NLS-1$
				type[0] = t;
				return false;
			}

			public boolean visit(NullNode arg0) throws AnyException {
				// dunno what to do with this one
				SimpleType t = new SimpleType();
				t.setName("string");//$NON-NLS-1$
				type[0] = t;
				return false;
			}

			public boolean visit(ObjectNode arg0) throws AnyException {

				String typeName = getTypeName(fieldName);
				RecordType t = new RecordType();
				t.setName(typeName);
				type[0] = t;

				String fn = typeName.toUpperCase().toLowerCase();
				Record oldrec = (Record) recs.get(fn);

				Record rec = new Record();
				rec.setName(typeName);
				recs.put(fn, rec);

				new PartsFromJsonUtil(_msgHandler).process(arg0, rec, recs);

				if (oldrec != null) {
					mergeRecords(oldrec, rec);
				}

				return false;
			}

			public boolean visit(StringNode arg0) throws AnyException {
				SimpleType t = new SimpleType();
				t.setName("string");//$NON-NLS-1$
				type[0] = t;
				return false;
			}
		};

		valNode.accept(visitor);

		return type[0];
	}
}
