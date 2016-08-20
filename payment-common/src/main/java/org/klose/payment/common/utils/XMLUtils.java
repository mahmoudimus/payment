package org.klose.payment.common.utils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLUtils {
//	public static XMLWriter createPrettyFormatXMLWriter(OutputStream bos)
//			throws UnsupportedEncodingException {
//		OutputFormat format = OutputFormat.createPrettyPrint();
//		format.setEncoding("UTF-8");
//		format.setExpandEmptyElements(true);
//		format.setTrimText(false);
//		format.setIndent(" ");
//		XMLWriter writer = new XMLWriter(bos, format);
//		return writer;
//	}
//
//	public static Element addElement(Element parentElement, String elementName, String text) {
//		Element newElement = parentElement.addElement(elementName);
//		if (text != null && text.trim().length() > 0) {
//			newElement.addText(text);
//		}
//		return newElement;
//	}
//
//	public static String extract(Element element, String xpath) {
//		Element node = (Element) element.selectSingleNode(xpath);
//		if (node != null) {
//			return node.getTextTrim();
//		}
//		return null;
//	}
	
	@SuppressWarnings("unchecked")
	public static Map parseToMap(String strxml, String encoding) throws JDOMException, IOException {
		//strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

		if(null == strxml || "".equals(strxml)) {
			return null;
		}
		
		if(encoding == null || encoding.isEmpty())
			encoding = "UTF-8";
		Map m = new HashMap();
		
		InputStream in = new ByteArrayInputStream(strxml.getBytes(encoding));
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		for (Object aList : list) {
			org.jdom.Element e = (org.jdom.Element) aList;
			String k = e.getName();
			String v;
			List children = e.getChildren();
			if (children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = getChildrenText(children);
			}

			m.put(k, v);
		}
	
		in.close();
		
		return m;
	}
	
	private static String getChildrenText(List children) {
		StringBuilder sb = new StringBuilder();
		if(!children.isEmpty()) {
			for (Object aChildren : children) {
				org.jdom.Element e = (org.jdom.Element) aChildren;
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<").append(name).append(">");
				if (!list.isEmpty()) {
					sb.append(getChildrenText(list));
				}
				sb.append(value);
				sb.append("</").append(name).append(">");
			}
		}
		
		return sb.toString();
	}
}
