/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package eglx.services;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import eglx.services.ServiceKind;
import egl.lang.AnyException;
import org.eclipse.edt.runtime.java.egl.lang.EString;
import java.lang.String;
public class ServiceInvocationException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public ServiceKind source;
	@javax.xml.bind.annotation.XmlTransient
	public String detail1;
	@javax.xml.bind.annotation.XmlTransient
	public String detail2;
	@javax.xml.bind.annotation.XmlTransient
	public String detail3;
	public ServiceInvocationException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((ServiceInvocationException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.source = ((ServiceInvocationException) source).source;
		this.detail1 = ((ServiceInvocationException) source).detail1;
		this.detail2 = ((ServiceInvocationException) source).detail2;
		this.detail3 = ((ServiceInvocationException) source).detail3;
	}
	public ServiceInvocationException ezeNewValue(Object... args) {
		return new ServiceInvocationException();
	}
	public void ezeSetEmpty() {
		source = null;
		detail1 = Constants.EMPTY_STRING;
		detail2 = Constants.EMPTY_STRING;
		detail3 = Constants.EMPTY_STRING;
	}
	public boolean isVariableDataLength() {
		return false;
	}
	public void loadFromBuffer(ByteStorage buffer, Program program) {
	}
	public int sizeInBytes() {
		return 0;
	}
	public void storeInBuffer(ByteStorage buffer) {
	}
	public void ezeInitialize() {
		source = null;
		detail1 = Constants.EMPTY_STRING;
		detail2 = Constants.EMPTY_STRING;
		detail3 = Constants.EMPTY_STRING;
	}
	@org.eclipse.edt.javart.json.Json(name="source", clazz=ServiceKind.class, asOptions={})
	public ServiceKind getSource() {
		return (source);
	}
	public void setSource( ServiceKind ezeValue ) {
		this.source = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="detail1", clazz=EString.class, asOptions={})
	public String getDetail1() {
		return (detail1);
	}
	public void setDetail1( String ezeValue ) {
		this.detail1 = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="detail2", clazz=EString.class, asOptions={})
	public String getDetail2() {
		return (detail2);
	}
	public void setDetail2( String ezeValue ) {
		this.detail2 = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="detail3", clazz=EString.class, asOptions={})
	public String getDetail3() {
		return (detail3);
	}
	public void setDetail3( String ezeValue ) {
		this.detail3 = ezeValue;
	}
}
