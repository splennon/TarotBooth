package org.overworld.tarotbooth.printer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.overworld.tarotbooth.model.Deck.Card;
import org.overworld.tarotbooth.model.GameModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.image.BitImageWrapper;
import com.github.anastaciocintra.escpos.image.Bitonal;
import com.github.anastaciocintra.escpos.image.BitonalThreshold;
import com.github.anastaciocintra.escpos.image.CoffeeImageImpl;
import com.github.anastaciocintra.escpos.image.EscPosImage;

import net.glxn.qrgen.javase.QRCode;

@Component
public class PrintService implements InitializingBean {

	@Value("${printerDevice}")
	private String printerDevice;

	@Value("${printCardRootDirectory}")
	private String printCardRootDirectory;
	
	@Value("${printCardQrPrefix}")
	private String printCardQrPrefix;
	
	@Value("${printerDisable}")
	private boolean printerDisable;
	
	@Autowired
	private GameModel gameModel;
	
	private OutputStream stream;

	public void printRun(){
		
		if (printerDisable) {
			System.out.println("Skipping print per application properties");
			return;
		}
		
		try {
			
			printHeader();
			
			try {
				Card past = gameModel.getPast().get();
				Card present = gameModel.getPresent().get();
				Card future = gameModel.getFuture().get();
				printCard("Your Past", past.name(), printCardRootDirectory + "/" + past.filename(), past.pastText());
				printCard("Your Present", present.name(), printCardRootDirectory  + "/"+ present.filename(), present.presentText());
				printCard("Your Future", future.name(), printCardRootDirectory + "/" + future.filename(), future.futureText());
				printVouchers(printCardQrPrefix + "p=" + past.cardId() + "&r=" + present.cardId() + "&f=" + future.cardId());
			} catch (NoSuchElementException e) {
				System.out.println("Missing a card, so skipping reading print");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printImage(String fileName) throws IOException {
		Bitonal algorithm = new BitonalThreshold(170);
		URL imageURL = PrintService.class.getResource(fileName);
		BufferedImage imageBufferedImage = ImageIO.read(imageURL);
		EscPosImage escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
		BitImageWrapper imageWrapper = new BitImageWrapper();
		EscPos escpos = new EscPos(stream);
		imageWrapper.setMode(BitImageWrapper.BitImageMode._8DotSingleDensity);
		escpos.write(imageWrapper, escposImage);
		escpos.close();
	}
	
	public void printBarcode(String content) throws IOException {
		ByteArrayOutputStream outStream = QRCode.from(content).stream();
		ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
		Bitonal algorithm = new BitonalThreshold(170);
		BufferedImage imageBufferedImage = ImageIO.read(inStream);
		EscPosImage escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
		BitImageWrapper imageWrapper = new BitImageWrapper();
		EscPos escpos = new EscPos(stream);
		imageWrapper.setMode(BitImageWrapper.BitImageMode._8DotSingleDensity);
		escpos.write(imageWrapper, escposImage);
		escpos.close();
	}
	
	public void printHeader() throws IOException {

		stream.write(Srp500Commands.INITIALISE);		
		
		printImage("mmeez.png");
		
		stream.write(Srp500Commands.COLOUR_RED);
		stream.write(Srp500Commands.DOUBLE_STRIKE(true));
		stream.write(Srp500Commands.JUSTIFY_CENTRE);
		stream.write(Srp500Commands.PRINT_MODE(true, true, true, true, true));
		stream.write("ezziestarot.com".getBytes());
		stream.write(Srp500Commands.COLOUR_BLACK);
		stream.write(Srp500Commands.DOUBLE_STRIKE(false));
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.LF);
	}
	
	public void printCard(String title, String cardName, String fileName, String description) throws IOException {
		
		stream.write(Srp500Commands.JUSTIFY_CENTRE);
		stream.write(Srp500Commands.COLOUR_BLACK);
		stream.write(Srp500Commands.PRINT_MODE(false, false, false, false, false));
		stream.write(title.getBytes());
		stream.write(Srp500Commands.JUSTIFY_LEFT);
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.LF);
		
		printImage(fileName);
		
		stream.write(Srp500Commands.PRINT_MODE(false, false, false, false, false));
		stream.write(Srp500Commands.LINE_SPACING((byte) 40));
		stream.write(description.getBytes());
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.LF);
	}
	
