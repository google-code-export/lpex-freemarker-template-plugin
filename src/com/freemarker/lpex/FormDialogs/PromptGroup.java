package com.freemarker.lpex.formdialogs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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

	public static Map<String, Object> getPromptGroupData(String promptGroup) {
		return (Map<String, Object>) LPEXTemplate.formData.get(promptGroup);
	}
	
	public static ArrayList<Map<String, Object>> getPromptGroupDataRepeatsArray(String promptGroup) {
		Map<String, Object> map = PromptGroup.getPromptGroupData(promptGroup);
		return (ArrayList<Map<String, Object>>) map.get("repeats");
	}
	
	public static Map<String, Object> getPromptGroupDataByRepeatIndex(String promptGroup, Integer index) {
		return (Map<String, Object>) getPromptGroupDataRepeatsArray(promptGroup).get(index);
	}
	
	public static Map<String, Object> getPrimaryPromptGroupData(String promptGroup) {
		return (Map<String, Object>) getPromptGroupDataRepeatsArray(promptGroup).get(0);
	}
	
	public static String getAllPromptValuesConcatenated(PromptGroup promptGroup) {
		String allValues = "";
		try {
			ArrayList<Map<String, Object>> repeats = getPromptGroupDataRepeatsArray(promptGroup.getName());
			for (Map<String, Object> map:repeats) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String key = entry.getKey();
					String value = (String)entry.getValue();
					Prompt p = promptGroup.getPromptByName(key);
					//Ignore the value if it is the same as the hint value
					if (!value.equalsIgnoreCase(p.getHint())) {
					    allValues += value;
					}
				}
			}
		} catch (Exception e) {}
		return allValues;
	}
	
	public static String getAllPromptValuesConcatenatedByIndex(PromptGroup promptGroup, Integer index) {
		String allValues = "";
		try {
			Map<String, Object> map = PromptGroup.getPromptGroupDataByRepeatIndex(promptGroup.getName(), index);
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = (String)entry.getValue();
				Prompt p = promptGroup.getPromptByName(key);
				//Ignore the value if it is the same as the hint value
				if (!value.equalsIgnoreCase(p.getHint())) {
				    allValues += value;
				}
			}
		} catch (Exception e) {}
		return allValues;
	}
	
	public Prompt getPromptByName(String name) {
		Prompt prompt = null;
		for (Prompt p:prompts) {
			if (p.getName().equalsIgnoreCase(name)) {
				return p;
			}
		}
		return prompt;
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