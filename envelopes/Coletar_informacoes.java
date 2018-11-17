package br.com.ven2020.envelopes2018.envelopes;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import br.com.ven2020.envelopes2018.Config;
import br.com.ven2020.envelopes2018.R;


public class Coletar_informacoes extends AppCompatActivity {

    String global_TAU12="";
    String global_NU12="";
    String global_E1="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coletar_informacoes);


        Bundle extras = getIntent().getExtras();

        String funcao="";
        funcao=extras.getString("funcao");
        global_TAU12=extras.getString("TAU12");
        global_NU12=extras.getString("NU12");
        global_E1=extras.getString("E1");

        if(funcao.contentEquals("coletar_numero_pontos"))
        {
            coletar_numero_pontos(Config.numeroPontos);
        }

//coletar_larc03
        if(funcao.contentEquals("coletar_larc03"))
        {
            coletar_larc03(
                    Config.numero_circulos,
                    Config.larc03_alpha_0,
                    Config.numero_elementos_circulos
            );
        }
//Nov07
        if(funcao.contentEquals("coletar_puck"))
        {
            coletar_puck(
                    Config.numero_circulos,
                    Config.puck_m_sigF,
                    Config.numero_elementos_circulos
            );
        }


    }


//Novembro06

    public void coletar_numero_pontos(int numero)
    {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("Número de pontos");
        //define a mensagem
        builder.setMessage("Por favor indique o número de pontos do envelope");
        //Colocamos a caixa de texto de entrada
        final EditText input = new EditText(this);
        input.setText(numero + "");
        builder.setView(input);

        //define um botão como positivo
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("valor_coletado", m_Text);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }});
        builder.show();
    }


    String global_larc03_numero_circulos="";

    //Outubro 29
    public void coletar_larc03(
            int numero,
            final double larc03_alpha_0,
            int numero_circulos
    )
    {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("(Larc03)  Número de círculos (GRID)");
        //define a mensagem
        builder.setMessage("Por favor indique o número de círculos da grid");
        //Colocamos a caixa de texto de entrada
        final EditText input = new EditText(this);
        input.setText(numero + "");
        builder.setView(input);

        //define um botão como positivo
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_larc03_numero_circulos=m_Text;
                coletar_larc03_alpha(larc03_alpha_0,Config.numero_elementos_circulos);
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }});
        builder.show();
    }

//Novembro06

    String global_larc03_alpha_0="";

    public void coletar_larc03_alpha(
            double larc03_alpha_0,
            final int numero_elementos_circulos
    )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Larc03) Valor do alpha");
        builder.setMessage("Por favor indique o ângulo alpha (Degree)");
        final EditText input = new EditText(this);
        input.setText(larc03_alpha_0+ "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_larc03_alpha_0=m_Text;
                coletar_numero_elementos_do_circulo(numero_elementos_circulos);

            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }});
        builder.show();
    }
//Novembro06
    String global_larc03_numero_elementos_circulos="";

    public void coletar_numero_elementos_do_circulo(
            int numero_circulos_coletado
    )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Larc03) elementos do círculos");
        builder.setMessage("Número de elementos do círculos da GRID");
        final EditText input = new EditText(this);
        input.setText(numero_circulos_coletado+ "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_larc03_numero_elementos_circulos=m_Text;
                coletar_TAU23(global_TAU12);
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }});
        builder.show();
    }

//Novembro06
    String global_larc03_TAU23="";

    public void coletar_TAU23(
            String TAU12
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Larc03) TAU23");
        builder.setMessage("TAU23 (Pa)");
        final EditText input = new EditText(this);
        input.setText(TAU12 + "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_larc03_TAU23 = m_Text;

                coletar_Y_T_is(global_TAU12);
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

    //Novembro06
    String global_larc03_Y_T_is="";

    public void coletar_Y_T_is(
            String TAU12
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Larc03) Y_T_is");
        builder.setMessage("Y_T_is (Pa)");
        final EditText input = new EditText(this);
        input.setText(TAU12 + "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_larc03_Y_T_is = m_Text;

                coletar_S_L_is(global_TAU12);
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

    //Novembro06
    String global_larc03_S_L_is="";

    public void coletar_S_L_is(
            String TAU12
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Larc03) S_L_is");
        builder.setMessage("S_L_is (Pa)");
        final EditText input = new EditText(this);

        input.setText(Math.sqrt(2)*Double.parseDouble(TAU12) + "");

        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_larc03_S_L_is = m_Text;

                Intent resultIntent = new Intent();

                resultIntent.putExtra("numero_circulos_coletado", global_larc03_numero_circulos);
                resultIntent.putExtra("alpha_coletado", global_larc03_alpha_0);
                resultIntent.putExtra("numero_circulos_elementos_coletado", global_larc03_numero_elementos_circulos);
                resultIntent.putExtra("TAU23_coletado", global_larc03_TAU23);
                resultIntent.putExtra("Y_T_is_coletado", global_larc03_Y_T_is);
                resultIntent.putExtra("S_L_is_coletado", global_larc03_S_L_is);

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

/////////////////////////////////////////////////////////////////////////////
    String global_puck_numero_circulos="";

    //Outubro 29
    public void coletar_puck(
            int numero,
            final double puck_m_sigF,
            int numero_circulos
    )
    {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("(Puck)  Número de círculos (GRID)");
        //define a mensagem
        builder.setMessage("Por favor indique o número de círculos da grid");
        //Colocamos a caixa de texto de entrada
        final EditText input = new EditText(this);
        input.setText(numero + "");
        builder.setView(input);

        //define um botão como positivo
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_puck_numero_circulos=m_Text;
                coletar_puck_puck_m_sigF(puck_m_sigF,Config.numero_elementos_circulos);
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }});
        builder.show();
    }

