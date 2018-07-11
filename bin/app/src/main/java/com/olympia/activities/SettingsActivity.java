package com.olympia.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.olympia.AdapterListString;
import com.olympia.Globals;
import com.olympia.MainActivity;
import com.olympia.R;
import com.olympia.RecyclerItemClickListener;
import com.olympia.Vocabulary;
import com.olympia.cloud9_api.ApiUtils;
import com.olympia.cloud9_api.C9User;
import com.olympia.cloud9_api.ICloud9;
import com.olympia.room_db.AppDatabase;
import com.olympia.room_db.RoomDbFacade;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.loadTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ArrayList<String> supportedUILanguages = new ArrayList<>();
        supportedUILanguages.add(getResources().getString(R.string.lang_en));
        supportedUILanguages.add(getResources().getString(R.string.lang_ru));
        supportedUILanguages.add(getResources().getString(R.string.lang_hi));
        ArrayList<String> supportedUICodes = new ArrayList<>();
        supportedUICodes.add("en");
        supportedUICodes.add("ru");
        supportedUICodes.add("hi");

        ArrayList<String> supportedDictLanguages = new ArrayList<>();
        supportedDictLanguages.add(getResources().getString(R.string.dict_lang_en));
        supportedDictLanguages.add(getResources().getString(R.string.dict_lang_es));
        supportedDictLanguages.add(getResources().getString(R.string.dict_lang_hi));
        ArrayList<String> supportedDictCodes = new ArrayList<>();
        supportedDictCodes.add("en");
        supportedDictCodes.add("es");
        supportedDictCodes.add("hi");

        ArrayList<String> supportedThemes = new ArrayList<>();
        supportedThemes.add(getResources().getString(R.string.theme_1));
        supportedThemes.add(getResources().getString(R.string.theme_2));
        supportedThemes.add(getResources().getString(R.string.theme_3));
        supportedThemes.add(getResources().getString(R.string.theme_4));

        Button b1 = findViewById(R.id.save_to_db),
            b2 = findViewById(R.id.restore_from_db),
            b3 = findViewById(R.id.clear_from_db),
            b4 = findViewById(R.id.account_change_name),
            b5 = findViewById(R.id.account_change_email),
            b6 = findViewById(R.id.account_change_password),
            b7 = findViewById(R.id.account_logout),
            b8 = findViewById(R.id.account_delete),
            b9 = findViewById(R.id.show_stats),
            b10 = findViewById(R.id.change_ui_language),
            b11 = findViewById(R.id.change_dict_language),
            b12 = findViewById(R.id.change_theme),
            b13 = findViewById(R.id.font_size);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        RoomDbFacade.saveAllKeywords(AppDatabase.getAppDatabase(getApplicationContext()));
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.database_saved), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        RoomDbFacade.getAllKeywords(AppDatabase.getAppDatabase(getApplicationContext()));
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.database_restored), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        RoomDbFacade.deleteAll(AppDatabase.getAppDatabase(getApplicationContext()));
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.database_cleared), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangeNameActivity.class);
                startActivityForResult(intent, Globals.CHANGE_NAME_ACTIVITY);
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangeEmailActivity.class);
                startActivityForResult(intent, Globals.CHANGE_EMAIL_ACTIVITY);
            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivityForResult(intent, Globals.CHANGE_PASSWORD_ACTIVITY);
            }
        });

        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Globals.SETTINGS_EXTRA, Globals.LOGOUT_REQUESTED);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(v.getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialog_delete_account, null);
                categoryBuilder.setView(mView);
                AlertDialog dialog = categoryBuilder.create();
                TextView title = mView.findViewById(R.id.title),
                        descr = mView.findViewById(R.id.descr);

                title.setText(getResources().getString(R.string.deleting_account_title));
                descr.setText(getResources().getString(R.string.deleting_account_descr));

                EditText password = mView.findViewById(R.id.password);

                Button positiveBtn = mView.findViewById(R.id.button_positive),
                        negativeBtn = mView.findViewById(R.id.button_negative);
                positiveBtn.setText(getResources().getString(R.string.delete));
                negativeBtn.setText(getResources().getString(R.string.cancel));

                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ICloud9 cloud9service = ApiUtils.getAPIService();
                        cloud9service.deleteAccount(MainActivity.currentUsername,
                                password.getText().toString())
                                .enqueue(new Callback<C9User>() {

                                    @Override
                                    public void onResponse(Call<C9User> call, Response<C9User> response) {
                                        if (response.isSuccessful()) {
                                            Intent returnIntent = new Intent();
                                            returnIntent.putExtra(Globals.SETTINGS_EXTRA, Globals.DELETE_ACCOUNT_REQUESTED);
                                            setResult(Activity.RESULT_OK, returnIntent);
                                            dialog.dismiss();
                                            finish();
                                        } else {
                                            try {
                                                JSONObject error = new JSONObject(response.errorBody().string());
                                                Log.e(Globals.TAG, error.getString("msg"));
                                                Toast.makeText(getApplicationContext(), error.getString("msg"), Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
                                                String s = getResources().getString(R.string.error_server_unreachable);
                                                Log.e(Globals.TAG, s);
                                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<C9User> call, Throwable t) {
                                        String s = String.format(Locale.ENGLISH, getResources().getString(R.string.error_failed_attempt),
                                                getResources().getString(R.string.account_delete));
                                        Log.e(Globals.TAG, s);
                                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                });
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });

        b10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View w = getLayoutInflater().inflate(R.layout.dialog_select_language, null);
                TextView header = w.findViewById(R.id.select_label);
                header.setText(getResources().getString(R.string.select_lang));
                builder.setView(w);

                RecyclerView languages = w.findViewById(R.id.selection_list);
                languages.setLayoutManager(new GridLayoutManager(v.getContext(), 1));
                AlertDialog dialog = builder.create();

                languages.addOnItemTouchListener(
                        new RecyclerItemClickListener(v.getContext(), languages, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override public void onItemClick(View view, int position) {
                                Locale locale = new Locale(supportedUICodes.get(position));
                                Locale.setDefault(locale);
                                Configuration config = getBaseContext().getResources().getConfiguration();
                                config.locale = locale;
                                getBaseContext().getResources().updateConfiguration(config,
                                        getBaseContext().getResources().getDisplayMetrics());
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra(Globals.SETTINGS_EXTRA, Globals.CHANGE_UI_LANGUAGE_REQUESTED);
                                setResult(Activity.RESULT_OK, returnIntent);
                                dialog.dismiss();
                                finish();
                            }
                            @Override public void onLongItemClick(View view, int position) {
                                //* Do nothing
                            }
                        })
                );
                AdapterListString languagesAdapter = new AdapterListString(supportedUILanguages);
                languages.setAdapter(languagesAdapter);

                //* IMPORTANT! DO NOT PLACE BEFORE SETTING ADAPTER!
                languages.measure(0, 0);

                Button negativeBtn = w.findViewById(R.id.button_negative);
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        b11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View w = getLayoutInflater().inflate(R.layout.dialog_select_language, null);
                TextView header = w.findViewById(R.id.select_label);
                header.setText(getResources().getString(R.string.select_lang));
                builder.setView(w);

                RecyclerView languages = w.findViewById(R.id.selection_list);
                languages.setLayoutManager(new GridLayoutManager(v.getContext(), 1));
                AlertDialog dialog = builder.create();

                languages.addOnItemTouchListener(
                        new RecyclerItemClickListener(v.getContext(), languages, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override public void onItemClick(View view, int position) {
                                Vocabulary.currentDictLanguage = supportedDictCodes.get(position);
                                dialog.dismiss();
                            }
                            @Override public void onLongItemClick(View view, int position) {
                                //* Do nothing
                            }
                        })
                );
                AdapterListString languagesAdapter = new AdapterListString(supportedDictLanguages);
                languages.setAdapter(languagesAdapter);

                //* IMPORTANT! DO NOT PLACE BEFORE SETTING ADAPTER!
                languages.measure(0, 0);

                Button negativeBtn = w.findViewById(R.id.button_negative);
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        b12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View w = getLayoutInflater().inflate(R.layout.dialog_select_language, null);
                TextView header = w.findViewById(R.id.select_label);
                header.setText(getResources().getString(R.string.select_theme));
                builder.setView(w);

                RecyclerView themes = w.findViewById(R.id.selection_list);
                themes.setLayoutManager(new GridLayoutManager(v.getContext(), 1));
                AlertDialog dialog = builder.create();

                themes.addOnItemTouchListener(
                        new RecyclerItemClickListener(v.getContext(), themes, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override public void onItemClick(View view, int position) {
                                Globals.currentTheme = position;
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra(Globals.SETTINGS_EXTRA, Globals.CHANGE_THEME_REQUESTED);
                                setResult(Activity.RESULT_OK, returnIntent);
                                dialog.dismiss();
                                finish();
                            }
                            @Override public void onLongItemClick(View view, int position) {
                                //* Do nothing
                            }
                        })
                );
                AdapterListString languagesAdapter = new AdapterListString(supportedThemes);
                themes.setAdapter(languagesAdapter);

                //* IMPORTANT! DO NOT PLACE BEFORE SETTING ADAPTER!
                themes.measure(0, 0);

                Button negativeBtn = w.findViewById(R.id.button_negative);
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        b13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                startActivityForResult(intent, 0);
            }
        });
    }
}
