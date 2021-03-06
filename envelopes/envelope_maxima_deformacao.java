package br.com.ven2020.envelopes2018.envelopes;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.com.ven2020.envelopes2018.Config;
import br.com.ven2020.envelopes2018.R;
import br.com.ven2020.envelopes2018.database.DatabaseHelper;
import br.com.ven2020.envelopes2018.database.historico;

/**
 * Programa parecio com o Envelope de máxima tensão,mas que faz algumas transformações
 * para poder representar no plano de esforços sigma_x-sigma_y
 *
 * Ele gera uma linha no caso de rotação mutiplo interiro de Pi/4 quando gamma for nulo,
 * apenas uma consequencia matemática.
 */

public class envelope_maxima_deformacao {

    Double tolerancia = 1.0e-2;

    //String base_gnu="http://192.168.250.1/setembro/graficas_janeiro/diagramacao/gnu_registrar.php?identificador=";
    String base_gnu= Config.endereco_registrar_dados_gnu+"?identificador=";

    private Context myContext;
    private Activity myActivity;

    private String lamina_usada = "";
    private String criterio_usado = "";
    private String envelope_usado = "";

    private String endereco = ""; //endereço do DB local que contem as informações do DB

//janeiro10
// construtor da Classe

    public envelope_maxima_deformacao(
            Context myContext,
            String lamina_usada,
            String criterio_usado,
            String envelope_usado,
            String endereco,
            Activity myActivity) {
        this.myContext = myContext;
        this.lamina_usada = lamina_usada;
        this.criterio_usado = criterio_usado;
        this.envelope_usado = envelope_usado;
        this.endereco = endereco;
        this.myActivity = myActivity;
    }


    /*****
     Janeiro 10
     As propriedades de cada lâmina são obtidas desde o DB
     ********/

    Double f_EPSILON_T_1;
    Double f_EPSILON_T_2;
    Double f_EPSILON_C_1;
    Double f_EPSILON_C_2;
    Double f_GAMMA12;

    Double f_E1;
    Double f_E2;
    Double f_NU12;
    Double f_G12;

    Double anguloGLOBAL=0.0;
    Double gammaGLOBAL =0.0;

    public void obtem_valores_desde_DB() {
        //Procuramos as propriedades da lâmina, neste caso valores de deformação

        DatabaseHelper dbHelper = new DatabaseHelper(this.myContext, this.endereco);
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
            //Log.e("erro", e.getMessage());
        }

        List<String> list = dbHelper.obter_propriedades_laminas(this.lamina_usada);

//este sistema básico obtem as informações primeiramente como String, para //Logo transforma-as em Double

        String EPSILON_T_1=list.get(19);//"EPSILON_T_1:  "+cursor.getString(19));//EPSILON_T_1`	TEXT,
        String EPSILON_T_2=list.get(20);//"+cursor.getString(20));//EPSILON_T_2`	TEXT,
        String EPSILON_C_1=list.get(21);//"EPSILON_C_1:  "+cursor.getString(21));//EPSILON_C_1`	TEXT,
        String EPSILON_C_2=list.get(22);// "+cursor.getString(22));//EPSILON_C_2`	TEXT,
        String GAMMA12=list.get(23); //list.add("GAMMA12: "+cursor.getString(23));//GAMMA12`	TEXT,

        EPSILON_T_1=EPSILON_T_1.substring(EPSILON_T_1.indexOf(":")+1,EPSILON_T_1.length());
        EPSILON_T_2=EPSILON_T_2.substring(EPSILON_T_2.indexOf(":")+1,EPSILON_T_2.length());
        EPSILON_C_1=EPSILON_C_1.substring(EPSILON_C_1.indexOf(":")+1,EPSILON_C_1.length());
        EPSILON_C_2=EPSILON_C_2.substring(EPSILON_C_2.indexOf(":")+1,EPSILON_C_2.length());
        GAMMA12=GAMMA12.substring(GAMMA12.indexOf(":")+1,GAMMA12.length());

        f_EPSILON_T_1 = Double.parseDouble(EPSILON_T_1);
        f_EPSILON_T_2 = Double.parseDouble(EPSILON_T_2);
        f_EPSILON_C_1 = -Double.parseDouble(EPSILON_C_1);
        f_EPSILON_C_2 = -Double.parseDouble(EPSILON_C_2);
        f_GAMMA12 = Double.parseDouble(GAMMA12);

        //junho22

        String E1=list.get(3);//cursor.getString(3)) );//E1`	TEXT,
        String E2=list.get(4);
        String NU12=list.get(9);//cursor.getString(9)));//NU12`	TEXT,
        String G12=list.get(6);//cursor.getString(6)));//G12`	TEXT,


        E1=E1.substring(E1.indexOf(":")+1,E1.length());
        E2=E2.substring(E2.indexOf(":")+1,E2.length());
        NU12=NU12.substring(NU12.indexOf(":")+1,NU12.length());
        G12=G12.substring(G12.indexOf(":")+1,G12.length());

