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

/**
 * Created by julian on 14/10/17.
 * //marco15
 */

public class envelope_christensen {

    //Saídas
    private  ArrayList<Double> serie_1_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_1_y = new ArrayList<Double>();
    private  ArrayList<Double> serie_2_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_2_y = new ArrayList<Double>();

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

    Integer numeroPontos=Config.numeroPontos;

    Double[][] pares_pontos;

    public envelope_christensen(Context myContext,
                                String lamina_usada, String criterio_usado,
                                String envelope_usado,
                                String endereco, Activity myActivity,
                                int numero_pontos)
    {
        this.myContext=myContext;
        this.lamina_usada=lamina_usada;
        this.criterio_usado=criterio_usado;
        this.envelope_usado=envelope_usado;
        this.endereco=endereco;
        this.myActivity=myActivity;

        numeroPontos= Config.numeroPontos;

        if(numero_pontos<Config.numeroPontos*100)
        {
            numeroPontos=numero_pontos;
        }


        pares_pontos = new Double[this.numeroPontos][2];
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

public double[][] fazer_grafica(Double angulo, Double tau_xy)
{

        List<Double> saida_x = new ArrayList<Double>();
        List<Double> saida_y = new ArrayList<Double>();

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

//De uma forma mais densa,
// a função gerar_conica emite apenas aqueles pontos cujo IF seja unitário

        gerar_coeficientes_matriz(tau_xy,angulo);
        gerar_conica(tau_xy,angulo);

    /* Estranho
        for(int i=0; i<pares_pontos.length;i++)
        {
            Log.d("O29_CHRISTENSEN","  "+i+"  "+pares_pontos[i][0]);
        }
        */


/*
*
* Parte SUPEIOR do gráfico
*
* */
    LineGraphSeries<DataPoint> series_superior=new LineGraphSeries<DataPoint>();

        //componemos a saída
        //double[][] saida = new double[4+numeroPontos][2];
        double[][] saida = new double[4][2];
//Colocamos apenas os valores extremos

//maximo X
        int indice_temp_extremo=0;
        int indice_saida=0;
        double temp=0.0;
        for(int i=0;i<pares_pontos.length;i++)
        {
            if(temp<=pares_pontos[i][0]) //em X
            {
                indice_temp_extremo=i;
                temp=pares_pontos[i][0];
            }
        }
        saida[indice_saida][0]=pares_pontos[indice_temp_extremo][0];
        saida[indice_saida][1]=pares_pontos[indice_temp_extremo][1];
        indice_saida++;

    //Log.d("Maio11X","maximo X   "+indice_saida+"\n"+indice_temp_extremo);

//Minimo X
        indice_temp_extremo=0;
        temp=0;
        for(int i=0;i<pares_pontos.length;i++)
        {
            if(temp>=pares_pontos[i][0]) //em X
            {
                indice_temp_extremo=i;
                temp=pares_pontos[i][0];
            }
        }
        saida[indice_saida][0]=pares_pontos[indice_temp_extremo][0];
        saida[indice_saida][1]=pares_pontos[indice_temp_extremo][1];
        indice_saida++;

    //Log.d("Maio11X","Min X   "+indice_saida+"\n"+indice_temp_extremo);
//Maximo Y
        indice_temp_extremo=0;
        temp=0;
        for(int i=0;i<pares_pontos.length;i++)
        {
            if(temp<=pares_pontos[i][1]) //em Y
            {
                indice_temp_extremo=i;
                temp=pares_pontos[i][1];
            }
        }
        saida[indice_saida][0]=pares_pontos[indice_temp_extremo][0];
        saida[indice_saida][1]=pares_pontos[indice_temp_extremo][1];
        indice_saida++;
//Minimo Y
        indice_temp_extremo=0;
        temp=0;
        for(int i=0;i<pares_pontos.length;i++)
        {
            if(temp>=pares_pontos[i][1]) //em Y
            {
                indice_temp_extremo=i;
                temp=pares_pontos[i][1];
            }
        }
        saida[indice_saida][0]=pares_pontos[indice_temp_extremo][0];
        saida[indice_saida][1]=pares_pontos[indice_temp_extremo][1];
        indice_saida++;

/*
//colocamos a informação que complementa os primeiros 4 extremos
        for(Integer i=0;i<pares_pontos.length;i++)
        {
            //Log.d("maio22","i\n"+i);
            saida[i+4][0]=pares_pontos[i][0];
            saida[i+4][1]=pares_pontos[i][1];
        }
*/
        vetorizar_duas_series(pares_pontos);

        LineGraphSeries<DataPoint> series_inferior =new LineGraphSeries<DataPoint>();

        for(Integer i=0;i<serie_1_x.size(); i++)
        {
            series_inferior.appendData(new DataPoint(serie_1_x.get(i), serie_1_y.get(i)), true, 100);
//Log.d("PLOT",);
        }

//Para se conetar com a curva anterior
        series_superior.appendData(new DataPoint(serie_1_x.get(0), serie_1_y.get(0)), true, 100);

        for(Integer i=0;i<serie_2_x.size(); i++)
        {
            series_superior.appendData(new DataPoint(serie_2_x.get(i), serie_2_y.get(i)), true, 100);
//Log.d("PLOT",);
        }

        series_superior.appendData(new DataPoint(serie_1_x.get(serie_1_x.size()-1), serie_1_y.get(serie_1_x.size()-1)), true, 100);

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

        series_superior.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(myActivity, "Envelope: "+dataPoint, Toast.LENGTH_SHORT).show();
            }
        });
        //graph.addSeries(series_0);
        graph.addSeries(series_superior);
        graph.addSeries(series_inferior);

        graph.clearSecondScale();

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
//Outubro29
    public void vetorizar_duas_series(Double[][] pares_pontos)
    {

         maiorX=pares_pontos[0][0];
         maiorY=pares_pontos[0][1];

         menorX=pares_pontos[0][0];
         menorY=pares_pontos[0][1];

//Posições

        Integer posicao_aiorX=0;
        Integer posicao_aiorY=0;

        Integer posicao_enorX=0;
        Integer posicao_enorY=0;

        for(Integer i=0;i<pares_pontos.length;i++) {
            //Log.d("Outubro29",i+"\n"+pares_pontos[i][0]+" "+pares_pontos[i][1]);
        }

        for(Integer i=0;i<pares_pontos.length;i++)
        {


            if(maiorX<pares_pontos[i][0])
            {
                maiorX=pares_pontos[i][0];
                posicao_aiorX=i;
            }

            if(maiorY<pares_pontos[i][1])
            {
                maiorY=pares_pontos[i][1];
                posicao_aiorY=i;
            }

            if(menorX>pares_pontos[i][0])
            {
                menorX=pares_pontos[i][0];
                posicao_enorX=i;
            }

            if(menorY>pares_pontos[i][1])
            {
                menorY=pares_pontos[i][1];
                posicao_enorY=i;
            }
        }
// A partir das posições
                          /*
        Log.d("Outubro29","\n"+
        maiorX+"  "+pares_pontos[posicao_aiorX][0]+"    "+posicao_aiorX+" X \n"+
        maiorY+"  "+pares_pontos[posicao_aiorY][1]+"    "+posicao_aiorY+" Y \n"+
        menorX+"  "+pares_pontos[posicao_enorX][0]+"    "+posicao_enorX+" X \n"+
        menorY+"  "+pares_pontos[posicao_enorY][1]+"    "+posicao_enorY+" Y \n"
        );
                            */
        Integer i_vetor=posicao_enorX;
        Integer i_vetor_seguinte=i_vetor+1;

        do
        {
            //Log.d("outubro2917","\n"+pares_pontos[i_vetor][0]+"  "+pares_pontos[i_vetor][1]+"   "+i_vetor);

            serie_1_x.add(pares_pontos[i_vetor][0]);
            serie_1_y.add(pares_pontos[i_vetor][1]);

            pares_pontos[i_vetor][0]=null;
            pares_pontos[i_vetor][1]=null;

            i_vetor=i_vetor+1;
            i_vetor_seguinte=1+i_vetor;

            if(i_vetor>=pares_pontos.length)
            {
                i_vetor=i_vetor-pares_pontos.length;

            }

            if(i_vetor_seguinte>=pares_pontos.length)
            {
                i_vetor_seguinte=i_vetor_seguinte-pares_pontos.length;

            }
        }  while (pares_pontos[i_vetor][0]<=pares_pontos[i_vetor_seguinte][0]);

        //Mesmo saindo do While ele coloco o ultimo

        //Log.d("outubro2917","\n"+pares_pontos[i_vetor][0]+"  "+pares_pontos[i_vetor][1]+"------------"+i_vetor);


        serie_1_x.add(pares_pontos[i_vetor][0]);
        serie_1_y.add(pares_pontos[i_vetor][1]);

        pares_pontos[i_vetor][0]=null;
        pares_pontos[i_vetor][1]=null;


//Por geometria sabemos que o restante será a curva supeperior
        //Log.d("outubro2917777","Ordenado\n"+serie_1_x.toString());
        //Log.d("outubro2917777","Ordenado\n"+serie_1_y.toString());
//Com os elementos restantes representamos na outra serie
//Mas antes de obter novos Minims para determinar uma sequencia

        fazer_segunda_serie(pares_pontos);
///
    }

