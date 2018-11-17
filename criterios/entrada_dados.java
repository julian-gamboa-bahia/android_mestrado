package br.com.ven2020.envelopes2018.criterios;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.ven2020.envelopes2018.R;
import br.com.ven2020.envelopes2018.database.DatabaseHelper;
import br.com.ven2020.envelopes2018.database.historico;
import br.com.ven2020.envelopes2018.database.db_historico_estados_esforcos;
import br.com.ven2020.envelopes2018.envelopes.Coletar_informacoes;


public class entrada_dados extends AppCompatActivity {

    String nome_lamina="";
    String criterio_usado="";


    String s_edit_textView_sigma_x="";
    String s_edit_textView_sigma_y="";
    String s_edit_textView_tau_xy="";
    String s_edit_textView_angulo="";

    String origen="";
    String indice="";

    String wu_biaxial="";
    String envelope_usado="";

    //janeiro08, seekbar para facilitar uso do sistema

    SeekBar seek_bar_textView_sigma_x;
    SeekBar seek_bar_textView_sigma_y;
    SeekBar seek_bar_textView_tau_xy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entradas_dados);

        //janeiro08, seekbar para facilitar uso do sistema
        seek_bar_textView_sigma_x = (SeekBar)findViewById(R.id.seek_bar_textView_sigma_x);
        seek_bar_textView_sigma_y = (SeekBar)findViewById(R.id.seek_bar_textView_sigma_y);
        seek_bar_textView_tau_xy = (SeekBar)findViewById(R.id.seek_bar_textView_tau_xy);

        //pegamos a ultima lamina

        historico db_historico=new historico(this);
        nome_lamina=db_historico.ultima_lamina_usada();

        TextView textView_nome_lamina=(TextView) this.findViewById(R.id.nome_lamina);

        //pegamos a ultima critério usado usado

        criterio_usado=db_historico.ultima_criterio_usado();
//Nov16
// no caso de ser instanciado por LISTA PONTOS
        envelope_usado=getIntent().getStringExtra("criterio");

        if(envelope_usado!=null)
        {
            criterio_usado=envelope_usado;
//Log.d("Nov16","if(envelope_usado!=null)");

        }

//Log.d("Nov16","envelope_usado criterio_usado "+criterio_usado);
//Log.d("Nov16","envelope_usado---envelope_usado  "+envelope_usado);


        textView_nome_lamina.setText(
                nome_lamina+
                "\n"+
                "Com o critério:  "+criterio_usado

        );


// quando o calculo for refeito pegamos os valores do DB

        origen=getIntent().getStringExtra("origen");
        indice=getIntent().getStringExtra("indice");

        //Log.d("Outubro11","origen\n"+indice);

        if(origen==null)
        {
            origen="direto";
        }
//Maio12
        /*
        entrada_dados.putExtra("sigma_x",listar_pontos_array[posicao_ponto][0]+"");
        entrada_dados.putExtra("sigma_y",listar_pontos_array[posicao_ponto][1]+"");
        entrada_dados.putExtra("tau_xy",tau_xy+"");
        * */

        String sigma_x=getIntent().getStringExtra("sigma_x");
        String sigma_y=getIntent().getStringExtra("sigma_y");
        String tau_xy=getIntent().getStringExtra("tau_xy");
        String angulo=getIntent().getStringExtra("angulo");
        String criterio=getIntent().getStringExtra("criterio");

//Maio16
//Procuramos o paranetro de Wu
        if(criterio!=null)
        {
            if(criterio.contentEquals("Tsai-Wu"))
            {
                wu_biaxial=getIntent().getStringExtra("wu_biaxial");
                //Log.d("wu_biaxial",                "getIntent\nwu_biaxial\n"+wu_biaxial);
            }
        }

//        Log.d("Maio12","String criterio=getIntent\n"+criterio);

        if(criterio_usado.length()==0)
        {
            criterio_usado=criterio;
            textView_nome_lamina.setText(
                    nome_lamina+
                            "\n"+
                            "Com o critério:  "+criterio_usado

            );
        }

        EditText edit_textView_sigma_x=(EditText) findViewById(R.id.edit_textView_sigma_x);
        EditText edit_textView_sigma_y=(EditText) findViewById(R.id.edit_textView_sigma_y);
        EditText edit_textView_tau_xy=(EditText) findViewById(R.id.edit_textView_tau_xy);
        EditText edit_textView_angulo=(EditText) findViewById(R.id.edit_textView_angulo);

