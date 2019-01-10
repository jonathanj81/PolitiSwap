package com.example.jon.politiswap;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.jon.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.example.jon.politiswap.DataUtils.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Implementation of App Widget functionality.
 */
public class PolicyWidgetProvider extends AppWidgetProvider {

    //private static final String WIDGET_VIEW_TYPE = "view_type";
    //public static int mViewType;

    private static final String PREFS_WIDGET_KEY = "user_id_widget_memory";
    private static final String USER_ID_KEY = "user_id_widget_key";

    private static RemoteViews mViews;

    static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String userID;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_WIDGET_KEY, Context.MODE_PRIVATE);
        if (prefs.contains(USER_ID_KEY)) {
            userID = prefs.getString(USER_ID_KEY, "");

            FirebaseDatabase.getInstance().getReference("UserInfo").child(userID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserInfo info = dataSnapshot.getValue(UserInfo.class);
                            String overallPoints =
                                    String.format(context.getResources().getString(R.string.widget_score_overall),String.valueOf(info.getOverallPoints()));
                            String swapsPoints =
                                    String.format(context.getResources().getString(R.string.widget_score_swap_creation),String.valueOf(info.getSwapCreatedPoints()));
                            String policiesPoints =
                                    String.format(context.getResources().getString(R.string.widget_score_policy_creation),String.valueOf(info.getPolicyCreatedPoints()));
                            String votePoints =
                                    String.format(context.getResources().getString(R.string.widget_score_votes),String.valueOf(info.getVotePoints()));
                            mViews = new RemoteViews(context.getPackageName(), R.layout.policy_widget_provider);
                            mViews.setTextViewText(R.id.widget_overall_score_view, overallPoints);
                            mViews.setTextViewText(R.id.widget_swaps_points_view, swapsPoints);
                            mViews.setTextViewText(R.id.widget_policies_points_view, policiesPoints);
                            mViews.setTextViewText(R.id.widget_votes_points_view, votePoints);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            mViews.setTextViewText(R.id.widget_overall_score_view, context.getText(R.string.widget_no_account_message));
        }

        appWidgetManager.updateAppWidget(appWidgetId, mViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidget(context, appWidgetManager, appWidgetId);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

