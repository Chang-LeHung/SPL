package org.spl.compiler.lexer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.spl.exceptions.SPLSyntaxError;

import java.io.IOException;
import java.net.URL;

public class InputBufferTest {

	@Test
	public void testInputBuffer() throws IOException {
		URL resource = Thread.currentThread().getContextClassLoader().getResource("arithmetic/test01.spl");
		assert resource != null;
		InputBuffer inputBuffer = new InputBuffer(resource.getPath());
		char c = inputBuffer.nextChar();
		System.out.println(c);
		c = inputBuffer.lookAhead();
		Assertions.assertEquals('a', c);
		c = inputBuffer.lookAhead(3);
		Assertions.assertEquals('=', c);
		c = inputBuffer.lookAhead(14);
		System.out.println(c);
		inputBuffer.close();
	}

	@Test
	public void testEscape() {
		char c = '\n';
		System.out.println(c);
	}

	public String getResource(String filename) {
		URL resource = Thread.currentThread().
				getContextClassLoader().
				getResource(filename);
		assert resource != null;
		return resource.getPath();
	}

	@Test
	public void testLexer() throws IOException, SPLSyntaxError {
		URL resource = Thread.currentThread().getContextClassLoader().getResource("arithmetic/test01.spl");
		assert resource != null;
		Lexer lexer = new Lexer(resource.getPath());
		lexer.doParse();
		System.out.println(lexer.getTokens());
	}

	@Test
	public void testBool() throws IOException, SPLSyntaxError {
		String resource = getResource("arithmetic/bool.spl");
		Lexer lexer = new Lexer(resource);
		lexer.doParse();
		System.out.println(lexer.getTokens());
	}
}
