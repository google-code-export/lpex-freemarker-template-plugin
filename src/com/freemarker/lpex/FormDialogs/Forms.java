package com.freemarker.lpex.formdialogs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import com.freemarker.lpex.Activator;
import com.freemarker.lpex.formdialogs.Prompt.InputType;
import com.freemarker.lpex.utils.PluginLogger;
import com.freemarker.lpex.utils.StackTraceUtil;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Forms implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private ArrayList<PromptGroup> promptGroups;
	
	public Forms()
	{
		promptGroups = new ArrayList<PromptGroup>();
	}
	
	public Forms(ArrayList<PromptGroup> promptGroups)
	{
		this.promptGroups = promptGroups;
	}
	
	public Forms(String xml) throws Exception
	{
		promptGroups = new ArrayList<PromptGroup>();
		this.loadFromXML(xml);
	}
	
	public void loadFromXML(String xml) throws Exception
	{
		Document xmlobj = loadXMLFromString(xml);
		name = xmlobj.getElementsByTagName("name").item(0).getTextContent();
		description = xmlobj.getElementsByTagName("description").item(0).getTextContent();
		if (promptGroups != null)
		{
			// Loop through each prompt group
			NodeList promptGroupNodes = xmlobj.getElementsByTagName("promptgroup");
			int numPromptGroupNodes = promptGroupNodes.getLength();
			//PluginLogger.logger.info("Number of Prompt Groups: "+numPromptGroupNodes);
			for (int i = 0; i < numPromptGroupNodes; i++)
			{
				PromptGroup pg = new PromptGroup();
				Element promptGroupElement = (Element) promptGroupNodes.item(i);
				pg.setName(promptGroupElement.getAttribute("name"));
				pg.setRepeatable(promptGroupElement.getAttribute("repeatable"));
				pg.setMaxRepeats(promptGroupElement.getAttribute("maxRepeats"));
				
				//Loop through each prompt element in this prompt group
				NodeList promptNodes = promptGroupElement.getElementsByTagName("prompt");
				int numPromptNodes = promptNodes.getLength();
				for (int j = 0; j < numPromptNodes; j++)
				{
					try
					{
						Element promptElement = (Element) promptNodes.item(j);
						pg.addPrompt(parsePrompt(promptElement));
						promptElement = null;
					}
					catch (Exception e) 
					{
						PluginLogger.logger.info("Skipped a invalid prompt: " + e.getMessage());
						//PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
					}
				}

				//Add the prompt group
				promptGroups.add(pg);
			}
		}
		xmlobj = null;
	}
	
	private Prompt parsePrompt(Element promptElement) throws Exception {
		Prompt prompt = new Prompt();
		try {
			prompt.setType(promptElement.getElementsByTagName("type").item(0).getTextContent());
		} catch (Exception e) {
			//Cannot hide this error, so prevent the prompt from being used
			throw new Exception("A valid prompt type is required.", e);
		}
		try {
			prompt.setName(promptElement.getElementsByTagName("name").item(0).getTextContent());
		} catch (Exception e) {
			//Cannot hide this error, so prevent the prompt from being used
			throw new Exception("A valid prompt name is required.", e);
		}
		try {
			prompt.setLabel(promptElement.getElementsByTagName("label").item(0).getTextContent());
		} catch (Exception e) {
			//Suppress this error and just use the name instead
			prompt.setLabel(prompt.getName());
		}
		try {
			prompt.setDescription(promptElement.getElementsByTagName("description").item(0).getTextContent());
		} catch (Exception e) {
			//Suppress this error and just use an empty string
			prompt.setDescription("");
		}
		try {
			prompt.setHint(promptElement.getElementsByTagName("hint").item(0).getTextContent());
		} catch (Exception e) {
			//Suppress this error and just use an empty string
			prompt.setHint("");
		}
		try {
			Object defaultObject = null;
			String defaultText = promptElement.getElementsByTagName("default").item(0).getTextContent();
			if (prompt.getType() == InputType.CHECKBOX) {
				if (defaultText == "checked") {
					defaultObject = new Boolean(true);
				}else{
					defaultObject = new Boolean(false);
				}
			}else if (prompt.getType() == InputType.MULTILINE) {
				defaultObject = defaultText;
			}else if (prompt.getType() == InputType.TEXT) {
				defaultObject = defaultText;
			}
			prompt.setDefaultValue(defaultObject);
		} catch (Exception e) {
			//Suppress this error and just use an empty string
			prompt.setDefaultValue(null);
		}
		
		//Apply the special attributes for some types
		parseTypeOptions(promptElement, prompt);
		
		return prompt;
	}
	
	private void parseTypeOptions(Element promptElement, Prompt prompt) {
		Element typeTag = (Element) promptElement.getElementsByTagName("type").item(0);
		if (prompt.getType() == InputType.CHECKBOX) {
			//Read additional options
			try {
				prompt.setCheckedValue(typeTag.getAttribute("checkedValue"));
			} catch (Exception e) {
				prompt.setCheckedValue("");
			}
			try {
				prompt.setUncheckedValue(typeTag.getAttribute("uncheckedValue"));
			} catch (Exception e) {
				prompt.setCheckedValue("");
			}
		}else if (prompt.getType() == InputType.DATE) {
			//Read additional options
			try {
				prompt.setDateFormat(typeTag.getAttribute("dateFormat"));
			} catch (Exception e) {
				prompt.setDateFormat(Prompt.DEFAULT_DATE_FORMAT);
			}
		}else if (prompt.getType() == InputType.MULTILINE) {
			//None
		}else if (prompt.getType() == InputType.TEXT) {
			//None
		}
	}
	
	@SuppressWarnings("unused")
	private String nodeListToString(Node list)
	{
		Document document = list.getOwnerDocument();
		DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
		LSSerializer serializer = domImplLS.createLSSerializer();
		return serializer.writeToString(list);
	}
	
    private Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
	
	public void addPromptGroup(PromptGroup promptGroup)
	{
		promptGroups.add(promptGroup);
	}
	
	public ArrayList<PromptGroup> getPromptGroups()
	{
		return promptGroups;
	}
	
	public PromptGroup getPromptGroup(int index)
	{
		return promptGroups.get(index);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void openAsFormDialog() {
		try {
			//Display display = PlatformUI.getWorkbench().getDisplay();
			Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			//TODO Figure out why it crashes here
			TemplateFormDialog dialog = new TemplateFormDialog(parentShell);
			dialog.create();
			dialog.getShell().setSize(800, 600);
			dialog.open();
		} catch (Exception e) {
			PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
		}
	}
	
	public void open() {
		//Draw a dialog for each prompt group
		for (PromptGroup promptGroup : promptGroups) {
			displayDialog(promptGroup, 0);
		}
		
	}
	
	private void displayDialog(final PromptGroup promptGroup, final Integer repeatIndex) {
		try {
			final Display display = PlatformUI.getWorkbench().getDisplay();
			final Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			
			// Draw the prompt using SWT
			final Shell shell = new Shell(parentShell, SWT.TITLE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			shell.setSize(600, 450);
			shell.setText(this.name + " - " + promptGroup.getName());
			shell.setLocation(200, 200);
			shell.setLayout(new GridLayout(2, false));

			//Add an OK button
			final Button buttonOK = new Button(shell, SWT.PUSH);
			buttonOK.setText("OK");
			buttonOK.setSize(80, 25);
			GridData gridData = new GridData();
			gridData.verticalAlignment = SWT.TOP;
			buttonOK.setLayoutData(gridData);
			shell.setDefaultButton(buttonOK);
			buttonOK.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					if (event.widget == buttonOK) {
						if (promptGroup.isRepeatable()) {
							if (repeatIndex < promptGroup.getMaxRepeats()) {
								//If entry is found then present again until no entry
								String allEnteredText = PromptGroup.getAllValuesByIndex(promptGroup.getName(), repeatIndex);
								//PluginLogger.logger.info("All entered text for " + promptGroup.getName() + " (" + (repeatIndex + 1) + "/" + promptGroup.getMaxRepeats() + "): \"" + allEnteredText + "\"");
								if (allEnteredText == "") {
									//PluginLogger.logger.info("Data entered is blank, so do not auto repeat.");
									shell.close();
								}else{
									//PluginLogger.logger.info("Auto repeat of the " + promptGroup.getName() + " dialog (" + (repeatIndex + 1) + "/" + promptGroup.getMaxRepeats() + ")");
									shell.close();
									displayDialog(promptGroup, repeatIndex + 1);
								}
							}
						}else{
							shell.close();
						}
					}
				}
			});
			
			//Show repeater button if needed
			if (promptGroup.isRepeatable()) {
				final Button buttonRepeat = new Button(shell, SWT.PUSH);
				buttonRepeat.setText("Add another " + promptGroup.getName());
				gridData.horizontalAlignment = GridData.FILL;
				buttonRepeat.setLayoutData(gridData);
				buttonRepeat.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						if (event.widget == buttonRepeat) {
							if (repeatIndex < promptGroup.getMaxRepeats()) {
								//PluginLogger.logger.info("Manual repeat of the " + promptGroup.getName() + " dialog (" + repeatIndex + 1 + "/" + promptGroup.getMaxRepeats() + ")");
								shell.close();
								displayDialog(promptGroup, repeatIndex + 1);
							}
						}
					}
				});
			}else{
				Label filler = new Label(shell, SWT.NONE);
				filler.setLayoutData(gridData);
			}

			
			// Enter key acts as the OK button
			/*Control[] widgets = shell.getChildren();
			for (int i = 0; i < widgets.length; i++) {
				widgets[i].addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent event) {
						if (event.character == SWT.ESC) {
							shell.close();
						}
					}
				});
			}*/

			//Draw all of the input fields
			promptGroup.render(shell, repeatIndex);

			//Present to the user
			shell.open();

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			PluginLogger.logger.info("Failed to render the prompt group");
			PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
		}
	}

    public final void handleReturnPress(Event e) {
        if ((e.keyCode == SWT.CR) || (e.keyCode == SWT.LF))
        {
            System.out.println("From Display I am the Key down !!" + e.keyCode);
        }
    }
	
	public void openOnSingleForm() {
		//Draw a single dialog with a group UI element for each prompt group
		try {
			// Draw the prompt using SWT
			Display display = PlatformUI.getWorkbench().getDisplay();
			Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			Shell shell = new Shell(parentShell);
			shell.setSize(500, 400);
			shell.setLocation(300, 300);
			shell.setLayout(new GridLayout(2, false));
			for (PromptGroup promptGroup : promptGroups) {
				Group group = new Group(shell, SWT.SHADOW_ETCHED_IN);
			    group.setText("Group Name Here");
				GridData gridData = new GridData();
				gridData.horizontalAlignment = SWT.FILL;
				gridData.grabExcessHorizontalSpace = true;
				gridData.verticalAlignment = SWT.FILL;
				gridData.grabExcessVerticalSpace = true;
				group.setLayoutData(gridData);
				promptGroup.render(group);
			}
			
			//Present to the user
			shell.open();

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			PluginLogger.logger.info("Failed to render the prompt group");
			PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
		}
	}
	
	public String toString()
	{
		String ret = "";
		Template template;
		String templateText = ""
			+ "Template Configuration \r\n"
			+ "--------------------------------------------\r\n"
			+ "Name:          ${name} \r\n"
			+ "Description:   ${description} \r\n"
			+ "<#list promptGroups as promptGroup> \r\n"
			+ "Prompt Group ${promptGroup_index} \r\n"
			+ "  Repeatable:   ${promptGroup.repeatable?string(\"yes\", \"no\")} \r\n"
			+ "  Max Repeats:  ${promptGroup.maxRepeats} \r\n"
			+ "  <#list promptGroup.prompts as prompt> \r\n"
			+ "  Prompt ${prompt_index} \r\n"
			+ "    Type:   ${prompt.type} \r\n"
			+ "    Name:   ${prompt.name} \r\n"
			+ "    Text:   ${prompt.text} \r\n"
			+ "  </#list> \r\n"
			+ "</#list>";

		try {
			template = new Template(this.name+".toString()", new StringReader(templateText), Activator.freemarkerConfig);
			Writer out = new OutputStreamWriter(output);
			template.process(this, out);
			out.flush();
			ret = output.toString();
		} catch (IOException e) {

		} catch (TemplateException e) {

		}
		
        return ret;
	}

    private transient OutputStream output = new OutputStream()
    {
        private StringBuilder string = new StringBuilder();
        @Override
        public void write(int b) throws IOException {
            this.string.append((char) b );
        }

        public String toString(){
            return this.string.toString();
        }
    };
}