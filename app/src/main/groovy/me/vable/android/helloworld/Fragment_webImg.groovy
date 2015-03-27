package me.vable.android.helloworld

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A fragment demo which will be put a web image on runtime
 */
public class Fragment_webImg extends Fragment {

    public Fragment_webImg() {}// Required empty public constructor


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("Fragment", "onActivityCreated");
        WeatherApp.getInstance().showToast("Fragement view Created, image loading......")
        getActivity().findViewById(R.id.textView).append("---when pic loaded, you can try Back button to revert")

        MyVolley.loadImg(R.id.theWebImg,"http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",getActivity())
        /*
            //下面这一大堆被封装进MyVolley了,    上面一句话搞定

        NetworkImageView imgView = (NetworkImageView)findViewById(R.id.theWebImg)
        Log.i("AndroidRuntime",imgView.toString())

        imgView.setDefaultImageResId(R.drawable.default_image);
        imgView.setErrorImageResId(R.drawable.failed_image);
        imgView.setImageUrl("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",
                MyVolley.getImageLoader());
        */

    }

    //下面这些都是垃圾，只是为了 log出来了解

    @Override
    void onCreate(Bundle savedInstanceState) {
        Log.d("Fragment", "onCreate");
        super.onCreate(savedInstanceState)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Fragment", "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_webimg, container, false);
    }

    @Override
    void onPause() {
        super.onPause()
        Log.d("Fragment", "onPause");
    }

    @Override
    void onDestroy() {
        super.onDestroy()
        Log.d("Fragment", "onDestroy");
    }

    @Override
    void onResume() {
        super.onResume()
        Log.d("Fragment", "onResume");
    }
}
