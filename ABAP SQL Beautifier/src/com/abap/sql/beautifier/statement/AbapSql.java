package com.abap.sql.beautifier.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abap.sql.beautifier.Abap;
import com.abap.sql.beautifier.Activator;
import com.abap.sql.beautifier.preferences.PreferenceConstants;
import com.abap.sql.beautifier.statement.parts.Into;
import com.abap.sql.beautifier.utility.Utility;

public class AbapSql {

	private Map<String, AbapSqlPart> parts;
	private List<String> order;
	private boolean isOldSyntax = true;
	private List<String> comments = new ArrayList<String>();
	private String startWhite = "";

	public AbapSql(String sql, int diff) {
		startWhite = Utility.getWhiteChars(diff);

		parts = new HashMap<String, AbapSqlPart>();

		// get order
		isOldSyntax = checkIfOldSyntax(sql);
		if (isOldSyntax) {
			setOrder(buildOrder(PreferenceConstants.ORDER_OLD_SYNTAX));
		} else {
			setOrder(buildOrder(PreferenceConstants.ORDER_NEW_SYNTAX));
		}

		appendSecWords();

		sql = filterComments(sql);

		sql = Utility.cleanString(sql);

		setFromString(sql);

		resetFormat();

	}

	private void appendSecWords() {
		// append keywords, which are introducing the same part
		int index;

		// INTO ~ APPENDING
		index = order.indexOf(Abap.INTO);
		if (index != -1) {
			order.add(index, Abap.APPENDING);
		}
		
		// UPTO ~ OFFSET
		index = order.indexOf(Abap.UPTO);
		if (index != -1) {
			order.add(index, Abap.OFFSET);
		}

	}

	private String filterComments(String sql) {
		String returnSql = "";
		String asterisks = "*";
		String apostrophes = "\"";

		if (sql.contains(asterisks) || sql.contains(apostrophes)) {
			List<String> lines = Arrays.asList(sql.split("\r\n"));

			for (String line : lines) {
				if (line.contains(asterisks) || line.contains(apostrophes)) {
					int pos = line.indexOf(asterisks);

					// check if asterisks is first char --> full line comment
					if (pos == 0) {
						comments.add(line.trim());
						continue;
					}

					// asterisks was not a comment --> check apostrophes
					pos = line.indexOf(apostrophes);

					if (pos == -1) {
						returnSql = returnSql + line + "\r\n";
					} else {
						comments.add(line.substring(pos).trim());
						returnSql = returnSql + line.substring(0, pos) + "\r\n";
					}

				} else {
					returnSql = returnSql + line + "\r\n";
				}

			}

			return returnSql;

		} else {
			// does not contain any comments, return original
			return sql;
		}

	}

	private List<String> buildOrder(String syntaxType) {
		List<String> returnOrder = new ArrayList<>();

		String curOrderString = Activator.getDefault().getPreferenceStore().getString(syntaxType);

		List<String> orderSplit = Arrays.asList(curOrderString.split(","));

		for (String keyword : orderSplit) {

			keyword = keyword.trim();
			
			//add additional select single
			if(this.isOldSyntax == false && keyword.equals(Abap.SELECTFROM)) {
				returnOrder.add(Abap.SELECT_SINGLE_FROM);
			}

			// ignore empty values
			if (keyword != "") {
				returnOrder.add(keyword.toUpperCase());
			}
		}

		return returnOrder;
	}

	public List<String> getOrder() {
		return order;
	}

	public void setOrder(List<String> order) {
		this.order = order;
	}

	public AbapSqlPart getPart(String partname) {

		AbapSqlPart part = parts.get(partname);

		if (partname == Abap.FROM && part == null) {
			part = parts.get(Abap.SELECTFROM);
		} else if (partname == Abap.INTO && part == null) {
			part = parts.get(Abap.APPENDING);
		}

		return part;

	}

	public void setPart(String partname, AbapSqlPart part) {

		parts.put(partname, part);

	}

