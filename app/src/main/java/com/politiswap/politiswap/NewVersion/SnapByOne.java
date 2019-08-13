package com.politiswap.politiswap.NewVersion;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SnapByOne extends PagerSnapHelper {

    private int startingPosition = 0;

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {

        int currentPosition;

        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }

        final View currentView = findSnapView(layoutManager);

        if (currentView == null) {
            return RecyclerView.NO_POSITION;
        }

        if (Math.abs(velocityX) > Math.abs(velocityY) && Math.abs(velocityX) > 100){
            if (velocityX < 0){
                currentPosition = startingPosition > 0 ? startingPosition - 1 : startingPosition;
            } else {
                currentPosition = startingPosition < layoutManager.getItemCount()-1 ? startingPosition + 1 : startingPosition;
            }
        } else {
            currentPosition = layoutManager.getPosition(currentView);
        }

        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        startingPosition = currentPosition;

        return currentPosition;
    }

    public void setStartingPosition(int position){
        startingPosition = position;
    }
}
