package br.com.ven2020.envelopes2018;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

import br.com.ven2020.envelopes2018.criterios.entrada_dados;
import br.com.ven2020.envelopes2018.database.DatabaseHelper;
import br.com.ven2020.envelopes2018.database.historico;
import br.com.ven2020.envelopes2018.envelopes.Envelopes_entrada_dados;

import static br.com.ven2020.envelopes2018.R.id.simpleListView_lista_criterios;


public class envelopes_falha extends AppCompatActivity {

    DatabaseHelper dbHelper= null;

    ListView simpleListView_criterios;

    String nome_lamina="";

    //Outubro12
    String origen="";
    String indice="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envelopes_falha);

        //pegamos a ultima lamina

        historico db_historico=new historico(this);
        nome_lamina=db_historico.ultima_lamina_usada();

        TextView textView_nome_lamina=(TextView) this.findViewById(R.id.nome_lamina);

        textView_nome_lamina.setText(nome_lamina);

        //Outubro26
        // Coletamos o estamo de esforços que será representado como mais um ponto

        origen=getIntent().getStringExtra("origen");
        indice=getIntent().getStringExtra("indice");

        /*
        * Junho 14
        *
        * Coletamos os dados de cada lâmina,
        * coletamos o
        *
        * */
    //    coletar_dados();
        listar_criterios();

    }

    /*
    *
    * */
    public void listar_criterios()
    {

        simpleListView_criterios=(ListView)findViewById(simpleListView_lista_criterios);

        final String[] lista_nome_criterios={
                "Máxima Tensão", //1
                "Máxima Deformação", //2
                "Tsai-Hill", //3
                "Azzi-Tsai", //4
                "Tsai-Wu", //5
                "Hoffman",//6
                "Hashin", //7
                "Christensen",//8
                "Puck",//9
                "Larc03",//10
                ""
        };



        ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();

        String vedado="";

        for (int i=0;i<lista_nome_criterios.length;i++)
        {
            HashMap<String,String> hashMap=new HashMap<>();//create a hashmap to store the data in key value pair






//Nov16
            if(
                    nome_lamina.contains("1025 Steel") ||
                    nome_lamina.contains("6061-T6 Alum") ||
                    nome_lamina.contains("AS4_3501-6") ||
                    nome_lamina.contains("AS4_3502") ||
                    nome_lamina.contains("AS4_8552")
                    )
            {

//Aqueles critérios que considerão a fratura na fibra

                if(lista_nome_criterios[i].contains("Hashin"))
                {
                    vedado="  (Não recomendao)";
                }

                if(lista_nome_criterios[i].contains("Christensen"))
                {
                    vedado="  (Não recomendao)";
                }
                if(lista_nome_criterios[i].contains("Puck"))
                {
                    vedado="  (Não recomendao)";
                }

                if(lista_nome_criterios[i].contains("Larc03"))
                {
                    vedado="  (Não recomendao)";
                }

                if(lista_nome_criterios[i].length()==0)
                {
                    vedado="";
                }
            }
            else
            {
                vedado="";
            }
            hashMap.put("name",lista_nome_criterios[i]+vedado);


            arrayList.add(hashMap);//add the hashmap into arrayList
        }
        //String[] from={"name","image"};//string array
        String[] from={"name"};//string array
        //int[] to={R.id.textView,R.id.imageView};//int array of views id's
        int[] to={R.id.textView};//int array of views id's
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,arrayList,R.layout.list_view_items,from,to);//Create object and set the parameters for simpleAdapter


        simpleListView_criterios.setAdapter(simpleAdapter);//sets the adapter for listView

        final historico db_historico=new historico(this);

        simpleListView_criterios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(lista_nome_criterios[i].length()!=0)
                {
                    //Log.d("Outubro04",lista_nome_criterios[i]);
                    //Registramos no Banco de dados a ENVELOPE
                    String agora=new Time(System.currentTimeMillis()).toString();

                    db_historico.insert_envelope_usado(lista_nome_criterios[i],agora);

                    Intent in = new Intent(getBaseContext(), Envelopes_entrada_dados.class);
                    //Outubro11
                    in.putExtra("origen",origen);
                    in.putExtra("indice",indice);
                    startActivityForResult(in, 1);
                }
            }
        });
    }

/*
*Outubro04
*
*
* */

/////////////////
}


/*
        //Registramos no Banco de dados a lamina usada:
        historico db_historico=new historico(this);
        String agora=new Time(System.currentTimeMillis()).toString();

//É registrada a lâmina usada no DB com o intuito de sugerir as últimas usadas

        db_historico.insert_lamina_usada(nome_lamina,agora);

        db_historico.insert_envelope_usado(nome_lamina,agora);
* */