package nl.andrewlalis.blockbookbinder.model;

import nl.andrewlalis.blockbookbinder.util.ApplicationProperties;

import java.util.HashMap;
import java.util.Map;

public class CharWidthMapper {
	private static final CharWidthMapper instance = new CharWidthMapper();

	public static CharWidthMapper getInstance() {
		return instance;
	}

	private final Map<Character, Integer> charWidthMap;

	public CharWidthMapper() {
		this.charWidthMap = new HashMap<>();
		this.initCharWidthMap();
	}

	public int getWidth(char c) {
		return this.charWidthMap.getOrDefault(c, 6);
	}

	private void initCharWidthMap() {
		this.charWidthMap.put(' ', 3);
		this.charWidthMap.put('!', 1);
		this.charWidthMap.put('"', 3);
		this.charWidthMap.put('\'', 1);
		this.charWidthMap.put('(', 3);
		this.charWidthMap.put(')', 3);
		this.charWidthMap.put('*', 3);
		this.charWidthMap.put(',', 1);
		this.charWidthMap.put('.', 1);
		this.charWidthMap.put(':', 1);
		this.charWidthMap.put(';', 1);
		this.charWidthMap.put('<', 4);
		this.charWidthMap.put('>', 4);
		this.charWidthMap.put('@', 6);
		this.charWidthMap.put('I', 3);
		this.charWidthMap.put('[', 3);
		this.charWidthMap.put(']', 3);
		this.charWidthMap.put('`', 2);
		this.charWidthMap.put('f', 4);
		this.charWidthMap.put('i', 1);
		this.charWidthMap.put('k', 4);
		this.charWidthMap.put('l', 2);
		this.charWidthMap.put('t', 3);
		this.charWidthMap.put('{', 3);
		this.charWidthMap.put('|', 1);
		this.charWidthMap.put('}', 3);
		this.charWidthMap.put('~', 6);

		final int defaultWidth = ApplicationProperties.getIntProp("book.default_char_width");
		for (char c = 32; c < 127; c++) {
			if (!this.charWidthMap.containsKey(c)) {
				this.charWidthMap.put(c, defaultWidth);
			}
		}
	}
}
