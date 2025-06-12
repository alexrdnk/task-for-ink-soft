package com.example.localnews_backend.bootstrap;

import com.example.localnews_backend.model.City;
import com.example.localnews_backend.repo.CityRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class CityLoader implements ApplicationRunner {
    private final CityRepository cityRepo;
    private static final Pattern SPLIT_OUTSIDE_QUOTES =
            Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

    public CityLoader(CityRepository cityRepo) {
        this.cityRepo = cityRepo;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (cityRepo.count() > 0) {
            System.out.println("CityLoader: already loaded, skipping.");
            return;
        }

        try (var reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("uscities.csv").getInputStream()))) {

            // 1) Read and parse the header
            String headerLine = reader.readLine();
            System.out.println("CityLoader header: " + headerLine);
            String[] headers = SPLIT_OUTSIDE_QUOTES.split(headerLine, -1);
            // strip quotes
            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].replaceAll("^\"|\"$", "");
            }

            Map<String,Integer> idx = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                idx.put(headers[i], i);
            }

            // 2) Verify we have the columns we need
            String[] required = {"city", "state_id", "lat", "lng", "population"};
            for (String col : required) {
                if (!idx.containsKey(col)) {
                    throw new IllegalStateException("Missing required column in CSV: " + col);
                }
            }

            // 3) Loop and import
            String line;
            int imported = 0;
            while ((line = reader.readLine()) != null) {
                String[] cols = SPLIT_OUTSIDE_QUOTES.split(line, -1);
                // strip quotes
                for (int i = 0; i < cols.length; i++) {
                    cols[i] = cols[i].replaceAll("^\"|\"$", "");
                }

                try {
                    String name  = cols[idx.get("city")];
                    String state = cols[idx.get("state_id")];
                    String latS  = cols[idx.get("lat")];
                    String lonS  = cols[idx.get("lng")];
                    String popS  = cols[idx.get("population")];

                    // basic sanity
                    if (name.isBlank() || state.isBlank() || latS.isBlank() || lonS.isBlank() || popS.isBlank())
                        continue;

                    City city = new City();
                    city.setName(name);
                    city.setStateCode(state);
                    city.setLat(new BigDecimal(latS));
                    city.setLon(new BigDecimal(lonS));
                    city.setPopulation(Integer.valueOf(popS));
                    cityRepo.save(city);
                    imported++;

                    if (imported % 5000 == 0) {
                        System.out.println("  Imported so far: " + imported);
                    }
                } catch (Exception ex) {
                    System.out.println("Skipping malformed line: " + line);
                }
            }

            System.out.println("CityLoader: total imported = " + cityRepo.count());
        }
    }
}
