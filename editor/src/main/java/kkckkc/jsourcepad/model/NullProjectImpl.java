package kkckkc.jsourcepad.model;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.util.Null;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class NullProjectImpl implements Null, Project {
    @Override
    public List<File> findFile(String query) {
        return Collections.emptyList();
    }

    @Override
    public File getProjectDir() {
        return null;
    }

    @Override
    public void refresh(File file) {
    }

    @Override
    public String getProjectRelativePath(String path) {
        return path;
    }

    @Override
    public List<File> getSelectedFiles() {
        return Collections.emptyList();
    }

    @Override
    public void setSelectedFiles(List<File> paths) {
    }

    @Override
    public SettingsManager getSettingsManager() {
        return Application.get().getSettingsManager();
    }

    @Override
    public Predicate<File> getFilePredicate() {
        return Predicates.alwaysFalse();
    }

    @Override
    public void register(File file) {
    }

    @Override
    public void unregister(File file) {
    }

    @Override
    public ProjectFinder getFinder() {
        return null;
    }

    @Override
    public ProjectFinder newFinder(File baseDirectory, String searchFor, ProjectFinder.Options options) {
        return null;  
    }
}
