package br.com.ven2020.envelopes2018;


/*
* Copiando do original:
* Quarta 14 junho
* Chave perdida, nesta vez a Chave será:
* outubro03
* https://github.com/codepath/android_guides/wiki/Sharing-Content-with-Intents
* */

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.ven2020.envelopes2018.database.DatabaseHelper;
import br.com.ven2020.envelopes2018.database.historico;
import br.com.ven2020.envelopes2018.envelopes.Envelopes_entrada_dados;
import br.com.ven2020.envelopes2018.envelopes.envelope_azzi_tsai;
import br.com.ven2020.envelopes2018.envelopes.envelope_christensen;
import br.com.ven2020.envelopes2018.envelopes.envelope_hashin;
import br.com.ven2020.envelopes2018.envelopes.envelope_hoffman;
import br.com.ven2020.envelopes2018.envelopes.envelope_larc03;
import br.com.ven2020.envelopes2018.envelopes.envelope_maxima_deformacao;
import br.com.ven2020.envelopes2018.envelopes.envelope_maxima_tensao;
import br.com.ven2020.envelopes2018.envelopes.envelope_tsai_hill;
import br.com.ven2020.envelopes2018.envelopes.envelope_tsai_wu;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    DatabaseHelper dbHelper= null;
    ListView simpleListView;

    //////////////////////////////////////////////////////////    onCreate    //////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//O botão de refazer se esconde se não tiver envelope para refazer:
        historico db_historico=new historico(this);

        /*
        historico db_historico=new historico(this);
        ArrayList<String> recuperar_angulo_parametroLIST=db_historico.recuperar_angulo_parametro_envelope();

        if(recuperar_angulo_parametroLIST.get(0).length()==0)
        {

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
        }

//junho20   quando for preciso lembramos da última gráfica feita.

        if(db_historico.getProfilesCount()!=0)
        {
            consulta_visualizar_grafico_anterior("Deseja ver o último envelope de falha construído?");
        }
        else
        {
            Button mudar_parametros=(Button) findViewById(R.id.mudar_parametros);
            mudar_parametros.setVisibility(View.GONE);

            TextView titulo_lembrar_grafico=(TextView) findViewById(R.id.titulo_lembrar_grafico);
            titulo_lembrar_grafico.setVisibility(View.VISIBLE);
            titulo_lembrar_grafico.setText("Lâminas:");
        }

        listar_laminas();

    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//junho20
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.esconder_grafico_inicial) {
            esconder_grafica();

            Button mudar_parametros=(Button) findViewById(R.id.mudar_parametros);
            mudar_parametros.setVisibility(View.GONE);

            TextView titulo_lembrar_grafico=(TextView) findViewById(R.id.titulo_lembrar_grafico);
            titulo_lembrar_grafico.setVisibility(View.VISIBLE);
            titulo_lembrar_grafico.setText("Lâminas:");

            Toast.makeText(getBaseContext(),"Esconder gráfico inicial",Toast.LENGTH_LONG).show();
            return true;
        }

        //exibir_grafico_recente

        if (id == R.id.exibir_grafico_recente) {

            historico db_historico=new historico(this);
            ArrayList<String> recuperar_angulo_parametroLIST=db_historico.recuperar_angulo_parametro_envelope();

            if(recuperar_angulo_parametroLIST.size()!=0)
            {
                //Log.d("Junho20","recuperar_angulo_parametroLIST\n"+recuperar_angulo_parametroLIST.toString());

                GraphView graph = (GraphView) findViewById(R.id.graph);
                graph.setVisibility(View.VISIBLE);
                lembrar_ultimo_grafico(recuperar_angulo_parametroLIST);
            }
            else
            {
                Toast.makeText(getBaseContext(),"Sem gráfico inicial",Toast.LENGTH_LONG).show();
            }
        }

            return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//Outubro03
// Vemos as laminas que foram usadas
        if (id == R.id.menu_propriedades_lamina)
        {
            Intent in = new Intent(getBaseContext(), historico_uso_laminas.class);
            startActivityForResult(in, 1);
        }
/////////////////////menu_esforcos_usados


//Outubro10
// Vemos os estados de esforços usados
        if (id == R.id.menu_esforcos_usados)
        {
            Intent in = new Intent(getBaseContext(), historico_estados_esforcos.class);
            startActivityForResult(in, 1);
        }

        if (id == R.id.nav_share) {

        }
        if (id == R.id.envelopes_construidos) {
            //lembrar_ultimo_grafico();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*
    * Junho 12
    * */

    public void listar_laminas()
    {

        List<String> list = dbHelper.obter_nomes_laminas();

        //Log.d("obter_nomes_laminas","obter_nomes_laminas "+list.toString());

        int numero_laminas_registradas=list.size();
        final String[] nome_lamina =new String[numero_laminas_registradas+1];
        final int[] animalImages=new int[numero_laminas_registradas+1];

        for (int i =0; i< list.size(); i++) {
            nome_lamina[i]=list.get(i);
            animalImages[i]=R.drawable.logo_mechg;
        }


        nome_lamina[numero_laminas_registradas]="";
        animalImages[numero_laminas_registradas]=R.drawable.tela_incial_icone_blanco;

        simpleListView=(ListView)findViewById(R.id.simpleListView);

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
*Outubro04
*
*
* */
    public void janela(View v,String nome_lamina)
    {
        //Registramos no Banco de dados a lamina usada:
        historico db_historico=new historico(this);
        String agora=new Time(System.currentTimeMillis()).toString();

//É registrada a lâmina usada no DB com o intuito de sugerir as últimas usadas

        db_historico.insert_lamina_usada(nome_lamina,agora);

        //db_historico.insert_criterio_usado(nome_lamina,agora);

        String[] str={"Propriedades da lâmina","Envelopes de Falha","Critérios de Falha"};

        AlertDialog.Builder aa=new AlertDialog.Builder(this);
        aa.setTitle(nome_lamina);

        aa.setSingleChoiceItems(str, 2, new DialogInterface.OnClickListener()
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

    /*
    * Outubro04
    * */
    public void lembrar_ultimo_grafico(ArrayList<String> recuperar_angulo_parametroLIST){

//Função invocada apenas quando tiver algo no Banco de dados
            TextView titulo_lembrar_grafico=(TextView) findViewById(R.id.titulo_lembrar_grafico);
            titulo_lembrar_grafico.setVisibility(View.VISIBLE);

            Button mudar_parametros=(Button) findViewById(R.id.mudar_parametros);
            mudar_parametros.setVisibility(View.VISIBLE);

            //ArrayList<String> recuperar_angulo_parametroLIST=db_historico.recuperar_angulo_parametro_envelope();

            String nome_lamina=recuperar_angulo_parametroLIST.get(0);
            Double angulo=Double.parseDouble(recuperar_angulo_parametroLIST.get(1));
            Double tau_xy=Double.parseDouble(recuperar_angulo_parametroLIST.get(2));
            String envelope_usado=recuperar_angulo_parametroLIST.get(3);

            String titulo=envelope_usado+"\ncom o ângulo de "+angulo+" (Degree), material: "+nome_lamina;

            Double parametroBIAXIAL=0.0;

            if(envelope_usado.contentEquals("Tsai-Wu"))
            {
                parametroBIAXIAL=Double.parseDouble(recuperar_angulo_parametroLIST.get(4));
                titulo=titulo+"\nCom parãmetro biaxial: "+parametroBIAXIAL+" (Pa)";
                if(parametroBIAXIAL==0.0)
                {
                    esconder_grafica();

                }
            }


        if(envelope_usado.contentEquals("Hashin"))
        {
            parametroBIAXIAL=Double.parseDouble(recuperar_angulo_parametroLIST.get(4));
            titulo=titulo+"\nCom TAU23 : "+parametroBIAXIAL+" (Pa)";
            if(parametroBIAXIAL==0.0)
            {
                esconder_grafica();

            }
        }


            titulo_lembrar_grafico.setText(titulo);

            //Log.d("parseDouble",                    "parseDouble\n"+nome_lamina+"\n"+angulo+"\n"+tau_xy+"\nnome_envelope\n"+envelope_usado);

            //String nome_lamina=db_historico.ultima_lamina_usada();

//Log.d("maio10","envelope_usado\n"+envelope_usado);
            if(envelope_usado.contentEquals("Máxima Tensão"))
            {
                envelope_maxima_tensao envelope_maxima_tensao = new envelope_maxima_tensao(
                        getBaseContext(),
                        nome_lamina,
                        "",
                        envelope_usado,
                        getFilesDir().getAbsolutePath(),
                        this
                );
                envelope_maxima_tensao.fazer_grafica(angulo,tau_xy);
            }

            if(envelope_usado.contentEquals("Máxima Deformação"))
            {
                envelope_maxima_deformacao envelope_maxima_deformacao = new envelope_maxima_deformacao(
                        getBaseContext(),
                        nome_lamina,
                        "",
                        envelope_usado,
                        getFilesDir().getAbsolutePath(),
                        this
                );
                envelope_maxima_deformacao.fazer_grafica(angulo,tau_xy);
            }

            if(envelope_usado.contentEquals("Tsai-Hill"))
            {

                envelope_tsai_hill envelope_tsai_hill = new envelope_tsai_hill(
                        getBaseContext(),
                        nome_lamina,
                        "",
                        envelope_usado,
                        getFilesDir().getAbsolutePath(),
                        this,
                        Config.numeroPontos
                );
                envelope_tsai_hill.fazer_grafica(angulo,tau_xy);
            }

            if(envelope_usado.contentEquals("Azzi-Tsai"))
            {

                // O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
                envelope_azzi_tsai envelope_azzi_tsai = new envelope_azzi_tsai(
                        getBaseContext(),
                        nome_lamina,
                        "",
                        envelope_usado,
                        getFilesDir().getAbsolutePath(),
                        this,
                        Config.numeroPontos
                );

                envelope_azzi_tsai.fazer_grafica(angulo,tau_xy);
            }

            if(envelope_usado.contentEquals("Tsai-Wu"))
            {
                // O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
                envelope_tsai_wu envelope_tsai_wu = new envelope_tsai_wu(
                        getBaseContext(),
                        nome_lamina,
                        "",
                        envelope_usado,
                        getFilesDir().getAbsolutePath(),
                        this,
                        Config.numeroPontos
                );
//Melhor evitar erro
                if(parametroBIAXIAL!=0)
                {
                    envelope_tsai_wu.fazer_grafica(angulo, tau_xy, parametroBIAXIAL);
                }
            }


            if(envelope_usado.contentEquals("Hoffman"))
            {
                envelope_hoffman envelope_hoffman = new envelope_hoffman(
                        getBaseContext(),
                        nome_lamina,
                        "",
                        envelope_usado,
                        getFilesDir().getAbsolutePath(),
                        this,
                        Config.numeroPontos
                );
                envelope_hoffman.fazer_grafica(angulo,tau_xy);
            }
//Hashin
        if(envelope_usado.contentEquals("Hashin"))
        {
            // O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
            envelope_hashin envelope_hashin = new envelope_hashin(
                    getBaseContext(),
                    nome_lamina,
                    "",
                    envelope_usado,
                    getFilesDir().getAbsolutePath(),
                    this,
                    Config.numeroPontos
            );

            envelope_hashin.fazer_grafica(angulo,tau_xy,parametroBIAXIAL);
        }
// ////////////////////////
            if(envelope_usado.contentEquals("Christensen"))
            {

                // O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
                envelope_christensen envelope_christensen = new envelope_christensen(
                        getBaseContext(),
                        nome_lamina,
                        "",
                        envelope_usado,
                        getFilesDir().getAbsolutePath(),
                        this,
                        Config.numeroPontos
                );
                if(angulo % 90 == 0)
                {
                    envelope_christensen.rotacao_pi_2(angulo,tau_xy);
                }
                else
                {
                    envelope_christensen.fazer_grafica(angulo,tau_xy);
                }
            }

//////////////////////////////////////////
    }
//No caso de tocar o gráfico será possível pular para o gráfico


    public void tocar_refazer(View v)
    {
        String informacoes="No caso de tela pequena é recomendado esconder inicialmente os controles";
        CasoTelaMinina(informacoes);
    }


    public void refazer_nucleo(boolean esconder_crontroles)
    {

        historico db_historico=new historico(this);

        ArrayList<String> recuperar_angulo_parametroLIST=db_historico.recuperar_angulo_parametro_envelope();

        String nome_lamina=recuperar_angulo_parametroLIST.get(0);
        Double angulo=Double.parseDouble(recuperar_angulo_parametroLIST.get(1));
        Double tau_xy=Double.parseDouble(recuperar_angulo_parametroLIST.get(2));
        String envelope_usado=recuperar_angulo_parametroLIST.get(3);

        //Log.d("Maio11angulo","angulo  "+tau_xy);

        //db_historico.insert_envelope_usado(animalName[i],agora);
        Intent in = new Intent(getBaseContext(), Envelopes_entrada_dados.class);
        in.putExtra("angulo_getStringExtra",angulo+"");
        in.putExtra("tau_getStringExtra",tau_xy+"");
        in.putExtra("esconder_crontroles",esconder_crontroles);
        startActivityForResult(in, 1);
    }

/*
* Outubro04
* */
    public void esconder_grafica(){

        //escondemos os elementos:
        // gráfico
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setVisibility(View.GONE);
        //botão
        Button mudar_parametros=(Button) findViewById(R.id.mudar_parametros);
        mudar_parametros.setVisibility(View.GONE);
        //titulo
        TextView titulo_lembrar_grafico=(TextView) findViewById(R.id.titulo_lembrar_grafico);
        titulo_lembrar_grafico.setText("Lâminas:");

    }

    //Janeiro19
    public void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        //lembrar_ultimo_grafico();
        esconder_grafica();
    }
//junho20

    private AlertDialog alerta;

    private void consulta_visualizar_grafico_anterior(String informacoes) {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("Consulta Inicial");
        //define a mensagem
        builder.setMessage(informacoes);

        final historico db_historico=new historico(this);

        //define um botão como positivo
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                GraphView graph = (GraphView) findViewById(R.id.graph);
                graph.setVisibility(View.VISIBLE);
                ArrayList<String> recuperar_angulo_parametroLIST=db_historico.recuperar_angulo_parametro_envelope();
                lembrar_ultimo_grafico(recuperar_angulo_parametroLIST);            }
        });
//define um botão como negativo.
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                Button mudar_parametros=(Button) findViewById(R.id.mudar_parametros);
                mudar_parametros.setVisibility(View.GONE);

                TextView titulo_lembrar_grafico=(TextView) findViewById(R.id.titulo_lembrar_grafico);
                titulo_lembrar_grafico.setVisibility(View.VISIBLE);
                titulo_lembrar_grafico.setText("Lâminas:");

            }
        });

        //cria o AlertDialog
        alerta = builder.create();
        alerta.show();
    }

//julho23
private void CasoTelaMinina(String informacoes) {
    //Cria o gerador do AlertDialog
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    //define o titulo
    builder.setTitle("Esconder Controles");
    //define a mensagem
    builder.setMessage(informacoes);

//define um botão como positivo
    builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface arg0, int arg1) {
            refazer_nucleo(true);
        }
    });
//define um botão como negativo.
    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface arg0, int arg1) {
            refazer_nucleo(false);
        }
    });

    //cria o AlertDialog
    alerta = builder.create();
    alerta.show();
}
// ///////////
}

/*
Futuro
* <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

* */
