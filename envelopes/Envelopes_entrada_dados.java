package br.com.ven2020.envelopes2018.envelopes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import java.util.List;

import br.com.ven2020.envelopes2018.Config;
import br.com.ven2020.envelopes2018.R;
import br.com.ven2020.envelopes2018.criterios_falha;
import br.com.ven2020.envelopes2018.database.DatabaseHelper;
import br.com.ven2020.envelopes2018.database.db_historico_estados_esforcos;
import br.com.ven2020.envelopes2018.database.historico;
import br.com.ven2020.envelopes2018.envelopes_falha;
import br.com.ven2020.envelopes2018.propriedades_lamina;


public class Envelopes_entrada_dados extends AppCompatActivity {

    String nome_lamina="";
    String envelope_usado="";

    String origen="";
    String indice="";

    double[][] listar_pontos_array;

    String s_ponto_sigma_x,s_ponto_sigma_y="";

    String endereco_teste= "";

    //Outubro28

    Double tau_xy=0.0;

    String tau_getStringExtra="";
    String angulo_getStringExtra="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entradas_dados_envelopes);

        //Dado que iniciando NÂO temos, entamos bloqueamos o bottaão
        Button lista_pontos=(Button) findViewById(R.id.lista_pontos);
        lista_pontos.setVisibility(View.INVISIBLE);

        //pegamos a ultima lamina

        historico db_historico=new historico(this);
        nome_lamina=db_historico.ultima_lamina_usada();

        TextView textView_nome_lamina=(TextView) this.findViewById(R.id.nome_lamina);


        //pegamos a ultima ENVELOPE usado

        envelope_usado=db_historico.ultima_envelope_usado();

        textView_nome_lamina.setText(
                nome_lamina+
                "\n"+
                "Com o critério:  "+envelope_usado

        );
//Outubro11,
// quando o calculo for refeito pegamos os valores do DB

        origen=getIntent().getStringExtra("origen");
        indice=getIntent().getStringExtra("indice");
        boolean esconder_crontroles= getIntent().getBooleanExtra("esconder_crontroles", false);


        if(origen==null)
        {
            origen="direto";
        }
//Maio11
        angulo_getStringExtra=getIntent().getStringExtra("angulo_getStringExtra");
        tau_getStringExtra=getIntent().getStringExtra("tau_getStringExtra");

        if (
                (angulo_getStringExtra!=null) &&
                (tau_getStringExtra!=null)
                )
        {
            TextView valor_angulo=(TextView) findViewById(R.id.valor_angulo);

            valor_angulo.setText("Ângulo (Degree): "+angulo_getStringExtra);

            refazer_grafico(Double.parseDouble(angulo_getStringExtra),Double.parseDouble(tau_getStringExtra));

            //Log.d("Junho20","\"Ângulo (Degree): \"+angulo_getStringExtra\n"+"angulo_getStringExtra\n"+angulo_getStringExtra);

            refazer_envelope_servidor(Double.parseDouble(angulo_getStringExtra),Double.parseDouble(tau_getStringExtra));

        }


        Integer indice_numerico;

        if(origen.contentEquals("historico"))
        {
            indice_numerico=Integer.parseInt(indice);
        }
        else
        {
            indice_numerico=0;
        }

        ArrayList<String> estado=procurar_estados_diretamente(indice_numerico);

        String edit_textView_sigma_x="0";
        String edit_textView_sigma_y="0";
        String edit_textView_tau_xy="0";
        String text_angulo="0";

        if(estado.size()>0)
        {
            edit_textView_sigma_x=estado.get(0);
            edit_textView_sigma_y=estado.get(1);
            edit_textView_tau_xy=estado.get(2);
            text_angulo=estado.get(3);
        }

        if(origen.contentEquals("historico"))
        {
            s_ponto_sigma_x=estado.get(0);
            s_ponto_sigma_y=estado.get(1);
        }

        final TextView valor_angulo=(TextView) findViewById(R.id.valor_angulo);

        if ((angulo_getStringExtra==null))
        {
            valor_angulo.setText("Ângulo (Degree): "+text_angulo);
        }


        Float f_text_angulo=Float.parseFloat(text_angulo);

        //Log.d("Outubro13","\n"+f_text_angulo);

        Integer seekbar_f_text_angulo;

//Outubro13: não faz sentido tratar ângulos doidos

        if(f_text_angulo>360.0)
        {
            seekbar_f_text_angulo=0;
        }
        else
        {
            seekbar_f_text_angulo=Math.round((f_text_angulo*100)/360);

        }

        //lembrar_ultimo_grafico();

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);

        seekBar.setProgress(seekbar_f_text_angulo);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
     //           seekBarValue.setText(String.valueOf(progress));
                //Log.d("barra","\n"+progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //Log.d("barra","\n"+"Inicia");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

                double angulo;

                angulo=(seekBar.getProgress()*360)/100;
                valor_angulo.setText("Ângulo (Degree): "+angulo);
                janela_ultimo_valor_seekBar(angulo);
            }
        });
//julho16
        if(Rid_ampliar_grafico)
        {
            RelativeLayout comandos_activity_entrada_dados_envelopes=(RelativeLayout) findViewById(R.id.comandos_activity_entrada_dados_envelopes);
            comandos_activity_entrada_dados_envelopes.setVisibility(View.GONE);
        }

//julho23
        if(esconder_crontroles)
        {
            RelativeLayout comandos_activity_entrada_dados_envelopes=(RelativeLayout) findViewById(R.id.comandos_activity_entrada_dados_envelopes);
            comandos_activity_entrada_dados_envelopes.setVisibility(View.GONE);
        }

