package musi.app.ftp;

import java.net.*;
import java.io.*;

//import org.apache.log4j.*;

public class FTPClient {
	private URLConnection client;
	private String host;
	private String user;
	private String password;
	private String remoteFile;
	private String errorMessage;
	private String successMessage;
	
//	private static final Logger log = Logger.getLogger(FTPClient.class);
	
	public FTPClient(){}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	  /** Setter method for the remote file, this must include the sub-directory path relative
	   to the user’s home directory, e.g you’e going to download a file that is within a sub directory
	   called “sdir”, and the file is named “d.txt”, so you shall include the path as “sdir/d.txt”
	   **/
	public void setRemoteFile(String remFile) {
		this.remoteFile = remFile;
	}
	
	// The method that returns the last message of success of any method call
	public synchronized String getLastSuccessMessage() {
		if (successMessage == null) {
			return "";
		}
		return successMessage;
	}
	
	// The method that returns the last message of error resulted from any exception of any method call
	public synchronized String getLastErrorMessage() {
		if (errorMessage == null) {
			return "";
		}
		return errorMessage;
	}
	
	/** The method that handles file uploading, this method takes the absolute file path of a local file
	 * to be uploaded to the remote FTP server and the remote file will then be transferred to the FTP server 
	 * and saved as the relative path name specified in method setRemoteFile
	 * @param localFilename - the local absolute file name of the file in local hard drive that needs to FTP 
	 * over
	 */
	public synchronized boolean uploadFile(String localFilename) {
		try {
			InputStream is = new FileInputStream(localFilename);
			BufferedInputStream bis = new BufferedInputStream(is);
			OutputStream os = client.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);
			byte[] buffer = new byte[1024];
			int readCount;
			
			while ((readCount = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, readCount);
			}
			bos.close();
			bis.close();
			is.close();
			
			this.successMessage = "Uploaded " + localFilename;
//			log.info(successMessage);
			
			return true;
		} catch (Exception ex) {
			catchException(ex);
			
			return false;			
		}
	}
	
	/** The method to download a file and save it onto the local drive of the client 
	 * in the specified absolute path
	 * @param localFilename
	 */
	public synchronized boolean downloadFile(String localFilename) {
		try {
			InputStream is = client.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			OutputStream os = new FileOutputStream(localFilename);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			byte[] buffer = new byte[1024];
			int readCount;
			
			while((readCount = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, readCount);
			}
			bos.close();
			is.close(); // close the FTP inputstream
			this.successMessage = "Downloaded";
//			log.info(successMessage);
			
			return true;
		} catch (Exception ex) {
			catchException(ex);
			
			return false;
		}
	}
	
	// The method to connect to the remote FTP server
	public synchronized boolean connect() {
		try{
			URL url = new URL("ftp://" + user + ":" + password + "@" + host + "/" + remoteFile);
			client = url.openConnection();
			
			return true;
		} catch (Exception ex) {
			catchException(ex);
			
			return false;
		}
	}

	private void catchException(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		ex.printStackTrace(pw);
		errorMessage = sw.getBuffer().toString();
//		log.error(errorMessage);
	}
}
