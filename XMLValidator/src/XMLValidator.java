import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;


/**
 * A sample application which shows how to perform a
 * XML document validation.
 */

public class XMLValidator {
	public void validateXml(String schemaFilename) {
		try {
			// define the type of schema - we use W3C:
			String schemaLang = "http://www.w3.org/2001/XMLSchema";

			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

			// create schema by reading it from an XSD file:
			Schema schema = factory.newSchema(new StreamSource("schema/msml.xsd"));
			Validator validator = schema.newValidator();

			File sourceDir = new File("source");

			File[] files = sourceDir.listFiles();

			StringBuilder sb;
			String line;
			BufferedReader br;
			FileReader fr;
			
			for (File file : files) {
				fr = new FileReader(file);
				sb = new StringBuilder();
				br = new BufferedReader(fr);
				
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				
				String fileName = sb.substring(sb.indexOf("reference=\"") + "reference=\"".length(), sb.indexOf("\" action"));
				
				try {
					fr.close();
				    br.close();
					// at last perform validation:
				    validator.validate(new StreamSource(file));
				    
				    file.renameTo(new File("valid" + File.separator + fileName + ".xml"));
				} catch (SAXException ex) {
					// we are here if the document is not valid:
					System.out.println("Invalid: " + file.getName() + "\nError: " + ex.getMessage());
					file.renameTo(new File("invalid" + File.separator + fileName + ".xml"));
					continue;
				}
				System.out.println("Valid: " + fileName);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}