//Outubro 26: Cuidado parte deste vector já possui elementos nulos
    public void fazer_segunda_serie(Double[][] pares_pontos)
    {

        double maiorX=0.0;
        double menorX=0.0;

        //Trasnformamos aquele vetor inicial (que contem NULOS) em uma lista:

        ArrayList<Double> transformada_x = new ArrayList<Double>();
        ArrayList<Double> transformada_y = new ArrayList<Double>();

        for(Integer i=0;i<pares_pontos.length;i++)
        {
            if(pares_pontos[i][0]!=null) {
                transformada_x.add(pares_pontos[i][0]);
                transformada_y.add(pares_pontos[i][1]);
            }
        }
//Posições,
// Por trigonometria sabemos que apenas é preciso fazer burbulha em X
/*
        Integer posicao_MaiorX=0;
        Integer posicao_menorX=0;
//Obtemos os extremos em X
        for(Integer i=0;i<transformada_x.size();i++)
        {
            if(maiorX<transformada_x.get(i))
                {
                    maiorX=transformada_x.get(i);
                    posicao_MaiorX=i;
                }

            if(menorX>transformada_x.get(i))
                {
                    menorX=transformada_x.get(i);
                    posicao_menorX=i;
                }

        }


        Log.d("Outubro29","\n"+
                maiorX+"  "+transformada_x.get(posicao_MaiorX)+"    "+posicao_MaiorX+" X \n"+
                menorX+"  "+transformada_x.get(posicao_menorX)+"    "+posicao_menorX+" X \n"
        );

        Log.d("outubro2917777","transformada\n"+transformada_x.toString());

        do
        {
            //Log.d("outubro2917","\n"+pares_pontos[i_vetor][0]+"  "+pares_pontos[i_vetor][1]+"   "+i_vetor);
            if(pares_pontos[i_vetor][0]!=null) {
                serie_1_x.add(pares_pontos[i_vetor][0]);
                serie_1_y.add(pares_pontos[i_vetor][1]);

                pares_pontos[i_vetor][0] = null;
                pares_pontos[i_vetor][1] = null;
            }
                i_vetor = i_vetor + 1;
                i_vetor_seguinte = 1 + i_vetor;

                if (i_vetor >= pares_pontos.length) {
                    i_vetor = i_vetor - pares_pontos.length;

                }

                if (i_vetor_seguinte >= pares_pontos.length) {
                    i_vetor_seguinte = i_vetor_seguinte - pares_pontos.length;

                }
        }  while (pares_pontos[i_vetor][0]<=pares_pontos[i_vetor_seguinte][0]);

        //Mesmo saindo do While ele coloco o ultimo

        Log.d("outubro2917","\n"+pares_pontos[i_vetor][0]+"  "+pares_pontos[i_vetor][1]+"------------"+i_vetor);


        serie_1_x.add(pares_pontos[i_vetor][0]);
        serie_1_y.add(pares_pontos[i_vetor][1]);

        pares_pontos[i_vetor][0]=null;
        pares_pontos[i_vetor][1]=null;
        */
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
            //Log.d("outubro2917","\n"+transformada_x.get(i)+"       "+transformada_y.get(i));
            serie_2_x.add(transformada_x.get(i));
            serie_2_y.add(transformada_y.get(i));
        }
    }
