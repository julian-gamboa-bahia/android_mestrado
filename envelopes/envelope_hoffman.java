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
import java.util.List;

import br.com.ven2020.envelopes2018.Config;
import br.com.ven2020.envelopes2018.R;
import br.com.ven2020.envelopes2018.database.DatabaseHelper;
import br.com.ven2020.envelopes2018.database.historico;

/**
 * Created by julian on 14/10/17.
 * //marco15
 */

public class envelope_hoffman {

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

    public envelope_hoffman(Context myContext, String lamina_usada, String criterio_usado,
                            String envelope_usado, String endereco, Activity myActivity,
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

        Double f_SIGMA_T_1= Double.parseDouble(SIGMA_T_1);
        //Log.d("Outubro05","\n"+SIGMA_T_2);
        //Float f_SIGMA_T_2= Float.parseFloat(SIGMA_T_2);
        Double f_SIGMA_T_2= Double.parseDouble(SIGMA_T_2);
        //Log.d("Outubro05","\n"+SIGMA_C_1);
        //Float f_SIGMA_C_1= Float.parseFloat(SIGMA_C_1);
        Double f_SIGMA_C_1= Double.parseDouble(SIGMA_C_1);
        //Log.d("Outubro05","\n"+SIGMA_C_2);
        //Float f_SIGMA_C_2= Float.parseFloat(SIGMA_C_2);
        Double f_SIGMA_C_2= Double.parseDouble(SIGMA_C_2);
        //Log.d("Outubro05","\n"+TAU12);
        //Float f_TAU12= Float.parseFloat(TAU12);
        Double f_TAU12= Double.parseDouble(TAU12);


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

        double tauXY=tau_xy;


        // COORDENADAS globais:

        // os efeitos da rotação da lamina:
        // daqui pa frente COORDENADAS GLOBAIS
        double  theta_radianos=(Math.PI/180)*angulo;


        double c=Math.cos(theta_radianos);
        double s=Math.sin(theta_radianos);

//Maio12: coeficientes em função das resistências:

    //  Coeficientes em função das resistências:

/*
    No original:
is1=((1/SIGMA_T_1)-(1/SIGMA_C_1));
is2=((1/SIGMA_T_2)-(1/SIGMA_C_2));
is1q=(1/SIGMA_T_1)*(1/SIGMA_C_1);
is2q=(1/SIGMA_T_2)*(1/SIGMA_C_2);
t12=(1/TAU12)*(1/TAU12);
F12=-(1/(2*SIGMA_T_1*SIGMA_C_1));
*/

    double is1=((1/f_SIGMA_T_1)-(1/f_SIGMA_C_1));
    double is2=((1/f_SIGMA_T_2)-(1/f_SIGMA_C_2));

    double is1q=(1/f_SIGMA_T_1)*(1/f_SIGMA_C_1);
    double is2q=(1/f_SIGMA_T_2)*(1/f_SIGMA_C_2);

    double t12=(1/f_TAU12)*(1/f_TAU12);
    double  F12=-(1/(2*f_SIGMA_T_1*f_SIGMA_C_1));

// Para Coeficiente a:

/* Original
fatorRotar_a=
+ Math.pow(cos,4)*is1q
+ Math.pow(cos,2)*Math.pow(sin,2)*t12
+ is2q*Math.pow(sin,4);
2*F12*Math.pow(cos,2)*Math.pow(sin,2)
* */

    double fatorRotar_a=
    Math.pow(c,4)*is1q                  //+ Math.pow(cos,4)*is1q
    + Math.pow(c,2)*Math.pow(s,2)*t12   //+ Math.pow(cos,2)*Math.pow(sin,2)*t12
    + Math.pow(s,4)*is2q         //+ + is2q*Math.pow(sin,4);
    +2*F12*Math.pow(c,2)*Math.pow(s,2);//2*F12*Math.pow(cos,2)*Math.pow(sin,2)

// Para Coeficiente c:
/*Original
fatorRotar_c=
+ Math.pow(cos,4)*is2q
+ Math.pow(cos,2)*Math.pow(sin,2)*t12
+ is1q*Math.pow(sin,4);
+ 2*F12*Math.pow(cos,2)*Math.pow(sin,2)
* */

    double 	fatorRotar_c=
    Math.pow(c,4)*is2q                  //+ Math.pow(cos,4)*is2q
    + Math.pow(c,2)*Math.pow(s,2)*t12   //+ Math.pow(cos,2)*Math.pow(sin,2)*t12
    + Math.pow(s,4)*is1q                //+ is1q*Math.pow(sin,4);
    +2*F12*Math.pow(c,2)*Math.pow(s,2); //+ 2*F12*Math.pow(cos,2)*Math.pow(sin,2)

// Para Coeficiente b:

/*Original
+ 2*Math.pow(cos,2)*is2q*Math.pow(sin,2)
+ 2*Math.pow(cos,2)*is1q*Math.pow(sin,2)
2*F12* Math.pow(cos,4)
+ 2*F12*Math.pow(sin,4)
- 2*Math.pow(cos,2)*Math.pow(sin,2)*t12
    */

    double fatorRotar_b=
    2*Math.pow(c,2)*Math.pow(s,2)*is2q              //+ 2*Math.pow(cos,2)*is2q*Math.pow(sin,2)
    +2*Math.pow(c,2)*Math.pow(s,2)*is1q             //+ 2*Math.pow(cos,2)*is1q*Math.pow(sin,2)
    +2*F12*Math.pow(c,4)                            //2*F12* Math.pow(cos,4)
    +Math.pow(s,4)*2*F12                            //+ 2*F12*Math.pow(sin,4)
    -2*Math.pow(c,2)*Math.pow(s,2)*t12;             //- 2*Math.pow(cos,2)*Math.pow(sin,2)*t12

//////////////////////////// Agora as potencias SIMPLES
/*Original
-4*F12*Math.pow(cos,3)*sin*tauXY
+ 4*F12*cos*Math.pow(sin,3)*tauXY
+ 4*Math.pow(cos,3)*is1q*sin*tauXY
- 2*Math.pow(cos,3)*sin*t12*tauXY
+ Math.pow(cos,2)*is1
- 4*cos*is2q*Math.pow(sin,3)*tauXY
+ 2*cos*Math.pow(sin,3)*t12*tauXY
 + is2*Math.pow(sin,2);
* */

    double fatorRotar_d=
    -4*F12*Math.pow(c,3)*s*tauXY        //-4*F12*Math.pow(cos,3)*sin*tauXY
    + 4*F12*c*Math.pow(s,3)*tauXY       //+ 4*F12*cos*Math.pow(sin,3)*tauXY
    + 4*Math.pow(c,3)*is1q*s*tauXY      //+ 4*Math.pow(cos,3)*is1q*sin*tauXY
    - 2*Math.pow(c,3)*s*t12*tauXY       //- 2*Math.pow(cos,3)*sin*t12*tauXY
    + Math.pow(c,2)*is1                 //+ Math.pow(cos,2)*is1
    - 4*c*is2q*Math.pow(s,3)*tauXY      //- 4*cos*is2q*Math.pow(sin,3)*tauXY
    + 2*c*Math.pow(s,3)*t12*tauXY       //+ 2*cos*Math.pow(sin,3)*t12*tauXY
    + is2*Math.pow(s,2);                //+ is2*Math.pow(sin,2);


    double fatorRotar_f=
    -4*F12*c*Math.pow(s,3)*tauXY    //-4*F12*cos*Math.pow(sin,3)*tauXY
    +4*F12*Math.pow(c,3)*s*tauXY    //+4*F12*Math.pow(cos,3)*sin*tauXY
    -4*Math.pow(c,3)*is2q*s*tauXY       //-4*Math.pow(cos,3)*is2q*sin*tauXY
    +2*Math.pow(c,3)*s*t12*tauXY        //+2*Math.pow(cos,3)*sin*t12*tauXY
    +Math.pow(c,2)*is2                //+Math.pow(cos,2)*is2
    +4*c*is1q*Math.pow(s,3)*tauXY       //+4*cos*is1q*Math.pow(sin,3)*tauXY
    -2*c*Math.pow(s,3)*t12*tauXY        //-2*cos*Math.pow(sin,3)*t12*tauXY
    +is1*Math.pow(s,2);                 //+is1*Math.pow(sin,2);

// Para Coeficiente g:

    double factorRotar_g=-1
    -2*c*is2*s*tauXY
    +4*Math.pow(c,2)*is1q*Math.pow(s,2)*Math.pow(tauXY,2)
    +4*Math.pow(c,2)*is2q*Math.pow(s,2)*Math.pow(tauXY,2)
    -2*Math.pow(c,2)*Math.pow(s,2)*t12*Math.pow(tauXY,2)
    +2*c*is1*s*tauXY
    +Math.pow(s,4)*t12*Math.pow(tauXY,2)
    -8*F12*Math.pow(c,2)*Math.pow(s,2)*Math.pow(tauXY,2)
    +Math.pow(c,4)*t12*Math.pow(tauXY,2);


        double d=(fatorRotar_d/2);
        double f=(fatorRotar_f/2);

        double g=factorRotar_g;


// no caso isotropico Poupamos calculo e fazemos apenas o estudo polar:

        double rotacionado=0.0;
        double x;

        double a=fatorRotar_a;
        c=fatorRotar_c;
        double b=(fatorRotar_b/2);

/*
        if (a!=c){
            if (c>a){
                x=(a-c)/(2*b);
                rotacionado=0.5*Inverse_cotan(x);
            }
            else
            {
                x=(a-c)/(2*b);
                rotacionado=(Math.PI/2)+0.5*Inverse_cotan(x);
            }
        }
        else
        {
            rotacionado=0;
        }
        */


 //Sendo preciso agora usar a formulação mais explicita

if(b!=0)
{
	double B=2*b;
	double D=2*d;
	double E=2*f;

	double A=a;
	double C=fatorRotar_c;

    double numerador=(C-A-Math.sqrt(Math.pow(A-C,2)+B*B));

	rotacionado=Math.atan(numerador/B);
}

    //Log.d("Maio11rotacionado","rotacionado   "+rotacionado);


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
    LineGraphSeries<DataPoint> series_superior=new LineGraphSeries<DataPoint>();

    //PointsGraphSeries<DataPoint> series_superior =new PointsGraphSeries<DataPoint>();

    Double[][] pares_pontos = new Double[numeroPontos][2];

//Fazemos a varredura de Izquerda para DIREITA , desde 0 para 2*Pi

    double ang=0;

    double passo=(2*Math.PI/numeroPontos);

    //rotacionado=0;

    Double coseno;
    Double seno;

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
        pares_pontos[i][0]=temporal_x;
        pares_pontos[i][1]=temporal_y;

        //Log.d("Maio16","http://127.0.0.1/maio/criterios/hoffman.htm?/108.0/4/00/"+temporal_x+"/"+temporal_y+"/0.00/0/undefined");
    }


         //Outubro 29
    /* Estranho

        for(int i=0; i<pares_pontos.length;i++)
        {
            Log.d("O29_HOFFMAN","  "+i+"  "+pares_pontos[i][0]);
        }
        */

    //
        //componemos a saída
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
        //
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


// ///////////////
}
