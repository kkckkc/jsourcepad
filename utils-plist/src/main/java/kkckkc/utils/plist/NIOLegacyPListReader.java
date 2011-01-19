package kkckkc.utils.plist;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;



public class NIOLegacyPListReader {
    private boolean textMateFormatting;

    public NIOLegacyPListReader() {
        this(false);
    }
    
    public NIOLegacyPListReader(boolean textMateFormatting) {
        this.textMateFormatting = textMateFormatting;
    }
    
	public Object read(byte[] bytes) {
		Tokenizer tokenizer = new Tokenizer(new String(bytes));
		return parseObject(tokenizer, tokenizer.nextToken());
	}
	
	private Object parseObject(Tokenizer tokenizer, Token token) {
		switch (token.type) {
		case LEFT_BRACE:
			return parseDictionary(tokenizer);
        case LEFT_PAR:
            return parseList(tokenizer);
		case QUOTE:
			return parseString(tokenizer);
        case LITERAL:
            return token.getValue().toString();
		default:
			throw new RuntimeException("Unexpected token " + token + "\n" + token.dumpNeighbourhood());
		}
	}

    private List<Object> parseList(Tokenizer tokenizer) {
        List<Object> list = new ArrayList<Object>();
        while (tokenizer.peekNextToken().getType() != TokenType.RIGHT_PAR) {
            list.add(parseObject(tokenizer, tokenizer.nextToken()));
            if (tokenizer.peekNextToken().getType() == TokenType.COMMA)
                tokenizer.nextToken(TokenType.COMMA);
        }
        tokenizer.nextToken(TokenType.RIGHT_PAR);
        return list;
    }

	private Map<Object, Object> parseDictionary(Tokenizer tokenizer) {
		Map<Object, Object> dictionary = new LinkedHashMap<Object, Object>();
		while (tokenizer.peekNextToken().getType() != TokenType.RIGHT_BRACE) {
			parseDictionaryEntry(dictionary, tokenizer);
		}
		tokenizer.nextToken(TokenType.RIGHT_BRACE);
		return dictionary;
	}

	private void parseDictionaryEntry(Map<Object, Object> destinationMap, Tokenizer tokenizer) {
		Object key = parseObject(tokenizer, tokenizer.nextToken());
		tokenizer.nextToken(TokenType.EQUALS);
		Object value = parseObject(tokenizer, tokenizer.nextToken());
		tokenizer.nextToken(TokenType.SEMICOLON);
		destinationMap.put(key, value);
	}

	private String parseString(Tokenizer tokenizer) {
		Token stringContent = tokenizer.nextToken(TokenType.STRING);
		tokenizer.nextToken(TokenType.QUOTE);
		return stringContent.getValue().toString();
	}

	
    static enum TokenizerState { INITIAL, IN_STRING, IN_DOUBLE_QUOTED_STRING }
    static enum TokenType { LEFT_BRACE, RIGHT_BRACE, LEFT_PAR, RIGHT_PAR, QUOTE, SEMICOLON, EQUALS, STRING, LITERAL, COMMA }

	class Tokenizer {
		private CharSequence buffer;
		private int position;

        private TokenizerState state = TokenizerState.INITIAL;
		private Queue<Token> tokenQueue = new LinkedList<Token>();
		
		public Tokenizer(CharSequence buffer) {
			this.buffer = buffer;
		}

		public Token peekNextToken() {
			if (tokenQueue.isEmpty()) fillTokenQueue();
			return tokenQueue.peek();
		}