	public void printVouchers(String url) throws IOException {
	
		stream.write(Srp500Commands.COLOUR_BLACK);
		printImage("bog.png");
		
		stream.write(Srp500Commands.JUSTIFY_CENTRE);
		stream.write(Srp500Commands.LINE_SPACING((byte) 40));
		stream.write(Srp500Commands.COLOUR_RED);
		stream.write(Srp500Commands.DOUBLE_STRIKE(true));
		stream.write(Srp500Commands.PRINT_MODE(true, true, true, true, true));
		stream.write("10% OFF!".getBytes());
		stream.write(Srp500Commands.COLOUR_BLACK);
		stream.write(Srp500Commands.DOUBLE_STRIKE(false));
		stream.write(Srp500Commands.LF);
		
		stream.write(Srp500Commands.COLOUR_BLACK);
		stream.write(Srp500Commands.PRINT_MODE(false, false, true, false, false));
		
		stream.write(Srp500Commands.CUT_PAPER); /* Lands before Bog Beaches */
		
		stream.write("The Bog Beaches Luxury Champagne".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("Charcuterie Experience with".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("Historical Context Tour.".getBytes());
		
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.PRINT_MODE(false, false, false, false, false));
		stream.write("Sample the epicurian delights of the".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("local bog wildlife in our beautiful".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("new interpretative centre with".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("stunning views over the historic".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("Bog Beaches!".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write(("One per customer: Voucher " + (new Random()).nextInt(1300)).getBytes());
		stream.write(Srp500Commands.JUSTIFY_LEFT);
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.LF);

		/* Bandy Rectangle, O'Reilly's Pub */
		
		stream.write(Srp500Commands.JUSTIFY_CENTRE);
		stream.write(Srp500Commands.LINE_SPACING((byte) 40));
		stream.write(Srp500Commands.DOUBLE_STRIKE(true));
		stream.write(Srp500Commands.PRINT_MODE(true, true, true, true, true));
		stream.write(Srp500Commands.COLOUR_RED);
		stream.write("Visit O'Reilly's Pub".getBytes());
		stream.write(Srp500Commands.DOUBLE_STRIKE(false));
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.PRINT_MODE(false, false, false, false, false));
		stream.write(Srp500Commands.COLOUR_BLACK);
		stream.write("In the bustling Bandy Rectangle.".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("Free local craft beer with all main".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("courses, with voucher.".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.LF);

		/* Estralada */
		
		stream.write(Srp500Commands.CUT_PAPER); /* Lands before Bandy Rectangle */
		
		stream.write(Srp500Commands.JUSTIFY_CENTRE);
		stream.write(Srp500Commands.LINE_SPACING((byte) 40));
		stream.write(Srp500Commands.DOUBLE_STRIKE(true));
		stream.write(Srp500Commands.PRINT_MODE(true, true, true, true, true));
		stream.write(Srp500Commands.COLOUR_RED);
		stream.write("Free Crystal!".getBytes());
		stream.write(Srp500Commands.DOUBLE_STRIKE(false));
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.PRINT_MODE(false, false, false, false, false));
		stream.write(Srp500Commands.COLOUR_BLACK);
		stream.write("With Estralada's Crystal Healing".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("Energetic Alignment session!".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("Monday to Thursday by appointment.".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("One per customer, seconds only.".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.LF);

		/* AI Reading */
		
		stream.write(Srp500Commands.JUSTIFY_CENTRE);
		stream.write(Srp500Commands.LINE_SPACING((byte) 40));
		stream.write(Srp500Commands.DOUBLE_STRIKE(true));
		stream.write(Srp500Commands.PRINT_MODE(true, true, true, true, true));
		stream.write(Srp500Commands.COLOUR_RED);
		
		stream.write(Srp500Commands.CUT_PAPER); /* Lands before Free Crystals */
		
		stream.write("Free AI Reading".getBytes());
		stream.write(Srp500Commands.DOUBLE_STRIKE(false));
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.PRINT_MODE(false, false, false, false, false));
		stream.write(Srp500Commands.COLOUR_BLACK);
		stream.write(Srp500Commands.LF);
		stream.write("Scan the \"QR Code\" below for".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("a free reading by ChatGPT!".getBytes());
		stream.write(Srp500Commands.LF);
		stream.write("Valid for 24 hours!".getBytes());
		stream.write(Srp500Commands.LF);

		stream.write(Srp500Commands.CUT_PAPER); /* Lands before AI Reading */
		
		printBarcode(url);
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.JUSTIFY_CENTRE);
		stream.write(Srp500Commands.LINE_SPACING((byte) 40));
		stream.write(Srp500Commands.DOUBLE_STRIKE(true));
		stream.write(Srp500Commands.PRINT_MODE(true, true, true, true, true));
		stream.write(Srp500Commands.COLOUR_RED);
		stream.write("NOW GET OUT!".getBytes());
		stream.write(Srp500Commands.DOUBLE_STRIKE(false));
		stream.write(Srp500Commands.LF);
		stream.write(Srp500Commands.PRINT_MODE(false, false, false, false, false));
		stream.write(Srp500Commands.COLOUR_BLACK);
		
		stream.write(Srp500Commands.FEED_AND_CUT);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Map<Integer, PrinterStatus> s = new HashMap<>();
		s.put(3, PrinterStatus.OFFLINE);
		s.put(5, PrinterStatus.COVER_OPEN);
		s.put(6, PrinterStatus.PAPER_MANUAL_FEED);
		s.put(10, PrinterStatus.MECHANICAL_ERROR);
		s.put(11, PrinterStatus.AUTOCUTTER_ERROR);
		s.put(13, PrinterStatus.UNRECOVERABLE_ERROR);
		s.put(14, PrinterStatus.AUTORECOVERABLE_ERROR);
		s.put(16, PrinterStatus.PAPER_LOW);
		s.put(17, PrinterStatus.PAPER_LOW);
		s.put(18, PrinterStatus.PAPER_OUT);
		s.put(19, PrinterStatus.PAPER_OUT);

		try {
			SerialPrinter ss = new SerialPrinter(printerDevice, "SRP-500", 0xFF, s); //TODO
			stream = ss.getOutputStream();
		} catch (SerialPortInvalidPortException e) {
			if (printerDisable) {
				System.err.println("Unable to initialise device: " + printerDevice + ", ignoring as printer disabled");
			} else {
				throw e;
			}
		}
	}
}
