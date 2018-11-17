package br.com.ven2020.envelopes2018;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.ven2020.envelopes2018.database.historico;

import static br.com.ven2020.envelopes2018.R.id.simpleListView;

public class historico_uso_laminas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_uso_laminas);


        historico db_historico=new historico(this);
        String nome_lamina=db_historico.ultima_lamina_usada();

        //outubro03: obterProcuranado o Histórico é possível

        ArrayList<String> obterProcuranado=db_historico.obterProcuraHistorico();

        TextView temporal_outubro03=(TextView) findViewById(R.id.temporal_outubro03);

        if(obterProcuranado.size()==0)
        {
            temporal_outubro03.setText(
                    "Até o momento não foram usadas nehuma lâmina\n"+
                    "Na tela inicial pode-se tocar sobre qualquer lâmina para obter ás suas propriedades"
            );
        }
        else
        {
            temporal_outubro03.setText("Lâminas usadas");
            listar_historico_uso_laminas();
        }
    }

    /*
    * outubro03
    *
    * */

    public void listar_historico_uso_laminas()
    {
        historico db_historico=new historico(this);
        ArrayList<String> obterProcuranado=db_historico.obterProcuraHistorico();

        int numero_laminas_registradas=obterProcuranado.size();

        final String[] nome_lamina =new String[numero_laminas_registradas+1];
        final int[] animalImages=new int[numero_laminas_registradas+1];

        for (int i =0; i< obterProcuranado.size(); i++) {
            nome_lamina[i]=obterProcuranado.get(i);
            animalImages[i]=R.drawable.logo_mechg;
        }


        nome_lamina[numero_laminas_registradas]="";
        animalImages[numero_laminas_registradas]=R.drawable.tela_incial_icone_blanco;

        ListView simpleListView=(ListView)findViewById(R.id.simpleListView);

        ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
        for (int i = 0; i< nome_lamina.length; i++)
        {
            HashMap<String,String> hashMap=new HashMap<>();//create a hashmap to store the data in key value pair
            hashMap.put("nome_lamina", nome_lamina[i]);
            hashMap.put("image",animalImages[i]+"");
            arrayList.add(hashMap);//add the hashmap into arrayList
        }
        String[] from={"nome_lamina","image"};//string array
        int[] to={R.id.textView,R.id.imageView};//int array of views id's
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,arrayList,R.layout.list_view_items,from,to);//Create object and set the parameters for simpleAdapter


        simpleListView.setAdapter(simpleAdapter);//sets the adapter for listView

        simpleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(nome_lamina[i].length()!=0)
                {
                    janela(view, nome_lamina[i]);
                }
                /////
            }
        });

    }

    /*
    * outubro03
    * Função idêntica daquela colocada no MAIN
    * */

    public void janela(View v,String nome_lamina)
    {
        //Registramos no Banco de dados a lamina usada:
        historico db_historico=new historico(this);
        String agora=new Time(System.currentTimeMillis()).toString();


//É registrada a lâmina usada no DB com o intuito de sugerir as últimas usadas

        db_historico.insert_lamina_usada(nome_lamina,agora);

        String[] str={"Propriedades da lâmina","Envelopes de Falha","Critérios de Falha"};

        AlertDialog.Builder aa=new AlertDialog.Builder(this);
        aa.setTitle("Mestrado Julian");

        aa.setSingleChoiceItems(str, 0, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO Auto-generated method stub
                switch (which)
                {
                    case 0:
                        Toast.makeText(getApplicationContext(), "Propriedades da lâmina",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "Envelopes de Falha", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Critérios de Falha", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });


        aa.setPositiveButton("Aceitar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView listAlert = ((AlertDialog) dialog).getListView();
                        int temporal_tema=0;
                        for (int i = 0; i < listAlert.getCount(); i++)
                        {
                            boolean checked = listAlert.isItemChecked(i);

                            if (checked) {                                temporal_tema = i;                            }
                        }
                        ////
                        switch (temporal_tema)
                        {
                            // Propriedades
                            case 0:
                                Intent in = new Intent(getBaseContext(), propriedades_lamina.class);
                                startActivityForResult(in, 1);
                                break;
                            //Envelopes
                            case 1:
                                in = new Intent(getBaseContext(), envelopes_falha.class);
                                startActivityForResult(in, 1);
                                break;
                            // Criterios
                            case 2:
                                in = new Intent(getBaseContext(), criterios_falha.class);
                                startActivityForResult(in, 1);
                                break;
                            default:
                                break;
                        }
                    }
                });

        aa.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // Final
        aa.show();
    }
////////////////////////////////////////////////////////
}
