package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.DBConnection;
import model.Staff;

import java.sql.*;

public class StaffController {
    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, Integer> staffIDColumn;
    @FXML private TableColumn<Staff, Integer> residentIDColumn;
    @FXML private TableColumn<Staff, String> positionColumn;
    @FXML private TextField txtResidentID, txtPosition;
    @FXML private Button btnAddStaff;

    @FXML
    public void initialize() {
        staffIDColumn.setCellValueFactory(new PropertyValueFactory<>("staffID"));
        residentIDColumn.setCellValueFactory(new PropertyValueFactory<>("residentID"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        staffTable.setItems(loadStaff());
    }

    public void addStaff() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Staff (residentID, position, startTerm, endTerm, officeStatus, assignedArea) VALUES (?, ?, CURDATE(), NULL, 'Active', 'Main')");
            ps.setInt(1, Integer.parseInt(txtResidentID.getText()));
            ps.setString(2, txtPosition.getText());
            ps.executeUpdate();
            staffTable.setItems(loadStaff());
            txtResidentID.clear();
            txtPosition.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<Staff> loadStaff() {
        ObservableList<Staff> list = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT staffID, residentID, position FROM Staff")) {
            while (rs.next()) {
                list.add(new Staff(
                    rs.getInt("staffID"),
                    rs.getInt("residentID"),
                    rs.getString("position")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
