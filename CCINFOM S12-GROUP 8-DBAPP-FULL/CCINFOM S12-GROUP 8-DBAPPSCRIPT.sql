
CREATE DATABASE IF NOT EXISTS barangay_db;
USE barangay_db;

CREATE TABLE Residents (
    residentID INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(50),
    lastName VARCHAR(50),
    birthDate DATE,
    gender ENUM('Male', 'Female'),
    civilStatus ENUM('Single', 'Married', 'Divorced', 'Widowed'),
    occupation VARCHAR(100),
    contactNo VARCHAR(20),
    streetName VARCHAR(100),
    residencyStart DATE,
    satisfactionRating DECIMAL(3,2)
);

CREATE TABLE Staff (
    staffID INT AUTO_INCREMENT PRIMARY KEY,
    residentID INT,
    position VARCHAR(50),
    startTerm DATE,
    endTerm DATE,
    officeStatus ENUM('Active', 'Inactive'),
    assignedArea VARCHAR(100),
    FOREIGN KEY (residentID) REFERENCES Residents(residentID)
);

CREATE TABLE Incident (
    reportID INT AUTO_INCREMENT PRIMARY KEY,
    residentID INT,
    staffID INT,
    eventType VARCHAR(100),
    date DATE,
    time TIME,
    location VARCHAR(100),
    personInvolved VARCHAR(100),
    status VARCHAR(50),
    FOREIGN KEY (residentID) REFERENCES Residents(residentID),
    FOREIGN KEY (staffID) REFERENCES Staff(staffID)
);

CREATE TABLE Project (
    projectID INT AUTO_INCREMENT PRIMARY KEY,
    staffID INT,
    startDate DATE,
    endDate DATE,
    budget DECIMAL(10,2),
    status VARCHAR(50),
    FOREIGN KEY (staffID) REFERENCES Staff(staffID)
);

CREATE TABLE ProjectParticipants (
    projectID INT,
    residentID INT,
    PRIMARY KEY (projectID, residentID),
    FOREIGN KEY (projectID) REFERENCES Project(projectID),
    FOREIGN KEY (residentID) REFERENCES Residents(residentID)
);
