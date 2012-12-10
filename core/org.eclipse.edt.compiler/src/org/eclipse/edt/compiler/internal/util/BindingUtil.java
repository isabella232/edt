package org.eclipse.edt.compiler.internal.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.edt.compiler.PartEnvironmentStack;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.TernaryExpression;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Class;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.DataItem;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.EClassProxy;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PatternType;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.eclipse.edt.mof.utils.NameUtile;

public class BindingUtil {
	
	private static final String INVALID_ANNOTATION = "edt invalid";
	public static final String ENVIRONMENT_ANNOTATION = "edt environment";
	
	private static Annotation ValidAnn;
	
	private static Map<Type, WeakReference<ArrayType>> typeBindingsToArrayTypes = new WeakHashMap<Type, WeakReference<ArrayType>>();
	private static Map<Type, WeakReference<ArrayType>> typeBindingsToArrayTypesNullable = new WeakHashMap<Type, WeakReference<ArrayType>>();
	
	private static Map<String, PackageAndPartName> aliasedTypesNames = new HashMap<String, PackageAndPartName>();
	static {
		aliasedTypesNames.put(NameUtile.getAsName("any"), new PackageAndPartName("eglx.lang", "EAny"));
		aliasedTypesNames.put(NameUtile.getAsName("bigint"), new PackageAndPartName("eglx.lang", "EBigint"));
		aliasedTypesNames.put(NameUtile.getAsName("boolean"), new PackageAndPartName("eglx.lang", "EBoolean"));
		aliasedTypesNames.put(NameUtile.getAsName("bytes"), new PackageAndPartName("eglx.lang", "EBytes"));
		aliasedTypesNames.put(NameUtile.getAsName("date"), new PackageAndPartName("eglx.lang", "EDate"));
		aliasedTypesNames.put(NameUtile.getAsName("decimal"), new PackageAndPartName("eglx.lang", "EDecimal"));
		aliasedTypesNames.put(NameUtile.getAsName("dictionary"), new PackageAndPartName("eglx.lang", "EDictionary"));
		aliasedTypesNames.put(NameUtile.getAsName("float"), new PackageAndPartName("eglx.lang", "EFloat"));
		aliasedTypesNames.put(NameUtile.getAsName("int"), new PackageAndPartName("eglx.lang", "EInt"));
		aliasedTypesNames.put(NameUtile.getAsName("number"), new PackageAndPartName("eglx.lang", "ENumber"));
		aliasedTypesNames.put(NameUtile.getAsName("smallfloat"), new PackageAndPartName("eglx.lang", "ESmallfloat"));
		aliasedTypesNames.put(NameUtile.getAsName("smallint"), new PackageAndPartName("eglx.lang", "ESmallint"));
		aliasedTypesNames.put(NameUtile.getAsName("string"), new PackageAndPartName("eglx.lang", "EString"));
		aliasedTypesNames.put(NameUtile.getAsName("timestamp"), new PackageAndPartName("eglx.lang", "ETimestamp"));
		aliasedTypesNames.put(NameUtile.getAsName("time"), new PackageAndPartName("eglx.lang", "ETime"));
	};


	public static boolean isValid(Part part) {
		return part != null && part.getAnnotation(INVALID_ANNOTATION) == null;
	}
	
	private static Annotation getInvalidAnn() {
		if (ValidAnn == null) {
			ValidAnn = createInvalidAnn();
		}
		return ValidAnn;
	}
	
	private static Annotation createInvalidAnn() {
		return createAnnotation(INVALID_ANNOTATION);
	}
	
	private static Annotation createAnnotation(String name) {
		Annotation ann = IrFactory.INSTANCE.createDynamicAnnotation(name);
		return ann;

	}
	
	public static void setValid(Part part, boolean value) {
		
		Annotation ann =  part.getAnnotation(INVALID_ANNOTATION);
		
		if (value) {
			if (ann == null) {
				return;
			}
			part.removeAnnotation(ann);
			
		}
		else {
			if (ann == null) {
				part.addAnnotation(getInvalidAnn());
			}
		}
	}
	
	public static String getLastSegment(String str) {
		if (str == null) {
			str = "";
		}
		int index = str.lastIndexOf(".");
		if (index < 0) {
			return str;
		}
		
		return str.substring(index + 1);
	}
	
	public static String removeLastSegment(String str) {
		if (str == null) {
			return null;
		}

		int index = str.lastIndexOf(".");
		if (index < 0) {
			return "";
		}
		
		return str.substring(0, index);

	}
	
	public static IRPartBinding createPartBinding(EObject obj) {
		if (obj instanceof Part) {
			return new IRPartBinding((Part)obj);
		}
		
		return null;
	}
	
