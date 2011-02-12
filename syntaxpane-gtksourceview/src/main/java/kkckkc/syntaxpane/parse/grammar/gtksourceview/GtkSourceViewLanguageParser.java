package kkckkc.syntaxpane.parse.grammar.gtksourceview;

import com.google.common.base.Suppliers;
import kkckkc.syntaxpane.parse.grammar.*;
import kkckkc.syntaxpane.parse.grammar.SubPatternContext.Where;
import kkckkc.syntaxpane.parse.grammar.util.PatternSupplier;
import kkckkc.syntaxpane.regex.NamedPatternFactory;
import kkckkc.syntaxpane.regex.Pattern;
import kkckkc.syntaxpane.regex.PatternFactory;
import kkckkc.utils.DomUtil;
import kkckkc.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kkckkc.utils.DomUtil.*;

public class GtkSourceViewLanguageParser {
	private File file;
	private String languageId;
	private PatternFactory factory;
	
	public GtkSourceViewLanguageParser(File file) {
		this.file = file;
		this.factory = new NamedPatternFactory();
	}

	public GtkSourceViewLanguage parse() throws IOException {
        Document doc = DomUtil.parse(file);

        languageId = doc.getDocumentElement().getAttribute("id");

        GtkSourceViewLanguage language = new GtkSourceViewLanguage(languageId);
        
        Element metadataElement = getChild(doc.getDocumentElement(), "metadata");
        if (metadataElement != null) {
        	for (Element prop : getChildren(metadataElement, "property")) {
        		String key = prop.getAttribute("name");
        		String value = getText(prop);
        		
        		if (key.equals("globs")) {
        			language.setFileNamePattern(
        					Suppliers.memoize(
        							new PatternSupplier(makeRegexpFromGlob(value), factory)));
        		}
	        }
        }
        
        Map<String, String> styles = new HashMap<String, String>();
        Element stylesElement = getChild(doc.getDocumentElement(), "styles");
        if (stylesElement != null) {
        	for (Element style : getChildren(stylesElement, "style")) {
	        	styles.put(style.getAttribute("id"), style.getAttribute("map-to"));
	        }	
        }
        language.setStyles(styles);
        

        Element definitions = getChild(doc.getDocumentElement(), "definitions");
        if (definitions != null) {
	        Map<String, String> regexps = new HashMap<String, String>();
	        for (Element regexp : getChildren(definitions, "define-regex")) {
	        	regexps.put(regexp.getAttribute("id"), getText(regexp)); 
	        }
	        
	        language.setDefinedRegexps(regexps);
        }
        
        if (definitions != null) {
	        List<Context> supportingContexts = new ArrayList<Context>();
	        for (Context cr : parseChildren(getChildren(definitions, "context"))) {
	        	if (languageId.equals(cr.getId())) {
	        		language.setRootContext(cr);
	        	} else {
	        		supportingContexts.add(cr);
	        	}
	        }
	        
	        language.setSupportingContexts(supportingContexts.toArray(new Context[] {}));
        }
        
        return language;
	}

	private String makeRegexpFromGlob(String value) {
		value = "(" + StringUtils.replace(value, ";", ")|(") + ")";
		value = StringUtils.replace(value, ".", "\\.");
		value = StringUtils.replace(value, "*", ".*");
		value = StringUtils.replace(value, "?", ".?");
		return value;
	}

	private List<Context> parseChildren(Iterable<Element> children) {
		List<Context> contexts = new ArrayList<Context>();
        for (Element context : children) {
        	if (languageId.equals(context.getAttribute("id"))) {
        		contexts.add(buildRootContext(context)); 
        	} else if (context.hasAttribute("ref")) {
        		contexts.add(buildContextReference(context));
        	} else if (context.hasAttribute("sub-pattern")) {
        		contexts.add(buildSubPatternContext(context));
        	} else if (getChild(context, "keyword") != null) {
        		contexts.add(buildKeywordContext(context));
        	} else if (getChild(context, "start") != null || getChild(context, "include") != null) {
        		contexts.add(buildContainerContext(context));
        	} else if (getChild(context, "match") != null) {
        		contexts.add(buildSimpleContext(context));
        	} else {
        		System.out.println("Unknown: " + context.getAttribute("id"));
        	}
        }
        return contexts;
	}

	private Context buildRootContext(Element context) {
		RootContext rootContext = new RootContext();
		rootContext.setId(context.getAttribute("id"));
		rootContext.setEndParent(makeBoolean(context.getAttribute("end-parent")));
		rootContext.setExtendParent(makeBoolean(context.getAttribute("extend-parent")));
		rootContext.setFirstLineOnly(makeBoolean(context.getAttribute("first-line-only")));
		rootContext.setOnceOnly(makeBoolean(context.getAttribute("once-only")));
		rootContext.setName(languageId);
		
		if (getChild(context, "include") != null) {
			List<Context> refs = parseChildren(getChildren(getChild(context, "include"), "context"));
			rootContext.setChildReferences(refs.toArray(new Context[] {}));
		}
		
		return rootContext;
	}



