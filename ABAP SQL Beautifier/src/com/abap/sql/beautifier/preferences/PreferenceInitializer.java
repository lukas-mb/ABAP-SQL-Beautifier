package com.abap.sql.beautifier.preferences;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.abap.sql.beautifier.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	private static final List<String> ORDER_OLD = Arrays.asList("SELECT", "UP TO", "INTO", "FROM", "FOR ALL ENTRIES",
			"CONNECTION", "WHERE", "HAVING", "GROUP BY", "ORDER BY");
	private static final List<String> ORDER_NEW = Arrays.asList("SELECT FROM", "FIELDS", "UP TO", "FOR ALL ENTRIES",
			"CONNECTION", "WHERE", "HAVING", "GROUP BY", "ORDER BY", "INTO");
	private String order = "";

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		store.setDefault(PreferenceConstants.LINE_CHAR_LIMIT, 75);
		store.setDefault(PreferenceConstants.COMMENTSTYLE, "UP");
		store.setDefault(PreferenceConstants.COMBINE_SMALL_JOINS, false);
		store.setDefault(PreferenceConstants.COMBINE_SMALL_JOINS_LIMIT, 20);
		store.setDefault(PreferenceConstants.COMBINE_SMALL_SELECT, false);
		store.setDefault(PreferenceConstants.COMBINE_CHAR_LIMIT, 20);
		store.setDefault(PreferenceConstants.TAB_ALL, 0);
		store.setDefault(PreferenceConstants.TAB_ALL_NEW_SYNTAX, 0);
		store.setDefault(PreferenceConstants.TAB_CONDITIONS, false);
		store.setDefault(PreferenceConstants.TAB_CONDITIONS_NEW_SYNTAX, false);
		store.setDefault(PreferenceConstants.ALLIGN_OPERS, true);
		store.setDefault(PreferenceConstants.OPERSTYLE, "sign");

		// concatenate order as String
		for (String keyword : ORDER_OLD) {
			order = order + keyword + ", ";
		}

		// replace last comma
		if (order.endsWith(", ")) {
			order = order.substring(0, order.length() - 2);
		}

		store.setDefault(PreferenceConstants.ORDER_OLD_SYNTAX, order);

		order = "";
		// concatenate order as String
		for (String keyword : ORDER_NEW) {
			order = order + keyword + ", ";
		}

		// replace last comma
		if (order.endsWith(", ")) {
			order = order.substring(0, order.length() - 2);
		}

		store.setDefault(PreferenceConstants.ORDER_NEW_SYNTAX, order);

		store.setDefault(PreferenceConstants.ESCAPING, false);
		store.setDefault(PreferenceConstants.COMMAS, false);

	}

}
