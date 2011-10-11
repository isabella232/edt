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
package org.eclipse.edt.gen.deployment.util;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.TypeName;
import org.eclipse.edt.mof.impl.AbstractVisitor;

public class PartReferenceCache {
	private Map<String,Set<Part>> cache;

	public PartReferenceCache(){
		cache = new HashMap<String, Set<Part>>();
	}

	public Set<Part> getReferencedPartsFor(Part part, Context ctx) {
		
		Set<Part> parts = cache.get(part.getFullyQualifiedName());
		if(parts == null){
			parts = new LinkedHashSet<Part>();
			getReferencedParts(part, ctx, parts);
			parts.remove(parts);
			cache.put(part.getFullyQualifiedName(), parts);
		}
		return parts;
	}
	
	private void getReferencedParts(Part part, Context ctx, Set<Part> allParts){
		if(allParts.contains(part)){
			if(part instanceof ExternalType){
				allParts.remove(part);
			}else{
				return;
			}
		}
		allParts.add(part);
		for(Part refPart: (new PartsReferencedResolver()).getReferencedPartsFor(part, ctx)){
			getReferencedParts(refPart, ctx, allParts);
		}
	}
	
	public class PartsReferencedResolver extends AbstractVisitor {
		Part root;
		Set<Part> referencedParts;
		Context ctx;
		
		PartsReferencedResolver() {
			disallowRevisit();
			referencedParts = new LinkedHashSet<Part>();
		}
		
		public boolean visit(Part part) {
			
			if(! CommonUtilities.isUserClass(part,ctx)){
				return false;
			}else{
				if (part != root) {
					referencedParts.add(part);
					return false;
				}
				return true;
			}
		}	

		public boolean visit(TypeName name) {
			name.getType().accept(this);
			return false;
		}
		
		public boolean visit(NewExpression newExpr) {
			newExpr.getType().accept(this);
			return true;
		}

		
		public Set<Part> getReferencedPartsFor(Part part, Context ctx) {
			root = part;
			this.ctx = ctx;
			root.accept(this);
			return referencedParts;
		}
	}	
	

//	public Set<Part> getReferencedPartsFor(Part part, Context ctx) {
//	Set<Part> parts = cache.get(part.getFullyQualifiedName());
//	if(parts == null){
//		parts = (new PartsReferencedResolver()).getReferencedPartsFor(part, ctx);
//		cache.put(part.getFullyQualifiedName(), parts);
//	}
//	return parts;
//}
//
//	public class PartsReferencedResolver extends AbstractVisitor {
//		Part root;
//		Set<Part> referencedParts;
//		Context ctx;
//		
//		PartsReferencedResolver() {
//			disallowRevisit();
//			referencedParts = new LinkedHashSet<Part>();
//		}
//		
//		public boolean visit(Part part) {
//			
//			if(! CommonUtilities.isUserClass(part,ctx)){
//				return false;
//			}else{
//				if (part != root) {
//					appendToReferencedParts(part);
//					
//					//add all super types of the part to the end of the list
//					if(part instanceof ExternalType){
//						addSuperTypes((ExternalType)part);
//					}
//				}
//				return true;
//			}
//		}	
//
//		private void addSuperTypes(ExternalType part){
//			for(Part superPart:part.getSuperTypes()){
//				if(superPart instanceof ExternalType && !CommonUtilities.isUserClass(superPart,ctx)){
//					appendToReferencedParts(superPart);
//					addSuperTypes((ExternalType)superPart);
//				}
//			}
//		}
//		
//		private void appendToReferencedParts(Part part){
//			//add to the end of the list
//			if(referencedParts.contains(part)){
//				referencedParts.remove(part);
//			}
//			referencedParts.add(part);
//
//		}
//		
//		public Set<Part> getReferencedPartsFor(Part part, Context ctx) {
//			root = part;
//			this.ctx = ctx;
//			root.accept(this);
//			for (Part p : referencedParts) {
//				System.err.println("find reference - "+p.getFullyQualifiedName());
//				
//			}
//			return referencedParts;
//		}
//		
//	}
	
}