	public static int getPartTypeConstant(Part part) {
		if (part instanceof Delegate) {
			return ITypeBinding.DELEGATE_BINDING;
		}
		if (part instanceof ExternalType) {
			return ITypeBinding.EXTERNALTYPE_BINDING;
		}
		if (part instanceof StereotypeType) {
			return ITypeBinding.STEREOTYPE_BINDING;
		}
		if (part instanceof AnnotationType) {
			return ITypeBinding.ANNOTATION_BINDING;
		}
		if (part instanceof Record) {
			return ITypeBinding.FLEXIBLE_RECORD_BINDING;
		}
		if (part instanceof Handler) {
			return ITypeBinding.HANDLER_BINDING;
		}
		if (part instanceof Interface) {
			return ITypeBinding.INTERFACE_BINDING;
		}
		if (part instanceof Library) {
			return ITypeBinding.LIBRARY_BINDING;
		}
		if (part instanceof Program) {
			return ITypeBinding.PROGRAM_BINDING;
		}
		if (part instanceof Service) {
			return ITypeBinding.SERVICE_BINDING;
		}
		if (part instanceof DataItem) {
			return ITypeBinding.DATAITEM_BINDING;
		}
		if (part instanceof FunctionPart) {
			return ITypeBinding.FUNCTION_BINDING;
		}
		if (part instanceof Enumeration) {
			return ITypeBinding.ENUMERATION_BINDING;
		}
		if (part instanceof EGLClass) {
			return ITypeBinding.CLASS_BINDING;
		}
		
		return -1;
	}
	
