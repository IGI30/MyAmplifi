package com.igi.myamplify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TransferUtility transferUtility;
    private ProgressDialog progressDialog;
    private Button downloadButton;

    public static final String TAG = "MainActivity";

    private static final String COGNITO_POOL_ID = "ap-south-1:34eefd5e-db42-4641-a887-55f43c45a8f2";
//    private static final String COGNITO_POOL_ID = "ap-south-1:34eefd5e-db42-4641-a887-55f43c45a8f2";
    private static final String BUCKET_NAME = "xrstudiogltf";
    private static final String FILE_NAME = "98746321.gltf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        AWSMobileClient.getInstance().initialize(this).execute();
        createTransferUtility();

        bindViews();
        bindActions();
    }

    private void bindViews() {
        downloadButton = findViewById(R.id.download_button);
    }

    private void bindActions() {
        downloadButton.setOnClickListener(view -> {
            showProgressDialogue(FILE_NAME);
            download(FILE_NAME);
        });
    }

    private void createTransferUtility() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                COGNITO_POOL_ID,
                Regions.AP_SOUTH_1
        );
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3Client, getApplicationContext());
    }

    void download(String objectKey) {
        final File fileDownload = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), objectKey);

        TransferObserver transferObserver = transferUtility.download(
                BUCKET_NAME,
                objectKey,
                fileDownload
        );
        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged: " + state);
                if (TransferState.COMPLETED.equals(state)) {
//                    imageViewDownload.setImageBitmap(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()));
                    progressDialog.dismiss();
                    Log.d(TAG, "onStateChanged: " + fileDownload.getAbsolutePath());
                    Toast.makeText(MainActivity.this, "File downloaded", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

            @Override
            public void onError(int id, Exception ex) {
                progressDialog.dismiss();
                Log.e(TAG, "onError: ", ex);
                Toast.makeText(MainActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressDialogue(String objectKey) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Downloading object key " + objectKey);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

//    void downloadFile() {
//        // KEY and SECRET are gotten when we create an IAM user above
//        BasicAWSCredentials credentials = new BasicAWSCredentials(KEY, SECRET);
//        AmazonS3Client s3Client = new AmazonS3Client(credentials);
//
//        TransferUtility transferUtility =
//                TransferUtility.builder()
//                        .context(getApplicationContext())
//                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
//                        .s3Client(s3Client)
//                        .build();
//
//        TransferObserver downloadObserver =
//                transferUtility.download("jsaS3/" + fileName, localFile);
//
//        downloadObserver.setTransferListener(new TransferListener() {
//
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                if (TransferState.COMPLETED == state) {
//                    // Handle a completed upload.
//                }
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
//                int percentDone = (int)percentDonef;
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//                // Handle errors
//            }
//
//        });
//    }
}