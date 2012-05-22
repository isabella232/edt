/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.utils;

public class InternUtil {

    private static final int NUM_COMPONENTS = 10; // the typical maximum number of components in a string array
    private static final int NUM_STRINGS = 5000;
    private static final int NUM_STRINGARRAYS = 250;
    
    private static StringArrayInternUtil[] STRINGARRAYS = new StringArrayInternUtil[NUM_COMPONENTS];
    private static StringInternUtil STRINGS = new StringInternUtil(NUM_STRINGS);
    
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    static {
        for(int i = 0; i < STRINGARRAYS.length; i++) {
            STRINGARRAYS[i] = new StringArrayInternUtil(NUM_STRINGARRAYS);
        }
    }
    
    public static String intern(String string) {
        return STRINGS.intern(string);
    }
    
    public static String[] intern(String[] strings) {
    	return STRINGARRAYS[strings.length > NUM_COMPONENTS - 1 ? 0 : strings.length].intern(strings); 
    }

	public static String internCaseSensitive(String identifier) {
		return identifier.intern();
	}

}

class StringArrayInternUtil {

    private String[][] stringArrays;
    private int size;
    private int capacity;
    private int threshold;
    
    public StringArrayInternUtil(int size) {
        // Protect against small tables
        if(size < 37) size = 37;
        
        this.stringArrays = new String[(int) (size * 1.5f)][];
        this.capacity = stringArrays.length;
        this.threshold = size;
    }
    
    public String[] intern(String[] stringArray) {
        // Guard against empty string arrays
        if(stringArray.length == 0) {
            return InternUtil.EMPTY_STRING_ARRAY;  
        }
        
        // Compute the index into the table
        int index = hashCode(stringArray) % capacity;
        
        // Linear probe in the table, if found return the existing entry
        String[] probe;
        while((probe = stringArrays[index]) != null) {
            if(equalsIgnoreCase(stringArray, probe)) return probe;
            if(++index == capacity) index = 0;
        }
        
        // Intern components of string array before putting into the table
        for(int i = 0; i < stringArray.length; i++) {
            stringArray[i] = InternUtil.intern(stringArray[i]);
        }
        stringArrays[index] = stringArray;
        
        // Rehash table if threshold exceeded
        if(++size == threshold) rehash();
        
        // Return the just inserted component-interned string arrays
        return stringArray;
    }
    
    private int hashCode(String[] stringArray) {
        // Use the case-insensitive hashcode of the last component as the hash code of the entire array
        String lastComponent = stringArray[stringArray.length - 1];
        
        int result = 0;
        char[] charArray = lastComponent.toCharArray();
        for(int i = 0; i < charArray.length; i++) {
            char current = charArray[i];
            result = 31 * result + Character.toLowerCase(Character.toUpperCase(current));
        }
        
        return result & 0x7FFFFFFF;
    }
    
    private boolean equalsIgnoreCase(String[] stringArray1, String[] stringArray2) {
        if(stringArray1.length != stringArray2.length) return false;
        
        for(int i = 0; i < stringArray1.length; i++) {
            if(!stringArray1[i].equalsIgnoreCase(stringArray2[i])) return false;
        }
        return true;
    }
    
    private void rehash() {
        int newCapacity = this.capacity * 2;
        
        // Copy tables, note that table entries are already unique, so no need to compare string arrays during probing
        String[][] newStringArrays = new String[newCapacity][];
        for(int i = 0; i < this.stringArrays.length; i++) {
            String[] current = this.stringArrays[i];
            if(current != null) {
                int index = hashCode(current) % newCapacity;
                while(newStringArrays[index] != null) {
                    if(++index == newCapacity) index = 0;
                }
                newStringArrays[index] = current;
            }
        }
        
        // Size is unchanged
        this.stringArrays = newStringArrays;
        this.capacity = newCapacity;
        this.threshold *= 2;
    }
    
}

class StringInternUtil {
    
     private String[] strings;
     private int size;
     private int capacity;
     private int threshold;
     
     public StringInternUtil(int size) {
         // Protect against small tables
         if(size < 37) size = 37;
         
         this.strings = new String[(int) (size * 1.5f)];
         this.capacity = strings.length;
         this.threshold = size;
     }
     
     public String intern(String string) {
         // Compute the index into the table
         int index = hashCode(string) % capacity;
         
         // Linear probe in the table, if found return the existing entry
         String probe;
         while((probe = strings[index]) != null) {
             if(string.equalsIgnoreCase(probe)) return probe;
             if(++index == capacity) index = 0;
         }
         
         strings[index] = string;
         
         // Rehash table if threshold exceeded
         if(++size == threshold) rehash();
         
         // Return the just inserted string
         return string;
     }
     
     private int hashCode(String string) {
         int result = 0;
         char[] charArray = string.toCharArray();
         for(int i = 0; i < charArray.length; i++) {
             char current = charArray[i];
             result = 31 * result + Character.toLowerCase(Character.toUpperCase(current));
         }
         
         return result & 0x7FFFFFFF;
     }
     
     private void rehash() {
         int newCapacity = this.capacity * 2;
         
         // Copy tables, note that table entries are already unique, so no need to compare strings during probing
         String[] newStrings = new String[newCapacity];
         for(int i = 0; i < this.strings.length; i++) {
             String current = this.strings[i];
             if(current != null) {
                 int index = hashCode(current) % newCapacity;
                 while(newStrings[index] != null) {
                     if(++index == newCapacity) index = 0;
                 }
                 newStrings[index] = current;
             }
         }
         
         // Size is unchanged
         this.strings = newStrings;
         this.capacity = newCapacity;
         this.threshold *= 2;
     }
}
