/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.edt.ide.ui.wizards.EGLDDBindingConfiguration;
import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Comm Types</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.ibm.etools.egl.internal.soa.serviceBinding.ServiceBindingPackage#getCommTypes()
 * @model
 * @generated
 */
public final class CommTypes extends AbstractEnumerator {
	
	/**
	 * The '<em><b>LOCAL</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>LOCAL</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LOCAL_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int LOCAL = 0;

	/**
	 * The '<em><b>CICSECI</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>CICSECI</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CICSECI_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int CICSECI = 1;

	/**
	 * The '<em><b>CICSJ2C</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>CICSJ2C</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CICSJ2C_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int CICSJ2C = 2;

	/**
	 * The '<em><b>CICSSSL</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>CICSSSL</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CICSSSL_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int CICSSSL = 3;

	/**
	 * The '<em><b>JAVA400</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>JAVA400</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #JAVA400_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int JAVA400 = 4;

	/**
	 * The '<em><b>TCPIP</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>TCPIP</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TCPIP_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TCPIP = 5;
	
	public static final int IMSJ2C = 6;
	
	public static final int IMSTCP = 7;	
	
	public static final int JAVA400J2C = 8;	
	
	public static final int SYSTEMI_LOCAL = 6;
	
	/**
	 * The '<em><b>CICSWS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>CICSWS</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CICSWS_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 * 
	 * NOTE: this should be at the end, if adding more protocols that can be used by the service binding client
	 * add them above this one, increase this CICSWS's value
	 */
	public static final int CICSWS = 7;	
	
