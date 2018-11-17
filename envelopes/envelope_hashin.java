package br.com.ven2020.envelopes2018.envelopes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.com.ven2020.envelopes2018.Config;
import br.com.ven2020.envelopes2018.R;
import br.com.ven2020.envelopes2018.database.DatabaseHelper;
import br.com.ven2020.envelopes2018.database.historico;


/**
 * Created by julian on 14/10/17.
 * //marco15
 */

public class envelope_hashin {

    //Saídas
    private  ArrayList<Double> serie_1_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_1_y = new ArrayList<Double>();
    private  ArrayList<Double> serie_2_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_2_y = new ArrayList<Double>();

    //Minimo 4 series, Máximo 8
//POSPOS
    private  ArrayList<Double> serie_POSPOS_1_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_POSPOS_1_y = new ArrayList<Double>();
    private  ArrayList<Double> serie_POSPOS_2_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_POSPOS_2_y = new ArrayList<Double>();
//NEGPOS
    private  ArrayList<Double> serie_NEGPOS_1_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_NEGPOS_1_y = new ArrayList<Double>();
    private  ArrayList<Double> serie_NEGPOS_2_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_NEGPOS_2_y = new ArrayList<Double>();
//NEGNEG
    private  ArrayList<Double> serie_NEGNEG_1_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_NEGNEG_1_y = new ArrayList<Double>();
    private  ArrayList<Double> serie_NEGNEG_2_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_NEGNEG_2_y = new ArrayList<Double>();
//POSNEG
    private  ArrayList<Double> serie_POSNEG_1_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_POSNEG_1_y = new ArrayList<Double>();
    private  ArrayList<Double> serie_POSNEG_2_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_POSNEG_2_y = new ArrayList<Double>();

    private double menorX=1;
    private double maiorX=1;
    private double menorY=1;
    private double maiorY=1;

    private Context myContext;
    private Activity myActivity;

    private Float f_edit_textView_sigma_x;
    private Float f_edit_textView_sigma_y;
    private Float f_edit_textView_tau_xy;
    private Float f_edit_textView_angulo;

    private String lamina_usada="";
    private String criterio_usado="";
    private String envelope_usado="";

    private String endereco="";

    //String base_gnu="http://192.168.250.1/setembro/graficas_janeiro/diagramacao/gnu_registrar.php?identificador=";
    String base_gnu= Config.endereco_registrar_dados_gnu+"?identificador=";

    Double angulo_global=0.0;

    private int numero_pontos;

    Integer numeroPontos=Config.numeroPontos;

    public envelope_hashin(
            Context myContext, String lamina_usada,
            String criterio_usado, String envelope_usado,
            String endereco, Activity myActivity,int numero_pontos)
    {
        this.myContext=myContext;
        this.lamina_usada=lamina_usada;
        this.criterio_usado=criterio_usado;
        this.envelope_usado=envelope_usado;
        this.endereco=endereco;
        this.myActivity=myActivity;
        this.numero_pontos=numero_pontos;

        numeroPontos= Config.numeroPontos;

        if(numero_pontos<Config.numeroPontos*100)
        {
            numeroPontos=numero_pontos;
        }
    }

    public boolean verificar_entrada(
        String    s_edit_textView_sigma_x,
        String    s_edit_textView_sigma_y,
        String    s_edit_textView_tau_xy,
        String    s_edit_textView_angulo
    )
    {
        String sinalizacao_erro= "Não é possível calcular o Indíce de Falha porque o valor de ";
//sigma_x
        try{
            sinalizacao_erro=sinalizacao_erro.concat("sigma_x"+" não é do tipo NUMERICO");
            this.f_edit_textView_sigma_x= Float.parseFloat(s_edit_textView_sigma_x);
        }
        catch (Exception e) {
            Toast.makeText(myContext,sinalizacao_erro,Toast.LENGTH_LONG).show();
            return false;
        }
//sigma_y
        sinalizacao_erro= "Não é possível calcular o Indíce de Falha porque o valor de ";

        try{
            sinalizacao_erro=sinalizacao_erro.concat("sigma_y"+" não é do tipo NUMERICO");
            this.f_edit_textView_sigma_y= Float.parseFloat(s_edit_textView_sigma_y);
        }
        catch (Exception e) {
            Toast.makeText(myContext,sinalizacao_erro,Toast.LENGTH_LONG).show();
            return false;
        }
//tau_xy
        sinalizacao_erro= "Não é possível calcular o Indíce de Falha porque o valor de ";
        try{
            sinalizacao_erro=sinalizacao_erro.concat("tau_xy"+" não é do tipo NUMERICO");
            this.f_edit_textView_tau_xy= Float.parseFloat(s_edit_textView_tau_xy);
        }
        catch (Exception e) {
            Toast.makeText(myContext,sinalizacao_erro,Toast.LENGTH_LONG).show();
            return false;
        }
//Angulo
        sinalizacao_erro= "Não é possível calcular o Indíce de Falha porque o valor de ";
        try{
            sinalizacao_erro=sinalizacao_erro.concat("ângulo"+" não é do tipo NUMERICO");
            this.f_edit_textView_angulo= Float.parseFloat(s_edit_textView_angulo);
        }
        catch (Exception e) {
            Toast.makeText(myContext,sinalizacao_erro,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

/*
* Outubro 14
* */

    Double f_SIGMA_T_1;
    Double f_SIGMA_T_2;
    Double f_SIGMA_C_1;
    Double f_SIGMA_C_2;
    Double f_TAU12;

    double a;
    double c;
    double b;
    double d;
    double f;
    double g;

    double tauXY;

    Double[][] pares_pontos = new Double[this.numeroPontos][2];

    List<Double> pares_pontos_x = new ArrayList<>();
    List<Double> pares_pontos_y = new ArrayList<>();

    List<Double> pares_pontos_x_total = new ArrayList<>();
    List<Double> pares_pontos_y_total = new ArrayList<>();

    Double TAU23_global;


    public double[][] fazer_grafica(Double angulo, Double tau_xy,Double TAU23)
    {

        TAU23_global=TAU23;
        String agora=new Time(System.currentTimeMillis()).toString();

//Log no servidor
        angulo_global=angulo;

        historico db_historico=new historico(myContext);

        db_historico.insert_envelope_usado(
                this.envelope_usado,
                agora
        );

//Procuramos as propriedades da lâmina

        DatabaseHelper dbHelper = new DatabaseHelper(myContext, this.endereco);
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
        }

        List<String> list = dbHelper.obter_propriedades_laminas(this.lamina_usada);

        String SIGMA_T_1=list.get(12);//("SIGMA_T_1: "+cursor.getString(12));//SIGMA_T_1`	TEXT,
        String SIGMA_T_2=list.get(13);//add("SIGMA_T_2: "+cursor.getString(13));//SIGMA_T_1`	TEXT,
        String SIGMA_C_1=list.get(14); //add("SIGMA_C_1: "+cursor.getString(14));//SIGMA_C_1`	TEXT,
        String SIGMA_C_2=list.get(15); //("SIGMA_C_2: "+cursor.getString(15));//SIGMA_C_1`	TEXT,
        String TAU12=list.get(16); //"TAU12: "+cursor.getString(16));//TAU12`	TEXT,

        SIGMA_T_1=SIGMA_T_1.substring(SIGMA_T_1.indexOf(":")+1,SIGMA_T_1.length());
        SIGMA_T_2=SIGMA_T_2.substring(SIGMA_T_2.indexOf(":")+1,SIGMA_T_2.length());
        SIGMA_C_1=SIGMA_C_1.substring(SIGMA_C_1.indexOf(":")+1,SIGMA_C_1.length());
        SIGMA_C_2=SIGMA_C_2.substring(SIGMA_C_2.indexOf(":")+1,SIGMA_C_2.length());
        TAU12=TAU12.substring(TAU12.indexOf(":")+1,TAU12.length());

        f_SIGMA_T_1= Double.parseDouble(SIGMA_T_1);
        f_SIGMA_T_2= Double.parseDouble(SIGMA_T_2);
        f_SIGMA_C_1= Double.parseDouble(SIGMA_C_1);
        f_SIGMA_C_2= Double.parseDouble(SIGMA_C_2);
        f_TAU12= Double.parseDouble(TAU12);


        //Log.d("Outubro14",""+f_SIGMA_T_1);
        //Log.d("Outubro14",""+f_SIGMA_T_2);
        //Log.d("Outubro14",""+f_SIGMA_C_1);
        //Log.d("Outubro14",""+f_SIGMA_C_2);
        //Log.d("Outubro14",""+f_TAU12);
        //Log.d("angulo",""+angulo);


        GraphView graph = this.myActivity.findViewById(R.id.graph);

//GRAFICA LEGENDA ESCALA
        ///Outubto14

        // custom label formatter to show currency "EUR"
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    String transformado=String.format("%2.2e",value);
                            //super.formatLabel(value, isValueX);

                    if(value==0)
                    {
                        return transformado+ " (Pa)";
                    }
                    else
                    {
                        return "";
                    }

                } else {
                    // show currency for y values

                    String transformado=String.format("%2.2e",value);
                    return transformado + " (Pa)";

                }
            }
        });


        graph.setTitle("Plano (sigma_x,sigma_y)\nParâmetro tau_xy="+tau_xy);
        graph.removeAllSeries();

/*
* Numerico Puro

* Numerico Puro

* Numerico Puro

* Outubro17
* */

//
// Outubro29 usamos o TAU XY que por default será um zero.

        tauXY=tau_xy;


        // COORDENADAS globais:

        // os efeitos da rotação da lamina:
        // daqui pa frente COORDENADAS GLOBAIS
        double  theta_radianos=(Math.PI/180)*angulo;


        double cos=Math.cos(theta_radianos);
        double sin=Math.sin(theta_radianos);

//Outubro29 Construindo os semi-planos
//Outubro29 Novo esquema
    if((angulo % 90)==0)
    {
        String comportamento=calcular_apenas_4_vertices(angulo,tauXY);
        if(!comportamento.contains("raiz_imaginaria"))
        {
            ordenar_quatro_pontos();
            usar_GraphView();

            double[][] saida = new double[4][2];

            int indice_saida=0;

            for(int i=0;i<pares_pontos_x.size();i++)
            {
                saida[indice_saida][0]=pares_pontos_x.get(indice_saida);
                saida[indice_saida][1]=pares_pontos_y.get(indice_saida);
                indice_saida++;
            }

            return saida;

        }
        else
        {
            double saida_nula[][]={{0,0},{0,0}};
            return saida_nula;
        }
    }
    else {
        gerador_TRACAO_FIBRA(cos, sin);

        //Antes de vetorizar deve TOTALIZAR
        if (pares_pontos_x.size() > 0) {
            for (Integer i = 0; i < pares_pontos_x.size(); i++) {
                pares_pontos_x_total.add(pares_pontos_x.get(i));
                pares_pontos_y_total.add(pares_pontos_y.get(i));
            }

//Outubro 19, 2018
            //filtro_distancia();

            vetorizar_duas_series_LISTA(
                    pares_pontos_x, pares_pontos_y,
                    serie_POSPOS_1_x, serie_POSPOS_1_y);

            fazer_segunda_serie_LISTA(pares_pontos_x, pares_pontos_y, serie_POSPOS_2_x, serie_POSPOS_2_y);
        }

//Outubro29 Construindo os semi-planos
//Outubro29 Novo esquema
//2) Compressão na fibra tratado como um //NEGPOS
        //Log.d("Outubro30","gerador_COMPRESSAO_FIBRA");

        gerador_COMPRESSAO_FIBRA(cos, sin);


        if (pares_pontos_x.size() > 0) {
            for (Integer i = 0; i < pares_pontos_x.size(); i++) {
                pares_pontos_x_total.add(pares_pontos_x.get(i));
                pares_pontos_y_total.add(pares_pontos_y.get(i));
            }
            //filtro_distancia();

            vetorizar_duas_series_LISTA(
                    pares_pontos_x, pares_pontos_y,
                    serie_NEGPOS_1_x, serie_NEGPOS_1_y);

            fazer_segunda_serie_LISTA(pares_pontos_x, pares_pontos_y, serie_NEGPOS_2_x, serie_NEGPOS_2_y);
        }

//Outubro29 Construindo os semi-planos
//Outubro29 Novo esquema
//3) Compressão na Matriz tratado como um //NEGNEG
        //Log.d("Outubro30","gerador_COMPRESSAO_MATRIZ");

        gerador_COMPRESSAO_MATRIZ(cos, sin);

        if (pares_pontos_x.size() > 0) {
            for (Integer i = 0; i < pares_pontos_x.size(); i++) {
                pares_pontos_x_total.add(pares_pontos_x.get(i));
                pares_pontos_y_total.add(pares_pontos_y.get(i));
            }
            //filtro_distancia();

            vetorizar_duas_series_LISTA(
                    pares_pontos_x, pares_pontos_y,
                    serie_NEGNEG_1_x, serie_NEGNEG_1_y);

            fazer_segunda_serie_LISTA(pares_pontos_x, pares_pontos_y, serie_NEGNEG_2_x, serie_NEGNEG_2_y);
        }
//Outubro29 Construindo os semi-planos
//Outubro29 Novo esquema
//4) Compressão na Matriz tratado como um //POSNEG
        //Log.d("Outubro30","gerador_TRACAO_MATRIZ");

        gerador_TRACAO_MATRIZ(cos, sin);

        if (pares_pontos_x.size() > 0) {
            for (Integer i = 0; i < pares_pontos_x.size(); i++) {
                pares_pontos_x_total.add(pares_pontos_x.get(i));
                pares_pontos_y_total.add(pares_pontos_y.get(i));
            }
            //filtro_distancia();

            vetorizar_duas_series_LISTA(
                    pares_pontos_x, pares_pontos_y,
                    serie_POSNEG_1_x, serie_POSNEG_1_y);

            fazer_segunda_serie_LISTA(pares_pontos_x, pares_pontos_y, serie_POSNEG_2_x, serie_POSNEG_2_y);
        }
///Fim do else de ANGULO % 90
    }

        //componemos a saída
        double[][] saida = new double[4][2];
