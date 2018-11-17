package br.com.ven2020.envelopes2018.criterios;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.ven2020.envelopes2018.Config;
import br.com.ven2020.envelopes2018.R;
import br.com.ven2020.envelopes2018.criterios_falha;
import br.com.ven2020.envelopes2018.database.DatabaseHelper;
import br.com.ven2020.envelopes2018.database.historico;
import br.com.ven2020.envelopes2018.database.db_historico_estados_esforcos;
import br.com.ven2020.envelopes2018.envelopes_falha;
import br.com.ven2020.envelopes2018.propriedades_lamina;

public class saida_criterios extends AppCompatActivity {

    String nome_lamina="";
    String criterio_usado="";
    String parametro_experimental_1="";

    String IF_0="";

    //Outubro09, para listar outros IF

    DatabaseHelper dbHelper= null;
    ListView simpleListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saida_criterios);

        nome_lamina=getIntent().getStringExtra("nome_lamina");
        criterio_usado=getIntent().getStringExtra("criterio_usado");

        IF_0=getIntent().getStringExtra("IF_0");
        String IF_1=getIntent().getStringExtra("IF_1");
        String IF_2=getIntent().getStringExtra("IF_2");

        String IF_3=getIntent().getStringExtra("IF_3");
        String IF_4=getIntent().getStringExtra("IF_4");
        String IF_5=getIntent().getStringExtra("IF_5");


        String IF_0_nome=getIntent().getStringExtra("IF_0_nome");
        String IF_1_nome=getIntent().getStringExtra("IF_1_nome");
        String IF_2_nome=getIntent().getStringExtra("IF_2_nome");


        String IF_3_nome=getIntent().getStringExtra("IF_3_nome");
        String IF_4_nome=getIntent().getStringExtra("IF_4_nome");
        String IF_5_nome=getIntent().getStringExtra("IF_5_nome");

        //Log.d("Nov17","\n"+IF_0+"\n"+IF_1+"\n"+IF_2+"\n"+IF_3+"\n"+IF_4+"\n"+IF_5);


        parametro_experimental_1=getIntent().getStringExtra("parametro_experimental_1");

        TextView textView_nome_criterio=(TextView) findViewById(R.id.textView_nome_criterio);

        textView_nome_criterio.setText(
                nome_lamina+"\n"+
                        criterio_usado+"\n"
        );

        if(IF_0!=null) {
            if (IF_0.length() > 0) {
                TextView textView_saida_IF = (TextView) findViewById(R.id.textView_saida_IF_0);
                textView_saida_IF.setText(IF_0_nome + "  " + IF_0);
            }
        }


        if(IF_1!=null)
        {
            if(IF_1.length()>0)
            {
                TextView textView_saida_IF=(TextView) findViewById(R.id.textView_saida_IF_1);
                textView_saida_IF.setText(IF_1_nome+"  "+  IF_1);
            }
        }

        if(IF_2!=null) {
            if (IF_2.length() > 0) {
                TextView textView_saida_IF = (TextView) findViewById(R.id.textView_saida_IF_2);
                textView_saida_IF.setText(IF_2_nome + "  " + IF_2);
            }
        }
