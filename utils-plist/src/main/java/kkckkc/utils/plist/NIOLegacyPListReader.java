package kkckkc.utils.plist;

import kkckkc.utils.plist.NIOLegacyPListReader.Tokenizer.Token;
import kkckkc.utils.plist.NIOLegacyPListReader.Tokenizer.Token.Type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;



public class NIOLegacyPListReader {
	
	public Object read(byte[] bytearr) {
		// Start parsing
		String s = new String(bytearr);
		Tokenizer tokenizer = new Tokenizer(s);
		return parseObject(tokenizer, tokenizer.nextToken());
	}

	
	private Object parseObject(Tokenizer tokenizer, Token token) {
		switch (token.type) {
		case LEFT_BRACE:
			return parseDictionary(tokenizer, token);
		case QUOTE:
			return parseString(tokenizer, token);
		default:
			throw new RuntimeException("Unexpected token " + token);
		}
	}


	private Map<Object, Object> parseDictionary(Tokenizer tokenizer, Token token) {
		Map<Object, Object> dest = new LinkedHashMap<Object, Object>();
		while (tokenizer.peekNextToken().getType() != Token.Type.RIGHT_BRACE) {
			parseDictionaryEntry(dest, tokenizer);
		}
		tokenizer.nextToken(Token.Type.RIGHT_BRACE);
		return dest;
	}


	private void parseDictionaryEntry(Map<Object, Object> dest, Tokenizer tokenizer) {
		Object key = parseObject(tokenizer, tokenizer.nextToken());
		tokenizer.nextToken(Type.EQUALS);
		Object value = parseObject(tokenizer, tokenizer.nextToken());
		tokenizer.nextToken(Type.SEMICOLON);
		dest.put(key, value);
	}


	private String parseString(Tokenizer tokenizer, Token token) {
		Token stringContent = tokenizer.nextToken(Token.Type.STRING);
		tokenizer.nextToken(Token.Type.QUOTE);
		return stringContent.getValue().toString();
	}

	
	
	static class Tokenizer {
		private CharSequence buffer;
		private int position;

		public static enum State { INITIAL, INSTRING }

        private State state = State.INITIAL;
		private Queue<Token> tokenQueue = new ConcurrentLinkedQueue<Token>();
		
		public Tokenizer(CharSequence buffer) {
			this.buffer = buffer;
		}

		public Token peekNextToken() {
			if (tokenQueue.isEmpty()) fillTokenQueue();
			return tokenQueue.peek();
		}

		public Token nextToken(Type type) {
			Token t = nextToken();
			if (t.type != type) {
				throw new RuntimeException("Unexpected token " + t);
			}
			return t;
		}

		public Token nextToken() {
			if (tokenQueue.isEmpty()) fillTokenQueue();
			return tokenQueue.poll();
		}

		private void fillTokenQueue() {
			boolean tokenQueueConsistent = false;
			while (position < buffer.length()) {
				char c = buffer.charAt(position);
				
				switch (state) {
					
					case INITIAL:
						if (c == '{') tokenQueue.add(new Token(Token.Type.LEFT_BRACE, position, buffer.subSequence(position, position + 1)));
						else if (c == '}') tokenQueue.add(new Token(Token.Type.RIGHT_BRACE, position, buffer.subSequence(position, position + 1)));
						else if (c == ';') tokenQueue.add(new Token(Token.Type.SEMICOLON, position, buffer.subSequence(position, position + 1)));
						else if (c == '=') tokenQueue.add(new Token(Token.Type.EQUALS, position, buffer.subSequence(position, position + 1)));
						else if (c == '\'') {
							tokenQueue.add(new Token(Token.Type.QUOTE, position, buffer.subSequence(position, position + 1)));
							state = State.INSTRING;
							tokenQueueConsistent = false;
						} else {
							tokenQueueConsistent = true;
						}
						break;
						
					case INSTRING:
						if (c == '\'') {
							Token startOfString = tokenQueue.peek();
							tokenQueue.add(new Token(Token.Type.STRING, startOfString.getPosition() + 1, buffer.subSequence(startOfString.getPosition() + 1, position)));
							tokenQueue.add(new Token(Token.Type.QUOTE, position, buffer.subSequence(position, position + 1)));
							state = State.INITIAL;
							tokenQueueConsistent = true;
						} else {
							tokenQueueConsistent = false;
						}
						break;
					
				}
				
				position++;
				
				if (! tokenQueue.isEmpty() && tokenQueueConsistent) return;
			}
		}

		static class Token {
			static enum Type { LEFT_BRACE, RIGHT_BRACE, QUOTE, SEMICOLON, EQUALS, STRING}

            private Type type;
			private CharSequence value;
			private int position;
			
			public Token(Type type, int position, CharSequence value) {
				this.type = type;
				this.position = position;
				this.value = value;
			}
			
			public int getPosition() {
				return position;
			}
			
			public Type getType() {
				return type;
			}
			
			public CharSequence getValue() {
				return value;
			}
			
			public String toString() {
				return type.toString() + " [" + value + "]";
			}
		}
	}
}
