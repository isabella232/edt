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
package org.eclipse.edt.mof.egl.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.utils.EList;

public class StructPartImpl extends PartImpl implements StructPart {
	private static int Slot_superTypes=0;
	private static int Slot_interfaces=1;
	private static int Slot_structuredFields=2;
	private static int Slot_constructors=3;
	private static int Slot_functions=4;
	private static int Slot_operations=5;
	private static int Slot_initializerStatements=6;
	private static int totalSlots = 7;
	
	public static int totalSlots() {
		return totalSlots + PartImpl.totalSlots();
	}
	
	static {
		int offset = PartImpl.totalSlots();
		Slot_superTypes += offset;
		Slot_interfaces += offset;
		Slot_structuredFields += offset;
		Slot_constructors += offset;
		Slot_functions += offset;
		Slot_operations += offset;
		Slot_initializerStatements += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<StructPart> getSuperTypes() {
		return (List<StructPart>)slotGet(Slot_superTypes);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Interface> getInterfaces() {
		return (List<Interface>)slotGet(Slot_interfaces);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<StructuredField> getStructuredFields() {
		return (List<StructuredField>)slotGet(Slot_structuredFields);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Constructor> getConstructors() {
		return (List<Constructor>)slotGet(Slot_constructors);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Function> getFunctions() {
		return (List<Function>)slotGet(Slot_functions);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Operation> getOperations() {
		return (List<Operation>)slotGet(Slot_operations);
	}
	
	@Override
	public List<StructuredField> getStructuredFields(String name) {
		List<StructuredField> list = new EList<StructuredField>();
		for (StructuredField field : getStructuredFields()) {
			if (field.getName().equalsIgnoreCase(name)) {
				list.add(field);
			}
		}
		return list;
	}
	
	@Override
	public List<Function> getFunctions(String name) {
		List<Function> list = new EList<Function>();
		for (Function f : getFunctions()) {
			if (f.getName().equalsIgnoreCase(name)) {
				list.add(f);
			}
		}
		return list;
	}
	@Override
	public List<Operation> getOperations(String name) {
		List<Operation> list = new EList<Operation>();
		for (Operation f : getOperations()) {
			if (f.getName().equalsIgnoreCase(name)) {
				list.add(f);
			}
		}
		return list;
	}
	
	@Override
	public Function getFunction(String name) {
		for (Function function : getFunctions()) {
			if (function.getName().equalsIgnoreCase(name)) {
				return function;
			}
		}
		return null;
	}
	
	@Override
	public void addMember(Member mbr) {
		if (mbr instanceof StructuredField) {
			getStructuredFields().add((StructuredField)mbr);
		}
		else if (mbr instanceof Operation) {
			getOperations().add((Operation)mbr);
		}
		else if (mbr instanceof Function) {
			getFunctions().add((Function)mbr);
		}
		else if (mbr instanceof Constructor) {
			getConstructors().add((Constructor)mbr);
		}
		else {
			throw new IllegalArgumentException(mbr.toString() + " is not a valid Member for Type: " + this.getFullyQualifiedName());
		}
		mbr.setContainer(this);
	}

	@Override
	public List<Member> getMembers() {
		List<Member> mbrs = new ArrayList<Member>();
		mbrs.addAll(getStructuredFields());
		mbrs.addAll(getFunctions());
		mbrs.addAll(getOperations());
		return mbrs;
	}
	@Override
	public boolean isSubtypeOf(StructPart part) {
		if (!getSuperTypes().isEmpty()) {
			for (StructPart superType : getSuperTypes()) {
				if (superType.equals(part)) {
					return true;
				}
			}
			for (StructPart superType : getSuperTypes()) {
				if (superType.isSubtypeOf(part)) return true;
			}
			return false;
		}
		else {
			return false;
		}
	}

	@Override
	public List<Member> getAllMembers() {
		List<Member> mbrs = new ArrayList<Member>();
		collectMembers(mbrs, new ArrayList<StructPart>());
		return mbrs;
	}
	
	@Override
	public void collectMembers(List<Member> mbrs, List<StructPart> alreadySeen) {
		if (alreadySeen.contains(this)) {
			return;
		}
		
		alreadySeen.add(this);
		mbrs.addAll(getMembers());
		if (!getSuperTypes().isEmpty()) {
			for(StructPart superType : getSuperTypes()) {
				superType.collectMembers(mbrs, alreadySeen);
			}
		}

	}
	
	@Override
	public StatementBlock getInitializerStatements() {
		return (StatementBlock)slotGet(Slot_initializerStatements);
	}   
	
	@Override
	public void setInitializerStatements(StatementBlock value) {
		slotSet(Slot_initializerStatements, value);
	}

}