//Nov17
        if(IF_3!=null) {
            if (IF_3.length() > 0) {
                TextView textView_saida_IF = (TextView) findViewById(R.id.textView_saida_IF_3);
                textView_saida_IF.setText(IF_3_nome + "  " + IF_3);
            }
        }

        if(IF_4!=null) {
            if (IF_4.length() > 0) {
                TextView textView_saida_IF = (TextView) findViewById(R.id.textView_saida_IF_4);
                textView_saida_IF.setText(IF_4_nome + "  " + IF_4);
            }
        }

        if(IF_5!=null) {
            if (IF_5.length() > 0) {
                TextView textView_saida_IF = (TextView) findViewById(R.id.textView_saida_IF_5);
                textView_saida_IF.setText(IF_5_nome + "  " + IF_5);
            }
        }

        colocar_href_repetir_calculo();
        esconder_botao_criterios_complexos();

    }

    public void esconder_botao_criterios_complexos()
    {
        if(
                criterio_usado.contentEquals("Puck") ||
                criterio_usado.contentEquals("Larc03")
                )
        {
            Button recalcular=(Button) findViewById(R.id.outros_if);
            recalcular.setVisibility(View.GONE);
        }
    }

    /*
    * Futuro: para o servidor local
    *
    * */

    public void colocar_href_repetir_calculo()
    {
        TextView repetir_calculo_servidor=(TextView) findViewById(R.id.repetir_calculo_servidor);

        String endereco_teste="";

        //azzi_tsai.htm?/23/03/XX/123/0/0

        endereco_teste= Config.endereco_base_teste_html;


        if(criterio_usado.contentEquals("Máxima Tensão"))
        {
            endereco_teste=endereco_teste+"maxima_tensao.htm?/";
        }
        if(criterio_usado.contentEquals("Máxima Deformação"))
        {
            endereco_teste=endereco_teste+"maxima_deformacao.htm?/";
        }
        if(criterio_usado.contentEquals("Tsai-Hill"))
        {
            endereco_teste=endereco_teste+"tsai_hill.htm?/";
        }
        if(criterio_usado.contentEquals("Azzi-Tsai")){
            endereco_teste=endereco_teste+"azzi_tsai.htm?/";
        }


        if(criterio_usado.contentEquals("Tsai-Wu"))
        {
            endereco_teste=endereco_teste+"tsai_wu.htm?/";
            //Log.d("Outubro04",criterio_usado);
            //finish();
        }
///////////////////criterio_6

        if(criterio_usado.contentEquals("Hoffman"))
        {
            endereco_teste=endereco_teste+"hoffman.htm?/";
            //Log.d("Outubro04",criterio_usado);
            //finish();
        }
///////////////////criterio_7
        if(criterio_usado.contentEquals("Hashin"))
        {
            endereco_teste=endereco_teste+"hashin.htm?/";
            //Log.d("Outubro04",criterio_usado);
            //finish();
        }
///////////////////criterio_8
        if(criterio_usado.contentEquals("Christensen"))
        {
            endereco_teste=endereco_teste+"christensen.htm?/";
            //Log.d("Outubro04",criterio_usado);
            //finish();
        }
///////////////////criterio_9
        if(criterio_usado.contentEquals("Puck"))
        {
            endereco_teste=endereco_teste+"puck.htm?/";
            //Log.d("Outubro04",criterio_usado);
            //finish();
        }

        if(criterio_usado.contentEquals("Larc03"))
        {
            endereco_teste=endereco_teste+"larc03.htm?/";
            //Log.d("Outubro04",criterio_usado);
            //finish();
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this,getFilesDir().getAbsolutePath());
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
        }

        String identificador_lamina = dbHelper.obter_identificador_laminas(nome_lamina);

        db_historico_estados_esforcos db_historico_estados=new db_historico_estados_esforcos(this);

        String estado_esforcos=db_historico_estados.obter_ultimo_estados_esforcos_URL();
//Não esta com slash
        String angulo=db_historico_estados.obter_ultimo_angulo_URL();


        endereco_teste=endereco_teste+angulo+"/"+identificador_lamina+"/xx"+estado_esforcos+"xx/"+parametro_experimental_1;

        //Log.d("Outubro06", "\n"+endereco_teste);

        repetir_calculo_servidor.setText(
                Html.fromHtml("<br><a href=\""+endereco_teste+"\">Refazer cálculo no servidor LOCALWEB</a><br>")
        );
        repetir_calculo_servidor.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /*
    * Outubro 09
    * Para evitar saturar a tela
    *
    * Junho21
    *
    * Com ajuda de uma caixa de dialogo, pode-se comparar de forma inteligente
    *
    * */

    public void Onclick_repetir_ifs(View v)
    {

        if(
                criterio_usado.contentEquals("Máxima Tensão") ||
                        criterio_usado.contentEquals("Máxima Deformação")
                )
        {
            String[] alternativas={
                    "IF (longitudinal)",
                    "IF (transversal)",
                    "IF (cisalhamento)",
            };
            janela_uniaoLOGICA(criterio_usado,alternativas);
        }

        if(
                criterio_usado.contentEquals("Hashin")
                        ||
                        criterio_usado.contentEquals("Christensen")
                )
        {
            String[] alternativas={
                    "IF (fibra)",
                    "IF (matriz)",
            };
            janela_uniaoLOGICA(criterio_usado,alternativas);
        }
        else
        {



            listar_laminas(criterio_usado,0);
        }
    }

    /*
    * Outubro 09
    * Geramos mais IF para outras lâminas
    * */

    public void listar_laminas(String alternativa,int indice)
    {

        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
        }
        List<String> list = dbHelper.obter_nomes_laminas(); //Cuidadoi porque o ultimo elemento é nulo

