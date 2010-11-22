package kkckkc.utils.plist;

import kkckkc.utils.plist.NIOLegacyPListReader.Tokenizer.Token;
import kkckkc.utils.plist.NIOLegacyPListReader.Tokenizer.Token.Type;

import java.util.*;
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
        case LEFT_PAR:
            return parseList(tokenizer, token);
		case QUOTE:
			return parseString(tokenizer, token);
        case LITERAL:
            return token.getValue().toString();
		default:
			throw new RuntimeException("Unexpected token " + token + "\n" + token.dumpNeighbourhood());
		}
	}

    private List<Object> parseList(Tokenizer tokenizer, Token token) {
        List<Object> dest = new ArrayList<Object>();
        while (tokenizer.peekNextToken().getType() != Token.Type.RIGHT_PAR) {
            dest.add(parseObject(tokenizer, tokenizer.nextToken()));
            if (tokenizer.peekNextToken().getType() == Token.Type.COMMA)
                tokenizer.nextToken(Token.Type.COMMA);
        }
        tokenizer.nextToken(Token.Type.RIGHT_PAR);
        return dest;
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

		public static enum State { INITIAL, INSTRING, INDQSTRING }

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
				throw new RuntimeException("Unexpected token " + t + "\n" + t.dumpNeighbourhood());
			}
			return t;
		}

		public Token nextToken() {
			if (tokenQueue.isEmpty()) fillTokenQueue();
			Token t = tokenQueue.poll();
            return t;
		}

		private void fillTokenQueue() {
			boolean tokenQueueConsistent = false;
			while (position < buffer.length()) {
				char c = buffer.charAt(position);
				
				switch (state) {
					
					case INITIAL:
						if (c == '{') tokenQueue.add(new Token(Token.Type.LEFT_BRACE, position, buffer, buffer.subSequence(position, position + 1)));
						else if (c == '}') tokenQueue.add(new Token(Token.Type.RIGHT_BRACE, position, buffer, buffer.subSequence(position, position + 1)));
                        else if (c == '(') tokenQueue.add(new Token(Token.Type.LEFT_PAR, position, buffer, buffer.subSequence(position, position + 1)));
                        else if (c == ')') tokenQueue.add(new Token(Token.Type.RIGHT_PAR, position, buffer, buffer.subSequence(position, position + 1)));
						else if (c == ';') tokenQueue.add(new Token(Token.Type.SEMICOLON, position, buffer, buffer.subSequence(position, position + 1)));
                        else if (c == ',') tokenQueue.add(new Token(Token.Type.COMMA, position, buffer, buffer.subSequence(position, position + 1)));
						else if (c == '=') tokenQueue.add(new Token(Token.Type.EQUALS, position, buffer, buffer.subSequence(position, position + 1)));
						else if (c == '\'') {
							tokenQueue.add(new Token(Token.Type.QUOTE, position, buffer, buffer.subSequence(position, position + 1)));
							state = State.INSTRING;
							tokenQueueConsistent = false;
                        } else if (c == '"') {
                            tokenQueue.add(new Token(Token.Type.QUOTE, position, buffer, buffer.subSequence(position, position + 1)));
                            state = State.INDQSTRING;
                            tokenQueueConsistent = false;
                        } else if (Character.isJavaIdentifierPart(c)) {
                            int start = position;
                            while (Character.isJavaIdentifierPart(buffer.charAt(position))) {
                                position++;
                            }
                            tokenQueue.add(new Token(Token.Type.LITERAL, start, buffer, buffer.subSequence(start, position)));
                            position--;

						} else {
							tokenQueueConsistent = true;
						}
						break;
						
					case INSTRING:
						if (c == '\'') {
							Token startOfString = tokenQueue.peek();
							tokenQueue.add(new Token(Token.Type.STRING, startOfString.getPosition() + 1, buffer, buffer.subSequence(startOfString.getPosition() + 1, position)));
							tokenQueue.add(new Token(Token.Type.QUOTE, position, buffer, buffer.subSequence(position, position + 1)));
							state = State.INITIAL;
							tokenQueueConsistent = true;
						} else {
							tokenQueueConsistent = false;
						}
						break;

                    case INDQSTRING:
                        if (c == '\\') {
                            position++;
                            tokenQueueConsistent = false;
                        } else if (c == '"') {
                            Token startOfString = tokenQueue.peek();
                            tokenQueue.add(new Token(Token.Type.STRING, startOfString.getPosition() + 1, buffer, unescape(buffer.subSequence(startOfString.getPosition() + 1, position))));
                            tokenQueue.add(new Token(Token.Type.QUOTE, position, buffer, buffer.subSequence(position, position + 1)));
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

        private CharSequence unescape(CharSequence charSequence) {
            StringBuilder b = new StringBuilder();

            boolean inEscape = false;
            for (int i = 0; i < charSequence.length(); i++) {
                char c = charSequence.charAt(i);
                if (! inEscape) {
                    if (c == '\\') inEscape = true;
                    else b.append(c);
                } else {
                    if (c == '\\') b.append("\\");
                    else if (c == '"') b.append("\"");
                    else if (c == 'b') b.append("\b");
                    else if (c == 'n') b.append("\n");
                    else if (c == 'r') b.append("\r");
                    else if (c == 't') b.append("\t");
                    else {
                        // TODO: Handle octal and hex escapes, see http://www.gnustep.org/resources/documentation/Developer/Base/Reference/NSPropertyList.html
                        b.append(c);
                    }
                }
            }

            return b;
        }

        static class Token {
            private CharSequence allText;

            static enum Type { LEFT_BRACE, RIGHT_BRACE, LEFT_PAR, RIGHT_PAR, QUOTE, SEMICOLON, EQUALS, STRING, LITERAL, COMMA }

            private Type type;
			private CharSequence value;
			private int position;
			
			public Token(Type type, int position, CharSequence allText, CharSequence value) {
				this.type = type;
				this.position = position;
				this.value = value;
                this.allText = allText;
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

            public String dumpNeighbourhood() {
                return allText.subSequence(Math.max(0, position - 40), position) +
                        "|" +
                        allText.subSequence(position, Math.min(allText.length() - 1, position + 40)).toString();
            }
		}
	}


    public static void main(String... args) {
        Map m = (Map) new NIOLegacyPListReader().read(
                ("{ begin = \"'\";\n" +
                        "  end = \"'\"; }").getBytes());
        System.out.println(m);
    }
}
