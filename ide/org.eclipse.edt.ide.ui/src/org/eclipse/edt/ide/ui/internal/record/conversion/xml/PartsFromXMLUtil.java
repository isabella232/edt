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
package org.eclipse.edt.ide.ui.internal.record.conversion.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.eclipse.edt.ide.ui.internal.record.conversion.IMessageHandler;
import org.eclipse.edt.ide.ui.internal.record.conversion.PartsUtil;
import org.eclipse.edt.ide.ui.templates.parts.Annotation;
import org.eclipse.edt.ide.ui.templates.parts.ArrayType;
import org.eclipse.edt.ide.ui.templates.parts.Field;
import org.eclipse.edt.ide.ui.templates.parts.Part;
import org.eclipse.edt.ide.ui.templates.parts.Record;
import org.eclipse.edt.ide.ui.templates.parts.RecordType;
import org.eclipse.edt.ide.ui.templates.parts.SimpleType;
import org.eclipse.edt.ide.ui.templates.parts.Type;

public class PartsFromXMLUtil extends PartsUtil {
	static java.util.Map<String,String> reservedXSINames = new java.util.HashMap<String,String>();
	static {
		reservedXSINames.put("type", "type");//$NON-NLS-1$
		reservedXSINames.put("nil", "nil");//$NON-NLS-1$
		reservedXSINames.put("schemaLocation", "schemaLocation");//$NON-NLS-1$
		reservedXSINames.put("noNamespaceSchemaLocation", "noNamespaceSchemaLocation");//$NON-NLS-1$
	}

	public PartsFromXMLUtil(IMessageHandler msgHandler) {
		super(msgHandler);
	}

	public Part[] process(Object node, Record wrapRec) {
		XMLNode xmlNode = new XMLNode((Node) node);
		String xmlName = getTypeName(xmlNode.getQName().getLocalPart());
		wrapRec.setName(xmlName);

		if (!xmlName.equals(xmlNode.getQName().getLocalPart())
				|| (xmlNode.getQName().getNamespaceURI() != null && xmlNode.getQName().getNamespaceURI().length() > 0) || xmlNode.isNillable()) {
			Annotation annotation = new Annotation();
			annotation.setName("XMLRootElement");//$NON-NLS-1$
			annotation.addField("name", "\"" + xmlNode.getQName().getLocalPart() + "\"");//$NON-NLS-1$
			if (xmlNode.getQName().getNamespaceURI() != null && xmlNode.getQName().getNamespaceURI().length() > 0)
				annotation.addField("namespace", "\"" + xmlNode.getQName().getNamespaceURI() + "\"");//$NON-NLS-1$
			if (xmlNode.isNillable())
				annotation.addField("nillable", "true");//$NON-NLS-1$
			wrapRec.addAnnotation(annotation);
		}

		LinkedHashMap<String,Record> map = new LinkedHashMap<String,Record>();
		map.put(wrapRec.getName().toUpperCase().toLowerCase(), wrapRec);
		Part[] parts = process(xmlNode, wrapRec, map);
		if(parts != null && parts.length == 1) {
			boolean hasChildElement = false;
			
			Field[] fields = wrapRec.getFields();
			for(Field field : fields) {
				String annoName = null;
				annoName = field.getAnnotationString();
				if(annoName == null) {
					hasChildElement = true;
				} else if(!annoName.contains("XMLAttribute")) {
					hasChildElement = true;
				}
			}
			
			if(!hasChildElement) {
				String fieldName = "simValue";
				Field field = new Field();
				field.setName(fieldName);
				SimpleType type = new SimpleType();
				type.setName("string?");//$NON-NLS-1$
				
				field.setType(type);
				wrapRec.addField(field);
			}
		}
		return parts;
	}

	public Part[] process(XMLNode node, Record wrapRec, LinkedHashMap<String,Record> recs) {
		java.util.HashMap<String,String> fieldNames = getFieldNames(node); 

		processChildAttributes(node, wrapRec, recs, fieldNames);
		processChildElements(node, wrapRec, recs, fieldNames);
		return (Part[]) recs.values().toArray(new Part[recs.values().size()]);
	}
	