//Colocamos apenas os valores extremos

//maximo X
        int indice_temp_extremo=0;
        int indice_saida=0;
        double temp=0.0;
        for(int i=0;i<pares_pontos_x_total.size();i++)
        {
            if(temp<=pares_pontos_x_total.get(i)) //em X
            {
                indice_temp_extremo=i;
                temp=pares_pontos_x_total.get(i);
            }
        }
        maiorX=pares_pontos_x_total.get(indice_temp_extremo);
        saida[indice_saida][0]=pares_pontos_x_total.get(indice_temp_extremo);
        saida[indice_saida][1]=pares_pontos_y_total.get(indice_temp_extremo);
        indice_saida++;

//Minimo X
        indice_temp_extremo=0;
        temp=0;
        for(int i=0;i<pares_pontos_x_total.size();i++)
        {
            if(temp>=pares_pontos_x_total.get(i)) //em X
            {
                indice_temp_extremo=i;
                temp=pares_pontos_x_total.get(i);
            }
        }
        menorX=pares_pontos_x_total.get(indice_temp_extremo);
        saida[indice_saida][0]=pares_pontos_x_total.get(indice_temp_extremo);
        saida[indice_saida][1]=pares_pontos_y_total.get(indice_temp_extremo);
        indice_saida++;

//Maximo Y
        indice_temp_extremo=0;
        temp=0;
        for(int i=0;i<pares_pontos_y_total.size();i++)
        {
            if(temp<=pares_pontos_y_total.get(i)) //em Y
            {
                indice_temp_extremo=i;
                temp=pares_pontos_y_total.get(i);
            }
        }

        maiorY=pares_pontos_y_total.get(indice_temp_extremo);

        saida[indice_saida][0]=pares_pontos_x_total.get(indice_temp_extremo);
        saida[indice_saida][1]=pares_pontos_y_total.get(indice_temp_extremo);
        indice_saida++;
//Minimo Y
        indice_temp_extremo=0;
        temp=0;
        for(int i=0;i<pares_pontos_y_total.size();i++)
        {
            if(temp>=pares_pontos_y_total.get(i)) //em Y
            {
                indice_temp_extremo=i;
                temp=pares_pontos_y_total.get(i);
            }
        }

        menorY=pares_pontos_y_total.get(indice_temp_extremo);

        saida[indice_saida][0]=pares_pontos_x_total.get(indice_temp_extremo);
        saida[indice_saida][1]=pares_pontos_y_total.get(indice_temp_extremo);
        indice_saida++;

        /*Outubro 29 Estranho

        for(int i=0; i<pares_pontos_x_total.size();i++)
        {
            Log.d("O29","  "+i+"  "+pares_pontos_x_total.get(i));
        }
        */

////////////////// Fim dos extremos

       //vetorizar_duas_series(pares_pontos);

        LineGraphSeries<DataPoint> serie_POSPOS_1_graph=new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> serie_POSPOS_2_graph=new LineGraphSeries<DataPoint>();

        LineGraphSeries<DataPoint> serie_NEGPOS_1_graph=new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> serie_NEGPOS_2_graph=new LineGraphSeries<DataPoint>();

        LineGraphSeries<DataPoint> serie_NEGNEG_1_graph=new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> serie_NEGNEG_2_graph=new LineGraphSeries<DataPoint>();

        LineGraphSeries<DataPoint> serie_POSNEG_1_graph=new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> serie_POSNEG_2_graph=new LineGraphSeries<DataPoint>();

//POSPOS
        if(serie_POSPOS_1_x.size()>0)
        {
            //Log.d("Outubro31","serie_POSPOS_1_x  "+serie_POSPOS_1_x.size());

            for(Integer i=0;i<serie_POSPOS_1_x.size(); i++)
            {
                serie_POSPOS_1_graph.appendData(new DataPoint(serie_POSPOS_1_x.get(i), serie_POSPOS_1_y.get(i)), true, 100);
            }
            graph.addSeries(serie_POSPOS_1_graph);
        }

        if(serie_POSPOS_2_x.size()>0)
        {
            //Log.d("Outubro31","serie_POSPOS_2_x  "+serie_POSPOS_2_x.size());

            for(Integer i=0;i<serie_POSPOS_2_x.size(); i++)
            {
                serie_POSPOS_2_graph.appendData(new DataPoint(serie_POSPOS_2_x.get(i), serie_POSPOS_2_y.get(i)), true, 100);
            }
            graph.addSeries(serie_POSPOS_2_graph);
        }
//NEGPOS
        if(serie_NEGPOS_1_x.size()>0)
        {
            //Log.d("Outubro31","serie_NEGPOS_1_x  "+serie_NEGPOS_1_x.size());

            for(Integer i=0;i<serie_NEGPOS_1_x.size(); i++)
            {
                serie_NEGPOS_1_graph.appendData(new DataPoint(serie_NEGPOS_1_x.get(i), serie_NEGPOS_1_y.get(i)), true, 100);
            }
            graph.addSeries(serie_NEGPOS_1_graph);
        }

        if(serie_NEGPOS_2_x.size()>0)
        {
            for(Integer i=0;i<serie_NEGPOS_2_x.size(); i++)
            {
                serie_NEGPOS_2_graph.appendData(new DataPoint(serie_NEGPOS_2_x.get(i), serie_NEGPOS_2_y.get(i)), true, 100);
            }
            graph.addSeries(serie_NEGPOS_2_graph);
        }
//NEGNEG
        if(serie_NEGNEG_1_x.size()>0)
        {
            for(Integer i=0;i<serie_NEGNEG_1_x.size(); i++)
            {
                serie_NEGNEG_1_graph.appendData(new DataPoint(serie_NEGNEG_1_x.get(i), serie_NEGNEG_1_y.get(i)), true, 100);
            }
            graph.addSeries(serie_NEGNEG_1_graph);
        }

        if(serie_NEGNEG_2_x.size()>0)
        {
            for(Integer i=0;i<serie_NEGNEG_2_x.size(); i++)
            {
                serie_NEGNEG_2_graph.appendData(new DataPoint(serie_NEGNEG_2_x.get(i), serie_NEGNEG_2_y.get(i)), true, 100);
            }
            graph.addSeries(serie_NEGNEG_2_graph);
        }
//POSNEG
        if(serie_POSNEG_1_x.size()>0)
        {
            for(Integer i=0;i<serie_POSNEG_1_x.size(); i++)
            {
                serie_POSNEG_1_graph.appendData(new DataPoint(serie_POSNEG_1_x.get(i), serie_POSNEG_1_y.get(i)), true, 100);
            }
            graph.addSeries(serie_POSNEG_1_graph);
        }

        if(serie_POSNEG_2_x.size()>0)
        {

            for(Integer i=0;i<serie_NEGNEG_2_x.size(); i++)
            {
                serie_POSNEG_2_graph.appendData(new DataPoint(serie_POSNEG_2_x.get(i), serie_POSNEG_2_y.get(i)), true, 100);
            }
            graph.addSeries(serie_POSNEG_2_graph);
        }

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        //graph.getViewport().setMinX(-1*f_SIGMA_T_1);
        graph.getViewport().setMinX(menorX*1.2);
        //graph.getViewport().setMaxX(1*f_SIGMA_T_1);
        graph.getViewport().setMaxX(maiorX*1.2);

        //graph.getViewport().setMinY(-1*f_SIGMA_T_1);
        graph.getViewport().setMinY(menorY*1.5);
        //graph.getViewport().setMaxY(1*f_SIGMA_T_1);
        graph.getViewport().setMaxY(maiorY*1.5);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.clearSecondScale();
//??? gnu
        return saida;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Outubro 26
    public void colocarPONTO(Double x,Double y)
    {
        //Log.d("outubro28","Muito legal\n"+ x+ y);
        GraphView graph = this.myActivity.findViewById(R.id.graph);

        LineGraphSeries<DataPoint> series_0 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(x, y)
        });

        series_0.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(myActivity, "Esforço antigo: "+dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        series_0.setColor(Color.GREEN);
        series_0.setDrawDataPoints(true);
        series_0.setDataPointsRadius(10);
        series_0.setThickness(8);

        graph.addSeries(series_0);

    }
    public void APAGARcolocarPONTO(Double angulo)
    {
        //Log.d("outubro28","Muito legal\n");
        GraphView graph = this.myActivity.findViewById(R.id.graph);
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

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setMinX(-2);
        graph.getViewport().setMaxX(2);

        graph.getViewport().setMinY(-2);
        graph.getViewport().setMaxY(2);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        series_0.setTitle("Random Curve 1");
        series_0.setColor(Color.GREEN);
        series_0.setDrawDataPoints(true);
        series_0.setDataPointsRadius(10);
        series_0.setThickness(8);

        graph.addSeries(series_0);
        graph.addSeries(series_1);

        graph.clearSecondScale();

    }



    // ///////////////////////////////////////////////////////////////////////////////////////
     public String existeENVELOPE()
     {
         return ""+this.lamina_usada+""+this.criterio_usado;
         /*
         this.envelope_usado,
                 this.f_edit_textView_sigma_x,
                 this.f_edit_textView_sigma_y,
                 this.f_edit_textView_tau_xy,
                 this.f_edit_textView_angulo,
                 */
     }

    // inversa da cotangente
    public double Inverse_cotan(double x){
        if (x<0)
        {
            return -(Math.PI/2)-Math.atan(x);
        }
        else{
            return (Math.PI/2)-Math.atan(x);
        }
    }


