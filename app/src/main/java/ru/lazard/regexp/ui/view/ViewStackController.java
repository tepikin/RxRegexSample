package ru.lazard.regexp.ui.view;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egor on 01.11.2016.
 */

public class ViewStackController {
    private ViewGroup viewGroup;
    private View currentView;
    private List<View> views = new ArrayList<>();

    public ViewStackController(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    protected void showView(View view) {
        if (view == null) return;
        if (currentView == view) return;
        viewGroup.removeView(currentView);
        viewGroup.addView(view);
        views.remove(view);
        views.add(view);
        currentView = view;
    }

    public void addView(View view) {
        showView(view);
    }
    public View popView() {
        View returnView = currentView;
        int viewIndex = views.indexOf(currentView);
        if (viewIndex <= 0) return null;
        View previousView = viewIndex > 0 ? views.get(viewIndex - 1) : null;
        views.remove(currentView);
        showView(previousView);
        return returnView;
    }

    public View replaceView(View view) {
        View returnView = currentView;
        int viewIndex = views.indexOf(currentView);
        if (viewIndex <= 0) return null;
        views.remove(currentView);
        showView(view);
        return returnView;
    }

}
