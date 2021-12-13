package com.sd.cleardir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Environment.isExternalStorageManager()) {
            Toast.makeText(this, "已获得访问所有文件权限", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);
        }
    }

    // 点击开始清理
    public void onClick(View v) {
        clearDir("/storage/emulated/0/Download");
        clearDir("/storage/emulated/0/DCIM/Screenshots");
        clearDir("/storage/emulated/0/Pictures");
        clearDir("/storage/emulated/0/Tencent/QQ_Images");
        Toast.makeText(getApplicationContext(), "清理结束", Toast.LENGTH_SHORT).show();
    }

    // 清空文件夹
    private boolean clearDir(String filePath) {
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            Toast.makeText(getApplicationContext(), "清空目录失败：" + filePath + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            } else if (file.isDirectory()) {
                flag = deleteDir(file.getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            Toast.makeText(getApplicationContext(), "删除目录失败！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // 删除单个文件
    private boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "删除单个文件" + filePath$Name + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "删除单个文件失败：" + filePath$Name + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 删除目录
    private boolean deleteDir(String filePath) {
        if (!clearDir(filePath)) {
            return false;
        }
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        if (dirFile.delete()) {
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "删除目录：" + filePath + "失败！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}