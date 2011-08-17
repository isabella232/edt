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
 * A representation of the model object '<em><b>CICSWS Protocol</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol#getTransaction <em>Transaction</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol#getUserID <em>User ID</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getCICSWSProtocol()
 * @model extendedMetaData="name='CICSWSProtocol' kind='empty'"
 * @generated
 */
public interface CICSWSProtocol extends Protocol {
	/**
	 * Returns the value of the '<em><b>Transaction</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Transaction</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transaction</em>' attribute.
	 * @see #setTransaction(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getCICSWSProtocol_Transaction()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='transaction'"
	 * @generated
	 */
	String getTransaction();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol#getTransaction <em>Transaction</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Transaction</em>' attribute.
	 * @see #getTransaction()
	 * @generated
	 */
	void setTransaction(String value);

	/**
	 * Returns the value of the '<em><b>User ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>User ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>User ID</em>' attribute.
	 * @see #setUserID(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getCICSWSProtocol_UserID()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='userID'"
	 * @generated
	 */
	String getUserID();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol#getUserID <em>User ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>User ID</em>' attribute.
	 * @see #getUserID()
	 * @generated
	 */
	void setUserID(String value);

} // CICSWSProtocol
