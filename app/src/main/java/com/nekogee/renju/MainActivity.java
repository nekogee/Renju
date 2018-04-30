package com.nekogee.renju;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by hui jie on 2018/4/17.
 */
public class MainActivity extends AppCompatActivity {

    private Chess_Panel panel;
    private AlertDialog.Builder builder;
    private AlertDialog dialog1;
    private Button button_restart;
    private Button button_regret;
    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        size = 10;

        panel = (Chess_Panel)findViewById(R.id.main_panel);

        button_regret = findViewById(R.id.button_regret);
        button_restart = findViewById(R.id.button_restart);
        button_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.show();
                panel.restartGame();
            }
        });
        button_regret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panel.regret();
            }
        });

        final String items[] = {"黑方","白方"};
        dialog1 = new AlertDialog.Builder(this)
                //.setIcon(R.mipmap.icon)
                .setTitle("你选择")
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0: panel.isWhite = true; break;
                            case 1: panel.isWhite = false;
                            default:
                        }
                    }
                })
                /*.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })*/
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!panel.isWhite) {
                            panel.myBlackArray.add(0,new Point(size/2,size/2));
                            panel.update();
                        }
                        dialog.dismiss();
                    }
                }).create();
        dialog1.show();

        builder= new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("游戏结束");
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                MainActivity.this.finish();
            }
        });
        builder.setPositiveButton("再来一局", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface interface1, int which) {
                dialog1.show();

                panel.restartGame();
            }
        });

        panel.setOnGameListener(new Chess_Panel.onGameListener() {

            @Override
            public void onGameOver(int i) {
                String str = "";
                if (i== Chess_Panel.WHITE_WIN) {
                    str = "白方胜利！";
                }else if (i== Chess_Panel.BLACK_WIN) {
                    str = "黑方胜利！";
                }
                builder.setMessage(str);
                builder.setCancelable(false);//不可用返回键取消
                AlertDialog dialog = builder.create();
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.x = 0;
                params.y = panel.getUnder();
                dialogWindow.setAttributes(params);//设置Dialog显示的位置
                dialog.setCanceledOnTouchOutside(false);//不可点击取消
                dialog.show();
            }
        } );

    }
}