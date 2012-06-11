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
package org.eclipse.edt.compiler.internal.mof2binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.edt.compiler.binding.AnnotationBinding;
import org.eclipse.edt.compiler.binding.AnnotationBindingForElement;
import org.eclipse.edt.compiler.binding.AnnotationFieldBinding;
import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ArrayDictionaryBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DataBinding;
import org.eclipse.edt.compiler.binding.DictionaryBinding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.FixedStructureBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationDataBinding;
import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeManager;
import org.eclipse.edt.compiler.binding.annotationType.StereotypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.IBindingEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.EMetadataType;
import org.eclipse.edt.mof.EModelElement;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.DataType;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ElementAnnotations;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.FormField;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.InvalidName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PatternType;
import org.eclipse.edt.mof.egl.SequenceType;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.egl.TopLevelFunctionName;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeName;
import org.eclipse.edt.mof.egl.lookup.ProxyPart;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.impl.AbstractVisitor;
import org.eclipse.edt.mof.impl.DynamicEObject;
import org.eclipse.edt.mof.serialization.ProxyEObject;


public abstract class Mof2BindingBase extends AbstractVisitor implements MofConversion {
	public static HashSet<String> ignoreTheseAnnotations = new HashSet<String>();
	{
		ignoreTheseAnnotations.add(IEGLConstants.EGL_LOCATION);
		ignoreTheseAnnotations.add(IEGLConstants.EGL_PARTABSOLUTEPATH);
	}
	
	private static String[] MOFPACKAGE = InternUtil.intern(new String[] {"egl", "lang", "reflect", "mof"});
	private static HashMap<String, String> MOF_STEREOTYPES = new HashMap<String, String>();
	{
		MOF_STEREOTYPES.put(InternUtil.intern("MofClass"), "org.eclipse.edt.mof.EClass");
		MOF_STEREOTYPES.put(InternUtil.intern("MofDataType"), "org.eclipse.edt.mof.EDataType");
		MOF_STEREOTYPES.put(InternUtil.intern("MofEnum"), "org.eclipse.edt.mof.EEnum");
	}
	
	private static Object[] EMPTYARRAY =  new Object[0];
	
	Stack<IBinding> stack = new Stack<IBinding>();
	Map<EObject, IBinding> elementToBindingMap = new HashMap<EObject, IBinding>();
	Map<String, IPartBinding> partBindings = new HashMap<String, IPartBinding>();
	Map<IPartBinding, ProxyEObject> bindingToProxyMap = new HashMap<IPartBinding, ProxyEObject>();
	IBindingEnvironment env;
	int elementAnnotationIndex = 0;
	
	public Mof2BindingBase(IBindingEnvironment env) {
		this.env = env;
	}

	public boolean visit(Object obj) {
		stack.push(null);
		return false;
	}
	
	protected IPartBinding createProxyBinding(ProxyPart proxy) {
		IPartBinding binding = (IPartBinding)getBinding(proxy);
		if (binding == null) {
			
			String name = proxy.getTypeSignature();
			
			String partName;
			String[] pkgName;
			int index = name.lastIndexOf(".");
			if (index < 0) {
				partName = name;
				pkgName = new String[0];
			}
			else {
				partName = name.substring(index + 1);
				pkgName = name.substring(0, index).split(".");
			}
			name = InternUtil.intern(partName);
			pkgName = InternUtil.intern(pkgName);
			binding = new EClassBinding(pkgName, partName);
			putProxy(binding, proxy);
			putBinding(proxy, binding);
		}
		return binding;
	}
	
	public boolean visit(GenericType type) {
		ITypeBinding elementType = null;
		// TODO Assume only ArrayType covered here
		if (type.getTypeArguments().isEmpty()) {
			elementType = PrimitiveTypeBinding.getInstance(Primitive.ANY);
		}
		else if (type.getTypeArguments().get(0) instanceof ProxyPart) {
			elementType = createProxyBinding((ProxyPart)type.getTypeArguments().get(0));
		}
		else {
			type.getTypeArguments().get(0).accept(this);
			elementType = (ITypeBinding)stack.pop();
		}
		stack.push(ArrayTypeBinding.getInstance(elementType));
		return false;
	}
	
