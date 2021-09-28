package javatar.com.photoweatherapp.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Arrays;
import java.util.List;

public class CurrantLocation {

    public static final int REQUEST_CHECK_SETTINGS = 777;

    private static final String TAG = "Location";

    private final FusedLocationProviderClient fusedLocationProviderClient;

    private Location mLocation;

    LocationManager manager;
    
    Activity activity;

    private static CurrantLocation ourInstance;

    public static CurrantLocation getInstance(Activity activity) {
        if (ourInstance == null){
            ourInstance = new CurrantLocation(activity);
        }
        return ourInstance;
    }

    private CurrantLocation(Activity activity) {
        this.activity = activity;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getCurrentLocation(LocationCallback locationCallback){
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            openLocation();
            return;
        }

        Dexter.withContext(activity)
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ))
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()){
                            try {
                                zoomMyCurrentLocation(locationCallback);
                            }catch (SecurityException e){
                                Log.d(TAG, "Lost location permission could not remove updates. "+e);
                                locationCallback.onFailed();
                            }
                        }else {
                            locationCallback.onFailed();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();
    }

    private void openLocation(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                task.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void zoomMyCurrentLocation(LocationCallback locationCallback) {
        Criteria criteria = new Criteria();
        Location location = manager.getLastKnownLocation(manager.getBestProvider(criteria, false));
        if (location != null) {
            mLocation = location;
            locationCallback.onGetLocation(mLocation);
            Log.d(TAG, "zoomMyCuurentLocation: location not null");
        } else {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    mLocation = task.getResult();
                    locationCallback.onGetLocation(mLocation);
                } else{
                    Log.d(TAG, "failed");
                    locationCallback.onFailed();
                }
            });
        }
    }

    public interface LocationCallback{
        void onGetLocation(Location location);
        void onFailed();
    }
}
