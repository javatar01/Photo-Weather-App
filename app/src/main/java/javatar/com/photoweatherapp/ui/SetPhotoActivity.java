package javatar.com.photoweatherapp.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javatar.com.photoweatherapp.R;
import javatar.com.photoweatherapp.data.apis.ApisRepository;
import javatar.com.photoweatherapp.data.models.WeatherInfo;
import javatar.com.photoweatherapp.databinding.ActivitySetPhotoBinding;
import javatar.com.photoweatherapp.utils.BitmapUtils;
import javatar.com.photoweatherapp.utils.CurrantLocation;
import javatar.com.photoweatherapp.utils.SelectMedia;

public class SetPhotoActivity extends AppCompatActivity implements CurrantLocation.LocationCallback {

    ActivitySetPhotoBinding binding;

    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_set_photo);

        path = getIntent().getStringExtra("uri_image");

        if (path != null){
            binding.deleteBottun.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(path)
                    .into(binding.photo);
            binding.shareButton.setVisibility(View.VISIBLE);
        }else {
            SelectMedia.showDialogImage(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case SelectMedia.TAKE_PHOTO_REQUEST_CODE:
                    File file = new File(SelectMedia.getImageFilePath());
                    Uri uri = FileProvider.getUriForFile(this, "javatar.com.photoweatherapp.fileprovider", file);
                    CropImage.activity(uri)
                            .start(this);
                    break;
                case SelectMedia.PICK_PHOTO_REQUEST_CODE:
                    assert data != null;
                    CropImage.activity(data.getData()).start(this);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    assert result != null;
                    Uri resultUri = result.getUri();

                    Glide.with(this)
                            .load(resultUri)
                            .into(binding.photo);

                    CurrantLocation.getInstance(this).getCurrentLocation(this);
                    break;
            }
        }else {
            finish();
        }
    }

    public void share(View view) {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        path = BitmapUtils.saveImage(BitmapUtils.getBitmapFromView(binding.layoutPhoto));
                        if (path != null){
                            binding.deleteBottun.setVisibility(View.VISIBLE);
                            BitmapUtils.shareImage(SetPhotoActivity.this, path);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    public void onGetLocation(Location location) {
        getWeather(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onFailed() {
        getWeather(30.0594838,31.2234448); // Cairo default
    }

    void getWeather(double lat,double lon){
        ApisRepository.getInstance().weather(lat,lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<WeatherInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        binding.progressBar.setVisibility(View.VISIBLE);
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(@NonNull WeatherInfo weatherInfo) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.shareButton.setVisibility(View.VISIBLE);
                        binding.cityName.setText(weatherInfo.getName());

                        int temp = (int) (weatherInfo.getMain().getTemp() - 273.15);

                        binding.temp.setText(temp + "°C");
                        binding.weatherDet.setText(weatherInfo.getWeather().get(0).getDescription());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void delete(View view) {
        BitmapUtils.deleteImageFile(this,path);
        finish();
    }
}