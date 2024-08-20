package v4lpt.vpt.c016.TPC;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class InfoFragment extends Fragment {

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        Button backButton = view.findViewById(R.id.backButton23);
        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).closeInfoFragment();
            }
        });

        TextView explanationTextView = view.findViewById(R.id.explanationTextView);
        String currentText = explanationTextView.getText().toString();
        String appVersion = getAppVersion();
        String updatedText = "App Version: " + appVersion + "\n\n" + currentText;
        explanationTextView.setText(updatedText);

        return view;
    }

    private String getAppVersion() {
        try {
            return requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}