package kkckkc.jsourcepad.ui.dialog.navigation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.Bundle;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;
import kkckkc.jsourcepad.util.AutoSuggestUtils;
import kkckkc.syntaxpane.model.Scope;
import kkckkc.syntaxpane.style.Match;
import kkckkc.utils.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;

public class BundleItemNavigationDialog extends NavigationDialog {

    private Window window;

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    public void show() {
        super.show();
    }
    
	public void valueChanged(ListSelectionEvent e) {
		BundleItemSupplier obj = (BundleItemSupplier) view.getResult().getSelectedValue();
		if (obj == null) {
			view.getPath().setText("Bundle: ");
		} else {
			view.getPath().setText("Bundle: " + getBundleName(obj));
		}
	}

    private String getBundleName(BundleItemSupplier bis) {
        return FileUtils.getBaseName(bis.getFile().getParentFile().getParentFile());
    }

    @Override
    public void init() {
        super.init();

        view.getJDialog().setTitle("Select Bundle Item");
        view.getResult().setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

                BundleItemSupplier bis = (BundleItemSupplier) value;
                return super.getListCellRendererComponent(list, bis.getName() + " - " + getBundleName(bis), index, isSelected, cellHasFocus);
            }
        });
    }

    @Override
    public void keyReleased(KeyEvent e) {
		if (e.getSource() instanceof JTextField) {
			if (view.getTextField().getText().length() > 0) {
				view.getResult().setListData(getBundleItems(view.getTextField().getText()).toArray());
				view.getResult().setSelectedIndex(0);
			} else {
				view.getResult().setListData(new Object[] {});
			}
		}
    }

    private Collection<BundleItemSupplier> getBundleItems(final String query) {
        java.util.List<BundleItemSupplier> all = Lists.newArrayList();

        for (Bundle bundle : Application.get().getBundleManager().getBundles()) {
            all.addAll(bundle.getItems());
        }

        java.util.List<BundleItemSupplier> dest = Lists.newArrayList();

        Predicate<String> predicate = AutoSuggestUtils.makePredicate(query);
        Predicate<BundleItemSupplier> scopePredicate = Predicates.alwaysTrue();

        Doc activeDoc = window.getDocList().getActiveDoc();
        if (activeDoc != null) {
            final Scope scope = activeDoc.getActiveBuffer().getInsertionPoint().getScope();
            scopePredicate = new Predicate<BundleItemSupplier>() {
                @Override
                public boolean apply(BundleItemSupplier input) {
                    if (input.getActivator().getScopeSelector() == null) return true;
                    Match match = input.getActivator().getScopeSelector().matches(scope, -1);
                    return match != null && match.isMatch();
                }
            };
        }

        for (BundleItemSupplier p : all) {
            if (predicate.apply(p.getName()) && scopePredicate.apply(p)) {
                dest.add(p);
            }
        }

        Ordering<BundleItemSupplier> scoringOrdering = Ordering.natural().onResultOf(
                new Function<BundleItemSupplier, Integer>() {
                    public Integer apply(BundleItemSupplier p) {
                        int score = 0;

                        // Score by matching characters, matches late in string decreases score
                        score -= AutoSuggestUtils.getScorePenalty(p.getName(), query);

                        return score;
                    }
                }).reverse();

        Collections.sort(dest, scoringOrdering);

        return dest;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			executeSelected();
		}
    }

	private void executeSelected() {
	    if (view.getResult().getSelectedValue() == null) return;

        BundleItemSupplier bis = (BundleItemSupplier) view.getResult().getSelectedValue();
        try {
            bis.get().execute(window, null);
            close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onEnter() {
        executeSelected();
    }

}