//Similar ao caso anterior
        if(origen.contentEquals("historico"))
        {

            Integer indice_numerico=Integer.parseInt(indice);
            ArrayList<String> estado=procurar_estados_diretamente(indice_numerico);

            //Apenas o ângulo é colocado sem ser convertido
            edit_textView_angulo.setText(estado.get(3));


            String valor_string=estado.get(0);
            final Float valor_float_edit_textView_sigma_x= Float.parseFloat(valor_string);
            edit_textView_sigma_x.setText(valor_float_edit_textView_sigma_x+"");

            valor_string=estado.get(1);
            final Float valor_float_edit_textView_sigma_y= Float.parseFloat(valor_string);
            edit_textView_sigma_y.setText(valor_float_edit_textView_sigma_y+"");

            valor_string=estado.get(2);
            final Float valor_float_edit_textView_tau_xy= Float.parseFloat(valor_string);
            edit_textView_tau_xy.setText(valor_float_edit_textView_tau_xy+"");

            ajustando_seekbars_50(
                    valor_float_edit_textView_sigma_x,
                    valor_float_edit_textView_sigma_y,
                    valor_float_edit_textView_tau_xy
            );
        }
        else
        {
//Se não for pelo histórico, pode ser pelo Estado já transferido

            if(
                    (sigma_x!=null) &&
                            (sigma_y!=null) &&
                            (tau_xy!=null) &&
                            (angulo!=null)
                    )
            {

                //Log.d("Maio12","entrada_dados\n"+sigma_x);
                //Apenas o ângulo é colocado sem ser convertido

                final Float valor_float_edit_textView_angulo= Float.parseFloat(angulo);
                edit_textView_angulo.setText(valor_float_edit_textView_angulo+"");

                //estadp de esforços

                final Float valor_float_edit_textView_sigma_x= Float.parseFloat(sigma_x);
                edit_textView_sigma_x.setText(valor_float_edit_textView_sigma_x+"");

                final Float valor_float_edit_textView_sigma_y= Float.parseFloat(sigma_y);
                edit_textView_sigma_y.setText(valor_float_edit_textView_sigma_y+"");

                final Float valor_float_edit_textView_tau_xy= Float.parseFloat(tau_xy);
                edit_textView_tau_xy.setText(valor_float_edit_textView_tau_xy+"");

            }
            else {
                //Log.d("Maio12else","entrada_dados\n"+sigma_x);
                colocar_valores(nome_lamina);
            }
        }

