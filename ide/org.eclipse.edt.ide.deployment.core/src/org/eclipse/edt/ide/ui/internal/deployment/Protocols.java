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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Protocols</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Protocols#getProtocolGroup <em>Protocol Group</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Protocols#getProtocol <em>Protocol</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getProtocols()
 * @model extendedMetaData="name='Protocols' kind='elementOnly'"
 * @generated
 */
public interface Protocols extends EObject {
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getProtocols_ProtocolGroup()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='protocol:group' namespace='##targetNamespace'"
	 * @generated
	 */
	FeatureMap getProtocolGroup();

	/**
	 * Returns the value of the '<em><b>Protocol</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.deployment.Protocol}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getProtocols_Protocol()
	 * @model containment="true" transient="true" changeable="false" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol' namespace='##targetNamespace' group='protocol:group'"
	 * @generated
	 */
	EList<Protocol> getProtocol();

} // Protocols
