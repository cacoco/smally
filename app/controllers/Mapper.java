package controllers;

import com.google.gson.JsonObject;
import play.Logger;
import play.cache.Cache;
import play.modules.spring.Spring;
import play.mvc.Controller;
import service.api.Counter;

public class Mapper extends Controller {

    private static final int INSTANCE_ENCODING_RADIX = 32;
    private static final String KEY_PREFIX = "url-";

    public static void index() {
        // render
        render();
    }

    public static void use(final String id) {
        Logger.info(String.format("Received id: [%s]", id));
        final Long count = Long.valueOf(id, INSTANCE_ENCODING_RADIX);
        final String key = String.format("%s%s", KEY_PREFIX, count);
        final String url = (String) Cache.get(key);
        if (url == null) {
            flash.error(String.format("No url mapping found for index: [%s]", id));
            index();
        }
        redirect(url);
    }

    public static void captureUrl(final String url) {
        final Counter counter = (Counter) Spring.getBean("counter");
        final long count = counter.next();

        // set the mapping in cache
        Cache.set(String.format("%s%s", KEY_PREFIX, count), url);

        // create short url
        // TODO: read the base url from configuration once DNS is in place
//        final String baseUrl = (String) Play.configuration.get("application.base.url");
        final String baseUrl = request.getBase();
        final String shortened = String.format("%s/%s",
                baseUrl,
                Long.toString(count, INSTANCE_ENCODING_RADIX));

        JsonObject json = new JsonObject();
        json.addProperty("smally-url", shortened);
        renderJSON(json.toString());
    }
}
