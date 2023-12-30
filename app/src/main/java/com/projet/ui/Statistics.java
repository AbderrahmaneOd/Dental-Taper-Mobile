package com.projet.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.projet.R;
import com.projet.entities.PW;
import com.projet.entities.StudentPW;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Statistics extends AppCompatActivity {

    private String studentId;
    private final String STUDENTS_URL = "http://192.168.1.104:8080/api/student-pws/student/";
    private static final String TAG = "StudentPW Request";
    private List<StudentPW> studentPWList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");
        Log.d("StudentID", "Student ID : " + studentId);

        //createLineChart();
        getStudentPW();
    }

    void createLineChart() {
        // Définition du LineChart dans votre activité
        LineChart lineChart = findViewById(R.id.idLineChart);

        // Création d'une liste de Entry pour stocker les points sur la courbe
        ArrayList<Entry> entries = new ArrayList<>();

        entries.add(new Entry(0, 9, "TP 1"));  // Premier point (x=0, y=9)
        entries.add(new Entry(1, 17, "TP 2")); // Deuxième point (x=1, y=17)
        entries.add(new Entry(2, 15, "TP 3")); // Troisième point (x=2, y=15)
        entries.add(new Entry(3, 19, "TP 4")); // Troisième point (x=2, y=15)
        entries.add(new Entry(4, 14, "TP 5")); // Troisième point (x=2, y=15)
        entries.add(new Entry(5, 20, "TP 6")); // Troisième point (x=2, y=15)
        entries.add(new Entry(6, 7, "TP 7")); // Troisième point (x=2, y=15)
        entries.add(new Entry(7, 10, "TP 8")); // Troisième point (x=2, y=15)
        entries.add(new Entry(8, 14, "TP 9")); // Troisième point (x=2, y=15)

        // Création d'un LineDataSet pour représenter la série de données
        LineDataSet dataSet = new LineDataSet(entries, "Évolution des notes");
        dataSet.setColor(Color.BLUE); // Couleur de la ligne

        // Création d'un LineData et configuration du LineChart
        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);


        // Désactiver l'axe vertical à droite
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Récupérer l'axe Y à gauche
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Valeur minimale de l'axe Y
        leftAxis.setAxisMaximum(20f); // Valeur maximale de l'axe Y

        // Configuration du format des labels pour l'axe X (l'axe horizontal)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Position de l'axe X en bas du graphique
        //xAxis.setGranularity(1f); // Espacement entre chaque étiquette (1f pour un espacement d'une unité)
        //xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Récupération du label si le point a un label, sinon retourne une chaîne vide
                List<Entry> data = dataSet.getValues();
                if (data.size() > (int) value && data.get((int) value).getData() != null) {
                    return data.get((int) value).getData().toString();
                } else {
                    return "";
                }
            }
        });

        // Actualisation du graphique
        lineChart.invalidate();

    }

    void createLineChart(List<StudentPW> studentPWList) {

        // Définition du LineChart dans votre activité
        LineChart lineChart = findViewById(R.id.idLineChart);

        // Création d'une liste de Entry pour stocker les points sur la courbe
        ArrayList<Entry> entries = new ArrayList<>();

        // studentPWList contient les données des notes des travaux pratiques pour un étudiant
        for (int i = 0; i < studentPWList.size(); i++) {
            StudentPW studentPW = studentPWList.get(i);
            float note = studentPW.getNote().floatValue(); // Convertir Double en float
            // Ajout des points sur la courbe
            entries.add(new Entry(i, note, studentPW.getPw().getTitle()));
        }

        // Création d'un LineDataSet pour représenter la série de données
        LineDataSet dataSet = new LineDataSet(entries, "Évolution des notes");
        dataSet.setColor(Color.BLUE); // Couleur de la ligne

        // Création d'un LineData et configuration du LineChart
        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);


        // Désactiver l'axe vertical à droite
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Récupérer l'axe Y à gauche
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Valeur minimale de l'axe Y
        leftAxis.setAxisMaximum(20f); // Valeur maximale de l'axe Y

        // Configuration du format des labels pour l'axe X (l'axe horizontal)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Position de l'axe X en bas du graphique
        //xAxis.setGranularity(1f); // Espacement entre chaque étiquette (1f pour un espacement d'une unité)
        //xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Récupération du label si le point a un label, sinon retourne une chaîne vide
                List<Entry> data = dataSet.getValues();
                if (data.size() > (int) value && data.get((int) value).getData() != null) {
                    return data.get((int) value).getData().toString();
                } else {
                    return "";
                }
            }
        });

        // Actualisation du graphique
        lineChart.invalidate();

    }

    public void getStudentPW(){
        // Créer une file d'attente pour les requêtes Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Créer une requête GET pour récupérer la liste des étudiants
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, STUDENTS_URL+studentId, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Traitement de la réponse JSON
                        studentPWList = parseStudentPWList(response);

                        //if(!studentPWList.isEmpty() && studentPWList != null){
                           // Log.d("Student PW", "Note : " + studentPWList.get(0).getNote()
                             //       + " PW title : " + studentPWList.get(0).getPw().getTitle());

                            createLineChart(studentPWList);
                        //}
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

    private List<StudentPW> parseStudentPWList(JSONArray jsonArray) {
        List<StudentPW> studentPWList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject studentPWJson = jsonArray.getJSONObject(i);
                StudentPW studentPW = new StudentPW();
                studentPW.setId(studentPWJson.getInt("id"));
                studentPW.setNote(studentPWJson.getDouble("note"));

                // Récupérer l'objet PW
                JSONObject pwJson = studentPWJson.getJSONObject("pw");
                PW pw = new PW();
                pw.setId(pwJson.getInt("id"));
                pw.setTitle(pwJson.getString("title"));
                studentPW.setPw(pw);

                studentPWList.add(studentPW);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return studentPWList;
    }
}