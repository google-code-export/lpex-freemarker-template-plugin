package com.freemarker.lpex.FormDialogs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import com.freemarker.lpex.Activator;
import com.freemarker.lpex.Utils.PluginLogger;
import com.freemarker.lpex.Utils.StackTraceUtil;

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
						Prompt p = new Prompt();
						Element promptElement = (Element) promptNodes.item(j);
						p.setType(promptElement.getElementsByTagName("type").item(0).getTextContent());
						p.setName(promptElement.getElementsByTagName("name").item(0).getTextContent());
						p.setLabel(promptElement.getElementsByTagName("label").item(0).getTextContent());
						p.setDescription(promptElement.getElementsByTagName("description").item(0).getTextContent());
						p.setHint(promptElement.getElementsByTagName("hint").item(0).getTextContent());
						pg.addPrompt(p);
					}
					catch (Exception e) 
					{
						PluginLogger.logger.info("Processing prompt "+j+" exited prematurely");
						//PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
					}
				}

				//Add the prompt group
				promptGroups.add(pg);
			}
		}
		xmlobj = null;
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
			final Shell shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			shell.setSize(500, 300);
			shell.setLocation(300, 300);
			shell.setLayout(new GridLayout(2, false));

			//Add an OK button
			final Button buttonOK = new Button(shell, SWT.PUSH);
			buttonOK.setText("OK");
			//buttonOK.setBounds(20, 0, 80, 25);
			GridData gridData = new GridData();
			gridData.verticalAlignment = SWT.TOP;
			buttonOK.setLayoutData(gridData);
			shell.setDefaultButton(buttonOK);
			buttonOK.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					if (event.widget == buttonOK) {
						shell.close();
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
								PluginLogger.logger.info("Launching " + promptGroup.getName() + " dialog (" + repeatIndex + "/" + promptGroup.getMaxRepeats() + ")");
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
			template = new Template(this.name+".toString()", new StringReader(templateText), Activator.cfg);
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