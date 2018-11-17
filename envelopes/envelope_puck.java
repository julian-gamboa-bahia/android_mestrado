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

public class envelope_puck {

    //Saídas
    private  ArrayList<Double> serie_1_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_1_y = new ArrayList<Double>();
    private  ArrayList<Double> serie_2_x = new ArrayList<Double>();
    private  ArrayList<Double> serie_2_y = new ArrayList<Double>();
//Extremos
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

    public envelope_puck(Context myContext,
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

//Para puck
    Double f_LARC03_EPSILON_T_1;
    Double f_LARC03_E1;
    Double f_LARC03_E2;
    Double f_NU12;
    Double f_G12;
    Double f_GAMMA12;

    Double  f_EPSILON_C_1;


    /*
    * Novembro 01
    * Sendo preciso localizar a grid em função do critério de Larc03
    * */

    public void passo_inicial_DB(double angulo)
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

        //larc03
        String LARC03_EPSILON_T_1=list.get(19); //getString(19)));//EPSILON_T_1`	TEXT,
        String LARC03_E1=list.get(3); //ring(3)) );//E1`	TEXT,
        String LARC03_E2=list.get(4); //ing(4)) );//E2`	TEXT,
        String NU12=list.get(9); //.getString(9)));//NU12`	TEXT,
        String G12=list.get(6); //e(cursor.getString(6)));//G12`	TEXT,
        String GAMMA12=list.get(23); //cursor.getString(23)));//GAMMA12`	TEXT,
        String EPSILON_C_1=list.get(21);//21)));//EPSILON_C_1`	TEXT,

        SIGMA_T_1=SIGMA_T_1.substring(SIGMA_T_1.indexOf(":")+1,SIGMA_T_1.length());
        SIGMA_T_2=SIGMA_T_2.substring(SIGMA_T_2.indexOf(":")+1,SIGMA_T_2.length());
        SIGMA_C_1=SIGMA_C_1.substring(SIGMA_C_1.indexOf(":")+1,SIGMA_C_1.length());
        SIGMA_C_2=SIGMA_C_2.substring(SIGMA_C_2.indexOf(":")+1,SIGMA_C_2.length());
        TAU12=TAU12.substring(TAU12.indexOf(":")+1,TAU12.length());
        G12=G12.substring(G12.indexOf(":")+1,G12.length());
        GAMMA12=GAMMA12.substring(GAMMA12.indexOf(":")+1,GAMMA12.length());

        //puck
        LARC03_EPSILON_T_1=LARC03_EPSILON_T_1.substring(LARC03_EPSILON_T_1.indexOf(":")+1,LARC03_EPSILON_T_1.length());
        LARC03_E1=LARC03_E1.substring(LARC03_E1.indexOf(":")+1,LARC03_E1.length());
        LARC03_E2=LARC03_E2.substring(LARC03_E2.indexOf(":")+1,LARC03_E2.length());
        NU12=NU12.substring(NU12.indexOf(":")+1,NU12.length());
        G12=G12.substring(G12.indexOf(":")+1,G12.length());
        EPSILON_C_1=EPSILON_C_1.substring(EPSILON_C_1.indexOf(":")+1,EPSILON_C_1.length());


        f_SIGMA_T_1= Double.parseDouble(SIGMA_T_1);
        f_SIGMA_T_2= Double.parseDouble(SIGMA_T_2);
        f_SIGMA_C_1= Double.parseDouble(SIGMA_C_1);
        f_SIGMA_C_2= Double.parseDouble(SIGMA_C_2);
        f_TAU12= Double.parseDouble(TAU12);
        f_GAMMA12=Double.parseDouble(GAMMA12);
        f_EPSILON_C_1=Double.parseDouble(EPSILON_C_1);

        //puck
        f_LARC03_EPSILON_T_1=Double.parseDouble(LARC03_EPSILON_T_1);
        f_LARC03_E1=Double.parseDouble(LARC03_E1);
        f_LARC03_E2=Double.parseDouble(LARC03_E2);
        f_NU12=Double.parseDouble(NU12);
        f_G12=Double.parseDouble(G12);

    }

    ArrayList<Double> pontos_x=new ArrayList<>();
    ArrayList<Double> pontos_y=new ArrayList<>();


