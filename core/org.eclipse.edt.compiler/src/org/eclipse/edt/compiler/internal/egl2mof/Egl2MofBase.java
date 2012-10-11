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
package org.eclipse.edt.compiler.internal.egl2mof;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.edt.compiler.Context;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.CircularBuildRequestException;
import org.eclipse.edt.compiler.internal.sdk.compile.ASTManager;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathInfo;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
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
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeName;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.lookup.ProxyElement;
import org.eclipse.edt.mof.egl.lookup.ProxyPart;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.eclipse.edt.mof.serialization.ProxyEClass;
import org.eclipse.edt.mof.serialization.ProxyEObject;
import org.eclipse.edt.mof.utils.EList;


@SuppressWarnings("unchecked")
abstract class Egl2MofBase extends AbstractASTVisitor implements MofConversion {
	
	private static Map<String, MofSerializable> processed = new HashMap<String, MofSerializable>();
	protected Stack<MofSerializable> partProcessingStack = new Stack<MofSerializable>();	
	private static String TempEClassMarker = "eze_temp";

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
	
	protected Stack<Object> stack;
	protected Map<Object, EObject> eObjects;
	protected Map<Object, ProxyEObject> proxies;
	
	
	Egl2MofBase(IEnvironment env) {
		this.env = env;
		this.mof = MofFactory.INSTANCE;
		this.factory = IrFactory.INSTANCE;
		stack = new Stack<Object>();
		eObjects = new HashMap<Object, EObject>();
		proxies = new HashMap<Object, ProxyEObject>();
	}
	
	public void setEnvironment(IEnvironment env) {
		this.env = env;
	}

	//************************************************************************************
	// Helper Routines
	//************************************************************************************
	protected EObject eStackPop() {
		return (EObject)stack.pop();
	}
	
	private MofSerializable getPartBeingProcessed() {
		return partProcessingStack.isEmpty()
			? null
			: partProcessingStack.peek();
	}
	
	protected EObject getEObjectFor(Member mbr) {
		if (mbr == null) return null;
		
		EObject result = eObjects.get(mbr);
		//if we cant find a real object, see if we have a proxy already
		if (result == null) {
			result = proxies.get(mbr);
		}
		//If no proxy is found, we need to make one instead
		if (result == null) {
			if (inMofContext) {
				result = new ProxyEObject();
			}
			else {
				result = new ProxyElement();
			}
			proxies.put(mbr, (ProxyEObject)result);
		}
		return result;
	}

	protected EObject getEObjectFor(Object element) {
		if (element == null) return null;
		EObject result = eObjects.get(element);
		//if we cant find a real object, see if we have a proxy already
		if (result == null) {
			result = proxies.get(element);
		}
		//If no proxy is found, we need to make one instead
		if (result == null) {
			if (inMofContext) {
				result = new ProxyEObject();
			}
			else {
				result = new ProxyElement();
			}
			proxies.put(element, (ProxyEObject)result);
		}
		return result;
	}
	
	protected Annotation getAnnotation(Element binding, String typeName) {
		if (binding != null) {
			return binding.getAnnotation(typeName);
		}
		return null;
	}
	
	protected EMetadataObject getEMetadataObject(EModelElement elem, String typeName) {
		if (elem != null) {
			return elem.getMetadata(typeName);
		}
		return null;
	}
	
	protected void createAnnotations(Element binding, EModelElement element) {
		if (binding == null) {
			return;
		}
		for (Annotation annotation : (List<Annotation>)binding.getAnnotations()) {
			element.getMetadataList().add((EMetadataObject)mofValueFrom(annotation));
		}
	}
	
	protected void createAnnotations(Part binding, Element eClass) {
		for (Annotation annotation : binding.getAnnotations()) {
			((Element)eClass).addAnnotation((Annotation)mofValueFrom(annotation));
		}
	}
	
	protected void createAnnotations(Part binding, EClass eClass) {
		Stereotype subtype = binding.getSubType();
		for (Annotation annotation : binding.getAnnotations()) {
			if (subtype == annotation) continue;
			eClass.getMetadataList().add((EMetadataObject)mofValueFrom(annotation));
		}
	}

	
	protected void createAnnotations(Element binding, Element element) {
		if (binding == null) {
			return;
		}
		for (Annotation annotation : binding.getAnnotations()) {
			element.getAnnotations().add(createAnnotation(annotation));
		}
	}

