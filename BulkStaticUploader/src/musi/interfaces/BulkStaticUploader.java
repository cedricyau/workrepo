package musi.interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

import musi.interfaces.mq.MQAdapter;
import musi.interfaces.requestbuilder.RequestBuilder;
import musi.interfaces.utils.XMLUtils;

import org.apache.log4j.Logger;

public class BulkStaticUploader {
	String sourceSystem;
	String bsaRequestQueue;
	String bsaReplyQueue;
	String sdrQueue;
	String sdrReplyQueue;
	String requestsDir;
	
	MQAdapter murexMqAdapter;
	MQAdapter bsaMqAdapter;
	MQAdapter bulkSdrAdapter;
	
	Logger log = Logger.getLogger(getClass());
	
	public void process() {
		// Build requests
		RequestBuilder rb = new RequestBuilder(requestsDir);
		rb.buildRequest();
	
		File[] files = new File(requestsDir).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		
		BufferedReader br = null;
		
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
				String murexResponse = null;
				String staticData = null;
				String securityId = fileName.substring(0, fileName.indexOf("."));
				
				Security security = new Security();
				security.setId(securityId);
				
				log.info("processing security : " + securityId);
				log.debug("message is : " + XMLUtils.prettyPrint(messageBody));
				
				try {
					sendBSARequests(messageBody);
					staticData = readBSAReplies();
					if (staticData == null) {
						log.info("no response from BSA for security : " + securityId);
						break;
					}
					sendStaticToMurex(staticData);
//					murexResponse = readMurexResponse(60000);
//					if (murexResponse == null) {
//						log.warn("no response from Murex for security : " + securityId);
//						continue;
//					}
					
//					log.debug("murex response is : " + XMLUtils.prettyPrint(murexResponse));
					log.info("successfully uploaded security " + securityId + " to MurexSp");
//					log.info("security " + securityId + " sent to MurexSp");
				} catch (IOException ie) {
					ie.printStackTrace();
				}
			}			
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
	
	private String readMurexResponse(long timeout) throws IOException {
		// Read replies from BSA
		return murexMqAdapter.readMessage(timeout);
	}

	private void sendStaticToMurex(String staticData) throws IOException {
		// Send static to Murex
		murexMqAdapter.writeMessage(staticData);	
	}

	public void sendBSARequests(String messageBody) throws IOException {
		// Send requests to BSA
		bsaMqAdapter.writeMessage(messageBody);
	}
	
	public String readBSAReplies() throws IOException {
		// Read replies from BSA
		return bsaMqAdapter.readMessage();
	}
	
	public String readBulkSecurityRequest() throws IOException {
		return bulkSdrAdapter.readMessage();
	}
	
	public void setBulkSdrMqAdapter(MQAdapter bulkSdrMqAdapter) {
		this.bulkSdrAdapter = bulkSdrMqAdapter;
	}
	
	public void setBsaMqAdapter(MQAdapter bsaMqAdapter) {
		this.bsaMqAdapter = bsaMqAdapter;
	}

	public void setMurexMqAdapter(MQAdapter murexMqAdapter) {
		this.murexMqAdapter = murexMqAdapter;
	}
	
	public void setRequestsDir(String requestsDir) {
		this.requestsDir = requestsDir;
	}
	
	public void destroy() {
		System.out.println("Bean will destroy now.");
		System.exit(0);
	}
}