    int global_puck_numero_circulos=0;
    double global_puck_m_sigF=0.0;
    int global_puck_numero_elementos_circulos=0;
    double global_puck_p_plus_TL=0.0;
    double global_puck_p_minus_TL=0.0;
    double global_puck_p_minus_TT=0.0;
    double global_puck_sigma_1_D=0.0;
    double global_puck_puck_R_TT_A=0.0;
    double global_puck_TAU12_C=0.0;
    double global_puck_NU12_f=0.0;
    double global_puck_E1_f=0.0;

    public double[][] fazer_grafica(Double angulo, Double tau_xy,
                                    int puck_numero_circulos,
                                    double puck_m_sigF,
                                    int puck_numero_elementos_circulos,
                                    double puck_p_plus_TL,
                                    double puck_p_minus_TL,
                                    double puck_p_minus_TT,
                                    double puck_sigma_1_D,
                                    double puck_puck_R_TT_A,
                                    double puck_TAU12_C,
                                    double puck_NU12_f,
                                    double puck_E1_f
    ) {

        global_puck_numero_circulos=puck_numero_circulos;
        global_puck_m_sigF=puck_m_sigF;
        global_puck_numero_elementos_circulos=puck_numero_elementos_circulos;
        global_puck_p_plus_TL=puck_p_plus_TL;
        global_puck_p_minus_TL=puck_p_minus_TL;
        global_puck_p_minus_TT=puck_p_minus_TT;
        global_puck_sigma_1_D=puck_sigma_1_D;
        global_puck_puck_R_TT_A=puck_puck_R_TT_A;
        global_puck_TAU12_C=puck_TAU12_C;
        global_puck_NU12_f=puck_NU12_f;
        global_puck_E1_f=puck_E1_f;

        passo_inicial_DB(angulo);

        Double[][] PRE_saida = new Double[4][2];
        double[][] saida = new double[4][2];

        PRE_saida=obter_centro_grid(angulo,tau_xy);

        for(int i=0;i<4;i++)
        {
            saida[i][0]=PRE_saida[i][0];
            saida[i][1]=PRE_saida[i][1];
        }

        return saida;
    }
//////

    public Double[][] obter_centro_grid(double angulo,double tau_xy)
    {
        estudando_segmento_02(angulo,tau_xy);
        estudando_segmento_01(angulo,tau_xy);

        double total_x=0.0;
        double total_y=0.0;

        for(int i=0;i<pontos_x.size();i++)
        {
            total_x=total_x+pontos_x.get(i);
            total_y=total_y+pontos_y.get(i);
        }

        double centro_x=total_x/pontos_x.size();
        double centro_y=total_y/pontos_y.size();

        construir_grid_centradas(centro_x,centro_y);

        calcula_if_grid(angulo,tau_xy);

        graham_scan();

        Double[][] saida = new Double[4][2];
        saida=vetorizar_representar(tau_xy);
        return saida;
    }

//Novembro02

