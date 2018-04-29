package com.oritmalki.mymusicapp2.ui.mainscreen;

import android.view.View;

import com.oritmalki.mymusicapp2.model.Measure;

public interface BeatClickCallback {

    void onClick(Measure measure, View beatView, int beatPosition);
}
