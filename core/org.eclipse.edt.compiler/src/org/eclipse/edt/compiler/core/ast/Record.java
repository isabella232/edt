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
package org.eclipse.edt.compiler.core.ast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * Record AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class Record extends Part{

	private Name partSubTypeOpt;

	public Record(Boolean privateAccessModifierOpt, SimpleName name, Name partSubTypeOpt, List structureContents, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, structureContents, startOffset, endOffset);
		
		if(partSubTypeOpt != null) {
			this.partSubTypeOpt = partSubTypeOpt;
			partSubTypeOpt.setParent(this);
		}
	}
	
	public boolean hasSubType() {
		return partSubTypeOpt != null;
	}
	
	public Name getSubType() {
		return partSubTypeOpt;
	}
	
	public List getStructureContents() {
		return contents;
	}
	
	public boolean isFlexible() {
		
		if(partSubTypeOpt != null && partSubTypeOpt.isSimpleName()) {
			String subTypeName = partSubTypeOpt.getIdentifier();

			if(subTypeName == InternUtil.intern("IndexedRecord") ||
			   subTypeName == InternUtil.intern("SerialRecord") ||
			   subTypeName == InternUtil.intern("RelativeRecord") ||
			   subTypeName == InternUtil.intern("MQRecord") ||
			   subTypeName == InternUtil.intern("VGUIRecord") ||
			   subTypeName == InternUtil.intern("DLISegment")) {
				return false;
			}
			
			if(subTypeName == InternUtil.intern("PSBRecord") ||
			   subTypeName == InternUtil.intern("CSVRecord") ||
			   subTypeName == InternUtil.intern("Annotation")) {
				return true;
			}
		}
		
		for(Iterator iter = getContents().iterator(); iter.hasNext();) {
			Object nextContent = iter.next();
			if(nextContent instanceof StructureItem) {
				return !((StructureItem) nextContent).hasLevel(); 
			}
		}
		
		return true;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			if(partSubTypeOpt != null) partSubTypeOpt.accept(visitor);
			acceptChildren(visitor, contents);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Name newPartSubTypeOpt = partSubTypeOpt != null ? (Name)partSubTypeOpt.clone() : null;
		
		return new Record(new Boolean(isPrivate), (SimpleName)name.clone(), newPartSubTypeOpt, cloneContents(), getOffset(), getOffset() + getLength());
	}
	
	public boolean isGeneratable(){
		if(hasSubType()){
			String subTypeName = partSubTypeOpt.getIdentifier();
			if(subTypeName == InternUtil.intern("VGUIRecord") || subTypeName == InternUtil.intern("consoleForm"))
	            return true;
		}
		return false;
	}

	public String getPartTypeName() {
		return IEGLConstants.KEYWORD_RECORD;
	}
	
	public int getPartType() {
		return RECORD;
	}

}
