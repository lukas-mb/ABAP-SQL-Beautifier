package com.abap.sql.beautifier.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.abap.sql.beautifier.Activator;

public class PreferencePageNewSyntax extends AbstractBeautifierPrefPage implements IWorkbenchPreferencePage {

	public PreferencePageNewSyntax() {
		super(GRID);

		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Settings for the new syntax");
	}

	@Override
	public void createFieldEditors() {

		createHeaderLabel("Order of keywords:");
		addField(new OrderEditor(PreferenceConstants.ORDER_NEW_SYNTAX, "", getFieldEditorParent()));
		
		createEmptyLabel();
		createHeaderLabel("Add spaces before each line:");
		addField(new IntegerFieldEditor(PreferenceConstants.TAB_ALL_NEW_SYNTAX, "Amount of empty spaces all lines (excl. SELECT FROM)",
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.TAB_CONDITIONS_NEW_SYNTAX,
				"Tab condition lines with additional empty spaces", getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void performApply() {

		super.performApply();
	}

	@Override
	public boolean performOk() {

		return super.performOk();
	}

}