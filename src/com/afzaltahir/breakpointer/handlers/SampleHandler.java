package com.afzaltahir.breakpointer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;

public class SampleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"Breakpointer",
				"Make sure you have added bpa.json file to your user home directory. Press OK to proceed.");
		BreakpointAdder ba = new BreakpointAdder();
		ba.addBreakpoints();
		MessageDialog.openInformation(
				window.getShell(),
				"Breakpointer",
				"Breakpoint added Successfully. \n\n Developed By:\n   Afzal Tahir Wattu (afzaltahir.com) \n\n Inspired By:\n   Syed Talha Ahmed😇\n\n Uninspired By:\n   Majid Ali 😅");
		return null;
	}
}