/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.dli.stmtFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 *  
 */
public class DLIDataContainerFactory implements IDLIDataContainerFactory {

    IDataBinding psbBinding;

    IDataBinding[] segmentBindings;

    DLIDataContainer dataContainer;

    HashMap bindingToDLISegmentRecordMap = new HashMap();
    
    private static final String[] EGLIODLI = new String[] {"egl", "io", "dli"};

    private abstract class Data implements IData {

        ITypeBinding type;

        String name;

        public boolean isDLISegmentRecord() {
            return false;
        }

        public boolean isPSBRecord() {
            return false;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ITypeBinding getType() {
            return type;
        }

        public void setType(ITypeBinding type) {
            this.type = type;
        }
    }

    private class DLISegmentRecord extends Data implements IDLISegmentRecord {

        String segmentName;

        String keyItemName;

        String keyItemFieldName;

        String hostVarQualifier;
        
        boolean array;

        public boolean isDLISegmentRecord() {
            return true;
        }

        public String getHostVarQualifier() {
            if (hostVarQualifier == null) {
            	if (isArray() && (getType() != null)) {
            		return getType().getName();
            	}
                return getName();
            }
            return hostVarQualifier;
        }

        public void setHostVarQualifier(String hostVarQualifier) {
            this.hostVarQualifier = hostVarQualifier;
        }

        public String getKeyItemName() {
            return keyItemName;
        }

        public void setKeyItemName(String keyItemName) {
            this.keyItemName = keyItemName;
        }

        public String getSegmentName() {
            if (segmentName == null) {
                return getType().getName();
            }
            return segmentName;
        }

        public void setSegmentName(String segmentName) {
            this.segmentName = segmentName;
        }

        public String getKeyItemFieldName() {
            return keyItemFieldName;
        }

        public void setKeyItemFieldName(String keyItemFieldName) {
            this.keyItemFieldName = keyItemFieldName;
        }

		public boolean typeMatches(ITypeBinding compType) {
			
			if (getType() == compType) {
				return true;
			}
			
			if (!Binding.isValidBinding(getType()) || !Binding.isValidBinding(compType)) {
				return false;
			}
			
			if (InternUtil.intern(getType().getName()) != InternUtil.intern(compType.getName())) {
				return false;
			}
			
			if (getType().getKind() == compType.getKind() && getType() instanceof PartBinding) {
				String[] pkgName = InternUtil.intern(((PartBinding) getType()).getPackageName());
				String[] compPkgName = InternUtil.intern(((PartBinding) compType).getPackageName());
				return pkgName == compPkgName;
			}
			return false;
		}

		public boolean isArray() {
			return array;
		}

		public void setArray(boolean array) {
			this.array = array;
		}
    }

    private class PCB implements IPCB {

        IRelationship[] eglHierarchy;

        String secondaryIndex;

        String secondaryIndexItemName;

        EnumerationDataBinding pCBType;

        public IRelationship[] getHierarchy() {
            return eglHierarchy;
        }

        public void setHierarchy(IRelationship[] relationship) {
            eglHierarchy = relationship;
        }

        public String getSecondaryIndex() {
            return secondaryIndex;
        }

        public void setSecondaryIndex(String secondaryIndex) {
            this.secondaryIndex = secondaryIndex;
        }

        public String getSecondaryIndexItemName() {
            if (secondaryIndexItemName == null) {
                return getSecondaryIndex();
            }
            return secondaryIndexItemName;
        }

        public void setSecondaryIndexItemName(String secondaryIndexItemName) {
            this.secondaryIndexItemName = secondaryIndexItemName;
        }

        public EnumerationDataBinding getPCBType() {
            return pCBType;
        }

        public void setPCBType(EnumerationDataBinding type) {
            pCBType = type;
        }
    }

    private class PCBStructure extends Data implements IPCBStructure {

        String redefines;

        IPSBRecord container;

        IPCB PCB;

        public IPSBRecord getContainer() {
            return container;
        }

        public void setContainer(IPSBRecord container) {
            this.container = container;
        }

        public IPCB getPCB() {
            if (PCB == null && getContainer() != null && getRedefines() != null) {
                PCBStructure redef = (PCBStructure) getContainer().getPCBStructureNamed(getRedefines());
                if (redef != null && redef.getRedefines() == null) {
                    //prevent possible loop with illegal redefines
                    return redef.getPCB();
                }
            }
            return PCB;
        }

