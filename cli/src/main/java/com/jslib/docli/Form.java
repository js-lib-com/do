package com.jslib.docli;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.converter.Converter;
import com.jslib.converter.ConverterException;
import com.jslib.converter.ConverterRegistry;
import com.jslib.dospi.Flags;
import com.jslib.dospi.IForm;
import com.jslib.dospi.IFormData;
import com.jslib.util.Strings;
import com.jslib.util.Types;

public class Form implements IForm {
	private static final Log log = LogFactory.getLog(Form.class);

	private final Converter converter;
	private final Console console;
	private final List<Field<?>> fields;

	private String legend;

	public Form(Console console) {
		this.converter = ConverterRegistry.getConverter();
		this.console = console;
		this.fields = new ArrayList<>();
	}

	@Override
	public void setLegend(String legend) {
		this.legend = legend;
	}

	@Override
	public <T> void addField(String name, Class<T> type) {
		addField(name, Flags.MANDATORY, type);
	}

	@Override
	public <T> void addField(String name, Flags flags, Class<T> type) {
		fields.add(new Field<T>(name, flags, type));
	}

	@Override
	public IFormData submit() {
		FormData data = new FormData();
		console.println(legend);

		for (Field<?> field : fields) {
			Object value = null;
			while (value == null) {
				String input = console.input(field.getName());

				if (input.isEmpty()) {
					if (field.getFlags() != Flags.OPTIONAL) {
						continue;
					}
					data.put(field.getName(), null);
					break;
				}

				try {
					value = converter.asObject(input, field.getType());
				} catch (ConverterException e) {
					log.warn(invalidValue(field.getType(), input, e));
					continue;
				}
				data.put(field.getName(), value);
			}
		}

		return data;
	}

	private static String invalidValue(Class<?> type, String value, ConverterException e) {
		if (Types.isEnum(type)) {
			return String.format("Invalid value '%s'. Should be one of: %s", value, Strings.join(type.getEnumConstants(), ", "));
		}
		return String.format("Invalid value '%s': %s", value, e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
	}

	private static class Field<T> {
		private final String name;
		private final Flags flags;
		private final Class<T> type;

		public Field(String name, Flags flags, Class<T> type) {
			this.name = name;
			this.flags = flags;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public Flags getFlags() {
			return flags;
		}

		public Class<T> getType() {
			return type;
		}
	}

	private static class FormData implements IFormData {
		private final Map<String, Object> data;

		public FormData() {
			this.data = new TreeMap<>();
		}

		public void put(String name, Object value) {
			data.put(name, value);
		}

		@Override
		public Iterator<String> iterator() {
			return data.keySet().iterator();
		}

		@Override
		public String get(String fieldName) {
			return get(fieldName, String.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T get(String fieldName, Class<T> type) {
			Object value = data.get(fieldName);
			if (!Types.isInstanceOf(value, type)) {
				throw new ClassCastException(String.format("Field %s is not of type %s.", fieldName, type));
			}
			return (T) value;
		}
	}
}
