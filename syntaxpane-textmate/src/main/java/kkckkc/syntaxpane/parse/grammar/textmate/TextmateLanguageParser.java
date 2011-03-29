package kkckkc.syntaxpane.parse.grammar.textmate;

import kkckkc.syntaxpane.parse.grammar.*;
import kkckkc.syntaxpane.parse.grammar.util.PatternSupplier;
import kkckkc.syntaxpane.regex.JoniPatternFactory;
import kkckkc.syntaxpane.regex.PatternFactory;
import kkckkc.utils.plist.PListReaderFacade;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class TextmateLanguageParser {
    private static Pattern CHANGE_BACKREF = Pattern.compile("\\\\([0-9]+)");
	private static final Comparator<SubPatternContext> SUBPATTERN_COMPARATOR = new Comparator<SubPatternContext>() {
        public int compare(SubPatternContext object1, SubPatternContext object2) {
	        return new Integer(object1.getSubPatternIdx()).compareTo(object2.getSubPatternIdx());
        }
	};

	private File file;
	private PatternFactory factory;
	private String rootScope;

	public TextmateLanguageParser(File file) {
		this.file = file;

        final PatternFactory delegate = new JoniPatternFactory();
		this.factory = new PatternFactory() {
            @Override
            public kkckkc.syntaxpane.regex.Pattern create(String s) {
                // TODO: Investigate if this is really needed
                //s = s.replaceAll("\\\\n", "\n");
                return delegate.create(s);
            }

            @Override
            public kkckkc.syntaxpane.regex.Pattern create(String s, int options) {
                // Not used
                return create(s);
            }
        };
	}

	public Language parse() throws IOException {
		PListReaderFacade plistReader = new PListReaderFacade();

		Map props = (Map) plistReader.read(file);
		
		Language language = new Language((String) props.get("scopeName"));
		language.setName((String) props.get("name"));
		
		if (props.containsKey("firstLineMatch"))
			language.setFirstLinePattern(new PatternSupplier((String) props.get("firstLineMatch"), factory));
		
		language.setFoldStart(new PatternSupplier((String) props.get("foldingStartMarker"), factory));
		language.setFoldEnd(new PatternSupplier((String) props.get("foldingStopMarker"), factory));
		
		List<String> fileTypes = (List<String>) props.get("fileTypes");
		if (fileTypes != null && ! fileTypes.isEmpty()) {
			StringBuilder fileTypePattern = new StringBuilder();
			for (String s : fileTypes) {
                fileTypePattern.append("(.*\\.").append(s).append(")|");
			}
			fileTypePattern.setLength(fileTypePattern.length() - 1);

			language.setFileNamePattern(new PatternSupplier(fileTypePattern.toString(), factory));
		}
		
		rootScope = (String) props.get("scopeName");
		RootContext rootContext = new RootContext(rootScope);
		
		List<Context> contexts = new ArrayList<Context>();
		for (Map entry : ((List<Map>) props.get("patterns"))) {
			contexts.add(parseContext(entry));
		}
		rootContext.setChildReferences(contexts.toArray(new Context[contexts.size()]));

		language.setRootContext(rootContext);
		
		if (props.containsKey("repository")) {
			contexts = new ArrayList<Context>();
			for (Map.Entry entry : ((Map<?, ?>) props.get("repository")).entrySet()) {
				Context c = buildContainerList((Map) entry.getValue(), (String) entry.getKey());
				c.setId((String) entry.getKey());
				c.setName((String) entry.getKey());
				
				if (c instanceof ContextList) {
					((ContextList) c).setBegin(null);
				} else {
					throw new RuntimeException("Unsupported context type " + c.getClass().getName() + " / " + c.getId());
				}
				contexts.add(c);
			}
			language.setSupportingContexts(contexts.toArray(new Context[contexts.size()]));
		}
		
		return language;
	}
	
	private Context parseContext(Map entry) {
		if (entry.containsKey("match")) {
			return parseSimpleContext(entry);
		} else if (entry.containsKey("include")) {
			return parseIncludeContext(entry);
		} else if (entry.containsKey("begin")) {
			return parseContainerContext(entry);
		} else {
			throw new RuntimeException("Unsupported context type " + entry);
		}
    }

	private Context buildContainerList(Map entry, String id) {
	    ContextList contextList = new ContextList(factory);

	    if (entry.containsKey("name") || entry.containsKey("begin") || entry.containsKey("match")) {
	    	Context context = parseContext(entry);
	    	if (context.getId() == null) {
		    	context.setId(id);
		    	context.setName(id);
	    	}
			contextList.setChildReferences(new Context[]{context});
	    } else {
	    	contextList.setName(id);
	    	contextList.setId(id);
	    	
			List<Map> patterns = (List<Map>) entry.get("patterns");
			List<Context> contexts = new ArrayList<Context>();
			if (patterns != null) {
				for (Map e : patterns) {
					contexts.add(parseContext(e));
				}
			}
			
			contextList.setChildReferences(contexts.toArray(new Context[contexts.size()]));
	    }
	    
	    return contextList;
    }

	private Context parseIncludeContext(Map entry) {
		String ref = (String) entry.get("include");
		
		if (ref.startsWith("#")) {
			return new ReferenceContext(ref.substring(1) + ":*");
			
		// TODO: Verify this
		} else if (ref.equals("$self") || ref.equals("$base")) {
			return new ReferenceContext(rootScope);
			
		} else {
			
			return new ReferenceContext(ref + ":" + ref);
			
		}
	}

	private Context parseContainerContext(Map entry) {
	    ContainerContext context = new ContainerContext(factory);
		context.setId((String) entry.get("name"));
		context.setName((String) entry.get("name"));

        if (entry.containsKey("applyEndPatternLast")) {
            context.setApplyEndPatternLast(true);
        } else {
            context.setApplyEndPatternLast(false);
        }

		if (entry.containsKey("disabled")) {
            context.setDisabled(true);
        } else {
            context.setDisabled(false);
        }

		if (entry.containsKey("begin")) 
			context.setBegin(factory.create((String) entry.get("begin")));
		if (entry.containsKey("end")) {
            context.setEnd(factory.create(fixBackrefs((String) entry.get("end"))));
        }

		List<Map> patterns = (List<Map>) entry.get("patterns");
		List<Context> contexts = new ArrayList<Context>();
		if (patterns != null) {
			for (Map e : patterns) {
				contexts.add(parseContext(e));
			}
		}

        if (entry.containsKey("contentName")) {
            ContainerContext containerContext = new ContainerContext(factory);
            containerContext.setId((String) entry.get("contentName"));
            containerContext.setName((String) entry.get("contentName"));
            containerContext.setContentNameContext(true);

            containerContext.setBegin(factory.create("(?=.)"));
            if (entry.containsKey("end"))
                containerContext.setEnd(factory.create("(?=" + fixBackrefs((String) entry.get("end")) + ")"));
            else
                containerContext.setEnd(factory.create("(?=.)"));

            containerContext.setChildReferences(contexts.toArray(new Context[contexts.size()]));

            contexts.clear();
            contexts.add(containerContext);
        }

		if (entry.containsKey("beginCaptures")) {
			List<SubPatternContext> dest = new ArrayList<SubPatternContext>();
			for (Map.Entry e : ((Map<?, ?>) entry.get("beginCaptures")).entrySet()) {
				SubPatternContext spc = new SubPatternContext();
				spc.setSubPattern((String) e.getKey());
				spc.setWhere(SubPatternContext.Where.START);
				spc.setId((String) ((Map) e.getValue()).get("name")); 
				dest.add(spc);
			}
			Collections.sort(dest, SUBPATTERN_COMPARATOR);
			contexts.addAll(dest);
		}
		
		if (entry.containsKey("endCaptures")) {
			List<SubPatternContext> dest = new ArrayList<SubPatternContext>();
			for (Map.Entry e : ((Map<?, ?>) entry.get("endCaptures")).entrySet()) {
				SubPatternContext spc = new SubPatternContext();
				spc.setSubPattern((String) e.getKey());
				spc.setWhere(SubPatternContext.Where.END);
				spc.setId((String) ((Map) e.getValue()).get("name")); 
				dest.add(spc);
			}
			Collections.sort(dest, SUBPATTERN_COMPARATOR);
			contexts.addAll(dest);
		}

        if (entry.containsKey("captures")) {
            List<SubPatternContext> dest = new ArrayList<SubPatternContext>();
            for (Map.Entry e : ((Map<?, ?>) entry.get("captures")).entrySet()) {
                SubPatternContext spc = new SubPatternContext();
                spc.setSubPattern((String) e.getKey());
                spc.setWhere(SubPatternContext.Where.START);
                spc.setId((String) ((Map) e.getValue()).get("name"));
                dest.add(spc);

                spc = new SubPatternContext();
                spc.setSubPattern((String) e.getKey());
                spc.setWhere(SubPatternContext.Where.END);
                spc.setId((String) ((Map) e.getValue()).get("name"));
                dest.add(spc);
            }
            Collections.sort(dest, SUBPATTERN_COMPARATOR);
            contexts.addAll(dest);
        }

		context.setChildReferences(contexts.toArray(new Context[contexts.size()]));
		
	    return context;
    }

    private String fixBackrefs(String s) {
        return CHANGE_BACKREF.matcher(s).replaceAll("\\\\%{$1@start}");
    }

    private Context parseSimpleContext(Map entry) {
		SimpleContext sc = new SimpleContext();
		sc.setId((String) entry.get("name")); 
		sc.setName((String) entry.get("name"));
		sc.setPattern(factory.create((String) entry.get("match")));

		if (entry.containsKey("captures")) {
			List<SubPatternContext> contexts = new ArrayList<SubPatternContext>();
			for (Map.Entry e : ((Map<?, ?>) entry.get("captures")).entrySet()) {
				SubPatternContext spc = new SubPatternContext();
				spc.setSubPattern((String) e.getKey());
				spc.setId((String) ((Map) e.getValue()).get("name")); 
				contexts.add(spc);
			}
			Collections.sort(contexts, SUBPATTERN_COMPARATOR);
			sc.setChildReferences(contexts.toArray(new Context[contexts.size()]));
		}
		
		return sc;
    }
	}
