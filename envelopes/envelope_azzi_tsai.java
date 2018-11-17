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
import java.util.Collections;
import java.util.List;

import br.com.ven2020.envelopes2018.Config;
import br.com.ven2020.envelopes2018.R;
import br.com.ven2020.envelopes2018.database.DatabaseHelper;
import br.com.ven2020.envelopes2018.database.historico;

import br.com.ven2020.envelopes2018.Config;


/**
 * Created by julian on 14/10/17.
 * //marco15
 */

public class envelope_azzi_tsai {

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

    String angulo_global="";

    private int numero_pontos;

    Integer numeroPontos=Config.numeroPontos;

    public envelope_azzi_tsai(
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


    public double[][] fazer_grafica(Double angulo, Double tau_xy)
    {

        String agora=new Time(System.currentTimeMillis()).toString();

//Log no servidor
        angulo_global=angulo+"_"+agora;

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

        //Maio16: coeficientes dos termos:
//POSPOS
            calcular_coeficientesPOSPOS(cos,sin);
            gerar_pontos();
            filtro_calculo_conica_POSPOS(angulo,tau_xy);

//Antes de vetorizar deve TOTALIZAR
        for(Integer i=0;i<pares_pontos_x.size();i++)
        {
            pares_pontos_x_total.add(pares_pontos_x.get(i));
            pares_pontos_y_total.add(pares_pontos_y.get(i));
        }

//Outubro 19, 2018
        filtro_distancia();


        vetorizar_duas_series_LISTA(
                    pares_pontos_x,pares_pontos_y,
                    serie_POSPOS_1_x,serie_POSPOS_1_y);


        fazer_segunda_serie_LISTA(pares_pontos_x,pares_pontos_y,serie_POSPOS_2_x,serie_POSPOS_2_y);
//NEGPOS

    calcular_coeficientesNEGPOS(cos,sin);
    gerar_pontos();
    filtro_calculo_conica_NEGPOS(angulo,tau_xy);

        //Antes de vetorizar deve TOTALIZAR
        for(Integer i=0;i<pares_pontos_x.size();i++)
        {
            pares_pontos_x_total.add(pares_pontos_x.get(i));
            pares_pontos_y_total.add(pares_pontos_y.get(i));
        }

//Outubro 19, 2018
        filtro_distancia();

        vetorizar_duas_series_LISTA(
            pares_pontos_x,pares_pontos_y,
            serie_NEGPOS_1_x,serie_NEGPOS_1_y);

        fazer_segunda_serie_LISTA(pares_pontos_x,pares_pontos_y,serie_NEGPOS_2_x,serie_NEGPOS_2_y);
//NEGNEG
        calcular_coeficientesNEGNEG(cos,sin);
        gerar_pontos();
        filtro_calculo_conica_NEGNEG(angulo,tau_xy);

        //Antes de vetorizar deve TOTALIZAR
        for(Integer i=0;i<pares_pontos_x.size();i++)
        {
            pares_pontos_x_total.add(pares_pontos_x.get(i));
            pares_pontos_y_total.add(pares_pontos_y.get(i));
        }

//Outubro 19, 2018
        filtro_distancia();

        vetorizar_duas_series_LISTA(
                pares_pontos_x,pares_pontos_y,
                serie_NEGNEG_1_x,serie_NEGNEG_1_y);

        fazer_segunda_serie_LISTA(pares_pontos_x,pares_pontos_y,serie_NEGNEG_2_x,serie_NEGNEG_2_y);

//POSNEG
    calcular_coeficientesPOSNEG(cos,sin);
    gerar_pontos();
    filtro_calculo_conica_POSNEG(angulo,tau_xy);
//Antes de vetorizar deve TOTALIZAR
        for(Integer i=0;i<pares_pontos_x.size();i++)
        {
            pares_pontos_x_total.add(pares_pontos_x.get(i));
            pares_pontos_y_total.add(pares_pontos_y.get(i));
        }

//Outubro 19, 2018
        filtro_distancia();

    vetorizar_duas_series_LISTA(
            pares_pontos_x,pares_pontos_y,
            serie_POSNEG_1_x,serie_POSNEG_1_y);

    fazer_segunda_serie_LISTA(pares_pontos_x,pares_pontos_y,serie_POSNEG_2_x,serie_POSNEG_2_y);

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
            for(Integer i=0;i<serie_POSPOS_1_x.size(); i++)
            {
                serie_POSPOS_1_graph.appendData(new DataPoint(serie_POSPOS_1_x.get(i), serie_POSPOS_1_y.get(i)), true, 100);
            }
            graph.addSeries(serie_POSPOS_1_graph);
        }

        if(serie_POSPOS_2_x.size()>0)
        {
            for(Integer i=0;i<serie_POSPOS_2_x.size(); i++)
            {
                serie_POSPOS_2_graph.appendData(new DataPoint(serie_POSPOS_2_x.get(i), serie_POSPOS_2_y.get(i)), true, 100);
            }
            graph.addSeries(serie_POSPOS_2_graph);
        }
//NEGPOS
        if(serie_NEGPOS_1_x.size()>0)
        {
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
public void  calcular_coeficientesPOSPOS(Double cos,Double sin)
{
    //  a*x²+c*y²+2bxy

    Double Quadrado_t1=(1/f_SIGMA_T_1)*(1/f_SIGMA_T_1);
    Double Quadrado_t2=(1/f_SIGMA_T_2)*(1/f_SIGMA_T_2);
    Double Quadrado_t12=(1/f_TAU12)*(1/f_TAU12);

    // sigma_X^2

/*

a=
+c^{4} \sigma_{x}^{2} t_{1}^{2}
+ s^{4} \sigma_{x}^{2} t_{2}^{2}
- c^{2} s^{2} \sigma_{x}^{2} t_{1}^{2}
+ c^{2} s^{2} \sigma_{x}^{2} t_{12}^{2}
\\
*/

    Double fatorRotar_a=Math.pow(cos,4)*Quadrado_t1
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

    Double fatorRotar_c=Math.pow(sin,4)*Quadrado_t1
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

    Double fatorRotar_b=- Math.pow(cos,4)*Quadrado_t1
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

    Double fatorRotar_d=6*Math.pow(cos,3)*sin*Quadrado_t1*tauXY
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

    Double fatorRotar_f=2*Math.pow(cos,3)*sin*Quadrado_t12*tauXY
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

    Double factorRotar_g=8*Math.pow(cos,2)*Math.pow(sin,2)*Quadrado_t1
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


//////////////////////////////////////////
}