        f_E1= Double.parseDouble(E1);
        f_E2= Double.parseDouble(E2);
        f_NU12= Double.parseDouble(NU12);
        f_G12= Double.parseDouble(G12);
    }

    /*****
     Registrando os envelopes, se facilitam os futuros testes
     ********/
    public void registrar_envelope_usado() {
        String agora = new Time(System.currentTimeMillis()).toString();

        historico db_historico = new historico(myContext);

        db_historico.insert_envelope_usado(
                this.envelope_usado,
                agora
        );
    }

    /*****
     Fazendo o gráfico propriamente dito
     ********/

    List<Double> possiveis_pontos_x = new ArrayList<Double>();
    List<Double> possiveis_pontos_y = new ArrayList<Double>();
    List<Double> TRUE_pontos_x = new ArrayList<Double>();
    List<Double> TRUE_pontos_y = new ArrayList<Double>();

    List<Double> ANGULADOS_TRUE_pontos_x = new ArrayList<Double>();
    List<Double> ANGULADOS_TRUE_pontos_y = new ArrayList<Double>();

    List<Double> POLIGONO_TRUE_pontos_x = new ArrayList<Double>();
    List<Double> POLIGONO_TRUE_pontos_y = new ArrayList<Double>();


    public double[][] fazer_grafica(Double angulo, Double gamma_xy)
    {
        anguloGLOBAL=angulo;
        gammaGLOBAL =gamma_xy;

        List<Double> saida_x = new ArrayList<Double>();
        List<Double> saida_y = new ArrayList<Double>();

        //Log.d("Junho20","gamma_xy  "+gamma_xy);

        //funções auxiliares
        registrar_envelope_usado();
        obtem_valores_desde_DB();

//Antes de entrar no cálculo numérico é preciso verificar que este gamma
// não gere um IF maior de 1

        if(Math.abs(gamma_xy/f_GAMMA12)>0.0)
        {
            return null;
        }

/*
* Numerico puro
*
* */

        //obtemos os potenciais pontos do envelope, ou sejam usando as equações
        obter_pontos(angulo, gamma_xy);


        //Janeiro 11: Dependendo do ângulo, usamos um ou outro algoritmo para simplificar a representação gráfica

        if ((angulo % 90) == 0)
        {
            // antes de ordenar será preciso transformar para o espaço de DEFORMAÇÔES,
            // apenas assim será serguro ordenar os 4 pontos.
            transformando_deformacoes_esforcos(possiveis_pontos_x,possiveis_pontos_y);
            ordenar_quatro_pontos();
            //Sabado
            usar_GraphView();
            //pontos para serem entregues como saida:
            saida_x.clear();
            saida_y.clear();
            for(int i=0;i<possiveis_pontos_x.size();i++)
            {
                saida_x.add(possiveis_pontos_x.get(i));
                saida_y.add(possiveis_pontos_y.get(i));
            }
        }
        else
        {
            ArrayList<Boolean> Saida = criterio_puro_maxima_tensao(angulo, gamma_xy);
//Não podemos Testar o criterio com esforço, por isso a transformação deve ser feita após o teste de TRUE
// mas esta deve ser feita com grande cuidado


            for (int i = 0; i < possiveis_pontos_y.size(); i++) {
                if (Saida.get(i)) {
                    //usando apenas os pontos que sejam definidos como convenientes ou seja aqueles
                    // de IF unitário
                    TRUE_pontos_x.add(possiveis_pontos_x.get(i));
                    TRUE_pontos_y.add(possiveis_pontos_y.get(i));

                }
            }
//Com ângulo multiplo de Pi/2
// As matrizes eq 4.1 até eq 4.4 possuim determinantes nulas,
// aqui se vem 8 pontos, os quais estão repetidos
            if ((angulo % 45) == 0)
            {
                transformando_deformacoes_esforcos(TRUE_pontos_x,TRUE_pontos_y);


                Toast.makeText(
                        myContext,
                        "Envelope de vértices  "+TRUE_pontos_x.size()
                        ,Toast.LENGTH_LONG).show();
                //primeiramente reduzir
                caso_duplos_reduzir_8_para_4_com_angulo45();
                //Após reduzir representamos
                usar_GraphView_de_45();
                //usar_GraphView();
                //zeramos para evitar re-uso dos dados
                TRUE_pontos_x.clear();
                TRUE_pontos_y.clear();

            }

            //Log.d("Marco13minimo_x","TRUE_pontos_x   "+TRUE_pontos_x.size());

//Varios ELSES , de 5 esquinas
            if (TRUE_pontos_x.size()==5) {
                Toast.makeText(
                        myContext,
                        "Envelope de 5 vértices"
                        ,Toast.LENGTH_LONG).show();


                transformando_deformacoes_esforcos(TRUE_pontos_x,TRUE_pontos_y);

                fechar5pontos();
            }
//Varios ELSES , de 6 esquinas
            if (
                    (TRUE_pontos_x.size()==6) &&
                            ((angulo % 45) != 0))
            {
                Toast.makeText(
                        myContext,
                        "Envelope de 6 vértices"
                        ,Toast.LENGTH_LONG).show();


                transformando_deformacoes_esforcos(TRUE_pontos_x,TRUE_pontos_y);

                fechar6pontos(angulo);
            }
//Varios ELSES , de 4 esquinas sem ângulo multiplo
            if (TRUE_pontos_x.size()==4) {
                Toast.makeText(
                        myContext,
                        "Envelope de 4 vértices"
                        ,Toast.LENGTH_LONG).show();

                transformando_deformacoes_esforcos(TRUE_pontos_x,TRUE_pontos_y);

                //Log.d("Junho20","TRUE_pontos_x   "+TRUE_pontos_x);

                fecharPONTOS(angulo);
            }
            //dentro do else

            //pontos para serem entregues como saida:
            saida_x.clear();
            saida_y.clear();
            for(int i=0;i<TRUE_pontos_x.size();i++)
            {
                saida_x.add(TRUE_pontos_x.get(i));
                saida_y.add(TRUE_pontos_y.get(i));
            }

///fim do else
        }
        //componemos a saída
        double[][] saida = new double[saida_x.size()][3];

        for(int i=0;i<saida_x.size();i++)
        {
            saida[i][0]=saida_x.get(i);
            saida[i][1]=saida_y.get(i);
            saida[i][2]=possiveis_pontos_xy.get(i);
        }
        return saida;
//fim de fazer gráfica
}

    /////////////////////////////////////////////////////////////////////////////////////////
    /*
    * Outubro30
    * */
    public boolean funcao_inversa(Double a, Double b, Double c, Double d) {
        if ((a * d) != (b * c)) {
//refinamento antes de declarar
// o determinante existente Detalhe numerico que deve ser ajeitado conforme o compilador
            Double deter = ((a * d) - (b * c));
            ////Log.d("funcao_inversa","Determinante\n"+deter);
            return !(Math.abs(deter) < 0.0001);
        } else {
            return false;
        }
    }

    /*
    * Outubro31
    * */
    public List<Double> funcao_inversa_RETURN(Double a, Double b, Double c, Double d) {
        ArrayList<Double> array_list = new ArrayList<Double>();
        if ((a * d) != (b * c)) {
            //Log.d("Marco07", anguloGLOBAL+"  Inverse Matrix de \n" + a + "  " + b + " \n" + c + "  " + d + "---------");
            Double determinante = (a * d) - (b * c);
            Double inverse_a, inverse_b, inverse_c, inverse_d;
            inverse_a = d / determinante;
            inverse_b = -b / determinante;
            inverse_c = -c / determinante;
            inverse_d = a / determinante;

            array_list.add(inverse_a);
            array_list.add(inverse_b);
            array_list.add(inverse_c);
            array_list.add(inverse_d);
            return array_list;
        } else {
            return array_list;
        }
    }

    public void obter_pontos(Double angulo, Double tau_xy) {
//Por ser de máxima tensão o alfa e beta muda um pouco
        Double alfa = 1.0;
        Double beta = 2.0;
        Double c = Math.cos((Math.PI / 180) * angulo);
        Double s = Math.sin((Math.PI / 180) * angulo);

        Double c2 = c * c;
        Double s2 = s * s;

        Double a, b, C, d;

        //limpamos para evitar problemas

        possiveis_pontos_x.clear();
        possiveis_pontos_y.clear();

        //Estes valores se relacionam com a matriz [[a b][c d]]
        a = c2;
        b = s2;
        C = b;
        d = a;


        List<Double> matriz_invertida = new ArrayList<Double>();

//Para aquelas equações 4.1 e 4.5
//Lembremos que no caso de um ângulo de Pi/4
// a inversa não existe dado que as duas faixas estão paralelas o que signfica que elas
// nunca se cortam.
// Neste ângulo de Pi/4 os pontos de corte se definem pelas outras equações
        if (funcao_inversa(a, b, C, d)) {

            matriz_invertida = funcao_inversa_RETURN(a, b, C, d);

            eq4_1(alfa, s, c, tau_xy, matriz_invertida);
            eq4_2(alfa, s, c, tau_xy, matriz_invertida);
            eq4_3(alfa, s, c, tau_xy, matriz_invertida);
            eq4_4(alfa, s, c, tau_xy, matriz_invertida);
        }

        //Construimos a segunda Matriz de multiplicação.
        // No caso de ser uma matriz sem inverssa então ficamos quietinhos
        //Para eq. 4.5 até 4.8:
        a = c2;
        b = s2;
        C = -beta * s * c;
        d = beta * s * c;


        if (funcao_inversa(a, b, C, d)) {

            matriz_invertida = funcao_inversa_RETURN(a, b, C, d);

            eq4_5(alfa, s, c, tau_xy, matriz_invertida);

            eq4_6(alfa, s, c, tau_xy, matriz_invertida);

            eq4_7(alfa, s, c, tau_xy, matriz_invertida);

            //Para eq. 4.8:
            eq4_8(alfa, s, c, tau_xy, matriz_invertida);

        }

//Para eq. 4.9 até 4.12:
        a = s2;
        b = c2;
        C = -beta * s * c;
        d = beta * s * c;


        if (funcao_inversa(a, b, C, d)) {
            matriz_invertida = funcao_inversa_RETURN(a, b, C, d);
//Para eq. 4.9:
            eq4_9(alfa, s, c, tau_xy, matriz_invertida);

            //Para eq. 4.10:
            eq4_10(alfa, s, c, tau_xy, matriz_invertida);

            //Para eq. 4.11:
            eq4_11(alfa, s, c, tau_xy, matriz_invertida);

            //Para eq. 4.12:
            eq4_12(alfa, s, c, tau_xy, matriz_invertida);

        }

//Janeiro23 Apartir de aqui é preciso calcular o IF de cada possível ponto para
// deixar apenas aqueles que estejam na condução de UNIÃO //LogICA
//Juntamos o valor de TAU_xy.
// Por ser um critério simples, se codifica de novo por aqui

        //Log.d("Janeiro23", "criterio_puro_maxima_tensao \n" + criterio_puro_maxima_tensao(angulo, tau_xy));

//////fim
    }

    // Nesta versão do código, apenas para explicitar são criadas funções onde temos cada caso especifico:
    public void eq4_1(
            Double alfa,
            Double s,
            Double c,
            Double tau_xy,
            List<Double> matriz_invertida
    ) {
        //Para eq. 4.1:
        Double vetor_1 = f_EPSILON_C_1 - alfa * s * c * tau_xy;
        Double vetor_2 = f_EPSILON_C_2 + alfa * s * c * tau_xy;

        Double inverse_a = matriz_invertida.get(0);
        Double inverse_b = matriz_invertida.get(1);
        Double inverse_c = matriz_invertida.get(2);
        Double inverse_d = matriz_invertida.get(3);

        Double x = vetor_1 * (inverse_a) + vetor_2 * inverse_b;
        Double y = vetor_1 * (inverse_c) + vetor_2 * inverse_d;

        possiveis_pontos_x.add(x);
        possiveis_pontos_y.add(y);

    }

    //eq 4.2
    public void eq4_2(
            Double alfa,
            Double s,
            Double c,
            Double tau_xy,
            List<Double> matriz_invertida
    ) {
        //Para eq. 4.2:
        Double vetor_1 = f_EPSILON_T_1 - alfa * s * c * tau_xy;
        Double vetor_2 = f_EPSILON_T_2 + alfa * s * c * tau_xy;

        Double inverse_a = matriz_invertida.get(0);
        Double inverse_b = matriz_invertida.get(1);
        Double inverse_c = matriz_invertida.get(2);
        Double inverse_d = matriz_invertida.get(3);

        Double x = vetor_1 * (inverse_a) + vetor_2 * inverse_b;
        Double y = vetor_1 * (inverse_c) + vetor_2 * inverse_d;

        possiveis_pontos_x.add(x);
        possiveis_pontos_y.add(y);
    }

    //eq 4.3
    public void eq4_3(
            Double alfa,
            Double s,
            Double c,
            Double tau_xy,
            List<Double> matriz_invertida
    ) {

        //Para eq. 4.3:
        Double vetor_1 = f_EPSILON_T_1 - alfa * s * c * tau_xy;
        Double vetor_2 = f_EPSILON_C_2 + alfa * s * c * tau_xy;

        Double inverse_a = matriz_invertida.get(0);
        Double inverse_b = matriz_invertida.get(1);
        Double inverse_c = matriz_invertida.get(2);
        Double inverse_d = matriz_invertida.get(3);

        Double x = vetor_1 * (inverse_a) + vetor_2 * inverse_b;
        Double y = vetor_1 * (inverse_c) + vetor_2 * inverse_d;

        possiveis_pontos_x.add(x);
        possiveis_pontos_y.add(y);
    }

    //eq 4.4
    public void eq4_4(
            Double alfa,
            Double s,
            Double c,
            Double tau_xy,
            List<Double> matriz_invertida
    ) {

        Double vetor_1 = f_EPSILON_C_1 - alfa * s * c * tau_xy;
        Double vetor_2 = f_EPSILON_T_2 + alfa * s * c * tau_xy;

        Double inverse_a = matriz_invertida.get(0);
        Double inverse_b = matriz_invertida.get(1);
        Double inverse_c = matriz_invertida.get(2);
        Double inverse_d = matriz_invertida.get(3);

        Double x = vetor_1 * (inverse_a) + vetor_2 * inverse_b;
        Double y = vetor_1 * (inverse_c) + vetor_2 * inverse_d;

        possiveis_pontos_x.add(x);
        possiveis_pontos_y.add(y);
    }

    //Entre eq. 4.5 e 4.12 temos outra FORMA
    //eq 4.5
    public void eq4_5(
            Double alfa,
            Double s,
            Double c,
            Double tau_xy,
            List<Double> matriz_invertida
    ) {
        Double c2 = c * c;
        Double s2 = s * s;
        //Para eq. 4.5:
        Double vetor_1 = f_EPSILON_C_1 - alfa * s * c * tau_xy;
        Double vetor_2 = -f_GAMMA12 - (c2 - s2) * tau_xy;

        Double inverse_a = matriz_invertida.get(0);
        Double inverse_b = matriz_invertida.get(1);
        Double inverse_c = matriz_invertida.get(2);
        Double inverse_d = matriz_invertida.get(3);

        Double x = vetor_1 * (inverse_a) + vetor_2 * inverse_b;
        Double y = vetor_1 * (inverse_c) + vetor_2 * inverse_d;

        possiveis_pontos_x.add(x);
        possiveis_pontos_y.add(y);

    }

    public void eq4_6(
            Double alfa,
            Double s,
            Double c,
            Double tau_xy,
            List<Double> matriz_invertida
    ) {
        Double c2 = c * c;
        Double s2 = s * s;

        //Para eq. 4.6:
        Double vetor_1 = f_EPSILON_T_1 - alfa * s * c * tau_xy;
        Double vetor_2 = f_GAMMA12 - (c2 - s2) * tau_xy;

        Double inverse_a = matriz_invertida.get(0);
        Double inverse_b = matriz_invertida.get(1);
        Double inverse_c = matriz_invertida.get(2);
        Double inverse_d = matriz_invertida.get(3);

        Double x = vetor_1 * (inverse_a) + vetor_2 * inverse_b;
        Double y = vetor_1 * (inverse_c) + vetor_2 * inverse_d;

        possiveis_pontos_x.add(x);
        possiveis_pontos_y.add(y);
    }

    //eq. 4.7
    public void eq4_7(
            Double alfa,
            Double s,
            Double c,
            Double tau_xy,
            List<Double> matriz_invertida
    ) {
        Double c2 = c * c;
        Double s2 = s * s;
        //Para eq. 4.7:
        Double vetor_1 = f_EPSILON_T_1 - alfa * s * c * tau_xy;
        Double vetor_2 = -f_GAMMA12 + (c2 - s2) * tau_xy;

        Double inverse_a = matriz_invertida.get(0);
        Double inverse_b = matriz_invertida.get(1);
        Double inverse_c = matriz_invertida.get(2);
        Double inverse_d = matriz_invertida.get(3);

        Double x = vetor_1 * (inverse_a) + vetor_2 * inverse_b;
        Double y = vetor_1 * (inverse_c) + vetor_2 * inverse_d;

        possiveis_pontos_x.add(x);
        possiveis_pontos_y.add(y);

    }

    //eq. 4.8
    public void eq4_8(
            Double alfa,
            Double s,
            Double c,
            Double tau_xy,
            List<Double> matriz_invertida
    ) {
        Double c2 = c * c;
        Double s2 = s * s;
        //Para eq. 4.8
        Double vetor_1 = f_EPSILON_C_1 - alfa * s * c * tau_xy;
        Double vetor_2 = f_GAMMA12 + (c2 - s2) * tau_xy;

        Double inverse_a = matriz_invertida.get(0);
        Double inverse_b = matriz_invertida.get(1);
        Double inverse_c = matriz_invertida.get(2);
        Double inverse_d = matriz_invertida.get(3);

        Double x = vetor_1 * (inverse_a) + vetor_2 * inverse_b;
        Double y = vetor_1 * (inverse_c) + vetor_2 * inverse_d;

        possiveis_pontos_x.add(x);
        possiveis_pontos_y.add(y);
    }

    //eq. 4.9
    public void eq4_9(
            Double alfa,
            Double s,
            Double c,
            Double tau_xy,
            List<Double> matriz_invertida
    ) {
        Double c2 = c * c;
        Double s2 = s * s;
        //Para eq. 4.9
        Double vetor_1 = f_EPSILON_C_2 - alfa * s * c * tau_xy;
        Double vetor_2 = -f_GAMMA12 - (c2 - s2) * tau_xy;

        Double inverse_a = matriz_invertida.get(0);
        Double inverse_b = matriz_invertida.get(1);
        Double inverse_c = matriz_invertida.get(2);
        Double inverse_d = matriz_invertida.get(3);

        Double x = vetor_1 * (inverse_a) + vetor_2 * inverse_b;
        Double y = vetor_1 * (inverse_c) + vetor_2 * inverse_d;

        possiveis_pontos_x.add(x);
        possiveis_pontos_y.add(y);
    }

    //eq. 4.10
    public void eq4_10(
            Double alfa,
            Double s,
            Double c,
            Double tau_xy,
            List<Double> matriz_invertida
    ) {
        Double c2 = c * c;
        Double s2 = s * s;
        //Para eq. 4.10
        Double vetor_1 = f_EPSILON_T_2 - alfa * s * c * tau_xy;
        Double vetor_2 = f_GAMMA12 - (c2 - s2) * tau_xy;

        Double inverse_a = matriz_invertida.get(0);
        Double inverse_b = matriz_invertida.get(1);
        Double inverse_c = matriz_invertida.get(2);
        Double inverse_d = matriz_invertida.get(3);

        Double x = vetor_1 * (inverse_a) + vetor_2 * inverse_b;
        Double y = vetor_1 * (inverse_c) + vetor_2 * inverse_d;

        possiveis_pontos_x.add(x);
        possiveis_pontos_y.add(y);
    }

    //eq. 4.11
    public void eq4_11(
            Double alfa,
            Double s,
            Double c,
            Double tau_xy,
            List<Double> matriz_invertida
    ) {
        Double c2 = c * c;
        Double s2 = s * s;
        //Para eq. 4.11
        Double vetor_1 = f_EPSILON_T_2 - alfa * s * c * tau_xy;
        Double vetor_2 = -f_GAMMA12 - (c2 - s2) * tau_xy;

        Double inverse_a = matriz_invertida.get(0);
        Double inverse_b = matriz_invertida.get(1);
        Double inverse_c = matriz_invertida.get(2);
        Double inverse_d = matriz_invertida.get(3);

        Double x = vetor_1 * (inverse_a) + vetor_2 * inverse_b;
        Double y = vetor_1 * (inverse_c) + vetor_2 * inverse_d;

        possiveis_pontos_x.add(x);
        possiveis_pontos_y.add(y);
    }

    //eq. 4.12
    public void eq4_12(
            Double alfa,
            Double s,
            Double c,
            Double tau_xy,
            List<Double> matriz_invertida
    ) {
        Double c2 = c * c;
        Double s2 = s * s;
        //Para eq. 4.12
        Double vetor_1 = f_EPSILON_C_2 - alfa * s * c * tau_xy;
        Double vetor_2 = f_GAMMA12 - (c2 - s2) * tau_xy;

        Double inverse_a = matriz_invertida.get(0);
        Double inverse_b = matriz_invertida.get(1);
        Double inverse_c = matriz_invertida.get(2);
        Double inverse_d = matriz_invertida.get(3);

        Double x = vetor_1 * (inverse_a) + vetor_2 * inverse_b;
        Double y = vetor_1 * (inverse_c) + vetor_2 * inverse_d;

        possiveis_pontos_x.add(x);
        possiveis_pontos_y.add(y);
    }

