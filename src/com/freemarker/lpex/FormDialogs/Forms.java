package com.freemarker.lpex.FormDialogs;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
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
	
	public void open() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		//Draw a dialog for each prompt group
		for (PromptGroup promptGroup : promptGroups) {
			try {
				// Draw the prompt using SWT
				final Shell shell = new Shell(parentShell, SWT.APPLICATION_MODAL);
				shell.setSize(500, 300);
				shell.setLocation(300, 300);
				shell.setLayout(new GridLayout(2, false));
				promptGroup.render(shell);

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