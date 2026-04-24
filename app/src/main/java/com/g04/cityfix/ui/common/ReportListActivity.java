package com.g04.cityfix.ui.common;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g04.cityfix.CityFixApplication;
import com.g04.cityfix.R;
import com.g04.cityfix.common.constraints.UserRole;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.domain.ReportService;
import com.g04.cityfix.domain.SearchService;
import com.g04.cityfix.domain.UserService;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Main page
 * @author Yifan Ji, Junhao Liu
 */
public class ReportListActivity extends NavigationBarActivity {

    private RecyclerView recyclerView;
    private ReportAdapter adapter;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private DocumentSnapshot lastVisibleDoc = null;
    private final int[] spinnerIds = {0, 0, 0};
    private final int PAGE_SIZE = 10;

    private final UserService userService = CityFixApplication.getUserService();
    private final ReportService reportService = CityFixApplication.getReportService();
    private final SearchService searchService = CityFixApplication.getSearchService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        recyclerView = findViewById(R.id.reportRecyclerView);
        adapter = new ReportAdapter(new ArrayList<>(), userService);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadInitialData();
        setUpScrollListener();
        setUpSearchOnClickListener();
        setUpTimeSpinnerListener();
        setUpStatusSpinnerListener();
        setUpTypeSpinnerListener();
        setupNavigationBar();

        adapter.setOnFavoriteClickListener(report -> {
            // Worker can't follow a report manually
            if (userService.getCurrentUser().getRole() == UserRole.USER_WORKER){
                return;
            }
            boolean isFavorite = userService.getFavoriteReportsLocal().contains(report);
            userService.toggleFavorite(report);

            String username = userService.getCurrentUser().getUsername();
            String reportId = report.getTimestamp().getTime() + "_" + report.getCitizenUsername();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(username);
            DocumentReference reportRef = db.collection("reports").document(reportId);

            if (!isFavorite) {
                userRef.update("favorites", FieldValue.arrayUnion(reportId));
                reportRef.update("favoriteUsernames", FieldValue.arrayUnion(username));
            } else {
                userRef.update("favorites", FieldValue.arrayRemove(reportId));
                reportRef.update("favoriteUsernames", FieldValue.arrayRemove(username));
            }

            int pos = adapter.getReportList().indexOf(report);
            if (pos != -1) {
                adapter.notifyItemChanged(pos);
            }
        });
    }

    private void loadInitialData() {
        isLoading = true;

        reportService.getReportsPaged(null, PAGE_SIZE, (reports, lastDoc) -> {
            List<String> favoriteIds = userService.getCurrentUser().getFavorites();

            for (RepairReport report : reports) {
                String reportId = report.getTimestamp().getTime() + "_" + report.getCitizenUsername();
                report.setFavorited(favoriteIds.contains(reportId));
            }

            runOnUiThread(() -> {
                adapter.updateData(reports);
                lastVisibleDoc = lastDoc;
                isLoading = false;

                if (reports.size() < PAGE_SIZE) isLastPage = true;
            });
        });
    }

    private void loadNextPage() {
        if (isLastPage || isLoading) return;

        isLoading = true;

        reportService.getReportsPaged(lastVisibleDoc, PAGE_SIZE, (reports, lastDoc) -> {
            runOnUiThread(() -> {
                adapter.appendData(reports);
                adapter.updateFilter(spinnerIds);
                lastVisibleDoc = lastDoc;
                isLoading = false;

                if (reports.size() < PAGE_SIZE) isLastPage = true;
            });
        });
    }

    private void setUpScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && !isLastPage && totalItemCount <= (lastVisibleItem + 2)) {
                    loadNextPage();
                }
            }
        });
    }

    private void resetSpinner() {
        Spinner timeSpinner = findViewById(R.id.timeSpinner);
        Spinner statusSpinner = findViewById(R.id.statusSpinner);
        Spinner typeSpinner = findViewById(R.id.typeSpinner);
        timeSpinner.setSelection(0);
        statusSpinner.setSelection(0);
        typeSpinner.setSelection(0);
    }

    private void searchEvent() {
        isLoading = true;
        String searchStr = ((EditText) findViewById(R.id.searchInput)).getText().toString().trim();

        resetSpinner();

        if (searchStr.isEmpty()) {
            loadInitialData();
            return;
        }

        searchService.search(this, searchStr, reports -> runOnUiThread(() -> {
            adapter.updateData(reports);
            isLoading = false;
            isLastPage = true;
            recyclerView.scrollToPosition(0);
        }));
    }

    private void setUpSearchOnClickListener() {
        ImageButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> searchEvent());
    }

    private void setUpTimeSpinnerListener() {
        Spinner timeSpinner = findViewById(R.id.timeSpinner);
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerIds[0] = (int) id;
                adapter.updateFilter(spinnerIds);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setUpStatusSpinnerListener() {
        Spinner statusSpinner = findViewById(R.id.statusSpinner);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerIds[1] = (int) id;
                adapter.updateFilter(spinnerIds);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setUpTypeSpinnerListener() {
        Spinner typeSpinner = findViewById(R.id.typeSpinner);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerIds[2] = (int) id;
                adapter.updateFilter(spinnerIds);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userService.reloadUserFromFirestore(this::loadInitialData);
    }
}
