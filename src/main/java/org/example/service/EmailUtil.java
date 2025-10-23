package org.example.service;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class EmailUtil {

    private static final String MAIL_USER = System.getenv("MAIL_USER");
    private static final String MAIL_PASS = System.getenv("MAIL_PASS");

    public void sendReport(String recipientEmail, String subject, String content)
    {
        this.sendCoreEmail(recipientEmail, subject, content, null, false);
    }

    public void sendReport(String recipientEmail, String subject, String content, String filePath)
    {
        this.sendCoreEmail(recipientEmail, subject, content, filePath, true);
    }

    private void sendCoreEmail(String recipientEmail, String subject, String content, String filePath, boolean requiresAttachment) {

        if (MAIL_USER == null || MAIL_PASS == null)
        {
            System.err.println("❌ CRITICAL ERROR: MAIL_USER/MAIL_PASS environment variables are NOT set.");
            System.out.println("Email aborted. Please set the environment variables with your App Password.");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(MAIL_USER, MAIL_PASS);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAIL_USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);

            if (requiresAttachment && filePath != null)
            {
                File attachmentFile = new File(filePath);

                if (!attachmentFile.exists())
                {
                    System.err.println("❌ Attachment file not found at: " + filePath + ". Aborting email.");
                    return;
                }

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(content);

                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                attachmentBodyPart.attachFile(attachmentFile);
                attachmentBodyPart.setFileName(attachmentFile.getName());

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                multipart.addBodyPart(attachmentBodyPart);

                message.setContent(multipart);
                Transport.send(message);
                System.out.println("✅ Inventory Report successfully SENT to: " + recipientEmail);

            }
            else
            {
                message.setText(content);
                Transport.send(message);
                System.out.println("✅ OTP successfully SENT to: " + recipientEmail);
            }


        } catch (MessagingException e)
        {
            System.err.println("❌ Error sending email. Check App Password and network settings.");
            e.printStackTrace();
        } catch (IOException e)
        {
            System.err.println("❌ Error handling attachment file.");
            e.printStackTrace();
        }
    }
}