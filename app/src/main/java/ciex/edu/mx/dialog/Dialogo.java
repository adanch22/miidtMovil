package ciex.edu.mx.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import ciex.edu.mx.activity.WebActivity;


/**
 * Created by azuloro on 5/02/16.
 */
public class Dialogo extends BaseDialogFragment<Dialogo.OnDialogFragmentClickListener> {
    Context cn;
    // interface to handle the dialog click back to the Activity
    public interface OnDialogFragmentClickListener {
        public void onOkClicked(Dialogo dialog);
        public void onCancelClicked(Dialogo dialog);
    }

    public void setContext(WebActivity con){
        cn=con;
    }

    // Create an instance of the Dialog with the input
    public static Dialogo newInstance(String title, String message) {
        Dialogo frag = new Dialogo();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("msg", message);

        frag.setArguments(args);

        return frag;
    }
    // Create a Dialog using default AlertDialog builder , if not inflate custom view in onCreateView
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setTitle(getArguments().getString("title"))
                .setMessage(getArguments().getString("msg"))
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Positive button clicked
                                //getActivityInstance().onOkClicked(Dialogo.this);
                                String URL = getArguments().getString("msg");
                                
                                if (URL.contains("on.fb.me")){
                                    Intent intent = newFacebookIntent(cn.getPackageManager(), "https://www.facebook.com/CIEXGro");
                                    startActivity(intent);
                                }else {
                                    //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                                    //view.getContext().startActivity(intent);
                                    //Toast.makeText(this, "¡Saliendo de " + Uri.parse(url).getHost() + "!", Toast.LENGTH_LONG).show();
                                }




                            }
                        }
                )
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // negative button clicked
                                //getActivityInstance().onCancelClicked(Dialogo.this);
                            }
                        }
                )
                .create();
    }




    public Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }
}
