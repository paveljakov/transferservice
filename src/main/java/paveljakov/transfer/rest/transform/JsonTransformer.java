package paveljakov.transfer.rest.transform;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;

import spark.ResponseTransformer;

@Singleton
public class JsonTransformer implements ResponseTransformer {

    private final Gson gson;

    @Inject
    JsonTransformer(final Gson gson) {
        this.gson = gson;
    }

    @Override
    public String render(final Object model) {
        return gson.toJson(model);
    }

}
