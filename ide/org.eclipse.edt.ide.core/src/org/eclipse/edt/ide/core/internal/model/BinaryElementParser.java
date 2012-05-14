/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.model.index.IDocument;
import org.eclipse.edt.ide.core.model.Flags;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.DataItem;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.ProgramParameter;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.impl.AbstractVisitor;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Deserializer;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.SerializationFactory;
import org.eclipse.edt.mof.serialization.xml.XMLSerializationFactory;

public class BinaryElementParser {
	private char[] packageName;
	private ISourceElementRequestor requestor;
	private IProject project;
	private boolean reportReferencesInStatements;
	
	public BinaryElementParser(ISourceElementRequestor requestor,IProject project) {
		this.requestor = requestor;
		this.project = project;
	}

	public void parseDocument(IDocument file, boolean reportReferencesInStatements) {
		this.reportReferencesInStatements = reportReferencesInStatements;
		byte[] contents;
		try {
			contents = file.getByteContent();
			index(contents);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param IRContents
	 */
	private void index(byte[] IRContents) {
		SerializationFactory factory = new XMLSerializationFactory();
		ByteArrayInputStream input = new ByteArrayInputStream(IRContents);
		
		ProjectEnvironment env = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
		try {
			Environment.pushEnv(env.getIREnvironment());
			env.initIREnvironments();
			
			Deserializer deserializer = factory.createDeserializer(input, env.getIREnvironment());
			EObject obj;
			Part part;
			
			try {
				obj = deserializer.deserialize();
				
				if (obj instanceof Part) {
					part = (Part)obj;
					String fullyQualifiedName = part.getFullyQualifiedName();
					if (fullyQualifiedName.indexOf('.') >= 0)
						fullyQualifiedName = fullyQualifiedName.substring(0,fullyQualifiedName.lastIndexOf('.'));
					packageName = fullyQualifiedName.toCharArray();
					requestor.enterEGLFile();
					if(packageName != null) {
						requestor.acceptPackage(0, 0, packageName);
					}
					
					handleEnterPart(part);
				}
			}catch(DeserializationException ex) {
				ex.printStackTrace();
			}
		}
		finally {
			Environment.popEnv();
		}
	}
	
	private void handleEnterPart(final Part partElement) {
		final PartInfoHelper partInfo = new PartInfoHelper();
		
		ElementLocation location = getElementLocation(partElement);
		partInfo.nameStart = location.elementOffSet;
		partInfo.nameEnd = location.elementLen;
		
		partInfo.name = partElement.getName().toCharArray();
		partInfo.modifier = (partElement.getAccessKind() == AccessKind.ACC_PRIVATE) ? Flags.AccPrivate : Flags.AccPublic;
		
		new PartVisitor(partInfo, partElement);
	}
	
	private void visitPart(int partType, PartInfoHelper pInfo, Part partElement) {
		String subTypeName = "";
		if(partElement instanceof DataItem){
			subTypeName = ((DataItem)partElement).getBaseType().toString();
		} else if(partElement.getSubType() != null){
			subTypeName = partElement.getSubType().getEClass().getName();
		}

		requestor.enterPart(partType, subTypeName.toCharArray(), partElement.hashCode(), 
				0, pInfo.modifier, pInfo.name, pInfo.nameStart, pInfo.nameEnd, pInfo.interfaceNames, 
				pInfo.parameterNames, pInfo.parameterTypes, pInfo.usagePartTypes, pInfo.usagePartPackages, 
				partElement.getFileName());
	}
	
	private ElementLocation getElementLocation(Element element) {
		ElementLocation location;
		Annotation annotation = element.getAnnotation(IEGLConstants.EGL_LOCATION);
		if (annotation != null) {
			int startOffset = 0;
			int length = 0;
			
			if (annotation.getValue(IEGLConstants.EGL_PARTOFFSET) != null)
				startOffset = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTOFFSET)).intValue();
			if (annotation.getValue(IEGLConstants.EGL_PARTLENGTH) != null)
				length = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLENGTH)).intValue();
			