/*
* Sábado 03 de Março 2018
* Este sistema super SIMPLES de ordenamento não funciona para o envelope de máxima DEFORMAÇÂO
* Por tanto é usado o sistema de ângulos
*
*
* */
    public void ordenar_quatro_pontos()
    {
        Double separacao_tecnica=Math.abs(possiveis_pontos_x.get(1))
                *1.0e-3; //pegamos o primeiro elemento
        //primeiramente evitamos que tenha um valor de X com multiplicidade
        for (int i = 0; i < possiveis_pontos_x.size(); i++) {
            for (int j = i + 1; j < possiveis_pontos_x.size(); j++) {
                Double diff = possiveis_pontos_x.get(i) - possiveis_pontos_x.get(j);

                if (diff == 0.0) {
                    possiveis_pontos_x.set(j, possiveis_pontos_x.get(i) + separacao_tecnica);
                }
            }
        }

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
//for
            }

        }

        //É preciso apenas ordenar por altura dado que
//Numeração em forma horaria começa pelo extremo esquerdo
        // 1) O extremo esquerdo será o ponto 1
        // 2) O extremo direito será o ponto 3
        // 3) A dúvida existe sobre os pontos 2 3  , mas entre estes escolhemos pelo coeficiente ângular
        // como regra.
        // Aquela que permita construir uma reta com o maior coeficiente ângular é o ponto.

        //TOMAR DE possiveis_pontos_y os pontos possiveis pontos 2 e 3

        Double confuso_2_x=possiveis_pontos_x.get(1); //Lembrar que os dois pontos estão à direito do ponto 1
        Double confuso_2_y=possiveis_pontos_y.get(1);

        Double confuso_3_x=possiveis_pontos_x.get(2);
        Double confuso_3_y=possiveis_pontos_y.get(2);

//Calculamos os coeficientes angulares:
        Double m_do_confuso_2=(confuso_2_y-possiveis_pontos_y.get(0))/(confuso_2_x-possiveis_pontos_x.get(0));
        Double m_do_confuso_3=(confuso_3_y-possiveis_pontos_y.get(0))/(confuso_3_x-possiveis_pontos_x.get(0));

//Aquele ponto confuso de mairo coeficiente angular será o ponto 2

        Double temp_x;
        Double temp_y;

        if(m_do_confuso_2>m_do_confuso_3)
        {
            temp_x=possiveis_pontos_x.get(3);
            temp_y=possiveis_pontos_y.get(3);

//O de maior coeficiente angular será o ponto 2
            possiveis_pontos_x.set(1,confuso_2_x);
            possiveis_pontos_y.set(1,confuso_2_y);
//E o de menor será o ponto 4
            possiveis_pontos_x.set(3,confuso_3_x);
            possiveis_pontos_y.set(3,confuso_3_y);
//O antigo ponto 4 será o ponto 3
            possiveis_pontos_x.set(2,temp_x);
            possiveis_pontos_y.set(2,temp_y);
        }
        else
        {
            temp_x=possiveis_pontos_x.get(3);
            temp_y=possiveis_pontos_y.get(3);

//O de maior coeficiente angular será o ponto 2
            possiveis_pontos_x.set(1,confuso_3_x);
            possiveis_pontos_y.set(1,confuso_3_y);
//E o de menor será o ponto 4
            possiveis_pontos_x.set(3,confuso_2_x);
            possiveis_pontos_y.set(3,confuso_2_y);
//O antigo ponto 4 será o ponto 3
            possiveis_pontos_x.set(2,temp_x);
            possiveis_pontos_y.set(2,temp_y);
        }
//Esclarecido
//////////////////
    }

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
        Double maximo_x = possiveis_pontos_x.get(possiveis_pontos_x.size() - 1-1);

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
                new DataPoint(possiveis_pontos_x.get(2), possiveis_pontos_y.get(2))

        });

        LineGraphSeries<DataPoint> series_1 = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(possiveis_pontos_x.get(0), possiveis_pontos_y.get(0)),
                new DataPoint(possiveis_pontos_x.get(3), possiveis_pontos_y.get(3)),
                new DataPoint(possiveis_pontos_x.get(2), possiveis_pontos_y.get(2))
        });

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.addSeries(series_0);
        graph.addSeries(series_1);

//Fevereiro11
//Fevereiro25
// criamos uma função para poder facilitar a exteriorização de diversos formatos GNU,

        enviando_data_gnu(possiveis_pontos_x,possiveis_pontos_y);
///////////////
    }

    // ////
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