	public boolean isOldSyntax() {
//		String firstLine = getPart(Abap.SELECT).getLines().get(0);
//		
//	
//		List<String> tokens = Arrays.asList(firstLine.trim().split(" "));
//
//		if (tokens.size() >= 2) {
//			if (tokens.get(0).equals(Abap.FROM) && tokens.get(1).equals(Abap.FROM)) {
//				return false;
//			}
//		}
		AbapSqlPart part = getPart(Abap.FROM);

		if (part == null) {
			part = getPart(Abap.SELECTFROM);
		}
		String firstLine = "";
		List<String> tokens;

		try {
			firstLine = part.getLines().get(0);
		} catch (IndexOutOfBoundsException | NullPointerException ex) {
			// try other method to check syntax
			part = getPart(Abap.SELECT);
			if (part == null) {
				part = getPart(Abap.SELECTFROM);
				if (part == null) {
					part = getPart(Abap.SELECT_SINGLE_FROM);
				}
			}
			firstLine = part.getLines().get(0).trim();

			tokens = Arrays.asList(firstLine.trim().split(" "));

			String firstToken = tokens.get(0);
			String secToken = tokens.get(1);
			if (firstToken.equals(Abap.SELECT) && secToken.equals(Abap.FROM)) {
				return false;
			} else {
				return true;
			}

		}

		tokens = Arrays.asList(firstLine.trim().split(" "));

		if (tokens.get(0).equals(Abap.SELECT)) {
			return false;
		} else {
			return true;
		}

	}

	public List<String> splitSql(String sql, List<String> keywords) {
		List<String> splits = new ArrayList<>();
		List<String> result = new ArrayList<>();

		String pattern = "\\b(" + String.join("|", keywords) + ")\\b";

		Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = regex.matcher(sql);

		int lastIndex = 0;

		String curString = "";

		while (matcher.find()) {
			int mStart = matcher.start();

			int mEnd = matcher.end();

			String keyword = sql.substring(mStart, mEnd).trim();

			// verify no literal
			boolean isLiteral = Utility.isLiteral(sql, mStart, mEnd);

			if (isLiteral) {
				// save cur statement
				curString = curString + sql.substring(lastIndex, mEnd);
				lastIndex = mEnd;
				continue;
			}

			if (!curString.matches("")) {
				// cur statement not empty, add to list
				curString = curString + sql.substring(lastIndex, mStart);
				splits.add(curString.trim());
				curString = "";
			}

			if (lastIndex < mStart) {
				splits.add(sql.substring(lastIndex, mStart).trim());
			}

			splits.add(sql.substring(mStart, mEnd).trim());

			lastIndex = mEnd;
		}

		// add remaining text after matching last keyword
		if (lastIndex < sql.length()) {
			splits.add(sql.substring(lastIndex).trim());
		}

		// combine keyword + rest
		String curLine = "";
		for (int i = 0; i < splits.size(); i++) {
			if (i % 2 == 0) {
				curLine = splits.get(i);

			} else {
				curLine += " " + splits.get(i);
				result.add(curLine);
			}
		}

		// sorting
		result.sort(Comparator.comparingInt(s -> getFirstMatchKeyIndex(s, keywords)));

		return result;
	}

	private int getFirstMatchKeyIndex(String string, List<String> keywords) {
		for (int i = 0; i < keywords.size(); i++) {
			String keyword = keywords.get(i);
			if (string.contains(keyword)) {
				return i;
			}
		}
		return keywords.size();
	}

	public void resetFormat() {
//		String sign = "\"";

		// clean all lines in all parts = reset format
		for (String keyword : getOrder()) {

			AbapSqlPart curPart = parts.get(keyword);

			if (parts.get(keyword) != null) {

				List<String> curLines = curPart.getLines();

				List<String> newLines = new ArrayList<String>();

				for (String line : curLines) {

//					if (line.contains(sign)) {
//						int pos = line.indexOf(sign);
//
//						comments.add(line.substring(pos));
//
//						line = line.substring(0, pos);
//					}

					line = Utility.deleteSpaces(line).trim();

					newLines.add(line);
				}

				setPart(keyword, curPart);
			}
		}

	}

	public void setFromString(String sql) {
		sql = Utility.cleanString(sql);

		if (sql.endsWith(".")) {
			// delete point
			sql = sql.substring(0, sql.length() - 1);
		}

		List<String> parts = splitSql(sql, getOrder());

		for (String keyword : getOrder()) {
			for (String split : parts) {
				if (split.trim().startsWith(keyword)) {
					List<String> part = Arrays.asList(split.split("\r\n"));

					// without starting spaces
					List<String> finalPartList = new ArrayList<>();
					for (String line : part) {
						finalPartList.add(line.trim());
					}

					AbapSqlPart finalPart = Factory.getPartObject(keyword, finalPartList);

					setPart(keyword, finalPart);

					break;
				}

			}
		}

		verifyOrder();

	}

