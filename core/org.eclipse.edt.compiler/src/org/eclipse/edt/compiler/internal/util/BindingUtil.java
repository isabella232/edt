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
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.DataItem;
import org.eclipse.edt.mof.egl.DataTable;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.ExitStatement;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Form;
import org.eclipse.edt.mof.egl.FormGroup;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.OpenUIStatement;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.ShowStatement;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.egl.StructuredRecord;
import org.eclipse.edt.mof.egl.TransferStatement;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.impl.CallStatementImpl;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.utils.NameUtile;

public class BindingUtil {
	
	private static final String INVALID_ANNOTATION = "edt invalid";
	
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
			setValid((Part) obj, false);
			return new IRPartBinding((Part)obj);
		}
		
		//TODO need to be able to handle mof type objects. We will simply create an ExternalType
		//to represent the object
		return null;
	}
	
	public static int getPartTypeConstant(Part part) {
		if (part instanceof Delegate) {
			return ITypeBinding.DELEGATE_BINDING;
		}
		if (part instanceof DataTable) {
			return ITypeBinding.DATATABLE_BINDING;
		}
		if (part instanceof ExternalType) {
			return ITypeBinding.EXTERNALTYPE_BINDING;
		}
		if (part instanceof StructuredRecord) {
			return ITypeBinding.FIXED_RECORD_BINDING;
		}
		if (part instanceof Form) {
			return ITypeBinding.FORM_BINDING;
		}
		if (part instanceof Record || part instanceof AnnotationType) {
			return ITypeBinding.FLEXIBLE_RECORD_BINDING;
		}
		if (part instanceof FormGroup) {
			return ITypeBinding.FORMGROUP_BINDING;
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
	
	public static IPartBinding createPartBinding(int type, String pkgName, String name) {
		Part part;
		switch (type) {
        case ITypeBinding.FILE_BINDING:
        	return new FileBinding(pkgName, name);
    	case ITypeBinding.DELEGATE_BINDING:
    		part = createDelegate(pkgName, name);
    		return createPartBinding(part);
        case ITypeBinding.DATATABLE_BINDING:
        	part = createDataTable(pkgName, name);
            return createPartBinding(part);
        case ITypeBinding.EXTERNALTYPE_BINDING:
        	part = createExternalType(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.FIXED_RECORD_BINDING:
        	part = createStructuredRecord(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.FLEXIBLE_RECORD_BINDING:
        	part = createRecord(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.FORM_BINDING:
        	part = createForm(pkgName, name);
        	return createPartBinding(part);
        case ITypeBinding.FORMGROUP_BINDING:
        	part = createFormGroup(pkgName, name);
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
        	part = createEGLClass(pkgName, name);
        	return createPartBinding(part);
        
        default:
            throw new RuntimeException("Unsupported kind: " + type);
		}		
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

	private static EGLClass createEGLClass(String pkgName, String name) {
		EGLClass eglClass = IrFactory.INSTANCE.createEGLClass();
		eglClass.setName(name);
		eglClass.setPackageName(pkgName);
		return eglClass;		
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

	private static StructuredRecord createStructuredRecord(String pkgName, String name) {
		StructuredRecord rcd = IrFactory.INSTANCE.createStructuredRecord();
		rcd.setName(name);
		rcd.setPackageName(pkgName);
		return rcd;		
	}
	
	private static Form createForm(String pkgName, String name) {
		Form frm = IrFactory.INSTANCE.createForm();
		frm.setName(name);
		frm.setPackageName(pkgName);
		return frm;		
	}

	private static FormGroup createFormGroup(String pkgName, String name) {
		FormGroup fmg = IrFactory.INSTANCE.createFormGroup();
		fmg.setName(name);
		fmg.setPackageName(pkgName);
		return fmg;		
	}

	private static DataTable createDataTable(String pkgName, String name) {
		DataTable dtb = IrFactory.INSTANCE.createDataTable();
		dtb.setName(name);
		dtb.setPackageName(pkgName);
		return dtb;		
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
		
		if (part instanceof Record) {
			return convertRecordToAnnotationType((Record)part);
		}
		
		return null;
	}
	
	public static AnnotationType getAnnotationType(Type type) {
		if (type instanceof AnnotationType) {
			return (AnnotationType) type;
		}
		
		if (type instanceof Record) {
			return convertRecordToAnnotationType((Record)type);
		}
		return null;
	}
	
	private static AnnotationType convertRecordToAnnotationType(Record annRecord) {
		//TODO add logic to convert a record defininition of type Annotation or Stereotype to an AnnotationType
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
		List<ElementKind> targs = annType.getTargets();
		for (ElementKind kind : targs) {
			if (kind == getElementKind(elem)) {
				return true;
			}
		}
		return false;
	}
	
	private static ElementKind getElementKind(Element elem) {
		if (elem instanceof Record) {
			return ElementKind.RecordPart;
		}
		
		if (elem instanceof StructuredRecord) {
			return ElementKind.StructuredRecordPart;
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
		
		if (elem instanceof FormGroup) {
			return ElementKind.FormGroupPart;
		}

		if (elem instanceof Form) {
			return ElementKind.FormPart;
		}

		if (elem instanceof DataTable) {
			return ElementKind.DataTablePart;
		}

		if (elem instanceof DataItem) {
			return ElementKind.DataItemPart;
		}
		
		if (elem instanceof FunctionPart) {
			return ElementKind.FunctionPart;
		}
		
		if (elem instanceof Field) {
			return ElementKind.FieldMbr;
		}
		
		if (elem instanceof StructuredField) {
			return ElementKind.StructuredFieldMbr;
		}
		
		if (elem instanceof Function) {
			return ElementKind.FunctionMbr;
		}

		if (elem instanceof Constructor) {
			return ElementKind.ConstructorMbr;
		}
		
		if (elem instanceof CallStatementImpl) {
			return ElementKind.CallStatement;
		}
		
		if (elem instanceof ExitStatement) {
			return ElementKind.ExitStatement;
		}

		if (elem instanceof ShowStatement) {
			return ElementKind.ShowStatement;
		}
		
		if (elem instanceof TransferStatement) {
			return ElementKind.TransferStatement;
		}

		if (elem instanceof OpenUIStatement) {
			return ElementKind.OpenUIStatement;
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
				}
			}
			if (list.isEmpty()) {
				return null;
			}
			return list;
		}
		return null;
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
		
		return findPart(ppn.getPackageName(), ppn.getPartName());
	}
	
	public static String getUnaliasedTypeName(Type type) {
		Classifier classifier = type.getClassifier();
		if (classifier != null) {
			String pkg = NameUtile.getAsName(classifier.getPackageName());
			String name = NameUtile.getAsName(classifier.getName());
			
			for (Map.Entry<String, PackageAndPartName> entry : aliasedTypesNames.entrySet()) {
				PackageAndPartName value = entry.getValue();
				if (NameUtile.equals(value.getPackageName(), pkg) && NameUtile.equals(value.getPartName(), name)) {
					return entry.getKey();
				}
			}
		}
		
		String sig = type.getTypeSignature();
		int lastDot = sig.lastIndexOf('.');
		if (lastDot == -1) {
			return sig;
		}
		return sig.substring(lastDot + 1);
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
		return !(sp.isSubtypeOf((StructPart)TypeUtils.getType(TypeUtils.Type_AnyValue)));
	}

	public static boolean isExplicitlyDynamicallyAccessible(Type type) {
		//Check the type for an annotation
		return (type != null && type.getClassifier() != null && type.getClassifier().getAnnotation("egl.lang.reflect.Dynamic") != null);
	}

	public static Member createDynamicAccessMember(Type type, String caseSensitiveName) {
		if (isDynamicallyAccessible(type)) {
			Field field = IrFactory.INSTANCE.createField();
			field.setName(caseSensitiveName);			
			field.setType(getEAny());
			return field;
		}
		return null;
	}
	
	public static Member createExplicitDynamicAccessMember(Type type, String caseSensitiveName) {
		if (isExplicitlyDynamicallyAccessible(type)) {
			Field field = IrFactory.INSTANCE.createField();
			field.setName(caseSensitiveName);			
			field.setType(getEAny());
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
	
	public static String getName(Element elem) {
		if (elem instanceof NamedElement) {
			return ((NamedElement)elem).getName();
		}
		return "";
	}
	
	public static boolean isParameterizableType(Type type) {
		return type instanceof ParameterizableType;
	}
	
	public static void setDefaultSupertype(StructPart part, StructPart defaultSuperType) {
		List<StructPart> superTypes = part.getSuperTypes();
		
		//check if there is already a supertype that is not just an interface
		for (StructPart stype : superTypes) {
			if (!(stype instanceof Interface)) {
				return;
			}
		}
		
		//check if there is a default supertype defined on the stereotype
		Stereotype subType = part.getSubType();
		if (subType != null && subType.getEClass() instanceof StereotypeType) {
			StereotypeType clazz = (StereotypeType) subType.getEClass();
			if (clazz.getDefaultSuperType() instanceof  StructPart) {
				superTypes.add(0, (StructPart)clazz.getDefaultSuperType());
				return;
			}
		}
		
		//add the passed supertype to the supertype list
		superTypes.add(0, defaultSuperType);
		
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
}
