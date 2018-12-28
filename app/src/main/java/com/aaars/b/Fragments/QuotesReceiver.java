package com.aaars.b.Fragments;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.aaars.b.GMailSender;
import com.aaars.b.Module;
import com.aaars.b.R;
import com.aaars.b.Splash;
import com.aaars.b.Triggers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import static com.aaars.b.Root.USER_ID;

public class QuotesReceiver extends BroadcastReceiver {

    Module md, md2, md3;

    @Override
    public void onReceive(final Context context, Intent intent) {
        md = new Module();
        md2 = new Module();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference drmd2 = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("5");
        ValueEventListener wifiTimerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md2 = dataSnapshot.getValue(Module.class);
                    quotesEmail(context, md2.parameters.get(0));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        drmd2.addValueEventListener(wifiTimerListener);

        md = new Module();
        DatabaseReference drmd = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("7");
        ValueEventListener xkcdListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md = dataSnapshot.getValue(Module.class);
                    xkcd(context, md.parameters.get(0));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        drmd.addValueEventListener(xkcdListener);

        md3 = new Module();
        DatabaseReference drmd3 = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("8");
        ValueEventListener nasaListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md3 = dataSnapshot.getValue(Module.class);
                    nasa(context, md3.parameters.get(0));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        drmd3.addValueEventListener(nasaListener);

    }

    void quotesEmail(final Context context, final String email) {
        try {
            String[] urlString = {"https://feeds.feedburner.com/quotationspage/qotd?format=xml", "https://www.brainyquote.com/link/quotebr.rss"};
            Parser parser = new Parser();
            parser.execute(urlString[new Random().nextInt(1)]);
            parser.onFinish(new Parser.OnTaskCompleted() {
                @Override
                public void onTaskCompleted(final ArrayList<Article> list) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                int random = new Random().nextInt(4);
                                GMailSender sender = new GMailSender(
                                        "automator.alpha@gmail.com",
                                        "AbCd1234");
                                sender.sendMail("Quote of the Day | Automator", "<h1>" + list.get(random).getDescription() + "<br>- <b>" + list.get(random).getTitle() + "</b></h1>",
                                        "Automator", email);
                                callNotification("A Module Ran - Sent Quote on your EMail", "Quote of the Day was sent to your EMail address", context, 8);

                            } catch (Exception e) {
                                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).start();
                }

                @Override
                public void onError() {
                    //what to do in case of error
                }
            });
        }
        catch(Exception e) {}
    }

    void xkcd(final Context context, final String email) {
        try {
            Parser parser = new Parser();
            parser.execute("https://xkcd.com/rss.xml?format=xml");
            parser.onFinish(new Parser.OnTaskCompleted() {
                @Override
                public void onTaskCompleted(final ArrayList<Article> list) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                int random = new Random().nextInt(2);
                                GMailSender sender = new GMailSender(
                                        "automator.alpha@gmail.com",
                                        "AbCd1234");
                                sender.sendMail("XKCD Snippet | Automator", "<h1>" + list.get(random).getDescription() + "</h1><br>- <b>" + list.get(random).getTitle() + "</b>",
                                        "Automator", email);
                                callNotification("A Module Ran - Sent XKCD snippet on your EMail", "XKCD snippet was sent to your email address", context, 9);

                            } catch (Exception e) {
                                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).start();
                }

                @Override
                public void onError() {
                    //what to do in case of error
                }
            });
        }
        catch(Exception e) {}
    }

    void nasa(final Context context, final String email) {
        try {
            Parser parser = new Parser();
            parser.execute(md3.parameters.get(1));
            parser.onFinish(new Parser.OnTaskCompleted() {
                @Override
                public void onTaskCompleted(final ArrayList<Article> list) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                int random = new Random().nextInt(2);
                                GMailSender sender = new GMailSender(
                                        "automator.alpha@gmail.com",
                                        "AbCd1234");
                                sender.sendMail("RSS Bundle | Automator", "<h1>" + list.get(random).getDescription() + "</h1><br>- <b>" + list.get(random).getTitle() + "</b><br><br>",
                                        "Automator", email);
                                callNotification("A Module Ran - Sent RSS bundle on your EMail", "RSS bundle was sent to your email address", context, 5);

                            } catch (Exception e) {
                                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).start();
                }

                @Override
                public void onError() {
                    //what to do in case of error
                }
            });
        }
        catch(Exception e) {}
    }

    public void callNotification(String title, String text, Context context, int id) {
        Intent intent = new Intent(context, Splash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default")
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setSmallIcon(R.drawable.alpha)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, mBuilder.build());
    }
}
