package com.abap.sql.beautifier.settings;

import java.util.ArrayList;
import java.util.List;

import com.abap.sql.beautifier.Abap;
import com.abap.sql.beautifier.Activator;
import com.abap.sql.beautifier.preferences.PreferenceConstants;
import com.abap.sql.beautifier.statement.AbapSqlPart;
import com.abap.sql.beautifier.statement.Factory;

public class CommentsAdder extends AbstractSqlSetting {
	private String commentStyle = "";

	public CommentsAdder() {

	}

	@Override
	public void apply() {

		// apply comments
		commentStyle = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.COMMENTSTYLE);

		if (abapSql.getComments().isEmpty()) {
			return;
		}

		if (commentStyle.equals("UP") || commentStyle.equals("DOWN")) {

			setCommentsToOrder();

			addFirstLineWhite();

			addComments();

		}

	}

	private void addFirstLineWhite() {
		AbapSqlPart part = abapSql.getPart(Abap.SELECT);
		String name;
		if (part == null) {
			part = abapSql.getPart(Abap.SELECTFROM);
			name = Abap.SELECTFROM;
		} else {
			name = Abap.SELECT;
		}

		List<String> lines = part.getLines();

		// add start white and replace it
		String firstLine = abapSql.getStartWhite() + lines.get(0);
		lines.set(0, firstLine);
		part.setLines(lines);

		abapSql.setPart(name, part);
	}

	private void addComments() {
		List<String> comments = abapSql.getComments();
		String firstComment = comments.get(0);
		// check if first comment is full line comment
		if (firstComment.startsWith("*")) {
			// add new line
			firstComment = "\r\n" + firstComment;
			comments.set(0, firstComment);
		}
		
		AbapSqlPart commentPart = Factory.getPartObject(Abap.COMMENT, comments);
		
		abapSql.setPart(Abap.COMMENT, commentPart);

	}

	private void setCommentsToOrder() {

		List<String> order = abapSql.getOrder();

		// set comments position
		if (commentStyle.equals("UP")) {
			order.add(0, Abap.COMMENT);
		} else if (commentStyle.equals("DOWN")) {
			order.add(order.size() - 1, Abap.COMMENT);
		}

		abapSql.setOrder(order);
	}

}