	private Context buildContainerContext(Element context) {
		final String start = getChildText(context, "start");
		final String end = getChildText(context, "end");
		
    	ContainerContext containerContext = new ContainerContext(factory) {
			public void compile() {
				if (isCompiled()) return;
				super.compile();
				compiled = true;
				
				this.beginPattern = makePattern((GtkSourceViewLanguage) language, start);
				this.endPattern = makePattern((GtkSourceViewLanguage) language, end);
				
				resolveStyle(this);
			}
		};
		containerContext.setId(context.getAttribute("id"));
		
		containerContext.setEndAtLineEnd(makeBoolean(context.getAttribute("end-at-line-end")));
		containerContext.setEndParent(makeBoolean(context.getAttribute("end-parent")));
		containerContext.setExtendParent(makeBoolean(context.getAttribute("extend-parent")));
		containerContext.setFirstLineOnly(makeBoolean(context.getAttribute("first-line-only")));
		containerContext.setOnceOnly(makeBoolean(context.getAttribute("once-only")));
		containerContext.setStyleInside(makeBoolean(context.getAttribute("style-inside")));
		containerContext.setName(context.getAttribute("style-ref"));
		
		if (getChild(context, "include") != null) {
			List<Context> refs = parseChildren(getChildren(getChild(context, "include"), "context"));
			containerContext.setChildReferences(refs.toArray(new Context[] {}));
		}
		
		return containerContext;
	}

	private Context buildSimpleContext(Element context) {
		final String match = getChildText(context, "match");
		SimpleContext simpleContext = new SimpleContext() {
			public void compile() {
				if (isCompiled()) return;
				super.compile();
				compiled = true;
			
				this.pattern = makePattern((GtkSourceViewLanguage) language, match);
				resolveStyle(this);
			}
		};
		simpleContext.setId(context.getAttribute("id"));
		simpleContext.setEndParent(makeBoolean(context.getAttribute("end-parent")));
		simpleContext.setExtendParent(makeBoolean(context.getAttribute("extend-parent")));
		simpleContext.setFirstLineOnly(makeBoolean(context.getAttribute("first-line-only")));
		simpleContext.setOnceOnly(makeBoolean(context.getAttribute("once-only")));
		simpleContext.setName(context.getAttribute("style-ref"));
		
		if (getChild(context, "include") != null) {
			List<Context> refs = parseChildren(getChildren(getChild(context, "include"), "context"));
			simpleContext.setChildReferences(refs.toArray(new Context[] {}));
		}

		return simpleContext;
	}

	private Context buildSubPatternContext(Element context) {
		SubPatternContext subPatternContext = new SubPatternContext() {
			public void compile() {
				if (isCompiled()) return;
				super.compile();
				compiled = true;
				
				resolveStyle(this);
			}
		};
		subPatternContext.setId(context.getAttribute("id"));
		subPatternContext.setSubPattern(context.getAttribute("sub-pattern"));
		subPatternContext.setName(context.getAttribute("style-ref"));
		
		if (context.hasAttribute("where")) {
			String where = context.getAttribute("where");
			if ("start".equals(where)) subPatternContext.setWhere(Where.START);
			else if ("end".equals(where)) subPatternContext.setWhere(Where.END);
		}
		
		return subPatternContext;
	}

	private Context buildKeywordContext(Element context) {
    	KeywordContext keywordContext = new KeywordContext(factory) {
			public void compile() {
				if (isCompiled()) return;
				
				if (prefix != null) 
					prefix = RegexpUtils.encode(prefix, ((GtkSourceViewLanguage) language).getDefinedRegexps());
				
				if (suffix != null) {
					suffix = RegexpUtils.encode(suffix, ((GtkSourceViewLanguage) language).getDefinedRegexps());
				}
				
				for (int i = 0; i < keywords.length; i++) {
					keywords[i] = RegexpUtils.encode(keywords[i], ((GtkSourceViewLanguage) language).getDefinedRegexps());
				}

				super.compile();
				compiled = true;
				
				
				resolveStyle(this);
			}
		};
		keywordContext.setId(context.getAttribute("id"));
		keywordContext.setPrefix(getChildText(context, "prefix"));
		keywordContext.setSuffix(getChildText(context, "suffix"));
		keywordContext.setEndParent(makeBoolean(context.getAttribute("end-parcent")));
		keywordContext.setExtendParent(makeBoolean(context.getAttribute("extend-parent")));
		keywordContext.setFirstLineOnly(makeBoolean(context.getAttribute("first-line-only")));
		keywordContext.setOnceOnly(makeBoolean(context.getAttribute("once-only")));
		keywordContext.setName(context.getAttribute("style-ref"));
		
		List<String> keywords = new ArrayList<String>();
		for (Element keyword : getChildren(context, "keyword")) {
			keywords.add(getText(keyword));
		}
		
		keywordContext.setKeywords(keywords.toArray(new String[] {}));
		
		return keywordContext;
	}

	private Context buildContextReference(Element context) {
		ReferenceContext referenceContext = new ReferenceContext();
		referenceContext.setRef(context.getAttribute("ref"));
		
		return referenceContext;
	}

	
	private void resolveStyle(Context context) {
		GtkSourceViewLanguage language = (GtkSourceViewLanguage) context.getLanguage();
		context.setName(language.resolveStyle(context.getName()));
	}

	private boolean makeBoolean(String attributeValue) {
        return attributeValue != null && "true".equals(attributeValue);
    }

	private Pattern makePattern(GtkSourceViewLanguage language, String str) {
		if (str == null) return null;
		return RegexpUtils.parse(str, language.getDefinedRegexps());
	}
}
