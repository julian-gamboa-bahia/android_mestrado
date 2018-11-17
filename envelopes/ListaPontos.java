package br.com.ven2020.envelopes2018.envelopes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.ven2020.envelopes2018.R;
import br.com.ven2020.envelopes2018.criterios.entrada_dados;
import br.com.ven2020.envelopes2018.criterios.numerico_maxima_tensao;
import br.com.ven2020.envelopes2018.database.DatabaseHelper;


public class ListaPontos extends AppCompatActivity {

    double[][] listar_pontos_array;
    String tau_xy="0.0";
    String angulo="0.0";
    String envelope_usado="";

    String nome_lamina="";
    String wu_biaxial="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pontos);
        /*
        */

        Integer listar_pontos_array_length=getIntent().getIntExtra("listar_pontos_array_length",0);


        //double [] listar_pontos_array_x=new double[listar_pontos_array_length];
        //double [] listar_pontos_array_y=new double[listar_pontos_array_length];

        Bundle extras = getIntent().getExtras();

        double [] listar_pontos_array_x=extras.getDoubleArray("listar_pontos_array_x");
        double [] listar_pontos_array_y=extras.getDoubleArray("listar_pontos_array_y");
        double [] listar_pontos_array_xy=extras.getDoubleArray("listar_pontos_array_xy");

        tau_xy=extras.getString("tau_xy");
        angulo=extras.getString("angulo");
        envelope_usado=extras.getString("envelope_usado");
        nome_lamina=extras.getString("nome_lamina");

        TextView titulo_lista_pontos=(TextView) findViewById(R.id.titulo_lista_pontos);


        String legenda="Critério de falha: "+envelope_usado+
                "\nPontos do Envelope (sigma_x,sigma_y,tau_xy)  (MPa)\n"
                +" lâmina rotacioanada: "+angulo+"(Degree)\n"+
                "material: "+nome_lamina;

        if(envelope_usado.contentEquals("Tsai-Wu"))
        {
            wu_biaxial=extras.getString("wu_biaxial");
            legenda=legenda+"\n"+"com o parâmetro biaxial: "+wu_biaxial+" (Pa) ";
        }
        titulo_lista_pontos.setText(legenda);

        listar_pontos_array=new double[listar_pontos_array_x.length][3];

        for(int i=0;i<listar_pontos_array_x.length;i++)
        {
            listar_pontos_array[i][0]=listar_pontos_array_x[i];
            listar_pontos_array[i][1]=listar_pontos_array_y[i];
            listar_pontos_array[i][2]=listar_pontos_array_xy[i];
        }

        listar_laminas();
    }
//Maio12

    public static String formatSignificant(double value, int significant)
    {
        MathContext mathContext = new MathContext(significant, RoundingMode.DOWN);
        BigDecimal bigDecimal = new BigDecimal(value, mathContext);
        return bigDecimal.toPlainString();
    }



    public void listar_laminas()
    {
        List<String> list =new ArrayList<>();

        for(int i=0;i<listar_pontos_array.length;i++)
        {
            String d3sigma_x=formatSignificant(listar_pontos_array[i][0]/1e+6, 3);
            String d3sigma_y=formatSignificant(listar_pontos_array[i][1]/1e+6, 3);
            String d3tau_xy=formatSignificant(Double.parseDouble(tau_xy)/1e+6, 3);

            if(envelope_usado.contentEquals("Máxima Deformação"))
            {
                d3tau_xy=formatSignificant(listar_pontos_array[i][2]/1e+6, 3);
            }

            list.add("("+d3sigma_x+","+d3sigma_y+","+d3tau_xy+")");

            String base="http://127.0.0.1/maio/criterios/maxima_deformacao.htm?/82.0/8/00/"+
                    listar_pontos_array[i][0]+
            "/"+
                    listar_pontos_array[i][1];

            if(envelope_usado.contentEquals("Máxima Deformação"))
            {
                base=base+"/"+listar_pontos_array[i][2]+"/0/2e10";
            }
            else
            {
                base=base+"/"+listar_pontos_array[i][2]+"/0/2e10";
            }

            //Log.d("maio17", "("+listar_pontos_array[i][0]+","+listar_pontos_array[i][1]+","+tau_xy+")");
            //Log.d("Junho21", base);
        }

        //Log.d("obter_nomes_laminas","obter_nomes_laminas "+list.toString());

        int numero_laminas_registradas=list.size();
        final String[] nome_lamina =new String[numero_laminas_registradas+1];
        final int[] animalImages=new int[numero_laminas_registradas+1];

        for (int i =0; i< list.size(); i++) {
            nome_lamina[i]=list.get(i);
            animalImages[i]=R.drawable.logo_mechg;
        }


        nome_lamina[numero_laminas_registradas]="";
        animalImages[numero_laminas_registradas]=R.drawable.tela_incial_icone_blanco;

        ListView simpleListView=(ListView)findViewById(R.id.simpleListView);

        ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();

        for (int i = 0; i< nome_lamina.length; i++)
        {
            HashMap<String,String> hashMap=new HashMap<>();//create a hashmap to store the data in key value pair
            hashMap.put("nome_lamina", nome_lamina[i]);
            hashMap.put("image",animalImages[i]+"");
            arrayList.add(hashMap);//add the hashmap into arrayList
        }
        String[] from={"nome_lamina","image"};//string array
        int[] to={R.id.textView,R.id.imageView};//int array of views id's
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,arrayList,R.layout.list_view_items,from,to);//Create object and set the parameters for simpleAdapter


        simpleListView.setAdapter(simpleAdapter);//sets the adapter for listView

        simpleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(nome_lamina[i].length()!=0)
                {
                    calcular_if_usando_lista(i);
                }
                /////
            }
        });
    }

    public void calcular_if_usando_lista(Integer posicao_ponto)
    {

        Intent entrada_dados = new Intent(getBaseContext(),
                br.com.ven2020.envelopes2018.criterios.entrada_dados.class);
        //Outubro11
        entrada_dados.putExtra("sigma_x",listar_pontos_array[posicao_ponto][0]+"");
        entrada_dados.putExtra("sigma_y",listar_pontos_array[posicao_ponto][1]+"");

        if(envelope_usado.contentEquals("Máxima Deformação"))
        {
            entrada_dados.putExtra("tau_xy",listar_pontos_array[posicao_ponto][2]+"");
        }
        else
        {
            entrada_dados.putExtra("tau_xy",tau_xy+"");
        }

        entrada_dados.putExtra("angulo",angulo+"");

//Nov16
// envelope_usado será coletado na classe entrada_dados
        entrada_dados.putExtra("criterio",envelope_usado+"");

        if(envelope_usado.contentEquals("Tsai-Wu"))
        {
            entrada_dados.putExtra("wu_biaxial",wu_biaxial+"");
            //Log.d("EUwu_biaxial",                "\nwu_biaxial\n"+biaxial        );
        }
        startActivityForResult(entrada_dados, 1);
    }
///////////////////////////
}
