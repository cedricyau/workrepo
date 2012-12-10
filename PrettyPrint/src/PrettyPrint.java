import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class PrettyPrint { 
	
	public void process() {
		File inputDir = new File("input");
		
		File[] files = inputDir.listFiles();
		
		
		for (File file : files) {			
			StringBuffer fileData = new StringBuffer(1000);
			
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				char[] buf = new char[1024];
				int numRead=0;
				
				while ((numRead = reader.read(buf))!=-1) {
					fileData.append(buf, 0, numRead);
				}

				reader.close();
				
			
				String xml = fileData.toString();
				xml = xml.replace("", "").replace("", "");
				
			    final StringWriter sw;
		
		        final OutputFormat format = OutputFormat.createPrettyPrint();
		        final org.dom4j.Document document = DocumentHelper.parseText(xml);
		        sw = new StringWriter();
		        final XMLWriter writer = new XMLWriter(sw, format);
		        writer.write(document);
		    } catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
		        e.printStackTrace();
		    }
			    
		    try {
				PrintWriter pw = new PrintWriter(new File("output/outputFile.xml"));
				pw.flush();
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		PrettyPrint pp = new PrettyPrint();
		pp.process();
	}
}