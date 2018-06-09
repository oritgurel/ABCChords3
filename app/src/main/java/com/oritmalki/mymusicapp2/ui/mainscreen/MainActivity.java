package com.oritmalki.mymusicapp2.ui.mainscreen;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxItemDecoration;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.oritmalki.mymusicapp2.R;
import com.oritmalki.mymusicapp2.database.MeasureRepository;
import com.oritmalki.mymusicapp2.model.Beat;
import com.oritmalki.mymusicapp2.model.Measure;
import com.oritmalki.mymusicapp2.ui.mainscreen.EditFragment.OnFragmentInteractionListener;
import com.oritmalki.mymusicapp2.viewmodel.MeasureListViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private RecyclerView recyclerView;
    private FlexboxLayoutManager layoutManager;
    private MeasureRepository measureRepository;
    private FloatingActionButton addBut;
    private FloatingActionButton remBut;
    private OnClickListener listener;
    MeasuresAdapter measuresAdapter;
    StringBuilder sb = new StringBuilder();
    String chord;
    Measure adapterPosition;
    MeasureListViewModel viewModel;
    List<Beat> beatsForInsersion;
    SharedPreferences preferences;
    Editor editor;
    Measure currentMeasure;
    int currentBeatPosition;
    private View currentBeatView;
    private FrameLayout editFragmentContainer;

    public final static String IS_C_ROOT_PRESSED = "IS_C_ROOT_PRESSED";
    public final static String IS_D_ROOT_PRESSED = "IS_D_ROOT_PRESSED";
    public final static String IS_E_ROOT_PRESSED = "IS_E_ROOT_PRESSED";
    public final static String IS_F_ROOT_PRESSED = "IS_F_ROOT_PRESSED";
    public final static String IS_G_ROOT_PRESSED = "IS_G_ROOT_PRESSED";
    public final static String IS_A_ROOT_PRESSED = "IS_A_ROOT_PRESSED";
    public final static String IS_B_ROOT_PRESSED = "IS_B_ROOT_PRESSED";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSharedPrefs();

        final MeasureListViewModel viewModel = ViewModelProviders.of(this).get(MeasureListViewModel.class);
        this.viewModel = viewModel;

        initializeViews(viewModel);

        observeViewModel(viewModel);

    }

    private void observeViewModel(MeasureListViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getMeasures().observe(this, new Observer<List<Measure>>() {
            @Override
            public void onChanged(@Nullable List<Measure> measures) {
                if (measures != null) {
                    measuresAdapter = new MeasuresAdapter(getApplicationContext(), beatClickCallback);
                    measuresAdapter.setMeasuresList(measures, getApplicationContext());
                    recyclerView.setAdapter(measuresAdapter);
                    recyclerView.smoothScrollToPosition(measures.size());
                    Log.d("ADD_MEASURE", "updated view");

                }
            }
        });
    }

    public void show(Measure measure, int currentBeatPosition) {

        EditFragment editFragment = EditFragment.newInstance(measure, currentBeatPosition);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        if (!(getSupportFragmentManager().getBackStackEntryCount() == 1)) {

            transaction.addToBackStack(editFragment.getClass().getSimpleName());
            editFragmentContainer.setVisibility(View.VISIBLE);
            transaction.replace(R.id.edit_fragment, editFragment, null).setTransition(android.R.transition.slide_top).commit();
            recyclerView.smoothScrollToPosition(measure.getNumber());
        }
//    }

    public void initializeViews(MeasureListViewModel viewModel) {

        listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.add_fab:
                        addEmptyMeasure(viewModel);
                        Log.d("ADD_MEASURE", "called addEmptyMeasure(viewModel) from activity");
                        break;
                    case R.id.remove_fab:
                        deleteMeasure(viewModel);
                        break;
                }
            }
        };


        addBut = findViewById(R.id.add_fab);
        addBut.setOnClickListener(listener);

        editFragmentContainer = findViewById(R.id.edit_fragment);

        remBut = findViewById(R.id.remove_fab);
        remBut.setOnClickListener(listener);

        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new FlexboxLayoutManager(MainActivity.this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setAlignItems(AlignItems.STRETCH);


        FlexboxItemDecoration itemDecoration = new FlexboxItemDecoration(MainActivity.this);
        itemDecoration.setDrawable(getApplicationContext().getDrawable(R.drawable.divider));
        itemDecoration.setOrientation(FlexboxItemDecoration.BOTH);
        recyclerView.addItemDecoration(itemDecoration);
        layoutManager.getBaseline();
        recyclerView.setLayoutManager(layoutManager);

    }

