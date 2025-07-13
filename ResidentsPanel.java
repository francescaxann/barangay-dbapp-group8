import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ResidentsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField[] fields;
    private JComboBox<String> genderCombo, civilStatusCombo;

    public ResidentsPanel() {
        setLayout(new BorderLayout());
        initializeUI();
        loadResidents();
    }

    private void initializeUI() {
        // Table setup
        model = new DefaultTableModel(new String[] {
            "ID", "First Name", "Last Name", "Birth Date", "Gender", "Civil Status",
            "Occupation", "Contact", "Street", "Residency Start", "Rating"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);

        // Form setup
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        String[] labels = {"First Name", "Last Name", "Birth Date (yyyy-mm-dd)", "Gender", "Civil Status",
                "Occupation", "Contact No", "Street", "Residency Start (yyyy-mm-dd)", "Rating"};

        fields = new JTextField[labels.length];
        for (int i = 0; i < fields.length; i++) {
            if (i == 3) { // Gender
                genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
                form.add(new JLabel(labels[i]));
                form.add(genderCombo);
            } else if (i == 4) { // Civil Status
                civilStatusCombo = new JComboBox<>(new String[]{"Single", "Married", "Divorced", "Widowed"});
                form.add(new JLabel(labels[i]));
                form.add(civilStatusCombo);
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

        addBtn.addActionListener(e -> addResident());
        updateBtn.addActionListener(e -> updateResident());
        deleteBtn.addActionListener(e -> deleteResident());
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

    private void loadResidents() {
        model.setRowCount(0);
        try (Connection con = DBConnector.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Residents");
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getInt("residentID"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getDate("birthDate").toString(),
                    rs.getString("gender"),
                    rs.getString("civilStatus"),
                    rs.getString("occupation"),
                    rs.getString("contactNo"),
                    rs.getString("streetName"),
                    rs.getDate("residencyStart").toString(),
                    rs.getDouble("satisfactionRating")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading residents: " + e.getMessage());
        }
    }

    private void addResident() {
        try {
            String sql = "INSERT INTO Residents (firstName, lastName, birthDate, gender, civilStatus, " +
                        "occupation, contactNo, streetName, residencyStart, satisfactionRating) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection con = DBConnector.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setString(1, fields[0].getText());
                ps.setString(2, fields[1].getText());
                ps.setString(3, fields[2].getText());
                ps.setString(4, (String) genderCombo.getSelectedItem());
                ps.setString(5, (String) civilStatusCombo.getSelectedItem());
                ps.setString(6, fields[5].getText());
                ps.setString(7, fields[6].getText());
                ps.setString(8, fields[7].getText());
                ps.setString(9, fields[8].getText());
                ps.setDouble(10, Double.parseDouble(fields[9].getText()));
                
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Resident added successfully!");
                loadResidents();
                clearForm();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding resident: " + e.getMessage());
        }
    }

    private void updateResident() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a resident to update");
            return;
        }

        int residentId = (int) model.getValueAt(selectedRow, 0);
        
        try {
            String sql = "UPDATE Residents SET firstName=?, lastName=?, birthDate=?, gender=?, " +
                        "civilStatus=?, occupation=?, contactNo=?, streetName=?, " +
                        "residencyStart=?, satisfactionRating=? WHERE residentID=?";
            
            try (Connection con = DBConnector.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setString(1, fields[0].getText());
                ps.setString(2, fields[1].getText());
                ps.setString(3, fields[2].getText());
                ps.setString(4, (String) genderCombo.getSelectedItem());
                ps.setString(5, (String) civilStatusCombo.getSelectedItem());
                ps.setString(6, fields[5].getText());
                ps.setString(7, fields[6].getText());
                ps.setString(8, fields[7].getText());
                ps.setString(9, fields[8].getText());
                ps.setDouble(10, Double.parseDouble(fields[9].getText()));
                ps.setInt(11, residentId);
                
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Resident updated successfully!");
                    loadResidents();
                    clearForm();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating resident: " + e.getMessage());
        }
    }

    private void deleteResident() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a resident to delete");
            return;
        }

        int residentId = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Delete resident ID " + residentId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnector.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM Residents WHERE residentID=?")) {
                
                ps.setInt(1, residentId);
                int deleted = ps.executeUpdate();
                if (deleted > 0) {
                    JOptionPane.showMessageDialog(this, "Resident deleted successfully!");
                    loadResidents();
                    clearForm();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting resident: " + e.getMessage());
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
            genderCombo.setSelectedItem(model.getValueAt(selectedRow, 4).toString());
            civilStatusCombo.setSelectedItem(model.getValueAt(selectedRow, 5).toString());
            fields[5].setText(model.getValueAt(selectedRow, 6).toString());
            fields[6].setText(model.getValueAt(selectedRow, 7).toString());
            fields[7].setText(model.getValueAt(selectedRow, 8).toString());
            fields[8].setText(model.getValueAt(selectedRow, 9).toString());
            fields[9].setText(model.getValueAt(selectedRow, 10).toString());
        }
    }

    private void clearForm() {
        for (JTextField field : fields) {
            if (field != null) field.setText("");
        }
        if (genderCombo != null) genderCombo.setSelectedIndex(0);
        if (civilStatusCombo != null) civilStatusCombo.setSelectedIndex(0);
        table.clearSelection();
    }
}