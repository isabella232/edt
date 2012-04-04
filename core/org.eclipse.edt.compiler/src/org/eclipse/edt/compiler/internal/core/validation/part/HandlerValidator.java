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
package org.eclipse.edt.compiler.internal.core.validation.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.SystemFunctionBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.FunctionParameter.AttrType;
import org.eclipse.edt.compiler.core.ast.FunctionParameter.UseType;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemLibrary;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class HandlerValidator extends FunctionContainerValidator {
	
	private static final String FUNCTION_BEFOREREPORTINIT = 	InternUtil.internCaseSensitive("beforeReportInit");  //$NON-NLS-1$
	private static final String FUNCTION_AFTERREPORTINIT = 	InternUtil.internCaseSensitive("afterReportInit");  //$NON-NLS-1$
	private static final String FUNCTION_BEFOREPAGEINIT = 	InternUtil.internCaseSensitive("beforePageInit");  //$NON-NLS-1$
	private static final String FUNCTION_AFTERPAGEINIT = 	InternUtil.internCaseSensitive("afterPageInit");  //$NON-NLS-1$
	private static final String FUNCTION_BEFORECOLUMNINIT = 	InternUtil.internCaseSensitive("beforeColumnInit");  //$NON-NLS-1$
	private static final String FUNCTION_AFTERCOLUMNINIT = 	InternUtil.internCaseSensitive("afterColumnInit");  //$NON-NLS-1$
	private static final String FUNCTION_BEFOREGROUPINIT = 	InternUtil.internCaseSensitive("beforeGroupInit");  //$NON-NLS-1$
	private static final String FUNCTION_AFTERGROUPINIT = 	InternUtil.internCaseSensitive("afterGroupInit");  //$NON-NLS-1$
	private static final String FUNCTION_BEFOREDETAILEVAL = 	InternUtil.internCaseSensitive("beforeDetailEval");  //$NON-NLS-1$
	private static final String FUNCTION_AFTERDETAILEVAL = 	InternUtil.internCaseSensitive("afterDetailEval");  //$NON-NLS-1$

	private static final String STRINGVARIABLE = "stringVariable";  //$NON-NLS-1$

	   private static final SystemFunctionBinding AFTERDETAILEVAL = SystemLibrary.createSystemFunction(
	   		FUNCTION_AFTERDETAILEVAL,
			null,
			new String[0]		,
			new ITypeBinding[0]	,
			new UseType[0]		,
			0
		);
	   
	   private static final SystemFunctionBinding BEFOREDETAILEVAL = SystemLibrary.createSystemFunction(
	   		FUNCTION_BEFOREDETAILEVAL,
			null,
			new String[0]		,
			new ITypeBinding[0]	,
			new UseType[0]		,
			0
		);
	   
	   private static final SystemFunctionBinding AFTERGROUPINIT = SystemLibrary.createSystemFunction(
	   		FUNCTION_AFTERGROUPINIT,
			null,
			new String[]		{STRINGVARIABLE},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN},
			0
		);
	   
	   private static final SystemFunctionBinding BEFOREGROUPINIT = SystemLibrary.createSystemFunction(
	   		FUNCTION_BEFOREGROUPINIT,
			null,
			new String[]		{STRINGVARIABLE},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN},
			0
		);
	   
	   private static final SystemFunctionBinding AFTERCOLUMNINIT = SystemLibrary.createSystemFunction(
	   		FUNCTION_AFTERCOLUMNINIT,
			null,
			new String[0]		,
			new ITypeBinding[0]	,
			new UseType[0]		,
			0
		);
	   
	   private static final SystemFunctionBinding BEFORECOLUMNINIT = SystemLibrary.createSystemFunction(
	   		FUNCTION_BEFORECOLUMNINIT,
			null,
			new String[0]		,
			new ITypeBinding[0]	,
			new UseType[0]		,
			0
		);
	   
	   private static final SystemFunctionBinding AFTERPAGEINIT = SystemLibrary.createSystemFunction(
	   		FUNCTION_AFTERPAGEINIT,
			null,
			new String[0]		,
			new ITypeBinding[0]	,
			new UseType[0]		,
			0
		);
	   
	   private static final SystemFunctionBinding BEFOREPAGEINIT = SystemLibrary.createSystemFunction(
	   		FUNCTION_BEFOREPAGEINIT,
			null,
			new String[0]		,
			new ITypeBinding[0]	,
			new UseType[0]		,
			0
		);
	   
	   private static final SystemFunctionBinding AFTERREPORTINIT = SystemLibrary.createSystemFunction(
	   		FUNCTION_AFTERREPORTINIT,
			null,
			new String[0]		,
			new ITypeBinding[0]	,
			new UseType[0]		,
			0
		);
	
	   private static final SystemFunctionBinding BEFOREREPORTINTI = SystemLibrary.createSystemFunction(
	   		FUNCTION_BEFOREREPORTINIT,
			null,
			new String[0]		,
			new ITypeBinding[0]	,
			new UseType[0]		,
			0
		);
	
	   public static SystemFunctionBinding[] JasperReportCallbackFunctions = new SystemFunctionBinding[] {
				BEFOREREPORTINTI,
				AFTERREPORTINIT,
				BEFOREPAGEINIT,
				AFTERPAGEINIT,
				BEFORECOLUMNINIT,
				AFTERCOLUMNINIT,
				BEFOREGROUPINIT,
				AFTERGROUPINIT,
				BEFOREDETAILEVAL,
				AFTERDETAILEVAL
			};
		
	   private static String[] requireEventType = new String[] { 
		   InternUtil.intern("ElementName"), 
		   InternUtil.intern("RowType"),
		   InternUtil.intern("RowNumber"), 
		   InternUtil.intern("GroupName"), 
		   InternUtil.intern("ColumnNumber") 
	   };
	   
	   private static String[] birtPkg = InternUtil.intern(new String[] { "egl", "report", "birt" });
	   private static String[] jsfPkg = InternUtil.intern(new String[] { "egl", "ui", "jsf" });
	   private static String[] ruiPkg = InternUtil.intern(new String[] { "egl", "rui" });
	   
	   private static String eventTypeString = InternUtil.intern("eventType");
	   
	   private static Object[] birtFunctionValidationInfo = new Object[] {
		   new Object[] {new Integer(2), new Object[] {new String[] {"LabelInstance", "TextInstance", "DynamicTextInstance", "DataInstance", "ImageInstance", "GridInstance", "TableInstance", "ListInstance", "RowInstance", "CellInstance"}, new String[] {"ReportContext"} }},
		   new Object[] {new Integer(2), new Object[] {new String[] {"LabelInstance", "TextInstance", "DynamicTextInstance", "DataInstance", "ImageInstance", "GridInstance", "TableInstance", "ListInstance", "RowInstance", "CellInstance"}, new String[] {"ReportContext"} }},

		   new Object[] {new Integer(2), new Object[] {new String[] {"DataSourceInstance", "DataSetInstance"}, new String[] {"ReportContext"} }},
		   new Object[] {new Integer(2), new Object[] {new String[] {"DataSourceInstance", "DataSetInstance"}, new String[] {"ReportContext"} }},
		   new Object[] {new Integer(2), new Object[] {new String[] {"DataSourceInstance", "DataSetInstance"}, new String[] {"ReportContext"} }},

		   new Object[] {new Integer(1), new Object[]{new String[] {"ReportContext"} }},
		   new Object[] {new Integer(1), new Object[]{new String[] {"ReportContext"} }},

		   new Object[] {new Integer(3), new Object[] {new String[] {"DataSetInstance"}, new String[] {"DataSetRow"}, new String[] {"ReportContext"} }},

		   new Object[] {new Integer(1), new Object[] {new String[] {"DataSourceInstance", "DataSetInstance"} }},
		   new Object[] {new Integer(1), new Object[] {new String[] {"DataSourceInstance", "DataSetInstance"} }},

		   new Object[] {new Integer(2), new Object[] {new String[] {"DataSetInstance"}, new String[] {"UpdatableDataSetRow"}}, "boolean" }
	   };
	
	HandlerBinding handlerBinding = null;
	Handler handler = null;
	
	public HandlerValidator(IProblemRequestor problemRequestor, HandlerBinding partBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, partBinding, compilerOptions);
		handlerBinding = partBinding;
	}
	
	public boolean visit(Handler ahandler) {
		handler = ahandler;
		partNode = ahandler;
		EGLNameValidator.validate(handler.getName(), EGLNameValidator.HANDLER, problemRequestor, compilerOptions);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(ahandler);
		validateHandler();
		
		if (handlerBinding.getAnnotation(new String[] {"egl", "ui", "jasper"}, "JasperReport") != null){
			validateJasperReportHandler();
		}
		else {
			if (handlerBinding.getAnnotation(birtPkg, "BirtHandler") != null){
				validateBirtHandler();
			}
			else {
				if (handlerBinding.getAnnotation(jsfPkg, "JSFHandler") != null){
					validateJSFHandler();
				}
				else {
					if (handlerBinding.getAnnotation(ruiPkg, "RUIHandler") != null){
						validateRUIHandler();
					}
				}
			}
		}
		
		checkInterfaceFunctionsOverriden();
		return true;
	}
	
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
		super.visit(classDataDeclaration);
		return false;
	}
	
	public boolean visit(NestedFunction nestedFunction) {
		super.visit(nestedFunction);
		return false;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		super.visit(settingsBlock);
		return false;
	}
	
	public boolean visit(UseStatement useStatement) {
		super.visit(useStatement);
		return false;
	}
	
	private void validateJSFHandler() {
		IPartBinding part = (IPartBinding) handler.getName().resolveBinding();
		if (part == null) {
			return;
		}
		
		IAnnotationBinding typeAnn = part.getSubTypeAnnotationBinding();
		if (typeAnn == null) {
			return;
		}
		
		IAnnotationBinding onConstAnn = (IAnnotationBinding) typeAnn.findData(IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION);
		if (!Binding.isValidBinding(onConstAnn)) {
			return;
		}
		
		final IFunctionBinding funcBinding = (IFunctionBinding) onConstAnn.getValue();
		
		if (funcBinding == null) {
			return;
		}	
		
		validateNoForward(funcBinding);
		
		
	}
	
	private void validateHandler() {
		DefaultASTVisitor visitor1 =  new DefaultASTVisitor() {
			public boolean visit(NestedFunction nestedFunction) {
				
				final String funcName = nestedFunction.getName().getCanonicalName();
				
				DefaultASTVisitor visitor2 = new DefaultASTVisitor() {
					public boolean visit(NestedFunction nestedFunction) {
						return true;
					}
					public boolean visit(FunctionParameter functionParameter) {				
						if (functionParameter.getAttrType() == AttrType.FIELD){
							problemRequestor.acceptProblem(functionParameter,
									IProblemRequestor.FUNCTION_PARAMETERS_DO_NOT_SUPPORT_NULLABLE_AND_FIELD,
									new String[] {
									functionParameter.getName().getCanonicalName(),
									funcName,
									handler.getName().getCanonicalName(),
									IEGLConstants.KEYWORD_FIELD});
						}
						return false;
					}

				};
				nestedFunction.accept(visitor2);
				return false;
			}
			public boolean visit(Handler handler) {
				return true;
			}
			
								
		};
		
		handler.accept(visitor1);
	}

	
	private void validateRUIHandler() {
		DefaultASTVisitor visitor1 =  new DefaultASTVisitor() {
			public boolean visit(NestedFunction nestedFunction) {
				
				final String funcName = nestedFunction.getName().getCanonicalName();
				
				DefaultASTVisitor visitor2 = new DefaultASTVisitor() {
					public boolean visit(NestedFunction nestedFunction) {
						return true;
					}
					public boolean visit(FunctionParameter functionParameter) {				
						if (functionParameter.getAttrType() == AttrType.SQLNULLABLE){
							problemRequestor.acceptProblem(functionParameter,
									IProblemRequestor.FUNCTION_PARAMETERS_DO_NOT_SUPPORT_NULLABLE_AND_FIELD,
									new String[] {
									functionParameter.getName().getCanonicalName(),
									funcName,
									handler.getName().getCanonicalName(),
									IEGLConstants.KEYWORD_SQLNULLABLE});
						}
						return false;
					}

				};
				nestedFunction.accept(visitor2);
				return false;
			}
			public boolean visit(Handler handler) {
				return true;
			}
			
								
		};
		
		handler.accept(visitor1);
	}
	
	private void validateNoForward(final IFunctionBinding funcBinding) {
		
		DefaultASTVisitor visitor = new DefaultASTVisitor() {
			public boolean visit(Handler handler) {
				return true;
			}
			public boolean visit(NestedFunction nestedFunction) {
				IDataBinding binding = nestedFunction.getName().resolveDataBinding();
				if (Binding.isValidBinding(binding) && binding.getKind() == IDataBinding.NESTED_FUNCTION_BINDING) {
					if (((NestedFunctionBinding)binding).getType() == funcBinding) {
						validateNoForward(nestedFunction);
					}
				}
				return false;
			}
		};
		handler.accept(visitor);		
	}
	
	private void validateNoForward(NestedFunction function) {
		
		AbstractASTVisitor visitor = new AbstractASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.ForwardStatement stmt) {
				//Allow forward to URL
				if (!stmt.isForwardToURL()) {
					problemRequestor.acceptProblem(stmt,
							IProblemRequestor.FORWARD_NOT_ALLOWED,
							new String[] {
								handler.getName().getCanonicalName()
							});
				}
				return false;
			}
		};
		function.accept(visitor);		
	}

	
	private void validateBirtHandler() {

		final Map annotationNodes = new HashMap();

		handler.accept(new AbstractASTVisitor() {

			public boolean visit(NestedFunction nestedFunction) {

				int eventType = getEventType(nestedFunction);
				validateAnnotations(eventType, nestedFunction);

				if (eventType > 0) {
					validateFunctionAttribs(nestedFunction, eventType, getEventTypeString(nestedFunction));
					validateElementName(nestedFunction);
					validateOnCreateOnRender(nestedFunction, eventType);
				}

				return false;
			}
			
			private void validateOnCreateOnRender(NestedFunction nestedFunction, int eventType) {
				
				if (eventType != 1 && eventType != 2) {
					return;
				}
				
				int rowType = getRowType(nestedFunction);
				if (rowType == 4 || rowType == 5) {
					IAnnotationBinding ann = getBirtAnnotationBinding("groupName", nestedFunction);
					if (ann == null) {
						problemRequestor.acceptProblem((Node)annotationNodes.get(InternUtil.intern("rowType")),
								IProblemRequestor.BIRT_FUNCTION_ANN_REQUIRED,
								new String[] {
									"GroupName", "RowType" , getRowTypeString(nestedFunction)
								});
					}
				}


				
				if (nestedFunction.getFunctionParameters().size() < 1) {
					return;
				}
				

																
				ITypeBinding type = ((FunctionParameter)nestedFunction.getFunctionParameters().get(0)).getType().resolveTypeBinding();
				if (!Binding.isValidBinding(type)) {
					return;
				}

				if (type.getName() == InternUtil.intern("CellInstance")) {
					IAnnotationBinding colNum = getBirtAnnotationBinding("columnNumber", nestedFunction);
					if (colNum != null) {
						if (!(colNum.getValue() instanceof Integer) || ((Integer) colNum.getValue()).intValue() < 1) {
							problemRequestor.acceptProblem((Node) annotationNodes.get(InternUtil.intern("columnNumber")),
									IProblemRequestor.BIRT_FUNCTION_VALUE_MUST_BE_GT_0,
									new String[] {"ColumnNumber"});
						}
					}
				}
				
								
				if (type.getName() == InternUtil.intern("RowInstance") || type.getName() == InternUtil.intern("CellInstance")) {
					IAnnotationBinding rowNum = getBirtAnnotationBinding("rowNumber", nestedFunction);
					if (rowNum != null) {
						if (!(rowNum.getValue() instanceof Integer) || ((Integer) rowNum.getValue()).intValue() < 1) {
							problemRequestor.acceptProblem((Node) annotationNodes.get(InternUtil.intern("rowNumber")),
									IProblemRequestor.BIRT_FUNCTION_VALUE_MUST_BE_GT_0, new String[] { "RowNumber" });
						}
					}
				}
			}
			
			private IAnnotationBinding getBirtAnnotationBinding(String name, NestedFunction nestedFunction) {
				IBinding binding = nestedFunction.getName().resolveBinding();
				return binding.getAnnotation(birtPkg, name);
			}
			
			
			private void validateElementName(NestedFunction nestedFunction) {
				IAnnotationBinding ann = getBirtAnnotationBinding("elementName", nestedFunction);
				if (ann == null) {
					problemRequestor.acceptProblem((Node)annotationNodes.get(eventTypeString),
							IProblemRequestor.BIRT_FUNCTION_ANN_REQUIRED,
							new String[] {
								"ElementName", "EventType" , getEventTypeString(nestedFunction)
							});
				}
			}
			
			private void validateFunctionAttribs(NestedFunction nestedFunction, int eventType, String evType) {
				if (eventType > birtFunctionValidationInfo.length) {
					return;
				}
				
				String functionName = nestedFunction.getName().getCanonicalName();
				
				Object[] info = (Object[])birtFunctionValidationInfo[eventType - 1];
				int numParms = ((Integer)info[0]).intValue();
				Object[] parmsInfo = (Object[]) info[1];
				String returnInfo = null;
				if (info.length == 3) {
					returnInfo = (String) info[2];
				}
				
				if (returnInfo != null) {
					boolean error = true;
					if(nestedFunction.hasReturnType()) {
						ITypeBinding type = nestedFunction.getReturnType().resolveTypeBinding();
						if (Binding.isValidBinding(type) && type.getName() == InternUtil.intern(returnInfo)) {
							error = false;
						}						
					}					
					if (error) {
						problemRequestor.acceptProblem(nestedFunction.getName(),
								IProblemRequestor.BIRT_FUNCTION_NEEDS_RETURN,
								new String[] {
									functionName, evType, returnInfo
								});
					}
				}
				else {
					if (nestedFunction.hasReturnType()) {
						problemRequestor.acceptProblem(nestedFunction.getName(),
								IProblemRequestor.BIRT_FUNCTION_HAS_RETURN,
								new String[] {
									functionName, evType
								});
					}
				}
				
				if (nestedFunction.getFunctionParameters().size() != numParms) {
					problemRequestor.acceptProblem(nestedFunction.getName(),
							IProblemRequestor.BIRT_FUNCTION_WRONG_NUMBER_PARMS,
							new String[] {
								functionName, evType, Integer.toString(numParms)
							});
					return;
				}
				
				Iterator i = nestedFunction.getFunctionParameters().iterator();
				int index = 0;
				while (i.hasNext()) {
					FunctionParameter parm = (FunctionParameter) i.next();
					validateParmType(parm, (String[]) parmsInfo[index], evType, functionName);
					index = index + 1;
					
				}
			}
			
			private void validateParmType(FunctionParameter parm, String[] validTypes, String evType, String functionName) {
				boolean error = true;
				ITypeBinding parmType = parm.getType().resolveTypeBinding();
				if (Binding.isValidBinding(parmType)) {

					if (parmType.getKind() == ITypeBinding.EXTERNALTYPE_BINDING) {
						ExternalTypeBinding extType = (ExternalTypeBinding) parmType;
						if (extType.getPackageName() == birtPkg) {
							for (int i = 0; (i < validTypes.length && error); i++) {
								if (extType.getName() == InternUtil.intern(validTypes[i])) {
									error = false;
								}
							}
						}
					}
				}
				else {
					error = false;
				}

				if (error) {
					if (validTypes.length == 1) {
						problemRequestor.acceptProblem(parm.getType(), IProblemRequestor.BIRT_FUNCTION_PARM_MUST_BE, new String[] {
								parm.getName().getCanonicalName(), functionName, evType, validTypes[0] });
					} else {
						StringBuffer buffer = new StringBuffer();
						for (int i = 0; i < validTypes.length; i++) {
							if (i != 0) {
								buffer.append(", ");
							}
							buffer.append(validTypes[i]);
						}

						problemRequestor.acceptProblem(parm.getType(), IProblemRequestor.BIRT_FUNCTION_PARM_MUST_BE_ONE_OF, new String[] {
							parm.getName().getCanonicalName(), functionName, evType, buffer.toString() });
					}
				}
			}
						
			private int getRowType(NestedFunction nestedFunction) {
				IAnnotationBinding ann = getBirtAnnotationBinding("rowType", nestedFunction);
				if (ann == null) {
					return -1;
				}
				EnumerationDataBinding enumBinding = (EnumerationDataBinding) ann.getValue();
				return enumBinding.geConstantValue();
				
			}

			private String getRowTypeString(NestedFunction nestedFunction) {
				IBinding binding = nestedFunction.getName().resolveBinding();
				IAnnotationBinding ann =binding.getAnnotation(birtPkg, "rowType");
				if (ann == null) {
					return "";
				}
				EnumerationDataBinding enumBinding = (EnumerationDataBinding) ann.getValue();
				return enumBinding.getCaseSensitiveName();
				
			}
			
			private int getEventType(NestedFunction nestedFunction) {
				IAnnotationBinding ann = getBirtAnnotationBinding(eventTypeString, nestedFunction);
				if (ann == null) {
					return -1;
				}
				EnumerationDataBinding enumBinding = (EnumerationDataBinding) ann.getValue();
				return enumBinding.geConstantValue();
				
			}

			private String getEventTypeString(NestedFunction nestedFunction) {
				IBinding binding = nestedFunction.getName().resolveBinding();
				IAnnotationBinding ann =binding.getAnnotation(birtPkg, eventTypeString);
				if (ann == null) {
					return "";
				}
				EnumerationDataBinding enumBinding = (EnumerationDataBinding) ann.getValue();
				return enumBinding.getCaseSensitiveName();
				
			}
			
			private void validateAnnotations(final int eventType, final NestedFunction function) {

				function.accept(new AbstractASTVisitor() {
					public boolean visit(SettingsBlock settingsBlock) {
						return true;
					}

					public boolean visit(org.eclipse.edt.compiler.core.ast.Assignment assignment) {
						IAnnotationBinding ann = assignment.resolveBinding();
						if (ann != null) {
							IAnnotationTypeBinding type = ann.getAnnotationType();
							if (type != null && type.getPackageName() == birtPkg) {
								annotationNodes.put(ann.getName(), assignment.getRightHandSide());

								if (eventType == -1) {
									for (int i = 0; i < requireEventType.length; i++) {
										if (ann.getName() == requireEventType[i]) {
											problemRequestor.acceptProblem(assignment.getLeftHandSide(),
													IProblemRequestor.ANNOTATION_REQIRED_WITH_ANNOTATION,
													new String[] {
															"EventType", ann.getCaseSensitiveName()
														});
										}
									}
								}
								
								
							}
						}

						return false;
					}
				});
			}
		});

	}
	
	protected void validateJasperReportHandler() {
		handler.accept(new AbstractASTVisitor() {
			public boolean visit(NestedFunction nestedFunction) {
				SystemFunctionBinding sysBinding = getCallbackFunction(nestedFunction);
				if (sysBinding != null) {

					IBinding binding = ((NestedFunctionBinding) nestedFunction.getName().resolveBinding()).getType();
					if (StatementValidator.isValidBinding(binding) && binding.isFunctionBinding()) {
						FunctionBinding funcBinding = (FunctionBinding) binding;

						if (sysBinding.getParameters().size() != funcBinding.getParameters().size()) {
							problemRequestor
									.acceptProblem(
											nestedFunction.getName(),
											IProblemRequestor.INVALID_NUMBER_OF_PARAMETERS,
											new String[] {
													nestedFunction.getName()
															.getCanonicalName(),
													new Integer(sysBinding
															.getParameters()
															.size()).toString() });
						} else {
							for (int i = 0; i < sysBinding.getParameters()
									.size(); i++) {
								FunctionParameterBinding sysParam = getFunctionParameterBinding((IBinding) sysBinding
										.getParameters().get(i));
								FunctionParameterBinding funcParam = getFunctionParameterBinding((IBinding) funcBinding
										.getParameters().get(i));
								boolean error = funcParam == null;
								if (!error) {
									error = getNonNullable(sysParam.getType()) != getNonNullable(funcParam
											.getType());
								}

								if (error) {
									FunctionParameter p = (FunctionParameter) nestedFunction
											.getFunctionParameters().get(i);
									problemRequestor
											.acceptProblem(
													p.getName(),
													IProblemRequestor.PARAMETER_HAS_WRONG_TYPE,
													new String[] {
															p != null ? p
																	.getName()
																	.getCanonicalName()
																	: "",
															funcBinding
																	.getName(),
															StatementValidator
																	.getTypeString(sysParam
																			.getType()) });
								}
							}

						}
					}
				}
				return false;
			}

			private ITypeBinding getNonNullable(ITypeBinding type) {
				return type.isNullable() ? type.getNonNullableInstance() : type;
			}
		});
	}

	protected FunctionParameterBinding getFunctionParameterBinding (IBinding binding){
		if (StatementValidator.isValidBinding(binding) && binding.isDataBinding()){
			if (((IDataBinding)binding).getKind() == IDataBinding.FUNCTION_PARAMETER_BINDING){
				return (FunctionParameterBinding)binding;
			}
		}
		return null;
	}
	
	protected SystemFunctionBinding getCallbackFunction(NestedFunction nestedFunction) {
		String fName = InternUtil.intern(nestedFunction.getName().getCanonicalName());
		for (int i = 0; i < JasperReportCallbackFunctions.length; i++){
			if (fName == JasperReportCallbackFunctions[i].getName()){
				return JasperReportCallbackFunctions[i];
			}
			
		}
		return null;
	}
	
	
	private void checkInterfaceFunctionsOverriden() {
		for(Iterator iter = getInterfaceFunctionList().iterator(); iter.hasNext();) {
			IFunctionBinding interfaceFunc = (IFunctionBinding) ((NestedFunctionBinding) iter.next()).getType();
			boolean foundMatchingHandlerFunc = false;
			for(Iterator iter2 = handlerBinding.getDeclaredFunctions().iterator(); !foundMatchingHandlerFunc && iter2.hasNext();) {
				IFunctionBinding handlerFunc = (IFunctionBinding) ((NestedFunctionBinding) iter2.next()).getType();
				if(TypeCompatibilityUtil.functionSignituresAreIdentical(handlerFunc, interfaceFunc, compilerOptions)) {
					foundMatchingHandlerFunc = true;
				}				   
			}
			if(!foundMatchingHandlerFunc) {
				problemRequestor.acceptProblem(
					handler.getName(),
					IProblemRequestor.INTERFACE_FUNCTION_MISSING,
					new String[] {
						handler.getName().getCanonicalName(),
						interfaceFunc.getCaseSensitiveName() + "(" + getTypeNamesList(interfaceFunc.getParameters()) + ")",
						interfaceFunc.getDeclarer().getCaseSensitiveName()
					});
			}
		}
	}
	
	private List getInterfaceFunctionList() {
		List retVal = new ArrayList();
		List interfaceList = handlerBinding.getImplementedInterfaces();
		for (int i = 0; i < interfaceList.size(); i++){
			InterfaceBinding interfaceBinding = (InterfaceBinding)interfaceList.get(i);
			for(Iterator iter = interfaceBinding.getDeclaredAndInheritedFunctions().iterator(); iter.hasNext();) {
				NestedFunctionBinding fBinding = (NestedFunctionBinding) iter.next();
				if(!fBinding.isPrivate()) {
					retVal.add(fBinding);
				}
			}
		}
		return retVal;
	}

	private static String getTypeNamesList( List types ) {
		StringBuffer sb = new StringBuffer();
		if( !types.isEmpty() ) {
			sb.append( " " );
		}
		for( Iterator iter = types.iterator(); iter.hasNext(); ) {
			FunctionParameterBinding nextParm = (FunctionParameterBinding) iter.next();
			ITypeBinding nextType = nextParm.getType();
			if (StatementValidator.isValidBinding(nextType)){
				sb.append( nextType.getCaseSensitiveName() );
				if( nextParm.isInput() ) {
					sb.append( " " + IEGLConstants.KEYWORD_IN );
				}
				else if( nextParm.isOutput() ) {
					sb.append( " " + IEGLConstants.KEYWORD_OUT );
				}
				else {
					sb.append( " " + IEGLConstants.KEYWORD_INOUT );
				}
				if( iter.hasNext() ) {
					sb.append( ", " );
				}
				else {
					sb.append( " " );
				}
			}
		}
		return sb.toString();
	}

}
