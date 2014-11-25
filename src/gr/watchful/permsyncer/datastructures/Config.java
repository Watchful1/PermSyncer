package gr.watchful.permsyncer.datastructures;

public class Config {
	public String spreadsheetUrl;
	public String FTPUsername;
	public String FTBPassword;
	public String FTBServer;
	public boolean forceUpdate;

	public boolean init() {
		boolean changed = false;
		if(spreadsheetUrl == null) {
			changed = true;
			spreadsheetUrl = "urlhere";
		}
		if(FTPUsername == null) {
			changed = true;
			FTPUsername = "username";
		}
		if(FTBPassword == null) {
			changed = true;
			FTBPassword = "password";
		}
		if(FTBServer == null) {
			changed = true;
			FTBServer = "serveraddress";
		}
		return changed;
	}
}
