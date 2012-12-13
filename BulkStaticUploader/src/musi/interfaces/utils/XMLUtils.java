package musi.interfaces.utils;

import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XMLUtils
{
	public static String prettyPrint(String xml)
	{
		if (xml.isEmpty()) {
			throw new RuntimeException("xml was null or blank in prettyPrint()");
		}

		final StringWriter sw;
		 
		try
		{
			OutputFormat format = OutputFormat.createPrettyPrint();
			Document document = DocumentHelper.parseText(xml);
			sw = new StringWriter();
			final XMLWriter writer = new XMLWriter(sw, format);
	        writer.write(document);
		}
		catch (Exception e) {
			throw new RuntimeException("Error pretty printing xml:\n" + xml, e);
		}
		return sw.toString();
	}

	public static String removeAttributes(String xml)
	{
		Pattern p = Pattern.compile(" type=\"([a-zA-Z]*)\"");
		Matcher m = p.matcher(xml);
		StringBuffer sb = new StringBuffer(xml.length());

		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);

		return sb.toString();
	}
	
	public static String xmlEncode(String value) {
		return value.replace("<", "&lt;").replace(">", "&gt;").replace("'", "&quot;");
	}
}