		public Token nextToken(TokenType type) {
			Token t = nextToken();
			if (t.type != type) {
				throw new RuntimeException("Unexpected token " + t + "\n" + t.dumpNeighbourhood());
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
						if (c == '{') tokenQueue.add(new Token(TokenType.LEFT_BRACE, position, buffer, buffer.subSequence(position, position + 1)));
						else if (c == '}') tokenQueue.add(new Token(TokenType.RIGHT_BRACE, position, buffer, buffer.subSequence(position, position + 1)));
                        else if (c == '(') tokenQueue.add(new Token(TokenType.LEFT_PAR, position, buffer, buffer.subSequence(position, position + 1)));
                        else if (c == ')') tokenQueue.add(new Token(TokenType.RIGHT_PAR, position, buffer, buffer.subSequence(position, position + 1)));
						else if (c == ';') tokenQueue.add(new Token(TokenType.SEMICOLON, position, buffer, buffer.subSequence(position, position + 1)));
                        else if (c == ',') tokenQueue.add(new Token(TokenType.COMMA, position, buffer, buffer.subSequence(position, position + 1)));
						else if (c == '=') tokenQueue.add(new Token(TokenType.EQUALS, position, buffer, buffer.subSequence(position, position + 1)));
						else if (c == '\'') {
							tokenQueue.add(new Token(TokenType.QUOTE, position, buffer, buffer.subSequence(position, position + 1)));
							state = TokenizerState.IN_STRING;
							tokenQueueConsistent = false;
                        } else if (c == '"') {
                            tokenQueue.add(new Token(TokenType.QUOTE, position, buffer, buffer.subSequence(position, position + 1)));
                            state = TokenizerState.IN_DOUBLE_QUOTED_STRING;
                            tokenQueueConsistent = false;
                        } else if (Character.isJavaIdentifierPart(c)) {
                            int start = position;
                            while (Character.isJavaIdentifierPart(buffer.charAt(position))) {
                                position++;
                            }
                            tokenQueue.add(new Token(TokenType.LITERAL, start, buffer, buffer.subSequence(start, position)));
                            position--;

						} else {
							tokenQueueConsistent = true;
						}
						break;
						
					case IN_STRING:
                        if (textMateFormatting && c == '\'' && buffer.charAt(position + 1) == '\'') {
                            position += 1;
                            tokenQueueConsistent = false;
                        } else if (c == '\'') {
							Token startOfString = tokenQueue.peek();
							tokenQueue.add(new Token(TokenType.STRING, startOfString.getPosition() + 1, buffer, unescape(buffer.subSequence(startOfString.getPosition() + 1, position))));
							tokenQueue.add(new Token(TokenType.QUOTE, position, buffer, buffer.subSequence(position, position + 1)));
							state = TokenizerState.INITIAL;
							tokenQueueConsistent = true;
						} else {
							tokenQueueConsistent = false;
						}
						break;

                    case IN_DOUBLE_QUOTED_STRING:
                        if (c == '\\') {
                            position++;
                            tokenQueueConsistent = false;
                        } else if (c == '"') {
                            Token startOfString = tokenQueue.peek();
                            tokenQueue.add(new Token(TokenType.STRING, startOfString.getPosition() + 1, buffer, unescape(buffer.subSequence(startOfString.getPosition() + 1, position))));
                            tokenQueue.add(new Token(TokenType.QUOTE, position, buffer, buffer.subSequence(position, position + 1)));
                            state = TokenizerState.INITIAL;
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
            StringBuilder builder = new StringBuilder();

            boolean inEscape = false;
            if (textMateFormatting) {
                for (int i = 0; i < charSequence.length(); i++) {
                    char c = charSequence.charAt(i);
                    if (! inEscape) {
                        if (c == '\'') inEscape = true;
                        else builder.append(c);
                    } else {
                        if (c == '\'') {
                            builder.append("'");
                            inEscape = false;
                        } else throw new RuntimeException("Cannot happen");
                    }
                }
            } else {
                for (int i = 0; i < charSequence.length(); i++) {
                    char c = charSequence.charAt(i);
                    if (! inEscape) {
                        if (c == '\\') inEscape = true;
                        else builder.append(c);
                    } else {
                        if (c == '\\') builder.append("\\");
                        else if (c == '"') builder.append("\"");
                        else if (c == 'b') builder.append("\b");
                        else if (c == 'n') builder.append("\n");
                        else if (c == 'r') builder.append("\r");
                        else if (c == 't') builder.append("\t");
                        else {
                            // TODO: Handle octal and hex escapes, see http://www.gnustep.org/resources/documentation/Developer/Base/Reference/NSPropertyList.html
                            builder.append(c);
                        }
                    }
                }
            }

            return builder;
        }

	}

    static class Token {
        private CharSequence allText;

        private TokenType type;
        private CharSequence value;
        private int position;

        public Token(TokenType type, int position, CharSequence allText, CharSequence value) {
            this.type = type;
            this.position = position;
            this.value = value;
            this.allText = allText;
        }

        public int getPosition() {
            return position;
        }

        public TokenType getType() {
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
