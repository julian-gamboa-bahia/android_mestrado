package br.com.ven2020.envelopes2018.database;

/*
*  * Created by julian on 14/06/17.
* */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;


public class db_historico_estados_esforcos extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "historico_estados.db";
    public static final String TABLE_NAME = "historico_estados";
//Colunas:
    public static final String COLUMN_id = "id";
    public static final String COLUMN_lamina_usada = "lamina_usada";
    public static final String COLUMN_envelope_usado = "envelope_usado";
    public static final String COLUMN_criterio_usado = "criterio_usado";
    public static final String COLUMN_DATA = "data";

    public static final String COLUMN_sigma_x = "sigma_x";
    public static final String COLUMN_sigma_y = "sigma_y";
    public static final String COLUMN_tau_xy = "tau_xy";
    public static final String COLUMN_angulo = "angulo";


    private HashMap hp;

    public db_historico_estados_esforcos(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String query_creater="create table "+TABLE_NAME+
                "(" +
                "id integer primary key, " +
                "lamina_usada text," +
                "envelope_usado text," +
                "criterio_usado text," +
                "data text, " +
                "sigma_x text, " +
                "sigma_y text, " +
                "tau_xy text, " +
                "angulo text " +
                ")";
        db.execSQL(query_creater);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS historico_estados");
        onCreate(db);
    }

    // Outubro04

    public boolean insert_estado_esforcos(
            String lamina_usada,
            String criterio_usado,
            String envelope_usado,
            Float sigma_x,
            Float sigma_y,
            Float tau_xy,
            Float angulo,
            String data
            )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_lamina_usada,lamina_usada);
        contentValues.put(COLUMN_envelope_usado,envelope_usado);
        contentValues.put(COLUMN_criterio_usado,criterio_usado);

        contentValues.put(COLUMN_sigma_x, sigma_x);
        contentValues.put(COLUMN_sigma_y, sigma_y);
        contentValues.put(COLUMN_tau_xy, tau_xy);
        contentValues.put(COLUMN_angulo, angulo);
        contentValues.put(COLUMN_DATA, data);




        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

/*
* Outubro04
* */

    public ArrayList<String> obter_historico_estados_esforcos() {

        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();

//        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" order by id desc limit 10", null);
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" where length("+COLUMN_lamina_usada+")>0 order by id desc limit 10", null);


        res.moveToFirst();

        String s_COLUMN_sigma_x="";
        String s_COLUMN_sigma_y="";
        String s_COLUMN_tau_xy="";
        String s_COLUMN_angulo="";

        int i=0;

        while(res.isAfterLast() == false){
            s_COLUMN_sigma_x=res.getString(res.getColumnIndex(COLUMN_sigma_x));
            s_COLUMN_sigma_y=res.getString(res.getColumnIndex(COLUMN_sigma_y));
            s_COLUMN_tau_xy=res.getString(res.getColumnIndex(COLUMN_tau_xy));
            s_COLUMN_angulo=res.getString(res.getColumnIndex(COLUMN_angulo));



            if(s_COLUMN_sigma_x.length()>5)
            {
                Double temporal=Double.parseDouble(res.getString(res.getColumnIndex(COLUMN_sigma_x)));
                String transformado=String.format("%2.2e",temporal);
                s_COLUMN_sigma_x=transformado;
            }

            if(s_COLUMN_sigma_y.length()>5)
            {
                Double temporal=Double.parseDouble(res.getString(res.getColumnIndex(COLUMN_sigma_x)));
                String transformado=String.format("%2.2e",temporal);
                s_COLUMN_sigma_y=transformado;
            }

            if(s_COLUMN_tau_xy.length()>5)
            {
                Double temporal=Double.parseDouble(res.getString(res.getColumnIndex(COLUMN_sigma_x)));
                String transformado=String.format("%2.2e",temporal);
                s_COLUMN_tau_xy=transformado;
            }

            String preparado=
                    "("
                    +s_COLUMN_sigma_x
                            +","
                            +s_COLUMN_sigma_y
                            +","
                            +s_COLUMN_tau_xy
                            +")"+
                    "\n" +
                            "com ângulo de rotação de "+
                    s_COLUMN_angulo;

            array_list.add(preparado);

            //Log.d("Outubro04","numero da lista "+i);
            //Log.d("Outubro04","numero da lista "+i);
            //Log.d("Outubro28",s_COLUMN_sigma_x);
            //Log.d("Outubro28",s_COLUMN_sigma_y);
            //Log.d("Outubro28",s_COLUMN_tau_xy);
            //Log.d("Outubro04",s_COLUMN_angulo);

            res.moveToNext();
        }
/*
        Log.d("Outubro28",lamina_usada);
        Log.d("Outubro28",envelope_usado);
        Log.d("Outubro28",criterio_usado);

        Log.d("Outubro28",""+sigma_x);
        Log.d("Outubro28",""+sigma_y);
        Log.d("Outubro28",""+tau_xy);
        Log.d("Outubro28",""+angulo);
        Log.d("Outubro28",data);
  */

        return array_list;
    }

