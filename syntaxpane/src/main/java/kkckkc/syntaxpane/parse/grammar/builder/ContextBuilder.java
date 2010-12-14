package kkckkc.syntaxpane.parse.grammar.builder;

import kkckkc.syntaxpane.parse.grammar.*;
import kkckkc.syntaxpane.regex.NamedPatternFactory;


public class ContextBuilder<T extends Context, U> implements Builder<T> {
	protected T t;
	
	public ContextBuilder(T t) {
		this.t = t;
	}

	public T build() {
		return t;
	}

	
	public static ReferenceContextBuilder referenceContext() {
		return new ReferenceContextBuilder(new ReferenceContext());
	}

	public static ReferenceContextBuilder referenceContext(String ref) {
		return new ReferenceContextBuilder(new ReferenceContext(ref));
	}

	
	public static SubPatternContextBuilder subPatternContext() {
		return new SubPatternContextBuilder(new SubPatternContext());
	}

	public static SubPatternContextBuilder subPatternContext(String id) {
		return new SubPatternContextBuilder(new SubPatternContext(id));
	}

	
	public static SimpleContextBuilder simpleContext() {
		return new SimpleContextBuilder(new SimpleContext());
	}

	public static SimpleContextBuilder simpleContext(String id) {
		return new SimpleContextBuilder(new SimpleContext(id));
	}
	

	public static KeywordContextBuilder keywordContext() {
		return new KeywordContextBuilder(new KeywordContext(new NamedPatternFactory()));
	}

	public static KeywordContextBuilder keywordContext(String id) {
		return new KeywordContextBuilder(new KeywordContext(new NamedPatternFactory(), id));
	}

	
	public static ContainerContextBuilder containerContext() {
		return new ContainerContextBuilder(new ContainerContext(new NamedPatternFactory()));
	}

	public static ContainerContextBuilder containerContext(String id) {
		return new ContainerContextBuilder(new ContainerContext(new NamedPatternFactory(), id));
	}

	
	public static RootContextBuilder rootContext(String id) {
		return new RootContextBuilder(new RootContext(id));
	}

}
