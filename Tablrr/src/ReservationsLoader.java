import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import back.ITable;
import back.Preference;
import back.ReservationBean;
import back.ReservationBeansCatalogue;
import back.SingleTable;
import back.ReservationBeansCatalogue.Builder;

/**
 * Enum representing a reservations loader. 
 * @author Orfeas Liossatos
 *
 */
public enum ReservationsLoader implements ReservationBeansCatalogue.Loader {
    INSTANCE;

    private static final int NAME_INDEX = 0;
    private static final int PREFERENCE_INDEX = 1;
    private static final int PEOPLE_INDEX = 2;
    private static final int TABLE_INDEX = 3;
    private static final int RESERVATION_DATE_INDEX = 4;
    private static final int COMMENT_INDEX = 5;
    private static final int CONCERT_DATE_INDEX = 6;
    private static final int BLOCK_INDEX = 7;
    
    @Override
    public void load(InputStream inputStream, Builder builder) throws IOException {
        try (InputStreamReader inputReader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
                BufferedReader buffReader = new BufferedReader(inputReader)) {
            
            String name;   
            Preference preference ;
            int numberOfPeople;     
            ITable table;      
            LocalDateTime resDateTime;    
            String comment;
            LocalDate concertDate;
            boolean block;
            
            String line = buffReader.readLine(); // Skip the fist line
            while ((line = buffReader.readLine()) != null) {
                
                String features[] = line.split(",");
                
                // Les valeurs qui ont des valeurs par défaut
                name            = (features[NAME_INDEX].isBlank()) ? "-" 
                        : features[NAME_INDEX]; 

                preference      = (features[PREFERENCE_INDEX].isBlank()) ? Preference.NONE
                        : Preference.fromString(features[PREFERENCE_INDEX]);
                
                numberOfPeople  = (features[PEOPLE_INDEX].isBlank()) ? -1
                        : Integer.parseInt(features[PEOPLE_INDEX]);
                
                table           = (features[TABLE_INDEX].isBlank()) ? SingleTable.chairBehind() 
                        : ITable.parse(features[TABLE_INDEX]);
                
                resDateTime     = (features[RESERVATION_DATE_INDEX].isBlank()) ? LocalDateTime.now()
                        : LocalDateTime.parse(features[RESERVATION_DATE_INDEX]);
                
                comment         = (features[COMMENT_INDEX].isBlank()) ? "-"
                        : features[COMMENT_INDEX];
                
                concertDate     = (features[CONCERT_DATE_INDEX].isBlank()) ? LocalDate.now()
                        : LocalDate.parse(features[CONCERT_DATE_INDEX]);
                
                block         = (features[BLOCK_INDEX].isBlank()) ? false
                        : Boolean.parseBoolean(features[BLOCK_INDEX]);
                
                builder.addReservationBean(new ReservationBean(
                        name,
                        preference,
                        numberOfPeople,
                        table,
                        resDateTime,
                        comment,
                        concertDate,
                        block));
            }
            System.out.print("done");
        }
    }

}
