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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Default Profile</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getPreview <em>Preview</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getControls <em>Controls</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getCategory <em>Category</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getDefaultProfile()
 * @model extendedMetaData="name='DefaultProfile' kind='elementOnly'"
 * @generated
 */
public interface DefaultProfile extends EObject {
	/**
	 * Returns the value of the '<em><b>Preview</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Preview</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Preview</em>' containment reference.
	 * @see #setPreview(Preview)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getDefaultProfile_Preview()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='preview'"
	 * @generated
	 */
	Preview getPreview();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getPreview <em>Preview</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Preview</em>' containment reference.
	 * @see #getPreview()
	 * @generated
	 */
	void setPreview(Preview value);

	/**
	 * Returns the value of the '<em><b>Controls</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Controls</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Controls</em>' containment reference.
	 * @see #setControls(Controls)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getDefaultProfile_Controls()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='controls'"
	 * @generated
	 */
	Controls getControls();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getControls <em>Controls</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Controls</em>' containment reference.
	 * @see #getControls()
	 * @generated
	 */
	void setControls(Controls value);

	/**
	 * Returns the value of the '<em><b>Category</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.formatting.profile.Category}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Category</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Category</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getDefaultProfile_Category()
	 * @model type="org.eclipse.edt.ide.ui.internal.formatting.profile.Category" containment="true"
	 *        extendedMetaData="kind='element' name='category'"
	 * @generated
	 */
	EList getCategory();

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
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getDefaultProfile_Name()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // DefaultProfile
