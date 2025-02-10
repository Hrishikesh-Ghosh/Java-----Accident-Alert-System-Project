import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.json.JSONArray;
import org.json.JSONObject;

public class AccidentAlertSystem {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/accidentalertsystem";
    private static final String USER = "root";
    private static final String PASS = "sql123";
    private static final double ACCEL_THRESHOLD = 9.8; // m/s^2 for high impact
    private static final double ORIENT_THRESHOLD = 45.0; // degrees for orientation change
    private static Timer timer;

    public static void main(String[] args) {
        startDataCollection();
    }

    public static void startDataCollection() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                double[] accelerometerData = getAccelerometerData();
                double[] gyroscopeData = getGyroscopeData();
                double[] gpsData = getGPSData();

                // Save GPS data periodically
                saveGPSData(gpsData);

                if (isAccident(accelerometerData, gyroscopeData)) {
                    handleAccident(gpsData);
                }
            }
        }, 0, 5000); // Run every 5 seconds
    }

    private static boolean isAccident(double[] accelerometer, double[] gyroscope) {
        return accelerometer[0] > ACCEL_THRESHOLD || Math.abs(gyroscope[1]) > ORIENT_THRESHOLD;
    }

    private static void handleAccident(double[] gpsData) {
        // Fetch address from Maps API
        String address = fetchAddress(gpsData[0], gpsData[1]);
        int nearestServiceId = findNearestEmergencyService(gpsData[0], gpsData[1]);

        // Send notification
        sendNotification(nearestServiceId, address, gpsData);
    }

    private static void saveGPSData(double[] gpsData) {
        String sql = "INSERT INTO GPSLog (user_id, latitude, longitude, timestamp) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 1); // Assuming user_id 1
            stmt.setDouble(2, gpsData[0]);
            stmt.setDouble(3, gpsData[1]);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String fetchAddress(double latitude, double longitude) {
        String address = "";
        String apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=YOUR_API_KEY";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(content.toString());
            address = json.getJSONArray("results").getJSONObject(0).getString("formatted_address");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    private static int findNearestEmergencyService(double latitude, double longitude) {
        int serviceId = -1;
        String sql = "SELECT service_id, " +
                "(6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) + sin(radians(?)) * sin(radians(latitude)))) AS distance " +
                "FROM EmergencyService " +
                "ORDER BY distance LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, latitude);
            stmt.setDouble(2, longitude);
            stmt.setDouble(3, latitude);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                serviceId = rs.getInt("service_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serviceId;
    }

    private static void sendNotification(int serviceId, String address, double[] gpsData) {
        String sql = "INSERT INTO IncidentReport (user_id, latitude, longitude, timestamp, status) VALUES (?, ?, ?, NOW(), 'Pending')";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 1); // Assuming user_id 1
            stmt.setDouble(2, gpsData[0]);
            stmt.setDouble(3, gpsData[1]);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Placeholder for notification logic
        System.out.println("NOW SENDING E-MAIL...");
        //from,password,to,subject,message
        String mailTo=System.getenv("Email_ID");
        String mailFrom=System.getenv("Email_ID");
        String password=System.getenv("Email_PWD");
        String subject="Send from Alert System";
        String message1="Hi, This is to notify that the Vehicle was exceeding the Speed Limit and CRASHED !!";

        Mailer.sendEmail(mailFrom,password,mailTo,subject,message1);
        //change from, password and to
    }

    // Placeholder for obtaining accelerometer data
    private static double[] getAccelerometerData() {
        return new double[]{Math.random() * 10, 0, 0}; // Replace with actual sensor data
    }

    // Placeholder for obtaining gyroscope data
    private static double[] getGyroscopeData() {
        return new double[]{0, Math.random() * 50, 0}; // Replace with actual sensor data
    }

    // Placeholder for obtaining GPS data
    private static double[] getGPSData() {
        return new double[]{12.9716, 77.5946}; // Replace with actual GPS data
    }
}