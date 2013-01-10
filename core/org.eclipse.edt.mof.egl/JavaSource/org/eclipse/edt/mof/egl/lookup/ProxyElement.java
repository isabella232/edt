/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.lookup;

import java.util.List;

import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.ProxyEObject;


public class ProxyElement extends ProxyEObject implements Element, Member {

	@Override
	public void addAnnotation(Annotation ann) {
		// TODO Auto-generated method stub

	}

	@Override
	public Annotation getAnnotation(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Annotation> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toStringHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessKind getAccessKind() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Container getContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isAbstract() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isStatic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAccessKind(AccessKind value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContainer(Container value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIsAbstract(Boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIsStatic(Boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIsNullable(boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setType(Type value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation getAnnotation(AnnotationType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAnnotation(Annotation ann) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCaseSensitiveName() {
		return getName();
	}

	@Override
	public Element resolveElement() {
		return this;
	}

	@Override
	public Member resolveMember() {
		return this;
	}

}
