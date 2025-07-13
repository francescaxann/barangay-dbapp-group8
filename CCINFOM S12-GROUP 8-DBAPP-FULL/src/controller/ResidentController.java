package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.DBConnection;
import model.Resident;

import java.sql.*;

public class ResidentController {
    @FXML private TableView<Resident> residentTable;
    @FXML private TableColumn<Resident, Integer> idColumn;
    @FXML private TableColumn<Resident, String> firstNameColumn;
    @FXML private TableColumn<Resident, String> lastNameColumn;
    @FXML private TextField txtFirstName, txtLastName;
    @FXML private Button btnAdd;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("residentID"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        residentTable.setItems(loadResidents());
    }

    public void addResident() {
        String fName = txtFirstName.getText();
        String lName = txtLastName.getText();
        if (fName.isEmpty() || lName.isEmpty()) return;
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Residents(firstName, lastName) VALUES (?, ?)");
            ps.setString(1, fName);
            ps.setString(2, lName);
            ps.executeUpdate();
            residentTable.setItems(loadResidents());
            txtFirstName.clear();
            txtLastName.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<Resident> loadResidents() {
        ObservableList<Resident> list = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT residentID, firstName, lastName FROM Residents")) {
            while (rs.next()) {
                list.add(new Resident(
                    rs.getInt("residentID"),
                    rs.getString("firstName"),
                    rs.getString("lastName")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
