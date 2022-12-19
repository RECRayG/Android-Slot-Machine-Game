package com.example.azinothreeaxes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    interface ChangeListener {
        void newImage(int img);
    }

    public static final Random RANDOM = new Random();
    private boolean changeStarted;
    private Change change1, change2, change3;
    public static long randomLong(long lower, long upper) {
        return lower + (long) (RANDOM.nextDouble() * (upper - lower));
    }

    private ImageView azino_down;
    private ImageView azino_up;
    private ImageView azino;
    private ImageView azino_lantern;
    private ImageView icon_1;
    private ImageView icon_2;
    private ImageView icon_3;
    private ImageButton start_azino;

    private ImageView you_lose;
    private ImageView little_win;
    private ImageView big_win;

    private Timer timer;
    private Timer timerChangeLight;

    private int icon_1n;
    private int icon_2n;
    private int icon_3n;
    private boolean icon_1b;
    private boolean icon_2b;
    private boolean icon_3b;
    private int count_scroll;

    private int period;
    private int change_speed;
    private  int time;

    private boolean little_win_anim = false;
    private boolean you_lose_anim = false;
    private boolean big_win_anim = false;

    private int colorLight;

    public class Change extends Thread {
        private int[] icons = {R.drawable.icon_axe, R.drawable.icon_bar, R.drawable.icon_bell,
                                R.drawable.icon_lemon, R.drawable.icon_cherry};
        public int currentIndex;
        private ChangeListener changeListener;
        private long frameDuration;
        private long startIn;
        private boolean isStarted;

        public Change (ChangeListener changeListener, long frameDuration, long startIn) {
            this.changeListener = changeListener;
            this.frameDuration = frameDuration;
            this.startIn = startIn;
            currentIndex = 0;
            isStarted = true;
        }

        public void nextImg() {
            currentIndex = (int)(Math.random() * icons.length);


            if(icon_1b)
                change1.stopChange();

            if(icon_2b)
                change2.stopChange();

            if(icon_3b)
                change3.stopChange();
        }

        @Override
        public void run() {
            try {
                Thread.sleep(startIn);
            } catch (InterruptedException e) {
            }

            while (isStarted) {
                try {
                    Thread.sleep(frameDuration);
                } catch (InterruptedException e) {
                }

                nextImg();
                this.frameDuration = change_speed;

                if (changeListener != null) {
                    changeListener.newImage(icons[currentIndex]);
                }
            }
        }

        public void stopChange() {
            isStarted = false;

            // Если завершилась анимация последней иконки
            if(this.equals(change3)) {
                start_azino.startAnimation(AnimationUtils.loadAnimation(start_azino.getContext(), R.anim.appear));
                azino.setImageDrawable(getResources().getDrawable(R.drawable.azino));

                azino_down.startAnimation(AnimationUtils.loadAnimation(azino_down.getContext(), R.anim.fade));
                azino_up.startAnimation(AnimationUtils.loadAnimation(azino_up.getContext(), R.anim.fade));

                timerChangeLight.cancel();
                timerChangeLight.purge();
                timerChangeLight = new Timer();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                azino_lantern.startAnimation(AnimationUtils.loadAnimation(azino_lantern.getContext(), R.anim.appear));
                                switch (colorLight) {
                                    case 1:
                                        azino_lantern.setImageDrawable(getResources().getDrawable(R.drawable.azino_whait_yellow));
                                        colorLight = 2;
                                        break;
                                    case 2:
                                        azino_lantern.setImageDrawable(getResources().getDrawable(R.drawable.azino_whait_red));
                                        colorLight = 3;
                                        break;
                                    case 3:
                                        azino_lantern.setImageDrawable(getResources().getDrawable(R.drawable.azino_whait_green));
                                        colorLight = 1;
                                        break;
                                }

                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        azino_lantern.startAnimation(AnimationUtils.loadAnimation(azino_lantern.getContext(), R.anim.fade));
                                    }
                                }, 1500);
                            }
                        });
                    }
                }, 0, 3000);

                if (change1.currentIndex == change2.currentIndex && change2.currentIndex == change3.currentIndex) {
                    big_win.startAnimation(AnimationUtils.loadAnimation(big_win.getContext(), R.anim.appear));
                    big_win_anim = true;
                    little_win_anim = false;
                    you_lose_anim = false;
                } else if (change1.currentIndex == change2.currentIndex || change2.currentIndex == change3.currentIndex
                        || change1.currentIndex == change3.currentIndex) {
                    little_win.startAnimation(AnimationUtils.loadAnimation(little_win.getContext(), R.anim.appear));
                    big_win_anim = false;
                    little_win_anim = true;
                    you_lose_anim = false;
                } else {
                    you_lose.startAnimation(AnimationUtils.loadAnimation(you_lose.getContext(), R.anim.appear));
                    big_win_anim = false;
                    little_win_anim = false;
                    you_lose_anim = true;
                }

                changeStarted = false;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeStarted = false;

        azino_down = findViewById(R.id.azino_down);
        azino_up = findViewById(R.id.azino_up);

        azino = findViewById(R.id.azino);
        azino_lantern = findViewById(R.id.azino_lantern);

        you_lose = findViewById(R.id.you_lose);
        little_win = findViewById(R.id.little_win);
        big_win = findViewById(R.id.big_win);

        icon_1 = findViewById(R.id.icon_1);
        icon_2 = findViewById(R.id.icon_2);
        icon_3 = findViewById(R.id.icon_3);

        start_azino = findViewById(R.id.start_azino);

        icon_1n = 1;
        icon_2n = 2;
        icon_3n = 3;

        colorLight = 1;

        // Таймер, запускающий анимацию ожидания
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       azino_lantern.startAnimation(AnimationUtils.loadAnimation(azino_lantern.getContext(), R.anim.appear));
                       switch (colorLight) {
                           case 1:
                               azino_lantern.setImageDrawable(getResources().getDrawable(R.drawable.azino_whait_yellow));
                               colorLight = 2;
                               break;
                           case 2:
                               azino_lantern.setImageDrawable(getResources().getDrawable(R.drawable.azino_whait_red));
                               colorLight = 3;
                               break;
                           case 3:
                               azino_lantern.setImageDrawable(getResources().getDrawable(R.drawable.azino_whait_green));
                               colorLight = 1;
                               break;
                       }

                       Handler handler = new Handler();
                       handler.postDelayed(new Runnable() {
                           public void run() {
                               azino_lantern.startAnimation(AnimationUtils.loadAnimation(azino_lantern.getContext(), R.anim.fade));
                           }
                       }, 1500);
                   }
                });
            }
        }, 0, 3000);

        start_azino.setOnClickListener(action -> {
                if(!changeStarted) {
                    if(big_win_anim) {
                        big_win.startAnimation(AnimationUtils.loadAnimation(big_win.getContext(), R.anim.fade));
                    }
                    if(little_win_anim) {
                        little_win.startAnimation(AnimationUtils.loadAnimation(little_win.getContext(), R.anim.fade));
                    }
                    if(you_lose_anim) {
                        you_lose.startAnimation(AnimationUtils.loadAnimation(you_lose.getContext(), R.anim.fade));
                    }

                    timer.cancel();
                    timer.purge();
                    timer = new Timer();

                    azino_lantern.clearAnimation();

                    time = 0;
                    count_scroll = (int)randomLong(8, 16); // Время прокрутки
                    period = 200; // Шаг времени анимации
                    change_speed = 100; // Начальная скорость анимации (для последующих /2)

                    icon_1b = false;
                    icon_2b = false;
                    icon_3b = false;

                    start_azino.startAnimation(AnimationUtils.loadAnimation(start_azino.getContext(), R.anim.fade_faster));
                    azino.setImageDrawable(getResources().getDrawable(R.drawable.azino_go));

                    azino_down.startAnimation(AnimationUtils.loadAnimation(azino_down.getContext(), R.anim.appear));
                    azino_up.startAnimation(AnimationUtils.loadAnimation(azino_up.getContext(), R.anim.appear));

                    azino_lantern.startAnimation(AnimationUtils.loadAnimation(azino_lantern.getContext(), R.anim.appear_faster));
                    azino_lantern.setImageDrawable(getResources().getDrawable(R.drawable.azino_whait_yellow));


                    change1 = new Change(new ChangeListener() {
                        @Override
                        public void newImage(final int img) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    icon_1.setImageResource(img);
                                }
                            });
                        }
                    }, change_speed, randomLong(0, 200));

                    change1.start();

                    change2 = new Change(new ChangeListener() {
                        @Override
                        public void newImage(final int img) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    icon_2.setImageResource(img);
                                }
                            });
                        }
                    }, change_speed, randomLong(0, 200));

                    change2.start();

                    change3 = new Change(new ChangeListener() {
                        @Override
                        public void newImage(final int img) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    icon_3.setImageResource(img);
                                }
                            });
                        }
                    }, change_speed, randomLong(0, 200));

                    change3.start();

                    timerChangeLight = new Timer();
                    timerChangeLight.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(changeStarted) {
                                time += period;
                                if (!icon_1b) {
                                    if (!(count_scroll > time / 1000)) {
                                        icon_1b = true;
                                        change_speed *= 2;
                                        time += period * 1.5;
                                    }
                                }
                                if (!icon_2b) {
                                    if (!(count_scroll * 2 > time / 1000)) {
                                        icon_2b = true;
                                        change_speed *= 2;
                                        time += period * 2.5;
                                    }
                                }
                                if (!icon_3b) {
                                    if (!(count_scroll * 3 > time / 1000)) {
                                        icon_3b = true;
                                    }
                                }
                            }
                        }
                    }, 0, period);

                    changeStarted = true;
                }
        });
    }
}