//maio17
public void calcular_coeficientesPOSNEG(double cos,double sin)
{
    //  a*x²+c*y²+2bxy

//No quandrante (-,-) este valor deve ser de Tração

    double Quadrado_t1=(1/f_SIGMA_T_1)*(1/f_SIGMA_T_1);

    double Quadrado_tc=(1/f_SIGMA_C_1)*(1/f_SIGMA_C_1);

//No quandrante (-,-) este valor deve ser de compressão
    double Quadrado_t2=(1/f_SIGMA_C_2)*(1/f_SIGMA_C_2);
    double Quadrado_t12=(1/f_TAU12)*(1/f_TAU12);

    // sigma_X^2

/*

a=
cos^{4}  t_{1}^{2}
+ cos^{2} sin^{2} t_{12}^{2}
- cos^{2}  sin^{2} tc^{2}
+ \sigma_{x}^{2} sin^{4} t_{2}^{2}
\\
*/

    double fatorRotar_a=
            Math.pow(cos,4)*Quadrado_t1 //cos^{4} \sigma_{x}^{2} t_{1}^{2}
                    + Math.pow(sin,4)*Quadrado_t2 //+ \sigma_{x}^{2} sin^{4} t_{2}^{2}
                    - Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_tc  //- cos^{2} \sigma_{x}^{2} sin^{2} tc^{2}
                    + Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12; //+ cos^{2} \sigma_{x}^{2} sin^{2} t_{12}^{2}


    //   sigma_Y^2
/*
c=
+ cos^{4}  t_{2}^{2}
+ cos^{2}  sin^{2} t_{12}^{2}
- cos^{2}  sin^{2} tc^{2}
+  sin^{4} t_{1}^{2}
*/

    double fatorRotar_c=
            Math.pow(sin,4)*Quadrado_t1  			//+ \sigma_{y}^{2} sin^{4} t_{1}^{2}
                    + Math.pow(cos,4)*Quadrado_t2 			//+ cos^{4} \sigma_{y}^{2} t_{2}^{2}
                    - Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_tc 	//- cos^{2} \sigma_{y}^{2} sin^{2} tc^{2}
                    + Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12; //+ cos^{2} sin^{2} t_{12}^{2}

    //   sigma_Y*sigma_X

/*

2b=
 + 2 cos^{2} \sigma_{x} \sigma_{y} sin^{2} t_{1}^{2}
- 2 cos^{2} \sigma_{x} \sigma_{y} sin^{2} t_{12}^{2}
+ 2 cos^{2} \sigma_{x} \sigma_{y} sin^{2} t_{2}^{2}
- cos^{4} \sigma_{x} \sigma_{y} tc^{2}
- \sigma_{x} \sigma_{y} sin^{4} tc^{2}
*/

    double fatorRotar_b=
            - Math.pow(cos,4)*Quadrado_tc			   //- cos^{4}tc^{2}
                    + 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1  // + 2 cos^{2}  sin^{2} t_{1}^{2}
                    - 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12 //- 2 cos^{2}  sin^{2} t_{12}^{2}
                    - Math.pow(sin,4)*Quadrado_tc			   //- sin^{4} tc^{2}
                    + 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t2; //+ 2 cos^{2}  sin^{2} t_{2}^{2}

    // No caso de materiais isotropicos, ou transversalmente isotropicos temos que ajustar um pouco,
    // A representa será mudada apenas um pouco, para evitar divisão por zero.

    //  a*x²+c*y²+2bxy
    //Usando a biblioteca gráfica:

//////////////////////////// Agora as potencias SIMPLES

//  sigmaX, colocamos o 2d
/*

2d=
+ 4 cos^{3} \sigma_{x} sin t_{1}^{2} \tau_{xy}
- 2 cos^{3} \sigma_{x} sin t_{12}^{2} \tau_{xy}
+ 2 cos^{3} \sigma_{x} sin \tau_{xy} tc^{2}
+ 2 cos \sigma_{x} sin^{3} t_{12}^{2} \tau_{xy}
- 4 cos \sigma_{x} sin^{3} t_{2}^{2} \tau_{xy}
- 2 cos \sigma_{x} sin^{3} \tau_{xy} tc^{2}
*/

    double fatorRotar_d=
            +4*Math.pow(cos,3)*sin*Quadrado_t1*tauXY  		//+ 4 cos^{3} sin t_{1}^{2} \tau_{xy}
                    + 2*sin*Math.pow(cos,3)*Quadrado_tc*tauXY		//+ 2 cos^{3}  sin \tau_{xy} tc^{2}
                    - 2*Math.pow(cos,3)*sin*Quadrado_t12*tauXY		//- 2 cos^{3} sin t_{12}^{2} \tau_{xy}
                    - 2*cos*Math.pow(sin,3)*Quadrado_tc*tauXY 		//- 2 cos  sin^{3} \tau_{xy} tc^{2}
                    + 2*cos*Math.pow(sin,3)*Quadrado_t12*tauXY 		//+ 2 cos  sin^{3} t_{12}^{2} \tau_{xy}
                    - 4*cos*Math.pow(sin,3)*Quadrado_t2*tauXY; 		//- 4 cos  sin^{3} t_{2}^{2} \tau_{xy}

/*

2f=
+ 2 cos^{3} sin t_{12}^{2} \tau_{xy}
*/

    double fatorRotar_f=
            2*Math.pow(cos,3)*sin*Quadrado_t12*tauXY 	//+ 2 cos^{3} \sigma_{y} sin t_{12}^{2} \tau_{xy}
                    - 4*Math.pow(cos,3)*sin*Quadrado_t2*tauXY	//- 4 cos^{3} \sigma_{y} sin t_{2}^{2} \tau_{xy}
                    - 2*Math.pow(cos,3)*sin*Quadrado_tc*tauXY	//- 2 cos^{3} \sigma_{y} sin \tau_{xy} tc^{2}
                    + 4*cos*Math.pow(sin,3)*Quadrado_t1*tauXY	//+ 4 cos \sigma_{y} sin^{3} t_{1}^{2} \tau_{xy}
                    + 2*cos*Math.pow(sin,3)*Quadrado_tc*tauXY	//+ 2 cos \sigma_{y} sin^{3} \tau_{xy} tc^{2}
                    - 2*cos*Math.pow(sin,3)*Quadrado_t12*tauXY;	//- 2 cos \sigma_{y} sin^{3} t_{12}^{2} \tau_{xy}

/*

g=-1


*/

    double factorRotar_g=
            4*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1 		//+ 4 cos^{2} sin^{2} t_{1}^{2} \tau_{xy}^{2}
                    +4*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_tc		//+ 4 cos^{2} sin^{2} \tau_{xy}^{2} tc^{2}
                    - 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12	//- 2 cos^{2} sin^{2} t_{12}^{2} \tau_{xy}^{2}
                    + 4*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t2		//+ 4 cos^{2} sin^{2} t_{2}^{2} \tau_{xy}^{2}
                    + Math.pow(cos,4)*Quadrado_t12		//+ cos^{4} t_{12}^{2} \tau_{xy}^{2}
                    + Math.pow(sin,4)*Quadrado_t12;		//+ sin^{4} t_{12}^{2} \tau_{xy}^{2}


    a=fatorRotar_a;
    c=fatorRotar_c;

    b=(fatorRotar_b/2);
    d=(fatorRotar_d/2);
    f=(fatorRotar_f/2);

    g=-1+factorRotar_g*(tauXY*tauXY);
}


//maio17
public void calcular_coeficientesNEGNEG(double cos,double sin)
{
    //  a*x²+c*y²+2bxy

//No quandrante (-,-) este valor deve ser de compressão

    double Quadrado_t1=(1/f_SIGMA_C_1)*(1/f_SIGMA_C_1);

//No quandrante (-,-) este valor deve ser de compressão

    double Quadrado_t2=(1/f_SIGMA_C_2)*(1/f_SIGMA_C_2);
    double Quadrado_t12=(1/f_TAU12)*(1/f_TAU12);

    // sigma_X^2

/*

a=
+c^{4} \sigma_{x}^{2} t_{1}^{2}
+ s^{4} \sigma_{x}^{2} t_{2}^{2}
- c^{2} s^{2} \sigma_{x}^{2} t_{1}^{2}
+ c^{2} s^{2} \sigma_{x}^{2} t_{12}^{2}
\\
*/

    double fatorRotar_a=Math.pow(cos,4)*Quadrado_t1
            + Math.pow(sin,4)*Quadrado_t2
            - Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1
            + Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;


    //   sigma_Y^2
/*
c=
+ s^{4} \sigma_{y}^{2} t_{1}^{2}
+ c^{4} \sigma_{y}^{2} t_{2}^{2}
- c^{2} s^{2} \sigma_{y}^{2} t_{1}^{2}
+ c^{2} s^{2} \sigma_{y}^{2} t_{12}^{2}
*/

    double fatorRotar_c=Math.pow(sin,4)*Quadrado_t1
            + Math.pow(cos,4)*Quadrado_t2
            - Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1
            + Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;

    //   sigma_Y*sigma_X

/*

2b=
- c^{4} \sigma_{x} \sigma_{y} t_{1}^{2}
+ 2 c^{2} s^{2} \sigma_{x} \sigma_{y} t_{1}^{2}
- 2 c^{2} s^{2} \sigma_{x} \sigma_{y} t_{12}^{2}
\\
- s^{4} \sigma_{x} \sigma_{y} t_{1}^{2}
+ 2 c^{2} s^{2} \sigma_{x} \sigma_{y} t_{2}^{2}
*/

    double fatorRotar_b=- Math.pow(cos,4)*Quadrado_t1
            + 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1
            - 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12
            - Math.pow(sin,4)*Quadrado_t1
            + 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t2;

    // No caso de materiais isotropicos, ou transversalmente isotropicos temos que ajustar um pouco,
    // A representa será mudada apenas um pouco, para evitar divisão por zero.

    //  a*x²+c*y²+2bxy
    //Usando a biblioteca gráfica:

//////////////////////////// Agora as potencias SIMPLES

//  sigmaX, colocamos o 2d
/*

2d=
+ 6 Math.pow(cos,3)*sin*Quadrado_t1 *tauXY
- 2 Math.pow(cos,3)*sin*Quadrado_t12 *tauXY
- 2*c*Math.pow(sin,3)  Quadrado_t1 *tauXY
\\
+ 2*c*Math.pow(sin,3)  Quadrado_t12 *tauXY
- 4*c*Math.pow(sin,3)  Quadrado_t2 *tauXY
*/

    double fatorRotar_d=6*Math.pow(cos,3)*sin*Quadrado_t1*tauXY
            - 2*Math.pow(cos,3)*sin*Quadrado_t12*tauXY
            - 2*cos*Math.pow(sin,3)*Quadrado_t1*tauXY
            + 2*cos*Math.pow(sin,3)*Quadrado_t12*tauXY
            - 4*cos*Math.pow(sin,3)*Quadrado_t2*tauXY;

/*


2f=
+ 2 Math.pow(cos,3)*sin*Quadrado_t12 *tauXY
- 4 Math.pow(cos,3)*sin*Quadrado_t2 *tauXY
- 2 Math.pow(cos,3)*sin*Quadrado_t1 *tauXY
\\
+ 6*c*Math.pow(sin,3)  Quadrado_t1 *tauXY
- 2*c*Math.pow(sin,3)  Quadrado_t12 *tauXY

*/

    double fatorRotar_f=2*Math.pow(cos,3)*sin*Quadrado_t12*tauXY
            - 4*Math.pow(cos,3)*sin*Quadrado_t2*tauXY
            - 2*Math.pow(cos,3)*sin*Quadrado_t1*tauXY
            + 6*cos*Math.pow(sin,3)*Quadrado_t1*tauXY
            - 2*cos*Math.pow(sin,3)*Quadrado_t12*tauXY;

/*

g=-1
+ 8 Math.pow(cos,2) Math.pow(sin,2) Quadrado_t1 *tauXY^{2}
- 2 Math.pow(cos,2) Math.pow(sin,2) Quadrado_t12 *tauXY^{2}
+ 4 Math.pow(cos,2) Math.pow(sin,2) Quadrado_t2 *tauXY^{2}
\\
+ Math.pow(cos,4) Quadrado_t12 *tauXY^{2}
+ Math.pow(sin,4) Quadrado_t12 *tauXY^{2}


*/

    double factorRotar_g=8*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1
            - 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12
            + 4*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t2
            + Math.pow(cos,4)*Quadrado_t12
            + Math.pow(sin,4)*Quadrado_t12;


    a=fatorRotar_a;
    c=fatorRotar_c;

    b=(fatorRotar_b/2);
    d=(fatorRotar_d/2);
    f=(fatorRotar_f/2);

    g=-1+factorRotar_g*(tauXY*tauXY);
}


    //maio16
public void calcular_coeficientesNEGPOS(double cos,double sin)
{
    //  a*x²+c*y²+2bxy

//No quandrante (-,+) este valor deve ser de compressão

    double Quadrado_t1=(1/f_SIGMA_C_1)*(1/f_SIGMA_C_1);
    double Quadrado_t2=(1/f_SIGMA_T_2)*(1/f_SIGMA_T_2);
    double Quadrado_t12=(1/f_TAU12)*(1/f_TAU12);

    // sigma_X^2

/*

a=
+c^{4} \sigma_{x}^{2} t_{1}^{2}
+ s^{4} \sigma_{x}^{2} t_{2}^{2}
- c^{2} s^{2} \sigma_{x}^{2} t_{1}^{2}
+ c^{2} s^{2} \sigma_{x}^{2} t_{12}^{2}
\\
*/

    double fatorRotar_a=Math.pow(cos,4)*Quadrado_t1
            + Math.pow(sin,4)*Quadrado_t2
            - Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1
            + Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;


    //   sigma_Y^2
/*
c=
+ s^{4} \sigma_{y}^{2} t_{1}^{2}
+ c^{4} \sigma_{y}^{2} t_{2}^{2}
- c^{2} s^{2} \sigma_{y}^{2} t_{1}^{2}
+ c^{2} s^{2} \sigma_{y}^{2} t_{12}^{2}
*/

    double fatorRotar_c=Math.pow(sin,4)*Quadrado_t1
            + Math.pow(cos,4)*Quadrado_t2
            - Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1
            + Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;

    //   sigma_Y*sigma_X

/*

2b=
- c^{4} \sigma_{x} \sigma_{y} t_{1}^{2}
+ 2 c^{2} s^{2} \sigma_{x} \sigma_{y} t_{1}^{2}
- 2 c^{2} s^{2} \sigma_{x} \sigma_{y} t_{12}^{2}
\\
- s^{4} \sigma_{x} \sigma_{y} t_{1}^{2}
+ 2 c^{2} s^{2} \sigma_{x} \sigma_{y} t_{2}^{2}
*/

    double fatorRotar_b=- Math.pow(cos,4)*Quadrado_t1
            + 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1
            - 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12
            - Math.pow(sin,4)*Quadrado_t1
            + 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t2;

    // No caso de materiais isotropicos, ou transversalmente isotropicos temos que ajustar um pouco,
    // A representa será mudada apenas um pouco, para evitar divisão por zero.

    //  a*x²+c*y²+2bxy
    //Usando a biblioteca gráfica:

//////////////////////////// Agora as potencias SIMPLES

//  sigmaX, colocamos o 2d
/*

2d=
+ 6 Math.pow(cos,3)*sin*Quadrado_t1 *tauXY
- 2 Math.pow(cos,3)*sin*Quadrado_t12 *tauXY
- 2*c*Math.pow(sin,3)  Quadrado_t1 *tauXY
\\
+ 2*c*Math.pow(sin,3)  Quadrado_t12 *tauXY
- 4*c*Math.pow(sin,3)  Quadrado_t2 *tauXY
*/

    double fatorRotar_d=6*Math.pow(cos,3)*sin*Quadrado_t1*tauXY
            - 2*Math.pow(cos,3)*sin*Quadrado_t12*tauXY
            - 2*cos*Math.pow(sin,3)*Quadrado_t1*tauXY
            + 2*cos*Math.pow(sin,3)*Quadrado_t12*tauXY
            - 4*cos*Math.pow(sin,3)*Quadrado_t2*tauXY;

/*


2f=
+ 2 Math.pow(cos,3)*sin*Quadrado_t12 *tauXY
- 4 Math.pow(cos,3)*sin*Quadrado_t2 *tauXY
- 2 Math.pow(cos,3)*sin*Quadrado_t1 *tauXY
\\
+ 6*c*Math.pow(sin,3)  Quadrado_t1 *tauXY
- 2*c*Math.pow(sin,3)  Quadrado_t12 *tauXY

*/

    double fatorRotar_f=2*Math.pow(cos,3)*sin*Quadrado_t12*tauXY
            - 4*Math.pow(cos,3)*sin*Quadrado_t2*tauXY
            - 2*Math.pow(cos,3)*sin*Quadrado_t1*tauXY
            + 6*cos*Math.pow(sin,3)*Quadrado_t1*tauXY
            - 2*cos*Math.pow(sin,3)*Quadrado_t12*tauXY;

/*

g=-1
+ 8 Math.pow(cos,2) Math.pow(sin,2) Quadrado_t1 *tauXY^{2}
- 2 Math.pow(cos,2) Math.pow(sin,2) Quadrado_t12 *tauXY^{2}
+ 4 Math.pow(cos,2) Math.pow(sin,2) Quadrado_t2 *tauXY^{2}
\\
+ Math.pow(cos,4) Quadrado_t12 *tauXY^{2}
+ Math.pow(sin,4) Quadrado_t12 *tauXY^{2}


*/

    double factorRotar_g=8*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1
            - 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12
            + 4*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t2
            + Math.pow(cos,4)*Quadrado_t12
            + Math.pow(sin,4)*Quadrado_t12;


    a=fatorRotar_a;
    c=fatorRotar_c;

    b=(fatorRotar_b/2);
    d=(fatorRotar_d/2);
    f=(fatorRotar_f/2);

    g=-1+factorRotar_g*(tauXY*tauXY);
}


