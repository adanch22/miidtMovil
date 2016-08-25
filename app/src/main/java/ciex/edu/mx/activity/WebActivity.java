package ciex.edu.mx.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import ciex.edu.mx.R;
import ciex.edu.mx.app.MyApplication;
import ciex.edu.mx.dialog.Dialogo;

public class WebActivity extends AppCompatActivity {
    WebView web_view;
    String url;
    WebView view;
    String URL;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Check for login session. If not logged in launch
         * login activity
         * */
        if (MyApplication.getInstance().getPrefManager().getUser() == null) {
            launchLoginActivity();
        }

        setContentView(R.layout.activity_web);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        web_view = (WebView) findViewById(R.id.webkit);
        url="http://ciex.edu.mx";
        String url2="/";
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Javascript
        WebSettings webSettings = this.web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Url
        this.web_view.loadUrl(this.url);

        // enlaces externos en el navegador
        this.web_view.setWebViewClient(new WebViewCliente(this));
    }

    @Override
    protected void onPause(){
        this.web_view.loadUrl("http://");
        super.onPause();
    }

    @Override
    // botón de retroceso
    public void onBackPressed() {
        if(web_view.canGoBack()) {
            web_view.goBack();
        } else {
            super.onBackPressed();
        }
    }

   /* public void UrlExterna(){
        if (URL.startsWith("http://on.fb.me")){
            Intent intent = newFacebookIntent(getApplicationContext().getPackageManager(), "https://www.facebook.com/CIEXGro");
            startActivity(intent);
        }else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
            view.getContext().startActivity(intent);
            Toast.makeText(this, "¡Saliendo de " + Uri.parse(url).getHost() + "!", Toast.LENGTH_LONG).show();
        }
    }
*/


    public class WebViewCliente extends WebViewClient {
        WebActivity context;

        WebViewCliente(WebActivity cn){
            context=cn;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String URL1) {
            URL = URL1;

            // Url base
            if (URL.startsWith(url)) {
                progressBar.setVisibility(View.VISIBLE);
                return false;
            }
            String title = "¡Saliendo de " + Uri.parse(url).getHost() + "!";
            String text = "¿Desea abrir " + Uri.parse(URL).getHost() + " en un explorador externo?";

            Dialogo generalDialogFragment = Dialogo.newInstance(title,text);
            generalDialogFragment.setContext(context);
            generalDialogFragment.show(getSupportFragmentManager(), "dialog");


        return true;
    }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

    }

    private void launchLoginActivity() {
        Intent intent = new Intent(WebActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
