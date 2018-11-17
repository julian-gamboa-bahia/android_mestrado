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

import br.com.ven2020.envelopes2018.database.db_historico_estados_esforcos;
import br.com.ven2020.envelopes2018.database.historico;

public class historico_estados_esforcos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_estados_esforcos);


        db_historico_estados_esforcos DB_historico_estados_esforcos=new db_historico_estados_esforcos(this);

        //outubro03: obterProcuranado o Histórico é possível

        ArrayList<String> obterProcuranado=DB_historico_estados_esforcos.obter_historico_estados_esforcos();//pedio inerte de informação

        TextView temporal_outubro10=(TextView) findViewById(R.id.temporal_outubro10);

        if(obterProcuranado.size()==0)
        {
            temporal_outubro10.setText(
                    "Até o momento não foram feitos cálculos\n"
            );
        }
        else
        {
            temporal_outubro10.setText("Esforços usados (Sigma x, Sigma Y, Tau XY)");
            listar_historico_uso_laminas();
        }
    }

    /*
    * outubro03
    * invoca uma janela de alternativas
    * o Que fazer com os ESFORÇOS ja usados.
    * */

    public void listar_historico_uso_laminas()
    {
        db_historico_estados_esforcos DB_historico_estados_esforcos=new db_historico_estados_esforcos(this);
        //historico db_historico=new historico(this);
        ArrayList<String> obterProcuranado=DB_historico_estados_esforcos.obter_historico_estados_esforcos();

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
                    procurar_estados_diretamente(i);
                    janela(view, procurar_lamina_diretamente(i),i);
                }
                /////
            }
        });

    }

    /**
     * Outubro11
     * */
    public ArrayList<String> procurar_estados_diretamente(Integer i)
    {
        db_historico_estados_esforcos DB_historico_estados_esforcos=new db_historico_estados_esforcos(this);

        ArrayList<String> obterEstado=DB_historico_estados_esforcos.obter_estados_esforcos_especifico(i);


        return  obterEstado;
    }


    /**
     * Outubro11
     * */
    public String procurar_lamina_diretamente(Integer i)
    {
        db_historico_estados_esforcos DB_historico_estados_esforcos=new db_historico_estados_esforcos(this);

        String obterLamina=DB_historico_estados_esforcos.procurar_lamina_diretamente(i);

        //Log.d("Outubro11"," valor de i \n"+i+" \n"+obterEstado.toString());

        return  obterLamina;
    }

    /*
    * outubro10
    Mais complexo:

    Deve colocar o nome da lamina como ultimo registro usado, e passar por INDICE o estado de esforços

    * */

    public void janela(View v,String nome_lamina, final Integer indice_estado_esforco)
    {
        //Registramos no Banco de dados a lamina usada:
        historico db_historico=new historico(this);
        String agora=new Time(System.currentTimeMillis()).toString();


//É registrada a lâmina usada no DB com o intuito de sugerir as últimas usadas

        db_historico.insert_lamina_usada(nome_lamina,agora);

        String[] str={"Representaçao no Envelope","Recalcular Critérios de Falha"};

        AlertDialog.Builder aa=new AlertDialog.Builder(this);
        aa.setTitle(nome_lamina);

        aa.setSingleChoiceItems(str, 1, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO Auto-generated method stub
                switch (which)
                {
                    case 0:
                        Toast.makeText(getApplicationContext(), "Representaçao no Envelope de Falha",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "Recalcular Critérios de Falha", Toast.LENGTH_SHORT).show();
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
                            // Colocar no envelope
                            case 0:
                                Intent in = new Intent(getBaseContext(), envelopes_falha.class);
                                in.putExtra("origen","historico");
                                in.putExtra("indice",indice_estado_esforco+""); //Passamos o indice para ser localizado no banco de dados
                                startActivityForResult(in, 1);
                                break;
                            //Recalcular
                            case 1:
                                in = new Intent(getBaseContext(), criterios_falha.class);
                                in.putExtra("origen","historico");
                                in.putExtra("indice",indice_estado_esforco+""); //Passamos o indice para ser localizado no banco de dados
                                startActivity(in);
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
