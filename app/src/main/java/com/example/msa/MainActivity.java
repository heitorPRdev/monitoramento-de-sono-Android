package com.example.msa;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase bancoDados;
    public ListView lvDados;
    TextView tvHorasTotais;
    LinearLayout layoutHome;
    LinearLayout layoutInputDados;
    TextView tvRes;
    EditText etTime;
    Button btnEnviardados;
    Button btnHomeCad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvHorasTotais = (TextView) findViewById(R.id.tvHorasTotais);
        lvDados = (ListView) findViewById(R.id.lvDados);
        etTime = (EditText) findViewById(R.id.etTime);
        btnEnviardados = (Button) findViewById(R.id.btnEnviardados);
        tvRes = (TextView) findViewById(R.id.tvRes);
        btnHomeCad = (Button) findViewById(R.id.btnHomeCad);
        layoutInputDados = (LinearLayout) findViewById(R.id.layoutInputDados);
        layoutHome = (LinearLayout) findViewById(R.id.layoutHome);
        layoutHome.setVisibility(View.VISIBLE);
        criarBancoDados();
        listDados();
        btnHomeCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnHomeCad.getText().toString().equals("Cadastro")){
                    layoutInputDados.setVisibility(View.VISIBLE);
                    layoutHome.setVisibility(View.INVISIBLE);
                    cadastro();
                }else{
                    layoutInputDados.setVisibility(View.INVISIBLE);
                    layoutHome.setVisibility(View.VISIBLE);
                    listDados();
                }
            }
        });



    }
    public void criarBancoDados(){
        try{
            bancoDados = openOrCreateDatabase("msa", MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS horaDormida("+
                    "id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", hDormida VARCHAR)");

            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void cadastro(){
        btnHomeCad.setText("Home");
        tvRes.setText("");
        etTime.setText("");
        btnEnviardados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try {
                    String horaDormida = etTime.getText().toString();
                    bancoDados = openOrCreateDatabase("msa", MODE_PRIVATE, null);
                    String sql = "INSERT INTO horaDormida(hDormida) VALUES (?)";
                    SQLiteStatement stmt = bancoDados.compileStatement(sql);
                    stmt.bindString(1, horaDormida);
                    stmt.executeInsert();
                    tvRes.setText("Catalogado com Sucesso!");
                    bancoDados.close();
                } catch (Exception e) {
                    tvRes.setText("Houve um erro desculpe");
                }

            }
        });
    }
    public void listDados(){
        btnHomeCad.setText("Cadastro");

        float num1,total = 0;
        try{
            bancoDados = openOrCreateDatabase("msa", MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT hDormida FROM horaDormida ORDER BY id DESC LIMIT 7", null);
            ArrayList<String> linhas = new ArrayList<String>();
            ArrayAdapter meuAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,android.R.id.text1,linhas);
            lvDados.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            while(meuCursor!=null){
                linhas.add(meuCursor.getString(0));
                num1 = Float.parseFloat(meuCursor.getString(0).replaceAll(":","."));
                total = num1 + total;
                meuCursor.moveToNext();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        tvHorasTotais.setText(String.format("%.2f horas dormidas pela semana",total/7).replaceFirst(",",":"));
    }
}