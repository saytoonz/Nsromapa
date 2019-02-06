package com.nsromapa.nsromapa;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar bar;
    Button refresh;

    private ValueCallback<Uri[]> mUploadMessage;
    private FilePickerDialog dialog;
    private String LOG_TAG = "DREG";
    private Uri[] results;

    ////Initialize hide notification bar when window is on focus.
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            hideSystemUI();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseMessaging.getInstance().subscribeToTopic("nsromapa");
        FirebaseInstanceId.getInstance().getToken();

        refresh=(Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setVisibility(v.GONE);
               if (webView.canGoBack()){
                   webView.goBack();
               }else{
//                   LoadWeb("http://192.168.8.100/nsromapa2");
                 LoadWeb("http://nsromapa.ga");
               }
            }
        });

        refresh.setVisibility(refresh.GONE);

        LoadWeb("http://nsromapa.ga");
//        LoadWeb("http://192.168.8.100/nsromapa2");

    }


    public void LoadWeb(String url){
       webView=(WebView) findViewById(R.id.webView);
        bar=(ProgressBar) findViewById(R.id.progressBar2);
        webView.setWebViewClient(new myWebClient(webView, this.getWindow()));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowFileAccess(true);



        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.getSettings().setDatabaseEnabled(true);

        //Improve webView Performance
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSavePassword(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setEnableSmoothTransition(true);

        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


        webView.loadUrl(url);
        //webView.loadUrl("http://192.168.8.100/nsromapa2");



//        //Setting Download files
//        webView.setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//
//                request.allowScanningByMediaScanner();
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//Notify client once download is completed!
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url,contentDisposition,mimetype)); //Select folder and add filename
//                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                downloadManager.enqueue(request);
//                Toast.makeText(MainActivity.this, "Downloading File", Toast.LENGTH_LONG).show();
//            }
//        });

        //Interacting with browser UI....
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                // Double check that we don't have any existing callbacks
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = filePathCallback;

                openFileSelectionDialog();

                return true;
            }


                       //JS Alert
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Nsromapa")
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();

                return true;
            }


            //JS Confirm

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                AlertDialog dialog=new AlertDialog.Builder(view.getContext())
                        .setTitle("Nsromapa")
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                        .create();

                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();

                return true;
            }
        });

    }



    private void openFileSelectionDialog() {

        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }

        //Create a DialogProperties object.
        final DialogProperties properties = new DialogProperties();

        //Instantiate FilePickerDialog with Context and DialogProperties.
        dialog = new FilePickerDialog(MainActivity.this, properties);
        dialog.setTitle("Select a File");
        dialog.setPositiveBtnName("Select");
        dialog.setNegativeBtnName("Cancel");
//        properties.selection_mode = DialogConfigs.MULTI_MODE; // for multiple files
        properties.selection_mode = DialogConfigs.SINGLE_MODE; // for single file
        properties.selection_type = DialogConfigs.FILE_SELECT;

        //Method handle selected files.
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                results = new Uri[files.length];
                for (int i = 0; i < files.length; i++) {
                    String filePath = new File(files[i]).getAbsolutePath();
                    if (!filePath.startsWith("file://")) {
                        filePath = "file://" + filePath;
                    }
                    results[i] = Uri.parse(filePath);
                    Log.d(LOG_TAG, "file path: " + filePath);
                    Log.d(LOG_TAG, "file uri: " + String.valueOf(results[i]));
                }
                mUploadMessage.onReceiveValue(results);
                mUploadMessage = null;
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (null != mUploadMessage) {
                    if (null != results && results.length >= 1) {
                        mUploadMessage.onReceiveValue(results);
                    } else {
                        mUploadMessage.onReceiveValue(null);
                    }
                }
                mUploadMessage = null;
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (null != mUploadMessage) {
                    if (null != results && results.length >= 1) {
                        mUploadMessage.onReceiveValue(results);
                    } else {
                        mUploadMessage.onReceiveValue(null);
                    }
                }
                mUploadMessage = null;
            }
        });

        dialog.show();

    }

    private class myWebClient extends WebViewClient {

        Window window;
        private TextToSpeech tts;

        public int speak(String innerText){
            //Automatically stops if it's speaking already
            tts.speak(innerText, 0, null);
            return 0;
        }

        public myWebClient(WebView aWebView, Window aWindow)
        {
            webView = aWebView;
            window = aWindow;

            webView.addJavascriptInterface(this, "android"); //so I can access this via android now?
            tts = new TextToSpeech(window.getContext(), ttsInitListener);
        }

        private TextToSpeech.OnInitListener ttsInitListener = new TextToSpeech.OnInitListener() {
            public void onInit(int version) {
                //Don't care now!
                //Maybe let the user know hello!
            }
        };



        /////Hiding progress Bar
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            bar.setVisibility(View.GONE);
//            swipe.setRefreshing(false);
        }

        /////Show progress Bar when pages starts to load
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            bar.setVisibility(bar.VISIBLE);
        }


        //Open all links in the same webview
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // If url contains mailto link then open Mail Intent
            if (request.getUrl().toString().contains("mailto:")) {

                // Could be cleverer and use a regex
                //Open links in new browser
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString())));

                // Here we can open new activity

                return true;

            } else {
                // Stay within this webview and load url
                view.loadUrl(request.getUrl().toString());
                return true;
            }

        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
           // super.onReceivedError(view, request, error);
            webView.loadUrl("file:///android_asset/error.html");
            refresh.setVisibility(refresh.VISIBLE);
        }

    }



//FlipScreen not Loading again
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }






    ///When the back key is pressed go back to history-1
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode==KeyEvent.KEYCODE_BACK)&&(webView.canScrollVertically(0))){
            webView.scrollTo(0,0);
//            webView.reload();
            return true;
        }else if ((keyCode==KeyEvent.KEYCODE_BACK)&&(webView.canGoBack())){
                webView.goBack();
                return true;
        }
        return super.onKeyDown(keyCode,event);
    }





    ///Hide status/navigation/notification bar
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private View hideSystemUI() {
        View decorder=getWindow().getDecorView();
        decorder.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        return decorder;
    }


    //Add this method to show Dialog when the required permission has been granted to the app.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (dialog != null) {
                        openFileSelectionDialog();
                    }
                } else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(MainActivity.this, "Permission is Required", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
