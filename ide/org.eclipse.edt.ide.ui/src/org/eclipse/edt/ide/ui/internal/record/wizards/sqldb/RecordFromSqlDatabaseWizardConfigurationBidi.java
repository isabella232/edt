
package org.eclipse.edt.ide.ui.internal.record.wizards.sqldb;

import java.util.HashMap;

import org.eclipse.edt.compiler.internal.EGLBasePlugin;

public class RecordFromSqlDatabaseWizardConfigurationBidi extends RecordFromSqlDatabaseWizardConfiguration {
	
	//@bd1a start
	boolean isBidi = false;
	private boolean Bidi;
	private boolean bidiHashing;
	private HashMap bidiAttributesHashMap = null; // hashtable to hold bidi attributes for Tables
	public static final String DB_KEY = new String("DATABASE_BIDI_ATTRIBUTES");
	//@bd1a end
	
	public static boolean BidiSettingSelected = false; //@bd3a
	
	//@bd2a Start
	private HashMap bidiContentHashMap;
	private static String BCT_KEY = "bct";
	private static String BCT_NEEDED_KEY = "bctNeeded";
	private static String BIDI_DISABLED_KEY = "disableBIDI";
	private static String BIDI_DISABLED_META_KEY = "disableMetadata"; //@bd3a
	private static String OVERRIDE_KEY = "override";
	private static String DB_VISUAL_KEY = "dbVisual";
	private static String DB_RTL_KEY = "dbRTL";
	private static String DB_SYMSWAP_KEY = "dbSymSwap";
	private static String DB_NUMSWAP_KEY = "dbNumSwap";
	private static String CLIENT_RTL_KEY = "clientRTL";
	private boolean clientVisual = false;
	public static int BCT_LENGTH = 5;
	//@bd2a End
	 

	public RecordFromSqlDatabaseWizardConfigurationBidi() {
		super();
	}

	@Override
	protected void setDefaultValues() {
		super.setDefaultValues();
		
		//@bd1a start
		isBidi=EGLBasePlugin.getPlugin().getPreferenceStore().getBoolean
		(EGLBasePlugin.BIDI_ENABLED_OPTION);

		if (isBidi) {
			bidiAttributesHashMap = new HashMap();
			bidiContentHashMap = new HashMap(); //@bd2a
		}
		//@bd1a end
	}
	
	//@bd2a start
	/**
	 * @return true if Bidi flag is on
	 */
	public boolean isBidi() {
		return Bidi;
	}

	/**
	 * @param Bidi is set to true if Bidi flag is on
	 */
	public void setBidi(boolean Bidi) {
		this.Bidi = Bidi;
	}
	
	/**
	 * Setter & getter for bidiHashing button.
	 * 
	 * @param newbidiHashing: this hashMap hold complete Bidi attributes
	 */
	public void setBidiHashing(boolean newbidiHashing) {
		this.bidiHashing = newbidiHashing;
	}
	
	/**
	 * Getter for bidiHashing button
	 * 
	 * @return true if Bidi hashing flag is on
	 */
	public boolean isBidiHashing() {
		return bidiHashing;
	}
	//@bd2a end

	//@bd1a start
	/**
	 * Set Bidi attributes for the target object
	 * 
	 * @param target the target object (database or table)
	 * @param bidiAttributes the bidi attributes to be set
	 */
	public void setBidiAttributes(Object target, Object bidiAttributes) {
		bidiAttributesHashMap.put(target, bidiAttributes);
	}

	/**
	 * Returns the Bidi attributes of target object.
	 * 
	 * @param target the target object (database or table)
	 * @return the bidi attributes of the object
	 */
	public Object getBidiAttributes(Object target) {
		return bidiAttributesHashMap.get(target);
	}

	/**
	 * Sets the BidiAttributesHashMap.
	 * 
	 * @param newBidiAttributesHashMap
	 */
	public void setBidiAttributesHashMap(HashMap newBidiAttributesHashMap) {
		if (null != newBidiAttributesHashMap)
			bidiAttributesHashMap.putAll(newBidiAttributesHashMap);
	}

	/**
	 * @return the Bidi attributes hashMap
	 */
	public HashMap getBidiAttributesHashMap() {
		return bidiAttributesHashMap;
	}
	//@bd1a end
	
	//@bd2a Start
	
	/**
	 * Sets the BidiContentHashMap.
	 * 
	 * @param newBidiContentHashMap
	 */
	public void setBidiContentHashMap(HashMap newBidiContentHashMap) {
		bidiContentHashMap = newBidiContentHashMap;
	}
	
	/**
	 * @return BidiContentHashMap
	 */
	public HashMap getBidiContentHashMap() {
		return bidiContentHashMap;
	}
	
