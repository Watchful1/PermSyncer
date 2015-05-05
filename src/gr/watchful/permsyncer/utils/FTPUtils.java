package gr.watchful.permsyncer.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FTPUtils {
	public static boolean uploadFTP(String user, String pass, String server, File local, String remoteLocation) {
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
	public static boolean uploadSFTP(String user, String pass, String server, File local, String remoteLocation) {
		JSch jsch = new JSch();
		Session session = null;
		Channel channel = null;
		ChannelSftp sftpChannel = null;
		boolean done = false;
		try {
			session = jsch.getSession(user, server, 22);
			session.setPassword(pass);

			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			channel = session.openChannel("sftp");
			channel.setInputStream(System.in);
			channel.setOutputStream(System.out);
			channel.connect();
			sftpChannel = (ChannelSftp) channel;

			byte[] bufr = new byte[(int) local.length()];
			FileInputStream fis = new FileInputStream(local);
			fis.read(bufr);
			ByteArrayInputStream fileStream = new ByteArrayInputStream(bufr);
			sftpChannel.put(fileStream, remoteLocation);
			fileStream.close();
			done = true;
		} catch (IOException ex) {
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		} finally {
				if (sftpChannel != null) {
					sftpChannel.exit();
				}
				if (channel != null) {
					channel.disconnect();
				}
				if (session != null) {
					session.disconnect();
				}
			return done;
		}
	}

}