//maio16
    public void gerar_pontos()
    {

        pares_pontos_x.clear();
        pares_pontos_y.clear();
        // no caso isotropico Poupamos calculo e fazemos apenas o estudo polar:
        double rotacionado=0.0;
        double x;
        //Sendo preciso agora usar a formulação mais explicita

        if(b!=0)
        {
            double B=2*b;
            double D=2*d;
            double E=2*f;

            double A=a;
            double C=c;

            double numerador=(C-A-Math.sqrt(Math.pow(A-C,2)+B*B));

            rotacionado=Math.atan(numerador/B);
        }
        // vemos os cumprimentos dos eixos principais desta elipse:
        // cumprimento dos eixos, usando todos os coeficientes:	try:
        double raioMaior=Math.sqrt((2*(a*f*f+c*d*d+g*b*b-2*b*d*f-1*a*c*g))/((b*b-a*c)*(Math.sqrt(Math.pow((a-c),2)+4*b*b)-(a+c))));
        double raioMenor=Math.sqrt((2*(a*f*f+c*d*d+g*b*b-2*b*d*f-1*a*c*g))/((b*b-a*c)*(-Math.sqrt(Math.pow((a-c),2)+4*b*b)-(a+c))));
        // Origem
        double origem_x=(c*d-b*f)/(b*b-a*c);
        double origem_y=(a*f-b*d)/(b*b-a*c);

        double r;
        double y;

        double temporal_x;
        double temporal_y;

/*
*
* Parte SUPEIOR do gráfico
*
* */

        //PointsGraphSeries<DataPoint> series_superior =new PointsGraphSeries<DataPoint>();

        //pares_pontos = new Double[numeroPontos][2];

//Fazemos a varredura de Izquerda para DIREITA , desde 0 para 2*Pi

        double ang=0;

        double passo=(2*Math.PI/numeroPontos);

        //rotacionado=0;
        double coseno;
        double seno;

        for(Integer i=0;i<numeroPontos;i++)
        {
            coseno=Math.cos(ang);
            seno=Math.sin(ang);
            r=(raioMaior*raioMenor)/(Math.sqrt(raioMaior*raioMaior*seno*seno+raioMenor*raioMenor*coseno*coseno));
            x=r*coseno;
            y=r*seno;

            // aplicamos uma rotação de euler:
            temporal_x=x*Math.cos(rotacionado)-y*Math.sin(rotacionado);
            temporal_y=x*Math.sin(rotacionado)+y*Math.cos(rotacionado);
            // Origem
            if (origem_x!=0){
                temporal_x=temporal_x+origem_x;
            }
            if (origem_y!=0){
                temporal_y=temporal_y+origem_y;
            }

            //Log.d("Outubro29","Ângulo: ("+i+")"+ang+"   ");
            ang=ang+passo;
//melhora
            //pares_pontos[i][0]=temporal_x;
            pares_pontos_x.add(temporal_x);
            //pares_pontos[i][1]=temporal_y;
            pares_pontos_y.add(temporal_y);
        }
//////////
    }
//Maio17
public void filtro_calculo_conica_POSPOS(double angulo,double tau_xy)
{

//Transformando para coordenadas LOCAIS

    List<Double> pares_pontos_locais_1=new ArrayList<>();
    List<Double> pares_pontos_locais_2=new ArrayList<>();
    List<Double> pares_pontos_locais_12=new ArrayList<>();

    double theta_radianos=(Math.PI/180)*angulo;

    double cos=Math.cos(theta_radianos);
    double sin=Math.sin(theta_radianos);
    double c2=cos*cos;
    double s2=sin*sin;

    for(Integer i=0;i<pares_pontos_x.size();i++)
    {
        pares_pontos_locais_1.add(c2*pares_pontos_x.get(i)+s2*pares_pontos_y.get(i)+2*cos*sin*tau_xy);
        pares_pontos_locais_2.add(s2*pares_pontos_x.get(i)+c2*pares_pontos_y.get(i)-2*cos*sin*tau_xy);
        pares_pontos_locais_12.add(-sin*cos*pares_pontos_x.get(i)+sin*cos*pares_pontos_y.get(i)+(c2-s2)*tau_xy);
    }

//Filtragem em coordenadas locais

//   Para valores de X

    for(Integer i=0;i<pares_pontos_locais_1.size();i++)
    {
        if(pares_pontos_locais_1.get(i)<0.0)
        {
            pares_pontos_locais_1.set(i,null);
            pares_pontos_locais_2.set(i,null);
            pares_pontos_locais_12.set(i,null);
        }
    }

    pares_pontos_locais_1.removeAll(Collections.singleton(null));
    pares_pontos_locais_2.removeAll(Collections.singleton(null));
    pares_pontos_locais_12.removeAll(Collections.singleton(null));

    //   Para valores de Y

    for(Integer i=0;i<pares_pontos_locais_2.size();i++)
    {
        if(pares_pontos_locais_2.get(i)<0.0)
        {
            pares_pontos_locais_1.set(i,null);
            pares_pontos_locais_2.set(i,null);
            pares_pontos_locais_12.set(i,null);
        }
    }
//Eliminas os null antes
    pares_pontos_locais_1.removeAll(Collections.singleton(null));
    pares_pontos_locais_2.removeAll(Collections.singleton(null));
    pares_pontos_locais_12.removeAll(Collections.singleton(null));

//Zeramos para poder encher de novo
    pares_pontos_x.clear();
    pares_pontos_y.clear();
//	alert(pares_pontos_x.length);
//	alert(pares_pontos_y.length);

    for(Integer i=0;i<pares_pontos_locais_1.size();i++)
    {
        pares_pontos_x.add(c2*pares_pontos_locais_1.get(i)+s2*pares_pontos_locais_2.get(i)-2*cos*sin*pares_pontos_locais_12.get(i));
        pares_pontos_y.add(s2*pares_pontos_locais_1.get(i)+c2*pares_pontos_locais_2.get(i)+2*cos*sin*pares_pontos_locais_12.get(i));
      //  pares_pontos_xy[i]=sin*cos*pares_pontos_locais_1[i]-sin*cos*pares_pontos_locais_2[i]+(c2-s2)*pares_pontos_locais_12[i];
    }
//////fim do filtro
}
//Maio16
//Maio17
public void filtro_calculo_conica_NEGPOS(double angulo,double tau_xy)
{

//Transformando para coordenadas LOCAIS

    List<Double> pares_pontos_locais_1=new ArrayList<>();
    List<Double> pares_pontos_locais_2=new ArrayList<>();
    List<Double> pares_pontos_locais_12=new ArrayList<>();

    double theta_radianos=(Math.PI/180)*angulo;

    double cos=Math.cos(theta_radianos);
    double sin=Math.sin(theta_radianos);
    double c2=cos*cos;
    double s2=sin*sin;

    for(Integer i=0;i<pares_pontos_x.size();i++)
    {
        pares_pontos_locais_1.add(c2*pares_pontos_x.get(i)+s2*pares_pontos_y.get(i)+2*cos*sin*tau_xy);
        pares_pontos_locais_2.add(s2*pares_pontos_x.get(i)+c2*pares_pontos_y.get(i)-2*cos*sin*tau_xy);
        pares_pontos_locais_12.add(-sin*cos*pares_pontos_x.get(i)+sin*cos*pares_pontos_y.get(i)+(c2-s2)*tau_xy);
    }

//Filtragem em coordenadas locais

//   Para valores de X

    for(Integer i=0;i<pares_pontos_locais_1.size();i++)
    {
        if(pares_pontos_locais_1.get(i)>0.0)
        {
            pares_pontos_locais_1.set(i,null);
            pares_pontos_locais_2.set(i,null);
            pares_pontos_locais_12.set(i,null);
        }
    }

    pares_pontos_locais_1.removeAll(Collections.singleton(null));
    pares_pontos_locais_2.removeAll(Collections.singleton(null));
    pares_pontos_locais_12.removeAll(Collections.singleton(null));

    //   Para valores de Y

    for(Integer i=0;i<pares_pontos_locais_2.size();i++)
    {
        if(pares_pontos_locais_2.get(i)<0.0)
        {
            pares_pontos_locais_1.set(i,null);
            pares_pontos_locais_2.set(i,null);
            pares_pontos_locais_12.set(i,null);
        }
    }
//Eliminas os null antes
    pares_pontos_locais_1.removeAll(Collections.singleton(null));
    pares_pontos_locais_2.removeAll(Collections.singleton(null));
    pares_pontos_locais_12.removeAll(Collections.singleton(null));

//Zeramos para poder encher de novo
    pares_pontos_x.clear();
    pares_pontos_y.clear();
//	alert(pares_pontos_x.length);
//	alert(pares_pontos_y.length);

    for(Integer i=0;i<pares_pontos_locais_1.size();i++)
    {
        pares_pontos_x.add(c2*pares_pontos_locais_1.get(i)+s2*pares_pontos_locais_2.get(i)-2*cos*sin*pares_pontos_locais_12.get(i));
        pares_pontos_y.add(s2*pares_pontos_locais_1.get(i)+c2*pares_pontos_locais_2.get(i)+2*cos*sin*pares_pontos_locais_12.get(i));
        //  pares_pontos_xy[i]=sin*cos*pares_pontos_locais_1[i]-sin*cos*pares_pontos_locais_2[i]+(c2-s2)*pares_pontos_locais_12[i];
    }
//////fim do filtro NEGPOS
}
 //maio16
//Maio17
 public void filtro_calculo_conica_NEGNEG(double angulo,double tau_xy)
 {

//Transformando para coordenadas LOCAIS

     List<Double> pares_pontos_locais_1=new ArrayList<>();
     List<Double> pares_pontos_locais_2=new ArrayList<>();
     List<Double> pares_pontos_locais_12=new ArrayList<>();

     double theta_radianos=(Math.PI/180)*angulo;

     double cos=Math.cos(theta_radianos);
     double sin=Math.sin(theta_radianos);
     double c2=cos*cos;
     double s2=sin*sin;

     for(Integer i=0;i<pares_pontos_x.size();i++)
     {
         pares_pontos_locais_1.add(c2*pares_pontos_x.get(i)+s2*pares_pontos_y.get(i)+2*cos*sin*tau_xy);
         pares_pontos_locais_2.add(s2*pares_pontos_x.get(i)+c2*pares_pontos_y.get(i)-2*cos*sin*tau_xy);
         pares_pontos_locais_12.add(-sin*cos*pares_pontos_x.get(i)+sin*cos*pares_pontos_y.get(i)+(c2-s2)*tau_xy);
     }

//Filtragem em coordenadas locais

//   Para valores de X

     for(Integer i=0;i<pares_pontos_locais_1.size();i++)
     {
         if(pares_pontos_locais_1.get(i)>0.0)
         {
             pares_pontos_locais_1.set(i,null);
             pares_pontos_locais_2.set(i,null);
             pares_pontos_locais_12.set(i,null);
         }
     }

     pares_pontos_locais_1.removeAll(Collections.singleton(null));
     pares_pontos_locais_2.removeAll(Collections.singleton(null));
     pares_pontos_locais_12.removeAll(Collections.singleton(null));

     //   Para valores de Y

     for(Integer i=0;i<pares_pontos_locais_2.size();i++)
     {
         if(pares_pontos_locais_2.get(i)>0.0)
         {
             pares_pontos_locais_1.set(i,null);
             pares_pontos_locais_2.set(i,null);
             pares_pontos_locais_12.set(i,null);
         }
     }
//Eliminas os null antes
     pares_pontos_locais_1.removeAll(Collections.singleton(null));
     pares_pontos_locais_2.removeAll(Collections.singleton(null));
     pares_pontos_locais_12.removeAll(Collections.singleton(null));

//Zeramos para poder encher de novo
     pares_pontos_x.clear();
     pares_pontos_y.clear();
//	alert(pares_pontos_x.length);
//	alert(pares_pontos_y.length);

     for(Integer i=0;i<pares_pontos_locais_1.size();i++)
     {
         pares_pontos_x.add(c2*pares_pontos_locais_1.get(i)+s2*pares_pontos_locais_2.get(i)-2*cos*sin*pares_pontos_locais_12.get(i));
         pares_pontos_y.add(s2*pares_pontos_locais_1.get(i)+c2*pares_pontos_locais_2.get(i)+2*cos*sin*pares_pontos_locais_12.get(i));
         //  pares_pontos_xy[i]=sin*cos*pares_pontos_locais_1[i]-sin*cos*pares_pontos_locais_2[i]+(c2-s2)*pares_pontos_locais_12[i];
     }
//////fim do filtro NEGNEG
 }
    //maio16
