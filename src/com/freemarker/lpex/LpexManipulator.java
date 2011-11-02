package com.freemarker.lpex;

import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.freemarker.lpex.preferences.ParserAssociationPreferencePage;
import com.freemarker.lpex.preferences.PreferenceConstants;
import com.freemarker.lpex.utils.PluginLogger;
import com.freemarker.lpex.utils.StackTraceUtil;
import com.ibm.lpex.alef.preferences.ParserAssociationsPreferencePage;
import com.ibm.lpex.core.LpexView;

public class LPEXManipulator {
	
	private LpexView view = null;
	private String selectedTemplateName = "";
	private String templateHintWord = "";
	private int x = 0;
	private int y = 0;

	public LPEXManipulator(LpexView view) {
		this.setView(view);
	}
	
	public void addBlockTextAtCursorPosition(String block) {
		if (templateHintWord != "") {
			view.doAction(view.actionId("deletePrevWord"));
		}
		view.doCommand("add");
		int prevIndex = 0;
		for (int i = block.indexOf('\n'); i >= 0; i = block.indexOf('\n', i + 1)) {
			String line = block.substring(prevIndex, i);
			line = line.replaceAll("[\\r\\n]", "");
			view.doCommand("insert " + line);
			prevIndex = i;
		}
	}

	public void promptTemplateChooser(String[] entries) {

		if ( entries.length == 0 ) {
			selectedTemplateName = "";
			return;
		} else if ( entries.length == 1 ) {
			selectedTemplateName = entries[0];
			return;
		}

		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		//Color yellow = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		Color yellow = new Color(display, 255, 255, 225); 
		int windowX = shell.getLocation().x;
		int windowY = shell.getLocation().y;

		//final Shell dialog = new Shell(shell, SWT.APPLICATION_MODAL | SWT.NO_TRIM);
		final Shell dialog = new Shell(shell, SWT.APPLICATION_MODAL | SWT.NO_TRIM);
		dialog.setText("Select template to use");
		int width = 300;
		int height = 140;
		dialog.setSize(width, height);
		dialog.setLocation(x+windowX+width, y+windowY+height);

		final List myList = new List(dialog, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL );
		myList.setBackground(yellow);
		for (int i=0; i<entries.length; i++) { 
			//Add the entry without the file extension - case insensitive replace all (?i)
			String entry = entries[i].replaceAll("(?i).ftl","");
			myList.add(entry); 
		}
		myList.select(0);
		myList.setBounds(0, 0, width, height);

		final Button buttonOK = new Button(dialog, SWT.PUSH);
		buttonOK.setText("OK");
		buttonOK.setBounds(20, 0, 80, 25);

		dialog.setDefaultButton(buttonOK);

		Button buttonCancel = new Button(dialog, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonCancel.setBounds(120, 0, 80, 25);

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == buttonOK) {
					String selected[] = myList.getSelection();
					if (selected.length > 0 ) {
						selectedTemplateName = selected[0];
					}
				}
				dialog.close();
			}
		};

		buttonOK.addListener(SWT.Selection, listener);
		buttonCancel.addListener(SWT.Selection, listener);
		dialog.open();

		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		yellow.dispose();
	}
	
	public String getTemplateFolderFromParser() {
		//Get the parser key for the current document
		String parser = view.query("parser");
		if (parser == null) {
			String filename = view.query("name");
			int dot = filename.lastIndexOf(".");
			parser = filename.substring(dot+1);
			PluginLogger.logger.info("LPEX parser returned null so we used file extension instead from " + filename);
		}
		PluginLogger.logger.info("Parser: " + parser);
		
		String templateFolderName = "unknown";
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String[] parserMappings = ParserAssociationPreferencePage.parseToArray(store.getString(PreferenceConstants.P_PARSER_MAPPINGS));
		for (String mappingStr : parserMappings)
		{
			try {
				String[] mapping = readParserMapping(mappingStr);
				if (mapping[0].toLowerCase().equals(parser.toLowerCase()))
				{
					templateFolderName = mapping[1];
				}
			} catch (Exception e)
			{
				PluginLogger.logger.warning("Failed to load a parser mapping");
				PluginLogger.logger.warning(StackTraceUtil.getStackTrace(e));
			}
		}
		PluginLogger.logger.info("Template folder: " + templateFolderName);
		
		return templateFolderName;
	}
	
	private String[] readParserMapping(String mapping) {
		StringTokenizer tokenizer = new StringTokenizer(mapping, "=");
		int tokenCount = tokenizer.countTokens();
		String[] elements = new String[tokenCount];
		for (int i = 0; i < tokenCount; i++) {
			elements[i] = tokenizer.nextToken();
		}
		return elements;
	}

	public String getCursorWord() {
		int endCol = view.queryInt("displayPosition");
		String wholeLine = view.query("text");
		String word = "";
		int startCol = endCol - 1;
		while ((startCol >= 1) && (startCol <= wholeLine.length())
				&& (isNameChar(wholeLine.charAt(startCol - 1)))) {
			startCol -= 1;
		}
		while ((endCol <= wholeLine.length())
				&& (isNameChar(wholeLine.charAt(endCol - 1)))) {
			endCol += 1;
		}
		if (endCol - startCol > 1) {
			word = wholeLine.substring(startCol, endCol - 1);
		}
		templateHintWord = word;
		return word;
	}

	private static boolean isNameChar(char charToCheck) {
		if (((charToCheck >= 'A') && (charToCheck <= 'Z'))
				|| ((charToCheck >= 'a') && (charToCheck <= 'z'))
				|| ((charToCheck >= '0') && (charToCheck <= '9'))
				|| (charToCheck == '#') || (charToCheck == '@')
				|| (charToCheck == '$') || (charToCheck == '_')) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isInBlock(LpexView view) {

		String blockType = view.query("block.type");
		if (blockType.equals("none")) {
			return false;
		}

		if (!(view.queryOn("block.inView"))) {
			return false;
		}

		if (blockType.equals("stream")) {
			return true;
		}

		int displayPosition = view.queryInt("displayPosition");
		int currLine = view.queryInt("element");

		if (displayPosition == 0) {
			return false;
		}

		if ((currLine < view.queryInt("block.topElement"))
				|| (currLine > view.queryInt("block.bottomElement"))) {
			return false;
		}

		if (blockType.equals("character")) {
			if ((view.queryInt("block.topElement") == currLine && displayPosition < view
					.queryInt("block.topPosition"))
					|| (view.queryInt("block.bottomElement") == currLine && displayPosition > view
							.queryInt("block.bottomPosition"))) {
				return false;
			}
		}

		if (blockType.equals("rectangle")) {
			if ((displayPosition < view.queryInt("block.topPosition"))
					|| (displayPosition > view.queryInt("block.bottomPosition"))) {
				return false;
			}
		}

		return true;

	}

	public void getCursorPosition() {
		// determine the cursor pixel position
		x = view.queryInt("pixelPosition");
		//x = view.window().getBounds().x;
		y = view.queryInt("cursorRow") * view.queryInt("rowHeight");

		// adjust for the prefix area, expand/hide area, horizontal scroll
		if (x >= 0) {
			if (view.query("prefixArea").equalsIgnoreCase("ON")) {
				x += view.queryInt("prefixAreaWidth");				
			}
			if (view.query("expandHide").equalsIgnoreCase("ON")) {
				x += view.queryInt("prefixAreaWidth");				
			}
			x -= view.queryInt("scroll");
		}

		// determine the y offset and height
		int rowHeight = view.queryInt("rowHeight");
		int rows = view.queryInt("rows");

		if (rows > 0) {
			// adjust y offset for the top expand header
			if (view.elementOfRow(1) == 0) {
				//
			} else {
				y = rowHeight;
			}

			// use only the element rows shown in the text area
			for (int i = 2; i <= rows && view.elementOfRow(i) != 0; i++) {
				y += rowHeight;
			}
		}
	}
	
	public LpexView getView() {
		return view;
	}

	public void setView(LpexView view) {
		this.view = view;
	}

	public String getSelectedTemplateName() {
		return selectedTemplateName;
	}
	
	public String getSelectedTemplateNameNoExt() {
		String test = selectedTemplateName;
		try {
			int extIndex = selectedTemplateName.lastIndexOf(".");
			test = selectedTemplateName.substring(0,extIndex);
		} catch (Exception e) {}
		return test;
	}

	public void setSelectedTemplateName(String selectedTemplateName) {
		this.selectedTemplateName = selectedTemplateName;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}
	
}