//janeiro23, Se definem como globais com o intuito de corrigir erro

    ArrayList<Double> Array_normalizado_if_1 = new ArrayList<Double>();
    ArrayList<Double> Array_normalizado_if_2 = new ArrayList<Double>();
    ArrayList<Double> Array_normalizado_if_12 = new ArrayList<Double>();

    public ArrayList<Boolean> criterio_puro_maxima_tensao(
            Double angulo,
            Double tau_xy) {
        /*
        * Numerico,
        *
        *
        * */
        ArrayList<Boolean> Saida = new ArrayList<Boolean>();


        //Log.d("Janeiro23", "valores de IF \n" + possiveis_pontos_x);
        //Log.d("Janeiro23", "valores de IF \n" + possiveis_pontos_y);

        double theta_radianos = (Math.PI / 180) * angulo;

        Double c = Math.cos(theta_radianos);
        Double s = Math.sin(theta_radianos);
        Double c2 = c * c;
        Double s2 = s * s;

        //vemos se o IF diretamente

        double sigma_x;
        double sigma_y;

        Double sigma_1;
        Double sigma_2;
        Double tau_12;
        Double normalizado_if_1;
        Double normalizado_if_2;
        Double normalizado_if_12;


        //Cuidado com as trasnformações que se fazem dado que no
        // caso das deformações é preciso levar em conta os alphas
        //
        Double alpha=1.0;
        Double beta=2.0;


        for (int i = 0; i < possiveis_pontos_x.size(); i++) {

            sigma_x = possiveis_pontos_x.get(i);
            sigma_y = possiveis_pontos_y.get(i);

            //calculando em locais
            sigma_1 = c2 * sigma_x + s2 * sigma_y + alpha * c * s * tau_xy;
            sigma_2 = s2 * sigma_x + c2 * sigma_y - alpha * c * s * tau_xy;
            tau_12 = -beta*s * c * sigma_x + beta*s * c * sigma_y + (c2 - s2) * tau_xy;


            //normalizando
            //Para a direção local 1
            if(sigma_1>0) {
                normalizado_if_1 = Math.abs(sigma_1 / f_EPSILON_T_1); // Tração
            }
            else {
                normalizado_if_1 = Math.abs(sigma_1 / f_EPSILON_C_1); // Compressão
            }
            //Para a direção local 2
            if(sigma_2>0) {
                normalizado_if_2 = Math.abs(sigma_2 / f_EPSILON_T_2); // Tração
            }
            else {
                normalizado_if_2 = Math.abs(sigma_2 / f_EPSILON_C_2); // Compressão
            }
            //Para o esforço cisalhante
            normalizado_if_12 = Math.abs(tau_12 / f_GAMMA12);
            //Se qualquer um dos anteriores supera o valor de 1, significa que o IF foi superado.
            //janeiro24 mas estando ciente do erro numérico pode ser mími é preciso definir uma tolerância
            // de 0.01


            Array_normalizado_if_1.add(normalizado_if_1);
            Array_normalizado_if_2.add(normalizado_if_2);
            Array_normalizado_if_12.add(normalizado_if_12);

            //Usamos a tolerancia definida de forma global
            // Lembremos que este critério é a união lógica de 3 subcritérios

            if (
                            (normalizado_if_1 <= 1+tolerancia) &&
                            (normalizado_if_2 <= 1+tolerancia) &&
                            (normalizado_if_12 <= 1+tolerancia)

                    ) {
                Saida.add(true);
            } else {
                Saida.add(false);
            }
        }


        return Saida;
    }
//Janeiro26
//Janeiro30

    ArrayList<List<Double>> SUB_arco_XY = new ArrayList<List<Double>>();
    ArrayList<List<Double>> COMPLEMENTO_arco_XY = new ArrayList<List<Double>>();

/*
No caso de um envelope de 6 vértices será precisa usar o algoritmo de ângulos.

Calcular cada ângulo significa usar um maior poder computacional.

Se cálculam todos os ângulos, exceto o já conetado!!!

1) Construir a pila de vértices não conetados. Pode-se começar por qualquer um
2) Procurar o primeiro par e indicar na pila de conetados.
3)


* */
    public void fechar6pontos(Double angulo_entrada) {
        //Fazemos um estudo MASSIVO dos 6 pontos
        //Estudo inicial partendo do primeiro ponto

        ArrayList<Integer> numeros=new ArrayList<Integer>();
//Por segurança limpamos a lista
        numeros.clear();

        numeros.add(1);
        numeros.add(2);
        numeros.add(3);
        numeros.add(4);
        numeros.add(5);

        Double ponto_inicial_x = TRUE_pontos_x.get(0);
        Double ponto_inicial_y = TRUE_pontos_y.get(0);

        //Fazemos a varredura dos pares para obter o ângulo.
//Solução rápida: Invocar a função várias vezes
        ArrayList<Double> angulos_possiveis=new ArrayList<Double>();
        Double angulo_temporal=0.0;
        Integer i_temp=0,j_temp=0;

        for (int i = 1; i < TRUE_pontos_x.size(); i++)
        {
            for(int j=i+1; j<TRUE_pontos_x.size();j++)
            {
                Double angulo=angulo(ponto_inicial_x,ponto_inicial_y,i,j);
                angulos_possiveis.add(angulo);
                if(angulo>=angulo_temporal)
                {
                    angulo_temporal=angulo;
                    i_temp=i;
                    j_temp=j;
                }
                //Log.d("Marco05", "\n "+angulo+" Ângulo entre os pontos " + i + "  " + j+" e o ponto 1 (inicial)");
            }
        }

        ANGULADOS_TRUE_pontos_x.add(TRUE_pontos_x.get(j_temp));
        ANGULADOS_TRUE_pontos_y.add(TRUE_pontos_y.get(j_temp));

        ANGULADOS_TRUE_pontos_x.add(TRUE_pontos_x.get(0));
        ANGULADOS_TRUE_pontos_y.add(TRUE_pontos_y.get(0));

        ANGULADOS_TRUE_pontos_x.add(TRUE_pontos_x.get(i_temp));
        ANGULADOS_TRUE_pontos_y.add(TRUE_pontos_y.get(i_temp));

        numeros.remove(numeros.indexOf(i_temp));
        numeros.remove(numeros.indexOf(j_temp));

//Ele pega o maior ângulo, dado que envolve uma figura....

        // Sorting
        Collections.sort(angulos_possiveis);
//Eliminamos o primeiro elemento.
//        Log.d("Fevereiro06","Ordenado\n"+angulos_possiveis.get(9)+"   "+angulo_temporal);
        //Log.d("Marco05","2 pontos complementares\n  i  "+i_temp+" j "+j_temp);

//Extremo para ser estudado:

        Integer ANTIGO_i_temp=i_temp;
        Integer ANTIGO_j_temp=j_temp;

        //Estudo inicial partendo do primeiro ponto
        ponto_inicial_x = TRUE_pontos_x.get(ANTIGO_i_temp);
        ponto_inicial_y = TRUE_pontos_y.get(ANTIGO_i_temp);

        Double ponto_inicial_x_CONTRA = TRUE_pontos_x.get(ANTIGO_j_temp);
        Double ponto_inicial_y_CONTRA = TRUE_pontos_y.get(ANTIGO_j_temp);

        //Fazemos a varredura dos pares para obter o ângulo.
//Solução rápida: Invocar a função várias vezes
        angulos_possiveis=new ArrayList<Double>();
        angulo_temporal=0.0;
//Tendo 3 ptos. Será preciso apenas detetar os outros
        //Log.d("Marco05","X Temos os primeiros 3\n  i  "+ANGULADOS_TRUE_pontos_x);
        //Log.d("Marco05","Y Temos os primeiros 3\n  i  "+ANGULADOS_TRUE_pontos_y);
//
        angulo_temporal=0.0;
        angulos_possiveis.clear();

        for(int j=1; j<TRUE_pontos_x.size();j++) //acima de 0 (Antigo Ponto Inicial)
        {
            if(j!=ANTIGO_i_temp)
            {

                Double angulo=angulo(
                        ponto_inicial_x,
                        ponto_inicial_y,
                        0,
                        j);
                angulos_possiveis.add(angulo);
                if(angulo>=angulo_temporal)
                {
                    angulo_temporal=angulo;
                    j_temp=j;
                }
            }
        }

        // Sorting
        Collections.sort(angulos_possiveis);

        numeros.remove(numeros.indexOf(j_temp));

        ANGULADOS_TRUE_pontos_x.add(TRUE_pontos_x.get(j_temp));
        ANGULADOS_TRUE_pontos_y.add(TRUE_pontos_y.get(j_temp));
//Domingo
// Agora do outro lado

        angulos_possiveis=new ArrayList<Double>();
        angulo_temporal=0.0;

        angulo_temporal=0.0;
        angulos_possiveis.clear();

        for(int j=1; j<TRUE_pontos_x.size();j++) //acima de 0 (Antigo Ponto Inicial)
        {
            if(j!=ANTIGO_j_temp)
            {

                Double angulo=angulo(
                        ponto_inicial_x_CONTRA,
                        ponto_inicial_y_CONTRA,
                        0,
                        j);
                angulos_possiveis.add(angulo);
                if(angulo>=angulo_temporal)
                {
                    angulo_temporal=angulo;
                    i_temp=0;
                    j_temp=j;
                }
            }
        }

        // Sorting
        Collections.sort(angulos_possiveis);

        //colocamos o não usado como inicial, é como um restante

        numeros.remove(numeros.indexOf(j_temp));

        POLIGONO_TRUE_pontos_x.add(TRUE_pontos_x.get(numeros.get(0)));
        POLIGONO_TRUE_pontos_y.add(TRUE_pontos_y.get(numeros.get(0)));

        POLIGONO_TRUE_pontos_x.add(TRUE_pontos_x.get(j_temp));
        POLIGONO_TRUE_pontos_y.add(TRUE_pontos_y.get(j_temp));

        for(Integer i=0;i<ANGULADOS_TRUE_pontos_x.size();i++)
        {
            POLIGONO_TRUE_pontos_x.add(ANGULADOS_TRUE_pontos_x.get(i));
            POLIGONO_TRUE_pontos_y.add(ANGULADOS_TRUE_pontos_y.get(i));
        }
//Fevereiro08
        construir_COM_POLIGONO_TRUE_pontos(angulo_entrada);
//
////////////////////
    }
    //janeiro06
    public Double angulo(
            Double ponto_inicial_x,
            Double ponto_inicial_y,
            int i,
            int j
    )
    {
        //ponto inicial
        //Será preciso calcular as normas
        // e até um produto inter
        //Vetorizando
        Double vetor_1_x=TRUE_pontos_x.get(i)-ponto_inicial_x;
        Double vetor_1_y=TRUE_pontos_y.get(i)-ponto_inicial_y;
        //ponto final
        Double vetor_2_x=TRUE_pontos_x.get(j)-ponto_inicial_x;
        Double vetor_2_y=TRUE_pontos_y.get(j)-ponto_inicial_y;

        //normas

        //vetor_1

        Double norma_1=Math.sqrt(vetor_1_x*vetor_1_x+vetor_1_y*vetor_1_y);

        Double norma_2=Math.sqrt(vetor_2_x*vetor_2_x+vetor_2_y*vetor_2_y);

        //Produto interno

        Double interno=vetor_1_x*vetor_2_x+vetor_1_y*vetor_2_y;

        Double argumento=interno/(norma_1*norma_2);

        Double angulo=Math.acos(argumento);

        return (angulo*180)/Math.PI;
    }
//Fevereiroi08
// Trabalhando apenas com POLIGONO_TRUE

    /*De forma similar como se faz com os envelopes restantes*/