////////Oncreate
    }

    public void colocar_valores(String nome_lamina) {
        final EditText edit_textView_sigma_x=(EditText) findViewById(R.id.edit_textView_sigma_x);
        final EditText edit_textView_sigma_y=(EditText) findViewById(R.id.edit_textView_sigma_y);
        final EditText edit_textView_tau_xy=(EditText) findViewById(R.id.edit_textView_tau_xy);
        EditText edit_textView_angulo=(EditText) findViewById(R.id.edit_textView_angulo);

        db_historico_estados_esforcos db_historico_estados=new db_historico_estados_esforcos(this);
        ArrayList<String> obter_ultimo_estados_esforcos=db_historico_estados.obter_ultimo_estados_esforcos();

        if(obter_ultimo_estados_esforcos.size()>0)
        {
//Janeiro08
// notação cientifica
            String valor_string=obter_ultimo_estados_esforcos.get(0);
            final Float valor_float_edit_textView_sigma_x= Float.parseFloat(valor_string);
            edit_textView_sigma_x.setText(valor_float_edit_textView_sigma_x+"");

            valor_string=obter_ultimo_estados_esforcos.get(1);
            final Float valor_float_edit_textView_sigma_y= Float.parseFloat(valor_string);

            edit_textView_sigma_y.setText(valor_float_edit_textView_sigma_y+"");

            valor_string=obter_ultimo_estados_esforcos.get(2);
            final Float valor_float_edit_textView_tau_xy= Float.parseFloat(valor_string);

            edit_textView_tau_xy.setText(valor_float_edit_textView_tau_xy+"");
            edit_textView_angulo.setText(obter_ultimo_estados_esforcos.get(3));

            ajustando_seekbars_50(
                    valor_float_edit_textView_sigma_x,
                    valor_float_edit_textView_sigma_y,
                    valor_float_edit_textView_tau_xy
            );
        }
        else
        {
//escondemos os SeekBars dado que começa desde zero...
            seek_bar_textView_sigma_x.setVisibility(View.GONE);
            seek_bar_textView_sigma_y.setVisibility(View.GONE);
            seek_bar_textView_tau_xy.setVisibility(View.GONE);

            edit_textView_sigma_x.setText("0");
            edit_textView_sigma_y.setText("0");
            edit_textView_tau_xy.setText("0");
            edit_textView_angulo.setText("0");
        }
    }

    /*
    * No momento de calcular fzemos uma verificação de dados
    * Exceto nos casos em que já estiver colocado por passe de argumentos
    * */

    public void Onclick_calcular(View v)
    {
        EditText edit_textView_sigma_x=(EditText) findViewById(R.id.edit_textView_sigma_x);
        EditText edit_textView_sigma_y=(EditText) findViewById(R.id.edit_textView_sigma_y);
        EditText edit_textView_tau_xy=(EditText) findViewById(R.id.edit_textView_tau_xy);
        EditText edit_textView_angulo=(EditText) findViewById(R.id.edit_textView_angulo);

        s_edit_textView_sigma_x=edit_textView_sigma_x.getText().toString();
        s_edit_textView_sigma_y=edit_textView_sigma_y.getText().toString();
        s_edit_textView_tau_xy=edit_textView_tau_xy.getText().toString();
        s_edit_textView_angulo=edit_textView_angulo.getText().toString();

        criterios_nao_experimentais();

//criterio_5
        /*
        * Usamos uma janela para coletar o parametro experimental
        * */
        if(criterio_usado.contentEquals("Tsai-Wu"))
        {
            janela_tsai_wu();
        }
///////////////////criterio_6
///////////////////criterio_7
        if(criterio_usado.contentEquals("Hashin"))
        {

            janela_hashin();
        }
///////////////////criterio_8
        if(criterio_usado.contentEquals("Christensen"))
        {
            numerico_cristensen numerico_cristensen = new numerico_cristensen(
                    getBaseContext(),
                    nome_lamina,
                    criterio_usado,
                    "",//envelope_usado
                    getFilesDir().getAbsolutePath()
            );


            if(numerico_cristensen.verificar_entrada(
                    s_edit_textView_sigma_x,
                    s_edit_textView_sigma_y,
                    s_edit_textView_tau_xy,
                    s_edit_textView_angulo
            )) {

                ArrayList<String> array_list = new ArrayList<String>();
                array_list = numerico_cristensen.calcular_if(nome_lamina);

                Intent saida_criterios = new Intent(getBaseContext(), saida_criterios.class);
                saida_criterios.putExtra("nome_lamina", nome_lamina);
                saida_criterios.putExtra("criterio_usado", criterio_usado);


                saida_criterios.putExtra("IF_0", array_list.get(0));
                saida_criterios.putExtra("IF_1", array_list.get(1));
//È preciso colocar pelo menos um STRING vazio ""
                saida_criterios.putExtra("IF_2", "");

                saida_criterios.putExtra("IF_0_nome", "if (fibra)");
                saida_criterios.putExtra("IF_1_nome", "if (matriz)");

                //Log.d("Outubro12","numerico_tsai_wu.calcular_if+\n");

                startActivity(saida_criterios);
            }

        }
///////////////////criterio_9
        if(criterio_usado.contentEquals("Puck"))
        {
            Intent Coletar_criterios_puck_larc03 = new Intent(getBaseContext(), Coletar_criterios_puck_larc03.class);
            Coletar_criterios_puck_larc03.putExtra("funcao","coletar_puck");

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


            Coletar_criterios_puck_larc03.putExtra("TAU12",ajuda_TAU12);
            Coletar_criterios_puck_larc03.putExtra("NU12",ajuda_NU12);
            Coletar_criterios_puck_larc03.putExtra("E1",ajuda_E1);

            startActivityForResult(Coletar_criterios_puck_larc03, 109);
        }


        if(criterio_usado.contentEquals("Larc03"))
        {

            Intent Coletar_criterios_puck_larc03 = new Intent(getBaseContext(), Coletar_criterios_puck_larc03.class);
            Coletar_criterios_puck_larc03.putExtra("funcao","coletar_larc03");

            DatabaseHelper dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
            try {
                dbHelper.prepareDatabase();
            } catch (IOException e) {
            }
            List<String> list = dbHelper.obter_propriedades_laminas(nome_lamina);

            String ajuda_TAU12=list.get(16);
            ajuda_TAU12=ajuda_TAU12.substring(ajuda_TAU12.indexOf(":")+1,ajuda_TAU12.length());


            Coletar_criterios_puck_larc03.putExtra("TAU12",ajuda_TAU12);

            startActivityForResult(Coletar_criterios_puck_larc03, 110);
        }
    }

