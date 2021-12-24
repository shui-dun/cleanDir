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

        if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);
        }
    }

    // 点击开始清理
    public void onClick(View v) {
        // 很多软件把保存的图片放在这里
        clearDirWithoutDirs("/storage/emulated/0/Pictures");
        // telegram的图片以及文件
        clearDirWithoutDirs("/storage/emulated/0/Telegram");
        // 腾讯的文件基本上看到私有目录下的，但也有一点放在这里
        clearDirWithoutDirs("/storage/emulated/0/Tencent");
        // 截图
        clearDir("/storage/emulated/0/DCIM/Screenshots");
        // 截屏
        clearDir("/storage/emulated/0/DCIM/ScreenRecorder");
        // 下载目录
        clearDir("/storage/emulated/0/Download");
        // 有一些app将保存的图片直接放在DCIM下，比如淘宝
        clearShallowDir("/storage/emulated/0/DCIM");
        Toast.makeText(getApplicationContext(), "清理结束", Toast.LENGTH_SHORT).show();
    }

    // 清空文件夹，但只删除文件，不删除文件夹
    private boolean clearDirWithoutDirs(String filePath) {
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            Toast.makeText(getApplicationContext(), "清空目录失败：" + dirFile + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            for (File file : dirFile.listFiles()) {
                if (file.isFile()) {
                    if (!deleteSingleFile(file.getAbsolutePath())) {
                        return false;
                    }
                } else if (file.isDirectory()) {
                    if (!clearDirWithoutDirs(file.getAbsolutePath())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // 清空文件夹
    private boolean clearDir(String filePath) {
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
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
            Toast.makeText(getApplicationContext(), "清空目录失败！", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "删除文件" + filePath$Name + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "删除文件失败：" + filePath$Name + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 删除目录
    private boolean deleteDir(String filePath) {
        if (!clearDir(filePath)) {
            return false;
        }
        File dirFile = new File(filePath);
        if (dirFile.delete()) {
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "删除目录：" + filePath + "失败！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 只删除一级目录下的文件
    private boolean clearShallowDir(String filePath) {
        File dirFile = new File(filePath);
        File[] files = dirFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                if (!deleteSingleFile(file.getAbsolutePath())) {
                    return false;
                }
            }
        }
        return true;
    }
}