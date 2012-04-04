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
package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
public class FieldLenUtility {
    IDataBinding dataBinding;
    private ICompilerOptions compilerOptions;
	private Scope currentScope;

    /**
     *  
     */
    public FieldLenUtility(IDataBinding dataBinding, ICompilerOptions compilerOptions, Scope currentScope) {
        super();
        this.dataBinding = dataBinding;
        this.compilerOptions = compilerOptions;
        this.currentScope = currentScope;
    }

    /**
     * Checks to see if the fieldLen property is set. If not, it adds the
     * annotation with the appropriate value to the dataBinding
     */
    public void checkFieldLen() {
        IAnnotationBinding annotation = dataBinding.getAnnotation(new String[]{"egl", "ui"}, "FieldLen");
        if (annotation != null && annotation.getValue() != null) {
            return;
        }

        ITypeBinding type = dataBinding.getType().getBaseType();
        if (type.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING) {
            return;
        }

        ITypeBinding fieldLenType = currentScope.findPackage(InternUtil.intern("eglx")).resolvePackage(InternUtil.intern("ui")).resolveType(InternUtil.intern("FieldLen"));
        if(IBinding.NOT_FOUND_BINDING != fieldLenType) {        
			AnnotationBinding fieldLen = new AnnotationBinding(InternUtil.internCaseSensitive("FieldLen"), dataBinding.getDeclaringPart(), new AnnotationTypeBindingImpl((FlexibleRecordBinding) fieldLenType, dataBinding.getDeclaringPart()));
	        
	        fieldLen.setValue(new Integer(getDefaultFieldLen(type)), null, null, compilerOptions, false);
	        fieldLen.setCalculated(true);
	        
	        dataBinding.addAnnotation(fieldLen);
        }
    }
    
    public int getDefaultFieldLen() {
    	ITypeBinding type = dataBinding.getType().getBaseType();
        if (type.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING) {
        	return -1;        
        }
        return getDefaultFieldLen(type);
    }

    private int getDefaultFieldLen(ITypeBinding type) {
        PrimitiveTypeBinding primType = (PrimitiveTypeBinding) type;
        Primitive prim = primType.getPrimitive();
        if (prim == Primitive.DATE) {
            return getDefaultDateFieldLen();
        }
        if (prim == Primitive.TIME) {
            return getDefaultTimeFieldLen();
        }
        if (prim == Primitive.TIMESTAMP) {
            return getDefaultTimeStampFieldLen();
        }
        return getDefaultFieldLen(primType);
    }
    
    private int getDefaultFieldLen(PrimitiveTypeBinding primType) {
		int length = primType.getLength();
		int scale = primType.getDecimals();

		switch (primType.getPrimitive().getType()) {
			
			case Primitive.MONEY_PRIMITIVE :
				length = length + getNumericItemFormattingProperties(length, scale );
				if (scale > 0)
					length = length + 1;
				
				if (dataBinding.getAnnotation(new String[] {"egl", "ui"}, "Currency") == null) {
					length = length + 1;
				}

				break;
			
			case Primitive.SMALLFLOAT_PRIMITIVE :
				length = 10 + getNumericItemFormattingProperties(length, scale);
				break;
				
			case Primitive.FLOAT_PRIMITIVE :
				length = 20 + getNumericItemFormattingProperties(length, scale);
				break;

			case Primitive.BIGINT_PRIMITIVE :
			case Primitive.INT_PRIMITIVE :
			case Primitive.SMALLINT_PRIMITIVE :
				length = length + getNumericItemFormattingProperties(length, scale );
				break;
				
			case Primitive.BIN_PRIMITIVE :
			case Primitive.DECIMAL_PRIMITIVE :
			case Primitive.PACF_PRIMITIVE :
			case Primitive.NUM_PRIMITIVE :
			case Primitive.NUMC_PRIMITIVE :
				length = length + getNumericItemFormattingProperties(length, scale );
				if (scale > 0)
					length = length + 1;
				break;
				
			case Primitive.DBCHAR_PRIMITIVE :
				length = length * 2;
				break;
				
			case Primitive.CHAR_PRIMITIVE :
			case Primitive.HEX_PRIMITIVE :
			case Primitive.MBCHAR_PRIMITIVE :
			case Primitive.UNICODE_PRIMITIVE :
				break;
		}
		return length;
        
    }
    
