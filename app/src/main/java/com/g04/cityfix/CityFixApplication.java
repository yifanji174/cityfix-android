package com.g04.cityfix;

import android.app.Application;
import android.util.Log;

import com.g04.cityfix.common.constraints.ReportStatus;
import com.g04.cityfix.common.utils.ResponseResult;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.data.model.User;
import com.g04.cityfix.data.repository.ReportRepository;
import com.g04.cityfix.data.repository.UserRepository;
import com.g04.cityfix.domain.NotificationService;
import com.g04.cityfix.domain.ReportService;
import com.g04.cityfix.domain.SearchService;
import com.g04.cityfix.domain.UserService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CityFixApplication extends Application {
    private static UserService userService;
    private static ReportService reportService;
    private static SearchService searchService;
    private static ExecutorService executorService;
    private static NotificationService notificationService;

    /**
     * Initialize dependency injection
     * @author Jerry Yang
     */
    @Override
    public void onCreate(){
        super.onCreate();

        //Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        //Initialize dependency injection
        UserRepository userRepository = new UserRepository(firestore);
        userService = new UserService(userRepository);

        ReportRepository reportRepository = new ReportRepository(firestore);
        reportService = new ReportService(reportRepository);

        searchService = new SearchService(reportRepository);

        notificationService = new NotificationService();

        executorService = Executors.newFixedThreadPool(4);



        //Test
//        RepairReport testReport = new RepairReport("test","test","test", ReportStatus.REPORT_REPORTED,new Date(),new ArrayList<>());
//        reportService.addReport(testReport);

        // Test for search
//        List<RepairReport> reports = searchService.search("Sewer hydrant");
//        for (RepairReport report: reports) {
//            Log.d("App", report.getTitle());
//        }

        // Initialize test accounts
//        initializeTestAccounts();

        // add 2500 reports to Firebase
        //addReports();

    }

    public static UserService getUserService() {
        return userService;
    }

    public static ReportService getReportService() {
        return reportService;
    }

    public static SearchService getSearchService() { return searchService; }

    public static NotificationService getNotificationService() {
        return notificationService;
    }

    public static ExecutorService getExecutorService() { return executorService; }

    /**
     * Initializes test user accounts for development and testing purposes
     * Creates two citizen accounts with predefined credentials
     * @auther u7901628 Sonia Lin
     */
    private void initializeTestAccounts() {
        // Create test account 1
        User testUser1 = new User(
                "comp2100@anu.edu.au",
                "comp2100",
                0,  // USER_CITIZEN
                new ArrayList<>()
        );

        // Create test account 2
        User testUser2 = new User(
                "comp6442@anu.edu.au",
                "comp6442",
                1,  // USER_WORKER
                new ArrayList<>()
        );

        // Add test accounts to database
        userService.addUser(testUser1);
        userService.addUser(testUser2);
    }

    /**
     * Adds sample repair reports to the database for testing and demonstration
     * Creates 2500 reports (250 reports for each of 10 different issue types)
     * with various statuses, dates, and locations
     * @auther u7901628 Sonia Lin
     */
    private void addReports(){
        // Predefined lists of sample data
        List<String> titles = Arrays.asList("Fire hydrant damaged", "Manhole cover missing", "Traffic light malfunction", "pothole", "burst water pipe",
                "loose sidewalk tiles", "exposed cables", "broken roadside bench", "streetlight malfunction", "sewer backup");
        List<String> descriptions = Arrays.asList("Fire hydrant is leaking and causing water damage",
                "Manhole cover is missing",
                "Traffic light at intersection not changing colors",
                "Large pothole damaging vehicles",
                "Water pipe burst causing flooding on street",
                "Multiple sidewalk tiles are loose and creating trip hazard",
                "Electrical cables exposed on street pole",
                "Public bench has broken slats and is unsafe",
                "Street light flickering and not working properly",
                "Sewer system is backing up and causing odor");
        List<String> types = Arrays.asList("Other", "Other", "Other", "Road Damage", "Water Leak",
                "Road Damage", "Other", "Other", "Street Light Malfunction", "Other");

        // Create sample dates for reports
        List<Date> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dates.add(sdf.parse("2025-03-15 12:24:11"));
            dates.add(sdf.parse("2025-04-15 16:14:31"));
            dates.add(sdf.parse("2024-03-15 11:54:14"));
            dates.add(sdf.parse("2024-04-15 09:04:25"));
            dates.add(sdf.parse("2024-12-19 02:24:19"));
            dates.add(sdf.parse("2025-01-15 17:14:38"));
            dates.add(sdf.parse("2024-02-15 01:54:41"));
            dates.add(sdf.parse("2024-08-15 19:04:37"));
            dates.add(sdf.parse("2024-12-25 14:52:16"));
            dates.add(sdf.parse("2024-08-15 15:04:31"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Sample image URLs
        String imageURL1 = "https://i.imgur.com/rn8dMc3.jpeg";
        String imageURL2 = "https://i.imgur.com/QKEE2GH.jpeg";
        String imageURL3 = "https://i.imgur.com/yAxvBIu.jpeg";

        // Generate 10 different types of reports
        for (int j = 0; j < 10; j++){
            String title = titles.get(j);
            String description = descriptions.get(j);
            String type = types.get(j);
            int status = j % 4;
            Date date = dates.get(j);

            ArrayList<String> imageURLs = new ArrayList<>();
            if (status > 0) imageURLs.add(imageURL1);
            if (status > 1) imageURLs.add(imageURL2);
            if (status > 2) imageURLs.add(imageURL3);

            // Create 250 reports for each type with different citizen usernames
            for (int i = 1; i <= 250; i++){
                String citizenUsername = "citizen" + i;

                // Randomly select a GPS location
                Random random = new Random();
                // latitude: -90.0 to 90.0
                double latitude = -90.0 + 180.0 * random.nextDouble();
                // longitude: -180.0 to 180.0
                double longitude = -180.0 + 360.0 * random.nextDouble();
                String location = String.format("%.6f, %.6f", latitude, longitude);

                // Create and add the report
                RepairReport testReport = new RepairReport(title, description, citizenUsername, status, date, imageURLs, location, type);
                reportService.addReport(testReport);
            }
        }
    }
}