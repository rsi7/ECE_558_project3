package ece558.pdx.edu.project3;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.PlacePicker;


/**
 * LocationUpdate.java handles the UI appearing on Sign In
 *
 */
public class LocationUpdate extends AppCompatActivity {
    private static final String EXTRA_USER_NAME="edu.pdx.ece.fjl.project3.user_name";
    private static final int PLACE_PICKER_REQUEST = 1;
    private EditText send_message;
    private EditText mLocationET;
    private TextView mLocationTV;
    private Button send;
    private Button mCurrentLoc;
    Editable message;
    LoginDataBaseAdapter loginData;
    public double latitude,longitude,modalat,modalon;
    public String address;

    public static Intent newIntent(Context packageContext, String userName) {
        Intent intent = new Intent(packageContext,LocationUpdate.class);
        intent.putExtra(EXTRA_USER_NAME, userName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_update);

        mLocationET = (EditText) findViewById(R.id.Location_EditText);
        mLocationET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(LocationUpdate.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        mLocationTV = (TextView) findViewById(R.id.Current_Loc_TextView);

        mCurrentLoc = (Button) findViewById(R.id.Current_Loc_Button);
        mCurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationTV.setText(address);
            }
        });

        /*
         * Do not edit the code below as it is dependent on server just fill the required snippets
         *
         */
        send_message = (EditText) findViewById(R.id.Send_Message);
        send = (Button) findViewById(R.id.Send_Button);
        loginData = new LoginDataBaseAdapter(this);
        loginData = loginData.open();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * OnClick event for send button gets username and location details
                 */
                message=send_message.getText();
                Bundle extras=getIntent().getExtras();
                String rx_username=extras.getString(EXTRA_USER_NAME);

                String rx_lat=loginData.getLat(rx_username);
                String rx_lon=loginData.getLng(rx_username);

                /**
                 * store in latitude , longitude variables to pass to json object
                 */
                modalat= Double.parseDouble(rx_lat);
                modalon= Double.parseDouble(rx_lon);

                try {

                    /**
                     * Creates a JSON object and uses toSend.put to send home, current location along with message
                     *Pass data as name/value pair where you cannot edit name written
                     *in " " ex:"home_lat" as this are hard coded on server side.
                     *You can change the variable name carrying value ex:modalat
                     */
                    JSONObject toSend = new JSONObject();
                    toSend.put("home_lat", modalat);
                    toSend.put("home_lon",modalon);
                    toSend.put("c_lat", latitude);
                    toSend.put("c_lon",longitude);
                    toSend.put("message",message);

                    /**
                     * Creates transmitter object to send data to server
                     */
                    JSONTransmitter transmitter = new JSONTransmitter();
                    transmitter.execute(new JSONObject[] {toSend});

                    /**
                     * Receives a message from the server which is displayed as toast
                     */
                    JSONObject output=transmitter.get();
                    String op=output.getString("message");
                    Toast.makeText(LocationUpdate.this,op, Toast.LENGTH_LONG).show();

                }
                //To handle exceptions
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng LaLo = place.getLatLng();
                latitude = LaLo.latitude;
                longitude = LaLo.longitude;
                address = place.getAddress().toString();
                mLocationET.setText(address);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id== R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}