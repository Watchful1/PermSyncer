package gr.watchful.permsyncer.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FTPUtils {
	public static boolean upload(String user, String pass, String server, File local, String remoteLocation) {
		FTPClient ftpClient = new FTPClient();
		boolean done = false;
		try {
			ftpClient.connect(server);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();

			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			InputStream inputStream = new FileInputStream(local);
			done = ftpClient.storeFile(remoteLocation, inputStream);
			inputStream.close();
		} catch (IOException ex) {
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return done;
		}
	}
}
