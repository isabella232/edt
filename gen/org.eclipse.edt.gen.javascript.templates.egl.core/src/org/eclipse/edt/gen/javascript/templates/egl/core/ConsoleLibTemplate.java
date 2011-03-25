/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascript.templates.egl.core;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.NativeTypeTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.InvocationExpression;

public class ConsoleLibTemplate extends NativeTypeTemplate {
	private static final String displayLineMode = "displayLineMode";
	private static final String promptLineMode = "promptLineMode";
	private static final String activateWindow = "activateWindow";
	private static final String activateWindowByName = "activateWindowByName";
	private static final String clearActiveWindow = "clearActiveWindow";
	private static final String clearWindow = "clearWindow";
	private static final String clearWindowByName = "clearWindowByName";
	private static final String closeActiveWindow = "closeActiveWindow";
	private static final String closeWindow = "closeWindow";
	private static final String closeWindowByName = "closeWindowByName";
	private static final String drawBox = "drawBox";
	private static final String drawBoxWithColor = "drawBoxWithColor";
	private static final String displayAtLine = "displayAtLine";
	private static final String displayAtPosition = "displayAtPosition";
	private static final String displayMessage = "displayMessage";
	private static final String displayError = "displayError";
	private static final String getFieldBuf = "getFieldBuf";
	private static final String hideErrorWindow = "hideErrorWindow";
	private static final String openWindow = "openWindow";
	private static final String openWindowByName = "openWindowByName";
	private static final String openWindowWithForm = "openWindowWithForm";
	private static final String openWindowWithFormByName = "openWindowWithFormByName";
	private static final String clearActiveForm = "clearActiveForm";
	private static final String clearForm = "clearForm";
	private static final String clearFields = "clearFields";
	private static final String clearFieldsByName = "clearFieldsByName";
	private static final String displayForm = "displayForm";
	private static final String displayFormByName = "displayFormByName";
	private static final String displayFields = "displayFields";
	private static final String displayFieldsByName = "displayFieldsByName";
	private static final String gotoField = "gotoField";
	private static final String gotoFieldByName = "gotoFieldByName";
	private static final String nextField = "nextField";
	private static final String previousField = "previousField";
	private static final String isCurrentField = "isCurrentField";
	private static final String isCurrentFieldByName = "isCurrentFieldByName";
	private static final String isFieldModified = "isFieldModified";
	private static final String isFieldModifiedByName = "isFieldModifiedByName";
	private static final String getKey = "getKey";
	private static final String getKeyCode = "getKeyCode";
	private static final String getKeyName = "getKeyName";
	private static final String lastKeyTyped = "lastKeyTyped";
	private static final String cancelArrayDelete = "cancelArrayDelete";
	private static final String cancelArrayInsert = "cancelArrayInsert";
	private static final String currentArrayScreenLine = "currentArrayScreenLine";
	private static final String currentArrayDataLine = "currentArrayDataLine";
	private static final String scrollDownPage = "scrollDownPage";
	private static final String scrollDownLines = "scrollDownLines";
	private static final String scrollUpPage = "scrollUpPage";
	private static final String scrollUpLines = "scrollUpLines";
	private static final String setArrayLine = "setArrayLine";
	private static final String setCurrentArrayCount = "setCurrentArrayCount";
	private static final String showHelp = "showHelp";
	private static final String hideMenuItem = "hideMenuItem";
	private static final String hideMenuItemByName = "hideMenuItemByName";
	private static final String gotoMenuItem = "gotoMenuItem";
	private static final String gotoMenuItemByName = "gotoMenuItemByName";
	private static final String showMenuItem = "showMenuItem";
	private static final String showMenuItemByName = "showMenuItemByName";
	private static final String currentArrayCount = "currentArrayCount";
	private static final String showAllMenuItems = "showAllMenuItems";
	private static final String hideAllMenuItems = "hideAllMenuItems";
	private static final String updateWindowAttributes = "updateWindowAttributes";
	private static final String registerConsoleForm = "registerConsoleForm";
	private static final String openFileDialog = "openFileDialog";
	private static final String openDirectoryDialog = "openDirectoryDialog";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(displayLineMode))
			genDisplayLineMode(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(promptLineMode))
			genPromptLineMode(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(activateWindow))
			genActivateWindow(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(activateWindowByName))
			genActivateWindowByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(clearActiveWindow))
			genClearActiveWindow(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(clearWindow))
			genClearWindow(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(clearWindowByName))
			genClearWindowByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(closeActiveWindow))
			genCloseActiveWindow(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(closeWindow))
			genCloseWindow(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(closeWindowByName))
			genCloseWindowByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(drawBox))
			genDrawBox(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(drawBoxWithColor))
			genDrawBoxWithColor(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(displayAtLine))
			genDisplayAtLine(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(displayAtPosition))
			genDisplayAtPosition(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(displayMessage))
			genDisplayMessage(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(displayError))
			genDisplayError(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getFieldBuf))
			genGetFieldBuf(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(hideErrorWindow))
			genHideErrorWindow(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(openWindow))
			genOpenWindow(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(openWindowByName))
			genOpenWindowByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(openWindowWithForm))
			genOpenWindowWithForm(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(openWindowWithFormByName))
			genOpenWindowWithFormByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(clearActiveForm))
			genClearActiveForm(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(clearForm))
			genClearForm(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(clearFields))
			genClearFields(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(clearFieldsByName))
			genClearFieldsByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(displayForm))
			genDisplayForm(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(displayFormByName))
			genDisplayFormByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(displayFields))
			genDisplayFields(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(displayFieldsByName))
			genDisplayFieldsByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(gotoField))
			genGotoField(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(gotoFieldByName))
			genGotoFieldByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(nextField))
			genNextField(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(previousField))
			genPreviousField(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(isCurrentField))
			genIsCurrentField(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(isCurrentFieldByName))
			genIsCurrentFieldByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(isFieldModified))
			genIsFieldModified(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(isFieldModifiedByName))
			genIsFieldModifiedByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getKey))
			genGetKey(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getKeyCode))
			genGetKeyCode(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getKeyName))
			genGetKeyName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(lastKeyTyped))
			genLastKeyTyped(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(cancelArrayDelete))
			genCancelArrayDelete(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(cancelArrayInsert))
			genCancelArrayInsert(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(currentArrayScreenLine))
			genCurrentArrayScreenLine(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(currentArrayDataLine))
			genCurrentArrayDataLine(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(scrollDownPage))
			genScrollDownPage(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(scrollDownLines))
			genScrollDownLines(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(scrollUpPage))
			genScrollUpPage(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(scrollUpLines))
			genScrollUpLines(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setArrayLine))
			genSetArrayLine(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setCurrentArrayCount))
			genSetCurrentArrayCount(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(showHelp))
			genShowHelp(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(hideMenuItem))
			genHideMenuItem(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(hideMenuItemByName))
			genHideMenuItemByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(gotoMenuItem))
			genGotoMenuItem(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(gotoMenuItemByName))
			genGotoMenuItemByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(showMenuItem))
			genShowMenuItem(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(showMenuItemByName))
			genShowMenuItemByName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(currentArrayCount))
			genCurrentArrayCount(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(showAllMenuItems))
			genShowAllMenuItems(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(hideAllMenuItems))
			genHideAllMenuItems(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(updateWindowAttributes))
			genUpdateWindowAttributes(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(registerConsoleForm))
			genRegisterConsoleForm(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(openFileDialog))
			genOpenFileDialog(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(openDirectoryDialog))
			genOpenDirectoryDialog(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genDisplayLineMode(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genPromptLineMode(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genActivateWindow(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genActivateWindowByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genClearActiveWindow(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genClearWindow(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genClearWindowByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCloseActiveWindow(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCloseWindow(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCloseWindowByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDrawBox(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDrawBoxWithColor(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDisplayAtLine(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDisplayAtPosition(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDisplayMessage(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDisplayError(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetFieldBuf(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genHideErrorWindow(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genOpenWindow(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genOpenWindowByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genOpenWindowWithForm(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genOpenWindowWithFormByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genClearActiveForm(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genClearForm(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genClearFields(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genClearFieldsByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDisplayForm(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDisplayFormByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDisplayFields(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDisplayFieldsByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGotoField(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGotoFieldByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genNextField(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genPreviousField(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIsCurrentField(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIsCurrentFieldByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIsFieldModified(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIsFieldModifiedByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetKey(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetKeyCode(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetKeyName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genLastKeyTyped(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCancelArrayDelete(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCancelArrayInsert(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCurrentArrayScreenLine(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCurrentArrayDataLine(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genScrollDownPage(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genScrollDownLines(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genScrollUpPage(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genScrollUpLines(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetArrayLine(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetCurrentArrayCount(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genShowHelp(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genHideMenuItem(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genHideMenuItemByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGotoMenuItem(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGotoMenuItemByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genShowMenuItem(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genShowMenuItemByName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCurrentArrayCount(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genShowAllMenuItems(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genHideAllMenuItems(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genUpdateWindowAttributes(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genRegisterConsoleForm(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genOpenFileDialog(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genOpenDirectoryDialog(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