//Nov16


    //Larc03
    Double global_larc03_alpha_0=0.0;
    Double global_larc03_TAU23=0.0;
    Double global_larc03_Y_T_is=0.0;
    Double global_larc03_S_L_is=0.0;
    //puck
    Double global_puck_m_sigF=1.0;
    Double global_puck_p_plus_TL=1.0;
    Double global_puck_p_minus_TL=1.0;
    Double global_puck_p_minus_TT=1.0;
    Double global_puck_sigma_1_D=1.0;
    Double global_puck_puck_R_TT_A=1.0;
    Double global_puck_TAU12_C=1.0;
    Double global_puck_NU12_f=1.0;
    Double global_puck_E1_f=1.0;
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    //larc03
    if(requestCode==109)
    {
        if (resultCode == RESULT_OK) {
            String m_sigF = data.getStringExtra("m_sigF_coletado");
            String p_plus_TL = data.getStringExtra("p_plus_TL_coletado");
            String p_minus_TL = data.getStringExtra("p_minus_TL_coletado");
            String p_minus_TT = data.getStringExtra("p_minus_TT_coletado");
            String sigma_1_D = data.getStringExtra("sigma_1_D_coletado");
            String puck_R_TT_A = data.getStringExtra("puck_R_TT_A_coletado");
            String TAU12_C = data.getStringExtra("TAU12_C_coletado");
            String NU12 = data.getStringExtra("NU12_coletado");
//Log.d("Nov07","NU12  "+NU12);
            String E1 = data.getStringExtra("E1_coletado");

            global_puck_m_sigF=Double.parseDouble(m_sigF);
            global_puck_p_plus_TL=Double.parseDouble(p_plus_TL);
            global_puck_p_minus_TL=Double.parseDouble(p_minus_TL);
            global_puck_p_minus_TT=Double.parseDouble(p_minus_TT);
            global_puck_sigma_1_D=Double.parseDouble(sigma_1_D);
            global_puck_puck_R_TT_A=Double.parseDouble(puck_R_TT_A);
            global_puck_TAU12_C=Double.parseDouble(TAU12_C);
            global_puck_NU12_f=Double.parseDouble(NU12);
            global_puck_E1_f=Double.parseDouble(E1);


            ativar_criterio_puck();
        }
        else
        {
            finish();
        }
    }


    //larc03
    if(requestCode==110)
    {
            if (resultCode == RESULT_OK) {
                String alpha = data.getStringExtra("alpha_coletado");
                String TAU23 = data.getStringExtra("TAU23_coletado");
                String Y_T_is = data.getStringExtra("Y_T_is_coletado");
                String S_L_is = data.getStringExtra("S_L_is_coletado");

                global_larc03_alpha_0=Double.parseDouble(alpha);
                global_larc03_TAU23=Double.parseDouble(TAU23);
                global_larc03_Y_T_is=Double.parseDouble(Y_T_is);
                global_larc03_S_L_is=Double.parseDouble(S_L_is);
                ativar_criterio_larc03();
            }
            else
            {
                finish();
            }
        }
}
//Nov16
    public void ativar_criterio_larc03() {
/*
        Log.d("Nov17","  global_larc03_alpha_0 "+global_larc03_alpha_0);
        Log.d("Nov17"," global_larc03_TAU23  "+global_larc03_TAU23);
        Log.d("Nov17"," global_larc03_Y_T_is  "+global_larc03_Y_T_is);
        Log.d("Nov17","  global_larc03_S_L_is "+global_larc03_S_L_is);
*/

        numerico_larc03 numerico_larc03 = new numerico_larc03(
                getBaseContext(),
                nome_lamina,
                criterio_usado,
                "",//envelope_usado
                getFilesDir().getAbsolutePath(),
                global_larc03_alpha_0,
                global_larc03_TAU23,
                global_larc03_Y_T_is,
                global_larc03_S_L_is
        );

        if (numerico_larc03.verificar_entrada(
                s_edit_textView_sigma_x,
                s_edit_textView_sigma_y,
                s_edit_textView_tau_xy,
                s_edit_textView_angulo
        )) {

            ArrayList<String> array_list = new ArrayList<String>();
            array_list = numerico_larc03.calcular_if(nome_lamina,0.0);

            Intent saida_criterios = new Intent(getBaseContext(), saida_criterios.class);
            saida_criterios.putExtra("nome_lamina", nome_lamina);
            saida_criterios.putExtra("criterio_usado", criterio_usado);


            saida_criterios.putExtra("IF_0", array_list.get(0));
            saida_criterios.putExtra("IF_1", array_list.get(1));
//È preciso colocar pelo menos um STRING vazio ""
            saida_criterios.putExtra("IF_2",array_list.get(2));
            saida_criterios.putExtra("IF_3",array_list.get(3));
            saida_criterios.putExtra("IF_4",array_list.get(4));
            saida_criterios.putExtra("IF_5",array_list.get(5));

            saida_criterios.putExtra("IF_0_nome", "if (larc03#01)");
            saida_criterios.putExtra("IF_1_nome", "if (larc03#02)");
            saida_criterios.putExtra("IF_2_nome", "if (larc03#03)");
            saida_criterios.putExtra("IF_3_nome", "if (larc03#04)");
            saida_criterios.putExtra("IF_4_nome", "if (larc03#05)");
            saida_criterios.putExtra("IF_5_nome", "if (larc03#05)");

            //Log.d("Outubro12","numerico_tsai_wu.calcular_if+\n");

            startActivity(saida_criterios);
        }
    }

    //Nov16
    public void ativar_criterio_puck() {

    /*
                global_puck_m_sigF=Double.parseDouble(m_sigF);
            global_puck_p_plus_TL=Double.parseDouble(p_plus_TL);
            global_puck_p_minus_TL=Double.parseDouble(p_minus_TL);
            global_puck_p_minus_TT=Double.parseDouble(p_minus_TT);
            global_puck_sigma_1_D=Double.parseDouble(sigma_1_D);
            global_puck_puck_R_TT_A=Double.parseDouble(puck_R_TT_A);
            global_puck_TAU12_C=Double.parseDouble(TAU12_C);
            global_puck_NU12_f=Double.parseDouble(NU12);
            global_puck_E1_f=Double.parseDouble(E1);
    * */

        numerico_puck03 numerico_puck03 = new numerico_puck03(
                getBaseContext(),
                nome_lamina,
                criterio_usado,
                "",//envelope_usado
                getFilesDir().getAbsolutePath(),
                global_puck_m_sigF,
        global_puck_p_plus_TL,
        global_puck_p_minus_TL,
        global_puck_p_minus_TT,
        global_puck_sigma_1_D,
        global_puck_puck_R_TT_A,
        global_puck_TAU12_C,
        global_puck_NU12_f,
        global_puck_E1_f
        );


        if (numerico_puck03.verificar_entrada(
                s_edit_textView_sigma_x,
                s_edit_textView_sigma_y,
                s_edit_textView_tau_xy,
                s_edit_textView_angulo
        )) {

            ArrayList<String> array_list = new ArrayList<String>();
            array_list = numerico_puck03.calcular_if(nome_lamina,0.0);

            Intent saida_criterios = new Intent(getBaseContext(), saida_criterios.class);
            saida_criterios.putExtra("nome_lamina", nome_lamina);
            saida_criterios.putExtra("criterio_usado", criterio_usado);


            saida_criterios.putExtra("IF_0", array_list.get(0));
            saida_criterios.putExtra("IF_1", array_list.get(1));
//È preciso colocar pelo menos um STRING vazio ""
            saida_criterios.putExtra("IF_2",array_list.get(2));
            saida_criterios.putExtra("IF_3",array_list.get(3));
            saida_criterios.putExtra("IF_4",array_list.get(4));

            saida_criterios.putExtra("IF_0_nome", "if (modo #01)");
            saida_criterios.putExtra("IF_1_nome", "if (modo #02)");
            saida_criterios.putExtra("IF_2_nome", "if (modo #03)");
            saida_criterios.putExtra("IF_3_nome", "if (modo #04)");
            saida_criterios.putExtra("IF_4_nome", "if (modo #05)");

            //Log.d("Outubro12","numerico_tsai_wu.calcular_if+\n");

            startActivity(saida_criterios);
        }
    }



    public void coleta_dados(String nome_lamina){
        EditText edit_textView_sigma_x=(EditText) findViewById(R.id.edit_textView_sigma_x);
        EditText edit_textView_sigma_y=(EditText) findViewById(R.id.edit_textView_sigma_y);
        EditText edit_textView_tau_xy=(EditText) findViewById(R.id.edit_textView_tau_xy);
        EditText edit_textView_angulo=(EditText) findViewById(R.id.edit_textView_angulo);

        String s_edit_textView_sigma_x=edit_textView_sigma_x.getText().toString();
        String s_edit_textView_sigma_y=edit_textView_sigma_y.getText().toString();
        String s_edit_textView_tau_xy=edit_textView_tau_xy.getText().toString();
        String s_edit_textView_angulo=edit_textView_angulo.getText().toString();

        //Log.d("Outubro04",s_edit_textView_sigma_x);
        //Log.d("Outubro04",s_edit_textView_sigma_y);
        //Log.d("Outubro04",s_edit_textView_tau_xy);
        //Log.d("Outubro04",s_edit_textView_angulo);
    }

