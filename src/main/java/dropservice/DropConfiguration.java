package dropservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;

public class DropConfiguration extends Configuration {

    @NotEmpty
    private String jwtTokenSecret = "dfwzsdzwh823zebdwdz772632gdsbd";

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }

    public byte[] getJwtTokenSecret() throws UnsupportedEncodingException {
        return jwtTokenSecret.getBytes("UTF-8");
    }
}