	public boolean visit(SequenceType type) {
		ParameterizableType prim = type.getParameterizableType();
		PrimitiveTypeBinding binding = null;

		int len = 0;
		if (type.getLength() != null) {
			len = type.getLength();
		}
		
		Primitive primitive = getPrimitive(prim);
		if (primitive != null) {
			binding = PrimitiveTypeBinding.getInstance(primitive, len);
		}
		
		stack.push(binding);
		return false;
	}
	
	public boolean handleParameterizableType(ParameterizableType prim) {
		PrimitiveTypeBinding binding = null;
		Primitive primitive = getPrimitive(prim);
		if (primitive != null) {
			binding = PrimitiveTypeBinding.getInstance(primitive);
		}
		
		stack.push(binding);
		return false;
	}
	
	private Primitive getPrimitive(ParameterizableType prim) {
		
		String str = prim.getMofSerializationKey();
		if (str.equalsIgnoreCase(Type_EGLDecimal)) {
			return Primitive.DECIMAL;
		}
		else if (str.equalsIgnoreCase(Type_EGLBin)) {
			return Primitive.BIN;
		}
		else if (str.equalsIgnoreCase(Type_EGLNum)) {
			return Primitive.NUM;
		}
		else if (str.equalsIgnoreCase(Type_EGLNumc)) {
			return Primitive.NUMC;
		}
		else if (str.equalsIgnoreCase(Type_EGLPacf)) {
			return Primitive.PACF;
		}
		else if (str.equalsIgnoreCase(Type_EGLMonthInterval)) {
			return Primitive.MONTHSPAN_INTERVAL;
		}
		else if (str.equalsIgnoreCase(Type_EGLSecondsInterval)) {
			return Primitive.SECONDSPAN_INTERVAL;
		}
		else if (str.equalsIgnoreCase(Type_EGLInterval)) {
			return Primitive.SECONDSPAN_INTERVAL;
		}

		else if (str.equalsIgnoreCase(Type_EGLTimestamp)) {
			return Primitive.TIMESTAMP;
		}
		else if (str.equalsIgnoreCase(Type_EGLChar)) {
			return Primitive.CHAR;
		}
		else if (str.equalsIgnoreCase(Type_EGLDBChar)) {
			return Primitive.DBCHAR;
		}
		else if (str.equalsIgnoreCase(Type_EGLMBChar)) {
			return Primitive.MBCHAR;
		}
		else if (str.equalsIgnoreCase(Type_EGLHex)) {
			return Primitive.HEX;
		}
		else if (str.equalsIgnoreCase(Type_EGLUnicode)) {
			return Primitive.UNICODE;
		}
		else if (str.equalsIgnoreCase(Type_EGLString)) {
			return Primitive.STRING;
		}
		else if (str.equalsIgnoreCase(Type_EGLBytes)) {
			return Primitive.BYTES;
		}
		return null;
	}
	
	
	public boolean visit(FixedPrecisionType type) {
		ParameterizableType prim = type.getParameterizableType();
		PrimitiveTypeBinding binding = null;
		Primitive primitive = getPrimitive(prim);
		if (primitive != null) {
			binding = PrimitiveTypeBinding.getInstance(primitive, type.getLength(), type.getDecimals());
		}

		stack.push(binding);
		return false;
	}
	
	public boolean visit(PatternType type) {
		ParameterizableType prim = type.getParameterizableType();
		PrimitiveTypeBinding binding = null;
		Primitive primitive = getPrimitive(prim);
		if (primitive != null) {
			binding = PrimitiveTypeBinding.getInstance(primitive, type.getPattern());
		}
		
		stack.push(binding);
		return false;
	}
	
