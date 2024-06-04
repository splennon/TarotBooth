package org.overworld.tarotbooth.model;

import java.util.HashMap;

public class Deck extends HashMap<String, Deck.Card> {

	private static final long serialVersionUID = -8299688802942289003L;
	
	public static record Card(String tag, String filename) {};

	{
		this.put("C01", new Card("C01", "C01.png"));
		this.put("C02", new Card("C02", "C02.png"));
		this.put("C03", new Card("C03", "C03.png"));
		this.put("C04", new Card("C04", "C04.png"));
		this.put("C05", new Card("C05", "C05.png"));
		this.put("C06", new Card("C06", "C06.png"));
		this.put("C07", new Card("C07", "C07.png"));
		this.put("C08", new Card("C08", "C08.png"));
		this.put("C09", new Card("C09", "C09.png"));
		this.put("C10", new Card("C10", "C10.png"));
		this.put("C11", new Card("C11", "C11.png"));
		this.put("C12", new Card("C12", "C12.png"));
		this.put("C13", new Card("C13", "C13.png"));
		this.put("C14", new Card("C14", "C14.png"));
		this.put("M00", new Card("M00", "M00.png"));
		this.put("M01", new Card("M01", "M01.png"));
		this.put("M02", new Card("M02", "M02.png"));
		this.put("M03", new Card("M03", "M03.png"));
		this.put("M04", new Card("M04", "M04.png"));
		this.put("M05", new Card("M05", "M05.png"));
		this.put("M06", new Card("M06", "M06.png"));
		this.put("M07", new Card("M07", "M07.png"));
		this.put("M08", new Card("M08", "M08.png"));
		this.put("M09", new Card("M09", "M09.png"));
		this.put("M10", new Card("M10", "M10.png"));
		this.put("M11", new Card("M11", "M11.png"));
		this.put("M12", new Card("M12", "M12.png"));
		this.put("M13", new Card("M13", "M13.png"));
		this.put("M14", new Card("M14", "M14.png"));
		this.put("M15", new Card("M15", "M15.png"));
		this.put("M16", new Card("M16", "M16.png"));
		this.put("M17", new Card("M17", "M17.png"));
		this.put("M18", new Card("M18", "M18.png"));
		this.put("M19", new Card("M19", "M19.png"));
		this.put("M20", new Card("M20", "M20.png"));
		this.put("M21", new Card("M21", "M21.png"));
		this.put("P01", new Card("P01", "P01.png"));
		this.put("P02", new Card("P02", "P02.png"));
		this.put("P03", new Card("P03", "P03.png"));
		this.put("P04", new Card("P04", "P04.png"));
		this.put("P05", new Card("P05", "P05.png"));
		this.put("P06", new Card("P06", "P06.png"));
		this.put("P07", new Card("P07", "P07.png"));
		this.put("P08", new Card("P08", "P08.png"));
		this.put("P09", new Card("P09", "P09.png"));
		this.put("P10", new Card("P10", "P10.png"));
		this.put("P11", new Card("P11", "P11.png"));
		this.put("P12", new Card("P12", "P12.png"));
		this.put("P13", new Card("P13", "P13.png"));
		this.put("P14", new Card("P14", "P14.png"));
		this.put("S01", new Card("S01", "S01.png"));
		this.put("S02", new Card("S02", "S02.png"));
		this.put("S03", new Card("S03", "S03.png"));
		this.put("S04", new Card("S04", "S04.png"));
		this.put("S05", new Card("S05", "S05.png"));
		this.put("S06", new Card("S06", "S06.png"));
		this.put("S07", new Card("S07", "S07.png"));
		this.put("S08", new Card("S08", "S08.png"));
		this.put("S09", new Card("S09", "S09.png"));
		this.put("S10", new Card("S10", "S10.png"));
		this.put("S11", new Card("S11", "S11.png"));
		this.put("S12", new Card("S12", "S12.png"));
		this.put("S13", new Card("S13", "S13.png"));
		this.put("S14", new Card("S14", "S14.png"));
		this.put("W01", new Card("W01", "W01.png"));
		this.put("W02", new Card("W02", "W02.png"));
		this.put("W03", new Card("W03", "W03.png"));
		this.put("W04", new Card("W04", "W04.png"));
		this.put("W05", new Card("W05", "W05.png"));
		this.put("W06", new Card("W06", "W06.png"));
		this.put("W07", new Card("W07", "W07.png"));
		this.put("W08", new Card("W08", "W08.png"));
		this.put("W09", new Card("W09", "W09.png"));
		this.put("W10", new Card("W10", "W10.png"));
		this.put("W11", new Card("W11", "W11.png"));
		this.put("W12", new Card("W12", "W12.png"));
		this.put("W13", new Card("W13", "W13.png"));
		this.put("W14", new Card("W14", "W14.png"));

	}
}
