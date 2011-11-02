package com.freemarker.lpex.formdialogs;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.freemarker.lpex.Activator;
import com.freemarker.lpex.preferences.PreferenceConstants;
import com.freemarker.lpex.utils.PluginLogger;
import com.freemarker.lpex.utils.StackTraceUtil;

public class Prompt implements Serializable {

	private static final long serialVersionUID = 3L;

	public enum InputType implements Serializable {
		TEXT, DATE, MULTILINE, CHECKBOX
	}

	public final static String DEFAULT_DATE_FORMAT = Activator.preferenceStore.getString(PreferenceConstants.P_DATE_FORMAT);
	private String groupPromptName = "";
	private Integer currentRepeat = 0;
	private Object shell = null;
	private Boolean isGrouped = false;
	private InputType type = InputType.TEXT;
	private String name = "";
	private String label = "";
	private String description = "";
	private String hint = "";
	private String checkedValue = "";
	private String uncheckedValue = "";
	private String dateFormat = DEFAULT_DATE_FORMAT;
	private Object defaultValue = "";
	private org.eclipse.swt.events.ModifyListener modifyTextListener;

	public Prompt() {
	}

	public Prompt(InputType type, String name, String label, String description, String hint) {
		setType(type);
		setName(name);
		setLabel(label);
		setDescription(description);
		setHint(hint);
	}
	
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getCheckedValue() {
		return checkedValue;
	}

	public void setCheckedValue(String checkedValue) {
		this.checkedValue = checkedValue;
	}

	public String getUncheckedValue() {
		return uncheckedValue;
	}

	public void setUncheckedValue(String uncheckedValue) {
		this.uncheckedValue = uncheckedValue;
	}

	public static String getPromptValueAt(String promptGroup, String prompt, Integer index) {
		Map<String, Object> map = PromptGroup.getRepeatingData(promptGroup).get(index);
		return (String) map.get(prompt);
	}

	public Integer getCurrentRepeat() {
		return currentRepeat;
	}

	public void setCurrentRepeat(Integer currentRepeat) {
		this.currentRepeat = currentRepeat;
	}

	public String getGroupPromptName() {
		return groupPromptName;
	}

	public void setGroupPromptName(String groupPromptName) {
		this.groupPromptName = groupPromptName;
	}

	public Object getShell() {
		return shell;
	}

	public void setShell(Object shell) {
		this.shell = shell;
	}

	public InputType getType() {
		return type;
	}

	public void setType(InputType type) {
		this.type = type;
	}
	
	public void setType(String type) {
		if (type.equalsIgnoreCase("text")) setType(InputType.TEXT);
		else if (type.equalsIgnoreCase("multiline")) setType(InputType.MULTILINE);
		else if (type.equalsIgnoreCase("checkbox")) setType(InputType.CHECKBOX);
		else if (type.equalsIgnoreCase("date")) setType(InputType.DATE);
		else setType(InputType.TEXT);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			this.name = "";
		}else{
			this.name = name;
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		if (label == null) {
			this.label = "";
		}else{
			this.label = label;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description == null) {
			this.description = "";
		}else{
			this.description = description;
		}
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		if (hint == null) {
			this.hint = "";
		}else{
			this.hint = hint;
		}
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		if (defaultValue == null) {
			this.defaultValue = new String("");
		}else{
			this.defaultValue = defaultValue;
		}
	}
	
	public void render(Group group) {
		isGrouped = true;
		_render(group);
	}
	
	public void render(Shell shell) {
		isGrouped = false;
		_render(shell);
	}
	
	private void _render(Object shell) {
		setShell(shell);
		renderPromptLabel();
		//renderPromptDescription();
		
		//Choose which style input control to render based on the type
		if (this.type == InputType.TEXT) renderTextInput();
		else if (this.type == InputType.MULTILINE) renderMultilineTextInput();
		else if (this.type == InputType.CHECKBOX) renderCheckboxInput();
		else if (this.type == InputType.DATE) renderDateInput();
	}
	
