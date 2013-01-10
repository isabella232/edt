/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author winghong
 */
public class SoftLRUCache {
    
    private static final boolean DEBUG = false;
    
    private static class SoftReferenceWithKey extends SoftReference {
        Object key;
        
        SoftReferenceWithKey(Object key, Object referent, ReferenceQueue q) {
            super(referent, q);
            this.key = key;
        }
    }
    private Map softReferences;
    
    private ReferenceQueue referenceQueue;

    public SoftLRUCache() {
    	this.softReferences = new LinkedHashMap(16, 0.75f, true);
    	this.referenceQueue = new ReferenceQueue();
    }
    
    public SoftLRUCache(final int maxSize) {
        this.softReferences = new LinkedHashMap((int)(maxSize * 0.75f), 0.75f, true) {
			private static final long serialVersionUID = 1503311551124552810L;

			protected boolean removeEldestEntry(Entry eldest) {
                return size() > maxSize;
            }
        };
        this.referenceQueue = new ReferenceQueue();
    }
    
    public void put(Object key, Object value) {
        if(DEBUG) System.out.println("Put: " + key); //$NON-NLS-1$
        
        removeClearedReferences();
        
        softReferences.put(key, new SoftReferenceWithKey(key, value, referenceQueue));
    }
    
    public Object get(Object key) {
        removeClearedReferences();
        
        SoftReferenceWithKey ref = (SoftReferenceWithKey) softReferences.get(key);
        
        if(ref == null) {
            return null;
        }
        
        Object result = ref.get();

        if(DEBUG && result == null) System.out.println("Key " + key + " has been cleared"); //$NON-NLS-1$ //$NON-NLS-2$
        
        return result;
    }
    
    public void remove(Object key) {
    	removeClearedReferences();
        
        softReferences.remove(key);		
	}
    
    private void removeClearedReferences() {
        SoftReferenceWithKey ref;
        while((ref = (SoftReferenceWithKey) referenceQueue.poll()) != null) {
            if(DEBUG) System.out.println("Removing cleared soft reference"); //$NON-NLS-1$
            softReferences.remove(ref.key);
        }
    }
    
    public static void main(String[] args) {
        SoftLRUCache cache = new SoftLRUCache(5);
        
        for(int i = 0; i < 1000; i++) {
            Integer key = new Integer((int) Math.floor(Math.random() * 100));

            if(Math.round(Math.random()) == 0) {
	            int sizeInKilobytes = (int) Math.floor(Math.random() * 100);
	            int[] value = new int[sizeInKilobytes * 1024];
	            cache.put(key, value);
            }
            else {
                if(cache.get(key) != null) {
                    System.out.println("Get: " + key); //$NON-NLS-1$
                }
                else {
                    System.out.println("Key Not found: " + key); //$NON-NLS-1$
                }
            }
        }
    }
}