	private java.util.HashMap<String,String> getFieldNames( XMLNode node ) {
		java.util.HashMap<String,String> fieldNames = new java.util.HashMap<String,String>();
		
		Iterator<ArrayList<XMLNode>> attrs = node.getChildAttributes().iterator();
		while (attrs.hasNext()) {
			ArrayList<XMLNode> child = attrs.next();
			XMLNode xmlNode = child.get(0);
			String originalName = xmlNode.getQName().getLocalPart();
			fieldNames.put(originalName.toLowerCase(), originalName);
		}
		Iterator<ArrayList<XMLNode>> elems = node.getChildElements().iterator();
		while (elems.hasNext()) {
			ArrayList<XMLNode> child = elems.next();
			XMLNode xmlNode = child.get(0);
			String originalName = xmlNode.getQName().getLocalPart();
			fieldNames.put(originalName.toLowerCase(), originalName);
		}
		return fieldNames;
	}

	public void processChildAttributes(XMLNode node, Record wrapRec, LinkedHashMap<String,Record> recs, java.util.HashMap<String,String> fieldNames) {
		Iterator<ArrayList<XMLNode>> i = node.getChildAttributes().iterator();
		while (i.hasNext()) {
			ArrayList<XMLNode> child = i.next();
			XMLNode xmlNode = child.get(0);
			String originalName = xmlNode.getQName().getLocalPart();
			String fieldName = getFieldName(originalName, wrapRec.getName(), fieldNames);
			String xmlName = (originalName.equals(fieldName)) ? null : originalName;

			Field field = new Field();
			field.setName(fieldName);
			field.setType(getType(child, recs, fieldName));

			Annotation annotation = new Annotation();
			annotation.setName("XMLAttribute");//$NON-NLS-1$
			if (xmlName != null)
				annotation.addField("name", "\"" + xmlName + "\"");//$NON-NLS-1$
			if (xmlNode.getQName().getNamespaceURI() != null && xmlNode.getQName().getNamespaceURI().length() > 0)
				annotation.addField("namespace", "\"" + xmlNode.getQName().getNamespaceURI() + "\"");//$NON-NLS-1$
			field.addAnnotation(annotation);

			wrapRec.addField(field);
		}
	}

	public void processChildElements(XMLNode node, Record wrapRec, LinkedHashMap<String,Record> recs, java.util.HashMap<String,String> fieldNames) {
		Iterator<ArrayList<XMLNode>> i = node.getChildElements().iterator();
		while (i.hasNext()) {
			ArrayList<XMLNode> child = i.next();
			XMLNode xmlNode = child.get(0);
			String originalName = xmlNode.getQName().getLocalPart();
			String fieldName = getFieldName(originalName, wrapRec.getName(), fieldNames);
			String xmlName = (originalName.equals(fieldName)) ? null : originalName;

			Field field = new Field();
			field.setName(fieldName);
			field.setType(getType(child, recs, (originalName.equals(originalName.toUpperCase()) ? fieldName.toUpperCase() : fieldName)));

			if (xmlName != null || (xmlNode.getQName().getNamespaceURI() != null && xmlNode.getQName().getNamespaceURI().length() > 0) || xmlNode.isNillable()) {
				Annotation annotation = new Annotation();
				annotation.setName("XMLElement");//$NON-NLS-1$
				if (xmlName != null)
					annotation.addField("name", "\"" + xmlName + "\"");//$NON-NLS-1$
				if (xmlNode.getQName().getNamespaceURI() != null && xmlNode.getQName().getNamespaceURI().length() > 0)
					annotation.addField("namespace", "\"" + xmlNode.getQName().getNamespaceURI() + "\"");//$NON-NLS-1$
				if (xmlNode.isNillable())
					annotation.addField("nillable", "true");//$NON-NLS-1$
				field.addAnnotation(annotation);
			}

			wrapRec.addField(field);
		}
	}

