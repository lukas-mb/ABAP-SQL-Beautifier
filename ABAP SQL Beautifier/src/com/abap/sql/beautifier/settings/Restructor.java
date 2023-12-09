package com.abap.sql.beautifier.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.abap.sql.beautifier.Abap;
import com.abap.sql.beautifier.statement.AbapSqlPart;
import com.abap.sql.beautifier.utility.Utility;

public class Restructor extends AbstractSqlSetting {

	public Restructor() {
	}

	@Override
	public void apply() {

		splitFrom();
		splitWhere();

	}

	private ArrayList getNextIndexCondWord(String combinedWhere) {
		int firstIndex = -1;
		ArrayList list = new ArrayList();
		String word = "";

		// check if "where" or "and,or"
		if (combinedWhere.toUpperCase().startsWith("WHERE")) {
			word = "WHERE";
			firstIndex = 0;
		} else {
			int andIndex = indexOfKeyword(combinedWhere, "AND");

			int orIndex = indexOfKeyword(combinedWhere, "OR");

			// check if at least one cond word found
			if (orIndex != -1 || andIndex != -1) {

				if (andIndex == -1) {
					andIndex = Integer.MAX_VALUE;
				}

				if (orIndex == -1) {
					orIndex = Integer.MAX_VALUE;
				}

				// get the smaller (first) one and add 1 (indexOf condition is with 1 space)
				if (andIndex < orIndex) {
					firstIndex = andIndex + 1;
					word = "AND";
				} else {
					firstIndex = orIndex + 1;
					word = "OR";
				}
			}

		}

		// check if there is no condition word
		if (firstIndex != -1) {
			
			list.add(word);
			list.add(firstIndex);
		}

		return list;
	}

	private int indexOfKeyword(String statement, String keyword) {
		keyword = " " + keyword + " ";
		int curIndex = 0;
		int lastIndex = 0;
		int keywordIndex = 0;
		boolean isLiteral;
		while (keywordIndex != -1) {
			keywordIndex = statement.indexOf(keyword, curIndex);
			if (keywordIndex != -1) {
				// if keyword found, check if literal
				isLiteral = Utility.isLiteral(statement, keywordIndex, (keywordIndex + " AND ".length()));
				if (isLiteral) {
					curIndex = keywordIndex + 1;
					continue;
				}

				if (lastIndex == keywordIndex) {
					break;
				} else {
					lastIndex = keywordIndex;
				}

			}

		}

		return keywordIndex;
	}

	private void splitFrom() {
		AbapSqlPart fromPart = abapSql.getPart(Abap.FROM);

		if (fromPart != null) {

			List<String> fromLines = fromPart.getLines();

			StringBuilder combinedFrom = new StringBuilder();
			for (String line : fromLines) {
				combinedFrom.append(line).append(" ");
			}

			List<String> joinKeywords = new ArrayList<String>();

			if (abapSql.isOldSyntax()) {
				joinKeywords.add(Abap.FROM);
			} else {
				joinKeywords.add(Abap.SELECTFROM);
			}

			joinKeywords.addAll(1, Abap.JOINS);

			fromLines = abapSql.splitSql(combinedFrom.toString(), joinKeywords);

			List<String> newFromLines = new ArrayList<>();

			String replacement = "";
			for (String join : fromLines) {
				// dummy separator
				replacement = " " + Utility.placeholder + "AND ";
				List<String> splitsAnd = Arrays.asList(join.replace(" AND ", replacement).split(Utility.placeholder));
				for (String subline : splitsAnd) {
					replacement = " " + Utility.placeholder + "ON ";
					List<String> splitsOn = Arrays
							.asList(subline.replace(" ON ", replacement).split(Utility.placeholder));

					for (String subSubLine : splitsOn) {
						newFromLines.add(subSubLine.trim());
					}
				}
			}

			fromPart.setLines(newFromLines);

			abapSql.setPart(Abap.FROM, fromPart);

		}
	}

	private void splitWhere() {
		AbapSqlPart wherePart = abapSql.getPart(Abap.WHERE);

		if (wherePart == null) {
			return;
		}

		List<String> whereLines = wherePart.getLines();

		List<String> conditions = new ArrayList<>();
		String regex = "";
		String curCondition;
		String word;
		int index;
		ArrayList nextCondWord;

		String combinedWhere = "";
		for (String line : whereLines) {
			combinedWhere = combinedWhere + line + " ";
		}

		// logic for the algorithm below: get index of next condition word and cut it
		// off from the long String
		try {
			while (combinedWhere.trim().length() > 0) {
				curCondition = "";

				nextCondWord = getNextIndexCondWord(combinedWhere); // 0 = word, 1 = index

				if (nextCondWord.isEmpty()) {
					break;
				}
				index = (int) nextCondWord.get(1);
				word = nextCondWord.get(0).toString();
				// first cond word
				curCondition = combinedWhere.substring(index, word.length() + index);

				// delete cond word from big string
				regex = curCondition.replace("(", "\\(");
				regex = regex.replace(")", "\\)");

				combinedWhere = combinedWhere.replaceFirst(regex, "");

				// get next cond word
				nextCondWord = getNextIndexCondWord(combinedWhere);

				if (!nextCondWord.isEmpty()) {
					String restOfCond = combinedWhere.substring(0, (int) nextCondWord.get(1));
					curCondition = curCondition + restOfCond;

					conditions.add(curCondition.trim());

					// delete rest of cond from big string
					regex = restOfCond.trim();
//					regex = restOfCond.replace("(", "\\(").trim();
				} else {
					// last condition
					curCondition = curCondition + combinedWhere;

					conditions.add(curCondition.trim());

					// delete from big string
					regex = curCondition;
//					regex = curCondition.replace("(", "\\(");
//					regex = regex.replace(")", "\\)");
				}
				// escape asterisks and other signs
				regex = Utility.escapeRegex(regex);

				combinedWhere = combinedWhere.replaceFirst(regex, "");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<String> newWhereLines = new ArrayList<>();

		// cleaning: delete spaces and combine single statements if there is a 'between'
		String curCond = "";
		for (String condition : conditions) {
			String cleanedCond = Utility.cleanString(condition);
			if (cleanedCond.contains(" BETWEEN ")) {
				curCond = cleanedCond + " ";
				continue;
			}
			cleanedCond = curCond + cleanedCond;
			newWhereLines.add(cleanedCond);
			curCond = "";

		}

		// fusion corresponding TODO

		wherePart.setLines(newWhereLines);

		abapSql.setPart(Abap.WHERE, wherePart);
	}



}