			location = new ElementLocation(startOffset,startOffset + length);
		} else {
			location = new ElementLocation(0,0);
		}
		return location;
	}
	
	public class PartVisitor extends AbstractVisitor {
		private int partType;
		private PartInfoHelper partInfo;
		private Part part;
		
		PartVisitor(PartInfoHelper partInfo, Part part) {
			disallowRevisit();
			this.partInfo = partInfo;
			this.part = part;
			part.accept(this);
		}
		
		public boolean visit(Part part) {
			//do not visit any parts besides the one we are interested in
			return shouldVisit(part);
		}
		
		private boolean shouldVisit(Part part) {
			return this.part == part;
		}
		
		public boolean visit(Annotation annotation){
			//String name = annotation.getAnnotation(type)
			return false;
		}
		
		public void endVisit(Annotation annotation){
			//requestor.exitPropertyBlock(0);
		}
		
		public boolean visit(AnnotationType annoType) {
			if (!shouldVisit(annoType)) {
				return false;
			}
			
			//String name = annoType.getName();
			this.partType = IRPartType.PART_RECORD;
			visitPart(partType , partInfo, annoType);
			return true;
		}
		
		public boolean visit(Delegate delegate) {
			if (!shouldVisit(delegate)) {
				return false;
			}
			this.partType = IRPartType.PART_DELEGATE;
			visitPart(partType, partInfo, delegate);
			
			//handle as a function
			AccessKind accessKind = delegate.getAccessKind();
			boolean isPublic = true;
			if(accessKind != null && accessKind == AccessKind.ACC_PRIVATE) {
				isPublic = false;
			}
			int modifier = isPublic ? Flags.AccPublic : Flags.AccPrivate;
			ElementLocation location = getElementLocation(delegate);
			int declStart = location.elementOffSet;
			int declEnd = location.elementLen; 
			int nameStart = 0;
			int nameEnd = nameStart;
			
			List<FunctionParameter> funPara = delegate.getParameters();
			int funcParaLen = funPara.size();
			char[][] parmNames = new char[funcParaLen][];
			char[][] typeNames = new char[funcParaLen][];
			int[] typeIdentifiers = new int[funcParaLen];
			char[][] useTypes = new char[funcParaLen][];
			char[][] parmPackages = new char[funcParaLen][];
			boolean[] areNullable = new boolean[funcParaLen];
			
            FunctionParameter parameter;
			
			for(int i = 0; i < funcParaLen; i++ ) {
				parameter = funPara.get(i);
				//requestor.acceptPartReference(Util.toCompoundChars(parameter.getType().toString()), paraStart, paraEnd);
				
				parmNames[i] = parameter.getName().toString().toCharArray();
				typeNames[i] = parameter.getType().getTypeSignature().toCharArray();	//if the parameter is an array, typeNames[i] ends with '[]'
				areNullable[i] = parameter.isNullable();
				
				String useType = "";
				ParameterKind paraKind = parameter.getParameterKind();
				if(paraKind == ParameterKind.PARM_IN) {
					useType = "in";
				} else if(paraKind == ParameterKind.PARM_INOUT) {
					useType = "inout";
				} else {
					useType = "out";
				}
				useTypes[i] = useType.toCharArray();
				
				String packageName = null;
				if(parameter.getType() != null && parameter.getType().getClassifier() != null)
					packageName = parameter.getType().getClassifier().getPackageName();
				
				if(packageName != null) {
					parmPackages[i] = packageName.toCharArray();
				} else {
					parmPackages[i] = null;
				}
				typeIdentifiers[i] = ISourceElementRequestor.UNKNOWN_TYPE;
			}//for loop
			
			Type retType = delegate.getReturnType();
			char[] fieldName = "".toCharArray();
			char[] retFieldPkg = null;
			if(retType != null) {
				//requestor.acceptPartReference(Util.toCompoundChars(retField.getFullyQualifiedName()), 0, 0);
				String packageName = retType.getClassifier().getPackageName();
				if(packageName != null) {
					retFieldPkg = packageName.toCharArray();
				} else {
					retFieldPkg = null;
				}
				
				fieldName = retType.getClassifier().getName().toCharArray();
			}
			
			requestor.enterFunction(declStart, modifier, fieldName, retFieldPkg, delegate.getId().toCharArray(),
					nameStart, nameEnd, typeNames, parmNames, useTypes, areNullable, parmPackages);
			
			return true;
		}
		
		public boolean visit(ExternalType externalType) {
			if (!shouldVisit(externalType)) {
				return false;
			}
			
			this.partType = IRPartType.PART_EXTERNALTYPE;
			visitPart(partType, partInfo, externalType);
			return true;
		}
		
		public boolean visit(Function function) {
			this.partType = IRPartType.PART_FUNCTION;
			
			//if it is a referenced function in a program, do not count it
			/*if(function.isTopLevelFunction() && function.getContainer() != null){
				return false;
			}*/
			
			String functionName = function.getName();
			if( functionName.equalsIgnoreCase("<init>") ) {//no implicit functon in EDT
				return false;
			}
			
			ElementLocation location = getElementLocation(function);
			int declStart = location.elementOffSet;
			int declEnd = location.elementLen; 
			
			AccessKind accessKind = function.getAccessKind();
			boolean isPublic = true;
			if(accessKind != null && accessKind == AccessKind.ACC_PRIVATE) {
				isPublic = false;
			}
			int modifier = isPublic ? Flags.AccPublic : Flags.AccPrivate;
			int nameStart = 0;
			int nameEnd = nameStart;
			
			List<FunctionParameter> funPara = function.getParameters();
			int funcParaLen = funPara.size();
			char[][] parmNames = new char[funcParaLen][];
			char[][] typeNames = new char[funcParaLen][];
			int[] typeIdentifiers = new int[funcParaLen];
			char[][] useTypes = new char[funcParaLen][];
			char[][] parmPackages = new char[funcParaLen][];
			boolean[] areNullable = new boolean[funcParaLen];
			
			FunctionParameter parameter;
			
			for(int i = 0; i < funcParaLen; i++ ) {
				parameter = funPara.get(i);
				//requestor.acceptPartReference(Util.toCompoundChars(parameter.getType().toString()), paraStart, paraEnd);
				
				parmNames[i] = parameter.getName().toString().toCharArray();
				typeNames[i] = parameter.getType().getTypeSignature().toCharArray();	//if the parameter is an array, typeNames[i] ends with '[]'
				areNullable[i] = parameter.isNullable();
				
				String useType = "";
				ParameterKind paraKind = parameter.getParameterKind();
				if(paraKind == ParameterKind.PARM_IN) {
					useType = "in";
				} else if(paraKind == ParameterKind.PARM_INOUT) {
					useType = "inout";
				} else {
					useType = "out";
				}
				useTypes[i] = useType.toCharArray();
				
				String packageName = null;
				if(parameter.getType() != null && parameter.getType().getClassifier() != null)
					packageName = parameter.getType().getClassifier().getPackageName();
				
				if(packageName != null) {
					parmPackages[i] = packageName.toCharArray();
				} else {
					parmPackages[i] = null;
				}
				typeIdentifiers[i] = ISourceElementRequestor.UNKNOWN_TYPE;
			}//for loop
			
			Field retField = function.getReturnField();
			char[] fieldName = "".toCharArray();
			char[] retFieldPkg = null;
			if(retField != null) {
				//requestor.acceptPartReference(Util.toCompoundChars(retField.getFullyQualifiedName()), 0, 0);
				String packageName = retField.getType().getClassifier().getPackageName();
				if(packageName != null) {
					retFieldPkg = packageName.toCharArray();
				} else {
					retFieldPkg = null;
				}
				
				fieldName = retField.getName().toCharArray();
			}
			
			requestor.enterFunction(declStart, modifier, fieldName, retFieldPkg, function.getId().toCharArray(),
					nameStart, nameEnd, typeNames, parmNames, useTypes, areNullable, parmPackages);
			return true;
		}
		
		public void endVisit(Function function) {
			/*if(function.isTopLevelFunction() && function.getContainer() != null){
				return;
			}*/
			
			String functionName = function.getName();
			if(functionName.toString().equalsIgnoreCase("<init>") ) {
				return;
			}
			requestor.exitFunction(0);
		}
		
		public boolean visit(FunctionParameter parameter) {
			ElementLocation location = getElementLocation(parameter);
			int declStart = location.elementOffSet;
			int declEnd = location.elementLen; 
			
			String fullyQualifiedName = parameter.getName() + " " + parameter.getType().getClassifier().getId();
			requestor.acceptPartReference(Util.toCompoundChars(fullyQualifiedName), declStart, declEnd);
			return true;
		}
		
		public boolean visit(FunctionPart functionPart) {
			if (!shouldVisit(functionPart)) {
				return false;
			}

			visitPart(partType, partInfo, functionPart);
			return true;
		}
		
		public boolean visit(Handler handler) {
			if (!shouldVisit(handler)) {
				return false;
			}
			this.partType = IRPartType.PART_HANDLER;
			updatePartInfo(handler);
			
			visitPart(partType, partInfo, handler);
			return true;
		}
		
		public boolean visit(Interface itf) {
			if (!shouldVisit(itf)) {
				return false;
			}
			this.partType = IRPartType.PART_INTERFACE;
			visitPart(partType , partInfo, itf);
			return true;
		}
		
		public boolean visit(Library library) {
			if (!shouldVisit(library)) {
				return false;
			}

			this.partType = IRPartType.PART_LIBRARY;
			updatePartInfo(library);
			
			visitPart(partType , partInfo, library);
			return true;
		}
		
		public boolean visit(Program program) {
			if (!shouldVisit(program)) {
				return false;
			}
			this.partType = IRPartType.PART_PROGRAM;
			updatePartInfo(program);
			
			if(program.isCallable()){
				int paramLength = program.getParameters().size();
				if(paramLength > 0){
					char[][] paramNames = new char[paramLength][];
					char[][] paramTypes = new char[paramLength][];
					List<ProgramParameter> params = program.getParameters();
					for(int i = 0; i < paramLength; i++ ) {
						paramNames[i] = params.get(i).getId().toCharArray();
						paramTypes[i] = params.get(i).getType().toString().toCharArray();
					}
					partInfo.parameterNames = paramNames;
					partInfo.parameterTypes = paramTypes;
				}
			}
			
			visitPart(partType , partInfo, program);
			return true;
		}
		
		public boolean visit(Record partElement) {
			if (!shouldVisit(partElement)) {
				return false;
			}
			this.partType = IRPartType.PART_RECORD;
			visitPart(partType , partInfo, partElement);
			return true;
		}
		
		public boolean visit(Service service) {
			if (!shouldVisit(service)) {
				return false;
			}
			this.partType = IRPartType.PART_SERVICE;
			updatePartInfo(service);
			
			List<Interface> implementedInterfaceNames = service.getInterfaces();
			int implementedInterfacesLength = implementedInterfaceNames.size();
			if (implementedInterfacesLength > 0) {
				partInfo.interfaceNames = new char[implementedInterfacesLength][];
				for (int i = 0; i < implementedInterfacesLength; i++) {
					partInfo.interfaceNames[i] = implementedInterfaceNames.get(i).getFullyQualifiedName().toCharArray();
				}
			}
			
			visitPart(partType, partInfo, service);
			return true;
		}
		
		
		private void updatePartInfo(LogicAndDataPart part) {
			List<Part> usedParts = part.getUsedParts();
			if(usedParts != null && usedParts.size() > 0) {
				char[][] usagePartTypes = new char[usedParts.size()][];
				char[][] usagePartPackages = new char[usedParts.size()][];
				
				for(int i = 0; i < usedParts.size(); i++ ) {
					usagePartTypes[i] = usedParts.get(i).getName().toCharArray();
					usagePartPackages[i] = usedParts.get(i).getPackageName().toCharArray();
				}
				
				partInfo.usagePartTypes = usagePartTypes;
				partInfo.usagePartPackages = usagePartPackages;
			}
		}
	}
	
	private class PartInfoHelper {
		char[] name;
		int nameStart;
		int nameEnd;
		int modifier;
		char[][] interfaceNames = null;
		
		char[][] parameterNames = null;
		char[][] parameterTypes = null;
		char[][] usagePartTypes = null;
		char[][] usagePartPackages = null;
	}
	
	private class ElementLocation {
		int elementOffSet;
		int elementLen;
		
		public ElementLocation(int offset,int len) {
			this.elementOffSet = offset;
			this.elementLen = len;
		}
	}
}
