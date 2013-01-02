package musi.interfaces.bsu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import musi.interfaces.bsu.mq.MQAdapter;
import musi.interfaces.bsu.pojo.Response;
import musi.interfaces.bsu.pojo.Security;
import musi.interfaces.bsu.requestbuilder.BondRequestBuilder;
import musi.interfaces.bsu.requestbuilder.EquityRequestBuilder;
import musi.interfaces.bsu.requestbuilder.RequestBuilder;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class BulkStaticUploader {
	final String outputFileExtension = ".xml";
	
	String requestsDir;
	String responsesDir;
	String processedDir;
	String reportDir;
	String inputFileDir;
	String bondSecurityFile;
	String equitySecurityFile;
	String bondFieldFile;
	String equityFieldFile;
	String equitySourceSystem;
	String bondSourceSystem;
	
	MQAdapter murexMqAdapter;
	MQAdapter bondMqAdapter;
	MQAdapter equityMqAdapter;
	MQAdapter bulkSdrAdapter;
	
	Logger log = Logger.getLogger(getClass());
	
	public void process() {
		// Build requests
		RequestBuilder bondRequestBuilder = new BondRequestBuilder(requestsDir, bondSecurityFile, bondFieldFile);
		bondRequestBuilder.buildRequest();
		
		RequestBuilder equityRequestBuilder = new EquityRequestBuilder(requestsDir, equitySecurityFile, equityFieldFile);
		equityRequestBuilder.buildRequest();
		
		File[] files = new File(requestsDir).listFiles(new FilenameFilter() {
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
				
				String murexResponse = null;
				String staticData = null;
				String[] securityDetails = fileName.substring(0, fileName.indexOf(".")).split("_");
				String securityType = securityDetails[0];
				String securityId = securityDetails[2];
				
				log.info("processing security : " + securityId);
				log.debug("message is : " + messageBody);
				
				String sourceSystem = "";
				
				try {
					if (securityType.equals("EQUITY")) {
						sendEquityRequests(messageBody);
						staticData = readBondReplies();
						sourceSystem = equitySourceSystem;
					} else {
						sendBondRequests(messageBody);
						staticData = readBondReplies();
						sourceSystem = bondSourceSystem;
					}
					
					if (staticData == null) {
						log.info("no response from BSA for security : " + securityId);
						break;
					}
					
					dumpBSAResponse(staticData, fileName);

					sendStaticToMurex(staticData, sourceSystem);
					
					murexResponse = readMurexResponse(60000);
					
					if (murexResponse == null) {
						log.warn("no response from Murex for security : " + securityId);
						continue;
					}
					
					securityResponseInfo.putAll(processResponse(murexResponse, securityDetails));
					
					log.info("successfully uploaded security " + securityId + " to MurexSp");
					
					markSecurityAsProcessed(file);					
				} catch (IOException ie) {
					log.error(ie.getMessage());
				} catch (XPathExpressionException e) {
					log.error(e.getMessage());
				} catch (ParserConfigurationException e) {
					log.error(e.getMessage());
				} catch (SAXException e) {
					log.error(e.getMessage());
				}
			}
			generateResponseReport(securityResponseInfo);
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

	public void markSecurityAsProcessed(File requestFile) {
		File processedFile = new File(processedDir + "/" + requestFile.getName());
		
		if (processedFile.exists()) {
			processedFile.delete();
		}
		
		if (requestFile.renameTo(processedFile)) {
			log.info("successfully moved request to " + processedFile);
		} else {
			log.warn("unable to move request file: " + requestFile);
		
		}
	}
	
	public String readMurexResponse(long timeout) throws IOException {
		// Read replies from BSA
		return murexMqAdapter.readMessage(timeout);
	}
	
	public Map<Security, Response> processResponse(String murexResponse, String[] securityDetails) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		Security security = new Security();
		security.setIdType(securityDetails[0]);
		security.setId(securityDetails[2]);
		
		Map<Security, Response> securityResponseInfo = new HashMap<Security, Response>();
		
		String statusXPath = "/response/status";
		String descriptionXPath = "/response/description";
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(murexResponse)));
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = null;
		
		expr = xpath.compile(statusXPath);
		String status = (String) expr.evaluate(doc, XPathConstants.STRING);
		
		expr = xpath.compile(descriptionXPath);
		String description = (String) expr.evaluate(doc, XPathConstants.STRING);
		
		Response response = new Response(status, description);

		securityResponseInfo.put(security, response);
		
		log.info("Status: " + status);
		log.info("Description: " + description);
		
		return securityResponseInfo;
	}

	public void dumpBSAResponse(String messageBody, String fileName) throws IOException {
		Writer writer = null;
		BufferedWriter bw = null;
		
		try {
			writer = new FileWriter(new File(responsesDir + "/" + fileName));
			
			bw = new BufferedWriter(writer);
			
			bw.write(messageBody);
			bw.flush();			
		} finally {
			bw.close();
		}		
	}
	
	public void generateResponseReport(Map<Security, Response> securityResponseInfo) {
		DateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String date = sdf.format(new Date());
		
		File reportFile = new File("BulkStaticUploaderReport-" + date + ".csv");
		
		OutputStream fos = null;
		int failedSecurityCount = 0;
		int successfulSecurityCount = 0;
		
		try {
			fos = new FileOutputStream(new File(reportDir + "/" + reportFile));
			
			Iterator<Security> securityResponseInfoIter = securityResponseInfo.keySet().iterator();
			
			StringBuilder sb = new StringBuilder("SECURITY,TYPE,STATUS,DESCRIPTION");
			
			while (securityResponseInfoIter.hasNext()) {
				Security security = securityResponseInfoIter.next();
				Response responseInfo = securityResponseInfo.get(security);
				String securityId = security.getId();
				String securityType = security.getIdType();
				String status = responseInfo.getStatus();
				String description = responseInfo.getDescription();
				
				if (status.equalsIgnoreCase("FAILURE")) {
					failedSecurityCount++;
				} else {
					successfulSecurityCount++;
					continue;
				}
				
				sb.append("\n");
				sb.append(securityId);
				sb.append(",");
				sb.append(securityType);
				sb.append(",");
				sb.append(status);
				sb.append(",");
				sb.append(description);				
				
				fos.write(sb.toString().getBytes());
				
				sb = new StringBuilder();
			}
		} catch (IOException e) {
			log.info(e.getMessage());
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
		}		
		
		log.info("Number of failed securities: " + failedSecurityCount);
		log.info("Number of successful securities: " + successfulSecurityCount);
	}

	public void sendStaticToMurex(String staticData, String sourceSystem) throws IOException {
		murexMqAdapter.setSourceSystem(sourceSystem);
		
		// Send static to Murex
		murexMqAdapter.writeMessage(staticData);	
	}

	public void sendBondRequests(String messageBody) throws IOException {
		// Send requests to BSA
		bondMqAdapter.writeMessage(messageBody);
	}
	
	public String readBondReplies() throws IOException {
		// Read replies from BSA
		return bondMqAdapter.readMessage();
	}
	
	public void sendEquityRequests(String messageBody) throws IOException {
		// Send requests to Fidessa
		equityMqAdapter.writeMessage(messageBody);
	}
	
	public String readEquityReplies() throws IOException {
		// Read replies from Fidessa
		return equityMqAdapter.readMessage();
	}
	
	public String readBulkSecurityRequest() throws IOException {
		return bulkSdrAdapter.readMessage();
	}
	
	public void setBulkSdrMqAdapter(MQAdapter bulkSdrMqAdapter) {
		this.bulkSdrAdapter = bulkSdrMqAdapter;
	}
	
	public void setBondMqAdapter(MQAdapter bondMqAdapter) {
		this.bondMqAdapter = bondMqAdapter;
	}
	
	public void setEquityMqAdapter(MQAdapter equityMqAdapter) {
		this.equityMqAdapter = equityMqAdapter;
	}

	public void setMurexMqAdapter(MQAdapter murexMqAdapter) {
		this.murexMqAdapter = murexMqAdapter;
	}
	
	public void setRequestsDir(String requestsDir) {
		this.requestsDir = requestsDir;
	}
	
	public void setResponsesDir(String responsesDir) {
		this.responsesDir = responsesDir;
	}
	
	public void setProcessedDir(String processedDir) {
		this.processedDir = processedDir;
	}
	
	public void setReportDir(String reportDir) {
		this.reportDir = reportDir;
	}
	
	public void setInputFileDir(String inputFileDir) {
		this.inputFileDir = inputFileDir;
	}
	
	public void setBondSecurityFile(String bondSecurityFile) {
		this.bondSecurityFile = inputFileDir + bondSecurityFile;
	}
	
	public void setBondFieldFile(String bondFieldFile) {
		this.bondFieldFile = inputFileDir + bondFieldFile;
	}
	
	public void setEquitySecurityFile(String equitySecurityFile) {
		this.equitySecurityFile = inputFileDir + equitySecurityFile;
	}
	
	public void setEquityFieldFile(String equityFieldFile) {
		this.equityFieldFile = inputFileDir + equityFieldFile;
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
		log.info("Bean will destroy now.");
	}
}
