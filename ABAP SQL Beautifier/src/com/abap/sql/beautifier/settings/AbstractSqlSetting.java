package com.abap.sql.beautifier.settings;

import com.abap.sql.beautifier.statement.AbapSql;

public abstract class AbstractSqlSetting {

	protected AbapSql abapSql;

	public AbstractSqlSetting() {
	}


	public abstract void apply();


	public AbapSql getAbapSql() {
		return abapSql;
	}


	public void setAbapSql(AbapSql abapSql) {
		this.abapSql = abapSql;
	}

}
