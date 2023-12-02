package com.example.wander_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.example.wander_app.models.LocationPDF;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailAsyncSender extends AsyncTask<Void, Void, Boolean> {
    private Context cont;
    private Session session;
    private String senderEmail = "wanderapp.sjsu@gmail.com";
    private String appSenderPassword = "nkjj rilx kixp llbl";
    private String recipientEmail;
    private List<ItineraryItem> locations;
    private String destination;

    public EmailAsyncSender(Context cont, String recipient, List<ItineraryItem> locations, String destination) {
        this.recipientEmail = recipient;
        this.cont = cont;
        this.locations = locations;
        this.destination = destination;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.v("EmailAsync", "Starting send email...");
        try {
            String pdfFilePath = createPDF();
            session = getConfiguredSession();
            MimeMessage message = createMimeMessage(pdfFilePath);
            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Session getConfiguredSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, appSenderPassword);
                    }
                });
    }

    private String createPDF() {
        String pdfFilePath = cont.getExternalFilesDir(null) + "/WanderItinerary.pdf";

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();

            Paragraph title = new Paragraph("Wander Itinerary");
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph title2 = new Paragraph("Trip to " + destination + "!");
            title2.setAlignment(Element.ALIGN_CENTER);
            document.add(title2);


            // Add locations
            for (ItineraryItem location : locations) {
                addLocationToPDF(document, location);
            }

            document.close();

            return pdfFilePath;
        } catch (Exception e) {
            Log.e("EmailPDFAsyncTask", "Error creating PDF", e);
            return null;
        }
    }

    private void addLocationToPDF(Document document, ItineraryItem location) throws DocumentException, IOException {
        document.add(new Paragraph("\n"));
        document.add(new Paragraph(location.getLocationName()));
        document.add(new Paragraph(location.getDescription()));
        document.add(new Paragraph(location.getAddress()));

        String pic = location.getImgUrl();
        Bitmap bitmap = downloadImage(cont, pic);

        if (bitmap != null) {
            Image image = Image.getInstance(bitmapToByteArray(bitmap));
            document.add(image);
        }
    }

    private static Bitmap downloadImage(Context context, String url) {
        try {
            FutureTarget<Bitmap> futureTarget = Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .submit();

            return futureTarget.get();
        } catch (Exception e) {
            Log.e("EMAILASYNC", "Error downloading image", e);
            return null;
        }
    }

    private static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private MimeMessage createMimeMessage(String pdfFilePath) throws MessagingException, IOException {
        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));

        Multipart multipart = new MimeMultipart();
        BodyPart bodyPart = new MimeBodyPart();
        message.setSubject("Wander Itinerary");

        bodyPart.setText("Please find the attached Wander Itinerary PDF!");
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(new File(pdfFilePath));
        multipart.addBodyPart(bodyPart);
        multipart.addBodyPart(attachmentPart);
        message.setContent(multipart);

        return message;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(cont,"Email Sent!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(cont,"Email Failed to Send!", Toast.LENGTH_SHORT).show();
        }
    }
}