public void construir_COM_POLIGONO_TRUE_pontos(Double angulo)
{
//O primeiro passo dever ser procurar extremos MAX, MIM

    Double minimo_x=0.0;
    for(int i=0;i<POLIGONO_TRUE_pontos_x.size();i++)
    {
        if(POLIGONO_TRUE_pontos_x.get(i)<minimo_x)
        {
            minimo_x=POLIGONO_TRUE_pontos_x.get(i);
        }
    }
    Double maximo_x=0.0;
    for(int i=0;i<POLIGONO_TRUE_pontos_x.size();i++)
    {
        if(POLIGONO_TRUE_pontos_x.get(i)>=maximo_x)
        {
            maximo_x=POLIGONO_TRUE_pontos_x.get(i);
        }
    }
    Double minimo_y = 0.0;

    for(int i=0;i<POLIGONO_TRUE_pontos_y.size();i++)
    {
        //Log.d("uniesquina_"+angulo,POLIGONO_TRUE_pontos_x.get(i)+"  <=  "+minimo_y);
        if(POLIGONO_TRUE_pontos_y.get(i)<=minimo_y)
        {
            minimo_y=POLIGONO_TRUE_pontos_y.get(i);
            //Log.d("uniesquina_"+angulo, "minimo_y\n"+minimo_y);
        }
    }

    //Log.d("uniesquina_"+angulo, "POLIGONO_TRUE_pontos_y\n"+POLIGONO_TRUE_pontos_y);

    Double maximo_y = 0.0;
    for(int i=0;i<POLIGONO_TRUE_pontos_y.size();i++)
    {
        if(POLIGONO_TRUE_pontos_y.get(i)>=maximo_y)
        {
            maximo_y=POLIGONO_TRUE_pontos_y.get(i);
        }
    }



    //Aqui é onde ordenamos e contruimos o envelope, mais primeiramente é preciso ordenar
    //1)Obter o ponto mais na isquerda:
    Double mais_esquerda=0.0;
    Double mais_direita=0.0;

    Integer Ymais_esquerda=0;
    Integer Ymais_direita=0;

    //Apenas escolher o
    // mais_esquerda

    for(int i=0;i<POLIGONO_TRUE_pontos_x.size();i++)
    {
        if(POLIGONO_TRUE_pontos_x.get(i)<mais_esquerda)
        {
            mais_esquerda=POLIGONO_TRUE_pontos_x.get(i);
            Ymais_esquerda=i;
        }
    }

    //Log.d("Fevereiro08_"," mais_esquerda  "+mais_esquerda+" , "+POLIGONO_TRUE_pontos_y.get(Ymais_esquerda));

    //Apenas escolher o
    // mais_direita

    for(int i=0;i<POLIGONO_TRUE_pontos_x.size();i++)
    {
        if(POLIGONO_TRUE_pontos_x.get(i)>mais_direita)
        {
            mais_direita=POLIGONO_TRUE_pontos_x.get(i);
            Ymais_direita=i;
        }
    }

    //Log.d("Fevereiro08_"," mais_direita  "+mais_direita+" , "+POLIGONO_TRUE_pontos_y.get(Ymais_direita));
//arco 1
    List<Double> arco_1_x = new ArrayList<Double>();
    List<Double> arco_1_y = new ArrayList<Double>();
//arco 2
    List<Double> arco_2_x = new ArrayList<Double>();
    List<Double> arco_2_y = new ArrayList<Double>();


    //aproveitando o fato que ele vai sequenciado é preciso

    int contadorNULL=0;

    if(Ymais_esquerda<Ymais_direita)
    {
    //Cuidado com os null
    // que fossem colocados
        for(int j=Ymais_esquerda;j<=Ymais_direita;j++)
        {
            arco_2_x.add(POLIGONO_TRUE_pontos_x.get(j));
            arco_2_y.add(POLIGONO_TRUE_pontos_y.get(j));

            if(
                (j>Ymais_esquerda)
                        && (j<Ymais_direita)
            )
            {
                POLIGONO_TRUE_pontos_x.set(j,null);
                POLIGONO_TRUE_pontos_y.set(j,null);
                contadorNULL++;
            }
        }
        //Log.d("Fevereiro27","contadorNULL\n"+contadorNULL);
    }
    else
    {
            for(int j=Ymais_esquerda;j<POLIGONO_TRUE_pontos_y.size();j++)
            {
                arco_2_x.add(POLIGONO_TRUE_pontos_x.get(j));
                arco_2_y.add(POLIGONO_TRUE_pontos_y.get(j));

                if(j>Ymais_esquerda)
                {
                    POLIGONO_TRUE_pontos_x.set(j,null);
                    POLIGONO_TRUE_pontos_y.set(j,null);
                    contadorNULL++;
                }

            }
            for(int j=0;j<=Ymais_direita;j++)
            {
                arco_2_x.add(POLIGONO_TRUE_pontos_x.get(j));
                arco_2_y.add(POLIGONO_TRUE_pontos_y.get(j));
                if(j<Ymais_direita) {
                    POLIGONO_TRUE_pontos_x.set(j,null);
                    POLIGONO_TRUE_pontos_y.set(j,null);
                    contadorNULL++;
                }

            }
            //Log.d("Fevereiro27","ELSE contadorNULL\n"+contadorNULL);
    }




    POLIGONO_TRUE_pontos_x.removeAll(Collections.singleton(null));
    POLIGONO_TRUE_pontos_y.removeAll(Collections.singleton(null));
//-------
        for(Integer i=(POLIGONO_TRUE_pontos_x.size()-1);i>=0; i--)
        {
            arco_1_x.add(POLIGONO_TRUE_pontos_x.get(i));
            arco_1_y.add(POLIGONO_TRUE_pontos_y.get(i));
        }

        //agora as funcoes com
    // Graphview
    // Graphview
    // Graphview
    // Graphview
    // Graphview
    // Graphview
    // Graphview

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

        graph.removeAllSeries();
        graph.setVisibility(View.VISIBLE);

        //colocamos os valores extremos:


        graph.getViewport().setMinX(minimo_x * 1.5);
        graph.getViewport().setMaxX(maximo_x * 1.5);

        graph.getViewport().setMinY(minimo_y * 1.5);
        graph.getViewport().setMaxY(maximo_y * 1.5);

    //Log.d("ANTES","POLIGONO_TRUE_pontos_y\n"+POLIGONO_TRUE_pontos_y);

        LineGraphSeries<DataPoint> series_0 =new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> series_1 =new LineGraphSeries<DataPoint>();

        boolean ORDEM_ASCII=true;

        Double minimo=arco_1_x.get(0);

        for (Integer i = 0; i < arco_1_x.size(); i++)
        {
            if(arco_1_x.get(i)<minimo)
            {
                ORDEM_ASCII=false;
            }
            minimo=arco_1_x.get(i);
        }
        //Se for preciso ordenar, pazemos o ordenamento dos pares, ou seja uma troca dupla


        if(!ORDEM_ASCII)
        {
            Double menor_arco_1_x;
            Double menor_arco_1_y;
            Double TEMP_arco_1_x;
            Double TEMP_arco_1_y;
            for (Integer i = 0; i < arco_1_x.size(); i++)
            {
                menor_arco_1_x=arco_1_x.get(i);
                menor_arco_1_y=arco_1_y.get(i);
                for(Integer j=i+1;j<arco_1_x.size();j++)
                {

                    if(arco_1_x.get(j)<menor_arco_1_x)
                    {
                        TEMP_arco_1_x=arco_1_x.get(j);
                        arco_1_x.set(j,menor_arco_1_x);
                        arco_1_x.set(i,TEMP_arco_1_x);
//O mesmo para Y para assim manter o sistema ordenado
                        TEMP_arco_1_y=arco_1_y.get(j);
                        arco_1_y.set(j,menor_arco_1_y);
                        arco_1_y.set(i,TEMP_arco_1_y);

                        break;
                    }
                }
            }
        }



        for (Integer i = 0; i < arco_1_x.size(); i++)
        {
            series_0.appendData(new DataPoint(arco_1_x.get(i), arco_1_y.get(i)), true, 100);

        }
        for (Integer i = 0; i < arco_2_x.size(); i++) {
            series_1.appendData(new DataPoint(arco_2_x.get(i), arco_2_y.get(i)), true, 100);

        }

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.addSeries(series_0);
        graph.addSeries(series_1);


    //Enviando as informações ao servidor
// Arco superior //Arco Inferior
    enviando_data_gnu_completo(arco_1_x,arco_1_y,arco_2_x,arco_2_y);




//////////
    }


//Domingo Fevereiro25
    public void enviando_data_gnu
    (
            List<Double> possiveis_pontos_x,
            List<Double> possiveis_pontos_y
    )
    {
        Date d = new Date();
        String dayOfTheWeek = (String) DateFormat.format("EEEE", d);
        String day = (String) DateFormat.format("dd", d); // 20
        String monthString = (String) DateFormat.format("MMM", d); // Jun
        String year = (String) DateFormat.format("yyyy", d); // 2013
        //Expressado em milisegundos
        String agora = System.currentTimeMillis()+"";
        //Construimos
        String identificador = anguloGLOBAL+"_"+dayOfTheWeek + "_" + day + "_" + monthString + "_" + year + "_"+agora;
        //Passamos o identificador ao DB
        String url=base_gnu+identificador;
        String arquivo_gnu="";

        for(int i=0;i<possiveis_pontos_x.size();i++)
        {
            arquivo_gnu=arquivo_gnu+possiveis_pontos_x.get(i)+" "+possiveis_pontos_y.get(i)+"%5Cn";
        }

        //Passamos os dados
        arquivo_gnu=arquivo_gnu.replace(" ","%20");
        url=url+"&dados="+arquivo_gnu;

    }
//Domingo Fevereiro25
    public void enviando_data_gnu_completo
    (
            List<Double> arco_1_possiveis_pontos_x,
            List<Double> arco_1_possiveis_pontos_y,
            List<Double> arco_2_possiveis_pontos_x,
            List<Double> arco_2_possiveis_pontos_y
    )
    {
        Date d = new Date();
        String dayOfTheWeek = (String) DateFormat.format("EEEE", d);
        String day = (String) DateFormat.format("dd", d); // 20
        String monthString = (String) DateFormat.format("MMM", d); // Jun
        String year = (String) DateFormat.format("yyyy", d); // 2013
        //Expressado em milisegundos
        String agora = System.currentTimeMillis()+"";
        //Construimos
        String identificador = anguloGLOBAL+"_"+dayOfTheWeek + "_" + day + "_" + monthString + "_" + year + "_"+agora;
        //Passamos o identificador ao DB
        String url=base_gnu+identificador;
        String arquivo_gnu="";
        //arquivo_gnu=arquivo_gnu+"#    Primeiro Arco"+"%5Cn";

        for(int i=0;i<arco_1_possiveis_pontos_x.size();i++)
        {
            arquivo_gnu=arquivo_gnu+arco_1_possiveis_pontos_x.get(i)+" "+arco_1_possiveis_pontos_y.get(i)+"%5Cn";
        }
//Por ser completo, é preciso passar as informações dos dois arcos
        //arquivo_gnu=arquivo_gnu+"#     Segundo Arco"+"%5Cn";

        for(int i=0;i<arco_2_possiveis_pontos_x.size();i++)
        {
            arquivo_gnu=arquivo_gnu+arco_2_possiveis_pontos_x.get(i)+" "+arco_2_possiveis_pontos_y.get(i)+"%5Cn";
        }

        //Passamos os dados
        arquivo_gnu=arquivo_gnu.replace(" ","%20");
        url=url+"&dados="+arquivo_gnu;

    }

