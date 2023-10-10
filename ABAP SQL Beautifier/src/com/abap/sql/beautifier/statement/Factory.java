package com.abap.sql.beautifier.statement;

import java.util.List;

import com.abap.sql.beautifier.Abap;
import com.abap.sql.beautifier.statement.parts.Comment;
import com.abap.sql.beautifier.statement.parts.Connection;
import com.abap.sql.beautifier.statement.parts.Fields;
import com.abap.sql.beautifier.statement.parts.From;
import com.abap.sql.beautifier.statement.parts.GroupBy;
import com.abap.sql.beautifier.statement.parts.Having;
import com.abap.sql.beautifier.statement.parts.Into;
import com.abap.sql.beautifier.statement.parts.OrderBy;
import com.abap.sql.beautifier.statement.parts.Select;
import com.abap.sql.beautifier.statement.parts.UpTo;
import com.abap.sql.beautifier.statement.parts.Where;

public final class Factory {

	public static AbapSqlPart getPartObject(String name, List<String> lines) {
		AbapSqlPart returnPart = null;
		switch (name) {
		case Abap.SELECT:
			returnPart = new Select(lines);
		case Abap.FROM:
			returnPart = new From(lines);
		case Abap.SELECTFROM:
			returnPart = new From(lines);
		case Abap.ORDERBY:
			returnPart = new OrderBy(lines);
		case Abap.UPTO:
			returnPart = new UpTo(lines);
		case Abap.FORALLENTRIES:
			returnPart = new UpTo(lines);
		case Abap.WHERE:
			returnPart = new Where(lines);
		case Abap.GROUPBY:
			returnPart = new GroupBy(lines);
		case Abap.INTO:
			returnPart = new Into(lines);
		case Abap.HAVING:
			returnPart = new Having(lines);
		case Abap.FIELDS:
			returnPart = new Fields(lines);
		case Abap.CONNECTION:
			returnPart = new Connection(lines);
		case Abap.COMMENT:
			returnPart = new Comment(lines);
		default:
			returnPart = new AbapSqlPart(lines);
		}
		return returnPart;
	}

}
