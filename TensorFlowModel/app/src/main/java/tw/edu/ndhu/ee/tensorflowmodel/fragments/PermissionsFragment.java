package tw.edu.ndhu.ee.tensorflowmodel.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import java.util.Set;
import java.util.function.BiConsumer;

import tw.edu.ndhu.ee.tensorflowmodel.R;

public class PermissionsFragment extends Fragment {

    final static String[] PERMISSIONS = {Manifest.permission.CAMERA};
    final static int REQUEST_CODE = 2000;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(!checkPermissions(requireContext())) {
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    areGranted -> {
                        boolean getAllPermission = true;
                        Set<String> keySet = areGranted.keySet();
                        for(String key : keySet){
                            if(!areGranted.get(key))
                                getAllPermission = false;
                        }
                        if(getAllPermission){
                            Navigation.findNavController(requireActivity(),R.id.nav_frame).navigate(
                                    PermissionsFragmentDirections.actionPermissionsToCamera()
                            );
                        }else{
                            Toast.makeText(requireContext(),"Didn't get the required permissions",Toast.LENGTH_SHORT).show();
                        }
                    }).launch(PERMISSIONS);
        }
        else Navigation.findNavController(requireActivity(), R.id.nav_frame).navigate(
                PermissionsFragmentDirections.actionPermissionsToCamera());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static boolean checkPermissions(Context context){
        for (String per : PERMISSIONS){
            if( ContextCompat.checkSelfPermission(context,per) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    };
}
