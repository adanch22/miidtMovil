package ciex.edu.mx.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import ciex.edu.mx.activity.ExercisesActivity;
import ciex.edu.mx.activity.QuizActivity;
import ciex.edu.mx.activity.ResourcesActivity;
import ciex.edu.mx.model.Exercise;


/**
 * Fragmento con diálogo de lista simple
 */
public class ListDialog extends DialogFragment {

    private String title;
    private String level;
    private String book;
    private String unit;
    private String type;

    private Intent intent;
    @SuppressLint("ValidFragment")
    public ListDialog(String title,String type, String level, String book, String unit) {
        this.title = title;
        this.level = level;
        this.book = book;
        this.unit = unit;
        this.type = type;
    }

    public ListDialog(){

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
        if (type.equals("default"))
            items[0] = "Abrir ejercicios del OA";
        else
            items[0] = "Abrir videoquiz del OA";

        items[1] = "Abrir contenido del OA";

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
                                       // intent = new Intent(getActivity(), ExercisesActivity.class);
                                        if(type.equals("default")){
                                            intent = new Intent(getActivity(), ExercisesActivity.class);
                                            intent.putExtra("title",title);
                                            intent.putExtra("level",level);
                                            intent.putExtra("book",book);
                                            intent.putExtra("unit",unit);
                                            startActivity(intent);
                                        }else if (type.equals("videoquiz")){
                                            intent = new Intent(getActivity(), QuizActivity.class);
                                            intent.putExtra("title",title);
                                            intent.putExtra("level",level);
                                            intent.putExtra("book",book);
                                            intent.putExtra("unit",unit);
                                            startActivity(intent);
                                        }


                                        //
                                        break;
                                    case 1:
                                        Toast.makeText(getActivity(), items[item], Toast.LENGTH_SHORT).show();
                                        intent = new Intent(getActivity(), ResourcesActivity.class);
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

