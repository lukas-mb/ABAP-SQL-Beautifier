package com.abap.sql.beautifier.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public abstract class AbstractBeautifierPrefPage extends FieldEditorPreferencePage {
	public AbstractBeautifierPrefPage(int grid) {
		super(GRID);
	}

	protected void createEmptyLabel() {
		Label label = new Label(getFieldEditorParent(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 3, 1));
		label.setText("");
		label.setForeground(new Color(Display.getCurrent(), 0, 0, 0)); // black

	}

	protected void createHeaderLabel(String header) {
		Label label = new Label(getFieldEditorParent(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 3, 1));
		label.setText(header);
		label.setForeground(new Color(Display.getCurrent(), 0, 0, 0)); // black

		FontData[] data = label.getFont().getFontData();
		for (FontData datum : data) {
			datum.setStyle(SWT.BOLD);
		}

		Font font = new Font(label.getDisplay(), data);

		label.setFont(font);
	}

}