	/**
	 * Set bctNeeded attribute for the target object
	 * 
	 * @param target the target object (table)
	 * @param newBct the bct attributes to be set
	 */
	public void setBctNeeded(Object target, boolean bctNeeded) {
		if(bidiContentHashMap !=null ){
			HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
			HashMap newTableAttrs =null;
			if(bidiAttrMap== null && bctNeeded)
				newTableAttrs = new HashMap();
			else if(bidiAttrMap!= null)
				newTableAttrs = new HashMap(bidiAttrMap);
			
			if(newTableAttrs!= null){
				newTableAttrs.put(BCT_NEEDED_KEY, bctNeeded);
				bidiContentHashMap.put(target, newTableAttrs);
			}
		}
	}

	/**
	 * Returns the bctNeeded attribute of target object.
	 * 
	 * @param target the target object (table)
	 * @return the bct attributes of the object
	 */
	public boolean isBctNeeded(Object target) {
		if(bidiContentHashMap ==null )
			return false;
		HashMap bidiAttrMap = ((HashMap) bidiContentHashMap.get(target));
		if(bidiAttrMap == null)
			return false;
		return ((Boolean)bidiAttrMap.get(BCT_NEEDED_KEY));
	}
	
	/**
	 * Set bct attribute for the target object
	 * 
	 * @param target the target object (database, table or column)
	 * @param newBct the bct attributes to be set
	 */
	public void setBct(Object target, String newBct) {
		HashMap bidiAttrMap = (HashMap) bidiContentHashMap.get(target);
		HashMap newTableAttrs;
		if(bidiAttrMap== null)
			newTableAttrs = new HashMap();
		else
			newTableAttrs = new HashMap(bidiAttrMap);
		
		newTableAttrs.put(BCT_KEY, newBct);
		bidiContentHashMap.put(target, newTableAttrs);
	}

	/**
	 * Returns the bct attribute of target object.
	 * 
	 * @param target the target object (database, table or cloumn)
	 * @return the bct attributes of the object
	 */
	public String getBct(Object target) {
		if(bidiContentHashMap ==null )
			return null;
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		if(bidiAttrMap == null)
			return null;
		return ((String)bidiAttrMap.get(BCT_KEY));
	}
	
	/**
	 * remove the bct attribute from the target object.
	 * 
	 * @param target the target object (table or cloumn)
	 */
	public void removeBct(Object target) {
		if(bidiContentHashMap !=null ){
			HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
			if(bidiAttrMap != null){
				bidiAttrMap.remove(BCT_KEY);
				bidiContentHashMap.put(target, bidiAttrMap);
			}
		}		
	}
	
	/**
	 * Set dbVisual attribute for the target object
	 * 
	 * @param target the target object (database, table or column)
	 * @param isDBVisual the clientRTL attribute to be set
	 */
	public void setDBVisual(Object target, boolean isDBVisual) {
		if(bidiContentHashMap !=null ){
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		HashMap newTableAttrs;
		if(bidiAttrMap== null)
			newTableAttrs = new HashMap();
		else
			newTableAttrs = new HashMap(bidiAttrMap);
		
		newTableAttrs.put(DB_VISUAL_KEY, isDBVisual);
		bidiContentHashMap.put(target, newTableAttrs);
		}
	}

	/**
	 * Returns the dbRTL attribute of target object.
	 * 
	 * @param target the target object (database, table or cloumn)
	 * @return the dbRTL attribute of the object
	 */
	public boolean isDBVisual(Object target) {
		if(bidiContentHashMap ==null )
			return false;
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		if(bidiAttrMap == null)
			return false;
		if(((Boolean)bidiAttrMap.get(DB_VISUAL_KEY))!=null)
			return ((Boolean)bidiAttrMap.get(DB_VISUAL_KEY));
		return false;
	}
	
	/**
	 * Set dbRTL attribute for the target object
	 * 
	 * @param target the target object (database, table or column)
	 * @param isDBRTL the clientRTL attribute to be set
	 */
	public void setDBRTL(Object target, boolean isDBRTL) {
		if(bidiContentHashMap !=null ){
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		HashMap newTableAttrs;
		if(bidiAttrMap== null)
			newTableAttrs = new HashMap();
		else
			newTableAttrs = new HashMap(bidiAttrMap);
		
		newTableAttrs.put(DB_RTL_KEY, isDBRTL);
		bidiContentHashMap.put(target, newTableAttrs);
		}
	}

	/**
	 * Returns the dbRTL attribute of target object.
	 * 
	 * @param target the target object (database, table or cloumn)
	 * @return the dbRTL attribute of the object
	 */
	public boolean isDBRTL(Object target) {
		if(bidiContentHashMap ==null )
			return false;
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		if(bidiAttrMap == null)
			return false;
		if(((Boolean)bidiAttrMap.get(DB_RTL_KEY))!=null)
			return ((Boolean)bidiAttrMap.get(DB_RTL_KEY));
		return false;
	}
	
