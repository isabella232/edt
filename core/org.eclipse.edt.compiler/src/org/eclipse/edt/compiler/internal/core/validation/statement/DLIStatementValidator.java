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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.IDliIOStatement;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.UsingPCBClause;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.annotation.MustBeDLINameAnnotationValidator;
import org.eclipse.edt.compiler.internal.dli.IBooleanExpression;
import org.eclipse.edt.compiler.internal.dli.IBooleanExpressionSSAConditions;
import org.eclipse.edt.compiler.internal.dli.IBooleanOperatorExpression;
import org.eclipse.edt.compiler.internal.dli.ICommandCodes;
import org.eclipse.edt.compiler.internal.dli.ICondition;
import org.eclipse.edt.compiler.internal.dli.ISSAConditions;
import org.eclipse.edt.compiler.internal.dli.ISegmentSearchArgument;
import org.eclipse.edt.compiler.internal.dli.IStatement;
import org.eclipse.edt.compiler.internal.dli.stmtFactory.DLIDefaultStatementFactory;
import org.eclipse.edt.compiler.internal.dli.stmtFactory.IDLISegmentRecord;
import org.eclipse.edt.compiler.internal.dli.stmtFactory.IHierarchy;
import org.eclipse.edt.compiler.internal.dli.stmtFactory.IHierarchyEntry;
import org.eclipse.edt.compiler.internal.dli.stmtFactory.IPCB;
import org.eclipse.edt.compiler.internal.dli.stmtFactory.IRelationship;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Jason Peterson
 */

public class DLIStatementValidator {

	private class CommandCode {
		String code;

		String modifier;

		int offset;

		public CommandCode(String code, int offset) {
			super();
			this.code = code;
			this.offset = offset;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getModifier() {
			if (modifier == null) {
				modifier = "";
			}
			return modifier;
		}

		public void appendModifier(String aString) {
			modifier = getModifier() + aString;
		}

		public int getModifierAsInt() {
			if (getModifier().length() == 0) {
				return -1;
			}
			return Integer.parseInt(getModifier());
		}

		public int getLength() {
			return 1 + getModifier().length();
		}

		public void setModifier(String modifier) {
			this.modifier = modifier;
		}

		public int getOffset() {
			return offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}
	}

	private static HashMap functionCallsToValidCommandCodes = new HashMap();
	static {
		functionCallsToValidCommandCodes.put("GU", "DLQUVCPMRSWZ");
		functionCallsToValidCommandCodes.put("GHU", "DLQUVCPMRSWZ");
		functionCallsToValidCommandCodes.put("ISRT", "DLFUVCMRSWZ");
		functionCallsToValidCommandCodes.put("REPL", "NMSWZ");
		functionCallsToValidCommandCodes.put("DLET", "Z");
		functionCallsToValidCommandCodes.put("GN", "DLFQUVCPMRSWZ");
		functionCallsToValidCommandCodes.put("GNP", "DLFQUVCPMRSWZ");
		functionCallsToValidCommandCodes.put("GHN", "DLFQUVCPMRSWZ");
		functionCallsToValidCommandCodes.put("GHNP", "DLFQUVCPMRSWZ");
	}

	private static final int ADD_IO = 1;

	private static final int GET_KEY_IO = 2;

	private static final int GET_POSITION_IO = 3;

	private static final int REPLACE_IO = 4;

	private static final int DELETE_IO = 5;

	private IDliIOStatement dliIOStmt;

	private IProblemRequestor problemRequestor;

	private int dliIOType = 0;

	private int offset = 0;

	private boolean inlineDLIStatement = false;

	private String pcbName;

	private boolean forUpdate = false;

	private boolean getInParent = false;

	private DLIDefaultStatementFactory dliStmtFactory = null;

	private boolean hasValidTarget = false;

	private boolean targetIsArray = false;

	public DLIStatementValidator(IDliIOStatement statement, IProblemRequestor problemRequestor, int dliIOType) {
		this.dliIOStmt = statement;
		this.problemRequestor = problemRequestor;
		this.dliIOType = dliIOType;
	}

	public void validateGetByPosDLI(UsingPCBClause pcbClause, boolean hasForUpdate, boolean hasGetInParent) {
		setForUpdate(hasForUpdate);
		setGetInParent(hasGetInParent);
		validateDLI(pcbClause);
	}

	public void validateGetByKeyDLI(UsingPCBClause pcbClause, boolean hasForUpdate) {
		setForUpdate(hasForUpdate);
		validateDLI(pcbClause);
	}

	public void validateDLI(UsingPCBClause pcbClause) {
		initialize(pcbClause);
		if (isInlineDLIStatement()) {
			validateInlineDLI();
		}
		if (isInlineDLIStatement() || hasValidTarget()) {
			validateDLIStatement();
		}
	}

	private void validateInlineDLI() {
		Iterator i = dliIOStmt.getDliInfo().getModel().getStatements().iterator();
		boolean first = true;

		int index = 1;
		while (i.hasNext()) {
			IStatement modelStmt = (IStatement) i.next();
			if (first) {
				validateFunctionCodes(modelStmt);
				first = false;
			}
			validateCommandCodes(modelStmt);
			validateOnlyOneSsaAllowed(modelStmt);
			validateHasAtLeastOneSSA(modelStmt);

			if (index > 1) {
				if (getDliIOType() == GET_KEY_IO) {
					if (index == 2) {
						if (!"GN".equalsIgnoreCase(modelStmt.getDLIFunction()) && !"GNP".equalsIgnoreCase(modelStmt.getDLIFunction())) {
							problemRequestor.acceptProblem(getOffset() + modelStmt.getOffset(), getOffset() + modelStmt.getOffset()
									+ modelStmt.getDLIFunction().length(), IMarker.SEVERITY_ERROR,
									IProblemRequestor.DLI_GET_BY_KEY_SECOND_STATEMENT_FUNCTION_CODE_INVALID, new String[] { modelStmt
											.getDLIFunction() });
						} else {
							if (modelStmt.getSegmentSearchArguments() != null && modelStmt.getSegmentSearchArguments().size() > 1) {
								problemRequestor.acceptProblem(getOffset() + modelStmt.getOffset(), getOffset() + modelStmt.getOffset()
										+ modelStmt.getLength(), IMarker.SEVERITY_ERROR,
										IProblemRequestor.DLI_ONLY_ONE_SSA_ALLOWED_FOR_SECOND_STMT);
							}
						}
					} else {
						problemRequestor.acceptProblem(getOffset() + modelStmt.getOffset(), getOffset() + modelStmt.getOffset()
								+ modelStmt.getLength(), IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_TOO_MANY_CALLS_FOR_GETBYKEY,
								new String[] { modelStmt.getDLIFunction() });
					}
				} else {
					problemRequestor.acceptProblem(getOffset() + modelStmt.getOffset(), getOffset() + modelStmt.getOffset()
							+ modelStmt.getLength(), IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_TOO_MANY_CALLS, new String[] { modelStmt
							.getDLIFunction() });
				}
			}
			index = index + 1;
		}
	}

