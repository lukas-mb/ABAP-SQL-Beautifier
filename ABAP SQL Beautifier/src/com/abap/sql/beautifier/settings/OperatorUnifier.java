package com.abap.sql.beautifier.settings;

import java.util.ArrayList;
import java.util.List;

import com.abap.sql.beautifier.Activator;
import com.abap.sql.beautifier.preferences.PreferenceConstants;
import com.abap.sql.beautifier.statement.AbapSqlPart;

public class OperatorUnifier extends AbstractSqlSetting {

	String operStyle;

	public OperatorUnifier() {
	}

	public OperatorUnifier(String operStyle) {
		this.operStyle = operStyle;
	}

	@Override
	public void apply() {
		String operStyle = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.OPERSTYLE);
		if (operStyle == "sign" || operStyle == "char") {
			for (String keyword : abapSql.getOrder()) {

				AbapSqlPart curPart = abapSql.getPart(keyword);

				if (curPart != null) {
					List<String> curPartLines = curPart.getLines();
					List<String> newPartLines = new ArrayList<>();
					for (String line : curPartLines) {

						if (operStyle == "sign") {
							line = line.replace(" EQ ", " = ");
							line = line.replace(" NE ", " <> ");
							line = line.replace(" LT ", " < ");
							line = line.replace(" GT ", " > ");
							line = line.replace(" LE ", " <= ");
							line = line.replace(" GE ", " >= ");
						} else if (operStyle == "char") {
							line = line.replace(" = ", " EQ ");
							line = line.replace(" <> ", " NE ");
							line = line.replace(" < ", " LT ");
							line = line.replace(" > ", " GT ");
							line = line.replace(" <= ", " LE ");
							line = line.replace(" >= ", " GE ");
						}

						newPartLines.add(line);

					}
					
					curPart.setLines(newPartLines);

					abapSql.setPart(keyword, curPart);
				}
			}
		}

	}

	public void setOperStyleToChar() {
		operStyle = "char";
	}

	public void setOperStyleToSign() {
		operStyle = "sign";
	}

}
