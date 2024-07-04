package org.overworld.tarotbooth.printer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Represents an ESC/POS generic printer connected to a serial port (Including
 * serial over USB).
 */
@Slf4j
public class SerialPrinter extends SubmissionPublisher<PrinterStatus> {

	public static final int GS = 29;

	private volatile boolean finished;
	private String printerName;
	private final SerialPort comPort;
	private Map<Integer, PrinterStatus> statusMapping;
	private BitSet rawStatus = new BitSet(32);
	private boolean xoff = false;

	public boolean getXoff() {
		return this.xoff;
	}

	/**
	 * Get the map of all errors returned by the printer.
	 *
	 * @return Status map
	 */
	public List<PrinterStatus> getErrors() {

		List<PrinterStatus> result = new ArrayList<>();

		int ord = 0;
		while ((ord = rawStatus.nextSetBit(ord)) != -1) {
			if (statusMapping.containsKey(ord)) {
				result.add(statusMapping.get(ord));
			} else {
				log.trace(String.format("Unknown error bit at position %s in status for printer %s", ord, printerName));
			}
			ord++;
		}
		return result;
	}

	/**
	 * @param portDescriptor "COM[*]" or "/dev/tty[*]" identifier
	 * @param printerName    a friendly name for the printer for log messages
	 * @param statusHex      the byte to send to the printer to request status,
	 *                       usually 0xFF but some printers use this to influence
	 *                       the conditions checked
	 * @param statusMapping  a map of error conditions by the bit position in which
	 *                       they appear
	 * @see SerialPort#getCommPort(String)
	 */
	public SerialPrinter(String portDescriptor, String printerName, int statusHex,
			Map<Integer, PrinterStatus> statusMapping) throws IOException {

		this.statusMapping = statusMapping;
		this.printerName = printerName;
		comPort = SerialPort.getCommPort(portDescriptor);

		if (!comPort.openPort()) {
			throw new IOException(
					String.format("Error connecting to printer named %s on port %s", printerName, portDescriptor));
		}

		comPort.addDataListener(new SerialPortDataListener() {

			int backcount = 0;
			byte[] backdata = new byte[4];

			@Override
			public void serialEvent(SerialPortEvent event) {

				byte[] bytes = event.getReceivedData();

				FOREACHBYTE: for (byte b : bytes) {

					if (b == 0x13) {
						log.debug("Setting XOFF=true (pause transmision) on printer " + printerName);
						SerialPrinter.this.xoff = true;
					} else if (b == 0x11) {
						log.debug("Setting XOFF=false (resume transmission) on printer " + printerName);
						SerialPrinter.this.xoff = false;
					} else if ((b & 0x93) == 0x10) { /* Start of ASB Block */
						backdata[0] = b;
						backcount = 1;
						continue FOREACHBYTE;
					} else if (b == 0x0F) { /* End of ASB Block */
						if (backcount == 3) {
							backdata[3] = b;
							backcount = 0;
							rawStatus = BitSet.valueOf(Arrays.copyOf(backdata, 4));
							for (PrinterStatus s : getErrors()) {
								log.debug("Submitting status: " + s);
								SerialPrinter.this.submit(s);
							}
						}
					} else if (backcount == 1) {
						backdata[2] = b;
						backcount = 2;
						continue FOREACHBYTE;
					} else if (backcount == 2) {
						backdata[3] = b;
						backcount = 3;
						continue FOREACHBYTE;
					} else { /* Any other sequence is unrecognised */
						log.debug(String.format(
								"Unrecognised printer update for printer %s: received %s (%s hex %s dec) (Backdata: %s)",
								printerName, Integer.toBinaryString(b), Integer.toHexString(b), b,
								Arrays.toString(backdata)));
					}
					/* Reset these unless accumulating a 4-byte block */
					backcount = 0;
					Arrays.fill(backdata, (byte) 0);
				}
			}

			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
			}
		});

		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				java.io.OutputStream out = comPort.getOutputStream();
				try {
					out.write(GS);
					out.write('a');
					out.write(statusHex);
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 1, 60, TimeUnit.SECONDS);

		finished = false;
	}

	/**
	 * Finish the SerialStatus class, release comPort resource to be used on another
	 * program. You should call this method before exit. After finish is called, you
	 * cannot use anymore this instance.
	 */
	public void finish() {

		if (finished)
			return;
		finished = true;
		comPort.removeDataListener();
		comPort.closePort();

		this.submit(PrinterStatus.PRINTER_CLOSED);
	}

	public boolean isFinished() {
		return finished;
	}

	/**
	 * Provide one instance of OutputStream of SerialStatus
	 *
	 * @return one instance of outputstream
	 * @throws IOException
	 */

	public java.io.OutputStream getOutputStream() throws IOException {
		List<PrinterStatus> errors = this.getErrors();
		if (errors.size() > 0) {
			throw new IOException(String.format("Error opening printer %s, the printer has errors %s", printerName,
					errors.toString()));
		}
		return comPort.getOutputStream();
	}
}