package kkckkc.jsourcepad.ui.dialog.settings;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.model.settings.SettingsPanel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SettingsDialog implements Dialog<SettingsView>, BeanFactoryAware {

    private SettingsView view;
    private BeanFactory beanFactory;
    private List<SettingsPanel> panels;

    @Override
    public void close() {
        view.getJDialog().dispose();
    }

    @Override
    @Autowired
    public void setView(SettingsView view) {
        this.view = view;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    public void init() {
        Map<String, SettingsPanel> panelMap = ((ListableBeanFactory) beanFactory).getBeansOfType(SettingsPanel.class);

        panels = Lists.newArrayList();
        panels.addAll(panelMap.values());
        Collections.sort(panels, new Comparator<SettingsPanel>() {
             public int compare(SettingsPanel o1, SettingsPanel o2) {
                 return o1.getOrder() - o2.getOrder();
             }
         });
 
        for (SettingsPanel panel : panels) {
            if (panel.getOrder() == SettingsPanel.ORDER_DONT_SHOW) continue;
            
            view.addSettingsPanel(panel.getName(), panel.getView().getJPanel());
        }

        view.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        view.getOkButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAndClose();
            }
        });

        view.getApplyButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
    }


    public void saveAndClose() {
        save();
        close();
    }

    private void save() {
        boolean requiresRestart = false;
        for (SettingsPanel panel : panels) {
            requiresRestart |= panel.save();
        }

        if (requiresRestart)
            JOptionPane.showMessageDialog(view.getJDialog(), "Some changes requires a restart before they take effect");
    }

    public void show() {
        for (SettingsPanel panel : panels) {
            panel.load();
        }
        view.getJDialog().pack();
        view.getJDialog().setVisible(true);
    }
}
