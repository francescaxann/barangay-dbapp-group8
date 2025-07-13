import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StaffPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField[] fields;
    private JComboBox<String> statusCombo;

    public StaffPanel() {
        setLayout(new BorderLayout());
        initializeUI();
        loadStaff();
    }

    private void initializeUI() {
        // Table setup
        model = new DefaultTableModel(new String[] {
            "ID", "Resident ID", "Position", "Start Term", "End Term", "Status", "Assigned Area"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);

        // Form setup
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        String[] labels = {"Resident ID", "Position", "Start Term (yyyy-mm-dd)", 
                          "End Term (yyyy-mm-dd)", "Status", "Assigned Area"};

        fields = new JTextField[labels.length];
        for (int i = 0; i < fields.length; i++) {
            if (i == 4) { // Status
                statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
                form.add(new JLabel(labels[i]));
                form.add(statusCombo);
            } else {
                form.add(new JLabel(labels[i]));
                fields[i] = new JTextField();
                form.add(fields[i]);
            }
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");

        addBtn.addActionListener(e -> addStaff());
        updateBtn.addActionListener(e -> updateStaff());
        deleteBtn.addActionListener(e -> deleteStaff());
        clearBtn.addActionListener(e -> clearForm());

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        // Selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromSelectedRow();
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(form, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadStaff() {
        model.setRowCount(0);
        try (Connection con = DBConnector.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Staff");
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getInt("staffID"),
                    rs.getInt("residentID"),
                    rs.getString("position"),
                    rs.getDate("startTerm").toString(),
                    rs.getDate("endTerm").toString(),
                    rs.getString("officeStatus"),
                    rs.getString("assignedArea")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading staff: " + e.getMessage());
        }
    }

    private void addStaff() {
        try {
            String sql = "INSERT INTO Staff (residentID, position, startTerm, endTerm, officeStatus, assignedArea) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (Connection con = DBConnector.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, Integer.parseInt(fields[0].getText()));
                ps.setString(2, fields[1].getText());
                ps.setString(3, fields[2].getText());
                ps.setString(4, fields[3].getText());
                ps.setString(5, (String) statusCombo.getSelectedItem());
                ps.setString(6, fields[5].getText());
                
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Staff member added successfully!");
                loadStaff();
                clearForm();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding staff: " + e.getMessage());
        }
    }

    private void updateStaff() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to update");
            return;
        }

        int staffId = (int) model.getValueAt(selectedRow, 0);
        
        try {
            String sql = "UPDATE Staff SET residentID=?, position=?, startTerm=?, " +
                        "endTerm=?, officeStatus=?, assignedArea=? WHERE staffID=?";
            
            try (Connection con = DBConnector.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, Integer.parseInt(fields[0].getText()));
                ps.setString(2, fields[1].getText());
                ps.setString(3, fields[2].getText());
                ps.setString(4, fields[3].getText());
                ps.setString(5, (String) statusCombo.getSelectedItem());
                ps.setString(6, fields[5].getText());
                ps.setInt(7, staffId);
                
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Staff member updated successfully!");
                    loadStaff();
                    clearForm();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating staff: " + e.getMessage());
        }
    }

    private void deleteStaff() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to delete");
            return;
        }

        int staffId = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Delete staff member ID " + staffId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnector.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM Staff WHERE staffID=?")) {
                
                ps.setInt(1, staffId);
                int deleted = ps.executeUpdate();
                if (deleted > 0) {
                    JOptionPane.showMessageDialog(this, "Staff member deleted successfully!");
                    loadStaff();
                    clearForm();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting staff: " + e.getMessage());
            }
        }
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            clearForm();
            fields[0].setText(model.getValueAt(selectedRow, 1).toString());
            fields[1].setText(model.getValueAt(selectedRow, 2).toString());
            fields[2].setText(model.getValueAt(selectedRow, 3).toString());
            fields[3].setText(model.getValueAt(selectedRow, 4).toString());
            statusCombo.setSelectedItem(model.getValueAt(selectedRow, 5).toString());
            fields[5].setText(model.getValueAt(selectedRow, 6).toString());
        }
    }

    private void clearForm() {
        for (JTextField field : fields) {
            if (field != null) field.setText("");
        }
        if (statusCombo != null) statusCombo.setSelectedIndex(0);
        table.clearSelection();
    }
}