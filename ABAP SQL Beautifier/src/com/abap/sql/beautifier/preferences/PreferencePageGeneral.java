package com.abap.sql.beautifier.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import com.abap.sql.beautifier.Activator;

public class PreferencePageGeneral extends AbstractBeautifierPrefPage implements IWorkbenchPreferencePage {

	public PreferencePageGeneral() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("General settings for ABAP SQL Beautifier");
	}

	public void createFieldEditors() {
		addField(new RadioGroupFieldEditor(
				PreferenceConstants.OPERSTYLE, "Unify operators:", 3, new String[][] { { "disabled", "" },
						{ "sign (e.g. \'=\')", "sign" }, { "char (e.g. \'EQ\')", "char" } },
				getFieldEditorParent(), true));

		addField(new RadioGroupFieldEditor(PreferenceConstants.COMMENTSTYLE, "Store comments:", 3,
				new String[][] { { "delete", "DELETE" }, { "down", "DOWN" }, { "up", "UP" } }, getFieldEditorParent(),
				true));

		addField(new BooleanFieldEditor(PreferenceConstants.ALLIGN_OPERS, "Allign operators in conditions",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor(PreferenceConstants.PRETTY_PRINT,
				"Pretty print the sql statement (plugin backend needed)", getFieldEditorParent()));

		addField(new IntegerFieldEditor(PreferenceConstants.LINE_CHAR_LIMIT, "General char limit for one line",
				getFieldEditorParent()));

	}

	public void init(IWorkbench workbench) {
	}

}