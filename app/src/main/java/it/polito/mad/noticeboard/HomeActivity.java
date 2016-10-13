package it.polito.mad.noticeboard;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.Serializable;

import it.polito.mad.noticeboard.db.Advertiser;
import it.polito.mad.noticeboard.db.DBCallbacks;
import it.polito.mad.noticeboard.db.NoticeBoardDB;
import it.polito.mad.noticeboard.db.Student;
import it.polito.mad.noticeboard.advertiser.AdvertiserMainActivity;
import it.polito.mad.noticeboard.db_parcel.PAdvertiserData;
import it.polito.mad.noticeboard.db_parcel.PStudentData;
import it.polito.mad.noticeboard.student.search.BrowseActivity;


public class HomeActivity extends AppCompatActivity
        implements DBCallbacks.UserLoginCallback,
        DBCallbacks.AdvertiserSignUpCallback,
        DBCallbacks.StudentSignUpCallback {

    private enum UserChoice implements Serializable {
        student, homeowner, none
    }

    private ProgressBar waitPB;
    private Button signupButton, loginButton;
    private EditText username, passwd;
    private UserChoice curChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseObject po = NoticeBoardDB.tryLocalLogin();
        if (po != null){
            Class profileActivityClass = (po instanceof Student) ?
                    BrowseActivity.class : AdvertiserMainActivity.class;
            startActivity(new Intent(this, profileActivityClass));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);
        signupButton = (Button) findViewById(R.id.signup);
        loginButton = (Button) findViewById(R.id.login);
        username = (EditText) findViewById(R.id.username_edittext);
        passwd = (EditText) findViewById(R.id.password_edittext);
        waitPB = (ProgressBar) findViewById(R.id.wait);

        findViewById(R.id.student_choice_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUserChoice(UserChoice.student);
            }
        });
        findViewById(R.id.advertiser_choice_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUserChoice(UserChoice.homeowner);
            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.checkNetworkUp(HomeActivity.this)){
                    return;
                }
                String name = username.getText().toString();
                String pass = passwd.getText().toString();
                NoticeBoardDB.remoteLogIn(name, pass, HomeActivity.this);
                setUserInteraction(false);
            }
        });
        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.layout_dialog_signup);
                dialog.setTitle(curChoice == UserChoice.student ? R.string.signup_student :
                    R.string.signup_advertiser);
                dialog.findViewById(R.id.signup_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Util.checkNetworkUp(HomeActivity.this)){
                            return;
                        }
                        EditText firstNameEditText, lastNameEditText, eMailEditText;
                        firstNameEditText = (EditText) dialog.findViewById(R.id.first_name);
                        lastNameEditText = (EditText) dialog.findViewById(R.id.last_name);
                        eMailEditText = (EditText) dialog.findViewById(R.id.location);

                        switch (curChoice){
                            case homeowner:
                                PAdvertiserData aHolder = new PAdvertiserData();
                                aHolder.firstName(firstNameEditText.getText().toString())
                                        .lastName(lastNameEditText.getText().toString())
                                        .eMail(eMailEditText.getText().toString());
                                NoticeBoardDB.signUpAdvertiser(
                                        username.getText().toString(),
                                        passwd.getText().toString(),
                                        aHolder,
                                        HomeActivity.this);
                                break;
                            case student:
                                PStudentData sHolder = new PStudentData();
                                sHolder.firstName(firstNameEditText.getText().toString())
                                        .lastName(lastNameEditText.getText().toString())
                                        .eMail(eMailEditText.getText().toString());
                                NoticeBoardDB.signUpStudent(
                                        username.getText().toString(),
                                        passwd.getText().toString(),
                                        sHolder,
                                        HomeActivity.this);
                                break;
                        }
                        dialog.dismiss();
                        setUserInteraction(false);
                    }
                });
                dialog.show();
            }
        });

        curChoice = (savedInstanceState == null) ? UserChoice.none :
                (UserChoice) savedInstanceState.getSerializable("choice");
        switchUserChoice(curChoice);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null){
            outState.putSerializable("choice", curChoice);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            switchUserChoice((UserChoice) savedInstanceState.getSerializable("choice"));
        }
    }

    @Override
    public void onAdvertiserLoginSuccess(Advertiser homeOwner) {
        setUserInteraction(true);
        Toast.makeText(HomeActivity.this, homeOwner.toString(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, AdvertiserMainActivity.class));
        finish();
    }

    @Override
    public void onStudentLoginSuccess(Student student) {
        setUserInteraction(true);
        Toast.makeText(HomeActivity.this, student.toString(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, BrowseActivity.class));
        finish();
    }

    @Override
    public void onLoginError(ParseException pe) {
        setUserInteraction(true);
        Toast.makeText(HomeActivity.this, pe.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private void switchUserChoice(UserChoice choice){
        curChoice = choice;
        Resources res = getResources();
        switch (choice){
            case student:
                if (!loginButton.isEnabled()) loginButton.setEnabled(true);
                if (!signupButton.isEnabled()) signupButton.setEnabled(true);
                if (!username.isEnabled()) username.setEnabled(true);
                if (!passwd.isEnabled()) passwd.setEnabled(true);
                signupButton.setBackgroundColor(res.getColor(R.color.student_color));
                break;
            case homeowner:
                if (!loginButton.isEnabled()) loginButton.setEnabled(true);
                if (!signupButton.isEnabled()) signupButton.setEnabled(true);
                if (!username.isEnabled()) username.setEnabled(true);
                if (!passwd.isEnabled()) passwd.setEnabled(true);
                signupButton.setBackgroundColor(res.getColor(R.color.advertiser_color));
                break;
            default:
                loginButton.setEnabled(false);
                signupButton.setEnabled(false);
                username.setEnabled(false);
                passwd.setEnabled(false);
                signupButton.setBackgroundColor(res.getColor(android.R.color.transparent));
                break;
        }
    }

    private void setUserInteraction(boolean isSet){
        waitPB.setVisibility(isSet ? View.INVISIBLE : View.VISIBLE);
        username.setEnabled(isSet);
        passwd.setEnabled(isSet);
        loginButton.setEnabled(isSet);
        signupButton.setEnabled(isSet);
    }

    @Override
    public void onAdvertiserSignUpSuccess(Advertiser homeOwner) {
        setUserInteraction(true);
        Toast.makeText(this, "Successfully signed up", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdvertiserSignUpError(ParseException exception) {
        setUserInteraction(true);
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStudentSignUpSuccess(Student student) {
        setUserInteraction(true);
        Toast.makeText(this, "Successfully signed up", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStudentSignUpException(ParseException exception) {
        setUserInteraction(true);
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
