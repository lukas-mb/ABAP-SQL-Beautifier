package com.abap.sql.beautifier;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sap.adt.project.IProjectProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

public class PrettyPrinterConnector implements IPartListener2 {

	private static final String ADT_PREFIX = "com.sap.adt";
	private static final String FM = "Z_ADT_PRETTY_PRINTER";

	private static JCoDestination getDestination(IWorkbenchPart part) throws JCoException {
		IProjectProvider projectProvider = (IProjectProvider) part;
		IProject project = projectProvider.getProject();
		String destinationId = com.sap.adt.project.AdtCoreProjectServiceFactory.createCoreProjectService()
				.getDestinationId(project);
		return JCoDestinationManager.getDestination(destinationId);
	}

	public static String prettyPrint(String inputCode) {

		StringBuilder outputCode = new StringBuilder();

		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = activeWindow.getActivePage();
		IWorkbenchPartReference partRef = activePage.getActivePartReference();

		String id = partRef.getId();

		if (id.startsWith(ADT_PREFIX)) {
			JCoDestination destination;
			try {
				destination = getDestination(partRef.getPart(true));

				JCoFunction function = destination.getRepository().getFunction(FM);
				function.getImportParameterList().setValue("IV_CODE", inputCode);

				function.execute(destination);

				JCoTable table = function.getExportParameterList().getTable("ET_LINES");

				for (int i = 0; i < table.getNumRows(); i++) {
					table.setRow(i);
					outputCode.append(table.getString()).append(System.lineSeparator());
				}

			} catch (JCoException e) {
				e.printStackTrace();

			}
		}

		return outputCode.toString();
	}

}
