import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing a catalogue of tables
 * @author Orfeas Liossatos
 *
 */
public final class TableBeansCatalogue {
    
    private final List<TableBean> tableBeans;
    
    public TableBeansCatalogue(List<TableBean> tables) {
        this.tableBeans = List.copyOf(tables);
    }
    
    /**
     * Returns the list of tableBeans
     * @return the list of tableBeans
     */
    public List<TableBean> tableBeans() { return this.tableBeans; }
    
    /**
     * Interface representing a loader of tableBeans
     * @author Orfeas Liossatos
     */
    public interface Loader {

        /**
         * Loads the tableBeans from the input stream {@code inputStream} and adds them 
         * to the tableBeansCatalogue of the builder {@code builder}, 
         * or raises IOException in case of an input/output error.
         * @param inputStream
         *          the input stream
         * @param builder
         *          the list of tableBeans builder
         * @throws IOException
         *          in case of an input/output error.
         */
        public abstract void load(InputStream inputStream, Builder builder) throws IOException;
    }
    
    /**
     * A TableBeansCatalogue Builder
     * <p>
     * Publique, finale.
     * @author Orfeas Liossatos 
     */
    public final static class Builder {

        private List<TableBean> tableBeans;
        
        public Builder() {
            tableBeans = new ArrayList<>();
        }
        
        public Builder addTableBean(TableBean tableBean) {
            tableBeans.add(tableBean);
            return this;
        }
        
        public List<TableBean> reservationBeans() {
            return Collections.unmodifiableList(tableBeans);
        }
        
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }
        
        public TableBeansCatalogue build() {
            return new TableBeansCatalogue(tableBeans);
        }
    }
}