	protected void createAnnotations(Part binding, Part part) {
		Annotation subtype = binding.getSubType();
		boolean bypass = subtype != null && isEGLReflectType(binding);
		for (Annotation annotation : binding.getAnnotations()) {
			if (bypass && subtype == annotation) continue;
			if (inMofContext) {
				((EClass)part).getMetadataList().add((EMetadataObject)mofValueFrom(annotation));
			}
			else {
				part.getAnnotations().add((Annotation)mofValueFrom(annotation));
			}
		}

	}
	private Annotation createAnnotation(Annotation annBinding) {
		EObject ann = mofValueFrom(annBinding);
		if (ann instanceof Annotation) return (Annotation)ann;
		else {
			throw new IllegalArgumentException("Type " + ann.getEClass().getETypeSignature() + " is not an AnnotationType");
		}
	}
	
	private MemberName createMemberName(Element binding) {
		MemberName name = factory.createMemberName();
		if(binding instanceof NamedElement){
			name.setId(((NamedElement)binding).getCaseSensitiveName());
		}
		name.setMember((Member)getEObjectFor(binding));
		return name;
	}
	
	protected Object getValue(Object annotationValue, boolean isMetaDataFieldValue) {

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

		if (annotationValue instanceof Enumeration) {
			return annotationValue;
		}

		if (annotationValue instanceof Part) {
			EObject value = mofTypeFor((Part)annotationValue);
			
			if (value instanceof Part && 
					!annotationValue.equals(value) &&
					!isMetaDataFieldValue) {
				value = createTypeName((Part)value);
			}
			return value;
		}

		if (annotationValue instanceof Annotation) {
			return mofValueFrom((Annotation) annotationValue);
		}

		if (annotationValue instanceof Expression) {
			((Expression) annotationValue).accept(this);
			return (org.eclipse.edt.mof.egl.Expression)stack.pop();
		}

		if (annotationValue instanceof Member) {

			Member valueBinding = (Member) annotationValue;
			Container declarer = valueBinding.getContainer();
			// for values that resolve to a member in a part other than the part being compiled,
			// create a member access with a qualifier that is a PartName
			if (getPartBeingProcessed() != null && 
					declarer instanceof Part && 
					!getPartBeingProcessed().getMofSerializationKey().equalsIgnoreCase(((MofSerializable)declarer).getMofSerializationKey())) {
				PartName nameType = factory.createPartName();
				nameType.setId(((Part)declarer).getCaseSensitiveName());
				nameType.setPackageName(((Part)declarer).getCaseSensitivePackageName());
				MemberAccess access = factory.createMemberAccess();
				access.setId(valueBinding.getCaseSensitiveName());
				access.setQualifier(nameType);
				return access;
			}
			
			return createMemberName(valueBinding);
		}

		if (annotationValue instanceof Type) {
			EObject value = mofTypeFor((Type) annotationValue);
			if (value instanceof Type) {
				value = createTypeName((Type)value);
			}
			return value;
		}

		if (annotationValue instanceof Object[]) {
			Object[] arrVal = (Object[]) annotationValue;
			Object[] value = new Object[arrVal.length];
			for (int i = 0; i < value.length; i++) {
				value[i] = getValue(arrVal[i], isMetaDataFieldValue);
			}
			return value;
		}

		if (annotationValue instanceof EList) {
			EList value = new EList(((EList)annotationValue).size());
			for(Object obj : (EList)annotationValue) {
				value.add(getValue(obj, isMetaDataFieldValue));
			}
			return value;
		}

		return annotationValue;
	}
	
	private MemberName createMemberName(Member mbr) {
		MemberName name = factory.createMemberName();
		name.setId(mbr.getCaseSensitiveName());
		name.setMember((Member)getEObjectFor(mbr));
		return name;
	}
	
	
	private TypeName createTypeName(Type type) {
		if (type instanceof Part) {
			Part part = (Part) type;
			PartName partName = factory.createPartName();
			partName.setId(part.getCaseSensitiveName());
			partName.setPackageName(part.getCaseSensitivePackageName());
			return partName;
		}
		else {
			String key = type.getMofSerializationKey();
			TypeName typeName = factory.createTypeName();
			typeName.setId(key);
			return typeName;
		}
	}