//Marco15
    /*
        //Saídas
    private  ArrayList<Double> serie_1_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_1_y = new ArrayList<Double>();
    private  ArrayList<Double> serie_2_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_2_y = new ArrayList<Double>();
    * */

//maio21

    double a;
    double c;
    double b;

    double d;
    double f;

    double g;


public void gerar_coeficientes_matriz(double tau_xy,double angulo)
{
    double tauXY=tau_xy;
    double  theta_radianos=(Math.PI/180)*angulo;
    double cos=Math.cos(theta_radianos);
    double sin=Math.sin(theta_radianos);

//Maio21: coeficientes :

    // Coeficientes da Matriz
    double    a_m=(Math.pow(cos,2)*Math.pow(sin,2))/(Math.pow(f_TAU12,2))+(Math.pow(sin,4))/(f_SIGMA_T_2*f_SIGMA_C_2);
    double    c_m=(Math.pow(cos,2)*Math.pow(sin,2))/(Math.pow(f_TAU12,2))+(Math.pow(cos,4))/(f_SIGMA_T_2*f_SIGMA_C_2);
    double    DUPLO_b_m=(2*Math.pow(cos,2)*Math.pow(sin,2))/(f_SIGMA_T_2*f_SIGMA_C_2)-(2*Math.pow(cos,2)*Math.pow(sin,2))/(Math.pow(f_TAU12,2));
    double    DUPLO_d_m=-(2*sin*Math.pow(cos,3)*tauXY)/(Math.pow(f_TAU12,2))+(2*cos*Math.pow(sin,3)*tauXY)/(Math.pow(f_TAU12,2))-(4*cos*tauXY*Math.pow(sin,3))/(f_SIGMA_T_2*f_SIGMA_C_2)+ Math.pow(sin,2)/f_SIGMA_T_2- Math.pow(sin,2)/f_SIGMA_C_2;
    double    DUPLO_f_m=-(2*cos*Math.pow(sin,3)*tauXY)/(Math.pow(f_TAU12,2)) +(2*sin*Math.pow(cos,3)*tauXY)/(Math.pow(f_TAU12,2))-(4*sin*tauXY*Math.pow(cos,3))/(f_SIGMA_T_2*f_SIGMA_C_2) + Math.pow(cos,2)/f_SIGMA_T_2- Math.pow(cos,2)/f_SIGMA_C_2;
    double    g_m=-1+ (Math.pow(sin,4)*Math.pow(tauXY,2))/(Math.pow(f_TAU12,2))-(2*Math.pow(cos,2)*Math.pow(sin,2)*Math.pow(tauXY,2))/(Math.pow(f_TAU12,2)) + (4*Math.pow(cos,2)*Math.pow(sin,2)*Math.pow(tauXY,2))/(f_SIGMA_T_2*f_SIGMA_C_2)+(Math.pow(cos,4)*Math.pow(tauXY,2))/(Math.pow(f_TAU12,2))  + (2*sin*cos*tauXY)/f_SIGMA_C_2- (2*sin*cos*tauXY)/f_SIGMA_T_2;


     a=a_m;
     c=c_m;
     b=(DUPLO_b_m/2);

     d=(DUPLO_d_m/2);
     f=(DUPLO_f_m/2);

     g=g_m;

}

