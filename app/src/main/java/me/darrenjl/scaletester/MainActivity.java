package me.darrenjl.scaletester;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Random;


public class MainActivity extends ActionBarActivity {

    private final int CHECK_CODE = 0x1;
    private final Random random = new Random();
    private final Activity mActivity = this;
    private Speaker speaker;
    private SpeakerTask speakerTask;
    private TextView numberText;
    private TextView delayText;
    private SeekBar seekBar;
    private int delayInSeconds = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numberText = (TextView) findViewById(R.id.textView);
        delayText = (TextView) findViewById(R.id.delay);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(4);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                delayInSeconds = progresValue + 1;
                delayText.setText(String.valueOf(delayInSeconds));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }


        });

        checkTTS();
    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            speaker.allow(true);
            speakerTask = new SpeakerTask();
            speakerTask.execute();
        } else {
            speakerTask.cancel(true);
            speakerTask = null;
            speaker.allow(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                speaker = new Speaker(this);
            } else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }

    private void checkTTS() {
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    private class SpeakerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            while (!this.isCancelled()) {
                final String num = String.valueOf(random.nextInt(10) + 1);
                speaker.speak(num);

                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        numberText.setText(num);
                    }
                });

                try {
                    Thread.sleep(delayInSeconds*1000, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
