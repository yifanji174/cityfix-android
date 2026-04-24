package com.g04.cityfix.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.g04.cityfix.common.utils.Peekable;
import com.g04.cityfix.data.model.Notification;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.domain.HFInferenceProviderLLMService;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author Junao Xiong
 */
public class NotificationTest {
    Notification testNotification;

    @Test
    public void titleTest() {
        testNotification = new Notification("Title2", "describe2", "12345678",new RepairReport(),1);
        long time = testNotification.getTime();
        String test = "Title2";
        assertTrue(test.equals(testNotification.getTitle()));
        testNotification = new Notification("Title3", "describe2", "12345678",new RepairReport(),1);
        assertFalse(test.equals(testNotification.getTitle()));
    }

    //find out if the method return correct answer.
    @Test
    public void notificationDateTest() throws InterruptedException {
        testNotification = new Notification("Title2", "describe2", "12345678",new RepairReport(),1);
        long time = testNotification.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());
        assertTrue(sdf.format(time).equals(testNotification.getDateString()));
    }

    @Test
    public void notificationTimeTest() throws InterruptedException {
        testNotification = new Notification("Title2", "describe2", "12345678",new RepairReport(),1);
        long time = testNotification.getTime();
        assertTrue(time == testNotification.getTime());
        //ensure tha the time of the notification be set correctly
        Thread.sleep(300);
        testNotification = new Notification("Title3", "describe2", "12345678",new RepairReport(),2);
        assertFalse(time == testNotification.getTime());
    }

    @Test
    public void notificationTimeOfDateTest() throws InterruptedException {
        testNotification = new Notification("Title2", "describe2", "12345678",new RepairReport(),1);
        long time = testNotification.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        assertTrue(sdf.format(time).equals(testNotification.getTimeOfDayString()));
    }

}