//interaction with adapter
    private final BeatClickCallback beatClickCallback = new BeatClickCallback() {

        @Override
        public void onClick(Measure measure, View beatView, int beatPosition) {
            currentMeasure = measure;
            currentBeatPosition = beatPosition;
            currentBeatView = beatView;

            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                show(measure, currentBeatPosition);
            }
        }

    };

//DAO methods
    public void addEmptyMeasure(MeasureListViewModel viewModel) {
        viewModel.addEmptyMeasure(getApplication());

    }

    public void updateMeasure(MeasureListViewModel viewModel, Measure measure) {

        viewModel.updateMeasure(getApplication(), measure);
    }

    public void deleteMeasure(MeasureListViewModel viewModel) {
        viewModel.deleteMeasure(getApplication());

    }

//when button pressed in editFragment
    @Override
    public void onFragmentInteraction(View view) {

        switch (view.getId()) {

            case R.id.add_chord:
                Button usedStack1 = findViewById(R.id.last_used_1);

                if (chord !=null) {

                    beatsForInsersion = new ArrayList<>(currentMeasure.getBeats());
//                    for (int i=0; i<beatsForInsersion.size(); i++) {
                    if (chord != null)
                        beatsForInsersion.get(currentBeatPosition).setChordName(chord);
                        //TODO prompt for next beat
//                    }
                    currentMeasure.setBeats(beatsForInsersion);

                    //update database
                    updateMeasure(viewModel, currentMeasure);
                    usedStack1.setText(chord);
                    recyclerView.findViewHolderForAdapterPosition(currentBeatPosition + 1).itemView.performClick();
                 }
                break;
            case R.id.c:
                onRootSelected(view, IS_C_ROOT_PRESSED);
                break;
            case R.id.d:
                onRootSelected(view, IS_D_ROOT_PRESSED);
                break;
            case R.id.e:
                onRootSelected(view, IS_E_ROOT_PRESSED);
                break;
            case R.id.f:
                onRootSelected(view, IS_F_ROOT_PRESSED);
                break;
            case R.id.g:
                onRootSelected(view, IS_G_ROOT_PRESSED);
                break;
            case R.id.a:
                onRootSelected(view, IS_A_ROOT_PRESSED);
                break;
            case R.id.b:
                onRootSelected(view, IS_B_ROOT_PRESSED);
                break;
        }
    }

//selecting chord root on edit fragment
    public void onRootSelected(View view, String isPressedPref) {
        TextView preview = findViewById(R.id.preview_select_tv);

        if (!preferences.getBoolean(isPressedPref, false)) {
            view.setPressed(true);
            view.setBackgroundColor(getResources().getColor(R.color.buttonPressed));
            editor.putBoolean(isPressedPref, true).commit();
            String text = (String) ((Button) view).getText();
            chord = text;
            preview.setText(chord);
        } else if ((preferences.getBoolean(isPressedPref, false))) {

            view.setBackground(getResources().getDrawable(R.drawable.measure_background_white));
            editor.putBoolean(isPressedPref, false).commit();
            view.setPressed(false);
            chord = "";
            preview.setText(chord);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (editFragmentContainer.getVisibility() == View.VISIBLE) {
            editFragmentContainer.setVisibility(View.GONE);
        }
    }

    public void initSharedPrefs() {
            final SharedPreferences preferences = getApplicationContext().getSharedPreferences("myApp", MODE_PRIVATE);
            this.preferences = preferences;
            Editor editor = preferences.edit();
            this.editor = editor;

            editor.putBoolean(IS_C_ROOT_PRESSED, false).commit();
            editor.putBoolean(IS_D_ROOT_PRESSED, false).commit();
            editor.putBoolean(IS_E_ROOT_PRESSED, false).commit();
            editor.putBoolean(IS_F_ROOT_PRESSED, false).commit();
            editor.putBoolean(IS_G_ROOT_PRESSED, false).commit();
            editor.putBoolean(IS_A_ROOT_PRESSED, false).commit();
            editor.putBoolean(IS_B_ROOT_PRESSED, false).commit();


    }



}
