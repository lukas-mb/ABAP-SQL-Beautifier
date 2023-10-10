package com.abap.sql.beautifier.settings;

import java.util.ArrayList;
import java.util.List;

import com.abap.sql.beautifier.Abap;
import com.abap.sql.beautifier.Activator;
import com.abap.sql.beautifier.preferences.PreferenceConstants;
import com.abap.sql.beautifier.statement.AbapSqlPart;
import com.abap.sql.beautifier.utility.Utility;

public class SpaceAdder extends AbstractSqlSetting {

	String emptySpaces = "";

	public SpaceAdder() {

	}

	@Override
	public void apply() {

		// everything
		tabAllLines();

		// conditions
		boolean shouldTabCond;
		boolean oldSyntax = abapSql.isOldSyntax();

		if (oldSyntax) {
			shouldTabCond = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.TAB_CONDITIONS);
		} else {
			shouldTabCond = Activator.getDefault().getPreferenceStore()
					.getBoolean(PreferenceConstants.TAB_CONDITIONS_NEW_SYNTAX);
		}

		if (shouldTabCond) {

			addSpacesFrom();

			addSpacesWhere();

		}
	}

	private void addSpacesFrom() {
		AbapSqlPart fromPart = abapSql.getPart(Abap.FROM);

		if (fromPart == null) {
			return;
		}

		List<String> fromLines = fromPart.getLines();
		List<String> newFromLines = new ArrayList<>();

		for (String line : fromLines) {
			String newLine = tabConditionLine(line);
			newFromLines.add(newLine);
		}

		fromPart.setLines(newFromLines);

		abapSql.setPart(Abap.FROM, fromPart);


	}

	private void addSpacesWhere() {
		AbapSqlPart wherePart = abapSql.getPart(Abap.WHERE);

		// if no conditions in sql, nothing to do
		if (wherePart == null) {
			return;
		}

		List<String> whereLines = wherePart.getLines();
		List<String> newWhereLines = new ArrayList<>();

		for (String line : whereLines) {
			newWhereLines.add(tabConditionLine(line));
		}

		wherePart.setLines(newWhereLines);

		abapSql.setPart(Abap.WHERE, wherePart);
	}

	private void tabAllLines() {
		// add empty spaces to every line except SELECT depending on preferences
		int emptySpacesInt;

		if (abapSql.isOldSyntax()) {
			emptySpacesInt = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.TAB_ALL);
		} else {
			emptySpacesInt = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.TAB_ALL_NEW_SYNTAX);
		}

		emptySpaces = Utility.getWhiteChars(emptySpacesInt);

		for (String keyword : abapSql.getOrder()) {
			if (abapSql.getPart(keyword) != null) {

				AbapSqlPart curPart = abapSql.getPart(keyword);
				List<String> curPartLines = curPart.getLines();
				List<String> newLines = new ArrayList<>();

				if (keyword.equalsIgnoreCase(Abap.SELECT) || keyword.equalsIgnoreCase(Abap.SELECTFROM)) {
					// exclude first line
					newLines.add(curPartLines.get(0));
					curPartLines.remove(0);
				}

				for (String line : curPartLines) {
					String newLine = abapSql.getStartWhite() + emptySpaces + line; // diff = empty spaces in selected
																					// ABAP Code
					newLines.add(newLine);
				}
				curPart.setLines(newLines);
				abapSql.setPart(keyword, curPart);
			}
		}

	}

	private String tabConditionLine(String line) {
		String newLine;

		// AND --> 1 space less
		if (line.trim().startsWith("AND")) {
			newLine = "  " + line;
		} else if (line.trim().startsWith("OR") || line.trim().startsWith("ON")) {
			newLine = "   " + line;
		} else {
			newLine = line;
		}

		return newLine;
	}

}
