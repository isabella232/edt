/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.egl2mof;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.edt.compiler.Context;
import org.eclipse.edt.compiler.binding.AnnotationBinding;
import org.eclipse.edt.compiler.binding.AnnotationBindingForElement;
import org.eclipse.edt.compiler.binding.AnnotationFieldBinding;
import org.eclipse.edt.compiler.binding.ArrayDictionaryBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ClassConstantBinding;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.ConstructorBinding;
import org.eclipse.edt.compiler.binding.DataBinding;
import org.eclipse.edt.compiler.binding.DictionaryBinding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.FormGroupBinding;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IPartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.MultiplyOccuringItemTypeBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationDataBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.internal.core.builder.CircularBuildRequestException;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.ExpressionParser;
import org.eclipse.edt.compiler.internal.sdk.compile.ASTManager;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathInfo;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.EMetadataType;
import org.eclipse.edt.mof.EModelElement;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.Form;
import org.eclipse.edt.mof.egl.FormField;
import org.eclipse.edt.mof.egl.FormGroup;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.InvalidName;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeName;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.lookup.ProxyElement;
import org.eclipse.edt.mof.egl.lookup.ProxyPart;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.egl.utils.TimeStampAndIntervalPatternFixer;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.eclipse.edt.mof.serialization.ProxyEClass;
import org.eclipse.edt.mof.serialization.ProxyEObject;
import org.eclipse.edt.mof.utils.EList;


@SuppressWarnings("unchecked")
abstract class Egl2MofBase extends AbstractASTVisitor implements MofConversion {
	
	protected static Map<String, MofSerializable> processed = new HashMap<String, MofSerializable>();
	protected static Map<String, ProxyPart> proxyParts = new HashMap<String, ProxyPart>();
	protected Stack<MofSerializable> partProcessingStack = new Stack<MofSerializable>();	
	protected static String TempEClassMarker = "eze_temp";
	protected static String[] reflectPackage = {"egl", "lang", "reflect"};

	MofFactory mof;
	IrFactory factory;
	IEnvironment env;
	Context context;
	
	// Is the type being created in the Mof meta model hierarchy
	boolean inMofContext = false;
	
	// Is the type being created a proxy type used to reference
	// types from EGL that are in the Mof type heirarchy
	boolean inMofProxyContext = false;
	
	// Is the type being created meant to be an instance of 
	// org.eclipse.edt.mof.EMetadataType
	boolean inEMetadataTypeContext = false;
	
	// Is the type being created an EGL AnnotationType
	// note that these types are also inMofContext
	boolean inAnnotationTypeContext = false;
	
	boolean visitFunctionBody = false;
	
	public Stack<Object> stack;
	public Map<IBinding, EObject> eObjects;
	public Map<IBinding, ProxyEObject> proxies;
	
	
	Egl2MofBase(IEnvironment env) {
		this.env = env;
		this.mof = MofFactory.INSTANCE;
		this.factory = IrFactory.INSTANCE;
		stack = new Stack<Object>();
		eObjects = new HashMap<IBinding, EObject>();
		proxies = new HashMap<IBinding, ProxyEObject>();
	}
	
	public void setEnvironment(IEnvironment env) {
		this.env = env;
	}

	//************************************************************************************
	// Helper Routines
	//************************************************************************************
	protected void eStackPush(EObject obj, IBinding element) {
		stack.push(obj);
		eObjects.put(element, obj);
	}
	
	protected EObject eStackPop() {
		return (EObject)stack.pop();
	}
	
	protected MofSerializable getPartBeingProcessed() {
		return partProcessingStack.isEmpty()
			? null
			: partProcessingStack.peek();
	}
	
	protected EObject getEObjectFor(IBinding element) {
		if (element == null) return null;
		IBinding key = element instanceof NestedFunctionBinding ? ((NestedFunctionBinding)element).getType() : element;
		EObject result = eObjects.get(key);
		//if we cant find a real object, see if we have a proxy already
		if (result == null) {
			result = proxies.get(key);
		}
		//If no proxy is found, we need to make one instead
		if (result == null) {
			if (inMofContext) {
				result = new ProxyEObject();
			}
			else {
				result = new ProxyElement();
			}
			proxies.put(key, (ProxyEObject)result);
		}
		return result;
	}
	
	protected IAnnotationBinding getAnnotation(IBinding binding, String typeName) {
		if (binding != null) {
			for (IAnnotationBinding ann : (List<IAnnotationBinding>)binding.getAnnotations()) {
				if (ann.getCaseSensitiveName().equalsIgnoreCase(typeName))
					return ann;
				else
					continue;
			}
			if (binding instanceof AnnotationTypeBindingImpl) {
				ITypeBinding record = ((AnnotationTypeBindingImpl)binding).getAnnotationRecord();
				if (record != null) {
					for (IAnnotationBinding ann : (List<IAnnotationBinding>)record.getAnnotations()) {
						if (ann.getCaseSensitiveName().equalsIgnoreCase(typeName))
							return ann;
						else
							continue;
					}
				}
			}
		}
		return null;
	}
	
	protected void createAnnotations(IBinding binding, EModelElement element) {
		if (!Binding.isValidBinding(binding)) {
			return;
		}
		for (AnnotationBinding annotation : (List<AnnotationBinding>)binding.getAnnotations()) {
			if (!(annotation instanceof AnnotationBindingForElement)) {
				element.getMetadataList().add((EMetadataObject)mofValueFrom(annotation));
			}
		}
	}
	
	protected void createAnnotations(IPartBinding binding, EClass eClass) {
		IAnnotationBinding subtype = binding.getSubTypeAnnotationBinding();
		for (AnnotationBinding annotation : (List<AnnotationBinding>)binding.getAnnotations()) {
			if (subtype == annotation) continue;
			if (!(annotation instanceof AnnotationBindingForElement)) {
				eClass.getMetadataList().add((EMetadataObject)mofValueFrom(annotation));
			}
		}
	}
	
	protected void createAnnotations(IPartBinding binding, AnnotationType eClass) {
		IAnnotationBinding subtype = binding.getSubTypeAnnotationBinding();
		for (AnnotationBinding annotation : (List<AnnotationBinding>)binding.getAnnotations()) {
			if (subtype == annotation) continue;
			if (!(annotation instanceof AnnotationBindingForElement)) {
				eClass.getAnnotations().add((Annotation)mofValueFrom(annotation));
			}
		}
	}
	protected void createAnnotations(IBinding binding, Element element) {
		if (!Binding.isValidBinding(binding)) {
			return;
		}
		for (AnnotationBinding annotation : (List<AnnotationBinding>)binding.getAnnotations()) {
			if (!(annotation instanceof AnnotationBindingForElement)) {
				element.getAnnotations().add(createAnnotation(binding, annotation));
			}
		}
	}
	
	protected void createElementAnnotations(StructureItemBinding binding, StructuredField field) {
		if (field.getOccurs() > 1) {
			for (IAnnotationBinding annBinding : (List<IAnnotationBinding>)binding.getAnnotations()) {
				if (annBinding instanceof AnnotationBindingForElement) {
					int index = ((AnnotationBindingForElement)annBinding).getIndex();
					Annotation eAnn = createAnnotation(binding, annBinding);
					field.getElementAnnotations(index).add(eAnn);
				}
			}
		}
	}
			
	protected void createElementAnnotations(FormFieldBinding binding, FormField field) {
		if (field.getOccurs() > 1) {
			for (IAnnotationBinding annBinding : (List<IAnnotationBinding>)binding.getAnnotations()) {
				if (annBinding instanceof AnnotationBindingForElement) {
					int index = ((AnnotationBindingForElement)annBinding).getIndex();
					Annotation eAnn = createAnnotation(binding, annBinding);
					field.getElementAnnotations(index).add(eAnn);
				}
			}
		}
	}
			
	
	protected void createAnnotations(IPartBinding binding, Part part) {
		IAnnotationBinding subtype = binding.getSubTypeAnnotationBinding();
		boolean bypass = subtype != null && isEGLReflectType(binding);
		for (AnnotationBinding annotation : (List<AnnotationBinding>)binding.getAnnotations()) {
			if (bypass && subtype == annotation) continue;
			if (!(annotation instanceof AnnotationBindingForElement)) {
				if (inMofContext) {
					((EClass)part).getMetadataList().add((EMetadataObject)mofValueFrom(annotation));
				}
				else {
					part.getAnnotations().add((Annotation)mofValueFrom(annotation));
				}
			}
		}

	}
	private Annotation createAnnotation(IBinding binding, IAnnotationBinding annBinding) {
		EObject ann = mofValueFrom(annBinding);
		if (ann instanceof Annotation) return (Annotation)ann;
		else {
			throw new IllegalArgumentException("Type " + ann.getEClass().getETypeSignature() + " is not an AnnotationType");
		}
	}
	
//	private EMetadataObject createEMetadataObject(IBinding binding, IAnnotationBinding annBinding) {
//
//		EMetadataObject annotation;
//		
//		EClass annType;
//		if (annBinding.getType() instanceof AnnotationTypeBindingImpl) {
//			annType = (EClass)mofTypeFor(annBinding.getType());
//			annotation = (EMetadataObject)annType.newInstance();
//		}
//		else {
//			if (inMofContext) {
//				annotation = MofFactory.INSTANCE.createEMetadataObject(true);
//			}
//			else {
//				annotation = IrFactory.INSTANCE.createAnnotation(annBinding.getCaseSensitiveName());
//			}
//		}
//		Iterator i = annBinding.getAnnotations().iterator();
//
//		while (i.hasNext()) {
//			AnnotationBinding subAnnotation = (AnnotationBinding) i.next();
//			// For some reason, the fields sometime get added to the
//			// annotataionBinding as annotations
//			if ((subAnnotation instanceof AnnotationBinding)) {
//				if (inMofContext) {
//					annotation.getMetadataList().add(createEMetadataObject(annBinding, subAnnotation));
//				}
//				else {
//					((Annotation)annotation).getAnnotations().add(createAnnotation(annBinding, subAnnotation));
//				}
//			}
//		}
//
//		i = annBinding.getAnnotationFields().iterator();
//		while (i.hasNext()) {
//			AnnotationFieldBinding field = (AnnotationFieldBinding) i.next();
//			Object value = mofValueFrom(getValue(binding, field.getValue()));
//			annotation.eSet(field.getCaseSensitiveName(), value);
//		}
//		return annotation;
//	}
	
