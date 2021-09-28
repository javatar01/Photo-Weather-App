package javatar.com.photoweatherapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import javatar.com.photoweatherapp.R;
import javatar.com.photoweatherapp.data.models.Image;
import javatar.com.photoweatherapp.databinding.ActivityMainBinding;
import javatar.com.photoweatherapp.utils.BitmapUtils;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    ImageAdapter adapter;

    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        adapter = new ImageAdapter();

        binding.listPhotos.setAdapter(adapter);
        binding.listPhotos.setLayoutManager(new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false));

        viewModel.getMutableLiveData().observe(this,uris -> {
            uris.add(null);
            adapter.setList(uris);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        viewModel.getData();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }
}