package br.com.ven2020.envelopes2018.criterios;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import br.com.ven2020.envelopes2018.database.DatabaseHelper;
import br.com.ven2020.envelopes2018.database.db_historico_estados_esforcos;

/**
 * Created by julian on 04/10/17.
 */

public class numerico_larc03 {

    private Context myContext;

    private Float f_edit_textView_sigma_x;
    private Float f_edit_textView_sigma_y;
    private Float f_edit_textView_tau_xy;
    private Float f_edit_textView_angulo;

    private String lamina_usada="";
    private String criterio_usado="";
    private String envelope_usado="";

    private String endereco="";


    private Double global_larc03_alpha_0=0.0;
    private Double global_larc03_TAU23=0.0;
    private Double global_larc03_Y_T_is=0.0;
    private Double global_larc03_S_L_is=0.0;

    numerico_larc03(Context myContext, String lamina_usada, String criterio_usado, String envelope_usado,
                    String endereco,
                    Double global_larc03_alpha_0,
                    Double global_larc03_TAU23,
                    Double global_larc03_Y_T_is,
                    Double global_larc03_S_L_is
    )
    {
        this.myContext=myContext;
        this.lamina_usada=lamina_usada;
        this.criterio_usado=criterio_usado;
        this.envelope_usado=envelope_usado;
        this.endereco=endereco;

        this.global_larc03_alpha_0=global_larc03_alpha_0;
        this.global_larc03_TAU23=global_larc03_TAU23;
        this.global_larc03_Y_T_is=global_larc03_Y_T_is;
        this.global_larc03_S_L_is=global_larc03_S_L_is;
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
///// Após a verificação podemos calcular, é INSERIR
// não adianta inserir ESTADOS errados


    public ArrayList<String> calcular_if(String nome_lamina, Double tau23)
    {
        ArrayList<String> array_list = new ArrayList<String>();

        String agora=new Time(System.currentTimeMillis()).toString();

        db_historico_estados_esforcos db_historico_estados=new db_historico_estados_esforcos(myContext);

        db_historico_estados.insert_estado_esforcos(
                this.lamina_usada,
                this.criterio_usado,
                this.envelope_usado,
                this.f_edit_textView_sigma_x,
                this.f_edit_textView_sigma_y,
                this.f_edit_textView_tau_xy,
                this.f_edit_textView_angulo,
                agora
        );
/**
 * Outubro 05/10/17.
 * No momento de calcular ele deve procurar as propriedades da lâmina no DB
 *
 *
 */

//Procuramos as propriedades da lâmina

        DatabaseHelper dbHelper = new DatabaseHelper(myContext, this.endereco);
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
        }

        List<String> list = dbHelper.obter_propriedades_laminas(nome_lamina);

        String E1=list.get(3);//(cursor.getString(3)) );//E1`	TEXT,
        String E2=list.get(4);//(cursor.getString(3)) );//E1`	TEXT,
        String NU12=list.get(9);//(cursor.getString(9)));//NU12`	TEXT,
        String EPSILON_T_1=list.get(19);//parseDouble(cursor.getString(19)));//EPSILON_T_1`	TEXT,
        String EPSILON_C_1=list.get(21);//(21)));//EPSILON_C_1`	TEXT,
        String GAMMA12=list.get(23);//getString(23)));//GAMMA12`	TEXT,

        String G12=list.get(6);//g(6)));//G12`	TEXT,

        E1=E1.substring(E1.indexOf(":")+1,E1.length());
        E2=E2.substring(E2.indexOf(":")+1,E2.length());
        NU12=NU12.substring(NU12.indexOf(":")+1,NU12.length());
        EPSILON_T_1=EPSILON_T_1.substring(EPSILON_T_1.indexOf(":")+1,EPSILON_T_1.length());
        EPSILON_C_1=EPSILON_C_1.substring(EPSILON_C_1.indexOf(":")+1,EPSILON_C_1.length());
        GAMMA12=GAMMA12.substring(GAMMA12.indexOf(":")+1,GAMMA12.length());
        G12=G12.substring(G12.indexOf(":")+1,G12.length());

        String SIGMA_T_1=list.get(12);//("SIGMA_T_1: "+cursor.getString(12));//SIGMA_T_1`	TEXT,
        String SIGMA_T_2=list.get(13);//add("SIGMA_T_2: "+cursor.getString(13));//SIGMA_T_1`	TEXT,
        String SIGMA_C_1=list.get(14); //add("SIGMA_C_1: "+cursor.getString(14));//SIGMA_C_1`	TEXT,
        String SIGMA_C_2=list.get(15); //("SIGMA_C_2: "+cursor.getString(15));//SIGMA_C_1`	TEXT,
        String TAU12=list.get(16); //"TAU12: "+cursor.getString(16));//TAU12`	TEXT,
//Futuro

        String TAU13=list.get(17);//        list.add("TAU23: "+cursor.getString(18));//TAU12`	TEXT,
        String TAU23=list.get(18);//        list.add("TAU23: "+cursor.getString(18));//TAU12`	TEXT,

        SIGMA_T_1=SIGMA_T_1.substring(SIGMA_T_1.indexOf(":")+1,SIGMA_T_1.length());
        SIGMA_T_2=SIGMA_T_2.substring(SIGMA_T_2.indexOf(":")+1,SIGMA_T_2.length());
        SIGMA_C_1=SIGMA_C_1.substring(SIGMA_C_1.indexOf(":")+1,SIGMA_C_1.length());
        SIGMA_C_2=SIGMA_C_2.substring(SIGMA_C_2.indexOf(":")+1,SIGMA_C_2.length());

        TAU12=TAU12.substring(TAU12.indexOf(":")+1,TAU12.length());

        TAU13=TAU13.substring(TAU13.indexOf(":")+1,TAU13.length());
        TAU23=TAU23.substring(TAU23.indexOf(":")+1,TAU23.length());

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

        Double f_E1=Double.parseDouble(E1);
        Double f_E2=Double.parseDouble(E2);
        Double f_NU12=Double.parseDouble(NU12);
        Double f_EPSILON_T_1=Double.parseDouble(EPSILON_T_1);
        Double f_EPSILON_C_1=Double.parseDouble(EPSILON_C_1);
        Double f_GAMMA12=Double.parseDouble(GAMMA12);
        Double f_G12=Double.parseDouble(G12);


        Double f_TAU23;

//Outubro12,colocamos o parametro experimental apenas se for preciso

        if(TAU23==null)
        {
            f_TAU23=tau23;
        }
        else
        {
            f_TAU23= Double.parseDouble(TAU13);
        }

        /*
        * Numerico,
        * */

        Float theta=this.f_edit_textView_angulo;
        Float sigma_x=this.f_edit_textView_sigma_x;
        Float sigma_y=this.f_edit_textView_sigma_y;
        Float tau_xy=this.f_edit_textView_tau_xy;

        double theta_radianos=(Math.PI/180)*theta;

        Double c=Math.cos(theta_radianos);
        Double s=Math.sin(theta_radianos);
        Double c2=c*c;
        Double s2=s*s;

        Double sigma_1=c2*sigma_x+s2*sigma_y+2*c*s*tau_xy;
        Double sigma_2=s2*sigma_x+c2*sigma_y-2*c*s*tau_xy;
        Double tau_12=-s*c*sigma_x+s*c*sigma_y+(c2-s2)*tau_xy;

//Novembro17

        Double if_larc_03_compressao_matriz_larc0301=0.0;

        if(sigma_2<0)
        {
            if(sigma_2<=sigma_1)
            {
                Double alpha_0=53.2*(Math.PI/180); //Cuidado, é preciso colocar aqui em radianos.
                Double alpha=alpha_0;
                Double eta_L=(f_TAU12*Math.cos(alpha_0*2))/(f_SIGMA_C_2*Math.pow(Math.cos(alpha_0),2));
                Double tau_eff_L=Math.cos(alpha)*(Math.abs(tau_12)+eta_L*sigma_2*Math.cos(alpha));

                Double termo_L=0.0;
                Double previo=Math.pow(tau_eff_L/f_TAU12,2);
                if(previo>0)
                {
                    termo_L=previo;
                }
			/*
			Caso do Termo L
			*/

                TAU23=TAU12;
                Double eta_T=-1.0/(Math.tan(2*alpha_0));

                Double tau_eff_T=0.0;
                previo=-sigma_2*Math.cos(alpha)*(Math.sin(alpha)-eta_T*Math.cos(alpha));
                if(previo>0)
                {
                    tau_eff_T=previo;
                }

                Double termo_T=Math.pow(tau_eff_T/global_larc03_TAU23,2);
                //saída

                if_larc_03_compressao_matriz_larc0301=termo_T+termo_L;

            }
        }

        //  Modo 2 , TRAÇÂO na matriz:

        Double if_larc_03_tracao_matriz=0.0;
        if(sigma_2>=0)
        {
            Double NU21=f_NU12*(f_E2/f_E1);
            Double lambda_22=2*((1/f_E2)-(Math.pow(NU21,2)/f_E1));
            Double lambda_44=1/f_G12;
            //pode-se obter apenas usando os Lambda, e os valores de (Y_T_is,S_L_is)
            //caso lâmina grossa
            Double Y_T_is=1.12*Math.sqrt(2)*f_SIGMA_T_2;
            Double S_L_is=Math.sqrt(2)*f_TAU12;
            Double g=(lambda_22/lambda_44)*Math.pow((Y_T_is/S_L_is),2);
            //calculo dos termos o IF2
            Double termo_sigma_2=(1-g)*(sigma_2/Y_T_is);
            Double termo_sigma_2_QUADRADO=g*Math.pow(sigma_2/Y_T_is,2);
            Double termo_tau_12_QUADRADO=Math.pow(tau_12/S_L_is,2);

            if_larc_03_tracao_matriz=termo_sigma_2+termo_sigma_2_QUADRADO+termo_tau_12_QUADRADO;
        }

//  Modo 3 , TRAÇÂO na fibra:
        Double if_larc_03_tracao_fibra=0.0;
        if(sigma_1>=0)
        {
            Double numerador_if_larc_03_tracao_fibra=((sigma_1/f_E1)-(sigma_2/f_E2)*f_NU12);
            if_larc_03_tracao_fibra=numerador_if_larc_03_tracao_fibra/f_EPSILON_T_1;
        }


//	Modo 4, Compressão na Fibra, Depende do valor de sigma_2_m
        Double if_larc_03_compressao_fibra_larc03_04=0.0;
        Double if_larc_03_compressao_fibra_larc03_05=0.0;

        Double fi=0.0;//calcular_Fi(sigma_1,sigma_2,tau_12,theta,SIGMA_T_1,SIGMA_T_2,SIGMA_C_1,SIGMA_C_2,TAU12);

        theta_radianos=(Math.PI/180)*fi;
        c=Math.cos(theta_radianos);
        s=Math.sin(theta_radianos);
        c2=c*c;
        s2=s*s;
        Double sigma_1_m=c2*sigma_x+s2*sigma_y+2*c*s*tau_xy;
        Double sigma_2_m=s2*sigma_x+c2*sigma_y-2*c*s*tau_xy;
        Double tau_12_m=-s*c*sigma_x+s*c*sigma_y+(c2-s2)*tau_xy;

        if(sigma_1<0)
        {
            if(sigma_2_m<0)
            {
                Double S_L_is=Math.sqrt(2)*f_TAU12;
                Double alpha_0=53.2*(Math.PI/180);
                Double eta_L=(f_TAU12*Math.cos(alpha_0*2))/(f_SIGMA_C_2*Math.pow(Math.cos(alpha_0),2));
                Double previo=(Math.abs(tau_12_m)+eta_L*sigma_2_m)/(S_L_is);
                if(previo>0)
                {
                    if_larc_03_compressao_fibra_larc03_04=previo;
                }
            }
        }

//Modo 5, Compressão na Fibra, Depende do valor de fi
        if_larc_03_compressao_fibra_larc03_05=0.0;

        if(sigma_1<0)
        {
            if(sigma_2_m>=0)
            {
                Double NU21=f_NU12*(f_E2/f_E1);
                Double lambda_22=2*((1/f_E2)-(Math.pow(NU21,2)/f_E1));
                Double lambda_44=1/f_G12;
                Double S_L_is=Math.sqrt(2)*f_TAU12;


                Double Y_T_is=1.12*Math.sqrt(2)*f_SIGMA_T_2;
                Double g=(lambda_22/lambda_44)*Math.pow((Y_T_is/S_L_is),2);

                Double termo_sigma_2=(1-g)*(sigma_2_m/Y_T_is);
                Double termo_sigma_2_QUADRADO=g*Math.pow(sigma_2_m/Y_T_is,2);
                Double termo_tau_12_QUADRADO=Math.pow(tau_12_m/S_L_is,2);

                if_larc_03_compressao_fibra_larc03_05=termo_sigma_2+termo_sigma_2_QUADRADO+termo_tau_12_QUADRADO;
            }

        }



	/*
Modo 6, Compressão na Matriz
	Muito similar ao MODO 1 , mas nesta vez é preciso considerar valores "m"
	Os valores "m" dependem dp ângulo da fibra, que pode ser ZERO 	00000
	e por tanto num caso assim estamos de novo no critério Modo 1
	Caso do Termo T_m
	*/


        Double if_larc_03_compressao_matriz_larc03_06=0.0;

        if(sigma_2<0)
        {
            if(sigma_2>sigma_1)
            {
                Double alpha_0=53.2*(Math.PI/180); //Cuidado, é preciso colocar aqui em radianos.
                Double alpha=alpha_0;
                Double eta_L=0.0;
                Double tau_eff_L=Math.cos(alpha)*(Math.abs(tau_12)+eta_L*sigma_2*Math.cos(alpha));
                Double termo_L=Math.pow(tau_eff_L/f_TAU12,2);

			/*
			Caso do Termo L_m
			*/

                TAU23=TAU12;
                Double eta_T=-1/(Math.tan(2*alpha_0));
                Double tau_eff_T=-sigma_2*Math.cos(alpha)*(Math.sin(alpha)-eta_T*Math.cos(alpha));
                Double termo_T=Math.pow(tau_eff_T/global_larc03_TAU23,2);
                //saída
                if_larc_03_compressao_matriz_larc03_06=termo_T+termo_L;
            }
        }

        array_list.add(""+if_larc_03_compressao_matriz_larc0301);
        array_list.add(""+if_larc_03_tracao_matriz);
        array_list.add(""+if_larc_03_tracao_fibra);
        array_list.add(""+if_larc_03_compressao_matriz_larc03_06);
        array_list.add(""+if_larc_03_compressao_fibra_larc03_04);
        array_list.add(""+if_larc_03_compressao_fibra_larc03_05);

        return  array_list;
    }



/////////////////////////
}
