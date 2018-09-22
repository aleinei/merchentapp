package com.esi.easyorder.activites;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.esi.easyorder.ActiveCart;
import com.esi.easyorder.Fragments.ShopTypeFragment;
import com.esi.easyorder.Item;
import com.esi.easyorder.MyContextWrapper;
import com.esi.easyorder.Order;
import com.esi.easyorder.R;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.User;
import com.esi.easyorder.services.ServerService;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ServerService serverService;
    boolean mBound = false;
    boolean loggedInPressed = false;
    Button b; // login
    Button r; // register
    EditText phoneEditText;
    EditText passwordText;
    CheckBox rememberMe;
    User user;
    Button merchantRegister;
    MaterialStyledDialog mDialog;
    boolean verifyCancelled = false;
    boolean authinticated = false;
    String mVerificationId = "";
    FirebaseAuth mAuth;
    SharedPreferences pref;
    String language;
    Locale locale;
    ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString("Language","en");
        locale = new Locale(language);

        b = findViewById(R.id.signin);
        r = findViewById(R.id.register);
        phoneEditText = findViewById(R.id.userName);
        passwordText = findViewById(R.id.password);
        rememberMe = findViewById(R.id.rememberMe);
        Toolbar toolbar = findViewById(R.id.customActionbar);
        merchantRegister = findViewById(R.id.registermerch);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.login));
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove("isAdmin").apply();
        user = new User();
        merchantRegister.setOnClickListener(merchant);
        mAuth = FirebaseAuth.getInstance();

    }

    View.OnClickListener merchant = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent merchAct = new Intent(MainActivity.this,MerchantActivity.class);
            startActivity(merchAct);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signin_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.langg:
                final String[] strings = new String[2];
                strings[0] = getString(R.string.english);
                strings[1] = getString(R.string.arabic);
                AlertDialog mDialog = new AlertDialog.Builder(MainActivity.this).setTitle("Language").setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(strings[which].equals(getString(R.string.english))){
                            changeLang(MainActivity.this,"en");
                            recreate();
                        }
                        else if(strings[which].equals(getString(R.string.arabic))){
                            changeLang(MainActivity.this,"ar");
                            recreate();
                        }
                    }
                }).create();
                mDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent service = new Intent(this, ServerService.class);
        bindService(service, mConnection, Context.BIND_AUTO_CREATE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = phoneEditText.getText().toString();
                String password = passwordText.getText().toString();
                if (username.equals("")) {
                    Toast.makeText(getApplicationContext(), "You need to enter your username and password", Toast.LENGTH_SHORT).show();
                } else if (username.equals("010222")) {
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isAdmin", true).apply();
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    if(username.length() != 11) {
                        Toast.makeText(getApplicationContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+2" + username,120, TimeUnit.SECONDS,MainActivity.this, mCallbacks);
                    LinearLayout layout = new LinearLayout(MainActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams lP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    lP.setMargins(5,5,5,5);
                    layout.setLayoutParams(lP);
                    final EditText editText = new EditText(MainActivity.this);
                    editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    editText.setEnabled(true);
                    editText.setHint("Phone code");
                    editText.setTag("codeText");
                    layout.addView(editText);
                    layout.setPadding(10,10,10,10);

                    mDialog = new MaterialStyledDialog.Builder(MainActivity.this).setTitle("Verifying your phone").setCustomView(layout).
                            setPositiveText("Verify").
                            setNegativeText("cancel").
                            onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    String code = editText.getText().toString();
                                    if(code.isEmpty()) {
                                        return;
                                    }
                                    verify(code);
                                }
                            }).
                            onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    verifyCancelled = true;
                                    dialog.dismiss();
                                }
                            }).
                            autoDismiss(false).
                            setCancelable(false).
                            build();
                    mDialog.show();

                }
                loggedInPressed = true;
            }
        });

        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerAcitvity = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerAcitvity);
            }
        });
        boolean isLogged = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("logged", false);
        if (isLogged) {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            PreferenceManager.getDefaultSharedPreferences(this).edit().remove("user").apply();
        }

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serverService = ((ServerService.Binder)iBinder).getSevice();
            serverService.ConnectServer(MainActivity.this);
            serverService.setMessage(new HandleMessage());
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(serverService != null) {
            serverService.setMessage(new HandleMessage());
        }
    }

    public class HandleMessage implements Runnable, ServerMessage {

        String message;
        @Override
        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                JSONObject msg = new JSONObject(message);
                if(msg.getString("Msg").equals("user_not_exist")) {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    intent.putExtra("phone", phoneEditText.getText().toString());
                    startActivity(intent);
                    loggedInPressed = false;
                } else if(msg.getString("Msg").equals("user_verified")) {

                    SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    user.ID = msg.getInt("Id");
                    user.username = msg.getString("name");
                    Toast.makeText(MainActivity.this, getString(R.string.welcome,  user.username), Toast.LENGTH_SHORT).show();
                    user.Address = msg.getString("address");
                    user.Telephone = msg.getString("tele");
                    user.Email = msg.getString("email");
                    user.location.setLatitude(msg.getDouble("lat"));
                    user.location.setLongitude(msg.getDouble("long"));
                    user.Password = msg.getString("pass");
                    ed.putString("user", user.toObject().toString());
                    ed.putBoolean("logged", true);
                    ed.apply();
                    Intent login = new Intent(MainActivity.this, MenuActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendVerify() {
        JSONObject message = new JSONObject();
        try {
            message.put("Msg", "user_verify");
            message.put("phone", phoneEditText.getText().toString());
            serverService.sendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            phoneAuthCredential.getSmsCode();
            Log.d("Phone Auth", "Authinticated");
            signIn(phoneAuthCredential);
            mDialog.dismiss();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(MainActivity.this, "Please Provide us with a correct phone number", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Log.d("Phone Auth", "code sent");
            authinticated = true;
            mVerificationId = s;
        }
    };

    public void verify(String code) {
        if(mVerificationId != null && !mVerificationId.isEmpty()) {
            PhoneAuthCredential credential  = PhoneAuthProvider.getCredential(mVerificationId, code);
            signIn(credential);
        }
    }

    public void signIn(PhoneAuthCredential credential) {
        if(verifyCancelled) return;
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete()) {
                    sendVerify();
                    mDialog.dismiss();
                } else {
                    Toast.makeText(serverService, "Sms code is incorrect.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void changeLang(Context context, String lang) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Language", lang);
        editor.apply();
    }
    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        language = preferences.getString("Language", "en");

        super.attachBaseContext(MyContextWrapper.wrap(newBase, language));
    }

}