//Sexta02 Março
//Sexta22 Junho

    public void APAGARtransformando_deformacoes_esforcos
    (
                    List<Double> entrada_x,
                    List<Double> entrada_y
    )
    {


        Double c = Math.cos((Math.PI / 180) * anguloGLOBAL);
        Double s = Math.sin((Math.PI / 180) * anguloGLOBAL);

        List<Double> valores_Q=obter_elementos_Q();
//esforços
        List<Double> valores_SIMGA_1=new ArrayList<Double>();
        List<Double> valores_SIMGA_2=new ArrayList<Double>();
        List<Double> valores_TAU_12=new ArrayList<Double>();

        Double Q11=valores_Q.get(0);
        Double Q12=valores_Q.get(1);
        Double Q22=valores_Q.get(2);
        Double Q66=valores_Q.get(3);

        Double sigma_1;
        Double sigma_2;
//Lei de Hook matricial
        for(int i=0;i<entrada_x.size();i++)
        {
            sigma_1=Q11*entrada_x.get(i)+Q12*entrada_y.get(i);
            sigma_2=Q12*entrada_x.get(i)+Q22*entrada_y.get(i);
            valores_SIMGA_1.add(sigma_1);       //entrada_x.get(i)*1E5);
            valores_SIMGA_2.add(sigma_2);       //entrada_y.get(i)*1E5);
        }
        //Se faz de forma bastante explicita para acompanhar as formulas já definidas

        List<Double> valores_SIMGA_x=new ArrayList<Double>();
        List<Double> valores_SIMGA_y=new ArrayList<Double>();

//Pre Rotação
        Double TAU_12;

        for(int i=0;i<entrada_x.size();i++)
        {
            TAU_12=(
                gammaGLOBAL -(valores_SIMGA_1.get(i)-valores_SIMGA_2.get(i))*s*c
                    )/
                    (
                            (c*c-s*s)*Q66
                    );

            valores_TAU_12.add(TAU_12);
        }

//Rotação
        Double sigma_x;
        Double sigma_y;

        for(int i=0;i<valores_SIMGA_1.size();i++)
        {
            sigma_x=c*c*valores_SIMGA_1.get(i)+s*s*valores_SIMGA_2.get(i)-2*s*c*valores_TAU_12.get(i);

            valores_SIMGA_x.add(sigma_x);

            sigma_y=s*s*valores_SIMGA_1.get(i)+c*c*valores_SIMGA_2.get(i)+2*s*c*valores_TAU_12.get(i);

            valores_SIMGA_y.add(sigma_y);
        }
//Entregamos a transformação
        for(int i=0;i<entrada_x.size();i++)
        {
            entrada_x.set(i,valores_SIMGA_x.get(i));
            entrada_y.set(i,valores_SIMGA_y.get(i));
        }

    }

//Sexta02
//Construção simples da Matriz Q
    public List<Double> obter_elementos_Q()
    {
        List<Double> saida=new ArrayList<Double>();


        DatabaseHelper dbHelper = new DatabaseHelper(this.myContext, this.endereco);
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
            //Log.e("erro", e.getMessage());
        }

        List<String> list = dbHelper.obter_propriedades_laminas(this.lamina_usada);

        String E_1=list.get(3);//E1`	TEXT,
        String E_2=list.get(4);//E2`	TEXT,
        String G_12=list.get(6);//G12`	TEXT,
        String NU_12=list.get(9);//NU12`	TEXT,

        E_1=E_1.substring(E_1.indexOf(":")+1,E_1.length());
        E_2=E_2.substring(E_2.indexOf(":")+1,E_2.length());
        NU_12=NU_12.substring(NU_12.indexOf(":")+1,NU_12.length());
        G_12=G_12.substring(G_12.indexOf(":")+1,G_12.length());

        double d_E_1 = Double.parseDouble(E_1);
        double d_E_2 = Double.parseDouble(E_2);
        double d_NU_12 = Double.parseDouble(NU_12);
        double d_G_12 = Double.parseDouble(G_12);


        Double d_NU_21=d_NU_12*(d_E_2/d_E_1);

        Double Q11=d_E_1/(1-d_NU_21*d_NU_12);
        Double Q12=(d_NU_12*d_E_2)/(1-d_NU_21*d_NU_12);
        Double Q22=d_E_2/(1-d_NU_21*d_NU_12);
        Double Q66=d_G_12;

        saida.add(Q11);
        saida.add(Q12);
        saida.add(Q22);
        saida.add(Q66);

        return saida;
    }

/*
* Domingo Março 04
* Depurando Junho 20
* */
public void fecharPONTOS(Double angulo_entrada) {
    //Fazemos um estudo MASSIVO dos pontos
    //Estudo inicial partendo do primeiro ponto, que será um estudo especial dado que
    // não sabemos qual é o primeiro ângulo interno da área

    ArrayList<Integer> numeros=new ArrayList<Integer>();
//Preparaos o array de numeração conforme a dimensão do TRUE_pontos_x
    for(int i=0;i<TRUE_pontos_x.size();i++)
    {
        numeros.add(i);
    }

    Double ponto_inicial_x = TRUE_pontos_x.get(0);
    Double ponto_inicial_y = TRUE_pontos_y.get(0);

    //Fazemos a varredura dos pares para obter o ângulo.
//Solução rápida: Invocar a função várias vezes
    ArrayList<Double> angulos_possiveis=new ArrayList<Double>();
    Double angulo_temporal=0.0;
    Integer i_temp=0,j_temp=0;

    for (int i = 1; i < TRUE_pontos_x.size(); i++)
    {
        for(int j=i+1; j<TRUE_pontos_x.size();j++)
        {
            Double angulo=angulo(ponto_inicial_x,ponto_inicial_y,i,j);
            angulos_possiveis.add(angulo);
            if(angulo>=angulo_temporal)
            {
                angulo_temporal=angulo;
                i_temp=i;
                j_temp=j;
            }
            //Log.d("Marco05", "\n "+angulo+" Ângulo entre os pontos " + i + "  " + j+" e o ponto 1 (inicial)");
        }
    }

    ANGULADOS_TRUE_pontos_x.add(TRUE_pontos_x.get(j_temp));
    ANGULADOS_TRUE_pontos_y.add(TRUE_pontos_y.get(j_temp));

    ANGULADOS_TRUE_pontos_x.add(TRUE_pontos_x.get(0));
    ANGULADOS_TRUE_pontos_y.add(TRUE_pontos_y.get(0));

    ANGULADOS_TRUE_pontos_x.add(TRUE_pontos_x.get(i_temp));
    ANGULADOS_TRUE_pontos_y.add(TRUE_pontos_y.get(i_temp));

    numeros.remove(numeros.indexOf(i_temp));
    numeros.remove(numeros.indexOf(j_temp));

//Ele pega o maior ângulo, dado que envolve uma figura....

    // Sorting
    Collections.sort(angulos_possiveis);
//Eliminamos o primeiro elemento.
//        Log.d("Fevereiro06","Ordenado\n"+angulos_possiveis.get(9)+"   "+angulo_temporal);
    //Log.d("Marco05","2 pontos complementares\n  i  "+i_temp+" j "+j_temp);

//Extremo para ser estudado:

    Integer ANTIGO_i_temp=i_temp;
    Integer ANTIGO_j_temp=j_temp;

    //Estudo inicial partendo do primeiro ponto
    ponto_inicial_x = TRUE_pontos_x.get(ANTIGO_i_temp);
    ponto_inicial_y = TRUE_pontos_y.get(ANTIGO_i_temp);

    Double ponto_inicial_x_CONTRA = TRUE_pontos_x.get(ANTIGO_j_temp);
    Double ponto_inicial_y_CONTRA = TRUE_pontos_y.get(ANTIGO_j_temp);

    //Fazemos a varredura dos pares para obter o ângulo.
//Solução rápida: Invocar a função várias vezes
    angulos_possiveis=new ArrayList<Double>();
    angulo_temporal=0.0;
//Tendo 3 ptos. Será preciso apenas detetar os outros
    //Log.d("Marco05","X Temos os primeiros 3\n  i  "+ANGULADOS_TRUE_pontos_x);
    //Log.d("Marco05","Y Temos os primeiros 3\n  i  "+ANGULADOS_TRUE_pontos_y);
//
    angulo_temporal=0.0;
    angulos_possiveis.clear();

    for(int j=1; j<TRUE_pontos_x.size();j++) //acima de 0 (Antigo Ponto Inicial)
    {
        if(j!=ANTIGO_i_temp)
        {
            Double angulo=angulo(
                    ponto_inicial_x,
                    ponto_inicial_y,
                    0,
                    j);
            angulos_possiveis.add(angulo);
            if(angulo>=angulo_temporal)
            {
                angulo_temporal=angulo;
                j_temp=j;
            }
        }
    }

    // Sorting
    Collections.sort(angulos_possiveis);
    //Log.d("Marco05", "Ordenando pelo ANTIGO_i_temp\n "+angulos_possiveis);
    //Log.d("Marco05", "j_temp\n "+j_temp);

    numeros.remove(numeros.indexOf(j_temp));

    ANGULADOS_TRUE_pontos_x.add(TRUE_pontos_x.get(j_temp));
    ANGULADOS_TRUE_pontos_y.add(TRUE_pontos_y.get(j_temp));

    POLIGONO_TRUE_pontos_x.clear();
    POLIGONO_TRUE_pontos_y.clear();

    for(Integer i=0;i<ANGULADOS_TRUE_pontos_x.size();i++)
    {
        POLIGONO_TRUE_pontos_x.add(ANGULADOS_TRUE_pontos_x.get(i));
        POLIGONO_TRUE_pontos_y.add(ANGULADOS_TRUE_pontos_y.get(i));
    }
//Fevereiro08
    construir_COM_POLIGONO_TRUE_pontos(angulo_entrada);
    //Log.d("Marco05", "POLIGONO_TRUE_pontos_x\n "+POLIGONO_TRUE_pontos_x);
    //Log.d("Marco05", "POLIGONO_TRUE_pontos_y\n "+POLIGONO_TRUE_pontos_y);

    //enviando_data_gnu(ANGULADOS_TRUE_pontos_x,ANGULADOS_TRUE_pontos_y);
//
////////////////////////////////////////
    }
