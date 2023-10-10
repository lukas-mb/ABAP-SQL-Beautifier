package com.abap.sql.beautifier.statement;

import java.util.List;

public class AbapSqlPart {

	public String name = this.getClass().toString();

	public AbapSqlPart(List<String> lines) {
		setLines(lines);
	}

	protected List<String> lines;

	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

}
