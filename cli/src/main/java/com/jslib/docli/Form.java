package com.jslib.docli;

import com.jslib.dospi.Flags;
import com.jslib.dospi.IForm;
import com.jslib.dospi.IFormData;

public class Form implements IForm {
	private Console console;

	public Form(Console console) {
		this.console = console;
	}

	@Override
	public IFormData submit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> void addField(String name, Class<T> type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void addField(String name, Flags flags, Class<T> type) {
		// TODO Auto-generated method stub
		
	}
}