/*
Tendo os 5 coeficientes , pode-se gerar uma curva cônica.

* */

public void gerar_conica(double tau_xy,double angulo)
{



    double rotacionado=0.0;
    double x;

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

//Fazemos a varredura de Izquerda para DIREITA , desde 0 para 2*Pi

    double ang=0;

    double passo=(2*Math.PI/numeroPontos);

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

        ang=ang+passo;
//adicionado apenas se for unitário
        double valor_if=if_christensen(tau_xy,angulo,temporal_x,temporal_y);
        double tolerancia=0.01;

        if(
                (valor_if<(1+tolerancia)) &&
                (valor_if>(1-tolerancia))
                )
        {
            pares_pontos[i][0]=temporal_x;
            pares_pontos[i][1]=temporal_y;
        }
        else
        {
            if(i>=1)
            {
                pares_pontos[i][0]=pares_pontos[i-1][0];
                pares_pontos[i][1]=pares_pontos[i-1][1];
            }
            else
            {
                pares_pontos[i][0]=0.0;
                pares_pontos[i][1]=0.0;
            }
        }
    }
}

/*
criterio propriamente dito

* */

public double if_christensen(double tau_xy,double angulo,double sigma_x,double sigma_y)
{

    double theta_radianos=(Math.PI/180)*angulo;

    Double c=Math.cos(theta_radianos);
    Double s=Math.sin(theta_radianos);
    Double c2=c*c;
    Double s2=s*s;

    Double sigma_1=c2*sigma_x+s2*sigma_y+2*c*s*tau_xy;
    Double sigma_2=s2*sigma_x+c2*sigma_y-2*c*s*tau_xy;
    Double tau_12=-s*c*sigma_x+s*c*sigma_y+(c2-s2)*tau_xy;

//Outubro11

    Double if_fibra;
    Double if_matriz;

    if_fibra=
            sigma_1*((1.0/f_SIGMA_T_1) - (1.0/f_SIGMA_C_1))
                    +(sigma_1*sigma_1)/(f_SIGMA_C_1*f_SIGMA_T_1);


//Matriz

    if_matriz =
            sigma_2*((1.0/f_SIGMA_T_2) - (1.0/f_SIGMA_C_2))
                    +(sigma_2*sigma_2)/(f_SIGMA_C_2*f_SIGMA_T_2)
                    +(tau_12*tau_12)/(f_TAU12*f_TAU12);

    double tolerancia=0.01;

    if(if_matriz>if_fibra)
    {
        return if_matriz;
    }
    else
    {
        return if_fibra;
    }
}