//Maio17
    public void filtro_calculo_conica_POSNEG(double angulo,double tau_xy)
    {

//Transformando para coordenadas LOCAIS

        List<Double> pares_pontos_locais_1=new ArrayList<>();
        List<Double> pares_pontos_locais_2=new ArrayList<>();
        List<Double> pares_pontos_locais_12=new ArrayList<>();

        double theta_radianos=(Math.PI/180)*angulo;

        double cos=Math.cos(theta_radianos);
        double sin=Math.sin(theta_radianos);
        double c2=cos*cos;
        double s2=sin*sin;

        for(Integer i=0;i<pares_pontos_x.size();i++)
        {
            pares_pontos_locais_1.add(c2*pares_pontos_x.get(i)+s2*pares_pontos_y.get(i)+2*cos*sin*tau_xy);
            pares_pontos_locais_2.add(s2*pares_pontos_x.get(i)+c2*pares_pontos_y.get(i)-2*cos*sin*tau_xy);
            pares_pontos_locais_12.add(-sin*cos*pares_pontos_x.get(i)+sin*cos*pares_pontos_y.get(i)+(c2-s2)*tau_xy);
        }

//Filtragem em coordenadas locais

//   Para valores de X

        for(Integer i=0;i<pares_pontos_locais_1.size();i++)
        {
            if(pares_pontos_locais_1.get(i)<0.0)
            {
                pares_pontos_locais_1.set(i,null);
                pares_pontos_locais_2.set(i,null);
                pares_pontos_locais_12.set(i,null);
            }
        }

        pares_pontos_locais_1.removeAll(Collections.singleton(null));
        pares_pontos_locais_2.removeAll(Collections.singleton(null));
        pares_pontos_locais_12.removeAll(Collections.singleton(null));

        //   Para valores de Y

        for(Integer i=0;i<pares_pontos_locais_2.size();i++)
        {
            if(pares_pontos_locais_2.get(i)>0.0)
            {
                pares_pontos_locais_1.set(i,null);
                pares_pontos_locais_2.set(i,null);
                pares_pontos_locais_12.set(i,null);
            }
        }
//Eliminas os null antes
        pares_pontos_locais_1.removeAll(Collections.singleton(null));
        pares_pontos_locais_2.removeAll(Collections.singleton(null));
        pares_pontos_locais_12.removeAll(Collections.singleton(null));

//Zeramos para poder encher de novo
        pares_pontos_x.clear();
        pares_pontos_y.clear();
//	alert(pares_pontos_x.length);
//	alert(pares_pontos_y.length);

        for(Integer i=0;i<pares_pontos_locais_1.size();i++)
        {
            pares_pontos_x.add(c2*pares_pontos_locais_1.get(i)+s2*pares_pontos_locais_2.get(i)-2*cos*sin*pares_pontos_locais_12.get(i));
            pares_pontos_y.add(s2*pares_pontos_locais_1.get(i)+c2*pares_pontos_locais_2.get(i)+2*cos*sin*pares_pontos_locais_12.get(i));
            //  pares_pontos_xy[i]=sin*cos*pares_pontos_locais_1[i]-sin*cos*pares_pontos_locais_2[i]+(c2-s2)*pares_pontos_locais_12[i];
        }
//////fim do filtro POSNEG
    }
//maio16

/*

*/
    public void vetorizar_duas_series_LISTA
            (
            List<Double> pares_pontos_temp_x,
            List<Double> pares_pontos_temp_y,
            List<Double> lista_saida_x,
            List<Double> lista_saida_y
            )
    {
        //Posições
        Integer posicao_aiorX=0;
        Integer posicao_aiorY=0;
        Integer posicao_enorX=0;
        Integer posicao_enorY=0;
        maiorX=pares_pontos_temp_x.get(0);
        maiorY=pares_pontos_temp_y.get(0);
        menorX=pares_pontos_temp_x.get(0);
        menorY=pares_pontos_temp_y.get(0);

//maiorX
        for(Integer i=0;i<pares_pontos_temp_x.size();i++)
        {
            if (maiorX < pares_pontos_temp_x.get(i))
            {
                maiorX = pares_pontos_temp_x.get(i);
                posicao_aiorX = i;
            }
        }
//menorX
        for(Integer i=0;i<pares_pontos_temp_x.size();i++)
        {
            if(menorX > pares_pontos_temp_x.get(i))
            {
                menorX=pares_pontos_temp_x.get(i);
                posicao_enorX=i;
            }
        }
//maiorY
        for(Integer i=0;i<pares_pontos_temp_y.size();i++) {
            if (maiorY < pares_pontos_temp_y.get(i)) {
                maiorY = pares_pontos_temp_y.get(i);
                posicao_aiorY = i;
            }
        }
//menorY
        for(Integer i=0;i<pares_pontos_temp_y.size();i++) {
            if (menorY > pares_pontos_temp_y.get(i)) {
                menorY = pares_pontos_temp_y.get(i);
                posicao_enorY = i;
            }
        }

        //Maio18
        //Log.d("LISTA_extremos","\n"+                maiorX+"  "+pares_pontos_temp_x.get(posicao_aiorX)+"    "+posicao_aiorX+" X \n"+                maiorY+"  "+pares_pontos_temp_y.get(posicao_aiorY)+"    "+posicao_aiorY+" Y \n"+                menorX+"  "+pares_pontos_temp_x.get(posicao_enorX)+"    "+posicao_enorX+" X \n"+                menorY+"  "+pares_pontos_temp_y.get(posicao_enorY)+"    "+posicao_enorY+" Y \n"        );

        Integer i_vetor=posicao_enorX;
        Integer i_vetor_seguinte=i_vetor+1;

        if(i_vetor_seguinte==pares_pontos_temp_x.size())
        {
            return;
        }

        do
        {

            lista_saida_x.add(pares_pontos_temp_x.get(i_vetor));
            lista_saida_y.add(pares_pontos_temp_y.get(i_vetor));

            pares_pontos_temp_x.set(i_vetor,null);
            pares_pontos_temp_y.set(i_vetor,null);

            i_vetor=i_vetor+1;
            i_vetor_seguinte=1+i_vetor;

            if(i_vetor>=pares_pontos_temp_x.size())
            {
                i_vetor=i_vetor-pares_pontos.length;

            }

            if(i_vetor_seguinte>=pares_pontos_temp_x.size())
            {
                i_vetor_seguinte=i_vetor_seguinte-pares_pontos_temp_x.size();

            }

            //Se por acaso vai ler um NULL ele mesmo sai do CICLO
            if(pares_pontos_temp_x.get(i_vetor_seguinte)==null)
            {
                break;
            }

        }  while (pares_pontos_temp_x.get(i_vetor)<=pares_pontos_temp_x.get(i_vetor_seguinte));

        //Mesmo saindo do While ele coloco o ultimo

        lista_saida_x.add(pares_pontos_temp_x.get(i_vetor));
        lista_saida_y.add(pares_pontos_temp_y.get(i_vetor));

        pares_pontos_temp_x.set(i_vetor,null);
        pares_pontos_temp_y.set(i_vetor,null);


        pares_pontos_temp_x.removeAll(Collections.singleton(null));
        pares_pontos_temp_y.removeAll(Collections.singleton(null));

        //Outubro 18, 2018 Filtro adicional
        for(int i=0;i<lista_saida_x.size();i++)
        {
            if(lista_saida_x.get(i)==0.0)
            {
                if(lista_saida_x.get(i)==0.0)
                {
                    lista_saida_x.remove(i);
                    lista_saida_y.remove(i);
                }
            }
        }
    }

//Maio18, chamada diretamente desde o principal
    public void fazer_segunda_serie_LISTA
    (
            List<Double> pares_pontos_temp_x,
            List<Double> pares_pontos_temp_y,
            List<Double> lista_saida_x,
            List<Double> lista_saida_y
    )
    {

        //Trasnformamos aquele vetor inicial (que contem NULOS) em uma lista:
        ArrayList<Double> transformada_x = new ArrayList<Double>();
        ArrayList<Double> transformada_y = new ArrayList<Double>();

        for(Integer i=0;i<pares_pontos_temp_x.size();i++)
        {
            if(pares_pontos_temp_x.get(i)!=null) {
                transformada_x.add(pares_pontos_temp_x.get(i));
                transformada_y.add(pares_pontos_temp_y.get(i));
            }
        }

        double temporal_1;
        double temporal_2;
        double temporal_1_y;
        double temporal_2_y;

        for(Integer i=0;i<transformada_x.size();i++) {
            for (Integer j = 0; j < transformada_x.size(); j++) {
                if (transformada_x.get(j)>=transformada_x.get(i))
                {
                    temporal_1=transformada_x.get(i);
                    temporal_2=transformada_x.get(j);

                    temporal_1_y=transformada_y.get(i);
                    temporal_2_y=transformada_y.get(j);

                    transformada_x.set(j,temporal_1);
                    transformada_x.set(i,temporal_2);

                    transformada_y.set(j,temporal_1_y);
                    transformada_y.set(i,temporal_2_y);
                }
            }
        }

        for(Integer i=0;i<transformada_x.size();i++)
        {
            lista_saida_x.add(transformada_x.get(i));
            lista_saida_y.add(transformada_y.get(i));
        }
        //Outubro 18, 2018 Filtro adicional
        for(int i=0;i<lista_saida_x.size();i++)
        {
            if(lista_saida_x.get(i)==0.0)
            {
                if(lista_saida_x.get(i)==0.0)
                {
                    lista_saida_x.remove(i);
                    lista_saida_y.remove(i);
                }
            }
        }
    }
//Outbro 2018
    public void filtro_distancia()
    {
        List<Double> distancias = new ArrayList<>();

        Double anterior_x=pares_pontos_x.get(0);
        Double anterior_y=pares_pontos_y.get(0);

        Double delta_x,delta_y,tempDISTANCIA;

        for(int i=0;i<pares_pontos_x.size();i++)
        {
            delta_x=Math.pow((anterior_x-pares_pontos_x.get(i)),2);
            delta_y=Math.pow((anterior_y-pares_pontos_y.get(i)),2);
            tempDISTANCIA=Math.sqrt(delta_x+delta_y);
            distancias.add(tempDISTANCIA);
            anterior_x=pares_pontos_x.get(i);
            anterior_y=pares_pontos_y.get(i);
        }
//calculamos a media
        Double soma=0.0;
        for(int i=0;i<distancias.size();i++)
        {
            soma=soma+distancias.get(i);
        }

//colocamos apenas aqueles que sejam proximos)
        Double media=(soma/distancias.size());

        //Log.d("filtro_distancia", "Media  "+media+" antes "+pares_pontos_x.size());

        for(int i=0;i<distancias.size();i++)
        {
            if(Math.abs(distancias.get(i)-media)>(media/1.0))
            {
                pares_pontos_x.set(i,0.0);
                pares_pontos_y.set(i,0.0);
            }
        }

        //Log.d("filtro_distancia", "Media  "+media+" DEPOIS "+pares_pontos_x.size());
//////////////
    }

/*
* Outubro 29
* Migração Suave:
* Ao invés de ter 4 quadrantes, vamos ter 4 semiplanos,
* sendo preciso desmontar todos menos 1
* */

    public void gerador_TRACAO_FIBRA(Double cos,Double sin)
    {
        calcular_coeficientesTRACA_OFIBRA(cos,sin);

        if(controle_conica_tipo_elipse()=="elipse"){
            gerar_pontos();
        }
        filtro_calculo_conica_TRACAO_FIBRA(angulo_global,tauXY);
        filtro_criterio();
    }