///////
    }


    /*
    * Outubro12
    * Recoperamos o ponto para que seja representado no gráfico
    * */


    public ArrayList<String> procurar_estados_diretamente(Integer i)
    {
        db_historico_estados_esforcos DB_historico_estados_esforcos=new db_historico_estados_esforcos(this);

        ArrayList<String> obterEstado=DB_historico_estados_esforcos.obter_estados_esforcos_especifico(i);

        //Log.d("Outubro11"," valor de i \n"+i+" \n"+obterEstado.toString());

        return  obterEstado;
    }
//////////////////////////
    /*
    * Outubro12
    * */
public void lembrar_ultimo_grafico(){

    //pegamos a ultima ENVELOPE usado

    historico db_historico=new historico(this);
    String envelope_usado=db_historico.ultima_envelope_usado();


    GraphView graph = (GraphView) findViewById(R.id.graph);
    graph.setVisibility(View.VISIBLE);

    LineGraphSeries<DataPoint> series_0 = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(-1, -1),
            new DataPoint(-1, 1),
            new DataPoint(1, 1)
    });

    LineGraphSeries<DataPoint> series_1 = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(-1, -1),
            new DataPoint(1, -1),
            new DataPoint(1.01, 1.01)
    });


    graph.setTitle("Plano (sigma_x,sigma_y)\nParâmetro tau_xy="+tau_xy);

    graph.getViewport().setXAxisBoundsManual(true);
    graph.getViewport().setYAxisBoundsManual(true);

    graph.getViewport().setMinX(-2);
    graph.getViewport().setMaxX(2);

    graph.getViewport().setMinY(-2);
    graph.getViewport().setMaxY(2);

    graph.getViewport().setScalable(true);
    graph.getViewport().setScalableY(true);

    graph.addSeries(series_0);
    graph.addSeries(series_1);

    graph.clearSecondScale();
}


////Outubro13
    public void janela_ultimo_valor_seekBar(double angulo)
    {
        AlertDialog.Builder aa=new AlertDialog.Builder(this);

//TITULO
        aa.setTitle("Rotacionar o envelope (Degree)");
// TEXT entrada
// Set up the input
        final EditText input = new EditText(this);
        aa.setView(input);
        input.setText(angulo+"");

        aa.setPositiveButton("Aceitar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        Double D_biaxial_experimental=0.0;
                        try {
                            D_biaxial_experimental = Double.parseDouble(m_Text);
                        }
                        catch (NumberFormatException e)
                        {
                            D_biaxial_experimental=0.0;
                        }


                        janela_coletar_TAU_APOS_ultimo_valor_seekBar(D_biaxial_experimental,envelope_usado);


                    }
                });

        aa.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        aa.show();
    }
// //////////////////
    double biaxial=0.0;
    Double angulo_global=0.0;
// Outubro13
    public void refazer_grafico(Double angulo,Double tau)
    {
        angulo_global=angulo;
//No caso de Tsai-Wu, ele deve fazer mais um registro para colocar o parâmetro biaxial certo
        historico db_historico=new historico(this);
        db_historico.insert_envelope_paramestros_usados(
                nome_lamina
                ,envelope_usado,
                angulo+"",
                tau_xy+""
                ,"",""+biaxial);


//Tsai-wu
        if(envelope_usado.contentEquals("Tsai-Wu"))
        {
            coletar_informacoes();
        }
///
        if(envelope_usado.contentEquals("Hashin"))
        {
            coletar_informacoes();
        }
///////////////////////////////////////////////////////////////////////////
        if(envelope_usado.contentEquals("Christensen"))
        {
            coletar_informacoes();
        }
///////////////////////////////////////////////////////////////////////////
        if(envelope_usado.contentEquals("Puck"))
        {
            coletar_informacoes();
        }

        if(envelope_usado.contentEquals("Larc03"))
        {
            coletar_informacoes();
        }

        if(envelope_usado.contentEquals("Máxima Tensão"))
        {
// O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
            envelope_maxima_tensao envelope_maxima_tensao = new envelope_maxima_tensao(
                    getBaseContext(),
                    nome_lamina,
                    "",
                    envelope_usado,
                    getFilesDir().getAbsolutePath(),
                    this
            );


            listar_pontos_array=envelope_maxima_tensao.fazer_grafica(angulo,tau_xy);

            if(origen.contentEquals("historico"))
            {
                Double d_ponto_sigma_x = Double.parseDouble(s_ponto_sigma_x);
                Double d_ponto_sigma_y = Double.parseDouble(s_ponto_sigma_y);
                //envelope_maxima_tensao.colocarPONTO(d_ponto_sigma_x, d_ponto_sigma_y);
            }

        }

        if(envelope_usado.contentEquals("Máxima Deformação"))
        {
// O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
            envelope_maxima_deformacao envelope_maxima_deformacao = new envelope_maxima_deformacao(
                    getBaseContext(),
                    nome_lamina,
                    "",
                    envelope_usado,
                    getFilesDir().getAbsolutePath(),
                    this
            );

            listar_pontos_array=envelope_maxima_deformacao.fazer_grafica(angulo,tau_xy);

            if(origen.contentEquals("historico"))
            {
                Double d_ponto_sigma_x = Double.parseDouble(s_ponto_sigma_x);
                Double d_ponto_sigma_y = Double.parseDouble(s_ponto_sigma_y);
                //envelope_maxima_tensao.colocarPONTO(d_ponto_sigma_x, d_ponto_sigma_y);
            }
        }

        if(envelope_usado.contentEquals("Tsai-Hill")) {
//Primeiramente se coletam as informações para que o COLETOR de resultados possa ter as devidas informações
           coletar_informacoes();
        }
//////////////////////////////////////////////////////////////////////////
        if(envelope_usado.contentEquals("Azzi-Tsai"))
        {
            coletar_informacoes();
        }
//////////////////////////////////////////////////////////////////////////
        if(envelope_usado.contentEquals("Hoffman"))
        {
            coletar_informacoes();
        }

//Limpamos a tela antes de refazer o desenho

        visibilizar_quando_tiver_extremos();

    }

    ////Outubro29
    public void janela_coletar_TAU_APOS_ultimo_valor_seekBar(final double angulo,String criterio)
    {

        AlertDialog.Builder aa=new AlertDialog.Builder(this);

//TITULO
        aa.setTitle("Por favor indique o esforço TauXY (Pa)");

        if(envelope_usado.contentEquals("Máxima Deformação"))
        {
            aa.setTitle("Por favor indique a deformação Gamma ");
        }

// TEXT entrada
// Set up the input
        final EditText input = new EditText(this);
        input.setText("0.0");
        aa.setView(input);

        aa.setPositiveButton("Aceitar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        Double gamma=Double.parseDouble(m_Text);
                        //Log.d("Marco08","janela_coletar_TAU_APOS_ultimo_valor_seekBar\n"+gamma);
//Outubro13
// Rotamos o envelope
//Marco09
                        refazer_envelope_servidor(angulo,gamma);
                        refazer_grafico(angulo,gamma);
                        angulo_getStringExtra=angulo+"";
                    }
                });

        aa.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        aa.show();
    }
