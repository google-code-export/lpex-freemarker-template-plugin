package com.freemarker.lpex.formdialogs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.freemarker.lpex.Activator;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class LPEXTemplate {
	private Forms form = null;
	private String templateText = null;
	public static Map<String, Object> formData = null;
	private String result = null;
	
	public LPEXTemplate(String templateText) {
		this.templateText = templateText;
	}
	
	public LPEXTemplate(File file) throws IOException {
		this.templateText = readFileAsString(file);
	}
	
	public void initializeFormData() {
		formData = new HashMap<String, Object>();
		for (PromptGroup promptGroup : form.getPromptGroups()) {
			//Create a hash for this prompt group level
			Map<String, Object> pgHash = new HashMap<String, Object>();
			
			//Create a list of 1 or more collections of prompt results
			ArrayList<Map<String, Object>> repeats = new ArrayList<Map<String, Object>>();
			
			//Initialize the first in the list
			//repeats.add(promptGroup.getInitializedMap());
			
			//Create a set of shortcut fields for the first prompt result
			pgHash.putAll(promptGroup.getInitializedMap());
			
			//Add the repeat set
			pgHash.put("repeats", repeats);
			
			//Attach this new prompt group level to the root
			formData.put(promptGroup.getName(), pgHash);
		}
	}
	
	public void buildForm() throws Exception {
		String form_xml = getFormXML(this.templateText);
		if ((form_xml != "") && (form_xml != null)) {
			try {
				form = new Forms();
				form.loadFromXML(form_xml);
			} catch (Exception e) {
				form = null;
				throw new Exception("Failed parsing the form XML.", e);
			}
		}else{
			throw new Exception("No form XML found.");
		}
	}
	
	public Forms getForm() throws Exception {
		if (form == null) {
			throw new Exception("Template form not loaded.");
		}
		return form;
	}

	public String merge() throws TemplateException, IOException, Exception {
		if (form == null) {
			throw new Exception("Template form not loaded.");
		}

		if (formData == null) {
			throw new Exception("Form data not loaded.");
		}
		
		//Create the FreeMarker template
		Template template = new Template(form.getName(), new StringReader(templateText), Activator.cfg);

		//Merge data model and template into a string 
		Writer out = new OutputStreamWriter(output);
		template.process(formData, out);
		out.flush();
		result = output.toString();
		
		//Cleanup
		out = null;
		template = null;
		
		return result;
	}
	
	public static String getFormDataAsString() {
		String out = "Form Data\r\n";

		//Loop through each prompt group data element
	    Iterator pgd_it = LPEXTemplate.formData.entrySet().iterator();
	    while (pgd_it.hasNext()) {
	        Map.Entry promptGroup = (Map.Entry)pgd_it.next();
	        String promptGroupName = (String) promptGroup.getKey();
	        Map<String, Object> promptGroupMap = (Map<String, Object>) promptGroup.getValue();
	        out += promptGroupName + "\r\n";
	        
	        //Loop through each collected set of prompt data
	        int i = 0;
	        ArrayList<Map<String, Object>> repeats = (ArrayList<Map<String, Object>>) promptGroupMap.get("repeats");
			for (Map<String, Object> map:repeats) {
				out += "   Repeat (" + (i+1)  + "/" + repeats.size() + ")\r\n";
			    Iterator it = map.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pairs = (Map.Entry)it.next();
					out += "      " + pairs.getKey() + " : " + pairs.getValue() + "\r\n";
			    }
			    i++;
			}
	    }
		
		return out;
	}
	
	public String getResult() throws TemplateException, IOException, Exception {
		if (result != null) {
			return result;
		}else{
			return merge();
		}
	}

	public String getName() throws Exception {
		if (form == null) {
			throw new Exception("Template form not loaded.");
		}
		return form.getName();
	}

	public String getDescription() throws Exception {
		if (form == null) {
			throw new Exception("Template form not loaded.");
		}
		return form.getDescription();
	}
	
	private String getFormXML(String rawTemplate) throws Exception
	{
		String formBlock = "";
		try {
			String startingTag = "<#--";
			String endingTag = "-->";
			int startingPosition = 0;
			int endingPosition = 0;
			startingPosition = rawTemplate.indexOf(startingTag) + startingTag.length();
			endingPosition = rawTemplate.indexOf(endingTag);
			formBlock = rawTemplate.substring(startingPosition, endingPosition);
		} catch (Exception e) {
			formBlock = "";
			throw new Exception("Didn't find any XML in the first comment.", e);
		}
		return formBlock;
	}
	
    private OutputStream output = new OutputStream()
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
    
	private String readFileAsString(File file) throws java.io.IOException {
		
		byte[] buffer = new byte[(int) file.length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(file));
			f.read(buffer);
		} finally {
			if (f != null)
				try {
					f.close();
				} catch (IOException ignored) {
				}
		}
		return new String(buffer);
	}
}
