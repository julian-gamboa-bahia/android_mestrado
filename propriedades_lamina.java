package br.com.ven2020.envelopes2018;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.ven2020.envelopes2018.database.DatabaseHelper;
import br.com.ven2020.envelopes2018.database.historico;

public class propriedades_lamina extends AppCompatActivity {

    DatabaseHelper dbHelper= null;

    ListView simpleListView_propriedades;

    String nome_lamina="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propriedades_lamina);

        //pegamos a ultima lamina

        historico db_historico=new historico(this);
        nome_lamina=db_historico.ultima_lamina_usada();

//Colocamos o nome da lamina
        TextView textView_nome_lamina=(TextView) this.findViewById(R.id.nome_lamina);
        textView_nome_lamina.setText(nome_lamina);

//Educativo: explicitamos que deve se tocar em cada PROPRIEDADE para
        TextView instrucoes_nome_lamina=(TextView) this.findViewById(R.id.instrucoes_nome_lamina);
        instrucoes_nome_lamina.setText("Toque em cada propriedade para entender o seu significado");

        /*
        * Junho 14
        *
        * */
        listar_laminas();

    }


    //Outubro03 1) Comentário explicativo que aparece quando for tocado algumas das propriedades da lâmina.

    public void listar_laminas()
    {
        final String[] unidade_propriedade_lamina ={
                "",//"Identificador da lâmina dentro do Banco de dados",
                "",//"Nome da lâmina",
                " (Kgr/m3)", //Desidade",
                " (Pa)",// "Módulo de elasticidade na direção local 1 (paralela com a fibra de reforço)",
                " (Pa)",//"Módulo de elasticidade na direção local 2",
                " (Pa)",//"Módulo de elasticidade na direção local 3",
                " (Pa)",//"Módulo de cisalhamento no plano local 1-2", //  "G12:
                " (Pa)",//"Módulo de cisalhamento no plano local 1-3",//  "G13:
                " (Pa)",//"Módulo de cisalhamento no plano local 2-3",//  "G23:

                "",//Coeficiente de Poisson no plano local 1-2",//  "NU12:
                "",//"Coeficiente de Poisson no plano local 1-3",//  "NU13:
                "",//"Coeficiente de Poisson no plano local 2-3",//  "NU23:

                " (Pa)",//"Esforço máximo de tensão da direção local 1",  //  "SIGMA_T_1
                " (Pa)",//"Esforço máximo de tensão da direção local 2",  //  "SIGMA_T_2

                " (Pa)",//"Esforço máximo de compressão da direção local 1",//  "SIGMA_C_1:
                " (Pa)",//"Esforço máximo de compressão da direção local 2",//  "SIGMA_C_2:

                " (Pa)",//"Esforço máximo de cisalhamento no plano local 1-2",//  "TAU12: "
                " (Pa)",//"Esforço máximo de cisalhamento no plano local 1-3",//  "TAU13: "
                " (Pa)",//"Esforço máximo de cisalhamento no plano local 2-3",//  "TAU23: "

                " (m/m)",//"Deformação máxima, do tipo tração, na direção local 1",//  "EPSILON_T_1:
                " (m/m)",//"Deformação máxima, do tipo tração, na direção local 2",//  "EPSILON_T_2:
                " (m/m)",//"Deformação máxima, do tipo compressão, na direção local 1",//  "EPSILON_C_1:
                " (m/m)",//"Deformação máxima, do tipo compressão, na direção local 2",//  "EPSILON_C_2:
                " (m/m)",//"Deformação máxima, do tipo cisalhamento, no plano local 1-2"//  "//GAMMA12`:
                ""
        };

        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
        }

        final List<String> list = dbHelper.obter_propriedades_laminas(nome_lamina);

        //Log.d("Outubro05","obter_nomes_laminas "+list.size());

        int numero_laminas_registradas=list.size();
        final String[] propriedade_lamina =new String[numero_laminas_registradas+1];
        final int[] animalImages=new int[numero_laminas_registradas+1];

        for (int i =0; i< list.size(); i++) {
            propriedade_lamina[i]=list.get(i);
            animalImages[i]=R.drawable.logo_mechg;
        }


        propriedade_lamina[numero_laminas_registradas]="";
        animalImages[numero_laminas_registradas]=R.drawable.tela_incial_icone_blanco;

        simpleListView_propriedades=(ListView) this.findViewById(R.id.simpleListView_propriedades_laminas);

        ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();

        String unidades="";

        for (int i = 0; i< propriedade_lamina.length; i++)
        {
            HashMap<String,String> hashMap=new HashMap<>();//create a hashmap to store the data in key value pair

            unidades=unidade_propriedade_lamina[i];

            hashMap.put("name",propriedade_lamina[i]+unidades);
            hashMap.put("image",animalImages[i]+"");
            arrayList.add(hashMap);//add the hashmap into arrayList
        }
        String[] from={"name","image"};//string array
        int[] to={R.id.textView,R.id.imageView};//int array of views id's

        SimpleAdapter simpleAdapter=new SimpleAdapter(this,arrayList,R.layout.list_view_items,from,to);//Create object and set the parameters for simpleAdapter

        simpleListView_propriedades.setAdapter(simpleAdapter);

        final String[] conceito_propriedade_lamina ={
                "Identificador da lâmina dentro do Banco de dados",
                "Nome da lâmina",
"Desidade",
"Módulo de elasticidade na direção local 1 (paralela com a fibra de reforço)",
"Módulo de elasticidade na direção local 2",
"Módulo de elasticidade na direção local 3",
"Módulo de cisalhamento no plano local 1-2", //  "G12:
"Módulo de cisalhamento no plano local 1-3",//  "G13:
"Módulo de cisalhamento no plano local 2-3",//  "G23:

"Coeficiente de Poisson no plano local 1-2",//  "NU12:
"Coeficiente de Poisson no plano local 1-3",//  "NU13:
"Coeficiente de Poisson no plano local 2-3",//  "NU23:

"Esforço máximo de tensão da direção local 1",  //  "SIGMA_T_1
"Esforço máximo de tensão da direção local 2",  //  "SIGMA_T_2

"Esforço máximo de compressão da direção local 1",//  "SIGMA_C_1:
"Esforço máximo de compressão da direção local 2",//  "SIGMA_C_2:

"Esforço máximo de cisalhamento no plano local 1-2",//  "TAU12: "
"Esforço máximo de cisalhamento no plano local 1-3",//  "TAU13: "
"Esforço máximo de cisalhamento no plano local 2-3",//  "TAU23: "

"Deformação máxima, do tipo tração, na direção local 1",//  "EPSILON_T_1:
"Deformação máxima, do tipo tração, na direção local 2",//  "EPSILON_T_2:
"Deformação máxima, do tipo compressão, na direção local 1",//  "EPSILON_C_1:
"Deformação máxima, do tipo compressão, na direção local 2",//  "EPSILON_C_2:
"Deformação máxima, do tipo cisalhamento, no plano local 1-2"//  "//GAMMA12`:
        };

        simpleListView_propriedades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Log.d("Outubro05","Ajeitando o erro indicado pela GooglePLAY"+i);

                if(i<list.size())
                {
                    String explicacao = "";
                    explicacao = "A propriedade denominada como" +
                            "\n  " +
                            "\"" +
                            propriedade_lamina[i].substring(0, propriedade_lamina[i].indexOf(":")) +
                            "\"" +
                            "\n  " +
                            "Pode-se entender como:" +
                            "\n"
                            + conceito_propriedade_lamina[i];

                    Toast.makeText(getBaseContext(), explicacao, Toast.LENGTH_LONG).show();

                    TextView instrucoes_nome_lamina = (TextView) findViewById(R.id.instrucoes_nome_lamina);
                    instrucoes_nome_lamina.setVisibility(View.GONE);
                }
            }
        });
    }
}
