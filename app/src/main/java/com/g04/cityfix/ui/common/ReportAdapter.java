package com.g04.cityfix.ui.common;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.g04.cityfix.CityFixApplication;
import com.g04.cityfix.R;
import com.g04.cityfix.common.constraints.ReportStatus;
import com.g04.cityfix.common.constraints.ReportType;
import com.g04.cityfix.common.constraints.UserRole;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.domain.ReportService;
import com.g04.cityfix.domain.UserService;
import com.g04.cityfix.ui.citizen.ReportDetailCitizenActivity;
import com.g04.cityfix.ui.worker.ReportDetailWorkerActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A single report in main page
 * @author Yifan Ji
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private final ReportService reportService = CityFixApplication.getReportService();

    private List<RepairReport> reportList;

    private final UserService userService;

    private OnFavoriteClickListener favoriteClickListener;

    private Mode mode = Mode.NORMAL;

    private final List<RepairReport> allReports;

    private final List<Boolean> favoriteStatus;

    public enum Mode { NORMAL, FAVORITE }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(RepairReport report);
    }

    // Constructor
    public ReportAdapter(List<RepairReport> reportList, UserService userService) {
        this.reportList = reportList;
        this.allReports = new ArrayList<>(reportList);
        this.favoriteStatus = new ArrayList<>();
        for (int i = 0; i < reportList.size(); i++) {
            favoriteStatus.add(false);
        }
        this.userService = userService;
    }

    // Set current adapter mode
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    // Reset filter base
    public void updateData(List<RepairReport> newReports) {
        this.reportList = newReports;
        allReports.clear();
        allReports.addAll(newReports);
        notifyDataSetChanged();
    }

    public void appendData(List<RepairReport> moreReports) {
        int start = reportList.size();
        reportList.addAll(moreReports);
        allReports.addAll(moreReports);
        notifyItemRangeInserted(start, moreReports.size());
    }

    public void removeItem(RepairReport report) {
        int index = -1;
        for (int i = 0; i < reportList.size(); i++) {
            if (reportList.get(i).getId().equals(report.getId())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            reportList.remove(index);
            notifyItemRemoved(index);
        } else {
            notifyDataSetChanged();
        }
    }

    // Refresh a single item
    public void refreshItem(RepairReport report) {
        for (int i = 0; i < reportList.size(); i++) {
            if (reportList.get(i).getId().equals(report.getId())) {
                notifyItemChanged(i);
                return;
            }
        }
    }

    public List<RepairReport> getReportList() {
        return reportList;
    }

    // ViewHolder for each card
    static class ReportViewHolder extends RecyclerView.ViewHolder {
        ImageView reportImageView;
        TextView titleTextView;
        TextView locationTextView;
        TextView typeTextView;
        TextView timestampTextView;
        ImageButton favoriteButton;
        ImageView reportStatus;
        TextView reportUser;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reportImageView = itemView.findViewById(R.id.reportImage);
            titleTextView = itemView.findViewById(R.id.reportTitle);
            locationTextView = itemView.findViewById(R.id.reportLocation);
            typeTextView = itemView.findViewById(R.id.reportType);
            timestampTextView = itemView.findViewById(R.id.reportTime);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            reportStatus = itemView.findViewById(R.id.reportStatus);
            reportUser = itemView.findViewById(R.id.reportFrom);
        }
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        RepairReport report = reportList.get(position);

        holder.titleTextView.setText(report.getTitle());
        holder.typeTextView.setText("Type: " + report.getType());
        holder.reportUser.setText("From:" + report.getCitizenUsername());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        holder.timestampTextView.setText("Report time: " + sdf.format(report.getTimestamp()));

        switch (report.getStatus()) {
            case ReportStatus.REPORT_REPORTED:
                holder.reportStatus.setImageResource(R.drawable.statues_not_start_yet); break;
            case ReportStatus.REPORT_SEEN:
                holder.reportStatus.setImageResource(R.drawable.statues_be_seen); break;
            case ReportStatus.REPORT_PROCESSING:
                holder.reportStatus.setImageResource(R.drawable.statues_in_process); break;
            case ReportStatus.REPORT_SOLVED:
                holder.reportStatus.setImageResource(R.drawable.statues_finished); break;
            default:
                holder.reportStatus.setImageResource(R.drawable.statues_not_start_yet); break;
        }

        String location = report.getLocation();
        if (location != null && location.matches("-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?")) {
            try {
                String[] latLng = location.split(",");
                double lat = Double.parseDouble(latLng[0]);
                double lng = Double.parseDouble(latLng[1]);
                Geocoder geocoder = new Geocoder(holder.itemView.getContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                holder.locationTextView.setText("Location: " + (!addresses.isEmpty() ? addresses.get(0).getAddressLine(0) : "Unknown"));
            } catch (IOException e) {
                holder.locationTextView.setText("Location: Unavailable");
            }
        } else {
            holder.locationTextView.setText("Location: " + (location != null ? location : "N/A"));
        }

        // Image with Glide
        List<String> imageURLs = report.getImageURLs();
        if (imageURLs != null && !imageURLs.isEmpty()) {
            Glide.with(holder.reportImageView.getContext())
                    .load(imageURLs.get(0))
                    .placeholder(R.drawable.new_report_selected)
                    .into(holder.reportImageView);
        } else {
            holder.reportImageView.setImageResource(R.drawable.new_report_selected);
        }

        // Favorite icon
        boolean isFavorite = userService.getFavoriteReportsLocal().contains(report);
        holder.favoriteButton.setImageResource(isFavorite ? R.drawable.star1 : R.drawable.star0);

        holder.favoriteButton.setOnClickListener(v -> {
            if (favoriteClickListener != null) {
                favoriteClickListener.onFavoriteClick(report);
            }
        });

        // Jump to detail page
        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent;
            int userRole = userService.getCurrentUser().getRole();
            if (userRole == UserRole.USER_CITIZEN) {
                intent = new Intent(context, ReportDetailCitizenActivity.class);
            } else {
                if (report.getStatus() == ReportStatus.REPORT_REPORTED) {
                    report.setStatus(ReportStatus.REPORT_SEEN);
                    reportService.markAsSeen(report.getId());
                    holder.reportStatus.setImageResource(R.drawable.statues_be_seen);
                }
                intent = new Intent(context, ReportDetailWorkerActivity.class);
                reportService.notifyOnThirdView(report);
            }
            intent.putExtra("report", report);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }

    // Filter & sorting logic
    private void sortDataByTime(boolean isLatest) {
        Comparator<RepairReport> comp = Comparator.comparing(RepairReport::getTimestamp);
        if (isLatest) comp = comp.reversed();
        this.reportList.sort(comp);
    }

    private void filterDataByStatus(int status) {
        this.reportList = this.reportList.stream()
                .filter(report -> report.getStatus() == status)
                .collect(Collectors.toList());
    }

    private void filterDataByType(int type) {
        String[] types = ReportType.getLabels();
        this.reportList = this.reportList.stream()
                .filter(report -> report.getType() != null && report.getType().equals(types[type]))
                .collect(Collectors.toList());
    }

    public void updateFilter(int[] filterIds) {
        this.reportList.clear();
        this.reportList.addAll(allReports);

        if (filterIds[0] != 0) sortDataByTime(filterIds[0] == 1);
        if (filterIds[1] != 0) filterDataByStatus(filterIds[1] - 1);
        if (filterIds[2] != 0) filterDataByType(filterIds[2] - 1);

        notifyDataSetChanged();
    }
}

