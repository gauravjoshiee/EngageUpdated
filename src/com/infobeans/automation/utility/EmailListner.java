package com.infobeans.automation.utility;

import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import javax.mail.internet.MimeMultipart;
import javax.mail.search.AndTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import com.infobeans.automation.config.Constants;
import com.infobeans.automation.config.EmailSettings;
import com.infobeans.automation.core.DriverMembers;

@SuppressWarnings("static-access")
public class EmailListner {
	
	public static Message [] messages;
	public static String expectedSubject;
	public static String expectedRecipient;

	public static void sendMail(){
		// Create object of Property file
				Properties props = new Properties();
		 
				// this will set host of server- you can change based on your requirement 
				props.put("mail.smtp.host", "smtp.gmail.com");
		 
				// set the port of socket factory 
				props.put("mail.smtp.socketFactory.port", "465");
		 
				// set socket factory
				props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		 
				// set the authentication to true
				props.put("mail.smtp.auth", "true");
		 
				// set the port of SMTP server
				props.put("mail.smtp.port", "465");
				
				props.put("mail.smtp.connectiontimeout", "5000");
                props.put("mail.smtp.timeout", "5000");
				
				// This will handle the complete authentication
				Session session = Session.getDefaultInstance(props,
		 
						new javax.mail.Authenticator() {
		 
							protected PasswordAuthentication getPasswordAuthentication() {
		 
							return new PasswordAuthentication(EmailSettings.sendUserName, EmailSettings.sendUserPassword);
		 
							}
		 
						});
				try {
					 
					// Create object of MimeMessage class
					Message message = new MimeMessage(session);
		 
					// Set the from address
					message.setFrom(new InternetAddress(EmailSettings.emailReportFrom));
		 
					// Set the recipient address
					message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(EmailSettings.emailReportTo));
		            
		                        // Add the subject link
					message.setSubject("Automation_Report - "+Constants.SUITE_NAME+" - "+ExcelUtils.getDate("yyyy_MM_dd_hh-mm"));
		 
					// Create object to add multimedia type content
					BodyPart messageBodyPart1 = new MimeBodyPart();
		 
					// Set the body of email
					messageBodyPart1.setText("This is message body");
		 
					// Create another object to add another content
					MimeBodyPart messageBodyPart2 = new MimeBodyPart();
		 
					// Mention the file which you want to send
					String filePath = Reporting.reportPath;
					String filename = ("Automation_Report_"+Constants.SUITE_NAME+"_"+ExcelUtils.getDate("yyyy_MM_dd_hh-mm")+".html");
		 
					// Create data source and pass the filename
					DataSource source = new FileDataSource(filePath);
		 
					// set the handler
					messageBodyPart2.setDataHandler(new DataHandler(source));
		 
					// set the file
					messageBodyPart2.setFileName(filename);
		 
					// Create object of MimeMultipart class
					Multipart multipart = new MimeMultipart();
		 
					// add body part 1
					multipart.addBodyPart(messageBodyPart2);
		 
					// add body part 2
					multipart.addBodyPart(messageBodyPart1);
		 
					// set the content
					message.setContent(multipart);
		 
					// finally send the email
					Transport.send(message);
		 
					System.out.println("=====Email Sent=====");
		 
				} catch (MessagingException e) {
		 
					throw new RuntimeException(e);
		 
				}
	}		

	public static void getMail(DriverMembers obj) 
		   {
		      try {
		    	  String host = obj.xlObj.getRunConfig("mailBoxHost");
		    	  String storeType = obj.xlObj.getRunConfig("mailBoxStoreType");
		    	  String port = obj.xlObj.getRunConfig("mailBoxPort");
		    	  String username = obj.xlObj.getRunConfig("mailBoxUser");
		    	  String password = obj.xlObj.getRunConfig("mailBoxPassword");
		      //create properties field
		      Properties props = new Properties();

		      props.put("mail.pop3.host", host);
		      props.put("mail.pop3.port", port);
		      props.put("mail.pop3.starttls.enable", "true");
		      props.put("mail.pop3.connectiontimeout", "25000");
		      Session emailSession = Session.getInstance(props);
		  
		      //create the POP3 store object and connect with the pop server
		      Store store = emailSession.getStore(storeType);
		      store.connect(host, username, password);

		      //create the folder object and open it
		      Folder emailFolder = store.getFolder("INBOX");
		      
		      
		      Folder[] f = store.getDefaultFolder().list();
		      for(Folder fd:f)
		          System.out.println(">> "+fd.getName());
		      
		      emailFolder.open(Folder.READ_WRITE);
		      
		      
		     
		      // retrieve the messages from the folder in an array and print it
		      expectedSubject = (obj.sPageData.trim()+" "+obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, obj.sAppender, obj));
		      SearchTerm term = new SubjectTerm(expectedSubject);
		      System.out.println("Email folder message count - "+emailFolder.getMessageCount());
		      messages = emailFolder.search(term);
		      
		      
		      System.out.println("messages.length---" + messages.length);
		      
		      getEmailWithSubject(obj);

		      //close the store and folder objects
		      emailFolder.close(false);
		      store.close();

		      } catch (NoSuchProviderException e) {
		         e.printStackTrace();
		      } catch (MessagingException e) {
		         e.printStackTrace();
		      } catch (Exception e) {
		         e.printStackTrace();
		      }
		   }
	
	
	private static void getEmailWithSubject(DriverMembers obj){
			String emailSubject;
			String emailBody;
			try {
				expectedRecipient = obj.xlObj.getRunConfig(obj.sPageObject);
				for (int i = 0, n = messages.length; i < n; i++) {
				  Message message = messages[i];
				  Address[] recipient=message.getRecipients(RecipientType.TO);
				  System.out.println(message.getSubject());
			
				for(int j=0;j<recipient.length;j++){
					if (recipient[j].toString().equals(expectedRecipient)){
						emailSubject = message.getSubject();
						System.out.println(emailSubject);
						if(emailSubject.equals(expectedSubject)){
							emailBody=message.getContent().toString();
							System.out.println("Found email with specifed subject to specified user");
							System.out.println(emailBody);
						}
			         }
				}
			
			  }
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Message found but eeror in retriving message body");
			e.printStackTrace();
		} 
	}
	
}