	private Type getType(ArrayList<XMLNode> nodelist, final LinkedHashMap<String,Record> recs, final String fieldName) {

		final Type[] type = new Type[1];

		if (nodelist.size() > 1) { // process array
			type[0] = processArrayType(nodelist, recs, fieldName);
		} else if (nodelist.get(0).getChildElements().size() > 0) { // process
																				// record
			type[0] = processRecordType(nodelist.get(0), recs, fieldName);
		} else if (nodelist.get(0).getChildAttributes().size() > 0) {
			type[0] = processSimpleRecordType(nodelist.get(0), recs, fieldName);
		} else {
			SimpleType t = new SimpleType();
			t.setName("string");//$NON-NLS-1$
			type[0] = t;
		}

		return type[0];
	}

	private Type processArrayType(ArrayList<XMLNode> nodelist, LinkedHashMap<String,Record> recs, String fieldName) {
		ArrayType type = new ArrayType();
		Iterator<XMLNode> i = nodelist.iterator();
		while (i.hasNext()) {
			ArrayList<XMLNode> list = new ArrayList<XMLNode>();
			list.add(i.next());
			Type elementType = getType(list, recs, fieldName);

			// if no type is set, then use the current type
			if (type.getElementType() == null) {
				type.setElementType(elementType);
			}
			// if the new type is a record and the old type is simple,
			// do a merge to set the record fields as nullable and use the
			// record as the type.
			// can happen if:
			// <Details>
			// <WeatherData />
			// <WeatherData>
			// <Day>Saturday, February 13, 2010</Day>
			// <WeatherImage>http://forecast.weather.gov/images/wtf/sct.jpg</WeatherImage>
			// <MaxTemperatureF>41</MaxTemperatureF>
			// <MinTemperatureF>23</MinTemperatureF>
			// <MaxTemperatureC>5</MaxTemperatureC>
			// <MinTemperatureC>-5</MinTemperatureC>
			// </WeatherData>
			// </Details>
			else if (elementType instanceof RecordType && type.getElementType() instanceof SimpleType) {
				String typeName = elementType.getName();
				String fn = typeName.toUpperCase().toLowerCase();
				Record rec = (Record) recs.get(fn);

				mergeRecords(new Record(), rec);
				type.setElementType(elementType);
			}
			// If the new type is simple and the old type is a record,
			// use the record but call mergerecords to set the record fields
			// as nullable.
			// can happen if:
			// <Details>
			// <WeatherData>
			// <Day>Saturday, February 13, 2010</Day>
			// <WeatherImage>http://forecast.weather.gov/images/wtf/sct.jpg</WeatherImage>
			// <MaxTemperatureF>41</MaxTemperatureF>
			// <MinTemperatureF>23</MinTemperatureF>
			// <MaxTemperatureC>5</MaxTemperatureC>
			// <MinTemperatureC>-5</MinTemperatureC>
			// </WeatherData>
			// <WeatherData />
			// </Details>
			else if (elementType instanceof SimpleType && type.getElementType() instanceof RecordType) {
				String typeName = type.getElementType().getName();
				String fn = typeName.toUpperCase().toLowerCase();
				Record rec = (Record) recs.get(fn);

				mergeRecords(new Record(), rec);
			}
		}
		return type;

	}

	private Type processRecordType(XMLNode node, LinkedHashMap<String,Record> recs, String fieldName) {
		String typeName = getTypeName(fieldName);
		RecordType type = new RecordType();
		type.setName(typeName);

		String fn = typeName.toUpperCase().toLowerCase();
		Record oldrec = (Record) recs.get(fn);
		Record rec = new Record();
		rec.setName(typeName);
		recs.put(fn, rec);

		new PartsFromXMLUtil(_msgHandler).process(node, rec, recs);

		if (oldrec != null) {
			mergeRecords(oldrec, rec);
		}

		return type;
	}

