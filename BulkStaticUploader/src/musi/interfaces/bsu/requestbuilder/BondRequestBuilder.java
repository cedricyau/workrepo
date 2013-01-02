package musi.interfaces.bsu.requestbuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import musi.interfaces.bsu.pojo.Security;
import musi.interfaces.bsu.utils.XMLUtils;

import org.apache.log4j.Logger;

public class BondRequestBuilder extends RequestBuilder {
	private String requestsDir;
	private Logger log = Logger.getLogger(getClass());
	
	public BondRequestBuilder(String requestsDir, String securityFile, String fieldFile){
		super(securityFile, fieldFile);
		this.requestsDir = requestsDir;
		
		addSecurities();
		addFields();
	}
	
	public void buildRequest() {
		log.info("Number of securities to process: " + securities.size());
		
		try {
			// Clean output directory
			File outputLocation = new File(requestsDir);
			File[] currentDirList = outputLocation.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.startsWith("BOND_");
				}
			});
			
			for (File file : currentDirList) {
				file.delete();				
			}
			
			OutputStreamWriter osw = null;
			StringBuilder xml = null;
			int securitiesCount = 0;
			
			try {
				for (Security security : securities) {
					
					String securityId = security.getId();
					String securityType = security.getIdType();
					
					if (securityType.equals("UNKNOWN")) {
						log.error("Invalid security type: " + securityType + " for security: " + securityId);
						continue;
					}
					
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
					xml.append("<code>").append(securityId).append("</code>");
					xml.append("<codeType>").append(securityType).append("</codeType>");
					xml.append("</security>");
					
					xml.append("</securities>");
					
					xml.append("</flexibleRequest>");
					xml.append("</BloombergInterfaceServerRequest>");
					
					osw = new OutputStreamWriter(new FileOutputStream(new File(requestsDir + File.separator + "BOND_" + securityType + "_" + securityId + ".xml")));
					osw.write(XMLUtils.prettyPrint(xml.toString()));
					osw.flush();
					securitiesCount++;
				}
				log.info("Number of securities processed: " + securitiesCount);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (osw != null) {
						osw.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}