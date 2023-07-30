package com.example.caloriecalculator;



import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.lang.reflect.Type;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {


    FirebaseFirestore fstore;
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    private TextInputEditText age, height, weight;
    private RadioGroup gender;
    private MaterialRadioButton male, female;
    private TextView calories, required, textView1, textView2, textView3, textView4, textView5, textView6, text_dummy;
    private AppCompatButton calculate, reset;
    String m="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fstore = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView= findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        age = findViewById(R.id.age);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        gender = findViewById(R.id.gender);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        calories = findViewById(R.id.calories);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        text_dummy = findViewById(R.id.text_dummy);
        required = findViewById(R.id.required);
        calculate = findViewById(R.id.calculate);
        reset = findViewById(R.id.reset);



        if (user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else{
            textView.setText(user.getEmail());
            DocumentReference documentReference = fstore.collection("users").document(user.getEmail());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, retrieve the data


                        // Retrieve other fields as needed

                         //Use the retrieved data as desired
                        String f= document.getString("gender");

                        if (f != null) {
                            if (f.equals("male")) {
                                male.setChecked(true);
                            } else if (f.equals("female")) {
                                female.setChecked(true);
                            }
                        }

                        age.setText(document.getString("age"));
                        height.setText(document.getString("height"));
                        weight.setText(document.getString("weight"));

                        textView1.setText(document.getString("little or no exercise"));
                        textView2.setText(document.getString("exercise1_3"));
                        textView3.setText(document.getString("exercise3_4"));
                        textView4.setText(document.getString("intense3_4"));
                        textView5.setText(document.getString("intense6_7"));
                        textView6.setText(document.getString("very intense exercise daily"));

                        Log.d(TAG, "Age: " + age);
                        Log.d(TAG, "Height: " + height);
                        Log.d(TAG, "Weight: " + weight);
                        Log.d(TAG, "Gender: " + gender);


                        // Update UI or perform any other operations with the data
                        // ...
                    } else {
                        // Document doesn't exist
                        Log.d(TAG, "No such document");
                    }
                } else {
                    // Task failed with exception
                    Log.d(TAG, "Task failed with exception: ", task.getException());
                }
            }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG,"on Failure:"+ e.toString());
                }
            });


        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });



        reset.setOnClickListener(v -> {
            age.setText("");
            height.setText("");
            weight.setText("");
            gender.clearCheck();
            calories.setText("Calories");
            textView1.setText("");
            textView2.setText("");
            textView3.setText("");
            textView4.setText("");
            textView5.setText("");
            textView6.setText("");
            text_dummy.setVisibility(View.GONE);
            required.setVisibility(View.GONE);
        });


        calculate.setOnClickListener(v -> {


            String ageText = age.getText().toString();
            String heightText = height.getText().toString();
            String weightText = weight.getText().toString();

            // Creating the pattern for the regular expression
            // This will check if the value is a number or not
            Pattern pattern = Pattern.compile("[0-9]+");

            // Creating the variables for the checks and setting them to false
            // These will be used to check if the values are empty or not
            boolean ageCheck = false;
            boolean heightCheck = false;
            boolean weightCheck = false;

            // Checking if the age text field is empty or not
            // If it is empty, then it will show an error message
            if(ageText.isEmpty()){
                age.setError("Please enter your age");
                age.requestFocus();
                ageCheck = false;
            } else if (!pattern.matcher(ageText).matches()) {
                age.setError("Please enter your age correctly");
                age.requestFocus();
                ageCheck = false;
            } else {
                age.setError(null);
                ageCheck = true;
            }

            // Checking if the height text field is empty or not
            // If it is empty, then it will show an error message
            if(heightText.isEmpty()){
                height.setError("Please enter your height");
                height.requestFocus();
                heightCheck = false;
            } else if (!pattern.matcher(heightText).matches()) {
                age.setError("Please enter your age correctly");
                age.requestFocus();
                heightCheck = false;
            } else {
                height.setError(null);
                heightCheck = true;
            }

            // Checking if the weight text field is empty or not
            // If it is empty, then it will show an error message
            if(weightText.isEmpty()){
                weight.setError("Please enter your weight");
                weight.requestFocus();
                weightCheck = false;
            } else if (!pattern.matcher(weightText).matches()) {
                age.setError("Please enter your age correctly");
                age.requestFocus();
                weightCheck = false;
            } else {
                weight.setError(null);
                weightCheck = true;
            }

            // Checking if the user has selected the gender or not
            if(gender.getCheckedRadioButtonId() == -1) {
                required.setText("Please Select Gender");
                required.setVisibility(View.VISIBLE);
            } else {
                required.setText("");
                required.setVisibility(View.GONE);

                // Checking if all the values are not empty
                if(ageCheck && heightCheck && weightCheck){

                    // Calling the calculateBMR method
                    calculateCalorie();
                    DocumentReference documentReference =fstore.collection("users").document(user.getEmail());
                    Map<String,Object> userf = new HashMap<>();
                    userf.put("age", ageText);
                    userf.put("height", heightText);
                    userf.put("weight", weightText);
                    userf.put("gender", m);
                    userf.put("little or no exercise", textView1.getText());
                    userf.put("exercise1_3", textView2.getText());
                    userf.put("exercise3_4", textView3.getText());
                    userf.put("intense3_4", textView4.getText());
                    userf.put("intense6_7", textView5.getText());
                    userf.put("very intense exercise daily", textView6.getText());
                    documentReference.set(userf).addOnSuccessListener((OnSuccessListener) (aVoid) ->{
                        Log.d(TAG, "on success: user profile is created");
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG,"on Failure:"+ e.toString());
                        }
                    });
                }
            }


        });

    }

    // Creating the calculate method to calculate the calories required
    public void calculateCalorie(){

        // Getting the values from the text fields
        int ageValue = Integer.parseInt(age.getText().toString());
        int heightValue = Integer.parseInt(height.getText().toString());
        int weightValue = Integer.parseInt(weight.getText().toString());

        // Creating the variable for the total calories
        double totalCalories = 0;

        if(gender.getCheckedRadioButtonId()== male.getId()){
            m="male";
            // If user is "Male" then the following formula will be used to calculate the calories
            totalCalories = (10 * weightValue) + (6.25 * heightValue) - (5 * ageValue + 5);

            // Setting the text to the calories text view
            text_dummy.setVisibility(View.VISIBLE);

        } else {
            m="female";
            // If user is "Female" then the following formula will be used to calculate the calories
            totalCalories = (10 * weightValue) + (6.25 * heightValue) - (5 * ageValue - 161);
            calories.setText(String.format("%.2f", totalCalories)+"*");
            text_dummy.setVisibility(View.VISIBLE);
        }

        // Setting the text to the calories in the table layout and rounding it to 2 decimal places
        textView1.setText(String.format("%.2f", totalCalories)+"*");
        textView2.setText(String.format("%.2f", totalCalories*1.149)+"*");
        textView3.setText(String.format("%.2f", totalCalories*1.220)+"*");
        textView4.setText(String.format("%.2f", totalCalories*1.292)+"*");
        textView5.setText(String.format("%.2f", totalCalories*1.437)+"*");
        textView6.setText(String.format("%.2f", totalCalories*1.583)+"*");

        // Setting the text to the text view and making it visible
        required.setText("*"+"Thank you for using this app");
        required.setTextSize(12);
        required.setVisibility(View.VISIBLE);

    }
}