package com.freemarker.lpex.formdialogs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class PromptGroup implements Serializable {
	
	private static final long serialVersionUID = 2L;
	private String name;
	private boolean repeatable = false;
	private int maxRepeats = 0;
	private ArrayList<Prompt> prompts = new ArrayList<Prompt>();

	public PromptGroup() {}

	public PromptGroup(ArrayList<Prompt> prompts) throws Exception {
		setPrompts(prompts);
	}

	public static Map<String, Object> getRawData(String promptGroup) {
		return (Map<String, Object>) LPEXTemplate.formData.get(promptGroup);
	}
	
	public static ArrayList<Map<String, Object>> getRepeatingData(String promptGroup) {
		Map<String, Object> map = PromptGroup.getData(promptGroup);
		return (ArrayList<Map<String, Object>>) map.get("repeats");
	}
	
	public static Map<String, Object> getDataAt(String promptGroup, Integer index) {
		return (Map<String, Object>) getRepeatingData(promptGroup).get(index);
	}
	
	public static Map<String, Object> getData(String promptGroup) {
		return (Map<String, Object>) getRepeatingData(promptGroup).get(0);
	}
	
	public static String getAllValues(String promptGroup) {
		String allValues = "";
		try {
			Map<String, Object> promptGroupMap = (Map<String, Object>) LPEXTemplate.formData.get(promptGroup);
			ArrayList<Map<String, Object>> repeats = (ArrayList<Map<String, Object>>) promptGroupMap.get("repeats");
			
			for (Map<String, Object> map:repeats) {
			    Iterator it = map.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pairs = (Map.Entry)it.next();
			        allValues += pairs.getValue();
			    }
			}
		} catch (Exception e) {}
		return allValues;
	}
	
	public static String getAllValuesByIndex(String promptGroup, Integer index) {
		String allValues = "";
		try {
			Map<String, Object> promptGroupMap = (Map<String, Object>) LPEXTemplate.formData.get(promptGroup);
	        ArrayList<Map<String, Object>> repeats = (ArrayList<Map<String, Object>>) promptGroupMap.get("repeats");
	        
			Map<String, Object> map = repeats.get(index);
		    Iterator it = map.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        allValues += pairs.getValue();
		    }
		} catch (Exception e) {}
		return allValues;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRepeatable() {
		return repeatable;
	}

	public void setRepeatable(boolean repeatable) {
		this.repeatable = repeatable;
	}

	public void setRepeatable(String repeatable) {
		if ((repeatable.compareToIgnoreCase("yes") == 0) || 
			(repeatable.compareToIgnoreCase("true") == 0)) {
			setRepeatable(true);
		}else{
			setRepeatable(false);
		}
	}

	public int getMaxRepeats() {
		return maxRepeats;
	}

	public void setMaxRepeats(int maxRepeats) {
		this.maxRepeats = maxRepeats;
	}
	
	public void setMaxRepeats(String maxRepeats) {
		if (maxRepeats != null)
		{
			try {
				setMaxRepeats(Integer.parseInt(maxRepeats));
			}
			catch(Exception e) {
				setMaxRepeats(0);
			}
		}
	}

	public ArrayList<Prompt> getPrompts() {
		return prompts;
	}

	public void setPrompts(ArrayList<Prompt> prompts) throws Exception {
		if ((this.name == "") || (this.name == null)) {
			throw new Exception("Prompt group must have a name.");
		}
		for (Prompt p:prompts) {
			p.setGroupPromptName(this.name);
		}
		this.prompts = prompts;
	}

	public void addPrompt(Prompt prompt) throws Exception {
		if ((this.name == "") || (this.name == null)) {
			throw new Exception("Prompt group must have a name.");
		}
		prompt.setGroupPromptName(this.name);
		prompts.add(prompt);
	}
	
	public Map<String, Object> getInitializedMap() {
		Map<String, Object> initMap = new HashMap<String, Object>();
		for (Prompt prompt:getPrompts()) {
			initMap.put(prompt.getName(), "");
		}
		return initMap;
	}
	
	public void render(Shell shell, Integer repeatIndex) {
		for (Prompt prompt : prompts) 
		{
			prompt.setCurrentRepeat(repeatIndex);
			prompt.render(shell);
		}
	}
	
	public void render(Group group) {
		for (Prompt prompt : prompts) 
		{
			prompt.render(group);
		}
	}
}