//Recalcular os IFs
        db_historico_estados_esforcos db_historico_estados=new db_historico_estados_esforcos(this);


//Mas antes, registramos a ultima lâmina usada
        historico db_historico=new historico(this);
        String lembrar_ultima_lamina=db_historico.ultima_lamina_usada();

        ArrayList<String> estado_esforcos = new ArrayList<String>();
        estado_esforcos=db_historico_estados.obter_ultimo_estados_esforcos();

        String sigma_x=estado_esforcos.get(0);
        String sigma_y=estado_esforcos.get(1);
        String tau_xy=estado_esforcos.get(2);
        String angulo=estado_esforcos.get(3);

//Ao parecer vamos ter que registrar inumeas laminas.

        int numero_laminas_registradas=list.size();
        final String[] nome_lamina_previo =new String[numero_laminas_registradas+1];
        final String[] nome_lamina =new String[numero_laminas_registradas+1];
        final int[] animalImages=new int[numero_laminas_registradas+1];

        //Float[] if_calculado_paralelo =new Float[numero_laminas_registradas+1];
        Float[][] if_calculado_paralelo = new Float[numero_laminas_registradas+1][2];


        for (int i =0; i< list.size(); i++) {
            //nome_lamina_previo[i]=list.get(i)+" "+estado_esforcos.get(0)+" "+estado_esforcos.get(1)+" "+estado_esforcos.get(2)+" "+estado_esforcos.get(3);

            String if_calculado="";

//Se escolhe conforme os códigos já programados

            if(criterio_usado.contentEquals("Máxima Tensão"))
            {
                numerico_maxima_tensao numerico_maxima_tensao = new numerico_maxima_tensao(
                        getBaseContext(),
                        nome_lamina_previo[i],
                        criterio_usado,
                        "",//envelope_usado
                        getFilesDir().getAbsolutePath()
                );

                if(numerico_maxima_tensao.verificar_entrada(
                        sigma_x,
                        sigma_y,
                        tau_xy,
                        angulo
                ))
                {
                    ArrayList<String> array_list = new ArrayList<String>();
                    //MT
                    array_list=numerico_maxima_tensao.calcular_if(list.get(i));


                    if(array_list.size()>0)
                    {

                        //if_calculado="if (longitudinal):  "+array_list.get(0);
if_calculado=array_list.get(indice);

                        /*
                        saida_criterios.putExtra("IF_1_nome", "if (transversal)");
                        saida_criterios.putExtra("IF_2_nome", "if (cisalhante)");
                        */
                    }
                }
            }
            if(criterio_usado.contentEquals("Máxima Deformação"))
            {
                numerico_maxima_deformacao numerico_maxima_deformacao = new numerico_maxima_deformacao(
                        getBaseContext(),
                        nome_lamina_previo[i],
                        criterio_usado,
                        "",//envelope_usado
                        getFilesDir().getAbsolutePath()
                );

                if(numerico_maxima_deformacao.verificar_entrada(
                        sigma_x,
                        sigma_y,
                        tau_xy,
                        angulo
                ))
                {
                    ArrayList<String> array_list = new ArrayList<String>();
                    //Log.d("Outubro09","\n"+nome_lamina_previo[i]);
                    //MD
                    array_list=numerico_maxima_deformacao.calcular_if(list.get(i));
                    if_calculado=array_list.get(indice);
                }
            }
            if(criterio_usado.contentEquals("Tsai-Hill"))
            {
                numerico_tsai_hill numerico_tsai_hill = new numerico_tsai_hill(
                        getBaseContext(),
                        nome_lamina_previo[i],
                        criterio_usado,
                        "",//envelope_usado
                        getFilesDir().getAbsolutePath()
                );

                if(numerico_tsai_hill.verificar_entrada(
                        sigma_x,
                        sigma_y,
                        tau_xy,
                        angulo
                ))
                {
                    ArrayList<String> array_list = new ArrayList<String>();
                    //Log.d("Outubro09","\n"+nome_lamina_previo[i]);
                    array_list=numerico_tsai_hill.calcular_if(list.get(i));
                    if_calculado=array_list.get(0);
                }

            }

            if(criterio_usado.contentEquals("Azzi-Tsai"))
            {
                numerico_azzi_tsai numerico_azzi_tsai = new numerico_azzi_tsai(
                        getBaseContext(),
                        nome_lamina_previo[i],
                        criterio_usado,
                        "",//envelope_usado
                        getFilesDir().getAbsolutePath()
                );

                if(numerico_azzi_tsai.verificar_entrada(
                        sigma_x,
                        sigma_y,
                        tau_xy,
                        angulo
                ))
                {
                    ArrayList<String> array_list = new ArrayList<String>();
                    //Log.d("Outubro09","\n"+nome_lamina_previo[i]);
                    array_list=numerico_azzi_tsai.calcular_if(list.get(i));
                    if_calculado=array_list.get(0);
                }

            }

            if(criterio_usado.contentEquals("Hoffman"))
            {
                numerico_hoffman numerico_hoffman = new numerico_hoffman(
                        getBaseContext(),
                        nome_lamina_previo[i],
                        criterio_usado,
                        "",//envelope_usado
                        getFilesDir().getAbsolutePath()
                );

                if(numerico_hoffman.verificar_entrada(
                        sigma_x,
                        sigma_y,
                        tau_xy,
                        angulo
                ))
                {
                    ArrayList<String> array_list = new ArrayList<String>();
                    //Log.d("Outubro09","\n"+nome_lamina_previo[i]);
                    array_list=numerico_hoffman.calcular_if(list.get(i));
                    if_calculado=array_list.get(0);
                }
            }
//Nov16
            if(criterio_usado.contentEquals("Hashin"))
            {
                numerico_hashin numerico_hashin = new numerico_hashin(
                        getBaseContext(),
                        nome_lamina_previo[i],
                        criterio_usado,
                        "",//envelope_usado
                        getFilesDir().getAbsolutePath()
                );

                if(numerico_hashin.verificar_entrada(
                        sigma_x,
                        sigma_y,
                        tau_xy,
                        angulo
                ))
                {
                    ArrayList<String> array_list = new ArrayList<String>();
                    //Log.d("Outubro09","\n"+nome_lamina_previo[i]);
                    //MD
                    array_list=numerico_hashin.calcular_if(list.get(i),1e10);
                    if_calculado=array_list.get(indice);
                }
            }

//Nov16
            if(criterio_usado.contentEquals("Christensen"))
            {
                numerico_cristensen numerico_cristensen = new numerico_cristensen(
                        getBaseContext(),
                        nome_lamina_previo[i],
                        criterio_usado,
                        "",//envelope_usado
                        getFilesDir().getAbsolutePath()
                );

                if(numerico_cristensen.verificar_entrada(
                        sigma_x,
                        sigma_y,
                        tau_xy,
                        angulo
                ))
                {
                    ArrayList<String> array_list = new ArrayList<String>();
                    //Log.d("Outubro09","\n"+nome_lamina_previo[i]);
                    //MD
                    array_list=numerico_cristensen.calcular_if(list.get(i));
                    if_calculado=array_list.get(indice);
                }
            }

/*
            //Criamos aquele ARRAY duplo para poder ordenar os IFs
            //Cuidadoi porque o ultimo elemento é nulo

* */

            //Log.d("Outubro10","if_calculado.length\n"+if_calculado);

            if(if_calculado.length()>0)
            {
                if_calculado_paralelo[i][0]=Float.parseFloat(if_calculado);
                if_calculado_paralelo[i][1]=Float.parseFloat(i+"");
            }

            if(if_calculado.length()>0)
            {
                nome_lamina[i]=list.get(i)+"\n"+"IF: "+if_calculado;
            }
            else
            {
                nome_lamina[i]=list.get(i);
            }

            animalImages[i]=R.drawable.logo_mechg;
        }

