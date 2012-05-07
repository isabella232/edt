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
import org.eclipse.edt.mof.egl.DataItem;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.ProgramParameter;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
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
				packageName = obj.getEClass().getPackageName().toCharArray();
				requestor.enterEGLFile();
				if(packageName != null) {
					requestor.acceptPackage(0, 0, packageName);
				}
				
				if (obj instanceof Part) {
					part = (Part)obj;
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
		
		//for locating, which may be useful in later version
		Annotation annotation = partElement.getAnnotation(IEGLConstants.EGL_LOCATION);
		
		if (annotation != null) {
			int startOffset = 0;
			int length = 0;
			
			if (annotation.getValue(IEGLConstants.EGL_PARTOFFSET) != null)
				startOffset = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTOFFSET)).intValue();
			if (annotation.getValue(IEGLConstants.EGL_PARTLENGTH) != null)
				length = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLENGTH)).intValue();
			
			partInfo.nameStart = startOffset;
			partInfo.nameEnd = startOffset + length;
		}
		
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
	
	
	public class PartVisitor extends AbstractVisitor {
		private int partType;
		private PartInfoHelper partInfo;
		
		PartVisitor(PartInfoHelper partInfo, Part part) {
			disallowRevisit();
			this.partInfo = partInfo;
			part.accept(this);
		}
		
		/*public boolean visit(Function function) {
			this.partType = IRPartType.PART_FUNCTION;
			
			function.getParameters();
		}*/
		
		public boolean visit(Library library) {
			this.partType = IRPartType.PART_LIBRARY;
			
			List<Part> usedParts = library.getUsedParts();
			if(usedParts != null && usedParts.size() > 0) {
				char[][] usagePartTypes = new char[usedParts.size()][];
				char[][] usagePartPackages = new char[usedParts.size()][];
				
				int i = 0;
				for(Part usedPart : usedParts) {
					usagePartTypes[i] = usedPart.getName().toCharArray();
					usagePartPackages[i] = usedPart.getPackageName().toCharArray();
					i++;
				}
				
				partInfo.usagePartTypes = usagePartTypes;
				partInfo.usagePartPackages = usagePartPackages;
			}
			
			visitPart(partType , partInfo, library);
			return true;
		}
		
		//TODO
		public boolean visit(Program program) {
			this.partType = IRPartType.PART_PROGRAM;
			
			List<Part> usedParts = program.getUsedParts();
			if(usedParts != null && usedParts.size() > 0) {
				char[][] usagePartTypes = new char[usedParts.size()][];
				char[][] usagePartPackages = new char[usedParts.size()][];
				
				int i = 0;
				for(Part usedPart : usedParts) {
					usagePartTypes[i] = usedPart.getName().toCharArray();
					usagePartPackages[i] = usedPart.getPackageName().toCharArray();
					i++;
				}
				
				partInfo.usagePartTypes = usagePartTypes;
				partInfo.usagePartPackages = usagePartPackages;
			}
			
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
			this.partType = IRPartType.PART_RECORD;
			visitPart(partType , partInfo, partElement);
			return true;
		}
		
		public boolean visit(Service service) {
			this.partType = IRPartType.PART_SERVICE;
			
			List<Part> usedParts = service.getUsedParts();
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
}