//janeiro18 Com o intuito de compartilhar os gráficos com minha princesa vou colocar este sistema
// de captura imagem e enviar pelo WhatsAPP
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_direito_entrada_envelope, menu);
        return true;
    }

    boolean Rid_ampliar_grafico=false;

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        CharSequence userText = "julho16";
        outState.putCharSequence("savedText", userText);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        CharSequence userText =savedInstanceState.getCharSequence("savedText");

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.ampliar_grafico) {

            //Rid_ampliar_grafico
            RelativeLayout comandos_activity_entrada_dados_envelopes=(RelativeLayout) findViewById(R.id.comandos_activity_entrada_dados_envelopes);
            comandos_activity_entrada_dados_envelopes.setVisibility(View.GONE);

            Toast.makeText(getBaseContext(),"Ampliar o gráfico",Toast.LENGTH_LONG).show();

            Rid_ampliar_grafico=true;

            return true;
        }

//junho20

        if (id == R.id.informacoes_grafico) {
            TextView valor_angulo=(TextView) findViewById(R.id.valor_angulo);
            TextView nome_lamina=(TextView) findViewById(R.id.nome_lamina);
            mostrar_informacoes_grafico(nome_lamina.getText()+"  "+valor_angulo.getText());
        }

        if (id == R.id.reduzir_grafico) {

            RelativeLayout comandos_activity_entrada_dados_envelopes=(RelativeLayout) findViewById(R.id.comandos_activity_entrada_dados_envelopes);
            comandos_activity_entrada_dados_envelopes.setVisibility(View.VISIBLE);

            Toast.makeText(getBaseContext(),"Reduzir o gráfico",Toast.LENGTH_LONG).show();

            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    //Junho19

    private void mostrar_informacoes_grafico(String informacoes) {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("Informações sobre o gráfico");
        //define a mensagem
        builder.setMessage(informacoes);
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
    }




    //Janeiro19
    public void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            //View v1 = getWindow().getDecorView().getRootView();
            //janeiro19
            View v1 = this.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            v1.buildDrawingCache(true);

//Antigo
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

    public void novoCapturador(View v)
    {
 //https://stackoverflow.com/questions/8294110/taking-screenshot


        v.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        // creates immutable clone
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());

        Canvas canvas = new Canvas(b) {
            @Override
            public boolean isHardwareAccelerated() {
                return true;
            }
        };

        v.setDrawingCacheEnabled(false); // clear drawing cache
    }
//Marco08
    public void refazer_envelope_servidor(Double angulo,Double gamma) {
        TextView repetir_envelope_servidor = (TextView) findViewById(R.id.repetir_envelope_servidor);

        DatabaseHelper dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
        }

        String identificador_lamina = dbHelper.obter_identificador_laminas(nome_lamina);

        //azzi_tsai.htm?/23/03/XX/123/0/0

        //maxima_deformacao.htm?/13/1/xx/xx/xx/0

        endereco_teste = Config.endereco_base_teste_html_envelopes;


        if (envelope_usado.contentEquals("Máxima Tensão")) {
            endereco_teste = endereco_teste + "maxima_tensao.htm?/";
        }
        if (envelope_usado.contentEquals("Máxima Deformação")) {
            endereco_teste = endereco_teste + "maxima_deformacao.htm?/";
        }
        if (envelope_usado.contentEquals("Tsai-Hill")) {
            endereco_teste = endereco_teste + "tsai_hill.htm?/";
        }
        if (envelope_usado.contentEquals("Azzi-Tsai")) {
            endereco_teste = endereco_teste + "azzi_tsai.htm?/";
        }


        if (envelope_usado.contentEquals("Tsai-Wu")) {
            endereco_teste = endereco_teste + "tsai_wu.htm?/";
        }
///////////////////criterio_6

        if (envelope_usado.contentEquals("Hoffman")) {
            endereco_teste = endereco_teste + "hoffman.htm?/";
        }
///////////////////criterio_7
        if (envelope_usado.contentEquals("Hashin")) {
            endereco_teste = endereco_teste + "hashin.htm?/";
        }
///////////////////criterio_8
        if (envelope_usado.contentEquals("Christensen")) {
            endereco_teste = endereco_teste + "christensen.htm?/";
            //Log.d("Outubro04",criterio_usado);
            //finish();
        }
///////////////////criterio_9
        if (envelope_usado.contentEquals("Puck")) {
            endereco_teste = endereco_teste + "puck.htm?/";
        }

        if (envelope_usado.contentEquals("Larc03")) {
            endereco_teste = endereco_teste + "larc03.htm?/";
        }