/*
Outubro07
Chamamos os critérios que não precisam de parametros experimentais
*/
    public void criterios_nao_experimentais(){
        //criterio_1
        if(criterio_usado.contentEquals("Máxima Tensão"))
        {
            numerico_maxima_tensao numerico_maxima_tensao = new numerico_maxima_tensao(
                    getBaseContext(),
                    nome_lamina,
                    criterio_usado,
                    "",//envelope_usado
                    getFilesDir().getAbsolutePath()
            );


            if(numerico_maxima_tensao.verificar_entrada(
                    s_edit_textView_sigma_x,
                    s_edit_textView_sigma_y,
                    s_edit_textView_tau_xy,
                    s_edit_textView_angulo
            ))
            {
                ArrayList<String> array_list = new ArrayList<String>();
                array_list=numerico_maxima_tensao.calcular_if(nome_lamina);

                if(array_list.size()>0)
                {
                    Intent saida_criterios = new Intent(getBaseContext(),saida_criterios.class);
                    saida_criterios.putExtra("nome_lamina", nome_lamina);
                    saida_criterios.putExtra("criterio_usado", criterio_usado);

                        saida_criterios.putExtra("IF_2", array_list.get(2));
                        saida_criterios.putExtra("IF_1", array_list.get(1));
                        saida_criterios.putExtra("IF_0", array_list.get(0));


                    saida_criterios.putExtra("IF_0_nome", "if (longitudinal)");
                    saida_criterios.putExtra("IF_1_nome", "if (transversal)");
                    saida_criterios.putExtra("IF_2_nome", "if (cisalhante)");

                    startActivity(saida_criterios);

                }

            }
        }
//criterio_2
        if(criterio_usado.contentEquals("Máxima Deformação"))
        {
            numerico_maxima_deformacao numerico_maxima_deformacao = new numerico_maxima_deformacao(
                    getBaseContext(),
                    nome_lamina,
                    criterio_usado,
                    "",//envelope_usado
                    getFilesDir().getAbsolutePath()
            );

            //junho21

            String base="http://127.0.0.1/maio/criterios/maxima_deformacao.htm?/82.0/8/00/"+
                    s_edit_textView_sigma_x+
                    "/"+
                    s_edit_textView_sigma_y;
                base=base+"/"+s_edit_textView_tau_xy+"/0/2e10";


            if(numerico_maxima_deformacao.verificar_entrada(
                    s_edit_textView_sigma_x,
                    s_edit_textView_sigma_y,
                    s_edit_textView_tau_xy,
                    s_edit_textView_angulo
            ))
            {
                ArrayList<String> array_list = new ArrayList<String>();
                array_list=numerico_maxima_deformacao.calcular_if(nome_lamina);

                if(array_list.size()>0)
                {
                    Intent saida_criterios = new Intent(getBaseContext(),saida_criterios.class);
                    saida_criterios.putExtra("nome_lamina", nome_lamina);
                    saida_criterios.putExtra("criterio_usado", criterio_usado);


                        saida_criterios.putExtra("IF_2", array_list.get(2));
                        saida_criterios.putExtra("IF_1", array_list.get(1));
                        saida_criterios.putExtra("IF_0", array_list.get(0));

                    saida_criterios.putExtra("IF_0_nome", "if (longitudinal)");
                    saida_criterios.putExtra("IF_1_nome", "if (transversal)");
                    saida_criterios.putExtra("IF_2_nome", "if (cisalhante)");

                    startActivity(saida_criterios);

                }

            }
        }
//criterio_3
        if(criterio_usado.contentEquals("Tsai-Hill"))
        {
            numerico_tsai_hill numerico_tsai_hill = new numerico_tsai_hill(
                    getBaseContext(),
                    nome_lamina,
                    criterio_usado,
                    "",//envelope_usado
                    getFilesDir().getAbsolutePath()
            );


            if(numerico_tsai_hill.verificar_entrada(
                    s_edit_textView_sigma_x,
                    s_edit_textView_sigma_y,
                    s_edit_textView_tau_xy,
                    s_edit_textView_angulo
            ))
            {
                ArrayList<String> array_list = new ArrayList<String>();
                array_list=numerico_tsai_hill.calcular_if(nome_lamina);

                if(array_list.size()>0)
                {
                    Intent saida_criterios = new Intent(getBaseContext(),saida_criterios.class);
                    saida_criterios.putExtra("nome_lamina", nome_lamina);
                    saida_criterios.putExtra("criterio_usado", criterio_usado);

                    saida_criterios.putExtra("IF_2", "");
                    saida_criterios.putExtra("IF_1", "");
                    saida_criterios.putExtra("IF_0", array_list.get(0));

                    saida_criterios.putExtra("IF_0_nome", "if ");


                    startActivity(saida_criterios);

                }

            }
        }
//criterio_4
        if(criterio_usado.contentEquals("Azzi-Tsai"))
        {
            numerico_azzi_tsai numerico_azzi_tsai = new numerico_azzi_tsai(
                    getBaseContext(),
                    nome_lamina,
                    criterio_usado,
                    "",//envelope_usado
                    getFilesDir().getAbsolutePath()
            );


            if(numerico_azzi_tsai.verificar_entrada(
                    s_edit_textView_sigma_x,
                    s_edit_textView_sigma_y,
                    s_edit_textView_tau_xy,
                    s_edit_textView_angulo
            ))
            {
                ArrayList<String> array_list = new ArrayList<String>();
                array_list=numerico_azzi_tsai.calcular_if(nome_lamina);

                if(array_list.size()>0)
                {
                    Intent saida_criterios = new Intent(getBaseContext(),saida_criterios.class);
                    saida_criterios.putExtra("nome_lamina", nome_lamina);
                    saida_criterios.putExtra("criterio_usado", criterio_usado);


                    saida_criterios.putExtra("IF_2", "");
                    saida_criterios.putExtra("IF_1", "");
                    saida_criterios.putExtra("IF_0", array_list.get(0));

                    saida_criterios.putExtra("IF_0_nome", "if ");
                    startActivity(saida_criterios);
                }
            }
        }
//Critério Hoffman
        if(criterio_usado.contentEquals("Hoffman"))
        {
            numerico_hoffman numerico_hoffman = new numerico_hoffman(
                    getBaseContext(),
                    nome_lamina,
                    criterio_usado,
                    "",//envelope_usado
                    getFilesDir().getAbsolutePath()
            );


            if(numerico_hoffman.verificar_entrada(
                    s_edit_textView_sigma_x,
                    s_edit_textView_sigma_y,
                    s_edit_textView_tau_xy,
                    s_edit_textView_angulo
            ))
            {
                ArrayList<String> array_list = new ArrayList<String>();
                array_list=numerico_hoffman.calcular_if(nome_lamina);

                if(array_list.size()>0)
                {
                    Intent saida_criterios = new Intent(getBaseContext(),saida_criterios.class);
                    saida_criterios.putExtra("nome_lamina", nome_lamina);
                    saida_criterios.putExtra("criterio_usado", criterio_usado);


                    saida_criterios.putExtra("IF_2", "");
                    saida_criterios.putExtra("IF_1", "");
                    saida_criterios.putExtra("IF_0", array_list.get(0));

                    saida_criterios.putExtra("IF_0_nome", "if ");

                    startActivity(saida_criterios);
                }
            }
            //Log.d("Outubro07","7777777777777777777777777"+criterio_usado);
        }
        //////////////
    }