//Outubro29
    public void calcular_coeficientesTRACA_OFIBRA(Double cos,Double sin){
        Double Quadrado_t1=(1/f_SIGMA_T_1)*(1/f_SIGMA_T_1);
        Double Quadrado_t2=(1/f_SIGMA_T_2)*(1/f_SIGMA_T_2);
        Double Quadrado_t12=(1/f_TAU12)*(1/f_TAU12);

        Double fatorRotar_a=- Math.pow(cos,4)*Quadrado_t1+ Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;
        Double fatorRotar_c=Math.pow(sin,4)*Quadrado_t1+ Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;
        Double fatorRotar_b=+ 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1 - 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;
        Double fatorRotar_d=4*Math.pow(cos,3)*sin*Quadrado_t1*tauXY- 2*Math.pow(cos,3)*sin*Quadrado_t12*tauXY+ 2*cos*Math.pow(sin,3)*Quadrado_t12*tauXY;

        Double fatorRotar_f=4*Math.pow(sin,3)*cos*Quadrado_t1*tauXY- 2*Math.pow(sin,3)*cos*Quadrado_t12*tauXY+ 2*sin*Math.pow(cos,3)*Quadrado_t12*tauXY;

        Double factorRotar_g=4*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1- 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12	+ Math.pow(cos,4)*Quadrado_t12+ Math.pow(sin,4)*Quadrado_t12;

        a=fatorRotar_a;
        c=fatorRotar_c;
        b=(fatorRotar_b/2);
        d=(fatorRotar_d/2);
        f=(fatorRotar_f/2);
        g=-1+factorRotar_g*(tauXY*tauXY);
    }


    //Outubro29
    public void gerador_TRACAO_MATRIZ(Double cos,Double sin)
    {
        calcular_coeficientes_MATRIZ_TRACAO(cos,sin);
        //gerar_pontos();
        if(controle_conica_tipo_elipse()=="elipse"){
            gerar_pontos();
        }
        filtro_calculo_conica_TRACAO_MATRIZ(angulo_global,tauXY);

        //Deixando em pares pontos apenas aqueles de IF unitário
        filtro_criterio();
    }

    public void calcular_coeficientes_MATRIZ_TRACAO(Double cos,Double sin)
    {
        Double Quadrado_t1=(1/f_SIGMA_T_1)*(1/f_SIGMA_T_1);
        Double Quadrado_tc=(1/f_SIGMA_C_1)*(1/f_SIGMA_C_1);
        Double Quadrado_t2=(1/f_SIGMA_C_2)*(1/f_SIGMA_C_2);
        Double Quadrado_t12=(1/f_TAU12)*(1/f_TAU12);

        Double fatorRotar_a=+ Math.pow(sin,4)*Quadrado_t2+ Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;
        Double fatorRotar_c=+ Math.pow(cos,4)*Quadrado_t2+ Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;
        Double fatorRotar_b=- 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12+ 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t2;
        Double fatorRotar_d=- 2*Math.pow(cos,3)*sin*Quadrado_t12*tauXY+ 2*cos*Math.pow(sin,3)*Quadrado_t12*tauXY- 4*cos*Math.pow(sin,3)*Quadrado_t2*tauXY;
        Double fatorRotar_f=- 2*Math.pow(sin,3)*cos*Quadrado_t12*tauXY+ 2*sin*Math.pow(cos,3)*Quadrado_t12*tauXY- 4*sin*Math.pow(cos,3)*Quadrado_t2*tauXY;
        Double factorRotar_g=- 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12+ 4*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t2+ Math.pow(cos,4)*Quadrado_t12+ Math.pow(sin,4)*Quadrado_t12;

        a=fatorRotar_a;
        c=fatorRotar_c;
        b=(fatorRotar_b/2);
        d=(fatorRotar_d/2);
        f=(fatorRotar_f/2);
        g=-1+factorRotar_g*(tauXY*tauXY);
    }

    public void filtro_calculo_conica_TRACAO_MATRIZ(double angulo,double tau_xy)
    {
//Transformando para coordenadas LOCAIS

        List<Double> pares_pontos_locais_1=new ArrayList<>();
        List<Double> pares_pontos_locais_2=new ArrayList<>();
        List<Double> pares_pontos_locais_12=new ArrayList<>();

        double theta_radianos=(Math.PI/180)*angulo;

        double cos=Math.cos(theta_radianos);
        double sin=Math.sin(theta_radianos);
        double c2=cos*cos;
        double s2=sin*sin;

        for(Integer i=0;i<pares_pontos_x.size();i++)
        {
            pares_pontos_locais_1.add(c2*pares_pontos_x.get(i)+s2*pares_pontos_y.get(i)+2*cos*sin*tau_xy);
            pares_pontos_locais_2.add(s2*pares_pontos_x.get(i)+c2*pares_pontos_y.get(i)-2*cos*sin*tau_xy);
            pares_pontos_locais_12.add(-sin*cos*pares_pontos_x.get(i)+sin*cos*pares_pontos_y.get(i)+(c2-s2)*tau_xy);
        }

//Filtragem em coordenadas locais
//   Para valores de X

        for(Integer i=0;i<pares_pontos_locais_1.size();i++)
        {
            if(pares_pontos_locais_2.get(i)<0.0)
            {
                pares_pontos_locais_1.set(i,null);
                pares_pontos_locais_2.set(i,null);
                pares_pontos_locais_12.set(i,null);
            }
        }

        pares_pontos_locais_1.removeAll(Collections.singleton(null));
        pares_pontos_locais_2.removeAll(Collections.singleton(null));
        pares_pontos_locais_12.removeAll(Collections.singleton(null));

//Eliminas os null antes
        pares_pontos_locais_1.removeAll(Collections.singleton(null));
        pares_pontos_locais_2.removeAll(Collections.singleton(null));
        pares_pontos_locais_12.removeAll(Collections.singleton(null));

//Zeramos para poder encher de novo
        pares_pontos_x.clear();
        pares_pontos_y.clear();
//	alert(pares_pontos_x.length);
//	alert(pares_pontos_y.length);

        for(Integer i=0;i<pares_pontos_locais_1.size();i++)
        {
            pares_pontos_x.add(c2*pares_pontos_locais_1.get(i)+s2*pares_pontos_locais_2.get(i)-2*cos*sin*pares_pontos_locais_12.get(i));
            pares_pontos_y.add(s2*pares_pontos_locais_1.get(i)+c2*pares_pontos_locais_2.get(i)+2*cos*sin*pares_pontos_locais_12.get(i));
            //  pares_pontos_xy[i]=sin*cos*pares_pontos_locais_1[i]-sin*cos*pares_pontos_locais_2[i]+(c2-s2)*pares_pontos_locais_12[i];
        }
//////fim do filtro
    }
    //Outubro29
    public void gerador_COMPRESSAO_FIBRA(Double cos,Double sin)
    {
        calcular_coeficientes_COMPRESSAO_FIBRA(cos,sin);

        if(controle_conica_tipo_elipse()=="elipse"){
            gerar_pontos();
        }
        filtro_calculo_conica_COMPRESSAO_FIBRA(angulo_global,tauXY);
        filtro_criterio();
    }
    //Outubro29
    public void calcular_coeficientes_COMPRESSAO_FIBRA(Double cos,Double sin)
    {
        Double Quadrado_t1=(1/f_SIGMA_C_1)*(1/f_SIGMA_C_1);
        Double Quadrado_t2=(1/f_SIGMA_T_2)*(1/f_SIGMA_T_2);
        Double Quadrado_t12=(1/f_TAU12)*(1/f_TAU12);

        Double fatorRotar_a=- Math.pow(cos,4)*Quadrado_t1+ Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;
        Double fatorRotar_c=Math.pow(sin,4)*Quadrado_t1+ Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;
        Double fatorRotar_b=+ 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1 - 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;
        Double fatorRotar_d=4*Math.pow(cos,3)*sin*Quadrado_t1*tauXY- 2*Math.pow(cos,3)*sin*Quadrado_t12*tauXY+ 2*cos*Math.pow(sin,3)*Quadrado_t12*tauXY;
        Double fatorRotar_f=4*Math.pow(sin,3)*cos*Quadrado_t1*tauXY- 2*Math.pow(sin,3)*cos*Quadrado_t12*tauXY+ 2*sin*Math.pow(cos,3)*Quadrado_t12*tauXY;
        Double factorRotar_g=4*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1- 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12+ Math.pow(cos,4)*Quadrado_t12+ Math.pow(sin,4)*Quadrado_t12;


        a=fatorRotar_a;
        c=fatorRotar_c;
        b=(fatorRotar_b/2);
        d=(fatorRotar_d/2);
        f=(fatorRotar_f/2);
        g=-1+factorRotar_g*(tauXY*tauXY);
    }

    public void filtro_calculo_conica_COMPRESSAO_FIBRA(double angulo,double tau_xy)
    {
//Transformando para coordenadas LOCAIS

            List<Double> pares_pontos_locais_1=new ArrayList<>();
            List<Double> pares_pontos_locais_2=new ArrayList<>();
            List<Double> pares_pontos_locais_12=new ArrayList<>();

            double theta_radianos=(Math.PI/180)*angulo;

            double cos=Math.cos(theta_radianos);
            double sin=Math.sin(theta_radianos);
            double c2=cos*cos;
            double s2=sin*sin;

            for(Integer i=0;i<pares_pontos_x.size();i++)
            {
                pares_pontos_locais_1.add(c2*pares_pontos_x.get(i)+s2*pares_pontos_y.get(i)+2*cos*sin*tau_xy);
                pares_pontos_locais_2.add(s2*pares_pontos_x.get(i)+c2*pares_pontos_y.get(i)-2*cos*sin*tau_xy);
                pares_pontos_locais_12.add(-sin*cos*pares_pontos_x.get(i)+sin*cos*pares_pontos_y.get(i)+(c2-s2)*tau_xy);
            }

//Filtragem em coordenadas locais
//   Para valores de X

        for(Integer i=0;i<pares_pontos_locais_1.size();i++)
            {
                if(pares_pontos_locais_1.get(i)>0.0)
                {
                    pares_pontos_locais_1.set(i,null);
                    pares_pontos_locais_2.set(i,null);
                    pares_pontos_locais_12.set(i,null);
                }
            }

            pares_pontos_locais_1.removeAll(Collections.singleton(null));
            pares_pontos_locais_2.removeAll(Collections.singleton(null));
            pares_pontos_locais_12.removeAll(Collections.singleton(null));

//Eliminas os null antes
            pares_pontos_locais_1.removeAll(Collections.singleton(null));
            pares_pontos_locais_2.removeAll(Collections.singleton(null));
            pares_pontos_locais_12.removeAll(Collections.singleton(null));

//Zeramos para poder encher de novo
            pares_pontos_x.clear();
            pares_pontos_y.clear();
//	alert(pares_pontos_x.length);
//	alert(pares_pontos_y.length);

            for(Integer i=0;i<pares_pontos_locais_1.size();i++)
            {
                pares_pontos_x.add(c2*pares_pontos_locais_1.get(i)+s2*pares_pontos_locais_2.get(i)-2*cos*sin*pares_pontos_locais_12.get(i));
                pares_pontos_y.add(s2*pares_pontos_locais_1.get(i)+c2*pares_pontos_locais_2.get(i)+2*cos*sin*pares_pontos_locais_12.get(i));
                //  pares_pontos_xy[i]=sin*cos*pares_pontos_locais_1[i]-sin*cos*pares_pontos_locais_2[i]+(c2-s2)*pares_pontos_locais_12[i];
            }
//////fim do filtro
    }

    //Outubro29
    public void gerador_COMPRESSAO_MATRIZ(Double cos,Double sin)
    {
        calcular_coeficientes_COMPRESSAO_MATRIZ(cos,sin);
        if(controle_conica_tipo_elipse()=="elipse"){
            gerar_pontos();
        }
        filtro_calculo_conica_COMPRESSAO_MATRIZ(angulo_global,tauXY);

        filtro_criterio();
    }
    //Outubro29
    public void calcular_coeficientes_COMPRESSAO_MATRIZ(Double cos,Double sin)
    {
        Double TAU23=f_TAU12; //TAU23_global;
        Double Quadrado_t2=Math.pow((1/(2*TAU23)),2);
        Double coeficiente=(f_SIGMA_C_2/(2*TAU23));
        Double Simples_t2=(-1+Math.pow(coeficiente,2))*(1/f_SIGMA_C_2);
        Double Quadrado_t12=(1/f_TAU12)*(1/f_TAU12);

        Double fatorRotar_a=+ Math.pow(sin,4)*Quadrado_t2+ Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;
        Double fatorRotar_c=+ Math.pow(cos,4)*Quadrado_t2+ Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12;
        Double fatorRotar_b=- 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12+ 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t2;
        Double fatorRotar_d=- 2*Math.pow(cos,3)*sin*Quadrado_t12*tauXY+Math.pow(sin,2)*Simples_t2*tauXY+ 2*cos*Math.pow(sin,3)*Quadrado_t12*tauXY- 4*cos*Math.pow(sin,3)*Quadrado_t2*tauXY;
        Double fatorRotar_f=- 2*Math.pow(sin,3)*cos*Quadrado_t12*tauXY+Math.pow(cos,2)*Simples_t2+ 2*sin*Math.pow(cos,3)*Quadrado_t12*tauXY- 4*sin*Math.pow(cos,3)*Quadrado_t2*tauXY;
        Double factorRotar_g=-2*cos*sin*Simples_t2*tauXY- 2*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t12+ 4*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t2+ Math.pow(sin,4)*Quadrado_t12+ Math.pow(cos,4)*Quadrado_t12;


        a=fatorRotar_a;
        c=fatorRotar_c;
        b=(fatorRotar_b/2);
        d=(fatorRotar_d/2);
        f=(fatorRotar_f/2);
        g=-1+factorRotar_g*(tauXY*tauXY);
    }

    public void filtro_calculo_conica_COMPRESSAO_MATRIZ(double angulo,double tau_xy)
    {
//Transformando para coordenadas LOCAIS

        List<Double> pares_pontos_locais_1=new ArrayList<>();
        List<Double> pares_pontos_locais_2=new ArrayList<>();
        List<Double> pares_pontos_locais_12=new ArrayList<>();

        double theta_radianos=(Math.PI/180)*angulo;

        double cos=Math.cos(theta_radianos);
        double sin=Math.sin(theta_radianos);
        double c2=cos*cos;
        double s2=sin*sin;

        for(Integer i=0;i<pares_pontos_x.size();i++)
        {
            pares_pontos_locais_1.add(c2*pares_pontos_x.get(i)+s2*pares_pontos_y.get(i)+2*cos*sin*tau_xy);
            pares_pontos_locais_2.add(s2*pares_pontos_x.get(i)+c2*pares_pontos_y.get(i)-2*cos*sin*tau_xy);
            pares_pontos_locais_12.add(-sin*cos*pares_pontos_x.get(i)+sin*cos*pares_pontos_y.get(i)+(c2-s2)*tau_xy);
        }

//Filtragem em coordenadas locais
//   Para valores de X
//		if(pares_pontos_locais_2[i]>0.0):

        for(Integer i=0;i<pares_pontos_locais_1.size();i++)
        {
            if(pares_pontos_locais_2.get(i)>0.0)
            {
                pares_pontos_locais_1.set(i,null);
                pares_pontos_locais_2.set(i,null);
                pares_pontos_locais_12.set(i,null);
            }
        }

        pares_pontos_locais_1.removeAll(Collections.singleton(null));
        pares_pontos_locais_2.removeAll(Collections.singleton(null));
        pares_pontos_locais_12.removeAll(Collections.singleton(null));

//Eliminas os null antes
        pares_pontos_locais_1.removeAll(Collections.singleton(null));
        pares_pontos_locais_2.removeAll(Collections.singleton(null));
        pares_pontos_locais_12.removeAll(Collections.singleton(null));

//Zeramos para poder encher de novo
        pares_pontos_x.clear();
        pares_pontos_y.clear();
//	alert(pares_pontos_x.length);
//	alert(pares_pontos_y.length);

        for(Integer i=0;i<pares_pontos_locais_1.size();i++)
        {
            pares_pontos_x.add(c2*pares_pontos_locais_1.get(i)+s2*pares_pontos_locais_2.get(i)-2*cos*sin*pares_pontos_locais_12.get(i));
            pares_pontos_y.add(s2*pares_pontos_locais_1.get(i)+c2*pares_pontos_locais_2.get(i)+2*cos*sin*pares_pontos_locais_12.get(i));
            //  pares_pontos_xy[i]=sin*cos*pares_pontos_locais_1[i]-sin*cos*pares_pontos_locais_2[i]+(c2-s2)*pares_pontos_locais_12[i];
        }
//////fim do filtro
    }
    //Outubro29
    public String controle_conica_tipo_elipse()
    {
        Double a_f=a;
        Double b_f=b;
        Double c_f=c;
        Double d_f=d;
        Double f_f=f;
        Double g_f=g;
//#Usamos um conjunto de funcoes para facilitar estas determinantes
        double delta=calcular_delta(a_f,b_f,c_f,d_f,f_f,g_f);
        Double I=a_f+c_f;
        Double J=calcular_J(a_f,b_f,c_f,d_f,f_f,g_f);
        Double K=calcular_K(a_f,b_f,c_f,d_f,f_f,g_f);

        String tipo_conica="nao_determinado";

        if((delta!=0) && (J>0) && ((delta/I)<0))
        {
            tipo_conica="elipse";
        }

        if((delta!=0) &&(J==0))
        {
            tipo_conica="parabola";
        }

        if((delta!=0) && (J<0))
        {
            tipo_conica="hyperbola";
        }

        if((delta==0) && (J==0))
        {
            tipo_conica="paralelas_real";
        }

        if((delta==0) && (J>0) && (K>0))
        {
            tipo_conica="linhas_intersection_imaginarias";
        }

        if((delta==0) && (J<0) && (K<0))
        {
            tipo_conica="linhas_intersection_real";
        }

        if((delta==0) && (J<0) && (K==0))
        {
            tipo_conica="linhas_concidentes";
        }

        return tipo_conica;

    }

