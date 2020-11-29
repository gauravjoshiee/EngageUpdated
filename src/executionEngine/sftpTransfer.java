package executionEngine;
import java.util.Iterator;
import java.util.Map;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class sftpTransfer {

static Session jschSession;
	
	private static ChannelSftp setupJsch() throws JSchException {
	    JSch jsch = new JSch();
	    jsch.setKnownHosts("/Users/john/.ssh/known_hosts");
	    String username="itr_sftp";
		String remoteHost="3.214.95.201";
		jschSession = jsch.getSession(username, remoteHost);
	    String password="LyricistExpansive!";
//	    jschSession.setPort(8999);
	    java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        jschSession.setConfig(config);
		jschSession.setPassword(password);
	    jschSession.connect();
	    return (ChannelSftp) jschSession.openChannel("sftp");
	}
	
	public static void transferToRemote(Map<String,String> fileList) throws JSchException, SftpException {
	    ChannelSftp channelSftp = setupJsch();
	    channelSftp.connect();
	    String remoteDir = "/QA/Outbound/MDM/";
	    Iterator<?> it = fileList.entrySet().iterator();
	    
	    while(it.hasNext()){
	    	@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
        	String localfileName = (String) pair.getKey();
        	String localFilePath = (String) pair.getValue();
        	try{
        		channelSftp.put(localFilePath, remoteDir + localfileName+".txt");
        		System.out.println("Successfully transferred file "+localfileName);
        	}catch(Exception e){
        		System.out.println("Unable to transfer file "+localfileName);
        	}
	    }
//	    String localFile = "src/main/resources/sample.txt";
//	    
//	  
//	    channelSftp.put(localFile, remoteDir + "jschFile.txt");
	    System.out.println("Connection successful");
	    channelSftp.disconnect();
	    channelSftp.exit();
	    tearDownJsch();
	}
	
	private static void tearDownJsch() {
		jschSession.disconnect();
	}
	
}
