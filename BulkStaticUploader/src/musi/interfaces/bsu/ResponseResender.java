package musi.interfaces.bsu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import musi.interfaces.bsu.pojo.Response;
import musi.interfaces.bsu.pojo.Security;

import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;


public class ResponseResender {
	String responsesDir;
	String equitySourceSystem;
	String bondSourceSystem;
	
	Logger log = Logger.getLogger(getClass());
	
	public static void main(String[] args) throws IOException {
		AbstractApplicationContext bulkStaticUploaderContext = new ClassPathXmlApplicationContext("spring-main-context.xml");
		BulkStaticUploader bsu = (BulkStaticUploader) bulkStaticUploaderContext.getBean("bulkStaticUploader");
		
		AbstractApplicationContext responseResenderContext = new ClassPathXmlApplicationContext("spring-response_resender-context.xml");
		ResponseResender rr = (ResponseResender) responseResenderContext.getBean("responseResender");
		
		rr.processResponses(bsu);
		
		bulkStaticUploaderContext.registerShutdownHook();
		responseResenderContext.registerShutdownHook();
		
		JOptionPane.showMessageDialog(null, "Complete");		
	}
	
	public void processResponses(BulkStaticUploader bsu) {
		File[] files = new File(responsesDir).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		
		BufferedReader br = null;
		Map<Security, Response> securityResponseInfo = new HashMap<Security, Response>();
		
		try {
			for (File file : files) {				
				String fileName = file.getName();
				
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				StringBuilder sb = new StringBuilder();
				String line = null;
				
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				
				String messageBody = sb.toString();
				
				br.close();

				String[] securityDetails = fileName.substring(0, fileName.indexOf(".")).split("_");
				String securityType = securityDetails[0];
				
				String sourceSystem = "";
				
				String murexResponse = null;
				
				try {
					if (securityType.equals("EQUITY")) {
						sourceSystem = equitySourceSystem;
					} else if (securityType.equals("BOND")){
						sourceSystem = bondSourceSystem;
					}
					
					bsu.sendStaticToMurex(messageBody, sourceSystem);
					
					murexResponse = bsu.readMurexResponse(60000);
					
					if (murexResponse == null) {
						log.warn("no response from Murex for security ");
						continue;
					}
					
					securityResponseInfo.putAll(bsu.processResponse(murexResponse, securityDetails));
					
				} catch (IOException ie) {
					ie.printStackTrace();
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}
			}
			bsu.generateResponseReport(securityResponseInfo);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getResponsesDir() {
		return responsesDir;
	}

	public void setResponsesDir(String responsesDir) {
		this.responsesDir = responsesDir;
	}

	public String getEquitySourceSystem() {
		return equitySourceSystem;
	}

	public void setEquitySourceSystem(String equitySourceSystem) {
		this.equitySourceSystem = equitySourceSystem;
	}

	public String getBondSourceSystem() {
		return bondSourceSystem;
	}

	public void setBondSourceSystem(String bondSourceSystem) {
		this.bondSourceSystem = bondSourceSystem;
	}

	public void destroy() {
		log.info("Bean will now destroy");
	}
}