	private Type processSimpleRecordType(XMLNode node, LinkedHashMap<String,Record> recs, String fieldName) {
		String typeName = getTypeName(fieldName);
		RecordType type = new RecordType();
		type.setName(typeName);

		String fn = typeName.toUpperCase().toLowerCase();
		Record oldrec = (Record) recs.get(fn);

		Record rec = new Record();
		rec.setName(typeName);
		recs.put(fn, rec);

		// add simpleContent annotation
		Annotation annotation = new Annotation();
		annotation.setName("XMLValue");//$NON-NLS-1$
		annotation.addField("kind", "XMLStructureKind.simpleContent");//$NON-NLS-1$
		//annotation.setValue("XMLStructureKind.simpleContent");//$NON-NLS-1$
		rec.addAnnotation(annotation);

		new PartsFromXMLUtil(_msgHandler).process(node, rec, recs);

		// add value field
		Field field = new Field();
		field.setName("egl_value");//$NON-NLS-1$
		SimpleType t = new SimpleType();
		t.setName("string");//$NON-NLS-1$
		t.setNullable(true);
		field.setType(t);
		rec.addField(field);

		if (oldrec != null) {
			mergeRecords(oldrec, rec);
		}

		return type;
	}

	class XMLAttr extends XMLNode {
		public XMLAttr(Node node) {
			super(node);
		}

		public QName getQName() {
			if (qname == null) {
				String localpart = node.getNodeName();
				String prefix = "";//$NON-NLS-1$
				String namespace = null;
				int ndx = localpart.indexOf(':');
				// only get attribute namespace if it's qualified
				if (ndx != -1) {
					prefix = localpart.substring(0, ndx);
					localpart = localpart.substring(ndx + 1);
					if (prefix.length() > 0)
						namespace = lookupNamespace(node, prefix);
				}
				qname = new QName(namespace, localpart, prefix);
			}
			return qname;
		}

	}

	class XMLNode {
		protected Node node = null;
		protected QName qname = null;
		protected java.util.List<ArrayList<XMLNode>> children = null;
		protected java.util.List<ArrayList<XMLNode>> attributes = null;
		protected Boolean isNillable = null;

		public XMLNode(Node node) {
			this.node = node;
		}

