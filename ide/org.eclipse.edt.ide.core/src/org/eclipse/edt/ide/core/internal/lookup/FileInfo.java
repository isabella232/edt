/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;


/**
 * @author svihovec
 *
 */
public class FileInfo implements IFileInfo {

	private class FileInfoEntry{
		private int partType;
		private String caseSensitivePartName;
		private ISourceRange sourceRange;
		private byte[] md5Key;
		
		public FileInfoEntry(int partType, ISourceRange sourceRange, String caseSensitivePartName, byte[] md5Key){
			this.partType = partType;
			this.sourceRange = sourceRange;
			this.caseSensitivePartName = caseSensitivePartName;
			this.md5Key = md5Key;
		}
		
		public int getPartType() {
			return partType;
		}
		
		public ISourceRange getSourceRange() {
			return sourceRange;
		}
		
		public String getCaseSensitivePartName(){
			return caseSensitivePartName;
		}
		
		public byte[] getMD5Key(){
			return md5Key;
		}
	}
	
	private LinkedHashMap partEntries = new LinkedHashMap();
	private int[] lineOffsets = null;
	
	public void addPart(String partName, int partType, int sourceStart, int sourceLength, String caseSensitivePartName, byte[] md5Key){
		partEntries.put(partName, new FileInfoEntry(partType, new SourceRange(sourceStart, sourceLength), caseSensitivePartName, md5Key));
	}
	
	public void setLineOffsets(int[] offsets) {
		this.lineOffsets = offsets;		
	}
	
	public ISourceRange getPartRange(String partName) {
		return ((FileInfoEntry)partEntries.get(partName)).getSourceRange();
	}
	
	public int getPartType(String partName) {
		return ((FileInfoEntry)partEntries.get(partName)).getPartType();
	}
	
	public String getCaseSensitivePartName(String partName){
		return ((FileInfoEntry)partEntries.get(partName)).getCaseSensitivePartName();
	}
	
	public byte[] getMD5Key(String partName){
		return ((FileInfoEntry)partEntries.get(partName)).getMD5Key();
	}

	public Set getPartNames() {
		return Collections.unmodifiableSet(partEntries.keySet());
	}

	public int getLineNumberForOffset(int offset){
		
		int index = Arrays.binarySearch(lineOffsets, offset);
		
		if(index >=0){
			return index;
		}else{
			return -(index) - 2;
		}
	}
	
	public int getNumberOfLines(){
		return lineOffsets.length;
	}
	
	public int getOffsetForLine(int line){
		return lineOffsets[line];
	}
}