	private Library getLibraryContainer(Type binding) {
		if (binding == null) {
			return null;
		}
		if (binding instanceof Library) {
			return (Library) binding;
		}
		if (binding instanceof Member) {
			Container part = ((Member) binding).getContainer();
			if (part instanceof Library) {
				return (Library) part;
			}
		}
		return null;
	}
	
	private void eAdd(EObject target, String fieldName, Object value) {
		((List)target.eGet(fieldName)).add(value);
	}
	
	private boolean shouldReflect(AnnotationType reflectAnnotationType, String fieldName) {
		if (reflectAnnotationType == null) {
			return false;
		}
		EField field = reflectAnnotationType.getEField(fieldName);
		if (field == null) {
			return false;
		}
		
		return field.getMetadata(IEGLConstants.PROPERTY_NOREFLECT) == null;
	}
	
	private boolean isSpecialNamedElementCase(Object o, EType type) {
		if (o instanceof String && type instanceof EClass) {
			if (isSubClassOf((EClass)type, factory.getNamedElementEClass())) {
				return true;
			}			
		}
		return false;
	}
	
	private List<EField> getFieldsBelowEClass(EClass target) {
		List<EField> fields = new EList<EField>();
		if (target == null || target.getETypeSignature().equalsIgnoreCase(Type_EClass)) {
			return fields;
		}
		fields.addAll(target.getEFields());
		if (!target.getSuperTypes().isEmpty()) {
			fields.addAll(0, getFieldsBelowEClass(target.getSuperTypes().get(0)));
		}
		return fields;
				
	}
	
