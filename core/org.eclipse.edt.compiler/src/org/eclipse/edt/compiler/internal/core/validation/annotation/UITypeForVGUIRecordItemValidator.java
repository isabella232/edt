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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.UITypeKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class UITypeForVGUIRecordItemValidator extends DefaultFieldContentAnnotationValidationRule {
   
    private static class EGLCaseInsensitiveComparator implements Comparator {

    	/* (non-Javadoc)
    	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    	 */
    	public int compare(Object arg0, Object arg1) {
    		if (arg0 == null) {
    			return -1;
    		}
    		if (arg1 == null) {
    			return 1;
    		}
    				
    		return arg0.toString().compareToIgnoreCase(arg1.toString());
    	}
    }
    
	private static Set propertiesForWhichUITypeValueOfProgramLinkOrUIFormIsNotAllowed = new TreeSet( new EGLCaseInsensitiveComparator() );
	static{
		propertiesForWhichUITypeValueOfProgramLinkOrUIFormIsNotAllowed.addAll( Arrays.asList( new String[] {
			InternUtil.intern(IEGLConstants.PROPERTY_TYPECHKMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDVALUESMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_CURRENCY),
			InternUtil.intern(IEGLConstants.PROPERTY_CURRENCYSYMBOL),
			InternUtil.intern(IEGLConstants.PROPERTY_NUMERICSEPARATOR),
			InternUtil.intern(IEGLConstants.PROPERTY_SIGN),
			InternUtil.intern(IEGLConstants.PROPERTY_INPUTREQUIRED),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORFUNCTION),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORDATATABLE),
			InternUtil.intern(IEGLConstants.PROPERTY_ZEROFORMAT),
			InternUtil.intern(IEGLConstants.PROPERTY_MINIMUMINPUT),
			InternUtil.intern(IEGLConstants.PROPERTY_NEEDSSOSI),
			InternUtil.intern(IEGLConstants.PROPERTY_RUNVALIDATORFROMPROGRAM),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDVALUES),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORFUNCTIONMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORDATATABLEMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_MINIMUMINPUTMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_INPUTREQUIREDMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_TYPECHKMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDVALUESMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_FILLCHARACTER),
			InternUtil.intern(IEGLConstants.PROPERTY_ISBOOLEAN),
			InternUtil.intern(IEGLConstants.PROPERTY_DATEFORMAT),
			InternUtil.intern(IEGLConstants.PROPERTY_TIMEFORMAT),
			InternUtil.intern(IEGLConstants.PROPERTY_UPPERCASE)
		} ) );
	}
	
	private static Set propertiesForWhichUITypeValueOfNoneIsNotAllowed = new TreeSet( new EGLCaseInsensitiveComparator() );
	static{
		propertiesForWhichUITypeValueOfNoneIsNotAllowed.addAll( Arrays.asList( new String[] {
			InternUtil.intern(IEGLConstants.PROPERTY_CURRENCY),
			InternUtil.intern(IEGLConstants.PROPERTY_CURRENCYSYMBOL),
			InternUtil.intern(IEGLConstants.PROPERTY_NUMERICSEPARATOR),
			InternUtil.intern(IEGLConstants.PROPERTY_SIGN),
			InternUtil.intern(IEGLConstants.PROPERTY_ISBOOLEAN),
			InternUtil.intern(IEGLConstants.PROPERTY_ZEROFORMAT),
			InternUtil.intern(IEGLConstants.PROPERTY_DISPLAYNAME),
			InternUtil.intern(IEGLConstants.PROPERTY_HELP),
			InternUtil.intern(IEGLConstants.PROPERTY_NUMELEMENTSITEM),
			InternUtil.intern(IEGLConstants.PROPERTY_SELECTEDINDEXITEM),
			InternUtil.intern(IEGLConstants.PROPERTY_INPUTREQUIRED),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORFUNCTION),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORDATATABLE),
			InternUtil.intern(IEGLConstants.PROPERTY_MINIMUMINPUT),
			InternUtil.intern(IEGLConstants.PROPERTY_NEEDSSOSI),
			InternUtil.intern(IEGLConstants.PROPERTY_RUNVALIDATORFROMPROGRAM),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDVALUES),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORFUNCTIONMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORDATATABLEMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_TYPECHKMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_VALIDVALUESMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_FILLCHARACTER),
			InternUtil.intern(IEGLConstants.PROPERTY_DATEFORMAT),
			InternUtil.intern(IEGLConstants.PROPERTY_TIMEFORMAT),
			InternUtil.intern(IEGLConstants.PROPERTY_UPPERCASE),
			InternUtil.intern(IEGLConstants.PROPERTY_MINIMUMINPUTMSGKEY),
			InternUtil.intern(IEGLConstants.PROPERTY_INPUTREQUIREDMSGKEY)
		} ) );
	}
	
	private static List validPrimitivesForUITypeSubmit = Arrays.asList( new Primitive[] {
		Primitive.CHAR,
		Primitive.MBCHAR,
		Primitive.DBCHAR,
		Primitive.UNICODE
	} );
	
	private static Set uiTypeValuesForWhichProgramLinkDataPropertyIsNotAllowed = new TreeSet();
	static{
		uiTypeValuesForWhichProgramLinkDataPropertyIsNotAllowed.addAll( Arrays.asList( new String[] {
			InternUtil.intern("hidden"),
			InternUtil.intern("input"),
			InternUtil.intern("inputOutput"),
			InternUtil.intern("output"),
			InternUtil.intern("submit"),
			InternUtil.intern("submitBypass"),
			InternUtil.intern("none")
		} ) );
	}

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_UITYPE));
		if(annotationBinding != null) {
			EnumerationDataBinding value = (EnumerationDataBinding) annotationBinding.getValue();
			if(AbstractBinder.enumerationIs(value, new String[] {"egl", "ui", "webTransaction"}, "UITypeKind", "UIFORM") ||
			   AbstractBinder.enumerationIs(value, new String[] {"egl", "ui", "webTransaction"}, "UITypeKind", "PROGRAMLINK")) {
				if(allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_PROGRAMLINKDATA)) == null) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.PROPERTY_REQUIRED_WHEN_UITYPE_IS_PROGRAMLINK_OR_UIFORM,
						new String[] { "@" + IEGLConstants.PROPERTY_PROGRAMLINKDATA } );
				}
				
				for(Iterator iter = propertiesForWhichUITypeValueOfProgramLinkOrUIFormIsNotAllowed.iterator(); iter.hasNext();) {
					String nextPropName = (String) iter.next();
					IAnnotationBinding otherABinding = (IAnnotationBinding) allAnnotations.get(nextPropName);
					if(otherABinding != null) {
						Object otherValue = otherABinding.getValue();
						if(!isUnapplicable(otherValue)) {
							problemRequestor.acceptProblem(
								errorNode,
								IProblemRequestor.PROPERTY_NOT_ALLOWED_WITH_UITYPE_OF,
								new String[] {
									nextPropName,
									toCommaList( new String[] {
										UITypeKind.UIFORM.getCaseSensitiveName(),
										UITypeKind.PROGRAMLINK.getCaseSensitiveName()
									})
								});
						}
					}
				}
			}
			else if(AbstractBinder.enumerationIs(value, new String[] {"egl", "ui", "webTransaction"}, "UITypeKind", "NONE")) {
				for(Iterator iter = propertiesForWhichUITypeValueOfNoneIsNotAllowed.iterator(); iter.hasNext();) {
					String nextPropName = (String) iter.next();
					IAnnotationBinding otherABinding = (IAnnotationBinding) allAnnotations.get(nextPropName);
					if(otherABinding != null) {
						Object otherValue = otherABinding.getValue();
						if(!isUnapplicable(otherValue)) {
							problemRequestor.acceptProblem(
								errorNode,
								IProblemRequestor.PROPERTY_NOT_ALLOWED_WITH_UITYPE_OF,
								new String[] {
									nextPropName,
									toCommaList( new String[] {
										UITypeKind.NONE.getName()
									})
								});
						}
					}
				}
			}
			else if(AbstractBinder.enumerationIs(value, new String[] {"egl", "ui", "webTransaction"}, "UITypeKind", "SUBMIT") ||
					AbstractBinder.enumerationIs(value, new String[] {"egl", "ui", "webTransaction"}, "UITypeKind", "SUBMITBYPASS")) {
				ITypeBinding tBinding = containerBinding.getType();
				if(tBinding != null && IBinding.NOT_FOUND_BINDING != tBinding) {
					boolean typeIsValid = false;
					
					if(ITypeBinding.PRIMITIVE_TYPE_BINDING == tBinding.getKind()) {
						typeIsValid = validPrimitivesForUITypeSubmit.contains(((PrimitiveTypeBinding) tBinding).getPrimitive());
					}
					
					if(!typeIsValid) {
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.UITYPE_OF_REQUIRES_PRIMITIVE_TYPE_OF,
							new String[] {
								AbstractBinder.enumerationIs(value, new String[] {"egl", "ui", "webTransaction"}, "UITypeKind", "SUBMIT") ?
									UITypeKind.SUBMIT.getName() :
									UITypeKind.SUBMITBYPASS.getName(),
								toCommaList((Primitive[]) validPrimitivesForUITypeSubmit.toArray(new Primitive[0]))
							});
					}
				}
			}
			
			if(canonicalContainerName.equals("*") && !AbstractBinder.enumerationIs(value, new String[] {"egl", "ui", "webTransaction"}, "UITypeKind", "NONE")) {
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.FILLER_ITEMS_REQUIRE_UITYPE_NONE);
			}
			
			if(allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_PROGRAMLINKDATA)) != null &&
			   uiTypeValuesForWhichProgramLinkDataPropertyIsNotAllowed.contains(((EnumerationDataBinding) value).getName())) {
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.PROPERTY_NOT_ALLOWED_WITH_UITYPE_OF,
					new String[] {
						"@" + IEGLConstants.PROPERTY_PROGRAMLINKDATA,
						toCommaList((String[]) uiTypeValuesForWhichProgramLinkDataPropertyIsNotAllowed.toArray(new String[0]))
					});
			}
		}
	}
	
	private boolean isUnapplicable(Object value) {
		if(value == Boolean.NO) {
			return true;
		}
		if(value instanceof EnumerationDataBinding) {
			EnumerationDataBinding eDataBinding = (EnumerationDataBinding) value;
			if(InternUtil.intern("NONE") == eDataBinding.getName()) {
				return true;
			}
		}
		return false;
	}

	private static String toCommaList( String[] ary ) {
		StringBuffer sb = new StringBuffer();
		for( int i = 0; i < ary.length; i++ ) {
			sb.append( ary[i] );
			if( i != ary.length - 1 ) {
				sb.append( ", " );
			}
		}
		return sb.toString();
	}
	
	private static String toCommaList( Primitive[] ary ) {
		StringBuffer sb = new StringBuffer();
		for( int i = 0; i < ary.length; i++ ) {
			sb.append( ary[i].getName() );
			if( i != ary.length - 1 ) {
				sb.append( ", " );
			}
		}
		return sb.toString();
	}
	
	private static String toCommaList( IDataBinding[] ary ) {
		StringBuffer sb = new StringBuffer();
		for( int i = 0; i < ary.length; i++ ) {
			sb.append( ary[i].getName() );
			if( i != ary.length - 1 ) {
				sb.append( ", " );
			}
		}
		return sb.toString();
	}
}
