package br.com.ven2020.envelopes2018.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
	  private final static String TAG = "DatabaseHelper";
      private final Context myContext;

      private static final String DATABASE_NAME = "database.db";

    private static final int DATABASE_VERSION = 1;


      private String pathToSaveDBFile;
      public DatabaseHelper(Context context, String filePath) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.myContext = context;
            pathToSaveDBFile = new StringBuffer(filePath).append("/").append(DATABASE_NAME).toString();
      }
      public void prepareDatabase() throws IOException {
            boolean dbExist = checkDataBase();
            if(dbExist) {
                  int currentDBVersion = getVersionId();


                  if (DATABASE_VERSION > currentDBVersion) {
                      deleteDb();
                      try {
                    	  copyDataBase();
        			  } catch (IOException e) {
    				  }
                  }
            } else {
                try {
              	  copyDataBase();
  			    } catch (IOException e) {
				}
            }
      }
      private boolean checkDataBase() {
            boolean checkDB = false;
            try {
                File file = new File(pathToSaveDBFile);
                checkDB = file.exists();
            } catch(SQLiteException e) {
            }
            return checkDB;
      }
      private void copyDataBase() throws IOException {
            OutputStream os = new FileOutputStream(pathToSaveDBFile);
            InputStream is = myContext.getAssets().open("sqlite/"+DATABASE_NAME);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                  os.write(buffer, 0, length);
            }
            is.close();
            os.flush();
            os.close();
      }
      public void deleteDb() {
            File file = new File(pathToSaveDBFile);
            if(file.exists()) {
                  file.delete();
            }
      }
      @Override
      public void onCreate(SQLiteDatabase db) {
      }
      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      }

// Analise
// utubro03
//    No projeto anterior temos:
// D/Outubro03: SQLiteDatabase: /data/user/0/br.com.envelopes2017.envelopes2017/files/database.db
//  E neste temos:
//   D/Outubro03: SQLiteDatabase: /data/user/0/br.com.ven2020.envelopes2018/files/database.db


    private int getVersionId() {
  		SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);




        String query = "SELECT version_id FROM dbVersion";
  		Cursor cursor = db.rawQuery(query, null);
  		cursor.moveToFirst();
  		int v =  cursor.getInt(0);
  		db.close();
  		return v; 
  	  }

    /*
    * Junho 12
    * */

    public List<String> obter_nomes_laminas() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT nome FROM macro_laminas";
        Cursor cursor = db.rawQuery(query, null);

        List<String> list = new ArrayList<String>();

        while(cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        db.close();
        return list;
    }


        /*
    * Junho 14
    * */

    public List<String> obter_propriedades_laminas(String nome_lamina) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM macro_laminas where nome=\""+nome_lamina+"\"";
        Cursor cursor = db.rawQuery(query, null);

        List<String> list = new ArrayList<String>();
//Novembro
        while(cursor.moveToNext()) {
            list.add("id:  "+cursor.getString(0));
            list.add("nome: "+cursor.getString(1));//NOME`	TEXT NOT NULL,
            list.add("desidade:  "+Double.parseDouble(cursor.getString(2)) );//DENSIDADE`	TEXT NOT NULL,
            list.add("E1: "+Double.parseDouble(cursor.getString(3)) );//E1`	TEXT,
            list.add("E2: "+Double.parseDouble(cursor.getString(4)) );//E2`	TEXT,
            list.add("E3: "+Double.parseDouble(cursor.getString(5)));//E3`	TEXT,
            list.add("G12: "+Double.parseDouble(cursor.getString(6)));//G12`	TEXT,
            list.add("G13: "+Double.parseDouble(cursor.getString(7)));//G12`	TEXT,
            list.add("G23: "+Double.parseDouble(cursor.getString(8)));//G12`	TEXT,
            list.add("NU12: "+Double.parseDouble(cursor.getString(9)));//NU12`	TEXT,
            list.add("NU13: "+Double.parseDouble(cursor.getString(10)));//NU12`	TEXT,
            list.add("NU23: "+cursor.getString(11));//NU12`	TEXT,
            list.add("SIGMA_T_1: "+Double.parseDouble(cursor.getString(12)));//SIGMA_T_1`	TEXT,
            list.add("SIGMA_T_2: "+Double.parseDouble(cursor.getString(13)));//SIGMA_T_2`	TEXT,
            list.add("SIGMA_C_1: "+Double.parseDouble(cursor.getString(14)));//SIGMA_C_1`	TEXT,
            list.add("SIGMA_C_2: "+Double.parseDouble(cursor.getString(15)));//SIGMA_C_2`	TEXT,
            list.add("TAU12: "+Double.parseDouble(cursor.getString(16)));//TAU12`	TEXT,
            list.add("TAU13: "+Double.parseDouble(cursor.getString(17)));//TAU12`	TEXT,
            list.add("TAU23: "+cursor.getString(18));//TAU12`	TEXT,
            list.add("EPSILON_T_1:  "+Double.parseDouble(cursor.getString(19)));//EPSILON_T_1`	TEXT,
            list.add("EPSILON_T_2:  "+Double.parseDouble(cursor.getString(20)));//EPSILON_T_2`	TEXT,
            list.add("EPSILON_C_1:  "+Double.parseDouble(cursor.getString(21)));//EPSILON_C_1`	TEXT,
            list.add("EPSILON_C_2:  "+Double.parseDouble(cursor.getString(22)));//EPSILON_C_2`	TEXT,
            list.add("GAMMA12: "+Double.parseDouble(cursor.getString(23)));//GAMMA12`	TEXT,
            /*
            list.add( );//ALPHA1`	TEXT,
            list.add( );//ALPHA2`	TEXT,
            list.add( );//ALPHA3`	TEXT,
            list.add( );//BETA1`	TEXT,
            list.add( );//BETA2`	TEXT,
            list.add( );//BETA3`	TEXT,
            list.add( );//C`	TEXT,
            list.add( );//K1`	TEXT,
            list.add( );//K2`	TEXT,
            list.add( );//K3`	TEXT,
            list.add( );//user_id`	integer
                        */
        }
        db.close();
        return list;
    }
/*
* Outubro06
* */

    public String obter_identificador_laminas(String nome_lamina) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "SELECT id FROM macro_laminas where nome=\""+nome_lamina+"\"";
        Cursor cursor = db.rawQuery(query, null);

        String identificador="";

        while(cursor.moveToNext()) {
            identificador=cursor.getString(0);
        }
        db.close();
        return identificador;
    }



    //////////////
}