//Ordenamos os IF de maior a menor quando o IF for negativo

//          Arrays.sort(if_calculado_paralelo[], Collections.reverseOrder());

//Ordenanmento simples de Burbulha: bubblesort
//janeiro27
        boolean sorted=true;
        Float temp;
        Float temp1;

// Ordenamos de menor paara Maior, mas no caso de que o IF for negativo o ordenamento será o contrario

        Float analisar_IF_0=Float.parseFloat(IF_0);

        if(analisar_IF_0>0)
        {
            while (sorted) {
                sorted = false;
                for (int i = 0; i < (if_calculado_paralelo.length-1); i++) {

                    //Log.d("Outubro10","Burble i+\n"+i);

                    if ((if_calculado_paralelo[i][0]!=null) && (if_calculado_paralelo[i+1][0]!=null)) {
                        if (if_calculado_paralelo[i][0] > if_calculado_paralelo[i + 1][0]) {
                            temp = if_calculado_paralelo[i][0];
                            temp1 = if_calculado_paralelo[i][1];

                            if_calculado_paralelo[i][0] = if_calculado_paralelo[i + 1][0]; if_calculado_paralelo[i][1] = if_calculado_paralelo[i + 1][1];
                            if_calculado_paralelo[i + 1][0] = temp;                     if_calculado_paralelo[i + 1][1] = temp1;
                            sorted = true;
                        }
                    }
                }
            }
        }
        else
        {
            while (sorted) {
                sorted = false;
                for (int i = 0; i < (if_calculado_paralelo.length-1); i++) {

                    //Log.d("Outubro10","Burble i+\n"+i);

                    if ((if_calculado_paralelo[i][0]!=null) && (if_calculado_paralelo[i+1][0]!=null)) {
                        if (if_calculado_paralelo[i][0] < if_calculado_paralelo[i + 1][0]) {
                            temp = if_calculado_paralelo[i][0];
                            temp1 = if_calculado_paralelo[i][1];

                            if_calculado_paralelo[i][0] = if_calculado_paralelo[i + 1][0]; if_calculado_paralelo[i][1] = if_calculado_paralelo[i + 1][1];
                            if_calculado_paralelo[i + 1][0] = temp;                     if_calculado_paralelo[i + 1][1] = temp1;
                            sorted = true;
                        }
                    }
                }
            }
        }


        //Log.d("Outubro10","Final Comprimento\n  "+if_calculado_paralelo.length);


        String uniaoLOGICA = "";

        //junho20
