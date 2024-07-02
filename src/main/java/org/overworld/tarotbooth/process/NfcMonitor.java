package org.overworld.tarotbooth.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

public class NfcMonitor {

	private String lastLine;
	private BufferedReader reader;
	
	@Getter
	private Instant lastInstant = Instant.EPOCH;
	@Getter
	private String lastCard;
	
	private Pattern pattern = Pattern.compile("(\\d*)\\s+([\\d\\w]+)");
	
	public void attach(String processName, String env) throws IOException {
		
		ProcessBuilder builder = new ProcessBuilder(processName);
		builder.environment().put("extra", env);

		Process process = builder.start();
		InputStream stdout = process.getInputStream();
		reader = new BufferedReader (new InputStreamReader(stdout));
	}
	
	public void poll() throws IOException {
		
		while (reader.ready() && (lastLine = reader.readLine()) != null) {}
		
		if (lastLine == null)
			return;
		
		Matcher m = pattern.matcher(lastLine);
		if (m.matches()) {
			lastInstant = Instant.ofEpochSecond(Long.parseLong(m.group(1)));
			lastCard = m.group(2);
		} else {
			System.err.println("Matcher does not match line " + lastLine);
		}
	}
	
	public static void main(String...a) throws IOException {
		
		NfcMonitor mon = new NfcMonitor();
		mon.attach("/Users/stephen/workspaces/tarot/TarotBooth/barf.sh", "hiImExtra");
		
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mon.poll();
			System.out.println("At instant " + Instant.now());
			System.out.println("" + mon.lastInstant + " " + mon.lastCard);
		}
	}
}
