import java.util.Properties;    
import javax.mail.Message;   
import javax.mail.MessagingException;  
import javax.mail.PasswordAuthentication; 
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress; 
import javax.mail.internet.MimeMessage;   
  
class Mailer{  
    public static void sendEmail(String from,String password,String to,String sub,String msg){  
          
		  //Get properties object    
          Properties props = new Properties();  
          props.put("mail.transport.protocol", "smtp");	
          props.put("mail.smtp.starttls.enable","true"); 
		  
          props.put("mail.smtp.host", "smtp.gmail.com");   
		  props.put("mail.smtp.auth", "true");  
		  props.put("mail.smtp.port", "587"); 
		  
		 props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		 props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	  
//       props.put("mail.smtp.socketFactory.port", "587");    
//       props.put("mail.smtp.socketFactory.class",  "javax.net.ssl.SSLSocketFactory");    
//	     props.put("mail.smtp.ssl.enable", "true");  


  
          //get Session   
          Session session = Session.getInstance(props,    
           new javax.mail.Authenticator() {    
           protected PasswordAuthentication getPasswordAuthentication() {    
           return new PasswordAuthentication(from,password);  
           }    
          });    
		  session.setDebug(true);
		  
		  
		  System.out.println ("Session object created, composing Message...............");
          //compose message 
          try {    
           MimeMessage message = new MimeMessage(session);  
		   message.setFrom(new InternetAddress(from));
           message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));    
           message.setSubject(sub);    
           message.setText(msg);    
           //send message  
		   System.out.println ("Sending Message...............");
           Transport.send(message);    
		   System.out.println("Message sent successfully");    
          } catch (MessagingException e) {throw new RuntimeException(e);}    
             
    }  
}  

public class SendMailSSL{    
 public static void main(String[] args) {    
     //from,password,to,subject,message  
	 	String mailTo=System.getenv("Email_ID");
		String mailFrom=System.getenv("Email_ID");
		String password=System.getenv("Email_PWD");
		String subject="Send from Alert System";
		String message1="Hi, This is to notify that the Vehicle was exceeding the Speed Limit and CRASHED !!";
        
		Mailer.sendEmail(mailFrom,password,mailTo,subject,message1);  
     //change from, password and to  
 }    
}  