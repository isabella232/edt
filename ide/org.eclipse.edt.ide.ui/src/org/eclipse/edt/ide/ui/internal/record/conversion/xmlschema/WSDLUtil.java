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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.xerces.xs.StringList;
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
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;

public class WSDLUtil {
	private static int k16 = 16384;

	public static boolean isStandardArray(XSTypeDefinition xsdType) {
		boolean rc = false;
		if (xsdType.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
			XSComplexTypeDefinition complexType = (XSComplexTypeDefinition) xsdType;
			XSParticle particle = complexType.getParticle();
			if (particle != null) {
				XSTerm term = particle.getTerm();
				if (term != null && term instanceof XSModelGroup) {
					XSObjectList list = ((XSModelGroup) particle.getTerm()).getParticles();
					XSObjectList attributes = complexType.getAttributeUses();
					if (list.getLength() == 1 && attributes.getLength() == 0) {
						XSParticle innerParticle = (XSParticle) list.item(0);
						if (innerParticle.getMaxOccursUnbounded() || innerParticle.getMaxOccurs() > 1) {
							if (innerParticle.getTerm() instanceof XSElementDeclaration) {
								XSElementDeclaration element = (XSElementDeclaration) innerParticle.getTerm();
								if (element != null) {
									rc = true;
								}
							}
						}
					}
				}
			}
		}
		return rc;
	}

	public static boolean isString(XSSimpleTypeDefinition simpleType) {
		return XSConstants.STRING_DT == simpleType.getBuiltInKind()
				&& (isString(getLength(simpleType), getMaxLength(simpleType)) || getItemLength(simpleType) >= k16);
	}

	private static boolean isString(int length, int maxLength) {
		return length == -1 && maxLength == -1;
	}
	
	public static boolean isTimeStamp(XSSimpleTypeDefinition simpleType) {
		return XSConstants.DATETIME_DT == simpleType.getBuiltInKind();
	}

	public static boolean isUnicode(XSSimpleTypeDefinition simpleType) {
		// no collapse and a length or a max with a min
		return XSConstants.STRING_DT == simpleType.getBuiltInKind()
				&& isUnicode(hasCollapse(simpleType), getLength(simpleType), getMaxLength(simpleType), getMinLength(simpleType));
	}

	private static boolean isUnicode(boolean hasCollapse, int length, int maxLength, int minLength) {
		// no collapse and a length or a max with a min
		return !hasCollapse && (length != -1 || (maxLength != -1 && minLength != -1 && (maxLength == minLength)));
	}

	public static boolean isLimitedString(XSSimpleTypeDefinition simpleType) {
		// no collapse and a length or a max with a min
		return XSConstants.STRING_DT == simpleType.getBuiltInKind() && !isString(getLength(simpleType), getMaxLength(simpleType))
				&& !isUnicode(hasCollapse(simpleType), getLength(simpleType), getMaxLength(simpleType), getMinLength(simpleType));
	}

