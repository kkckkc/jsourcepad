package kkckkc.jsourcepad.bundleeditor;

import com.google.common.base.Strings;
import kkckkc.jsourcepad.bundleeditor.model.BundleDocImpl;
import kkckkc.jsourcepad.model.bundle.BundleStructure;
import kkckkc.jsourcepad.model.bundle.TextmateKeystrokeEncoding;
import kkckkc.jsourcepad.ui.DocPresenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BasicBundleDocPresenter extends DocPresenter {
    @Override
    public void init() {
        super.init();

        final BundleDocImpl bDoc = (BundleDocImpl) doc;
        final BasicBundleDocViewImpl bView = (BasicBundleDocViewImpl) view;

        bView.getScope().setText(Strings.nullToEmpty(bDoc.getScope()));
        if (Strings.isNullOrEmpty(bDoc.getKeyEquivalent())) {
            bView.getKeyEquivalent().setText("");
        } else {
            String s = TextmateKeystrokeEncoding.parse(bDoc.getKeyEquivalent()).toString();
            s = s.replaceAll("pressed ", "").replaceAll("typed ", "");
            bView.getKeyEquivalent().setText(s);
        }
        bView.getTabTrigger().setText(bDoc.getTabTrigger());
        bView.getName().setText(bDoc.getName());

        if (! Strings.isNullOrEmpty(bDoc.getKeyEquivalent())) bView.getActivation().setSelectedItem(BasicBundleDocViewImpl.KEY_EQUIVALENT);
        if (! Strings.isNullOrEmpty(bDoc.getTabTrigger())) bView.getActivation().setSelectedItem(BasicBundleDocViewImpl.TAB_TRIGGER);

        if (bDoc.getType() == BundleStructure.Type.SYNTAX) {
            bView.getScope().setEnabled(false);
        }

        bDoc.setSaveCallback(new Runnable() {
            @Override
            public void run() {
                saveCallback();
            }
        });

        register(bView.getKeyEquivalent());
        register(bView.getName());
        register(bView.getScope());
        register(bView.getTabTrigger());
        register(bView.getActivation());

        bView.getKeyEquivalent().addFocusListener(new FocusListener() {

            boolean disabled = false;

            @Override
            public void focusGained(FocusEvent focusEvent) {
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                if (Strings.isNullOrEmpty(bView.getKeyEquivalent().getText())) return;

                if (focusEvent.isTemporary()) {
                    return;
                }

                KeyStroke ks = KeyStroke.getKeyStroke(bView.getKeyEquivalent().getText());
                if (ks == null) {
                    JOptionPane optionPane = new JOptionPane("The value must be a valid keystroke",
                        JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
                    optionPane.createDialog(bView.getKeyEquivalent(), null).show();

                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            bView.getKeyEquivalent().requestFocus();
                        }
                    });
                }
            }
        });
    }

    protected void saveCallback() {
        BundleDocImpl bDoc = (BundleDocImpl) doc;
        BasicBundleDocViewImpl bView = (BasicBundleDocViewImpl) view;

        bDoc.setScope(bView.getScope().getText());
        bDoc.setName(bView.getName().getText());
        if (bView.getActivation().getSelectedItem().equals(BasicBundleDocViewImpl.KEY_EQUIVALENT)) {
            bDoc.setKeyEquivalent(TextmateKeystrokeEncoding.toString(KeyStroke.getKeyStroke(bView.getKeyEquivalent().getText())));
            bDoc.setTabTrigger(null);
        } else {
            bDoc.setKeyEquivalent(null);
            bDoc.setTabTrigger(bView.getTabTrigger().getText());
        }
    }

    protected void register(JTextField jtf) {
        jtf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                BundleDocImpl bDoc = (BundleDocImpl) doc;
                bDoc.setModified(true);
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }
        });
    }


    protected void register(JComboBox jcb) {
        jcb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    BundleDocImpl bDoc = (BundleDocImpl) doc;
                    bDoc.setModified(true);
                }
            }
        });
    }

}