	/**
	 * Set dbSymSwap attribute for the target object
	 * 
	 * @param target the target object (database, table or column)
	 * @param isDBSymSwap the clientRTL attribute to be set
	 */
	public void setDBSymSwap(Object target, boolean isDBSymSwap) {
		if(bidiContentHashMap !=null ){
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		HashMap newTableAttrs;
		if(bidiAttrMap== null)
			newTableAttrs = new HashMap();
		else
			newTableAttrs = new HashMap(bidiAttrMap);
		
		newTableAttrs.put(DB_SYMSWAP_KEY, isDBSymSwap);
		bidiContentHashMap.put(target, newTableAttrs);
		}
	}

	/**
	 * Returns the dbSymSwap attribute of target object.
	 * 
	 * @param target the target object (database, table or cloumn)
	 * @return the dbSymSwap attribute of the object
	 */
	public boolean isDBSymSwap(Object target) {
		if(bidiContentHashMap ==null )
			return false;
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		if(bidiAttrMap == null)
			return false;
		if(((Boolean)bidiAttrMap.get(DB_SYMSWAP_KEY))!=null)
			return ((Boolean)bidiAttrMap.get(DB_SYMSWAP_KEY));
		return false;
	}
	
	/**
	 * Set dbNumSwap attribute for the target object
	 * 
	 * @param target the target object (database, table or column)
	 * @param isDBNumSwap the clientRTL attribute to be set
	 */
	public void setDBNumSwap(Object target, boolean isDBNumSwap) {
		if(bidiContentHashMap !=null ){
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		HashMap newTableAttrs;
		if(bidiAttrMap== null)
			newTableAttrs = new HashMap();
		else
			newTableAttrs = new HashMap(bidiAttrMap);
		
		newTableAttrs.put(DB_NUMSWAP_KEY, isDBNumSwap);
		bidiContentHashMap.put(target, newTableAttrs);
		}
	}

	/**
	 * Returns the dbNumSwap attribute of target object.
	 * 
	 * @param target the target object (database, table or cloumn)
	 * @return the dbNumSwap attribute of the object
	 */
	public boolean isDBNumSwap(Object target) {
		if(bidiContentHashMap ==null )
			return false;
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		if(bidiAttrMap == null)
			return false;
		if(((Boolean)bidiAttrMap.get(DB_NUMSWAP_KEY))!=null)
			return ((Boolean)bidiAttrMap.get(DB_NUMSWAP_KEY));
		return false;
	}
	
	/**
	 * Set clientVisual attribute for the target object
	 * 
	 * @param target the target object (database)
	 * @param isClientVisual the clientVisual attribute to be set
	 */
	public void setClientVisual(Object target, boolean isClientVisual) {
		clientVisual = isClientVisual;
	}

	/**
	 * Returns the clientVisual attribute of target object.
	 * 
	 * @param target the target object (database)
	 * @return the clientVisual attribute of the object
	 */
	public boolean isClientVisual(Object target) {
		return clientVisual;
	}
	
	/**
	 * Set clientRTL attribute for the target object
	 * 
	 * @param target the target object (database, table or column)
	 * @param isClientRTL the clientRTL attribute to be set
	 */
	public void setClientRTL(Object target, boolean isClientRTL) {
		if(bidiContentHashMap !=null ){
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		HashMap newTableAttrs;
		if(bidiAttrMap== null)
			newTableAttrs = new HashMap();
		else
			newTableAttrs = new HashMap(bidiAttrMap);
		
		newTableAttrs.put(CLIENT_RTL_KEY, isClientRTL);
		bidiContentHashMap.put(target, newTableAttrs);
		}
	}

	/**
	 * Returns the clientRTL attribute of target object.
	 * 
	 * @param target the target object (database, table or cloumn)
	 * @return the clientRTL attribute of the object
	 */
	public boolean isClientRTL(Object target) {
		if(bidiContentHashMap ==null )
			return false;
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		if(bidiAttrMap == null)
			return false;
		if(((Boolean)bidiAttrMap.get(CLIENT_RTL_KEY))!=null)
			return ((Boolean)bidiAttrMap.get(CLIENT_RTL_KEY));
		return false;
	}
	

	/**
	 * Returns the reverseNeeded attribute of target object.
	 * 
	 * @param target the target object (database, table or cloumn)
	 * @return the reverseNeeded attribute of the object
	 */
	public boolean isReverseNeeded(Object target) {
		return isClientVisual(target) && (isClientRTL(target) ^ isDBRTL(target));
	}
	
	/**
	 * Set override attribute for the target object
	 * 
	 * @param target the target object (table or column)
	 * @param isOverride the clientRTL attribute to be set
	 */
	public void setOverride(Object target, boolean isOverride) {
		if(bidiContentHashMap !=null ){
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		HashMap newTableAttrs;
		if(bidiAttrMap== null)
			newTableAttrs = new HashMap();
		else
			newTableAttrs = new HashMap(bidiAttrMap);
		
		newTableAttrs.put(OVERRIDE_KEY, isOverride);
		bidiContentHashMap.put(target, newTableAttrs);
		}
	}

