package kkckkc.jsourcepad.model;

import kkckkc.utils.Pair;

import java.util.List;
import java.util.Map;

public interface MacroManager {
    public void startRecording();
    public void stopRecording();
    public void abortRecording();

    boolean isRecording();
    boolean hasRecordedMacro();

    public Macro getRecordedMacro();
    public Macro makeMacro(List<Pair<Class, Map<String, ?>>> steps);

    public interface Macro {
        public List<Pair<Class, Map<String, ?>>> getSteps();
        public void execute();
    }

    public interface Listener {
        public void startRecording();
        public void stopRecording();
    }
}
