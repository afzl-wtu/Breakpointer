package com.afzaltahir.breakpointer.handlers;

import java.util.Arrays;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;


public class BreakpointAdder {

    private  String[] TARGET_PACKAGES;
    private  String[] KEYWORDS;
	private String[] TARGET_CLASSES;

    public void addBreakpoints() {
    	ConfigLoader cl = new ConfigLoader();
    	cl.loadConfig();
    	TARGET_PACKAGES = cl.getPackages().toArray(new String[0]);
    	KEYWORDS = cl.getKeywords().toArray(new String[0]);
    	TARGET_CLASSES=cl.getJavaClasses().toArray(new String[0]);
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IProject[] projects = root.getProjects();

        for (IProject project : projects) {
            try {
                if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
                    IJavaProject javaProject = JavaCore.create(project);
                    scanPackagesAndAddBreakpoints(javaProject);
                }
            } catch (CoreException e) {
                logToConsole("Error: " + e.getMessage());
            }
        }
    }

    private void scanPackagesAndAddBreakpoints(IJavaProject javaProject) throws CoreException {
    	if (TARGET_PACKAGES.length==0) {
    		IPackageFragment[] packages = javaProject.getPackageFragments();
            for (IPackageFragment pkg : packages) {
                if (pkg.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    for (ICompilationUnit unit : pkg.getCompilationUnits()) {
                    	String className = unit.getElementName().replace(".java", ""); 
                    	if (Arrays.asList(TARGET_CLASSES).contains(className) || TARGET_CLASSES.length==0) 
                    	{ addBreakpointsInUnit(unit); }
                    }
                }
            }
    		return;
    	}
        for (String packageName : TARGET_PACKAGES) {
            IPackageFragment[] packages = javaProject.getPackageFragments();
            for (IPackageFragment pkg : packages) {
                if (pkg.getElementName().equals(packageName) && pkg.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    for (ICompilationUnit unit : pkg.getCompilationUnits()) {
                    	String className = unit.getElementName().replace(".java", ""); 
                    	if (Arrays.asList(TARGET_CLASSES).contains(className) || TARGET_CLASSES.length==0) 
                    	{ addBreakpointsInUnit(unit); }
                    }
                }
            }
        }
    }

  private void addBreakpointsInUnit(ICompilationUnit unit) {
    try {
        IResource resource = unit.getCorrespondingResource();
        if (resource == null) {
            logToConsole("Resource is null for unit: " + unit.getElementName());
            return;
        }

        IType primaryType = unit.findPrimaryType();
        String fullyQualifiedName = (primaryType != null) 
                                    ? primaryType.getFullyQualifiedName() 
                                    : getFallbackQualifiedName(unit);

        if (fullyQualifiedName == null) {
            logToConsole("Cannot determine fully qualified name for " + unit.getElementName());
            return;
        }

        String source = unit.getSource();
        String[] lines = source.split("\\R");
        int lineNumber = 1;

        for (String line : lines) {
            boolean isMatched = Arrays.stream(KEYWORDS).anyMatch(line::contains);
            if (isMatched && !line.trim().startsWith("//") && !line.trim().startsWith("/*") && !line.trim().startsWith("*")) {
                IBreakpoint breakpoint = JDIDebugModel.createLineBreakpoint(
                    resource,
                    fullyQualifiedName, // Fully qualified class name
                    lineNumber, 
                    -1, -1, 0, 
                    true, 
                    null
                );
                DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(breakpoint);
                logToConsole("Breakpoint added in " + fullyQualifiedName + " at line " + lineNumber);
            }
            lineNumber++;
        }
    } catch (CoreException e) {
        logToConsole("Error adding breakpoint: " + e.getMessage());
    }
}

private String getFallbackQualifiedName(ICompilationUnit unit) {
    try {
        String packageName = unit.getParent().getElementName();
        String className = unit.getElementName().replace(".java", "");
        return packageName.isEmpty() ? className : packageName + "." + className;
    } catch (Exception e) {
        logToConsole("Error generating fallback qualified name: " + e.getMessage());
        return null;
    }
}
    private void logToConsole(String message) {
        MessageConsole console = findConsole("Breakpoint Adder Console");
        MessageConsoleStream out = console.newMessageStream();
        out.println(message);
    }

    private MessageConsole findConsole(String name) {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsole[] consoles = plugin.getConsoleManager().getConsoles();
        
        for (IConsole console : consoles) {
            if (console instanceof MessageConsole && console.getName().equals(name)) {
                return (MessageConsole) console;
            }
        }
        // No console found, so create a new one
        MessageConsole console = new MessageConsole(name, null);
        plugin.getConsoleManager().addConsoles(new IConsole[] { console });
        return console;
    }

}

