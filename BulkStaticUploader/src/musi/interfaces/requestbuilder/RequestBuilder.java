package musi.interfaces.requestbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import musi.interfaces.utils.XMLUtils;

import org.apache.log4j.Logger;

public class RequestBuilder {
	private List<String> securities;
	private List<String> fields;
	private String outputDir;
	private Logger log = Logger.getLogger(getClass());
	
	public RequestBuilder(String outputDir){
		this.outputDir = outputDir;
	}
	
	public void buildRequest() {
		addSecurities();
		addFields();
		
		
		log.info("Number of securities to process: " + securities.size());
		
		try {
			// Clean output directory
			File outputLocation = new File(outputDir);
			File[] currentDirList = outputLocation.listFiles();
			for (File file : currentDirList) {
				file.delete();				
			}
			
			OutputStreamWriter osw = null;
			StringBuilder xml = null;
			int securitiesCount = 0;
			
			try {
				for (String security : securities) {
					
					String[] securityDetails = security.split("\\|");
					
					xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					xml.append("<BloombergInterfaceServerRequest type='flexibleRequest'>");
					xml.append("<flexibleRequest>");
		
					// Add fields
					Iterator<String> fieldIter = fields.iterator();
		
					xml.append("<fields>");
					while (fieldIter.hasNext()) {
						xml.append("<field>").append(fieldIter.next()).append("</field>");
					}
					xml.append("</fields>");
					
					// Add securities
					xml.append("<securities>");	
					
					xml.append("<security>");
					xml.append("<code>").append(securityDetails[0]).append("</code>");
					xml.append("<codeType>").append("ISIN").append("</codeType>");
					xml.append("</security>");
					
					xml.append("</securities>");
					
					xml.append("</flexibleRequest>");
					xml.append("</BloombergInterfaceServerRequest>");
					
					osw = new OutputStreamWriter(new FileOutputStream(new File(outputDir + File.separator + securityDetails[0] + ".xml")));
					osw.write(XMLUtils.prettyPrint(xml.toString()));
					osw.flush();
					securitiesCount++;
				}
				log.info("Number of securities processed: " + securitiesCount);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addSecurities() {
		InputStreamReader is = null;
		BufferedReader br = null;
		try {
			is = new InputStreamReader(new FileInputStream(new File("./SecurityFile.txt")));
			br = new BufferedReader(is);
			securities = new ArrayList<String>();
			String line;
			
			while ((line = br.readLine()) != null) {
				securities.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private void addFields() {
		InputStreamReader is = null;
		BufferedReader br = null;
		try {
			is = new InputStreamReader(new FileInputStream(new File("./FieldFile.txt")));
			br = new BufferedReader(is);
			fields = new ArrayList<String>();
			String line;
			
			while ((line = br.readLine()) != null) {
				fields.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}