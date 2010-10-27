package kkckkc.jsourcepad.theme.substance;

import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.model.Project;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.ProjectViewImpl;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.springframework.beans.factory.annotation.Autowired;

public class SubstanceProjectViewImpl extends ProjectViewImpl {

    @Autowired
    public SubstanceProjectViewImpl(Project project, Window window, DocList docList) {
        super(project, window, docList);

        SubstanceLookAndFeel.setDecorationType(this, DecorationAreaType.GENERAL);
    }


}
