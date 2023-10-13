package com.abap.sql.beautifier.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utility {

	public final static String placeholder = "@!%&";

	public static int countKeyword(String statement, String keyword) {

		keyword = keyword.toUpperCase();

		int count = 0;

		List<String> cleanTokenList = convertToCleanTokenList(statement);

		for (int i = 0; i < cleanTokenList.size(); i++) {
			String token = cleanTokenList.get(i).trim();
			if (token.equalsIgnoreCase(keyword)) {

				if (i == 0) {
					count++;
					continue;
				}

				if (i < cleanTokenList.size()) {
					String tokenBefore = cleanTokenList.get(i - 1);
					String tokenAfter = cleanTokenList.get(i + 1);

					if (!tokenBefore.trim().matches("'") && !tokenAfter.trim().matches("'")) {
						count++;
					}

				}

			}
		}

		return count;
	}

	public static boolean isLiteral(String statement, int start, int end) {
		String statementStart;

		if (start == 0) {
			statementStart = statement.substring(0, start);
		} else {
			statementStart = statement.substring(0, start - 1);
		}

		String statementMid = statement.substring(statementStart.length(), end + 1) + placeholder + " ";
		String statementEnd = statement.substring(end + 1, statement.length());

		statement = statementStart + statementMid + statementEnd;

		List<String> cleanTokenList = convertToCleanTokenList(statement);

		boolean isLiteral = false;
		int count = 0;

		for (int i = 1; i < cleanTokenList.size(); i++) {

			String curToken = cleanTokenList.get(i);

			// count all " ' "
			if (curToken.matches("'")) {
				count++;
			}

			if (curToken.contains(placeholder)) {

				curToken = curToken.replaceAll(placeholder, "");

				String tokenBefore = cleanTokenList.get(i - 1).trim();
				String tokenAfter = cleanTokenList.get(i + 1).trim();

				boolean startLiteral = (tokenBefore.matches("'") || curToken.startsWith("'"));
				boolean endsLiteral = (tokenAfter.matches("'") || curToken.endsWith("'"));

				if (startLiteral && endsLiteral) {
					if (count % 2 != 0) {
						isLiteral = true;
					}

				}

				break;

			}
		}

		return isLiteral;
	}

	private static List<String> convertToCleanTokenList(String statement) {
		statement = statement.toUpperCase();

		List<String> tokenList = Arrays.asList(statement.trim().split(" "));

		List<String> cleanTokenList = new ArrayList<String>();

		// clean tokenList
		for (String token : tokenList) {
			token = token.replaceAll("\r\n", " ");
			if (!token.isBlank()) {
				cleanTokenList.add(token);
			}
		}

		return cleanTokenList;

	}

	public static String deleteSpaces(String statement) {
		// delete mulitple spaces
		while (statement.contains("  ") || statement.contains("	 ")) {
			statement = statement.replace("  ", " ");
			statement = statement.replace("	", " ");
		}

		return statement;
	}

	public static String deleteLines(String statement) {
		// return statement.replace("\r\n", " ");
		return statement.replace(System.lineSeparator(), " ");
	}

	public static String cleanString(String statement) {
		String cleanedString = deleteLines(statement);
		cleanedString = deleteSpaces(cleanedString);
		return cleanedString;
	}

	public static List<String> getAllOperators() {
		List<String> opers = new ArrayList<>();

		opers.addAll(getTwoCharOpers());

		opers.addAll(getOneCharOpers());

		return opers;
	}

	public static List<String> getOneCharOpers() {
		List<String> opers = new ArrayList<>();

		opers.add(" = ");
		opers.add(" < ");
		opers.add(" > ");

		return opers;

	}

	public static List<String> getTwoCharOpers() {
		List<String> opers = new ArrayList<>();

		opers.add(" EQ ");
		opers.add(" NE ");
		opers.add(" LT ");
		opers.add(" GT ");
		opers.add(" LE ");
		opers.add(" GE ");

		opers.add(" <> ");
		opers.add(" <= ");
		opers.add(" >= ");

		opers.add(" IN ");

		return opers;
	}

	public static List<String> splitLine(String statement, int limit) {
		List<String> lines = new ArrayList<String>();

		int lengthBefore = statement.length();
		statement = statement.strip();
		String whiteChars = Utility.getWhiteChars(lengthBefore - statement.length());
		String whiteCharsTabLines = whiteChars + "  ";

		if (statement.length() > limit) {

			int count = 0;
			int subStrLength = limit;
			while (statement != null) {

				if (statement.length() < subStrLength) {
					subStrLength = statement.length();
				}

				String curLine = statement.substring(0, subStrLength);

				String regex = escapeRegex(statement.trim());

				if (!curLine.matches(regex)) {

					// get index of last space in this line
					int curIndex = curLine.lastIndexOf(" ");

					if (curIndex != -1) {
						// index found
						curLine = curLine.substring(0, curIndex);
					} else {
						// no space found --> find next space
						for (int i = (subStrLength + 1); i < statement.length(); i++) {
							curLine = statement.substring(0, i);
							curIndex = curLine.lastIndexOf(" ");
							if (curIndex != -1) {
								break;
							}
						}

						// still not found? take whole statement
						if (curIndex == -1) {
							curLine = statement;
						}
					}
				}

				if (count == 0) {
					curLine = whiteChars + curLine;
				} else {
					curLine = whiteCharsTabLines + curLine;
				}

				lines.add(curLine);

				regex = escapeRegex(curLine.trim());

				statement = statement.replaceFirst(regex, "").trim();

				if (statement.isBlank()) {
					statement = null;
					break;
				}

				count++;
			}

		} else {
			statement = whiteChars + statement;
			lines.add(statement);
		}

		return lines;

	}

	public static String getWhiteChars(int amount) {
		String white = "";

		for (int i = 0; i < amount; i++) {
			white = white + " ";
		}

		return white;
	}

	public static String escapeRegex(String regex) {
		regex = regex.replace("*", "\\*");
		regex = regex.replace("(", "\\(");
		regex = regex.replace(")", "\\)");

		return regex;
	}

}
