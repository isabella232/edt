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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class BaseAnnotationValidator implements IAnnotationValidationRule {
	
	private static Map xsdBaseValuesToValidEGLPrimitives = new TreeMap();
	static {
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("string"), new org.eclipse.edt.compiler.core.ast.Primitive[] {
			Primitive.STRING,
			Primitive.CHAR,
			Primitive.DBCHAR,
			Primitive.MBCHAR,
			Primitive.UNICODE } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("integer"), new Primitive[] {Primitive.NUM } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("int"), new Primitive[] {Primitive.INT } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("long"), new Primitive[] {Primitive.BIGINT } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("short"), new Primitive[] {Primitive.SMALLINT } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("decimal"), new Primitive[] {
			Primitive.DECIMAL,
			Primitive.MONEY,
			Primitive.NUM,
			Primitive.NUMC,
			Primitive.PACF} );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("float"), new Primitive[] {Primitive.SMALLFLOAT } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("double"), new Primitive[] {Primitive.FLOAT } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("boolean"), new Primitive[] {Primitive.SMALLINT } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("byte"), new Primitive[] {Primitive.HEX } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("unsignedInt"), new Primitive[] {Primitive.BIGINT } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("unsignedShort"), new Primitive[] {Primitive.INT } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("unsignedByte"), new Primitive[] {Primitive.HEX } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("QName"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("dateTime"), new Primitive[] {Primitive.TIMESTAMP } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("date"), new Primitive[] {Primitive.DATE } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("time"), new Primitive[] {Primitive.TIME } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("anyURI"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("base64Binary"), new Primitive[] {Primitive.HEX } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("hexBinary"), new Primitive[] {Primitive.HEX } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("anySimpleType"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("duration"), new Primitive[] {Primitive.MONTHSPAN_INTERVAL, Primitive.SECONDSPAN_INTERVAL } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("gYearMonth"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("gYear"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("gMonthDay"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("gDay"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("gMonth"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("normalizedString"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("token"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("language"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("Name"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("NCName"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("ID"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("NMTOKEN"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("NMTOKENS"), new Primitive[] {Primitive.STRING } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("nonPositiveInteger"), new Primitive[] {Primitive.DECIMAL } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("negativeInteger"), new Primitive[] {Primitive.DECIMAL } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("nonNegativeInteger"), new Primitive[] {Primitive.DECIMAL } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("unsignedLong"), new Primitive[] {Primitive.DECIMAL } );
		xsdBaseValuesToValidEGLPrimitives.put( InternUtil.intern("positiveInteger"), new Primitive[] {Primitive.DECIMAL } );
	}
	
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(targetTypeBinding != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == targetTypeBinding.getBaseType().getKind()) {
			PrimitiveTypeBinding myPrimitiveType = (PrimitiveTypeBinding) targetTypeBinding.getBaseType();
			Primitive myPrimitive = myPrimitiveType.getPrimitive();
			IAnnotationBinding aBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_DERIVATIONBASE));
			if(aBinding != null) {
				String val = InternUtil.intern(aBinding.getValue().toString());
				
				Primitive[] validPrimitives = (Primitive[]) xsdBaseValuesToValidEGLPrimitives.get( val );
				if( validPrimitives != null && !containsType( validPrimitives, equivalentPrimitive(myPrimitiveType) ) ) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.PROPERTY_XSD_BASE_NOT_COMPATIBLE_WITH_PRIMITIVE,
						new String[] {
							val,
							myPrimitive == Primitive.BIN ?
								myPrimitiveType.getCaseSensitiveName() :
								myPrimitive.getName() } );								
				}
			}
		}
	}
	
	private static boolean containsType(Primitive[] primAry, Primitive prim) {
		for( int i = 0; i < primAry.length; i++ ) {
			if( primAry[i] == prim ) {
				return true;
			}
		}
		return false;
	}
	
	private static Primitive equivalentPrimitive( PrimitiveTypeBinding primType ) {
		Primitive prim = primType.getPrimitive();
		if( prim == Primitive.BIN) {
			if( primType.getDecimals() == 0 ) {
				if( primType.getLength() == 4 ) {
					return Primitive.SMALLINT;
				}
				if( primType.getLength() == 9 ) {
					return Primitive.INT;
				}
				if( primType.getLength() == 18 ) {
					return Primitive.BIGINT;
				}
			}
			else {
				return Primitive.DECIMAL;
			}
		}
		return prim;
	}
}