	private void renderPromptLabel() {
		Label label = null;
		//Label filler = null;
		if (isGrouped) {
			label = new Label((Group) shell, SWT.NONE);
			//filler = new Label((Group) shell, SWT.NONE);
		}else{
			label = new Label((Shell) shell, SWT.NONE);
			//filler = new Label((Shell) shell, SWT.NONE);
		}
		
		label.setText(this.label + ":");
		label.setToolTipText(this.description);
		//label.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD));
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		label.setLayoutData(gridData);
		//filler.setLayoutData(gridData);
	}
	
	private void renderPromptDescription() {
		Label description = null;
		if (isGrouped) {
			description = new Label((Group) shell, SWT.WRAP | SWT.NONE);
		}else{
			description = new Label((Shell) shell, SWT.WRAP | SWT.NONE);
		}
		
		description.setText(this.description);
		description.setSize(150, 40);
		description.setLocation(0, 0);  
		//GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		GridData gridData = new GridData();
		gridData.widthHint = 10;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.heightHint = 50;
		gridData.verticalAlignment = SWT.FILL;
		description.setLayoutData(gridData);
	}
	
	private void renderTextInput() {
		Text text = null;
		if (isGrouped) {
			text = new Text((Group) shell, SWT.BORDER);
		}else{
			text = new Text((Shell) shell, SWT.BORDER);
		}
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.TOP;
		text.setLayoutData(gridData);
		text.setToolTipText(hint);
		text.setData("promptGroupName", groupPromptName);
		text.setData("promptName", this.name);
		text.setData("repeatIndex", this.currentRepeat);
		text.setData("hint", this.hint);
		text.setData("default", this.defaultValue);

		//Choose to show the hint or the default value
		try {
			String defaultText = (String) defaultValue;
			if ((defaultText.equals("")) || (defaultText == null)) {
				if (hint != "") {
					addAutoClearingHint(text, hint);
				}
			}else{
				text.setText(defaultText);
			}
		} catch (Exception e) {}

		//Add the listener to capture the data entered automatically
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				// Get the widget whose text was modified
				Text text = (Text) event.widget;
				String pomptHint = (String) text.getData("hint");
				//Check for non entry
				if ((text.getText() == pomptHint) ||
					(text.getText() == "")) {
					return;
				}
				String promptGroupName = (String) text.getData("promptGroupName");
				String promptName = (String) text.getData("promptName");
				Integer repeatIndex = (Integer) text.getData("repeatIndex");
				Map<String, Object> promptGroup = (Map<String, Object>) LPEXTemplate.formData.get(promptGroupName);
                ArrayList<Map<String, Object>> repeats = (ArrayList<Map<String, Object>>) promptGroup.get("repeats");
                try {
					repeats.get(repeatIndex).put(promptName, text.getText());
				} catch (Exception e) {
					Map<String, Object> capturedValue = new HashMap<String, Object>();
					capturedValue.put(promptName, text.getText());
					repeats.add(repeatIndex, capturedValue);
				}
				// If it's the first item create the natural shortcuts to
				// prevent having to use the repeats array when there is 
				// only one item
				if (repeatIndex == 0) {
					promptGroup.put(promptName, text.getText());
				}
			}
		});
		text.notifyListeners(SWT.Modify, new Event());
	}
	
	private void renderMultilineTextInput() {
		Text text = null;
		if (isGrouped) {
			text = new Text((Group) shell, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		}else{
			text = new Text((Shell) shell, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		}
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		text.setLayoutData(gridData);
		text.setToolTipText(hint);
		text.setData("promptGroupName", groupPromptName);
		text.setData("promptName", this.name);
		text.setData("repeatIndex", this.currentRepeat);
		text.setData("hint", this.hint);
		text.setData("default", this.defaultValue);
		
		//Choose to show the hint or the default value
		try {
			String defaultText = (String) defaultValue;
			if ((defaultText.equals("")) || (defaultText == null)) {
				if (hint != "") {
					addAutoClearingHint(text, hint);
				}
			}else{
				text.setText(defaultText);
			}
		} catch (Exception e) {}
		
		//Prevent the multiline control from stealing the tab key for \t characters
		text.addTraverseListener(new TraverseListener() {
		    public void keyTraversed(TraverseEvent e) {
		        if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
		            e.doit = true;
		        }
		    }
		});

		//Add the listener to capture the data entered automatically
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				try {
					// Get the widget whose text was modified
					Text text = (Text) event.widget;
					String pomptHint = (String) text.getData("hint");
					//Check for non entry
					if ((text.getText() == pomptHint) ||
						(text.getText() == "")) {
						return;
					}
					String promptGroupName = (String) text.getData("promptGroupName");
					String promptName = (String) text.getData("promptName");
					Integer repeatIndex = (Integer) text.getData("repeatIndex");
					Map<String, Object> promptGroup = (Map<String, Object>) LPEXTemplate.formData.get(promptGroupName);
					ArrayList<Map<String, Object>> repeats = (ArrayList<Map<String, Object>>) promptGroup.get("repeats");
					try {
						repeats.get(repeatIndex).put(promptName, text.getText());
					} catch (Exception e) {
						Map<String, Object> capturedValue = new HashMap<String, Object>();
						capturedValue.put(promptName, text.getText());
						repeats.add(repeatIndex, capturedValue);
					}
					// If it's the first item create the natural shortcuts to
					// prevent having to use the repeats array when there is 
					// only one item
					if (repeatIndex == 0) {
						promptGroup.put(promptName, text.getText());
					}
				} catch (Exception e) {
					PluginLogger.logger.info("Failed to handle multi-line text modification.");
					PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
				}
			}
		});
		text.notifyListeners(SWT.Modify, new Event());
	}
	
	private void renderCheckboxInput() {
		Button checkbox = null;
		if (isGrouped) {
			checkbox = new Button((Group) shell, SWT.CHECK);
		}else{
			checkbox = new Button((Shell) shell, SWT.CHECK);
		}

		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.TOP;
		checkbox.setLayoutData(gridData);
		checkbox.setText(name);
		checkbox.setData("promptGroupName", groupPromptName);
		checkbox.setData("promptName", this.name);
		checkbox.setData("repeatIndex", this.currentRepeat);
		checkbox.setData("hint", this.hint);
		checkbox.setData("checkedValue", this.checkedValue);
		checkbox.setData("uncheckedValue", this.uncheckedValue);
		try {
			checkbox.setData("default", (Boolean)this.defaultValue);
		} catch (Exception ex) {
			checkbox.setData("default", false);
		}
		try {
			Boolean checked = (Boolean) defaultValue;
			checkbox.setSelection(checked);
		} catch (Exception e) {}
	    checkbox.setToolTipText(hint);

		//Add the listener to capture the data entered automatically
		//checkbox.addListener(SWT.Selection, new Listener() {
	    checkbox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				Button checkbox = (Button) event.widget;
				String checkboxValue = checkbox.getSelection() ? 
						(String) checkbox.getData("checkedValue") : 
						(String) checkbox.getData("uncheckedValue");
				String promptGroupName = (String) checkbox.getData("promptGroupName");
				String promptName = (String) checkbox.getData("promptName");
				Integer repeatIndex = (Integer) checkbox.getData("repeatIndex");
				Map<String, Object> promptGroup = (Map<String, Object>) LPEXTemplate.formData.get(promptGroupName);
				ArrayList<Map<String, Object>> repeats = (ArrayList<Map<String, Object>>) promptGroup.get("repeats");
				try {
					repeats.get(repeatIndex).put(promptName, checkboxValue);
				} catch (Exception e) {
					Map<String, Object> capturedValue = new HashMap<String, Object>();
					capturedValue.put(promptName, checkboxValue);
					repeats.add(repeatIndex, capturedValue);
				}
				// If it's the first item create the natural shortcuts to
				// prevent having to use the repeats array when there is
				// only one item
				if (repeatIndex == 0) {
					promptGroup.put(promptName, checkboxValue);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
		checkbox.notifyListeners(SWT.Modify, new Event());
	}
	
	private void renderDateInput() {
		DateTime date = null;
		if (isGrouped) {
			date = new DateTime((Group) shell, SWT.DATE | SWT.SHORT | SWT.BORDER | SWT.DROP_DOWN);
		}else{
			date = new DateTime((Shell) shell, SWT.DATE | SWT.SHORT | SWT.BORDER | SWT.DROP_DOWN);
		}

		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.TOP;
		date.setLayoutData(gridData);
		date.setToolTipText(hint);
		date.setData("promptGroupName", groupPromptName);
		date.setData("promptName", this.name);
		date.setData("repeatIndex", this.currentRepeat);
		//Data format string rules here:
		//  http://download.oracle.com/javase/tutorial/i18n/format/simpleDateFormat.html
		date.setData("dateFormat", this.dateFormat);
		date.setData("hint", this.hint);
		date.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DateTime date = (DateTime) event.widget;
				String promptGroupName = (String) date.getData("promptGroupName");
				String promptName = (String) date.getData("promptName");
				Integer repeatIndex = (Integer) date.getData("repeatIndex");
				String dateFormat = (String) date.getData("dateFormat");
				//PluginLogger.logger.info("Date format: " + dateFormat);
				//Build date object from results
			    Date selectedDate = null;
			    String selectedDateFormatted = "";
				try {
					//Build the date from the parts of the widget using the default date format
					SimpleDateFormat formatter = new SimpleDateFormat(Prompt.DEFAULT_DATE_FORMAT);
					String dateToParse = (date.getMonth() + 1) + "/" + date.getDay() + "/" + date.getYear();
					//PluginLogger.logger.info("Pieced together date from widget: " + dateToParse);
					selectedDate = formatter.parse(dateToParse);
					//PluginLogger.logger.info("Parsed date: " + selectedDate.toString());
					//Assign the date format from the template author
					formatter = new SimpleDateFormat(dateFormat);
					selectedDateFormatted = formatter.format(selectedDate);
					//PluginLogger.logger.info("Formatted date: " + selectedDateFormatted);
				} catch (Exception e) {
					//PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
				}
				
				Map<String, Object> promptGroup = (Map<String, Object>) LPEXTemplate.formData.get(promptGroupName);
				ArrayList<Map<String, Object>> repeats = (ArrayList<Map<String, Object>>) promptGroup.get("repeats");
				try {
					repeats.get(repeatIndex).put(promptName, selectedDateFormatted);
				} catch (Exception e) {
					Map<String, Object> capturedValue = new HashMap<String, Object>();
					capturedValue.put(promptName, selectedDateFormatted);
					repeats.add(repeatIndex, capturedValue);
				}
				// If it's the first item create the natural shortcuts to
				// prevent having to use the repeats array when there is
				// only one item
				if (repeatIndex == 0) {
					promptGroup.put(promptName, selectedDateFormatted);
				}
			}
		});
	}
	
    private void addAutoClearingHint(final Text text, final String defaultText) {
    	if(text.getText().equals("")) {
            text.setText(defaultText);
            text.setForeground(text.getDisplay().getSystemColor(SWT.COLOR_GRAY));
        }
        text.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                if(text.getText().equals(defaultText)) {
                    text.setText("");
                    text.setForeground(null);
                }
            }

            public void focusLost(FocusEvent e) {
                if(text.getText().equals("")) {
                    text.setText(defaultText);
                    text.setForeground(text.getDisplay().getSystemColor(SWT.COLOR_GRAY));
                }
            }
        });
    }
    /*
	private class ModifyListener {
		public void modifyText(ModifyEvent event) {
			// Get the widget whose text was modified
			Text text = (Text) event.widget;
			String promptGroupName = (String) text.getData("promptGroupName");
			String promptName = (String) text.getData("promptName");
			Integer repeatIndex = (Integer) text.getData("repeatIndex");
			Map<String, Object> promptGroup = (Map<String, Object>) LPEXTemplate.formData.get(promptGroupName);
			ArrayList<Map<String, Object>> repeats = (ArrayList<Map<String, Object>>) promptGroup.get("repeats");
			repeats.get(repeatIndex).put(promptName, text.getText());
			// If it's the first item create the natural shortcuts to prevent
			// having
			// to use the repeats array when there is only one item
			if (repeatIndex == 0) {
				promptGroup.put(promptName, text.getText());
			}
		}
	}*/
}