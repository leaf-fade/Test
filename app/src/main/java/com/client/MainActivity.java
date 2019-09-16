package com.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.client.aop.AddValue;
import com.client.ui.activity.SkinActivity;
import com.client.ui.bean.User;
import com.dblib.db.BaseDao;
import com.dblib.db.BaseDaoFactory;
import com.test_annotation.PrintMe;
import com.test_annotation.UseNum;

import java.util.List;

@PrintMe(value = "这是个测试")
public class MainActivity extends AppCompatActivity {
    //private MyAidl myAidl;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("123", "onCreate: ================");
        bindService();
    }

    private void bindService(){
        Intent intent = new Intent();
        //服务端的aidl目录必须和客户端一致
        //服务端的包名及具体的aidl service
       /* intent.setComponent(new ComponentName(
                "com.service",
                "com.service.MyAidlService"));

        Log.i("123", "onCreate: =======绑定========="+bindService(intent, conn, Context.BIND_AUTO_CREATE));*/
    }

    /*private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("123", "onCreate: =======执行连接=========");
            //myAidl = MyAidl.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };*/

    @AddValue("测试")
    public void click(View view) {
        //myAidl.addPerson(new Person("dn", 10));
        //List<Person> people = myAidl.getPersonList();
        //Toast.makeText(this, people.toString(), Toast.LENGTH_SHORT).show();
        UseNum.use(this.getClass());
        //insert();
    }

    private void insert() {
        BaseDao<User> baseDao = BaseDaoFactory.getOurInstance().getBaseDao(User.class);
        baseDao.insert(new User(1, "alan", "123"));
        Toast.makeText(this, "执行成功！", Toast.LENGTH_SHORT).show();
    }

    private void delete() {
        BaseDao<User> baseDao = BaseDaoFactory.getOurInstance().getBaseDao(User.class);
        User where = new User(1, "alan", "123");
        int delete = baseDao.delete(where);
        Toast.makeText(this, "执行成功！", Toast.LENGTH_SHORT).show();
    }

    private void update() {
        BaseDao<User> baseDao = BaseDaoFactory.getOurInstance().getBaseDao(User.class);
        User user = new User(1, "alan", "123");
        User where = new User(2, "lalala", "123");
        long update = baseDao.update(user,where);
        Toast.makeText(this, "执行成功！", Toast.LENGTH_SHORT).show();
    }

    private void query() {
        BaseDao<User> baseDao = BaseDaoFactory.getOurInstance().getBaseDao(User.class);
        User user = new User();
        user.setId(1);
        List<User> querys = baseDao.query(user);
        Toast.makeText(this, "执行成功！", Toast.LENGTH_SHORT).show();
    }
    /**
     * 进入换肤
     *
     * @param view
     */
    public void skinSelect(View view) {
        startActivity(new Intent(this, SkinActivity.class));
    }
}
