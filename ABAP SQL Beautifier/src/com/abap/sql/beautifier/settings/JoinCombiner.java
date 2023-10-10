package com.abap.sql.beautifier.settings;

import java.util.ArrayList;
import java.util.List;

import com.abap.sql.beautifier.Abap;
import com.abap.sql.beautifier.Activator;
import com.abap.sql.beautifier.preferences.PreferenceConstants;
import com.abap.sql.beautifier.statement.AbapSqlPart;

public class JoinCombiner extends AbstractSqlSetting {

	public JoinCombiner() {

	}


	@Override
	public void apply() {
		if (abapSql.isOldSyntax()) {
			// TODO enable also for new syntax
			
			if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.COMBINE_SMALL_JOINS)) {

				int limit = Activator.getDefault().getPreferenceStore()
						.getInt(PreferenceConstants.COMBINE_SMALL_JOINS_LIMIT);

				AbapSqlPart fromPart = abapSql.getPart(Abap.FROM);

				List<String> fromLines = fromPart.getLines();

				String fromLine = "";

				for (String line : fromLines) {
					if(fromLine.trim().equals("")) {
						fromLine = fromLine + line;
					} else {
						fromLine = fromLine + " " + line;
					}
					
					if (fromLine.length() > limit) {
						// too big, not possible
						return;
					}
				}
				fromLines = new ArrayList<String>();
				fromLines.add(fromLine);
				fromPart.setLines(fromLines);
				abapSql.setPart(Abap.FROM, fromPart);
			}

		}
	}

}
