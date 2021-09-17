package com.goup.pin.pinaa.app;

import android.annotation.SuppressLint;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.goup.pin.pinaa.app.databinding.FragmentLuckySevenViewBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.ldoublem.loadingviewlib.view.LVGhost;
import com.unity3d.player.UnityPlayerActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class LuckySevenView extends Fragment {

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {

            int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            Activity activity = getActivity();
            if (activity != null
                    && activity.getWindow() != null) {
                activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            }

        }
    };

    private FragmentLuckySevenViewBinding binding;
    ProgressBar progressBar;
    WebView webView;
    public static String url;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mHidePart2Runnable.run();
        binding = FragmentLuckySevenViewBinding.inflate(inflater, container, false);
        webView = binding.luckyView;
        progressBar = binding.luckyProgress;

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setBackgroundColor(Color.WHITE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);

        CookieManager cookieManager = CookieManager.getInstance();
        CookieManager.setAcceptFileSchemeCookies(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);
        Chrome();
        WebClient();
        Reciever();

        webView.loadUrl(url);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true ) {
            @Override
            @MainThread
            public void handleOnBackPressed() {

                if (webView.canGoBack())
                    webView.goBack();
                else {
                    NavHostFragment.findNavController(LuckySevenView.this)
                            .navigate(R.id.action_luckySevenView_to_luckySevenMain);
                }

            }
        });


        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void Chrome()
    {
        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress < 100 && progressBar.getVisibility() == progressBar.GONE) {
                    progressBar.setVisibility(progressBar.VISIBLE);
                }
                if (newProgress == 100) {
                    progressBar.setVisibility(progressBar.GONE);
                }
            }

            private void SetDexter()
            {
                Dexter.withContext(getActivity())
                        .withPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            }
                        }).check();
            }

            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                SetDexter();

                LuckySevenConst.setCallBack(filePathCallback);
                Intent intentOne = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File filetoDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File file = null;
                try {
                    file = File.createTempFile("LUCKYSEVEN" +
                            new SimpleDateFormat("yyyyMMdd_HHmmss",
                                    Locale.getDefault()).
                                    format(new Date()) + "_", ".jpg", filetoDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(file != null)
                {
                    Uri fromFile = FileProvider(file);
                    LuckySevenConst.setURL(fromFile);
                    intentOne.putExtra(MediaStore.EXTRA_OUTPUT, fromFile);
                    intentOne.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Intent intentTwo = new Intent(Intent.ACTION_GET_CONTENT);
                    intentTwo.addCategory(Intent.CATEGORY_OPENABLE);
                    intentTwo.setType("image/*");

                    Intent intentChooser = new Intent(Intent.ACTION_CHOOSER);
                    intentChooser.putExtra(Intent.EXTRA_INTENT, intentOne);
                    intentChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intentTwo});

                    startActivityForResult(intentChooser, LuckySevenConst.getCode());

                    return true;
                }
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }

            Uri FileProvider(File file)
            {
                return FileProvider.getUriForFile(getActivity(), getActivity().getApplication().getPackageName() + ".provider", file);
            }
        });
    }
    public void WebClient()
    {
        webView.setWebViewClient(new WebViewClient()
        {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String str = LuckySevenConst.Firebase.getString(new String(Base64.decode(getActivity().getResources().getString(R.string.lucky_seven_add), Base64.DEFAULT)));
                if (url.contains(str)) {
                    Intent i = new Intent(getActivity(), UnityPlayerActivity.class);
                    getActivity().startActivity(i);
                    getActivity().finish();
                }
                else super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(TikTok(url)
                        && Instagram(url)
                        && Facebook(url)
                        && LinkedIn(url)
                        &&Twitter(url)
                        && Ok(url)
                        && Vk(url)
                        && Youtube(url))
                    view.loadUrl(url);
                return true;
            }

            boolean TikTok(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.tiktok), Base64.DEFAULT)));
            }
            boolean Facebook(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.facebook), Base64.DEFAULT)));
            }
            boolean Instagram(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.instagram), Base64.DEFAULT)));
            }
            boolean LinkedIn(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.linkedin), Base64.DEFAULT)));
            }
            boolean Twitter(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.twitter), Base64.DEFAULT)));
            }
            boolean Ok(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.ok), Base64.DEFAULT)));
            }
            boolean Vk(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.vk), Base64.DEFAULT)));
            }
            boolean Youtube(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.youtube), Base64.DEFAULT)));
            }
        });
    }
    public void Reciever()
    {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        getActivity().registerReceiver(new BroadcastReceiver() {
            public String url;
            public boolean check;
            ConnectivityManager manager;
            NetworkInfo info;
            @Override

            public void onReceive(Context context, Intent intent) {
                Manager();
                Info();
                check = info != null && info.isConnectedOrConnecting();
                if (check) {
                    if (url != null)
                        webView.loadUrl(url);
                } else {
                    url = webView.getUrl();
                    webView.loadUrl(new String(android.util.Base64.decode(context.getResources().getString(R.string.index), Base64.DEFAULT)));
                }
            }

            void Manager() {
                manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            }

            void Info() {
                info = manager.getActiveNetworkInfo();
            }
        }, intentFilter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (LuckySevenConst.getCode() == requestCode)
            if (LuckySevenConst.getCallBack() == null) return;
        if (resultCode != -1) {
            LuckySevenConst.getCallBack().onReceiveValue(null);
            return;
        }
        Uri result = (data == null)? LuckySevenConst.getURL() : data.getData();
        if(result != null && LuckySevenConst.getCallBack() != null)
            LuckySevenConst.getCallBack().onReceiveValue(new Uri[]{result});
        else if(LuckySevenConst.getCallBack() != null)
            LuckySevenConst.getCallBack().onReceiveValue(new Uri[] {LuckySevenConst.getURL()});
        LuckySevenConst.setCallBack(null);
        super.onActivityResult(requestCode, resultCode, data);
    }
}