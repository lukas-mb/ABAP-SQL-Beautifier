package com.abap.sql.beautifier.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abap.sql.beautifier.Activator;
import com.abap.sql.beautifier.preferences.PreferenceConstants;
import com.abap.sql.beautifier.utility.Utility;

public class AbapSqlStatement_BACKUP {

	public static final String FROM = "FROM";

	public static final String INTO = "INTO";

	public static final List<String> JOINS = Arrays.asList("FROM", "INNER JOIN", "JOIN", "CROSS JOIN", "OUTER JOIN",
			"FULL OUTER JOIN", "LEFT OUTER JOIN", "RIGHT OUTER JOIN");
	public static final String ORDERBY = "ORDER BY";
	public static final String SELECT = "SELECT";
	public static final String UPTO = "UP TO";
	public static final String WHERE = "WHERE";
	public static final String GROUPBY = "GROUP BY";
	public static final String HAVING = "HAVING";
	public static final String FIELDS = "FIELDS";

	private String comment = "";

	private List<String> order = new ArrayList<>();

	private Map<String, List<String>> sqlMap = new HashMap<>();

	private boolean isOldSyntax = true;

	public AbapSqlStatement_BACKUP() {
		setPrefOrder();
	}

	public AbapSqlStatement_BACKUP(String sql) {
		setPrefOrder();

		setFromString(sql);
	}

	public AbapSqlStatement_BACKUP(String sql, List<String> newOrder) {

		setFromString(sql);

		setPrefOrder();

		setOrder(newOrder);
	}

	public String convertToString() {
		reformat();

		StringBuilder sql = new StringBuilder();

		for (String keyword : order) {
			if (sqlMap.containsKey(keyword)) {
				List<String> curPart = sqlMap.get(keyword);
				for (String line : curPart) {
					sql.append(line).append("\r\n");
				}

			}
		}

		return sql.toString();
	}

	// without formatting
	public String convertToStringStandard() {
		String sql = "";

		// concatenate empty chars depending on preferences.
		// Those will be before every line except SELECT.
		String emptySpaces = "";
		int emptySpacesInt = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.TAB_ALL);

		for (int i = 0; i < emptySpacesInt; i++) {
			emptySpaces = emptySpaces + " ";
		}

		// build String
		for (String keyword : order) {
			if (sqlMap.containsKey(keyword)) {
				List<String> curPart = sqlMap.get(keyword);
				if (keyword == SELECT) {
					for (String line : curPart) {
						sql = sql + "\r\n" + line;
					}
				} else {
					for (String line : curPart) {
						sql = sql + "\r\n" + emptySpaces + line;
					}
				}

			}
		}

		// delete point
		sql = sql.replace(".", "");

		// delete last "\r\n"
		sql = sql.substring(0, sql.length() - "\r\n".length());

