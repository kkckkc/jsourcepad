package kkckkc.jsourcepad.model;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.model.settings.ProjectSetting;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.util.Null;
import kkckkc.jsourcepad.util.action.AcceleratorManager;
import kkckkc.jsourcepad.util.action.ActionManager;
import kkckkc.jsourcepad.util.command.CommandExecutor;
import kkckkc.jsourcepad.util.messagebus.AbstractMessageBus;
import kkckkc.jsourcepad.util.messagebus.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;


public class WindowImpl extends AbstractMessageBus implements MessageBus, ScopeRoot, BeanFactoryAware, Window {

    private int id;
	private DefaultListableBeanFactory beanFactory;

	// Collaborators
	private ActionManager actionManager;

	private ScriptEngine scriptEngine;
    private JFrame container;

    public WindowImpl() {
        container = new JFrame();
    }

    @NotNull
    public ScriptEngine getScriptEngine() {
		if (scriptEngine == null) {
			ScriptEngineManager mgr = new ScriptEngineManager();
			
			scriptEngine = mgr.getEngineByName("JavaScript");
			Bindings bindings = scriptEngine.createBindings();
			bindings.put("window", this);
			bindings.put("app", Application.get());
			scriptEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
		}
		
		return scriptEngine;
	}

    @NotNull
    @Override
    public CommandExecutor getCommandExecutor() {
        return beanFactory.getBean(CommandExecutor.class);
    }

    public int getId() {
        return id;
    }

    @Override
    public void beginWait(boolean show, Runnable cancelAction) {
        RootPaneContainer root = container;
        root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (show) {
            root.getGlassPane().setBackground(Color.black);
        } else {
            root.getGlassPane().setBackground(new Color(0, 0, 0, 0f));
        }
        if (! root.getGlassPane().isVisible())
            root.getGlassPane().setVisible(true);
    }

    @Override
    public void endWait() {
        RootPaneContainer root = container;
        root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        root.getGlassPane().setVisible(false);
        root.getGlassPane().setBackground(null);
    }

    public void setId(int id) {
        this.id = id;
    }

    @Autowired
	public void setActionManager(ActionManager actionManager) {
	    this.actionManager = actionManager;
    }
	
	@NotNull
    @Override
	public Project getProject() {
		Project project = beanFactory.getBean(Project.class);
        if (project.getProjectDir() == null) return new NullProjectImpl();
        return project;
	}

	@NotNull
    @Override
	public DocList getDocList() {
		return beanFactory.getBean(DocList.class);
	}

	@Override
	public <T> T getPresenter(Class<? extends T> clazz) {
		return beanFactory.getBean(clazz);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (DefaultListableBeanFactory) beanFactory;
	}

	@NotNull
    @Override
	public ActionManager getActionManager() {
		return actionManager;
	}

    @NotNull
    @Override
    public AcceleratorManager getAcceleratorManager() {
        return beanFactory.getBean(AcceleratorManager.class);
    }

    public DefaultListableBeanFactory getBeanFactory() {
	    return beanFactory;
    }

    @Override
    public JFrame getContainer() {
        return container;
    }

    @Override
    public void requestFocus() {
        container.requestFocus();
    }

    @Override
    public void saveState() {
        WindowState windowState = new WindowState();

        List<String> openFiles = Lists.newArrayList();
        for (Doc doc : getDocList().getDocs()) {
            if (! doc.isBackedByFile()) continue;
            openFiles.add(doc.getFile().toString());
        }

        windowState.setOpenFiles(openFiles);
        windowState.setBounds(container.getBounds());
        Doc activeDoc = getDocList().getActiveDoc();
        windowState.setSelectedFile(activeDoc == null ? null :
            activeDoc.getFile() == null ? null : activeDoc.getFile().toString());

        getProject().getSettingsManager().update(windowState);
    }

    @Override
    public void restoreState() {
        WindowState windowState = getProject().getSettingsManager().get(WindowState.class);
        if (windowState != null) {
            container.setBounds(windowState.getBounds());

            for (String file : windowState.getOpenFiles()) {
                getDocList().open(new File(file));
            }

            if (Null.Utils.isNull(getProject()) &&
                    windowState.openFiles.isEmpty() && getDocList().getDocCount() == 0) {
                getDocList().create();
            }

            if (windowState.getSelectedFile() != null) {
                for (Doc doc : getDocList().getDocs()) {
                    if (new File(windowState.getSelectedFile()).equals(doc.getFile())) {
                        getDocList().setActive(doc);
                        break;
                    }
                }
            }
        }
    }


    public static class WindowState implements ProjectSetting {

        private Rectangle bounds;
        private List<String> openFiles;
        private String selectedFile;

        public String getSelectedFile() {
            return selectedFile;
        }

        public void setSelectedFile(String selectedFile) {
            this.selectedFile = selectedFile;
        }

        public Rectangle getBounds() {
            return bounds;
        }

        public void setBounds(Rectangle position) {
            this.bounds = position;
        }

        public List<String> getOpenFiles() {
            return openFiles;
        }

        public void setOpenFiles(List<String> openFiles) {
            this.openFiles = openFiles;
        }

        @Override
        public SettingsManager.Setting getDefault() {
            return null;
        }
    }
}