/*
* Outubro07
* janela para obter parametros experimentais
* */

    public void  janela_tsai_wu()
    {
        AlertDialog.Builder aa=new AlertDialog.Builder(this);

//TITULO
        aa.setTitle("Paramêtro Experimental");
// TEXT entrada
// Set up the input
        final EditText input = new EditText(this);
        aa.setView(input);

        if(wu_biaxial.length()==0)
        {
            DatabaseHelper dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
            try {
                dbHelper.prepareDatabase();
            } catch (IOException e) {
            }
            List<String> list = dbHelper.obter_propriedades_laminas(nome_lamina);

            String ajuda_TAU12=list.get(16);
            ajuda_TAU12=ajuda_TAU12.substring(ajuda_TAU12.indexOf(":")+1,ajuda_TAU12.length());

            wu_biaxial=ajuda_TAU12;
        }

        input.setText(wu_biaxial);



        aa.setPositiveButton("Aceitar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        //Double D_biaxial_experimental=Double.parseDouble(m_Text);
//Nov17 Log.d("wu_biaxial","D_biaxial_experimental\n"+D_biaxial_experimental);
                        operacoes_janela_tsai_wu(m_Text);
                        //Log.d("Outubro07","Estranho que não abre a ativity\n");
                    }
                });

        aa.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        aa.show();
    }