//maio22

    List<Double> possiveis_pontos_x = new ArrayList<Double>();
    List<Double> possiveis_pontos_y = new ArrayList<Double>();

    public double[][] rotacao_pi_2(Double angulo, Double tau_xy) {


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

    double theta_radianos=(Math.PI/180)*angulo;

    double cos=Math.cos(theta_radianos);
    double sin=Math.sin(theta_radianos);

    double c2=cos*cos;
    double s2=sin*sin;

    double SIGMA_T_1_rotacionado=c2*f_SIGMA_T_1+s2*f_SIGMA_T_2;
    double SIGMA_C_1_rotacionado=c2*f_SIGMA_C_1+s2*f_SIGMA_C_2;
    SIGMA_C_1_rotacionado=-SIGMA_C_1_rotacionado;

    double SIGMA_T_2_rotacionado=s2*f_SIGMA_T_1+c2*f_SIGMA_T_2;
    double SIGMA_C_2_rotacionado=s2*f_SIGMA_C_1+c2*f_SIGMA_C_2;
    SIGMA_C_2_rotacionado=-SIGMA_C_2_rotacionado;

    maiorX=SIGMA_T_1_rotacionado*1.5;
    maiorY=SIGMA_T_2_rotacionado*1.5;

    menorX=SIGMA_C_1_rotacionado*1.5;
    menorY=SIGMA_C_2_rotacionado*1.5;

    Double[][] pares_pontos_rotacao_pi_2= new Double[4][2];

    Integer indice=0;
    //vertice + +
    pares_pontos_rotacao_pi_2[indice][0]=SIGMA_T_1_rotacionado;
    pares_pontos_rotacao_pi_2[indice][1]=SIGMA_T_2_rotacionado;
    indice++;
//vertice - +
    pares_pontos_rotacao_pi_2[indice][0]=SIGMA_C_1_rotacionado;
    pares_pontos_rotacao_pi_2[indice][1]=SIGMA_T_2_rotacionado;
    indice++;
//vertice --
    pares_pontos_rotacao_pi_2[indice][0]=SIGMA_C_1_rotacionado;
    pares_pontos_rotacao_pi_2[indice][1]=SIGMA_C_2_rotacionado;
    indice++;
//vertice + -
    pares_pontos_rotacao_pi_2[indice][0]=SIGMA_T_1_rotacionado;
    pares_pontos_rotacao_pi_2[indice][1]=SIGMA_C_2_rotacionado;
    indice++;
    //componemos a saída
    double[][] saida = new double[4][2];
//Colocamos apenas os valores extremos

//maximo X
    int indice_temp_extremo=0;
    int indice_saida=0;
    double temp=0.0;
    for(int i=0;i<pares_pontos_rotacao_pi_2.length;i++)
    {
        if(temp<=pares_pontos_rotacao_pi_2[i][0]) //em X
        {
            indice_temp_extremo=i;
            temp=pares_pontos_rotacao_pi_2[i][0];
        }
    }
    saida[indice_saida][0]=pares_pontos_rotacao_pi_2[indice_temp_extremo][0];
    saida[indice_saida][1]=pares_pontos_rotacao_pi_2[indice_temp_extremo][1];
    indice_saida++;

//Minimo X
    indice_temp_extremo=0;
    temp=0;
    for(int i=0;i<pares_pontos_rotacao_pi_2.length;i++)
    {
        if(temp>=pares_pontos_rotacao_pi_2[i][0]) //em X
        {
            indice_temp_extremo=i;
            temp=pares_pontos_rotacao_pi_2[i][0];
        }
    }
    saida[indice_saida][0]=pares_pontos_rotacao_pi_2[indice_temp_extremo][0];
    saida[indice_saida][1]=pares_pontos_rotacao_pi_2[indice_temp_extremo][1];
    indice_saida++;

//Maximo Y
    indice_temp_extremo=0;
    temp=0;
    for(int i=0;i<pares_pontos_rotacao_pi_2.length;i++)
    {
        if(temp<=pares_pontos_rotacao_pi_2[i][1]) //em Y
        {
            indice_temp_extremo=i;
            temp=pares_pontos_rotacao_pi_2[i][1];
        }
    }
    saida[indice_saida][0]=pares_pontos_rotacao_pi_2[indice_temp_extremo][0];
    saida[indice_saida][1]=pares_pontos_rotacao_pi_2[indice_temp_extremo][1];
    indice_saida++;
//Minimo Y
    indice_temp_extremo=0;
    temp=0;
    for(int i=0;i<pares_pontos_rotacao_pi_2.length;i++)
    {
        if(temp>=pares_pontos_rotacao_pi_2[i][1]) //em Y
        {
            indice_temp_extremo=i;
            temp=pares_pontos_rotacao_pi_2[i][1];
        }
    }
    saida[indice_saida][0]=pares_pontos_rotacao_pi_2[indice_temp_extremo][0];
    saida[indice_saida][1]=pares_pontos_rotacao_pi_2[indice_temp_extremo][1];
    indice_saida++;

        for(Integer i=0;i<pares_pontos_rotacao_pi_2.length;i++)
        {
            possiveis_pontos_x.add(pares_pontos_rotacao_pi_2[i][0]);
            possiveis_pontos_y.add(pares_pontos_rotacao_pi_2[i][1]);
        }


        ordenar_quatro_pontos();
        usar_GraphView();
        return saida;
    }
