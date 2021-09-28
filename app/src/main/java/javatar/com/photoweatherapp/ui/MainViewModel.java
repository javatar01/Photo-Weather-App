package javatar.com.photoweatherapp.ui;

import android.os.Environment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<String>> getMutableLiveData() {
        return mutableLiveData;
    }

    void getData() {
        List<String> uris =new ArrayList<>();
        //TARGET FOLDER
        String dir = Environment.getExternalStorageDirectory()+"/Pictures/Photo Weather App";
        File downloadsFolder = new File(dir);

        System.out.println(dir);

        if(downloadsFolder.isDirectory())
        {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files = downloadsFolder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            assert files != null;
            for (File file : files) {
                uris.add(file.getAbsolutePath());
            }
            mutableLiveData.setValue(uris);
        }
    }
}
