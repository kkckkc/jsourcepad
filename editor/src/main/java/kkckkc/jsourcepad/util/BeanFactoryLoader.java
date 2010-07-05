package kkckkc.jsourcepad.util;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.theme.Theme;

public class BeanFactoryLoader {
	public interface Scope<P> { 
		public String getResource();
	}
	
	public static final Scope<Void> APPLICATION = new Scope<Void>() {
        public String getResource() {
	        return "/application.xml";
        }
	};
	
	public static final Scope<Application> WINDOW = new Scope<Application>() {
        public String getResource() {
	        return "/window.xml";
        }
	};
	
	public static final Scope<Window> DOCUMENT = new Scope<Window>() {
        public String getResource() {
	        return "/document.xml";
        }
	};
	
	
	@SuppressWarnings("unchecked")
    public DefaultListableBeanFactory load(Scope<?> scope) {
		return load((Scope<ScopeRoot>) scope, null);
	}
	
	public <P extends ScopeRoot> DefaultListableBeanFactory load(Scope<P> scope, P parent) {
		PerformanceLogger.get().enter(this, "load");
		
		DefaultListableBeanFactory container;
		if (parent == null) {
			container = new DefaultListableBeanFactory();
		} else {
			container = new DefaultListableBeanFactory(parent.getBeanFactory());
		}

		
		AutowiredAnnotationBeanPostProcessor a = new AutowiredAnnotationBeanPostProcessor();
		a.setBeanFactory(container);
		container.addBeanPostProcessor(a);
		
		InitDestroyAnnotationBeanPostProcessor p = new InitDestroyAnnotationBeanPostProcessor();
		p.setDestroyAnnotationType(PreDestroy.class);
		p.setInitAnnotationType(PostConstruct.class);
		
		container.addBeanPostProcessor(p);
		
		
		XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(container);
		xmlBeanDefinitionReader.loadBeanDefinitions(new ClassPathResource(scope.getResource()));

		// Load theme overrides
		if (parent != null) {
			Theme theme = Application.get().getTheme();
			Resource themeResource = theme.getOverridesLocation(scope);
			if (themeResource != null) {
				xmlBeanDefinitionReader.loadBeanDefinitions(themeResource);
			}
		}

		
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		ppc.postProcessBeanFactory(container);
		
		
		
		PerformanceLogger.get().exit();
		
		return container;
	}
}
