package kkckkc.jsourcepad.util;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.PluginManager;
import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.theme.Theme;
import kkckkc.utils.DomUtil;
import kkckkc.utils.IteratorIterable;
import kkckkc.utils.PerformanceLogger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Iterator;
import java.util.ServiceLoader;

public class BeanFactoryLoader {
    private static final FastDocumentLoader FAST_DOCUMENT_LOADER = new FastDocumentLoader();

    private void loadXml(XmlBeanDefinitionReader xmlBeanDefinitionReader, Scope<?> scope) throws BeanDefinitionStoreException {
        // Improve performance by reusing XML parsers
        xmlBeanDefinitionReader.setDocumentLoader(FAST_DOCUMENT_LOADER);
        xmlBeanDefinitionReader.loadBeanDefinitions(new ClassPathResource(scope.getResource()));
    }

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

        container.setAllowEagerClassLoading(false);
		
		AutowiredAnnotationBeanPostProcessor a = new AutowiredAnnotationBeanPostProcessor();
		a.setBeanFactory(container);
		container.addBeanPostProcessor(a);
		
		InitDestroyAnnotationBeanPostProcessor p = new InitDestroyAnnotationBeanPostProcessor();
		p.setDestroyAnnotationType(PreDestroy.class);
		p.setInitAnnotationType(PostConstruct.class);
		
		container.addBeanPostProcessor(p);
		
		
		XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(container);
        
		loadXml(xmlBeanDefinitionReader, scope);

		// Load theme overrides
        for (Plugin plugin : PluginManager.getActivePlugins()) {
            Resource r = plugin.getOverridesLocation(scope);
            if (r == null) continue;

            xmlBeanDefinitionReader.loadBeanDefinitions(r);
        }
		
		PerformanceLogger.get().exit();
		
		return container;
	}

    private static class FastDocumentLoader implements DocumentLoader {
        public Document loadDocument(InputSource inputSource, EntityResolver entityResolver, ErrorHandler errorHandler, int validationMode, boolean namespaceAware) throws Exception {
            return DomUtil.parse(inputSource);
        }
    }
}
