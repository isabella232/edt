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
package org.eclipse.edt.ide.ui.internal.formatting.ui;

import java.net.URL;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.formatting.CodeFormatterConstants;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Category;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ComboControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Control;
import org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot;
import org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Group;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preference;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfileFactory;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage;
import org.eclipse.edt.ide.ui.internal.formatting.profile.RadioControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ReferenceControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.util.ProfileResourceFactoryImpl;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * This is a development tool
 * it reads from the format_builtin.profile
 * - check for the "display" attribute on the <category>, <group> node
 * - check for the "display" and "altDisplay" attribute on the <pref> node
 * - check for the "choices" attribute on the <control> node, comboControl and radioControl
 * 
 * then generates the NLS, key value pairs, sorted by key value alphabetically, user should copy the result in the console to
 * 
 * ConstructedFormattingMessages.properties
 * 
 * User should also pay attention to any error in the console, fix them accordingly
 *
 */
public class GenConstructedFormattingMessagesNLS {
	private final static char WHITESPACE = ' ';
	private final static char UNDERSCORE = '_';
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenConstructedFormattingMessagesNLS genNLSMsgs = new GenConstructedFormattingMessagesNLS();
		
		//figure out the current class file location

		URL classUrl = genNLSMsgs.getClass().getResource("/org/eclipse/edt/ide/ui/internal/formatting/GenConstructedFormattingMessagesNLS.class");
		if (classUrl != null && classUrl.getProtocol().equals( "file" )){
			String initializerPath = classUrl.getFile();
			
			//try to get the ide.ui plugin directory
			IPath path = new Path(initializerPath);			
			String[] segs = path.segments();
			for(int i=segs.length-1; i>=0; i--){
				path = path.removeLastSegments(1);
				if(segs[i].equals(EDTUIPlugin.PLUGIN_ID))
					break;
			}
			
			//then try to find the org.eclipse.edt.ide.ui plugin folder 
			//and its sub folder builtinPreference to locate the format_builtin.profile
			path = path.append(EDTUIPlugin.PLUGIN_ID);
			path = path.append(ProfileManager.PROFILE_BUILDIN_PATH);
			
			genNLSMsgs.readFromBuiltInProfile(path);
		}
	}
	
	//need to register these EMF package and resource factory, since we're not running in eclipse env	
	private void registerEMF(){
		//TODO EDT Update?
		EPackage.Registry.INSTANCE.put("http://www.ibm.com/xmlns/egl/formatting/1.0", new EPackage.Descriptor() {

			public EFactory getEFactory() {
				return ProfileFactory.eINSTANCE;
			}

			public EPackage getEPackage() {
				return ProfilePackage.eINSTANCE;
			}			
		});
		
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("profile", new ProfileResourceFactoryImpl());
	}

	public void readFromBuiltInProfile(IPath buildInPath){
		registerEMF();
		EGLFormatProfileRoot root = FormatProfileRootHelper.getEGLFormatProfileModel(buildInPath);
		
		//keep track of the model change
		final boolean[] emfModelChanged = new boolean[]{false};		
		root.eResource().eAdapters().add(new Adapter(){
			public void notifyChanged(Notification notification) {
				emfModelChanged[0] = true;				
			}

			public Notifier getTarget() {return null ;}
			public boolean isAdapterForType(Object type) {return false ;}
			public void setTarget(Notifier newTarget) {}			
		});
		
		FormatProfiles formatProfiles = root.getFormatProfiles();
		DefaultProfile defaultProfile = formatProfiles.getDefaultProfile();
		if(defaultProfile != null){
			
			/**
			 * key is String - the nls key, value is String - the nls value in english 
			 */
			SortedMap nlsMap = new TreeMap();
			
			EList categories = defaultProfile.getCategory();
			for(Iterator it = categories.iterator(); it.hasNext();){
				Category category = (Category)it.next();
				String categoryDisplay = category.getDisplay();
				
				//StringBuffer nlsKeyInProfile = new StringBuffer();				
				String categoryID = category.getId();
				
				//display on <category> node
				String nlsKeyInProfile = populateNLSMap(nlsMap, categoryID, categoryDisplay, CodeFormatterConstants.DISPLAY_TREE_DELIMITER) ;
				if(nlsKeyInProfile.length()>0)
					category.setDisplay(nlsKeyInProfile);
				
				EList groups = category.getGroup();
				for(Iterator itgrp=groups.iterator(); itgrp.hasNext();){
					Group grp = (Group)itgrp.next();
					//display on <group> node					
					nlsKeyInProfile = populateNLSMap(nlsMap, categoryID, grp.getDisplay(), CodeFormatterConstants.DISPLAY_TREE_DELIMITER);
					if(nlsKeyInProfile.length()>0)
						grp.setDisplay(nlsKeyInProfile);

										
					EList prefs = grp.getPref();
					for(Iterator pIt=prefs.iterator(); pIt.hasNext();){
						Preference pref = (Preference)pIt.next();
						String prefSettingID = CodeFormatterConstants.getPreferenceSettingKey(categoryID, pref.getId());
						
						//display on <preference> node
						nlsKeyInProfile = populateNLSMap(nlsMap, prefSettingID, pref.getDisplay(), CodeFormatterConstants.DISPLAY_TREE_DELIMITER);
						if(nlsKeyInProfile.length()>0)
							pref.setDisplay(nlsKeyInProfile);
						
						//altDisplay on <preference> node
						nlsKeyInProfile = populateNLSMap(nlsMap, prefSettingID, pref.getAltDisplay(), CodeFormatterConstants.DISPLAY_TREE_DELIMITER);
						if(nlsKeyInProfile.length()>0)
							pref.setAltDisplay(nlsKeyInProfile);
												
						Control control = pref.getControl();
						Control controlInstance = control;
						if (control instanceof ReferenceControl) {
							ReferenceControl refControl = (ReferenceControl)control;
							controlInstance = FormatProfileRootHelper.getReferencedControl(defaultProfile, refControl);					
						}
						
						if(controlInstance instanceof ComboControl){
							ComboControl comboControl = (ComboControl)controlInstance ;
							String choices = comboControl.getChoices();
							nlsKeyInProfile = populateNLSMap(nlsMap, prefSettingID, choices, FormatProfileRootHelper.DELIMITER_COMMA);
							if(nlsKeyInProfile.length()>0)
								comboControl.setChoices(nlsKeyInProfile);
							
						}
						else if(controlInstance instanceof RadioControl){
							RadioControl radioControl = (RadioControl)controlInstance ;
							String choices = radioControl.getChoices();
							nlsKeyInProfile = populateNLSMap(nlsMap, prefSettingID, choices, FormatProfileRootHelper.DELIMITER_COMMA);
							radioControl.setChoices(nlsKeyInProfile);
						}													
					}
				}
			}
			
			System.out.println();
			System.out.println("========the following goes into the .properties file===============");
			for(Iterator itmap = nlsMap.keySet().iterator(); itmap.hasNext();){
				String key = (String)itmap.next();
				String value = (String)nlsMap.get(key);
				System.out.print(key);
				System.out.print(" = ");
				System.out.println(value);
			}
			
			if(emfModelChanged[0]){
				//save the default Profile if the model has changed
				FormatProfileRootHelper.saveEGLFormatProfile(buildInPath, root);
			}
		}
	}

	/**
	 * 
	 * @param nlsMap
	 * @param settingId
	 * @param displayAttributeValue
	 * @param deilimiter
	 * @return nlsKeyInProfile in profile, should start with %, if displayAttributeValue does not start with %
	 */
	private String populateNLSMap(SortedMap nlsMap, String settingId, String displayAttributeValue, String deilimiter) {
		StringBuffer nlsKeyInProfile = new StringBuffer();
		if(displayAttributeValue != null && displayAttributeValue.length()>0){
			//parse for . delimiter
			String[] displays = FormatProfileRootHelper.parseTokenizedString(displayAttributeValue, deilimiter);		
			for(int i=0; i<displays.length; i++){
				String display = displays[i];
							
				if(display.trim().length()>0){	//if it's only white space, leave it alone
					String nlsKey = display;
					//check for % starting char, if it starts with %, it's already NLS key
					if(!display.startsWith(FormatProfileRootHelper.NLSKEY_LEADINGCHAR)){
						//check for white space, replace white space with _
						nlsKey = display.replace(WHITESPACE, UNDERSCORE);
						StringBuffer strNLSKey = new StringBuffer();
						//need to remove any illegal character 
						for(int j=0; j<nlsKey.length(); j++){
							char c = nlsKey.charAt(j);
							
							//only allow underscore, number and alphabet
							//note it shouldn't start with a number
							if(j== 0 && ('0' <= c && c <= '9')){
								System.out.println("**********ERROR********");
								System.out.println("setting id [" + settingId + "]: display name - [" + display + "] starts with a number!!!");
								System.out.println("***********************");
							}
							else if(c == UNDERSCORE 
									|| ('0' <= c && c <='9') 
									|| ('A' <= c && c <= 'Z')
									|| ('a' <= c && c <= 'z')){
								strNLSKey.append(c);
							}
						}
						
						nlsKey = strNLSKey.toString();
						
						//check to see if it's already in the map, if so, check to see if the value is the same, it should be
						if(nlsMap.containsKey(nlsKey)){
							String displayInMap = (String)nlsMap.get(nlsKey);
							if(!displayInMap.equals(display)){
								System.out.println("**********ERROR********");
								System.out.println("setting id [" + settingId + "]: display key - [" + nlsKey + "] existed with - [" + displayInMap + "]");
								System.out.println("but same key now has another value - [" + display + "]");
								System.out.println("***********************");								
							}
						}
						else
							nlsMap.put(nlsKey, display);		
						
						nlsKey = FormatProfileRootHelper.NLSKEY_LEADINGCHAR + nlsKey;
					}
					if(i > 0)
						nlsKeyInProfile.append(deilimiter);
					nlsKeyInProfile.append(nlsKey);		
					
				}
			}
		}
		return nlsKeyInProfile.toString();
	}
	
	
}
