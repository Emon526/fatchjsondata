package com.android.jsondatafatch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;

import com.android.jsondatafatch.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
ArrayList<String>userlist;
ArrayAdapter<String>listAdapter;
Handler mainHandler = new Handler();
ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intializeUserlist();
        binding.fatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new fetchData().start();
            }
        });

    }

    private void intializeUserlist() {
        userlist = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,userlist);
        binding.fatchlist.setAdapter(listAdapter);
    }

    class  fetchData extends  Thread{
        String data = "";
        @Override

        public void run() {
            mainHandler.post(new Runnable() {
                @Override
                public void run()  {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Fatching");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });
            URL url;

            {
                try {
                    url = new URL("https://api.npoint.io/732ec5fcea9cb6865a54");
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                    InputStream inputStream = httpsURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = bufferedReader.readLine())!=null)
                    {
                        data = data +line ;

                    }
                    if (!data.isEmpty())
                    {
                        JSONObject jsonObject = new JSONObject(data);
                        JSONArray users = jsonObject.getJSONArray("Users");
                        userlist.clear();

                        for (int i=0; i<users.length();i++)
                        {
                            JSONObject names = users.getJSONObject(i);
                            String name = names.getString("name");

                            userlist.add(name);

                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(progressDialog != null ){

                            progressDialog.cancel();
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

        }


    }
}

