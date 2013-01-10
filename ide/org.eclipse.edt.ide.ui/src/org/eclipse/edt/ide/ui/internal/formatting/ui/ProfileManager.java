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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.formatting.CodeFormatterConstants;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Category;
import org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot;
import org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Group;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preference;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Profile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfileFactory;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Setting;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.osgi.framework.Bundle;

public class ProfileManager extends Observable{

	public static class PreferenceSettingValue{
		private String fInitialValue;		//initial value is read from the format.profile on disk
		private String fCurrValue;			//current value is the current UI control's value
		
		public PreferenceSettingValue(String initValue, String currValue){
			fInitialValue = initValue;
			fCurrValue = currValue;
		}
		
		public void setInitialValue(String initValue){
			fInitialValue = initValue;
		}
		
		public void setCurrValue(String currValue){
			fCurrValue = currValue;
		}
		
		public String getCurrentValue(){ return fCurrValue;}
		public String getInitialValue(){ return fInitialValue;}
		
		public boolean hasSettingValueChanged(){
			return !fInitialValue.equals(fCurrValue);
		}
	}

	public static final String PROFILE_BUILDIN_PATH = "builtInPreference/format_builtin.profile"; //$NON-NLS-1$
	private static final String PROFILE_WORKSPACE_PATH = "format.profile"; //$NON-NLS-1$
	private static final String PROFILE_BUILDIN_DISPLAY_SUFFIX = NewWizardMessages.builtIn;
		
	/**
	 * the EMF model root for the build in profile, located at egl.ui plugin/"buildInPreference/format_buildin.profile"
	 */
	private EGLFormatProfileRoot fBuildInProfileRoot;
	
	/**
	 * the EMF model root for the custom profile in the current workspace, located at the current workspace folder
	 */
	private EGLFormatProfileRoot fCustomWSProfileRoot;
	
    /**
     * key is a string in the format: "categoryID.prefID"
     * value is PreferenceSettingValue: initValue in the profile and newValue from UI 
     * 
     * - this map holds ALL the preference setting values from a profile(or DefaultProfile)
     * 	 this is passed to the each of the tab page to set initial value of the UI control and set current value change from the UI control
     * - this map is a singleton, since the ProfileManger class is a singleton, 
     *   this is passed to the the formatting engine, which will use this map to get the current preference at any given time 
     */	
	private Map fCurrentAllPreferenceSettingMap;
		
	private static ProfileManager INSTANCE;
	
	private ProfileManager(){
		fBuildInProfileRoot = null;
		fCustomWSProfileRoot = null;
		fCurrentAllPreferenceSettingMap = new Hashtable();
	}
	
	/**
	 * singleton and also an Observable, this object will live till the end of the eclipse session
	 * so whoever calls addObserver, deleteObserver should also be called at its clean up
	 * 
	 * @return
	 */
	public static ProfileManager getInstance(){
		if(INSTANCE == null)
			INSTANCE = new ProfileManager();
		return INSTANCE;
	}
	
	public Map getCurrentPreferenceSettingMap(){
		if(fCurrentAllPreferenceSettingMap.isEmpty())
			loadCurrentAllPreferenceSettingMap();
		
		return fCurrentAllPreferenceSettingMap;
	}
	
	public EGLFormatProfileRoot getBuildInFormatProfileRoot(){
		if(fBuildInProfileRoot == null){
			IPath buildInPath = getBuildInPreferenceProfilePath();
			if(buildInPath != null){
				fBuildInProfileRoot = FormatProfileRootHelper.getEGLFormatProfileModel(buildInPath);
			}			
		}			
		return fBuildInProfileRoot;			
	}
	
