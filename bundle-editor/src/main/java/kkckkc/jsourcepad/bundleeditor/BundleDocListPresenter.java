package kkckkc.jsourcepad.bundleeditor;

import kkckkc.jsourcepad.bundleeditor.command.CommandBundleDocPresenter;
import kkckkc.jsourcepad.bundleeditor.command.CommandBundleDocViewImpl;
import kkckkc.jsourcepad.bundleeditor.manifest.ManifestBundleDocPresenter;
import kkckkc.jsourcepad.bundleeditor.manifest.ManifestBundleDocViewImpl;
import kkckkc.jsourcepad.bundleeditor.model.BundleDocImpl;
import kkckkc.jsourcepad.bundleeditor.template.TemplateBundleDocPresenter;
import kkckkc.jsourcepad.bundleeditor.template.TemplateBundleDocViewImpl;
import kkckkc.jsourcepad.command.window.InsertTextCommand;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.bundle.BundleStructure;
import kkckkc.jsourcepad.ui.DocListPresenter;
import kkckkc.jsourcepad.ui.DocPresenter;
import kkckkc.jsourcepad.ui.InsertTextCommandManager;
import org.springframework.beans.factory.annotation.Autowired;

public class BundleDocListPresenter extends DocListPresenter {

    private InsertTextCommandManager insertTextCommandManager;

    @Autowired
    public void setInsertTextCommandManager(InsertTextCommandManager insertTextCommandManager) {
        this.insertTextCommandManager = insertTextCommandManager;
    }

    @Override
    protected DocPresenter createPresenter(Doc doc) {
        if (! (doc instanceof BundleDocImpl)) return super.createPresenter(doc);

        BundleDocImpl bdi = (BundleDocImpl) doc;

        if (bdi.getType() == null) {
            return super.createPresenter(doc);
        }

        if (bdi.getType() == BundleStructure.Type.COMMAND) {

            CommandBundleDocPresenter presenter = new CommandBundleDocPresenter();
            presenter.setDoc(doc);
            presenter.setWindow(docList.getWindow());
            presenter.setView(new CommandBundleDocViewImpl());
            presenter.setInsertTextCommandManager(insertTextCommandManager);
            presenter.init();

            return presenter;

        } else if (bdi.getType() == BundleStructure.Type.TEMPLATE) {

           TemplateBundleDocPresenter presenter = new TemplateBundleDocPresenter();
           presenter.setDoc(doc);
           presenter.setWindow(docList.getWindow());
           presenter.setView(new TemplateBundleDocViewImpl());
            presenter.setInsertTextCommandManager(insertTextCommandManager);
           presenter.init();

           return presenter;

        } else if (bdi.getType() == BundleStructure.Type.MANIFEST) {

            ManifestBundleDocPresenter presenter = new ManifestBundleDocPresenter();
            presenter.setDoc(doc);
            presenter.setWindow(docList.getWindow());
            presenter.setView(new ManifestBundleDocViewImpl());
            presenter.setInsertTextCommandManager(insertTextCommandManager);
            presenter.init();

            return presenter;


        } else {

            BasicBundleDocPresenter presenter = new BasicBundleDocPresenter();
            presenter.setDoc(doc);
            presenter.setWindow(docList.getWindow());
            presenter.setView(new BasicBundleDocViewImpl());
            presenter.setInsertTextCommandManager(insertTextCommandManager);
            presenter.init();

            return presenter;
        }
    }
}
