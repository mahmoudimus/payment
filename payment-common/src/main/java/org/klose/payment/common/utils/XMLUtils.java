package org.klose.payment.common.utils;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XMLUtils {
	public static XMLWriter createPrettyFormatXMLWriter(OutputStream bos)
			throws UnsupportedEncodingException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		format.setExpandEmptyElements(true);
		format.setTrimText(false);
		format.setIndent(" ");
		XMLWriter writer = new XMLWriter(bos, format);
		return writer;
	}

	public static Element addElement(Element parentElement, String elementName, String text) {
		Element newElement = parentElement.addElement(elementName);
		if (text != null && text.trim().length() > 0) {
			newElement.addText(text);
		}
		return newElement;
	}

	public static String extract(Element element, String xpath) {
		Element node = (Element) element.selectSingleNode(xpath);
		if (node != null) {
			return node.getTextTrim();
		}
		return null;
	}
	
	
	public static Map parseToMap(String strxml, String encoding) throws JDOMException, IOException {
		//strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

		if(null == strxml || "".equals(strxml)) {
			return null;
		}
		
		if(encoding == null || encoding.isEmpty()) {
			encoding = "UTF-8";
		}
		Map m = new HashMap();
		
		InputStream in = new ByteArrayInputStream(strxml.getBytes(encoding));
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		org.jdom.Element root = (org.jdom.Element) doc.getRootElement();
		List list = root.getChildren();
		Iterator it = list.iterator();
		while(it.hasNext()) {
			org.jdom.Element e = (org.jdom.Element) it.next();
			String k = e.getName();
			String v = "";
			List children = e.getChildren();
			if(children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = getChildrenText(children);
			}
			
			m.put(k, v);
		}
	
		in.close();
		
		return m;
	}
	
	public static String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if(!children.isEmpty()) {
			Iterator it = children.iterator();
			while(it.hasNext()) {
				org.jdom.Element e = (org.jdom.Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if(!list.isEmpty()) {
					sb.append(getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}
		
		return sb.toString();
	}
}
