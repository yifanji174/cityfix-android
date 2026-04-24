package com.g04.cityfix.ui.common;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g04.cityfix.R;
import com.g04.cityfix.data.model.Notification;

import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    // report what be cliecked
    public interface OnNotificationClickListener {
        void onNotificationClick(Notification n);
    }

    private final List<Notification> notificationList;
    private final OnNotificationClickListener listener;

    /**
     * put list and the listner into the adapter
     * @param notificationList the list of notifications
     * @param listener the listener of notification list.
     * @author Junao Xiong
     */
    public NotificationAdapter(List<Notification> notificationList, OnNotificationClickListener listener) {
        this.notificationList = notificationList;
        this.listener = listener;
    }

    /**
     * Creates appropriate ViewHolder.
     * @param parent parent The parent ViewGroup
     * @param viewType The type of view
     * @return ViewHolder for the specific message type
     * @author Junao Xiong
     */
    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }



    /**
     * Binds a notification item to the given ViewHolder for display in the list.
     * @param holder The ViewHolder into which the data should be loaded
     * @param position The position of the item within the data set
     * @author Junao Xiong
     */
    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        // set text of text views in notification item
        holder.titleTextView.setText(notification.getTitle());
        holder.descriptionTextView.setText(notification.getDescription());
        holder.dateTextView.setText(notification.getDateString());
        holder.timeTextView.setText(notification.getTimeOfDayString());

        //get statues of notification
        int statues = notification.getStatues();

        //switch the icon of the notification depending on the statues be passed
        if(statues == 3){
            holder.statuesView.setImageResource(R.drawable.notification_finished);
        }
        else if(statues == 1){
            holder.statuesView.setImageResource(R.drawable.notification_be_seen);
        }
        else{
            holder.statuesView.setImageResource(R.drawable.notification);
        }

        // change the statues of red dot of notification
        if (notification.getBeRead()) {
            holder.redDotView.setVisibility(View.GONE);
        } else {
            holder.redDotView.setVisibility(View.VISIBLE);
        }

        // set click event
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });
    }

    /**
     * return how much elements include
     * @return the number of items in notification
     * @author Junao Xiong
     */
    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    /**
     * ViewHolder part
     * @author Junao Xiong
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, dateTextView, timeTextView;
        ImageView redDotView, statuesView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notification_title);
            descriptionTextView = itemView.findViewById(R.id.notification_description);
            dateTextView = itemView.findViewById(R.id.notification_date);
            timeTextView = itemView.findViewById(R.id.notification_time);
            redDotView = itemView.findViewById(R.id.Notification_redDot);
            statuesView = itemView.findViewById(R.id.notification_icon);
        }
    }
}

