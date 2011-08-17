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
package org.eclipse.edt.ide.ui.internal.deployment;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>System IProtocol</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol#getBinddir <em>Binddir</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol#getLibrary <em>Library</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSystemIProtocol()
 * @model extendedMetaData="name='System-iProtocol' kind='empty'"
 * @generated
 */
public interface SystemIProtocol extends Protocol {
	/**
	 * Returns the value of the '<em><b>Binddir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Binddir</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Binddir</em>' attribute.
	 * @see #setBinddir(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSystemIProtocol_Binddir()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='binddir'"
	 * @generated
	 */
	String getBinddir();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol#getBinddir <em>Binddir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Binddir</em>' attribute.
	 * @see #getBinddir()
	 * @generated
	 */
	void setBinddir(String value);

	/**
	 * Returns the value of the '<em><b>Library</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Library</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Library</em>' attribute.
	 * @see #setLibrary(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSystemIProtocol_Library()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='library'"
	 * @generated
	 */
	String getLibrary();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol#getLibrary <em>Library</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Library</em>' attribute.
	 * @see #getLibrary()
	 * @generated
	 */
	void setLibrary(String value);

} // SystemIProtocol