	/**
	 * The '<em><b>LOCAL</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LOCAL
	 * @generated
	 * @ordered
	 */
	public static final CommTypes LOCAL_LITERAL = new CommTypes(LOCAL, "LOCAL", "LOCAL"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>CICSECI</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CICSECI
	 * @generated
	 * @ordered
	 */
	public static final CommTypes CICSECI_LITERAL = new CommTypes(CICSECI, "CICSECI", "CICSECI"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>CICSJ2C</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CICSJ2C
	 * @generated
	 * @ordered
	 */
	public static final CommTypes CICSJ2C_LITERAL = new CommTypes(CICSJ2C, "CICSJ2C", "CICSJ2C"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>CICSSSL</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CICSSSL
	 * @generated
	 * @ordered
	 */
	public static final CommTypes CICSSSL_LITERAL = new CommTypes(CICSSSL, "CICSSSL", "CICSSSL"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>JAVA400</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #JAVA400
	 * @generated
	 * @ordered
	 */
	public static final CommTypes JAVA400_LITERAL = new CommTypes(JAVA400, "JAVA400", "JAVA400"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>TCPIP</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TCPIP
	 * @generated
	 * @ordered
	 */
	public static final CommTypes TCPIP_LITERAL = new CommTypes(TCPIP, "TCPIP", "TCPIP"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>CICSWS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CICSWS
	 * @generated
	 * @ordered
	 */
	public static final CommTypes CICSWS_LITERAL = new CommTypes(CICSWS, "CICSWS", "CICSWS"); //$NON-NLS-1$ //$NON-NLS-2$
	
	public static final CommTypes IMSJ2C_LITERAL = new CommTypes(IMSJ2C, "IMSJ2C", "IMSJ2C"); //$NON-NLS-1$ //$NON-NLS-2$
	
	public static final CommTypes IMSTCP_LITERAL = new CommTypes(IMSTCP, "IMSTCP", "IMSTCP"); //$NON-NLS-1$ //$NON-NLS-2$ 
	
	public static final CommTypes SYSTEMI_LOCAL_LITERAL = new CommTypes(SYSTEMI_LOCAL, "SYSTEMI_LOCAL", "SYSTEM-I LOCAL"); //$NON-NLS-1$ //$NON-NLS-2$ 
	
	public static final CommTypes JAVA400J2C_LITERAL = new CommTypes(JAVA400J2C, "JAVA400J2C", "JAVA400J2C"); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * An array of all the '<em><b>Comm Types</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final CommTypes[] VALUES_ARRAY =
		new CommTypes[] {
			LOCAL_LITERAL,
			CICSECI_LITERAL,
			CICSJ2C_LITERAL,
			CICSSSL_LITERAL,
			JAVA400_LITERAL,
			JAVA400J2C_LITERAL,
			TCPIP_LITERAL,
//			IMSJ2C_LITERAL,
//			IMSTCP_LITERAL,
			SYSTEMI_LOCAL_LITERAL,
			CICSWS_LITERAL
		};
	
	private static final CommTypes[] VALUES_EGLBINDING_ARRAY = 
		new CommTypes[]{
			LOCAL_LITERAL,
			CICSECI_LITERAL,
			CICSJ2C_LITERAL,
			CICSSSL_LITERAL,
			JAVA400_LITERAL,
			JAVA400J2C_LITERAL,
			TCPIP_LITERAL
	//		IMSJ2C_LITERAL,
	//		IMSTCP_LITERAL,
		};
	
	private static final CommTypes[] VALUES_NATIVEBINDING_ARRAY = 
		new CommTypes[]{
			SYSTEMI_LOCAL_LITERAL,
			JAVA400_LITERAL,
			JAVA400J2C_LITERAL
		};	
		
	private static final CommTypes[] VALUES_CLIENTBINDING_ARRAY = 
		new CommTypes[]{
		LOCAL_LITERAL,
		CICSECI_LITERAL,
		CICSJ2C_LITERAL,
		CICSSSL_LITERAL,
		JAVA400_LITERAL,
		JAVA400J2C_LITERAL,
		TCPIP_LITERAL,
		SYSTEMI_LOCAL_LITERAL
//		IMSJ2C_LITERAL,
//		IMSTCP_LITERAL,
	};
		
	
	/**
	 * A public read-only list of all the '<em><b>Comm Types</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	private static final List VALUES_EGLBINDING = Collections.unmodifiableList(Arrays.asList(VALUES_EGLBINDING_ARRAY));
	
	private static final List VALUES_NATIVEBINDING = Collections.unmodifiableList(Arrays.asList(VALUES_NATIVEBINDING_ARRAY));
	
	private static final List VALUES_CLIENTBINDING = Collections.unmodifiableList(Arrays.asList(VALUES_CLIENTBINDING_ARRAY));
	
	public static List getSupportedProtocol(int bindingType){
		//TODO fix before checkin
		switch(bindingType){
		case EGLDDBindingConfiguration.BINDINGTYPE_EGL:
			return VALUES_EGLBINDING;
		case EGLDDBindingConfiguration.BINDINGTYPE_NATIVE:
			return VALUES_NATIVEBINDING;
		}
		return null;
	}
	
	/**
	 * Returns the '<em><b>Comm Types</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static CommTypes get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			CommTypes result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Comm Types</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static CommTypes getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			CommTypes result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Comm Types</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static CommTypes get(int value) {
		switch (value) {
			case LOCAL: return LOCAL_LITERAL;
			case CICSECI: return CICSECI_LITERAL;
			case CICSJ2C: return CICSJ2C_LITERAL;
			case CICSSSL: return CICSSSL_LITERAL;
			case JAVA400: return JAVA400_LITERAL;
			case JAVA400J2C: return JAVA400J2C_LITERAL;
			case TCPIP: return TCPIP_LITERAL;
//			case IMSJ2C: return IMSJ2C_LITERAL;
//			case IMSTCP: return IMSTCP_LITERAL;
			case CICSWS: return CICSWS_LITERAL;		
			case SYSTEMI_LOCAL: return SYSTEMI_LOCAL_LITERAL;
		}
		return null;	
	}

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private CommTypes(int value, String name, String literal) {
		super(value, name, literal);
	}

} //CommTypes
