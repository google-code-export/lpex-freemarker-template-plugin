package com.freemarker.lpex.FormDialogs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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

	public PromptGroup(ArrayList<Prompt> prompts) {
		setPrompts(prompts);
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
		if (repeatable.compareToIgnoreCase("true") == 0)
		{
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

	public void setPrompts(ArrayList<Prompt> prompts) {
		this.prompts = prompts;
	}

	public void addPrompt(Prompt prompt) {
		prompts.add(prompt);
	}
	
	public Map<String, Object> getInitializedMap() {
		Map<String, Object> initMap = new HashMap<String, Object>();
		for (Prompt prompt:getPrompts()) {
			//TODO Remove the value assigned here for testing
			initMap.put(prompt.getName(), prompt.getHint());
			//initMap.put(prompt.getName(), "");
		}
		return initMap;
	}
	
	public void render(Shell shell) {
		for (Prompt prompt : prompts) 
		{
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