//Marco10
//Ordenamos os 5 pontos
 public void fechar5pontos()
 {


     //Fazemos um estudo MASSIVO dos 6 pontos
     //Estudo inicial partendo do primeiro ponto

     ArrayList<Integer> numeros=new ArrayList<Integer>();
//Por segurança limpamos a lista
     numeros.clear();

     numeros.add(1);
     numeros.add(2);
     numeros.add(3);
     numeros.add(4);
     numeros.add(5);

     Double ponto_inicial_x = TRUE_pontos_x.get(0);
     Double ponto_inicial_y = TRUE_pontos_y.get(0);

     //Fazemos a varredura dos pares para obter o ângulo.
//Solução rápida: Invocar a função várias vezes
     ArrayList<Double> angulos_possiveis=new ArrayList<Double>();
     Double angulo_temporal=0.0;
     Integer i_temp=0,j_temp=0;

     for (int i = 1; i < TRUE_pontos_x.size(); i++)
     {
         for(int j=i+1; j<TRUE_pontos_x.size();j++)
         {
             Double angulo=angulo(ponto_inicial_x,ponto_inicial_y,i,j);
             angulos_possiveis.add(angulo);
             if(angulo>=angulo_temporal)
             {
                 angulo_temporal=angulo;
                 i_temp=i;
                 j_temp=j;
             }
             //Log.d("Marco05", "\n "+angulo+" Ângulo entre os pontos " + i + "  " + j+" e o ponto 1 (inicial)");
         }
     }

     ANGULADOS_TRUE_pontos_x.add(TRUE_pontos_x.get(j_temp));
     ANGULADOS_TRUE_pontos_y.add(TRUE_pontos_y.get(j_temp));

     ANGULADOS_TRUE_pontos_x.add(TRUE_pontos_x.get(0));
     ANGULADOS_TRUE_pontos_y.add(TRUE_pontos_y.get(0));

     ANGULADOS_TRUE_pontos_x.add(TRUE_pontos_x.get(i_temp));
     ANGULADOS_TRUE_pontos_y.add(TRUE_pontos_y.get(i_temp));

     numeros.remove(numeros.indexOf(i_temp));
     numeros.remove(numeros.indexOf(j_temp));

//Ele pega o maior ângulo, dado que envolve uma figura....

     // Sorting
     Collections.sort(angulos_possiveis);
//Eliminamos o primeiro elemento.
//        Log.d("Fevereiro06","Ordenado\n"+angulos_possiveis.get(9)+"   "+angulo_temporal);
     //Log.d("Marco05","2 pontos complementares\n  i  "+i_temp+" j "+j_temp);

//Extremo para ser estudado:

     Integer ANTIGO_i_temp=i_temp;
     Integer ANTIGO_j_temp=j_temp;

     //Estudo inicial partendo do primeiro ponto
     ponto_inicial_x = TRUE_pontos_x.get(ANTIGO_i_temp);
     ponto_inicial_y = TRUE_pontos_y.get(ANTIGO_i_temp);

     Double ponto_inicial_x_CONTRA = TRUE_pontos_x.get(ANTIGO_j_temp);
     Double ponto_inicial_y_CONTRA = TRUE_pontos_y.get(ANTIGO_j_temp);

     //Fazemos a varredura dos pares para obter o ângulo.
//Solução rápida: Invocar a função várias vezes
     angulos_possiveis=new ArrayList<Double>();
     angulo_temporal=0.0;
//Tendo 3 ptos. Será preciso apenas detetar os outros
     //Log.d("Marco05","X Temos os primeiros 3\n  i  "+ANGULADOS_TRUE_pontos_x);
     //Log.d("Marco05","Y Temos os primeiros 3\n  i  "+ANGULADOS_TRUE_pontos_y);
//
     angulo_temporal=0.0;
     angulos_possiveis.clear();

     for(int j=1; j<TRUE_pontos_x.size();j++) //acima de 0 (Antigo Ponto Inicial)
     {
         if(j!=ANTIGO_i_temp)
         {
             Double angulo=angulo(
                     ponto_inicial_x,
                     ponto_inicial_y,
                     0,
                     j);
             angulos_possiveis.add(angulo);
             if(angulo>=angulo_temporal)
             {
                 angulo_temporal=angulo;
                 j_temp=j;
             }
         }
     }

     // Sorting
     Collections.sort(angulos_possiveis);

     numeros.remove(numeros.indexOf(j_temp));

     ANGULADOS_TRUE_pontos_x.add(TRUE_pontos_x.get(j_temp));
     ANGULADOS_TRUE_pontos_y.add(TRUE_pontos_y.get(j_temp));
//Domingo
// Agora do outro lado

     angulos_possiveis=new ArrayList<Double>();

     angulo_temporal=0.0;

     for(int j=1; j<TRUE_pontos_x.size();j++) //acima de 0 (Antigo Ponto Inicial)
     {
         if(j!=ANTIGO_j_temp)
         {

             Double angulo=angulo(
                     ponto_inicial_x_CONTRA,
                     ponto_inicial_y_CONTRA,
                     0,
                     j);
             angulos_possiveis.add(angulo);
             if(angulo>=angulo_temporal)
             {
                 angulo_temporal=angulo;
                 i_temp=0;
                 j_temp=j;
             }
         }
     }

     // Sorting
     Collections.sort(angulos_possiveis);
     //Log.d("Marco05", "Ordenando pelo ANTIGO_i_temp\n "+angulos_possiveis);
     //Log.d("Marco05", "j_temp\n "+j_temp);
/*
     //colocamos o não usado como inicial, é como um restante

     numeros.remove(numeros.indexOf(j_temp));

     POLIGONO_TRUE_pontos_x.add(TRUE_pontos_x.get(numeros.get(0)));
     POLIGONO_TRUE_pontos_y.add(TRUE_pontos_y.get(numeros.get(0)));
*/
     POLIGONO_TRUE_pontos_x.add(TRUE_pontos_x.get(j_temp));
     POLIGONO_TRUE_pontos_y.add(TRUE_pontos_y.get(j_temp));

     for(Integer i=0;i<ANGULADOS_TRUE_pontos_x.size();i++)
     {
         POLIGONO_TRUE_pontos_x.add(ANGULADOS_TRUE_pontos_x.get(i));
         POLIGONO_TRUE_pontos_y.add(ANGULADOS_TRUE_pontos_y.get(i));
     }
//Fevereiro08
     construir_COM_POLIGONO_TRUE_pontos(anguloGLOBAL);

     /*

        Log.d("Marco05", "POLIGONO_TRUE_pontos_x\n "+POLIGONO_TRUE_pontos_x);
        Log.d("Marco05", "POLIGONO_TRUE_pontos_y\n "+POLIGONO_TRUE_pontos_y);
//
////////////////////
     * */
 }
//Março 11
// O primeiro que se faz é identificar a multiplicidade de cada valor

public void caso_duplos_reduzir_8_para_4_com_angulo45(){

    analisar_x();
    analisar_y();
    comparar_envelope_45();
}

List<Boolean> identicos_x=new ArrayList<Boolean>();

List<Boolean> identicos_y=new ArrayList<Boolean>();

public void analisar_x()
{

    for(int i=0; i<TRUE_pontos_x.size();i++)
    {
        identicos_x.add(false);
    }

    for(int i=0; i<TRUE_pontos_x.size();i++)
    {
        for(int j=i+1; j<TRUE_pontos_x.size();j++)
        {
            Double diff=TRUE_pontos_x.get(i)-TRUE_pontos_x.get(j);


            //if(TRUE_pontos_x.get(i)==TRUE_pontos_x.get(j))
            if(Math.abs(diff)<tolerancia)
            {
                identicos_x.set(i,true);
                identicos_x.set(j,true);

            }
        }
    }

}

public void analisar_y(){


    for(int i=0; i<TRUE_pontos_y.size();i++)
    {
        identicos_y.add(false);
    }

    for(int i=0; i<TRUE_pontos_y.size();i++)
    {
        for(int j=i+1; j<TRUE_pontos_y.size();j++)
        {
            Double diff=TRUE_pontos_y.get(i)-TRUE_pontos_y.get(j);



            //if(TRUE_pontos_x.get(i)==TRUE_pontos_x.get(j))
            if(Math.abs(diff)<tolerancia)
            {
                identicos_y.set(i,true);
                identicos_y.set(j,true);

            }
        }
    }


}

