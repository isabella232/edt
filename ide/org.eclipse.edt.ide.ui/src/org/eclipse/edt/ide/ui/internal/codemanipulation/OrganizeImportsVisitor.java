/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.codemanipulation;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.ReturningToNameClause;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.eclipse.edt.ide.ui.internal.codemanipulation.OrganizeImportsOperation.OrganizedImportSection;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class OrganizeImportsVisitor extends AbstractASTExpressionVisitor{
	private Name currentPartName = null;
	private OrganizedImportSection resolvedTypes;
	private Map /*<String>, <Name>*/unresolvedTypes;
	Set /*<ImportDeclaration>*/ originalImports;
//	private Boolean fIsIncludeRefFunc;
	private IProject project;
	private IEnvironment env;

	public OrganizeImportsVisitor(OrganizedImportSection resolvedTypes, Map unresolvedTypes, Set /*<ImportDeclaration>*/ oldImports, Boolean isIncludeRefFunc, IProject project) {
		super(); 
		this.resolvedTypes = resolvedTypes;
		this.unresolvedTypes = unresolvedTypes;
		this.originalImports = oldImports;
//		this.fIsIncludeRefFunc = isIncludeRefFunc;
		this.project = project;
		this.env = SystemEnvironmentManager.findSystemEnvironment(project, null).getIREnvironment();
	}

	public void setCurrentPartName(Name partName)
	{
		currentPartName = partName;
	}
	
	public boolean visitExpression(Expression expression) {
		if (expression.isName())
			addUnresolvedName((Name)expression);
		return true;
	}

	public boolean visit(CallStatement callStatement) {
		Expression invocationTarget = callStatement.getInvocationTarget();
		if(!invocationTarget.isName()) {
			Type binding = invocationTarget.resolveType();
			if(binding != null) {
				addToResovledTypes(binding);
			}
		}
		else
			addUnresolvedName((Name)invocationTarget);
			
		return true;
	}
	
	public boolean visit(AnnotationExpression annotationExpression) {
		Name annotationName = annotationExpression.getName();
		// Defect 55539 - Don't add system types to unresolved list
		// Defect 61502 - Add user-defined annotations like MVC to unresolved list
		// sysPartBinding is a FlexibleRecordBinding for system annotations
		// sysPartBinding is a NotFoundBinding for user-defined annotations
		IPartBinding sysPartBinding = SystemEnvironmentManager.findSystemEnvironment(project, null).getPartBinding(null, annotationName.getIdentifier());
		if(sysPartBinding == null || !sysPartBinding.isValid()) {
			addUnresolvedName(annotationName);
		}
		return true;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		settingsBlock.accept(new AbstractASTVisitor(){
			public boolean visit(Assignment assignment) {
				Expression lhsExp = assignment.getLeftHandSide();
				visitExpression(lhsExp);
				return true;
			}
		});
		return true;
	}
	
	public boolean visit(Service service) {
		handlePart(service);
        for(Iterator iter = service.getImplementedInterfaces().iterator(); iter.hasNext();) {
    		Name nextName = (Name) iter.next();
    		addUnresolvedName(nextName);
        }
        return true;
	}
	
	public boolean visit(ExternalType externalType) {
		handlePart(externalType);
		for(Iterator iter = externalType.getExtendedTypes().iterator(); iter.hasNext();) {			
			Name nextName = (Name) iter.next();
    		addUnresolvedName(nextName);
    	}
		return true;
	}
	
	public boolean visit(Record record) {
		handlePart(record);
		return true;
	}
	
	public boolean visit(Handler handler) {
		handlePart(handler);
		return true;
	}
	
	public boolean visit(EGLClass eglClass) {
		handlePart(eglClass);
		return true;
	}
	
	public boolean visit(Interface interfaceNode) {
		handlePart(interfaceNode);
		return true;
	}
	
	public boolean visit(Library library) {
		handlePart(library);
		return true;
	}
	
	public boolean visit(Program program) {
		handlePart(program);
		return true;
	}
	
	private void handlePart(Part part) {
		Name subType = part.getSubType();
		if(subType != null) {
			addUnresolvedName(subType);
		}
	}

	public boolean visit(UseStatement useStatement) {
        for (Iterator iter = useStatement.getNames().iterator(); iter.hasNext();) 
        {
            Name nextName = (Name) iter.next();
            addUnresolvedName(nextName);
        }		
		return true;
	}

	public boolean visit(ReturningToNameClause returningToNameClause) {
		addUnresolvedName(returningToNameClause.getName());
		return true;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.FunctionInvocation functionInvocation) {
		Expression expr = functionInvocation.getTarget();
		expr.accept(this);
		// Defect RATLC01531980/49410 - Add imports for top-level functions
		expr.accept(new AbstractASTExpressionVisitor(){
			public boolean visitExpression(Expression expr) {
				if (expr.isName() && ((Name)expr).isSimpleName())
					addUnresolvedName((Name)expr);
				return false;
			}
		});
		return true;
	}
	
	private void addUnresolvedName(Name nameNode)
	{
		Type binding = nameNode.resolveType();
		addToUnresolvedTypes(nameNode, binding);
	}

	private void addToUnresolvedTypes(Name nameNode, Type binding) {
		if (binding == null){
			addToUnresolvedTypes(nameNode);
		}
	}
	
	public boolean visit(QualifiedName qualifiedName) {
		Name nameNode = qualifiedName;
		while (nameNode.isQualifiedName())
			nameNode = ((QualifiedName)nameNode).getQualifier();		

		addUnresolvedName(nameNode);
		return true;		
	}
		
	
	public boolean visit(NameType nameType) {		
		Type typeBinding = nameType.resolveType();
		addToUnresolvedTypes(nameType.getName(), typeBinding);		
		return true;
	}

	public boolean visitName(Name name) {
		if(name != currentPartName)			//skip the current bound part
		{
			Type binding = name.resolveType();		
			if(binding != null) 
			{
				addToResovledTypes(binding);
			}
			else		//unrevolved binding
			{
				//addToUnresolvedTypes(name);
			}
		}		
			
		//return super.visitName(name);
		return true;
	}

	private void addToResovledTypes(Type typeBinding) {
		if(typeBinding instanceof org.eclipse.edt.mof.egl.Part)
		{
			org.eclipse.edt.mof.egl.Part classBinding = (org.eclipse.edt.mof.egl.Part) typeBinding;
			String partName = classBinding.getCaseSensitiveName();
			
			String packageName = classBinding.getCaseSensitivePackageName();
			boolean isSysPart;
			if(packageName != null && packageName.length() > 0){
				isSysPart = IRUtils.isSystemPart(packageName + "." + partName, this.env);
			}else{
				isSysPart = IRUtils.isSystemPart(partName, this.env);
			}		
			if(!isSysPart || (isSysPart && isInOriginalImports(packageName, partName))) {	
				resolvedTypes.addImport(packageName, partName);
			}
		}
		else if(typeBinding instanceof AnnotationType){
			AnnotationType annotationTypeBinding = (AnnotationType)typeBinding;
			String partName = annotationTypeBinding.getCaseSensitiveName();			
			String pkgName = annotationTypeBinding.getCaseSensitivePackageName();
			if(pkgName != null) {
				boolean isSysAnnotation;
				if(pkgName.length() > 0){
					isSysAnnotation = IRUtils.isSystemPart(pkgName + "." + partName, this.env);
				}else{
					isSysAnnotation = IRUtils.isSystemPart(partName, this.env);
				}				
				//if it is the system annotation, but it was already in the original imports
				//we want to keep it
				if(!isSysAnnotation || (isSysAnnotation && isInOriginalImports(pkgName, partName))){
					resolvedTypes.addImport(pkgName, partName);
				}
			}
		}
	}
	
	private boolean isInOriginalImports(String pkgName2Test, String partName2Test){
		boolean fndInOriginal = false;
		//check to see if this part is already in the existing import section
		Iterator oldIt = originalImports.iterator();						
		while(oldIt.hasNext() && !fndInOriginal)
		{
			ImportDeclaration oldImportDecl = (ImportDeclaration)(oldIt.next());
			Name importNameNode = oldImportDecl.getName();
			String oldPackageName = "";							 //$NON-NLS-1$
			if(!oldImportDecl.isOnDemand())
			{
				String oldPartName = importNameNode.getIdentifier();
				if(partName2Test.equalsIgnoreCase(oldPartName))
				{					
					if(importNameNode instanceof QualifiedName)
					{
						oldPackageName = ((QualifiedName)importNameNode).getQualifier().getCanonicalName();
					}
					//else if SimpleName, packagename is empty string  
					if(oldPackageName.equalsIgnoreCase(pkgName2Test))
						fndInOriginal = true;
				}
			}
			else  //on demand
			{								
				oldPackageName = importNameNode.getCanonicalName();
				if(pkgName2Test.equalsIgnoreCase(oldPackageName))
				{
					fndInOriginal = true;
				}
			}
		}						
		return fndInOriginal;
	}

	private void addToUnresolvedTypes(Name name) {
		//add to the unresolved type list
		addToMapTypes(name, unresolvedTypes);
	}	
		
	private void addToMapTypes(Name name, Map types){
		String strname = name.getIdentifier();
		if(!types.containsKey(strname)) {
			types.put(strname, name);
		}
	}
}
