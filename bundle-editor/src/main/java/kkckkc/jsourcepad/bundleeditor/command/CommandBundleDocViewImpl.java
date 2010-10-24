package kkckkc.jsourcepad.bundleeditor.command;

import kkckkc.jsourcepad.bundleeditor.BasicBundleDocViewImpl;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CommandBundleDocViewImpl extends BasicBundleDocViewImpl {
    private JComboBox input;
    private JComboBox save;
    private JComboBox secondayInput;
    private JComboBox output;
    private JPanel secondaryInputPanel;
    
    public static final String SAVE_NOTHING = "Nothing";
    public static final String SAVE_CURRENT_FILE = "Current File";
    public static final String SAVE_ALL_FILES_IN_PROJECT = "All Files in Project";

    public static final String OUTPUT_DISCARD = "Discard";
    public static final String OUTPUT_REPLACE_SELECTED_TEXT = "Replace Selected Text";
    public static final String OUTPUT_REPLACE_DOCUMENT = "Replace Document";
    public static final String OUTPUT_INSERT_AS_TEXT = "Insert as Text";
    public static final String OUTPUT_INSERT_AS_SNIPPET = "Insert as Snippet";
    public static final String OUTPUT_SHOW_AS_HTML = "Show as HTML";
    public static final String OUTPUT_SHOW_AS_TOOLTIP = "Show as Tooltip";
    public static final String OUTPUT_CREATE_NEW_DOCUMENT = "Create New Document";

    public static final String INPUT_NONE = "None";
    public static final String INPUT_SELECTED_TEXT = "Selected Text";
    public static final String INPUT_ENTIRE_DOCUMENT = "Entire Document";
    public static final String INPUT2_DOCUMENT = "Document";

    public static final String INPUT2_LINE = "Line";
    public static final String INPUT2_WORD = "Word";
    public static final String INPUT2_CHARACTER = "Character";
    public static final String INPUT2_SCOPE = "Scope";
    public static final String INPUT2_NOTHING = "Nothing";

    @Override
    protected void initComponents() {
        super.initComponents();

        save = new JComboBox(new String[] { SAVE_NOTHING, SAVE_CURRENT_FILE, SAVE_ALL_FILES_IN_PROJECT });
        output = new JComboBox(new String[] {
                OUTPUT_DISCARD, OUTPUT_REPLACE_SELECTED_TEXT, OUTPUT_REPLACE_DOCUMENT, OUTPUT_INSERT_AS_TEXT,
                OUTPUT_INSERT_AS_SNIPPET, OUTPUT_SHOW_AS_HTML, OUTPUT_SHOW_AS_TOOLTIP, OUTPUT_CREATE_NEW_DOCUMENT });
        input = new JComboBox(new String[] { INPUT_NONE, INPUT_SELECTED_TEXT, INPUT_ENTIRE_DOCUMENT });
        secondayInput = new JComboBox(new String[] {
                INPUT2_DOCUMENT, INPUT2_LINE, INPUT2_WORD, INPUT2_CHARACTER, INPUT2_SCOPE, INPUT2_NOTHING });


        secondaryInputPanel = new JPanel();
        secondaryInputPanel.setLayout(new CardLayout());

        JPanel secondaryInputPanelContents = new JPanel();
        secondaryInputPanelContents.setLayout(new FlowLayout());
        secondaryInputPanelContents.add(new JLabel("or"));
        secondaryInputPanelContents.add(secondayInput);

        secondaryInputPanel.add(new JPanel(), INPUT_NONE);
        secondaryInputPanel.add(secondaryInputPanelContents, INPUT_SELECTED_TEXT);
        secondaryInputPanel.add(new JPanel(), INPUT_ENTIRE_DOCUMENT);

        input.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                ((CardLayout) secondaryInputPanel.getLayout()).show(secondaryInputPanel, (String) itemEvent.getItem());
            }
        });
    }

    protected void layout() {
        panel = new JPanel();
        panel.setLayout(new MigLayout("insets panel", "[right][grow,10sp]", "[]r[]r[]r[grow]r[]r[]r[]r[]r[]r[]"));

        layoutHeader();

        panel.add(new JLabel("Save:"));
        panel.add(save, "wrap");

        panel.add(new JLabel("Command(s):"), "top");
        panel.add(getSourcePane(), "gapx 3 3,grow,wrap");

        panel.add(new JLabel("Input:"));
        panel.add(input, "split");
        panel.add(secondaryInputPanel, "wrap");

        panel.add(new JLabel("Output:"));
        panel.add(output, "wrap");

        layoutFooter();
    }

    public JComboBox getInput() {
        return input;
    }

    public JComboBox getSave() {
        return save;
    }

    public JComboBox getSecondayInput() {
        return secondayInput;
    }

    public JComboBox getOutput() {
        return output;
    }
}
