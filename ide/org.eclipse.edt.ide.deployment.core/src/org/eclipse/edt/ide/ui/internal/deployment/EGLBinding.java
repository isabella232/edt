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

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EGL Binding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getProtocolGroup <em>Protocol Group</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getProtocol <em>Protocol</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getAlias <em>Alias</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getServiceName <em>Service Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLBinding()
 * @model extendedMetaData="name='EGLBinding' kind='elementOnly'"
 * @generated
 */
public interface EGLBinding extends EObject {
	/**
	 * Returns the value of the '<em><b>Protocol Group</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol Group</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol Group</em>' attribute list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLBinding_ProtocolGroup()
	 * @model dataType="org.eclipse.emf.ecore.EFeatureMapEntry" required="true" many="false"
	 *        extendedMetaData="kind='group' name='protocol:group' namespace='##targetNamespace'"
	 * @generated
	 */
	FeatureMap getProtocolGroup();

	/**
	 * Returns the value of the '<em><b>Protocol</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol</em>' containment reference.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLBinding_Protocol()
	 * @model containment="true" required="true" transient="true" changeable="false" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol' namespace='##targetNamespace' group='protocol:group'"
	 * @generated
	 */
	Protocol getProtocol();

	/**
	 * Returns the value of the '<em><b>Alias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Alias</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Alias</em>' attribute.
	 * @see #setAlias(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLBinding_Alias()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName"
	 *        extendedMetaData="kind='attribute' name='alias'"
	 * @generated
	 */
	String getAlias();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getAlias <em>Alias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Alias</em>' attribute.
	 * @see #getAlias()
	 * @generated
	 */
	void setAlias(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLBinding_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName" required="true"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Service Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Service Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Service Name</em>' attribute.
	 * @see #setServiceName(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLBinding_ServiceName()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName"
	 *        extendedMetaData="kind='attribute' name='serviceName'"
	 * @generated
	 */
	String getServiceName();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLBinding#getServiceName <em>Service Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Service Name</em>' attribute.
	 * @see #getServiceName()
	 * @generated
	 */
	void setServiceName(String value);

} // EGLBinding
