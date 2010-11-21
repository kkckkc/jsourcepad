package kkckkc.syntaxpane.parse;

import javax.swing.text.AbstractDocument.Content;
import javax.swing.text.BadLocationException;


public class ContentCharProvider extends AbstractCharProvider {
	private Content content;
	
	public ContentCharProvider(Content content) {
		super();
		this.content = content;
	}

	@Override
	public int getLength() {
		return content.length();
	}

	@Override
	public CharSequence getSubSequence(int start, int end) {
		try {
			return content.getString(start, end - start);
		} catch (BadLocationException e) {
			System.out.println(start + " - " + end + " (" + content.length() + ")");
			throw new RuntimeException(e);
		}
	}

}
