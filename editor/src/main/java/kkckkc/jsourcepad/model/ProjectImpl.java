package kkckkc.jsourcepad.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import kkckkc.jsourcepad.model.settings.IgnorePatternProjectSettings;
import kkckkc.jsourcepad.model.settings.ProjectSettingsManager;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.util.DefaultFileMonitor;
import kkckkc.jsourcepad.util.FileMonitor;
import kkckkc.jsourcepad.util.QueryUtils;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.utils.PerformanceLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class ProjectImpl implements Project, DocList.Listener, Window.FocusListener, FileMonitor.Listener, SettingsManager.Listener<IgnorePatternProjectSettings> {
	private static final int MAX_ENTRIES = 20;
	private Map<File, Long> lru = new LinkedHashMap<File, Long>(MAX_ENTRIES, .75F, true) {
		protected boolean removeEldestEntry(Map.Entry<File, Long> eldest) {
			return size() > MAX_ENTRIES;
		}
	};

	// Collaborators
	private File projectDir;
	private Window window;

	private final Set<File> cache = Sets.newHashSet();
	
	private DefaultFileMonitor fileMonitor;
	private List<File> selectedFiles = Lists.newArrayList();
    private SettingsManager settingsManager;

    private Predicate<File> predicate;

	@Autowired
	public void setWindow(Window window) {
	    this.window = window;
    }
	
	@Autowired
	public void setProjectDir(File projectDir) {
	    this.projectDir = projectDir;
    }
	
	@PostConstruct
	public void init() {
        if (projectDir != null) {

            window.topic(Window.FocusListener.class).subscribeWeak(DispatchStrategy.ASYNC, this);

            settingsManager = new ProjectSettingsManager(window, projectDir);
            settingsManager.subscribe(IgnorePatternProjectSettings.class, this, true, window);
        }
	}
	
	public File getProjectDir() {
		return projectDir;
	}

	@Override
    public void refresh(File file) {
	    window.topic(RefreshListener.class).post().refresh(file);
    	cache.clear();
    }

	@Override
    public List<File> findFile(final String query) {
		populateCacheIfEmpty();
		
		PerformanceLogger.get().enter(this, "findFile");
		
		List<File> dest = Lists.newArrayList();
		
		Predicate<String> predicate = QueryUtils.makePredicate(query);
		
		for (File f : cache) {
			if (predicate.apply(f.getName())) dest.add(f);
		}
		
		Ordering<File> scoringOrdering = Ordering.natural().onResultOf(
				new Function<File, Integer>() {
                    public Integer apply(File from) {
                    	int score = 0;
                    	
                    	// Score by depth. longer path decreases score
                    	score -= StringUtils.countOccurrencesOf(from.getPath(), File.separator);
                    	
                    	// Score by matching characters, matches late in string decreases score
                        score -= QueryUtils.getScorePenalty(from.getName(), query);
                    	
                		// Increase score by looking a LRU
                		Long l = lru.get(from);
                		if (l != null) {
                			score += System.currentTimeMillis() - l;
                		}
                		
	                    return score;
                    }
				}).reverse();
		
		Collections.sort(dest, scoringOrdering);
		
		PerformanceLogger.get().exit();
		
	    return dest;
    }

	private void populateCacheIfEmpty() {
		synchronized (cache) {
			if (cache.isEmpty()) {
		    	populateCacheRecusively(predicate, projectDir, 10000);
		    }
		}
    }
	
	@Override
    public void closed(int index, Doc doc) {
    }

	@Override
    public void created(Doc doc) {
		lru.put(doc.getFile(), System.currentTimeMillis());
    }

	@Override
    public void selected(int index, Doc doc) {
		lru.put(doc.getFile(), System.currentTimeMillis());
    }

	@Override
    public String getProjectRelativePath(String path) {
	    String s = path.substring(projectDir.toString().length());
	    if ("".equals(s)) return "/";
	    return s;
    }

	private void populateCacheRecusively(Predicate<File> condition, File file, int maxFiles) {
		if (maxFiles <= 0) return;
		
		if (! file.isDirectory()) {
			if (condition.apply(file)) cache.add(file);
			
			return;
		}
		
		File[] children = file.listFiles();
		if (children == null) return;
		
		for (File f : children) {
			populateCacheRecusively(condition, f, maxFiles - 1);
		}
	}
	
	@Override
    public void focusLost(Window window) {
    }

	@Override
    public void focusGained(Window window) {
		fileMonitor.requestRescan();
        synchronized (cache) {
            cache.clear();
        }
    }

	@Override
    public void fileCreated(File file) {
	    refresh(file);
    }

	@Override
    public void fileRemoved(File file) {
	    refresh(file);
    }

	@Override
    public void fileUpdated(File file) {
	    refresh(file);
    }

	@Override
    public List<File> getSelectedFiles() {
	    return selectedFiles;
    }

	@Override
    public void setSelectedFiles(List<File> paths) {
	    this.selectedFiles = paths;
    }

    @Override
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    @Override
    public Predicate<File> getFilePredicate() {
        return predicate;
    }

    @Override
    public void register(File file) {
        fileMonitor.register(file, predicate);
    }

    @Override
    public void unregister(File file) {
        fileMonitor.unregister(file);
    }

    @Override
    public void settingUpdated(IgnorePatternProjectSettings settings) {
        IgnorePatternProjectSettings ignorePatternProjectSettings = getSettingsManager().get(IgnorePatternProjectSettings.class);
        final Pattern pattern = Pattern.compile(ignorePatternProjectSettings.getPattern());

        predicate = new Predicate<File>() {
            public boolean apply(File input) {
                return ! pattern.matcher(input.getName()).matches();
            }
        };

        // TODO: Reregister all open files
        fileMonitor = new DefaultFileMonitor(Application.get().getThreadPool());
        fileMonitor.addListener(this);
        fileMonitor.register(getProjectDir(), predicate);
    }
}