// no caso dos critérios extremos,é preciso ordenar apenas por um critério.
        if(
                criterio_usado.contentEquals("Máxima Tensão") ||
                criterio_usado.contentEquals("Máxima Deformação")

                ) {
            uniaoLOGICA = alternativa;
        }

        for (int i = 0; i< (if_calculado_paralelo.length-1); i++)
        {
            //Integer numero_material=Float.floatToIntBits(if_calculado_paralelo[i][1]);

            if(if_calculado_paralelo[i][1]!=null)
            {
                String previo=if_calculado_paralelo[i][1]+"";
                previo=previo.substring(0,previo.indexOf("."));
                Integer numero_material= Integer.parseInt(previo);
                nome_lamina_previo[i]=list. get(numero_material)+"\n"+"IF "+uniaoLOGICA+": "+if_calculado_paralelo[i][0];
            }
            //Log.d("Outubro10","Final i+\n"+i+"\n"+if_calculado_paralelo[i][0]+"------"+if_calculado_paralelo[i][1]);
        }

//Ordenamos os IF de menor a MAIOR quando o IF for positivo


        db_historico.insert_lamina_usada(lembrar_ultima_lamina,"repetirIFS");

        nome_lamina_previo[numero_laminas_registradas]="";
        animalImages[numero_laminas_registradas]=R.drawable.tela_incial_icone_blanco;

        simpleListView=(ListView)findViewById(R.id.simpleListView_saidas_if);
        simpleListView.setVisibility(View.VISIBLE);

        ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
        for (int i = 0; i< nome_lamina_previo.length; i++)
        {
            HashMap<String,String> hashMap=new HashMap<>();//create a hashmap to store the data in key value pair
            hashMap.put("nome_lamina_previo", nome_lamina_previo[i]);
            hashMap.put("image",animalImages[i]+"");
            arrayList.add(hashMap);//add the hashmap into arrayList
        }
        String[] from={"nome_lamina_previo","image"};//string array
        int[] to={R.id.textView,R.id.imageView};//int array of views id's
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,arrayList,R.layout.list_view_items,from,to);//Create object and set the parameters for simpleAdapter


        simpleListView.setAdapter(simpleAdapter);//sets the adapter for listView

        simpleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(nome_lamina_previo[i].length()!=0)
                {
                    janela(view, nome_lamina_previo[i]);
                }
                /////
            }
        });
    }