	/**
	 * try to get the EMF EGLFormatProfileRoot model from the workspace location
	 * if the file does not exsit, and user pass in true for createNew, then a new file(format.profile)
	 * will be created at the workspace location 
	 * 
	 * @param createNew - true/false
	 * @return
	 */
	public EGLFormatProfileRoot getCustomWorkspaceFormatProfileRoot(boolean createNew){
		IPath wsPrefProfilePath = getWorkspacePreferenceProfilePath();		
		if(fCustomWSProfileRoot == null){
			File wsPrefProfile = wsPrefProfilePath.toFile();
			if(wsPrefProfile.exists())
				fCustomWSProfileRoot = FormatProfileRootHelper.getEGLFormatProfileModel(wsPrefProfilePath);					
		}			
		if(fCustomWSProfileRoot == null && createNew){
			fCustomWSProfileRoot = FormatProfileRootHelper.createNewEGLFormatProfileModel(wsPrefProfilePath);
		}
		return fCustomWSProfileRoot;
	}
	
	private static IPath getWorkspacePreferenceProfilePath(){
		return EDTUIPlugin.getDefault().getStateLocation().append(PROFILE_WORKSPACE_PATH);
	}
	
	public void saveCustomWorkspaceFormatProfile(boolean clearCachedFormatProfileEMFModel){
		EGLFormatProfileRoot root = getCustomWorkspaceFormatProfileRoot(true);
		if(root != null)
			FormatProfileRootHelper.saveEGLFormatProfile(getWorkspacePreferenceProfilePath(), root);
		
		clearCachedModel(clearCachedFormatProfileEMFModel) ;			
	}

	public void clearCachedModel(boolean clearCachedFormatProfileEMFModel) {
		//clear the cache, next time, these model will be read from the disk again
		if(clearCachedFormatProfileEMFModel){
			fBuildInProfileRoot = null;
			fCustomWSProfileRoot = null;
			fCurrentAllPreferenceSettingMap.clear();
		}
	}
	