//maxima_deformacao.htm?/13/1/xx/xx/xx/0
        endereco_teste = endereco_teste + angulo + "/" + identificador_lamina + "/xx/xx/xx/" + gamma;

        //Log.d("Marco09", "\n"+endereco_teste);

        repetir_envelope_servidor.setText(
                Html.fromHtml("<br><a href=\"" + endereco_teste + "\">Refazer envelope no servidor LOCALWEB</a><br>")
        );
        repetir_envelope_servidor.setMovementMethod(LinkMovementMethod.getInstance());

        //Marco17
    }

    String dados_envelope="";
/*
Com um aviso prévio vemos que deseja ser compartilhado:
1) Endereco no servidor
2) Pontos do gráfico
**/

    public void onClick_compartilhar(View v)
    {

        String[] str={
                "Endereço URL para refazer no servidor",
                "Pontos extremos do envelope"};

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
                        Toast.makeText(getApplicationContext(),
                                "Com este endereço poderá refazer no servidor o mesmo gráfico",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(),
                                "Pontos deste envelope para ser usados no programa GNUPLOT", Toast.LENGTH_SHORT).show();
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
                            // Endereço
                            case 0:
                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                String shareBody =endereco_teste;
                                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                startActivity(Intent.createChooser(sharingIntent,
                                        "Compartilhar endereço para refazer Envelope"));
                                break;
                            // Pontos
                            case 1:
                                calcular_compartilhar_pontos();
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

//Maio11
public void activity_lista_pontos(View v)
{
    double [] listar_pontos_array_x=new double[4];
    double [] listar_pontos_array_y=new double[4];
    double [] listar_pontos_array_xy=new double[4];

    for(int i=0;i<4;i++)
    {
        listar_pontos_array_x[i]=listar_pontos_array[i][0];
        listar_pontos_array_y[i]=listar_pontos_array[i][1];

        if(envelope_usado.contentEquals("Máxima Deformação"))
        {
            listar_pontos_array_xy[i]=listar_pontos_array[i][2];
        }

        //Log.d("listar_pontos_array_xy", "i "+i+"\n"+listar_pontos_array[i][2]);
    }
    Intent in = new Intent(getBaseContext(), ListaPontos.class);

    in.putExtra("listar_pontos_array_x",listar_pontos_array_x);
    in.putExtra("listar_pontos_array_y",listar_pontos_array_y);
    in.putExtra("listar_pontos_array_xy",listar_pontos_array_xy);

    if((tau_getStringExtra!=null) && (tau_xy==null))
    {
        in.putExtra("tau_xy",tau_getStringExtra+"");
    }
    else
    {
        in.putExtra("tau_xy",tau_xy+"");
    }

    if(angulo_getStringExtra!=null)
    {
        in.putExtra("angulo",angulo_getStringExtra+"");

    }
    else
    {
        in.putExtra("angulo","0.0");
    }

    if(envelope_usado.contentEquals("Tsai-Wu"))
    {
        in.putExtra("wu_biaxial",biaxial+"");
        //Log.d("EUwu_biaxial",                "\nwu_biaxial\n"+biaxial        );
    }


    in.putExtra("listar_pontos_array_length",listar_pontos_array.length);
    in.putExtra("envelope_usado",envelope_usado);
    in.putExtra("nome_lamina",nome_lamina);
    startActivityForResult(in, 1);
}
//Maio16

    private AlertDialog alerta;

    private void alert_biaxial_nulo() {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("Tsai-WU");
        //define a mensagem
        builder.setMessage("Não pode indicar um parâmetro biaxial nulo");
//define um botão como positivo
        builder.setPositiveButton("Indicar Parâmetro", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
//Coletar o Parâmetro Biaxial correto
            }
        });
//define um botão como negativo.
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
    }
//Maio16
//April 02

    public void janela_coletar_WU_ultimo_valor_seekBar(final Double angulo)
    {
        AlertDialog.Builder aa=new AlertDialog.Builder(this);

//TITULO
        aa.setTitle("Por favor indique o parâmetro biaxial");

        /*
        if(envelope_usado.contentEquals("Máxima Deformação"))
        {
            aa.setTitle("Por favor indique a deformação Gamma ");
        }
*/
// TEXT entrada
// Set up the input
        final EditText input = new EditText(this);
        input.setText("0.0");
        aa.setView(input);
//colocamos um valor tentativo de biaxial
// Se faz necesario colocar uma resistencia conforme o materil:
        DatabaseHelper dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
        }
        List<String> list = dbHelper.obter_propriedades_laminas(nome_lamina);
        //Log.d("list.get(12)",""+list.get(12));
        String ajuda_biaxial_SIGMA_T_1=list.get(12);
        ajuda_biaxial_SIGMA_T_1=ajuda_biaxial_SIGMA_T_1.substring(ajuda_biaxial_SIGMA_T_1.indexOf(":")+1,ajuda_biaxial_SIGMA_T_1.length());

        input.setText(""+ajuda_biaxial_SIGMA_T_1);

        aa.setPositiveButton("Aceitar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//Aqui deveria ativar a função GRAFICAR do Wu
                        String m_Text = input.getText().toString();
                        biaxial=Double.parseDouble(m_Text);

                        if(biaxial!=0)
                        {
                            refazer_grafico_tsai_wu(angulo,tau_xy,biaxial);
                            //  Log.d("maio16","angulo \n"+angulo+"\ntau_xy \n"+tau_xy+"\nbiaxial\n"+biaxial);
                        }
                        else
                        {
                            alert_biaxial_nulo();
                        }

                    }
                });

        aa.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        aa.show();
    }