	private void validateHasAtLeastOneSSA(IStatement modelStmt) {
		if (modelStmt == null) {
			return;
		}

		// Get Next type of statements do not require an SSA
		if (IEGLConstants.DLIKEYWORD_GN.equalsIgnoreCase(modelStmt.getDLIFunction())
				|| IEGLConstants.DLIKEYWORD_GHN.equalsIgnoreCase(modelStmt.getDLIFunction())
				|| IEGLConstants.DLIKEYWORD_GNP.equalsIgnoreCase(modelStmt.getDLIFunction())
				|| IEGLConstants.DLIKEYWORD_GHNP.equalsIgnoreCase(modelStmt.getDLIFunction())) {
			return;
		}

		if (modelStmt.getSegmentSearchArguments() == null || modelStmt.getSegmentSearchArguments().size() == 0) {
			problemRequestor.acceptProblem(getOffset() + modelStmt.getOffset(),
					getOffset() + modelStmt.getOffset() + modelStmt.getLength(), IMarker.SEVERITY_ERROR,
					IProblemRequestor.DLI_NEED_AT_LEAST_1_SSA);
		}
	}

	private void validateOnlyOneSsaAllowed(IStatement modelStmt) {
		if (IEGLConstants.DLIKEYWORD_DLET.equalsIgnoreCase(modelStmt.getDLIFunction())) {
			if (modelStmt.getSegmentSearchArguments() != null && modelStmt.getSegmentSearchArguments().size() > 1) {
				problemRequestor.acceptProblem(getOffset() + modelStmt.getOffset(), getOffset() + modelStmt.getOffset()
						+ modelStmt.getLength(), IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_ONLY_ONE_SSA_ALLOWED_FOR_CALL,
						new String[] { modelStmt.getDLIFunction() });
			}
		}
	}

	private void validateCommandCodes(IStatement stmt) {
		String dliFunction = stmt.getDLIFunction();
		List ssas = stmt.getSegmentSearchArguments();
		Iterator i = ssas.iterator();
		List allPreviousCommandCodes = new ArrayList();
		List allPreviousSSASegmentNames = new ArrayList();
		while (i.hasNext()) {
			ISegmentSearchArgument ssa = (ISegmentSearchArgument) i.next();
			validateCommandCodes(ssa, dliFunction, allPreviousCommandCodes, allPreviousSSASegmentNames);
		}
	}

	private void validateCommandCodes(ISegmentSearchArgument ssa, String dliFunction, List allPreviousCommandCodes,
			List allPreviousSSASegmentNames) {
		ICommandCodes cmdCodes = ssa.getCommandCodes();

		checkValuesClause(ssa, cmdCodes);
		checkQualificationAllowed(ssa, dliFunction);
		checkSSASegmentName(ssa, allPreviousCommandCodes);

		if (cmdCodes == null) {
			checkCommandCodeD(allPreviousCommandCodes, ssa, dliFunction);
			return;
		}

		String validCodes = (String) functionCallsToValidCommandCodes.get(dliFunction.toUpperCase());
		if (validCodes == null) {
			return;
		}

		CommandCode[] codes = parseCommandCodes(cmdCodes.getCommandCodes().toUpperCase());

		checkCommandCodesForOneAllowed(codes, cmdCodes);
		if (cmdCodes.getCommandCodes().length() > 4) {
			int startOffset = getOffset() + cmdCodes.getOffset();
			problemRequestor.acceptProblem(startOffset, startOffset + codes.length, IMarker.SEVERITY_ERROR,
					IProblemRequestor.DLI_MAX_NUMBER_COMMAND_CODES_EXCEEDED);
		}

		List codesAlreadyFound = new ArrayList();
		for (int i = 0; i < codes.length; i++) {
			CommandCode code = codes[i];
			if (validCodes.indexOf(code.getCode()) == -1) {
				int startOffset = getOffset() + cmdCodes.getOffset() + code.getOffset();
				problemRequestor.acceptProblem(startOffset, startOffset + 1, IMarker.SEVERITY_ERROR,
						IProblemRequestor.DLI_INVALID_COMMAND_CODE_FOR_CALL, new String[] { code.getCode(), dliFunction });
			}
			checkMutuallyExclusiveCommandCodes(code, codesAlreadyFound, cmdCodes);
			checkCommandCodeC(code, allPreviousCommandCodes, cmdCodes, ssa, dliFunction);
			checkNumModifier(code, ssa);

			if (codesAlreadyFound.indexOf(code.getCode()) == -1) {
				codesAlreadyFound.add(code.getCode());
			} else {
				int startOffset = getOffset() + cmdCodes.getOffset() + code.getOffset();
				problemRequestor.acceptProblem(startOffset, startOffset + 1, IMarker.SEVERITY_ERROR,
						IProblemRequestor.DLI_COMMAND_CODE_REPEATED, new String[] { code.getCode() });
			}
		}
		allPreviousCommandCodes.addAll(codesAlreadyFound);
		checkCommandCodeD(allPreviousCommandCodes, ssa, dliFunction);
	}

