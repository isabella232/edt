/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.formatting.profile;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Combo Control</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.ComboControl#getChoices <em>Choices</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getComboControl()
 * @model extendedMetaData="name='ComboControl' kind='empty'"
 * @generated
 */
public interface ComboControl extends Control {
	/**
	 * Returns the value of the '<em><b>Choices</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Choices</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Choices</em>' attribute.
	 * @see #setChoices(String)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getComboControl_Choices()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='choices'"
	 * @generated
	 */
	String getChoices();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.ComboControl#getChoices <em>Choices</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Choices</em>' attribute.
	 * @see #getChoices()
	 * @generated
	 */
	void setChoices(String value);

} // ComboControl
