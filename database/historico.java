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


public class historico extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "historico.db";
    public static final String CONTACTS_TABLE_NAME = "historico";
//Colunas:
    public static final String CONTACTS_COLUMN_id = "id";
    public static final String CONTACTS_COLUMN_lamina_usada = "lamina_usada";
    public static final String CONTACTS_COLUMN_envelope_usado = "envelope_usado";
    public static final String CONTACTS_COLUMN_criterio_usado = "criterio_usado";
    public static final String CONTACTS_COLUMN_DATA = "data";


    private HashMap hp;

    public historico(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String query_creater="create table historico " +
                "(" +
                "id integer primary key, " +
                "lamina_usada text," +
                "envelope_usado text," +
                "criterio_usado text," +
                "data text " +
                ")";

        db.execSQL(query_creater);
//maioDETALHES
        query_creater="create table angulosPARAMETROS " +
                "(" +
                "id integer primary key, " +
                "lamina_usada text," +
                "envelope_usado text," +
                "angulo text," +
                "parametroTAUXY text," +
                "parametroGAMMAXY text," +
                "parametroBIAXIAL text," +
                "parametroTAU23 text," +
                "data text " +
                ")";

        db.execSQL(query_creater);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS historico");
        db.execSQL("DROP TABLE IF EXISTS angulosPARAMETROS");
        onCreate(db);
    }

// Outubro04

    public boolean insert_envelope_usado(String envelope_usado, String data)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_envelope_usado, envelope_usado);
        contentValues.put("data", data);
        db.insert("historico", null, contentValues);
        return true;
    }

// Maio10

    public boolean insert_envelope_paramestros_usados
            (
    String lamina_usada,
    String envelope_usado,
    String angulo,
    String parametroTAUXY,
    String parametroGAMMAXY,
    String parametroBIAXIAL
            )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_lamina_usada, lamina_usada);
        contentValues.put(CONTACTS_COLUMN_envelope_usado, envelope_usado);
        contentValues.put("angulo", angulo);
        contentValues.put("parametroTAUXY", parametroTAUXY);
        contentValues.put("parametroGAMMAXY", parametroGAMMAXY);
        contentValues.put("parametroBIAXIAL", parametroBIAXIAL);
        db.insert("angulosPARAMETROS", null, contentValues);
        return true;
    }

    public long getProfilesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "angulosPARAMETROS");
        db.close();
        return count;
    }

    // Junho 14

    public boolean insert_lamina_usada (String lamina_usada, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("lamina_usada", lamina_usada);
        contentValues.put("data", data);
        db.insert("historico", null, contentValues);
        return true;
    }

    // Junho 14

    public boolean insert_criterio_usado(String criterio_usado, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_criterio_usado, criterio_usado);
        contentValues.put("data", data);
        db.insert("historico", null, contentValues);
        return true;
    }

/*
* Junho 09
* Colocamos e, forma descendente, justamente para que tenha alguma utilidade
* */

    public ArrayList<String> obterProcuraHistorico() {

        ArrayList<String> array_list = new ArrayList<String>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        //Cursor res =  db.rawQuery( "select DISTINCT "+CONTACTS_COLUMN_lamina_usada+" from historico uniq", null);
        Cursor res =  db.rawQuery( "" +
                "select DISTINCT "+CONTACTS_COLUMN_lamina_usada+" from historico uniq where length("+CONTACTS_COLUMN_lamina_usada+")>0 order by id desc", null);

        /// order by id desc limit 1"

        res.moveToFirst();
    //colocamos apenas uma unica especialidade:

        String data="";
        String lamina_usada="";
        int i=0;

        while(res.isAfterLast() == false){

            //Integer id=res.getInt(res.getColumnIndex(CONTACTS_COLUMN_id));
            lamina_usada=res.getString(res.getColumnIndex(CONTACTS_COLUMN_lamina_usada));

            int Comprimento_lamina_usada=0;
            try{
                Comprimento_lamina_usada=lamina_usada.length();
            }
            catch (Error e)
            {

            }

            if(Comprimento_lamina_usada!=0)
            {
                array_list.add(lamina_usada);
            }
            //array_list.add(""+id);
            //Log.d("id ","id "+id+"   "+i);
            res.moveToNext();
        }
        return array_list;
    }