	protected void setReflectTypeValues(EObject target, AnnotationType reflectTypeBinding) {
		if (reflectTypeBinding == null) return;
		EClass eClass = target.getEClass();
		for (EField field : getFieldsBelowEClass(eClass)) {
			
			EField reflectField = reflectTypeBinding.getEClass().getEField(field.getName());
			if ( reflectField != null && !(reflectField.getCaseSensitiveName().equals("annotations"))) {
				Object obj = mofValueFrom(reflectTypeBinding.eGet(field));
				if (obj != null) {
					if (obj instanceof List)
						for (Object o : (List)obj) {
							// Special handling for reflect values that are just strings but can be converted to instances of the target field's type
							EType type = (EType)((EGenericType)field.getEType()).getETypeArguments().get(0);
							if (o instanceof PartName) {
								o = ((PartName)o).getPart();
							}
							((List)(target.eGet(field))).add(o);
						}
					else {
						EClassifier type = (EClassifier)field.getEType();
						if (obj instanceof PartName) {
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
	}

	protected boolean hasAnnotationField(String name, Annotation ann) {
		if (ann.getEClass() != null) {
			for (EField field : ann.getEClass().getEFields()) {
				if (field.getName().equalsIgnoreCase(name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected void setReflectTypeValues(EObject target, Annotation reflectTypeBinding) {
		if (reflectTypeBinding == null) return;
		EClass eClass = target.getEClass();
		for (EField field : eClass.getAllEFields()) {
			Object obj = mofValueFrom(getFieldValue(reflectTypeBinding, field.getName()));
			if (obj != null) {
				if (shouldReflect((AnnotationType)reflectTypeBinding.getEClass(), field.getName())) {
					if (obj instanceof List)
						for (Object o : (List)obj) {
							// Special handling for reflect values that are just strings but can be converted to instances of the target field's type
							EType type = (EType)((EGenericType)field.getEType()).getETypeArguments().get(0);
							if (isSpecialNamedElementCase(o, type)) {
								o = convertStringValueToNamedElementType((String)o, (EClass)type);
							}
							else if (o instanceof PartName) {
								o = ((PartName)o).getPart();
							}
							((List)(target.eGet(field))).add(o);
						}
					else {
						EClassifier type = (EClassifier)field.getEType();
						if ((obj instanceof String) && type instanceof EClass && isSubClassOf((EClass)type, mof.getENamedElementClass())) {
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
	}
	
	private Object getFieldValue(Annotation annotation, String fieldName) {
		Object result = null;
		if (hasAnnotationField(fieldName, annotation)) {
			Object obj = annotation.getValue(fieldName);
			if (obj instanceof String && ((String) obj).length() == 0) {
				return null;
			}
			result = getValue(obj, false);
		}
		return result;
	}

	
	private EObject convertStringValueToNamedElementType(String obj, EClass eClass ) {
		EObject result = eClass.newInstance();
		result.eSet("name", obj);
		return result;
	}
	
	protected void updateProxyReferences(Object element, EObject obj) {
		ProxyEObject proxy = proxies.get(element);
		if (proxy != null) {
			proxy.updateReferences(obj);
		}
	}
	
	
	private List<Object> mofValueFrom(Object[] array) {
		List<Object> result = new EList<Object>();
		for (Object obj : array) {
			result.add(mofValueFrom(obj));
		}
		return result;
	}
	
	private List<Object> mofValueFrom(List<Object> list) {
		List<Object> result = new EList<Object>();
		for (Object obj : list) {
			result.add(mofValueFrom(obj));
		}
		return result;
	}
	
	protected Object mofValueFrom(EEnumLiteral enumLiteral) {
		Object convertedValue;
		EEnum eenum = (EEnum)enumLiteral.getDeclarer();
		String name = enumLiteral.getCaseSensitiveName();
		if (eenum.getETypeSignature().equals(Type_ElementKind)) {
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
					convertedValue = Enum.valueOf(enumClass, name);
				} catch (ClassNotFoundException e) {
					convertedValue = enumLiteral;
				}
			}
		}
		else {
			Class<Enum> enumClass;
			try {
				enumClass = (Class<Enum>)Class.forName(eenum.getETypeSignature());
				convertedValue = Enum.valueOf(enumClass, name);
			} catch (ClassNotFoundException e) {
				convertedValue = enumLiteral;
			}
		}
		return convertedValue;
	}

	
	protected Object mofValueFrom(Object value) {
		if (value instanceof Annotation) {
			return mofValueFrom((Annotation)value);
		}
		
		if (value instanceof EEnumLiteral) {
			return mofValueFrom((EEnumLiteral)value);
		}
		
		if (value instanceof Member) {
			return getEObjectFor((Member)value);
		}
		
		if (value instanceof Type) {
			return mofTypeFor((Type)value);
		}
		
		if (value instanceof Part) {
			return mofValueFrom((Part)value);
		}
		
		if (value instanceof Object[]) {
			return mofValueFrom((Object[])value);
		}
		
		if (value instanceof List) {
			return mofValueFrom((List<Object>)value);
		}
		
		return value;	
	}
	
	private EObject mofValueFrom(Part part) {
		if (!(part instanceof ProxyPart) && isMofProxy(part)) {
			return resolveProxy(part);
		}
		else
			return part;
	}

	protected EObject mofValueFrom(Annotation binding) {
		
		if (binding == null || binding.getEClass() == null) {
			return binding;
		}
		
		
		EObject value = null;

		if (isEMetadataType((Part)binding.getEClass())) {
			 try {
				EClassifier clzz = MofFactory.INSTANCE.getTypeNamed(binding.getEClass().getETypeSignature());
				if (clzz instanceof EClass) {
					value = ((EClass)clzz).newInstance();
				}
			} catch (Exception e) {
			}
		}
		
		if (value == null) {
			value = binding.getEClass().newInstance();
		}
		
		for(Annotation subAnnotation : binding.getAnnotations()){
			if (value instanceof Annotation) {
				((Annotation)value).getAnnotations().add((Annotation)mofValueFrom(subAnnotation));
			}
			else {
				((EMetadataObject)value).getMetadataList().add((EMetadataObject)mofValueFrom(subAnnotation));
			}
		}
		
		boolean isEMetadataObject = !(value instanceof Annotation);  //if it is not an annotation, it must be a metaDataObject
		for(EField field : binding.getEClass().getEFields()) {
			
			EField valueField = null;
			if (value.getEClass() != null) {
				valueField = value.getEClass().getEField(field.getCaseSensitiveName());
			}
			
			if (valueField != null) {
				Object oldVale = binding.eGet(field);
				Object fieldValue = mofValueFrom(getValue(oldVale, isEMetadataObject));
				value.eSet(valueField, fieldValue);
			}
		}
		return value;
	}

	protected EClass mofMemberTypeFor(Element binding) {
		EClass mbrType;
		if (binding instanceof Function) {
			Annotation opAnn = binding.getAnnotation("egl.lang.reflect.mof.Operation");
			if (opAnn == null) {
				mbrType = (EClass)getMofSerializable(Type_Function);
			}
			else {
				mbrType = (EClass)getMofSerializable(Type_Operation);
			}

		}
		else if (binding instanceof Constructor) {
			mbrType = (EClass)getMofSerializable(Type_Constructor);
		}
		else if (binding instanceof ConstantField) {
			mbrType = (EClass)getMofSerializable(Type_ConstantField);
		}
		else if (binding instanceof Field) {
			mbrType = (EClass)getMofSerializable(Type_Field);
		}
		else {
			mbrType = (EClass)getMofSerializable(Type_Field);
		}
		return mbrType;
	}
	
	protected EObject mofTypeFromTypedElement(Element element) {
		Type type = null;
		if (element instanceof Function) { 
			type = ((Function)element).getType();
		} 
		else if (element instanceof Constructor) {
			type = (Part)((Constructor)element).getContainer();
		}
		else if (element instanceof TypedElement) {
			type = ((TypedElement)element).getType();
		}
		else if (element instanceof Type) {
				type = (Type)element; 
		}
		if (type != null)
			return mofTypeFor(type);
		else
			return null;
	}
		
	protected EObject mofTypeFor(Type type) {
		EObject eType = null;
		if (type instanceof ArrayType) {
			Type elementType = ((ArrayType)type).getElementType();
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
				generic.setElementsNullable(((ArrayType)type).elementsNullable());
				eType = generic;
			}
		}
		else {
			if(type != null){
				String key = mofTypeSignatureFor(type); 
				eType = getMofSerializable(key);

				if (eType == null && ! key.startsWith(EGL_KeyScheme) && type instanceof Annotation) {
					key = EGL_KeyScheme + key;
					eType = getMofSerializable(key);
				}

				if (eType == null) {
					if (type instanceof Part) {
						if (inMofContext && isMofClass((Part)type) && type instanceof org.eclipse.edt.mof.egl.ExternalType) {
							eType = createTempEClass((org.eclipse.edt.mof.egl.ExternalType)type);
						}
						else {
							eType = createProxyPart(((Part)type).getFullyQualifiedName());
						}
					}
				}
			}
		}
		
		return eType;
		
	}

	private String getMofDataTypeSignatureFor(Type type) {
		if (type == null) {
			return null;
		}
		Classifier classifier = type.getClassifier();
		if (classifier == null) {
			return null;
		}
		
		//handle any, string, boolean, int, float, decimal
		if (classifier.equals(TypeUtils.Type_ANY)) {
			return MofConversion.Type_JavaObject;
		}
		if (classifier.equals(TypeUtils.Type_BOOLEAN)) {
			return MofConversion.Type_EBoolean;
		}
		if (classifier.equals(TypeUtils.Type_STRING)) {
			return MofConversion.Type_EString;
		}
		if (classifier.equals(TypeUtils.Type_INT) || type.equals(TypeUtils.Type_SMALLINT) || type.equals(TypeUtils.Type_BIGINT)) {
			return MofConversion.Type_EInteger;
		}
		if (classifier.equals(TypeUtils.Type_FLOAT) || type.equals(TypeUtils.Type_SMALLFLOAT)) {
			return MofConversion.Type_EFloat;
		}
		if (classifier.equals(TypeUtils.Type_DECIMAL)) {
			return MofConversion.Type_EDecimal;
		}
		
		return null;
		
	}
	protected String mofTypeSignatureFor(Type type) {
		String typeSignature;
		
		//handle mof dataTypes
		if (inMofContext) {
			typeSignature = getMofDataTypeSignatureFor(type);
			if (typeSignature != null) {
				return typeSignature;
			}
		}
		
		if (type instanceof AnnotationType) {
			if ((inMofContext &&  !inAnnotationTypeContext) || isEMetadataType(type) ) {
				typeSignature = ((AnnotationType)type).getETypeSignature();
			}
			else {
				typeSignature = type.getMofSerializationKey();
			}
		}
		else if (type instanceof Part) {
			
			if (inMofProxyContext) {
				typeSignature = ((Part)type).getFullyQualifiedName();
			}
			
			else if (isMofProxy((Part)type)) { 
				Annotation ann = ((Part)type).getSubType();
				typeSignature = (String)getFieldValue(ann, "packageName");
				if (typeSignature == null) {
					typeSignature = ((Part)type).getCaseSensitivePackageName();
				}
				typeSignature += ".";
				String name = (String)getFieldValue(ann, "name");
				if (name == null || "".equals(name)) name = ((Part)type).getCaseSensitiveName();
				typeSignature += name;
			}
			else if (isReflectType((Part)type)) {
				Annotation ann = ((Part)type).getSubType();
				String name = (String)getFieldValue(ann, "name");
				if (name == null) name = ((Part)type).getCaseSensitiveName();
				typeSignature = ((Part)type).getCaseSensitivePackageName() + "." + name;
				if (!isMofReflectType((Part)type) && isEGLReflectType((Part)type)) {
					typeSignature = EGL_KeyScheme + typeSignature; 
				}
			}
			else	
				typeSignature = type.getMofSerializationKey();
		}
		else {
			if (inMofContext) {
				typeSignature = Type_EObject;
			}
			else {
				typeSignature = type.getMofSerializationKey();
			}
		}
		return typeSignature;
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
	

	private EObject newEObject(String typeSignature) {
		EClass eClass = (EClass)getMofSerializable(typeSignature);
		return eClass.newInstance();
	}
		
	private EClass createTempEClass(org.eclipse.edt.mof.egl.ExternalType part) {
		EClass eClass = mof.createEClass(true);
		eClass.setName(part.getCaseSensitiveName());
		eClass.setPackageName(part.getCaseSensitivePackageName());
		String key = part.getMofSerializationKey();
		env.save(key, eClass, false);
		for (Field binding : part.getFields()) {
			EField field = mof.createEField(true);
			field.setName(binding.getCaseSensitiveName());
			EType type = null;
			try {
				type = (EType)mofTypeFor(binding.getType());
				field.setEType(type);
				field.setIsNullable(binding.isNullable());
			} catch (CircularBuildRequestException e) {
				// If this occurs just assume JavaObject type
				type = MofFactory.INSTANCE.getEObjectClass();
			}
			eClass.addMember(field);
			field.setDeclarer(eClass);
		}
		Annotation subtype = part.getSubType();
		setReflectTypeValues(eClass, subtype);
		eClass.getMetadataList().add(getTempClassMarker());
		List<EClass> superTypes = new ArrayList<EClass>();
		for (StructPart superType : part.getSuperTypes()) {
			superTypes.add((EClass)mofTypeFor(superType));
		}
		eClass.addSuperTypes(superTypes);
		return eClass;
	}
	
	private boolean isTempEClass(EObject eclass) {
		return eclass instanceof EClass
			? ((EClass)eclass).getMetadata(TempEClassMarker) != null
			: false;
	}
	
	private EMetadataObject getTempClassMarker() {
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

	private ProxyEObject createProxyPart(String name) {
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
 	
	private String concatWithSeparator(String[] fragments, String separator) {
		StringBuffer result = new StringBuffer();
		for (int i=0; i<fragments.length; i++) {
			result.append(fragments[i]);
			if (i < fragments.length-1) {
				result.append(separator);
			}
		}
		return result.toString();
	}
	
	private void eSet(EObject target, String fieldName, Object value) {
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
	
	private void eAdd(List list, Object value) {
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
	
	private boolean isAbstractFunction(Function function) {
		return function != null && function.getContainer() instanceof ExternalType;
	}
	
	// A ReflectType is an EGL type stereotyped by a Stereotype
	// marked as one that represents a type in the underlying meta model.
	// In this way such types can be represented in EGL but are then
	// converted into instances of the Reflect type the stereotype
	// represents.  For intstance, Record parts stereotyped by Annotation are then
	// converted into instances of the reflected type "org.eclipse.edt.mof.egl.AnnotationType"
	// as the stereotype "egl.lang.Annotation" reflects the underlying model.
	private boolean isReflectType(Type typeBinding) {
		if (!(typeBinding instanceof Part)) return false;
		boolean isReflectType = typeBinding instanceof AnnotationType || isEMetadataType((Part)typeBinding);
		if (!isReflectType) {
			isReflectType = getAnnotation(typeBinding, "egl.lang.reflect.PartType") != null;
			if (!isReflectType ) {
				Part part = (Part) typeBinding;
				if (part.getSubType() != null) {
					isReflectType = 
							(getAnnotation((StereotypeType)part.getSubType().getEClass(), "egl.lang.reflect.PartType") != null) ||
							(getEMetadataObject((StereotypeType)part.getSubType().getEClass(), "PartType") != null);
				}
			}
		}
		return isReflectType;
	}
	
	/**
	 * Return the default super type as defined by the @DefaultSuperType annotation
	 * @param typeBinding
	 * @return
	 */
	private MofSerializable getDefaultSuperType(Type typeBinding) {	
		if (!(typeBinding instanceof Part)) return null;
		Annotation subtype = ((Part)typeBinding).getSubType();
		String superTypeName = null;
		if (subtype != null) {
			superTypeName = getDefaultSuperTypeSignature((Part)typeBinding);
		}
		return superTypeName != null ? (MofSerializable)getMofSerializable(superTypeName) : null;
	}
	/**
	 * Assumes that the typeBinding parameter is a reflected type as defined by isReflectType(ITypeBinding)
	 * @param typeBinding
	 * @return
	 */
	private EClass getReflectedType(Part typeBinding) {	
		if (typeBinding == null) return null;
		Annotation subtype = typeBinding.getSubType();
		String reflectedTypeName = null;
		if (subtype != null) {
			reflectedTypeName = getReflectedTypeSignature(typeBinding);
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
	protected boolean isMofReflectType(Part typeBinding) {
		EClass reflectedType = getReflectedType(typeBinding);
		if (reflectedType != null) {
			return isSubClassOf(reflectedType, MofFactory.INSTANCE.getEModelElementClass());
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
	private boolean isEGLReflectType(Part typeBinding) {
		EClass reflectedType = getReflectedType(typeBinding);
		if (reflectedType != null) {
			return isSubClassOf(reflectedType, IrFactory.INSTANCE.getElementEClass());
		}
		// TODO: Annotation and Stereotype themselves are not represented as ReflectTypes
		// because their definitions are still part of the old IR model in the
		// system parts eglar
		return false;
	}

	
	// Assumes the record is a Stereotype defintiion
	private String getDefaultSuperTypeSignature(Part type) {
		Annotation subtype = type.getSubType();
		Annotation reflectType = getAnnotation(subtype, "egl.lang.reflect.DefaultSuperType");
		if (reflectType != null) {
			return ((Type)reflectType.getValue()).getMofSerializationKey();
		}
		else if (isEMetadataType(type)) {
			return Type_EMetadataObject;
		}
		else if (type instanceof AnnotationType) {
			return Type_Annotation;
		}
		else if (type instanceof StereotypeType) {
			return Type_Stereotype;
		}
		return null;
	}

	// Assumes the record is a Stereotype defintiion
	private String getReflectedTypeSignature(Part type) {
		Annotation subtype = type.getSubType();
		if (subtype != null) {
			Annotation ann = getAnnotation((AnnotationType)subtype.getEClass(), "egl.lang.reflect.PartType");
			if (ann != null) {
				return (String)ann.getValue();
			}
			EMetadataObject meta = getEMetadataObject((AnnotationType)subtype.getEClass(), "PartType");
			if (meta != null) {
				return (String) meta.eGet("value");
			}
		}
		
		if (isEMetadataType(type)) {
			return Type_EMetadataType;
		}
		else if (type instanceof StereotypeType) {
			return Type_StereotypeType;
		}
		else if (type instanceof AnnotationType) {
			return Type_AnnotationType;
		}
		return null;
	}
	
	protected boolean isEMetadataType(Type edtType) {
		if("IsEMetadataType".equalsIgnoreCase(BindingUtil.getName(edtType))) return true;
		Annotation ann = getAnnotation(edtType, "egl.lang.reflect.IsEMetadataType");
		return (ann != null && ((java.lang.Boolean)ann.getValue()).equals(java.lang.Boolean.TRUE));
	}

	protected boolean isEMetadataObject(Annotation ann) {
		return isEMetadataType((AnnotationType)ann.getEClass());
	}
	

	private boolean isMofClass(Part edtType) {
		Stereotype ann = edtType.getStereotype();
		return (ann != null && ann.getEClass().getName().equalsIgnoreCase("MofClass"));
	}
	
	private boolean isMofDataType(Part edtType) {	
		Annotation ann = edtType.getSubType();
		return ann != null && ann.getEClass().getName().equalsIgnoreCase("MofDataType");
	}
	
	private boolean isMofBaseType(Part edtType) {
		Annotation ann = edtType.getSubType();
		return ann != null && ann.getEClass().getName().equalsIgnoreCase("MofBaseType"); 
	}
		
	protected boolean isMofProxy(Part part) {
		Stereotype ann = part.getStereotype();
		return (ann != null && ann.getEClass().getName().equalsIgnoreCase("MofClass") && (java.lang.Boolean)ann.eGet("isProxy") == true);
	}

	private EClass resolveProxy(Part part) {
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
			int packageSegmentCount = ((Classifier)obj).getCaseSensitivePackageName().split("\\.").length;
			
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
	
	private int getLine(Node node) {
		if (context != null) {
			return context.getLineNumber(node);
		}
		return 0;
	}
	
	protected void setUpEglTypedElement(TypedElement obj, Member edtObj) {
		
		Type type = null;
		EObject mofType = mofTypeFor(edtObj.getType());
		if (mofType instanceof Type) {
			type = (Type)mofType;
		}
	
		if (getPartBeingProcessed() == null) {
			System.out.println("Null processing part: " + obj.toStringHeader());
		}
		else {
			type = handleGenericType(type);
		}
		
		obj.setType(type);
		
		obj.setName(edtObj.getCaseSensitiveName());
		
		obj.setIsNullable(edtObj.isNullable());
	}
	
	protected String mofPartTypeSignatureFor(org.eclipse.edt.compiler.core.ast.Part node) {
		Part part = (Part)node.getName().resolveType();
		return mofPartTypeSignatureFor(part);
	}
	
	protected String mofPartTypeSignatureFor(Part part) {
		String typeSignature;
		if (isReflectType(part)) {
			return getReflectedTypeSignature(part);
		}
		else {
			
			if (inMofContext) {
				if (part instanceof Enumeration) {
					return Type_EEnum;
				}
				 
				if (part instanceof org.eclipse.edt.mof.egl.ExternalType) {
					return Type_EClass;
				}
			}
		}
		return part.getEClass().getETypeSignature();

	}

	
	private boolean isSubClassOf(EClass child, EClass parent) {
		return BindingUtil.isSubClassOf(child, parent);
	}

	private Type handleGenericType(Type type) {
		if (type != null && type.getTypeSignature().equals(Type_Any)) {
			// TODO This is the default way generic types are referenced in current
			// EGL: by having a convention that a single typed parameter name is 
			// always mapped to type ANY.  This is not generally sufficient but
			// good enough to handle the minimal places EGL uses this today
			Classifier part = (Classifier)getPartBeingProcessed();
			if (!part.getTypeParameters().isEmpty()) {
				type = factory.createGenericType();
				((GenericType)type).setTypeParameter(part.getTypeParameters().get(0));
				return type;
			}
		}
		
		//Handle the case of Any[]
		if (type instanceof ArrayType) {
			ArrayType arrType = (ArrayType)type;
			Type newType = handleGenericType(arrType.getElementType());
			if (newType != null && newType != arrType.getElementType()) {
				arrType.getTypeArguments().remove(0);
				arrType.getTypeArguments().add(0, newType);
			}
			return arrType;
		}
		
		return type;
	}

	
}
