CREATE TABLE User (
    user_id INT PRIMARY KEY,
    name VARCHAR(100),
    phone VARCHAR(15),
    emergency_contact VARCHAR(15)
);

CREATE TABLE GPSLog (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    latitude DOUBLE,
    longitude DOUBLE,
    timestamp DATETIME,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE EmergencyService (
    service_id INT PRIMARY KEY,
    name VARCHAR(100),
    phone VARCHAR(15),
    latitude DOUBLE,
    longitude DOUBLE
);

CREATE TABLE IncidentReport (
    report_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    latitude DOUBLE,
    longitude DOUBLE,
    timestamp DATETIME,
    status VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);