/*
* Junho, 12
* */
    public void delete_id(String IdIntDbprocuras)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] remover_ids={IdIntDbprocuras};

        db.delete("historico","id = ? ",remover_ids);
    }

    /*
    * Junho 14
    * */

    public String ultima_lamina_usada () {

        SQLiteDatabase db = this.getReadableDatabase();

        //Cursor res =  db.rawQuery( "select lamina_usada  from historico order by id desc limit 1", null );
        Cursor res =  db.rawQuery(
                "select "+CONTACTS_COLUMN_lamina_usada+"  from historico  where length("+CONTACTS_COLUMN_lamina_usada+")>0 order by id desc limit 1"
                , null );
        res.moveToFirst();
        String lamina_usada="";

        try {
            lamina_usada = res.getString(res.getColumnIndex(CONTACTS_COLUMN_lamina_usada));
        }catch (Exception e) {
        }


        return lamina_usada;
    }


    /*
    * Outubro04
    * */

    public String ultima_envelope_usado () {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery(
"select "+CONTACTS_COLUMN_envelope_usado+"  from historico  where length("+CONTACTS_COLUMN_envelope_usado+")>0 order by id desc limit 1"
                , null );

        res.moveToFirst();
        String envelope_usado="";

        try {
            envelope_usado = res.getString(res.getColumnIndex(CONTACTS_COLUMN_envelope_usado));
        }catch (Exception e) {
        }
        return envelope_usado;
    }

    /*
    * Outubro04
    * */

    public String ultima_criterio_usado () {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery(
                "select "+CONTACTS_COLUMN_criterio_usado+"  from historico  where length("+CONTACTS_COLUMN_criterio_usado+")>0 order by id desc limit 1"
                , null );

        res.moveToFirst();
        String envelope_usado="";

        try {
            envelope_usado = res.getString(res.getColumnIndex(CONTACTS_COLUMN_criterio_usado));
        }catch (Exception e) {
        }
        return envelope_usado;
    }

        /*
    * Maio 10, 2018
    * */

public ArrayList<String> recuperar_angulo_parametro_envelope () {

    ArrayList<String> lamina_angulo_tauxy_envelope=new ArrayList<>();

    SQLiteDatabase db = this.getReadableDatabase();
    Cursor res =  db.rawQuery(
    "select *  from angulosPARAMETROS order by id desc limit 1"
    , null );
    res.moveToFirst();
    String lamina_usada="";

    try {
            lamina_usada = res.getString(res.getColumnIndex(CONTACTS_COLUMN_lamina_usada));
    }catch (Exception e) {
    }

    String angulo_usado="";

    try {
        angulo_usado = res.getString(res.getColumnIndex("angulo"));
    }catch (Exception e) {
    }

    String parametroTAUXY="";

    try {
        parametroTAUXY = res.getString(res.getColumnIndex("parametroTAUXY"));
    }catch (Exception e) {
        //Log.e("lamina_usada","lamina_usada "+lamina_usada);
    }

    String envelope="";

    try {
        envelope = res.getString(res.getColumnIndex(CONTACTS_COLUMN_envelope_usado));
    }catch (Exception e) {
        //Log.e("lamina_usada","lamina_usada "+lamina_usada);
    }

    String parametroBIAXIAL="";

    try {
        parametroBIAXIAL = res.getString(res.getColumnIndex("parametroBIAXIAL"));
    }catch (Exception e) {
        //Log.e("lamina_usada","lamina_usada "+lamina_usada);
    }

    if(lamina_usada.length()!=0)
        lamina_angulo_tauxy_envelope.add(lamina_usada);

    if(angulo_usado.length()!=0)
        lamina_angulo_tauxy_envelope.add(angulo_usado);

    if(parametroTAUXY.length()!=0)
        lamina_angulo_tauxy_envelope.add(parametroTAUXY);

    if(envelope.length()!=0)
        lamina_angulo_tauxy_envelope.add(envelope);

    if(parametroBIAXIAL.length()!=0)
        lamina_angulo_tauxy_envelope.add(parametroBIAXIAL);


    return lamina_angulo_tauxy_envelope;
}

    /////////////////////
}