		public java.util.List<ArrayList<XMLNode>> getChildElements() {
			if (this.children == null) {
				java.util.Map<String,ArrayList<XMLNode>> map = new java.util.LinkedHashMap<String,ArrayList<XMLNode>>();

				NodeList children = this.node.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					// test to see if child is an element node
					Node child = children.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						XMLNode xmlNode = new XMLNode(child);
						if (map.containsKey(xmlNode.getQName().toString())) {
							ArrayList<XMLNode> list = map.get(xmlNode.getQName().toString());
							list.add(xmlNode);
						} else {
							ArrayList<XMLNode> list = new ArrayList<XMLNode>();
							list.add(xmlNode);
							map.put(xmlNode.getQName().toString(), list);
						}
					}
				}
				this.children = new ArrayList<ArrayList<XMLNode>>(map.values());
			}
			return this.children;
		}

		public java.util.List<ArrayList<XMLNode>> getChildAttributes() {
			if (this.attributes == null) {
				java.util.Map<String,ArrayList<XMLNode>> map = new java.util.LinkedHashMap<String,ArrayList<XMLNode>>();

				NamedNodeMap children = this.node.getAttributes();
				if (children != null) {
					for (int i = 0; i < children.getLength(); i++) {
						Node child = children.item(i);
						XMLAttr xmlAttr = new XMLAttr(child);
						// if the attribute is a special xml name or is the
						// start of a
						// namespace definition, then ignore
						if (isSpecialAttribute(xmlAttr))
							continue;
						ArrayList<XMLNode> list = new ArrayList<XMLNode>();
						list.add(xmlAttr);
						map.put(xmlAttr.getQName().toString(), list);
					}
				}
				this.attributes = new ArrayList<ArrayList<XMLNode>>(map.values());
			}
			return this.attributes;
		}

		private boolean isSpecialAttribute(XMLAttr xmlAttr) {
			if (xmlAttr.getQName().getPrefix().equals("xmlns") || xmlAttr.getQName().getLocalPart().equals("xmlns"))//$NON-NLS-1$
				return true;

			if (reservedXSINames.containsKey(xmlAttr.getQName().getLocalPart())
					&& (xmlAttr.getQName().getNamespaceURI().equalsIgnoreCase("http://www.w3.org/2001/XMLSchema-instance") || xmlAttr.getQName().getPrefix()
							.equals("xsi")))//$NON-NLS-1$
				return true;

			return false;
		}

		public boolean isNillable() {
			if (isNillable == null) {
				isNillable = new Boolean(false);
				NamedNodeMap attributes = node.getAttributes();
				if (attributes != null) {
					for (int i = 0; i < attributes.getLength(); i++) {
						Node attribute = attributes.item(i);
						XMLAttr xmlAttr = new XMLAttr(attribute);

						if (xmlAttr.getQName().getLocalPart().equals("nil")//$NON-NLS-1$
								&& (xmlAttr.getQName().getNamespaceURI().equalsIgnoreCase("http://www.w3.org/2001/XMLSchema-instance") || xmlAttr.getQName()
										.getPrefix().equals("xsi"))) {//$NON-NLS-1$
							String value = getElementText(attribute);
							isNillable = new Boolean((value != null && value.equals("true")));//$NON-NLS-1$
						}
					}
				}
			}
			return isNillable.booleanValue();
		}

		private String getElementText(Node node) {
			NodeList children = node.getChildNodes();
			StringBuffer value = new StringBuffer();
			for (int j = 0; j < children.getLength(); j++) {
				Node childnode = children.item(j);
				if (childnode.getNodeType() == 3)
					value.append(childnode.getNodeValue());
			}
			return (value.toString().trim().length() == 0) ? null : value.toString().trim();
		}

		public QName getQName() {
			if (qname == null) {
				String localpart = node.getNodeName();
				String prefix = "";
				String namespace = null;
				int ndx = localpart.indexOf(':');
				if (ndx != -1) {
					prefix = localpart.substring(0, ndx);
					localpart = localpart.substring(ndx + 1);
				}
				namespace = lookupNamespace(node, prefix);
				qname = new QName(namespace, localpart, prefix);
			}
			return qname;
		}

		protected String lookupNamespace(Node node, String prefix) {
			String namespace = null;

			int type = node.getNodeType();
			switch (type) {
			case Node.ELEMENT_NODE:
				NamedNodeMap map = node.getAttributes();
				boolean bFound = false;
				if (map != null) {
					for (int i = 0; i < map.getLength(); i++) {
						Attr attribute = (Attr) map.item(i);
						// if a namespace attribute
						if (attribute.getNodeName().startsWith("xmlns")) {//$NON-NLS-1$
							String nsPrefix = "";
							int ndx = attribute.getNodeName().indexOf(':');
							if (ndx != -1) {
								nsPrefix = attribute.getNodeName().substring(ndx + 1);
							}
							if (prefix.equals(nsPrefix)) {
								namespace = attribute.getNodeValue();
								bFound = true;
								break;
							}
						}
					}
				}
				if (!bFound) {
					namespace = lookupNamespace(node.getParentNode(), prefix);
				}
				break;
			case Node.DOCUMENT_NODE:
			case Node.ENTITY_NODE:
			case Node.NOTATION_NODE:
			case Node.DOCUMENT_FRAGMENT_NODE:
			case Node.DOCUMENT_TYPE_NODE:
				break;
			case Node.ATTRIBUTE_NODE:
				namespace = lookupNamespace(((Attr) node).getOwnerElement(), prefix);
				break;
			default: {
				System.out.println("We are here!!, type=" + node.getNodeType());
			}
			}
			return namespace;
		}
	}
}
