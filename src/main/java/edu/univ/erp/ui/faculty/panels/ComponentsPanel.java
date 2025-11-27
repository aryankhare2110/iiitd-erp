package edu. univ.erp.ui. faculty.panels;

import edu. univ.erp.domain.*;
import edu.univ.erp.service.FacultyService;
import edu.univ. erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common. UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ComponentsPanel extends JPanel {

    private final FacultyService facultyService = new FacultyService();
    private Faculty faculty;

    private JComboBox<String> sectionCombo;
    private List<Section> sections;

    private JTable componentsTable;
    private DefaultTableModel componentsModel;

    private JLabel totalWeightLabel;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    public ComponentsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        faculty = facultyService.getMyProfile();

        add(UIUtils.createHeaderWithBadge("Assessment Components",
                        "Define grading components with weights (must total 100%).  Grade scale: A: 90+, A-: 85+, B: 80+, B-: 75+, C: 70+, C-: 65+, D: 60+, F: <60",
                        facultyService.isMaintenanceMode(),
                        " ⚠ MAINTENANCE MODE "),
                BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel. setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(248, 249, 250));
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Section Selector
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setBackground(new Color(248, 249, 250));
        selectorPanel. setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        selectorPanel.add(UIUtils.makeLabel("Select Section:", true));
        sectionCombo = new JComboBox<>();
        sectionCombo.setPreferredSize(new Dimension(400, 30));
        sectionCombo. addActionListener(e -> loadComponents());
        selectorPanel.add(sectionCombo);

        mainPanel.add(selectorPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Total Weight Card
        JPanel weightCard = createWeightCard();
        mainPanel.add(weightCard);
        mainPanel.add(Box. createVerticalStrut(20));

        // Components Table
        componentsModel = new DefaultTableModel(
                new String[]{"Component Type", "Description", "Weight (%)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        componentsTable = UIUtils.createStyledTable(componentsModel);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(248, 249, 250));
        tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        tablePanel.add(new JScrollPane(componentsTable), BorderLayout.CENTER);

        mainPanel.add(tablePanel);
        add(mainPanel, BorderLayout.CENTER);

        // Buttons
        addButton = UIUtils.primaryButton("Add Component", e -> addComponent());
        editButton = UIUtils. secondaryButton("Edit Component", e -> editComponent());
        deleteButton = UIUtils.secondaryButton("Delete Component", e -> deleteComponent());

        add(UIUtils.createButtonRow(
                addButton,
                editButton,
                deleteButton), BorderLayout.SOUTH);

        loadSections();
        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean maintenanceMode = facultyService.isMaintenanceMode();
        addButton.setEnabled(!maintenanceMode);
        editButton.setEnabled(!maintenanceMode);
        deleteButton. setEnabled(!maintenanceMode);

        if (maintenanceMode) {
            sectionCombo.setEnabled(false);
        }
    }

    private JPanel createWeightCard() {
        JPanel card = new JPanel(new BorderLayout());
        card. setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content. setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Total Weight");
        titleLabel.setFont(new Font("Helvetica Neue", Font. PLAIN, 14));
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel. setAlignmentX(Component. LEFT_ALIGNMENT);

        totalWeightLabel = new JLabel("0%");
        totalWeightLabel.setFont(new Font("Helvetica Neue", Font. BOLD, 28));
        totalWeightLabel.setForeground(new Color(33, 37, 41));
        totalWeightLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(totalWeightLabel);

        card.add(content, BorderLayout. CENTER);
        return card;
    }

    private void loadSections() {
        sectionCombo.removeAllItems();
        if (faculty == null) return;

        sections = facultyService. getMySections();
        for (Section s : sections) {
            Course c = facultyService.getCourseById(s.getCourseID());
            if (c != null) {
                sectionCombo.addItem(c. getCode() + " - " + s.getTerm() + " " + s. getYear());
            }
        }

        if (! sections.isEmpty()) {
            loadComponents();
        }
    }

    private void loadComponents() {
        int idx = sectionCombo.getSelectedIndex();
        if (idx == -1 || sections.isEmpty()) return;

        Section section = sections.get(idx);
        componentsModel.setRowCount(0);
        List<SectionComponent> components = facultyService.getComponents(section. getSectionID());

        for (SectionComponent c : components) {
            String typeName = facultyService.getComponentTypeName(c.getTypeID());

            componentsModel.addRow(new Object[]{
                    typeName,
                    c.getDescription() != null ? c.getDescription() : "-",
                    c. getWeight()
            });
        }

        double totalWeight = facultyService.getTotalComponentWeight(section.getSectionID());
        totalWeightLabel.setText(String.format("%.0f%%", totalWeight));

        if (totalWeight == 100) {
            totalWeightLabel.setForeground(new Color(25, 135, 84)); // Green
        } else {
            totalWeightLabel.setForeground(new Color(220, 53, 69)); // Red
        }

        if (components.isEmpty()) {
            componentsModel.addRow(new Object[]{"No components defined", "", ""});
        }
    }

    private void addComponent() {
        if (facultyService.isMaintenanceMode()) {
            DialogUtils.errorDialog("Cannot add components during maintenance mode.");
            return;
        }

        int idx = sectionCombo.getSelectedIndex();
        if (idx == -1) {
            DialogUtils.errorDialog("Please select a section first.");
            return;
        }

        Section section = sections.get(idx);
        List<ComponentType> types = facultyService.getAllComponentTypes();

        if (types.isEmpty()) {
            DialogUtils.errorDialog("No component types available.  Please contact administrator.");
            return;
        }

        String[] typeNames = types.stream().map(ComponentType::getName).toArray(String[]::new);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel. setBorder(new EmptyBorder(10, 10, 10, 10));

        JComboBox<String> typeCombo = new JComboBox<>(typeNames);
        JTextField descField = new JTextField();
        JTextField weightField = new JTextField();

        panel.add(new JLabel("Component Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Description (optional):"));
        panel.add(descField);
        panel. add(new JLabel("Weight (%):"));
        panel.add(weightField);

        if (JOptionPane.showConfirmDialog(this, panel, "Add Component",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String typeName = (String) typeCombo.getSelectedItem();
            int typeId = types.stream()
                    . filter(t -> t.getName(). equals(typeName))
                    .findFirst(). get().getTypeID();

            String desc = descField.getText().trim();
            double weight = Double. parseDouble(weightField.getText(). trim());

            if (weight <= 0 || weight > 100) {
                DialogUtils.errorDialog("Weight must be between 0 and 100.");
                return;
            }

            SectionComponent component = new SectionComponent(
                    0,
                    section.getSectionID(),
                    typeId,
                    null,  // day - not needed for grading
                    null,  // start_time - not needed for grading
                    null,  // end_time - not needed for grading
                    weight,
                    desc.isEmpty() ? null : desc
            );

            if (facultyService.addComponent(component)) {
                DialogUtils. successDialog("Component added successfully!");
                loadComponents();
            } else {
                DialogUtils.errorDialog("Failed to add component.");
            }

        } catch (NumberFormatException e) {
            DialogUtils.errorDialog("Invalid weight format.  Please enter a number.");
        } catch (Exception e) {
            DialogUtils.errorDialog("Error: " + e. getMessage());
            e.printStackTrace();
        }
    }

    private void editComponent() {
        if (facultyService.isMaintenanceMode()) {
            DialogUtils.errorDialog("Cannot edit components during maintenance mode.");
            return;
        }

        int r = componentsTable.getSelectedRow();
        if (r == -1 || componentsModel.getValueAt(r, 0).equals("No components defined")) {
            DialogUtils. errorDialog("Please select a valid component to edit.");
            return;
        }

        int idx = sectionCombo.getSelectedIndex();
        Section section = sections.get(idx);
        List<SectionComponent> components = facultyService.getComponents(section.getSectionID());

        if (r >= components.size()) return;
        SectionComponent component = components.get(r);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel. setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField descField = new JTextField(component.getDescription() != null ? component.getDescription() : "");
        JTextField weightField = new JTextField(String.valueOf(component. getWeight()));

        panel.add(new JLabel("Description (optional):"));
        panel.add(descField);
        panel.add(new JLabel("Weight (%):"));
        panel.add(weightField);

        if (JOptionPane.showConfirmDialog(this, panel, "Edit Component",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double weight = Double.parseDouble(weightField.getText().trim());

            if (weight <= 0 || weight > 100) {
                DialogUtils.errorDialog("Weight must be between 0 and 100.");
                return;
            }

            String desc = descField. getText().trim();

            SectionComponent updated = new SectionComponent(
                    component.getComponentID(),
                    component.getSectionID(),
                    component.getTypeID(),
                    component. getDay(),       // Keep existing day
                    component.getStartTime(), // Keep existing start_time
                    component.getEndTime(),   // Keep existing end_time
                    weight,
                    desc.isEmpty() ? null : desc
            );

            if (facultyService.updateComponent(updated)) {
                DialogUtils.successDialog("Component updated successfully!");
                loadComponents();
            } else {
                DialogUtils.errorDialog("Failed to update component.");
            }

        } catch (NumberFormatException e) {
            DialogUtils.errorDialog("Invalid weight format. Please enter a number.");
        }
    }

    private void deleteComponent() {
        if (facultyService.isMaintenanceMode()) {
            DialogUtils.errorDialog("Cannot delete components during maintenance mode.");
            return;
        }

        int r = componentsTable.getSelectedRow();
        if (r == -1 || componentsModel.getValueAt(r, 0).equals("No components defined")) {
            DialogUtils.errorDialog("Please select a valid component to delete.");
            return;
        }

        if (! DialogUtils.confirmDialog("Are you sure you want to delete this component?  This will also delete all associated scores.")) {
            return;
        }

        int idx = sectionCombo.getSelectedIndex();
        Section section = sections.get(idx);
        List<SectionComponent> components = facultyService.getComponents(section.getSectionID());

        if (r >= components.size()) return;
        SectionComponent component = components.get(r);

        if (facultyService.deleteComponent(component. getComponentID())) {
            DialogUtils.successDialog("Component deleted successfully!");
            loadComponents();
        } else {
            DialogUtils.errorDialog("Failed to delete component.");
        }
    }

    public void refresh() {
        loadSections();
        updateButtonStates();
    }
}