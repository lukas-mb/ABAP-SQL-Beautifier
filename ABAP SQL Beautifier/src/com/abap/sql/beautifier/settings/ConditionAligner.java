package com.abap.sql.beautifier.settings;

import java.util.ArrayList;
import java.util.List;

import com.abap.sql.beautifier.Abap;
import com.abap.sql.beautifier.Activator;
import com.abap.sql.beautifier.preferences.PreferenceConstants;
import com.abap.sql.beautifier.statement.AbapSqlPart;
import com.abap.sql.beautifier.utility.Utility;

public class ConditionAligner extends AbstractSqlSetting {

	public ConditionAligner() {
	}

	private List<String> alignOperators(List<String> sqlConditions) {
		sqlConditions = deleteUnnecessaryEmptyChars(sqlConditions);

		int curIndex = 0;
		int maxIndex = getMaxIndexOfOperator(sqlConditions);

		StringBuilder sb;

		boolean shouldAlignOneCharOpers = !validateOpersSameLength(sqlConditions);

		List<String> returnLines = new ArrayList<>();

		for (String line : sqlConditions) {

			sb = new StringBuilder(line);

			curIndex = getIndexOfOper(line);

			// if no oper found --> curIndex = -1
			if (curIndex >= 0) {

				curIndex = 0;
				while (curIndex < maxIndex) {

					curIndex = getIndexOfOper(line);

					// if still below max index, insert empty char
					if (curIndex < maxIndex) {
						line = sb.insert(curIndex, " ").toString();
					}

				}
			}

			if (shouldAlignOneCharOpers) {
				for (String oper : Utility.getOneCharOpers()) {
					if (line.contains(oper)) {
						line = sb.insert(curIndex + 2, " ").toString();
						break;
					}
				}
			}

			returnLines.add(line);

		}

		return returnLines;
	}

	@Override
	public void apply() {

		boolean shouldAlign = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.ALLIGN_OPERS);

		if (shouldAlign) {
			
			AbapSqlPart wherePart = abapSql.getPart(Abap.WHERE);

			if (wherePart != null) {
				List<String> wherePartLines = wherePart.getLines();
				wherePartLines = alignOperators(wherePartLines);
				wherePart.setLines(wherePartLines);
				abapSql.setPart(Abap.WHERE, wherePart);
			}

			AbapSqlPart fromPart = abapSql.getPart(Abap.FROM);

			if (fromPart != null) {
				List<String> fromPartLines = fromPart.getLines();
				fromPartLines = alignOperators(fromPartLines);
				fromPart.setLines(fromPartLines);
				abapSql.setPart(Abap.FROM, fromPart);
			}

		}

	}

	private List<String> deleteUnnecessaryEmptyChars(List<String> sqlConditions) {
		// delete tabs and unnecessary empty chars
		for (int i = 0; i < sqlConditions.size(); i++) {
			String curLine = sqlConditions.get(i);
			curLine = curLine.replace("\t", "");
			sqlConditions.set(i, curLine);
		}

		return sqlConditions;
	}

	private int getIndexOfOper(String line) {
		List<String> opers = Utility.getAllOperators();
		for (String oper : opers) {
			int index = line.indexOf(oper);
			index = line.lastIndexOf(oper);
			if (index != -1) {
				// oper found
				return index;
			}
		}
		return -1;
	}

	private int getMaxIndexOfOperator(List<String> sqlConditions) {
		int curIndex = 0;
		int maxIndex = 0;
		for (String line : sqlConditions) {
			curIndex = getIndexOfOper(line);
			if (curIndex > maxIndex) {
				maxIndex = curIndex;
			}
		}

		return maxIndex;

	}

	// returns true if all opers are having the same length (2 or 1)
	private boolean validateOpersSameLength(List<String> sqlConditions) {
		boolean containsOne = false;
		boolean containsTwo = false;
		for (String line : sqlConditions) {
			for (String oper : Utility.getOneCharOpers()) {
				if (line.contains(oper)) {
					containsOne = true;
					break;
				}
			}
			for (String oper : Utility.getTwoCharOpers()) {
				if (line.contains(oper)) {
					containsTwo = true;
					break;
				}
			}
		}
		if (containsOne == containsTwo) {
			// contains both
			return false;
		}
		// same length
		return true;
	}

}
