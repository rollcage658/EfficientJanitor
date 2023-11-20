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

/*
Below is written the data structures,Objects and function used:

Data Structures:
we use List of floats to store values(no need for double as it just gives a larger number(up to 64bits))
then we pass the weight list to the function to get a Map<Integer, List<Float>> which represent the trip number and bag order accordingly
List<Float> weightList: list that stores the weights of the bags as floats numbers
Map<Integer, List<Float>> trips: map that stores the trip number as the key and a list of bag weights for that trip as the value

we also use Primitive variables to achieve the following:
float MAX_BAG_WEIGHT: constant that represents the maximum weight a bag can have used for function calculateTrips to determine number of trips needed and used for checking valid weight enter
int numberOfBags: int that keeps track of the total number of bags for the UI
float combineWeight: float number that represents the combined weight of all the bags for the UI

Objects:
EditText etInputBagWeight: EditText object where the user enters the weight of a bag
TextView tvTotalBags, tvAnswer: These are TextView objects used to display the total number of bags and the answer respectively
ImageButton btnCalc, btnReset: These are ImageButton objects which the user can click to calculate the trips or reset
DecimalFormat decimalFormat: DecimalFormat object used to format the weights to have up to two decimal places

The MainActivity class also implements the View.OnClickListener interface which means it has an onClick method that gets called when a view is clicked
The onClick method checks which view was clicked and performs the appropriate action If calculate button was clicked, it calculates the trips and displays the result and If the reset button was clicked, it resets the inputs and outputs.
The calculateTrips method calculates the minimum number of trips needed to carry all the bags and returns a Map<Integer, List<Float>> of the trips
The initViews and initAnimationAndClickListeners methods are used to initialize the views and set up the click listeners and animations
The setTotalBagsView method updates the display of the total number of bags and their combined weight
The reset method resets the inputs and outputs
*/