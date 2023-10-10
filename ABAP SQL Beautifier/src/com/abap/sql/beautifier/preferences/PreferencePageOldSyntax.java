package com.abap.sql.beautifier.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.abap.sql.beautifier.Activator;

public class PreferencePageOldSyntax extends AbstractBeautifierPrefPage implements IWorkbenchPreferencePage {

	public PreferencePageOldSyntax() {
		super(GRID);

		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Settings for the old syntax");
	}

	@Override
	public void createFieldEditors() {

		createHeaderLabel("Order of keywords:");
		addField(new OrderEditor(PreferenceConstants.ORDER_OLD_SYNTAX, "", getFieldEditorParent()));

		createEmptyLabel();

		createHeaderLabel("Combining select with FROM/INTO:");
		addField(new BooleanFieldEditor(PreferenceConstants.COMBINE_SMALL_SELECT,
				"Combine small SELECT with FROM/INTO to one line", getFieldEditorParent()));

		addField(new IntegerFieldEditor(PreferenceConstants.COMBINE_CHAR_LIMIT, "Combine char limit for one line",
				getFieldEditorParent()));

		createEmptyLabel();

		createHeaderLabel("Add spaces before each line:");
		addField(new IntegerFieldEditor(PreferenceConstants.TAB_ALL, "Amount of empty spaces all lines (excl. SELECT)",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor(PreferenceConstants.TAB_CONDITIONS,
				"Tab condition lines with additional empty spaces", getFieldEditorParent()));

//		addField(new BooleanFieldEditor(PreferenceConstants.COMMAS, "Add commas between fields in SELECT area",
//				getFieldEditorParent()));
//
//		addField(new BooleanFieldEditor(PreferenceConstants.ESCAPING, "Add escaping ('@') to hostvariables (BETA)",
//				getFieldEditorParent()));
		
		createEmptyLabel();

		createHeaderLabel("Combining FROM/JOINS:");
		addField(new BooleanFieldEditor(PreferenceConstants.COMBINE_SMALL_JOINS,
				"Combine small FROM with JOINS to one line", getFieldEditorParent()));

		addField(new IntegerFieldEditor(PreferenceConstants.COMBINE_SMALL_JOINS_LIMIT,
				"Combine char limit for one line", getFieldEditorParent()));

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