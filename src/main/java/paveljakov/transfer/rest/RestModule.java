package paveljakov.transfer.rest;

import javax.inject.Singleton;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dagger.Module;
import dagger.Provides;
import paveljakov.transfer.rest.controller.ControllersModule;
import paveljakov.transfer.rest.error.ErrorHandlersModule;

@Module(includes = {ControllersModule.class, ErrorHandlersModule.class})
public class RestModule {

    @Provides
    @Singleton
    ObjectMapper provideObjectMapper() {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule());
    }

}
