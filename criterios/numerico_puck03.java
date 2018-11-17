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

public class numerico_puck03 {

    private Context myContext;

    private Float f_edit_textView_sigma_x;
    private Float f_edit_textView_sigma_y;
    private Float f_edit_textView_tau_xy;
    private Float f_edit_textView_angulo;

    private String lamina_usada="";
    private String criterio_usado="";
    private String envelope_usado="";

    private String endereco="";


    private Double global_puck_m_sigF=0.0;
    private Double global_puck_p_plus_TL=0.0;
    private Double global_puck_p_minus_TL=0.0;
    private Double global_puck_p_minus_TT=0.0;
    private Double global_puck_sigma_1_D=0.0;
    private Double global_puck_puck_R_TT_A=0.0;
    private Double global_puck_TAU12_C=0.0;
    private Double global_puck_NU12_f=0.0;
    private Double global_puck_E1_f=0.0;

    numerico_puck03(Context myContext, String lamina_usada, String criterio_usado, String envelope_usado,
                    String endereco,
                    Double global_puck_m_sigF,
                    Double global_puck_p_plus_TL,
                    Double global_puck_p_minus_TL,
                    Double global_puck_p_minus_TT,
                    Double global_puck_sigma_1_D,
                    Double global_puck_puck_R_TT_A,
                    Double global_puck_TAU12_C,
                    Double global_puck_NU12_f,
                    Double global_puck_E1_f
    )
    {
        this.myContext=myContext;
        this.lamina_usada=lamina_usada;
        this.criterio_usado=criterio_usado;
        this.envelope_usado=envelope_usado;
        this.endereco=endereco;

        this.global_puck_m_sigF=global_puck_m_sigF;
        this.global_puck_p_plus_TL=global_puck_p_plus_TL;
        this.global_puck_p_minus_TL=global_puck_p_minus_TL;
        this.global_puck_p_minus_TT=global_puck_p_minus_TT;
        this.global_puck_sigma_1_D=global_puck_sigma_1_D;
        this.global_puck_puck_R_TT_A=global_puck_puck_R_TT_A;
        this.global_puck_TAU12_C=global_puck_TAU12_C;
        this.global_puck_NU12_f=global_puck_NU12_f;
        this.global_puck_E1_f=global_puck_E1_f;

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

        E1=E1.substring(E1.indexOf(":")+1,E1.length());
        E2=E2.substring(E2.indexOf(":")+1,E2.length());
        NU12=NU12.substring(NU12.indexOf(":")+1,NU12.length());
        EPSILON_T_1=EPSILON_T_1.substring(EPSILON_T_1.indexOf(":")+1,EPSILON_T_1.length());
        EPSILON_C_1=EPSILON_C_1.substring(EPSILON_C_1.indexOf(":")+1,EPSILON_C_1.length());
        GAMMA12=GAMMA12.substring(GAMMA12.indexOf(":")+1,GAMMA12.length());

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

//	Modo 1
        Double if_puck_modo_1=0.0;

        if (sigma_1 >= 0.0)
        {

            Double EPSILON_1=((sigma_1/f_E1)-f_NU12*(sigma_2/f_E2));
            Double E1_f=global_puck_E1_f;
            Double NU12_f=global_puck_NU12_f;
            Double m_sigF=global_puck_m_sigF;
            Double aux4_1 = (NU12_f/E1_f)*m_sigF*sigma_2;
            Double aux4 = EPSILON_1 + aux4_1;
            if_puck_modo_1 = (1.0/f_EPSILON_T_1)*aux4;
        }

//  Modo 2
        Double if_puck_modo_2=0.0;
        if (sigma_1 < 0.0)
        {
            Double EPSILON_1=((sigma_1/f_E1)-f_NU12*(sigma_2/f_E2));
            Double E1_f=global_puck_E1_f;
            Double m_sigF=global_puck_m_sigF;
            Double NU12_f=global_puck_NU12_f;
            Double aux5_1 = (NU12_f/E1_f)*m_sigF*sigma_2;
            Double aux5_2 = EPSILON_1 + aux5_1;
            Double aux5 = (1.0/f_EPSILON_C_1)*Math.abs(aux5_2);
            Double aux6 = Math.pow(10*f_GAMMA12,2);
            if_puck_modo_2 = aux5 + aux6;
        }

//Modo 3 ,
        Double if_puck_modo_3=0.0;
        if (sigma_2 >= 0.0)
        {
            Double p_plus_TL=global_puck_p_plus_TL;
            Double sigma_1_D=global_puck_sigma_1_D;
            Double aux7_1 = tau_12/f_TAU12;
            Double aux7 = Math.pow(aux7_1, 2);
            Double aux8_1 = p_plus_TL*(f_SIGMA_T_2/f_TAU12);
            Double aux8_2 = Math.pow(1 - aux8_1, 2);
            Double aux8_3_1 = sigma_2/f_SIGMA_T_2;
            Double aux8_3 = Math.pow(aux8_3_1, 2);
            Double aux8 = aux8_2*aux8_3;
            Double raiz= Math.sqrt( aux7 + aux8 );
            Double aux0_1 = tau_12/f_TAU12;
            Double aux0=p_plus_TL*aux0_1;
            Double termo_fibra=Math.abs(sigma_1/sigma_1_D);
            if_puck_modo_3=aux0+raiz;
        }


//	Modo 4,

        Double if_puck_modo_4=0.0;
        Double if_puck_modo_5=0.0;

        if (sigma_2 < 0.0)
        {
            Double p_plus_TL=global_puck_p_plus_TL;
            Double sigma_1_D=global_puck_sigma_1_D;
            Double tau12_C=global_puck_TAU12_C;
            Double R_TT_A=global_puck_puck_R_TT_A;
            Double t = Math.abs(sigma_2/f_TAU12);
            Double t_maior = R_TT_A/Math.abs(tau12_C);
            if((t >= 0) && (t <= t_maior))
            {
                Double p_minus_TL=global_puck_p_minus_TL;
                sigma_1_D=global_puck_sigma_1_D;
                Double aux7_1 = tau_12;
                Double aux7 = Math.pow(aux7_1, 2);
                Double aux8_1 = global_puck_p_minus_TL*f_SIGMA_T_2;
                Double aux8_2 = Math.pow(aux8_1, 2);
                Double raiz=Math.sqrt(aux7+aux8_2);
                Double aux0_1=raiz+p_minus_TL;
                Double aux0=aux0_1/f_TAU12;
                Double termo_fibra=Math.abs(sigma_1/sigma_1_D);
                if_puck_modo_4=termo_fibra+aux0;

            }


//Modo 5, Compressão na Fibra, Depende do valor de fi
            Double t_INV = Math.abs(f_TAU12/sigma_2);
            Double t_maior_INV = Math.abs(tau12_C)/R_TT_A;
            if((t_INV >= 0) && (t <= t_maior_INV))
            {
                Double p_minus_TT=global_puck_p_minus_TT;
                sigma_1_D=global_puck_sigma_1_D;
                Double aux12_1 = 2*(1 + p_minus_TT)*tau12_C;
                Double aux12_2 = tau_12/aux12_1;
                Double aux12 = Math.pow(aux12_2,2);
                Double aux13_1 = sigma_2/f_SIGMA_C_2;
                Double aux13 = Math.pow(aux13_1,2);
                Double aux14_1 = f_SIGMA_C_2/(-sigma_2);
                Double aux14 = aux14_1*( aux12 + aux13 );
                Double termo_fibra=Math.abs(sigma_1/sigma_1_D);
                if_puck_modo_5=termo_fibra+aux14;
            }

        }


        array_list.add(""+if_puck_modo_1);
        array_list.add(""+if_puck_modo_2);
        array_list.add(""+if_puck_modo_3);
        array_list.add(""+if_puck_modo_4);
        array_list.add(""+if_puck_modo_5);

        return  array_list;
    }



/////////////////////////
}
