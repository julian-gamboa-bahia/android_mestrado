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
 * Created by julian on 07/10/17.
 *
 * Para introduzir  o parametro biaxial
 *
 * biaxial_experimental
 *
 * e colocda uma janela adicional
 */

public class numerico_tsai_wu {

    private Context myContext;

    private Float f_edit_textView_sigma_x;
    private Float f_edit_textView_sigma_y;
    private Float f_edit_textView_tau_xy;
    private Float f_edit_textView_angulo;

    private String lamina_usada="";
    private String criterio_usado="";
    private String envelope_usado="";

    private String endereco="";

    numerico_tsai_wu(Context myContext, String lamina_usada, String criterio_usado, String envelope_usado, String endereco)
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
        String    s_edit_textView_angulo,
        String biaxial_experimental
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

//Biaxial
        sinalizacao_erro= "Não é possível calcular o Indíce de Falha porque o valor de ";
        try{
            sinalizacao_erro=sinalizacao_erro.concat("Parâmetro Biaxial"+" não é do tipo NUMERICO");
            //this.f_edit_textView_angulo= Float.parseFloat(biaxial_experimental);
        }
        catch (Exception e) {
            Toast.makeText(myContext,sinalizacao_erro,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }
///// Após a verificação podemos calcular, é INSERIR
// não adianta inserir ESTADOS errados


    public ArrayList<String> calcular_if(String nome_lamina,Double biaxial_experimental)
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
 * Outubro 06/10/17.
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


        //Log.d("Outubro05",""+f_SIGMA_T_1);
        //Log.d("Outubro05",""+f_SIGMA_T_2);
        //Log.d("Outubro05",""+f_SIGMA_C_1);
        //Log.d("Outubro05",""+f_SIGMA_C_2);
        //Log.d("Outubro05",""+f_TAU12);

        /*
        * Numerico,
        *
        *
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

        Double if_1_t=Math.abs(sigma_1/f_SIGMA_T_1); // Tração
        Double if_1_c=Math.abs(sigma_1/f_SIGMA_C_1); // Compressão

        Double if_1;
        Double if_2;

// Neste critério o IF deve ser único, mas fazemos a composição por partes para que fique bem didático
// Por ter uima escrita um pouco extensa então formulamos individualmente os termos

        Double componente_potencia_1_sigma_1=sigma_1*((1/f_SIGMA_T_1)-(1/f_SIGMA_C_1));
        Double componente_potencia_1_sigma_2=sigma_2*((1/f_SIGMA_T_2)-(1/f_SIGMA_C_2));

        Double componente_potencia_2_sigma_1=(sigma_1*sigma_1)*(1/(f_SIGMA_T_1*f_SIGMA_C_1));
        Double componente_potencia_2_sigma_2=(sigma_2*sigma_2)*(1/(f_SIGMA_T_2*f_SIGMA_C_2));

        Double componente_potencia_2_tau_12=(tau_12*tau_12)*(1/(f_TAU12*f_TAU12));

//Previo ao compoente experimental


        Double F12_potencia_1=((1/f_SIGMA_T_1)-(1/f_SIGMA_C_1)+(1/f_SIGMA_T_2)-(1/f_SIGMA_C_2))*biaxial_experimental;
        Double F12_potencia_2=((1/(f_SIGMA_T_1*f_SIGMA_C_1))+(1/(f_SIGMA_T_2*f_SIGMA_C_2)))*biaxial_experimental*biaxial_experimental;
        Double F12_sem_normalizar=1-F12_potencia_1-F12_potencia_2;
        Double F12=F12_sem_normalizar/(2*biaxial_experimental*biaxial_experimental);

        Double componente_experimental=2*sigma_1*sigma_2*F12;

        Double somando=componente_potencia_1_sigma_1+componente_potencia_1_sigma_2+componente_potencia_2_sigma_1+componente_potencia_2_sigma_2+componente_potencia_2_tau_12+componente_experimental;
        Double if_unico=somando;
        //////////

        array_list.add(""+if_unico);

        //Log.d("verificar_entrada",""+this.f_edit_textView_sigma_x);
        //Log.d("verificar_entrada",""+this.f_edit_textView_sigma_y);
        //Log.d("verificar_entrada",""+this.f_edit_textView_tau_xy);
        //Log.d("verificar_entrada Wu",""+this.f_edit_textView_angulo);
        //Log.d("verificar_entrada Wu",""+biaxial_experimental);
        return  array_list;
    }



/////////////////////////
}