	private static IPath getBuildInPreferenceProfilePath(){
		Bundle eglResourcesBundle = Platform.getBundle(EDTUIPlugin.PLUGIN_ID);
		URL url = FileLocator.find(eglResourcesBundle, new Path(PROFILE_BUILDIN_PATH), null);
		try {
			url = FileLocator.resolve(url);
			String pathStr = null;
 			if ( "file".equals( url.getProtocol() ) ) {
				pathStr = url.getFile();
			} else {
				pathStr = url.toString();
			}
			if(url != null) //$NON-NLS-1$
			{
				Path path = new Path(pathStr);
				return path;
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
		
	/**
	 * 
	 * @return the display name of the selected Profile or DefaultProfile,
	 * 		   NOT the name attribute value
	 */
	public String getSelectedProfileDisplayName(){
		return getProfileName(getSelectedProfile(), true);
	}
	
	public String getProfileDisplayName(EObject profileOrDefautlProfile){
		return getProfileName(profileOrDefautlProfile, true);
	}
		
	public boolean isSelectedProfileBuildIn(){
		EObject obj = getSelectedProfile();
		return isProfileBuildIn(obj);
	}
	
	public boolean isProfileBuildIn(EObject obj){
		if(obj instanceof DefaultProfile)
			return true;
		
		if(obj instanceof Profile)
			return ((Profile)obj).isIsBuildIn();
		
		return false;		
	}
	
	/**
	 * check the selection attribute of the format.profile in the workspace, then find the profile that has this name
	 * if workspace doesn't have the format.profile or a profile with that name can not be found
	 * try to find the profile in the buildin profile, if non can be found, return the DefaultProfile
	 * 
	 * @return - either a Profile or DefaultProfile
	 */
	public EObject getSelectedProfile(){
		String selectedProfileName = null;
		EObject selProfile = null;
		
		//try to load the profile from workspace, get its selection
		EGLFormatProfileRoot customWSProfileRoot = getCustomWorkspaceFormatProfileRoot(false);
		if(customWSProfileRoot != null){
			FormatProfiles profilesroot = customWSProfileRoot.getFormatProfiles();
			selectedProfileName = profilesroot.getSelection();
			if(selectedProfileName != null && selectedProfileName.length()>0)
				selProfile = getProfileByName(selectedProfileName, true);
		}
		
		if(selProfile == null){
			selProfile = getProfileByName(null, true);
		}
		
		return selProfile;
	}
	
	/**
	 * return a profile with the name (profileName) 
	 * if none found, return DefaultProfile
	 * 		if returnDefaultProfileIfNotFound is true
	 * 			or
	 * 		if the profileName matches the DefaultProfile name
	 * 
	 * @param profileName
	 * @param returnDefaultProfileIfNotFound 
	 * @return - either a Profile or DefaultProfile
	 */
	public EObject getProfileByName(String profileName, boolean returnDefaultProfileIfNotFound){
		EObject selProfile = null;
		
		//try to load the profile from workspace, get its selection
		EGLFormatProfileRoot customWSProfileRoot = getCustomWorkspaceFormatProfileRoot(false);
		if(customWSProfileRoot != null){
			FormatProfiles profilesroot = customWSProfileRoot.getFormatProfiles();
			if(profileName != null && profileName.length()>0)
				selProfile = FormatProfileRootHelper.findProfileByName(profileName, profilesroot);
		}
		
		//if not found in the custom workspace profile, try to find it in the build in profile
		if(customWSProfileRoot == null || selProfile == null){
			EGLFormatProfileRoot buildInProfileRoot = getBuildInFormatProfileRoot();
			if(buildInProfileRoot != null){
				FormatProfiles buildInProfilesRoot = buildInProfileRoot.getFormatProfiles();
				
				//try to find the build in profile
				if(profileName != null && profileName.length()>0)
					selProfile = FormatProfileRootHelper.findProfileByName(profileName, buildInProfilesRoot);				
				
				//can not find the profile or no selection attribute value, then return the DefaultProfile
				if(selProfile == null){
					DefaultProfile defaultProfile = buildInProfilesRoot.getDefaultProfile();
					if(returnDefaultProfileIfNotFound || profileName.equals(defaultProfile.getName()))
						selProfile = defaultProfile;
				}
			}
		}		
		return selProfile;
	}

	/**
	 * 
	 * @param profileName
	 * @return 
	 */
	public boolean containsProfile(String profileName){
		return getProfileByName(profileName, false) != null; 
	}
	
	/**
	 * sets the selection attribute on the <egl:format_profiles> of the custom format.profile in workspace
	 * if there was no custom format.profile in worksapce, one will be created there 
	 * 
	 * @param index
	 */
	public void setSelectedProfile(int index){
		List allProfiles = getAllProfiles();
		Object obj = allProfiles.get(index);
		setSelectedProfile(obj) ;		
	}

	/**
	 * 
	 * @param obj - either DefaultProfile or Profile
	 */
	public void setSelectedProfile(Object obj) {
		String selName = ""; //$NON-NLS-1$
		if(obj instanceof DefaultProfile){
			selName = ((DefaultProfile)obj).getName();
		}
		else if(obj instanceof Profile){
			selName = ((Profile)obj).getName();
		}
		EGLFormatProfileRoot profileRoot = getCustomWorkspaceFormatProfileRoot(true);
		if(profileRoot != null){
			FormatProfiles profilesroot = profileRoot.getFormatProfiles();
			profilesroot.setSelection(selName);
			
			updateCurrentAllPreferenceSettingMap((EObject)obj);
			
			notifyObservers(SELECTION_CHANGED_EVENT);
		}
	}
	
	private void loadCurrentAllPreferenceSettingMap(){		
		EGLFormatProfileRoot buildinformatRoot = getBuildInFormatProfileRoot();
		if(buildinformatRoot != null){
			FormatProfiles profilesroot = buildinformatRoot.getFormatProfiles();
			DefaultProfile defaultprofile = profilesroot.getDefaultProfile();
			EList catList = defaultprofile.getCategory();
			for(Iterator catIt= catList.iterator(); catIt.hasNext();){
				Category category = (Category)catIt.next();
				String categoryID = category.getId();
				EList groupList = category.getGroup();
				for(Iterator grpIt = groupList.iterator(); grpIt.hasNext();){
					Group group = (Group)grpIt.next();
					EList prefList = group.getPref();
					for(Iterator prefIt=prefList.iterator(); prefIt.hasNext();){
						Preference pref = (Preference)prefIt.next();
						String prefId = pref.getId();						
						updateCurrentAllPreferenceSettingMap(categoryID, prefId, getSelectedProfile()) ;	
					}
				}
			}
		}
	}

	private void updateCurrentAllPreferenceSettingMap(EObject profileOrDefaultProfile){
		if(fCurrentAllPreferenceSettingMap.isEmpty()){
			loadCurrentAllPreferenceSettingMap();
		}
		else{
			for(Iterator it = fCurrentAllPreferenceSettingMap.keySet().iterator(); it.hasNext();){
				String key = (String)it.next();
				//parse the key to get the category id and preference id
				String[] result = CodeFormatterConstants.getCategoryIDnPrefID(key);
				updateCurrentAllPreferenceSettingMap(result[0], result[1], profileOrDefaultProfile) ;
			}
		}
	}
	
	private void updateCurrentAllPreferenceSettingMap(String categoryID, String prefId, EObject profileOrDefaultProfile) {
		String valueAttribValue = getPreferenceValue(categoryID, prefId, profileOrDefaultProfile);
		String key = CodeFormatterConstants.getPreferenceSettingKey(categoryID, prefId);
		Object valObj = fCurrentAllPreferenceSettingMap.get(key);
		PreferenceSettingValue prefSettingVal = null;
		if(valObj == null){
			prefSettingVal = new PreferenceSettingValue(valueAttribValue, valueAttribValue);
		}
		else{
			prefSettingVal = (PreferenceSettingValue)valObj;
			prefSettingVal.setInitialValue(valueAttribValue);
			prefSettingVal.setCurrValue(valueAttribValue);							
		}						
		//store in the map
		fCurrentAllPreferenceSettingMap.put(key, prefSettingVal);
	}
	
	
	/**
	 * delete the profile at index index
	 * if this profile is a base to others, others' base will be updated to use this profile's base 
	 * and the settings will be copied to the others appropriately 
	 * 
	 * @param index - the index of the profile that will be deleted
	 * @param updateSelection - if true, this method will update the selection attribute on the <egl:format_profile>
	 * 							to select the one before this deleted profile
	 * @return - true successfully deleted profile
	 * 			 false - did not delete the profile, could be one of the following error
	 * 					it could be index is out of bound 
	 * 					the index is a built in profile
	 * 					EMF could not delete the object
	 */
	public boolean deleteProfile(int index, boolean updateSelection){
		List allprofiles = getAllProfiles();
		try{
			EObject obj = (EObject)allprofiles.get(index);
			if(obj != null){
				if(isProfileBuildIn(obj)) //you can not delete DefaultProfile or build in profile
					return false;		
				else{//it has to be Profile, you can not delete DefaultProfile or build in profile			
					//get the parent node of the to be deleted profile, <egl:format_profiles>
					FormatProfiles formatprofiles = (FormatProfiles)obj.eContainer();
					
					//before we delete the profile, we need to find all the profiles that use this as the base 
					//update its base to this profile's base
					Profile profile2BeDeleted = (Profile)obj;
					String newBase = profile2BeDeleted.getBase();	
					EList settings2BeDeleted = profile2BeDeleted.getSetting();
					
					List affectedProfiles = FormatProfileRootHelper.getProfilesWhoseBaseIs(profile2BeDeleted.getName(), formatprofiles);
					for(Iterator it=affectedProfiles.iterator(); it.hasNext();){
						Profile affectedprofile = (Profile)it.next();
						affectedprofile.setBase(newBase);
						//also we need to copy all the settings in the profile2BeDeleted to affected profile
						//if the affected profile does not have that setting
						for(Iterator its = settings2BeDeleted.iterator(); its.hasNext();){
							Setting setting2BeDeleted = (Setting)its.next();
							Setting overideSetting = FormatProfileRootHelper.getPreferenceSettingByID(affectedprofile, setting2BeDeleted.getCategory(), setting2BeDeleted.getPref());
							if(overideSetting == null){
								//if the setting is not found in the affected profile, clone one
								overideSetting = (Setting)EcoreUtil.copy(setting2BeDeleted);												
								//add the clone one to the affected profie
								affectedprofile.getSetting().add(overideSetting);
							}
						}				
					}			
					
					//remove the profile
					if(formatprofiles.getProfile().remove(obj)){
						notifyObservers(PROFILE_DELETED_EVENT);
						if(updateSelection){
							Object newObj = allprofiles.get(index>0?index-1:0);
							setSelectedProfile(newObj);
						}
						return true;
					}
				}		
			}
			return false;			
		}
		catch(IndexOutOfBoundsException e){
			return false;
		}
	}
	
	/**
	 * 
	 * @param newProfileName
	 * @param baseProfile	- expecting either a Profile or DefaultProfile
	 * @param useNewAsSelection
	 * @return
	 */
	public Profile createNewProfile(String newProfileName, Object baseProfile, boolean useNewAsSelection){
		String baseProfileName = getProfileName(baseProfile, false);
		return createNewProfile(newProfileName, baseProfileName, useNewAsSelection);
	}	
	
	/**
	 * add the newProfile to the workspace format.profile
	 * 
	 * @param newProfileName
	 * @param useNewAsSelection - if true, this method will update the selection attribute on the <egl:format_profile> 
	 * 							  to be this newly created profile
	 * @return Profile - the newly created Profile object
	 */
	private Profile createNewProfile(String newProfileName, String base, boolean useNewAsSelection){
		EGLFormatProfileRoot formatProfileRoot = getCustomWorkspaceFormatProfileRoot(true);
		if(formatProfileRoot != null)
			return createNewProfile(newProfileName, base, useNewAsSelection, formatProfileRoot.getFormatProfiles());
		return null;
	}
	
	private Profile createNewProfile(String newProfileName, String base, boolean useNewAsSelection, FormatProfiles profilesroot){
		Profile newProfile = ProfileFactory.eINSTANCE.createProfile();
		newProfile.setName(newProfileName);
		newProfile.setBase(base);
		newProfile.setIsBuildIn(false);			//custom one, not a build in
		profilesroot.getProfile().add(newProfile);
		notifyObservers(PROFILE_CREATED_EVENT);
		
		if(useNewAsSelection){
			//set the newly created profile as the selection
			setSelectedProfile(newProfile);
		}
		return newProfile;
	}
	
	/**
	 * 
	 * @return list of the profile display names
	 */
	public String[] getAllProfileDisplayNames(){
		List profiles = getAllProfiles();
		int cnt = profiles.size();
		String[] profileDisplayNames = new String[cnt];
		int i=0;
		for(Iterator it = profiles.iterator(); it.hasNext(); i++){
			Object obj = it.next();
			profileDisplayNames[i] = getProfileName(obj, true);
		}
		
		return profileDisplayNames;
	}
	
	/**
	 * 
	 * @param obj - expecting either DefaultProfile or Profile
	 * @param getDisplay - true:  return the displayName
	 * 					 - false: return the profile name attribute 
	 * @return
	 */
	private String getProfileName(Object obj, boolean getDisplay){
		String name = ""; //$NON-NLS-1$
		if(obj instanceof DefaultProfile){
			name = ((DefaultProfile)obj).getName();
			if(getDisplay)
				name += PROFILE_BUILDIN_DISPLAY_SUFFIX;
		}
		else if(obj instanceof Profile){
			Profile profile = (Profile)obj;
			name = profile.getName();
			if(profile.isIsBuildIn() && getDisplay)
				name += PROFILE_BUILDIN_DISPLAY_SUFFIX;
		}		
		return name;
	}	
	
	/**
	 * 
	 * @return List of All the <DefaultProfile> <Profile>, including DefaultProfile, build in profiles and
	 * 		   custom workspace profiles
	 * 			
	 */
	public List getAllProfiles(){
		List profiles = new ArrayList();
		
		//add all the build ins, including the defaultProfile
		EGLFormatProfileRoot buildinProfileRoot = getBuildInFormatProfileRoot();
		if(buildinProfileRoot != null){
			FormatProfiles profilesroot = buildinProfileRoot.getFormatProfiles();
			DefaultProfile defaultProfile = profilesroot.getDefaultProfile();
			if(defaultProfile != null)
				profiles.add(defaultProfile);
			EList otherbuildins = profilesroot.getProfile();
			if(otherbuildins != null && !otherbuildins.isEmpty()){
				profiles.addAll(otherbuildins);
			}
		}
		
		//add all the custom ones
		EGLFormatProfileRoot customWSProfileRoot = getCustomWorkspaceFormatProfileRoot(false);
		if(customWSProfileRoot != null){
			FormatProfiles profilesroot = customWSProfileRoot.getFormatProfiles();
			EList customOnes = profilesroot.getProfile();
			if(customOnes != null && !customOnes.isEmpty())
				profiles.addAll(customOnes);
		}
		return profiles;
	}
	
	public int getProfileIndexByName(String profileName){
		EObject fndProfile = getProfileByName(profileName, false);
		if(fndProfile != null)
			return getProfileIndex(fndProfile);
		return -1;
	}
	
	private int getProfileIndex(EObject defaultProfileOrProfile){
		List allProfiles = getAllProfiles();
		int i=0;
		for(Iterator it=allProfiles.iterator(); it.hasNext();){
			if(defaultProfileOrProfile == it.next())
				return i;
			i++;
		}
		return -1;
	}
	
	public String getDefaultPreviewCode(){
		EGLFormatProfileRoot buildinProfileRoot = getBuildInFormatProfileRoot();
		if(buildinProfileRoot != null){
			FormatProfiles profilesroot = buildinProfileRoot.getFormatProfiles();
			DefaultProfile defaultProfile = profilesroot.getDefaultProfile();
			if(defaultProfile != null){
				return defaultProfile.getPreview().getCode();
			}
		}
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * return the value attribute of a preference in a profile(or DefaultProfile), 
	 * the preference is identified by category id and preference id
	 * 
	 * try to get the value of the preference setting in the current profile, if it's not found
	 * try to get it from its base, then its base till it's found, eventually will be found in the DefaultProfile
	 * 
	 * @param categoryID
	 * @param prefID
	 * @param profileOrDefaultProfile
	 * @return
	 */
	public String getPreferenceValue(String categoryID, String prefID, EObject profileOrDefaultProfile){
		String prefValue = ""; //$NON-NLS-1$
		if(profileOrDefaultProfile instanceof DefaultProfile){
			DefaultProfile defaultProfile = (DefaultProfile)profileOrDefaultProfile;
			Preference pref = FormatProfileRootHelper.getPreferenceByID(defaultProfile, categoryID, prefID);
			if(pref != null)
				prefValue = pref.getValue();
		}
		else if(profileOrDefaultProfile instanceof Profile){
			Profile profile = (Profile)profileOrDefaultProfile;
			Setting setting = FormatProfileRootHelper.getPreferenceSettingByID(profile, categoryID, prefID);
			if(setting != null){
				prefValue = setting.getValue();
			}
			else{	//try to get the value from the base profile
				String baseProfileName = profile.getBase();
				EObject baseProfile = getProfileByName(baseProfileName, true);
				prefValue = getPreferenceValue(categoryID, prefID, baseProfile);
			}
			
		}
		return prefValue;
	}
	
	public void profileRenamed(){
		notifyObservers(PROFILE_RENAMED_EVENT);
	}
	
	/**
	 * undo the current value changes in the map, set the current value back to initial value
	 * 
	 * @param preferenceSetting
	 */
	public void undoPreferenceSettings(Map preferenceSetting){
		for(Iterator it=preferenceSetting.keySet().iterator(); it.hasNext();){
			Object key = it.next();
			ProfileManager.PreferenceSettingValue settingVal = (ProfileManager.PreferenceSettingValue)preferenceSetting.get(key);
			if(settingVal.hasSettingValueChanged()){
				//undo the current value change
				//set the current value back to the initial value
				settingVal.setCurrValue(settingVal.getInitialValue());
			}			
		}
	}
	
	/**
	 * Parse the input file, return the 1st Profile EMF node in the input file
	 * 
	 * NOTE: caller should catch the RuntimeException which means the input file is not a valid egl formatting profile 
	 * it can not be parsed with the EGLFormattingProfile schema
	 * 
	 * @param eglFormattingProfileFile
	 * @return
	 * @throws RuntimeException
	 */
	public Profile getFirstProfileByParsing(File eglFormattingProfileFile){
		IPath importedFilePath = new Path(eglFormattingProfileFile.getPath());
		
		//get the imported File
		EGLFormatProfileRoot importedFormatProfileRoot = FormatProfileRootHelper.getEGLFormatProfileModel(importedFilePath);
		
		FormatProfiles formatprofiles = importedFormatProfileRoot.getFormatProfiles();
		EList profiles = formatprofiles.getProfile();
		if(profiles != null && !profiles.isEmpty())
			return (Profile)profiles.get(0);
		
		return null;
	}
	
	/**
	 * add the input Profile EMF node to the customeWorkspaceFormatProfileRoot
	 * and set this as the current selected profile
	 * 
	 * @param profile2Add - to be added to the customWorkspaceFormatProfileRoot
	 * @param useAsSelection - true, will set the input Profile as selected profile 
	 */
	public void addProfile(Profile profile2Add, boolean useAsSelection){
		EGLFormatProfileRoot formatProfileRoot = getCustomWorkspaceFormatProfileRoot(true);
		if(formatProfileRoot != null){
			FormatProfiles formatprofiles = formatProfileRoot.getFormatProfiles();
			formatprofiles.getProfile().add(profile2Add);
			notifyObservers(PROFILE_CREATED_EVENT);
			
			if(useAsSelection)
				setSelectedProfile(profile2Add);
		}
	}	
	
	public void exportSelectedProfile(File exportedFile){
		IPath exportedFilePath = new Path(exportedFile.getPath());
		
		//create a new egl:format_profiles root
		EGLFormatProfileRoot exportedFormatProfileRoot = FormatProfileRootHelper.createNewEGLFormatProfileModel(exportedFilePath);
		
		FormatProfiles formatprofiles = exportedFormatProfileRoot.getFormatProfiles();		
		Profile exportProfile = ProfileFactory.eINSTANCE.createProfile();
		formatprofiles.getProfile().add(exportProfile);
		
		//======================= set all the attributes on the exported profile
		//set name
		DefaultProfile defaultProfile = getBuildInFormatProfileRoot().getFormatProfiles().getDefaultProfile();
		String defaultBuildInProfileName = defaultProfile.getName();		
		EObject selProfile = getSelectedProfile();
		if(selProfile instanceof Profile)
			exportProfile.setName(((Profile)selProfile).getName());	
		else if(selProfile instanceof DefaultProfile)
			exportProfile.setName(defaultBuildInProfileName + "_exported");		 //$NON-NLS-1$
		//always use default profile name as base
		exportProfile.setBase(defaultBuildInProfileName);
		//always not a built in
		exportProfile.setIsBuildIn(false);
		
		//=============== create all the settings for the exported profile
		Map currPrefs = getCurrentPreferenceSettingMap();
		for(Iterator it = currPrefs.keySet().iterator(); it.hasNext();){
			String key = (String)it.next();
			//parse the key to get the category id and preference id
			String[] result = CodeFormatterConstants.getCategoryIDnPrefID(key);
		
			Setting setting = ProfileFactory.eINSTANCE.createSetting();
			setting.setCategory(result[0]);
			setting.setPref(result[1]);
			
			PreferenceSettingValue valObj = (PreferenceSettingValue)currPrefs.get(key);
			setting.setValue(valObj.getCurrentValue());
			
			exportProfile.getSetting().add(setting);
		}
		
		//persist to disk
		FormatProfileRootHelper.saveEGLFormatProfile(exportedFilePath, exportedFormatProfileRoot);
	}
	
	/**
	 * update the EMF model of the profile with all the values in the map
	 * check to see if the value in the map is a new value(changed from initial value), 
	 * if so,
	 * 		check to see if the profile already has this setting, set its new value
	 * 		otherwise, create a new setting with the new value, add to the profile
	 * 		
	 * 		changes made in a base profile should not affect the profile that's based on it
	 * 		check to see if the profile is a base profile to other profiles, if so, the affected profile should 
	 * 		have the initial value, instead of the new changed value
	 * 
	 * @param profile
	 * @param preferenceSetting - Map, key is a string in the format of CategoryID.prefID
	 * 								   value is initValue and currentValue
	 */
	public void updateProfileSettings(Profile profile, Map preferenceSetting){
		boolean isSettingChanged = false;
		FormatProfiles formatprofiles = (FormatProfiles)profile.eContainer();
		for(Iterator it = preferenceSetting.keySet().iterator(); it.hasNext();){
			Object key = it.next();
			ProfileManager.PreferenceSettingValue settingVal = (ProfileManager.PreferenceSettingValue)preferenceSetting.get(key);
			if(settingVal.hasSettingValueChanged()){
				isSettingChanged = true;
				//need to modify the setting to this profile
				//parse the key to categoryID and prefID
				String[] result = CodeFormatterConstants.getCategoryIDnPrefID((String)key);				
				//try to find if this profile already has this setting
				Setting setting = FormatProfileRootHelper.getPreferenceSettingByID(profile, result[0], result[1]);
				if(setting != null){
					setting.setValue(settingVal.getCurrentValue());
				}
				else{//create a new setting, then add to this profile
					setting = ProfileFactory.eINSTANCE.createSetting();
					setting.setCategory(result[0]);
					setting.setPref(result[1]);
					setting.setValue(settingVal.getCurrentValue());
					profile.getSetting().add(setting);
				}
				
				//changes made in a base profile should not affect the profile that's based on it
				//when we change the value in this profile, which is a base profile to others
				//the others profile's value should not change, it should be the initial value 
				List affectedProfiles = FormatProfileRootHelper.getProfilesWhoseBaseIs(profile.getName(), formatprofiles);
				for(Iterator ita=affectedProfiles.iterator(); ita.hasNext();){
					Profile affectedprofile = (Profile)ita.next();
					//set the setting with its initial value
					Setting settingaffected = FormatProfileRootHelper.getPreferenceSettingByID(affectedprofile, result[0], result[1]);
					//if the affect profile has not override this setting, we need to create one with the initial value
					if(settingaffected == null){
						settingaffected = ProfileFactory.eINSTANCE.createSetting();
						settingaffected.setCategory(result[0]);
						settingaffected.setPref(result[1]);
						settingaffected.setValue(settingVal.getInitialValue());
						affectedprofile.getSetting().add(settingaffected);
					}
				}				
			}
		}
		
		if(isSettingChanged)
			notifyObservers(SETTINGS_CHANGED_EVENT);
	}
	
	/**
	 * The possible events for observers listening to this class.
	 */
	public final static int SELECTION_CHANGED_EVENT= 1;
	public final static int PROFILE_DELETED_EVENT= 2;
	public final static int PROFILE_RENAMED_EVENT= 3;
	public final static int PROFILE_CREATED_EVENT= 4;
	public final static int SETTINGS_CHANGED_EVENT= 5;
	
	protected void notifyObservers(int message) {
		setChanged();
		notifyObservers(new Integer(message));
	}

	
}
