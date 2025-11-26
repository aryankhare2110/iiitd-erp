package edu.univ.erp.ui.faculty.panels;

import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.domain.Faculty;
import edu.univ.erp.service.FacultyService;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class ScoresPanel extends JPanel {
    private final FacultyService facultyService;
    private Faculty faculty;

    public ScoresPanel() {
        facultyService = new FacultyService();
        faculty = facultyService.getMyProfile();
        int instr_id = faculty.getFacultyId();

    }

    public void refresh() {}
}