	protected MemberName createMemberName(IBinding binding) {
		MemberName name = factory.createMemberName();
		name.setId(binding.getCaseSensitiveName());
		name.setMember((Member)getEObjectFor(binding));
		return name;
	}
	
	protected Object getFieldValue(IAnnotationBinding annotationBinding, String fieldName) {
		Object result = null;
		for(AnnotationFieldBinding field : (List<AnnotationFieldBinding>)annotationBinding.getAnnotationFields()) {
			if (field.getCaseSensitiveName().equalsIgnoreCase(fieldName)) {
				result = getValue(null, field.getValue(), false);
				if (result != null) break;
			}
		}
		return result;
	}
	
	protected Object getValue(IBinding binding, Object annotationValue, boolean isMetaDataFieldValue) {
		if (annotationValue == IBinding.NOT_FOUND_BINDING) {
			InvalidName name = factory.createInvalidName();
			name.setId("EZENOTFOUND");
			return name;
		}

		// Some well defined array types:
		if (annotationValue instanceof Integer[] || annotationValue instanceof Integer[][] || annotationValue instanceof int[]
				|| annotationValue instanceof int[][] || annotationValue instanceof String[] || annotationValue instanceof String[][]) {
			return annotationValue;
		}

		if (annotationValue == Boolean.NO) {
			return java.lang.Boolean.FALSE;
		}

		if (annotationValue == Boolean.YES) {
			return java.lang.Boolean.TRUE;
		}

		if (annotationValue instanceof SystemEnumerationDataBinding || annotationValue instanceof SystemEnumerationDataBinding[]) {
			return annotationValue;
		}

		if (annotationValue instanceof EnumerationDataBinding) {
			return annotationValue;
		}

		if (annotationValue instanceof PartBinding) {
			EObject value = mofTypeFor((PartBinding)annotationValue);
			
			if (value instanceof Part && !isMetaDataFieldValue) {
				value = createTypeName((Part)value);
			}
			return value;
		}
		if (annotationValue instanceof IFunctionBinding) {
			IFunctionBinding functionBinding = (IFunctionBinding) annotationValue;
			IPartBinding declarer = functionBinding.getDeclarer();

			// for annotations that resolve to a library function, create a
			// FieldAccess with
			// the PartName for the library as the qualifier
			if (declarer != null && declarer.getKind() == ITypeBinding.LIBRARY_BINDING) {
				PartName nameType = factory.createPartName();
				nameType.setId(declarer.getCaseSensitiveName());
				nameType.setPackageName(asString(declarer.getPackageName()));
				
				MemberAccess access = factory.createMemberAccess();
				access.setId(functionBinding.getCaseSensitiveName());
				access.setQualifier(nameType);
				return access;
			}
			return createMemberName(functionBinding);
		}

		if (annotationValue instanceof IAnnotationBinding) {
			return mofValueFrom((IAnnotationBinding) annotationValue);
		}

		if (annotationValue instanceof Expression) {
			((Expression) annotationValue).accept(this);
			return (org.eclipse.edt.mof.egl.Expression)stack.pop();
		}

		if (annotationValue instanceof IDataBinding) {

			IDataBinding valueBinding = (IDataBinding) annotationValue;
			IPartBinding declarer = valueBinding.getDeclaringPart();
			// for annotations that resolve to a library function, create a
			// FieldAccess with
			// the NameType for the library as the qualifier
			if (declarer != null && declarer.getKind() == ITypeBinding.LIBRARY_BINDING && declarer != getLibraryContainer(binding)) {
				PartName nameType = factory.createPartName();
				nameType.setId(declarer.getCaseSensitiveName());
				nameType.setPackageName(asString(declarer.getPackageName()));
				MemberAccess access = factory.createMemberAccess();
				access.setId(binding.getCaseSensitiveName());
				access.setQualifier(nameType);
				return access;
			}
			
			//If the value is a formDataBinding, create a simple expression and route through the expression generator
			if (Binding.isValidBinding(valueBinding) && valueBinding.getKind() == IDataBinding.FORM_BINDING) {
				Expression expr = createExpression(binding, (IDataBinding) annotationValue, false);
				expr.accept(this);
				return (Expression)stack.pop();

			}

			// if annotation is on a data binding, must create a simpleName
			// expression here and set the binding...then route through
			// expressionGenerator
			if (binding instanceof DataBinding) {
				ITypeBinding container = getContainer(valueBinding);
				if (container != null && container == ((DataBinding) valueBinding).getType()) {
					Expression expr = createExpression(binding, (IDataBinding) annotationValue, true);
					expr.accept(this);
					return (Expression)stack.pop();
				}
			}
			return createMemberName((IDataBinding) annotationValue);
		}

		if (annotationValue instanceof ITypeBinding) {
			EObject value = mofTypeFor((ITypeBinding) annotationValue);
			if (value instanceof Type) {
				value = createTypeName((Type)value);
			}
			return value;
		}

		if (annotationValue instanceof Object[]) {
			Object[] arrVal = (Object[]) annotationValue;
			Object[] value = new Object[arrVal.length];
			for (int i = 0; i < value.length; i++) {
				value[i] = getValue(binding, arrVal[i], isMetaDataFieldValue);
			}
			return value;
		}

		return annotationValue;
	}
	
