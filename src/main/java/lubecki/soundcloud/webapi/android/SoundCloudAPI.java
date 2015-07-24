package lubecki.soundcloud.webapi.android;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.util.Date;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Class which builds a {@link SoundCloudService} to access the SoundCloud API. To make
 * authenticated requests, use the {@link SoundCloudAuthenticator} class to obtain an access token
 * and then call {@link #setToken(String)}.
 */
public class SoundCloudAPI {

  public static final String SOUNDCLOUD_API_ENDPOINT = "https://api.soundcloud.com";

  private final SoundCloudService service;

  private final String clientId;
  private String token;

  /**
   * Creates a {@link SoundCloudService}. Serializes with JSON.
   *
   * @param clientId Client ID provided by SoundCloud.
   */
  public SoundCloudAPI(String clientId) {
    this.clientId = clientId;

    Gson gson =
        new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .create();

    RestAdapter adapter = new RestAdapter.Builder().setClient(new OkClient())
        .setEndpoint(SOUNDCLOUD_API_ENDPOINT)
        .setRequestInterceptor(new SoundCloudRequestInterceptor())
        .setConverter(new GsonConverter(gson))
        .build();

    service = adapter.create(SoundCloudService.class);
  }

  /**
   * Gives access to a {@link SoundCloudService}.
   *
   * @return The {@link SoundCloudService} created by this {@link SoundCloudAPI}.
   */
  public SoundCloudService getService() {
    return service;
  }

  /**
   * Sets the auth token needed by the service in order to make authenticated requests.
   *
   * @param token The OAuth token to use for authenticated requests.
   */
  public void setToken(String token) {
    this.token = token;
  }

  private class SoundCloudRequestInterceptor implements RequestInterceptor {
    @Override public void intercept(RequestFacade request) {
      request.addQueryParam("client_id", clientId);
      if (token != null) {
        request.addQueryParam("oauth_token", token);
      }
    }
  }
}