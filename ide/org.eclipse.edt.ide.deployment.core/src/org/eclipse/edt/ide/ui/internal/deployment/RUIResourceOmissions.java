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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>RUI Resource Omissions</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.RUIResourceOmissions#getResource <em>Resource</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRUIResourceOmissions()
 * @model extendedMetaData="name='RUIResourceOmissions' kind='elementOnly'"
 * @generated
 */
public interface RUIResourceOmissions extends EObject {
	/**
	 * Returns the value of the '<em><b>Resource</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.deployment.RUIResource}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resource</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resource</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getRUIResourceOmissions_Resource()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='resource'"
	 * @generated
	 */
	EList<RUIResource> getResource();

} // RUIResourceOmissions