/*
* Outubro04
* */

    public ArrayList<String> obter_ultimo_estados_esforcos() {

        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" where length("+COLUMN_lamina_usada+")>0 order by "+COLUMN_id+" desc limit 1 ",null);

        res.moveToFirst();

        String s_COLUMN_sigma_x="";
        String s_COLUMN_sigma_y="";
        String s_COLUMN_tau_xy="";
        String s_COLUMN_angulo="";

        int i=0;

        while(res.isAfterLast() == false){
            s_COLUMN_sigma_x=res.getString(res.getColumnIndex(COLUMN_sigma_x));
            s_COLUMN_sigma_y=res.getString(res.getColumnIndex(COLUMN_sigma_y));
            s_COLUMN_tau_xy=res.getString(res.getColumnIndex(COLUMN_tau_xy));
            s_COLUMN_angulo=res.getString(res.getColumnIndex(COLUMN_angulo));

            array_list.add(s_COLUMN_sigma_x);
            array_list.add(s_COLUMN_sigma_y);
            array_list.add(s_COLUMN_tau_xy);
            array_list.add(s_COLUMN_angulo);

            res.moveToNext();
        }
        return array_list;
    }

/*
* Outubro06
* */

    public String obter_ultimo_estados_esforcos_URL() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" order by "+COLUMN_id+" desc limit 1", null);

        res.moveToFirst();

        String segmento_url_estado_esforcos="";


        while(res.isAfterLast() == false){

            String s_COLUMN_sigma_x=res.getString(res.getColumnIndex(COLUMN_sigma_x));
            String s_COLUMN_sigma_y=res.getString(res.getColumnIndex(COLUMN_sigma_y));
            String s_COLUMN_tau_xy=res.getString(res.getColumnIndex(COLUMN_tau_xy));
            String s_COLUMN_angulo=res.getString(res.getColumnIndex(COLUMN_angulo));

            segmento_url_estado_esforcos=segmento_url_estado_esforcos+"/"+s_COLUMN_sigma_x+"/"+s_COLUMN_sigma_y+"/"+s_COLUMN_tau_xy+"/";

            res.moveToNext();
        }
        return segmento_url_estado_esforcos;
    }

/*
* Outubro06
* */

    public String obter_ultimo_angulo_URL() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" order by "+COLUMN_id+" desc limit 1", null);

        res.moveToFirst();

        String segmento_url_estado_esforcos="";


        while(res.isAfterLast() == false){

            String s_COLUMN_angulo=res.getString(res.getColumnIndex(COLUMN_angulo));

            segmento_url_estado_esforcos=s_COLUMN_angulo;

            res.moveToNext();
        }
        return segmento_url_estado_esforcos;
    }
/*
* Outubro10
* */

    public ArrayList<String> obter_estados_esforcos_especifico(Integer i) {

        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" where length("+COLUMN_lamina_usada+")>0 order by id desc limit 10", null);


        res.moveToFirst();

        String s_COLUMN_sigma_x="";
        String s_COLUMN_sigma_y="";
        String s_COLUMN_tau_xy="";
        String s_COLUMN_angulo="";

        int controle=0;

        while(res.isAfterLast() == false){
            s_COLUMN_sigma_x=res.getString(res.getColumnIndex(COLUMN_sigma_x));
            s_COLUMN_sigma_y=res.getString(res.getColumnIndex(COLUMN_sigma_y));
            s_COLUMN_tau_xy=res.getString(res.getColumnIndex(COLUMN_tau_xy));
            s_COLUMN_angulo=res.getString(res.getColumnIndex(COLUMN_angulo));

            if(controle==i)
            {
                array_list.add(s_COLUMN_sigma_x);
                array_list.add(s_COLUMN_sigma_y);
                array_list.add(s_COLUMN_tau_xy);
                array_list.add(s_COLUMN_angulo);
            }

            controle++;
            //Log.d("Outubro04","numero da lista "+i);
            //Log.d("Outubro04",s_COLUMN_sigma_x);
            //Log.d("Outubro04",s_COLUMN_sigma_y);
            //Log.d("Outubro04",s_COLUMN_tau_xy);
            //Log.d("Outubro04",s_COLUMN_angulo);

            res.moveToNext();
        }
        return array_list;
    }
/*
* Outubro11
* */

    public String procurar_lamina_diretamente(Integer i) {

        String saida="";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" where length("+COLUMN_lamina_usada+")>0 order by id desc limit 10", null);

        res.moveToFirst();
        String s_COLUMN_lamina_usada="";

        int controle=0;
        while(res.isAfterLast() == false){
            s_COLUMN_lamina_usada=res.getString(res.getColumnIndex(COLUMN_lamina_usada));

            if(controle==i)
            {
                saida=s_COLUMN_lamina_usada;
            }

            controle++;
            //Log.d("Outubro04","numero da lista "+i);
            //Log.d("Outubro04",s_COLUMN_sigma_x);
            //Log.d("Outubro04",s_COLUMN_sigma_y);
            //Log.d("Outubro04",s_COLUMN_tau_xy);
            //Log.d("Outubro04",s_COLUMN_angulo);

            res.moveToNext();
        }
        return saida;
    }
    /////////////////////
}
