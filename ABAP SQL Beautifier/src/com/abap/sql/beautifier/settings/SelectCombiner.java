package com.abap.sql.beautifier.settings;

import java.util.List;

import com.abap.sql.beautifier.Abap;
import com.abap.sql.beautifier.Activator;
import com.abap.sql.beautifier.preferences.PreferenceConstants;
import com.abap.sql.beautifier.statement.AbapSqlPart;

public class SelectCombiner extends AbstractSqlSetting {

	public SelectCombiner() {
	}


	@Override
	public void apply() {

		if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.COMBINE_SMALL_SELECT)) {

			int limit = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.COMBINE_CHAR_LIMIT);

			AbapSqlPart selectPart = abapSql.getPart(Abap.SELECT);
			List<String> selectLines = selectPart.getLines();

			// combine with from
			if (selectLines.get(0).trim().length() < limit) {

				AbapSqlPart fromPart = abapSql.getPart(Abap.FROM);
				List<String> fromLines = fromPart.getLines();

				// only if from is with one line
				if (fromLines.size() <= 1) {
					String combinedSelect = selectLines.get(0) + " " + fromLines.get(0);
					selectLines.remove(0);
					selectLines.add(combinedSelect);
					fromLines.remove(0);

					fromPart.setLines(fromLines);
					selectPart.setLines(selectLines);

					abapSql.setPart(Abap.FROM, null);
					abapSql.setPart(Abap.SELECT, selectPart);
				}

			}

			// combine with into
			if (abapSql.containsPart(Abap.FORALLENTRIES)) {
				// if the statement contains FORALLENTRIES --> INTO must be at the end
				return;
			}
			
			selectLines = abapSql.getPart(Abap.SELECT).getLines();

			if (selectLines.get(0).trim().length() < limit) {
				AbapSqlPart intoPart = abapSql.getPart(Abap.INTO);
				List<String> intoLines = intoPart.getLines();

				// only if into is with one line
				if (intoLines.size() <= 1) {
					String combinedSelect = selectLines.get(0) + " " + intoLines.get(0);
					selectLines.remove(0);
					selectLines.add(combinedSelect);
					intoLines.remove(0);

					intoPart.setLines(intoLines);
					selectPart.setLines(selectLines);
					abapSql.setPart(Abap.INTO, null);
					abapSql.setPart(Abap.SELECT, selectPart);
				}

			}
		}
	}

}
