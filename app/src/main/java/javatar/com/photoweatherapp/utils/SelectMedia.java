package javatar.com.photoweatherapp.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javatar.com.photoweatherapp.R;
import javatar.com.photoweatherapp.databinding.Dialog2OptionsBinding;

public class SelectMedia {

    public static final int PICK_PHOTO_REQUEST_CODE = 500;
    public static final int TAKE_PHOTO_REQUEST_CODE = 501;

    private static String imageFilePath;

    @SuppressLint({"QueryPermissionsNeeded", "IntentReset"})
    public static void showDialogImage(Activity activity){
        AlertDialog builder = new AlertDialog.Builder(activity).create();

        Dialog2OptionsBinding optionsBinding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.dialog_2_options,null,false);

        builder.setView(optionsBinding.getRoot());

        optionsBinding.option1.setOnClickListener(v ->{
            builder.dismiss();
            checkPermission(activity,() -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                activity.startActivityForResult(intent,PICK_PHOTO_REQUEST_CODE);
            });
        });

        optionsBinding.option2.setOnClickListener(v ->{
            builder.dismiss();
            checkPermission(activity,() -> {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                    Uri photoURI;
                    File photoFile = createImageFile();
                    photoURI = FileProvider.getUriForFile(activity,
                            "javatar.com.photoweatherapp.fileprovider", photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    activity.startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST_CODE);
                }
            });
        });

        optionsBinding.cancelButton.setOnClickListener(v ->{
            builder.dismiss();
            activity.finish();
        });

        builder.show();
    }

    private static File createImageFile() {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/Fox Scope");

        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        File image = null;
        if (success) {
            image = new File(storageDir, imageFileName);
        }

        if (image != null) {
            imageFilePath = image.getAbsolutePath();
        }
        return image;
    }

    public static String getImageFilePath() {
        return imageFilePath;
    }

    public static void checkPermission(Context context, IPermission permission){
        Dexter.withContext(context)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        permission.onPermission();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    interface IPermission{
        void onPermission();
    }
}