//matriz de 3x3

    public Double calcular_delta(Double a,Double b,Double c,Double d,Double f,Double g)
    {
        return 	(a*c*g-a*Math.pow(f,2) - Math.pow(b,2)*g + 2*b*d*f - c*Math.pow(d,2));
    }


    public Double calcular_J(Double a,Double b,Double c,Double d,Double f,Double g)
    {
        return 	(a*c - Math.pow(b,2));
    }

    public Double calcular_K(Double a,Double b,Double c,Double d,Double f,Double g)
    {
        return 	(a*g + c*g - Math.pow(d,2) - Math.pow(f,2));
    }

    public void filtro_calculo_conica_TRACAO_FIBRA(double angulo,double tau_xy)
    {
//Transformando para coordenadas LOCAIS

        List<Double> pares_pontos_locais_1=new ArrayList<>();
        List<Double> pares_pontos_locais_2=new ArrayList<>();
        List<Double> pares_pontos_locais_12=new ArrayList<>();

        double theta_radianos=(Math.PI/180)*angulo;

        double cos=Math.cos(theta_radianos);
        double sin=Math.sin(theta_radianos);
        double c2=cos*cos;
        double s2=sin*sin;

        for(Integer i=0;i<pares_pontos_x.size();i++)
        {
            pares_pontos_locais_1.add(c2*pares_pontos_x.get(i)+s2*pares_pontos_y.get(i)+2*cos*sin*tau_xy);
            pares_pontos_locais_2.add(s2*pares_pontos_x.get(i)+c2*pares_pontos_y.get(i)-2*cos*sin*tau_xy);
            pares_pontos_locais_12.add(-sin*cos*pares_pontos_x.get(i)+sin*cos*pares_pontos_y.get(i)+(c2-s2)*tau_xy);
        }

//Filtragem em coordenadas locais
//   Para valores de X

        for(Integer i=0;i<pares_pontos_locais_1.size();i++)
        {
            if(pares_pontos_locais_1.get(i)<0.0)
            {
                pares_pontos_locais_1.set(i,null);
                pares_pontos_locais_2.set(i,null);
                pares_pontos_locais_12.set(i,null);
            }
        }

        pares_pontos_locais_1.removeAll(Collections.singleton(null));
        pares_pontos_locais_2.removeAll(Collections.singleton(null));
        pares_pontos_locais_12.removeAll(Collections.singleton(null));

//Eliminas os null antes
        pares_pontos_locais_1.removeAll(Collections.singleton(null));
        pares_pontos_locais_2.removeAll(Collections.singleton(null));
        pares_pontos_locais_12.removeAll(Collections.singleton(null));

//Zeramos para poder encher de novo
        pares_pontos_x.clear();
        pares_pontos_y.clear();
//	alert(pares_pontos_x.length);
//	alert(pares_pontos_y.length);

        for(Integer i=0;i<pares_pontos_locais_1.size();i++)
        {
            pares_pontos_x.add(c2*pares_pontos_locais_1.get(i)+s2*pares_pontos_locais_2.get(i)-2*cos*sin*pares_pontos_locais_12.get(i));
            pares_pontos_y.add(s2*pares_pontos_locais_1.get(i)+c2*pares_pontos_locais_2.get(i)+2*cos*sin*pares_pontos_locais_12.get(i));
            //  pares_pontos_xy[i]=sin*cos*pares_pontos_locais_1[i]-sin*cos*pares_pontos_locais_2[i]+(c2-s2)*pares_pontos_locais_12[i];
        }
//////fim do filtro
    }
//Outubro31
//Outubro31
//Outubro31
//Outubro31

    public void filtro_criterio()
    {

        double tolerancia=0.01;

        List<Double> pares_pontos_locais_x=new ArrayList<>();
        List<Double> pares_pontos_locais_y=new ArrayList<>();

        for(Integer i=0;i<pares_pontos_x.size();i++)
        {
            double indice_temp=criterio_puro(
                    pares_pontos_x.get(i),
                    pares_pontos_y.get(i),
                    tauXY,
                    angulo_global,
                    f_SIGMA_T_1,
                    f_SIGMA_T_2,
                    f_SIGMA_C_1,
                    f_SIGMA_C_2,
                    f_TAU12);

            if((indice_temp<(1+tolerancia)) && (indice_temp>(1-tolerancia)))
            {
                pares_pontos_locais_x.add(pares_pontos_x.get(i));
                pares_pontos_locais_y.add(pares_pontos_y.get(i));
            }

        }

        pares_pontos_x.clear();
        pares_pontos_y.clear();

        for(Integer i=0;i<pares_pontos_locais_x.size();i++)
        {
            pares_pontos_x.add(pares_pontos_locais_x.get(i));
            pares_pontos_y.add(pares_pontos_locais_y.get(i));
        }

/////////////
    }

//Outubro31
    public double criterio_puro(double sigma_x,
                                double sigma_y,
                                double tau_xy,
                                double theta,
                                double SIGMA_T_1,
                                double SIGMA_T_2,
                                double SIGMA_C_1,
                                double SIGMA_C_2,
                                double TAU12)
    {
        double theta_radianos=(Math.PI/180)*theta;

        double c=Math.cos(theta_radianos);
        double s=Math.sin(theta_radianos);
        double c2=c*c;
        double s2=s*s;
        double sigma_1=c2*sigma_x+s2*sigma_y+2*c*s*tau_xy;
        double sigma_2=s2*sigma_x+c2*sigma_y-2*c*s*tau_xy;
        double tau_12=-s*c*sigma_x+s*c*sigma_y+(c2-s2)*tau_xy;

// Dentre os critérios experimentaiso mais simples, apenas escolher conforme o sinal,
// para determinar o caso perante o qual estamos.
// Se o reforço não tiver fibra o melhor será ADVERTIR o cálculo.


//  Modo 1 , falha na fibra:

        double termo_inversos=(1/SIGMA_T_1)-(1/SIGMA_C_1);
        double termo_inversos_produtos=(1/(SIGMA_T_1*SIGMA_C_1));

        double if_falha_fibra=termo_inversos*sigma_1+termo_inversos_produtos*Math.pow(sigma_1,2);

//  Modo 2 , falha na Matriz:

        termo_inversos=(1/SIGMA_T_2)-(1/SIGMA_C_2);

        termo_inversos_produtos=(1/(SIGMA_T_2*SIGMA_C_2));

        double normalizado_tau_12=tau_12/TAU12;

        double if_falha_matriz=termo_inversos*sigma_2+termo_inversos_produtos*Math.pow(sigma_2,2)+normalizado_tau_12*normalizado_tau_12;

        double indices[]={if_falha_matriz,if_falha_fibra};
        Arrays.sort(indices);

//alert(indices);
        return indices[1];
    }
