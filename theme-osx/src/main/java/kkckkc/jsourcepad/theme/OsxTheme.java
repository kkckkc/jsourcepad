package kkckkc.jsourcepad.theme;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.jsourcepad.util.BeanFactoryLoader.Scope;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;



public class OsxTheme implements Theme {

	public OsxTheme() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		
		//UIDefaults defaults = UIManager.getDefaults( );
		//defaults.put( "TabbedPane.useSmallLayout", Boolean.TRUE );
	}
	
	@Override
	public String getLookAndFeel() {
		/*try {
			UIManager.setLookAndFeel(
			        ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel()
			    );
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return null;
	}

	@Override
    public Resource getOverridesLocation(Scope<?> scope) {
		if (scope == BeanFactoryLoader.DOCUMENT) {
			return new ClassPathResource("/osx-document.xml");
		} else if (scope == BeanFactoryLoader.WINDOW) {
			return new ClassPathResource("/osx-window.xml");
		}
		return null;
    }

}
