package kkckkc.jsourcepad.model;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.util.command.Command;
import kkckkc.jsourcepad.util.command.CommandExecutor;
import kkckkc.jsourcepad.util.command.CommandMapperManager;
import kkckkc.jsourcepad.util.command.WindowCommand;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.Subscription;
import kkckkc.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;

public class MacroManagerImpl implements MacroManager, CommandExecutor.Listener {
    private CommandMapperManager commandMapperManager;
    private Window window;
    private Subscription subscription;
    private boolean recording;
    private List<Pair<Class, Map<String, ?>>> currentMacro;
    private Macro macro;

    @Autowired
    public void setCommandMapperManager(CommandMapperManager commandMapperManager) {
        this.commandMapperManager = commandMapperManager;
    }

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @PreDestroy
    public void destroy() {
        if (subscription != null) subscription.unsubscribe();
    }

    @Override
    public void startRecording() {
        window.topic(MacroManager.Listener.class).post().startRecording();
        subscription = Application.get().topic(CommandExecutor.Listener.class).subscribe(DispatchStrategy.SYNC, this);
        recording = true;
        currentMacro = Lists.newArrayList();
        macro = null;
    }

    @Override
    public void abortRecording() {
        subscription.unsubscribe();
        window.topic(MacroManager.Listener.class).post().stopRecording();
        recording = false;
    }

    @Override
    public void stopRecording() {
        subscription.unsubscribe();
        window.topic(MacroManager.Listener.class).post().stopRecording();
        recording = false;

        macro = new MacroImpl(currentMacro, commandMapperManager);
    }

    @Override
    public boolean isRecording() {
        return recording;
    }

    @Override
    public boolean hasRecordedMacro() {
        return macro != null;
    }

    @Override
    public Macro getRecordedMacro() {
        return macro;
    }

    @Override
    public Macro makeMacro(List<Pair<Class, Map<String, ?>>> steps) {
        return new MacroImpl(steps, commandMapperManager);
    }

    @Override
    public void commandExecuted(Command command) {
        if (recording) {
            if (! (command instanceof WindowCommand)) return;

            WindowCommand windowCommand = (WindowCommand) command;
            if (windowCommand.getWindow() != window) return;

            Pair<Class, Map<String, ?>> externalRepresentation = commandMapperManager.toExternalRepresentation(command);
            currentMacro.add(externalRepresentation);
        }
    }


    private static class MacroImpl implements Macro {
        private final List<Pair<Class, Map<String, ?>>> steps;
        private CommandMapperManager commandMapperManager;

        public MacroImpl(List<Pair<Class, Map<String, ?>>> steps, CommandMapperManager commandMapperManager) {
            this.steps = steps;
            this.commandMapperManager = commandMapperManager;
        }

        @Override
        public List<Pair<Class, Map<String, ?>>> getSteps() {
            return steps;
        }

        @Override
        public void execute() {
            for (Pair<Class, Map<String, ?>> step : steps) {
                Command command = commandMapperManager.fromExternalRepresentation(step);
                command.execute();
            }
        }
    }
}