    public Double[][] vetorizar_representar(double tau_xy)
    {

        Double[][] pares_pontos = new Double[pontos_x.size()][2];

        for(int i=0;i<pontos_y.size();i++)
        {
            pares_pontos[i][0]=pontos_x.get(i);
            pares_pontos[i][1]=pontos_y.get(i);

        }

        Double[][] saida = new Double[4][2];

        saida=obter_extremos(pares_pontos);

        vetorizar_duas_series(pares_pontos);

        LineGraphSeries<DataPoint> series_inferior =new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> series_superior =new LineGraphSeries<DataPoint>();

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

    public Double[][] obter_extremos(Double[][] pares_pontos)
    {
        Double[][] saida = new Double[4][2];

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

        return saida;
    }

    public void construir_grid_centradas(double centro_x,double centro_y)
    {
        //Calculamos o raio deste sistema:

        ArrayList<Double> distancia=new ArrayList<>();

        for(int i=0;i<pontos_x.size();i++)
        {
            distancia.add(Math.sqrt(Math.pow(pontos_x.get(i)-centro_x,2)+Math.pow(pontos_y.get(i)-centro_y,2)));
        }

        Collections.sort(distancia);

        double raio_menor=distancia.get(0);
        double raio_maior=distancia.get(distancia.size()-1);


        int numero_circulos=100;//coletar_numero_circulos_grid(100);
        int numero_passos_grid=100;//coletar_pontos_ciculo(100);
        double passos_inter_circulos=(raio_maior+0.0)/numero_circulos;
//começa-se por um quase nulo

        pontos_x.clear();
        pontos_y.clear();

        double raio=1.0;
        for(int j=0;j<numero_circulos;j++)
        {
            circulo(raio,centro_x,centro_y,numero_passos_grid);
            raio=raio+passos_inter_circulos+0.0;
        }
////////////////
    }

    public void calcula_if_grid(double theta,double tau_xy)
    {
        ArrayList<Double> grid_x_if_unitario=new ArrayList<>();
        ArrayList<Double> grid_y_if_unitario=new ArrayList<>();
        double tolerancia=0.1;

        for(int j=0;j<pontos_x.size();j++)
        {
            double x=pontos_x.get(j);
            double y=pontos_y.get(j);
            double[] IF=larc03(x,y,tau_xy,theta,f_SIGMA_T_1,f_SIGMA_T_2,f_SIGMA_C_1,f_SIGMA_C_2,f_TAU12);

            if(IF[0]<=(1+tolerancia))
            {
                if(IF[1]<=(1+tolerancia))
                {
                    if(IF[2]<=(1+tolerancia))
                    {
                        if(IF[3]<=(1+tolerancia))
                        {
                            if(IF[4]<=(1+tolerancia))
                            {
                                    int unitario=0;
                                    if(IF[0]>=(1-tolerancia))
                                    {
                                        unitario=1;
                                    }
                                    if(IF[1]>=(1-tolerancia))
                                    {
                                        unitario=1;
                                    }
                                    if(IF[2]>=(1-tolerancia))
                                    {
                                        unitario=1;
                                    }
                                    if(IF[3]>=(1-tolerancia))
                                    {
                                        unitario=1;
                                    }
                                    if(IF[4]>=(1-tolerancia))
                                    {
                                        unitario=1;
                                    }

                                    if(unitario==1)
                                    {
                                        grid_x_if_unitario.add(pontos_x.get(j));
                                        grid_y_if_unitario.add(pontos_y.get(j));

                                    }

                            }

                        }
                    }
                }
            }
        }

        pontos_x.clear();
        pontos_y.clear();

        for(int k=0;k<grid_x_if_unitario.size();k++)
        {
            pontos_x.add(grid_x_if_unitario.get(k));
            pontos_y.add(grid_y_if_unitario.get(k));
        }
    }

    public void circulo(double raio,double centro_x,double centro_y,int numero_passos_grid)
    {
        double passo_angular=(2*Math.PI+0.0)/numero_passos_grid;
        double theta=0.0;
        for(int i=0;i<numero_passos_grid;i++)
        {
            double temp_x=raio*Math.cos(theta)+centro_x;
            double temp_y=raio*Math.sin(theta)+centro_y;
            theta=theta+passo_angular;
            pontos_x.add(temp_x);
            pontos_y.add(temp_y);
        }
    }


    public void estudando_segmento_01(double angulo,double tau_xy)
    {
        double[] ponto_corte_03_01=corte_03_01(angulo,tau_xy);
        double[] ponto_corte_01_06=testando_larc03_01(angulo,tau_xy,ponto_corte_03_01);


        pontos_x.add(ponto_corte_03_01[0]);
        pontos_y.add(ponto_corte_03_01[1]);

        pontos_x.add(ponto_corte_01_06[0]);
        pontos_y.add(ponto_corte_01_06[1]);

   }

    public double[] testando_larc03_01(double theta,double tau_xy,double[] ponto)
    {
        //Prévio
        double theta_radianos=(Math.PI/180)*theta;
        double c=Math.cos(theta_radianos);
        double s=Math.sin(theta_radianos);
        double c2=c*c;
        double s2=s*s;

        double coeficiente_angular=-(s2/c2);
        //Usando a formula COEFICIENTE_ANGULAR com Ponto
        double x_0=ponto[0];
        double y_0=ponto[1];

        b=-coeficiente_angular*x_0+y_0;

        //agosto18 usando um método mais simples que o BISECTION, temos

        double[] if_inicial=larc03(x_0,y_0,tau_xy,theta,f_SIGMA_T_1,f_SIGMA_T_2,f_SIGMA_C_1,f_SIGMA_C_2,f_TAU12);
        double if_larc_03_compressao_matriz_larc0301=if_inicial[2]; //lembrar         IFs[2] = if_larc_03_compressao_matriz_larc0301;

        double x=x_0;
        double y=y_0;

        double passo=Math.abs(x_0/10);

        int guardian=1;
        while(Math.abs(1-if_larc_03_compressao_matriz_larc0301)<0.01)
        {
            x=x-passo;
            y=coeficiente_angular*x+b;

            if_inicial=larc03(x,y,tau_xy,theta,f_SIGMA_T_1,f_SIGMA_T_2,f_SIGMA_C_1,f_SIGMA_C_2,f_TAU12);
            if_larc_03_compressao_matriz_larc0301=if_inicial[2];

            if(guardian>1000)
            {
                //alert("vemos o larc03.01  "+if_inicial[2]);
                break;
            }
            guardian++;
        }

        double[] saida=new double[2];
        saida[0] = x;
        saida[1] = y;
        return saida;
    }

   public double[] corte_03_01(double angulo,double tau_xy)
   {
            //Em função do coeficiente angular do larc03.02 fazemos a varredura:
            double cos=Math.cos((angulo*Math.PI)/180);
            double sin=Math.sin((angulo*Math.PI)/180);

            double denominador=((sin*sin)/f_LARC03_E1-((cos*cos)/f_LARC03_E2)*f_NU12);
            double numerador_b=f_LARC03_EPSILON_T_1-2*cos*sin*tau_xy*((1.0)/f_LARC03_E1+((1.0)/f_LARC03_E2)*f_NU12);
            double numerador_m=-(((cos*cos)/f_LARC03_E1)-((sin*sin)/f_LARC03_E2)*f_NU12);

            double coeficiente_angular=numerador_m/denominador;

            b=numerador_b/denominador;
            double corte_eixo_x=-(b/coeficiente_angular);

            return metodo_bisection_linha_criterio_02(tau_xy,corte_eixo_x,coeficiente_angular,b,angulo,2);
   }


    public void estudando_segmento_02(double angulo,double tau_xy)
    {

        double[] ponto_corte_02_03=corte_02_03(angulo,tau_xy);
        double[] ponto_corte_02_05=testando_larc02_05(angulo,tau_xy,ponto_corte_02_03);

        pontos_x.add(ponto_corte_02_03[0]);
        pontos_y.add(ponto_corte_02_03[1]);

        pontos_x.add(ponto_corte_02_05[0]);
        pontos_y.add(ponto_corte_02_05[1]);
    }



    public double[] testando_larc02_05(double theta,double tau_xy,double[] ponto)
    {

        //Prévio
        double theta_radianos=(Math.PI/180)*theta;
        double c=Math.cos(theta_radianos);
        double s=Math.sin(theta_radianos);
        double c2=c*c;
        double s2=s*s;

        double coeficiente_angular=-(s2/c2);
        //Usando a formula COEFICIENTE_ANGULAR com Ponto
        double x_0=ponto[0];
        double y_0=ponto[1];

        b=-coeficiente_angular*x_0+y_0;

        //agosto18 usando um método mais simples que o BISECTION, temos

        double [] if_inicial=larc03(x_0,y_0,tau_xy,theta,f_SIGMA_T_1,f_SIGMA_T_2,f_SIGMA_C_1,f_SIGMA_C_2,f_TAU12);
        double if_larc_03_compressao_matriz_larc0302=if_inicial[1];

        double x=x_0;
        double y=y_0;

        double passo=Math.abs(x_0/10);

        double guardian=1;
        while(Math.abs(1-if_larc_03_compressao_matriz_larc0302)<0.01)
        {
            x=x-passo;
            y=coeficiente_angular*x+b;

            if_inicial=larc03(x,y,tau_xy,theta,f_SIGMA_T_1,f_SIGMA_T_2,f_SIGMA_C_1,f_SIGMA_C_2,f_TAU12);
            if_larc_03_compressao_matriz_larc0302=if_inicial[1];

            if(guardian>1000)
            {
                //alert("vemos o larc03.02  "+if_inicial[1]);
                break;
            }
            guardian++;
        }


        double[] saida=new double[2];
        saida[0] = x;
        saida[1] = y;
        return saida;
    }

    public double[] corte_02_03(double angulo,double tauXY)
    {
        double theta_radianos=(Math.PI/180)*angulo;
        double cos=Math.cos(theta_radianos);
        double sin=Math.sin(theta_radianos);
        double denominador=((sin*sin)/f_LARC03_E1-((cos*cos)/f_LARC03_E2)*f_NU12);
        double numerador_b=f_LARC03_EPSILON_T_1-2*cos*sin*tauXY*((1.0)/f_LARC03_E1+((1.0)/f_LARC03_E2)*f_NU12);
        double numerador_m=-(((cos*cos)/f_LARC03_E1)-((sin*sin)/f_LARC03_E2)*f_NU12);
        double coeficiente_angular=numerador_m/denominador;
        b=numerador_b/denominador;
        double corte_eixo_x=-(b/coeficiente_angular);
        return metodo_bisection_linha_criterio_02(tauXY,corte_eixo_x,coeficiente_angular,b,angulo,1);
    }

    public double[] metodo_bisection_linha_criterio_02(
            double tauXY,
            double corte_eixo_x,
            double coeficiente_angular,
            double b,
            double angulo,
            int indice_interesse
            )
    {
//Vemos se o INICIO e FIM representam um intervalo CONVENIENTE
        double amplificacao=1.0;

        double y=corte_eixo_x*coeficiente_angular+b;
        double[] if_inicial=larc03(corte_eixo_x,y,tauXY,angulo,f_SIGMA_T_1,f_SIGMA_T_2,f_SIGMA_C_1,f_SIGMA_C_2,f_TAU12);

        double x=0.0;
        if(indice_interesse==1)
        {
            x=corte_eixo_x*(1+amplificacao);
        }

        if(indice_interesse==2)
        {
            x=0.0;
        }

        y=x*coeficiente_angular+b;
        double[]  if_final=larc03(x,y,tauXY,angulo,f_SIGMA_T_1,f_SIGMA_T_2,f_SIGMA_C_1,f_SIGMA_C_2,f_TAU12);

        double anterior=x;
        double novo=0.0;
        double multiplicador=10.0;
        double passo_reducao=(x-corte_eixo_x)/multiplicador;

	//Usando o método numérico BISECTION

        double gaurdian=1;
        double proximidade=Math.abs(if_inicial[indice_interesse]-if_final[indice_interesse]);

        while (0.0001<proximidade)
        {
            int gaurdian_interno = 1;

            while ((if_inicial[indice_interesse] < 1) && (if_final[indice_interesse] > 1))
            {
                x = anterior - passo_reducao;
                y = x * coeficiente_angular + b;
                if_final = larc03(x, y, tauXY, angulo, f_SIGMA_T_1, f_SIGMA_T_2, f_SIGMA_C_1, f_SIGMA_C_2, f_TAU12);

                if (if_final[indice_interesse] > 1)
                {
                    anterior = x;
                }

                if (if_final[indice_interesse] < 1) {
                    novo = x;
                }
                if (gaurdian_interno > 1000)
                {
                    break;
                }

                gaurdian_interno=gaurdian_interno + 1;
            }

            x = anterior;
            y = x * coeficiente_angular + b;
            if_final = larc03(x, y, tauXY, angulo, f_SIGMA_T_1, f_SIGMA_T_2, f_SIGMA_C_1, f_SIGMA_C_2, f_TAU12);
            x = novo;
            y = x * coeficiente_angular + b;
            if_inicial = larc03(x, y, tauXY, angulo, f_SIGMA_T_1, f_SIGMA_T_2, f_SIGMA_C_1, f_SIGMA_C_2, f_TAU12);
            passo_reducao = (anterior - novo) / (multiplicador * 10.0);
            proximidade = Math.abs(if_inicial[indice_interesse] - if_final[indice_interesse]);
            if (gaurdian > 1000)
            {
                break;
            }

            gaurdian=gaurdian_interno + 1;
        }
        double ponto[]=new double[2];
        ponto[0]=x;
        ponto[1]=y;
        return ponto;
    }

//Novembro01
    public double[] larc03(
            double sigma_x,
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

//Modos do IF
//Modo 1

        double if_puck_modo_1=0.0;
        if (sigma_1 >= 0.0)
        {
            double EPSILON_1=((sigma_1/f_LARC03_E2)-f_NU12*(sigma_2/f_LARC03_E2));
//double m_sigF=1.0;
//double NU12_f=f_NU12;
//double E1_f=f_LARC03_E1;
            double aux4_1 = (global_puck_NU12_f/global_puck_E1_f)*global_puck_m_sigF*sigma_2;
            double aux4 = EPSILON_1 + aux4_1;
            if_puck_modo_1 = (1.0/f_LARC03_EPSILON_T_1)*aux4;
        }
//  Modo 2
        double if_puck_modo_2=0.0;
        if (sigma_1 < 0.0)
        {
            double EPSILON_1=((sigma_1/f_LARC03_E1)-f_NU12*(sigma_2/f_LARC03_E2));
//double E1_f=f_LARC03_E1;
//double m_sigF=1.0;
//double NU12_f=f_NU12;
//double EPSILON_C_1=f_LARC03_EPSILON_T_1;

            double aux5_1 = (global_puck_NU12_f/global_puck_E1_f)*global_puck_m_sigF*sigma_2;
            double aux5_2 = EPSILON_1 + aux5_1;
            double aux5 = (1.0/f_EPSILON_C_1)*Math.abs(aux5_2);
            double aux6 = Math.pow( 10*f_GAMMA12,2);
            if_puck_modo_2 = aux5 + aux6;
        }

//Modo 3 ,
        double if_puck_modo_3=0.0;
        if (sigma_2 >= 0.0)
        {
//double p_plus_TL=1.0;
//double sigma_1_D=1.0;
            double aux7_1 = tau_12/TAU12;
            double aux7 = Math. pow(aux7_1, 2);
            double aux8_1 = global_puck_p_plus_TL*(SIGMA_T_2/TAU12);
            double aux8_2 = Math.pow(1 - aux8_1, 2);
            double aux8_3_1 = sigma_2/SIGMA_T_2;
            double aux8_3 = Math.pow(aux8_3_1, 2);
            double aux8 = aux8_2*aux8_3;
            double raiz= Math.sqrt( aux7 + aux8 );
            double aux0_1 = tau_12/TAU12;
            double aux0=global_puck_p_plus_TL*aux0_1;
            double termo_fibra=Math.abs(sigma_1/global_puck_sigma_1_D);
            if_puck_modo_3=aux0+raiz;
        }

//	Modo 4 if_puck_modo_4
        double if_puck_modo_4=0.0;
        double if_puck_modo_5=0.0;

        if (sigma_2 < 0.0)
        {
//double tau12_C=1.0;
//double R_TT_A=1.0;
            double t = Math.abs(sigma_2/TAU12);
            double t_maior = global_puck_puck_R_TT_A/Math.abs(global_puck_TAU12_C);
            if((t >= 0) && (t <= t_maior))
            {
//                double p_minus_TL=1.0;
//                double global_puck_sigma_1_D=1.0;
                double aux7_1 = tau_12;
                double aux7 = Math.pow(aux7_1, 2);
                double aux8_1 = global_puck_p_minus_TL*SIGMA_T_2;
                double aux8_2 = Math.pow(aux8_1, 2);
                double raiz=Math.sqrt(aux7+aux8_2);
                double aux0_1=raiz+global_puck_p_minus_TL;
                double aux0=aux0_1/TAU12;
                double termo_fibra=Math.abs(sigma_1/global_puck_sigma_1_D);
                if_puck_modo_4=termo_fibra+aux0;
            }

//	Modo 5, Compressão na Fibra, Depende do valor de fi
            double t_INV = Math.abs(TAU12/sigma_2);
            double t_maior_INV = Math.abs(global_puck_TAU12_C)/global_puck_puck_R_TT_A;

            if((t_INV >= 0) && (t <= t_maior_INV))
            {
//double p_minus_TT=1.0;
//double sigma_1_D=1.0;
                double aux12_1 = 2*(1 + global_puck_p_minus_TT)*global_puck_TAU12_C;
                double aux12_2 = tau_12/aux12_1;
                double aux12 = Math.pow(aux12_2,2);
                double aux13_1 = sigma_2/SIGMA_C_2;
                double aux13 = Math.pow(aux13_1,2);
                double aux14_1 = SIGMA_C_2/(-sigma_2);
                double aux14 = aux14_1*( aux12 + aux13 );
                double termo_fibra=Math.abs(sigma_1/global_puck_sigma_1_D);
                if_puck_modo_5=termo_fibra+aux14;
            }
        }

        //entrega de resultados
    double IFs[]=new double[6];

    IFs[0] = if_puck_modo_1; //1.0; //if_larc_03_tracao_fibra;
    IFs[1] = if_puck_modo_2;//1.0; //if_larc_03_tracao_matriz;
    IFs[2] = if_puck_modo_3; //if_larc_03_compressao_matriz_larc0301; //
    IFs[3] = if_puck_modo_4;//1.0; //if_larc_03_compressao_matriz_larc03_06;
    IFs[4] = if_puck_modo_5; //1.0; //if_larc_03_compressao_fibra_larc03_04;

    return IFs;
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

//maio22

    List<Double> possiveis_pontos_x = new ArrayList<Double>();
    List<Double> possiveis_pontos_y = new ArrayList<Double>();


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


//Nov01

    public void graham_scan()
    {

        ArrayList<Double> pares_pontos_x=new ArrayList<>(pontos_x);
        ArrayList<Double> pares_pontos_y=new ArrayList<>(pontos_y);

        /*
        pares_pontos_x.add(4.0); 	pares_pontos_y.add(2.0);
        pares_pontos_x.add(0.0); 	pares_pontos_y.add(0.0);
        pares_pontos_x.add(7.0); 	pares_pontos_y.add(2.0);
        pares_pontos_x.add(7.5); 	pares_pontos_y.add(3.0);
        pares_pontos_x.add(8.0); 	pares_pontos_y.add(2.5);
        pares_pontos_x.add(10.0); 	pares_pontos_y.add(-1.0);
        pares_pontos_x.add(8.0); 	pares_pontos_y.add(1.0);
        */

        List<Double> copiados_x = new ArrayList<>(pares_pontos_x);
        List<Double> copiados_y = new ArrayList<>(pares_pontos_y);

        // Passo 1 : obter o que está na parte mais infeior do eixo Y
        ArrayList<Double> pontos_ordenados_x=new ArrayList<>();
        ArrayList<Double> pontos_ordenados_y=new ArrayList<>();

        ArrayList<Double> menor_x_list=new ArrayList<>();
        ArrayList<Double> menor_y_list=new ArrayList<>();

// Na verdade é um duplo ordenamento DISSIMULADO
// Neste primeiro FOR fazemos o ordenamento apenas para X
//Escolhe o Y que estiver na posição mais inferior

        int tamanho_inicial=copiados_y.size();

        for(int j=0;j<tamanho_inicial;j++)
        {
            double menor_x=copiados_x.get(0);
            double menor_y=copiados_y.get(0);
            int indice=0;

            for(int i=0;i<copiados_y.size();i++)
            {
                if(menor_y>=copiados_y.get(i))
                {
                    menor_y=copiados_y.get(i);
                    menor_x=copiados_x.get(i);
                    indice=i;
                    //desempate no caso de ter a mesma Y, será usado o ponto com menor X
                }
            }
            menor_x_list.add(menor_x);
            menor_y_list.add(menor_y);
            copiados_x.remove(indice);
            copiados_y.remove(indice);
        }

        //Agora escolhemos o menor valor de Y e se este tiver multiplicidade acima de um será preciso ordenar
        double menor_x=menor_x_list.get(0);
        double menor_y=menor_y_list.get(0);


        int multiplicidade=0;

        for(int i=1;i<menor_y_list.size();i++)
        {
            if(menor_y==menor_y_list.get(i))
            {
                multiplicidade=multiplicidade+1;
            }
        }


        if(multiplicidade>0)
        {
            // Sera preciso ordenar em X
            menor_x=menor_x_list.get(0);
            menor_y=menor_y_list.get(0);
            int indice=0;
            for(int i=0;i<menor_x_list.size();i++)
            {

                if(menor_x>=menor_x_list.get(i))
                {
                    menor_x=menor_x_list.get(i);
                    menor_y=menor_y_list.get(i);
                    indice=i;
                }
            }
        }
        pontos_ordenados_x.add(menor_x);
        pontos_ordenados_y.add(menor_y);

//# Passo 2 : ordenamos os pontos conforme o ângulo usando ARC TAN(x)

        //copiamos de NOVO dado que foi destruido
        copiados_x = new ArrayList<>(pares_pontos_x);
        copiados_y = new ArrayList<>(pares_pontos_y);

        int indice_apagar=0;

        ArrayList<Double> angulos=new ArrayList<>();

        for(int i=0;i<copiados_x.size();i++)
        {
            double delta_x=(copiados_x.get(i)-menor_x)+0.0;
            double delta_y=(copiados_y.get(i)-menor_y)+0.0;
            if(delta_x!=0.0)
            {
                angulos.add(Math.atan(delta_y/delta_x));
            }
            else
            {
                if(delta_y==0.0)
                {
                    indice_apagar=i;
                }
                else
                {
                    angulos.add(Math.PI/2.0);
                }
            }
        }

        copiados_x.remove(indice_apagar);
        copiados_y.remove(indice_apagar);


//com os ângulos pode-se ordenar
//resrevamos os ângulos para verificar a qualidade do Algoritmo
        ArrayList<Double> angulo_ordenado=new ArrayList<>();

        tamanho_inicial=angulos.size();

        for(int j=0;j<tamanho_inicial;j++)
        {
            double menor_angulo=angulos.get(0);
            double x_menor_angulo=copiados_x.get(0);
            double y_menor_angulo=copiados_y.get(0);
            int indice=0;
            for(int i=0;i<angulos.size();i++)
            {
                if(menor_angulo>=angulos.get(i))
                {
                    menor_angulo=angulos.get(i);
                    x_menor_angulo=copiados_x.get(i);
                    y_menor_angulo=copiados_y.get(i);
                    indice=i;
                }
            }
            copiados_x.remove(indice);
            copiados_y.remove(indice);
            angulos.remove(indice);

            angulo_ordenado.add(menor_angulo);
            pontos_ordenados_x.add(x_menor_angulo);
            pontos_ordenados_y.add(y_menor_angulo);
        }


        // Agora ver se de verdade é uma ENVOLVENTE
        ArrayList<Double> stack_x=new ArrayList<>();
        ArrayList<Double> stack_y=new ArrayList<>();

// Para 0
        stack_x.add(pontos_ordenados_x.get(0));
        stack_y.add(pontos_ordenados_y.get(0));
//Para 1
        stack_x.add(pontos_ordenados_x.get(1));
        stack_y.add(pontos_ordenados_y.get(1));

// Em tese deveria meter todos os pontos, mas é preciso ter cuidado com aqueles que estejam MUITO ADENTRO
 	for(int i=2;i<pontos_ordenados_x.size();i++)
	{
		int proximoTOP=stack_x.size()-2;
		int TOP=stack_x.size()-1;
		//alert("indice "+i);
		double o=ccw(stack_x.get(proximoTOP),stack_x.get(TOP),pontos_ordenados_x.get(i),stack_y.get(proximoTOP),stack_y.get(TOP),pontos_ordenados_y.get(i));

		if(o==0.0)
		{
			// Caso extremo que TODOS estejam numa única linha reta
			stack_x.remove(stack_x.size()-1);
			stack_y.remove(stack_y.size()-1);

			stack_x.add(pontos_ordenados_x.get(i));
			stack_y.add(pontos_ordenados_y.get(i));
		}
		else
		{
			if(o>0.0)
			{
				// aceita positivos
				stack_x.add(pontos_ordenados_x.get(i));
				stack_y.add(pontos_ordenados_y.get(i));

			}
			else
			{
				//# Nos negatiovos, ele deve REMOVER o ponto
				while((o<=0.0) && (proximoTOP>=0.0))
				{
				    stack_x.remove(stack_x.size()-1);
                    stack_y.remove(stack_y.size()-1);
					proximoTOP=stack_x.size()-2;
					TOP=stack_x.size()-1;
                    o=ccw(stack_x.get(proximoTOP),stack_x.get(TOP),pontos_ordenados_x.get(i),stack_y.get(proximoTOP),stack_y.get(TOP),pontos_ordenados_y.get(i));
                }
				stack_x.add(pontos_ordenados_x.get(i));
				stack_y.add(pontos_ordenados_y.get(i));
			}
		}
	}

///////Finalizado o GS
        pontos_x.clear();
        pontos_y.clear();

        for(int i=0; i<stack_x.size();i++)
        {
            //Log.d("Novembro01","   "+stack_x.get(i)+"  "+stack_y.get(i)+"  ");
            pontos_x.add(stack_x.get(i));
            pontos_y.add(stack_y.get(i));
        }
////
    }
    /*
    #Return a  positive number for a left turn (ACEITA)
    # and negative for a right turn (elimina)
    */
    public double ccw(double p1_x, double p2_x, double p3_x,double p1_y, double p2_y, double p3_y)
    {
        double total=(p2_x+0.0 - p1_x)*(p3_y+0.0 - p1_y) - (p2_y+0.0 - p1_y)*(p3_x+0.0 - p1_x);
        //alert(" Testando com:" + p1_x+" "+p2_x+" "+p3_x+" result   "+total);
        return total;
    }
///////////////////////
}