	/**
	 * Returns the override attribute of target object.
	 * 
	 * @param target the target object (table or cloumn)
	 * @return the override attribute of the object
	 */
	public boolean isOverride(Object target) {
		if(bidiContentHashMap ==null )
			return false;
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		if(bidiAttrMap == null)
			return false;
		if(((Boolean)bidiAttrMap.get(OVERRIDE_KEY))!=null)
			return ((Boolean)bidiAttrMap.get(OVERRIDE_KEY));
		return false;
	}
	
	/**
	 * Set bidiDisabled attribute for the target table
	 * 
	 * @param target the target Object (table)
	 * @param isBidiDisabled the clientRTL attribute to be set
	 */
	public void setBidiDisabled(Object target, boolean isBidiDisabled) {
		if(bidiContentHashMap !=null ){
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		HashMap newTableAttrs;
		if(bidiAttrMap== null)
			newTableAttrs = new HashMap();
		else
			newTableAttrs = new HashMap(bidiAttrMap);
		
		newTableAttrs.put(BIDI_DISABLED_KEY, isBidiDisabled);
		bidiContentHashMap.put(target, newTableAttrs);
		}
	}

	/**
	 * Returns the bidiDisabled attribute of table object.
	 * 
	 * @param target the target object (table)
	 * @return the bidiDisabled attribute of the object
	 */
	public boolean isBidiDisabled(Object target) {
		if(bidiContentHashMap ==null )
			return false;
		HashMap bidiAttrMap = (HashMap)bidiContentHashMap.get(target);
		if(bidiAttrMap == null)
			return false;
		if(((Boolean)bidiAttrMap.get(BIDI_DISABLED_KEY))!=null)
			return ((Boolean)bidiAttrMap.get(BIDI_DISABLED_KEY));
		return false;
	}
	
	/**
	 * Set TableInherits attribute for the target table
	 * 
	 * @param target the target Object (table)
	 * @param TableInherits attribute to be set
	 */
	public void setMetadataInherit(Object target, String isInherit) {
		if(bidiAttributesHashMap !=null ){
			HashMap bidiAttrMap = (HashMap)bidiAttributesHashMap.get(target);
			HashMap newTableAttrs;
			if(bidiAttrMap== null)
				newTableAttrs = new HashMap();
			else
				newTableAttrs = new HashMap(bidiAttrMap);
			newTableAttrs.put("TableInherits", isInherit);
			
			bidiAttributesHashMap.put(target, newTableAttrs);
		}		
	}
	
	/**
	 * Returns the TableInherits attribute of table object.
	 * 
	 * @param target the target object (table)
	 * @return the TableInherits attribute of the object
	 */
	public String getMetadataInherit(Object target) {
		if(bidiAttributesHashMap ==null )
			return "";
		HashMap bidiAttrMap = (HashMap)bidiAttributesHashMap.get(target);
		if(bidiAttrMap == null)
			return "";
		if(((String)bidiAttrMap.get("TableInherits"))!=null)
			return (((String)bidiAttrMap.get("TableInherits")));
		return "";
	}
	//@bd2a End
	
	//@bd3a Start
	/**
	 * Set disableMetadata attribute for the target database or table
	 * 
	 * @param target the target Object (table)
	 * @param TableInherits attribute to be set
	 */
	public void setDisableMetadata(Object target, boolean isDisabled) {
		if(bidiAttributesHashMap !=null ){
			HashMap bidiAttrMap = (HashMap)bidiAttributesHashMap.get(target);
			HashMap newTableAttrs;
			if(bidiAttrMap== null)
				newTableAttrs = new HashMap();
			else
				newTableAttrs = new HashMap(bidiAttrMap);
			newTableAttrs.put(BIDI_DISABLED_META_KEY, isDisabled);
			
			bidiAttributesHashMap.put(target, newTableAttrs);
		}		
	}
	
	/**
	 * Returns disableMetadata attribute for the target database or table.
	 * 
	 * @param target the target object (database or table)
	 * @return the disableMetadata attribute of the object
	 */
	public boolean isDisableMetadata(Object target) {
		if(bidiAttributesHashMap ==null )
			return false;
		HashMap bidiAttrMap = (HashMap)bidiAttributesHashMap.get(target);
		if(bidiAttrMap == null)
			return false;
		if(((Boolean)bidiAttrMap.get(BIDI_DISABLED_META_KEY))!=null)
			return (((Boolean)bidiAttrMap.get(BIDI_DISABLED_META_KEY)));
		return false;
	}
	//@bd3a End

}
