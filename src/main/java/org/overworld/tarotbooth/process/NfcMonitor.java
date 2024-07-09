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
	
	private String sensorName;
	
	private Pattern pattern = Pattern.compile("(\\d*)\\s+([\\d\\w]+)");
	
	public void attach(String sensorName, String processName, String envKey, String env) throws IOException {
		
		this.sensorName = sensorName;
		
		ProcessBuilder builder = new ProcessBuilder()
				.command(processName)
				.redirectErrorStream(true);
		builder.environment().put(envKey, env);
		Process process = builder.start();
		
		System.out.println("Started nfc reader for " + sensorName + " with PID " + process.pid());
		InputStream stdout = process.getInputStream();
		process.getOutputStream().close();
		reader = new BufferedReader (new InputStreamReader(stdout));
	}
	
	public void poll() throws IOException {
		
		String read;
		while (reader.ready() && (read = reader.readLine()) != null) {
				 lastLine = read;
		}
		
		if (lastLine == null)
			return;
		
		System.out.println("Read from NFC " + sensorName + " " + lastLine);
		
		Matcher m = pattern.matcher(lastLine);
		if (m.matches()) {
			lastInstant = Instant.ofEpochSecond(Long.parseLong(m.group(1)));
			lastCard = m.group(2);
		} else {
			System.err.println("Sensor: " + sensorName + " Matcher does not match line " + lastLine);
		}
	}
}