	public static boolean checkIfOldSyntax(String sql) {
		sql = Utility.cleanString(sql);
		List<String> splits = Arrays.asList(sql.split(" "));
		if (splits.size() > 2) {

			String secToken = splits.get(1).toString().toUpperCase();
			String thirdToken = splits.get(2).toString().toUpperCase();
			
			if (secToken.equals(Abap.FROM) || (secToken.equals(Abap.SINGLE) && thirdToken.equals(Abap.FROM))) {
				// new syntax
				return false;
			}


		}
		// oldSyntax
		return true;

	}

	@Override
	public String toString() {
		String sqlString = "";

		int limit = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.LINE_CHAR_LIMIT);

		if (limit == 0) {
			// forbidden
			limit = 1;
		}

		if (!isOldSyntax && this.getPart(Abap.FIELDS) == null) {
			convertToNewSyntax();
		}

		for (String keyword : getOrder()) {
			AbapSqlPart curPart = parts.get(keyword);
			if (curPart != null) {
				List<String> curLines = curPart.getLines();
				for (String line : curLines) {

					if (line.length() < limit || keyword.equals(Abap.COMMENT)) {
						// no split on comments
						sqlString = sqlString + line + "\r\n";
					} else {
						// line limit reached
						List<String> splittedLines = Utility.splitLine(line, limit);
						for (String splittedLine : splittedLines) {
							sqlString = sqlString + splittedLine + "\r\n";
						}
					}

				}
			}
		}
		
		// build return String
		sqlString = sqlString.substring(0, sqlString.length() - "\r\n".length());
		
		return sqlString;
	}

	public void setPoint() {
		List<String> reverseOrder = new ArrayList<String>(getOrder());
		Collections.reverse(reverseOrder);

		for (String keyword : reverseOrder) {
			if (keyword.equals(Abap.COMMENT)) {
				continue;
			}
			if (containsPart(keyword)) {
				// last part found. set point to the end
				AbapSqlPart lastPart = getPart(keyword);
				List<String> lines = lastPart.getLines();
				int pos = lines.size() - 1;
				String lastLine = lines.get(pos) + ".";
				lines.set(pos, lastLine);

//				lastPart.setLines(lines);
//				setPart(keyword, lastPart);
				return;
			}

		}

	}

	public AbapSql convertToNewSyntax() {
		if (!isOldSyntax && this.getPart(Abap.FIELDS) == null) {

			// transform the from part to 'select' and fields to 'fields'
			AbapSqlPart selectPart = getPart(Abap.SELECT);
			AbapSqlPart fromPart = getPart(Abap.FROM);

			List<String> selectLines = selectPart.getLines();
			List<String> fromLines = fromPart.getLines();

			List<String> newFromLines = new ArrayList<String>();
			List<String> fieldLines = new ArrayList<String>();

			// reset from
			setPart(Abap.FROM, null);

			// build fields
			for (String line : selectLines) {
				if (line.trim().startsWith(Abap.SELECT)) {
					line = line.replaceFirst(Abap.SELECT, "");
				}
				fieldLines.add(line);
			}

			AbapSqlPart fieldsPart = Factory.getPartObject(Abap.FIELDS, fieldLines);
			setPart(Abap.FIELDS, fieldsPart);

			// new select

			for (int i = 0; i < fromLines.size(); i++) {
				String curLine = fromLines.get(i);
				if (i == 0) {
					curLine = Abap.SELECT + curLine;
				}
				newFromLines.add(curLine);
			}

			AbapSqlPart newSelectPart = Factory.getPartObject(Abap.SELECT, newFromLines);
			setPart(Abap.SELECT, newSelectPart);

		}

		return this;

	}

	public void verifyOrder() {
		// deactivated --> not necessary?

//		// check if statement contains FOR ALL ENTRIES and if INTO is at the end
//		if (containsPart(Abap.FORALLENTRIES)) {
//			List<String> curOrder = getOrder();
//
//			if (!curOrder.get(curOrder.size() - 1).equals(Abap.INTO)) {
//				for (int i = 0; i < curOrder.size(); i++) {
//					String keyword = curOrder.get(i);
//
//					if (keyword.equals(Abap.INTO)) {
//						// remove from current position and add it to the end
//						curOrder.remove(i);
//						curOrder.add(keyword);
//						return;
//					}
//
//				}
//
//			}
//
//		}
	}

	public boolean containsPart(String partname) {
		AbapSqlPart part = getPart(partname);

		if (part == null) {
			return false;
		} else {
			return true;
		}
	}

	public String getStartWhite() {
		return startWhite;
	}

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
	}

}