/*
Outubro07
Complemenmto da janela para obter parametros experimentais
* */
    public void operacoes_janela_tsai_wu(String biaxial_experimental)
    {
        numerico_tsai_wu numerico_tsai_wu = new numerico_tsai_wu(
                getBaseContext(),
                nome_lamina,
                criterio_usado,
                "",//envelope_usado
                getFilesDir().getAbsolutePath()
        );


        if(numerico_tsai_wu.verificar_entrada(
                s_edit_textView_sigma_x,
                s_edit_textView_sigma_y,
                s_edit_textView_tau_xy,
                s_edit_textView_angulo,
                biaxial_experimental
        )) {

            Double f_biaxial_experimental= Double.parseDouble(biaxial_experimental);


            ArrayList<String> array_list = new ArrayList<String>();
            array_list = numerico_tsai_wu.calcular_if(nome_lamina,f_biaxial_experimental);

            Intent saida_criterios = new Intent(getBaseContext(), saida_criterios.class);
            saida_criterios.putExtra("nome_lamina", nome_lamina);
            saida_criterios.putExtra("criterio_usado", criterio_usado);


            saida_criterios.putExtra("IF_2", "");
            saida_criterios.putExtra("IF_1", "");
            saida_criterios.putExtra("IF_0", array_list.get(0));

            saida_criterios.putExtra("IF_0_nome", "if ");

            //Log.d("Outubro12","numerico_tsai_wu.calcular_if+\n");

            saida_criterios.putExtra("parametro_experimental_1", ""+biaxial_experimental);
            startActivity(saida_criterios);
        }
    }

    /**
     * Outubro11
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
* janela para obter parametros experimentais, neste caso o TAU23
*
* */
    public void janela_hashin()
    {
        AlertDialog.Builder aa=new AlertDialog.Builder(this);

//TITULO
        aa.setTitle("Valor de TAU23");
// TEXT entrada
// Set up the input
        final EditText input = new EditText(this);
//Usamos como valor de
        DatabaseHelper dbHelper = new DatabaseHelper(getBaseContext(),getFilesDir().getAbsolutePath());
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
        }

        List<String> list = dbHelper.obter_propriedades_laminas(nome_lamina);
        String TAU23=list.get(17);//        list.add("TAU23: "+cursor.getString(18));//TAU12`	TEXT,
        TAU23=TAU23.substring(TAU23.indexOf(":")+1,TAU23.length());

        input.setText(TAU23);


        aa.setView(input);

        aa.setPositiveButton("Aceitar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        Double D_biaxial_experimental=Double.parseDouble(m_Text);
                        operacoes_janela_hashin(D_biaxial_experimental);
                        //Log.d("Outubro07","Estranho que não abre a ativity\n");
                    }
                });

        aa.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        aa.show();
    }
    /*
    Outubro07
    Complemenmto da janela para obter parametros experimentais
    * */
    public void operacoes_janela_hashin(Double biaxial_experimental)
    {
        numerico_hashin numerico_hashin = new numerico_hashin(
                getBaseContext(),
                nome_lamina,
                criterio_usado,
                "",//envelope_usado
                getFilesDir().getAbsolutePath()
        );


        if(numerico_hashin.verificar_entrada(
                s_edit_textView_sigma_x,
                s_edit_textView_sigma_y,
                s_edit_textView_tau_xy,
                s_edit_textView_angulo
        )) {

            ArrayList<String> array_list = new ArrayList<String>();

            array_list = numerico_hashin.calcular_if(nome_lamina,biaxial_experimental);

            if(array_list.size()>0)
            {
                Intent saida_criterios = new Intent(getBaseContext(),saida_criterios.class);
                saida_criterios.putExtra("nome_lamina", nome_lamina);
                saida_criterios.putExtra("criterio_usado", criterio_usado);


                saida_criterios.putExtra("IF_2", "");
                saida_criterios.putExtra("IF_1", array_list.get(1));
                saida_criterios.putExtra("IF_0", array_list.get(0));

                saida_criterios.putExtra("IF_1_nome", "if (matriz)");
                saida_criterios.putExtra("IF_0_nome", "if (fibra)");

                saida_criterios.putExtra("parametro_experimental_1", ""+biaxial_experimental);
                startActivity(saida_criterios);
            }


        }
    }