public void comparar_envelope_45()
{
    List<Boolean> identicos_xy=new ArrayList<Boolean>();

    List<Integer> eliminar_TRUE=new ArrayList<Integer>();

    int contador=0;

    for(int i=0;i<identicos_x.size();i++)
    {
        identicos_xy.add(identicos_x.get(i) && identicos_y.get(i));
    }

    for(int i=0;i<identicos_xy.size();i++)
    {
        if (identicos_xy.get(i))
        {
            for(int j=i+1;j<identicos_xy.size();j++)
            {
                if(Math.abs(TRUE_pontos_x.get(i)
                        -TRUE_pontos_x.get(j))
                        <tolerancia)
                {
                    if(Math.abs(TRUE_pontos_y.get(i)
                            -TRUE_pontos_y.get(j))
                            <tolerancia)
                    {
                        contador++;
                        eliminar_TRUE.add(i);
                    }
                }
            }
        }
    }
    //Eliminamos aqueles que estejam duplicados
    for(int j=0;j<eliminar_TRUE.size();j++)
    {
        TRUE_pontos_x.set(j,null);
        TRUE_pontos_y.set(j,null);
    }
    TRUE_pontos_x.removeAll(Collections.singleton(null));
    TRUE_pontos_y.removeAll(Collections.singleton(null));


}
//Marco12

    public void ANTIGOusar_GraphView_de_45()
    {
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

        graph.setVisibility(View.VISIBLE);

        graph.removeAllSeries();
//sexta02 no caso de 4 pontos parece funcionar sem problema
        //transformando_deformacoes_esforcos(possiveis_pontos_x,possiveis_pontos_y); //funciona

//colocamos os valores extremos:
        //Collections.sort(TRUE_pontos_x);


        //Fazemos ordenamento por burbulha

        boolean ORDEM_ASCII=true;

        Double minimo=TRUE_pontos_x.get(0);

        for (Integer i = 0; i < TRUE_pontos_x.size(); i++)
        {
            if(TRUE_pontos_x.get(i)<minimo)
            {
                ORDEM_ASCII=false;
            }
            minimo=TRUE_pontos_x.get(i);
        }

        if(!ORDEM_ASCII)
        {
            Double menor_arco_1_x;
            Double menor_arco_1_y;
            Double TEMP_arco_1_x;
            Double TEMP_arco_1_y;
            for (Integer i = 0; i < TRUE_pontos_x.size(); i++)
            {
                menor_arco_1_x=TRUE_pontos_x.get(i);
                menor_arco_1_y=TRUE_pontos_y.get(i);
                for(Integer j=i+1;j<TRUE_pontos_x.size();j++)
                {

                    if(TRUE_pontos_x.get(j)<menor_arco_1_x)
                    {
                        TEMP_arco_1_x=TRUE_pontos_x.get(j);
                        TRUE_pontos_x.set(j,menor_arco_1_x);
                        TRUE_pontos_x.set(i,TEMP_arco_1_x);
//O mesmo para Y para assim manter o sistema ordenado
                        TEMP_arco_1_y=TRUE_pontos_y.get(j);
                        TRUE_pontos_y.set(j,menor_arco_1_y);
                        TRUE_pontos_y.set(i,TEMP_arco_1_y);
                        break;
                    }
                }
//for

            }

        }



        Double minimo_x = TRUE_pontos_x.get(0);
//máximo X em ponto 3
        Double maximo_x = TRUE_pontos_x.get(TRUE_pontos_x.size()-1);

Collections.sort(TRUE_pontos_y);

        Double minimo_y = TRUE_pontos_y.get(0);
//máximo X em ponto 3
        Double maximo_y = TRUE_pontos_y.get(TRUE_pontos_y.size()-1);

        Double escala=1e6;


//Sabemos que são apenas 4 pontos, e por tanto serão conetados de forma imediata

        LineGraphSeries<DataPoint> series_0 = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(TRUE_pontos_x.get(0)/escala, TRUE_pontos_y.get(0)/escala),
                //new DataPoint(TRUE_pontos_x.get(1), TRUE_pontos_y.get(1))
                //new DataPoint(TRUE_pontos_x.get(2), TRUE_pontos_y.get(2))
                new DataPoint(TRUE_pontos_x.get(3)/escala, TRUE_pontos_y.get(3)/escala)

        });


        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.getViewport().setMinX(minimo_x * 1.5);
        graph.getViewport().setMaxX(maximo_x * 1.5);

        graph.getViewport().setMinY(minimo_y * 1.5);
        graph.getViewport().setMaxY(maximo_y * 1.5);

        graph.addSeries(series_0);
        //graph.addSeries(series_1);



        enviando_data_gnu(TRUE_pontos_x,TRUE_pontos_y);
///////////////
    }
// Maio 09

    public void usar_GraphView_de_45()
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
                    return transformado + " (MPa)";

                }
            }
        });

        graph.removeAllSeries();

        //Fazemos ordenamento por burbulha

        boolean ORDEM_ASCII=true;

        Double minimo=TRUE_pontos_x.get(0);

        for (Integer i = 0; i < TRUE_pontos_x.size(); i++)
        {
            if(TRUE_pontos_x.get(i)<minimo)
            {
                ORDEM_ASCII=false;
            }
            minimo=TRUE_pontos_x.get(i);
        }

        if(!ORDEM_ASCII)
        {
            Double menor_arco_1_x;
            Double menor_arco_1_y;
            Double TEMP_arco_1_x;
            Double TEMP_arco_1_y;
            for (Integer i = 0; i < TRUE_pontos_x.size(); i++)
            {
                menor_arco_1_x=TRUE_pontos_x.get(i);
                menor_arco_1_y=TRUE_pontos_y.get(i);
                for(Integer j=i+1;j<TRUE_pontos_x.size();j++)
                {

                    if(TRUE_pontos_x.get(j)<menor_arco_1_x)
                    {
                        TEMP_arco_1_x=TRUE_pontos_x.get(j);
                        TRUE_pontos_x.set(j,menor_arco_1_x);
                        TRUE_pontos_x.set(i,TEMP_arco_1_x);
//O mesmo para Y para assim manter o sistema ordenado
                        TEMP_arco_1_y=TRUE_pontos_y.get(j);
                        TRUE_pontos_y.set(j,menor_arco_1_y);
                        TRUE_pontos_y.set(i,TEMP_arco_1_y);
                        break;
                    }
                }
//for

            }

        }



        Double minimo_x = TRUE_pontos_x.get(0);
//máximo X em ponto 3
        Double maximo_x = TRUE_pontos_x.get(TRUE_pontos_x.size()-1);

        Collections.sort(TRUE_pontos_y);

        Double minimo_y = TRUE_pontos_y.get(0);
//máximo X em ponto 3
        Double maximo_y = TRUE_pontos_y.get(TRUE_pontos_y.size()-1);


        Double escala=1e6;

        maximo_x=maximo_x/escala;
        maximo_y=maximo_y/escala;

        minimo_x=minimo_x/escala;
        minimo_y=minimo_y/escala;

        for (Integer i = 0; i < TRUE_pontos_x.size(); i++)
        {
            TRUE_pontos_x.set(i,TRUE_pontos_x.get(i)/escala);
            TRUE_pontos_y.set(i,TRUE_pontos_y.get(i)/escala);
        }


        graph.getViewport().setMinX(minimo_x * 1.5);
        graph.getViewport().setMaxX(maximo_x * 1.5);

        graph.getViewport().setMinY(minimo_y * 1.5);
        graph.getViewport().setMaxY(maximo_y * 1.5);

//Sabemos que são apenas 4 pontos, e por tanto serão conetados de forma imediata

        LineGraphSeries<DataPoint> series_0 = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(TRUE_pontos_x.get(0), TRUE_pontos_y.get(0)),
                new DataPoint(TRUE_pontos_x.get(1), TRUE_pontos_y.get(1)),
                new DataPoint(TRUE_pontos_x.get(2), TRUE_pontos_y.get(2)),
                new DataPoint(TRUE_pontos_x.get(3), TRUE_pontos_y.get(3))

        });

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.addSeries(series_0);
        //graph.addSeries(series_1);



///////////////
    }
//

//Sexta22 Junho

    List<Double> possiveis_pontos_xy=new ArrayList<Double>();

    public void transformando_deformacoes_esforcos
            (
                    List<Double> entrada_x,
                    List<Double> entrada_y
            )
    {

        Double c = Math.cos((Math.PI / 180) * anguloGLOBAL);
        Double s = Math.sin((Math.PI / 180) * anguloGLOBAL);

//esforços
        List<Double> valores_SIMGA_x=new ArrayList<Double>();
        List<Double> valores_SIMGA_y=new ArrayList<Double>();

        List<Double> valores_Q=obter_elementos_Q_barra();

        Double Q11=valores_Q.get(0);
        Double Q12=valores_Q.get(1);
        Double Q22=valores_Q.get(2);
        Double Q66=valores_Q.get(3);

        Double Q16=valores_Q.get(4);
        Double Q26=valores_Q.get(5);

        Double sigma_x;
        Double sigma_y;
        Double possivel_tau_xy;

        for(int i=0;i<entrada_x.size();i++)
        {
//junho20
            sigma_x=Q11*entrada_x.get(i)+Q12*entrada_y.get(i)+Q16*gammaGLOBAL;
            sigma_y=Q12*entrada_x.get(i)+Q22*entrada_y.get(i)+Q26*gammaGLOBAL;
            possivel_tau_xy =Q16*entrada_x.get(i)+Q26*entrada_y.get(i)+Q66*gammaGLOBAL;
            valores_SIMGA_x.add(sigma_x);
            valores_SIMGA_y.add(sigma_y);
            possiveis_pontos_xy.add(possivel_tau_xy);
        }

//Entregamos a transformação
        for(int i=0;i<entrada_x.size();i++)
        {
            entrada_x.set(i,valores_SIMGA_x.get(i));
            entrada_y.set(i,valores_SIMGA_y.get(i));
        }




    }
//junho20
public List<Double> obter_elementos_Q_barra()
{
    Double theta_radianos = (Math.PI / 180) * anguloGLOBAL;
    Double c = Math.cos(theta_radianos);
    Double s = Math.sin(theta_radianos);

    Double NU21=f_NU12*(f_E2/f_E1);

    Double Q11=f_E1/(1-NU21*f_NU12);
    Double Q12=(f_NU12*f_E2)/(1-NU21*f_NU12);
    Double Q22=f_E2/(1-NU21*f_NU12);
    Double Q66=f_G12;

    Double Q11_barra=(Math.pow(c,4))*Q11+(Math.pow(s,4))*Q22 + 2*(Math.pow(c,2))*(Math.pow(s,2))*(Q12 + 2*Q66);
/*
    Log.d("Junho20",
            "Math.pow(c,4)*Q11 "+Math.pow(c,4)*Q11+"\n"+
                    "(Math.pow(s,4))*Q22 "+(Math.pow(s,4))*Q22+"\n"+
"2*(Math.pow(c,2))*(Math.pow(s,2))*(Q12 + 2*Q66) "+2*(Math.pow(c,2))*(Math.pow(s,2))*(Q12 + 2*Q66)+"\n"+
                    "Q11_barra  "+Q11_barra+"\n"
    );

    Log.d("Junho20","2*(Math.pow(c,2))*(Math.pow(s,2))"
            +2*(Math.pow(c,2))*(Math.pow(s,2))+"\n"
            +"*(Q12 + 2*Q66) "+(Q12 + 2*Q66)
    );

    Log.d("Junho20",
            "Q12 "+Q12+
                    " Q66 "+Q66
    );
*/


    Double Q12_barra=(Math.pow(c,4) + Math.pow(s,4))*Q12 + (Math.pow(c,2))*(Math.pow(s,2))*(Q11 + Q22 - 4*Q66);
    Double Q22_barra=(Math.pow(s,4))*Q11 + 2*(Math.pow(c,2))*(Math.pow(s,2))*(Q12 + 2*Q66) + (Math.pow(c,4))*Q22;
    Double Q66_barra=(Math.pow(c,2))*(Math.pow(s,2))*(Q11 + Q22 - 2*Q12 - 2*Q66) + (Math.pow(c,4) + Math.pow(s,4))*Q66;

    Double Q16_barra=(Math.pow(c,3))*s*(Q11 - Q12 - 2*Q66) - c*(Math.pow(s,3))*(Q22 - Q12 - 2*Q66);
    Double Q26_barra=c*(Math.pow(s,3))*(Q11 - Q12 - 2*Q66) - s*(Math.pow(c,3))*(Q22 - Q12 - 2*Q66);


    List<Double> saidaQbar=new ArrayList<>();

    saidaQbar.add(Q11_barra);
    saidaQbar.add(Q12_barra);
    saidaQbar.add(Q22_barra);
    saidaQbar.add(Q66_barra);

    saidaQbar.add(Q16_barra);
    saidaQbar.add(Q26_barra);

    return saidaQbar;
}

// /////////////////////////////////////////////////////////
}
