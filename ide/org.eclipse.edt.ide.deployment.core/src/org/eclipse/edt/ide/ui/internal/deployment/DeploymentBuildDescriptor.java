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
 * A representation of the model object '<em><b>Build Descriptor</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.DeploymentBuildDescriptor#getFileName <em>File Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getDeploymentBuildDescriptor()
 * @model extendedMetaData="name='DeploymentBuildDescriptor' kind='elementOnly'"
 * @generated
 */
public interface DeploymentBuildDescriptor extends DeploymentTarget {
	/**
	 * Returns the value of the '<em><b>File Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>File Name</em>' attribute.
	 * @see #setFileName(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getDeploymentBuildDescriptor_FileName()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='fileName'"
	 * @generated
	 */
	String getFileName();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.DeploymentBuildDescriptor#getFileName <em>File Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>File Name</em>' attribute.
	 * @see #getFileName()
	 * @generated
	 */
	void setFileName(String value);

} // DeploymentBuildDescriptor
