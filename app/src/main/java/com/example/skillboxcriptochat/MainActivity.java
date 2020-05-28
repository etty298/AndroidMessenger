package com.example.skillboxcriptochat;

import android.content.DialogInterface;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    Button sendButton;
    EditText userInput;
    RecyclerView chatWindow;
    ActionBar actionBar;
    Context context;
    MessageController controller;
    Server server;
    static String myName = "";

    public void alertBuild() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myName = input.getText().toString();
                server.sendName(myName);
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) { //During window creation (activity)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alertBuild();

        sendButton = findViewById(R.id.sendButton);
        userInput = findViewById(R.id.messageText);
        chatWindow = findViewById(R.id.chatWindow);
        actionBar = getSupportActionBar();
        context = getApplicationContext();

        controller = new MessageController();
        controller.setIncomingLayout(R.layout.message);
        controller.setOutgoingLayout(R.layout.outgoing_message);
        controller.setMessageTextId(R.id.messageText);
        controller.setUserNameId(R.id.userName);
        controller.setMessageTimeId(R.id.messageDate);
        controller.appendTo(chatWindow, this);

//        controller.addMessage(
//                new MessageController.Message("Hi everyone, this is Skillbox. You created your first Android app", "Skillbox", false)
//        );
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = userInput.getText().toString();
                if (text != null && !(text.equals(""))) {
                    controller.addMessage(
                            new MessageController.Message(text, myName, true)
                    );
                    server.sendMessage(text);
                    userInput.setText("");
                }

            }
        });

        server = new Server(
                new Consumer<Pair<String, String>>() {
                    @Override
                    public void accept(final Pair<String, String> p) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                controller.addMessage(
                                        new MessageController.Message(p.second, p.first, false)
                                );
                            }
                        });
                    }
                },
                new Consumer<Integer>() {
                    @Override
                    public void accept(final Integer usersAmount) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                actionBar.setTitle("Users online: " + usersAmount);
                            }
                        });
                    }
                }
        );

        server.connect();
    }
}
