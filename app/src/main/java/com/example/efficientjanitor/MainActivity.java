package com.example.efficientjanitor;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.efficientjanitor.animation.PushDownAnim;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final float MAX_BAG_WEIGHT = 3.0f;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private EditText etInputBagWeight;
    private TextView tvTotalBags, tvAnswer;
    private ImageButton btnCalc, btnReset;
    private List<Float> weightList = new ArrayList<>();

    // Local variables to keep track for UI purpose
    private int numberOfBags;
    private float combineWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initAnimationAndClickListeners();
    }
    private void initViews() {
        etInputBagWeight = findViewById(R.id.et_bag_weight);
        tvTotalBags = findViewById(R.id.total_bags);
        btnCalc = findViewById(R.id.btn_calc);
        btnReset = findViewById(R.id.btn_reset);
        tvAnswer = findViewById(R.id.tv_answer);

        // Init for first time so 0 is shown instead of %d
        setTotalBagsView(numberOfBags, String.valueOf(combineWeight));
    }

    private void initAnimationAndClickListeners() {
        PushDownAnim.setPushDownAnimTo(btnCalc).setOnClickListener(this);
        PushDownAnim.setPushDownAnimTo(btnReset).setOnClickListener(this);
    }

    private void reset() {
        etInputBagWeight.setText("");
        numberOfBags = 0;
        combineWeight = 0;
        setTotalBagsView(numberOfBags, String.valueOf(combineWeight));
        weightList.clear();
        tvAnswer.setText("");
    }

    private void setTotalBagsView(int currentNumberOfBags, String currentCombineWeight) {
        tvTotalBags.setText(String.format(getString(R.string.total_bags), currentNumberOfBags, currentCombineWeight));
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == btnCalc.getId()) {
            // Surround this code with try/catch because we trying to parse a String to Float and it can fail
            try {
                float bagWeight = Float.parseFloat(etInputBagWeight.getText().toString());
                // UI actions
                etInputBagWeight.setText("");
                /* Check if bag is no more then 3kg and if so then stop cause this will cause the while loop at function calculateTrips() to run indefinitely and crash the app(
                if a bag weight is greater than MAX_BAG_WEIGHT the condition currentWeight + weight <= MAX_BAG_WEIGHT will never be true for that bag
                as a result the bag is never removed from the list causing the outer while loop to run indefinitely because localBag is never empty) */
                if (bagWeight > MAX_BAG_WEIGHT) {
                    tvAnswer.setText(String.format(getString(R.string.warning), String.valueOf(MAX_BAG_WEIGHT)));
                    return;
                }
                combineWeight = combineWeight + bagWeight;
                setTotalBagsView(++numberOfBags, decimalFormat.format(combineWeight));

                // Logic actions (usally done at viewModel but since this is a basic simple App we dont need to over complex simple tasks :) )
                weightList.add(bagWeight);
                Map<Integer, List<Float>> resultMap = calculateTrips(weightList);
                StringBuilder tripsText = new StringBuilder();
                for (Map.Entry<Integer, List<Float>> entry : resultMap.entrySet()) {
                    tripsText.append("Trip ").append(entry.getKey()).append(": ").append(entry.getValue().toString()).append("\n");
                }
                tvAnswer.setText(tripsText.toString());
            } catch (Exception e) {
                // Because this is a simple app for test we dont have behavior for failing except not crashing :)
                Log.e("MainActivity", "got exception ", e);
            }
        } else if (viewId == btnReset.getId()) {
            reset();
        }
    }

    public Map<Integer, List<Float>> calculateTrips(List<Float> bags) {
        // Create new list from passed list reference so we can modify it without changing the original list
        List<Float> localBag = new ArrayList<>(bags);
        // Minimum trips is always at least one
        int tripNumber = 1;
        // Create a Map that will contain the trip number and the bags order for that trip
        Map<Integer, List<Float>> trips = new HashMap<>();
        /* Run a while loop on the localBag list so we iterate over the bags adding each bag to the current trip if the total weight does not exceed 3kg
        once a bag is added to a trip it is removed from the list this process continues until all bags have been added to trips */
        while (!localBag.isEmpty()) {
            float currentWeight = 0;
            List<Float> currentTrip = new ArrayList<>();
            Iterator<Float> iterator = localBag.iterator();
            // Inner while loop to iterate for next element
            while (iterator.hasNext()) {
                float weight = iterator.next();
                if (currentWeight + weight <= MAX_BAG_WEIGHT) {
                    currentWeight += weight;
                    currentTrip.add(Float.valueOf(decimalFormat.format(weight)));
                    iterator.remove();
                }
            }
            trips.put(tripNumber, currentTrip);
            tripNumber++;
        }
        return trips;
    }

}