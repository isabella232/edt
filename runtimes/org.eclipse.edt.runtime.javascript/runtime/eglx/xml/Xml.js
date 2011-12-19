/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
egl.xmllib_xsi_url = "http://www.w3.org/2001/XMLSchema-instance";
/**
 * XmlLib
 */
egl.defineClass(
    'eglx.xml', 'XmlLib',
{
	"constructor" : function() {
	}
});
egl.eglx.xml.XmlLib["convertToXML"] = function( /*value*/value, /*boolean*/ doc) {
	value = egl.unboxAny(value);
	this.validateXMLObject(value);
	var namespaces = {};
	namespaces.xmlns_map = {};
	namespaces.count = 0;
	var xml = this.toXML(value, namespaces, null); // top level records
	xml = this.addNamespacesToXML(namespaces, xml);
	return typeof doc == "boolean" && doc ? "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml : xml;
};
egl.eglx.xml.XmlLib["toXML"] = function( /*value*/value, /*map*/namespaces, /*FieldInfo*/fieldInfo) {
	var s = [];
	//TODO check to add type

	if(value !== null && typeof value === "object" && "eze$$value" in value && "eze$$signature" in value){
		s.push(this.toXML(egl.unboxAny( value ), namespaces, fieldInfo));
	}
	else if (value !== null && typeof value === "object" && "eze$$getFieldInfos" in value) {
		s.push(this.eglClassToXML(value, namespaces, fieldInfo));
	} else if (value !== null && typeof value === "object" && value instanceof egl.eglx.lang.EDictionary) {
		s.push(this.dictionaryToXML(value, namespaces, fieldInfo));
	} else if (value !== null && typeof value === "object" && value instanceof Array) {
		s.push(this.arrayToXML(value, namespaces, fieldInfo));
	} else if (value === null) {
		s.push(this.nullToXML(value, namespaces, fieldInfo));
	} else if (value !== null && typeof value === "object" && value instanceof egl.eglx.lang.Enumeration) {
		s.push(this.primitiveToXML(value.value, namespaces, fieldInfo));
	} else {
		s.push(this.primitiveToXML(value, namespaces, fieldInfo));
	}
	return s.join('');
};
egl.eglx.xml.XmlLib["nullToXML"] = function( /*value*/value, /*map*/namespaces, /*FieldInfo*/fieldInfo) {
	var xmlStyle = fieldInfo.annotations["XMLStyle"];
	var s = [];
	if (xmlStyle.nillable) {
		var xmlName = xmlStyle.name;
		var xmlNamespace = xmlStyle.namespace;
		var prefix = "";
		if (xmlNamespace != null) {
			prefix = this.addNamespace(namespaces, xmlNamespace);
		}
		if (xmlName == null) {
			xmlName = value;
		}
		xmlName = prefix + xmlName;
		//skip field if fieldInfo.xmlStyle is XMLAttribute or  fieldInfo.xmlStyle is XMLElement.nillable == true
		s.push("<" + xmlName + " xsi:nil=\"true\"/>");
		egl.eglx.xml.XmlLib.addXSINamespace(namespaces);
	}
	return s.join('');
};
egl.eglx.xml.XmlLib["primitiveToXML"] = function( /*value*/value, /*map*/namespaces, /*FieldInfo*/fieldInfo) {
	var xmlStyle = fieldInfo.annotations["XMLStyle"];
	var xmlName = xmlStyle.name;
	var xmlNamespace = xmlStyle.namespace;
	var prefix = "";
	if (xmlNamespace != null) {
		prefix = this.addNamespace(namespaces, xmlNamespace);
	}
	if (xmlName == null) {
		xmlName = value;
	}
	xmlName = prefix + xmlName;
	var fieldValue = value;
	var kind = fieldInfo.eglSignature.charAt(0) !== '?'? fieldInfo.eglSignature.charAt(0) : fieldInfo.eglSignature.charAt(1);
	switch (kind) {
		case 'K':
			fieldValue = egl.eglx.lang.StringLib.format(fieldValue, "yyyy-MM-dd");
			break;
	//unsupported 0.7	case 'L':
	//		fieldValue = egl.eglx.lang.StringLib.format(fieldValue, "HH:mm:ss");//FIXME function doesn't exist
			break;
		case 'L':
			break;
		case 'J':
			fieldValue = egl.eglx.lang.StringLib.format(fieldValue, "yyyy-MM-dd HH:mm:ss");
			break;
	}
	if (value instanceof egl.javascript.BigDecimal) {
		fieldValue = field.toString();
	}
	fieldValue = (typeof (fieldValue) != "string") ? fieldValue : fieldValue.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
	if (xmlStyle instanceof egl.eglx.xml.binding.annotation.XMLAttribute) {
		return " " + xmlName + "=\"" + fieldValue + "\"";
	} else if(fieldInfo.annotations["XMLValue"] === undefined){
		return "<" + xmlName + ">" + fieldValue + "</" + xmlName + ">";
	}
	else{
		return fieldValue;
	}

};
egl.eglx.xml.XmlLib["dictionaryToXML"] = function( /*value*/value, /*map*/namespaces, /*FieldInfo*/fieldInfo) {
	var xmlStyle = fieldInfo.annotations["XMLStyle"];
	var xmlName = xmlStyle.name;
	var s = [ "<" + xmlName + ">" ];
	for (f in value) {
		if (!f.match(/^eze\$\$/) && (typeof this[f] != "function")) {
			//create an fieldInfo for each field
			var xmlElem = new egl.eglx.xml.binding.annotation.XMLElement(f, null, true);
			annotations = {};
			var fieldInfo = new egl.eglx.xml.binding.annotation.FieldInfo(
					null, null, fieldInfo.eglSignature,
					fieldInfo.eglType, null);
			this.toXML(f, namespaces, fieldInfo);
		}
	}
	s.push("</" + xmlName + ">");
	return s.join('');
};
egl.eglx.xml.XmlLib["arrayToXML"] = function( /*value*/value, /*map*/namespaces, /*FieldInfo*/fieldInfo) {
	var xmlStyle = fieldInfo != undefined && fieldInfo != null ? fieldInfo.annotations["XMLStyle"] : null;
	var xmlArray = fieldInfo.annotations["XMLArray"];
	//get the node name from the array[0]
	//if the xmlArray.length > 1 create new xmlArray[xmlArray.length -1] = xmlArray[1]
	//create new fieldInfo
	var xmlName;
	var wrapped = false;
	var names = null;
	if (xmlArray != undefined && xmlArray != null) {
		if (xmlArray.names != undefined
				&& xmlArray.names != null
				&& xmlArray.names.length > 0) {
			names = xmlArray.names.slice(0);
			xmlName = xmlArray.names[0];
		}
		if (xmlArray.wrapped != undefined
				&& xmlArray.wrapped != null) {
			wrapped = xmlArray.wrapped;
		}
	}
	if (xmlName == null) {
		xmlName = value;
	}
	if (names == null) {
		names = new Array();
		names[0] = "item";
	}
	var s = [];
	fieldInfo = egl.clone(fieldInfo);
	fieldInfo.eglSignature = fieldInfo.eglSignature.slice(1);
	if (wrapped) {
		s = [ "<" + xmlStyle.name + ">" ];
		fieldInfo.annotations["XMLStyle"].name = names[0];
	} else {
		xmlName = xmlStyle.name;
		fieldInfo.annotations["XMLStyle"].name = xmlStyle.name;
	}
	for (var idx = 0; idx < value.length; idx++) {
		if (value[0] instanceof Array) {
			fieldInfo.annotations["XMLArray"] = new egl.eglx.xmlXMLArray( true, names.slice(1));
		}
		s.push(this.toXML(value[idx], namespaces, fieldInfo));
	}
	if (wrapped) {
		s.push("</" + xmlStyle.name + ">");
	}
	return s.join('');
};
egl.eglx.xml.XmlLib["eglClassToXML"] = function(/*value*/value, /*map*/namespaces, /*FieldInfo*/fieldInfo) {
	var xmlStyle = fieldInfo != undefined && fieldInfo != null ? fieldInfo.annotations["XMLStyle"] : null;
	var partAnntations = value.eze$$getAnnotations();
	var xmlName = xmlStyle != null ? xmlStyle.name : null;
	var prefix = xmlStyle != null && xmlStyle.namespace != null ? 
					this.addNamespace(namespaces, xmlStyle.namespace) : "";
	if (xmlStyle === null && partAnntations != undefined
			&& partAnntations["XMLRootElement"] != undefined
			&& partAnntations["XMLRootElement"] != null) {
		if (partAnntations["XMLRootElement"].namespace != undefined
				&& partAnntations["XMLRootElement"].namespace != null) {
			prefix = this.addNamespace(namespaces, partAnntations["XMLRootElement"].namespace);
		}
		if (xmlName == null
				&& partAnntations["XMLRootElement"].name != undefined
				&& partAnntations["XMLRootElement"].name != null) {
			xmlName = partAnntations["XMLRootElement"].name;
		}
	}
	var isSimpleContent = partAnntations["XMLValue"] !== undefined &&
													partAnntations["XMLValue"] !== null &&
													partAnntations["XMLValue"].kind == egl.eglx.xml.binding.annotation.XMLStructureKind.simpleContent;
	xmlName = prefix + xmlName;
	var fieldInfos = value.eze$$getFieldInfos();
	var s = [ "<" + xmlName ];
	if (fieldInfos) {
		for ( var idx = 0; idx < fieldInfos.length; idx++) {
			if (fieldInfos[idx].annotations["XMLStyle"] instanceof egl.eglx.xml.binding.annotation.XMLAttribute) {
				var fieldValue;
				if (fieldInfos[idx].getterFunction instanceof Function) {
					fieldValue = value[fieldInfos[idx].getterFunction].apply();
				} else {
					fieldValue = value[fieldInfos[idx].getterFunction];
				}
				s.push(this.toXML(fieldValue, namespaces, fieldInfos[idx]));
			}
		}
		s.push(">");
		for ( var idx = 0; idx < fieldInfos.length; idx++) {
			if (!(fieldInfos[idx].annotations["XMLStyle"] instanceof egl.eglx.xml.binding.annotation.XMLAttribute)) {
				if (fieldInfos[idx].getterFunction instanceof Function) {
					fieldValue = value[fieldInfos[idx].getterFunction].apply();
				} else {
					fieldValue = value[fieldInfos[idx].getterFunction];
				}
				if(isSimpleContent){
					var annotations = new Array();
					annotations["XMLStyle"] = fieldInfos[idx].annotations["XMLStyle"];
					annotations["XMLValue"] = partAnntations["XMLValue"];
					var newFieldInfo = new egl.eglx.services.FieldInfo(fieldInfos[idx].getterFunction, fieldInfos[idx].setterFunction, fieldInfos[idx].eglSignature, fieldInfos[idx].eglType, annotations);
					s.push(this.toXML(fieldValue, namespaces, newFieldInfo));
				}
				else{
					s.push(this.toXML(fieldValue, namespaces, fieldInfos[idx]));
				}
			}
		}

	} else {
		s.push(">");
	}
	s.push("</" + xmlName + ">");
	return s.join('');
};
egl.eglx.xml.XmlLib["validateXMLObject"] = function(/*any*/object) {
	object = egl.unboxAny(object);
	if (object === null || typeof object !== "object" || !("eze$$getFieldInfos" in object)) {
		var field = object;
		if (object == undefined || object == null) {
			field = "undefined";
		}
		if (typeof object === "object" && "eze$$signature" in object) {
			field = egl.typeName(object.eze$$signature);
		}
		else{
			field = typeof object;
		}
		throw egl.createRuntimeException("CRRUI2108E", [ field ]);
	}
};
egl.eglx.xml.XmlLib["addNamespace"] = function(namespaces, namespace) {
	if (namespaces.xmlns_map[namespace])
		return namespaces.xmlns_map[namespace] + ":";

	namespaces.count++;
	var prefix = "ns" + namespaces.count;
	namespaces.xmlns_map[namespace] = prefix;
	return prefix + ":";
};
egl.eglx.xml.XmlLib["addXSINamespace"] = function(namespaces) {
	namespaces.xmlns_map[egl.xmllib_xsi_url] = "xsi";
};
egl.eglx.xml.XmlLib["addNamespacesToXML"] = function(namespaces, xml) {
	var s = [];
	for (f in namespaces.xmlns_map) {
		s.push(" xmlns:" + namespaces.xmlns_map[f] + "=\"" + f + "\"");
	}

	var ndx = xml.indexOf(">");
	return xml.substring(0, ndx) + s.join('') + xml.substring(ndx);
};
egl.eglx.xml.XmlLib["convertFromXML"] = function(/*String*/xml, /*any*/eglObj) {
	if (typeof (xml) != "string") {
		throw egl.createRuntimeException("CRRUI2030E", typeof (xml));
	}
	eglObj = egl.unboxAny(eglObj);
	this.validateXMLObject(eglObj);
	xml = egl.trim(xml);
	var dom = null;
	try {
		if (!egl.IE || egl.IEVersion >= 9) {
			dom = (new DOMParser()).parseFromString(xml, "application/xml");
		} else {
			if (egl.IE) {
				dom = new ActiveXObject("MSXML2.DOMDocument");
				dom.loadXML(xml);
			}
		}
	} catch (e) {
		throw egl.createRuntimeException("CRRUI2031E", e);
	}
	eglObj = this.eglFromXML(dom.documentElement, eglObj);
	return eglObj;
};
egl.eglx.xml.XmlLib["eglFromXML"] = function( /*node*/parentElement, /*egl rt object*/eglObj) {
	if(eglObj !== null && typeof eglObj === "object" && "eze$$value" in eglObj && "eze$$signature" in eglObj){
		return egl.boxAny(this.eglFromXML(parentElement, egl.unboxAny( eglObj )));
	}
	else if (eglObj !== null && typeof eglObj === "object" && "eze$$getFieldInfos" in eglObj) {
		return this.eglClassFromXML(parentElement, eglObj, null);
	}
};
egl.eglx.xml.XmlLib["fromXML"] = function( /*node*/elements, /*FieldInfo*/fieldInfo) {
	//FIXME how do I handle boxing
	var eglObj;
	//TODO look up type
	var type = null;
	if (elements != undefined && elements != null) {
		type = this.getAttributeNS(elements, "type", null);
		if (type != null) {
			//TODO look up the type call from XML
			//create the type from the xml?
		}
	} else {
		return null;
	}
	if ((eglObj == undefined || eglObj == null) && fieldInfo != null) {
		eglObj = new fieldInfo.eglType();
	}
	if(elements === undefined || elements === null ||
			(elements instanceof Array && elements.length === 0)){
		return null;
	}
	if (fieldInfo != null && (eglObj == undefined || eglObj == null)
			&& (elements[0].children == null || elements[0].children.length == 0)) {
		eglObj = this.primitiveFromXML(elements[0], fieldInfo);
	}
	//first check for array
	if (eglObj !== null && typeof eglObj === "object" && this.isEglArray(fieldInfo)) {
		return this.arrayFromXML(elements, eglObj, fieldInfo);
	} else if (eglObj !== null && typeof eglObj === "object" && "eze$$getFieldInfos" in eglObj) {
		return this.eglClassFromXML(elements[0], eglObj, fieldInfo);
	} else if (eglObj !== null && typeof eglObj === "object" && eglObj instanceof egl.eglx.lang.EDictionary) {
		return this.dictionaryFromXML(elements[0], eglObj, fieldInfo);
	} else if (eglObj !== null && typeof eglObj === "object" && eglObj instanceof egl.eglx.lang.Enumeration) {
		return this.enumerationFromXML(elements[0], eglObj, fieldInfo);
	} else if ((eglObj == undefined || eglObj == null)
			&& (elements[0].children != null && elements[0].children.length > 1)) {
		return this.dictionaryFromXML(elements[0], new egl.eglx.lang.EDictionary(), fieldInfo);
	} else {
		return this.primitiveFromXML(elements[0], fieldInfo);
	}
};
egl.eglx.xml.XmlLib["isEglArray"] = function(fieldInfo) {
	return typeof fieldInfo === "object" && fieldInfo.eglSignature.charAt(0) === "[";
};
egl.eglx.xml.XmlLib["isArray"] = function(/*nodes*/children) {
	return children != undefined && children instanceof Array
			&& children.length > 1 && children[0].localName
			&& children[1].localName;
};
egl.eglx.xml.XmlLib["primitiveFromXML"] = function( /*node*/element, /*FieldInfo*/fieldInfo) {
	if(element === undefined || element === null){
		return null;
	}
	else{
		return this.newPrimitiveFromXml(this.getElementText(element), fieldInfo);
	}
};
egl.eglx.xml.XmlLib["newPrimitiveFromXml"] = function( /*node*/value, /*FieldInfo*/fieldInfo) {
	if (value == null) {
		return null;
	} else {
		var nullable;
		var kind;

		var firstCharIdx = 0;
		var firstChar = fieldInfo == null ? 'S' : fieldInfo.eglSignature.charAt(0);
		if (firstChar !== '?') {
			nullable = '';
			kind = firstChar;
		} else {
			nullable = '?';
			kind = fieldInfo == null ? 'S' : fieldInfo.eglSignature.charAt(1);
			firstCharIdx = 1;
		}

		var value;
		switch (kind) {
		case 'S':
		case 's':
			break;

		case 'K':
			value = egl.stringToDate(value, "yyyy-MM-dd");
			break;

		case 'L':
			value = egl.stringToTime(value, "HH:mm:ss");
			break;

		case 'J':
			value = egl.stringToTimeStamp(value, "yyyy-MM-dd HH:mm:ss");
			break;

		case 'I':
			value = egl.eglx.lang.EInt32.fromEString(value);
			break;

		case 'i':
			value = egl.eglx.lang.EInt16.fromEString(value);
			break;

		case '0':
			value = egl.eglx.lang.EBoolean.fromEString(value);
			break;

		case 'F':
			value = egl.eglx.lang.EFloat64.fromEString(value);
			break;

		case 'f':
			value = egl.eglx.lang.EFloat32.fromEString(value);
			break;

		case 'B':
			value = egl.eglx.lang.EInt64.fromEString(value);
			break;

		case 'N':
			var colon = fieldInfo.eglSignature.indexOf(':');
			value = egl.eglx.lang.EDecimal.fromEString(jsonObject.toString(), 
						egl.convertStringToSmallint(fieldInfo.eglSignature.substring(colon + 1, fieldInfo.eglSignature.indexOf(';'))),
						egl.javascript.BigDecimal.prototype.NINES[egl.convertStringToSmallint(fieldInfo.eglSignature.substring(firstCharIdx + 1, colon)) - 1]);
			break;

		case 'd':
			var colon = fieldInfo.eglSignature.indexOf(':');
			value = egl.eglx.lang.EDecimal.fromEString(jsonObject.toString(), 
					egl.convertStringToSmallint(fieldInfo.eglSignature.substring(colon + 1, fieldInfo.eglSignature.indexOf(';'))),
					egl.javascript.BigDecimal.prototype.NINES[egl.convertStringToSmallint(fieldInfo.eglSignature.substring(firstCharIdx + 1, colon)) - 1]);
			break;
		case '9':
			var colon = fieldInfo.eglSignature.indexOf(':');
			value = egl.eglx.lang.EDecimal.fromEString(jsonObject.toString(), 
					egl.convertStringToSmallint(fieldInfo.eglSignature.substring(colon + 1, fieldInfo.eglSignature.indexOf(';'))),
					egl.javascript.BigDecimal.prototype.NINES[egl.convertStringToSmallint(fieldInfo.eglSignature.substring(firstCharIdx + 1, colon)) - 1]);
			break;
		}
	}
	return value;
};
egl.eglx.xml.XmlLib["enumerationFromXML"] = function( /*node*/element, /*egl rt object*/eglObj, /*FieldInfo*/fieldInfo) {
	return egl.eglx.services.$ServiceRT.convertToEnum(this.getElementText(element), fieldInfo.eglType);
};
egl.eglx.xml.XmlLib["dictionaryFromXML"] = function( /*node*/parentElement, /*egl rt object*/eglObj, /*FieldInfo*/fieldInfo) {
	if (eglObj == null) {
		eglObj = new egl.eglx.lang.EDictionary(true, true);
	}
	var element = this.getChildElements(parentElement);
	for (elementField in element) {
		//TODO
		//create an fieldInfo for each field, the name is the element name
		this.fromXML([elementField], null);
	}
	return eglObj;
};
egl.eglx.xml.XmlLib["arrayFromXML"] = function( /*node*/parentElement, /*egl rt object*/eglObj, /*FieldInfo*/fieldInfo) {
	var xmlStyle = fieldInfo != undefined && fieldInfo != null ? fieldInfo.annotations["XMLStyle"]
			: null;
	if (eglObj == null && fieldInfo != null) {
		//TODO create a new array;
	}
	var xmlArray = fieldInfo != undefined && fieldInfo != null ? fieldInfo.annotations["XMLArray"]
			: null;
	var xmlName;
	var wrapped = false;
	var names = null;
	if (xmlArray != undefined && xmlArray != null) {
		if (xmlArray.names != undefined
				&& xmlArray.names != null
				&& xmlArray.names.length > 0) {
			names = xmlArray.names.slice(0);
			xmlName = xmlArray.names[0];
		}
		if (xmlArray.wrapped != undefined
				&& xmlArray.wrapped != null) {
			wrapped = xmlArray.wrapped;
		}
	}
	if (names == null) {
		names = new Array();
		names[0] = "item";
	}
	var arrayElements = parentElement;
	if (wrapped) {//FIXME I don't care what the child element names are
		arrayElements = this.getChildElementNS(parentElement[0], names[0], xmlStyle.namespace);
	}
	fieldInfo = egl.clone(fieldInfo);
	fieldInfo.eglSignature = fieldInfo.eglSignature.slice(1);
	fieldInfo.annotations["XMLStyle"].name = wrapped ? names[0] : xmlStyle.name;
	var array = new Array();
	for (var idx = 0; idx < arrayElements.length; idx++) {
		if (this.isEglArray(fieldInfo)) {
			fieldInfo.annotations["XMLArray"] = new egl.eglx.xmlXMLArray( true, names.slice(1));
		}
		array[idx] = this.fromXML([arrayElements[idx]], fieldInfo);
	}
	return array;
};
egl.eglx.xml.XmlLib["eglClassFromXML"] = function(/*node*/recElement, /*egl rt object*/eglObj, /*FieldInfo*/fieldInfo) {
	var xmlStyle = fieldInfo != undefined && fieldInfo != null ? fieldInfo.annotations["XMLStyle"] : null;
	var partAnntations = eglObj.eze$$getAnnotations();
	var xmlName = xmlStyle != null ? xmlStyle.name : null;
	var namespace = xmlStyle != null ? xmlStyle.namespace : null;
	var prefix = "";
	if (xmlStyle === null && partAnntations != undefined
			&& partAnntations["XMLRootElement"] != undefined
			&& partAnntations["XMLRootElement"] != null) {
		if (namespace == null
				&& partAnntations["XMLRootElement"].namespace != undefined
				&& partAnntations["XMLRootElement"].namespace != null) {
			namespace = partAnntations["XMLRootElement"].namespace;
		}
		if (xmlName == null
				&& partAnntations["XMLRootElement"].name != undefined
				&& partAnntations["XMLRootElement"].name != null) {
			xmlName = partAnntations["XMLRootElement"].name;
		}
	}
	var isSimpleContent = partAnntations["XMLValue"] !== undefined &&
							partAnntations["XMLValue"] !== null &&
							partAnntations["XMLValue"].kind == egl.eglx.xml.binding.annotation.XMLStructureKind.simpleContent;
	var fieldInfos = eglObj.eze$$getFieldInfos();
	if (fieldInfos) {
		var value;
		for ( var idx = 0; idx < fieldInfos.length; idx++) {
			xmlStyle = fieldInfos[idx].annotations["XMLStyle"];
			var setValue = true;
			if (xmlStyle instanceof egl.eglx.xml.binding.annotation.XMLAttribute) {
				value = this.newPrimitiveFromXml(this.getAttributeNS(recElement, xmlStyle.name, xmlStyle.namespace), fieldInfos[idx]);
			} else if(!isSimpleContent){
				value = this.fromXML(this.getChildElementNS(recElement, xmlStyle.name, xmlStyle.namespace), fieldInfos[idx]);
			} else{
				value = this.newPrimitiveFromXml(this.getElementText(recElement), fieldInfos[idx]);
			}
			if(setValue){
				if (fieldInfos[idx].setterFunction instanceof Function) {
					fieldInfos[idx].setterFunction.apply(value);
				} else {
					eglObj[fieldInfos[idx].setterFunction] = value;
				}
			}
		}
	}
	return eglObj;
};
egl.eglx.xml.XmlLib["getChildElements"] = function(/*DOM Element*/element) {
	var children = element.childNodes;
	var elements = {};
	// loop through the immediate child elements looking for matches (2+ matches is an array)
	for ( var n = 0; n < children.length; n++) {
		// test From see if child is an element node
		if (children[n].nodeType == 1) {
			var pairsArray = children[n].tagName.split(':'); // IE doesn't support localName
			var localName = pairsArray[pairsArray.length - 1];
			if (!elements[localName]) {
				elements[localName] = [];
			}
			elements[localName][elements[localName].length] = children[n];
		}
	}
	var nodelists = [];
	var i = 0;
	for (f in elements) {
		nodelists[i] = elements[f];
		i++;
	}
	return nodelists;
};
egl.eglx.xml.XmlLib["getChildElementNS"] = function(/*DOM Element*/element, /*string*/name, /*string*/namespace) {
	namespace = (!namespace) ? this.defaultNamespace() : namespace;

	var children = element.childNodes;
	var elements = [];
	var i = 0;
	// loop through the immediate child elements looking for matches (2+ matches is an array)
	for ( var n = 0; n < children.length; n++) {
		// test to see if child is an element node
		if (children[n].nodeType == 1) {
			var pairsArray = children[n].tagName.split(':'); // IE doesn't support localName
			var localName = pairsArray[pairsArray.length - 1];
			if (localName == name && children[n].namespaceURI == namespace) {
				elements[i] = children[n];
				i++;
			}
		}
	}
	return elements;
};
egl.eglx.xml.XmlLib["getAttributeNS"] = function(/*DOM Element*/element, /*string*/name, /*string*/namespace) {
	namespace = (!namespace) ? this.defaultNamespace() : namespace;

	var attrs = element.attributes;
	if (attrs != null) {
		// loop through attributes
		for ( var i = 0; i < attrs.length; i++) {
			var attr = attrs.item(i);
			var pairsArray = attr.name.split(':'); // IE doesn't support localName
			var localName = pairsArray[pairsArray.length - 1];
			if (localName == name && attr.namespaceURI == namespace) {
				return attr.value;
			}
		}
	}
	return null;
};
egl.eglx.xml.XmlLib["getElementText"] = function(element) {
	var children = element.childNodes;
	var texts = [];
	var i = 0;
	for ( var n = 0; n < children.length; n++) {
		if (children[n].nodeType == 3 || children[n].nodeType == 4) {
			texts[i] = children[n].nodeValue;
			i++;
		}
	}
	return texts.join('');
};
//FIXME who uses this???
egl.eglx.xml.XmlLib["getElementTyped"] = function(element) {
	var text = this.getElementText(element);
	var value = text;
	var ltext = text.toLowerCase();
	if ("true" == ltext || "false" == ltext) {
		value = Boolean("true" == ltext);
	} else {
		var type = this.determineType(text);
		var any = egl.boxAny(text, "S;");
		value = egl.convertAnyToType(any, type);
	}
	return value;
};
egl.eglx.xml.XmlLib["determineType"] = function(/*string*/text) {
	text = text.trim();
	var isNumeric = true;
	var isFloat = false;
	var isDecimal = false;
	for ( var i = 0; i < text.length; i++) {
		var c = text.charAt(i);
		if (c == '.') {
			if (isDecimal) // more than one
			{
				isNumeric = false;
				break;
			}
			isDecimal = true;
		} else if (c == '+' || c == '-') {

		} else if (c == 'e' || c == 'E') {
			if (isFloat) // more than one
			{
				isNumeric = false;
				break;
			}
			isFloat = true;
		} else if (c < '0' || c > '9') // not numeric
		{
			isNumeric = false;
			break;
		}
	}
	var type = "S;";
	if (isNumeric) {
		if (isFloat) {
			type = "F;";
		} else if (isDecimal) {
			var bd = new egl.javascript.BigDecimal(text);
			var decimals = bd.scale() < 0 ? 0 : bd.scale();
			type = "d32:" + decimals + ";";
		} else {
			type = "I;";
		}
	}
	return type;
};
egl.eglx.xml.XmlLib["defaultNamespace"] = function() {
	if (egl.IE) {
		return "";
	} else {
		return null;
	}
};