        public void setPCB(IPCB eglpcb) {
            PCB = eglpcb;
        }

        public String getRedefines() {
            return redefines;
        }

        public void setRedefines(String redefines) {
            this.redefines = redefines;
        }
    }

    private class DLIDataContainer implements IDLIDataContainer {
        IPSBRecord psbRecord;

        IDLISegmentRecord[] segments;

        public IPSBRecord getPSB() {
            return psbRecord;
        }

        public void setPSB(IPSBRecord psbRecord) {
            this.psbRecord = psbRecord;
        }

        public IDLISegmentRecord[] getSegments() {
            return segments;
        }

        public void setSegments(IDLISegmentRecord[] segments) {
            this.segments = segments;
        }
    }

    private class PSBRecord extends Data implements IPSBRecord {

        private IPCBStructure[] PCBs;

        public boolean isPSBRecord() {
            return true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.edt.compiler.internal.dli.stmtFactory.IPSBRecord#getPCBs()
         */
        public IPCBStructure[] getPCBs() {
            return PCBs;
        }

        public IPCBStructure[] getRealPCBs() {
            List list = new ArrayList();
            for (int i = 0; i < getPCBs().length; i++) {
                if (((PCBStructure) getPCBs()[i]).getRedefines() == null) {
                    list.add(getPCBs()[i]);
                }
            }
            return (IPCBStructure[]) list.toArray(new IPCBStructure[list.size()]);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.edt.compiler.internal.dli.stmtFactory.IPSBRecord#getPCBStructureNamed(java.lang.String)
         */
        public IPCBStructure getPCBStructureNamed(String name) {
        	if (getPCBs() == null || name == null) {
                return null;
            }
            String internedName = InternUtil.intern(name);
            for (int i = 0; i < getPCBs().length; i++) {
                if (internedName == InternUtil.intern(getPCBs()[i].getName())) {
                    return getPCBs()[i];
                }
            }
            return null;
        }

        public void setPCBs(IPCBStructure[] bs) {
            PCBs = bs;
        }
    }

    private class Relationship implements IRelationship {

        IDLISegmentRecord ParentRecord;

        IDLISegmentRecord SegmentRecord;

        public IDLISegmentRecord getParentRecord() {
            return ParentRecord;
        }

        public void setParentRecord(IDLISegmentRecord parentRecord) {
            ParentRecord = parentRecord;
        }

        public IDLISegmentRecord getSegmentRecord() {
            return SegmentRecord;
        }

        public void setSegmentRecord(IDLISegmentRecord segmentRecord) {
            SegmentRecord = segmentRecord;
        }
    }

    public IDLIDataContainer createDLIDataContainer(IDataBinding psbBinding, IDataBinding[] segmentBindings) {
        dataContainer = new DLIDataContainer();
        this.psbBinding = psbBinding;
        this.segmentBindings = segmentBindings;
        dataContainer.setPSB(createPSB());
        dataContainer.setSegments(createSegments());
        return dataContainer;
    }

    private IDLISegmentRecord[] createSegments() {
        List list = new ArrayList();
        for (int i = 0; i < segmentBindings.length; i++) {
            IDLISegmentRecord rec = createSegment(segmentBindings[i]);
            if (rec != null) {
                list.add(rec);
            }
        }
        return (IDLISegmentRecord[]) list.toArray(new IDLISegmentRecord[list.size()]);
    }

    private IDLISegmentRecord createSegment(IDataBinding binding) {
        ITypeBinding typeBinding = getBaseTypeBinding(binding);

        if (typeBinding == null) {
            return null;
        }

        if (binding.getAnnotation(EGLIODLI, "DLISegment") == null) {
            return null;
        }

        IDLISegmentRecord rec = createDLISegmentRecord(binding, false);

        if (rec != null) {
            rec.setName(binding.getName());
        }
        return rec;
    }

    private ITypeBinding getBaseTypeBinding(IDataBinding dataBinding) {
        if (dataBinding == null || dataBinding == IBinding.NOT_FOUND_BINDING) {
            return null;
        }
        if (dataBinding.getType() == null || dataBinding.getType() == IBinding.NOT_FOUND_BINDING) {
            return null;
        }
        if (dataBinding.getType().getBaseType() == null || dataBinding.getType().getBaseType() == IBinding.NOT_FOUND_BINDING) {
            return null;
        }
        return dataBinding.getType().getBaseType();

    }

    private IPSBRecord createPSB() {

        FlexibleRecordBinding psbTypeBinding = (FlexibleRecordBinding) getBaseTypeBinding(psbBinding);
        if (psbTypeBinding == null) {
            return null;
        }

        PSBRecord psbRec = new PSBRecord();
        psbRec.setName(psbBinding.getName());
        psbRec.setType(psbTypeBinding);
        psbRec.setPCBs(createPCBStructures(psbTypeBinding.getDeclaredFields(), psbRec));
        return psbRec;
    }

    private IPCBStructure[] createPCBStructures(List fieldBindings, IPSBRecord psb) {
        List list = new ArrayList();
        Iterator i = fieldBindings.iterator();
        while (i.hasNext()) {
            IPCBStructure pcb = createPCBStructure((FlexibleRecordFieldBinding) i.next(), psb);
            if (pcb != null) {
                list.add(pcb);
            }
        }
        return (IPCBStructure[]) list.toArray(new IPCBStructure[list.size()]);
    }

    private IPCBStructure createPCBStructure(FlexibleRecordFieldBinding binding, IPSBRecord psb) {

        String redefinesName = null;
        IAnnotationBinding annotationBinding = binding.getAnnotation(new String[] {"egl", "core"}, "Redefines");
        if (annotationBinding != null) {
            Object oValue = annotationBinding.getValue();
            if (oValue instanceof IBinding) {
                IBinding value = (IBinding) annotationBinding.getValue();
                if (value != null && value != IBinding.NOT_FOUND_BINDING) {
                    redefinesName = value.getName();
                }
            }
            else {
                redefinesName = (String)oValue;
            }
        }

        annotationBinding = binding.getAnnotation(EGLIODLI, "PCB");

        if (redefinesName == null && annotationBinding == null) {
            //This is not really a PCB, so just return null
            return null;
        }

        PCBStructure pcb = new PCBStructure();
        pcb.setRedefines(redefinesName);
        pcb.setType(binding.getType());
        pcb.setName(binding.getName());
        pcb.setPCB(createPCB(annotationBinding));
        pcb.setContainer(psb);

        return pcb;
    }

    private IPCB createPCB(IAnnotationBinding annotationBinding) {
        if (annotationBinding == null) {
            return null;
        }

        PCB pcb = new PCB();

        IAnnotationBinding subAnnotationBinding = getField(annotationBinding, IEGLConstants.PROPERTY_SECONDARYINDEX);
        if (subAnnotationBinding != null) {
            pcb.setSecondaryIndex((String) subAnnotationBinding.getValue());
        }

        subAnnotationBinding = getField(annotationBinding, IEGLConstants.PROPERTY_SECONDARYINDEXITEM);
        if (subAnnotationBinding != null) {
            pcb.setSecondaryIndexItemName((String) subAnnotationBinding.getValue());
        }

        subAnnotationBinding = getField(annotationBinding, IEGLConstants.PROPERTY_PCBTYPE);
        if (subAnnotationBinding != null) {
            pcb.setPCBType((EnumerationDataBinding) subAnnotationBinding.getValue());
        }

        pcb.setHierarchy(createRelationships(getField(annotationBinding, IEGLConstants.PROPERTY_HIERARCHY)));
        return pcb;
    }

    private IRelationship[] createRelationships(IAnnotationBinding annotationBinding) {
        if (annotationBinding == null) {
            return null;
        }

        Object[] objValue = (Object[]) annotationBinding.getValue();
        if (objValue.length == 0) {
            return new Relationship[0];
        }

        IAnnotationBinding[] value = (IAnnotationBinding[]) objValue;

        Relationship[] relations = new Relationship[value.length];

        for (int i = 0; i < relations.length; i++) {
            relations[i] = createRelationship(value[i]);
        }
        return relations;
    }

    private Relationship createRelationship(IAnnotationBinding annotationBinding) {
        Relationship relation = new Relationship();
        IAnnotationBinding subAnnotationBinding = getField(annotationBinding, IEGLConstants.PROPERTY_SEGMENTRECORD);
        if (subAnnotationBinding != null) {
            relation.setSegmentRecord(createDLISegmentRecord((ITypeBinding) subAnnotationBinding.getValue(), true));
        }

        subAnnotationBinding = getField(annotationBinding, IEGLConstants.PROPERTY_PARENTRECORD);
        if (subAnnotationBinding != null) {
            relation.setParentRecord(createDLISegmentRecord((ITypeBinding) subAnnotationBinding.getValue(), true));
        }

        return relation;
    }

    private IDLISegmentRecord createDLISegmentRecord(IBinding binding, boolean useCache) {

        if (binding == null || binding == IBinding.NOT_FOUND_BINDING) {
            return null;
        }

        DLISegmentRecord rec;
        if (useCache) {
            rec = (DLISegmentRecord) bindingToDLISegmentRecordMap.get(binding);
            if (rec != null) {
                return rec;
            }
        }

        rec = new DLISegmentRecord();
        rec.setType(getBaseTypeBinding(binding));
        rec.setArray(isArray(binding));
        IAnnotationBinding annotationBinding = getField(binding.getAnnotation(EGLIODLI, "DLISegment"), IEGLConstants.PROPERTY_HOSTVARQUALIFIER);
        if (annotationBinding != null) {
            rec.setHostVarQualifier((String) annotationBinding.getValue());
        }

        annotationBinding = getField(binding.getAnnotation(EGLIODLI, "DLISegment"), IEGLConstants.PROPERTY_KEYITEM);
        if (annotationBinding != null) {
            IBinding value = (IBinding) annotationBinding.getValue();
            if (value != null && value != IBinding.NOT_FOUND_BINDING) {
                StructureItemBinding siBinding = (StructureItemBinding) value;
                String qualName = siBinding.getParentQualifiedName();
                rec.setKeyItemName(qualName);
                String keyItemFieldName = siBinding.getName();

                if (binding.isDataBinding()) {
                    IDataBinding dataBinding = (IDataBinding) binding;
                    annotationBinding = dataBinding.getAnnotationFor(EGLIODLI, "DLIFieldName",
                            new IDataBinding[] { siBinding });
                    if (annotationBinding != null) {
                        keyItemFieldName = (String) annotationBinding.getValue();
                    }
                } else {
                    annotationBinding = siBinding.getAnnotation(EGLIODLI, "DLIFieldName");
                    if (annotationBinding != null) {
                        keyItemFieldName = (String) annotationBinding.getValue();
                    }
                }
                rec.setKeyItemFieldName(keyItemFieldName);

            }
        }

        annotationBinding = getField(binding.getAnnotation(EGLIODLI, "DLISegment"), IEGLConstants.PROPERTY_SEGMENTNAME);
        if (annotationBinding != null) {
            rec.setSegmentName((String) annotationBinding.getValue());
        }

        rec.setName(binding.getName());

        return rec;
    }

    private boolean isArray(IBinding binding) {
    	if (!Binding.isValidBinding(binding)) {
    		return false;
    	}
    	
        if (binding.isTypeBinding()) {
            return ((ITypeBinding) binding).getKind() == ITypeBinding.ARRAY_TYPE_BINDING;
        }
        if (binding.isDataBinding()) {
            IDataBinding dataBinding = (IDataBinding) binding;
            if (Binding.isValidBinding(dataBinding.getType())) {
                return dataBinding.getType().getKind() == ITypeBinding.ARRAY_TYPE_BINDING;
            }
        }
        return false;
    }
    
    private ITypeBinding getBaseTypeBinding(IBinding binding) {
        if (binding.isTypeBinding()) {
            return (ITypeBinding) binding;
        }
        if (binding.isDataBinding()) {
            IDataBinding dataBinding = (IDataBinding) binding;
            if (dataBinding.getType() != null && dataBinding.getType() != IBinding.NOT_FOUND_BINDING) {
                return ((IDataBinding) binding).getType().getBaseType();
            }
        }
        return null;
    }
    
    private IAnnotationBinding getField(IAnnotationBinding aBinding, String fieldName) {
    	if(aBinding == null) {
    		return null;
    	}
    	IDataBinding fieldBinding = aBinding.findData(fieldName);
		return IBinding.NOT_FOUND_BINDING == fieldBinding ? null : (IAnnotationBinding) fieldBinding;
	}
}