	public static IPartBinding createPartBinding(int type, PackageAndPartName ppName) {
		IPartBinding partBinding = primCreatePartBinding(type, ppName.getCaseSensitivePackageName(), ppName.getCaseSensitivePartName());
		if (partBinding instanceof IRPartBinding) {
			setValid(((IRPartBinding)partBinding).getIrPart(), false);
		}
		return partBinding;
	}
	
	
	private static IPartBinding primCreatePartBinding(int type, String pkgName, String name) {
		Part part;
		switch (type) {
        case ITypeBinding.FILE_BINDING:
        	return new FileBinding(pkgName, name);
    	case ITypeBinding.DELEGATE_BINDING:
    		part = createDelegate(pkgName, name);
    		return createPartBinding(part);
        case ITypeBinding.EXTERNALTYPE_BINDING:
        	part = createExternalType(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.FLEXIBLE_RECORD_BINDING:
        	part = createRecord(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.HANDLER_BINDING:
        	part = createHandler(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.INTERFACE_BINDING:
        	part = createInterface(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.LIBRARY_BINDING:
        	part = createLibrary(pkgName, name);
        	return createPartBinding(part);
         case ITypeBinding.PROGRAM_BINDING:
         	part = createProgram(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.SERVICE_BINDING:
        	part = createService(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.DATAITEM_BINDING:
        	part = createDataItem(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.FUNCTION_BINDING:
        	part = createFunction(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.ENUMERATION_BINDING:
        	part = createEnumeration(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.CLASS_BINDING:
        	part = createClass(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.ANNOTATION_BINDING:
        	part = createAnnotationType(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.STEREOTYPE_BINDING:
        	part = createStereotypeType(pkgName, name);
        	return createPartBinding(part);
        
        default:
            throw new RuntimeException("Unsupported kind: " + type);
		}		
	}


	private static AnnotationType createAnnotationType(String pkgName, String name) {
		AnnotationType annType = IrFactory.INSTANCE.createAnnotationType();
		annType.setName(name);
		annType.setPackageName(pkgName);
		return annType;		
	}

	private static StereotypeType createStereotypeType(String pkgName, String name) {
		StereotypeType sterType = IrFactory.INSTANCE.createStereotypeType();
		sterType.setName(name);
		sterType.setPackageName(pkgName);
		return sterType;		
	}
	
	private static Program createProgram(String pkgName, String name) {
		Program pgm = IrFactory.INSTANCE.createProgram();
		pgm.setName(name);
		pgm.setPackageName(pkgName);
		return pgm;		
	}
	
	private static Library createLibrary(String pkgName, String name) {
		Library lib = IrFactory.INSTANCE.createLibrary();
		lib.setName(name);
		lib.setPackageName(pkgName);
		return lib;		
	}

	private static Handler createHandler(String pkgName, String name) {
		Handler hnd = IrFactory.INSTANCE.createHandler();
		hnd.setName(name);
		hnd.setPackageName(pkgName);
		return hnd;		
	}

	private static ExternalType createExternalType(String pkgName, String name) {
		ExternalType ext = IrFactory.INSTANCE.createExternalType();
		ext.setName(name);
		ext.setPackageName(pkgName);
		return ext;		
	}

	private static Service createService(String pkgName, String name) {
		Service ser = IrFactory.INSTANCE.createService();
		ser.setName(name);
		ser.setPackageName(pkgName);
		return ser;		
	}

	private static Class createClass(String pkgName, String name) {
		Class clazz = IrFactory.INSTANCE.createClass();
		clazz.setName(name);
		clazz.setPackageName(pkgName);
		return clazz;		
	}
	
	private static Enumeration createEnumeration(String pkgName, String name) {
		Enumeration enm = IrFactory.INSTANCE.createEnumeration();
		enm.setName(name);
		enm.setPackageName(pkgName);
		return enm;		
	}

	private static Record createRecord(String pkgName, String name) {
		Record rcd = IrFactory.INSTANCE.createRecord();
		rcd.setName(name);
		rcd.setPackageName(pkgName);
		return rcd;		
	}

	private static Delegate createDelegate(String pkgName, String name) {
		Delegate del = IrFactory.INSTANCE.createDelegate();
		del.setName(name);
		del.setPackageName(pkgName);
		return del;		
	}

	private static Interface createInterface(String pkgName, String name) {
		Interface inf = IrFactory.INSTANCE.createInterface();
		inf.setName(name);
		inf.setPackageName(pkgName);
		return inf;		
	}
	
	private static DataItem createDataItem(String pkgName, String name) {
		DataItem dti = IrFactory.INSTANCE.createDataItem();
		dti.setName(name);
		dti.setPackageName(pkgName);
		return dti;		
	}

	private static FunctionPart createFunction(String pkgName, String name) {
		FunctionPart fun = IrFactory.INSTANCE.createFunctionPart();
		fun.setName(name);
		fun.setPackageName(pkgName);
		return fun;		
	}
	
	public static Part findPart(String pkgName, String name) {
		IPartBinding partBinding = PartEnvironmentStack.getCurrentEnv().getPartBinding(pkgName, name);
		return getPart(partBinding);
	}
	
	public static Part getPart(IPartBinding partBinding) {
		if (partBinding == null) {
			return null;
		}

		if (!(partBinding instanceof IRPartBinding)) {
			return null;
		}
		
		return ((IRPartBinding)partBinding).getIrPart();
	}
	
	public static AnnotationType getAnnotationType(String pkgName, String name) {		
		Part part = findPart(pkgName, name);
		
		if (part instanceof AnnotationType) {
			return (AnnotationType) part;
		}	
				
		return null;
	}
	
	public static AnnotationType getAnnotationType(Type type) {
		if (type instanceof AnnotationType) {
			return (AnnotationType) type;
		}
		
		return null;
	}
	
	public static Part realize(Part part) {
		if (isValid(part)) {
			return part;
		}
		
		return findPart(part.getPackageName(), part.getName());
		
	}
	
	
	public static boolean functionSignituresAreIdentical(Function function1, Function function2) {
		return functionSignituresAreIdentical(function1, function2, true, true);
	}
	
	public static boolean functionSignituresAreIdentical(Function function1, Function function2, boolean includeReturnTypeInSignature, boolean includeParameterModifiersInSignature) {
		if (!NameUtile.equals(function1.getName(), function2.getName())) {
			return false;
		}
		return functionSignituresAreIdentical(new FunctionSignature(function1), new FunctionSignature(function2), includeReturnTypeInSignature, includeParameterModifiersInSignature);
	}
	
	public static boolean functionSignituresAreIdentical(IFunctionSignature fSignature1, IFunctionSignature fSignature2) {
		return functionSignituresAreIdentical(fSignature1, fSignature2, true, true);
	}
	
	public static boolean functionSignituresAreIdentical(IFunctionSignature fSignature1, IFunctionSignature fSignature2, boolean includeReturnTypeInSignature, boolean includeParameterModifiersInSignature) {
		List<FunctionParameter> parameters1 = fSignature1.getParameters();
		List<FunctionParameter> parameters2 = fSignature2.getParameters();
		
		if(parameters1.size() != parameters2.size()) {
			return false;
		}
		
		for(int i = 0; i < parameters1.size(); i++) {
			FunctionParameter parm1 = parameters1.get(i);
			FunctionParameter parm2 = parameters2.get(i);
			
			if(includeParameterModifiersInSignature) {
				
				if (parm1.getParameterKind() != parm2.getParameterKind()) {
					return false;
				}
				
				if (parm1.isConst() != parm2.isConst()) {
					return false;
				}

				if (parm1.isField() != parm2.isField()) {
					return false;
				}	
				
				if (parm1.isNullable() != parm2.isNullable()) {
					return false;
				}
			}
			
			if(!typesAreIdentical(parm1.getType(), parm2.getType())) {
				return false;
			}
						
		}
		
		if(includeReturnTypeInSignature) {
			Type returnType1 = fSignature1.getReturnType();
			Type returnType2 = fSignature2.getReturnType();
			if(returnType1 == null) {
				if(returnType2 != null) {
					return false;
				}
			}
			else {
				if(returnType2 == null) {
					return false;
				}
				else {
					if(!typesAreIdentical(returnType1, returnType2)) {
						return false;
					}
					if (fSignature1.isReturnNullable() != fSignature2.isReturnNullable()) {
						return false;
					}
				}
			}
		}
		
		
		return true;
	}

	public static boolean typesAreIdentical(Type type1, Type type2) {
		if (type1 == null && type2 == null) {
			return true;
		}
		
		if (type1 == null || type2 == null) {
			return false;
		}
		
		return type1.equals(type2);
	}
	
	public static interface IFunctionSignature {
		Type getReturnType();
		boolean isReturnNullable();
		List<FunctionParameter> getParameters();		
	}
	
	public static class DelegateSignature implements IFunctionSignature {
		private Delegate delegateBinding;

		public DelegateSignature(Delegate delegateBinding) {
			this.delegateBinding = delegateBinding;
		}

		public Type getReturnType() {
			return delegateBinding.getReturnType();
		}

		public List<FunctionParameter> getParameters() {
			return delegateBinding.getParameters();
		}
		public boolean isReturnNullable() {
			return delegateBinding.isNullable();
		}
	}
	
	public static class FunctionSignature implements IFunctionSignature {
		private Function functionBinding;

		public FunctionSignature(Function functionBinding) {
			this.functionBinding = functionBinding;
		}
		
		public Type getReturnType() {
			return functionBinding.getReturnType();
		}

		public List<FunctionParameter> getParameters() {
			return functionBinding.getParameters();
		}
		
		public boolean isReturnNullable() {
			return functionBinding.isNullable();
		}
	}
	
	public static boolean isApplicableFor(AnnotationType annType, Element elem) {
		annType = (AnnotationType)realize(annType);
		return isApplicableFor(elem, annType.getTargets());
	}
	
	public static boolean isApplicableFor(Element targetBinding, List<ElementKind> targets) {
		
		ElementKind targetType = BindingUtil.getElementKind(targetBinding);
		return isApplicableFor(targetType, targets, targetBinding instanceof Part);
	}

	
	public static boolean isApplicableFor(ElementKind targetType, List<ElementKind> targets, boolean targetIsPart) {
		
		if (targetType == null) {
			return false;
		}
		
		String targetTypeName = NameUtile.getAsName(targetType.name());

		for(Object nextTarget : targets) {

			String nextTargetName = null;

			if (nextTarget instanceof ElementKind) {
				if (targetType == nextTarget) {
					return true;
				}
				else {
					nextTargetName = NameUtile.getAsName(((ElementKind)nextTarget).name());
				}
			}
			else {			
				if (nextTarget instanceof EEnumLiteral) {
					nextTargetName = NameUtile.getAsName(((EEnumLiteral)nextTarget).getName());
					if (NameUtile.equals(nextTargetName, targetTypeName)) {
						return true;
					}
				}
			}
			
			if (targetIsPart && NameUtile.equals(nextTargetName, NameUtile.getAsName("part"))) {
				return true;
			}

		}
		return false;
	}

	
	public static ElementKind getElementKind(Element elem) {
		if (elem instanceof Record) {
			return ElementKind.RecordPart;
		}
		
		if (elem instanceof Program) {
			return ElementKind.ProgramPart;
		}
		
		if (elem instanceof Library) {
			return ElementKind.LibraryPart;
		}

		if (elem instanceof Handler) {
			return ElementKind.HandlerPart;
		}
				
		if (elem instanceof Interface) {
			return ElementKind.InterfacePart;
		}

		if (elem instanceof Service) {
			return ElementKind.ServicePart;
		}
		
		if (elem instanceof ExternalType) {
			return ElementKind.ExternalTypePart;
		}
		
		if (elem instanceof Delegate) {
			return ElementKind.DelegatePart;
		}
		
		if (elem instanceof Enumeration) {
			return ElementKind.EnumerationPart;
		}
		
		if (elem instanceof EnumerationEntry) {
			return ElementKind.EnumerationEntry;
		}
		
		if (elem instanceof Field) {
			return ElementKind.FieldMbr;
		}
				
		if (elem instanceof Constructor) {
			return ElementKind.ConstructorMbr;
		}
			
		if (elem instanceof Function) {
			return ElementKind.FunctionMbr;
		}

		if (elem instanceof EGLClass) {
			return ElementKind.ClassPart;
		}
		
		if (elem instanceof AnnotationType) {
			return ElementKind.RecordPart;
		}
		
		return null;
	}
	
	public static Type getBaseType(Type type) {
		if (type instanceof ArrayType) {
			return getBaseType(((ArrayType)type).getElementType());
		}
		return type;
	}
	
	public static List<Member> findPublicMembers(Type type, String id) {
		if (type instanceof Container) {
			
			List<Member> list = new ArrayList<Member>();
			for (Member mbr : ((Container)type).getAllMembers()) {
				if (NameUtile.equals(id, mbr.getId())) {
					if (!isPrivate(mbr)) {
						list.add(mbr);
						//If we find a field, we can stop searching, but for functions, we want to collect all the functions
						//with the given name
						if (mbr instanceof Field) {
							return list;
						}
					}
				}
			}
			if (list.isEmpty()) {
				return null;
			}
			return list;
		}
		return null;
	}

	public static List<Member> findMembers(Type type, String id) {
		if (type instanceof Container) {
			
			List<Member> list = new ArrayList<Member>();
			for (Member mbr : ((Container)type).getAllMembers()) {
				if (NameUtile.equals(id, mbr.getId())) {
					list.add(mbr);
					
					//If we find a field, we can stop searching, but for functions, we want to collect all the functions
					//with the given name
					if (mbr instanceof Field) {
						return list;
					}
				}
			}
			if (list.isEmpty()) {
				return null;
			}
			return list;
		}
		return null;
	}
	
	public static List<Field> getAllFields(Type type) {
		List<Field> fields = new ArrayList<Field>();
		if (type instanceof Container) {
			for (Member mbr : ((Container)type).getAllMembers()) {
				if (mbr instanceof Field) {
					fields.add((Field) mbr);
				}
			}
		}

		return fields;
	}


	public static List<Function> getAllFunctions(Type type) {
		List<Function> functions = new ArrayList<Function>();
		if (type instanceof Container) {
			for (Member mbr : ((Container)type).getAllMembers()) {
				if (mbr instanceof Function) {
					functions.add((Function) mbr);
				}
			}
		}

		return functions;
	}
	
	public static ArrayType getArrayType(Type elemType, boolean nullable)  {
		Map<Type, WeakReference<ArrayType>> map;
		
		if (nullable) {
			map = typeBindingsToArrayTypesNullable;
		}
		else {
	    	map = typeBindingsToArrayTypes;
		}
		WeakReference<ArrayType> result = map.get(elemType);
		
    	if(result == null || result.get() == null) {
    		
    		ArrayType newArray = IrFactory.INSTANCE.createArrayType();
    		newArray.setElementType(elemType);
    		newArray.setElementsNullable(nullable);
    		newArray.setClassifier((Classifier)TypeUtils.Type_LIST);
    		result = new WeakReference<ArrayType>(newArray);
    		
    		map.put(elemType, result);
    	}
    	return result.get();
	}
	
	public static Type findAliasedType(String name) {
		PackageAndPartName ppn = aliasedTypesNames.get(name);
		if (ppn == null) {
			return null;
		}
		
		return IRUtils.getEGLType(ppn.getPackageName() + "." + ppn.getPartName());		
	}
	
	public static String getUnaliasedTypeName(Type type, boolean includeParams) {
		if (type == null) {
			return "";
		}
		
		if (type.equals( TypeUtils.Type_NULLTYPE)) {
			return IEGLConstants.KEYWORD_NULL;
		}
		
		Classifier classifier = type.getClassifier();
		String unaliasedName = null;
		if (classifier != null) {
			String pkg = NameUtile.getAsName(classifier.getPackageName());
			String name = NameUtile.getAsName(classifier.getName());
			
			for (Map.Entry<String, PackageAndPartName> entry : aliasedTypesNames.entrySet()) {
				PackageAndPartName value = entry.getValue();
				if (NameUtile.equals(value.getPackageName(), pkg) && NameUtile.equals(value.getPartName(), name)) {
					unaliasedName = entry.getKey();
					
					if (includeParams) {
						if (type instanceof ParameterizedType) {
							String sig = type.getTypeSignature();
							int paren = sig.indexOf(Type.PrimArgsStartDelimiter);
							if (paren != -1) {
								unaliasedName += sig.substring(paren);
							}
						}
					}
					break;
				}
			}
		}
		
		if (!includeParams && unaliasedName == null && type instanceof ParameterizedType) {
			type = ((ParameterizedType)type).getParameterizableType();
		}
		
		if (unaliasedName == null) {
			unaliasedName = type.getTypeSignature();
		}
		
		if (type instanceof ParameterizedType) {
			// MOF returns the args in the format "(arg1:arg2)" instead of "(arg1, arg2)".
			int paren = unaliasedName.indexOf(Type.PrimArgsStartDelimiter);
			if (paren != -1) {
				unaliasedName = unaliasedName.replaceAll(Type.PrimArgDelimiter, ",");
			}
			
			if (type instanceof PatternType && paren != -1) {
				// Need to surround the pattern with quotes.
				int closeParen = unaliasedName.indexOf(Type.PrimArgsEndDelimiter);
				if (closeParen != -1) {
					unaliasedName = unaliasedName.substring(0, paren + 1) + "\"" + unaliasedName.substring(paren + 1, closeParen)
							+ "\"" + unaliasedName.substring(closeParen);
				}
			}
		}
		return unaliasedName;
	}
	
	public static String getTypeString(Type binding, boolean includeParams) {
		StringBuilder result = new StringBuilder();
		if (binding instanceof ArrayType) {
			result.append(getTypeString(((ArrayType)binding).getElementType(), includeParams));
			if (((ArrayType)binding).elementsNullable()) {
				result.append('?');
			}
			result.append("[]");
		}				
		else if (binding instanceof AnnotationType) {
			result.append('@');
			result.append(binding.getTypeSignature());
		}				
		else if (binding != null) {
			result.append(BindingUtil.getUnaliasedTypeName(binding, includeParams));
		}
		
		return result.toString();
	}
	
	public static String getShortTypeString(Expression expr, Type type) {
		if (expr instanceof ArrayLiteral && type instanceof ArrayType && TypeUtils.Type_NULLTYPE.equals(((ArrayType)type).getElementType())) {
			// Don't return "null[]" - instead just return the canonical expression. One or more elements caused the type to be null, like a function pointer.
			return expr.getCanonicalString();
		}
		if (type != null) {
			return getShortTypeString(type);
		}
		return getTypeName(expr.resolveMember());
	}
	
	public static String getShortTypeString(Type binding) {
		return getShortTypeString(binding, false);
	}
	
	public static String getShortTypeString(Type binding, boolean includeParams) {
		String s = getTypeString(binding, includeParams);
		int lastDot = s.lastIndexOf('.');
		if (lastDot == -1) {
			return s;
		}
		return s.substring(lastDot + 1);
	}
	
	public static String getTypeName(Member member) {
		if (member == null) {
			return "";
		}
		
		return getTypeName(member, member.getType());
	}
	
	public static String getTypeName(Member member, Type type) {
		StringBuilder buf = new StringBuilder();
		if (member instanceof FunctionMember) {
			buf.append(member.getName());
		}
		else if (type != null) {
			buf.append(getShortTypeString(type, true));
		}
		
		if(member != null && member.isNullable()){
			buf.append('?');;
		}
		return buf.toString();
	}
	
	public static Type getType(Member mbr) {
		if (mbr instanceof Function) {
			return null;
		}
		return mbr.getType();
		
	}
	
	public static Type getType(Object obj) {
		if (obj instanceof Member) {
			return getType((Member)obj);
		}
		if (obj instanceof Type) {
			return (Type)obj;
		}
		return null;
	}
	
	
	public static boolean isDynamicallyAccessible(Type type) {
		//All types are dynamically accessible, as long as they do not inherit from AnyValue
		if (type == null || !(type.getClassifier() instanceof StructPart)) {
			return false;
		}
		
		StructPart sp = (StructPart)type.getClassifier();
		return (sp instanceof Record) || !(sp.isSubtypeOf((StructPart)TypeUtils.getType(TypeUtils.Type_AnyValue)));
	}

	public static boolean isExplicitlyDynamicallyAccessible(Type type) {
		return TypeUtils.isDynamicType(type);
	}

	public static Member createDynamicAccessMember(Type type, String caseSensitiveName) {
		if (isDynamicallyAccessible(type)) {
			Field field = IrFactory.INSTANCE.createField();
			field.setName(caseSensitiveName);			
			field.setType(getEAny());
			field.setIsNullable(true);
			return field;
		}
		return null;
	}
	
	public static Member createExplicitDynamicAccessMember(Type type, String caseSensitiveName) {
		if (isExplicitlyDynamicallyAccessible(type)) {
			Field field = IrFactory.INSTANCE.createField();
			field.setName(caseSensitiveName);			
			field.setType(getEAny());
			field.setIsNullable(true);
			return field;
		}
		return null;
	}

	
	public static Part getEAny() {
		return findPart(NameUtile.getAsName(MofConversion.EGLX_lang_package), NameUtile.getAsName("eany"));
	}
	
	public static boolean isPrivate(Part part) {
		if (part == null) {
			return false;
		}
		return part.getAccessKind() == AccessKind.ACC_PRIVATE;
	}

	public static boolean isPrivate(Member mbr) {
		if (mbr == null) {
			return false;
		}
		return mbr.getAccessKind() == AccessKind.ACC_PRIVATE;
	}
	
	public static boolean isAbstract(Type type) {
		return type instanceof EGLClass && ((EGLClass)type).isAbstract();
	}
	
	public static String getName(Element elem) {
		if (elem instanceof NamedElement) {
			return ((NamedElement)elem).getCaseSensitiveName();
		}
		return "";
	}
	
	public static boolean isParameterizableType(Type type) {
		return type instanceof ParameterizableType || (type != null && type.getClassifier() instanceof ParameterizableType);
	}
	
	public static void setDefaultSupertype(StructPart part, Stereotype subType, StructPart defaultSuperType) {
		List<StructPart> superTypes = part.getSuperTypes();
		
		//check if there is already a supertype that is not just an interface
		for (StructPart stype : superTypes) {
			if (!(stype instanceof Interface)) {
				return;
			}
		}
		
		//check if there is a default supertype defined on the stereotype
		if (subType != null && subType.getEClass() instanceof StereotypeType) {
			StereotypeType clazz = (StereotypeType) subType.getEClass();
			if (clazz.getDefaultSuperType() instanceof  StructPart) {
				superTypes.add(0, (StructPart)clazz.getDefaultSuperType());
				return;
			}
		}
		
		//Make sure we do not set a part as it's own supertype (such as for EAny
		if (!part.equals(defaultSuperType)) {
			//add the passed supertype to the supertype list
			superTypes.add(0, defaultSuperType);
		}
		
	}
	
	public static boolean isZeroLiteral(Expression expr) {
		final boolean[] isZero = new boolean[1];
		expr.accept(new DefaultASTVisitor() {
			public boolean visit(IntegerLiteral integerLiteral) {
				isZero[0] = Integer.valueOf(integerLiteral.getValue()) == 0;
				return false;
			}
		});
		return isZero[0];
	}
	
	public static Part getDeclaringPart(Member m) {
		if (m != null) {
			Container c = m.getContainer();
			while (c != null && !(c instanceof Part)) {
				if (c instanceof Member) {
					c = ((Member)c).getContainer();
				}
				else {
					c = null;
				}
			}
			return (Part)c;
		}
		return null;
	}
	
	public static Part getDeclaringPart(Node n) {
		while (n != null && !(n instanceof org.eclipse.edt.compiler.core.ast.Part)) {
			n = n.getParent();
		}
		if (n instanceof org.eclipse.edt.compiler.core.ast.Part) {
			return (Part)((org.eclipse.edt.compiler.core.ast.Part)n).getName().resolveType();
		}
		return null;
	}
	
	public static boolean isUnresolvedGenericType(Type type) {
		if (type instanceof ArrayType) {
			// Only need to resolve if the root type is generic.
			Type root = type;
	    	while (root instanceof ArrayType) {
	    		root = ((ArrayType)root).getElementType();
	    	}
	    	return root instanceof GenericType;
		}
		return type instanceof GenericType;
	}
	
	public static Type resolveGenericType(Type type, Expression expr) {
		return resolveGenericType(type, getTypeForGenericQualifier(expr));
	}
	
	public static Type resolveGenericType(Type type, Type qualifierType) {
		if (type instanceof ArrayType) {
			if (isUnresolvedGenericType(type)) {
	    		return ((ArrayType)type).resolveTypeParameter(qualifierType);
	    	}
		}
		else if (type instanceof GenericType) {
			return ((GenericType)type).resolveTypeParameter(qualifierType);
		}
		
		return type;
	}
	
	public static Type getTypeForGenericQualifier(Expression expr) {
		final Type[] type = new Type[1];
		class ExitVisitor extends RuntimeException{private static final long serialVersionUID = 1L;};
		try {
			expr.accept(new AbstractASTVisitor() {
				@Override
				public boolean visit(FunctionInvocation functionInvocation) {
					functionInvocation.getTarget().accept(this);
					return false;
				};
				@Override
				public boolean visit(QualifiedName qualifiedName) {
					type[0] = qualifiedName.getQualifier().resolveType();
					throw new ExitVisitor();
				};
				@Override
				public boolean visit(BinaryExpression binaryExpression) {
					Type binType = binaryExpression.resolveType();
					if (binType != null) {
						// Could be "myString :: myList" or "myList :: myString". Resolve generics based on which classifier matches.
						Type type1 = binaryExpression.getFirstExpression().resolveType();
						if (type1 != null && type1.getClassifier() != null && type1.getClassifier().equals(binType.getClassifier())) {
							type[0] =  resolveGenericType(binType, binaryExpression.getFirstExpression());
						}
						else {
							type[0] =  resolveGenericType(binType, binaryExpression.getSecondExpression());
						}
						
						throw new ExitVisitor();
					}
					return false;
				};
				@Override
				public boolean visit(TernaryExpression ternaryExpression) {
					Type ternType = ternaryExpression.resolveType();
					if (ternType != null) {
						type[0] =  resolveGenericType(ternType, ternaryExpression.getSecondExpr());
						throw new ExitVisitor();
					}
					return false;
				};
				@Override
				public boolean visit(FieldAccess fieldAccess) {
					type[0] = fieldAccess.getPrimary().resolveType();
					throw new ExitVisitor();
				};
			});
		}
		catch (ExitVisitor e) {
		}
		
		if (type[0] != null) {
			return type[0];
		}
		
		return expr.resolveType();
	}
	
	public static void setEnvironment(Element e, IEnvironment env) {
		if (e == null) {
			return;
		}
		Annotation a = e.getAnnotation(ENVIRONMENT_ANNOTATION);
		if (a == null) {
			a = createAnnotation(ENVIRONMENT_ANNOTATION);
			a.setValue(env);
			e.addAnnotation(a);
		}
	}
	
	public static IEnvironment getEnvironment(Element e) {
		if (e == null) {
			return null;
		}
		Annotation a = e.getAnnotation(ENVIRONMENT_ANNOTATION);
		if (a != null) {
			return (IEnvironment)a.getValue();
		}
		return null;
	}
	
	public static Annotation getAnnotationWithSimpleName(Element obj, String simpleName) {
		for (Annotation ann : obj.getAnnotations()) {
			if (ann.getEClass().getName().equalsIgnoreCase(simpleName)) {
				return ann;
			}
		}
		return null;
	}
	
	public static EObject getMofClassProxyFor(Classifier obj) {
		//check for mof class proxy
		Annotation mofClassAnn = getAnnotationWithSimpleName(obj, "MofClass");
		if (mofClassAnn != null) {
			String name =  (String)mofClassAnn.getValue("name");
			String pkgName = (String)mofClassAnn.getValue("packageName");
			if (name == null || name.length() == 0) {
				name = obj.getCaseSensitiveName();
			}
			if (pkgName == null || pkgName.length() == 0) {
				pkgName = obj.getCaseSensitivePackageName();
			}
			
			String fullName;
			if (pkgName.length() == 0) {
				fullName = name;
			}
			else {
				fullName = pkgName + "." + name;
			}
			
			try {
				return Environment.getCurrentEnv().find(fullName);
			} catch (MofObjectNotFoundException e) {
			} catch (DeserializationException e) {
			}
		}
		return null;
	}
	
	public static boolean isSubClassOf(EClass child, EClass parent) {
		if (!child.getSuperTypes().isEmpty()) {
			for (EClass superType : child.getSuperTypes()) {
				if (superType.getETypeSignature().equals(parent.getETypeSignature())) {
					return true;
				}
			}
			for (EClass superType : child.getSuperTypes()) {
				if (isSubClassOf(superType, parent)) return true;
			}
			return false;
		}
		else {
			return false;
		}
	}
	
	public static EType getETypeFromProxy(Element elem) {
		//handle proxy types
		if (elem instanceof EClassProxy) {
			return ((EClassProxy)elem).getProxiedEClass();
		}
		
		Annotation ann = elem.getAnnotation("egl.lang.reflect.EClassProxy");
		if (ann != null) {
			String key = (String)ann.getValue();
			try {
				EObject eobj = Environment.getCurrentEnv().find(key);
				if (eobj instanceof EType) {
					return (EType) eobj;
				}
			} catch (MofObjectNotFoundException e) {
			} catch (DeserializationException e) {
			}
		}
		return null;
	}

	public static String getETypeSignatureFromProxy(Element elem) {
		//handle proxy types
		if (elem instanceof EClassProxy) {
			return ((EClassProxy)elem).getProxiedEClassName();
		}
		
		Annotation ann = elem.getAnnotation("egl.lang.reflect.EClassProxy");
		if (ann != null) {
			return (String)ann.getValue();
		}
		
		return null;
	}

	public static boolean isEClassProxy(Element elem) {
		//handle proxy types
		if (elem instanceof EClassProxy) {
			return true;
		}
		
		Annotation ann = elem.getAnnotation("egl.lang.reflect.EClassProxy");
		if (ann != null) {
			return true;
		}
		return false;
	}

	public static boolean isReferenceCompatible(Type lhsType, Type rhsType) {
		if (lhsType == null || rhsType == null) {
			return true;
		}
		
		if (lhsType instanceof ArrayType && rhsType instanceof ArrayType) {
			// Arrays must match exactly, including nullability.
			ArrayType array1 = (ArrayType)lhsType;
			ArrayType array2 = (ArrayType)rhsType;
			if (array1.elementsNullable() != array2.elementsNullable()) {
				return false;
			}
			
			Type elem1 = array1.getElementType();
			Type elem2 = array2.getElementType();
			while (elem1 instanceof ArrayType && elem2 instanceof ArrayType) {
				if (((ArrayType)elem1).elementsNullable() != ((ArrayType)elem2).elementsNullable()) {
					return false;
				}
				elem1 = ((ArrayType)elem1).getElementType();
				elem2 = ((ArrayType)elem2).getElementType();
			}
			return elem1.equals(elem2);
		}
		
		boolean isReferenceType = TypeUtils.isReferenceType(lhsType);
		if (isReferenceType != TypeUtils.isReferenceType(rhsType)) {
			return false;
		}
		
		// For value types they must match exactly, or rhsType must be a subtype of lhsType.
		if (!isReferenceType) {
			if (lhsType.equals(rhsType)) {
				return true;
			}
			
			if (rhsType.getClassifier() instanceof EGLClass && lhsType.getClassifier() instanceof StructPart
					&& ((EGLClass)rhsType.getClassifier()).isSubtypeOf((StructPart)lhsType.getClassifier())) {
				return true;
			}
			
			return false;
		}
		
		// For reference types they must be in the same hierarchy.
		return TypeUtils.areCompatible(lhsType.getClassifier(), rhsType.getClassifier());
	}
	
	public static boolean isMoveCompatible(Type targetType, Member targetMember, Type sourceType, Expression source) {
		if (source instanceof ArrayLiteral) {
			Type elementType = targetType;
			if (targetType instanceof ArrayType) {
				elementType = ((ArrayType)targetType).getElementType();
			}
			for (Expression e : ((ArrayLiteral)source).getExpressions()) {
				Type exprType = resolveGenericType(e.resolveType(), e);
				if (targetType instanceof ArrayType && TypeUtils.Type_NULLTYPE.equals(exprType) && ((ArrayType)targetType).elementsNullable()) {
					// Allow null entries in the literal.
				}
				else if (!IRUtils.isMoveCompatible(elementType, targetMember, exprType, e.resolveMember())) {
					return false;
				}
			}
			return true;
		}
		return IRUtils.isMoveCompatible(targetType, targetMember, sourceType, source.resolveMember());
	}
}