	public static boolean hasCollapse(XSSimpleTypeDefinition simpleType) {
		boolean result = false;
		if (simpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_WHITESPACE)) {
			result = ("collapse".equalsIgnoreCase(simpleType.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_WHITESPACE)));//$NON-NLS-1$
		}
		return result;
	}

	public static int getLength(XSSimpleTypeDefinition simpleType) {
		int length = -1;
		if (simpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_LENGTH)) {
			try {
				length = Integer.parseInt(simpleType.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_LENGTH));
			} catch (Exception e) {
			}
		}
		return length;
	}

	public static int getItemLength(XSSimpleTypeDefinition simpleType) {
		int length = 0;
		int maxLength = 0;
		if (getLength(simpleType) != -1) {
			length = getLength(simpleType);
		}
		if (getMaxLength(simpleType) != -1) {
			maxLength = getMaxLength(simpleType);
		}
		return length > maxLength ? length : maxLength;
	}

	public static int getMaxLength(XSSimpleTypeDefinition simpleType) {
		int maxLength = -1;
		if (simpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_MAXLENGTH)) {
			try {
				maxLength = Integer.parseInt(simpleType.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MAXLENGTH));
			} catch (Exception e) {
			}
		}
		return maxLength;
	}

	public static int getMinLength(XSSimpleTypeDefinition simpleType) {
		int minLength = -1;
		if (simpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_MINLENGTH)) {
			try {
				minLength = Integer.parseInt(simpleType.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MINLENGTH));
			} catch (Exception e) {
			}
		}
		return minLength;
	}

	public static int getTotalDigits(XSSimpleTypeDefinition simpleType) {
		int totalDigits = -1;
		if (simpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_TOTALDIGITS)) {
			try {
				totalDigits = Integer.parseInt(simpleType.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_TOTALDIGITS));
			} catch (Exception e) {
			}
		}
		return totalDigits;
	}

	public static String eglPattern(XSSimpleTypeDefinition simpleType) {
		if (!simpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_PATTERN)) {
			return "";
		}
		StringList patterns = simpleType.getLexicalPattern();
		if (patterns == null || patterns.getLength() == 0 || patterns.item(0) == null || patterns.item(0).length() == 0) {
			return "";
		}

		String wsdlPattern = patterns.item(0);
		StringBuffer buf = new StringBuffer();
		if (wsdlPattern != null && wsdlPattern.length() > 0) {
			buf.append(wsdlPattern);
		}
		int pos = 0;
		// remove any whitespace
		while (pos < buf.length()) {
			if (Character.isWhitespace(buf.charAt(pos))) {
				buf.deleteCharAt(pos);
			} else {
				pos++;
			}
		}
		// typical pattern
		// \\p\\{Nd}{1,3}D\\p{Nd}{1,3}H\\p{Nd}{6}M\\p{Nd}{1,2}S
		StringBuffer eglPattern = new StringBuffer();
		wsdlPattern = buf.toString().toUpperCase();
		if (wsdlPattern != null && wsdlPattern.length() > 0 && wsdlPattern.charAt(0) == 'P') {
			boolean secondInterval = wsdlPattern.indexOf('Y') == -1
					&& (wsdlPattern.indexOf('T') != -1 || wsdlPattern.indexOf('D') != -1 || wsdlPattern.indexOf('H') != -1 || wsdlPattern.indexOf('S') != -1);
			boolean singleM = wsdlPattern.indexOf('M') == wsdlPattern.lastIndexOf('M');
			Hashtable<String,Integer> types = new Hashtable<String,Integer>();
			String type;
			int idx;
			Integer digit;
			String[] parser = buf.toString().split("\\\\p");//$NON-NLS-1$
			for (int i = 0; i < parser.length; i++) {
				digit = null;
				type = null;
				parser[i] = parser[i];
				if (parser[i].length() > 0) {
					// if pattern has a {Nd} it's followed by the length
					if (parser[i].indexOf("{Nd}") != -1) {//$NON-NLS-1$
						// split it into Nd, length, and type
						String[] digitParser = parser[i].split("}");
						if (digitParser.length > 1) {
							// the type is the last element in the array
							type = digitParser[2];
							// the length can be in the form {x} or {x,y}
							if ((idx = digitParser[1].indexOf(',')) != -1) {
								// extract starting after the ,
								digitParser[1] = digitParser[1].substring(idx + 1);
							} else {
								// remove the leading {
								digitParser[1] = digitParser[1].substring(1);
							}
							// convert
							try {
								digit = new Integer(Integer.parseInt(digitParser[1]));
							} catch (NumberFormatException nfe) {
								digit = new Integer(0);
							}

						}
					} else if (parser[i].indexOf("\\d") != -1) {//$NON-NLS-1$
						// for \d split on the \d, the number of \d's - 1 == is
						// the digit count
						String[] digitParser = parser[i].split("\\\\d");//$NON-NLS-1$
						if (digitParser.length > 1) {
							// the type is the last element in the array only
							// take the last char
							type = digitParser[digitParser.length - 1].substring(digitParser[digitParser.length - 1].length() - 1);
							digit = new Integer(digitParser.length - 1);
						}
					}

					if (digit != null) {
						if (types.get(type) != null || (secondInterval && type.charAt(0) == 'M' && singleM)) {
							type = type.toLowerCase();
						}
						types.put(type, digit);
					}
				}
			}

			if (secondInterval) {
				eglPattern.append(convertType(types.get("D"), 'd'));//$NON-NLS-1$
				eglPattern.append(convertType(types.get("H"), 'H'));//$NON-NLS-1$
				eglPattern.append(convertType(types.get("m"), 'm'));//$NON-NLS-1$
				if (types.get(".") != null) {
					eglPattern.append(convertType(types.get("."), 's'));//$NON-NLS-1$
					eglPattern.append(convertType(types.get("S"), 'f'));//$NON-NLS-1$
				} else {
					eglPattern.append(convertType(types.get("S"), 's'));//$NON-NLS-1$
				}
			} else {
				eglPattern.append(convertType(types.get("Y"), 'y'));//$NON-NLS-1$
				eglPattern.append(convertType(types.get("M"), 'M'));//$NON-NLS-1$
			}
		}

		return eglPattern.toString();
	}

	public static String convertType(Integer digits, char eglType) {
		StringBuffer retVal = new StringBuffer();
		if (digits != null) {
			for (int i = 0; i < digits.intValue(); i++) {
				retVal.append(eglType);
			}
		}
		return retVal.toString();
	}

	public static String getEGLPrimitiveBase(XSSimpleTypeDefinition simpleType) {
		String type = "string";//$NON-NLS-1$

		switch (simpleType.getBuiltInKind()) {
		case XSConstants.INT_DT:
		case XSConstants.UNSIGNEDSHORT_DT:
			type = "int";//$NON-NLS-1$
			break;

		case XSConstants.INTEGER_DT:
			type = "num(31)";//$NON-NLS-1$

		case XSConstants.DECIMAL_DT:
			type = "decimal(31,4)";//$NON-NLS-1$
			break;

		case XSConstants.BOOLEAN_DT:
			type = "boolean";//$NON-NLS-1$
			break;

		case XSConstants.STRING_DT:
		case XSConstants.QNAME_DT:
		case XSConstants.ANYURI_DT:
		case XSConstants.ANYSIMPLETYPE_DT:
		case XSConstants.GYEARMONTH_DT:
		case XSConstants.GYEAR_DT:
		case XSConstants.GMONTHDAY_DT:
		case XSConstants.GDAY_DT:
		case XSConstants.GMONTH_DT:
		case XSConstants.NORMALIZEDSTRING_DT:
		case XSConstants.TOKEN_DT:
		case XSConstants.LANGUAGE_DT:
		case XSConstants.NAME_DT:
		case XSConstants.NCNAME_DT:
		case XSConstants.ID_DT:
		case XSConstants.NMTOKEN_DT:
		case XSConstants.LIST_DT: // NMTOKENS:
			type = "string";//$NON-NLS-1$
			break;

		case XSConstants.LONG_DT:
		case XSConstants.UNSIGNEDINT_DT:
			type = "bigint";//$NON-NLS-1$
			break;

		case XSConstants.SHORT_DT:
			type = "smallint";//$NON-NLS-1$
			break;

		case XSConstants.DATE_DT:
			type = "date";//$NON-NLS-1$
			break;

		case XSConstants.DOUBLE_DT:
			type = "float";//$NON-NLS-1$
			break;

		case XSConstants.FLOAT_DT:
			type = "smallfloat";//$NON-NLS-1$
			break;

		case XSConstants.BYTE_DT:
		case XSConstants.UNSIGNEDBYTE_DT:
			// case XSConstants.BASE64_DT:
		case XSConstants.HEXBINARY_DT:
			type = "hex(2)";//$NON-NLS-1$
			break;

		case XSConstants.DATETIME_DT:
			type = "timestamp";//$NON-NLS-1$
			break;

		case XSConstants.TIME_DT:
			type = "time";//$NON-NLS-1$
			break;

		case XSConstants.BASE64BINARY_DT:
			type = "blob";//$NON-NLS-1$
			break;

		case XSConstants.DURATION_DT:
			type = "interval";//$NON-NLS-1$
			break;

		case XSConstants.NONPOSITIVEINTEGER_DT:
		case XSConstants.NEGATIVEINTEGER_DT:
		case XSConstants.NONNEGATIVEINTEGER_DT:
		case XSConstants.UNSIGNEDLONG_DT:
		case XSConstants.POSITIVEINTEGER_DT:
			type = "decimal(31)";//$NON-NLS-1$
			break;

		default:
			type = "string";//$NON-NLS-1$
		}
		return type;
	}

	public static int[] getDecimalDefinition(XSSimpleTypeDefinition simpleType) {
		if (simpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_TOTALDIGITS)) {
			int length = 31;
			int decimalDig = 0;
			try {
				length = Integer.parseInt(simpleType.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_TOTALDIGITS));
			} catch (Exception e) {
			}
			if (simpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_FRACTIONDIGITS)) {
				try {
					decimalDig = Integer.parseInt(simpleType.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_FRACTIONDIGITS));
				} catch (Exception e) {
				}
			}
			return new int[] { length, decimalDig };
		} else {
			// handle RDz Web Service decimal definitions
			String minInclusive = (simpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_MININCLUSIVE)) ? simpleType
					.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MININCLUSIVE) : null;
			String maxInclusive = (simpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_MAXINCLUSIVE)) ? simpleType
					.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MAXINCLUSIVE) : null;
			String minExclusive = (simpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_MINEXCLUSIVE)) ? simpleType
					.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MINEXCLUSIVE) : null;
			String maxExclusive = (simpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE)) ? simpleType
					.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE) : null;

			if (minInclusive == null && maxInclusive == null && minExclusive == null && maxExclusive == null) {
				return null;
			}
			int[] decimal = new int[2];
			if (minInclusive != null) {
				getDecimal(minInclusive.trim(), decimal);
			}
			if (maxInclusive != null) {
				getDecimal(maxInclusive.trim(), decimal);
			}
			if (minExclusive != null) {
				getDecimal(minExclusive.trim(), decimal);
			}
			if (maxExclusive != null) {
				getDecimal(maxExclusive.trim(), decimal);
			}
			return decimal;
		}
	}

	private static void getDecimal(String clusive, int[] currentDecimal) {
		int length = 0;
		int decimal = 0;
		if (clusive != null && clusive.length() > 0) {
			try {
				BigDecimal bd = new BigDecimal(clusive);
				length = (bd.abs().toPlainString().length() + (bd.scale() > 0 ? -1 : 0));
				if (bd.scale() > 0) {
					decimal = bd.scale();
				}
			} catch (Exception e) {
				int idx = -1;
				if ((clusive.charAt(0) == '-' || clusive.charAt(0) == '+') && clusive.length() > 1) {
					clusive = clusive.substring(1);
				}
				length = clusive.length();
				idx = clusive.indexOf('.');
				if (idx > -1) {
					decimal = length - (idx + 1);
					length = length - decimal;
				}
			}

			if (length > currentDecimal[0]) {
				currentDecimal[0] = length;
			}
			if (decimal > currentDecimal[1]) {
				currentDecimal[1] = decimal;
			}
		}
	}

	public static XSObject[] findLocalAnonymousElements(XSModel xsModel, String name) {
		ArrayList<XSObject> objects = new ArrayList<XSObject>();

		XSNamedMap map = xsModel.getComponents(XSConstants.TYPE_DEFINITION);
		// complex types
		for (int i = 0; i < map.getLength(); i++) {
			XSTypeDefinition type = (XSTypeDefinition) map.item(i);
			if (type.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE && !type.getNamespace().equals("http://www.w3.org/2001/XMLSchema")//$NON-NLS-1$
					&& !WSDLUtil.isStandardArray(type))
				findLocalAnonymousElements((XSComplexTypeDefinition) type, name, objects);
		}

		map = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
		for (int i = 0; i < map.getLength(); i++) {
			XSObject xsObject = map.item(i);
			if (xsObject instanceof XSElementDeclaration) {
				XSElementDeclaration xsElement = (XSElementDeclaration) xsObject;
				findLocalAnonymousElements(xsElement, name, objects);
			}
		}

		return objects.toArray(new XSObject[objects.size()]);
	}

	public static void findLocalAnonymousElements(XSComplexTypeDefinition type, String name, ArrayList<XSObject> objects) {
		if (type.getParticle() == null) { // no children?
			return;
		}
		XSModelGroup xsModelGroup = (XSModelGroup) type.getParticle().getTerm();
		XSModelGroup extGroup = getExtensionGroup(type);
		if (xsModelGroup != extGroup) {// they are equal if extension doesn't
										// define additional fields
			findLocalAnonymousElements(xsModelGroup, extGroup, name, objects);
		}
	}

	public static void findLocalAnonymousElements(XSModelGroup xsModelGroup, XSModelGroup extGroup, String name, ArrayList<XSObject> objects) {
		XSObjectList xsObjectList = xsModelGroup.getParticles();
		for (int i = 0; i < xsObjectList.getLength(); i++) {
			XSParticle xsParticle = (XSParticle) xsObjectList.item(i);
			if (xsParticle.getTerm() instanceof XSModelGroup) {
				// if this record is an extension of this group, ignore...
				if (xsParticle.getTerm() != extGroup) {
					findLocalAnonymousElements((XSModelGroup) xsParticle.getTerm(), null, name, objects);
				}
				continue;
			}
			if (!(xsParticle.getTerm() instanceof XSElementDeclaration)) {
				// TODO handle this case?
				continue;
			}
			XSElementDeclaration xsElement = (XSElementDeclaration) xsParticle.getTerm();
			findLocalAnonymousElements(xsElement, name, objects);
		}

	}

	public static void findLocalAnonymousElements(XSElementDeclaration element, String name, ArrayList<XSObject> objects) {
		if (element.getTypeDefinition().getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
			XSComplexTypeDefinition type = (XSComplexTypeDefinition) element.getTypeDefinition();
			if (type.getAnonymous() && element.getScope() == XSConstants.SCOPE_LOCAL && name.equals(element.getName()) && !objects.contains(element)) {
				objects.add(element);
			}
			findLocalAnonymousElements(type, name, objects);
		}
	}

	/**
	 * Returns the model group associated with the extension derivation.
	 * 
	 * @param xsComplexType
	 * @return
	 */
	public static XSModelGroup getExtensionGroup(XSComplexTypeDefinition xsComplexType) {
		XSModelGroup group = null;

		XSTypeDefinition derivedType = xsComplexType.getBaseType();
		if (isExtensionType(xsComplexType) && derivedType instanceof XSComplexTypeDefinition) {
			if (xsComplexType.getParticle() != null) {
				group = (XSModelGroup) ((XSComplexTypeDefinition) derivedType).getParticle().getTerm();
			}
		}
		return group;
	}

	public static boolean isExtensionType(XSComplexTypeDefinition xsComplexType) {
		
		XSTypeDefinition derivedType = xsComplexType.getBaseType();
		return (xsComplexType.getDerivationMethod() == XSConstants.DERIVATION_EXTENSION && xsComplexType.derivedFromType(derivedType,
				XSConstants.DERIVATION_EXTENSION));
	}

	public static boolean isSpecialArrayType(XSTypeDefinition type) {
		return (type instanceof XSSimpleTypeDefinition && (((XSSimpleTypeDefinition) type).getBuiltInKind() == XSConstants.BASE64BINARY_DT || ((XSSimpleTypeDefinition) type)
				.getBuiltInKind() == XSConstants.HEXBINARY_DT));
	}
}
