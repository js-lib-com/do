package com.jslib.docore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.xml.xpath.XPathExpressionException;

import com.jslib.api.dom.Document;
import com.jslib.api.dom.DocumentBuilder;
import com.jslib.api.dom.Element;
import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.lang.BugError;
import com.jslib.util.Classes;

public abstract class XMLFile {
	private static final Log log = LogFactory.getLog(XMLFile.class);

	private final DocumentBuilder documentBuilder;
	protected final Document document;

	protected XMLFile(String root, InputStream inputStream) {
		log.trace("XMLFile(root, inputStream)");

		this.documentBuilder = Classes.loadService(DocumentBuilder.class);

		Document document;
		try {
			document = documentBuilder.loadXML(inputStream);
		} catch (Exception remoteException) {
			log.error(remoteException);
			try {
				log.debug("Cannot load XML file. Create empty %s document.", root);
				document = documentBuilder.loadXML(new EmptyStream(root));
			} catch (Exception localException) {
				log.error(localException);
				// SAX or IO exception on hard coded string reading
				throw new BugError(localException);
			}
		}
		this.document = document;
	}

	public String property(String... tagsPath) {
		return text(tagsPath);
	}

	protected String text(String... tagsPath) {
		return text(document.getRoot(), tagsPath);
	}

	protected String text(Element element, String... tagsPath) {
		for (String tagName : tagsPath) {
			element = getChildElement(element, tagName);
			if (element == null) {
				return null;
			}
		}
		return element.getText().trim();
	}

	protected <T> T getByTagName(String tagName, Function<Element, T> handler) {
		Element element = getChildElement(document.getRoot(), tagName);
		if (element == null) {
			return null;
		}
		return handler.apply(element);
	}

	protected <T> List<T> findByTagsPath(String tagsPath, Function<Element, T> handler) {
		List<T> list = new ArrayList<>();
		String[] tagNames = tagsPath.split("\\.");

		Element element = document.getRoot();
		for (int i = 0; i < tagNames.length - 1; ++i) {
			element = getChildElement(element, tagNames[i]);
			if (element == null) {
				return list;
			}
		}

		element.findByTag(tagNames[tagNames.length - 1]).forEach(dependency -> {
			T t = handler.apply(dependency);
			if (t != null) {
				list.add(t);
			}
		});
		return list;
	}

	private static Element getChildElement(Element element, String tagName) {
		try {
			return element.getByXPath("child::%s", tagName);
		} catch (XPathExpressionException e) {
			return null;
		}
	}

	/** Empty POM XML stream. */
	private static class EmptyStream extends InputStream {
		private final ByteArrayInputStream content;

		public EmptyStream(String root) {
			this.content = new ByteArrayInputStream(String.format("<%1$s></%1$s>", root).getBytes());
		}

		@Override
		public int read() throws IOException {
			return content.read();
		}
	}
}