//Maio22
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
//Maio22
//Usado apenas quando for preciso representar 4 pontos

    public void usar_GraphView()
    {
        GraphView graph = this.myActivity.findViewById(R.id.graph);
        graph.setVisibility(View.VISIBLE);

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


        graph.removeAllSeries();
//sexta02 no caso de 4 pontos parece funcionar sem problema
        //transformando_deformacoes_esforcos(possiveis_pontos_x,possiveis_pontos_y); //funciona

//colocamos os valores extremos:

        Double minimo_x = possiveis_pontos_x.get(0);
//máximo X em ponto 3
        Double maximo_x = possiveis_pontos_x.get(possiveis_pontos_x.size()-1/*-1*/);

        List<Double> possiveis_pontos_y_clone = new ArrayList<Double>();
//clonamos Y
        for (int i = 0; i < possiveis_pontos_y.size(); i++) {
            possiveis_pontos_y_clone.add(possiveis_pontos_y.get(i));
        }

        //ordenamos
        Collections.sort(possiveis_pontos_y_clone);

        Double minimo_y = possiveis_pontos_y_clone.get(0);
//máximo em ponto 3
        Double maximo_y = possiveis_pontos_y_clone.get(possiveis_pontos_x.size() - 1);

        graph.getViewport().setMinX(minimo_x * 1.5);
        graph.getViewport().setMaxX(maximo_x * 1.5);

        graph.getViewport().setMinY(minimo_y * 1.5);
        graph.getViewport().setMaxY(maximo_y * 1.5);

//Sabemos que são apenas 4 pontos, e por tanto serão conetados de forma imediata

        LineGraphSeries<DataPoint> series_0 = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(possiveis_pontos_x.get(0), possiveis_pontos_y.get(0)),
                new DataPoint(possiveis_pontos_x.get(1), possiveis_pontos_y.get(1)),
                new DataPoint(possiveis_pontos_x.get(2), possiveis_pontos_y.get(2)),
                new DataPoint(possiveis_pontos_x.get(3), possiveis_pontos_y.get(3))

        });

        LineGraphSeries<DataPoint> series_1 = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(possiveis_pontos_x.get(0), possiveis_pontos_y.get(0)),
                new DataPoint(possiveis_pontos_x.get(3), possiveis_pontos_y.get(3)),
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
//Maio22
public void ordenar_quatro_pontos()
{
    Double separacao_tecnica_1=
            Math.abs
                    (
                            possiveis_pontos_x.get(1)
                                    -possiveis_pontos_x.get(0)
                    )*1.0e-3; //pegamos o primeiro elemento

    Double separacao_tecnica_2=
            Math.abs
                    (
                            possiveis_pontos_x.get(2)
                                    -possiveis_pontos_x.get(1)
                    )*1.0e-3; //pegamos o primeiro elemento

    Double separacao_tecnica=separacao_tecnica_1;

    if(separacao_tecnica_1<separacao_tecnica_2)
    {
        separacao_tecnica=separacao_tecnica_2;
    }



    //primeiramente evitamos que tenha um valor de X com multiplicidade
    for (int i = 0; i < possiveis_pontos_x.size(); i++) {
        for (int j = i + 1; j < possiveis_pontos_x.size(); j++) {
            Double diff = possiveis_pontos_x.get(i) - possiveis_pontos_x.get(j);

            if (diff == 0.0) {
                possiveis_pontos_x.set(j, possiveis_pontos_x.get(i) + separacao_tecnica);
            }
        }
    }
    //posteriormente ordenamos os valores.

    //Fazemos ordenamento por burbulha

    boolean ORDEM_ASCII=true;

    Double minimo=possiveis_pontos_x.get(0);

    for (Integer i = 0; i < possiveis_pontos_x.size(); i++)
    {
        if(possiveis_pontos_x.get(i)<minimo)
        {
            ORDEM_ASCII=false;
        }
        minimo=possiveis_pontos_x.get(i);
    }

    if(!ORDEM_ASCII)
    {
        Double menor_arco_1_x;
        Double menor_arco_1_y;
        Double TEMP_arco_1_x;
        Double TEMP_arco_1_y;
        for (Integer i = 0; i < possiveis_pontos_x.size(); i++)
        {
            menor_arco_1_x=possiveis_pontos_x.get(i);
            menor_arco_1_y=possiveis_pontos_y.get(i);
            for(Integer j=i+1;j<possiveis_pontos_x.size();j++)
            {

                if(possiveis_pontos_x.get(j)<menor_arco_1_x)
                {
                    TEMP_arco_1_x=possiveis_pontos_x.get(j);
                    possiveis_pontos_x.set(j,menor_arco_1_x);
                    possiveis_pontos_x.set(i,TEMP_arco_1_x);
//O mesmo para Y para assim manter o sistema ordenado
                    TEMP_arco_1_y=possiveis_pontos_y.get(j);
                    possiveis_pontos_y.set(j,menor_arco_1_y);
                    possiveis_pontos_y.set(i,TEMP_arco_1_y);
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
            possiveis_pontos_x.get(1),
            possiveis_pontos_y.get(1),
            possiveis_pontos_x.get(2),
            possiveis_pontos_y.get(2)
    );

    double distancia_2_4 = distancias(
            possiveis_pontos_x.get(1),
            possiveis_pontos_y.get(1),
            possiveis_pontos_x.get(3),
            possiveis_pontos_y.get(3)
    );
    if (distancia_2_3 > distancia_2_4) {
        //fazemos a troca de 4 para 3
        double temporal_x = possiveis_pontos_x.get(2);
        double temporal_y = possiveis_pontos_y.get(2);

        //ponto 4 passa para ser 3

        possiveis_pontos_x.set(2, possiveis_pontos_x.get(3));
        possiveis_pontos_y.set(2, possiveis_pontos_y.get(3));

        //ponto 3 passa para ser 4

        possiveis_pontos_x.set(3, temporal_x);
        possiveis_pontos_y.set(3, temporal_y);
    }
    ////Log.d("Janeiro14","distancia_2_3 \n"+distancia_2_3+"\n"+distancia_2_4);
    //Log.d("Janeiro15", "x \n" + possiveis_pontos_x +        "\ny\n" + possiveis_pontos_y);
//Esclarecido
//////////////////
}

// /////////////////////////
}
