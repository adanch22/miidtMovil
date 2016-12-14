package ciex.edu.mx.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ciex.edu.mx.activity.UnitActivity;

/**
 * Fragmento con diálogo básico
 */
public class SimpleDialog extends DialogFragment {

    private String title;
    private String level;
    private String book;

    private Intent intent;
    @SuppressLint("ValidFragment")
    public SimpleDialog(String title, String level, String book) {
        this.title = title;
        this.level = level;
        this.book = book;
    }

    public SimpleDialog(){

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createSimpleDialog();
    }

    public AlertDialog createSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Desea salir del Objeto de Aprendizaje")
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                intent = new Intent(getActivity(), UnitActivity.class);
                                intent.putExtra("title",title);
                                intent.putExtra("level",level);
                                intent.putExtra("book",book);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

}

