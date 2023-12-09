package com.abap.sql.beautifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.PlatformUI;

import com.abap.sql.beautifier.preferences.PreferenceConstants;
import com.abap.sql.beautifier.settings.AbstractSqlSetting;
import com.abap.sql.beautifier.settings.CommentsAdder;
import com.abap.sql.beautifier.settings.ConditionAligner;
import com.abap.sql.beautifier.settings.JoinCombiner;
import com.abap.sql.beautifier.settings.OperatorUnifier;
import com.abap.sql.beautifier.settings.Restructor;
import com.abap.sql.beautifier.settings.SelectCombiner;
import com.abap.sql.beautifier.settings.SpaceAdder;
import com.abap.sql.beautifier.statement.AbapSql;
import com.abap.sql.beautifier.utility.BeautifierIcon;
import com.abap.sql.beautifier.utility.Utility;
import com.sap.adt.tools.abapsource.ui.AbapSourceUi;
import com.sap.adt.tools.abapsource.ui.IAbapSourceUi;
import com.sap.adt.tools.abapsource.ui.sources.IAbapSourceScannerServices;
import com.sap.adt.tools.abapsource.ui.sources.IAbapSourceScannerServices.Token;
import com.sap.adt.tools.abapsource.ui.sources.editors.AbapSourcePage;

public class StatementProcessor implements IQuickAssistProcessor {

	public IDocument document = null;
	public IAbapSourceScannerServices scannerServices = null;
	public AbapSourcePage sourcePage;
	public IAbapSourceUi sourceUi = null;
	private String sql = "";
	private String code;
	private int diff;
	private int end;
	private int offsetCursor;
	private int startReplacement;
	private boolean oldSyntax = true;

	private String beautifyStatement(String inputCode) {
		// otherwise the whole beautifier would not work
		inputCode = inputCode.toUpperCase();

		AbapSql abapSql = new AbapSql(inputCode, diff);

		System.out.println("==================================");
		System.out.println("Input");
		System.out.println(inputCode);

		List<AbstractSqlSetting> settings = generateSettings();

		// apply settings
		for (AbstractSqlSetting setting : settings) {
			System.out.println("==================================");
			System.out.println(setting.getClass().getSimpleName());

			setting.setAbapSql(abapSql);
			setting.apply();
			abapSql = setting.getAbapSql();

			System.out.println(abapSql.toString());

		}

		abapSql.setPoint();

		String outputCode = abapSql.toString();

		// pretty print depending on SAP user settings
		if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.PRETTY_PRINT)) {
			outputCode = PrettyPrinterConnector.prettyPrint(outputCode);
		}

		// delete last empty row
		if (outputCode.endsWith("\r\n")) {
			outputCode = outputCode.substring(0, outputCode.length() - "\r\n".length());
		}

		System.out.println("==================================");
		System.out.println("Output");
		System.out.println(outputCode);

		return outputCode;
	}

	@Override
	public boolean canAssist(IQuickAssistInvocationContext invocationContext) {
		// get part of code and check if the current code part is a sql statement

		document = invocationContext.getSourceViewer().getDocument();
		sourceUi = AbapSourceUi.getInstance();
		scannerServices = sourceUi.getSourceScannerServices();
		sourcePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
				.getAdapter(AbapSourcePage.class);
		code = document.get();

		// offset of current cursor
		offsetCursor = invocationContext.getOffset();

		// get offset of last and next dot
		int start = scannerServices.goBackToDot(document, offsetCursor) + 1;
		end = scannerServices.goForwardToDot(document, offsetCursor);

		// all words in selected code
		List<Token> statementTokens = scannerServices.getStatementTokens(document, start);

		// check first word
		if (statementTokens.size() > 0) {
			String firstToken = null;
			
			//get first non comment token
			for (int i = 0; i < statementTokens.size(); i++) {
				Token t = statementTokens.get(i);
				if (!scannerServices.isComment(document, t.offset)) {
					firstToken = t.toString();
					
					// offset of last dot and startReplacement could be different
					startReplacement = t.offset;

					try {
						int line = document.getLineOfOffset(startReplacement);
						int lineOffset = document.getLineOffset(line);

						diff = startReplacement - lineOffset;

					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					break;
				}
			}
			if (firstToken != null) {

				if (firstToken.equalsIgnoreCase(Abap.SELECT)) {
					sql = code.substring(startReplacement, end);

					String sqlHelper = sql.replaceAll(",", "");
					sqlHelper = Utility.cleanString(sqlHelper);
					List<String> customTokens = Arrays.asList(sqlHelper.split(" "));

					if (customTokens.size() > 2) {
						// check if old or new syntax
						String secToken = customTokens.get(1).toString().toUpperCase();
						String thirdToken = customTokens.get(2).toString().toUpperCase();
						if (secToken.equals(Abap.FROM)
								|| (secToken.equals(Abap.SINGLE) && thirdToken.equals(Abap.FROM))) {
							this.oldSyntax = false;
						}
					}

					// TODO
					// check if second SELECT or WHEN in statement --> not working currently in this
					// plugin

					// check if it contains multiple 'select' or 'when'

					// when?
					if (sql.toUpperCase().contains(" WHEN ")) {
						return false;
					}

					// mult. select?
					int count = Utility.countKeyword(sql, Abap.SELECT);

					if (count <= 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean canFix(Annotation annotation) {
		return true;
	}

	@Override
	public ICompletionProposal[] computeQuickAssistProposals(IQuickAssistInvocationContext invocationContext) {
		List<ICompletionProposal> proposals = new ArrayList<>();

		if (canAssist(invocationContext)) {

			int replaceLength = end - startReplacement + 1;

			String beautifulSql = "";
			String convertedSql = "";

			try {
				beautifulSql = beautifyStatement(sql);

				if (this.oldSyntax) {
					// convertedSql = convertToNewSyntax(beautifulSql);
				}

			} catch (Exception e) {

				// to avoid 'disturbing' other plugins with other proposals if there is a bug
				e.printStackTrace();
				return null;
			}

			String descBeautify = "Beautify this SQL statement depending on the settings in your preferences.";
			CompletionProposal beautifyProp = new CompletionProposal(beautifulSql, startReplacement, replaceLength, 0,
					BeautifierIcon.get(), "Beautify SQL-Statement", null, descBeautify);

			proposals.add(beautifyProp);

//			if (this.oldSyntax) {
//				CompletionProposal conversionProp = new CompletionProposal(convertedSql, startReplacement,
//						replaceLength, 0, BeautifierIcon.get(), "Convert SQL-Statement", null,
//						"Convert this SQL statement to the new syntax depending on the settings in your preferences.");
//
//				proposals.add(conversionProp);
//			}

			return proposals.toArray(new ICompletionProposal[proposals.size()]);

		}

		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	private List<AbstractSqlSetting> generateSettings() {
		List<AbstractSqlSetting> settings = new ArrayList<>();

		settings.add(new Restructor());
		settings.add(new OperatorUnifier());

		if (this.oldSyntax) {
//			settings.add(new CommasAdder());
			settings.add(new JoinCombiner());
			settings.add(new SelectCombiner());
//			settings.add(new EscapingAdder());
		}

		settings.add(new SpaceAdder());
		settings.add(new ConditionAligner());
		settings.add(new CommentsAdder());

		return settings;

	}

}
