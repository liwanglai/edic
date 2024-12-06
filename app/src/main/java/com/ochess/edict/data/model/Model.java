package com.ochess.edict.data.model;

import androidx.lifecycle.ViewModel;
import androidx.room.RoomDatabase;

import com.ochess.edict.data.Db;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class Model  extends ViewModel {
    private String _table;
    protected RoomDatabase db = Db.user;
    protected String tableName(){
        if(_table!=null) return _table;
        String tabName=this.getClass().getCanonicalName();
        String[] a=tabName.split("\\.");
        _table=a[a.length-1];
        _table = _table.replaceAll("([A-Z])","_$1");
        _table = _table.toLowerCase();
        if(_table.startsWith("_")){
            _table = _table.substring(1,_table.length());
        }
        return _table;
    }
    public Query getQuery(){
        return new Query(db,tableName());
    }
    protected void exec(Function0<Unit> function) {
        new Thread(()->{
            function.invoke();
        }).start();
    }

}
