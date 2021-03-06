package util;

import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

/**
 * TokenManager for the syntax area.
 * @author Tobias
 *
 */
public class MyTokenMaker extends AbstractTokenMaker {

	private int currentTokenStart,
				currentTokenType;
	
	@Override
	public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
		// This assumes all keywords, etc. were parsed as "identifiers."
		if (tokenType == Token.IDENTIFIER) {
			int value = wordsToHighlight.get(segment, start, end);
			if (value != -1) {
				tokenType = value;
			}
		}
		super.addToken(segment, start, end, tokenType, startOffset);
	}

	@Override
	public Token getTokenList(Segment text, int startTokenType, int startOffset) {

		resetTokenList();
		
		char[] array = text.array;
		int offset = text.offset;
		int count = text.count;
		int end = offset + count;

		// Token starting offsets are always of the form:
		// 'startOffset + (currentTokenStart-offset)', but since startOffset and
		// offset are constant, tokens' starting positions become:
		// 'newStartOffset+currentTokenStart'.
		int newStartOffset = startOffset - offset;

		currentTokenStart = offset;
		currentTokenType = startTokenType;

		for (int i = offset; i < end; i++) {

			char c = array[i];

			switch (currentTokenType) {

			case Token.NULL:

				currentTokenStart = i; // Starting a new token here.

				switch (c) {

				case ' ':
				case '\t':
					currentTokenType = Token.WHITESPACE;
					break;

				case '"':
					currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
					break;

//				case '-':
//					currentTokenType = Token.COMMENT_EOL;
//					break;

				default:
					if (RSyntaxUtilities.isDigit(c)) {
						currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
						break;
					} else if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
						currentTokenType = Token.IDENTIFIER;
						break;
					}

					// Anything not currently handled - mark as an identifier
					currentTokenType = Token.IDENTIFIER;
					break;

				} // End of switch (c).

				break;

			case Token.WHITESPACE:

				switch (c) {

				case ' ':
				case '\t':
					break; // Still whitespace.

				case '"':
					addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
					break;

//				case '-':
//					addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
//					currentTokenStart = i;
//					currentTokenType = Token.COMMENT_EOL;
//					break;

				default: // Add the whitespace token and start anew.

					addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
					currentTokenStart = i;

					if (RSyntaxUtilities.isDigit(c)) {
						currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
						break;
					} else if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
						currentTokenType = Token.IDENTIFIER;
						break;
					}

					// Anything not currently handled - mark as identifier
					currentTokenType = Token.IDENTIFIER;

				} // End of switch (c).

				break;

			default: // Should never happen
			case Token.IDENTIFIER:

				switch (c) {

				case ' ':
				case '\t':
					addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.WHITESPACE;
					break;

				case '"':
					addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
					break;

				default:
					if (RSyntaxUtilities.isLetterOrDigit(c) || c == '/' || c == '_') {
						break; // Still an identifier of some type.
					}
					// Otherwise, we're still an identifier (?).

				} // End of switch (c).

				break;

			case Token.LITERAL_NUMBER_DECIMAL_INT:

				switch (c) {

				case ' ':
				case '\t':
					addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
							newStartOffset + currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.WHITESPACE;
					break;

				case '"':
					addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
							newStartOffset + currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
					break;

				default:

					if (RSyntaxUtilities.isDigit(c)) {
						break; // Still a literal number.
					}

					// Otherwise, remember this was a number and start over.
					addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
							newStartOffset + currentTokenStart);
					i--;
					currentTokenType = Token.NULL;

				} // End of switch (c).

				break;

			case Token.COMMENT_EOL:
				i = end - 1;
				addToken(text, currentTokenStart, i, currentTokenType, newStartOffset + currentTokenStart);
				// We need to set token type to null so at the bottom we don't
				// add one more token.
				currentTokenType = Token.NULL;
				break;

			case Token.LITERAL_STRING_DOUBLE_QUOTE:
				if (c == '"') {
					addToken(text, currentTokenStart, i, Token.LITERAL_STRING_DOUBLE_QUOTE,
							newStartOffset + currentTokenStart);
					currentTokenType = Token.NULL;
				}
				break;

			} // End of switch (currentTokenType).

		} // End of for (int i=offset; i<end; i++).

		switch (currentTokenType) {

		// Remember what token type to begin the next line with.
		case Token.LITERAL_STRING_DOUBLE_QUOTE:
			addToken(text, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart);
			break;

		// Do nothing if everything was okay.
		case Token.NULL:
			addNullToken();
			break;

		// All other token types don't continue to the next line...
		default:
			addToken(text, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart);
			addNullToken();

		}

		// Return the first token in our linked list.
		return firstToken;

	}

	@Override
	public TokenMap getWordsToHighlight() {
		TokenMap tokenMap = new TokenMap();

		tokenMap.put("TYPEDEF", Token.RESERVED_WORD);
		tokenMap.put("CONSTDEF", Token.RESERVED_WORD);
		tokenMap.put("FUNCDEF", Token.RESERVED_WORD);
		tokenMap.put("SPECDEF", Token.RESERVED_WORD);
		tokenMap.put("ADAPDEF", Token.RESERVED_WORD);
		tokenMap.put("SUTDEF", Token.RESERVED_WORD);
		tokenMap.put("PROCDEF", Token.RESERVED_WORD);
		tokenMap.put("MODELDEF", Token.RESERVED_WORD);
		tokenMap.put("CNECTDEF", Token.RESERVED_WORD);
		tokenMap.put("ENDDEF", Token.RESERVED_WORD);
		
		tokenMap.put("CHAN", Token.RESERVED_WORD_2);
		tokenMap.put("BEHAVIOUR", Token.RESERVED_WORD_2);
		tokenMap.put("SUT", Token.RESERVED_WORD_2);
		tokenMap.put("MAP", Token.RESERVED_WORD_2);
		tokenMap.put("SOCK", Token.RESERVED_WORD_2);
		tokenMap.put("HOST", Token.RESERVED_WORD_2);
		tokenMap.put("PORT", Token.RESERVED_WORD_2);
		tokenMap.put("ENCODE", Token.RESERVED_WORD_2);
		tokenMap.put("DECODE", Token.RESERVED_WORD_2);
		tokenMap.put("CLIENTSOCK", Token.RESERVED_WORD_2);
		tokenMap.put("SERVERSOCK", Token.RESERVED_WORD_2);
		
		tokenMap.put("String", Token.VARIABLE);
		tokenMap.put("Int", Token.VARIABLE);

		tokenMap.put("::=", Token.SEPARATOR);
		tokenMap.put("?", Token.SEPARATOR);
		tokenMap.put("!", Token.SEPARATOR);
		
		tokenMap.put("|||", Token.OPERATOR);
		tokenMap.put("::", Token.OPERATOR);
		tokenMap.put("+", Token.OPERATOR);
		tokenMap.put("-", Token.OPERATOR);
		tokenMap.put("++", Token.OPERATOR);
		tokenMap.put("->", Token.OPERATOR);
		tokenMap.put("<-", Token.OPERATOR);
		tokenMap.put("#", Token.OPERATOR);
		tokenMap.put("##", Token.OPERATOR);
		
		tokenMap.put("LET", Token.PREPROCESSOR);
		tokenMap.put("IF", Token.PREPROCESSOR);
		tokenMap.put("ELSE", Token.PREPROCESSOR);
		tokenMap.put("THEN", Token.PREPROCESSOR);
		tokenMap.put("NI", Token.PREPROCESSOR);
		tokenMap.put("FI", Token.PREPROCESSOR);
		
		tokenMap.put("IN", Token.PREPROCESSOR);
		tokenMap.put("OUT", Token.PREPROCESSOR);
		
		tokenMap.put("--", Token.COMMENT_EOL);

		tokenMap.put("toString", Token.FUNCTION);
		tokenMap.put("fromString", Token.FUNCTION);

		
		
		return tokenMap;
	}
}