//maio16
public void refazer_grafico_tsai_wu(Double angulo,Double tau_xy, Double biaxial)
{


//No caso de Tsai-Wu, ele deve fazer mais um registro para colocar o parâmetro biaxial certo
    historico db_historico=new historico(this);
    db_historico.insert_envelope_paramestros_usados(
            nome_lamina
            ,envelope_usado,
            angulo+"",
            tau_xy+""
            ,"",
            ""+biaxial);

    envelope_tsai_wu envelope_tsai_wu = new envelope_tsai_wu(
            getBaseContext(),
            nome_lamina,
            "",
            envelope_usado,
            getFilesDir().getAbsolutePath(),
            this,
            numero_pontos_global
    );

    listar_pontos_array=envelope_tsai_wu.fazer_grafica(angulo,tau_xy,biaxial);

    visibilizar_quando_tiver_extremos();
}

//maio16
    public void visibilizar_quando_tiver_extremos()
    {
        ImageView gnu_compartilhar_dados=(ImageView) findViewById(R.id.gnu_compartilhar_dados);

        //Dado que iniciando NÂO temos, entamos bloqueamos o bottaão
        if(listar_pontos_array!=null)
        {
            if(listar_pontos_array.length>0)
            {
                Button lista_pontos=(Button) findViewById(R.id.lista_pontos);
                lista_pontos.setVisibility(View.VISIBLE);

                //Se tiver dados visualizamos o botão de compartilhar
                gnu_compartilhar_dados.setVisibility(View.VISIBLE);
            }

        }
    }

//Maio22
public void calcular_compartilhar_pontos(){

    dados_envelope="";

    for(Integer i=0;i<listar_pontos_array.length; i++)
    {
        dados_envelope=dados_envelope+listar_pontos_array[i][0]+"  "+listar_pontos_array[i][1]+"\n";
    }
    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    String shareBody =dados_envelope;
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "");
    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
    startActivity(Intent.createChooser(sharingIntent,
            "Compartilhar Pontos extremos do envelope"));
////
}
//Outubro 29
    int numero_pontos_global=0;
//Larc03
    Integer global_larc03_numero_circulos=10;
    Double global_larc03_alpha_0=0.0;
    Integer global_larc03_numero_elementos_circulos=10;
    Double global_larc03_TAU23=0.0;
    Double global_larc03_Y_T_is=0.0;
    Double global_larc03_S_L_is=0.0;
//puck
    Integer global_puck_numero_circulos=10;
    Double global_puck_m_sigF=1.0;
    Integer global_puck_numero_elementos_circulos=10;
    Double global_puck_p_plus_TL=1.0;
    Double global_puck_p_minus_TL=1.0;
    Double global_puck_p_minus_TT=1.0;
    Double global_puck_sigma_1_D=1.0;
    Double global_puck_puck_R_TT_A=1.0;
    Double global_puck_TAU12_C=1.0;
    Double global_puck_NU12_f=1.0;
    Double global_puck_E1_f=1.0;
/*
*
* Outubro 29:
*
*
*
*
*
* */

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    //Tsai-Hill
    if(requestCode==103){
        if (resultCode == RESULT_OK) {
            String returnValue = data.getStringExtra("valor_coletado");
            numero_pontos_global= Integer.parseInt(returnValue);
            construtor_envelope_tsai_hill();
        }
        else
        {
            finish();
        }
    }

    //Azzi-Tsai
    if(requestCode==104){
        if (resultCode == RESULT_OK) {
            String returnValue = data.getStringExtra("valor_coletado");
            numero_pontos_global= Integer.parseInt(returnValue);
            construtor_envelope_azzi_tsai();
        }
        else
        {
            finish();
        }
    }

    //Tsai-Wu
    if(requestCode==105){
        if (resultCode == RESULT_OK) {
            String returnValue = data.getStringExtra("valor_coletado");
            numero_pontos_global= Integer.parseInt(returnValue);
            construtor_envelope_tsai_wu();
        }
        else
        {
            finish();
        }
    }
//Hoffman
    if(requestCode==106){
        if (resultCode == RESULT_OK) {
            String returnValue = data.getStringExtra("valor_coletado");
            numero_pontos_global= Integer.parseInt(returnValue);
            construtor_envelope_hoffman();
        }
        else
        {
            finish();
        }
    }
//Hashin
    if(requestCode==107){
        if (resultCode == RESULT_OK) {
            String returnValue = data.getStringExtra("valor_coletado");
            numero_pontos_global= Integer.parseInt(returnValue);
            construtor_envelope_hashin();
        }
        else
        {
            finish();
        }
    }
//Christensen
    if(requestCode==108){
        if (resultCode == RESULT_OK) {
            String returnValue = data.getStringExtra("valor_coletado");
            numero_pontos_global= Integer.parseInt(returnValue);
            construtor_envelope_christensen();
        }
        else
        {
            finish();
        }
    }

//Puck
    if(requestCode==109){
        if (resultCode == RESULT_OK) {

            String numero_circulos = data.getStringExtra("numero_circulos_coletado");
            String m_sigF = data.getStringExtra("m_sigF_coletado");
            String numero_elementos_circulos = data.getStringExtra("numero_circulos_elementos_coletado");
            String p_plus_TL = data.getStringExtra("p_plus_TL_coletado");
            String p_minus_TL = data.getStringExtra("p_minus_TL_coletado");
            String p_minus_TT = data.getStringExtra("p_minus_TT_coletado");
            String sigma_1_D = data.getStringExtra("sigma_1_D_coletado");
            String puck_R_TT_A = data.getStringExtra("puck_R_TT_A_coletado");
            String TAU12_C = data.getStringExtra("TAU12_C_coletado");
            String NU12 = data.getStringExtra("NU12_coletado");
            String E1 = data.getStringExtra("E1_coletado");

            global_puck_numero_circulos=Integer.parseInt(numero_circulos);
            global_puck_m_sigF=Double.parseDouble(m_sigF);
            global_puck_numero_elementos_circulos=Integer.parseInt(numero_elementos_circulos);
            global_puck_p_plus_TL=Double.parseDouble(p_plus_TL);
            global_puck_p_minus_TL=Double.parseDouble(p_minus_TL);
            global_puck_p_minus_TT=Double.parseDouble(p_minus_TT);
            global_puck_sigma_1_D=Double.parseDouble(sigma_1_D);
            global_puck_puck_R_TT_A=Double.parseDouble(puck_R_TT_A);
            global_puck_TAU12_C=Double.parseDouble(TAU12_C);
            global_puck_NU12_f=Double.parseDouble(NU12);
            global_puck_E1_f=Double.parseDouble(E1);

            construtor_envelope_puck();
        }
        else
        {
            finish();
        }
    }