//Outubro31
    public String calcular_apenas_4_vertices(double angulo,double tauXY)
    {
        double theta_radianos=(Math.PI/180)*angulo;
        double cos=Math.cos(theta_radianos);
        double sin=Math.sin(theta_radianos);
        double c2=cos*cos;
        double s2=sin*sin;

        double TAU23=TAU23_global;//f_TAU12;//coletar_TAU23(TAU12);
        double resolvente_quadratica_termo_a=Math.pow((1.0/(2.0*TAU23)),2);
        double termoTAU23=Math.pow((f_SIGMA_C_2/(2.0*TAU23)),2);
        double resolvente_quadratica_termo_b=(1.0/(f_SIGMA_C_2))*(termoTAU23-1);
        double a=resolvente_quadratica_termo_a;
        double b=resolvente_quadratica_termo_b;
        double radicando=(Math.pow(b,2))-4*a*(-1.0);

        double preROOT_1;
        double preROOT_2;
        double ROOT_1;
        double ROOT_2;
        double raiz_de_interesse=0.0;

        if(radicando>=0)
        {
            preROOT_1=+Math.sqrt(radicando);
            preROOT_2=-Math.sqrt(radicando);
            ROOT_1=(-b+preROOT_1)/(2.0*a);
            ROOT_2=(-b+preROOT_2)/(2.0*a);
            if(ROOT_1<=0)
            {
                raiz_de_interesse=ROOT_1;
            }
            if(ROOT_2<=0)
            {
                raiz_de_interesse=ROOT_2;
            }
        }
        else
        {
            return "raiz_imaginaria";
        }

        double  SIGMA_C_2_raiz=raiz_de_interesse;

        double SIGMA_T_1_rotacionado;
        double SIGMA_T_2_rotacionado;

        double SIGMA_C_1_rotacionado;
        double SIGMA_C_2_rotacionado;
        /*
Com apenas
tau_xy NULO
*/
        if(tauXY==0)
        {
            SIGMA_T_1_rotacionado=c2*f_SIGMA_T_1+s2*f_SIGMA_T_2;
            SIGMA_T_2_rotacionado=s2*f_SIGMA_T_1+c2*f_SIGMA_T_2;

            SIGMA_C_1_rotacionado=c2*(-f_SIGMA_C_1)+s2*(SIGMA_C_2_raiz);
            SIGMA_C_2_rotacionado=s2*(-f_SIGMA_C_1)+c2*(SIGMA_C_2_raiz);
        }
        else
        {
/*
Com apenas tau_xy VIVO  Devem ser feitos mais calculos
*/
            resolvente_quadratica_termo_a=Math.pow((1.0/(f_SIGMA_T_1)),2);
            double normalizado_tau12=(tauXY/f_TAU12);
            double resolvente_quadratica_termo_c=-1.0+Math.pow(normalizado_tau12,2);
            a=resolvente_quadratica_termo_a;
            b=0.0;
            c=resolvente_quadratica_termo_c;
            radicando=-4*a*c;

            if(radicando>=0)
            {
                preROOT_1=+Math.sqrt(radicando);
                preROOT_2=-Math.sqrt(radicando);
                ROOT_1=(preROOT_1)/(2.0*a);
                ROOT_2=(preROOT_2)/(2.0*a);
                //A raÃ­z negativa serÃ¡ considerada como a raÃ­z de interesse
                if(ROOT_1>=0)
                {
                    raiz_de_interesse=ROOT_1;
                }
                if(ROOT_2>=0)
                {
                    raiz_de_interesse=ROOT_2;
                }
            }
		    else
            {
                return "raiz_imaginaria";

            }

            double SIGMA_T_1_raiz=raiz_de_interesse;
            //Parte SIGMA_2 positivo
            resolvente_quadratica_termo_a=Math.pow((1.0/(f_SIGMA_T_2)),2);
            normalizado_tau12=(tauXY/f_TAU12);
            resolvente_quadratica_termo_c=-1.0+Math.pow(normalizado_tau12,2);
            a=resolvente_quadratica_termo_a;
            b=0.0;
            c=resolvente_quadratica_termo_c;
            radicando=-4*a*c;

            if(radicando>=0)
            {
                preROOT_1=+Math.sqrt(radicando);
                preROOT_2=-Math.sqrt(radicando);
                ROOT_1=(preROOT_1)/(2.0*a);
                ROOT_2=(preROOT_2)/(2.0*a);
                //A raÃ­z negativa serÃ¡ considerada como a raÃ­z de interesse
                if(ROOT_1>=0)
                {
                    raiz_de_interesse=ROOT_1;
                }

                if(ROOT_2>=0)
                {
                    raiz_de_interesse=ROOT_2;
                }

            }
    		else
            {
                return "raiz_imaginaria";
            }

            double SIGMA_T_2_raiz=raiz_de_interesse;

            TAU23=TAU23_global;//f_TAU12; //coletar_TAU23(TAU12);
            resolvente_quadratica_termo_a=Math.pow((1.0/(2.0*TAU23)),2);
            termoTAU23=Math.pow((f_SIGMA_C_2/(2.0*TAU23)),2);
            resolvente_quadratica_termo_b=(1.0/(f_SIGMA_C_2))*(termoTAU23-1);
            a=resolvente_quadratica_termo_a;
            b=resolvente_quadratica_termo_b;
            radicando=(Math.pow(b,2))-4*a*(-1.0);
            if(radicando>=0)
            {
                preROOT_1=+Math.sqrt(radicando);
                preROOT_2=-Math.sqrt(radicando);
                ROOT_1=(-b+preROOT_1)/(2.0*a);
                ROOT_2=(-b+preROOT_2)/(2.0*a);
                //A raÃ­z negativa serÃ¡ considerada como a raÃ­z de interesse
                if(ROOT_1<=0)
                {
                    raiz_de_interesse=ROOT_1;
                }

                if(ROOT_2<=0)
                {
                    raiz_de_interesse=ROOT_2;
                }
            }
		    else
            {
                return "raiz_imaginaria";
            }

            //Pelo efeito do convÃªnio de sinal
            SIGMA_C_2_raiz=raiz_de_interesse;

            SIGMA_T_1_rotacionado=c2*SIGMA_T_1_raiz+s2*SIGMA_T_2_raiz;
            SIGMA_T_2_rotacionado=s2*SIGMA_T_1_raiz+c2*SIGMA_T_2_raiz;

            SIGMA_C_1_rotacionado=c2*(-f_SIGMA_C_1)+s2*(SIGMA_C_2_raiz);
            SIGMA_C_2_rotacionado=s2*(-f_SIGMA_C_1)+c2*(SIGMA_C_2_raiz);
        }

//vertice + +
        pares_pontos_x.add(SIGMA_T_1_rotacionado);
        pares_pontos_y.add(SIGMA_T_2_rotacionado);

//vertice - +
        pares_pontos_x.add(SIGMA_C_1_rotacionado);
        pares_pontos_y.add(SIGMA_T_2_rotacionado);

//vertice --
        pares_pontos_x.add(SIGMA_C_1_rotacionado);
        pares_pontos_y.add(SIGMA_C_2_rotacionado);

//vertice + -
        pares_pontos_x.add(SIGMA_T_1_rotacionado);
        pares_pontos_y.add(SIGMA_C_2_rotacionado);

        return "normal";
/////
    }
//
/*
* Quarta Outubro 31, 2018
* A biblioteca gráfica é problemática
*
* */
public void ordenar_quatro_pontos()
{
    Double separacao_tecnica_1=
            Math.abs
                    (
                            pares_pontos_x.get(1)
                                    -pares_pontos_x.get(0)
                    )*1.0e-3; //pegamos o primeiro elemento

    Double separacao_tecnica_2=
            Math.abs
                    (
                            pares_pontos_x.get(2)
                                    -pares_pontos_x.get(1)
                    )*1.0e-3; //pegamos o primeiro elemento

    Double separacao_tecnica=separacao_tecnica_1;

    if(separacao_tecnica_1<separacao_tecnica_2)
    {
        separacao_tecnica=separacao_tecnica_2;
    }

    //primeiramente evitamos que tenha um valor de X com multiplicidade
    for (int i = 0; i < pares_pontos_x.size(); i++) {
        for (int j = i + 1; j < pares_pontos_x.size(); j++) {
            Double diff = pares_pontos_x.get(i) - pares_pontos_x.get(j);

            if (diff == 0.0) {
                double separado=0.0;

                if(pares_pontos_x.get(i)<=0.0)
                {
                    separado=pares_pontos_x.get(i) + separacao_tecnica;
                }
                else
                {
                    separado=pares_pontos_x.get(i) - separacao_tecnica;
                }
                pares_pontos_x.set(j, separado);
            }
        }
    }
    //posteriormente ordenamos os valores.

    //Fazemos ordenamento por burbulha

    boolean ORDEM_ASCII=true;

    Double minimo=pares_pontos_x.get(0);

    for (Integer i = 0; i < pares_pontos_x.size(); i++)
    {
        if(pares_pontos_x.get(i)<minimo)
        {
            ORDEM_ASCII=false;
        }
        minimo=pares_pontos_x.get(i);
    }

    if(!ORDEM_ASCII)
    {
        Double menor_arco_1_x;
        Double menor_arco_1_y;
        Double TEMP_arco_1_x;
        Double TEMP_arco_1_y;
        for (Integer i = 0; i < pares_pontos_x.size(); i++)
        {
            menor_arco_1_x=pares_pontos_x.get(i);
            menor_arco_1_y=pares_pontos_y.get(i);
            for(Integer j=i+1;j<pares_pontos_x.size();j++)
            {

                if(pares_pontos_x.get(j)<menor_arco_1_x)
                {
                    TEMP_arco_1_x=pares_pontos_x.get(j);
                    pares_pontos_x.set(j,menor_arco_1_x);
                    pares_pontos_x.set(i,TEMP_arco_1_x);
//O mesmo para Y para assim manter o sistema ordenado
                    TEMP_arco_1_y=pares_pontos_y.get(j);
                    pares_pontos_y.set(j,menor_arco_1_y);
                    pares_pontos_y.set(i,TEMP_arco_1_y);
                    break;
                }
            }

        }
    }




    //Até aqui os pares (X,Y) podem podem ser representado, mas é preciso verificar que
    // a sequencia representa uma linha poligonal fechada,
    // para isso é apreciso verificar apenas uma aresta:
    // ou seja a aresta do 2do para o terceiro ponto

    //janeiro14
    // Desde o vertice segundo, vemos se ele está fazendo a aresta certa
    //calculamos distancia entre os vertices 2 e 3
    double distancia_2_3 = distancias(
            pares_pontos_x.get(1),
            pares_pontos_y.get(1),
            pares_pontos_x.get(2),
            pares_pontos_y.get(2)
    );

    double distancia_2_4 = distancias(
            pares_pontos_x.get(1),
            pares_pontos_y.get(1),
            pares_pontos_x.get(3),
            pares_pontos_y.get(3)
    );
    if (distancia_2_3 > distancia_2_4) {
        //fazemos a troca de 4 para 3
        double temporal_x = pares_pontos_x.get(2);
        double temporal_y = pares_pontos_y.get(2);

        //ponto 4 passa para ser 3

        pares_pontos_x.set(2, pares_pontos_x.get(3));
        pares_pontos_y.set(2, pares_pontos_y.get(3));

        //ponto 3 passa para ser 4

        pares_pontos_x.set(3, temporal_x);
        pares_pontos_y.set(3, temporal_y);
    }
////
}
//SUPORTE  Quarta Outubro 31, 2018

    public double distancias(
            double x1,
            double y1,
            double x2,
            double y2
    ) {
        double diff_x = x1 - x2;
        double diff_y = y1 - y2;
        return Math.sqrt(
                diff_x * diff_x +
                        diff_y * diff_y
        );
    }

///
public void usar_GraphView()
{
    GraphView graph = this.myActivity.findViewById(R.id.graph);
    graph.setVisibility(View.VISIBLE);

    graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
        @Override
        public String formatLabel(double value, boolean isValueX) {
            if (isValueX) {
                // show normal x values
                String transformado=String.format("%2.2e",value);
                //super.formatLabel(value, isValueX);

                if(value==0)
                {
                    return transformado+ " (Pa)";
                }
                else
                {
                    return "";
                }

            } else {
                // show currency for y values

                String transformado=String.format("%2.2e",value);
                return transformado + " (Pa)";

            }
        }
    });


    graph.removeAllSeries();
//sexta02 no caso de 4 pontos parece funcionar sem problema
    //transformando_deformacoes_esforcos(possiveis_pontos_x,possiveis_pontos_y); //funciona

//colocamos os valores extremos:

    Double minimo_x = pares_pontos_x.get(0);
//máximo X em ponto 3
    Double maximo_x = pares_pontos_x.get(pares_pontos_x.size()-1);


    List<Double> possiveis_pontos_y_clone = new ArrayList<Double>();
//clonamos Y
    for (int i = 0; i < pares_pontos_y.size(); i++) {
        possiveis_pontos_y_clone.add(pares_pontos_y.get(i));
    }

    //ordenamos
    Collections.sort(possiveis_pontos_y_clone);

    Double minimo_y = possiveis_pontos_y_clone.get(0);
//máximo em ponto 3
    Double maximo_y = possiveis_pontos_y_clone.get(pares_pontos_x.size() - 1);

    graph.getViewport().setMinX(minimo_x * 1.5);
    graph.getViewport().setMaxX(maximo_x * 1.5);

    graph.getViewport().setMinY(minimo_y * 1.5);
    graph.getViewport().setMaxY(maximo_y * 1.5);

//Sabemos que são apenas 4 pontos, e por tanto serão conetados de forma imediata

    LineGraphSeries<DataPoint> series_0 = new LineGraphSeries<DataPoint>(new DataPoint[]{
            new DataPoint(pares_pontos_x.get(0), pares_pontos_y.get(0)),
            new DataPoint(pares_pontos_x.get(1), pares_pontos_y.get(1)),
            new DataPoint(pares_pontos_x.get(2), pares_pontos_y.get(2)),
            new DataPoint(pares_pontos_x.get(3), pares_pontos_y.get(3))

    });

    LineGraphSeries<DataPoint> series_1 = new LineGraphSeries<DataPoint>(new DataPoint[]{
            new DataPoint(pares_pontos_x.get(0), pares_pontos_y.get(0)),
            new DataPoint(pares_pontos_x.get(3), pares_pontos_y.get(3)),
            //new DataPoint(possiveis_pontos_x.get(2), possiveis_pontos_y.get(2))
    });

    graph.getViewport().setXAxisBoundsManual(true);
    graph.getViewport().setYAxisBoundsManual(true);

    graph.getViewport().setScalable(true);
    graph.getViewport().setScalableY(true);

    graph.addSeries(series_0);
    graph.addSeries(series_1);
///////////////
}

/// ////////////////////////////////////
}
