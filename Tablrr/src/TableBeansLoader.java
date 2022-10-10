import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.TreeSet;

import back.CompositeTable;
import back.ITable;
import back.SingleTable;
import javafx.geometry.Point2D;

public enum TableBeansLoader implements TableBeansCatalogue.Loader {

    INSTANCE;

    private static final int CONCERT_DATE_INDEX = 0;

    @Override
    public void load(InputStream inputStream, TableBeansCatalogue.Builder builder) throws IOException {

        try (InputStreamReader inputReader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
                BufferedReader buffReader = new BufferedReader(inputReader)) {

            LocalDate concertDate = null;  
            ITable table = null;
            
            String line;
            while ((line = buffReader.readLine()) != null) {
                
                String features[] = line.split(",");
                // LOAD CONCERT DATE
                concertDate     = (features[CONCERT_DATE_INDEX].isBlank()) ? LocalDate.now()
                        : LocalDate.parse(features[CONCERT_DATE_INDEX]);
                
                // LOAD TABLES
                TreeSet<ITable> singleTables = new TreeSet<>();
                
                for (int t = 1 ; t < features.length; t += 5) {
                    singleTables.add(new SingleTable(
                            Integer.parseInt(features[t]), 
                            new Point2D(Double.parseDouble(features[t + 1]), Double.parseDouble(features[t + 2])),
                            Integer.parseInt(features[t + 3]),
                            Integer.parseInt(features[t + 4])));
                }
                
                if (singleTables.size() == 1) {
                    table = singleTables.first();
                } else if (features.length > 6) {
                    table = new CompositeTable(singleTables); 
                }
                
                builder.addTableBean(new TableBean(
                        table,
                        concertDate));
            }
        }
        
    }



}