//Larc03
    if(requestCode==110){
        if (resultCode == RESULT_OK) {

            String numero_circulos = data.getStringExtra("numero_circulos_coletado");
            String alpha = data.getStringExtra("alpha_coletado");
            String numero_elementos_circulos = data.getStringExtra("numero_circulos_elementos_coletado");
            String TAU23 = data.getStringExtra("TAU23_coletado");
            String Y_T_is = data.getStringExtra("Y_T_is_coletado");
            String S_L_is = data.getStringExtra("S_L_is_coletado");

            global_larc03_numero_circulos=Integer.parseInt(numero_circulos);
            global_larc03_alpha_0=Double.parseDouble(alpha);
            global_larc03_numero_elementos_circulos=Integer.parseInt(numero_elementos_circulos);
            global_larc03_TAU23=Double.parseDouble(TAU23);
            global_larc03_Y_T_is=Double.parseDouble(Y_T_is);
            global_larc03_S_L_is=Double.parseDouble(S_L_is);

            construtor_envelope_Larc03();
        }
        else
        {
            finish();
        }
    }
////
    visibilizar_quando_tiver_extremos();
////
}


//Outubro 29
    public void coletar_informacoes(){



        if(envelope_usado.contentEquals("Tsai-Hill"))
        {
            Intent Intent_Coletar_informacoes = new Intent(getBaseContext(), Coletar_informacoes.class);
            Intent_Coletar_informacoes.putExtra("funcao","coletar_numero_pontos");
            startActivityForResult(Intent_Coletar_informacoes, 103);
        }

        if(envelope_usado.contentEquals("Azzi-Tsai")){
            Intent Intent_Coletar_informacoes = new Intent(getBaseContext(), Coletar_informacoes.class);
            Intent_Coletar_informacoes.putExtra("funcao","coletar_numero_pontos");
            startActivityForResult(Intent_Coletar_informacoes, 104);
        }


        if(envelope_usado.contentEquals("Tsai-Wu"))
        {
            Intent Intent_Coletar_informacoes = new Intent(getBaseContext(), Coletar_informacoes.class);
            Intent_Coletar_informacoes.putExtra("funcao","coletar_numero_pontos");
            startActivityForResult(Intent_Coletar_informacoes, 105);
        }
///////////////////criterio_6

        if(envelope_usado.contentEquals("Hoffman"))
        {
            Intent Intent_Coletar_informacoes = new Intent(getBaseContext(), Coletar_informacoes.class);
            Intent_Coletar_informacoes.putExtra("funcao","coletar_numero_pontos");
            startActivityForResult(Intent_Coletar_informacoes, 106);
        }
///////////////////criterio_7
        if(envelope_usado.contentEquals("Hashin"))
        {
            if((angulo_global % 90)==0)
            {
                numero_pontos_global=Config.numeroPontos;
                construtor_envelope_hashin();
            }
            else
            {
                Intent Intent_Coletar_informacoes = new Intent(getBaseContext(), Coletar_informacoes.class);
                Intent_Coletar_informacoes.putExtra("funcao","coletar_numero_pontos");
                startActivityForResult(Intent_Coletar_informacoes, 107);
            }
        }
///////////////////criterio_8
        if(envelope_usado.contentEquals("Christensen"))
        {
            Intent Intent_Coletar_informacoes = new Intent(getBaseContext(), Coletar_informacoes.class);
            Intent_Coletar_informacoes.putExtra("funcao","coletar_numero_pontos");
            startActivityForResult(Intent_Coletar_informacoes, 108);
        }
///////////////
//Puck
//criterio_9
        if(envelope_usado.contentEquals("Puck"))
        {
            Intent Intent_Coletar_informacoes = new Intent(getBaseContext(), Coletar_informacoes.class);
            Intent_Coletar_informacoes.putExtra("funcao","coletar_puck");

            DatabaseHelper dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
            try {
                dbHelper.prepareDatabase();
            } catch (IOException e) {
            }
            List<String> list = dbHelper.obter_propriedades_laminas(nome_lamina);

            String ajuda_TAU12=list.get(16);
            ajuda_TAU12=ajuda_TAU12.substring(ajuda_TAU12.indexOf(":")+1,ajuda_TAU12.length());


            String ajuda_E1=list.get(3);
            ajuda_E1=ajuda_E1.substring(ajuda_E1.indexOf(":")+1,ajuda_E1.length());

            String ajuda_NU12=list.get(9);
            ajuda_NU12=ajuda_NU12.substring(ajuda_NU12.indexOf(":")+1,ajuda_NU12.length());


            Intent_Coletar_informacoes.putExtra("TAU12",ajuda_TAU12);
            Intent_Coletar_informacoes.putExtra("NU12",ajuda_NU12);
            Intent_Coletar_informacoes.putExtra("E1",ajuda_E1);
            startActivityForResult(Intent_Coletar_informacoes, 109);

        }
//Larc03
        if(envelope_usado.contentEquals("Larc03"))
        {
            Intent Intent_Coletar_informacoes = new Intent(getBaseContext(), Coletar_informacoes.class);
            Intent_Coletar_informacoes.putExtra("funcao","coletar_larc03");

            DatabaseHelper dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
            try {
                dbHelper.prepareDatabase();
            } catch (IOException e) {
            }
            List<String> list = dbHelper.obter_propriedades_laminas(nome_lamina);

            String ajuda_TAU12=list.get(16);
            ajuda_TAU12=ajuda_TAU12.substring(ajuda_TAU12.indexOf(":")+1,ajuda_TAU12.length());


            Intent_Coletar_informacoes.putExtra("TAU12",ajuda_TAU12);
            startActivityForResult(Intent_Coletar_informacoes, 110);
        }
    }

