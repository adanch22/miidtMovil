package ciex.edu.mx.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import ciex.edu.mx.activity.ExerciseActivity;
import ciex.edu.mx.activity.ResourceActivity;
import ciex.edu.mx.activity.UnitActivity;


/**
 * Fragmento con diálogo de lista simple
 */
public class ListDialog extends DialogFragment {

    private String title;
    private String level;
    private String book;
    private String unit;

    private Intent intent;

    public ListDialog(String title, String level, String book, String unit) {
        this.title = title;
        this.level = level;
        this.book = book;
        this.unit = unit;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createSingleListDialog();
    }

    /**
     * Crea un Diálogo con una lista de selección simple
     *
     * @return La instancia del diálogo
     */
    public AlertDialog createSingleListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final CharSequence[] items = new CharSequence[2];

        items[0] = "Ir a ejercicios del OA";
        items[1] = "Ir al contenido del OA";

       /* //setTitle("pulsa en la opción deseada").
        builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getActivity(), "Seleccionaste:" + items[which], Toast.LENGTH_SHORT).show();
                        switch(which) {
                            case 0:
                                Toast.makeText(getActivity(), "0:" + items[which], Toast.LENGTH_SHORT).show();
                                //
                                break;
                            case 1:
                                Toast.makeText(getActivity(), "1:" + items[which], Toast.LENGTH_SHORT).show();

                                break;

                        }//fin de switch
                    }
                });
*/
        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                Log.i("Dialogos", "Opción elegida: " + items[item]);

                                switch(item) {
                                    case 0:
                                        Toast.makeText(getActivity(), items[item] , Toast.LENGTH_SHORT).show();
                                        intent = new Intent(getActivity(), ExerciseActivity.class);
                                        intent.putExtra("title",title);
                                        intent.putExtra("level",level);
                                        intent.putExtra("book",book);
                                        intent.putExtra("unit",unit);
                                        startActivity(intent);
                                        //
                                        break;
                                    case 1:
                                        Toast.makeText(getActivity(), items[item], Toast.LENGTH_SHORT).show();
                                        intent = new Intent(getActivity(), ResourceActivity.class);
                                        intent.putExtra("title",title);
                                        intent.putExtra("level",level);
                                        intent.putExtra("book",book);
                                        intent.putExtra("unit",unit);
                                        startActivity(intent);
                                        break;

                                }//fin de switch
                            }
                        });

        return builder.create();
    }

}