    private int getNumericItemFormattingProperties(int precision, int scale) {
		int length = 0;
		
		if (dataBinding.getAnnotation(new String[] {"egl", "ui"}, "Currency") != null) {
			String currencySymbol = null;
			IAnnotationBinding csBinding = dataBinding.getAnnotation(new String[] {"egl", "ui"}, "CurrencySymbol");
			if (csBinding == null) {
			    length = length + 1;
			}
			else {
			    currencySymbol = (String)csBinding.getValue();
			    if (currencySymbol != null) {
			        length = length + currencySymbol.length();
			    }
			}
		}
				
		if(dataBinding.getAnnotation(new String[] {"egl", "ui"}, "NumericSeparator") != null) {
			length = length + (precision - scale - 1) / 3;
		}

		IAnnotationBinding signBinding = dataBinding.getAnnotation(new String[] {"egl", "ui"}, "Sign");
		if (signBinding != null) {
		    EnumerationDataBinding sign = (EnumerationDataBinding) signBinding.getValue();
		    if (sign != null) {
				if (sign.getName() == InternUtil.intern("parens")) {
					length = length + 2;
				}
				else if (sign.getName() == InternUtil.intern("leading") ||
						 sign.getName() == InternUtil.intern("trailing")) {
					length = length + 1;
				}
		    }
		    
		}
		
		return length;
	}

    private int getDefaultDateFieldLen() {

        IAnnotationBinding binding = dataBinding.getAnnotation(new String[] {"egl", "ui"}, "DateFormat");
        if (binding != null) {
            Object format = binding.getValue();
            if (format != null) {
            	if (format instanceof IDataBinding) {
            		IDataBinding dBinding = (IDataBinding) format;
	                if (AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "vg"}, "VGVar", "SYSTEMGREGORIANDATEFORMAT") ||
	                	AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "core"}, "StrLib", "DEFAULTDATEFORMAT")) {
	                    return 10;
	                }
	                if (AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "vg"}, "VGVar", "SYSTEMJULIANDATEFORMAT")) {
	                    return 8;
	                }
            	}
                if (format instanceof ClassConstantBinding) {
                    return ((PrimitiveTypeBinding)((ClassConstantBinding) format).getType()).getLength();
                }
                else {
                    //must be String
                    return ((String) format).length();
                }
            }
        }
        return 10;
    }

    private int getDefaultTimeFieldLen() {
        IAnnotationBinding binding = dataBinding.getAnnotation(new String[] {"egl", "ui"}, "TimeFormat");
        if (binding != null) {
            Object format = binding.getValue();
            if (format != null) {
            	if (format instanceof IDataBinding) {
            		IDataBinding dBinding = (IDataBinding) format;
	                if (AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "core"}, "StrLib", "DEFAULTTIMEFORMAT")) {
	                    return 8;
	                }
            	}
                if (format instanceof ClassConstantBinding) {
                    return ((PrimitiveTypeBinding)((ClassConstantBinding) format).getType()).getLength();
                }
                else {
                    //must be String
                    return ((String) format).length();
                }
            }
        }
        return 8;
    }

    private int getDefaultTimeStampFieldLen() {

        IAnnotationBinding binding = dataBinding.getAnnotation(new String[] {"egl", "ui"}, "TimestampFormat");
        if (binding != null) {
            Object format = binding.getValue();
            if (format != null) {
            	if (format instanceof IDataBinding) {
            		IDataBinding dBinding = (IDataBinding) format;
            		if (AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "core"}, "StrLib", "DEFAULTTIMESTAMPFORMAT")) {
	                    return 26;
	                }
            	}
                if (format instanceof ClassConstantBinding) {
                    return ((PrimitiveTypeBinding)((ClassConstantBinding) format).getType()).getLength();
                }
                else {
                    //must be String
                    return ((String) format).length();
                }
            }
        }
        return 26;
    }
        
}
