package paveljakov.transfer.rest.transform;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import spark.ResponseTransformer;

@Singleton
public class JsonTransformer implements ResponseTransformer {

    private final ObjectMapper objectMapper;

    @Inject
    JsonTransformer(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String render(final Object model) {
        try {
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(model);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

}
