package com.g04.cityfix.ui.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g04.cityfix.CityFixApplication;
import com.g04.cityfix.R;
import com.g04.cityfix.common.constraints.ReportType;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.domain.ReportService;
import com.g04.cityfix.domain.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Activity for the page where users can view favorite reports
 * @author Yifan Ji
 */
public class FavoriteReportListActivity extends NavigationBarActivity {

    private RecyclerView recyclerView;
    private ReportAdapter adapter;
    private final UserService userService = CityFixApplication.getUserService();
    private final ReportService reportService = CityFixApplication.getReportService();

    private Spinner timeSpinner, statusSpinner, typeSpinner;
    private final int[] spinnerIds = {0, 0, 0}; // Time, Status, Type filters

    private List<RepairReport> allFavorites = new ArrayList<>();

    private final String[] types = Stream.concat(
            Stream.of("All"),
            Arrays.stream(ReportType.getLabels())
    ).toArray(String[]::new);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        recyclerView = findViewById(R.id.reportRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allFavorites = new ArrayList<>(userService.getFavoriteReportsLocal());
        adapter = new ReportAdapter(new ArrayList<>(allFavorites), userService);
        adapter.setMode(ReportAdapter.Mode.FAVORITE);
        recyclerView.setAdapter(adapter);

        setupSpinners();
        setupFavoriteClickHandler();
        setupNavigationBar();

        if (allFavorites.isEmpty()) {
            Toast.makeText(this, "No favorites yet.", Toast.LENGTH_SHORT).show();
        }
    }

    // Setup time, status, type filter spinners
    private void setupSpinners() {
        timeSpinner = findViewById(R.id.timeSpinner);
        statusSpinner = findViewById(R.id.statusSpinner);
        typeSpinner = findViewById(R.id.typeSpinner);

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerIds[0] = position;
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerIds[1] = position;
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerIds[2] = position;
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Apply all filters
    private void applyFilters() {
        List<RepairReport> filtered = new ArrayList<>(allFavorites);

        // Filter by type
        if (spinnerIds[2] != 0) {
            String selectedType = types[spinnerIds[2]];
            filtered.removeIf(report -> report.getType() == null || !report.getType().equals(selectedType));
        }

        // Filter by status
        if (spinnerIds[1] != 0) {
            int selectedStatus = spinnerIds[1] - 1;
            filtered.removeIf(report -> report.getStatus() != selectedStatus);
        }

        // Sort by time
        if (spinnerIds[0] == 1) {
            filtered.sort((r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()));
        } else if (spinnerIds[0] == 2) {
            filtered.sort(Comparator.comparing(RepairReport::getTimestamp));
        }

        adapter.updateData(filtered);
    }

    // Update list
    private void setupFavoriteClickHandler() {
        adapter.setOnFavoriteClickListener(report -> {
            boolean isNowFavorite = userService.toggleFavorite(report);

            if (!isNowFavorite && adapter.getMode() == ReportAdapter.Mode.FAVORITE) {
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    allFavorites.remove(report);
                    applyFilters();
                }, 1000);
            } else {
                adapter.refreshItem(report);
            }
        });
    }

    // Refresh favorites from Firestore
    @Override
    protected void onResume() {
        super.onResume();
        userService.reloadUserFromFirestore(this::loadInitialData);
    }

    // Reload favorites
    private void loadInitialData() {
        reportService.getAllReports(reports -> {
            userService.loadFavoritesForCurrentUser(reports);
            allFavorites = new ArrayList<>(userService.getFavoriteReportsLocal());
            applyFilters();

            if (allFavorites.isEmpty()) {
                Toast.makeText(this, "No favorites yet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
