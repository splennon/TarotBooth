package org.overworld.tarotbooth.printer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Srp500Commands {

	/**
	 * Clears the data in the print buffer and resets the printer mode to the mode
	 * that was in effect when the power was turned on.
	 */
	public static final byte[] INITIALISE = { 0x1B, 0x40 };

	/**
	 * Print, return to home position and feed one line
	 */
	public static final byte[] LF = { 0x0a }; // line feed

	/**
	 * Print and return to home position
	 */
	public static final byte[] CR = { 0x0D }; // carriage return (without feed)

	/**
	 * Move forward one horizontal tab
	 */
	public static final byte[] HT = { 0x09 }; // horizontal tab

	/**
	 * Clear correctable printer error
	 */
	public static final byte[] CLEAR_ERROR = { 0x10, 0x05, 0x2 };

	/**
	 * Sets the character spacing for the right side of the character to [ n x 0.122
	 * mm {1/208 inches}] . The right-side character spacing for double-width mode
	 * is twice the normal value
	 *
	 * @param n the Sets the character spacing for the right side of the character
	 *          to [ n x 0.122 mm {1/208 inches}]
	 */
	public static final byte[] CHARACTER_SPACING(byte n) {
		return new byte[] { 0x1b, 0x20, n };
	}

	/**
	 * Sets line spacing, default is 32 (1/6 inch)
	 *
	 * @param n Sets the line spacing to [ n x (1/192)] inches.
	 */
	public static final byte[] LINE_SPACING(byte n) {
		return new byte[] { 0x1b, 0x33, n };
	}

	/**
	 * Set horizontal tab positions
	 *
	 * @param tabs specifies the column number (counted from the beginning of the
	 *             line) for setting a horizontal tab position.
	 */
	public static final byte[] SET__HORIZONTAL_TABS(byte[] tabs) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(new byte[] { 0x1b, 0x44 });
			outputStream.write(tabs);
		} catch (IOException e) {
			throw new RuntimeException("Error handling ByteArrayOutputStream");
		}
		return outputStream.toByteArray();
	}

	/**
	 * Clear previously set horizontal tabs
	 */
	public static final byte[] CLEAR_HORIZONTAL_TABS = { 0x1b, 0x44, 0x00 };

	/**
	 * Prints the data in the print buffer and feeds the paper
	 *
	 * @param n paper is fed n x 0.122mm {1/192 inches}
	 */
	public static final byte[] PRINT_AND_FEED(byte n) {
		return new byte[] { 0x1b, 0x4A, n };
	}

	/**
	 * Feed the paper to the cut position
	 */
	public static final byte[] CUT_PAPER_ALTERNATE = { 0x1d, 0x56, 0x01 };

	/**
	 * Feed the paper to the cut position
	 */
	public static final byte[] FEED_AND_CUT = { 0x1d, 0x56, 0x42, 0x00 };

	/**
	 * Perform a partial cut of the paper
	 */
	public static final byte[] CUT_PAPER = { 0x1b, 0x6d };

	/**
	 * Feed paper a number of lines
	 *
	 * @param lines the number of lines to feed
	 */
	public static final byte[] FEED(byte lines) {
		return new byte[] { 0x1b, 0x64, lines };
	}

	/**
	 * Kick out cash drawer on pin 2
	 */
	public static final byte[] DRAWER_KICK_2 = { 0x1b, 0x70, 0x00 }; // Sends a pulse to pin 2 []

	/**
	 * Kick out cash drawer on pin 5
	 */
	public static final byte[] DRAWER_KICK_5 = { 0x1b, 0x70, 0x01 }; // Sends a pulse to pin 5 []

	/**
	 * Select print mode
	 *
	 * @param fontB        to select Font B (Instead of Font A)
	 * @param emphasised   to select emphasis
	 * @param doubleHeight to select double height
	 * @param doubleWidth  to select double width
	 * @param underlined   to select underlining
	 */
	public static final byte[] PRINT_MODE(boolean fontB, boolean emphasised, boolean doubleHeight, boolean doubleWidth,
			boolean underlined) {
		int result = 0;
		if (fontB)
			result += 1;
		if (emphasised)
			result += 8;
		if (doubleHeight)
			result += 16;
		if (doubleWidth)
			result += 32;
		if (underlined)
			result += 128;
		return new byte[] { 0x1b, 0x21, (byte) result };
	}

	public static final byte[] PRINT_MODE_NORMAL = { 0x1b, 0x21, 0 };

	/**
	 * Print one row (8 pixels high) of raw image data, optionally in double density
	 *
	 * @param doubleDensity print with double density
	 * @param imageData     the raw data to print
	 * @return
	 */
	public static final byte[] PRINT_IMAGE(boolean doubleDensity, byte[] imageData) {
		int nH = imageData.length % 256;
		int nL = imageData.length / 256;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(new byte[] { 0x1b, 0x2a, (byte) (doubleDensity ? 1 : 0), (byte) nL, (byte) nH });
			outputStream.write(imageData);
		} catch (IOException e) {
			throw new RuntimeException("Error handling ByteArrayOutputStream");
		}
		return outputStream.toByteArray();
	}

	/**
	 * Underlining
	 *
	 * @param on turn on underlining
	 * @return
	 */
	public static final byte[] UNDERLINED(boolean on) {
		return new byte[] { 0x1b, 0x2d, (byte) (on ? 1 : 0) };
	}

	/**
	 * Emphasis
	 *
	 * @param on turn on emphasis
	 * @return
	 */
	public static final byte[] EMPHASISED(boolean on) {
		return new byte[] { 0x1b, 0x45, (byte) (on ? 1 : 0) };
	}

	/**
	 * Double-strike printing
	 *
	 * @param on turn on double-strike printing
	 */
	public static final byte[] DOUBLE_STRIKE(boolean on) {
		return new byte[] { 0x1b, 0x47, (byte) (on ? 1 : 0) };
	}

	/**
	 * Justify text left
	 */
	public static final byte[] JUSTIFY_LEFT = { 0x1b, 0x61, 0x00 };

	/**
	 * Justify text centre
	 */
	public static final byte[] JUSTIFY_CENTRE = { 0x1b, 0x61, 0x01 };

	/**
	 * Justify text right
	 */
	public static final byte[] JUSTIFY_RIGHT = { 0x1b, 0x61, 0x02 };

	/**
	 * Select printing in the first colour
	 */
	public static final byte[] COLOUR_BLACK = { 0x1b, 0x72, 0x00 };

	/**
	 * Select printing in the second colour
	 */
	public static final byte[] COLOUR_RED = { 0x1b, 0x72, 0x01 };

	public static final byte[] CHARSET_USA = { 0x1b, 0x52, 0x00 };
	public static final byte[] CHARSET_FRANCE = { 0x1b, 0x52, 0x01 };
	public static final byte[] CHARSET_GERMANY = { 0x1b, 0x52, 0x02 };
	public static final byte[] CHARSET_UK = { 0x1b, 0x52, 0x03 };
	public static final byte[] CHARSET_DENMARK1 = { 0x1b, 0x52, 0x04 };
	public static final byte[] CHARSET_SWEEDEN = { 0x1b, 0x52, 0x05 };
	public static final byte[] CHARSET_ITALY = { 0x1b, 0x52, 0x06 };
	public static final byte[] CHARSET_SPAIN1 = { 0x1b, 0x52, 0x07 };
	public static final byte[] CHARSET_NORWAY = { 0x1b, 0x52, 0x09 };
	public static final byte[] CHARSET_DENMARK2 = { 0x1b, 0x52, 0x10 };

	/**
	 * Turns unidirectional printing mode on or off
	 *
	 * @param on turn unidirectional printing on
	 */
	public static final byte[] UNIDIRECTIONAL_PRINTING(boolean on) {
		return new byte[] { 0x1b, 0x55, (byte) (on ? 1 : 0) };
	}

	/**
	 * Upside-down printing
	 *
	 * @param on to select printing upside-down
	 */
	public static final byte[] UPSIDE_DOWN(boolean on) {
		return new byte[] { 0x1b, 0x7b, (byte) (on ? 1 : 0) };
	}

	/**
	 * Select which paper sensors to enable
	 *
	 * @param nearEnd1 enable first near end sensor
	 * @param nearEnd2 enable second near end sensor
	 * @param end1     enable first end sensor
	 * @param end2     enable second end sensor
	 */
	public static final byte[] PAPER_DETECTION_ENABLE(boolean nearEnd1, boolean nearEnd2, boolean end1, boolean end2) {
		int result = 0;
		if (nearEnd1)
			result += 1;
		if (nearEnd2)
			result += 2;
		if (end1)
			result += 4;
		if (end2)
			result += 8;
		return new byte[] { 0x1b, 0x63, 0x33, (byte) result };
	}

	/**
	 * Select which paper sensor(s) arrest printing
	 *
	 * @param nearEnd1 near end sensor 1 stops printing
	 * @param nearEnd2 near end sensor 2 stops printing
	 */
	public static final byte[] PAPER_DETECTION_ENFORCE(boolean nearEnd1, boolean nearEnd2) {
		int result = 0;
		if (nearEnd1)
			result += 1;
		if (nearEnd2)
			result += 2;
		return new byte[] { 0x1b, 0x63, 0x34, (byte) result };
	}

	/**
	 * Control enablement of panel buttons
	 *
	 * @param on
	 */
	public static final byte[] PANEL_BUTTONS(boolean enabled) {
		return new byte[] { 0x1b, 0x63, 0x35, (byte) (enabled ? 0 : 1) };
	}

	// Character code table
	public static final byte[] CHARCODE_PC437 = { 0x1b, 0x74, 0x00 };
	public static final byte[] CHARCODE_PC850 = { 0x1b, 0x74, 0x02 };
	public static final byte[] CHARCODE_PC860 = { 0x1b, 0x74, 0x03 };
	public static final byte[] CHARCODE_PC863 = { 0x1b, 0x74, 0x04 };
	public static final byte[] CHARCODE_PC865 = { 0x1b, 0x74, 0x05 };
	public static final byte[] CHARCODE_PC1252 = { 0x1b, 0x74, (byte) 16 };
	public static final byte[] CHARCODE_PC866 = { 0x1b, 0x74, (byte) 17 };
	public static final byte[] CHARCODE_PC852 = { 0x1b, 0x74, (byte) 18 };
	public static final byte[] CHARCODE_PC858 = { 0x1b, 0x74, (byte) 19 };
	public static final byte[] CHARCODE_PC862 = { 0x1b, 0x74, (byte) 21 };
	public static final byte[] CHARCODE_PC864 = { 0x1b, 0x74, (byte) 22 };
	public static final byte[] CHARCODE_PC874 = { 0x1b, 0x74, (byte) 23 };

	private Srp500Commands() {
	}
}
