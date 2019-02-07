package tortel.fr.mypokedex.task;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tortel.fr.mypokedex.bean.RequestParam;
import tortel.fr.mypokedex.listener.PokeApiListener;
import tortel.fr.mypokedex.manager.DataRequester;


public class DataRequestTask extends AsyncTask<RequestParam, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(RequestParam... requestParams) {

        if (requestParams.length == 0) {
            return false;
        }

        List<JSONObject> results = new ArrayList<>();
        executeRequest(requestParams, 0, results);
        return true;

    }

    private void executeRequest(final RequestParam[] params, final int index, final List<JSONObject> results) {
        if (index >= params.length) {
            return;
        }

        final RequestParam requestParam = params[index];
        final PokeApiListener callback = (PokeApiListener) requestParam.getCallback();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, requestParam.getUri(), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        results.add(response);
                        // If there are no more requests to send
                        if (index + 1 >= params.length) {
                            callback.onRequestSuccessful(results, requestParam.getCategory());
                        } else {
                            // If there are still requests to send
                            executeRequest(params, index + 1, results);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onRequestFailure("ERROR VOLLEY: " + error.getMessage() + " " + error.toString(), requestParam.getCategory());
                    }
                });

        DataRequester.getInstance(requestParam.getContext()).addToRequestQueue(jsonObjectRequest);
    }
}