//janeiro09
public void ajustando_seekbars_50(
        final float valor_float_edit_textView_sigma_x,
        final float valor_float_edit_textView_sigma_y,
        final float valor_float_edit_textView_tau_xy

)
{
    final EditText edit_textView_sigma_x=(EditText) findViewById(R.id.edit_textView_sigma_x);
    final EditText edit_textView_sigma_y=(EditText) findViewById(R.id.edit_textView_sigma_y);
    final EditText edit_textView_tau_xy=(EditText) findViewById(R.id.edit_textView_tau_xy);
    //colocamos os valores conforme o Seekbar

    seek_bar_textView_sigma_x.setProgress(50);
    seek_bar_textView_sigma_y.setProgress(50);
    seek_bar_textView_tau_xy.setProgress(50);

    seek_bar_textView_sigma_x.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            Double relacao=(seekBar.getProgress()+0.0)/50;
            Double valor=(relacao)*valor_float_edit_textView_sigma_x;
            edit_textView_sigma_x.setText(""+valor);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            Double relacao=(seekBar.getProgress()+0.0)/50;
            Double valor=(relacao)*valor_float_edit_textView_sigma_x;
            edit_textView_sigma_x.setText(""+valor);
        }
    });

    seek_bar_textView_sigma_y.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            Double relacao=(seekBar.getProgress()+0.0)/50;
            Double valor=(relacao)*valor_float_edit_textView_sigma_y;
            edit_textView_sigma_y.setText(""+valor);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            Double relacao=(seekBar.getProgress()+0.0)/50;
            Double valor=(relacao)*valor_float_edit_textView_sigma_y;
            edit_textView_sigma_y.setText(""+valor);
        }
    });

    seek_bar_textView_tau_xy.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            Double relacao=(seekBar.getProgress()+0.0)/50;
            Double valor=(relacao)*valor_float_edit_textView_tau_xy;
            edit_textView_tau_xy.setText(""+valor);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            Double relacao=(seekBar.getProgress()+0.0)/50;
            Double valor=(relacao)*valor_float_edit_textView_tau_xy;
            edit_textView_tau_xy.setText(""+valor);
        }
    });
}
//

//JUlLHO  18
// Com o intuito de compartilhar os gráficos com minha princesa vou colocar este sistema
// de captura imagem e enviar pelo WhatsAPP
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_direito_criterio_entrada, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.calcular_indice_falha) {

            Toast.makeText(getBaseContext(),"Calculando o Indíce de Falha",Toast.LENGTH_LONG).show();

            View nova=new View(this);

            Onclick_calcular(nova);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

// //////////
//////////////////////////
}