	private String asString(String[] strArr) {
		if (strArr == null) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < strArr.length; i++) {
			if (i>0) buffer.append(".");
			buffer.append(strArr[i]);
		}
		return buffer.toString();
	
	}
	
	private TypeName createTypeName(Type type) {
		if (type instanceof Part) {
			Part part = (Part) type;
			PartName partName = factory.createPartName();
			partName.setId(part.getId());
			partName.setPackageName(part.getPackageName());
			return partName;
		}
		else {
			String key = type.getMofSerializationKey();
			TypeName typeName = factory.createTypeName();
			typeName.setId(key);
			return typeName;
		}
	}

	private Expression createExpression(IBinding binding, IDataBinding valueBinding, boolean addImplicitQualifier) {
		SimpleName name = new ExpressionParser(getCompilerOptions()).parseAsSimpleName(valueBinding.getCaseSensitiveName());
		name.setDataBinding(valueBinding);
		name.setTypeBinding(valueBinding.getType());
		name.setBinding(valueBinding);
		if (addImplicitQualifier) {
			name.setAttribute(org.eclipse.edt.compiler.core.ast.Name.IMPLICIT_QUALIFIER_DATA_BINDING, binding);
		}

		return name;
	}
	
	private ICompilerOptions getCompilerOptions() {
		ICompilerOptions compilerOptions = DefaultCompilerOptions.getInstance();
		return compilerOptions;
	}

	private ITypeBinding getContainer(IDataBinding db) {
		if (db.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
			return ((StructureItemBinding) db).getEnclosingStructureBinding();
		}
		return db.getDeclaringPart();
	}

	private LibraryBinding getLibraryContainer(IBinding binding) {
		if (binding == null) {
			return null;
		}
		if (binding instanceof LibraryBinding) {
			return (LibraryBinding) binding;
		}
		if (binding instanceof IDataBinding) {
			IPartBinding part = ((IDataBinding) binding).getDeclaringPart();
			if (part != null && part.getKind() == ITypeBinding.LIBRARY_BINDING) {
				return (LibraryBinding) part;
			}
		}
		return null;
	}
	
	protected void eAdd(EObject target, String fieldName, Object value) {
		((List)target.eGet(fieldName)).add(value);
	}
	
	private boolean shouldReflect(IAnnotationBinding reflectTypeBinding, String fieldName) {
		if (reflectTypeBinding != null && reflectTypeBinding.getAnnotationType() instanceof IPartSubTypeAnnotationTypeBinding) {
			IPartSubTypeAnnotationTypeBinding subType = (IPartSubTypeAnnotationTypeBinding)reflectTypeBinding.getAnnotationType();
			FlexibleRecordBinding record = subType.getAnnotationRecord();
			if (record != null) {
				IDataBinding binding = record.findData(InternUtil.intern(fieldName));
				if (binding != null) {
					if (binding.getAnnotation(IEGLConstants.PACKAGE_EGL_LANG_REFLECT, IEGLConstants.PROPERTY_NOREFLECT) != null) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private boolean isSpecialNamedElementCase(Object o, EType type) {
		if (o instanceof String && type instanceof EClass) {
			if (((EClass)type).isSubClassOf(factory.getNamedElementEClass())) {
				return true;
			}
			
			//more special processing to handle the case where the system mof parts are being compiled. In this case, there can be
			//an object identity problem with the type object retrieved from the field, and the types returned by the environment
			EObject newType = getMofSerializable(type.getMofSerializationKey());
			if (newType instanceof EClass) {
				if (((EClass)newType).isSubClassOf(factory.getNamedElementEClass())) {
					return true;
				}
			}
		}
		return false;
	}
	protected void setReflectTypeValues(EObject target, IAnnotationBinding reflectTypeBinding) {
		if (reflectTypeBinding == null) return;
		EClass eClass = target.getEClass();
		for (EField field : eClass.getAllEFields()) {
			
			Object obj = mofValueFrom(getFieldValue(reflectTypeBinding, field.getName()));
			if (obj != null) {
				if (shouldReflect(reflectTypeBinding, field.getName())) {
					if (obj instanceof List)
						for (Object o : (List)obj) {
							// Special handling for reflect values that are just strings but can be converted to instances of the target field's type
							EType type = (EType)((EGenericType)field.getEType()).getETypeArguments().get(0);
							if (isSpecialNamedElementCase(o, type)) {
								o = convertStringValueToNamedElementType((String)o, (EClass)type);
							}
							((List)(target.eGet(field))).add(o);
						}
					else {
						EClassifier type = (EClassifier)field.getEType();
						if ((obj instanceof String) && type instanceof EClass && ((EClass)type).isSubClassOf(mof.getENamedElementClass())) {
							obj = convertStringValueToNamedElementType((String)obj, (EClass)type);
						}
						else if (obj instanceof PartName) {
							obj = ((PartName)obj).getPart();
						}
						
						//change to use a proxy part if the type did not resolve correctly
						if (obj instanceof String && type == mof.getETypeClass()) {
							obj = createProxyPart((String) obj);
						}
						target.eSet(field, obj);
					}
				}
			}
		}
		if (target instanceof StereotypeType) {
			setReflectTypeValues(target, getAnnotation(reflectTypeBinding, "Stereotype"));
		}
	}
	
	protected EObject convertStringValueToNamedElementType(String obj, EClass eClass ) {
		EObject result = eClass.newInstance();
		result.eSet("name", obj);
		return result;
	}
	
	protected void updateProxyReferences(IBinding element, EObject obj) {
		ProxyEObject proxy = proxies.get(element);
		if (proxy != null) {
			proxy.updateReferences(obj);
		}
	}
	
	
	protected List<Object> mofValueFrom(Object[] array) {
		List<Object> result = new EList();
		for (Object obj : array) {
			result.add(mofValueFrom(obj));
		}
		return result;
	}
	
	protected List<Object> mofValueFrom(List list) {
		List<Object> result = new EList();
		for (Object obj : list) {
			result.add(mofValueFrom(obj));
		}
		return result;
	}
	
	protected Object mofValueFrom(Object value) {
		if (value instanceof IAnnotationBinding)
			return mofValueFrom((IAnnotationBinding)value);
		if (value instanceof ITypeBinding)
			return mofTypeFor((ITypeBinding)value);
		if (value instanceof EnumerationDataBinding)
			return mofValueFrom((EnumerationDataBinding)value);
		if (value instanceof IAnnotationBinding)
			return mofValueFrom((IAnnotationBinding)value);
		if (value instanceof IDataBinding)
			return getEObjectFor((IDataBinding)value);
		if (value instanceof Part)
			return mofValueFrom((Part)value);
		if (value instanceof Object[])
			return mofValueFrom((Object[])value);
		if (value instanceof List)
			return mofValueFrom((List)value);
		
		return value;	
	}
	
	protected EObject mofValueFrom(Part part) {
		if (!(part instanceof ProxyPart) && isMofProxy(part)) {
			return resolveProxy(part);
		}
		else
			return part;
	}

	protected EObject mofValueFrom(IAnnotationBinding binding) {
		
		if (!Binding.isValidBinding(binding)) {
			return null;
		}
		
		EObject value;
		if (binding.getAnnotationType() instanceof AnnotationTypeBindingImpl) {
			EClass typeClass = (EClass)mofTypeFor(binding.getAnnotationType());
			value = typeClass.newInstance();
		}
		else {
			if (isEMetadataObject(binding)) {
				value = MofFactory.INSTANCE.createEMetadataObject(true);
			}
			else {
				value = IrFactory.INSTANCE.createAnnotation(binding.getCaseSensitiveName());
			}
		}
		
		Iterator i = binding.getAnnotations().iterator();

		while (i.hasNext()) {
			AnnotationBinding subAnnotation = (AnnotationBinding) i.next();
			// For some reason, the fields sometime get added to the
			// annotataionBinding as annotations. Do not add these to the annotations of the value.
			//They will be handled when the AnnotationFields are processed next
			if (!(subAnnotation instanceof AnnotationFieldBinding)) {
				if (value instanceof Annotation) {
					((Annotation)value).getAnnotations().add((Annotation)mofValueFrom(subAnnotation));
				}
				else {
					((EMetadataObject)value).getMetadataList().add((EMetadataObject)mofValueFrom(subAnnotation));
				}
			}
		}
		for(AnnotationFieldBinding field : (List<AnnotationFieldBinding>)binding.getAnnotationFields()) {
			Object fieldValue = mofValueFrom(getValue(field, field.getValue(), isEMetadataObject(binding)));
			value.eSet(field.getCaseSensitiveName(), fieldValue);
		}
		return value;
	}
	
	protected Object mofValueFrom(EnumerationDataBinding dataBinding) {
		Object convertedValue;
		ITypeBinding typeBinding = dataBinding.getType();
		String name = dataBinding.getCaseSensitiveName();
		if (typeBinding.getCaseSensitiveName().equals("ElementKind")) {
			if (inEMetadataTypeContext) {
				if (name.equals(ElementKind_ExternalTypePart))
					convertedValue = getMofSerializable(Type_EClass);
				else if (name.equals(ElementKind_Part))
					convertedValue = getMofSerializable(Type_EClass);
				else if (name.equals(ElementKind_FieldMbr))
					convertedValue = getMofSerializable(Type_EField);
				else if (name.equals(ElementKind_FunctionMbr))
					convertedValue = getMofSerializable(Type_EFunction);
				else	
					convertedValue = mof.getEModelElementClass();
			} else {
				Class<Enum> enumClass;
				try {
					enumClass = (Class<Enum>)Class.forName(Type_ElementKind);
					convertedValue = Enum.valueOf(enumClass, dataBinding.getCaseSensitiveName());
				} catch (ClassNotFoundException e) {
					EEnum enumType = (EEnum)getMofSerializable(Type_ElementKind);
					convertedValue = enumType.getEEnumLiteral(dataBinding.getCaseSensitiveName());
				}
			}
		}
		else {
			Class<Enum> enumClass;
			try {
				enumClass = (Class<Enum>)Class.forName(typeBinding.getPackageQualifiedName());
				convertedValue = Enum.valueOf(enumClass, dataBinding.getCaseSensitiveName());
			} catch (ClassNotFoundException e) {
				EEnum enumType = (EEnum)mofTypeFor(typeBinding);
				convertedValue = enumType.getEEnumLiteral(dataBinding.getCaseSensitiveName());
			}
		}
		return convertedValue;
	}

//	protected Object mofValueFrom(EnumerationDataBinding dataBinding) {
//		Object convertedValue;
//		ITypeBinding typeBinding = dataBinding.getType();
//		String name = dataBinding.getCaseSensitiveName();
//		if (typeBinding.getCaseSensitiveName().equals("ElementKind")) {
//			if (name.equals(ElementKind_RecordPart))
//				convertedValue = getMofSerializable(Type_Record);
//			else if (name.equals(ElementKind_StructuredRecordPart))
//				convertedValue = getMofSerializable(Type_StructuredRecord);
//			else if (name.equals(ElementKind_ExternalTypePart))
//				convertedValue = getMofSerializable(inMofContext ? Type_EClass : Type_ExternalType);
//			else if (name.equals(ElementKind_ProgramPart))
//				convertedValue = getMofSerializable(Type_Program);
//			else if (name.equals(ElementKind_LibraryPart))
//				convertedValue = getMofSerializable(Type_Library);
//			else if (name.equals(ElementKind_HandlerPart))
//				convertedValue = getMofSerializable(Type_Handler);
//			else if (name.equals(ElementKind_InterfacePart))
//				convertedValue = getMofSerializable(Type_Interface);
//			else if (name.equals(ElementKind_ServicePart))
//				convertedValue = getMofSerializable(Type_Service);
//			else if (name.equals(ElementKind_DelegatePart))
//				convertedValue = getMofSerializable(Type_Delegate);
//			else if (name.equals(ElementKind_FormGroupPart))
//				convertedValue = getMofSerializable(Type_FormGroup);
//			else if (name.equals(ElementKind_FormPart))
//				convertedValue = getMofSerializable(Type_Form);
//			else if (name.equals(ElementKind_DataTablePart))
//				convertedValue = getMofSerializable(Type_DataTable);
//			else if (name.equals(ElementKind_DataItemPart))
//				convertedValue = getMofSerializable(Type_DataItem);
//			else if (name.equals(ElementKind_Part))
//				convertedValue = getMofSerializable(Type_Part);
//			else if (name.equals(ElementKind_FieldMbr))
//				convertedValue = getMofSerializable(inMofContext ? Type_EField : Type_Field);
//			else if (name.equals(ElementKind_FunctionMbr))
//				convertedValue = getMofSerializable(inMofContext ? Type_EFunction : Type_Function);
//			else if (name.equals(ElementKind_ConstructorMbr))
//				convertedValue = getMofSerializable(Type_Constructor);
//			else	
//				convertedValue = mof.getEModelElementClass();
//		} else {
//			EEnum enumType = (EEnum)mofTypeFor(typeBinding);
//			convertedValue = enumType.getEEnumLiteral(dataBinding.getCaseSensitiveName());
//		}
//		return convertedValue;
//	}
//

	protected EClass mofMemberTypeFor(IDataBinding binding) {
		EClass mbrType;
		if (binding instanceof NestedFunctionBinding) {
			IAnnotationBinding opAnn = this.getAnnotation(binding.getType(), "Operation");
			if (opAnn == null) {
				mbrType = (EClass)getMofSerializable(Type_Function);
			}
			else {
				mbrType = (EClass)getMofSerializable(Type_Operation);
			}

		}
		else if (binding instanceof ConstructorBinding) {
			mbrType = (EClass)getMofSerializable(Type_Constructor);
		}
		else if (binding instanceof ClassConstantBinding) {
			mbrType = (EClass)getMofSerializable(Type_ConstantField);
		}
		else if (binding instanceof ClassFieldBinding || binding instanceof FlexibleRecordFieldBinding) {
			mbrType = (EClass)getMofSerializable(Type_Field);
		}
		else if (binding instanceof StructureItemBinding) {
			mbrType = (EClass)getMofSerializable(Type_StructuredField);
		}
		else {
			mbrType = (EClass)getMofSerializable(Type_Field);
		}
		return mbrType;
	}
	
	protected EObject mofTypeFromTypedElement(IDataBinding element) {
		ITypeBinding type = null;
		if (element instanceof FunctionBinding) { 
			type = ((FunctionBinding)element).getReturnType();
		} 
		else if (element instanceof NestedFunctionBinding) {
			type = ((FunctionBinding)((NestedFunctionBinding)element).getType()).getReturnType();
		}
		else if (element instanceof ConstructorBinding) {
			type = ((ConstructorBinding)element).getDeclaringPart();
		}
		else {
			if (Binding.isValidBinding(element)) {
				type = element.getType(); 
			}
		}
		if (type != null)
			return mofTypeFor(type);
		else
			return null;
	}
	
	protected EObject mofTypeFromASTFor(IPartBinding binding) {
    	String partName = binding.getCaseSensitiveName();
        File declaringFile = SourcePathInfo.getInstance().getDeclaringFile(binding.getPackageName(), partName);
        Node partAST = ASTManager.getInstance().getAST(declaringFile, partName);
        Egl2Mof converter = new Egl2Mof(env);
        return converter.convert((org.eclipse.edt.compiler.core.ast.Part)partAST, null, null);
	}
	
	
	protected EObject mofTypeFor(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) return null;
		EObject eType;
		switch (type.getKind()) {
		case ITypeBinding.MULTIPLY_OCCURING_ITEM:
			return mofTypeFor(((MultiplyOccuringItemTypeBinding)type).getBaseType());
		case ITypeBinding.ARRAY_TYPE_BINDING: {
			ITypeBinding elementType = ((ArrayTypeBinding)type).getElementType();
			EObject mofType = mofTypeFor(elementType);
			if (inMofContext && mofType instanceof EType) {
				EGenericType generic = mof.createEGenericType(true);
				generic.setEClassifier(mof.getEListEDataType());
				generic.getETypeArguments().add((EType)mofType);
				eType = generic;
			}
			else {
				ArrayType generic = factory.createArrayType();
				generic.setClassifier((Classifier)getMofSerializable(Type_EGLList));
				generic.getTypeArguments().add((Type)mofType);
				generic.setElementsNullable(elementType.isNullable());
				eType = generic;
			}
			break;
		}
		case ITypeBinding.FORM_BINDING: {
			FormGroupBinding fgBinding = ((FormBinding)type).getEnclosingFormGroup();
			eType = null;
			if (fgBinding != null) {
				FormGroup fg = (FormGroup)mofTypeFor(fgBinding);
				for (Form form : fg.getForms()) {
					if (form.getName().equalsIgnoreCase(((FormBinding)type).getName())) {
						eType = form;
						break;
					}
				}
			}
			if (eType != null) {
				break;
			}
			// Fall through.
		}
		default: {
			String key = mofSerializationKeyFor(type); 
			eType = getMofSerializable(key);
			
			//When looking for annotations and stereotypes, search using the EGL scheme if we didnt find it as MOF
			if (eType == null && ! key.startsWith(EGL_KeyScheme) && (isAnnotationType(type) || isStereotypeType(type))) {
				key = EGL_KeyScheme + key;
				eType = getMofSerializable(key);
			}
			if (eType == null) {
				if (type instanceof IPartBinding) {
//					eType = mofTypeFromASTFor((IPartBinding)type);
					if (inMofContext && isMofClass((PartBinding)type) && type instanceof ExternalTypeBinding) {
						eType = createTempEClass((ExternalTypeBinding)type);
					}
					else {
						eType = createProxyPart(((IPartBinding)type).getPackageQualifiedName());
					}
				}
				else if (type instanceof PrimitiveTypeBinding) {			
					PrimitiveTypeBinding prim = ((PrimitiveTypeBinding)type);
					eType = createPrimitiveType(key, prim);
					switch (prim.getPrimitive().getType()) {
						case Primitive.CHAR_PRIMITIVE:
						case Primitive.MBCHAR_PRIMITIVE:
						case Primitive.DBCHAR_PRIMITIVE:
						case Primitive.UNICODE_PRIMITIVE:
						case Primitive.HEX_PRIMITIVE: {
							eSet(eType, "length", prim.getLength());
							break;
						}
						case Primitive.DECIMAL_PRIMITIVE: 
						case Primitive.BIN_PRIMITIVE:
						case Primitive.MONEY_PRIMITIVE:
						case Primitive.NUM_PRIMITIVE: {
							eSet(eType, "length", prim.getLength());
							eSet(eType, "decimals", prim.getDecimals());
							break;
						}
						case Primitive.INTERVAL_PRIMITIVE:
						case Primitive.TIME_PRIMITIVE:
						case Primitive.TIMESTAMP_PRIMITIVE:
						case Primitive.MONTHSPAN_INTERVAL_PRIMITIVE:
						case Primitive.SECONDSPAN_INTERVAL_PRIMITIVE: {
							eSet(eType, "pattern", getPattern(prim));
							break;
						}
					}
				}
				else if (type instanceof IAnnotationTypeBinding) {
					eType = createAnnotationType((IAnnotationTypeBinding)type);
				}
			}
		}
		}
		return eType;
	}
	
	
	protected String mofTypeSignatureFor(ITypeBinding type, boolean isArrayType) {
		String typeSignature;
		if (type instanceof PrimitiveTypeBinding) 
			typeSignature = mofPrimitiveTypeSignatureFor((PrimitiveTypeBinding)type);
		else if (type instanceof DictionaryBinding) {
			typeSignature = Type_EGLDictionary;
		}
		else if (type instanceof ArrayDictionaryBinding) {
			typeSignature = Type_EGLArrayDictionary;
		}
		else if (type instanceof ArrayTypeBinding) {
			typeSignature = inMofContext ? Type_EList : Type_EGLList;
			typeSignature += "<" + mofTypeSignatureFor(((ArrayTypeBinding)type).getElementType(), isArrayType) + ">";
		}
		else if (type instanceof IAnnotationTypeBinding) {
			if ((inMofContext &&  !inAnnotationTypeContext) || isEMetadataType(type) ) {
				typeSignature = type.getPackageQualifiedName();
			}
			else {
				typeSignature = EGL_KeyScheme + type.getPackageQualifiedName();
			}
		}
		else if (type instanceof IPartBinding) {
			if (isEDTReflectType((IPartBinding)type))
				typeSignature = mofEDTReflectTypeSignature((IPartBinding)type);
			else if (isMofProxy((IPartBinding)type) && !inMofProxyContext) { 
				IAnnotationBinding ann = ((IPartBinding)type).getSubTypeAnnotationBinding();
				typeSignature = (String)getFieldValue(ann, "packageName");
				if (typeSignature == null) {
					typeSignature = concatWithSeparator(type.getPackageName(), ".");
				}
				typeSignature += ".";
				String name = (String)getFieldValue(ann, "name");
				if (name == null || "".equals(name)) name = type.getCaseSensitiveName();
				typeSignature += name;
			}
			else if (isReflectType((IPartBinding)type) && !inMofProxyContext) {
				IAnnotationBinding ann = ((IPartBinding)type).getSubTypeAnnotationBinding();
				String name = (String)getFieldValue(ann, "name");
				if (name == null) name = type.getCaseSensitiveName();
				typeSignature = concatWithSeparator(type.getPackageName(), ".");
				typeSignature += ".";
				typeSignature += name;
				if (!isMofReflectType((IPartBinding)type) && isEGLReflectType((IPartBinding)type) || inMofProxyContext) {
					typeSignature = EGL_KeyScheme + typeSignature; 
				}
			}
			else	
				typeSignature = EGL_KeyScheme + type.getPackageQualifiedName();
		}
		else
			typeSignature = Type_EObject;
		return typeSignature;
	}
	
	boolean isEDTReflectType(IPartBinding binding) {
		String name = binding.getName();
		return name.equalsIgnoreCase("fieldRef")
			|| name.equalsIgnoreCase("internalRef")
			|| name.equalsIgnoreCase("partRef")
			|| name.equalsIgnoreCase("functionRef")
			|| name.equalsIgnoreCase("functionMemberRef")
			|| name.equalsIgnoreCase("typeRef")
			|| name.equalsIgnoreCase("sqlString")
			|| name.equalsIgnoreCase("FieldInTargetRef")
			|| name.equalsIgnoreCase("RecordRef")
			|| name.equalsIgnoreCase("serviceRef");
	}
	
	String mofEDTReflectTypeSignature(IPartBinding binding) {

		String typeSignature;
		boolean isMofTypeRef = inMofContext && !inAnnotationTypeContext;
		if (binding.getName().equalsIgnoreCase("fieldRef")) {
			typeSignature = isMofTypeRef ? Type_EField : Type_FieldRef;
		}
		else if (binding.getName().equalsIgnoreCase("fieldInTargetRef")) {
			typeSignature = isMofTypeRef ? Type_EModelElement : Type_FieldInTargetRef;
		}
		else if (binding.getName().equalsIgnoreCase("internalRef")) {
			typeSignature = isMofTypeRef ? Type_EModelElement : Type_InternalRef;
		}
		else if (binding.getName().equalsIgnoreCase("partRef")) {
			typeSignature = isMofTypeRef ? Type_EClassifier : Type_PartRef;
		}
		else if (binding.getName().equalsIgnoreCase("functionRef")) {
			typeSignature = isMofTypeRef ? Type_EFunction : Type_FunctionRef;
		}
		else if (binding.getName().equalsIgnoreCase("functionMemberRef")) {
			typeSignature = isMofTypeRef ? Type_EFunction : Type_FunctionMemberRef;
		}
		else if (binding.getName().equalsIgnoreCase("typeRef")) {
			typeSignature = isMofTypeRef ? Type_EType : Type_TypeRef;
		}
		else if (binding.getName().equalsIgnoreCase("serviceRef")) {
			typeSignature = isMofTypeRef ? Type_EClassifier : Type_ServiceRef;
		}
		else if (binding.getName().equalsIgnoreCase("recordRef")) {
			typeSignature = isMofTypeRef ? Type_EClassifier : Type_RecordRef;
		}
		else if (binding.getName().equalsIgnoreCase("sqlString")) {
			typeSignature = isMofTypeRef ? Type_EString : Type_SQLStringRef;
		}
		else 
			typeSignature = Type_EObject;
		
		return typeSignature;
	}
	
	protected Type mofPrimitiveFor(Primitive type) {
		String key = mofPrimitiveSignatureFor(type);
		return (Type)getMofSerializable(key);
	}
	
	protected String mofPrimitiveSignatureFor(Primitive type) {
		String typeSignature;
		switch (type.getType()) {
			case Primitive.ANY_PRIMITIVE: typeSignature = inMofContext ? Type_JavaObject : Type_EGLAny; break;
			case Primitive.BOOLEAN_PRIMITIVE: typeSignature = inMofContext ? Type_EBoolean : Type_EGLBoolean;break;
			case Primitive.INT_PRIMITIVE: typeSignature = inMofContext ? Type_EInteger : Type_EGLInt;break;
			case Primitive.STRING_PRIMITIVE: typeSignature = inMofContext ? Type_EString : Type_EGLString;break;
			case Primitive.BIGINT_PRIMITIVE: typeSignature = Type_EGLBigint;break; 
			case Primitive.BIN_PRIMITIVE: typeSignature = Type_EGLBin;break; 
			case Primitive.BLOB_PRIMITIVE: typeSignature = Type_EGLBlob;break; 
			case Primitive.CHAR_PRIMITIVE: typeSignature = inMofContext ? Type_EString : Type_EGLChar;break; 
			case Primitive.CLOB_PRIMITIVE: typeSignature = Type_EGLClob;break; 
			case Primitive.DATE_PRIMITIVE: typeSignature = Type_EGLDate;break; 
			case Primitive.DBCHAR_PRIMITIVE: typeSignature = Type_EGLDBChar;break; 
		//	case Primitive.DBCHARLIT_PRIMITIVE: typeSignature = Type_EGLDBCharLit;break; 
			case Primitive.DECIMAL_PRIMITIVE: typeSignature = inMofContext ? Type_EDecimal : Type_EGLDecimal;break; 
			case Primitive.FLOAT_PRIMITIVE: typeSignature = inMofContext ? Type_EFloat : Type_EGLFloat;break; 
			case Primitive.HEX_PRIMITIVE: typeSignature = Type_EGLHex;break; 
			case Primitive.INTERVAL_PRIMITIVE: typeSignature = Type_EGLInterval;break; 
			case Primitive.MBCHAR_PRIMITIVE: typeSignature = Type_EGLMBChar;break; 
			case Primitive.MONEY_PRIMITIVE: typeSignature = Type_EGLDecimal; break;
			case Primitive.MONTHSPAN_INTERVAL_PRIMITIVE: typeSignature = Type_EGLMonthInterval;break; 
			case Primitive.NUM_PRIMITIVE: typeSignature = Type_EGLNum; break;
			case Primitive.NUMBER_PRIMITIVE: typeSignature = Type_EGLNumber; break;
			case Primitive.NUMC_PRIMITIVE: typeSignature = Type_EGLNumc; break;
			case Primitive.PACF_PRIMITIVE: typeSignature = Type_EGLPacf; break;
			case Primitive.SECONDSPAN_INTERVAL_PRIMITIVE: typeSignature = Type_EGLSecondsInterval;break; 
			case Primitive.SMALLFLOAT_PRIMITIVE: typeSignature = Type_EGLSmallfloat; break;
			case Primitive.SMALLINT_PRIMITIVE: typeSignature = Type_EGLSmallint; break;
			case Primitive.TIME_PRIMITIVE: typeSignature = Type_EGLTime; break;
			case Primitive.TIMESTAMP_PRIMITIVE : typeSignature = Type_EGLTimestamp;break; 
			case Primitive.UNICODE_PRIMITIVE : typeSignature = Type_EGLUnicode; break;
			default: typeSignature = Type_EGLAny;
		}
		return typeSignature;
	}

	
	protected String mofPrimitiveTypeSignatureFor(PrimitiveTypeBinding type) {
		String typeSignature = mofPrimitiveSignatureFor(type.getPrimitive());	
		if (inMofContext) return typeSignature;
		
		switch (type.getPrimitive().getType()) {
		case Primitive.CHAR_PRIMITIVE:
		case Primitive.MBCHAR_PRIMITIVE:
		case Primitive.DBCHAR_PRIMITIVE:
		case Primitive.UNICODE_PRIMITIVE:
		case Primitive.HEX_PRIMITIVE:
		case Primitive.STRING_PRIMITIVE: {
			typeSignature += (type.getLength() == 0 ? "" : "(" + type.getLength() + ")");
			break;
		}
		case Primitive.DECIMAL_PRIMITIVE: 
		case Primitive.BIN_PRIMITIVE:
		case Primitive.MONEY_PRIMITIVE:
		case Primitive.NUM_PRIMITIVE: {
			typeSignature += (type.getLength() == 0 ? "" : "(" + type.getLength() + Type.PrimArgDelimiter + type.getDecimals() + ")");
			break;
		}
		case Primitive.INTERVAL_PRIMITIVE:
		case Primitive.MONTHSPAN_INTERVAL_PRIMITIVE:
		case Primitive.SECONDSPAN_INTERVAL_PRIMITIVE: {
			typeSignature += (type.getPattern() == null ? "" : "(" + getPattern(type) + ")");
			break;
		}
//		case Primitive.TIME_PRIMITIVE:
		case Primitive.TIMESTAMP_PRIMITIVE: {
			typeSignature += (type.getPattern() == null ? "" : "(" + getPattern(type) + ")");
			break;
		}

	}
	
		return typeSignature;
	}
	
	private String getPattern(PrimitiveTypeBinding type) {
		return new TimeStampAndIntervalPatternFixer(type.getPattern()).toString();
	}
	
	protected String getParameterizedTypeSignature(PrimitiveTypeBinding type) {
		String typeSignature = null;
		if (inMofContext) return typeSignature;
		
		switch (type.getPrimitive().getType()) {
			case Primitive.CHAR_PRIMITIVE:
			case Primitive.MBCHAR_PRIMITIVE:
			case Primitive.DBCHAR_PRIMITIVE:
			case Primitive.UNICODE_PRIMITIVE:
			case Primitive.HEX_PRIMITIVE:
			case Primitive.STRING_PRIMITIVE: {
				typeSignature = Type_SequenceType;
				break;
			}
			case Primitive.DECIMAL_PRIMITIVE: 
			case Primitive.BIN_PRIMITIVE:
			case Primitive.MONEY_PRIMITIVE:
			case Primitive.NUM_PRIMITIVE: {
				typeSignature = Type_FixedPrecisionType;
				break;
			}
			case Primitive.INTERVAL_PRIMITIVE:
			case Primitive.MONTHSPAN_INTERVAL_PRIMITIVE:
			case Primitive.SECONDSPAN_INTERVAL_PRIMITIVE: {
				typeSignature = Type_IntervalType;
				break;
			}
	//		case Primitive.TIME_PRIMITIVE:
			case Primitive.TIMESTAMP_PRIMITIVE: {
				typeSignature = Type_TimestampType;
				break;
			}

		}
	
		return typeSignature;
	}
	
	protected boolean isParameterizedType(PrimitiveTypeBinding type) {
		boolean is = false;
		switch (type.getPrimitive().getType()) {
			case Primitive.TIMESTAMP_PRIMITIVE: {
				is = type.getPattern() != null;
				break;
			}
			case Primitive.CHAR_PRIMITIVE:
			case Primitive.MBCHAR_PRIMITIVE:
			case Primitive.DBCHAR_PRIMITIVE:
			case Primitive.UNICODE_PRIMITIVE:
			case Primitive.HEX_PRIMITIVE:
			case Primitive.STRING_PRIMITIVE: 
			case Primitive.DECIMAL_PRIMITIVE: 
			case Primitive.BIN_PRIMITIVE:
			case Primitive.MONEY_PRIMITIVE:
			case Primitive.NUM_PRIMITIVE: 
			case Primitive.INTERVAL_PRIMITIVE:
			case Primitive.MONTHSPAN_INTERVAL_PRIMITIVE:
			case Primitive.SECONDSPAN_INTERVAL_PRIMITIVE: {
	//		case Primitive.TIME_PRIMITIVE:
				is = true;
				break;
			}
			default: is = false;
		}
		return (is && (type.getLength() != 0 || type.getDecimals() != 0 || type.getPattern() != null));
	}

//	protected EClass getMofPrimitiveTypeEClass(PrimitiveTypeBinding type) {
//		EClass eClass;
//		switch (type.getPrimitive().getType()) {
//			case Primitive.ANY_PRIMITIVE: eClass=factory.getPrimitiveTypeEClass(); break;
//			case Primitive.BOOLEAN_PRIMITIVE: eClass=factory.getBooleanTypeEClass();
//			case Primitive.INT_PRIMITIVE: eClass=factory.getIntegerTypeEClass();break;
//			case Primitive.STRING_PRIMITIVE: eClass=factory.getTextTypeEClass();break;
//			case Primitive.BIGINT_PRIMITIVE: eClass=factory.getIntegerTypeEClass();break; 
//			case Primitive.BIN_PRIMITIVE: eClass=factory.getFixedPrecisionTypeEClass();break; 
//			case Primitive.BLOB_PRIMITIVE: eClass=factory.getTextTypeEClass();break; 
//			case Primitive.CHAR_PRIMITIVE: eClass=factory.getTextTypeEClass();break; 
//			case Primitive.CLOB_PRIMITIVE: eClass=factory.getLobTypeEClass();break; 
//			case Primitive.DATE_PRIMITIVE: eClass=factory.getTimeTypeEClass();break; 
//			case Primitive.DBCHAR_PRIMITIVE: eClass=factory.getTextTypeEClass();break; 
//		//	case Primitive.DBCHARLIT_PRIMITIVE: eClassSignature = Type_EGLDBCharLit;break; 
//			case Primitive.DECIMAL_PRIMITIVE: eClass=factory.getFixedPrecisionTypeEClass();break; 
//			case Primitive.FLOAT_PRIMITIVE: eClass=factory.getFloatTypeEClass();break; 
//			case Primitive.HEX_PRIMITIVE: eClass=factory.getBinaryTypeEClass();break; 
//		//	case Primitive.INTERVAL_PRIMITIVE: eClassSignature = Type_EGLInterval;break; 
//			case Primitive.MBCHAR_PRIMITIVE: eClass=factory.getTextTypeEClass();break; 
//			case Primitive.MONEY_PRIMITIVE: eClass=factory.getFixedPrecisionTypeEClass(); break;
//			case Primitive.MONTHSPAN_INTERVAL_PRIMITIVE: eClass=factory.getTimeTypeEClass();break; 
//			case Primitive.NUM_PRIMITIVE: eClass=factory.getFixedPrecisionTypeEClass(); break;
////			case Primitive.NUMBER_PRIMITIVE: eClassSignature = Type_EGLNumber; break;
//			case Primitive.NUMC_PRIMITIVE: eClass=factory.getFixedPrecisionTypeEClass(); break;
//			case Primitive.PACF_PRIMITIVE: eClass=factory.getFixedPrecisionTypeEClass(); break;
//			case Primitive.SECONDSPAN_INTERVAL_PRIMITIVE: eClass=factory.getTimeTypeEClass();break; 
//			case Primitive.SMALLFLOAT_PRIMITIVE: eClass=factory.getFloatTypeEClass(); break;
//			case Primitive.SMALLINT_PRIMITIVE: eClass=factory.getIntegerTypeEClass(); break;
//			case Primitive.TIME_PRIMITIVE: eClass=factory.getTimeTypeEClass(); break;
//			case Primitive.TIMESTAMP_PRIMITIVE : eClass=factory.getTimeTypeEClass();break; 
//			case Primitive.UNICODE_PRIMITIVE : eClass=factory.getTextTypeEClass(); break;
//			default: eClass=factory.getPrimitiveTypeEClass();
//		}
//		return eClass;
//	}

	protected String mofPartTypeSignatureFor(org.eclipse.edt.compiler.core.ast.Part part) {
		IPartBinding binding = (IPartBinding)part.getName().resolveBinding();
		return mofPartTypeSignatureFor(binding);
	}
	
	protected String mofPartTypeSignatureFor(IPartBinding partBinding) {
		String typeSignature;
		if (isReflectType(partBinding)) {
			typeSignature = getReflectedTypeSignature(partBinding);
		}
		else {
			switch (partBinding.getKind()) {
			case ITypeBinding.DATAITEM_BINDING: typeSignature = Type_DataItem; break;
			case ITypeBinding.DATATABLE_BINDING: typeSignature = Type_DataTable; break;
			case ITypeBinding.DELEGATE_BINDING: typeSignature = Type_Delegate; break;
			case ITypeBinding.ENUMERATION_BINDING: typeSignature = inMofContext ? Type_EEnum : Type_Enumeration; break;
			case ITypeBinding.EXTERNALTYPE_BINDING: typeSignature = inMofContext ? Type_EClass : Type_ExternalType; break;
			case ITypeBinding.FIXED_RECORD_BINDING: typeSignature = Type_StructuredRecord; break;
			case ITypeBinding.FLEXIBLE_RECORD_BINDING: typeSignature = Type_Record; break;
			case ITypeBinding.FORM_BINDING: typeSignature = Type_Form; break;
			case ITypeBinding.FORMGROUP_BINDING: typeSignature = Type_FormGroup; break;
			case ITypeBinding.FUNCTION_BINDING: typeSignature = Type_FunctionPart; break;
			case ITypeBinding.HANDLER_BINDING: typeSignature = Type_Handler; break;
			case ITypeBinding.INTERFACE_BINDING: typeSignature = Type_Interface; break;
			case ITypeBinding.LIBRARY_BINDING: typeSignature = Type_Library; break;
			case ITypeBinding.PROGRAM_BINDING: typeSignature = Type_Program; break;
			case ITypeBinding.SERVICE_BINDING: typeSignature = Type_Service; break;
			default: typeSignature = Type_EClass;
			}
		}
		return typeSignature;

	}

	protected EObject getMofSerializable(String mofSignature) { 
		EObject result = null;
		try {
			result = env.find(mofSignature);
		} catch (MofObjectNotFoundException e) {
			result = processed.get(mofSignature);
		} catch (DeserializationException e2) {
			e2.printStackTrace();
			result = null;
		}
		return result;
	}
	

	protected String mofSerializationKeyFor(ITypeBinding binding) {
		return mofTypeSignatureFor(binding, false);
	}
	
	protected EObject newEObject(String typeSignature) {
		EClass eClass = (EClass)getMofSerializable(typeSignature);
		return eClass.newInstance();
	}
	
	protected EClass createAnnotationType(IAnnotationBinding annotation) {
		EClass annType = createAnnotationType((IAnnotationTypeBinding)annotation.getType());
		
		// If there was no real EGL definition for the annotation type then make one up
		// based on the annotation value itself.  This is the case for annotations used
		// by the system to add metadata on the fly such as eglLocation, len, etc.
		if (annType == null) {
			annType = mof.createEMetadataType(true);	
			annType.setName(annotation.getCaseSensitiveName());
			annType.setPackageName("");
			for(AnnotationFieldBinding fieldBinding : (List<AnnotationFieldBinding>)annotation.getAnnotationFields()) {
				EField field = mof.createEField(true);
				field.setName(fieldBinding.getCaseSensitiveName());
				EClassifier type;
				if (fieldBinding.getValue() instanceof String) {
					type = (EClassifier)getMofSerializable(Type_EString);
				} else if (fieldBinding.getValue() instanceof Boolean) {
					type = (EClassifier)getMofSerializable(Type_EBoolean);
				} else if (fieldBinding.getValue() instanceof Integer) {
					type = (EClassifier)getMofSerializable(Type_EInteger);
				} else if (fieldBinding.getValue() instanceof List) {
					type = (EClassifier)getMofSerializable(Type_EList);
				} else { type = (EClassifier)getMofSerializable(Type_EMetadataObject); }
				field.setEType(type);
				field.setDeclarer(annType);
				annType.getEFields().add(field);
			}
		}
		return annType;
	}
	
	// Creates an AnnotationType from a Flexibile record that has been stereotyped
	// as an Annotation or Stereotype
	protected EClass createAnnotationType(IAnnotationTypeBinding typeBinding) {
		if (typeBinding instanceof AnnotationTypeBindingImpl) {
			return createAnnotationType((AnnotationTypeBindingImpl)typeBinding);
		}
		else {
			AnnotationType annType = (AnnotationType)IrFactory.INSTANCE.createAnnotationType();
			return annType;
		}
	}
	
	protected EClass createAnnotationType(AnnotationTypeBindingImpl part) {
		EClass record = null;
		
		FlexibleRecordBinding annType = part.getAnnotationRecord();
		if (annType != null) {
			record = (EClass)newEObject(mofPartTypeSignatureFor(annType));
			record.setName(part.getCaseSensitiveName());
			record.setPackageName(concatWithSeparator(part.getPackageName(), "."));
			
			// Set up the valid targets specified with the stereotype associated with the annType record binding
			boolean previousInEMetadataTypeContext = inEMetadataTypeContext;
			if (record instanceof EMetadataType) {
				inEMetadataTypeContext = true;
			}
			IAnnotationBinding subtype = annType.getSubTypeAnnotationBinding();
			List targets = mofValueFrom((Object[])getFieldValue(subtype, "targets"));
			if (record instanceof EMetadataType) {
				for (Object eClass : targets) {
					((EMetadataType)record).getTargets().add((EClass)eClass);
				}
			}
			else {
				for (Object literal : targets) {
					((AnnotationType)record).getTargets().add((ElementKind)literal);
				}
			}
			inEMetadataTypeContext = previousInEMetadataTypeContext;
			// TODO: Nested annotation types will not work properly here
			// as this context is also used to choose the correct type of
			// MetadataType. i.e. An EMetadataType or an AnnotationType
			boolean currentMofContext = inMofContext;
			inMofContext = true;
			for (IDataBinding field : (List<IDataBinding>)annType.getDeclaredFields() ) {
				EField eField = mof.createEField(true);
				eField.setName(field.getCaseSensitiveName());
				
				eField.setEType((EType)mofTypeFor(field.getType()));
				eField.setContainment(true);
				
				((EClass)record).addMember(eField);
			}
			inMofContext = currentMofContext;
			
			if (record instanceof StereotypeType) {
				record.getSuperTypes().set(0, (EClass)getMofSerializable(Type_Stereotype));
			}
			else if (record instanceof AnnotationType) {			
				record.getSuperTypes().set(0, (EClass)getMofSerializable(Type_Annotation));
			}
		}
		env.save(record, false);
		return record;
	}
		
	
	protected EClass createTempEClass(ExternalTypeBinding part) {
		EClass eClass = mof.createEClass(true);
		eClass.setName(part.getCaseSensitiveName());
		eClass.setPackageName(concatWithSeparator(part.getPackageName(), "."));
		String key = mofSerializationKeyFor(part);
		env.save(key, eClass, false);
		for (ClassFieldBinding binding : (List<ClassFieldBinding>)part.getDeclaredData()) {
			EField field = mof.createEField(true);
			field.setName(binding.getCaseSensitiveName());
			EType type = null;
			try {
				type = (EType)mofTypeFor(binding.getType());
				field.setEType(type);
				if (binding.getType().isNullable()) {
					field.setIsNullable(true);
				}
			} catch (CircularBuildRequestException e) {
				// If this occurs just assume JavaObject type
				type = MofFactory.INSTANCE.getEObjectClass();
			}
			eClass.addMember(field);
			field.setDeclarer(eClass);
		}
		IAnnotationBinding subtype = part.getSubTypeAnnotationBinding();
		setReflectTypeValues(eClass, subtype);
		eClass.getMetadataList().add(getTempClassMarker());
		List<EClass> superTypes = new ArrayList<EClass>();
		for (ITypeBinding superType : (List<ITypeBinding>)part.getExtendedTypes()) {
			superTypes.add((EClass)mofTypeFor(superType));
		}
		eClass.addSuperTypes(superTypes);
		return eClass;
	}
	
	protected boolean isTempEClass(EObject eclass) {
		return eclass instanceof EClass
			? ((EClass)eclass).getMetadata(TempEClassMarker) != null
			: false;
	}
	
	protected EMetadataObject getTempClassMarker() {
		EMetadataType temp = (EMetadataType)getMofSerializable(TempEClassMarker);
		if (temp == null) {
			// Set up metadata type for marking an EClass as temporary
			temp = mof.createEMetadataType(true);
			temp.setName(TempEClassMarker);
			EField value = mof.createEField(true);
			value.setName("value");
			value.setEType(mof.getEBooleanEDataType());
			value.setDeclarer(temp);
			value.setInitialValue(true);
			temp.getEFields().add(value);
			env.save(TempEClassMarker, temp, false);
		}
		return (EMetadataObject)temp.newInstance();
	}

	protected ProxyEObject createProxyPart(String name) {
		ProxyEObject type;
		String key;
		if (inMofContext) {
			type = new ProxyEClass(name);
			key = ((ProxyEClass)type).getMofSerializationKey();
		}
		else {
			type = new ProxyPart(name);
			key = ((ProxyPart)type).getMofSerializationKey();
		}
		env.save(key, type, false);
		return type;
	}
 	
	protected Type createPrimitiveType(String key, PrimitiveTypeBinding part) {
		String parmTypeSignature = getParameterizedTypeSignature(part);
		Type type = null;
		if (parmTypeSignature != null && isParameterizedType(part)) {
			type = (ParameterizedType)((EClass)getMofSerializable(parmTypeSignature)).newInstance();
			String primSignature = mofPrimitiveSignatureFor(part.getPrimitive());
			int i = primSignature.indexOf(KeySchemeDelimiter);
			primSignature = i == -1 ? primSignature : primSignature.substring(i+1);
			ProxyPart eprim = new ProxyPart(primSignature);
			((ParameterizedType)type).setParameterizableType(eprim);
		}
		else {
			type = new ProxyPart(mofPrimitiveTypeSignatureFor(part));
		}
		env.save(key, type, false);
		return type;
	}

	protected String concatWithSeparator(String[] fragments, String separator) {
		StringBuffer result = new StringBuffer();
		for (int i=0; i<fragments.length; i++) {
			result.append(fragments[i]);
			if (i < fragments.length-1) {
				result.append(separator);
			}
		}
		return result.toString();
	}
	
	protected void eSet(EObject target, String fieldName, Object value) {
		EField field = target.getEClass().getEField(fieldName);
		eSet(target, field, value);
	}
	
	protected void eSet(EObject target, EField field, Object value) {
		Object convertedValue = mofValueFrom(value);
		
		if (field.getEType() instanceof EEnum && convertedValue instanceof Name) {
			EEnumLiteral lit = ((EEnum)field.getEType()).getEEnumLiteral(((Name)convertedValue).getId());
			if (lit != null) {
				convertedValue = lit;
			}
		}
		// Check for enum array
		else {
			if (field.getEType() instanceof EGenericType && ((EGenericType)field.getEType()).getETypeArguments().size() > 0 &&  ((EGenericType)field.getEType()).getETypeArguments().get(0) instanceof EEnum) {
				if ( convertedValue instanceof EList) {
					
					EEnum enumObj = (EEnum)((EGenericType)field.getEType()).getETypeArguments().get(0);
					EList<Object> newValue =  new EList<Object>();
					for (Object obj : (EList<Object>) convertedValue) {
						
						Object val = obj;
						if (obj instanceof Name) {
							EEnumLiteral lit = enumObj.getEEnumLiteral(((Name)obj).getId());
							if (lit != null) {
								val = lit;
							}
						}
						newValue.add(val);
					}
					convertedValue = newValue;
				}
			}
		}
		target.eSet(field, convertedValue);
	}
	
	protected void eAdd(List list, Object value) {
		Object convertedValue = null;
		if (value instanceof Object[]) {
			for (Object obj : (Object[])value) {
				convertedValue = mofValueFrom(obj);
				list.add(convertedValue);
			}
		} else {
			for (Object obj : (List)value) {
				convertedValue = mofValueFrom(obj);
				list.add(convertedValue);
			}
		}
	}
	
	protected boolean isAbstractFunction(Function function) {
		return function != null && function.getContainer() instanceof ExternalType;
	}
	
	// A ReflectType is an EGL type stereotyped by a Stereotype
	// marked as one that represents a type in the underlying meta model.
	// In this way such types can be represented in EGL but are then
	// converted into instances of the Reflect type the stereotype
	// represents.  For intstance, Record parts stereotyped by Annotation are then
	// converted into instances of the reflected type "org.eclipse.edt.mof.egl.AnnotationType"
	// as the stereotype "egl.lang.Annotation" reflects the underlying model.
	protected boolean isReflectType(ITypeBinding typeBinding) {
		if (!(typeBinding instanceof IPartBinding)) return false;
		IPartBinding partBinding = (IPartBinding)typeBinding;
		boolean isReflectType = isAnnotationType(partBinding) || isStereotypeType(partBinding) || isEMetadataType(partBinding);
		if (!isReflectType) {
			isReflectType = getAnnotation(typeBinding, "PartType") != null;
			if (!isReflectType && typeBinding instanceof IPartBinding) {
				isReflectType = getAnnotation(((IPartBinding)typeBinding).getSubType(), "PartType") != null;
			}
		}
		return isReflectType;
	}
	
	/**
	 * Return the default super type as defined by the @DefaultSuperType annotation
	 * @param typeBinding
	 * @return
	 */
	protected MofSerializable getDefaultSuperType(ITypeBinding typeBinding) {	
		if (!(typeBinding instanceof IPartBinding)) return null;
		IPartBinding partBinding = (IPartBinding)typeBinding;
		IAnnotationTypeBinding subtype = partBinding.getSubType();
		String superTypeName = null;
		if (subtype != null) {
			superTypeName = getDefaultSuperTypeSignature((IPartBinding)typeBinding);
		}
		return superTypeName != null ? (MofSerializable)getMofSerializable(superTypeName) : null;
	}
	/**
	 * Assumes that the typeBinding parameter is a reflected type as defined by isReflectType(ITypeBinding)
	 * @param typeBinding
	 * @return
	 */
	protected EClass getReflectedType(ITypeBinding typeBinding) {	
		if (!(typeBinding instanceof IPartBinding)) return null;
		IPartBinding partBinding = (IPartBinding)typeBinding;
		IAnnotationTypeBinding subtype = partBinding.getSubType();
		String reflectedTypeName = null;
		if (subtype != null) {
			reflectedTypeName = getReflectedTypeSignature((IPartBinding)typeBinding);
		}
		return reflectedTypeName != null ? (EClass)getMofSerializable(reflectedTypeName) : null;
	}
	
	/**
	 * Is the parameter typeBinding a ReflectType that reflects onto the MOF meta model
	 * @param typeBinding
	 * @return
	 */
	// TODO: Update definition of when a type is a Reflect type to use generalized 
	// @ReflectType annotation when syntax allows it so as to not use the hard coded
	// implementation below
	protected boolean isMofReflectType(ITypeBinding typeBinding) {
		EClass reflectedType = getReflectedType(typeBinding);
		if (reflectedType != null) {
			return reflectedType.isSubClassOf(MofFactory.INSTANCE.getEModelElementClass());
		}
		// TODO: Annotation and Stereotype themselves are not represented as ReflectTypes
		// because their definitions are still part of the old IR model in the
		// system parts eglar
		return false;
	}
	
	/**
	 * Is the parameter typeBinding a ReflectType that reflects onto the EGL meta model
	 * @param typeBinding
	 * @return
	 */
	protected boolean isEGLReflectType(ITypeBinding typeBinding) {
		EClass reflectedType = getReflectedType(typeBinding);
		if (reflectedType != null) {
			return reflectedType.isSubClassOf(IrFactory.INSTANCE.getElementEClass());
		}
		// TODO: Annotation and Stereotype themselves are not represented as ReflectTypes
		// because their definitions are still part of the old IR model in the
		// system parts eglar
		return false;
	}

	
	// Assumes the record is a Stereotype defintiion
	protected String getDefaultSuperTypeSignature(IPartBinding type) {
		IAnnotationBinding subtype = type.getSubTypeAnnotationBinding();
		IAnnotationBinding reflectType = getAnnotation(subtype.getType(), "DefaultSuperType");
		if (reflectType != null) {
			return mofTypeSignatureFor((ITypeBinding)reflectType.getValue(), false);
		}
		else if (isEMetadataType(type)) {
			return Type_EMetadataObject;
		}
		else if (isAnnotationType(type)) {
			return Type_Annotation;
		}
		else if (isStereotypeType(type)) {
			return Type_Stereotype;
		}
		return null;
	}

	// Assumes the record is a Stereotype defintiion
	protected String getReflectedTypeSignature(IPartBinding type) {
		IAnnotationBinding subtype = type.getSubTypeAnnotationBinding();
		IAnnotationBinding reflectType = getAnnotation(subtype.getType(), "PartType");
		if (reflectType != null) {
			return (String)reflectType.getValue();
		}
		else if (isEMetadataType(type)) {
			return Type_EMetadataType;
		}
		else if (isAnnotationType(type)) {
			return Type_AnnotationType;
		}
		else if (isStereotypeType(type)) {
			return Type_StereotypeType;
		}
		return null;
	}

	protected boolean isAnnotationType(ITypeBinding type) {
		if (!(type instanceof IPartBinding)) return false;
		IPartBinding edtType = (IPartBinding)type;
		IAnnotationBinding ann = edtType.getSubTypeAnnotationBinding();
		return (ann != null && ann.getName().equalsIgnoreCase("Annotation") && getAnnotation(ann, "Stereotype") == null)
			&& !isEMetadataType(edtType);
	}
	
	protected boolean isStereotypeType(ITypeBinding type) {
		if (!(type instanceof IPartBinding)) return false;
		IPartBinding edtType = (IPartBinding)type;
		IAnnotationBinding ann = edtType.getSubTypeAnnotationBinding();
		return (ann != null && ann.getName().equalsIgnoreCase("Annotation") && getAnnotation(ann, "Stereotype") != null)
			&& !isEMetadataType(edtType);
	}
	
	protected boolean isEMetadataType(ITypeBinding edtType) {
		if (edtType.getName().equalsIgnoreCase("IsEMetadataType")) return true;
		IAnnotationBinding ann = getAnnotation(edtType, "IsEMetadataType");
		return (ann != null && ((java.lang.Boolean)ann.getValue()).equals(java.lang.Boolean.TRUE));
	}

	protected boolean isEMetadataObject(IAnnotationBinding ann) {
		ITypeBinding annType = (ITypeBinding)ann.getType();
		if (annType instanceof AnnotationTypeBindingImpl) {
			annType = ((AnnotationTypeBindingImpl)annType).getAnnotationRecord();
		}
		return isEMetadataType(annType);
	}
	

	protected boolean isMofClass(IPartBinding edtType) {
		IAnnotationBinding ann = edtType.getSubTypeAnnotationBinding();
		return ann != null && ann.getName().equalsIgnoreCase("MofClass");
	}
	
	protected boolean isMofDataType(IPartBinding edtType) {	
		IAnnotationBinding ann = edtType.getSubTypeAnnotationBinding();
		return ann != null && ann.getName().equalsIgnoreCase("MofDataType");
	}
	
	protected boolean isMofBaseType(IPartBinding edtType) {
		IAnnotationBinding ann = edtType.getSubTypeAnnotationBinding();
		return ann != null && ann.getName().equalsIgnoreCase("MofBaseType"); 
	}
		
	protected boolean isMofProxy(IPartBinding edtType) {
		IAnnotationBinding ann = edtType.getSubTypeAnnotationBinding();
		return ann != null && ann.getName().equalsIgnoreCase("MofClass") && getFieldValue(ann, "isProxy") != null;

	}
	
	protected boolean isMofProxy(Part part) {
		Stereotype ann = part.getStereotype();
		return (ann != null && ann.getEClass().getName().equalsIgnoreCase("MofClass") && (java.lang.Boolean)ann.eGet("isProxy") == true);
	}

	protected EClass resolveProxy(Part part) {
		Stereotype ann = part.getStereotype();
		String packageName = (String)ann.eGet("packageName");
		String name = (String)ann.eGet("name");
		if (name == null || name.equals("")) name = part.getName();
		return (EClass)getMofSerializable(packageName + "." + name);
	}
	
	protected void setElementInformation(org.eclipse.edt.compiler.core.ast.Node node, EObject obj) {
		
		if (obj instanceof Element) {
			Element elem = (Element) obj;
			Annotation ann = factory.createDynamicAnnotation(IEGLConstants.EGL_LOCATION);
			ann.setValue(IEGLConstants.EGL_PARTLENGTH, new Integer(node.getLength()));
			ann.setValue(IEGLConstants.EGL_PARTOFFSET, new Integer(node.getOffset()));
			ann.setValue(IEGLConstants.EGL_PARTLINE, new Integer(getLine(node)));
			
			elem.addAnnotation(ann);
		}
		if (obj instanceof Classifier) {
			int packageSegmentCount = ((Classifier)obj).getPackageName().split("\\.").length;
			
			// Use the context's path info so that we don't lose the source file's case. Use the package name
			// to determine where the relative path begins.
			String absPath = context.getAbsolutePath();
			if (!absPath.contains("/")) {
				absPath = absPath.replace("\\", "/");
			}

			String[] absPathSegments = absPath.split("\\/");
			StringBuilder fileName = new StringBuilder(100);
			for (int i = absPathSegments.length - packageSegmentCount - 1; i < absPathSegments.length; i++) {
				fileName.append(absPathSegments[i]);
				if (i + 1 < absPathSegments.length) {
					fileName.append("/");
				}
			}
			
			((Classifier)obj).setFileName(fileName.toString());
		}

	}
	
	protected int getLine(Node node) {
		if (context != null) {
			return context.getLineNumber(node);
		}
		return 0;
	}
	
	public void setUpEglTypedElement(TypedElement obj, IDataBinding edtObj) {
		
		Type type = null;
		EObject mofType = mofTypeFromTypedElement(edtObj);
		if (mofType instanceof Type) {
			type = (Type)mofType;
		}
		if (type != null && type.getTypeSignature().equals(Type_Any)) {
			// TODO This is the default way generic types are referenced in current
			// EGL: by having a convention that a single typed parameter name is 
			// always mapped to type ANY.  This is not generally sufficient but
			// good enough to handle the minimal places EGL uses this today
			Classifier part = (Classifier)getPartBeingProcessed();
			if (part == null) System.out.println("Null processing part: " + obj.toStringHeader());
			else if (!part.getTypeParameters().isEmpty()) {
				type = factory.createGenericType();
				((GenericType)type).setTypeParameter(part.getTypeParameters().get(0));
			}
		}
		obj.setType(type);
		
		if (Binding.isValidBinding(edtObj)) {
			obj.setName(edtObj.getCaseSensitiveName());
		}
		
		// Nullable is an attribute of TypedElement not the type itself
		if (type != null) {
			boolean isNullable = edtObj.getType() instanceof IFunctionBinding 
				? ((IFunctionBinding)edtObj.getType()).getReturnType() != null && ((IFunctionBinding)edtObj.getType()).getReturnType().isNullable()
				: edtObj.getType().isNullable();
			obj.setIsNullable(isNullable);
		}
	}

}