/*
*Outubro09
*
*
* */

    public void janela(View v,String nome_lamina)
    {
        //Registramos no Banco de dados a lamina usada:
        historico db_historico=new historico(this);
        String agora=new Time(System.currentTimeMillis()).toString();

//É registrada a lâmina usada no DB com o intuito de sugerir as últimas usadas

//Neste caso é preciso limpar e elimiar o IF

        nome_lamina=nome_lamina.substring(0,nome_lamina.indexOf("\n"));

        db_historico.insert_lamina_usada(nome_lamina,agora);

        //db_historico.insert_criterio_usado(nome_lamina,agora);

        String[] str={"Propriedades da lâmina","Envelopes de Falha"};

        AlertDialog.Builder aa=new AlertDialog.Builder(this);
        aa.setTitle(nome_lamina);

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


//junho21
public void janela_uniaoLOGICA(String nome_criterio,final String[] alternativas)
{

    AlertDialog.Builder aa=new AlertDialog.Builder(this);
    aa.setTitle(nome_criterio);

    aa.setSingleChoiceItems(alternativas, 1, new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which)
        {
            // TODO Auto-generated method stub
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
                            break;
                        //Envelopes
                        case 1:

                            break;
                        // Criterios
                        case 2:

                            break;
                        default:
                            break;
                    }
                    listar_laminas(alternativas[temporal_tema],temporal_tema);
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

////////////////////
}


/*


        Arrays.sort(if_calculado_paralelo, new Comparator<Float[]>() {
            @Override
            public int compare(final Float[] entry1, final Float[] entry2) {
                final Float time1 = entry1[0];
                final Float time2 = entry2[0];
                return time1.compareTo(time2);
            }
        });


//Ordenanmento simples de Burbulha: bubblesort
        boolean sorted=false;
        Float temp;
        Float temp1;


        while (sorted) {
            sorted = false;
            for (int i = 0; i < (if_calculado_paralelo.length-1); i++) {

                Log.d("Outubro10","i+\n"+i);

                if (if_calculado_paralelo[i][0] > if_calculado_paralelo[i + 1][0]) {
                    temp = if_calculado_paralelo[i][0];
                    temp1 = if_calculado_paralelo[i][1];

                    if_calculado_paralelo[i][0] = if_calculado_paralelo[i + 1][0]; if_calculado_paralelo[i][1] = if_calculado_paralelo[i + 1][1];
                    if_calculado_paralelo[i + 1][0] = temp;                     if_calculado_paralelo[i + 1][1] = temp1;
                    sorted = true;
                }
            }
        }
* */

        /*
        final String[][] data = new String[][] {
                new String[] { "2009.07.25 20:24", "Message A" },
                new String[] { "2009.07.25 20:17", "Message G" },
                new String[] { "2009.07.25 20:25", "Message B" },
                new String[] { "2009.07.25 20:30", "Message D" },
                new String[] { "2009.07.25 20:01", "Message F" },
                new String[] { "2009.07.25 21:08", "Message E" },
                new String[] { "2009.07.25 19:54", "Message R" } };

        Arrays.sort(data, new Comparator<String[]>() {
            @Override
            public int compare(final String[] entry1, final String[] entry2) {
                final String time1 = entry1[0];
                final String time2 = entry2[0];
                return time1.compareTo(time2);
            }
        });

        for (final String[] s : data) {
            Log.d("Outubro09",s[0] + " " + s[1]);
        }
        */