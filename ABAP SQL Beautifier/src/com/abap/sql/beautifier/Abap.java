package com.abap.sql.beautifier;

import java.util.Arrays;
import java.util.List;

public final class Abap {

	// class to store keywords and stuff
	// --> easier to find use with 'where used list'

	public static final List<String> JOINS = Arrays.asList("INNER JOIN", "JOIN", "CROSS JOIN", "OUTER JOIN",
			"FULL OUTER JOIN", "LEFT OUTER JOIN", "RIGHT OUTER JOIN", "LEFT JOIN", "RIGHT JOIN");

	public static final String ORDERBY = "ORDER BY";
	public static final String SELECT = "SELECT";
	public static final String UPTO = "UP TO";
	public static final String FROM = "FROM";
	public static final String SELECTFROM = "SELECT FROM";
	public static final String SELECT_SINGLE_FROM = "SELECT SINGLE FROM";
	public static final String INTO = "INTO";
	public static final String WHERE = "WHERE";
	public static final String GROUPBY = "GROUP BY";
	public static final String HAVING = "HAVING";
	public static final String FIELDS = "FIELDS";
	public static final String CONNECTION = "CONNECTION";
	public static final String FORALLENTRIES = "FOR ALL ENTRIES";
	public static final String APPENDING = "APPENDING";
	public static final String OFFSET = "OFFSET";
	public static final String COMMENT = "COMMENT";
	public static final String SINGLE = "SINGLE";
	public static final String INTO_COR_FI_OF = "INTO CORRESPONDING FIELDS";
}
