package org.quasar.geographs.apiClient;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.quasar.geographs.algortihm.PointOfInterest;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class ApiClient {
	private Vertx vertx;
	private WebClient client;
	private String address = "quasar.ptws.net";
	private String endpoint = "/isctelisboa/wspois.php/point_of_interest";

	public ApiClient() {
		vertx = Vertx.vertx();
		client = WebClient.create(vertx);
	}

	public List<PointOfInterest> getPois() {
		List<PointOfInterest> mapPois = new LinkedList<PointOfInterest>();
		client.get(80, address, endpoint).as(BodyCodec.jsonArray()).send(ar -> {
			if (ar.succeeded()) {
				HttpResponse<JsonArray> response = ar.result();

				JsonArray body = response.body();
				System.out.println("Received response with status code" + response.statusCode());

				for (int i = 0; i < body.size(); i++) {
					JsonObject rs = body.getJsonObject(i);
					System.out.println(rs);
					int id = Integer.parseInt(rs.getString("point_id"));
					double latitude = Double.parseDouble(rs.getString("latitude"));
					double altitude = Double.parseDouble(rs.getString("altitude"));
					double longitude = Double.parseDouble(rs.getString("longitude"));
					int sustainability = Integer.parseInt(rs.getString("sustainability"));
					int openHour = Integer.parseInt(rs.getString("opens_hours"));
					int closeHour = Integer.parseInt(rs.getString("closes_hours"));
					int category = Integer.parseInt(rs.getString("category_id"));
					double price = Double.parseDouble(rs.getString("price"));

					PointOfInterest poi = new PointOfInterest(id, latitude, longitude, altitude, sustainability,
							openHour, closeHour, category, price);

					mapPois.add(poi);
				}

			} else {
				System.out.println("Something went wrong " + ar.cause().getMessage());
			}
		});
		return mapPois;
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		ApiClient ap = new ApiClient();
		ap.getPois();
	}
}
