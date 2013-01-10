/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.preferences;

import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.util.PropertyChangeEvent;

public class ContentAssistPreference {
	
	/**
	 * Changes the configuration of the given content assistant according to the given property
	 * change event and the given preference store.
	 *
	 * @param assistant the content assistant
	 * @param store the preference store
	 * @param event the property change event
	 */
	public static void changeConfiguration(ContentAssistant assistant, IPreferenceStore store, PropertyChangeEvent event) {
		String p= event.getProperty();
		if (EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION.equals(p)) {
			boolean enabled= store.getBoolean(EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION);
			assistant.enableAutoActivation(enabled);
		}
		
		if(EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY.equals(p)){
			int delayTime = store.getInt(EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY);
			assistant.setAutoActivationDelay(delayTime);
		}
	}
}
