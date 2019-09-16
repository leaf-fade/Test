package com.dblib.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.dblib.annotation.DbField;
import com.dblib.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.*;

public class BaseDao<T> implements IBaseDao<T>{

    //持有数据库操作的引用
    private SQLiteDatabase sqLiteDatabase;

    //表名
    private String tableName;

    //持有操作数据库所对应的java类型
    private Class<T> entityClass;

    //标记：用来表示是否做过初始化操作
    private boolean isInit = false;

    //定义一个缓存空间（key-字段名   value-成员变量）
    private HashMap<String, Field> cacheMap;

    protected void init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass){
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;

        if(!isInit){
            if(entityClass.getAnnotation(DbTable.class) == null){
                this.tableName = entityClass.getSimpleName();
            }else {
                this.tableName = entityClass.getAnnotation(DbTable.class).value();
            }

            //执行建表操作
            //create table if not exists
            // tb_user(_id integer, name varchar(20), password varchar(20))
            String createTableSql = getCreateTableSql();
            this.sqLiteDatabase.execSQL(createTableSql);
            cacheMap  = new HashMap<>();
            initCacheMap();
            isInit = true;

        }
    }

    private void initCacheMap() {
        //1、取得所有字段名
        String sql = "select * from " + tableName + " limit 1, 0";  //空表
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        //2、取所有的成员变量
        Field[] declaredFields = entityClass.getDeclaredFields();
        //把所有字段的访问权限打开
        for (Field f: declaredFields) {
            f.setAccessible(true);
        }

        //表的列名和属性一一对应
        for(String columnName : columnNames){
            Field columnField = null;
            for(Field field : declaredFields){
                String fieldName;  //对象中的成员变量名字
                if(field.getAnnotation(DbField.class) != null){
                    fieldName = field.getAnnotation(DbField.class).value();
                } else {
                    fieldName = field.getName();
                }

                if(columnName.equals(fieldName)){  //匹配
                    columnField = field;
                    break;
                }
            }

            if(columnField != null){
                cacheMap.put(columnName, columnField);
            }
        }

        cursor.close();
    }

    private String getCreateTableSql() {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(tableName).append("(");
        //反射得到所有的成员变量
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            Class type = field.getType();// 拿到成员的类型

            if(field.getAnnotation(DbField.class) != null){
                //通过注解获取
                if(type == String.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()).append(" TEXT,");
                } else  if(type == Integer.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()).append(" INTEGER,");
                }else  if(type == Long.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()).append(" BIGINT,");
                }else  if(type == Double.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()).append(" DOUBLE,");
                }else  if(type == byte[].class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()).append(" BLOB,");
                }
            } else {
                //通过反射获取
                if(type == String.class){
                    stringBuffer.append(field.getName()).append(" TEXT,");
                } else  if(type == Integer.class){
                    stringBuffer.append(field.getName()).append(" INTEGER,");
                }else  if(type == Long.class){
                    stringBuffer.append(field.getName()).append(" BIGINT,");
                }else  if(type == Double.class){
                    stringBuffer.append(field.getName()).append(" DOUBLE,");
                }else  if(type == byte[].class){
                    stringBuffer.append(field.getName()).append(" BLOB,");
                }
            }
        }
        //去掉多余的逗号
        if(stringBuffer.charAt(stringBuffer.length()  - 1) == ','){
            stringBuffer.deleteCharAt(stringBuffer.length()  - 1);
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    @Override
    public long insert(T entity) {
        //1、准本好ContentValues中需要的数据
        Map<String, String> map = getValues(entity);

        //2、把数据转移到ContentValues
        ContentValues values = getContentValues(map);
        //3、开始插入
        sqLiteDatabase.insert(tableName, null, values);
        return 0;
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            String value = map.get(key);
            if (value != null) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }

    private Map<String, String> getValues(T entity) {
        HashMap<String, String> map = new HashMap<>();
        //返回的是所有的成员变量
        for (Field field : cacheMap.values()) {
            field.setAccessible(true);
            try {
                //获取对象的属性值
                Object obj = field.get(entity);
                if (obj == null) {
                    continue;
                }
                String value = obj.toString();
                String key;
                if (field.getAnnotation(DbField.class) != null) {
                    key = field.getAnnotation(DbField.class).value();
                } else {
                    key = field.getName();
                }

                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    public long update(T entity, T where) {
        // sqLiteDatabase.update(tableName, contentValue, "id = ?", new String[]{"1"});
        int result;
        //1、准本好ContentValues中需要的数据
        Map<String, String> values = getValues(entity);
        ContentValues contentValues = getContentValues(values);
        //条件map
        Map<String, String> values1 = getValues(where);
        Condition condition = new Condition(values1);
        result = sqLiteDatabase.update(tableName, contentValues, condition.whereClause, condition.whereArgs);
        return result;
    }

    @Override
    public int delete(T where) {
        //        sqLiteDatabase.delete(tableName,"id = ?", new String[]{"1"} );
        int result;
        //1、准本好ContentValues中需要的数据
        Map<String, String> values = getValues(where);
        Condition condition = new Condition(values);
        result = sqLiteDatabase.delete(tableName, condition.whereClause, condition.whereArgs);
        return result;
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        //        sqLiteDatabase.query(tableName, null, "id = ?",
//                new String[], null. null, orderBy, "1, 5");
        //1、准本好ContentValues中需要的数据
        Map<String, String> values = getValues(where);

        String limitString = "";   //"2,6"
        if(startIndex != null && limit != null){
            limitString = startIndex + " , " + limit;
        }

        Condition condition = new Condition(values);

        Cursor query = sqLiteDatabase.query(tableName, null, condition.whereClause,
                condition.whereArgs, null, orderBy, limitString);

        List<T> result = getResult(query, where);  //游标  --- 》 javabean  --- list<javabaen>
        return result; 
    }

    private List<T> getResult(Cursor query, T where) {
        ArrayList list = new ArrayList();
        Object item = null;
        while (query.moveToNext()){
            try {
                item = where.getClass().newInstance();  //因为不知道 new  ? , 所以通过反射方式
                //cacheMap  (字段---成员变量的名字)
                for (Map.Entry<String, Field> entry : cacheMap.entrySet()) {
                    //取列名
                    String columnName = entry.getKey();

                    //以列名拿到列名在游标中的位置
                    int columnIndex = query.getColumnIndex(columnName);

                    Field value = entry.getValue();   //id
                    Class<?> type = value.getType();  //Integer
                    if (columnIndex != -1) {  //columnName = "age"
                        if (type == String.class) {
                            value.set(item, query.getString(columnIndex));//setid(1)
                        } else if (type == Double.class) {
                            value.set(item, query.getDouble(columnIndex));
                        } else if (type == Integer.class) {
                            value.set(item, query.getInt(columnIndex));
                        } else if (type == Long.class) {
                            value.set(item, query.getLong(columnIndex));
                        } else if (type == byte[].class) {
                            value.set(item, query.getBlob(columnIndex));
                        }
                    }
                }
                list.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        query.close();
        return list;
    }


    private class Condition {
        //where 1=1 代表永真，即使后面条件出错也不会报错    where 1=0 代表永假，只返回一个表头
        private String whereClause;  //1=1 and password = ？  --- >password = ？
        private String[] whereArgs;

        /**
         * new Person(1, "alan", "123")
         */
        public Condition(Map<String, String> whereClause ) {
            ArrayList<String> list = new ArrayList<>();  //whereArgs里面的内容存入的list
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append("1=1");
            //取得所有成员变量的名字
            Set<String> keys = whereClause.keySet();
            for (String key : keys) {
                String value = whereClause.get(key);
                if (value != null) {
                    stringBuffer.append(" and ").append(key).append("=?");
                    list.add(value);
                }
            }

            this.whereClause = stringBuffer.toString();
            this.whereArgs = list.toArray(new String[list.size()]);
        }
    }
}
