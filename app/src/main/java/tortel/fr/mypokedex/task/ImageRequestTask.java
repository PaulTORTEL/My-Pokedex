package tortel.fr.mypokedex.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;
import java.util.List;

import tortel.fr.mypokedex.bean.RequestParam;
import tortel.fr.mypokedex.listener.PokeApiListener;
import tortel.fr.mypokedex.manager.DataRequester;


public class ImageRequestTask extends AsyncTask<RequestParam, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(RequestParam... requestParams) {

        if (requestParams.length == 0) {
            return false;
        }

        List<Bitmap> results = new ArrayList<>();
        executeImageRequest(requestParams, 0, results);
        return true;

    }

    private void executeImageRequest(final RequestParam[] params, final int index, final List<Bitmap> results) {
        if (index >= params.length) {
            return;
        }

        final RequestParam requestParam = params[index];
        final PokeApiListener callback = (PokeApiListener) requestParam.getCallback();


        ImageRequest imageRequest = new ImageRequest(requestParam.getUri(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        results.add(bitmap);
                        if (index + 1 >= params.length) {
                            callback.onRequestSuccessful(results, requestParam.getCategory());
                        } else {
                            executeImageRequest(params, index + 1, results);
                        }
                    }
                }, 0, 0, null, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        callback.onRequestFailure("ERROR VOLLEY: " + error.getMessage() + " " + error.toString(), requestParam.getCategory());
                    }
                });


        DataRequester.getInstance(requestParam.getContext()).addToRequestQueue(imageRequest);
    }

}
