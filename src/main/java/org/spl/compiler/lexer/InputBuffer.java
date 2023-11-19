package org.spl.compiler.lexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InputBuffer {

	private final String fileName;
	private final BufferedReader reader;
	private final StringBuilder buffer;
	private int off;
	private boolean EOF;
	private int lineNo;
	private int columnNo;

	public InputBuffer(String filename) throws IOException {
		fileName = filename;
		reader = new BufferedReader(new FileReader(fileName));
		off = 0;
		EOF = false;
		buffer = new StringBuilder();
		buffer.append(reader.readLine());
		buffer.append('\n');
		lineNo = 1;
		columnNo = 0;
	}

	public static void main(String[] args) {
		System.out.println("Hello World");
	}

	public char nextChar() {
		if (EOF) return 0;
		if (off >= buffer.length()) {
			try {
				buffer.delete(0, buffer.length());
				String s = reader.readLine();
				if (s == null) {
					EOF = true;
					return 0;
				}
				buffer.append(s);
				buffer.append('\n');
				off = 0;
			} catch (IOException e) {
				// TODO: handle this
				return 0;
			}
		}
		if (EOF) return 0;
		if (off < buffer.length()) {
			char ret = buffer.charAt(off++);
			if (ret == '\n') {
				lineNo++;
				columnNo = 0;
			}
			else columnNo++;
			return ret;
		} else {
			return 0;
		}
	}

	public char lookAhead() {
		return lookAhead(1);
	}
	public char lookAhead(int n) {
		if (EOF) return 0;
		while ((n + off - 1) >= buffer.length()) {
			try {
				String s = reader.readLine();
				if (s == null) {
					return 0;
				}
				buffer.append(s);
				buffer.append('\n');
			} catch (IOException e) {
				// TODO: handle this
				return 0;
			}
		}
		return buffer.charAt(off + n - 1);
	}

	public int getLineNo() {
		return lineNo;
	}

	public int getColumnNo() {
		return columnNo;
	}

	public boolean reachEOF() {
		return EOF;
	}

	public void close() throws IOException {
		reader.close();
	}

	public String getFileName() {
		return fileName;
	}

	public String getBuffer() {
		return buffer.toString();
	}
}
