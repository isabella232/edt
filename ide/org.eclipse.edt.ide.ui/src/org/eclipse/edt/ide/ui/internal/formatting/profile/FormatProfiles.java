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
 * A representation of the model object '<em><b>Format Profiles</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getDefaultProfile <em>Default Profile</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getProfile <em>Profile</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getSelection <em>Selection</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getFormatProfiles()
 * @model extendedMetaData="name='FormatProfiles' kind='elementOnly'"
 * @generated
 */
public interface FormatProfiles extends EObject {
	/**
	 * Returns the value of the '<em><b>Default Profile</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Default Profile</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Default Profile</em>' containment reference.
	 * @see #setDefaultProfile(DefaultProfile)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getFormatProfiles_DefaultProfile()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='defaultProfile'"
	 * @generated
	 */
	DefaultProfile getDefaultProfile();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getDefaultProfile <em>Default Profile</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Default Profile</em>' containment reference.
	 * @see #getDefaultProfile()
	 * @generated
	 */
	void setDefaultProfile(DefaultProfile value);

	/**
	 * Returns the value of the '<em><b>Profile</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Profile</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Profile</em>' containment reference list.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getFormatProfiles_Profile()
	 * @model type="org.eclipse.edt.ide.ui.internal.formatting.profile.Profile" containment="true"
	 *        extendedMetaData="kind='element' name='profile'"
	 * @generated
	 */
	EList getProfile();

	/**
	 * Returns the value of the '<em><b>Selection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Selection</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Selection</em>' attribute.
	 * @see #setSelection(String)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getFormatProfiles_Selection()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='selection'"
	 * @generated
	 */
	String getSelection();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getSelection <em>Selection</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Selection</em>' attribute.
	 * @see #getSelection()
	 * @generated
	 */
	void setSelection(String value);

	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #setVersion(String)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getFormatProfiles_Version()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='version'"
	 * @generated
	 */
	String getVersion();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(String value);

} // FormatProfiles