	// Will only happen for primitive types that have no type arguments
	public boolean visit(StructPart type) {
		org.eclipse.edt.compiler.core.ast.Primitive astPrimitive = null;
		ITypeBinding binding = null;
		String key = type.getMofSerializationKey();
		if (key.equalsIgnoreCase(Type_EGLAny)) {
			astPrimitive = Primitive.ANY;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive);
		}
		else if (key.equalsIgnoreCase(Type_EGLBigint)) {
			astPrimitive = Primitive.BIGINT;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive);
		}
		else if (key.equalsIgnoreCase(Type_EGLBin)) {
			astPrimitive = Primitive.BIN;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive, ((FixedPrecisionType)type).getLength(), ((FixedPrecisionType)type).getDecimals());
		}
		else if (key.equalsIgnoreCase(Type_EGLBlob)) {
			astPrimitive = Primitive.BLOB;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive);
		}
		else if (key.equalsIgnoreCase(Type_EGLBoolean)) {
			astPrimitive = Primitive.BOOLEAN;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive);
		}
		else if (key.equalsIgnoreCase(Type_EGLClob)) {
			astPrimitive = Primitive.CLOB;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive);
		}
		else if (key.equalsIgnoreCase(Type_EGLDate)) {
			astPrimitive = Primitive.DATE;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive);
		}
		else if (key.equalsIgnoreCase(Type_EGLFloat)) {
			astPrimitive = Primitive.FLOAT;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive);
		}
		else if (key.equalsIgnoreCase(Type_EGLInt)) {
			astPrimitive = Primitive.INT;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive);
		}
		else if (key.equalsIgnoreCase(Type_EGLNumber)) {
			astPrimitive = Primitive.NUMBER;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive);
		}
		else if (key.equalsIgnoreCase(Type_EGLSmallint)) {
			astPrimitive = Primitive.SMALLINT;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive);
		}
		else if (key.equalsIgnoreCase(Type_EGLSmallfloat)) {
			astPrimitive = Primitive.SMALLFLOAT;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive);
		}
		else if (key.equalsIgnoreCase(Type_EGLMonthInterval)) {
			astPrimitive = Primitive.MONTHSPAN_INTERVAL;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive, ((PatternType)type).getPattern());
		}
		else if (key.equalsIgnoreCase(Type_EGLTime)) {
			astPrimitive = Primitive.TIME;
			binding = PrimitiveTypeBinding.getInstance(astPrimitive);
		}
		else if (key.equalsIgnoreCase(Type_EGLDictionary)) {
			binding = DictionaryBinding.INSTANCE;
		}
		else if (key.equalsIgnoreCase(Type_EGLArrayDictionary)) {
			binding = ArrayDictionaryBinding.INSTANCE;
		}

		stack.push(binding);
		return false;
	}
	
	public boolean visit(DataType type) {

		return false;
	}
	
	
	public boolean visit(EMetadataObject metadata) {
		IAnnotationBinding binding = createAnnotationBinding(metadata);
		stack.push(binding);
		return false;
	}
	
	public boolean visit(Annotation ann) {
		IAnnotationBinding binding = createAnnotationBinding(ann);
		stack.push(binding);
		return false;
	}
	
	abstract IPartBinding getPartBinding();
	
	public IBinding getBinding(EObject ir) {
		IBinding binding = elementToBindingMap.get(ir);
		if (binding != null || !(ir instanceof Part)) {
			return binding;
		}
		
		Part part = (Part) ir;
		
		String[] packageName = InternUtil.intern(part.getPackageName().split("[.]"));
		String name = InternUtil.intern(part.getName());
		binding = env.getCachedPartBinding(packageName, name);
		
		if (Binding.isValidBinding(binding)) {
			return binding;
		}
		
		return null;
	}
	
	public void putBinding(EObject ir,  IBinding binding) {
		elementToBindingMap.put(ir, binding);
		if (binding instanceof IPartBinding) {
			((IPartBinding)binding).setEnvironment(env);
			env.addPartBindingToCache((IPartBinding)binding);
		}
	}
	
	public IPartBinding getPartBinding(String key) {
		return partBindings.get(key);
	}
	
	public void putPartBinding(String key,  IPartBinding binding) {
		partBindings.put(key, binding);
		if (binding instanceof IPartBinding) {
			((IPartBinding)binding).setEnvironment(env);
			env.addPartBindingToCache((IPartBinding)binding);
		}
	}

	
	public ProxyEObject getProxy(IPartBinding binding) {
		return bindingToProxyMap.get(binding);
	}
	
	public void putProxy(IPartBinding binding, ProxyEObject value) {
		bindingToProxyMap.put(binding, value);
	}
	
	public void removeProxy(IPartBinding binding) {
		bindingToProxyMap.remove(binding);
	}
	
	public void handleAnnotations(Element ir, IBinding binding) {
		for (Annotation ann : ir.getAnnotations()) {
			if (!shouldIgnore(ann)) {
				ann.accept(this);
				binding.addAnnotation((IAnnotationBinding)stack.pop());
			}
		}
	}
	
	public void handleElementAnnotations(FormField ir, FormFieldBinding binding) {
		for (ElementAnnotations elemAnns : ir.getElementAnnotations()) {
			elementAnnotationIndex = elemAnns.getIndex();
			for (Annotation ann : elemAnns.getAnnotations()) {
				ann.accept(this);
				binding.addAnnotation((IAnnotationBinding)stack.pop());
			}
			elementAnnotationIndex = 0;
		}
	}

	public void handleElementAnnotations(StructuredField ir, StructureItemBinding binding) {
		for (ElementAnnotations elemAnns : ir.getElementAnnotations()) {
			elementAnnotationIndex = elemAnns.getIndex();
			for (Annotation ann : elemAnns.getAnnotations()) {
				ann.accept(this);
				binding.addAnnotation((IAnnotationBinding)stack.pop());
			}
			elementAnnotationIndex = 0;
		}
	}

	public void handleMetadata(EModelElement ir, IBinding binding) {
		for (EMetadataObject ann : ir.getMetadataList()) {
			ann.accept(this);
			binding.addAnnotation((IAnnotationBinding)stack.pop());
		}
	}

	private IBinding findData(IPartBinding partBinding, String id) {
		IDataBinding dataBinding = partBinding.findData(id);

		if (Binding.isValidBinding(dataBinding) && dataBinding.getKind() == DataBinding.NESTED_FUNCTION_BINDING) {
			return dataBinding.getType();
		}
		
		if (!Binding.isValidBinding(dataBinding) && partBinding instanceof FixedStructureBinding) {
			dataBinding = (IDataBinding) ((FixedStructureBinding) partBinding).getSimpleNamesToDataBindingsMap().get(id);
		}
		return dataBinding;
	}
	
	private boolean isStringList(List list) {
		for (Object val : list) {
			if (!(val instanceof String)) {
				return false;
			}
		}
		return true;
	}

	private boolean isIntegerList(List list) {
		for (Object val : list) {
			if (!(val instanceof Integer)) {
				return false;
			}
		}
		return true;
	}

	private boolean isStringArrayList(List list) {
		for (Object val : list) {
			if (!(val instanceof String[])) {
				return false;
			}
		}
		return true;
	}

	private boolean isIntegerArrayList(List list) {
		for (Object val : list) {
			if (!(val instanceof Integer[])) {
				return false;
			}
		}
		return true;
	}

	private Object getListValue(List list) {
		if (list.isEmpty()) {
			return EMPTYARRAY;
		}
		
		List convertedValues = new ArrayList();
		for (int i=0; i<list.size(); i++) {
			convertedValues.add(getValue(list.get(i)));
		}
		

		if (convertedValues.get(0) instanceof IAnnotationBinding) {			
			return (IAnnotationBinding[]) convertedValues.toArray(new IAnnotationBinding[convertedValues.size()]);
		}
		
		if (isStringList(convertedValues)) {
			return (String[]) convertedValues.toArray(new String[convertedValues.size()]);
		}
		
		if (isStringArrayList(convertedValues)) {
			return (String[][]) convertedValues.toArray(new String[convertedValues.size()][]);
		}
		

		if (isIntegerArrayList(convertedValues)) {
			return (Integer[][]) convertedValues.toArray(new Integer[convertedValues.size()][]);
		}

		if (isIntegerList(convertedValues)) {
			return (Integer[]) convertedValues.toArray(new Integer[convertedValues.size()]);
		}
		
		return (Object[])convertedValues.toArray(new Object[convertedValues.size()]);
				
	}
	
	
	protected Object getValue(Object value) {
	
		if (value instanceof InvalidName && "EZENOTFOUND".equalsIgnoreCase(((InvalidName)value).getId())) {
			return IBinding.NOT_FOUND_BINDING;
		}
		
		if (value instanceof java.lang.Boolean) {
			if (((java.lang.Boolean) value).booleanValue()) {
				return Boolean.YES;
			} else {
				return Boolean.NO;
			}
		}
		
		if (value instanceof Integer[] || value instanceof Integer[][] || value instanceof int[] || value instanceof int[][] || value instanceof String[] || value instanceof String[][] || value instanceof SystemEnumerationDataBinding || value instanceof SystemEnumerationDataBinding[]) {
			return value;
		}
	
	
//		if (value instanceof MemberAccess) {
//			MemberAccess fa = (MemberAccess) value;
//			fa.getQualifier().accept(this);
//			ITypeBinding typeBinding = (ITypeBinding)stack.pop();
//			if (fa.isAnnotationEnumAccess()) {
//				return new EnumerationDataBinding(InternUtil.internCaseSensitive(fa.getId()), getPartBinding(), typeBinding, 0);
//			}
//			//Check for a library function
//			if (Binding.isValidBinding(typeBinding) && typeBinding.getKind() == ITypeBinding.LIBRARY_BINDING) {
//				return new ClassFieldBinding(InternUtil.internCaseSensitive(fa.getId()), (LibraryBinding)typeBinding, IBinding.NOT_FOUND_BINDING);
//			}
//		}
		
		if (value instanceof List) {
			return getListValue((List) value);
		}
	
		if (value instanceof TypeName) {
			((TypeName) value).getType().accept(this);
			return stack.pop();
		}
		
		if (value instanceof Type) {
			((Type) value).accept(this);
			return stack.pop();
		}
		
		if (value instanceof EType) {
			((EType)value).accept(this);
			return stack.pop();
		}
	
		if (value instanceof TopLevelFunctionName) {
			TopLevelFunctionName tlfName = (TopLevelFunctionName) value;
			String[] pkgName = tlfName.getPackageName().split("[.]");
			return new TopLevelFunctionBinding(InternUtil.intern(pkgName), tlfName.getId());
		}
	
		if (value instanceof Name) {
			String name = InternUtil.intern(((Name) value).getId());
			return findData(getPartBinding(), name);
		}
	
		if (value instanceof Expression) {
			return value.toString();
		}
		
		if (value instanceof Annotation) {
			return createAnnotationBinding((Annotation)value);
		}
	
		if (value instanceof Annotation[]) {
			Annotation[] annotations = (Annotation[]) value;
			IAnnotationBinding[] annotationBindings = new IAnnotationBinding[annotations.length];
			for (int i = 0; i < annotationBindings.length; i++) {
				annotationBindings[i] = createAnnotationBinding(annotations[i]);
			}
			return annotationBindings;
		}
	
		if (value instanceof Object[]) {
			Object[] arrVal = (Object[]) value;
			if (arrVal.length > 0) {
				Object[] retValue = new Object[arrVal.length];
				for (int i = 0; i < arrVal.length; i++) {
					retValue[i] = getValue(arrVal[i]);
				}
				return retValue;
			}
			else {
				return EMPTYARRAY;
			}
		}
	
		return value;
	}
	
	protected IAnnotationBinding createAnnotationBinding(EMetadataObject annotation) {
		annotation.getEClass().accept(this);
		IPartBinding declarer = getPartBinding();
		IAnnotationTypeBinding type = new AnnotationTypeBindingImpl((FlexibleRecordBinding) stack.pop(), declarer);
		IAnnotationBinding binding = new AnnotationBinding(type.getName(), declarer, type, true);
		for  (EField field : annotation.getEClass().getEFields()) {
			Object value = getValue(annotation.eGet(field));
			if (value != EMPTYARRAY) {
				AnnotationFieldBinding fieldBinding = new AnnotationFieldBinding(InternUtil.internCaseSensitive(field.getName()), declarer, PrimitiveTypeBinding.getInstance(Primitive.ANY), type);
				fieldBinding.setValue(value, null, null, null, false);
				binding.addField(fieldBinding);
			}		
		}
		List<EMetadataObject> annotations = annotation.getMetadataList();
		for (int j = 0; j < annotations.size(); j++) {
			binding.addAnnotation(createAnnotationBinding(annotations.get(j)));
		}
		
		return binding;
	}
	
	protected IAnnotationBinding createStereotypeSubtypeFor(StereotypeType ir) {
		IPartBinding part = (IPartBinding)getBinding(ir);
		IAnnotationBinding binding = createAnnotationSubtypeFor(ir);
		IAnnotationTypeBinding stereotypeType = StereotypeAnnotationTypeBinding.getInstance();
		IAnnotationBinding stereotype = new AnnotationBinding(InternUtil.intern("Stereotype"), part, stereotypeType);
		binding.addAnnotation(stereotype);
		
		//add the isReference field to the annotationBinding
		if (ir.isReferenceType()) {
			ITypeBinding boolType = PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN);
			IAnnotationTypeBinding annType = AnnotationTypeManager.getAnnotationType("Annotation");
			AnnotationFieldBinding refTypeAnn = new AnnotationFieldBinding(InternUtil.intern("ReferenceType"), part, boolType, annType);
			refTypeAnn.setValue(Boolean.YES, null, null, null, false);
			stereotype.addField(refTypeAnn);
		}
		
		if (ir.getPartType() != null) {
			IAnnotationTypeBinding annType = AnnotationTypeManager.getAnnotationType("Annotation");
			AnnotationFieldBinding partTypeAnn = new AnnotationFieldBinding(InternUtil.intern("partType"), part, SystemPartManager.TYPEREF_BINDING, annType);
			ir.getPartType().accept(this);
			partTypeAnn.setValue(stack.pop(), null, null, null, false); 
			stereotype.addField(partTypeAnn);
		}

		if (ir.getDefaultSuperType() != null) {
			IAnnotationTypeBinding annType = AnnotationTypeManager.getAnnotationType("Annotation");
			AnnotationFieldBinding defaultSuperTypeAnn = new AnnotationFieldBinding(InternUtil.intern("defaultSuperType"), part, SystemPartManager.TYPEREF_BINDING, annType);
			ir.getDefaultSuperType().accept(this);
			defaultSuperTypeAnn.setValue(stack.pop(), null, null, null, false); 
			stereotype.addField(defaultSuperTypeAnn);
		}

		return binding;
	}
	
	// An AnnotationType when converted back to a AnnotationTypeBinding is really a Record
	// with an Annotation stereotype instance.  We must recreate that
	protected IAnnotationBinding createAnnotationSubtypeFor(EClass ir) {
		FlexibleRecordBinding declarer = (FlexibleRecordBinding)getBinding(ir);
		IAnnotationTypeBinding annType = AnnotationTypeManager.getAnnotationType(InternUtil.intern("Annotation"));
		String name = InternUtil.intern(ir.getName());
		IAnnotationBinding binding = new AnnotationBinding(annType.getName(), declarer, annType, true);
		for  (FlexibleRecordFieldBinding field : (List<FlexibleRecordFieldBinding>)declarer.getDeclaredFields()) {
			EField eField = ir.getEClass().getEField(field.getName());
			if (eField != null) {
				Object value = getValue(ir.eGet(eField));
				if (value != EMPTYARRAY) {
					AnnotationFieldBinding fieldBinding = new AnnotationFieldBinding(InternUtil.internCaseSensitive(field.getName()), declarer, PrimitiveTypeBinding.getInstance(Primitive.ANY), annType);
					fieldBinding.setValue(value, null, null, null, false);
					binding.addField(fieldBinding);
				}
			}
		}
		EnumerationTypeBinding elementKind = (EnumerationTypeBinding)env.getPartBinding(InternUtil.intern(new String[]{"egl", "core"}), InternUtil.intern("ElementKind"));
		EnumerationDataBinding[] list;
		int i = 0;
		IDataBinding entry = null;
		if (ir instanceof EMetadataType) {
			list = new EnumerationDataBinding[((EMetadataType)ir).getTargets().size()];
			for (EClass eClass : ((EMetadataType)ir).getTargets()) {
				if (eClass.getName().equals("EClass")) {
					entry = elementKind.findData(ElementKind_Part);
				}
				else if (eClass.getName().equals("EField")) {
					entry = elementKind.findData(ElementKind_FieldMbr);
				}
				else if (eClass.getName().equals("EFunction")) {
					entry = elementKind.findData(ElementKind_FunctionMbr);
				}
				else {
					entry = elementKind.findData(ElementKind_Part);
				}
				
				if (entry != null && entry != IBinding.NOT_FOUND_BINDING) {
					list[i]= (EnumerationDataBinding)entry;
					i++;
				}
			}
		}
		else {
			list = new EnumerationDataBinding[((AnnotationType)ir).getTargets().size()];
			for (ElementKind literal : ((AnnotationType)ir).getTargets()) {
				entry = elementKind.findData(InternUtil.intern(literal.name()));
				list[i] = (EnumerationDataBinding)entry;
				i++;
			}
			
			String valProxy = ((AnnotationType)ir).getValidationProxy();
			if (valProxy != null && valProxy.trim().length() > 0) {
				PrimitiveTypeBinding stringType = PrimitiveTypeBinding.getInstance(Primitive.STRING);
				AnnotationFieldBinding proxy = new AnnotationFieldBinding(InternUtil.intern("ValidationProxy"), declarer, stringType, annType);
				proxy.setValue(valProxy, null, null, null, false);
				binding.addField(proxy);
			}
		}
		ArrayTypeBinding arrayType = ArrayTypeBinding.getInstance(elementKind);
		AnnotationFieldBinding targets = new AnnotationFieldBinding(InternUtil.intern("targets"), declarer, arrayType, annType);
		targets.setValue(list, null, null, null, false);
		binding.addField(targets);
		
		if (ir instanceof AnnotationType) {
			List<Annotation> annotations = ((AnnotationType)ir).getAnnotations();
			for (int j = 0; j < annotations.size(); j++) {
				binding.addAnnotation(createAnnotationBinding(annotations.get(j)));
			}
		}		

		if (ir instanceof EMetadataType) {
			
			//BOOTSTRAP! Only certain EMetatDatTypes are stereotypes...these are:
			// egl.lang.reflect.mof.MofClass 
			// egl.lang.reflect.mof.MofDataType 
			// egl.lang.reflect.mof.MofEnum 
			if (declarer.getPackageName() == MOFPACKAGE && MOF_STEREOTYPES.keySet().contains(declarer.getName())) {
				
				//create and add the stereotype annotation
				IAnnotationTypeBinding stereotypeType = StereotypeAnnotationTypeBinding.getInstance();
				IAnnotationBinding stereotype = new AnnotationBinding(InternUtil.intern("Stereotype"), declarer, stereotypeType);
				binding.addAnnotation(stereotype);
				
				//create and add the partType annotation to the record
				FlexibleRecordBinding rec = (FlexibleRecordBinding)env.getPartBinding(InternUtil.intern(new String[]{"egl", "lang", "reflect"}), InternUtil.intern("PartType"));
				AnnotationTypeBindingImpl partTypeType = new AnnotationTypeBindingImpl(rec, declarer);
				IAnnotationBinding partType = new AnnotationBinding(InternUtil.intern("PartType"), declarer, partTypeType);
				partType.setValue(MOF_STEREOTYPES.get(declarer.getName()), null, null, null, false);
				declarer.addAnnotation(partType);
				
			}
		}
		
		return binding;
	}
	
	protected void addMofClassAnnotation(IPartBinding binding) {
		FlexibleRecordBinding rec = (FlexibleRecordBinding)env.getPartBinding(InternUtil.intern(new String[]{"egl", "lang", "reflect", "mof"}), InternUtil.intern("MofClass"));
		AnnotationTypeBindingImpl annType = new AnnotationTypeBindingImpl(rec, binding);
		IAnnotationBinding ann = new AnnotationBinding(InternUtil.intern("PartType"), binding, annType);
		binding.addAnnotation(ann);
		
	}
	
	protected IAnnotationBinding createAnnotationBinding(Annotation annotation) {
		IAnnotationTypeBinding type;
		String name = InternUtil.intern(annotation.getEClass().getName());
		IPartBinding declarer = null;
		if (!(annotation instanceof DynamicEObject)) {
			annotation.getEClass().accept(this);
			declarer = getPartBinding();
			type = new AnnotationTypeBindingImpl((FlexibleRecordBinding) stack.pop(), declarer);
		} else {
			type = AnnotationTypeManager.getAnnotationType(name);
			if (type != null && ((AnnotationTypeBinding)type).hasSingleValue()) {
				declarer = getPartBinding();
			}
		}
		
		AnnotationBinding annotationBinding;
		String typeName = annotation.getEClass().getName();
		
		if (elementAnnotationIndex > 0) {
			annotationBinding = new AnnotationBindingForElement(InternUtil.internCaseSensitive(typeName), getPartBinding(), type, elementAnnotationIndex);
		}
		else {
			annotationBinding = new AnnotationBinding(InternUtil.internCaseSensitive(typeName), getPartBinding(), type, true);
		}
		for  (EField field : annotation.getEClass().getEFields()) {
			Object value = getValue(annotation.eGet(field));
			if (value != EMPTYARRAY) {
				AnnotationFieldBinding fieldBinding = new AnnotationFieldBinding(InternUtil.internCaseSensitive(field.getName()), declarer, PrimitiveTypeBinding.getInstance(Primitive.ANY), type);
				fieldBinding.setValue(value, null, null, null, false);
				annotationBinding.addField(fieldBinding);
			}
		}
		List<Annotation> annotations = annotation.getAnnotations();
		for (int j = 0; j < annotations.size(); j++) {
			annotationBinding.addAnnotation(createAnnotationBinding(annotations.get(j)));
		}
		return annotationBinding;

	}
	
	boolean shouldIgnore(Annotation ir) {
		return ignoreTheseAnnotations.contains(ir.getEClass().getName());
	}
	
	boolean isEGLReflectStereotype(EMetadataType type) {
		return type.getMetadata("PartType") != null;
	}
		
}