//Novembro06

    String global_puck_m_sigF="";

    public void coletar_puck_puck_m_sigF(
            double puck_m_sigF,
            final int numero_elementos_circulos
    )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Puck) Valor do m_sigF");
        builder.setMessage("Por favor indique o m_sigF");
        final EditText input = new EditText(this);
        input.setText(puck_m_sigF+ "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_puck_m_sigF=m_Text;
                puck_coletar_numero_elementos_do_circulo(numero_elementos_circulos);

            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }});
        builder.show();
    }
    //Novembro06
    String global_puck_numero_elementos_circulos="";

    public void puck_coletar_numero_elementos_do_circulo(
            int numero_circulos_coletado
    )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Puck) elementos do círculos");
        builder.setMessage("Número de elementos do círculos da GRID");
        final EditText input = new EditText(this);
        input.setText(numero_circulos_coletado+ "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_puck_numero_elementos_circulos=m_Text;
                puck_coletar_p_plus_TL(Config.p_plus_TL);
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }});
        builder.show();
    }

    //Novembro06
    // p_plus_TL_coletado
    String global_puck_p_plus_TL="";

    public void puck_coletar_p_plus_TL(
            Double p_plus_TL
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Puck) p_plus_TL");
        builder.setMessage("p_plus_TL");
        final EditText input = new EditText(this);
        input.setText(p_plus_TL + "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_puck_p_plus_TL = m_Text;

                puck_coletar_puck_p_minus_TL(Config.p_minus_TL);
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

//Novembro07
    String global_puck_p_minus_TL="";

    public void puck_coletar_puck_p_minus_TL(
            Double p_minus_TL
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Puck) p_minus_TL");
        builder.setMessage("p_minus_TL");
        final EditText input = new EditText(this);
        input.setText(p_minus_TL + "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_puck_p_minus_TL = m_Text;

                puck_coletar_puck_sigma_1_D(Config.sigma_1_D);
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

//Novembro07
    String global_puck_sigma_1_D="";

    public void puck_coletar_puck_sigma_1_D(
            Double sigma_1_D
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Puck) sigma_1_D");
        builder.setMessage("sigma_1_D");
        final EditText input = new EditText(this);
        input.setText(sigma_1_D + "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_puck_sigma_1_D = m_Text;

                puck_coletar_R_TT_A(Config.R_TT_A);
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

    //Novembro07
    String global_puck_R_TT_A="";

    public void puck_coletar_R_TT_A(
            Double R_TT_A
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Puck) R_TT_A");
        builder.setMessage("R_TT_A");
        final EditText input = new EditText(this);
        input.setText(R_TT_A + "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_puck_R_TT_A = m_Text;

                puck_coletar_TAU12_C(Double.parseDouble(global_TAU12));
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

//Novembro07
    String global_puck_TAU12_C="";

    public void puck_coletar_TAU12_C(
            Double TAU12_C
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Puck) TAU12_C");
        builder.setMessage("TAU12_C (Pa)");
        final EditText input = new EditText(this);
        input.setText(TAU12_C + "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_puck_TAU12_C = m_Text;

                puck_coletar_NU12(Double.parseDouble(global_NU12));
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

//Novembro07
    String global_puck_NU12="";

    public void puck_coletar_NU12(
            Double NU12
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Puck) NU12 (fibra)");
        builder.setMessage("NU12 (fibra)");
        final EditText input = new EditText(this);
        input.setText(NU12 + "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_puck_NU12 = m_Text;

                puck_coletar_E1(Double.parseDouble(global_E1));
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

    //Novembro07
    String global_puck_E1="";

    public void puck_coletar_E1(
            Double E1
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Puck) E1 (fibra)");
        builder.setMessage("E1 (fibra)");
        final EditText input = new EditText(this);
        input.setText(E1 + "");
        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_puck_E1= m_Text;
                puck_coletar_global_puck_p_minus_TT(Config.p_minus_TT);
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

//Novembro07
    String global_puck_p_minus_TT="";

    public void puck_coletar_global_puck_p_minus_TT(
            Double p_minus_TT
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("(Puck) p_minus_TT");
        builder.setMessage("p_minus_TT");
        final EditText input = new EditText(this);

        input.setText(p_minus_TT + "");

        builder.setView(input);
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String m_Text = input.getText().toString();
                global_puck_p_minus_TT = m_Text;

                Intent resultIntent = new Intent();

resultIntent.putExtra("numero_circulos_coletado", global_puck_numero_circulos);
resultIntent.putExtra("m_sigF_coletado", global_puck_m_sigF);
resultIntent.putExtra("numero_circulos_elementos_coletado", global_puck_numero_elementos_circulos);
resultIntent.putExtra("p_plus_TL_coletado", global_puck_p_plus_TL);
resultIntent.putExtra("p_minus_TL_coletado", global_puck_p_minus_TL);
resultIntent.putExtra("p_minus_TT_coletado", global_puck_p_minus_TT);

resultIntent.putExtra("sigma_1_D_coletado", global_puck_sigma_1_D);
resultIntent.putExtra("puck_R_TT_A_coletado", global_puck_R_TT_A);
resultIntent.putExtra("TAU12_C_coletado", global_puck_TAU12_C);

resultIntent.putExtra("NU12_coletado", global_puck_NU12);
resultIntent.putExtra("E1_coletado", global_puck_E1);

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        //No caso de cancelamento Sai mesmo da tela
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

/////////////////////////////////////////////////////////////////////////////

}