	private void checkNumModifier(CommandCode code, ISegmentSearchArgument ssa) {
		String codeChar = code.getCode();
		if ("M".equals(codeChar) || "R".equals(codeChar) || "S".equals(codeChar) || "W".equals(codeChar) || "Z".equals(codeChar)) {
			if (code.getModifier().length() == 0) {
				int startOffset = getOffset() + ssa.getCommandCodes().getOffset() + code.getOffset();
				int endOffset = startOffset + code.getLength();
				problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_NO_CODE_MODIFIER,
						new String[] { code.getCode() });
			} else {
				if (code.getModifierAsInt() < 1 || code.getModifierAsInt() > 8) {
					int startOffset = getOffset() + ssa.getCommandCodes().getOffset() + code.getOffset();
					int endOffset = startOffset + code.getLength();
					problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR,
							IProblemRequestor.DLI_INVALID_CODE_MODIFIER, new String[] { code.getCode(), code.getModifier() });
				}
			}
		} else {
			if (code.getModifier().length() > 0) {
				int startOffset = getOffset() + ssa.getCommandCodes().getOffset() + code.getOffset();
				int endOffset = startOffset + code.getLength();
				problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_ILLEGAL_CODE_MODIFIER,
						new String[] { code.getCode(), code.getModifier() });
			}
		}
	}

	private boolean isDigit(String string) {
		return string.equals("0") || string.equals("1") || string.equals("2") || string.equals("3") || string.equals("4")
				|| string.equals("5") || string.equals("6") || string.equals("7") || string.equals("8") || string.equals("9");
	}

	private CommandCode[] parseCommandCodes(String codes) {
		List list = new ArrayList();
		CommandCode prevCode = null;
		for (int i = 0; i < codes.length(); i++) {
			String codeChar = codes.substring(i, i + 1);
			if (prevCode != null && isDigit(codeChar)) {
				prevCode.appendModifier(codeChar);
			} else {
				prevCode = new CommandCode(codeChar, i);
				list.add(prevCode);
			}
		}
		return (CommandCode[]) list.toArray(new CommandCode[list.size()]);
	}

	private void checkQualificationAllowed(ISegmentSearchArgument ssa, String dliFunction) {
		if (IEGLConstants.DLIKEYWORD_REPL.equalsIgnoreCase(dliFunction)) {
			if (ssa != null && ssa.getSSAConditions() != null) {
				int startOffset = getOffset() + ssa.getSSAConditions().getOffset();
				int endOffset = startOffset + ssa.getSSAConditions().getLength();
				problemRequestor
						.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_NO_CONDITIONS_FOR_REPL);
			}
		}
	}

	private void checkSSASegmentName(ISegmentSearchArgument ssa, List allPreviousSSASegmentNames) {
		if (ssa == null || ssa.getSegmentName() == null) {
			return;
		}
		if (allPreviousSSASegmentNames.contains(ssa.getSegmentName().getName().toUpperCase())) {
			int startOffset = getOffset() + ssa.getSegmentName().getOffset();
			int endOffset = startOffset + ssa.getSegmentName().getLength();
			problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_DUPLICATE_SSA,
					new String[] { ssa.getSegmentName().getName() });
		}
		allPreviousSSASegmentNames.add(ssa.getSegmentName().getName().toUpperCase());
	}

	private void checkValuesClause(ISegmentSearchArgument ssa, ICommandCodes codes) {
		// If there is no values clause, then just return
		if (ssa.getSSAConditions() == null || !ssa.getSSAConditions().isValueExpressionSSAConditions()) {
			return;
		}

		// At this point, we have a values clause. If there is a C command code,
		// then it is OK
		if (codes != null && codes.getCommandCodes() != null && codes.getCommandCodes().toUpperCase().indexOf("C") != -1) {
			return;
		}

		// YIKES...there is a values clause but no C command code!
		int startOffset = getOffset() + ssa.getSSAConditions().getOffset();
		int endOffset = startOffset + ssa.getSSAConditions().getLength();
		problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_VALUES_CLAUSE_MUST_FOLLOW_C);
	}

	private void checkMutuallyExclusiveCommandCodes(CommandCode code, List codesAlreadyFound, ICommandCodes cmdCodes) {

		String incompatCode = null;
		boolean incompatFound = false;

		if ("R".equalsIgnoreCase(code.getCode())) {
			if (codesAlreadyFound.contains("F")) {
				incompatCode = "F";
				incompatFound = true;
			}
			if (codesAlreadyFound.contains("Q")) {
				incompatCode = "Q";
				incompatFound = true;
			}
		}

		if ("L".equalsIgnoreCase(code.getCode())) {
			if (codesAlreadyFound.contains("F")) {
				incompatCode = "F";
				incompatFound = true;
			}
		}

		if ("U".equalsIgnoreCase(code.getCode())) {
			if (codesAlreadyFound.contains("V")) {
				incompatCode = "V";
				incompatFound = true;
			}
		}

		if ("F".equalsIgnoreCase(code.getCode())) {
			if (codesAlreadyFound.contains("R")) {
				incompatCode = "R";
				incompatFound = true;
			}
			if (codesAlreadyFound.contains("L")) {
				incompatCode = "L";
				incompatFound = true;
			}
		}

		if ("Q".equalsIgnoreCase(code.getCode())) {
			if (codesAlreadyFound.contains("R")) {
				incompatCode = "R";
				incompatFound = true;
			}
		}

		if ("V".equalsIgnoreCase(code.getCode())) {
			if (codesAlreadyFound.contains("U")) {
				incompatCode = "U";
				incompatFound = true;
			}
		}

		if (incompatFound) {
			int startOffset = getOffset() + cmdCodes.getOffset() + code.getOffset();
			problemRequestor.acceptProblem(startOffset, startOffset + 1, IMarker.SEVERITY_ERROR,
					IProblemRequestor.DLI_COMMAND_CODES_INCOMPATIBLE, new String[] { incompatCode, code.getCode() });
		}

	}

	private void checkCommandCodeC(CommandCode code, List allPreviousCommandCodes, ICommandCodes cmdCodes, ISegmentSearchArgument ssa,
			String dliFunction) {
		if (!"C".equals(code.getCode())) {
			return;
		}

		if (allPreviousCommandCodes.contains("C")) {
			int startOffset = getOffset() + cmdCodes.getOffset() + code.getOffset();
			problemRequestor.acceptProblem(startOffset, startOffset + 1, IMarker.SEVERITY_ERROR,
					IProblemRequestor.DLI_ONLY_ONE_C_COMMAND_CODE_ALLOWED);
		}

		if (ssa.getSSAConditions() != null && !ssa.getSSAConditions().isValueExpressionSSAConditions()) {
			int startOffset = getOffset() + ssa.getSSAConditions().getOffset();
			problemRequestor.acceptProblem(startOffset, startOffset + ssa.getSSAConditions().getLength(), IMarker.SEVERITY_ERROR,
					IProblemRequestor.DLI_NO_CONDITIONS_WITH_C_COMMAND_CODE);
		}

		if ("ISRT".equalsIgnoreCase(dliFunction) && allPreviousCommandCodes.contains("D")) {
			int startOffset = getOffset() + cmdCodes.getOffset() + code.getOffset();
			problemRequestor.acceptProblem(startOffset, startOffset + 1, IMarker.SEVERITY_ERROR,
					IProblemRequestor.DLI_COMMAND_CODE_C_CANNOT_FOLLOW_COMMAND_CODE_D);
		}
	}

	private void checkCommandCodeD(List allPreviousCommandCodes, ISegmentSearchArgument ssa, String dliFunction) {
		if (allPreviousCommandCodes.contains("D") && "ISRT".equalsIgnoreCase(dliFunction) && ssa.getSSAConditions() != null) {
			int startOffset = getOffset() + ssa.getSSAConditions().getOffset();
			problemRequestor.acceptProblem(startOffset, startOffset + ssa.getSSAConditions().getLength(), IMarker.SEVERITY_ERROR,
					IProblemRequestor.DLI_NO_QUALIFIED_SSA_CANNOT_FOLLOW_COMMAND_CODE_D);
		}
	}

	private void checkCommandCodesForOneAllowed(CommandCode[] codes, ICommandCodes cmdCodes) {

		boolean foundM = false;
		boolean foundS = false;
		boolean foundW = false;
		boolean foundZ = false;

		for (int i = 0; i < codes.length; i++) {
			if ("M".equalsIgnoreCase(codes[i].code)) {
				foundM = true;
				continue;
			}
			if ("S".equalsIgnoreCase(codes[i].code)) {
				foundS = true;
				continue;
			}
			if ("W".equalsIgnoreCase(codes[i].code)) {
				foundW = true;
				continue;
			}
			if ("Z".equalsIgnoreCase(codes[i].code)) {
				foundZ = true;
				continue;
			}

		}

		int numFound = 0;
		if (foundM) {
			numFound = numFound + 1;
		}
		if (foundS) {
			numFound = numFound + 1;
		}
		if (foundW) {
			numFound = numFound + 1;
		}
		if (foundZ) {
			numFound = numFound + 1;
		}

		if (numFound > 1) {
			int startOffset = getOffset() + cmdCodes.getOffset();
			problemRequestor.acceptProblem(startOffset, startOffset + cmdCodes.getLength(), IMarker.SEVERITY_ERROR,
					IProblemRequestor.DLI_ONLY_ONE_COMMAND_CODE_SUPPORTED);
		}
	}

	private void validateDLIStatement() {

		checkForPCB();
		validateIOTargetsInPCB();

		if (!dliIOStmt.getDliInfo().isHasDLICall()) {
			validateHaveHierarchy();
			return;
		}

		Iterator i = dliIOStmt.getDliInfo().getModel().getStatements().iterator();
		int j = 1;
		while (i.hasNext()) {
			IStatement modelStmt = (IStatement) i.next();
			validateSSAs(modelStmt.getSegmentSearchArguments());
			if (isInlineDLIStatement()) {
				validateFunctionCodesNeedingContext(modelStmt, j, i.hasNext());
				validateCommandCodesWithTargetArray(modelStmt);
				validateLastSSA(modelStmt);
				validateSSAsWithPathCallHaveTarget(modelStmt);
				validateSSAHierarchy(modelStmt);
				validateSSAsInPCB(modelStmt);
				if (j == 1) {
					validateSSAForEachTarget(modelStmt);
				}
			}
			j++;
		}
	}

	private void validateHaveHierarchy() {
		if (getDliStmtFactory().getPCB() != null && getDliStmtFactory().getPCB().getPCB() != null
				&& getDliStmtFactory().getPCB().getPCB().getPCBType() != null
				&& getDliStmtFactory().getPCB().getPCB().getPCBType().getName() == InternUtil.intern("GSAM")) {
			return;
		}

		if (getPcbName() != null
				&& getDliStmtFactory().getPCB() != null
				&& (getDliStmtFactory().getPCB().getPCB() == null || getDliStmtFactory().getPCB().getPCB().getHierarchy() == null || getDliStmtFactory()
						.getPCB().getPCB().getHierarchy().length == 0)) {
			if (getDliIOType() == ADD_IO || getDliIOType() == GET_KEY_IO) {
				problemRequestor.acceptProblem((Node) dliIOStmt, IProblemRequestor.DLI_NO_HIERARCHY_NO_DEFAULT_SSAS,
						IMarker.SEVERITY_ERROR, new String[] { getPcbName() });
			}
		}

	}

	private void validateSSAsWithPathCallHaveTarget(IStatement modelStmt) {
		if (modelStmt == null || modelStmt.getSegmentSearchArguments() == null) {
			return;
		}
		Iterator i = modelStmt.getSegmentSearchArguments().iterator();
		while (i.hasNext()) {
			ISegmentSearchArgument ssa = (ISegmentSearchArgument) i.next();
			ICommandCodes codes = ssa.getCommandCodes();
			if (codes != null) {
				int index = codes.getCommandCodes().toUpperCase().indexOf("D");
				if (index != -1 && ssa.getSegmentName() != null) {
					IDLISegmentRecord rec = getDliStmtFactory().getSegment(ssa.getSegmentName().getName());
					if (rec == null) {
						int startOffset = getOffset() + codes.getOffset() + index;
						int endOffset = startOffset + 1;
						problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR,
								IProblemRequestor.DLI_D_COMMAND_CODE_MUST_HAVE_TARGET, new String[] { ssa.getSegmentName().getName() });
					}
				}
			}
		}
	}

	private void validateSSAForEachTarget(IStatement modelStmt) {
		IDLISegmentRecord[] segments = getDliStmtFactory().getSegments();
		if (segments.length > 1) {
			for (int i = 0; i < segments.length; i++) {
				IDLISegmentRecord segment = segments[i];
				if (segment != null) {
					ISegmentSearchArgument ssa = getSSA(segment.getSegmentName(), modelStmt);
					if (ssa == null) {
						int startOffset = getOffset();
						int endOffset = startOffset + modelStmt.getLength();
						problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR,
								IProblemRequestor.DLI_MUST_BE_AN_SSA_FOR_EACH_TARGET, new String[] { segment.getSegmentName() });
					} else {
						if ((IEGLConstants.DLIKEYWORD_ISRT.equalsIgnoreCase(modelStmt.getDLIFunction()) && i == 0)
								|| (IEGLConstants.DLIKEYWORD_GU.equalsIgnoreCase(modelStmt.getDLIFunction()) && i < segments.length - 1)
								|| (IEGLConstants.DLIKEYWORD_GHU.equalsIgnoreCase(modelStmt.getDLIFunction()) && i < segments.length - 1)
								|| (IEGLConstants.DLIKEYWORD_GN.equalsIgnoreCase(modelStmt.getDLIFunction()) && i < segments.length - 1)
								|| (IEGLConstants.DLIKEYWORD_GHN.equalsIgnoreCase(modelStmt.getDLIFunction()) && i < segments.length - 1)
								|| (IEGLConstants.DLIKEYWORD_GNP.equalsIgnoreCase(modelStmt.getDLIFunction()) && i < segments.length - 1)
								|| (IEGLConstants.DLIKEYWORD_GHNP.equalsIgnoreCase(modelStmt.getDLIFunction()) && i < segments.length - 1)) {
							String codes = "";
							if (ssa.getCommandCodes() != null) {
								codes = ssa.getCommandCodes().getCommandCodes();
							}
							if (codes.toUpperCase().indexOf("D") == -1) {
								int startOffset = getOffset() + ssa.getOffset();
								int endOffset = startOffset + ssa.getLength();
								problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR,
										IProblemRequestor.DLI_D_COMMAND_CODE_MUST_BE_ON_FIRST_SSA, new String[] { ssa.getSegmentName()
												.getName() });
							}
						}
					}
				}
			}
		}
	}

	private ISegmentSearchArgument getSSA(String segmentName, IStatement modelStmt) {
		if (segmentName == null || modelStmt.getSegmentSearchArguments() == null) {
			return null;
		}
		Iterator i = modelStmt.getSegmentSearchArguments().iterator();
		while (i.hasNext()) {
			ISegmentSearchArgument ssa = (ISegmentSearchArgument) i.next();
			if (segmentName.equalsIgnoreCase(ssa.getSegmentName().getName())) {
				return ssa;
			}
		}
		return null;
	}

	private void validateIOTargetsInPCB() {
		if (getDliStmtFactory().getPCB() != null && getDliStmtFactory().getPCB().getPCB() != null
				&& getDliStmtFactory().getPCB().getPCB().getPCBType() != null
				&& getDliStmtFactory().getPCB().getPCB().getPCBType().getName() == InternUtil.intern("GSAM")) {
			problemRequestor.acceptProblem((Node) dliIOStmt, IProblemRequestor.DLI_PCB_IS_GSAM_PCB, IMarker.SEVERITY_ERROR,
					new String[] { getPcbName() });

			return;
		}

		IHierarchy hierarchy = getDliStmtFactory().getHierarchy();
		if (hierarchy == null || hierarchy.getEntries() == null || hierarchy.getEntries().length == 0) {
			return;
		}

		IDLISegmentRecord[] segments = getDliStmtFactory().getSegments();
		int level = -1;
		boolean foundError = false;
		for (int i = 0; i < segments.length; i++) {
			IDLISegmentRecord segment = segments[i];
			if (segment == null) {
				continue;
			}
			IHierarchyEntry entry = hierarchy.getEntry(segment.getSegmentName());
			if (entry == null) {
				String pcbName = getPcbName();
				if (pcbName == null) {
					pcbName = getDliStmtFactory().getPsb().getName() + "." + getDliStmtFactory().getPCB().getName();
				}
				problemRequestor.acceptProblem((Node) dliIOStmt, IProblemRequestor.DLI_SEGMENT_NOT_IN_PCB, IMarker.SEVERITY_ERROR,
						new String[] { pcbName, segment.getSegmentName() });
			} else {
				if (getDliIOType() == ADD_IO) {
					if (level != -1 && entry.getLevel() != level + 1) {
						foundError = true;
					}
				} else {
					if (level >= entry.getLevel()) {
						foundError = true;
					}
				}
				level = entry.getLevel();
			}
		}

		if (foundError) {
			int msgNum;
			if (getDliIOType() == ADD_IO) {
				msgNum = IProblemRequestor.DLI_ADD_TARGETS_MUST_FOLLOW_HIERARCHY;
			} else {
				msgNum = IProblemRequestor.DLI_TARGETS_MUST_FOLLOW_HIERARCHY;
			}

			String pcbName = getPcbName();
			if (pcbName == null) {
				pcbName = getDliStmtFactory().getPsb().getName() + "." + getDliStmtFactory().getPCB().getName();
			}
			problemRequestor.acceptProblem((Node) dliIOStmt, msgNum, IMarker.SEVERITY_ERROR, new String[] { pcbName });
		}

	}

	private boolean pcbContainsSegment(String name) {
		if (getDliStmtFactory().getPCB().getPCB() == null || name == null) {
			return false;
		}
		IPCB pcb = getDliStmtFactory().getPCB().getPCB();

		if (pcb.getHierarchy() == null) {
			return false;
		}

		IRelationship[] relations = pcb.getHierarchy();

		for (int i = 0; i < relations.length; i++) {
			if (relations[i].getSegmentRecord() != null && name.equalsIgnoreCase(relations[i].getSegmentRecord().getSegmentName())) {
				return true;
			}
		}

		return false;
	}

	private void validateSSAHierarchy(IStatement modelStmt) {
		if (modelStmt != null && getDliStmtFactory().getSegments().length > 0) {
			IDLISegmentRecord[] segments = getDliStmtFactory().getSegments();
			IDLISegmentRecord lastSegment = segments[segments.length - 1];
			if (lastSegment == null) {
				return;
			}
			IHierarchy hierarchy = getDliStmtFactory().getHierarchy(lastSegment);
			if (hierarchy == null || hierarchy.getEntries() == null || hierarchy.getEntries().length == 0) {
				return;
			}
			validateSSAsMatchHierarchy(hierarchy, modelStmt);

		}
	}

	private void validateSSAsMatchHierarchy(IHierarchy hierarchy, IStatement modelStmt) {

		int level = -1;
		Iterator i = modelStmt.getSegmentSearchArguments().iterator();
		List segmentsNotFound = new ArrayList();
		for (int j = 0; j < hierarchy.getEntries().length; j++) {
			segmentsNotFound.add(hierarchy.getEntries()[j]);
		}
		boolean hierarchyError = false;
		boolean foundCCmdCode = false;

		while (i.hasNext()) {
			ISegmentSearchArgument ssa = (ISegmentSearchArgument) i.next();
			String segmentName = ssa.getSegmentName().getName();
			ICommandCodes codes = ssa.getCommandCodes();
			foundCCmdCode = (codes != null) && (codes.getCommandCodes() != null)
					&& (codes.getCommandCodes().toUpperCase().indexOf("C") != -1);
			IHierarchyEntry entry = hierarchy.getEntry(segmentName);
			if (entry == null) {
				if (pcbContainsSegment(segmentName)) {
					hierarchyError = true;
				}
			} else {
				segmentsNotFound.remove(entry);
				if (entry.getLevel() <= level) {
					hierarchyError = true;
				}
				level = entry.getLevel();
			}
		}

		if (hierarchyError) {
			int startOffset = getOffset();
			int endOffset = startOffset + modelStmt.getLength();
			String pcbName = getPcbName();
			if (pcbName == null) {
				pcbName = getDliStmtFactory().getPsb().getName() + "." + getDliStmtFactory().getPCB().getName();
			}
			problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR,
					IProblemRequestor.DLI_SSAS_MUST_FOLLOW_HIERARCHY, new String[] { pcbName });
		}

		if (!foundCCmdCode
				&& (IEGLConstants.DLIKEYWORD_GU.equalsIgnoreCase(modelStmt.getDLIFunction()) || IEGLConstants.DLIKEYWORD_GHU
						.equalsIgnoreCase(modelStmt.getDLIFunction()))) {
			i = segmentsNotFound.iterator();
			while (i.hasNext()) {
				IHierarchyEntry entry = (IHierarchyEntry) i.next();
				int startOffset = getOffset();
				int endOffset = startOffset + modelStmt.getLength();
				String pcbName = getPcbName();
				if (pcbName == null) {
					pcbName = getDliStmtFactory().getPsb().getName() + "." + getDliStmtFactory().getPCB().getName();
				}
				problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR,
						IProblemRequestor.DLI_MUST_BE_AN_SSA_FOR_EACH_SEGMENT_TO_ROOT, new String[] { pcbName, entry.getSegmentName() });
			}
		}
	}

	private void validateSSAsInPCB(IStatement modelStmt) {

		if (getDliStmtFactory().getPCB() == null || getDliStmtFactory().getPCB().getPCB() == null
				|| getDliStmtFactory().getPCB().getPCB().getHierarchy() == null
				|| getDliStmtFactory().getPCB().getPCB().getHierarchy().length == 0) {
			return;
		}

		if (getDliStmtFactory().getPCB().getPCB().getPCBType() != null
				&& getDliStmtFactory().getPCB().getPCB().getPCBType().getName() == InternUtil.intern("GSAM")) {
			return;
		}

		Iterator i = modelStmt.getSegmentSearchArguments().iterator();

		while (i.hasNext()) {
			ISegmentSearchArgument ssa = (ISegmentSearchArgument) i.next();
			String segmentName = ssa.getSegmentName().getName();
			if (!pcbContainsSegment(segmentName)) {
				int startOffset = getOffset() + ssa.getSegmentName().getOffset();
				int endOffset = startOffset + ssa.getSegmentName().getLength();
				String pcbName = getPcbName();
				if (pcbName == null) {
					pcbName = getDliStmtFactory().getPsb().getName() + "." + getDliStmtFactory().getPCB().getName();
				}
				problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR,
						IProblemRequestor.DLI_SEGMENT_NOT_IN_HIERARCHY, new String[] { segmentName, pcbName });
			}
		}
	}

	private void validateLastSSA(IStatement modelStmt) {

		if (getDliStmtFactory().getPCB() != null && getDliStmtFactory().getPCB().getPCB() != null
				&& getDliStmtFactory().getPCB().getPCB().getPCBType() != null
				&& getDliStmtFactory().getPCB().getPCB().getPCBType().getName() == InternUtil.intern("GSAM")) {
			return;
		}

		if (modelStmt != null && getDliStmtFactory().getSegments().length > 0 && modelStmt.getSegmentSearchArguments() != null
				&& modelStmt.getSegmentSearchArguments().size() > 0 && getDliStmtFactory().getSegments().length > 0) {
			IDLISegmentRecord[] segments = getDliStmtFactory().getSegments();
			String segNameMustBe = segments[segments.length - 1].getSegmentName();
			ISegmentSearchArgument ssa = (ISegmentSearchArgument) modelStmt.getSegmentSearchArguments().get(
					modelStmt.getSegmentSearchArguments().size() - 1);
			if (!segNameMustBe.equalsIgnoreCase(ssa.getSegmentName().getName())) {
				int startOffset = getOffset() + ssa.getSegmentName().getOffset();
				int endOffset = startOffset + ssa.getSegmentName().getLength();
				problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_LAST_SSA_WRONG_NAME,
						new String[] { segNameMustBe });
			}
		}
	}

	private void checkForPCB() {
		if (getPcbName() == null && getDliStmtFactory().getPCB() == null && getDliStmtFactory().getSegments().length > 0
				&& getDliStmtFactory().getPsb() != null) {
			IDLISegmentRecord[] segments = getDliStmtFactory().getSegments();
			if (segments.length == 1) {
				String segName = segments[0].getSegmentName();
				problemRequestor.acceptProblem((Node) dliIOStmt, IProblemRequestor.DLI_NO_PCB_FOR_SEGMENT, IMarker.SEVERITY_ERROR,
						new String[] { getDliStmtFactory().getPsb().getName(), segName });
			} else {
				String segNames = segments[0].getSegmentName();
				for (int i = 1; i < segments.length; i++) {
					segNames = segNames + ", ";
					segNames = segNames + segments[i].getSegmentName();
				}
				problemRequestor.acceptProblem((Node) dliIOStmt, IProblemRequestor.DLI_NO_PCB_FOR_SEGMENTS, IMarker.SEVERITY_ERROR,
						new String[] { getDliStmtFactory().getPsb().getName(), segNames });
			}
		}
	}

	private void validateCommandCodesWithTargetArray(IStatement statement) {
		if (!isTargetAnArray() || statement == null) {
			return;
		}
		String dliFunction = statement.getDLIFunction().toUpperCase();
		Iterator i = statement.getSegmentSearchArguments().iterator();
		while (i.hasNext()) {
			ISegmentSearchArgument ssa = (ISegmentSearchArgument) i.next();
			if (ssa.getCommandCodes() == null) {
				continue;
			}
			String codes = ssa.getCommandCodes().getCommandCodes().toUpperCase();
			int index;

			if ("ISRT".equalsIgnoreCase(dliFunction)) {
				index = codes.indexOf("D");
				if (index != -1) {
					int start = getOffset() + ssa.getCommandCodes().getOffset() + index;
					problemRequestor.acceptProblem(start, start + 1, IMarker.SEVERITY_ERROR,
							IProblemRequestor.DLI_INVALID_COMMAND_CODE_FOR_CALL_WITH_ARRAY, new String[] { "D", dliFunction });
				}
			}

			if ("GU".equalsIgnoreCase(dliFunction)) {
				index = codes.indexOf("L");
				if (index != -1) {
					int start = getOffset() + ssa.getCommandCodes().getOffset() + index;
					problemRequestor.acceptProblem(start, start + 1, IMarker.SEVERITY_ERROR,
							IProblemRequestor.DLI_INVALID_COMMAND_CODE_FOR_CALL_WITH_ARRAY, new String[] { "L", dliFunction });
				}
				index = codes.indexOf("D");
				if (index != -1) {
					int start = getOffset() + ssa.getCommandCodes().getOffset() + index;
					problemRequestor.acceptProblem(start, start + 1, IMarker.SEVERITY_ERROR,
							IProblemRequestor.DLI_INVALID_COMMAND_CODE_FOR_CALL_WITH_ARRAY, new String[] { "D", dliFunction });
				}
			}

			if ("GN".equalsIgnoreCase(dliFunction) || "GNP".equalsIgnoreCase(dliFunction)) {
				index = codes.indexOf("F");
				if (index != -1) {
					int start = getOffset() + ssa.getCommandCodes().getOffset() + index;
					problemRequestor.acceptProblem(start, start + 1, IMarker.SEVERITY_ERROR,
							IProblemRequestor.DLI_INVALID_COMMAND_CODE_FOR_CALL_WITH_ARRAY, new String[] { "F", dliFunction });
				}

				index = codes.indexOf("L");
				if (index != -1) {
					int start = getOffset() + ssa.getCommandCodes().getOffset() + index;
					problemRequestor.acceptProblem(start, start + 1, IMarker.SEVERITY_ERROR,
							IProblemRequestor.DLI_INVALID_COMMAND_CODE_FOR_CALL_WITH_ARRAY, new String[] { "L", dliFunction });
				}

				index = codes.indexOf("D");
				if (index != -1) {
					int start = getOffset() + ssa.getCommandCodes().getOffset() + index;
					problemRequestor.acceptProblem(start, start + 1, IMarker.SEVERITY_ERROR,
							IProblemRequestor.DLI_INVALID_COMMAND_CODE_FOR_CALL_WITH_ARRAY, new String[] { "D", dliFunction });
				}
			}

		}
	}

	private void addMessage(int msgNumber, String[] inserts, int off1, int off2) {

		if (isInlineDLIStatement()) {
			problemRequestor.acceptProblem(off1, off2, IMarker.SEVERITY_ERROR, msgNumber, inserts);
		} else {
			// must hang the message on the io statement
			problemRequestor.acceptProblem((Node) dliIOStmt, msgNumber, IMarker.SEVERITY_ERROR, inserts);
		}
	}

	private void validateFunctionCodes(IStatement statement) {
		boolean isValid = false;
		String ioType = ""; //$NON-NLS-1$
		String functionType = ""; //$NON-NLS-1$
		String function = statement.getDLIFunction();

		switch (getDliIOType()) {
		case ADD_IO:
			ioType = IEGLConstants.KEYWORD_ADD;
			functionType = IEGLConstants.DLIKEYWORD_ISRT.toUpperCase();
			break;
		case GET_KEY_IO:
			ioType = "get by key"; //$NON-NLS-1$
			if (isForUpdate())
				functionType = IEGLConstants.DLIKEYWORD_GHU.toUpperCase();
			else
				functionType = IEGLConstants.DLIKEYWORD_GU.toUpperCase();
			break;
		case GET_POSITION_IO:
			ioType = "get by position"; //$NON-NLS-1$
			if (isForUpdate() && isGetInParent())
				functionType = IEGLConstants.DLIKEYWORD_GHNP.toUpperCase();
			else if (isForUpdate())
				functionType = IEGLConstants.DLIKEYWORD_GHN.toUpperCase();
			else if (isGetInParent())
				functionType = IEGLConstants.DLIKEYWORD_GNP.toUpperCase();
			else
				functionType = IEGLConstants.DLIKEYWORD_GN.toUpperCase();
			break;
		case REPLACE_IO:
			ioType = IEGLConstants.KEYWORD_REPLACE;
			functionType = IEGLConstants.DLIKEYWORD_REPL.toUpperCase();
			break;
		case DELETE_IO:
			ioType = IEGLConstants.KEYWORD_DELETE;
			functionType = IEGLConstants.DLIKEYWORD_DLET.toUpperCase();
			break;
		}

		isValid = function.equalsIgnoreCase(functionType) ? true : false;

		if (!isValid && !isGetInParent()) {
			problemRequestor
					.acceptProblem(getOffset(), getOffset() + function.length(), IMarker.SEVERITY_ERROR,
							isForUpdate() ? IProblemRequestor.DLI_GET_FORUPDATE_HAS_INVALID_FUNCTION_TYPE
									: IProblemRequestor.DLI_FUNCTION_TYPE_IS_INVALID_FOR_STATEMENT, new String[] { ioType, functionType,
									function });
		} else if (!isValid) {
			problemRequestor.acceptProblem(getOffset(), getOffset() + function.length(), IMarker.SEVERITY_ERROR,
					isForUpdate() ? IProblemRequestor.DLI_GET_BY_POSITION_INPARENT_AND_FORUPDATE_HAS_INVALID_FUNCTION_TYPE
							: IProblemRequestor.DLI_GET_BY_POSITION_INPARENT_HAS_INVALID_FUNCTION_TYPE, new String[] { function });
		}
	}

	private void validateFunctionCodesNeedingContext(IStatement statement, int numDliStmts, boolean hasNext) {

		if (getDliIOType() == GET_KEY_IO && !isForUpdate()) {

			if (numDliStmts == 1 && isTargetAnArray() && !hasNext) {
				problemRequestor.acceptProblem(getOffset(), getOffset() + statement.getLength(), IMarker.SEVERITY_ERROR,
						IProblemRequestor.DLI_GET_BY_KET_MUST_HAVE_TWO_DLI_CALLS_IF_TARGET_IS_ARRAY);
			} else if (numDliStmts != 2) {
				return;
			} else if (hasValidTarget() && !isTargetAnArray()) {
				problemRequestor.acceptProblem(getOffset() + statement.getOffset(), getOffset() + statement.getOffset()
						+ statement.getLength(), IMarker.SEVERITY_ERROR, IProblemRequestor.DLI_ONLY_ONE_DLI_CALL_UNLESS_TARGET_IS_ARRAY);
			}
		}
	}

	private void validateSSAs(List ssaList) {

		Iterator i = ssaList.iterator();
		while (i.hasNext()) {
			ISegmentSearchArgument modelSSA = (ISegmentSearchArgument) i.next();
			// validate SSA DLISegment name
			if (!MustBeDLINameAnnotationValidator.isValidDLIName(modelSSA.getSegmentName().getName())) {
				addMessage(IProblemRequestor.DLI_SEGMENT_NAME_IS_INVALID, new String[] { modelSSA.getSegmentName().getName() }, getOffset()
						+ modelSSA.getSegmentName().getOffset(), getOffset() + modelSSA.getSegmentName().getOffset()
						+ modelSSA.getSegmentName().getLength());
			}
			validateSSAConditions(modelSSA.getSSAConditions());
		}
	}

	private void validateSSAConditions(ISSAConditions conditions) {
		if (conditions != null) {
			if (conditions.isBooleanExpressionSSAConditions()) {
				IBooleanExpression boolExpr = ((IBooleanExpressionSSAConditions) conditions).getBooleanExpression();
				validateBooleanExpression(boolExpr);
			}
		}
	}

	private void validateBooleanExpression(IBooleanExpression boolExpr) {

		if (boolExpr != null && boolExpr.isCondition()) {
			ICondition modelCond = (ICondition) boolExpr;
			if (!MustBeDLINameAnnotationValidator.isValidDLIName(modelCond.getFieldName().getName())) {
				addMessage(IProblemRequestor.DLI_FIELD_NAME_IS_INVALID, new String[] { modelCond.getFieldName().getName() }, getOffset()
						+ modelCond.getFieldName().getOffset(), getOffset() + modelCond.getFieldName().getOffset()
						+ modelCond.getFieldName().getLength());
			}
			return;
		}

		if (boolExpr != null && boolExpr.isBooleanOperatorExpression()) {
			validateBooleanExpression(((IBooleanOperatorExpression) boolExpr).getLeftOperand());
			validateBooleanExpression(((IBooleanOperatorExpression) boolExpr).getRightOperand());
		}
	}

	private void initialize(UsingPCBClause pcbClause) {

		if (!dliIOStmt.getDliInfo().isDefaultStatement()) {
			setInlineDLIStatement(true);
			setOffset(dliIOStmt.getDliInfo().getInlineValueStart());
		}
		setDliStmtFactory(dliIOStmt.getDliInfo().getStatementFactory());

		if (pcbClause != null) {
			Expression expr = pcbClause.getPCB();
			setPcbName(expr.getCanonicalString());
		}
		if (dliIOStmt.getDliInfo().getTypeBindings() != null && dliIOStmt.getDliInfo().getTypeBindings().length > 0) {
			setHasValidTarget(true);
			if (dliIOStmt.getDliInfo().getTypeBindings()[0].getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
				setTargetIsArray(true);
			}
		}
	}

	/**
	 * @return Returns the dliIOType.
	 */
	private int getDliIOType() {
		return dliIOType;
	}

	/**
	 * @return Returns the offset.
	 */
	private int getOffset() {
		return offset;
	}

	/**
	 * @param offset
	 *            The offset to set.
	 */
	private void setOffset(int offset) {
		this.offset = offset;
	}

	public boolean isForUpdate() {
		return forUpdate;
	}

	public void setForUpdate(boolean forUpdate) {
		this.forUpdate = forUpdate;
	}

	public boolean isGetInParent() {
		return getInParent;
	}

	public void setGetInParent(boolean getInParent) {
		this.getInParent = getInParent;
	}

	public boolean isInlineDLIStatement() {
		return inlineDLIStatement;
	}

	public void setInlineDLIStatement(boolean inlineDLIStatement) {
		this.inlineDLIStatement = inlineDLIStatement;
	}

	public String getPcbName() {
		return pcbName;
	}

	public void setPcbName(String pcbName) {
		this.pcbName = pcbName;
	}

	public DLIDefaultStatementFactory getDliStmtFactory() {
		return dliStmtFactory;
	}

	public void setDliStmtFactory(DLIDefaultStatementFactory dliStmtFactory) {
		this.dliStmtFactory = dliStmtFactory;
	}

	public boolean hasValidTarget() {
		return hasValidTarget;
	}

	public void setHasValidTarget(boolean hasValidTarget) {
		this.hasValidTarget = hasValidTarget;
	}

	public boolean isTargetAnArray() {
		return targetIsArray;
	}

	public void setTargetIsArray(boolean targetIsArray) {
		this.targetIsArray = targetIsArray;
	}

}
