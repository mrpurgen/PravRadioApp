package eugenzh.ru.pravradiopodcast.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import eugenzh.ru.pravradiopodcast.R;

final public class CustomToast {
    public static void showMessage(Context context, String text){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);
        TextView textView = layout.findViewById(R.id.custom_toast_msg);
        textView.setText(text);

        Toast toast = new Toast(context);
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