//Outubro 29

    public void construtor_envelope_tsai_hill()
    {
// O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
        envelope_tsai_hill envelope_tsai_hill = new envelope_tsai_hill(
                getBaseContext(),
                nome_lamina,
                "",
                envelope_usado,
                getFilesDir().getAbsolutePath(),
                this,
                numero_pontos_global
        );

        listar_pontos_array=envelope_tsai_hill.fazer_grafica(angulo_global,tau_xy);

        if(origen.contentEquals("historico"))
        {
            Double d_ponto_sigma_x = Double.parseDouble(s_ponto_sigma_x);
            Double d_ponto_sigma_y = Double.parseDouble(s_ponto_sigma_y);
            envelope_tsai_hill.colocarPONTO(d_ponto_sigma_x, d_ponto_sigma_y);
        }
    }
//Outubro29
public void construtor_envelope_azzi_tsai()
{
    //Log.d("numero_pontos_global","construtor_envelope_azzi_tsai  "+numero_pontos_global);
    // O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
    envelope_azzi_tsai envelope_azzi_tsai = new envelope_azzi_tsai(
            getBaseContext(),
            nome_lamina,
            "",
            envelope_usado,
            getFilesDir().getAbsolutePath(),
            this,
            numero_pontos_global
    );

    //envelope_azzi_tsai.fazer_grafica(angulo,tau_xy);
    listar_pontos_array=envelope_azzi_tsai.fazer_grafica(angulo_global,tau_xy);

    if(origen.contentEquals("historico"))
    {
        Double d_ponto_sigma_x = Double.parseDouble(s_ponto_sigma_x);
        Double d_ponto_sigma_y = Double.parseDouble(s_ponto_sigma_y);
        envelope_azzi_tsai.colocarPONTO(d_ponto_sigma_x, d_ponto_sigma_y);
    }
}
    //Outubro29
    public void construtor_envelope_tsai_wu(){
        // O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
        envelope_tsai_wu envelope_tsai_wu = new envelope_tsai_wu(
                getBaseContext(),
                nome_lamina,
                "",
                envelope_usado,
                getFilesDir().getAbsolutePath(),
                this,
                numero_pontos_global
        );

        janela_coletar_WU_ultimo_valor_seekBar(angulo_global);

        if(origen.contentEquals("historico"))
        {
            Double d_ponto_sigma_x = Double.parseDouble(s_ponto_sigma_x);
            Double d_ponto_sigma_y = Double.parseDouble(s_ponto_sigma_y);
            envelope_tsai_wu.colocarPONTO(d_ponto_sigma_x, d_ponto_sigma_y);
        }
    }
    public  void construtor_envelope_hoffman(){

        // O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
        envelope_hoffman envelope_hoffman = new envelope_hoffman(
                getBaseContext(),
                nome_lamina,
                "",
                envelope_usado,
                getFilesDir().getAbsolutePath(),
                this,
                numero_pontos_global
        );

        //envelope_azzi_tsai.fazer_grafica(angulo,tau_xy);
        listar_pontos_array=envelope_hoffman.fazer_grafica(angulo_global,tau_xy);


        if(origen.contentEquals("historico"))
        {
            Double d_ponto_sigma_x = Double.parseDouble(s_ponto_sigma_x);
            Double d_ponto_sigma_y = Double.parseDouble(s_ponto_sigma_y);
            envelope_hoffman.colocarPONTO(d_ponto_sigma_x, d_ponto_sigma_y);
        }

    }
//Outubro29
    public void construtor_envelope_christensen()
    {
        // O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
        envelope_christensen envelope_christensen = new envelope_christensen(
                getBaseContext(),
                nome_lamina,
                "",
                envelope_usado,
                getFilesDir().getAbsolutePath(),
                this,
                numero_pontos_global
        );

        if(angulo_global % 90 == 0)
        {
            listar_pontos_array=envelope_christensen.rotacao_pi_2(angulo_global,tau_xy);
        }
        else
        {
            listar_pontos_array=envelope_christensen.fazer_grafica(angulo_global,tau_xy);
        }

        if(origen.contentEquals("historico"))
        {
            Double d_ponto_sigma_x = Double.parseDouble(s_ponto_sigma_x);
            Double d_ponto_sigma_y = Double.parseDouble(s_ponto_sigma_y);
            envelope_christensen.colocarPONTO(d_ponto_sigma_x, d_ponto_sigma_y);
        }

    }
//Outubro30
    public void construtor_envelope_hashin()
    {
        //Log.d("numero_pontos_global","construtor_envelope_hashin  "+numero_pontos_global);
        // O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
        envelope_hashin envelope_hashin = new envelope_hashin(
                getBaseContext(),
                nome_lamina,
                "",
                envelope_usado,
                getFilesDir().getAbsolutePath(),
                this,
                numero_pontos_global
        );

        //listar_pontos_array=envelope_hashin.fazer_grafica(angulo_global,tau_xy);
        janela_coletar_TAU23_ultimo_valor_seekBar(angulo_global);

        if(origen.contentEquals("historico"))
        {
            Double d_ponto_sigma_x = Double.parseDouble(s_ponto_sigma_x);
            Double d_ponto_sigma_y = Double.parseDouble(s_ponto_sigma_y);
            envelope_hashin.colocarPONTO(d_ponto_sigma_x, d_ponto_sigma_y);
        }
    }


