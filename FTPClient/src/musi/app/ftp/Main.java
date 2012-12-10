package musi.app.ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.log4j.*;

public class Main {
	
	private static final Logger log = Logger.getLogger(Main.class);
	private static Long lastModified = null;
	
	public static void main(String args[]) {
		FTPClient client = new FTPClient();
		String host = "";
		String user = "";
		String pass = "";
		String ftpType = "";
		String remoteDir = "";
		String localDir = "";
		int sleepDuration = 0;
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(new File("./etc/config.txt")));
			String line;
			
			String[] configs = new String[7];
			int index = 0;
			while ((line = br.readLine()) != null) {
				configs[index] = line;
				index++;
			}
			ftpType = configs[0];
			host = configs[1];
			user = configs[2];
			pass = configs[3];
			remoteDir = configs[4];
			localDir = configs[5];
			sleepDuration = Integer.valueOf(configs[6]);
			
			br.close();
		} catch (FileNotFoundException e1) {
			log.error("Cannot find config file");
		} catch (IOException e) {
			log.error("Cannot read from input file");
		}
		
		client.setHost(host);
		client.setUser(user);
		client.setPassword(pass);

		File fileDir = new File(localDir);
		
		if (ftpType.equalsIgnoreCase("upload")) {
			try {
				while (true) {
					File[] files = fileDir.listFiles(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							
							return (name.endsWith(".csv"));
						}
					});
					
					Arrays.sort(files, new Comparator<File>() {
						public int compare(File f1, File f2) {
							return Long.valueOf(((File) f1).lastModified()).compareTo(((File) f2).lastModified());
						}
					});
					
					log.info("Latest File is: " + files[files.length -1].getName());
					File latestFile = new File(files[files.length - 1].getPath());
					
					if ((lastModified == null) || (lastModified < latestFile.lastModified())) {
						lastModified = latestFile.lastModified();
//						for (File file : files) {
							client.setRemoteFile(remoteDir + "/" + latestFile.getName());
							boolean connected = client.connect();
							if (connected) {
								if (client.uploadFile(latestFile.getPath())) {
									log.info(client.getLastSuccessMessage());				
								} else {
									log.error(client.getLastErrorMessage());				
								}
							} else {
								log.error(client.getLastErrorMessage());
							}
//						}
					} else {
						log.info("No new files");
					}
//					log.info("Unlocking " + latestFile.getName());
					latestFile = null;
					log.info("Sleeping for " + sleepDuration + " seconds...");
					Thread.sleep(sleepDuration * 1000);
				}
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}
		} else {
		log.error(client.getLastErrorMessage());
		}
		System.exit(0);
	}
}