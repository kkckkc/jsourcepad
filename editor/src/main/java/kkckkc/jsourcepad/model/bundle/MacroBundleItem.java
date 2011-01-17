package kkckkc.jsourcepad.model.bundle;

import kkckkc.jsourcepad.model.MacroManager;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.macro.MacroEncodingManager;
import kkckkc.utils.Pair;

import java.util.List;
import java.util.Map;

public class MacroBundleItem implements BundleItem<Void> {
    private BundleItemSupplier bundleItemSupplier;
    private List commands;

    public MacroBundleItem(BundleItemSupplier bundleItemSupplier, List commands) {
        this.bundleItemSupplier = bundleItemSupplier;
        this.commands = commands;
    }

    @Override
    public void execute(Window window, Void context) throws Exception {
        List<Pair<Class,Map<String,?>>> steps = MacroEncodingManager.decodeTextMateFormat(commands);
        MacroManager.Macro macro = window.getMacroManager().makeMacro(steps);
        macro.execute();
    }

    @Override
    public BundleStructure.Type getType() {
        return BundleStructure.Type.MACRO;
    }

    public static BundleItem create(BundleItemSupplier bundleItemSupplier, Map props) {
        return new MacroBundleItem(bundleItemSupplier, (List) props.get("commands"));
    }
}
