package com.abap.sql.beautifier.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.abap.sql.beautifier.Abap;
import com.abap.sql.beautifier.Activator;
import com.abap.sql.beautifier.preferences.PreferenceConstants;
import com.abap.sql.beautifier.statement.AbapSqlPart;
import com.abap.sql.beautifier.statement.Factory;
import com.abap.sql.beautifier.utility.Utility;

public class CommasAdder extends AbstractSqlSetting {

	public CommasAdder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void apply() {

		if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.COMMAS)) {

			String select = Utility.cleanString(abapSql.getPart(Abap.SELECT).getLines().toString());

			// ignore special cases (e.g. string functions)
			boolean shouldApply = !(select.contains("*") || select.contains("(") || select.contains(")"));

			if (shouldApply) {
				List<String> splits = Arrays.asList(select.split(" "));

				List<String> newSelectList = new ArrayList<>();
				String newSelect = "";

				for (String element : splits) {
					if (element.equalsIgnoreCase("SELECT")) {
						newSelect = element;
					} else if (element.contains(",") || newSelect.endsWith("SELECT")) {
						newSelect = newSelect + " " + element;
					} else {

						newSelect = newSelect + ", " + element;
					}
				}

				newSelectList.add(newSelect);
				
				AbapSqlPart selectPart = Factory.getPartObject(Abap.SELECT, newSelectList);

				abapSql.setPart(Abap.SELECT, selectPart);
			}

		}

	}

}
