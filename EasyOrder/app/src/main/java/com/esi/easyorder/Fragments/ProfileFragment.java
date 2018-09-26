package com.esi.easyorder.Fragments;

import android.Manifest;
import android.animation.Animator;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dd.processbutton.iml.ActionProcessButton;
import com.esi.easyorder.ActiveCart;
import com.esi.easyorder.Adapters.CartAdapter;
import com.esi.easyorder.Item;
import com.esi.easyorder.Order;
import com.esi.easyorder.R;
import com.esi.easyorder.User;
import com.esi.easyorder.activites.CartActivity;
import com.esi.easyorder.activites.MainActivity;
import com.esi.easyorder.activites.MenuActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Server on 12/04/2018.
 */

public class ProfileFragment extends android.support.v4.app.Fragment {
    public MenuActivity menuActivity;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    LinearLayout passwordChange;
    LinearLayout controlsButtons;
    LinearLayout updateLayout;
    LinearLayout addressLayout;
    TextView updateText;
    FloatingActionButton changePassword;
    FloatingActionButton changeEmail;
    FloatingActionButton changeLocation;
    FloatingActionButton changePhone;
    FloatingActionMenu actionMenu;
    FancyButton saveChanges;
    FancyButton cancelChanges;
    EditText username;
    EditText email;
    EditText address;
    EditText phone;
    boolean isChangingPassword = false;
    boolean isEditingEmailNUser = false;
    boolean isEditingAddress = false;
    boolean isChangingPhone = false;
    boolean locationLocated = false;
    boolean isChangeAddress = false;
    ActionProcessButton getLocation;
    User user = null;
    String language;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.profile_layout, container, false);
        username = view.findViewById(R.id.profileUsername);
        email = view.findViewById(R.id.profileEmail);
        address = view.findViewById(R.id.profileAddress);
        phone = view.findViewById(R.id.profilePhone);
        passwordChange = view.findViewById(R.id.passwordChange);
        controlsButtons = view.findViewById(R.id.profileControlLayout);
        changePassword = view.findViewById(R.id.fab_changePassword);
        changeEmail = view.findViewById(R.id.fab_changeEmail);
        changeLocation = view.findViewById(R.id.fab_changeLocation);
        changePhone = view.findViewById(R.id.fab_changePhone);
        saveChanges = view.findViewById(R.id.saveChanges);
        cancelChanges = view.findViewById(R.id.cancelChanges);
        updateLayout = view.findViewById(R.id.updateLayout);
        updateText = view.findViewById(R.id.updateText);
        addressLayout = view.findViewById(R.id.addressLayout);
        actionMenu = view.findViewById(R.id.fab_menu);
        getLocation = view.findViewById(R.id.getLocation);
        pref = getActivity().getSharedPreferences("global",0);
        language = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("Language","ar");
        editor = pref.edit();
        if (menuActivity != null) {
            final String userString = PreferenceManager.getDefaultSharedPreferences(menuActivity).getString("user", "");
            if(!userString.equals(""))
            {
                user = new User();
                user.Deseralize(userString);
                username.setText(user.username);
                email.setText(user.Email);
                address.setText(user.Address);
                phone.setText(user.Telephone);
                menuActivity.setTitle(user.username + getString(R.string.menuprofile));
            }
        }

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TogglePasswordChange(true);
                ToggleControls(true);
                actionMenu.close(true);
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleEmailNPassword(true);
                ToggleControls(true);
                actionMenu.close(true);
            }
        });
        changeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleAddress(true);
                ToggleControls(true);
                actionMenu.close(true);
            }
        });
        changePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TogglePhone(true);
                ToggleControls(true);
                actionMenu.close(true);
            }
        });
        cancelChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TogglePasswordChange(false);
                ToggleControls(false);
                ToggleEmailNPassword(false);
                ToggleAddress(false);
                address.setText(user.Address);
                TogglePhone(false);
            }
        });
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean changed = false;
                if(isChangingPassword)
                {
                    EditText currentPassword = getView().findViewById(R.id.profileCurrentPassword);
                    EditText newPassword = getView().findViewById(R.id.profileNewPassword);
                    EditText newPasswordConfirm = getView().findViewById(R.id.profileNewPasswordConfirm);
                    String currentPasswordString = currentPassword.getText().toString();
                    String newPasswordString = newPassword.getText().toString();
                    String newPasswordConfirmString = newPasswordConfirm.getText().toString();

                    if(currentPasswordString.equals(newPasswordString))
                    {
                        Toast.makeText(menuActivity, "The password you trying to change is the same password you currently have!", Toast.LENGTH_SHORT).show();
                        changed = false;
                    }
                    else
                    {
                        if (newPasswordString.equals("") || currentPasswordString.equals(""))
                        {
                            Toast.makeText(menuActivity, "Please enter the password fields to continue", Toast.LENGTH_SHORT).show();
                            changed = false;
                        }
                        else if(!currentPasswordString.equals(user.Password))
                        {
                            Toast.makeText(menuActivity, "Your current password is incorrect, please try again", Toast.LENGTH_SHORT).show();
                            changed = false;
                        }
                        else if(newPasswordString.equals(newPasswordConfirmString))
                        {
                            UpdateUserPassword(newPasswordString);
                            user.Password = newPasswordString;
                        }
                        else
                        {
                            Toast.makeText(menuActivity, "Password doesn't match", Toast.LENGTH_SHORT).show();
                            changed = false;
                        }
                    }
                    if(changed) {
                        ToggleUpdate(true, false);
                    }
                }
                if(isEditingEmailNUser)
                {
                    boolean isEditingUsername = true;
                    boolean isEditingEmail = true;
                    if(username.getText().toString().equals(user.username))
                    {
                        isEditingUsername = false;
                    }
                    if(email.getText().toString().equals(user.Email))
                    {
                        isEditingEmail = false;
                    }

                    if(!isEditingUsername && !isEditingEmail)
                    {
                        Toast.makeText(menuActivity, "Please change the values of the required filed to update it", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String newUsername = username.getText().toString();
                        String newEmail = email.getText().toString();
                        if(isEditingEmail && isEditingUsername)
                        {

                            if(!newUsername.equals("") && !newEmail.equals(""))
                            {
                                if(newUsername.length() >= 6)
                                {
                                    UpdateUserEmailnUsername(newUsername, newEmail);
                                    user.username = newUsername;
                                    user.Email = newEmail;
                                    changed = true;
                                }
                            }
                        }
                        else
                        {
                            if(isEditingEmail)
                            {
                                if(!newEmail.equals(""))
                                {
                                    UpdateUserEmail(newEmail);
                                    user.Email = newEmail;
                                    changed = true;
                                }
                            }
                            else if(isEditingUsername)
                            {
                                if(!newUsername.equals(""))
                                {
                                    if(newUsername.length() >= 6)
                                    {
                                        UpdateUserName(newUsername);
                                        user.username = newUsername;
                                        changed = true;
                                    }
                                }
                            }
                        }
                    }
                    if(changed)
                        ToggleUpdate(true, false);
                }
                if(isEditingAddress)
                {

                    if(locationLocated)
                    {
                        EditText building = getView().findViewById(R.id.profileAddressBuilding);
                        EditText floorApt = getView().findViewById(R.id.profileAddressFloor);
                        EditText apt = getView().findViewById(R.id.profileAddressApt);
                        String buildingNumber = building.getText().toString();
                        String floor = floorApt.getText().toString();
                        String aprt = apt.getText().toString();
                        if(buildingNumber.equals("") || floor.equals(""))
                        {
                            Toast.makeText(menuActivity, "Please enter your building number and floor, apartment number", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String finalLocation = "مبني " + buildingNumber + " الدور " + floor + ", الشقة  " + aprt + " , " + address.getText().toString();
                            UpdateUserAdress(finalLocation);
                            user.Address = finalLocation;
                            address.setText(finalLocation);
                        }
                    }
                    else
                    {
                        Toast.makeText(menuActivity, "Please press on locate me to get your current location", Toast.LENGTH_SHORT).show();
                    }
                }
                if(isChangingPhone)
                {
                    String phoneNumber = phone.getText().toString();
                    if(!phoneNumber.equals(""))
                    {
                        if(phoneNumber.equals(user.Telephone))
                        {
                            Toast.makeText(menuActivity, "Please change the phone number or press cancel if you don't want to change it", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            UpdateUserPhone(phoneNumber);
                            user.Telephone = phoneNumber;
                        }
                    }
                    else
                    {
                        Toast.makeText(menuActivity, "Please enter your new phone number!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation.setProgress(1);
                final FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(menuActivity);
                LocationManager manager = (LocationManager) menuActivity.getSystemService(Context.LOCATION_SERVICE);
                boolean gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!gpsStatus) {
                    Toast.makeText(menuActivity, "Please enable your gps to be able to locate your location", Toast.LENGTH_SHORT).show();
                    getLocation.setProgress(-1);
                    return;
                }
                if (ActivityCompat.checkSelfPermission(menuActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(menuActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(menuActivity, "Location permission is blocked, please allow the application to use GPS", Toast.LENGTH_SHORT).show();
                    getLocation.setProgress(-1);
                    return;
                }

                getLocation.setProgress(50);
                locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        Toast.makeText(menuActivity, "Make sure that we have your current location correctly, in case it is not try renabling your gps or moving around.", Toast.LENGTH_LONG).show();
                        user.location = location;
                        locationLocated = true;
                        getLocation.setProgress(100);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("language ", language);
                                String uri = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng="+location.getLatitude()+","+location.getLongitude()+"&language="+language+"&key=AIzaSyAVA0pPuqzogG_SXD8yhRDKkSPSmNgyBhc");
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder().url(uri).build();
                                Response response = null;
                                try {
                                    response = client.newCall(request).execute();
                                    String fullString = response.body().string();
                                    JSONObject object = new JSONObject(fullString);
                                    Log.d("JSON Recieved", fullString);
                                    JSONArray results = object.getJSONArray("results");
                                    final String formattedaddress = results.getJSONObject(0).getString("formatted_address");
                                    Log.d("Address is: ", formattedaddress);
                                    menuActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            address.setText(formattedaddress);
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        getLocation.setProgress(-1);
                    }
                });

            }
        });
        return view;
    }

    public void TogglePasswordChange(boolean show)
    {
        isChangingPassword = show;
        if(show && passwordChange.getVisibility() == View.VISIBLE)
            return;
        if(!show && passwordChange.getVisibility() == View.GONE)
            return;

        if(!show)
        {
            YoYo.with(Techniques.SlideOutUp).duration(700).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    passwordChange.setVisibility(View.GONE);
                }
            }).playOn(passwordChange);
            EditText currentPassword = getView().findViewById(R.id.profileCurrentPassword);
            EditText newPassword = getView().findViewById(R.id.profileNewPassword);
            EditText newPasswordConfirm = getView().findViewById(R.id.profileNewPasswordConfirm);
            currentPassword.setText("");
            newPassword.setText("");
            newPasswordConfirm.setText("");
        }
        else
        {
            YoYo.with(Techniques.FadeIn).duration(700).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    passwordChange.setVisibility(View.VISIBLE);
                }
            }).playOn(passwordChange);
        }
    }

    public void ToggleControls(boolean show)
    {
        if(show && controlsButtons.getVisibility() == View.VISIBLE)
            return;
        if(!show && controlsButtons.getVisibility() == View.GONE)
            return;

        if(show)
        {
            YoYo.with(Techniques.SlideInLeft).duration(700).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    controlsButtons.setVisibility(View.VISIBLE);
                }
            }).playOn(controlsButtons);
        }
        else
        {
            YoYo.with(Techniques.SlideOutLeft).duration(700).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    controlsButtons.setVisibility(View.GONE);
                }
            }).playOn(controlsButtons);
        }
    }

    public void ToggleEmailNPassword(boolean enabled)
    {
        isEditingEmailNUser = enabled;
        if(enabled && username.isEnabled() && email.isEnabled())
            return;
        if(!enabled && !username.isEnabled() && !email.isEnabled())
            return;
        username.setEnabled(enabled);
        email.setEnabled(enabled);
    }

    public void ToggleAddress(boolean show)
    {
        isEditingAddress = show;
        if(show && addressLayout.getVisibility() == View.VISIBLE)
            return;
        if(!show && addressLayout.getVisibility() == View.GONE)
            return;

        if(show)
        {
            addressLayout.setVisibility(View.VISIBLE);
            address.setText("");

        }
        else
        {
            addressLayout.setVisibility(View.GONE);
        }
        address.setEnabled(show);
    }

    public void TogglePhone(boolean show)
    {
        isChangingPhone = show;
        phone.setEnabled(show);
    }
    void UpdateUserPassword(String newpassword)
    {
        if(user != null)
        {
            JSONObject msg = new JSONObject();
            try {
                msg.put("Msg", "update_user");

                msg.put("change_type", "password");
                msg.put("user_id", user.ID);
                msg.put("new_password", newpassword);
                menuActivity.serverService.sendMessage(msg.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void ToggleUpdate(boolean show, boolean delay)
    {
        if(show && updateLayout.getVisibility() == View.VISIBLE)
            return;
        if(!show && updateLayout.getVisibility() == View.GONE)
            return;

        if(show)
        {
            YoYo.with(Techniques.FadeIn).duration(700).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    updateLayout.setVisibility(View.VISIBLE);
                }
            }).playOn(updateLayout);
        }
        else
        {
            if(delay) {
                YoYo.with(Techniques.FadeOut).duration(700).delay(2000).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        updateLayout.setVisibility(View.GONE);
                    }
                }).playOn(updateLayout);
            }
            else
            {
                YoYo.with(Techniques.FadeOut).duration(700).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        updateLayout.setVisibility(View.GONE);
                    }
                }).playOn(updateLayout);
            }
        }
    }
    public void UserUpdate(boolean update, String message)
    {
        if(update)
        {
            updateText.setText("Updated!");
            if(isChangingPassword) {
                TogglePasswordChange(false);
                isChangingPassword = false;
            }
            if(isEditingEmailNUser)
            {
                ToggleEmailNPassword(false);
                isEditingEmailNUser = false;
            }
            if(isEditingAddress)
            {
                ToggleAddress(false);
                isEditingAddress = false;
                    String icart = getActivity().getIntent().getStringExtra("cart");
                    isChangeAddress = getActivity().getIntent().getBooleanExtra("isChangeAddress",false);
                    Log.d("Cart ", " "+icart);
                    Log.d("isChangeAddress ", " "+String.valueOf(isChangeAddress));
                    if(isChangeAddress){
                        try {
                            menuActivity.serverService.sendMessage(icart);
                            Order order = new Order();
                            ActiveCart cart = new ActiveCart();
                            cart.deserialize(getActivity().getIntent().getStringExtra("active_cart"));
                            order.cartOrder = cart;
                            order.viewd = false;
                            order.ID = 1;
                            order.OrderAddress = user.Address;
                            user.Orders.add(order);

                            editor.remove("cart");
                            editor.apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        isChangeAddress = false;
                    }
            }
            if(isChangingPhone)
            {
                TogglePhone(false);
                isChangingPhone = false;
            }
            ToggleControls(false);


            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove("user").apply();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("user", user.toObject().toString()).apply();
        }
        else
        {
            updateText.setText("Failed to Update, please try again");
        }
        if(message.equals(""))
            message = "Profile Update Failed";
        Toast.makeText(menuActivity, message, Toast.LENGTH_SHORT).show();
        ToggleUpdate(false, true);
    }

    public void UpdateUserEmailnUsername(String username, String email)
    {
        JSONObject message = new JSONObject();
        try {
            message.put("Msg", "update_user");
            message.put("change_type", "usernamenemail");
            message.put("user_id", user.ID);
            message.put("username", username);
            message.put("email", email);
            menuActivity.serverService.sendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void UpdateUserEmail(String email)
    {
        JSONObject message = new JSONObject();
        try {
            message.put("Msg", "update_user");
            message.put("change_type", "email");
            message.put("user_id", user.ID);
            message.put("email", email);
            menuActivity.serverService.sendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void UpdateUserName(String name)
    {
        JSONObject message = new JSONObject();
        try {
            message.put("Msg", "update_user");
            message.put("change_type", "username");
            message.put("user_id", user.ID);
            message.put("username", name);
            menuActivity.serverService.sendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void UpdateUserAdress(String addres)
    {
        JSONObject message = new JSONObject();
        try {
            message.put("Msg", "update_user");
            message.put("change_type", "address");
            message.put("user_id", user.ID);
            message.put("address", addres);
            message.put("lat", user.location.getLatitude());
            message.put("long", user.location.getLongitude());
            menuActivity.serverService.sendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void UpdateUserPhone(String phone)
    {
        JSONObject message = new JSONObject();
        try {
            message.put("Msg", "update_user");
            message.put("change_type", "phone");
            message.put("user_id", user.ID);
            message.put("phone", phone);
            menuActivity.serverService.sendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
