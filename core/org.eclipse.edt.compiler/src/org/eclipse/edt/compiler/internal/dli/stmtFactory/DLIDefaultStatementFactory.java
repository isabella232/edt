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
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ForUpdateClause;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.IDliIOStatement;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DLIDefaultStatementFactory {
    IPSBRecord psb;

    IDLISegmentRecord[] segments;

    ITypeBinding[] segmentTypeBindings;

    IPCBStructure pcb;

    IProblemRequestor problemRequestor;
    
    private static final String[] EGLIODLI = new String[] {"egl", "io", "dli"};

    private class Hierarchy implements IHierarchy {

        IHierarchyEntry[] entries;

        public Hierarchy(IRelationship[] relations) {
            super();

            List list = new ArrayList();
            if (relations != null) {
                HierarchyEntry previous = null;
                for (int i = 0; i < relations.length; i++) {
                    HierarchyEntry entry = new HierarchyEntry(relations[i], previous, i);
                    list.add(entry);
                    if (previous != null) {
                        previous.addChild(entry);
                    }
                    previous = entry;
                }
            }
            entries = (IHierarchyEntry[]) list.toArray(new IHierarchyEntry[list.size()]);
        }

        public Hierarchy(IPCB pcb) {
            super();
            List list = new ArrayList();
            if (pcb != null && pcb.getHierarchy() != null) {
                IRelationship[] relations = pcb.getHierarchy();
                HierarchyEntry root = getHierarchyEntryForRoot(relations);
                if (root != null) {
                    list.add(root);
                    List entriesNeedingChildren = new ArrayList();
                    entriesNeedingChildren.add(root);
                    buildHierarchy(list, entriesNeedingChildren, relations);
                }
            }
            entries = (IHierarchyEntry[]) list.toArray(new IHierarchyEntry[list.size()]);

        }

        private HierarchyEntry getHierarchyEntryForRoot(IRelationship[] relations) {
            HierarchyEntry root = null;
            for (int i = 0; i < relations.length; i++) {
                if (relations[i].getSegmentRecord() != null && relations[i].getParentRecord() == null) {
                    if (root == null) {
                        root = new HierarchyEntry(relations[i], null, 0);
                    } else {
                        return null;
                    }
                }
            }
            return root;
        }

        private void buildHierarchy(List list, List entriesNeedingChildren, IRelationship[] relations) {
            if (entriesNeedingChildren.size() == 0) {
                return;
            }
            List newList = new ArrayList();
            Iterator i = entriesNeedingChildren.iterator();
            while (i.hasNext()) {
                HierarchyEntry entry = (HierarchyEntry) i.next();
                List children = findChildren(entry, relations);
                boolean error = addChildren(entry, list, children);
                if (error) {
                    list.clear();
                    return;
                }
                newList.addAll(children);
            }

            buildHierarchy(list, newList, relations);
        }

        private boolean addChildren(HierarchyEntry entry, List list, List children) {
            Iterator i = children.iterator();
            while (i.hasNext()) {
                HierarchyEntry child = (HierarchyEntry) i.next();
                if (containsEntry(list, child)) {
                    return true;
                }
                entry.addChild(child);
            }
            list.addAll(children);
            return false;
        }

        private boolean containsEntry(List list, HierarchyEntry entry) {
            if (entry.getRelationship().getSegmentRecord() == null) {
                return false;
            }

            Iterator i = list.iterator();
            while (i.hasNext()) {
                HierarchyEntry next = (HierarchyEntry) i.next();
                if (next.getRelationship().getSegmentRecord() != null) {
                    if (next.getRelationship().getSegmentRecord().typeMatches(entry.getRelationship().getSegmentRecord().getType())) {
                        return true;
                    }
                }
            }
            return false;
        }

        private List findChildren(HierarchyEntry entry, IRelationship[] relations) {
            List list = new ArrayList();
            if (entry.getRelationship().getSegmentRecord() == null) {
                return list;
            }

            for (int i = 0; i < relations.length; i++) {
                if (relations[i].getParentRecord() != null) {
                    if (entry.getRelationship().getSegmentRecord().typeMatches(relations[i].getParentRecord().getType())) {
                        list.add(new HierarchyEntry(relations[i], entry, entry.getLevel() + 1));
                    }
                }
            }
            return list;
        }

        public IHierarchyEntry[] getEntries() {
            return entries;
        }

        public IHierarchyEntry getEntry(IDLISegmentRecord segment) {
        	if (segment == null) {
        		return null;
        	}
        	
            if (entries != null) {
                for (int i = 0; i < entries.length; i++) {
                	if (entries[i].getSegmentRecord() != null && entries[i].getSegmentRecord().typeMatches(segment.getType())) {
                        return entries[i];
                    }
                }
            }
            return null;
        }
        
        public IHierarchyEntry getEntry(String segmentName) {
        	if (entries != null && segmentName != null) {
            	String internedSegmentName = InternUtil.intern(segmentName);
             	for (int i = 0; i < entries.length; i++) {
                	if (segmentName != null && entries[i].getSegmentName() != null &&
                        internedSegmentName == InternUtil.intern(entries[i].getSegmentName())) {
                        return entries[i];
                    }
                }
            }
            return null;
        }
    }

    private class HierarchyEntry implements IHierarchyEntry {
        IRelationship relationship;

        IHierarchyEntry parent;

        List children = new ArrayList();

        int level;

        public HierarchyEntry(IRelationship relationship, IHierarchyEntry parent, int level) {
            super();
            this.relationship = relationship;
            this.parent = parent;
            this.level = level;
        }

        public List getChildren() {
            return children;
        }

        public int getLevel() {
            return level;
        }

        public IHierarchyEntry getParent() {
            return parent;
        }

        public IRelationship getRelationship() {
            return relationship;
        }

        public void addChild(IHierarchyEntry child) {
            children.add(child);
        }

        
        public String getSegmentName() {
            if (getRelationship() != null && getRelationship().getSegmentRecord() != null) {
                return getRelationship().getSegmentRecord().getSegmentName();
            }
            return null;
        }

		public IDLISegmentRecord getSegmentRecord() {
			if (getRelationship() != null) {
				return getRelationship().getSegmentRecord();
			}
			return null;
		}

    }

    private class SSA {
        String segmentName;

        String commandCodes;

        String condition;

        public SSA(String segmentName, String commandCodes, String condition) {
            super();
            this.segmentName = segmentName;
            this.commandCodes = commandCodes;
            this.condition = condition;
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            if (segmentName != null) {
                buffer.append(segmentName);
            }
            if (commandCodes != null) {
                buffer.append("*" + commandCodes);
            }
            if (condition != null) {
                buffer.append(" (");
                buffer.append(condition);
                buffer.append(")");
            }
            return buffer.toString();
        }

        public String getCommandCodes() {
            return commandCodes;
        }

        public void setCommandCodes(String commandCodes) {
            this.commandCodes = commandCodes;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getSegmentName() {
            return segmentName;
        }

        public void setSegmentName(String segmentName) {
            this.segmentName = segmentName;
        }
    }

    private void initialize(IPSBRecord psb, IDLISegmentRecord[] segments, IPCBStructure pcb, IDataBinding pcbBinding) {
        this.psb = psb;

        if (segments == null) {
            this.segments = new IDLISegmentRecord[0];
        } else {
            this.segments = segments;
        }

        if (pcb == null && pcbBinding == null) {
            this.pcb = getDefaultPCBRecord();
        } else {
            this.pcb = pcb;
        }

        if (this.pcb == null) {
            if (pcbBinding == null) {
                if (segments.length == 0) {
                    problemRequestor.acceptProblem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_TARGETS_INVALID);
                } else {
                    problemRequestor.acceptProblem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_DEFAULT_PCB_NOT_FOUND);
                }
            } else {
                if (pcbBinding != IBinding.NOT_FOUND_BINDING) {
	                problemRequestor.acceptProblem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_PCB_NOT_FOUND, new String[] { pcbBinding
	                        .getName() });
                }
            }
        }
    }

    public DLIDefaultStatementFactory(IDataBinding psbBinding, IDataBinding[] segmentDataBindings, ITypeBinding[] segmentTypeBindings,
            IDataBinding pcbBinding, IDataBinding[] pcbParms, IProblemRequestor problemRequestor) {
        super();
        this.segmentTypeBindings = segmentTypeBindings;
        this.problemRequestor = problemRequestor;

        startUp(psbBinding, segmentDataBindings, pcbBinding, pcbParms);
    }

    public void startUp(IDataBinding psbBinding, IDataBinding[] segmentDataBindings, IDataBinding pcbBinding, IDataBinding[] pcbParms) {

        IDLIDataContainer dataContainer = new DLIDataContainerFactory().createDLIDataContainer(psbBinding, segmentDataBindings);

        IPSBRecord psb = null;
        psb = dataContainer.getPSB();
        segments = dataContainer.getSegments();

        IPCBStructure pcb = getPCB(psb, pcbBinding, pcbParms);

        initialize(psb, segments, pcb, pcbBinding);

    }

    private IPCBStructure getPCB(IPSBRecord psb, IDataBinding pcbBinding, IDataBinding[] pcbParms) {
        if (psb == null || pcbBinding == null || pcbBinding == IBinding.NOT_FOUND_BINDING) {
            return null;
        }

        if (pcbBinding.getKind() == IDataBinding.PROGRAM_PARAMETER_BINDING) {
            if (pcbParms != null) {
                for (int i = 0; i < pcbParms.length; i++) {
                    if (pcbBinding == pcbParms[i]) {
                        IPCBStructure[] pcbs = psb.getRealPCBs();
                        if (pcbs.length > i) {
                            return pcbs[i];
                        } else {
                            return null;
                        }
                    }
                }
            }
            return null;
        }

        String pcbString = pcbBinding.getName();
        return psb.getPCBStructureNamed(pcbString);

    }

    public IPCBStructure getPCB(String name) {
        if (name == null || name.length() == 0) {
            return getDefaultPCBRecord();
        }
        if (psb != null) {
            return psb.getPCBStructureNamed(name);
        }
        return null;
    }

    public String getDLICallForDelete() {

        if (getPCB() == null || psb == null) {
            return null;
        }

        if (segmentTypeBindings.length != 1 || segmentTypeBindings[0].getAnnotation(EGLIODLI, "DLISegment") == null) {
            problemRequestor.acceptProblem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_TARGET_MUST_BE_DLISEGMENT);
            return null;
        }
        return "DLET " + ((IDLISegmentRecord) getSegments()[0]).getSegmentName();
    }

    public String getDLICallForReplace() {

        if (getPCB() == null || psb == null) {
            return null;
        }

        if (segmentTypeBindings.length != 1 || segmentTypeBindings[0].getAnnotation(EGLIODLI, "DLISegment") == null) {
            problemRequestor.acceptProblem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_TARGET_MUST_BE_DLISEGMENT);
            return null;
        }

        return "REPL " + ((IDLISegmentRecord) getSegments()[0]).getSegmentName();
    }

    public String getDLICallForGetByPosition(boolean isForUpdate, boolean isGetInParent) {

        if (getPCB() == null || psb == null) {
            return null;
        }

        if (getSegments().length == 0) {
            return null;
        }

        if (segmentTypeBindings.length > 1) {
            for (int i = 0; i < segmentTypeBindings.length; i++) {
                if (segmentTypeBindings[i].getAnnotation(EGLIODLI, "DLISegment") == null) {
                    problemRequestor.acceptProblem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_TARGETS_MUST_BE_DLISEGMENT);
                    return null;
                }
            }
        }

        IDLISegmentRecord[] segs = getSegments();
        List list = new ArrayList();
        for (int i = 0; i < segs.length; i++) {
            IDLISegmentRecord segRec = segs[i];
            if (segRec != null) {
                String segName = segRec.getSegmentName();
                String commandCodes = null;
                if (i < segs.length - 1) {
                    commandCodes = "D";
                }
                list.add(new SSA(segName, commandCodes, null));
            }
        }

        String hold = "";
        if (isForUpdate) {
            hold = "H";
        }

        String parent = "";
        if (isGetInParent) {
            parent = "P";
        }

        String functionCall = "G" + hold + "N" + parent;

        StringBuffer buffer = new StringBuffer();
        buffer.append(functionCall);

        Iterator i = list.iterator();
        int j = 0;
        while (i.hasNext()) {
            SSA ssa = (SSA) i.next();
            if (j == 0) {
                buffer.append(" ");
            } else {
                buffer.append("\r\n   ");
                if (isForUpdate) {
                    buffer.append(" ");
                }
                if (isGetInParent) {
                    buffer.append(" ");
                }
            }
            buffer.append(ssa.toString());
            j++;
        }
        return buffer.toString();
    }

    public String getDLICallForGetByKey(boolean isForUpdate) {

        if (getPCB() == null || psb == null) {
            return null;
        }

        if (getSegments().length == 0) {
            return null;
        }

        if (segmentTypeBindings.length == 1 && segmentTypeBindings[0].getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
            return getDLICallForGetByKeyUsingArray();
        }

        for (int i = 0; i < segmentTypeBindings.length; i++) {
            if (segmentTypeBindings[i].getAnnotation(EGLIODLI, "DLISegment") == null) {
                problemRequestor.acceptProblem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_TARGETS_MUST_BE_DLISEGMENT);
                return null;
            }
        }

        return getDLICallForGetByKeyUsingSegments(isForUpdate);
    }

    private String getDLICallForGetByKeyUsingArray() {

        IRelationship[] relations = getRelationships(getSegments()[getSegments().length - 1]);
        if (relations.length == 0) {
            return null;
        }

        SSA[] ssas = getSSAs(relations);
        if (ssas == null || ssas.length == 0) {
            return null;
        }

        String functionCall = "GU";

        StringBuffer buffer = new StringBuffer();
        buffer.append(functionCall);
        for (int i = 0; i < ssas.length; i++) {
            if (i == 0) {
                buffer.append(" ");
            } else {
                buffer.append("\r\n   ");
            }
            buffer.append(ssas[i].toString());
        }

        buffer.append("\r\n");
        buffer.append(getDLICallForGetByPosition(false, false));

        return buffer.toString();
    }

    private String getDLICallForGetByKeyUsingSegments(boolean isForUpdate) {

        IRelationship[] relations = getRelationships(getSegments()[getSegments().length - 1]);
        if (relations.length == 0) {
            return null;
        }

        SSA[] ssas = getSSAs(relations);
        if (ssas == null || ssas.length == 0) {
            return null;
        }

        String hold = "";
        if (isForUpdate) {
            hold = "H";
        }

        String functionCall = "G" + hold + "U";

        StringBuffer buffer = new StringBuffer();
        buffer.append(functionCall);
        for (int i = 0; i < ssas.length; i++) {
            if (i == 0) {
                buffer.append(" ");
            } else {
                buffer.append("\r\n   ");
                if (isForUpdate) {
                    buffer.append(" ");
                }
            }
            buffer.append(ssas[i].toString());
        }

        return buffer.toString();
    }

    public String getDLICallForAdd() {
        if (getPCB() == null || psb == null) {
            return null;
        }

        if (getSegments().length == 0) {
            return null;
        }

        if (segmentTypeBindings.length > 1) {
            for (int i = 0; i < segmentTypeBindings.length; i++) {
                if (segmentTypeBindings[i].getAnnotation(EGLIODLI, "DLISegment") == null) {
                    problemRequestor.acceptProblem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_TARGETS_MUST_BE_DLISEGMENT);
                    return null;
                }
            }
        }

        IRelationship[] relations = getRelationships(getSegments()[getSegments().length - 1]);
        if (relations.length == 0) {
            return null;
        }

        SSA[] ssas = getSSAs(relations);
        if (ssas == null || ssas.length == 0) {
            return null;
        }
        //Conditions cannot follow the D command Code
        boolean foundD = false;
        for (int i = 0; i < ssas.length; i++) {
            if (ssas[i].getCommandCodes() != null && ssas[i].getCommandCodes().indexOf("D") != -1) {
                foundD = true;
            }
            if (foundD) {
                ssas[i].setCondition(null);
            }
        }

        SSA lastSSA = ssas[ssas.length - 1];
        lastSSA.setCondition(null);

        String functionCall = "ISRT";

        StringBuffer buffer = new StringBuffer();
        buffer.append(functionCall);
        for (int i = 0; i < ssas.length; i++) {
            if (i == 0) {
                buffer.append(" ");
            } else {
                buffer.append("\r\n     ");
            }
            buffer.append(ssas[i].toString());
        }

        return buffer.toString();

    }

    private SSA[] getSSAs(IRelationship[] relations) {
        List list = new ArrayList();
        for (int i = 0; i < relations.length; i++) {
            SSA ssa = getSSA(relations[i], (i + 1 == relations.length));
            if (ssa != null) {
                list.add(ssa);
            }
        }
        return (SSA[]) list.toArray(new SSA[list.size()]);
    }

    private SSA getSSA(IRelationship relation, boolean last) {
        if (relation == null || relation.getSegmentRecord() == null) {
            return null;
        }

        IDLISegmentRecord segment = getSegment(relation.getSegmentRecord());
        String left = null;
        String right = null;

        if (relation.getParentRecord() == null && getPCB().getPCB().getSecondaryIndex() != null) {
            left = getPCB().getPCB().getSecondaryIndex();
        } else {
            if (segment.getKeyItemFieldName() != null) {
                left = segment.getKeyItemFieldName();
            }
        }

        if (relation.getParentRecord() == null && getPCB().getPCB().getSecondaryIndexItemName() != null) {
            right = getPCB().getPCB().getSecondaryIndexItemName();
        } else {
            if (segment.getKeyItemName() != null) {
                right = segment.getHostVarQualifier() + "." + segment.getKeyItemName();
            }
        }

        String segmentName = relation.getSegmentRecord().getSegmentName();
        String commandCode = null;
        if (segment != relation.getSegmentRecord() && !last) {
            commandCode = "D";
        }

        String condition = null;
        if (left != null && right != null) {
            condition = left + " = :" + right;
        }

        return new SSA(segmentName, commandCode, condition);
    }

    private IDLISegmentRecord getSegment(IDLISegmentRecord seg) {
        for (int i = 0; i < getSegments().length; i++) {
            IDLISegmentRecord test = getSegments()[i];
            if (seg.typeMatches(test.getType())) {
                return test;
            }
        }
        return seg;
    }

    public IPCBStructure getDefaultPCBRecord() {
        if (psb == null) {
            return null;
        }
        IPCBStructure[] pcbs = psb.getPCBs();
        for (int i = 0; i < pcbs.length; i++) {
            if (isDefaultPCB(pcbs[i])) {
                return pcbs[i];
            }
        }
        return null;
    }

    private boolean isDefaultPCB(IPCBStructure pcb) {
        if (getSegments() == null || getSegments().length < 1) {
            return false;
        }

        IDLISegmentRecord segment = getSegments()[getSegments().length - 1];
        if (segment == null) {
            return false;
        }

        IHierarchy hierarchy = getHierarchy(segment, pcb);

        if (hierarchy == null) {
            return false;
        }

        int level = -1;

        for (int i = 0; i < getSegments().length; i++) {
            segment = getSegments()[i];
            if (segment == null) {
                return false;
            }
            IHierarchyEntry entry = hierarchy.getEntry(segment);
            if (entry == null) {
                return false;
            }
            if (entry.getLevel() <= level) {
                return false;
            }
            level = entry.getLevel();
        }

        return true;
    }

    private boolean pcbContainsSegment(IPCBStructure pcb, IDLISegmentRecord segment) {
        return getRelationship(pcb, segment) != null;
    }

    private IRelationship getRelationship(IPCBStructure pcb, IDLISegmentRecord segment) {
        if (segment == null || pcb == null || pcb.getPCB() == null || pcb.getPCB().getHierarchy() == null) {
            return null;
        }
        IRelationship[] hierarchy = pcb.getPCB().getHierarchy();

        for (int i = 0; i < hierarchy.length; i++) {
            if (hierarchy[i].getSegmentRecord() != null && segment.typeMatches(hierarchy[i].getSegmentRecord().getType())) {
                return hierarchy[i];
            }
        }
        return null;
    }

    private IRelationship getRelationship(IDLISegmentRecord segment) {
        return getRelationship(getPCB(), segment);
    }

    private IRelationship[] getRelationships(IDLISegmentRecord segment) {
        IRelationship[] result = getRelationships(segment, getPCB());
        if (result.length == 0) {
            String pcbName = "";
            if (getPCB() != null) {
                pcbName = getPCB().getName();
            }
            problemRequestor.acceptProblem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_RELATIONSHIP_NOT_FOUND, new String[] {
                    pcbName, segment.getSegmentName() });
        }
        return result;
    }

    private IRelationship[] getRelationships(IDLISegmentRecord segment, IPCBStructure pcbStructure) {
        List list = new ArrayList();
        IDLISegmentRecord currentSegment = segment;
        while (currentSegment != null) {
            IRelationship r = getRelationship(pcbStructure, currentSegment);
            if (r == null || list.contains(r)) {
                currentSegment = null;
                continue;
            }
            list.add(0, r);
            currentSegment = r.getParentRecord();
        }
        return (IRelationship[]) list.toArray(new IRelationship[list.size()]);
    }

    public IPCBStructure getPCB() {
        return pcb;
    }

    public IDLISegmentRecord[] getSegments() {
        if (segments == null) {
            segments = new IDLISegmentRecord[0];
        }
        return segments;
    }

    public IDLISegmentRecord getSegment(String segmentName) {
        if (segmentName == null) {
            return null;
        }
        String internedSegmentName = InternUtil.intern(segmentName);
        IDLISegmentRecord[] segments = getSegments();
        for (int i = 0; i < segments.length; i++) {
            IDLISegmentRecord segment = segments[i];
            if (internedSegmentName == InternUtil.intern(segment.getSegmentName())) {
                return segment;
            }
        }
        return null;
    }

    public IPSBRecord getPsb() {
        return psb;
    }

    public IHierarchy getHierarchy(IDLISegmentRecord segment) {
        return getHierarchy(segment, getPCB());
    }

    public IHierarchy getHierarchy(IDLISegmentRecord segment, IPCBStructure pcbStructure) {
        if (segment == null || pcbStructure == null) {
            return null;
        }
        IRelationship[] relations = getRelationships(segment, pcbStructure);
        if (relations != null) {
            return new Hierarchy(relations);
        }
        return null;
    }

    public IHierarchy getHierarchy() {
        return getHierarchy(getPCB());
    }

    public IHierarchy getHierarchy(IPCBStructure pcbStructure) {
        if (pcbStructure == null || pcbStructure.getPCB() == null) {
            return null;
        }

        return new Hierarchy(pcbStructure.getPCB());
    }

    public String getDLICallForStatement(IDliIOStatement statement) {
        final String[] dliCall = new String[1];
        statement.accept(new DefaultASTVisitor() {
            public boolean visit(AddStatement statement) {
                dliCall[0] = getDLICallForAdd();
                return false;
            }

            public boolean visit(DeleteStatement statement) {
                dliCall[0] = getDLICallForDelete();
                return false;
            }

            public boolean visit(GetByKeyStatement statement) {
                final boolean[] forUpdate = new boolean[1];
                statement.accept(new DefaultASTVisitor() {
                    public boolean visit(GetByKeyStatement getByKeyStatement) {
                        return true;
                    }

                    public boolean visit(ForUpdateClause forUpdateClause) {
                        forUpdate[0] = true;
                        return false;
                    }
                });
                dliCall[0] = getDLICallForGetByKey(forUpdate[0]);
                return false;
            }

            public boolean visit(GetByPositionStatement statement) {
                final boolean[] forUpdate = new boolean[1];
                statement.accept(new DefaultASTVisitor() {
                    public boolean visit(GetByPositionStatement getByPositionStatement) {
                        return true;
                    }

                    public boolean visit(ForUpdateClause forUpdateClause) {
                        forUpdate[0] = true;
                        return false;
                    }
                });
                dliCall[0] = getDLICallForGetByPosition(forUpdate[0], statement.isGetInParent());
                return false;
            }

            public boolean visit(ReplaceStatement statement) {
                dliCall[0] = getDLICallForReplace();
                return false;
            }
        });
        return dliCall[0];
    }

	public IProblemRequestor getProblemRequestor() {
		return problemRequestor;
	}

}
