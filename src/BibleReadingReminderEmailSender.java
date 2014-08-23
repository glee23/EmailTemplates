
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

public class BibleReadingReminderEmailSender {
 
	public static void main(String[] args) throws IOException {
 
		if(args.length < 2) {
			throw new IllegalArgumentException("args[0] = gmail username, args[1] = gmail password");
		}
		
		final String username = args[0];
		final String password = args[1];
		int numSent = 0;
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
		
		String csvFile = "src/biblereadingreminder.txt";
		BufferedReader br = null;
		String line = "";
//		String cvsSplitBy = "\t";
	 
		try {
	 
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
	 
			        // use comma as separator
//				String[] parts = line.split(cvsSplitBy);
	 
				String email = line.trim();
				System.out.println(email);
				
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress("garrettlee23@gmail.com", "AAIV UW"));
				message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(email));
				message.setSubject("[aaiv] Daily Devotional Reminder");
				
				// Bundle the Soy files for your project into a SoyFileSet.
			    SoyFileSet sfs = new SoyFileSet.Builder().add(new File("src/biblereminder.soy")).build();

			    // Compile the template into a SoyTofu object.
			    // SoyTofu's newRenderer method returns an object that can render any template in the file set.
			    SoyTofu tofu = sfs.compileToTofu();

			    SoyTofu simpleTofu = tofu.forNamespace("bible");
							    
			    String html = simpleTofu.newRenderer(".email").render();
				
				message.setContent(html, "text/html");
				Transport.send(message);
	 
				System.out.println("Done");
				numSent++;
	 
			}
			
			System.out.println("Num Sent: " + numSent);
	 
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			br.close();
		}
 
	}
}