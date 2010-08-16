package kkckkc.jsourcepad.model.bundle.snippet;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Keymap;

import kkckkc.jsourcepad.model.Anchor;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;
import kkckkc.jsourcepad.model.bundle.EnvironmentProvider;
import kkckkc.jsourcepad.model.bundle.snippet.SnippetParser.Literal;
import kkckkc.jsourcepad.model.bundle.snippet.SnippetParser.Node;
import kkckkc.jsourcepad.model.bundle.snippet.SnippetParser.NodeVisitor;
import kkckkc.jsourcepad.model.bundle.snippet.SnippetParser.Script;
import kkckkc.jsourcepad.model.bundle.snippet.SnippetParser.Variable;
import kkckkc.jsourcepad.util.io.ScriptExecutor;
import kkckkc.jsourcepad.util.io.UISupportCallback;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Execution;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.util.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Snippet {
	
	public static final int INCREMENT = 1;
	public static final int DECREMENT = -1;
	
	private Collection<Node> nodes;
	private BundleItemSupplier bundleItemSupplier;

	// State
	private Map<String, String> environment;
	private List<SnippetConstituent> constituents;
	private Buffer buffer;
	private int currentTabStop;
	
	private boolean changeTrackingEnabled;
	
	public Snippet(String snippet, BundleItemSupplier bundleItemSupplier) {
		this.nodes = new SnippetParser().parse(snippet);
		this.bundleItemSupplier = bundleItemSupplier;
	}

	public void insert(final Window window, final Buffer buffer) {
		this.buffer = buffer;
		this.constituents = Lists.newArrayList();
		this.environment = EnvironmentProvider.getEnvironment(window, bundleItemSupplier);
		
		final Set<Integer> tabStops = Sets.newHashSet();
		final StringBuilder b = new StringBuilder();
		
		for (SnippetParser.Node node : nodes) {
			node.accept(new CompilingVisitor(window, b, tabStops));
		}
		
		String str = b.toString();
		int firstLineLength = str.indexOf('\n');
		
		String currentLine = buffer.getCurrentLine();
		
		int position = buffer.getInsertionPoint().getPosition();
		buffer.insertText(position, str, getAnchors());
		
		if (firstLineLength >= 0) {
			int endPosition = buffer.getInsertionPoint().getPosition();
			int tabCount = buffer.getDoc().getTabManager().getTabCount(currentLine);
			Interval snippetInterval = new Interval(position + firstLineLength + 1, endPosition);
			buffer.shift(snippetInterval, tabCount);
		}
		
		buffer.beginRestrictedEditing(new SnippetRestrictedEditor());
		this.changeTrackingEnabled = true;
		
		currentTabStop = -1;
		SnippetConstituent nextConstituent = findNextTabStop();
		if (nextConstituent != null) {
			currentTabStop = nextConstituent.getTabStopId();
			buffer.setSelection(new Interval(
					nextConstituent.getBounds().getFirst().getPosition(),
					nextConstituent.getBounds().getSecond().getPosition()));
		} else {
			buffer.endRestrictedEditing();
		}
	}

	private Anchor[] getAnchors() {
		List<Anchor> anchors = Lists.newArrayList();
		for (SnippetConstituent c : constituents) {
			anchors.add(c.getBounds().getFirst());
			anchors.add(c.getBounds().getSecond());
		}
	    return anchors.toArray(new Anchor[] {});
    }

	private SnippetConstituent findWithTabStopId(int id, int increment) {
		int maxTabStopId = findMaxTabStopId();			 
		
		while (id > 0 && id <= maxTabStopId) {
			for (SnippetConstituent c : constituents) {
				if (c.isCopy() || ! c.isActive()) continue;
				if (c.getTabStopId() == id) return c;
			}			
			id += increment;
		}
		
		// As fallback if next is not found, use index 0
		if (id > maxTabStopId) {
			for (SnippetConstituent c : constituents) {
				if (c.isCopy()) continue;
				if (c.getTabStopId() == 0)return c;
			}
		}
		
		return null;
	}

	private int findMaxTabStopId() {
	    int maxTabStopId = 0;
		for (SnippetConstituent c : constituents) {
			if (c.isCopy() || ! c.isActive()) continue;
			maxTabStopId = Math.max(maxTabStopId, c.getTabStopId());
		}
	    return maxTabStopId;
    }
	
	private SnippetConstituent findNextTabStop() {
		if (currentTabStop == 0) return null;
		if (currentTabStop == -1) {
			return findWithTabStopId(1, INCREMENT);
		} else {
			return findWithTabStopId(currentTabStop + 1, INCREMENT);
		}
	}

	private SnippetConstituent findPreviousTabStop() {
		if (currentTabStop == 1) return null;
		if (currentTabStop == 0) {
			return findWithTabStopId(findMaxTabStopId(), DECREMENT);
		} else {
			return findWithTabStopId(currentTabStop - 1, DECREMENT);
		}
	}
	
	
	class CompilingVisitor implements NodeVisitor {
	    private final Window window;
	    private final StringBuilder b;
	    private final Set<Integer> tabStops;

	    private CompilingVisitor(Window window, StringBuilder b, Set<Integer> tabStops) {
		    this.window = window;
		    this.b = b;
		    this.tabStops = tabStops;
	    }

	    @Override
	    public void visit(Literal literal) {
	        b.append(literal.getString());
	    }

	    @Override
	    public void visit(Variable variable) {
	        Anchor start = new Anchor(b.length(), Anchor.Bias.LEFT); 
	    	
	    	if (! variable.isTabStop()) {
	    		b.append(variable.evaluate(environment));
	        }

	    	for (Node n : variable.children()) {
	    		n.accept(this);
	    	}	                

	        if (variable.isTabStop()) {
	        	int tabStopId = Integer.parseInt(variable.getName());

	        	if (! tabStops.contains(tabStopId)) {
	        		environment.put(Integer.toString(tabStopId), b.substring(start.getPosition()));
	        	} else {
	        		b.append(environment.get(Integer.toString(tabStopId)));
	        	}
    	        Anchor end = new Anchor(b.length(), Anchor.Bias.RIGHT);
	        	
	        	constituents.add(new SnippetConstituent(
	        			variable,
	        			new Pair<Anchor, Anchor>(start, end),
	        			tabStopId,
	        			tabStops.contains(tabStopId)
	        	));
	        	tabStops.add(tabStopId);
	        }
	    }

	    @Override
	    public void visit(Script script) {
	        ScriptExecutor scriptExecutor = new ScriptExecutor(script.getBody(), Application.get().getThreadPool());
	        try {
	            Execution ex = scriptExecutor.execute(new UISupportCallback(window.getJFrame()), 
	            		new StringReader(""), 
	            		environment);
	            
	            ex.waitForCompletion();
	            
	            b.append(ex.getStdout());
	            
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        } catch (InterruptedException e) {
	            throw new RuntimeException(e);
	        } catch (ExecutionException e) {
	            throw new RuntimeException(e);
	        }
	    }
    }

	class SnippetConstituent {
		private Pair<Anchor, Anchor> bounds;
		private int tabStopId;
		private boolean copy;
		private boolean active;
		private Variable variable;

		public SnippetConstituent(Variable variable, Pair<Anchor, Anchor> bounds, int tabStopId, boolean copy) {
			this.variable = variable;
	        this.bounds = bounds;
	        this.tabStopId = tabStopId;
	        this.copy = copy;
	        this.active = true;
        }
		
		public boolean isActive() {
	        return active;
        }
		
		public void setActive(boolean active) {
	        this.active = active;
        }
		
		public Pair<Anchor, Anchor> getBounds() {
        	return bounds;
        }

		private Interval getBoundsAsInterval() {
	        return new Interval(bounds.getFirst().getPosition(), bounds.getSecond().getPosition());
        }

		public int getTabStopId() {
        	return tabStopId;
        }

		public boolean isCopy() {
        	return copy;
        }
		
		public String toString() {
			return tabStopId + ", " + copy + ", " + bounds;
		}

		public void textChanged(DocumentEvent de) {
			assert EventQueue.isDispatchThread() : "Only to be called from the EventQueue";
			
			if (copy) return;
			
			Interval i = getBoundsAsInterval();
			
			environment.put(Integer.toString(tabStopId), buffer.getText(i));
			
			for (final SnippetConstituent c : constituents) {
				if (c.isCopy() && c.getTabStopId() == tabStopId) {
					final String updatedValue = variable.evaluate(environment);

					try {
						changeTrackingEnabled = false;
						buffer.replaceText(
								c.getBoundsAsInterval(), 
								updatedValue, null);
					} finally {
						changeTrackingEnabled = true;
					}
				}
			}
		}
	}
	
	
	class SnippetRestrictedEditor implements Buffer.RestrictedEditor {
		private static final String SHIFT_TAB = "shift TAB";
		private static final String TAB = "TAB";
		
		private Keymap keymap;
		
		private Action tabAction;
		private Action shiftTabAction;
		
		@Override
	    public void init(Keymap keymap) {
		    this.keymap = keymap;
		    
		    // Save
		    tabAction = keymap.getAction(KeyStroke.getKeyStroke(TAB));
		    shiftTabAction = keymap.getAction(KeyStroke.getKeyStroke(SHIFT_TAB));

		    // Remove
	    	keymap.removeKeyStrokeBinding(KeyStroke.getKeyStroke(TAB));
	    	keymap.removeKeyStrokeBinding(KeyStroke.getKeyStroke(SHIFT_TAB));
		    
		    // Install new actions
	    	keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(TAB), new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
            		SnippetConstituent nextConstituent = findNextTabStop();
            		if (nextConstituent != null) {
            			currentTabStop = nextConstituent.getTabStopId();
            			buffer.setSelection(new Interval(
            					nextConstituent.getBounds().getFirst().getPosition(),
            					nextConstituent.getBounds().getSecond().getPosition()));
            			if (currentTabStop == 0) {
            				destroy();
            			}
            		}
                }
	    	});
	    	keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(SHIFT_TAB), new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
            		SnippetConstituent nextConstituent = findPreviousTabStop();
            		if (nextConstituent != null) {
            			currentTabStop = nextConstituent.getTabStopId();
            			buffer.setSelection(new Interval(
            					nextConstituent.getBounds().getFirst().getPosition(),
            					nextConstituent.getBounds().getSecond().getPosition()));
            		}
                }
	    	});
	    }

		private void destroy() {
	    	buffer.endRestrictedEditing();
	    	
	    	keymap.removeKeyStrokeBinding(KeyStroke.getKeyStroke(TAB));
	    	keymap.removeKeyStrokeBinding(KeyStroke.getKeyStroke(SHIFT_TAB));
	    	
	    	keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(TAB), tabAction);
	    	if (shiftTabAction != null) {
	    		keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(SHIFT_TAB), shiftTabAction);
	    	}
        }

		
		@Override
	    public void caretPositionChanged(int position) {
		    if (isOutsideOfAnchor(position)) {
		    	destroy();
		    	return;
		    }
	    }

		@Override
	    public void textChanged(final DocumentEvent de) {
			if (! changeTrackingEnabled) return;
			
		    if (isOutsideOfAnchor(de.getOffset() + de.getLength())) {
		    	destroy();
		    	return;
		    }

		    Interval changeInterval = new Interval(de.getOffset(), de.getOffset() + de.getLength());
		    
			for (final SnippetConstituent c : constituents) {
				Pair<Anchor, Anchor> pair = c.getBounds();
		    	if (c.getBoundsAsInterval().overlaps(changeInterval)) {
					if (de.getLength() > (pair.getSecond().getPosition() - pair.getFirst().getPosition())) {
			    		EventQueue.invokeLater(new Runnable() {
	                        public void run() {
	                        	c.setActive(false);
	        		    		c.textChanged(de);
	                        }
			    		});
					} else {
			    		EventQueue.invokeLater(new Runnable() {
	                        public void run() {
	        		    		c.textChanged(de);
	                        }
			    		});
					}
		    	}
			}
		}

		private boolean isOutsideOfAnchor(int position) {
			for (SnippetConstituent c : constituents) {
				Pair<Anchor, Anchor> pair = c.getBounds();
		    	if (pair.getFirst().getPosition() <= position && pair.getSecond().getPosition() >= position) {
		    		return false;
		    	}
		    }
		    return true;
	    }
	}
}
