import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.io.File;

public class EmailSender {
    private static final String host = "smtp.gmail.com";
    private static final String port = "587";
    private static final String senderEmail = "elforegaming123@gmail.com";
    private static final String password = "refu jsfg oihi qyji";

    public static void sendEmail(String recipientEmail, String subject, String bodyText) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);

        // Starts by creating a session
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setText(bodyText);

            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(mimeBodyPart);

            // Message contain headers and content
            // This will set the content of the message
            message.setContent(mimeMultipart);

            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmail(String recipientEmail, String subject, String bodyText, String attachmentPath) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);

        // Create a session
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);

            // Body part
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setText(bodyText);

            // Attachment part
            MimeBodyPart attachmentPart = new MimeBodyPart();
            File file = new File(attachmentPath);
            if (file.exists()) {
                attachmentPart.attachFile(file);
            } else {
                System.out.println("Attachment file not found: " + attachmentPath);
                return;
            }

            // Multipart (body + attachment)
            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(mimeBodyPart);
            mimeMultipart.addBodyPart(attachmentPart);

            message.setContent(mimeMultipart);

            // Send email
            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}