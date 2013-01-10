/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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
 * A representation of the model object '<em><b>Profile</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#getSetting <em>Setting</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#getBase <em>Base</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#isIsBuildIn <em>Is Build In</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getProfile()
 * @model extendedMetaData="name='Profile' kind='elementOnly'"
 * @generated
 */
public interface Profile extends EObject {
	/**
	 * Returns the value of the '<em><b>Setting</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.formatting.profile.Setting}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Setting</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Setting</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getProfile_Setting()
	 * @model type="org.eclipse.edt.ide.ui.internal.formatting.profile.Setting" containment="true"
	 *        extendedMetaData="kind='element' name='setting'"
	 * @generated
	 */
	EList getSetting();

	/**
	 * Returns the value of the '<em><b>Base</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Base</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Base</em>' attribute.
	 * @see #setBase(String)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getProfile_Base()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.NCName"
	 *        extendedMetaData="kind='attribute' name='base'"
	 * @generated
	 */
	String getBase();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#getBase <em>Base</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Base</em>' attribute.
	 * @see #getBase()
	 * @generated
	 */
	void setBase(String value);

	/**
	 * Returns the value of the '<em><b>Is Build In</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Build In</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Build In</em>' attribute.
	 * @see #isSetIsBuildIn()
	 * @see #unsetIsBuildIn()
	 * @see #setIsBuildIn(boolean)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getProfile_IsBuildIn()
	 * @model default="false" unique="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='isBuildIn'"
	 * @generated
	 */
	boolean isIsBuildIn();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#isIsBuildIn <em>Is Build In</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Build In</em>' attribute.
	 * @see #isSetIsBuildIn()
	 * @see #unsetIsBuildIn()
	 * @see #isIsBuildIn()
	 * @generated
	 */
	void setIsBuildIn(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#isIsBuildIn <em>Is Build In</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetIsBuildIn()
	 * @see #isIsBuildIn()
	 * @see #setIsBuildIn(boolean)
	 * @generated
	 */
	void unsetIsBuildIn();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#isIsBuildIn <em>Is Build In</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Is Build In</em>' attribute is set.
	 * @see #unsetIsBuildIn()
	 * @see #isIsBuildIn()
	 * @see #setIsBuildIn(boolean)
	 * @generated
	 */
	boolean isSetIsBuildIn();

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
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getProfile_Name()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // Profile