		// add global comment
		String commentReplace = comment + "\r\n";
		return sql.replaceFirst("\r\n", commentReplace);
	}

	private String deleteSpaces(String sql) {
		// delete mulitple spaces
		while (sql.contains("  ") || sql.contains("	 ")) {
			sql = sql.replace("  ", " ");
			sql = sql.replace("	", " ");
		}

		return sql;
	}

	public List<String> getAsList() {
		// returns the current SQL as one big list depending on current order

		ArrayList<String> sql = new ArrayList<>();

		for (String keyword : getOrder()) {
			if (getSqlMap().containsKey(keyword)) {
				List<String> curPart = sqlMap.get(keyword);
				sql.addAll(curPart);
			}
		}

		return sql;

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

	public List<String> getFrom() {
		return sqlMap.get(FROM);
	}

	public List<String> getInto() {
		return sqlMap.get(INTO);
	}

	public List<String> getOrder() {
		return order;
	}

	public List<String> getRest() {
		return sqlMap.get(null);
	}

	public List<String> getSelect() {
		return sqlMap.get(SELECT);
	}

	public List<String> getHaving() {
		return sqlMap.get(HAVING);
	}

	public List<String> getGroupBy() {
		return sqlMap.get(GROUPBY);
	}

	public List<String> getFields() {
		return sqlMap.get(FIELDS);
	}

	public Map<String, List<String>> getSqlMap() {
		return sqlMap;
	}

	public List<String> getSqlPart(String part) {
		return sqlMap.get(part);
	}

	public List<String> getWhere() {
		return sqlMap.get(WHERE);
	}

	public void printSql() {
		System.out.println(toString());
	}

	public void reformat() {
		// empty space for all lines ex. Select
		for (String keyword : sqlMap.keySet()) {
			if (keyword != SELECT) {
				List<String> part = sqlMap.get(keyword);
				List<String> newPart = new ArrayList<>();
				for (String line : part) {
					newPart.add(" " + line);
				}
				sqlMap.replace(keyword, newPart);

			}
		}

	}

	private void reorderSql() {
		Map<String, List<String>> sqlMapNew = new HashMap<>();

		for (String keyword : order) {
			List<String> curSqlPart = sqlMap.get(keyword);
			sqlMapNew.put(keyword, curSqlPart);
		}

		sqlMap = sqlMapNew;
	}

	public void resetFormat() {

		// trim all lines in all parts
		for (String keyword : getOrder()) {
			if (getSqlMap().containsKey(keyword)) {
				List<String> curPart = sqlMap.get(keyword);
				List<String> curPartNew = new ArrayList<>();
				for (String line : curPart) {

					// TODO add logic for saving the comments to the lines.
					// but for now: extract comment, add to global variable
					String sign = "\"";
					if (line.contains(sign)) {
						int pos = line.indexOf(sign);

						if (comment.length() > 1) {
							// additional separator
							comment = comment + " | " + line.substring(pos);
						} else {
							comment = comment + line.substring(pos);
						}

						line = line.substring(0, pos);
					}

					line = deleteSpaces(line).trim();

					curPartNew.add(line.trim());
				}
				setSqlPart(keyword, curPartNew);
			}
		}

	}

	public void setPrefOrder() {
		order = new ArrayList<>();

		String orderString = Activator.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.ORDER_OLD_SYNTAX);

		List<String> orderSplit = Arrays.asList(orderString.split(","));

		for (String keyword : orderSplit) {
			// ignore empty values
			if (keyword.trim() != "") {
				order.add(keyword.trim().toUpperCase());
			}
		}

	}

	public void setFrom(List<String> replacementPart) {
		sqlMap.replace(FROM, replacementPart);
	}

	public void setFromString(String sql) {
		sql = sql.trim();

		if (sql.endsWith(".")) {
			// delete point
			sql = sql.substring(0, sql.length() - 1);
		}

		List<String> parts = splitSql(sql, order);

		for (String keyword : order) {
			for (String split : parts) {
				if (split.trim().startsWith(keyword)) {
					List<String> part = Arrays.asList(split.split("\r\n"));

					// without starting spaces
					List<String> partFinal = new ArrayList<>();
					for (String line : part) {
						partFinal.add(line.trim());
					}

					sqlMap.put(keyword, partFinal);

					break;
				}

			}
		}

	}

	public void setInto(List<String> replacementPart) {
		sqlMap.replace(INTO, replacementPart);
	}

	public void setOrder(List<String> newOrder) {
		if (newOrder.equals(order) || newOrder.isEmpty()) {
			return;
		}

		List<String> finalOrder = new ArrayList<>();

		for (String keyword : newOrder) {
			if (order.contains(keyword)) {
				finalOrder.add(keyword);
			}
		}

		order = finalOrder;

		reorderSql();
	}

	public void setSelect(List<String> replacementPart) {
		sqlMap.replace(SELECT, replacementPart);
	}

	public void setSqlPart(String part, List<String> replacementPart) {
		sqlMap.replace(part, replacementPart);
	}

	public void setWhere(List<String> replacementPart) {
		sqlMap.replace(WHERE, replacementPart);
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
			System.out.println(keyword);

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
//		while (matcher.find()) {
//			int mStart = matcher.start();
//			int mEnd = matcher.end();
//			
//			if (lastIndex < mStart) {
//				splits.add(sql.substring(lastIndex, mStart).trim());
//			}
//			
//			splits.add(sql.substring(mStart, mEnd).trim());
//			
//			lastIndex = mEnd;
//		}

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

	@Override
	public String toString() {
		String sqlString = "";

		for (String keyword : getOrder()) {
			if (getSqlMap().containsKey(keyword)) {
				List<String> curPart = sqlMap.get(keyword);
				for (String line : curPart) {
					sqlString = sqlString + line + "\r\n";
				}
			}
		}

		return sqlString.substring(0, sqlString.length() - "\r\n".length()) + ".";
	}

	public void convertToNewSyntax() {
		List<String> selectPart = this.getSelect();
		String select = "";

		for (String line : selectPart) {
			select = select + line;
		}

		this.sqlMap.put(SELECT, JOINS);
	}

}
