package com.purehero.myndkapp00;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.purehero.myndkapp00.databinding.ActivityMainBinding;
import com.purehero.myndkapp00.dialog.AppSealingAlertDialog;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyApp00";

    private NativeLibrary nativeLibrary = new NativeLibrary();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d( TAG, "MainActivity::onCreate()");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE); // 화면 캡쳐 방지
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText( nativeLibrary.stringFromJNI());

        int btnIDs[] = {
            R.id.MyButton00, R.id.MyButton01, R.id.MyButton02,
            R.id.btn_adb_enabled_update, R.id.btn_development_enabled_update, R.id.btn_debugger_connected_update,
            R.id.btn_turn_off_usb_debugging, R.id.btn_open_development_options
        };
        for( int btnID : btnIDs ) {
            Button btn = findViewById(btnID);
            if (btn != null) {
                btn.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View view) {
        TextView tv = null;

        switch( view.getId()) {
            case R.id.MyButton00:
                AppSealingAlertDialog.showAlertDialog(
                        this,
                        AppSealingAlertDialog.DIALOG_TYPE_TOAST,
                        "[70007] [Auto Clicker for IDLE Game] tool detected. Please delete the tool and re-launch the application. (Tool can be removed in Settings > Apps)",
                        false );
                break;
            case R.id.MyButton01:
                AppSealingAlertDialog.showAlertDialog(
                        this,
                        AppSealingAlertDialog.DIALOG_TYPE_ALERT,
                        "[70007] [Auto Clicker for IDLE Game] 툴이 감지되었습니다. 해당 툴 삭제 후 앱을 다시 실행해 주시기 바랍니다. (설정의 앱관리에서 삭제할 수 있습니다)",
                        false );
                break;
            case R.id.MyButton02:
                AppSealingAlertDialog.showAlertDialog(
                        this,
                        AppSealingAlertDialog.DIALOG_TYPE_ALERT_TIMER,
                        "[70007] [Auto Clicker for IDLE Game] 툴이 감지되었습니다. 해당 툴 삭제 후 앱을 다시 실행해 주시기 바랍니다. (설정의 앱관리에서 삭제할 수 있습니다)",
                        true );
                Log.d( TAG, "Button clicked : " + ((Button) view ).getText());
                Utils.killMyProcess(4);
                break;

            case R.id.btn_adb_enabled_update :
                tv = (TextView) findViewById( R.id.tv_adb_enabled );
                if( tv != null ) {
                    tv.setText( getString( R.string.usb_debugging) + " : "+ Settings.Secure.getInt( this.getContentResolver(), Settings.Global.ADB_ENABLED, 0 ) );
                }
                break;

            case R.id.btn_development_enabled_update :
                tv = (TextView) findViewById( R.id.tv_development_enabled );
                if( tv != null ) {
                    tv.setText( getString( R.string.development_option) + " : " + Settings.Secure.getInt( this.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0 ) );
                }
                break;

            case R.id.btn_debugger_connected_update :
                tv = (TextView) findViewById( R.id.tv_debugger_connected );
                if( tv != null ) {
                    tv.setText( getString( R.string.debugger_connected) + " : " + Debug.isDebuggerConnected() );
                }
                break;

            case R.id.btn_turn_off_usb_debugging:
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if( Settings.System.canWrite(this)) {
                            Settings.Secure.putInt(this.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
                        } else {
                            Toast.makeText( this, "Settings 쓰기 권한이 없습니다.", Toast.LENGTH_LONG ).show();
                        }
                    } else {
                        Settings.Secure.putInt(this.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
                    }

                    Process process = Runtime.getRuntime().exec( "settings put global adb_enabled 0" );
                    process.waitFor();

                } catch (IOException e) {
                    e.printStackTrace();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_open_development_options :
                startActivity( new Intent( Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                break;
        }
    }
}