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

public class numerico_maxima_deformacao {

    private Context myContext;

    private Float f_edit_textView_sigma_x;
    private Float f_edit_textView_sigma_y;
    private Float f_edit_textView_tau_xy;
    private Float f_edit_textView_angulo;

    private String lamina_usada="";
    private String criterio_usado="";
    private String envelope_usado="";

    private String endereco="";

    numerico_maxima_deformacao(Context myContext, String lamina_usada, String criterio_usado, String envelope_usado, String endereco)
    {
        this.myContext=myContext;
        this.lamina_usada=lamina_usada;
        this.criterio_usado=criterio_usado;
        this.envelope_usado=envelope_usado;
        this.endereco=endereco;
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


    public ArrayList<String> calcular_if(String nome_lamina)
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


        String EPSILON_T_1=list.get(19);//"EPSILON_T_1:  "+cursor.getString(19));//EPSILON_T_1`	TEXT,
        String EPSILON_T_2=list.get(20);//"+cursor.getString(20));//EPSILON_T_2`	TEXT,
        String EPSILON_C_1=list.get(21);//"EPSILON_C_1:  "+cursor.getString(21));//EPSILON_C_1`	TEXT,
        String EPSILON_C_2=list.get(22);// "+cursor.getString(22));//EPSILON_C_2`	TEXT,
        String GAMMA12=list.get(23); //list.add("GAMMA12: "+cursor.getString(23));//GAMMA12`	TEXT,

        // Adicionado apenas para transformar esforços em deformações

        String  E1=list.get(3);//add("E1: "+cursor.getString(3) );//E1`	TEXT,
        String  E2=list.get(4);//("E2: "+cursor.getString(4) );//E2`	TEXT,
        String  G12=list.get(6);//("G12: "+cursor.getString(6));//G12`	TEXT,


        EPSILON_T_1=EPSILON_T_1.substring(EPSILON_T_1.indexOf(":")+1,EPSILON_T_1.length());
        EPSILON_T_2=EPSILON_T_2.substring(EPSILON_T_2.indexOf(":")+1,EPSILON_T_2.length());
        EPSILON_C_1=EPSILON_C_1.substring(EPSILON_C_1.indexOf(":")+1,EPSILON_C_1.length());
        EPSILON_C_2=EPSILON_C_2.substring(EPSILON_C_2.indexOf(":")+1,EPSILON_C_2.length());
        GAMMA12=GAMMA12.substring(GAMMA12.indexOf(":")+1,GAMMA12.length());

        E1=E1.substring(E1.indexOf(":")+1,E1.length());
        E2=E2.substring(E2.indexOf(":")+1,E2.length());
        G12=G12.substring(G12.indexOf(":")+1,G12.length());

        Double f_EPSILON_T_1= Double.parseDouble(EPSILON_T_1);
        //Log.d("Outubro05","\n"+EPSILON_T_2);
        //Float f_EPSILON_T_2= Float.parseFloat(EPSILON_T_2);
        Double f_EPSILON_T_2= Double.parseDouble(EPSILON_T_2);
        //Log.d("Outubro05","\n"+EPSILON_C_1);
        //Float f_EPSILON_C_1= Float.parseFloat(EPSILON_C_1);
        Double f_EPSILON_C_1= Double.parseDouble(EPSILON_C_1);
        //Log.d("Outubro05","\n"+EPSILON_C_2);
        //Float f_EPSILON_C_2= Float.parseFloat(EPSILON_C_2);
        Double f_EPSILON_C_2= Double.parseDouble(EPSILON_C_2);
        //Log.d("Outubro05","\n"+GAMMA12);
        //Float f_GAMMA12= Float.parseFloat(GAMMA12);
        Double f_GAMMA12= Double.parseDouble(GAMMA12);

        Double f_E1= Double.parseDouble(E1);
        Double f_E2= Double.parseDouble(E2);
        Double f_G12= Double.parseDouble(G12);
//junho21
        String NU12=list.get(9);//cursor.getString(9)));//NU12`	TEXT,
        NU12=NU12.substring(NU12.indexOf(":")+1,NU12.length());
        Double f_NU12= Double.parseDouble(NU12);

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

        // Usamos a matriz S para obter as deformações

        Double epsilon_1=(sigma_1/f_E1)-sigma_2*(f_NU12/f_E1);
        Double epsilon_2=(sigma_2/f_E2)-sigma_1*(f_NU12/f_E1);
        Double gamma_12=tau_12/f_G12;

        Double if_1_t=Math.abs(epsilon_1/f_EPSILON_T_1); // Tração
        Double if_1_c=Math.abs(epsilon_1/f_EPSILON_C_1); // Compressão

        Double if_1;
        Double if_2;

        if(epsilon_1>0)
        {
            if_1=if_1_t;
        }
        else
        {
            if_1=if_1_c;
        }

// Vemos o IF na direção local 2

        Double if_2_t=Math.abs(epsilon_2/f_EPSILON_T_2); // Tração
        Double if_2_c=Math.abs(epsilon_2/f_EPSILON_C_2); // Compressão

        if(epsilon_2>0)
        {
            if_2=if_2_t;
        }
        else
        {
            if_2=if_2_c;
        }
//Fevereiro27, depuração
        Double if_12=Math.abs(gamma_12/f_GAMMA12); //cisalhamento

        array_list.add(""+if_1);
        array_list.add(""+if_2);
        array_list.add(""+if_12);

        return  array_list;
    }



/////////////////////////
}