/////////////////////////////
public void janela_coletar_TAU23_ultimo_valor_seekBar(final Double angulo)
{
    AlertDialog.Builder aa=new AlertDialog.Builder(this);
//TITULO
    aa.setTitle("Por favor indique o TAU23");
// TEXT entrada
// Set up the input
    final EditText input = new EditText(this);
    input.setText("0.0");
    aa.setView(input);
//colocamos um valor tentativo de biaxial
// Se faz necesario colocar uma resistencia conforme o materil:
    DatabaseHelper dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
    try {
        dbHelper.prepareDatabase();
    } catch (IOException e) {
    }
    List<String> list = dbHelper.obter_propriedades_laminas(nome_lamina);

    String ajuda_TAU12=list.get(16);
    ajuda_TAU12=ajuda_TAU12.substring(ajuda_TAU12.indexOf(":")+1,ajuda_TAU12.length());

    input.setText(""+ajuda_TAU12);

    aa.setPositiveButton("Aceitar",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//Aqui deveria ativar a função GRAFICAR do Wu
                    String m_Text = input.getText().toString();
                    biaxial=Double.parseDouble(m_Text);

                    if(biaxial!=0)
                    {
                        refazer_grafico_hashin(angulo,tau_xy,biaxial);
                        //  Log.d("maio16","angulo \n"+angulo+"\ntau_xy \n"+tau_xy+"\nbiaxial\n"+biaxial);
                    }
                    else
                    {
                        alert_biaxial_nulo();
                    }

                }
            });

    aa.setNegativeButton("Cancelar",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
    aa.show();
}

//Outubro30
    public void refazer_grafico_hashin(Double angulo,Double tau_xy, Double biaxial)
    {
//No caso de Hashin, ele deve fazer mais um registro para colocar o parâmetro biaxial certo
        historico db_historico=new historico(this);
        db_historico.insert_envelope_paramestros_usados(
                nome_lamina
                ,envelope_usado,
                angulo+"",
                tau_xy+""
                ,"",
                ""+biaxial);

        envelope_hashin envelope_hashin = new envelope_hashin(
                getBaseContext(),
                nome_lamina,
                "",
                envelope_usado,
                getFilesDir().getAbsolutePath(),
                this,
                numero_pontos_global
        );

        //Log.d("Hashin","ENTRADA usando o parametroBIAXIAL  "+biaxial);

        listar_pontos_array=envelope_hashin.fazer_grafica(angulo,tau_xy,biaxial);

        visibilizar_quando_tiver_extremos();
    }

//Novembro01
    public void construtor_envelope_Larc03()
    {

        // O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
        envelope_larc03 envelope_larc03 = new envelope_larc03(
                getBaseContext(),
                nome_lamina,
                "",
                envelope_usado,
                getFilesDir().getAbsolutePath(),
                this,
                numero_pontos_global
        );

/*
        Log.d("Nov06","construtor_envelope_Larc03 global_larc03_numero_circulos "+global_larc03_numero_circulos);
        Log.d("Nov06","construtor_envelope_Larc03 global_larc03_alpha_0 "+global_larc03_alpha_0);
        Log.d("Nov06","construtor_envelope_Larc03 global_larc03_numero_elementos_circulos "+global_larc03_numero_elementos_circulos);
        Log.d("Nov06","construtor_envelope_Larc03 global_larc03_TAU23 "+global_larc03_TAU23);
        Log.d("Nov06","construtor_envelope_Larc03 global_larc03_Y_T_is "+global_larc03_Y_T_is);
        Log.d("Nov06","construtor_envelope_Larc03 global_larc03_S_L_is "+global_larc03_S_L_is);
*/

        listar_pontos_array=envelope_larc03.fazer_grafica(
                angulo_global,
                tau_xy,
                global_larc03_numero_circulos,
                global_larc03_alpha_0,
                global_larc03_numero_elementos_circulos,
                global_larc03_TAU23,
                global_larc03_Y_T_is,
                global_larc03_S_L_is
                );

        if(origen.contentEquals("historico"))
        {
            Double d_ponto_sigma_x = Double.parseDouble(s_ponto_sigma_x);
            Double d_ponto_sigma_y = Double.parseDouble(s_ponto_sigma_y);
            envelope_larc03.colocarPONTO(d_ponto_sigma_x, d_ponto_sigma_y);
        }
    }

//Novembro06
    public void construtor_envelope_puck()
    {

        // O construtor  é muito simples, ele NÂO COLOCA informações no DATABASE SQLite
        envelope_puck envelope_puck = new envelope_puck(
                getBaseContext(),
                nome_lamina,
                "",
                envelope_usado,
                getFilesDir().getAbsolutePath(),
                this,
                numero_pontos_global
        );

        listar_pontos_array=envelope_puck.fazer_grafica(
                angulo_global,
                tau_xy,
                global_puck_numero_circulos,
        global_puck_m_sigF,
        global_puck_numero_elementos_circulos,
        global_puck_p_plus_TL,
        global_puck_p_minus_TL,
        global_puck_p_minus_TT,
        global_puck_sigma_1_D,
        global_puck_puck_R_TT_A,
        global_puck_TAU12_C,
        global_puck_NU12_f,
        global_puck_E1_f
        );

        if(origen.contentEquals("historico"))
        {
            Double d_ponto_sigma_x = Double.parseDouble(s_ponto_sigma_x);
            Double d_ponto_sigma_y = Double.parseDouble(s_ponto_sigma_y);
            envelope_puck.colocarPONTO(d_ponto_sigma_x, d_ponto_sigma_y);
        }
    }

/////////////////////////////////////////////////////////////////////////////
}