package com.freemarker.lpex.preferences;

import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.freemarker.lpex.Activator;

public class ParserAssociationPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	// The list that displays the current parsers
	private List parserList;
	// The newEntryText is the text where new bad words are specified
	private Text newEntryText;

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {

		Composite entryTable = new Composite(parent, SWT.NULL);

		// Create a data that takes up the extra space in the dialog .
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		entryTable.setLayoutData(data);

		GridLayout layout = new GridLayout();
		entryTable.setLayout(layout);

		// Add in a dummy label for spacing
		new Label(entryTable, SWT.NONE);

		parserList = new List(entryTable, SWT.BORDER);

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		parserList.setItems(parseToArray(store.getString(PreferenceConstants.P_PARSER_MAPPINGS)));

		// Create a data that takes up the extra space in the dialog and spans
		// both columns.
		data = new GridData(GridData.FILL_BOTH);
		parserList.setLayoutData(data);

		Composite buttonComposite = new Composite(entryTable, SWT.NULL);

		GridLayout buttonLayout = new GridLayout();
		buttonLayout.numColumns = 2;
		buttonComposite.setLayout(buttonLayout);

		// Create a data that takes up the extra space in the dialog and spans
		// both columns.
		data = new GridData(GridData.FILL_BOTH
				| GridData.VERTICAL_ALIGN_BEGINNING);
		buttonComposite.setLayoutData(data);

		Button addButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);

		addButton.setText("Add to List");
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				parserList.add(newEntryText.getText(),
						parserList.getItemCount());
			}
		});

		newEntryText = new Text(buttonComposite, SWT.BORDER);
		// Create a data that takes up the extra space in the dialog .
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		newEntryText.setLayoutData(data);

		Button removeButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);

		removeButton.setText("Remove Selection");
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				parserList.remove(parserList.getSelectionIndex());
			}
		});

		data = new GridData();
		data.horizontalSpan = 2;
		removeButton.setLayoutData(data);

		return entryTable;
	}
	

	public static String[] parseToArray(String preferenceValue) {
		StringTokenizer tokenizer =
			new StringTokenizer(preferenceValue, ";");
		int tokenCount = tokenizer.countTokens();
		String[] elements = new String[tokenCount];

		for (int i = 0; i < tokenCount; i++) {
			elements[i] = tokenizer.nextToken();
		}

		return elements;
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		// Initialize the preference store we wish to use
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Setup the parser template folder associations.");
	}

	/**
	 * Performs special processing when this page's Restore Defaults button has
	 * been pressed. Sets the contents of the nameEntry field to be the default
	 */
	protected void performDefaults() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		parserList.setItems(parseToArray(store.getDefaultString(PreferenceConstants.P_PARSER_MAPPINGS)));
	}

	/**
	 * Method declared on IPreferencePage. Save the author name to the
	 * preference store.
	 */
	public boolean performOk() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < parserList.getItems().length; i++) {
			buffer.append(parserList.getItems()[i]);
			buffer.append(";");
		}
		store.setValue(PreferenceConstants.P_PARSER_MAPPINGS, buffer.toString());
		return super.performOk();
	}

}
