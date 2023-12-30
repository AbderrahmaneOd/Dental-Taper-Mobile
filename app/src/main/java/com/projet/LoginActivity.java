package com.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.projet.entities.Student;
import com.projet.entities.User;
import com.projet.ui.PWList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "GetStudents";
    //private static final String STUDENTS_URL = "http://192.168.43.91:8080/api/students";
    private static final String SUBMIT_URL = "http://192.168.1.104:8080/api/authentication";
    private static final String STUDENTS_URL = "http://192.168.1.104:8080/api/students";
    //private static final  String TOEKN = "JSESSIONID=rB6PI83hHhY_cV6T4xxXJM15EadzeBtrzpHwyoWA; XSRF-TOKEN=230a3667-fe7a-4d32-b335-8db5e82b0119";

    private EditText usernameEdt, passwordEdt;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_login);

        usernameEdt = findViewById(R.id.idUsernameEdt);
        passwordEdt = findViewById(R.id.idPasswordEdt);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //startActivity(intent);
                //getStudents();
                authenticate();
            }
        });

    }

    public void authenticate(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SUBMIT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Traitement de la réponse en cas de succès
                        Log.d(TAG, "Login successful " + response.toString());
                        getStudents();
                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Traitement des erreurs
                Log.e("VolleyError", error.toString());
                Toast.makeText(getApplicationContext(), "Username ou mot de passe est incorrect", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Paramètres form-data
                Map<String, String> params = new HashMap<>();
                params.put("username", usernameEdt.getText().toString());
                params.put("password", passwordEdt.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                // Définir les en-têtes pour form-data
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        // Ajouter la requête à la file d'attente
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getStudents() {
        // Créer une file d'attente pour les requêtes Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Créer une requête GET pour récupérer la liste des étudiants
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, STUDENTS_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Traitement de la réponse JSON
                        List<Student> studentList = parseStudentList(response);
                        for (Student student : studentList) {
                            //Log.d(TAG, "Student ID: " + student.getId() + ", Login: " + student.getUser().getLogin());

                            if(usernameEdt.getText().toString().equals(student.getUser().getLogin())){
                                Intent intent = new Intent(LoginActivity.this, PWList.class);
                                intent.putExtra("studentId", student.getId()+"");
                                Log.d(TAG, "Login successful " + student.getId());
                                startActivity(intent);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Gestion des erreurs
                        Log.e(TAG, "Error fetching students: " + error.toString());
                    }
                }) {
        };

        // Ajouter la requête à la file d'attente
        requestQueue.add(jsonArrayRequest);
    }

    private List<Student> parseStudentList(JSONArray jsonArray) {
        List<Student> studentList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject studentJson = jsonArray.getJSONObject(i);
                Student student = new Student();
                student.setId(studentJson.getInt("id"));
                //student.setNumber(studentJson.getString("number"));
                //student.setBirthDay(studentJson.getString("birthDay"));

                // Récupérer l'objet User
                JSONObject userJson = studentJson.getJSONObject("user");
                User user = new User();
                user.setLogin(userJson.getString("login"));


                student.setUser(user);

                // Autres attributs si nécessaire (comme "groupe")

                studentList.add(student);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return studentList;
    }
}