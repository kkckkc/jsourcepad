package kkckkc.jsourcepad.dialog.tmdialog.dialogs.html;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.dialog.tmdialog.BaseTmDialogDelegate;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InsertEntity extends BaseTmDialogDelegate {
    private JList list;
    private JCheckBox checkbox;
    private DefaultListModel listModel;

    @Override
    protected void buildDialog(JPanel panel) {
        panel.setLayout(new MigLayout("insets dialog", "[grow]", "[grow]r[]u[]"));

        listModel = new DefaultListModel();
        list = new JList(listModel);
        checkbox = new JCheckBox();

        panel.add(new JScrollPane(list), "grow,wrap");

        panel.add(checkbox, "split");
        panel.add(new JLabel("Insert as entity"), "wrap");

        panel.add(new JButton(OK_ACTION), "wrap, tag ok");

        jdialog.setTitle("Insert Entity");
    }

    @Override
    public void load(boolean isFirstTime, Map object) {
        List<Map> entities = (List<Map>) object.get("entities");
        Boolean insertAsEntity = (Boolean) object.get("insertAsEntity");

        if (insertAsEntity != null) checkbox.setSelected(insertAsEntity);
        if (entities != null) {
            for (Map m : entities) {
                listModel.addElement(new Entry(
                        (String) m.get("display"),
                        (String) m.get("char"),
                        (String) m.get("entity")
                ));
            }
        }
    }

    @Override
    protected Object getReturnData() {
        Map returnData = Maps.newHashMap();
        Map result = Maps.newHashMap();

        returnData.put("result", result);

        result.put("asEntity", checkbox.isSelected() ? "1" : "0");

        Entry entry = (Entry) list.getSelectedValue();
        result.put("returnArgument", Arrays.asList(entry.asMap()));

        return returnData;
    }

    static class Entry {
        String display;
        String character;
        String entity;

        Entry(String name, String character, String entity) {
            this.display = name;
            this.character = character;
            this.entity = entity;
        }

        public String toString() {
            return display;
        }

        public Map asMap() {
            Map map = Maps.newHashMap();
            map.put("char", character);
            map.put("entity", entity);
            return map;
        }
    }
}
