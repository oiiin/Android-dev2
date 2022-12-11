package algonquin.cst2335.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;

import android.view.View;

import android.widget.ImageView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import algonquin.cst2335.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding variableBinding;
    private static String TAG = "MainActivity";

    private Button Forecast;

    private EditText editCity;

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        variableBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(variableBinding.getRoot());
        Forecast=variableBinding.button;
        editCity=findViewById(R.id.edit_text);
        Forecast.setOnClickListener( clk-> {



            //This part goes at the top of the onCreate function:

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                //this goes in the button click handler:
                String cityName = editCity.getText().toString();
                String url = null;

                try {
                    url = "https://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(cityName,"UTF-8") + "&appid=7e943c97096a9784391a981c4d878b22&units=metric";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                final RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        (response)-> {
                            try {
                                JSONObject coord=response.getJSONObject("coord");
                                JSONArray weatherArray = response.getJSONArray ( "weather" );
                                JSONObject weather = weatherArray.getJSONObject(0);
                                String description = weather.getString("description");
                                String iconName = weather.getString("icon");
                                int vis = response.getInt("visibility");
                                String name = response.getString( "name" );
                                JSONObject mainObject = response.getJSONObject("main");
                                double current = mainObject.getDouble("temp");
                                double min = mainObject.getDouble("temp_min");
                                double max   = mainObject.getDouble("temp_max");
                                int humidity = mainObject.getInt("humidity");
                                String imgURL="https://openweathermap.org/img/w/" + iconName + ".png";

                                ImageRequest imgReq = new ImageRequest(imgURL, new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap bitmap) {
                                        variableBinding.imageView.setImageBitmap(bitmap);


                                    }
                                }, 1024, 1024, ImageView.ScaleType.CENTER, null, (error) -> {

                                });

                                queue.add(imgReq);
                                runOnUiThread(() -> {
                                    variableBinding.currT.setText("The current temperature is " + current);
                                    variableBinding.minT.setText("The min temperature is " + min);
                                    variableBinding.maxT.setText("The max temperature is " + max);
                                    variableBinding.state.setText("Weather description: " + description);
                                    variableBinding.currT.setVisibility (View.VISIBLE) ;
                                    variableBinding.minT.setVisibility (View.VISIBLE);
                                    variableBinding.maxT.setVisibility (View.VISIBLE);
                                    variableBinding.imageView.setVisibility (View.VISIBLE);
                                    variableBinding.state.setVisibility (View.VISIBLE);
                                });

                            }

                            catch (JSONException e){

                            }
                        },
                        (error)-> {

                        });
                queue.add(request);

            });
        });
    }
}